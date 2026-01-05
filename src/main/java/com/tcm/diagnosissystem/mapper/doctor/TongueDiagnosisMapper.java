// 路径: src/main/java/com/tcm/diagnosissystem/mapper/TongueDiagnosisMapper.java
package com.tcm.diagnosissystem.mapper.doctor;

import com.tcm.diagnosissystem.entity.TongueDiagnosis;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TongueDiagnosisMapper {

    // 以下方法在 XML 中定义，避免重复定义
    void insert(TongueDiagnosis diagnosis);
    TongueDiagnosis selectById(@Param("id") Long id);
    TongueDiagnosis findById(@Param("id") Long id);
    List<TongueDiagnosis> findByUserId(@Param("userId") Long userId);
    void deleteById(@Param("id") Long id);
    void deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
    void update(TongueDiagnosis diagnosis);
    TongueDiagnosis findLatestByUserId(@Param("userId") Long userId);
    List<TongueDiagnosis> findByUserIdWithPage(@Param("userId") Long userId, 
                                                @Param("offset") int offset, 
                                                @Param("pageSize") int pageSize);
    long countByUserId(@Param("userId") Long userId);
    List<TongueDiagnosis> findBySyndrome(@Param("syndrome") String syndrome);
    List<TongueDiagnosis> findByDateRange(@Param("userId") Long userId, 
                                          @Param("startDate") java.time.LocalDateTime startDate, 
                                          @Param("endDate") java.time.LocalDateTime endDate);
    void deleteBatch(@Param("ids") List<Long> ids);
    void updateById(TongueDiagnosis diagnosis);
    List<TongueDiagnosis> selectPendingList();

    @Select("SELECT * FROM tongue_diagnosis WHERE user_id = #{patientId} ORDER BY create_time DESC")
    List<TongueDiagnosis> selectByPatientId(@Param("patientId") Long patientId);

    // ✅ 查询待审核的舌诊记录
    @Select("SELECT * FROM tongue_diagnosis WHERE status = 0 ORDER BY create_time ASC")
    List<TongueDiagnosis> selectPendingReviews();

    // ✅ 查询医生已审核的记录
    @Select("SELECT * FROM tongue_diagnosis WHERE doctor_id = #{doctorId} AND status = 1 ORDER BY review_time DESC")
    List<TongueDiagnosis> selectReviewedByDoctor(@Param("doctorId") Long doctorId);

    // ✅ 更新审核信息
    @Update("UPDATE tongue_diagnosis SET doctor_id = #{doctorId}, final_syndrome = #{finalSyndrome}, " +
            "final_advice = #{finalAdvice}, status = 1, review_time = #{reviewTime} WHERE id = #{id}")
    int updateReview(TongueDiagnosis diagnosis);
}
