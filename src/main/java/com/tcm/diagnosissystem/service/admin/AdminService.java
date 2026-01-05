// 路径: src/main/java/com/tcm/diagnosissystem/service/AdminService.java
package com.tcm.diagnosissystem.service.admin;

import com.tcm.diagnosissystem.entity.Admin;
import com.tcm.diagnosissystem.mapper.admin.AdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    public Admin getAdminByUsername(String username) {
        return adminMapper.selectByUsername(username);
    }

    public Admin getAdminById(Long id) {
        return adminMapper.selectById(id);
    }
}
