package org.example.hikvisionintegration.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.hikvisionintegration.dto.UserInfo;
import org.example.hikvisionintegration.service.HikvisionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
@Tag(name = "2. User Management", description = "Hikvision qurilmasidagi foydalanuvchilarni boshqarish")
public class UserController {

    private final HikvisionService hikvisionService;

    public UserController(HikvisionService hikvisionService) {
        this.hikvisionService = hikvisionService;
    }

    @Operation(summary = "Yangi foydalanuvchi yaratish", description = "Qurilma xotirasiga yangi foydalanuvchi ma'lumotlarini (ism, xodim raqami) qo'shadi.")
    @ApiResponse(responseCode = "200", description = "Foydalanuvchi muvaffaqiyatli yaratildi")
    @ApiResponse(responseCode = "400", description = "Xatolik yuz berdi", content = @Content)
    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody UserInfo userInfo) {
        boolean success = hikvisionService.addUser(userInfo);
        if (success) {
            return ResponseEntity.ok("Foydalanuvchi muvaffaqiyatli yaratildi.");
        }
        return ResponseEntity.badRequest().body("Foydalanuvchi yaratishda xatolik.");
    }

    @Operation(
            summary = "Foydalanuvchiga yuz rasmini yuklash",
            description = "Mavjud foydalanuvchiga (employeeNo orqali) yuzining rasmini JPEG formatida yuklaydi."
    )
    @ApiResponse(responseCode = "200", description = "Rasm muvaffaqiyatli yuklandi")
    @ApiResponse(responseCode = "400", description = "Fayl bo'sh yoki xatolik yuz berdi", content = @Content)
    @PostMapping("/{employeeNo}/face")
    public ResponseEntity<String> uploadFace(
            @PathVariable String employeeNo,
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Rasm fayli bo'sh bo'lishi mumkin emas.");
        }
        try {
            boolean success = hikvisionService.uploadFaceImage(employeeNo, file.getInputStream());
            if (success) {
                return ResponseEntity.ok("Rasm muvaffaqiyatli yuklandi.");
            }
            return ResponseEntity.badRequest().body("Rasm yuklashda xatolik.");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Faylni o'qishda xatolik: " + e.getMessage());
        }
    }

    @Operation(summary = "Foydalanuvchini o'chirish", description = "Foydalanuvchini xodim raqami (employeeNo) orqali qurilma xotirasidan o'chiradi.")
    @ApiResponse(responseCode = "200", description = "Foydalanuvchi muvaffaqiyatli o'chirildi")
    @ApiResponse(responseCode = "400", description = "Xatolik yuz berdi", content = @Content)
    @DeleteMapping("/{employeeNo}")
    public ResponseEntity<String> deleteUser(@PathVariable String employeeNo) {
        boolean success = hikvisionService.deleteUser(employeeNo);
        if (success) {
            return ResponseEntity.ok("Foydalanuvchi muvaffaqiyatli o'chirildi.");
        }
        return ResponseEntity.badRequest().body("Foydalanuvchini o'chirishda xatolik.");
    }
}