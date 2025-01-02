package com.yourapp.desertcraft.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Objects;

public class FileUtils {
    public static String getRealPathFromURI(Uri uri, Context context) {
        Cursor returnCursor = null;
        String filePath = null;
        try {
            returnCursor = context.getContentResolver().query(uri, null, null, null, null);
            if (returnCursor != null && returnCursor.moveToFirst()) {
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                String name = returnCursor.getString(nameIndex);
                long size = returnCursor.getLong(sizeIndex);
                File file = new File(context.getFilesDir(), name);
                InputStream inputStream = null;
                OutputStream outputStream = null;
                try {
                    inputStream = context.getContentResolver().openInputStream(uri);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        outputStream = Files.newOutputStream(file.toPath());
                    }
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while (inputStream != null && (bytesRead = inputStream.read(buffer)) != -1) {
                        if (outputStream != null) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                    Log.e("File Size", "Size " + file.length());
                    Log.e("File Path", "Path " + file.getAbsolutePath());
                    filePath = file.getAbsolutePath();
                } catch (IOException e) {
                    Log.e("Exception", Objects.requireNonNull(e.getMessage()));
                } finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            if (returnCursor != null) {
                returnCursor.close();
            }
        }
        return filePath;
    }
}


