package com.example.nasapic.service;

import com.example.nasapic.entity.Picture;
import com.fasterxml.jackson.databind.JsonNode;
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
    @Cacheable("nasaUrl")
    public Mono<byte[]> getLargestPic(String sol, String camera) {
        return WebClient.builder().build()
                .method(HttpMethod.GET)
                .uri(URI.create(UrlService.getUrl(sol, camera)))
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(JsonNode.class))
                .flatMapIterable(node -> node.get("photos")
                        .findValues("img_src"))
                .map(JsonNode::asText)
                .map(this::createPicture)
                .sort(Comparator.comparing(Picture::getSize))
                .last()
                .map(picture -> getImgBytes(picture.getImgSrc()));
    }

    private Picture createPicture(String imgSrc) {
        var picture = new Picture();
        picture.setImgSrc(imgSrc);
        picture.setSize(getPictureSize(picture.getImgSrc()));
        return picture;
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
