package com.zhu.gmall.service;

import com.zhu.gmall.bean.PmsSearchParam;
import com.zhu.gmall.bean.PmsSearchSkuInfo;

import java.util.List;

public interface SearchService {

    List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam);
}
