package com.gradmate.gradmate_adminsystem_springboot.util;

import cn.hutool.crypto.digest.BCrypt;

public class PasswordUtil {
    private PasswordUtil() {
    }

    public static String hash(String raw) {
        return BCrypt.hashpw(raw, BCrypt.gensalt());
    }

    public static boolean matches(String raw, String hashed) {
        if (raw == null || hashed == null) {
            return false;
        }
        return BCrypt.checkpw(raw, hashed);
    }
}
