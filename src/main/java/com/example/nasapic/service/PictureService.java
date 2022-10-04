package com.example.nasapic.service;

import com.example.nasapic.entity.Picture;
import com.fasterxml.jackson.databind.JsonNode;
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
    public Picture getLargestPic(String nasaUrl) {
        return template.getForObject(URI.create(nasaUrl), JsonNode.class)
                .get("photos")
                .findValues("img_src")
                .stream()
                .map(JsonNode::asText)
                .map(this::createPicture)
                .max(Comparator.comparing(Picture::getSize))
                .orElseThrow(() -> new NoSuchElementException("Cannot find largest picture!"));
    }

    private Picture createPicture(String imgSrc) {
        var picture = new Picture();
        picture.setImgSrc(imgSrc);
        picture.setSize(getPictureSize(picture.getImgSrc()));
        picture.setImg(getImgBytes(picture.getImgSrc()));
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
