package com.dreamtec.bsp.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * General functions that assists with business logic, but are not specific to 
 * any business logic.
 * 
 * @author chinphek
 */
public class Utils {

    public static List<File> getFilesWithExtension(String dirname, final String ext) {
        File dir = new File(dirname);
        File[] list = dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
                return name.endsWith(ext);
			}
            
        });

        return Arrays.asList(list);
    }

    public static LocalDate toDate(String dateStr, String format) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(format);
        try {
            return LocalDate.parse(dateStr, dateFormatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static boolean isDate(String dateStr, String format) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(format);
        try {
            LocalDate.parse(dateStr, dateFormatter);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public static String[] splitCSV(String s) {
        List<String> list = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();

        boolean esc = false;
        for(int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if(c == '"') {
                esc = !esc;
            } else {
                if(esc) {
                    sb.append(c);
                } else {
                    if(c == ',') {
                        list.add(sb.toString());
                        sb = new StringBuilder();
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        list.add(sb.toString());

        return list.toArray(new String[0]);
    }

    public static double toAmount(String s) {
        s = s.replace(",", "");
        return Double.valueOf(s);
    }
    
}
