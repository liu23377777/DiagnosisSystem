package com.tcm.diagnosissystem.mapper.doctor;

import com.tcm.diagnosissystem.entity.NonDrugCatalog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * 非药品目录 Mapper，用于读取支付下拉框（item_name）及其价格（unit_price）
 */
@Mapper
public interface NonDrugCatalogMapper {

    /**
     * 下拉框数据：非药品项目名称 + 单价
     * 说明：同名 item_name 可能有多条记录，这里用 MIN(unit_price) 做展示价（可按需要改成 MAX/AVG 或按 id 取第一条）
     */
    @Select("""
            SELECT item_name AS itemName, MIN(unit_price) AS unitPrice
            FROM non_drug_catalog_sql_results
            WHERE item_name IS NOT NULL
            GROUP BY item_name
            ORDER BY item_name
            """)
    List<NonDrugCatalog> listPayItems();

    /**
     * 根据项目名称查价格
     */
    @Select("""
            SELECT unit_price
            FROM non_drug_catalog_sql_results
            WHERE item_name = #{itemName}
            ORDER BY id
            LIMIT 1
            """)
    BigDecimal findPriceByItemName(@Param("itemName") String itemName);

    @Select("SELECT * FROM non_drug_catalog_sql_results WHERE id = #{id}")
    NonDrugCatalog findById(Long id);
}
