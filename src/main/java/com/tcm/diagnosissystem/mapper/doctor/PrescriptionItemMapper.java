package com.tcm.diagnosissystem.mapper.doctor;

import com.tcm.diagnosissystem.entity.PrescriptionItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PrescriptionItemMapper {

    @Insert({
            "<script>",
            "INSERT INTO prescription_item(",
            "prescription_id,item_type,item_code,item_name,specification,unit_price,quantity,subtotal,`usage`,`frequency`",
            ") VALUES ",
            "<foreach collection='items' item='i' separator=','>",
            "(#{i.prescriptionId},#{i.itemType},#{i.itemCode},#{i.itemName},#{i.specification},#{i.unitPrice},#{i.quantity},#{i.subtotal},#{i.usage},#{i.frequency})",
            "</foreach>",
            "</script>"
    })
    int insertBatch(@Param("items") List<PrescriptionItem> items);

    @Select("SELECT * FROM prescription_item WHERE prescription_id=#{pid}")
    List<PrescriptionItem> selectByPrescriptionId(@Param("pid") Long pid);
}
