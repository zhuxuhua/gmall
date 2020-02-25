package com.zhu.gmall.user.service;

import com.zhu.gmall.user.bean.UmsMember;
import com.zhu.gmall.user.bean.UmsMemberReceiveAddress;

import java.util.List;

public interface UserService {
    List<UmsMember> getAllUser();

    List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId);
}
