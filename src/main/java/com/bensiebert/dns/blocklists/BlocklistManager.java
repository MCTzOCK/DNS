package com.bensiebert.dns.blocklists;

import com.bensiebert.dns.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class BlocklistManager {

    public static Set<String> blocklist = new HashSet<>();

    public static void loadBlocklists() throws IOException {
        System.out.println("Loading blocklist from " + Configuration.BLOCKLISTS);

        File blocklists = new File(Configuration.BLOCKLISTS);

        if (!blocklists.exists()) {
            blocklists.mkdirs();
            System.out.println("Blocklist directory created: " + blocklists.getAbsolutePath());
        }

        File[] files = blocklists.listFiles();

        if (files == null || files.length == 0) {
            throw new IOException("No blocklist files found in directory: " + blocklists.getAbsolutePath());
        }

        for (File file : files) {
            try {
                loadBlocklistFile(file);
            } catch (IOException e) {
                System.err.println("Error loading blocklist file " + file.getName() + ": " + e.getMessage());
            }
        }

        System.out.println("Blocklist loaded with " + blocklist.size() + " entries.");
    }

    private static void loadBlocklistFile(File blocklistFile) throws IOException {
        if (!blocklistFile.exists()) {
            throw new IOException("Blocklist file does not exist: " + blocklistFile.getAbsolutePath());
        }

        try (Scanner scanner = new Scanner(blocklistFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    if(line.startsWith("0.0.0.0")) {
                        line = line.substring(8).trim(); // Remove "0.0.0.0 " prefix
                    }
                    blocklist.add(line.toLowerCase());
                }
            }
        }
    }

    public static boolean isBlocked(String domain) {
        return blocklist.contains(domain.toLowerCase());
    }
}
