package com.example.lostandfound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.lostandfound.Utility.Hasher;

import org.junit.Test;


public class HasherTest {

    private static final String TEXT = "HSwe9d(S*";
    private static final String HASHED_TEXT = "1a8c06e5272bd31340e4e376988e4054f2787b45f46bd84b3a338448d3b07053";

    @Test
    public void testHash() {
        assertEquals(HASHED_TEXT, Hasher.hash(TEXT));
    }

    @Test
    public void testCompareHash() {
        assertTrue(Hasher.compareHash(TEXT, HASHED_TEXT));
        assertFalse(Hasher.compareHash(TEXT, TEXT));
    }
}