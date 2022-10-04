package com.example.nasapic.controller;

import com.example.nasapic.service.PictureService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PictureController {
    private final PictureService service;
    @Value("${nasa.api.url}")
    private String URL;
    @Value("${nasa.api.key}")
    private String KEY;

    @GetMapping({"/mars/pictures/largest/{sol}","/mars/pictures/largest/{sol}/{camera}"})
    public ResponseEntity<byte[]> getLargestPicture(@PathVariable String sol, @PathVariable(required = false) String camera) {
        var url = UriComponentsBuilder.fromHttpUrl(URL)
                .queryParam("api_key", KEY)
                .queryParam("sol", sol)
                .queryParamIfPresent("camera", Optional.ofNullable(camera))
                .toUriString();
        var headers = new HttpHeaders();
        headers.add("content-type","image/jpeg");
        return new ResponseEntity<>(service.getLargestPic(url).getImg(), headers, HttpStatus.OK);
    }
}
