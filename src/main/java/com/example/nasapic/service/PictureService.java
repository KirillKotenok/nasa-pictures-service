package com.example.nasapic.service;

import com.example.nasapic.entity.Photo;
import com.example.nasapic.entity.PhotoContainer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class PictureService {
    private final RestTemplate template;

    @SneakyThrows
    @Cacheable("nasa")
    public Mono<byte[]> getLargestPhoto(String sol, String camera) {
        return WebClient.builder().build()
                .method(HttpMethod.GET)
                .uri(URI.create(UrlService.getUrl(sol, camera)))
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(PhotoContainer.class))
                .flatMapIterable(PhotoContainer::getPhotos)
                .parallel()
                .doOnNext(this::getPhotoSize)
                .sorted(Comparator.comparing(Photo::getSize))
                .last()
                .map(picture -> getImgBytes(picture.getImgSrc()));
    }


    private byte[] getImgBytes(String imgSrc) {
        var redirectLocation = template.headForHeaders(URI.create(imgSrc)).getLocation();
        return template.getForObject(redirectLocation, byte[].class);
    }

    private void getPhotoSize(Photo photo) {
        var redirectUrl = template.headForHeaders(URI.create(photo.getImgSrc())).getLocation();
        var size = Long.parseLong(template.headForHeaders(URI.create(redirectUrl.toString())).getFirst("Content-length"));
        photo.setSize(size);
    }
}
