package org.example.hikvisionintegration.controller;


import org.example.hikvisionintegration.dto.HikvisionEvent;
import org.example.hikvisionintegration.service.LoginService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class WebhookController {

    private final LoginService loginService; // QO'SHILDI

    public WebhookController(LoginService loginService) { // QO'SHILDI
        this.loginService = loginService;
    }

    @PostMapping(value = "/face-event", consumes = MediaType.APPLICATION_XML_VALUE)
    public void handleFaceEvent(@RequestBody HikvisionEvent event) {
        System.out.println("\n------------------- YANGI HODISA -------------------");

        if (event != null && event.getEmployeeNoString() != null) {
            String employeeNo = event.getEmployeeNoString();
            System.out.println("Kameradan xabar: employeeNo = " + employeeNo);

            // Asosiy mantiqni chaqiramiz
            loginService.verifyFaceId(employeeNo); // O'ZGARTIRILDI

        } else {
            System.out.println("⚠️ Ma'lumotni o'qishda xatolik yoki xodim raqami topilmadi.");
        }
        System.out.println("--------------------------------------------------\n");
    }
}