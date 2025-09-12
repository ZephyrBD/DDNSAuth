package net.zbd.util;

import net.zbd.DDNSAuth;

import java.io.File;

public class FileGen {
    public static File FileGenMethod(String filename, String file) {
        try {
            File dataDir = new File("plugins/DDNSAuth");
            if (!dataDir.exists()) dataDir.mkdirs();

            File configFile = new File(dataDir, filename);
            if (!configFile.exists()) {
                java.nio.file.Files.writeString(configFile.toPath(), file);
            }
            return configFile;
        } catch (Exception e) {
            DDNSAuth.getLogger().error("Fail to create {}", filename, e);
        }
        return null;
    }
}
