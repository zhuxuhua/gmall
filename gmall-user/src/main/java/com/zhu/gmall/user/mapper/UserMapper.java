package com.zhu.gmall.user.mapper;

import com.zhu.gmall.bean.UmsMember;
import com.zhu.gmall.bean.UmsMember;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
@Component("userMapper")
public interface UserMapper extends Mapper<UmsMember> {
    List<UmsMember> selectAllUser();
}
