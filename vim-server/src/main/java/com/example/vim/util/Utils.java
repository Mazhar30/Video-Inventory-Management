package com.example.vim.util;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class Utils {

    public static String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        return StringUtils.getFilenameExtension(originalFilename);
    }

    public static boolean isProperFormattedFile(MultipartFile file) {
        String fileExtension = getFileExtension(file);
        return fileExtension.equals("mp4") || fileExtension.equals("mkv");
    }
}
