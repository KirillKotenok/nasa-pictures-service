package com.example.nasapic.service;

import com.example.nasapic.entity.Photo;
import com.example.nasapic.entity.Photos;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Comparator;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PictureService {
    private final RestTemplate template;

    @SneakyThrows
    @Cacheable("nasaUrl")
    public byte[] getLargestPic(String sol, String camera) {
        return getImgBytes(template.getForObject(URI.create(NasaUrlUtils.getUrl(sol, camera)), Photos.class).getPhotos()
                .stream()
                .peek(this::createPhoto)
                .max(Comparator.comparing(Photo::getSize))
                .orElseThrow(() -> new NoSuchElementException("No pictures found!")).getUrl());
    }

    private void createPhoto(Photo photo) {
        photo.setSize(getPictureSize(photo.getUrl()));
    }

    private byte[] getImgBytes(String imgSrc) {
        var redirectLocation = template.headForHeaders(URI.create(imgSrc)).getLocation();
        return template.getForObject(redirectLocation, byte[].class);
    }

    private Long getPictureSize(String imgSrc) {
        var redirectUrl = template.headForHeaders(URI.create(imgSrc)).getLocation();
        var size = Long.parseLong(template.headForHeaders(URI.create(redirectUrl.toString())).getFirst("Content-length"));
        return size;
    }
}
