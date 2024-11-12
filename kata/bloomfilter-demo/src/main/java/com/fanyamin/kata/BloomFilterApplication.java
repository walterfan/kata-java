package com.fanyamin.kata;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class BloomFilterApplication implements CommandLineRunner {

    // BloomFilter to store dictionary words
    private BloomFilter<String> bloomFilter;

    // Set expected insertions and false positive probability
    private static final int EXPECTED_WORDS = 10000;
    private static final double FALSE_POSITIVE_PROBABILITY = 0.01;

    public static void main(String[] args) {
        SpringApplication.run(BloomFilterApplication.class, args);
    }

    @Override
    public void run(String... args) throws IOException {

        String dictionaryPath = "./wordlist.txt";
        if (args.length == 0) {
            initSimpleBloomFilter(dictionaryPath);
            return;
        }

        // Check if the dictionary path is provided as an argument
        if (args.length > 0) {
            dictionaryPath = args[0];
        }
        loadDictionaryAndInitializeBloomFilter(dictionaryPath);
        // Prompt user for words to check
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a word to check (or type 'quit' to quit):");
        while (true) {
            String word = scanner.nextLine().trim();
            if (word.equalsIgnoreCase("quit")) {
                break;
            }

            // Check if the word might exist in the dictionary
            if (bloomFilter.mightContain(word)) {
                System.out.println("The word might be correct.");
            } else {
                System.out.println("The word is definitely incorrect.");
            }
        }
        scanner.close();
    }

    /**
     * Loads a dictionary from a file and initializes the Bloom filter with it.
     * Each word from the dictionary is added to the Bloom filter.
     */


    private void loadDictionaryAndInitializeBloomFilter(String dictionaryPath) throws IOException {
        // Initialize Bloom filter with string funnel and defined capacity and false positive rate
        bloomFilter = BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8), EXPECTED_WORDS, FALSE_POSITIVE_PROBABILITY);
    
        // Read words from the specified dictionary file using UTF-8 encoding
        List<String> words = Files.readAllLines(Path.of(dictionaryPath), StandardCharsets.ISO_8859_1);
        for (String word : words) {
            bloomFilter.put(word.trim());
        }

        System.out.println("Bloom filter initialized with dictionary words from: " + dictionaryPath);
    }

    private void initSimpleBloomFilter(String dictionaryPath) throws IOException {
        // Define the size of the bit array and number of hash functions
        int bitArraySize = 1000;
        int numHashFunctions = 5;

        // Initialize the Bloom filter
        SimpleBloomFilter bloomFilter = new SimpleBloomFilter(bitArraySize, numHashFunctions);

        // Add items to the Bloom filter
        bloomFilter.add("apple");
        bloomFilter.add("banana");
        bloomFilter.add("orange");

        // Check for existence
        System.out.println("Might contain 'apple': " + bloomFilter.mightContain("apple"));  // Expected: true
        System.out.println("Might contain 'banana': " + bloomFilter.mightContain("banana")); // Expected: true
        System.out.println("Might contain 'grape': " + bloomFilter.mightContain("grape"));   // Expected: false or true (false positive possible)
        System.out.println("Might contain 'strawberry': " + bloomFilter.mightContain("strawberry")); // Expected: false or true
    }
}
