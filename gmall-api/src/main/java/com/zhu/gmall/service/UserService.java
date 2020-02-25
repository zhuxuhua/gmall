package com.zhu.gmall.service;

import com.zhu.gmall.bean.UmsMember;
import com.zhu.gmall.bean.UmsMemberReceiveAddress;

import java.util.List;

public interface UserService {
    List<UmsMember> getAllUser();

    List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId);
}
