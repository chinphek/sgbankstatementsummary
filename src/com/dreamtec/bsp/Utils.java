package com.dreamtec.bsp;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

/**
 * General functions that assists with business logic, but are not specific to 
 * any business logic.
 * 
 * @author chinphek
 */
public class Utils {

    public static List<File> getFilesWithExtension(File dir, final String ext) {
        
        File[] list = dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
                return name.endsWith(ext);
			}
            
        });

        return Arrays.asList(list);
    }
    
}
