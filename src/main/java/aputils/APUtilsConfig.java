package aputils;

import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.util.Scanner;

public class APUtilsConfig {
    private static final File configFile = FabricLoader.getInstance().getConfigDir().resolve("aputil.txt").toFile();
    
    private static void verifyExists() {
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                FileWriter writer = new FileWriter(configFile);
                writer.write("[MISSING_CONFIG_UNKNOWN_VERSION]");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static String getVersion() {
        verifyExists();
        Scanner scanner;
        try {
            scanner = new Scanner(configFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "[COULD_NOT_READ_CONFIG_FILE]";
        }
        
        return scanner.next();
    }
}
