package com.yourapp.desertcraft.utils;

import java.util.ArrayList;
import java.util.List;

public class Constant {


    public static String cleanUpString(String input) {
        // Step 1: Remove leading and trailing commas
        input = input.replaceAll("^,+|,+$", "");

        // Step 2: Replace multiple commas and spaces with a single comma
        input = input.replaceAll("[,\\s]+", ",");

        // Step 3: Split the string by commas
        String[] parts = input.split(",");

        // Step 4: Trim each part and filter out empty parts
        List<String> cleanedParts = new ArrayList<>();
        for (String part : parts) {
            part = part.trim();
            if (!part.isEmpty()) {
                cleanedParts.add(part);
            }
        }

        // Step 5: Join the cleaned parts with ", " to form the final string
        return String.join(", ", cleanedParts);
    }
}
