package com.example.catalog.services;// src/main/java/com/example/catalog/services/DataSourceService.java

import com.example.catalog.model.Artist;

import java.io.IOException;

public interface DataSourceService {
    Artist getArtistById(String id) throws IOException;
    


}