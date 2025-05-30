package com.example.Final.service.Image;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;
import java.util.Map;

public class ImageUploadImgurService implements ImageUploadService{

    private static ImageUploadImgurService imageUploadInstance;

    private ImageUploadImgurService(){}

    public static synchronized ImageUploadImgurService getInstance(){
        if(imageUploadInstance == null){
            imageUploadInstance = new ImageUploadImgurService();
        }
        return imageUploadInstance;
    }

    public String uploadImage(MultipartFile image) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.imgur.com/3/image";

        String base64Image = Base64.getEncoder().encodeToString(image.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        String accessToken = "825c6d32b1d3b4781692a8648e87273cd0889cf9";
        headers.setBearerAuth(accessToken);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("image", base64Image);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        // Send the POST request
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            // Extract the "link" from the response body
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && (Boolean) responseBody.get("success")) {
                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                return (String) data.get("link");
            } else {
                throw new Exception("Image upload failed.");
            }
        } else {
            throw new Exception("Failed to upload image: " + response.getStatusCode());
        }
    }
}
