package com.tcm.diagnosissystem.controller.doctor;

import com.tcm.diagnosissystem.common.Result;
import com.tcm.diagnosissystem.entity.DrugCatalogItem;
import com.tcm.diagnosissystem.mapper.doctor.DrugCatalogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/drug")
public class DrugCatalogController {

    @Autowired
    private DrugCatalogMapper drugCatalogMapper;

    @GetMapping("/search")
    public Result<List<DrugCatalogItem>> search(@RequestParam(required = false) String keyword,
                                               @RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "20") int size){
        int offset = (page - 1) * size;
        return Result.success(drugCatalogMapper.search(keyword, offset, size));
    }
}

