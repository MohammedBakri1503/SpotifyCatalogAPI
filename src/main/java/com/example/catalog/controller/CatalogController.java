package com.example.catalog.controller;

import com.example.catalog.model.Artist;
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
import java.util.ArrayList;

import java.io.File;
import com.example.catalog.utils.SpotifyUtils;



import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import com.example.catalog.utils.CatalogUtils;

import java.util.Optional;
import java.util.stream.Collectors;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.Paths;

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
    public ResponseEntity<JsonNode> getPopularSongs(@RequestParam(defaultValue = "0") int offset,
                                                    @RequestParam(defaultValue = "-1") int limit) throws IOException {
        try {
            ClassPathResource resource = new ClassPathResource("data/popular_songs.json");
            File file = resource.getFile();

            List<JsonNode> songs = new ArrayList<>();
            objectMapper.readTree(file).elements().forEachRemaining(songs::add);

            if (songs.isEmpty()) {
                return createErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, "Temporary service unavailability");
            }

            // Apply offset and limit for pagination
            int start = Math.max(0, offset);
            int end = (limit > 0) ? Math.min(start + limit, songs.size()) : songs.size();
            List<JsonNode> paginatedSongs = songs.subList(start, end);

            return ResponseEntity.ok(objectMapper.valueToTree(paginatedSongs));

        } catch (IOException e) {
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to read popular songs data");
        }
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


    @GetMapping("/popularSongs/filter")
    public ResponseEntity<List<JsonNode>> filterSongs(
            @RequestParam Optional<String> name,
            @RequestParam Optional<Integer> minPopularity) {
        try {
            ClassPathResource resource = new ClassPathResource("data/popular_songs.json");
            File file = resource.getFile();
            List<JsonNode> songs = new ArrayList<>();
            objectMapper.readTree(file)
                    .elements()
                    .forEachRemaining(songs::add);

            // Filter by name if present
            if (name.isPresent()) {
                String nameFilter = name.get();
                songs = songs.stream()
                        .filter(song -> song.has("name") && song.get("name").asText().equalsIgnoreCase(nameFilter))
                        .collect(Collectors.toList());
            }

            // Filter by minimum popularity if present
            if (minPopularity.isPresent()) {
                int popularityFilter = minPopularity.get();
                songs = songs.stream()
                        .filter(song -> song.has("popularity") && song.get("popularity").asInt() >= popularityFilter)
                        .collect(Collectors.toList());
            }

            return ResponseEntity.ok(songs);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ArrayList<>());
        }
    }

    @GetMapping("/songs/mostRecent")
    public ResponseEntity<JsonNode> getMostRecentSong() {
        try {
            ClassPathResource resource = new ClassPathResource("data/popular_songs.json");
            File file = resource.getFile();
            JsonNode albums = objectMapper.readTree(file);

            List<JsonNode> songs  = new ArrayList<>();
            objectMapper.readTree(file)
                    .elements()
                    .forEachRemaining(songs::add);

            JsonNode mostRecentSong = CatalogUtils.getMostRecentSong(songs);

            return mostRecentSong != null ? new ResponseEntity<>(mostRecentSong, HttpStatus.OK) :
                    createErrorResponse(HttpStatus.NOT_FOUND, "No recent song found");
        } catch (IOException e) {
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to process data");
        }
    }
    @GetMapping("/songs/longest")
    public ResponseEntity<JsonNode> getLongestSong() {
        try {
            ClassPathResource resource = new ClassPathResource("data/popular_songs.json");
            File file = resource.getFile();
            List<JsonNode> songs = new ArrayList<>();
            objectMapper.readTree(file)
                    .elements()
                    .forEachRemaining(songs::add);

            JsonNode longestSong = CatalogUtils.getLongestSong(songs);
            return longestSong != null ? ResponseEntity.ok(longestSong) :
                    createErrorResponse(HttpStatus.NOT_FOUND, "No longest song found");
        } catch (IOException e) {
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to process data");
        }
    }



@GetMapping("/artists/{id}")
    public ResponseEntity<Artist> getArtistById(@PathVariable String id) throws IOException {
        if (! SpotifyUtils.isValidId(id)) {
            return ResponseEntity.badRequest().build();
        }

        ClassPathResource resource = new ClassPathResource("data/popular_artists.json");
        JsonNode artists = objectMapper.readTree(resource.getFile());

        JsonNode artistNode = artists.get(id);
        if (artistNode == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return  ResponseEntity.ok(objectMapper.treeToValue(artistNode, Artist.class));
    }


    @PostMapping("/artists")
    public ResponseEntity<Void> addArtist(@RequestBody Artist artist) throws IOException {
        ClassPathResource resource = new ClassPathResource("data/popular_artists.json");
        File file = resource.getFile();

        // Read the existing data
        JsonNode artistsNode = objectMapper.readTree(file);

        // Check if the artist already exists
        if (artistsNode.has(artist.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409 Conflict if the artist exists
        }

        // Convert Artist to JsonNode and add to the root JSON
        ((ObjectNode) artistsNode).set(artist.getId(), objectMapper.valueToTree(artist));

        // Write the updated JSON back to the file
        Files.write(Paths.get(file.toURI()),
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(artistsNode),
                StandardOpenOption.TRUNCATE_EXISTING);

        return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 Created
    }

}

