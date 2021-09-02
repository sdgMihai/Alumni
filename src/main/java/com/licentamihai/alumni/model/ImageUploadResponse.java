package com.licentamihai.alumni.model;

import lombok.Value;

@Value
public class ImageUploadResponse {
    String result = "OK";
    ImageData data;
    Integer numberOfImages = 1;
    String message = "upload successful";
    String path;

    public ImageUploadResponse(ImageData data, String path) {
        this.data = data;
        this.path = path;
    }
}
