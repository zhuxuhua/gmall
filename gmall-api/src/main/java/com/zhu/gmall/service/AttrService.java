package com.zhu.gmall.service;

import com.zhu.gmall.bean.PmsBaseAttrInfo;
import com.zhu.gmall.bean.PmsBaseAttrValue;

import java.util.List;

/**
 * Created by LuckyZhu on 2020/3/1
 */
public interface AttrService {
    List<PmsBaseAttrInfo> attrInfoList(String catalog3Id);

    String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);

    List<PmsBaseAttrValue> getAttrValueList(String attrId);
}
