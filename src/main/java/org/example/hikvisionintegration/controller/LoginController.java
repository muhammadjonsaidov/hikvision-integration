package org.example.hikvisionintegration.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.hikvisionintegration.dto.LoginRequest;
import org.example.hikvisionintegration.dto.LoginResponse;
import org.example.hikvisionintegration.service.LoginService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "1. Authentication", description = "Ikki bosqichli autentifikatsiya jarayoni") // Butun klass uchun teg
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Operation(
            summary = "1-Qadam: Login va Parol",
            description = "Foydalanuvchi login va parolini yuboradi. Muvaffaqiyatli bo'lsa, Face ID kutish holatiga o'tadi.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "So'rov muvaffaqiyatli, javobda keyingi qadam ko'rsatilgan",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class)))
            }
    )
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        return loginService.initiateLogin(loginRequest);
    }

    @Operation(
            summary = "3-Qadam: Autentifikatsiya statusini tekshirish",
            description = "Face ID tasdiqlangandan so'ng, ushbu endpoint orqali yakuniy natija va JWT token olinadi.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Joriy status (SUCCESS, PENDING_FACE_ID, FAILED) qaytariladi.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class)))
            }
    )
    @GetMapping("/status/{username}")
    public LoginResponse getStatus(@PathVariable String username) {
        return loginService.checkLoginStatus(username);
    }
}