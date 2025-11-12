package com.example.Ejemplo.services;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
public class ImgBBUploader {
    private final String IMGBB_API_KEY = "36b51553bbc62283b58273f943f6d2be"; // ← reemplaza con la tuya

    public String subirImagen(MultipartFile imagen) throws IOException {
        byte[] bytes = imagen.getBytes();
        String base64Image = Base64.getEncoder().encodeToString(bytes);

        String url = "https://api.imgbb.com/1/upload?key=" + IMGBB_API_KEY;

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("image", base64Image);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            // Extraer el enlace de la respuesta JSON
            String body = response.getBody();
            int start = body.indexOf("\"url\":\"") + 7;
            int end = body.indexOf("\"", start);
            return body.substring(start, end).replace("\\/", "/");
        }

        throw new RuntimeException("Error al subir imagen a ImgBB");
    }
}