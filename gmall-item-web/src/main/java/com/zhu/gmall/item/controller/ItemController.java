package com.zhu.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.zhu.gmall.bean.PmsProductSaleAttr;
import com.zhu.gmall.bean.PmsSkuInfo;
import com.zhu.gmall.bean.PmsSkuSaleAttrValue;
import com.zhu.gmall.service.SkuService;
import com.zhu.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Controller
public class ItemController {

    @Reference
    SkuService skuService;

    @Reference
    SpuService spuService;

    @RequestMapping("{skuId}.html")
    public String item(@PathVariable String skuId, ModelMap map, HttpServletRequest request) {

        String remoteAddr = request.getRemoteAddr();

        //request.getHeader("");//Nginx负载均衡时这样

        PmsSkuInfo pmsSkuInfo = skuService.getSkuById(skuId,remoteAddr);

        map.put("skuInfo", pmsSkuInfo);
        List<PmsProductSaleAttr> pmsProductSaleAttrs = spuService.spuSaleAttrListCheckBySku(pmsSkuInfo.getProductId(),skuId);
        map.put("spuSaleAttrListCheckBySku", pmsProductSaleAttrs);

        // 查询当前的sku的spu的其他的sku的集合的数组
        HashMap<String,String> skuSaleAttrHash = new HashMap<>();
        List<PmsSkuInfo> pmsSkuInfos = skuService.getSkuSaleAttrValueListBySpu(pmsSkuInfo.getProductId());

        for (PmsSkuInfo skuInfo : pmsSkuInfos) {
            String k = "";
            String v = skuInfo.getId();

            List<PmsSkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();

            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
                k += pmsSkuSaleAttrValue.getSaleAttrValueId() + "|";
            }

            skuSaleAttrHash.put(k,v);

        }

        //将sku的销售属性hash数组放到页面
        String skuSaleAttrHashJsonStr = JSON.toJSONString(skuSaleAttrHash);
        map.put("skuSaleAttrHashJsonStr",skuSaleAttrHashJsonStr);

        return "item";
    }

}
