package com.tcm.diagnosissystem.config;

import com.tcm.diagnosissystem.entity.Department;
import com.tcm.diagnosissystem.entity.Doctor;
import com.tcm.diagnosissystem.mapper.patient.DepartmentMapper;
import com.tcm.diagnosissystem.mapper.doctor.DoctorMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * 启动时自动填充医生数据，保证每个科室至少 3 名医生
 */
@Component
public class DoctorDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DoctorDataSeeder.class);

    private static final String DEFAULT_PWD = new BCryptPasswordEncoder()
            .encode("123456");

    private static final String[] SURNAMES = {"王","李","张","刘","陈","杨","赵","黄","周","吴","徐","孙","胡"};
    private static final String[] GIVEN = {"伟","芳","娜","敏","静","秀英","丽","强","磊","洋","勇","艳"};
    private static final String[] TITLES = {"住院医师","主治医师","副主任医师","主任医师"};

    @Autowired DoctorMapper doctorMapper;
    @Autowired DepartmentMapper departmentMapper;

    @Override
    public void run(String... args) {
        long total = doctorMapper.countAll();
        if(total>50){
            log.info("医生表已有 {} 条记录，跳过数据填充", total);
            return;
        }
        List<Department> depts = departmentMapper.selectAll();
        Random r = new Random();
        int created =0;
        for(Department d: depts){
            if(d.getName()==null) continue;
            int existing = doctorMapper.countByDeptName(d.getName());
            for(int i=existing;i<3;i++){
                Doctor doc = new Doctor();
                doc.setUsername(d.getCode().toLowerCase()+"_"+(i+1));
                doc.setPassword(DEFAULT_PWD);
                doc.setRealName(SURNAMES[r.nextInt(SURNAMES.length)]+GIVEN[r.nextInt(GIVEN.length)]);
                doc.setDepartment(d.getName());
                doc.setHospitalName("中医院");
                doc.setTitle(TITLES[r.nextInt(TITLES.length)]);
                doc.setStatus(1);
                doc.setCreateTime(LocalDateTime.now());
                doc.setUpdateTime(LocalDateTime.now());
                doctorMapper.insert(doc);
                created++;
            }
        }
        log.info("DoctorDataSeeder 生成医生 {} 条", created);
    }
}

