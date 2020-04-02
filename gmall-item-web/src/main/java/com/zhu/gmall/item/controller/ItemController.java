package com.zhu.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zhu.gmall.bean.PmsProductSaleAttr;
import com.zhu.gmall.bean.PmsSkuInfo;
import com.zhu.gmall.service.SkuService;
import com.zhu.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ItemController {

    @Reference
    SkuService skuService;

    @Reference
    SpuService spuService;

    @RequestMapping("{skuId}.html")
    public String item(@PathVariable String skuId, ModelMap map) {

        PmsSkuInfo pmsSkuInfo = skuService.getSkuById(skuId);

        map.put("skuInfo", pmsSkuInfo);
        List<PmsProductSaleAttr> pmsProductSaleAttrs = spuService.spuSaleAttrListCheckBySku(pmsSkuInfo.getProductId(),skuId);
        map.put("spuSaleAttrListCheckBySku", pmsProductSaleAttrs);
        return "item";
    }

    @RequestMapping("index")
    public String index(ModelMap modelMap) {

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add("循环数据" + i);
        }

        modelMap.put("list", list);
        modelMap.put("hello", "hello thymeleaf  !!!");
        modelMap.put("check", "0");
        return "index";
    }

}
