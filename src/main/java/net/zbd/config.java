package net.zbd;

import com.maxmind.geoip2.DatabaseReader;
import com.moandjiezana.toml.Toml;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import static net.zbd.ddnsauth.logger;

public class config {
    public static List<String> allowedDomains;
    public static List<String> allowedCidrs;
    public static List<String> allowedCountries;
    public static boolean allowLoopback;
    static DatabaseReader geoipReader;

    public static void loadConfig() {
        try {
            File dataDir = new File("plugins/ddnsauth");
            if (!dataDir.exists()) dataDir.mkdirs();

            File configFile = new File(dataDir, "config.toml");
            if (!configFile.exists()) {
                String defaultConfig = """
                        # 允许的 DDNS 域名
                        allowedDomains = ["example.com"]

                        # 允许的内网网段（CIDR 格式）
                        allowedCidrs = ["192.168.1.0/24"]

                        # 是否允许 127.0.0.1 和 0.0.0.0
                        allowLoopback = true

                        # 允许的国家（ISO 代码，如 CN, US）
                        allowedCountries = ["CN"]

                        # GeoLite2-Country.mmdb 文件路径
                        geoipDatabase = "plugins/ddnsauth/GeoLite2-Country.mmdb"
                        """;
                java.nio.file.Files.writeString(configFile.toPath(), defaultConfig);
            }

            Toml toml = new Toml().read(configFile);
            allowedDomains = toml.getList("allowedDomains", List.of());
            allowedCidrs = toml.getList("allowedCidrs", List.of());
            config.allowedCountries = toml.getList("allowedCountries", List.of("CN"));
            config.allowLoopback = toml.getBoolean("allowLoopback", true);

            String geoipPath = toml.getString("geoipDatabase", "plugins/ddnsauth/GeoLite2-Country.mmdb");
            File dbFile = new File(geoipPath);
            if (dbFile.exists()) {
                config.geoipReader = new DatabaseReader.Builder(new FileInputStream(dbFile)).build();
                logger.info("加载 GeoLite2 数据库成功: {}", dbFile.getAbsolutePath());
            } else {
                logger.warn("GeoLite2 数据库文件不存在: {},请自行前往 https://support.maxmind.com/hc/en-us/articles/4408216129947-Download-and-Update-Databases 下载", dbFile.getAbsolutePath());
            }

            logger.info("DDNSAuth 配置加载成功！");
        } catch (Exception e) {
            logger.error("加载配置失败", e);
        }
    }
}
