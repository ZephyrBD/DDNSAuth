package net.zbd.util;

import com.maxmind.geoip2.DatabaseReader;
import com.moandjiezana.toml.Toml;
import net.zbd.DDNSAuth;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigReader {
    public static List<String> allowedDomains;
    public static List<String> allowedCidrs;
    public static List<String> allowedCountries;
    public static boolean allowLoopback;
    static DatabaseReader geoipReader;

    private static final Map<String, String> messages = new HashMap<>();

    public static void init() {
        loadMessages();
        loadConfig();
    }

    public static void loadMessages() {
        try {
            String filename = "messages.toml";
            File configFile = FileGen.FileGenMethod(filename, """
            [general]
            pluginLoaded = "DDNSAuth loaded successfully!"
            configLoaded = "Configuration loaded successfully!"
            configFailed = "Failed to load configuration."
            messagesLoaded = "Messages loaded successfully!"
            fileMissing = "File not found: {file}"

            [geoip]
            notFound = "GeoLite2 database file not found: {file}, please download it from {url}"

            [login]
            invalidDomain = "Please use the correct domain to connect!"
            countryDenied = "Your region is not allowed to access the server"
            countryDeniedLog = "Denied {ip} from region {country}"
            geoipError = "GeoIP2 lookup failed: {ip}"
            ipError = "IP lookup error: {ip}"

            [command.reload]
            usage = "Usage: /ddnsreload <config|messages|all>"
            config = "Config file reloaded!"
            messages = "Language file reloaded!"
            all = "Config and language files reloaded!"
            unknown = "Unknown argument: {arg}"
            """);

            if (configFile == null) {
                DDNSAuth.getLogger().warn("Language file could not be created: {}", filename);
                return;
            }

            Toml toml = new Toml().read(configFile);
            messages.clear();

            toml.toMap().forEach((section, content) -> {
                if (content instanceof Map<?, ?> subMap) {
                    subMap.forEach((k, v) -> {
                        if (k != null && v != null) {
                            String key = section + "." + k;
                            messages.put(key, v.toString());
                        }
                    });
                }
            });

            DDNSAuth.getLogger().info(t("general.messagesLoaded"));
        } catch (Exception e) {
            DDNSAuth.getLogger().error("Failed to load language file!", e);
        }
    }

    public static String t(String key, Map<String, String> vars) {
        String msg = messages.getOrDefault(key, key);
        if (vars != null) {
            for (var entry : vars.entrySet()) {
                msg = msg.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        return msg;
    }

    public static String t(String key) {
        return t(key, null);
    }

    public static void loadConfig() {
        try {
            File configFile = FileGen.FileGenMethod("config.toml", """
                        # Allowed DDNS domains
                        allowedDomains = ["example.com"]

                        # Allowed internal network segments (in CIDR format)
                        allowedCidrs = ["192.168.1.0/24"]

                        # Is 127.0.0.1 and 0.0.0.0 allowed?
                        allowLoopback = true

                        # Allowed countries (ISO codes, such as CN, US)
                        allowedCountries = ["CN"]

                        # File path of GeoLite2 - Country.mmdb
                        geoipDatabase = "plugins/ddnsauth/GeoLite2-Country.mmdb"
                        """);
            if (configFile == null) {
                DDNSAuth.getLogger().info(t("general.configFailed"));
                return;
            }

            Toml toml = new Toml().read(configFile);
            allowedDomains = toml.getList("allowedDomains", List.of());
            allowedCidrs = toml.getList("allowedCidrs", List.of());
            allowedCountries = toml.getList("allowedCountries", List.of("CN"));
            allowLoopback = toml.getBoolean("allowLoopback", true);

            String geoipPath = toml.getString("geoipDatabase", "plugins/ddnsauth/GeoLite2-Country.mmdb");
            File dbFile = new File(geoipPath);
            if (dbFile.exists()) {
                geoipReader = new DatabaseReader.Builder(new FileInputStream(dbFile)).build();
                DDNSAuth.getLogger().info("GeoLite2 DB: {}", dbFile.getAbsolutePath());
            } else {
                DDNSAuth.getLogger().warn(t("geoip.notFound", Map.of(
                        "file", dbFile.getAbsolutePath(),
                        "url", "https://support.maxmind.com/hc/en-us/articles/4408216129947-Download-and-Update-Databases"
                )));
            }

            DDNSAuth.getLogger().info(t("general.configLoaded"));
        } catch (Exception e) {
            DDNSAuth.getLogger().error(t("general.configFailed"), e);
        }
    }
}
