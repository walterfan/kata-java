package com.fanyamin.kata;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class BloomFilterApplicationTests {

    private BloomFilter<String> bloomFilter;

    // Expected test data
    private static final int EXPECTED_WORDS = 10;
    private static final double FALSE_POSITIVE_PROBABILITY = 0.01;

    // Sample dictionary words
    private static final String[] DICTIONARY_WORDS = {"apple", "banana", "orange", "grape", "melon"};
    private static final String WORD_IN_DICTIONARY = "apple";
    private static final String WORD_NOT_IN_DICTIONARY = "strawberry";

    @BeforeEach
    public void setUp() {
        // Initialize the Bloom filter with the test configuration
        bloomFilter = BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8), EXPECTED_WORDS, FALSE_POSITIVE_PROBABILITY);

        // Add words to the Bloom filter
        for (String word : DICTIONARY_WORDS) {
            bloomFilter.put(word);
        }
    }

    @Test
    public void testWordInDictionary() {
        // Check if a word in the dictionary might be recognized as correct
        assertTrue(bloomFilter.mightContain(WORD_IN_DICTIONARY), "The word should be recognized as correct");
    }

    @Test
    public void testWordNotInDictionary() {
        // Check if a word not in the dictionary is recognized as incorrect
        assertFalse(bloomFilter.mightContain(WORD_NOT_IN_DICTIONARY), "The word should be recognized as incorrect");
    }

    @Test
    public void testFalsePositiveRate() {
        int falsePositives = 0;
        int totalChecks = 1000;

        // Check random words not in the dictionary to verify the false positive rate
        for (int i = 0; i < totalChecks; i++) {
            String randomWord = "random" + i;
            if (bloomFilter.mightContain(randomWord)) {
                falsePositives++;
            }
        }

        double falsePositiveRate = (double) falsePositives / totalChecks;
        assertTrue(falsePositiveRate <= FALSE_POSITIVE_PROBABILITY, "False positive rate is within expected bounds");
    }
}
