package com.tcm.diagnosissystem.mapper.doctor;

import com.tcm.diagnosissystem.entity.doctor.DrugCatalogItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DrugCatalogMapper {

    @Select("SELECT id, drug_code AS drugCode, drug_name AS drugName, specification, manufacturer, dosage_form AS dosageForm, approval_number AS approvalNumber, price, pinyincode " +
            "FROM drug_catalog_sql_results " +
            "WHERE (#{keyword} IS NULL OR #{keyword} = '' OR drug_name LIKE CONCAT('%',#{keyword},'%') OR pinyincode LIKE CONCAT('%',#{keyword},'%') OR drug_code LIKE CONCAT('%',#{keyword},'%')) " +
            "ORDER BY id LIMIT #{size} OFFSET #{offset}")
    List<DrugCatalogItem> search(@Param("keyword") String keyword,
                                @Param("offset") int offset,
                                @Param("size") int size);
}

