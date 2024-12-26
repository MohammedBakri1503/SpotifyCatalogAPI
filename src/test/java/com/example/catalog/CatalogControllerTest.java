package com.example.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.catalog.model.Artist;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.List;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetPopularSongs() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/popularSongs?offset=0&limit=5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("0VjIjW4GlUZAMYd2vXMi3b"))
                .andExpect(jsonPath("$[0].name").value("Blinding Lights"))
                .andExpect(jsonPath("$[0].popularity").value(87));
    }

    @Test
    public void testGetPopularArtists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/popularArtists"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.1Xyo4u8uXC1ZmMpatF05PJ.name").value("The Weeknd"))
                .andExpect(jsonPath("$.1Xyo4u8uXC1ZmMpatF05PJ.followers").value(95105223))
                .andExpect(jsonPath("$.1Xyo4u8uXC1ZmMpatF05PJ.popularity").value(96));
    }

    @Test
    public void testGetAlbumByIdValid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/albums/4yP0hdKOZPNshxUOjY0cZj"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("4yP0hdKOZPNshxUOjY0cZj"))
                .andExpect(jsonPath("$.name").value("After Hours"))
                .andExpect(jsonPath("$.release_date").value("2020-03-20"))
                .andExpect(jsonPath("$.total_tracks").value(14));
    }

    @Test
    public void testGetAlbumByIdInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/albums/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAlbumByIdNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/albums/000000000"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testFilterSongs() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/popularSongs/filter?name=Heat Waves&minPopularity=7"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Heat Waves"))
                .andExpect(jsonPath("$[0].popularity").value(7));
    }

    @Test
    public void testGetMostRecentSong() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/songs/mostRecent"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("2yRiJCQXmFu5gqY00gmQhQ"))
                .andExpect(jsonPath("$.name").value("Not Okay"));
    }

    @Test
    public void testGetLongestSong() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/songs/longest"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("4u7EnebtmKWzUH433cf5Qv"))
                .andExpect(jsonPath("$.duration_ms").value(354320));
    }

    @Test
    public void testGetArtistByIdValid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/artists/1Xyo4u8uXC1ZmMpatF05PJ"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("The Weeknd"))
                .andExpect(jsonPath("$.followers").value(95105223));
    }

    @Test
    public void testGetArtistByIdInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/artists/invalidId"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddArtist() throws Exception {
        Artist artist = new Artist();
        artist.setId("newArtistId3");
        artist.setName("New Artist");
        artist.setFollowers(1000);
        artist.setPopularity(80);
        artist.setGenres(List.of("pop"));

        String artistJson = objectMapper.writeValueAsString(artist);

        mockMvc.perform(MockMvcRequestBuilders.post("/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(artistJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void testAddArtistConflict() throws Exception {
        Artist artist = new Artist();
        artist.setId("1Xyo4u8uXC1ZmMpatF05PJ"); // Existing artist ID
        artist.setName("The Weeknd");

        String artistJson = objectMapper.writeValueAsString(artist);

        mockMvc.perform(MockMvcRequestBuilders.post("/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(artistJson))
                .andExpect(status().isConflict());
    }
}
