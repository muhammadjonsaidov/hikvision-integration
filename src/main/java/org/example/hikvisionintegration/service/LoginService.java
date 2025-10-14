package org.example.hikvisionintegration.service;

import org.example.hikvisionintegration.dto.LoginRequest;
import org.example.hikvisionintegration.dto.LoginResponse;
import org.example.hikvisionintegration.model.User;
import org.example.hikvisionintegration.repository.MockUserRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginService {

    private final MockUserRepository userRepository;

    // Face ID tekshiruvini kutayotgan foydalanuvchilarni saqlaydi (employeeNo -> username)
    private final Map<String, String> pendingVerification = new ConcurrentHashMap<>();

    // Tizimga kirish muvaffaqiyatli bo'lganlarni saqlaydi (username -> token)
    private final Map<String, String> successfulLogins = new ConcurrentHashMap<>();

    public LoginService(MockUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 1-QADAM: Login va parolni tekshirish
    public LoginResponse initiateLogin(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());

        if (userOptional.isPresent() && userOptional.get().getPassword().equals(request.getPassword())) {
            User user = userOptional.get();
            // Foydalanuvchini "kutish" ro'yxatiga qo'shamiz
            pendingVerification.put(user.getEmployeeNo(), user.getUsername());
            System.out.println("LOG: " + user.getUsername() + " uchun Face ID kutilmoqda (employeeNo: " + user.getEmployeeNo() + ")");
            return new LoginResponse("PENDING_FACE_ID", "Login/parol to'g'ri. Endi kameraga qarang.", null);
        }

        return new LoginResponse("FAILED", "Login yoki parol xato.", null);
    }

    // 2-QADAM: Kameradan kelgan employeeNo'ni tekshirish
    public void verifyFaceId(String employeeNo) {
        if (pendingVerification.containsKey(employeeNo)) {
            String username = pendingVerification.get(employeeNo);

            // "Dummy" JWT token yaratamiz
            String token = "dummy-jwt-token-" + UUID.randomUUID();

            // Foydalanuvchini muvaffaqiyatli ro'yxatga o'tkazamiz
            successfulLogins.put(username, token);

            // Kutish ro'yxatidan o'chiramiz
            pendingVerification.remove(employeeNo);
            System.out.println("LOG: " + username + " (employeeNo: " + employeeNo + ") yuz orqali tasdiqlandi. Token yaratildi.");
        } else {
            System.out.println("LOG: Noma'lum yoki kutilmagan employeeNo keldi: " + employeeNo);
        }
    }

    // 3-QADAM: Foydalanuvchi o'z statusini tekshiradi
    public LoginResponse checkLoginStatus(String username) {
        if (successfulLogins.containsKey(username)) {
            String token = successfulLogins.get(username);
            // Tokenni bir marta berganimizdan so'ng ro'yxatdan o'chiramiz
            successfulLogins.remove(username);
            return new LoginResponse("SUCCESS", "Tizimga xush kelibsiz!", token);
        }

        // Agar foydalanuvchi hali ham kutish ro'yxatida bo'lsa
        if (pendingVerification.containsValue(username)) {
            return new LoginResponse("PENDING_FACE_ID", "Hali ham Face ID tasdiqlanishi kutilmoqda...", null);
        }

        return new LoginResponse("FAILED", "Login sessiyasi topilmadi yoki vaqti tugadi.", null);
    }
}