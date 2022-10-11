package com.example.nasapic.controller;

import com.example.nasapic.service.PictureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PictureController {
    private final PictureService service;

    @GetMapping({"/mars/pictures/largest/{sol}", "/mars/pictures/largest/{sol}/{camera}"})
    public ResponseEntity<byte[]> getLargestPicture(@PathVariable String sol, @PathVariable(required = false) String camera) {
        var headers = new HttpHeaders();
        headers.add("content-type", "image/jpeg");
        return new ResponseEntity<>(service.getLargestPic(sol, camera), headers, HttpStatus.OK);
    }
}
