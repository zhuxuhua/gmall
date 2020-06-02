package com.zhu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.zhu.gmall.bean.PmsSkuAttrValue;
import com.zhu.gmall.bean.PmsSkuImage;
import com.zhu.gmall.bean.PmsSkuInfo;
import com.zhu.gmall.bean.PmsSkuSaleAttrValue;
import com.zhu.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.zhu.gmall.manage.mapper.PmsSkuImageMapper;
import com.zhu.gmall.manage.mapper.PmsSkuInfoMapper;
import com.zhu.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.zhu.gmall.service.SkuService;
import com.zhu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Autowired
    PmsSkuImageMapper pmsSkuImageMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {

        // 插入skuInfo
        int i = pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
        String skuId = pmsSkuInfo.getId();

        // 插入平台属性关联
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
            pmsSkuAttrValue.setSkuId(skuId);
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
        }

        // 插入销售属性关联
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
            pmsSkuSaleAttrValue.setSkuId(skuId);
            pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }

        // 插入图片信息
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        for (PmsSkuImage pmsSkuImage : skuImageList) {
            pmsSkuImage.setSkuId(skuId);
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }


    }

    public PmsSkuInfo getSkuByIdFormDb(String skuId) {

        //sku商品对象
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo skuInfo = pmsSkuInfoMapper.selectOne(pmsSkuInfo);

        //sku图片集合
        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> pmsSkuImageList = pmsSkuImageMapper.select(pmsSkuImage);
        skuInfo.setSkuImageList(pmsSkuImageList);

        return skuInfo;
    }

    @Override
    public PmsSkuInfo getSkuById(String skuId, String ip) {

        System.out.println("ip为" + ip + "的用户:" + Thread.currentThread().getName() + "进入了商品详情的请求");

        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();

        //链接缓存
        Jedis jedis = redisUtil.getJedis();


        //查询缓存
        String skuKey = "sku:" + skuId + ":info";
        String skuJson = jedis.get(skuKey);

        if (StringUtils.isNotBlank(skuJson)) {
            System.out.println("ip为" + ip + "的用户:" + Thread.currentThread().getName() + "从缓存中获取了商品详情");
            pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);
        } else {
            //如果没有缓存，查询MySQL
            System.out.println("ip为" + ip + "的用户:" + Thread.currentThread().getName() + "发现缓存中没有,申请缓存的分布式锁:" + "sku:" + skuId + ":lock");

            String token = UUID.randomUUID().toString();
            //设置分布式锁
            String OK = jedis.set("sku:" + skuId + ":lock", token, "nx", "px", 10 * 1000);//拿到锁的线程有10秒的过期时间

            if (StringUtils.isNotBlank(OK) && OK.equals("OK")) {
                //设置成功,有权在10秒过期时间内访问数据库
                System.out.println("ip为" + ip + "的用户:" + Thread.currentThread().getName() + "有权在10秒的过期时间访问数据库");
                pmsSkuInfo = getSkuByIdFormDb(skuId);

                try {
                    Thread.sleep(1000 * 5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (pmsSkuInfo != null) {
                    // mysql查询结果存入redis
                    jedis.set("sku:" + skuId + ":info", JSON.toJSONString(pmsSkuInfo));
                } else {
                    // 数据库中不存在该sku
                    // 为了防止缓存穿透将，null或者空字符串值设置给redis
                    jedis.setex("sku:" + skuId + ":info", 60 * 3, JSON.toJSONString(""));
                }

                //在访问MySQL后,将MySQL的分布式锁释放
                System.out.println("ip为" + ip + "的用户:" + Thread.currentThread().getName() + "将锁归还");
                String lockToken = jedis.get("sku:" + skuId + ":lock");
                //此处还可设置lua 处理极端并发情况
                if (StringUtils.isNotBlank(lockToken) && lockToken.equals(token)){
                    //jedis.eval("lua");可与用lua脚本，在查询到key的同时删除该key，防止高并发下的意外的发生
                    jedis.del("sku:" + skuId + ":lock");//用token确认删除的是自己的sku锁
                }

            } else {
                //设置失败,自旋(该线程在睡眠几秒后,重新尝试访问本方法)
                System.out.println("ip为" + ip + "的用户:" + Thread.currentThread().getName() + "没有拿到锁开始自旋");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return getSkuById(skuId, ip);
            }
        }


        jedis.close();


        return pmsSkuInfo;
    }

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId) {

        List<PmsSkuInfo> PmsSkuInfos = pmsSkuInfoMapper.selectSkuSaleAttrValueListBySpu(productId);

        return PmsSkuInfos;
    }

    @Override
    public List<PmsSkuInfo> getAllSku() {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectAll();

        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            String skuId = pmsSkuInfo.getId();

            PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(skuId);
            List<PmsSkuAttrValue> select = pmsSkuAttrValueMapper.select(pmsSkuAttrValue);

            pmsSkuInfo.setSkuAttrValueList(select);
        }
        return pmsSkuInfos;
    }


    @Override
    public boolean checkPrice(String productSkuId, BigDecimal price) {
        boolean b = false;

        PmsSkuInfo skuInfo = new PmsSkuInfo();
        skuInfo.setId(productSkuId);
        PmsSkuInfo skuInfo1 = pmsSkuInfoMapper.selectOne(skuInfo);

        BigDecimal price1 = skuInfo1.getPrice();

        if (price1.compareTo(price)==0){
            b = true;
        }


        return b;
    }
}
