package com.zhu.gmall.service;

import com.zhu.gmall.bean.PmsSkuInfo;

public interface SkuService {

    void saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    PmsSkuInfo getSkuById(String skuId);
}
