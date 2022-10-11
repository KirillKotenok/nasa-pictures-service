package com.example.nasapic.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PhotoContainer {
    private List<Photo> photos = new ArrayList<>();
}
