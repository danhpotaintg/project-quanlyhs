package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.util.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    public String generateUsername(String fullName, String customId) {
        //viet tat ten
        String abbreviation = StringUtils.getAbbreviatedName(fullName);

        // gheo theo mau
        return abbreviation + "_" + customId;
    }

    public String generateDefaultPassword(String customId) {
        //mk mac dinh la id
        return customId;
    }
}