package com.example.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.NONE)
public class ThrowingUtil {
    public static Supplier<UsernameNotFoundException> userNotFound(String username) {
        return () -> new UsernameNotFoundException(String.format("User: %s not found", username));
    }
}
