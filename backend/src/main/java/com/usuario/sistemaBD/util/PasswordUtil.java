package com.usuario.sistemaBD.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String hashPassword(String senha) {
        return encoder.encode(senha);
    }

    public boolean checkPassword(String senhaDigitada, String senhaHash) {
        return encoder.matches(senhaDigitada, senhaHash);
    }
}