package com.example.nasapic.service;

import lombok.experimental.UtilityClass;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@UtilityClass
public class NasaUrlUtils {
    private static final String URL = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos";
    private static final String KEY = "DEMO_KEY";

    public static String getUrl(String sol, String camera) {
        return UriComponentsBuilder.fromHttpUrl(URL)
                .queryParam("api_key", KEY)
                .queryParam("sol", sol)
                .queryParamIfPresent("camera", Optional.ofNullable(camera))
                .toUriString();
    }
}
