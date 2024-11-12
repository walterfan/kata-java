package com.fanyamin.kata;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.Random;

public class SimpleBloomFilter {
    private final BitSet bitSet;
    private final int bitArraySize;
    private final int numHashFunctions;
    private final int[] hashSeeds;

    /**
     * Constructor to initialize Bloom filter parameters.
     * 
     * @param bitArraySize      Size of the bit array.
     * @param numHashFunctions   Number of hash functions to use.
     */
    public SimpleBloomFilter(int bitArraySize, int numHashFunctions) {
        this.bitArraySize = bitArraySize;
        this.numHashFunctions = numHashFunctions;
        this.bitSet = new BitSet(bitArraySize);
        this.hashSeeds = generateRandomSeeds(numHashFunctions);
    }

    /**
     * Adds a string to the Bloom filter.
     * 
     * @param item Item to add to the filter.
     */
    public void add(String item) {
        for (int i = 0; i < numHashFunctions; i++) {
            int hashValue = getHash(item, hashSeeds[i]);
            bitSet.set(hashValue);
        }
    }

    /**
     * Checks if an item might be in the Bloom filter.
     * 
     * @param item Item to check.
     * @return true if the item might be in the filter; false if the item is definitely not in the filter.
     */
    public boolean mightContain(String item) {
        for (int i = 0; i < numHashFunctions; i++) {
            int hashValue = getHash(item, hashSeeds[i]);
            if (!bitSet.get(hashValue)) {
                return false; // If any bit is 0, item is definitely not in the filter
            }
        }
        return true; // Item might be in the filter
    }

    /**
     * Generates random seeds for the hash functions.
     * 
     * @param numSeeds Number of seeds to generate.
     * @return Array of random seeds.
     */
    private int[] generateRandomSeeds(int numSeeds) {
        int[] seeds = new int[numSeeds];
        Random random = new Random();
        for (int i = 0; i < numSeeds; i++) {
            seeds[i] = random.nextInt(Integer.MAX_VALUE);
        }
        return seeds;
    }

    /**
     * Generates a hash for a string with a given seed.
     * 
     * @param item Item to hash.
     * @param seed Seed for the hash function.
     * @return The hash value.
     */
    private int getHash(String item, int seed) {
        byte[] bytes = item.getBytes(StandardCharsets.UTF_8);
        int hash = 0;
        for (byte b : bytes) {
            hash = hash * seed + b;
        }
        return Math.abs(hash % bitArraySize); // Ensure hash is within bit array bounds
    }
}