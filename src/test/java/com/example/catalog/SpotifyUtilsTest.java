package com.example.catalog;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.example.catalog.utils.SpotifyUtils.isValidId;
import static com.example.catalog.utils.SpotifyUtils.isValidURI;
import static com.example.catalog.utils.SpotifyUtils.isValidId;
import static com.example.catalog.utils.SpotifyUtils.getSpotifyClient;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

//@Disabled("Should be enabled for Junit exercises")
public class SpotifyUtilsTest {

    @Test
    public void testValidId() {
        assertTrue(isValidId("6rqhFgbbKwnb9MLmUQDhG6")); // valid Spotify ID
        assertTrue(isValidId("1a2B3c4D5e6F7g8H9iJkL0mN")); // valid 22 character ID
        assertTrue(isValidId("a1b2C3d4E5f6G7h8I9jK0L1m2N")); // valid 30 character ID
    }
    //@Disabled("Should be enabled for Junit exercises")

    @Test
    public void testInvalidId() {
        assertFalse(isValidId(null)); // null ID
        assertFalse(isValidId("")); // empty ID
        assertFalse(isValidId("shortID")); // too short ID (less than 15 characters)
        assertFalse(isValidId("thisIDiswaytoolongtobevalidkkkk")); // too long ID (more than 30 characters)
        assertFalse(isValidId("!@#$$%^&*()_+")); // invalid characters
        assertFalse(isValidId("1234567890abcdefGHIJKLMNO!@#")); // includes invalid characters
    }


    @Test
    public void testValidUri() {
        assertTrue(isValidURI("spotify:track:6rqhFgbbKwnb9MLmUQDhG6")); // valid Spotify ID
        assertTrue(isValidURI("spotify:track:1a2B3c4D5e6F7g8H9iJkL0mN")); // valid 22 character ID
        assertTrue(isValidURI("spotify:track:a1b2C3d4E5f6G7h8I9jK0L1m2N")); // valid 30 character ID

    }

    @Test
    public void testInvalidUri() {
        assertFalse(isValidURI(null)); // null URI
        assertFalse(isValidURI("")); // empty URI
        assertFalse(isValidURI("spotify:track:shortID")); // too short ID (less than 15 characters)
        assertFalse(isValidURI("spotify:track:thisIDiswaytoolongtobevalidkkkk")); // too long ID (more than 30 characters)
        assertFalse(isValidURI("spotify:track:!@#$$%^&*()_+")); // invalid characters
        assertFalse(isValidURI("spotify:track:1234567890abcdefGHIJKLMNO!@#")); // includes invalid characters
        assertFalse(isValidURI("track:1234567890abcdefGHIJKLMNO!@#")); //DOES NOT include spotify
        assertFalse(isValidURI("spotify:1234567890abcdefGHIJKLMNO!@#"));  //DOES NOT include track
        assertFalse(isValidURI("1234567890abcdefGHIJKLMNO!@#"));  //DOES NOT include spotify and track

    }
    @Test
    public void testGetSpotifyClient() {
        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            getSpotifyClient("", "validSecret");
        });
        assertEquals("Invalid client ID or secret.", exception1.getMessage());

        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            getSpotifyClient("validid", "");
        });
        assertEquals("Invalid client ID or secret.", exception2.getMessage());

        Exception exception3 = assertThrows(IllegalArgumentException.class, () -> {
            getSpotifyClient("", "");
        });
        assertEquals("Invalid client ID or secret.", exception3.getMessage());

        Exception exception4 = assertThrows(IllegalArgumentException.class, () -> {
            getSpotifyClient(null, "validSecret");
        });
        assertEquals("Invalid client ID or secret.", exception4.getMessage());

    }

}
