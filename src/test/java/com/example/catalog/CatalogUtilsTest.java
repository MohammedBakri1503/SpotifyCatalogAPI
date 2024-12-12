package com.example.catalog;

import com.example.catalog.utils.CatalogUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class CatalogUtilsTest {

    private CatalogUtils catalogUtils;
    private List<JsonNode> songs;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        catalogUtils = new CatalogUtils();
        objectMapper = new ObjectMapper();

        // Sample song data for testing. TODO - Add more songs
        String jsonData = """
[
    {
        "duration_ms": 200040,
        "name": "Blinding Lights",
        "popularity": 87,
        "album": {
            "name": "After Hours",
            "release_date": "2020-03-20",
            "total_tracks": 14
        },
        "artists": [
            {
                "name": "The Weeknd"
            }
        ]
    },
    {
        "duration_ms": 233000,
        "name": "Shape of You",
        "popularity": 89,
        "album": {
            "name": "÷ (Divide)",
            "release_date": "2017-03-03",
            "total_tracks": 12
        },
        "artists": [
            {
                "name": "Ed Sheeran"
            }
        ]
    },
    {
        "duration_ms": 215000,
        "name": "Someone You Loved",
        "popularity": 81,
        "album": {
            "name": "Divinely Uninspired to a Hellish Extent",
            "release_date": "2019-05-17",
            "total_tracks": 12
        },
        "artists": [
            {
                "name": "Lewis Capaldi"
            }
        ]
    },
    {
        "duration_ms": 180000,
        "name": "Watermelon Sugar",
        "popularity": 85,
        "album": {
            "name": "Fine Line",
            "release_date": "2019-12-13",
            "total_tracks": 12
        },
        "artists": [
            {
                "name": "Harry Styles"
            }
        ]
    },
    {
        "duration_ms": 210000,
        "name": "Sunflower",
        "popularity": 91,
        "album": {
            "name": "Spider-Man: Into the Spider-Verse",
            "release_date": "2018-10-18",
            "total_tracks": 13
        },
        "artists": [
            {
                "name": "Post Malone"
            },
            {
                "name": "Swae Lee"
            }
        ]
    },
    {
        "duration_ms": 245000,
        "name": "One More Time",
        "popularity": 82,
        "album": {
            "name": "Discovery",
            "release_date": "2001-03-07",
            "total_tracks": 14
        },
        "artists": [
            {
                "name": "Daft Punk"
            }
        ]
    },
    {
        "duration_ms": 290000,
        "name": "God's Plan",
        "popularity": 96,
        "album": {
            "name": "Scorpion",
            "release_date": "2018-06-29",
            "total_tracks": 25
        },
        "artists": [
            {
                "name": "Drake"
            }
        ]
    },
    {
        "duration_ms": 220000,
        "name": "Levitating",
        "popularity": 88,
        "album": {
            "name": "Future Nostalgia",
            "release_date": "2020-03-27",
            "total_tracks": 11
        },
        "artists": [
            {
                "name": "Dua Lipa"
            }
        ]
    }
]
""";

        songs = new ArrayList<>();
        objectMapper.readTree(jsonData).forEach(songs::add);
    }






    @Test
    void testSortSongsByName() {
        List<JsonNode> sortedSongs = catalogUtils.sortSongsByName(songs);

        assertEquals("Blinding Lights", sortedSongs.get(0).get("name").asText());
        assertEquals("God's Plan", sortedSongs.get(1).get("name").asText());
        assertEquals("Levitating", sortedSongs.get(2).get("name").asText());
        assertEquals("One More Time", sortedSongs.get(3).get("name").asText());
        assertEquals("Shape of You", sortedSongs.get(4).get("name").asText());

    }

    @Test
    void testFilterSongsByPopularity() {
        List<JsonNode> filteredSongs = catalogUtils.filterSongsByPopularity(songs, 95);

        assertEquals(1, filteredSongs.size());
        assertEquals("God's Plan", filteredSongs.get(0).get("name").asText());
    }

    @Test
    void testDoesSongExistByName() {
        assertTrue(catalogUtils.doesSongExistByName(songs, "Shape of You"));
        assertTrue(catalogUtils.doesSongExistByName(songs, "Levitating"));
        assertFalse(catalogUtils.doesSongExistByName(songs, "Hello"));

    }

    @Test
    void testCountSongsByArtist() {
        long count = catalogUtils.countSongsByArtist(songs, "Drake");

        assertEquals(1, count);

        count = catalogUtils.countSongsByArtist(songs, "The Weeknd");
        assertEquals(1, count);

        count = catalogUtils.countSongsByArtist(songs, "Unknown Artist");
        assertEquals(0, count);
    }

    @Test
    void testGetLongestSong() {
        JsonNode longestSong = catalogUtils.getLongestSong(songs);

        assertNotNull(longestSong);
        assertEquals("God's Plan", longestSong.get("name").asText());
    }

    @Test
    void testGetSongByYear() {
        List<JsonNode> songsByYear = catalogUtils.getSongByYear(songs, 2020);

        assertEquals(2, songsByYear.size());
        assertEquals("Blinding Lights", songsByYear.get(0).get("name").asText());
    }

    @Test
    void testGetMostRecentSong() {
        JsonNode mostRecentSong = catalogUtils.getMostRecentSong(songs);

        assertNotNull(mostRecentSong);
        assertEquals("Levitating", mostRecentSong.get("name").asText());
    }


}