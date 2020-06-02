package com.zhu.gmall.manage.mapper;

import com.zhu.gmall.bean.PmsBaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created by LuckyZhu on 2020/3/1
 */
public interface PmsBaseAttrInfoMapper extends Mapper<PmsBaseAttrInfo> {
    List<PmsBaseAttrInfo> selectAttrValueByValueId(@Param("valueIdStr") String valueIdStr);
}
