package com.java.streams;

import java.util.*;
import java.util.stream.*;

/**
 * ============================================================
 *  STREAMS QUICK REFERENCE - CODING SIGNAL PATTERNS
 * ============================================================
 *  Copy-paste ready patterns for timed assessments.
 *  Each pattern is self-contained and runs in main().
 * ============================================================
 */
public class StreamsQuickRef {

    public static void main(String[] args) {

        List<int[]> intervals = List.of(new int[]{1,3}, new int[]{2,6}, new int[]{8,10});
        List<Integer> nums = List.of(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5);
        List<String> words = List.of("hello", "world", "hello", "java", "world", "hello");

        // ──────────────────────────────────────
        //  PATTERN 1: Frequency Map (SUPER COMMON)
        // ──────────────────────────────────────
        Map<Integer, Long> freq = nums.stream()
                .collect(Collectors.groupingBy(n -> n, Collectors.counting()));
        System.out.println("Freq: " + freq);

        // ──────────────────────────────────────
        //  PATTERN 2: Top K frequent elements
        // ──────────────────────────────────────
        int k = 2;
        List<Integer> topK = freq.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(k)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        System.out.println("Top " + k + " frequent: " + topK);

        // ──────────────────────────────────────
        //  PATTERN 3: Remove duplicates preserving order
        // ──────────────────────────────────────
        List<Integer> unique = nums.stream()
                .distinct()
                .collect(Collectors.toList());
        System.out.println("Unique: " + unique);

        // ──────────────────────────────────────
        //  PATTERN 4: Two-sum using a set (not pure streams but useful)
        // ──────────────────────────────────────
        int target = 7;
        Set<Integer> seen = new HashSet<>();
        nums.stream()
                .filter(n -> {
                    if (seen.contains(target - n)) return true;
                    seen.add(n);
                    return false;
                })
                .findFirst()
                .ifPresent(n -> System.out.println("Pair for " + target + ": " + (target - n) + " + " + n));

        // ──────────────────────────────────────
        //  PATTERN 5: Group strings by first character
        // ──────────────────────────────────────
        Map<Character, List<String>> byFirst = words.stream()
                .collect(Collectors.groupingBy(w -> w.charAt(0)));
        System.out.println("By first char: " + byFirst);

        // ──────────────────────────────────────
        //  PATTERN 6: Convert List<String> to Map<String, Integer> (word -> length)
        // ──────────────────────────────────────
        Map<String, Integer> wordLengths = words.stream()
                .distinct()
                .collect(Collectors.toMap(w -> w, String::length));
        System.out.println("Word lengths: " + wordLengths);

        // ──────────────────────────────────────
        //  PATTERN 7: Flatten & sort
        // ──────────────────────────────────────
        List<List<Integer>> nested = List.of(List.of(3, 1), List.of(5, 2), List.of(4, 6));
        List<Integer> flatSorted = nested.stream()
                .flatMap(Collection::stream)
                .sorted()
                .collect(Collectors.toList());
        System.out.println("Flat sorted: " + flatSorted);

        // ──────────────────────────────────────
        //  PATTERN 8: Index-based operations (IntStream.range)
        // ──────────────────────────────────────
        List<String> items = List.of("a", "b", "c", "d", "e");
        // Create index -> element map
        Map<Integer, String> indexed = IntStream.range(0, items.size())
                .boxed()
                .collect(Collectors.toMap(i -> i, items::get));
        System.out.println("Indexed: " + indexed);

        // Filter by index (even indices only)
        List<String> evenIndex = IntStream.range(0, items.size())
                .filter(i -> i % 2 == 0)
                .mapToObj(items::get)
                .collect(Collectors.toList());
        System.out.println("Even index elements: " + evenIndex);

        // ──────────────────────────────────────
        //  PATTERN 9: Generating sequences
        // ──────────────────────────────────────
        // First 10 Fibonacci numbers
        List<long[]> fibs = Stream.iterate(new long[]{0, 1}, f -> new long[]{f[1], f[0] + f[1]})
                .limit(10)
                .collect(Collectors.toList());
        List<Long> fibNums = fibs.stream().map(f -> f[0]).collect(Collectors.toList());
        System.out.println("First 10 Fibs: " + fibNums);

        // Range of numbers
        List<Integer> oneToTen = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());
        System.out.println("1-10: " + oneToTen);

        // ──────────────────────────────────────
        //  PATTERN 10: String manipulation
        // ──────────────────────────────────────
        String input = "Hello World";

        // Reverse a string
        String reversed = new StringBuilder(input).reverse().toString();
        // Or with streams:
        String reversedStream = input.chars()
                .mapToObj(c -> String.valueOf((char) c))
                .reduce("", (a, b) -> b + a);
        System.out.println("Reversed: " + reversedStream);

        // Check if palindrome
        String test = "racecar";
        boolean isPalindrome = IntStream.range(0, test.length() / 2)
                .allMatch(i -> test.charAt(i) == test.charAt(test.length() - 1 - i));
        System.out.println("'" + test + "' is palindrome? " + isPalindrome);

        // Count vowels
        long vowelCount = input.chars()
                .filter(c -> "aeiouAEIOU".indexOf(c) >= 0)
                .count();
        System.out.println("Vowels in '" + input + "': " + vowelCount);

        // ──────────────────────────────────────
        //  PATTERN 11: Collectors.toMap with merge
        // ──────────────────────────────────────
        // Sum scores per student when duplicates exist
        List<String[]> records = List.of(
                new String[]{"Alice", "90"},
                new String[]{"Bob", "85"},
                new String[]{"Alice", "95"},
                new String[]{"Bob", "88"}
        );
        Map<String, Integer> totalScores = records.stream()
                .collect(Collectors.toMap(
                        r -> r[0],
                        r -> Integer.parseInt(r[1]),
                        Integer::sum
                ));
        System.out.println("Total scores: " + totalScores);

        // ──────────────────────────────────────
        //  PATTERN 12: Optional chaining with streams
        // ──────────────────────────────────────
        List<String> data = List.of("10", "abc", "20", "xyz", "30");
        // Parse only valid integers, find max
        OptionalInt maxVal = data.stream()
                .flatMap(s -> {
                    try { return Stream.of(Integer.parseInt(s)); }
                    catch (NumberFormatException e) { return Stream.empty(); }
                })
                .mapToInt(Integer::intValue)
                .max();
        System.out.println("Max parseable: " + maxVal.orElse(-1));

        // ──────────────────────────────────────
        //  PATTERN 13: Parallel streams (mention in interview, use cautiously)
        // ──────────────────────────────────────
        long sum = nums.parallelStream()
                .mapToLong(Integer::longValue)
                .sum();
        System.out.println("Parallel sum: " + sum);

        // ──────────────────────────────────────
        //  PATTERN 14: Collector teeing (Java 12+)
        //  Compute two results simultaneously
        // ──────────────────────────────────────
        String minMax = nums.stream()
                .collect(Collectors.teeing(
                        Collectors.<Integer>minBy(Comparator.naturalOrder()),
                        Collectors.<Integer>maxBy(Comparator.naturalOrder()),
                        (Optional<Integer> min, Optional<Integer> max) ->
                                "min=" + min.orElse(0) + ", max=" + max.orElse(0)
                ));
        System.out.println("Teeing result: " + minMax);

        System.out.println("\n✅ All patterns ran successfully!");
    }
}


