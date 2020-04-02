package com.zhu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zhu.gmall.bean.PmsProductImage;
import com.zhu.gmall.bean.PmsProductInfo;
import com.zhu.gmall.bean.PmsProductSaleAttr;
import com.zhu.gmall.manage.util.PmsUploadUtil;
import com.zhu.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@CrossOrigin
public class SpuController {

    @Reference
    SpuService spuService;

    @RequestMapping("spuImageList")
    @ResponseBody
    public List<PmsProductImage> spuImageList(String spuId) {

        List<PmsProductImage> PmsProductImages = spuService.spuImageList(spuId);
        return PmsProductImages;
    }

    @RequestMapping("spuSaleAttrList")
    @ResponseBody
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId) {

        List<PmsProductSaleAttr> PmsProductSaleAttrs = spuService.spuSaleAttrList(spuId);
        return PmsProductSaleAttrs;
    }

    @RequestMapping("fileUpload")
    @ResponseBody
    public String fileUpload(@RequestParam("file") MultipartFile multipartFile) {
        //将图片或者音频上传到分布式文件存储系统
        String imgUrl = PmsUploadUtil.uploadImage(multipartFile);
        System.out.println(imgUrl);
        return imgUrl;
    }

    @RequestMapping("saveSpuInfo")
    @ResponseBody
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo) {
        spuService.saveSpuInfo(pmsProductInfo);

        return "success";
    }

    @RequestMapping("spuList")
    @ResponseBody
    public List<PmsProductInfo> spuList(String catalog3Id) {

        List<PmsProductInfo> pmsProductInfos = spuService.spuList(catalog3Id);
        return pmsProductInfos;
    }
}
