// src/main/java/com/example/catalog/services/DatabaseDataSourceService.java
package com.example.catalog.services;


import com.example.catalog.model.Artist;
import com.example.catalog.services.DataSourceService;
import org.springframework.stereotype.Service;

@Service
public class DatabaseDataSourceService implements DataSourceService {

    @Override
    public Artist getArtistById(String id) {
        return new Artist();
        //return db.findById(id).orElse(null);
    }
}