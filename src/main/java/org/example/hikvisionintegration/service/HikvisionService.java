package org.example.hikvisionintegration.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.example.hikvisionintegration.dto.UserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class HikvisionService {

    @Value("${hikvision.ip}") private String hikvisionIp;
    @Value("${hikvision.port}") private int hikvisionPort;
    @Value("${hikvision.username}") private String username;
    @Value("${hikvision.password}") private String password;
    @Value("${app.server.ip}") private String serverIp;
    @Value("${server.port}") private int serverPort;

    private final XmlMapper xmlMapper = new XmlMapper();

    public void configureWebhook() {
        String apiUrl = String.format("http://%s:%d/ISAPI/Event/notification/httpHosts/1", hikvisionIp, hikvisionPort);
        String webhookUrl = String.format("http://%s:%d/api/face-event", serverIp, serverPort);

        String xmlPayload = String.format(
            "<HttpHost version=\"2.0\" xmlns=\"http://www.isapi.org/ver20/XMLSchema\">" +
            "<id>1</id>" +
            "<url>%s</url>" +
            "<protocolType>HTTP</protocolType>" +
            "<addressingFormatType>ipaddress</addressingFormatType>" +
            "</HttpHost>", webhookUrl);

        System.out.println("Hikvision qurilmasini sozlanmoqda...");
        System.out.println("Webhook manzili sifatida o'rnatiladi: " + webhookUrl);

        BasicCredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(
            new AuthScope(hikvisionIp, hikvisionPort),
            new UsernamePasswordCredentials(username, password.toCharArray())
        );

        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(provider).build()) {
            HttpPut request = new HttpPut(apiUrl);
            request.setHeader("Content-Type", "application/xml");
            request.setEntity(new StringEntity(xmlPayload));

            httpClient.execute(request, response -> {
                int statusCode = response.getCode();
                if (statusCode == 200) {
                    System.out.println("✅ Muvaffaqiyatli! Hikvision qurilmasi endi hodisalarni sizning serveringizga yuboradi.");
                } else {
                    System.err.println("❌ Xatolik! Qurilmani sozlashda muammo yuz berdi. Status kodi: " + statusCode);
                    System.err.println("Javob: " + new String(response.getEntity().getContent().readAllBytes()));
                }
                return null;
            });
        } catch (IOException e) {
            System.err.println("❌ Ulanishda xatolik: " + e.getMessage());
        }
    }


    /**
     * Hikvision qurilmasiga yangi foydalanuvchi qo'shadi.
     */
    public boolean addUser(UserInfo userInfo) {
        String apiUrl = String.format("http://%s:%d/ISAPI/AccessControl/UserInfo/Record?format=xml", hikvisionIp, hikvisionPort);
        try {
            String xmlPayload = xmlMapper.writeValueAsString(userInfo);

            // Digest Auth bilan so'rov yuborish logikasi (configureWebhook'dan ko'chirilgan)
            CredentialsProvider provider = createCredentialsProvider();
            try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(provider).build()) {
                HttpPut request = new HttpPut(apiUrl);
                request.setHeader("Content-Type", "application/xml");
                request.setEntity(new StringEntity(xmlPayload));

                return httpClient.execute(request, response -> {
                    int statusCode = response.getCode();
                    if (statusCode == 200) {
                        System.out.println("✅ Foydalanuvchi muvaffaqiyatli qo'shildi: " + userInfo.getEmployeeNo());
                        return true;
                    } else {
                        System.err.println("❌ Foydalanuvchi qo'shishda xatolik! Status: " + statusCode);
                        System.err.println("Javob: " + new String(response.getEntity().getContent().readAllBytes()));
                        return false;
                    }
                });
            }
        } catch (IOException e) {
            System.err.println("❌ So'rov yuborishda xatolik: " + e.getMessage());
            return false;
        }
    }

    /**
     * Foydalanuvchiga yuz rasmini yuklaydi. (multipart/form-data)
     */
    public boolean uploadFaceImage(String employeeNo, InputStream imageStream) {
        String apiUrl = String.format("http://%s:%d/ISAPI/Intelligent/FDLib/FDSetUp?format=xml", hikvisionIp, hikvisionPort);

        String xmlMeta = String.format("<FaceData><employeeNo>%s</employeeNo></FaceData>", employeeNo);

        HttpEntity multipartEntity = MultipartEntityBuilder.create()
                .addTextBody("FaceData", xmlMeta, ContentType.APPLICATION_XML)
                .addBinaryBody("FaceImage", imageStream, ContentType.IMAGE_JPEG, "face.jpg")
                .build();

        CredentialsProvider provider = createCredentialsProvider();
        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(provider).build()) {
            HttpPost request = new HttpPost(apiUrl);
            request.setEntity(multipartEntity);

            return httpClient.execute(request, response -> {
                int statusCode = response.getCode();
                if (statusCode == 200) {
                    System.out.println("✅ Rasm muvaffaqiyatli yuklandi: " + employeeNo);
                    return true;
                } else {
                    System.err.println("❌ Rasm yuklashda xatolik! Status: " + statusCode);
                    System.err.println("Javob: " + new String(response.getEntity().getContent().readAllBytes()));
                    return false;
                }
            });
        } catch (IOException e) {
            System.err.println("❌ Rasm yuklash so'rovida xatolik: " + e.getMessage());
            return false;
        }
    }

    /**
     * Foydalanuvchini employeeNo bo'yicha o'chiradi.
     */
    public boolean deleteUser(String employeeNo) {
        String apiUrl = String.format("http://%s:%d/ISAPI/AccessControl/UserInfo/Delete?format=xml", hikvisionIp, hikvisionPort);
        String xmlPayload = String.format("<UserInfoDelCond><EmployeeNoList><employeeNo>%s</employeeNo></EmployeeNoList></UserInfoDelCond>", employeeNo);

        CredentialsProvider provider = createCredentialsProvider();
        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(provider).build()) {
            HttpPut request = new HttpPut(apiUrl);
            request.setHeader("Content-Type", "application/xml");
            request.setEntity(new StringEntity(xmlPayload));

            return httpClient.execute(request, response -> {
                int statusCode = response.getCode();
                if (statusCode == 200) {
                    System.out.println("✅ Foydalanuvchi o'chirildi: " + employeeNo);
                    return true;
                } else {
                    System.err.println("❌ Foydalanuvchi o'chirishda xatolik! Status: " + statusCode);
                    System.err.println("Javob: " + new String(response.getEntity().getContent().readAllBytes()));
                    return false;
                }
            });
        } catch (IOException e) {
            System.err.println("❌ O'chirish so'rovida xatolik: " + e.getMessage());
            return false;
        }
    }

    // Yordamchi metod (takrorlanishni oldini olish uchun)
    private CredentialsProvider createCredentialsProvider() {
        BasicCredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(
                new AuthScope(hikvisionIp, hikvisionPort),
                new UsernamePasswordCredentials(username, password.toCharArray())
        );
        return provider;
    }
}