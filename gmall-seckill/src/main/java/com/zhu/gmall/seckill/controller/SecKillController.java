package com.zhu.gmall.seckill.controller;

import com.zhu.gmall.util.RedisUtil;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

@Controller
public class SecKillController {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    RedissonClient redissonClient;

    @RequestMapping("secKill")
    @ResponseBody
    public String secKill() {
        RSemaphore semaphore = redissonClient.getSemaphore("106");
        boolean b = semaphore.tryAcquire();

        Jedis jedis = redisUtil.getJedis();

        Integer stock = Integer.parseInt(jedis.get("106"));

        if (b) {
            System.out.println("当前库存剩余数量" + stock + ",某用户抢购成功,当前抢购人数:" + (1000 - stock));
            System.out.println("发出订单的消息队列,由订单系统对当前抢购生成订单");
        } else {
            System.out.println("当前库存剩余数量" + stock + "某用户抢购失败");
        }
        jedis.close();
        return "2";
    }


    @RequestMapping("kill")
    @ResponseBody
    public String kill() {

        Jedis jedis = redisUtil.getJedis();

        Integer stock = Integer.parseInt(jedis.get("106"));

        if (stock > 0) {
            Transaction multi = jedis.multi();
            multi.incrBy("106", -1);
            List<Object> exec = multi.exec();
            if (exec != null && exec.size() > 0) {
                System.out.println("当前库存剩余数量" + stock + ",某用户抢购成功,当前抢购人数:" + (1000 - stock));
            } else {
                System.out.println("当前库存剩余数量" + stock + "某用户抢购失败");
            }

        }

        jedis.close();

        return "1";

    }

}
