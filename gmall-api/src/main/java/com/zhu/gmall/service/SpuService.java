package com.zhu.gmall.service;

import com.zhu.gmall.bean.PmsProductInfo;
import org.springframework.stereotype.Service;

import java.util.List;

public interface SpuService {
    List<PmsProductInfo> spuList(String catalog3Id);
}
