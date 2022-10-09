package com.example.nasapic.controller;

import com.example.nasapic.service.PictureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PictureController {
    private final PictureService service;

    @GetMapping(value = {"/mars/pictures/largest/{sol}","/mars/pictures/largest/{sol}/{camera}"}
            ,produces = MediaType.IMAGE_JPEG_VALUE)
    public Mono<byte[]> getLargestPicture(@PathVariable String sol, @PathVariable(required = false) String camera) {
        return service.getLargestPic(sol,camera);
    }
}
