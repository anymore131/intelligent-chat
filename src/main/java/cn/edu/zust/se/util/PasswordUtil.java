package cn.edu.zust.se.util;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

@Component
public class PasswordUtil  implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return DigestUtils.md5DigestAsHex(rawPassword.toString().getBytes());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encodedPassword.equals(DigestUtils.md5DigestAsHex(rawPassword.toString().getBytes()));
    }
}