package com.zhu.gmall.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.zhu.gmall.bean.UmsMember;
import com.zhu.gmall.bean.UmsMemberReceiveAddress;
import com.zhu.gmall.service.UserService;
import com.zhu.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.zhu.gmall.user.mapper.UserMapper;
import com.zhu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public List<UmsMember> getAllUser() {
        List<UmsMember> UmsMembers = userMapper.selectAll();//userMapper.selectAllUser();
        return UmsMembers;
    }

    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId) {
        // 封装的参数对象
        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setMemberId(memberId);
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.select(umsMemberReceiveAddress);


//        Example example = new Example(UmsMemberReceiveAddress.class);
//        example.createCriteria().andEqualTo("memberId",memberId);
//        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.selectByExample(example);

        return umsMemberReceiveAddresses;
    }

    @Override
    public UmsMember login(UmsMember umsMember) {

        Jedis jedis = null;

        try {

            jedis = redisUtil.getJedis();

            if (jedis != null){

                String umsMemberStr = jedis.get("user:" + umsMember.getPassword() + ":info");

                if (StringUtils.isNotBlank(umsMemberStr)){
                    //密码正确
                    UmsMember umsMemberFromCache = JSON.parseObject(umsMemberStr, UmsMember.class);

                    return umsMemberFromCache;
                }

            }

            //链接redis失败，开数据库查询
            UmsMember umsMemberFromDb = loginFromDb(umsMember);
            if (umsMemberFromDb != null){
                jedis.setex("user:"+umsMember.getPassword()+":info",60*60*24,JSON.toJSONString(umsMemberFromDb));
            }
            return null;

        } finally {
            jedis.close();
        }


    }

    private UmsMember loginFromDb(UmsMember umsMember) {

        List<UmsMember> umsMembers = userMapper.select(umsMember);

        if (umsMembers != null){
            return umsMembers.get(0);
        }

        return null;
    }

    @Override
    public void addUserToken(String token, String memberId) {

    }

    @Override
    public UmsMember addOauthUser(UmsMember umsMember) {

        return null;
    }

    @Override
    public UmsMember checkOauthUser(UmsMember umsCheck) {
        return null;
    }

    @Override
    public UmsMemberReceiveAddress getReceiveAddressById(String receiveAddressId) {
        return null;
    }
}
