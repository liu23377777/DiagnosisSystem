package com.tcm.diagnosissystem.controller.doctor;

import com.tcm.diagnosissystem.common.Result;
import com.tcm.diagnosissystem.entity.doctor.NonDrugCatalog;
import com.tcm.diagnosissystem.mapper.doctor.NonDrugCatalogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 非药品目录接口：提供收费项目给前端下拉框使用
 */
@RestController
@RequestMapping("/api/catalog")
public class CatalogApiController {

    @Autowired
    private NonDrugCatalogMapper catalogMapper;

    /**
     * 获取所有 item_name + unit_price（取最小价做展示）
     */
    @GetMapping("/non-drug-items")
    public Result<List<NonDrugCatalog>> listItems(){
        List<NonDrugCatalog> list = catalogMapper.listPayItems();
        return Result.success(list);
    }
}

