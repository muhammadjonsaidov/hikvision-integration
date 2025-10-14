package org.example.hikvisionintegration.repository;


import org.example.hikvisionintegration.model.User;
import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MockUserRepository {
    // Foydalanuvchilarni saqlash uchun (username -> User)
    private final Map<String, User> users = new ConcurrentHashMap<>();

    public MockUserRepository() {
        // Test uchun bir nechta foydalanuvchi qo'shamiz
        users.put("ali", new User("ali", "ali123", "101")); // "ali"ning Hikvision'dagi raqami 101
        users.put("vali", new User("vali", "vali123", "102")); // "vali"ning Hikvision'dagi raqami 102
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }
}