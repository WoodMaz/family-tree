package com.example.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.NoSuchElementException;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.NONE)
public class ThrowingUtils {
    public static Supplier<UsernameNotFoundException> userNotFound(String username) {
        return () -> new UsernameNotFoundException(String.format("User: %s not found", username));
    }

    public static Supplier<NoSuchElementException> personNotFound(String personId) {
        return () -> new NoSuchElementException(String.format("Person with ID: %s does not exist", personId));
    }
}
