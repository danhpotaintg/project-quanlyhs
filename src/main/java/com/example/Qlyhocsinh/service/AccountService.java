package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.util.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    public String generateUsername(String fullName, String customId) {
        // "Nguyễn Văn Anh" -> "anv"
        String abbreviation = StringUtils.getAbbreviatedName(fullName);

        // Ghép lại theo mẫu: anv_K22ST00001
        return abbreviation + "_" + customId;
    }

    public String generateDefaultPassword(String customId) {
        // Mật khẩu mặc định chính là ID (K22ST00001)
        return customId;
    }
}