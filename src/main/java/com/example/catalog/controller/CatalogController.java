package com.example.catalog.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import java.io.File;
import com.example.catalog.utils.SpotifyUtils;

@RestController
public class CatalogController {

    private final ObjectMapper objectMapper = new ObjectMapper();


    private ResponseEntity<JsonNode> createErrorResponse(HttpStatus status, String message) {
        ObjectNode errorResponse = objectMapper.createObjectNode();
        ObjectNode errorDetails = objectMapper.createObjectNode();
        errorDetails.put("status", status.value());
        errorDetails.put("message", message);
        errorResponse.set("error", errorDetails);
        return new ResponseEntity<>(errorResponse, status);
    }


    @GetMapping("/popularSongs")
    public ResponseEntity<JsonNode> getPopularSongs() throws IOException {
        try {
            ClassPathResource resource = new ClassPathResource("data/popular_songs.json");
            File file = resource.getFile();

            JsonNode songs = objectMapper.readTree(file);
            if (songs.isEmpty()) {
                // Simulate 503 Service Unavailable when the list is empty
                return createErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, "Temporary service unavailability");
            }

            return new ResponseEntity<>(songs, HttpStatus.OK);

        } catch (IOException e) {
            // Internal server error if file read fails
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to read popular songs data");
        }

        //ClassPathResource resource = new ClassPathResource("data/popular_songs.json");
    }

    @GetMapping("/popularArtists")
    public ResponseEntity<JsonNode> getPopularArtists() throws IOException {
        try {
            ClassPathResource resource = new ClassPathResource("data/popular_artists.json");
            File file = resource.getFile();
            JsonNode artists = objectMapper.readTree(file);
            return new ResponseEntity<>(artists, HttpStatus.OK);
        } catch (IOException e) {
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to read popular artists data");
        }
    }




    @GetMapping("/albums/{id}")
    public ResponseEntity<JsonNode> getAlbumById(@PathVariable String id) throws IOException {
        try {
            // Simulate 403 Forbidden if ID is all zeros

            if (id.matches("0+")) {
                return createErrorResponse(HttpStatus.FORBIDDEN, "Access to this album ID is restricted");
            }

            // Handle invalid ID format
            if (!SpotifyUtils.isValidId(id)) {
                return createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid ID");
            }



            // Read albums data
            ClassPathResource resource = new ClassPathResource("data/albums.json");
            File file = resource.getFile();
            JsonNode albums = objectMapper.readTree(file);

            // Find album by ID
            JsonNode album = albums.get(id);
            if (album != null) {
                return new ResponseEntity<>(album, HttpStatus.OK);
            } else {
                // 404 if album ID does not exist
                return createErrorResponse(HttpStatus.NOT_FOUND, "Album not found");
            }
        } catch (IOException e) {
            // Internal server error if file read fails
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to read albums data");
        }
    }

}