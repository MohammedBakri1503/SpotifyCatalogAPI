package com.example.catalog;

import com.example.catalog.utils.LRUCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;


class LRUCacheTest {

    LRUCache<String, String> cache;

    @Nested
    @DisplayName("when instantiated with capacity 3")
    class WhenInstantiated {

        @BeforeEach
        void createNewCache() {
            cache = new LRUCache<>(3);
        }

        @Test
        @DisplayName("cache is initially empty")
        void isEmpty() {
            assertTrue(cache.isEmpty());

            // TODO assert cache is empty
        }

        @Test
        @DisplayName("throws NullPointerException when getting a null key")
        void throwsExceptionWhenGettingNullKey() {
            Exception exception = assertThrows(NullPointerException.class, () -> {
                cache.get(null);
            });
            // TODO assert NullPointerException thrown on `cache.get(null)`
        }

        @Nested
        @DisplayName("after adding 2 elements")
        class AfterAdding2Elements {

            @BeforeEach
            void addElements() {
                Exception exception = assertThrows(NullPointerException.class, () -> {
                    cache.set(null,"song");
                });
                Exception exception1 = assertThrows(NullPointerException.class, () -> {
                    cache.set(null,null);
                });
                cache.set("song1","Hello");
                cache.set("song2","Shape of You");

                // TODO add 2 elements
            }

            @Test
            @DisplayName("cache contains the added elements")
            void containsAddedElements() {
                assertFalse(cache.isEmpty());
                assertEquals(2, cache.size());
                assertEquals("Hello", cache.get("song1"));
                assertEquals("Shape of You", cache.get("song2"));


                // TODO assert the added 2 elements are available
            }
        }

        @Nested
        @DisplayName("after adding 3 elements")
        class AfterAdding3Elements {

            @BeforeEach
            void addElements() {
                cache.set("song1","Hello");
                cache.set("song2","Shape of You");
                cache.set("song3","Hi");
                assertEquals(3, cache.size());

                // TODO add 3 elements
            }

            @Nested
            @DisplayName("when cleared")
            class WhenCleared {

                // addElements (in AfterAdding3Elements) is executed and then clearCache
                // before EACH test case in WhenCleared


                @BeforeEach
                void clearCache() {
                    cache.clear();
                    //assertTrue(cache.isEmpty());


                    // TODO clear the cache after
                }
                @Test
                void isEmpty2() {
                    assertTrue(cache.isEmpty());



                }
            }
        }

    }
}
