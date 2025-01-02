package com.yourapp.desertcraftadmin.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class Constant {

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

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

    public static String cleanOptions(String input) {
        // Step 1: Remove leading and trailing commas
        input = input.replaceAll("^,+|,+$", "");

        // Step 2: Replace multiple commas with a placeholder
        input = input.replaceAll(",+", ",placeholder,");

        // Step 3: Split the string by the placeholder
        String[] parts = input.split(",placeholder,");

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



    public static void showSoftInputKeyboardWithFocus(Context context, EditText editText) {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }
}
