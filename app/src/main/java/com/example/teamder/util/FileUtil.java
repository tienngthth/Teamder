package com.example.teamder.util;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FileUtil {

    public static void writeFile(Context context, String fileName, ArrayList<String> contents) {
        StringBuilder fileContents = new StringBuilder();
        File fileDir = context.getFilesDir();
        File file = new File(fileDir, fileName);
        for (String line : contents) {
            fileContents.append(line).append('\n');
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(fileContents.toString().getBytes());
            outputStream.close();
            Toast.makeText(context, "File downloaded to" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
