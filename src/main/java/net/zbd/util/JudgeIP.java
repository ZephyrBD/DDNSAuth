package net.zbd.util;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.zbd.DDNSAuth;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;

import static net.zbd.util.ConfigReader.*;

public class JudgeIP {
    @Subscribe
    public void onPreLogin(PreLoginEvent event) {
        InetSocketAddress remoteAddr = event.getConnection().getRemoteAddress();
        String ip = remoteAddr.getAddress().getHostAddress();

        Optional<InetSocketAddress> host = event.getConnection().getVirtualHost();
        String inputHost = host.map(InetSocketAddress::getHostString).orElse("");

        // loopback
        if (allowLoopback && (ip.equals("127.0.0.1") || ip.equals("0.0.0.0"))) {
            return;
        }

        // 内网
        for (String cidr : allowedCidrs) {
            if (ipInCidr(ip, cidr)) return;
        }

        // 域名检查
        if (!allowedDomains.contains(inputHost)) {
            event.setResult(PreLoginEvent.PreLoginComponentResult.denied(
                    Component.text(ConfigReader.t("login.invalidDomain"), NamedTextColor.RED)
            ));
            return;
        }

        // 地区检查
        if (geoipReader != null) {
            try {
                InetAddress inetAddress = InetAddress.getByName(ip);
                String country = geoipReader.country(inetAddress).getCountry().getIsoCode();
                if (!allowedCountries.contains(country)) {
                    DDNSAuth.getLogger().warn(ConfigReader.t("login.countryDeniedLog",
                            java.util.Map.of("ip", ip, "country", country)));
                    event.setResult(PreLoginEvent.PreLoginComponentResult.denied(
                            Component.text(ConfigReader.t("login.countryDenied"), NamedTextColor.RED)
                    ));
                }
            } catch (GeoIp2Exception ge) {
                DDNSAuth.getLogger().error(ConfigReader.t("login.geoipError", java.util.Map.of("ip", ip)), ge);
            } catch (Exception e) {
                DDNSAuth.getLogger().error(ConfigReader.t("login.ipError", java.util.Map.of("ip", ip)), e);
            }
        }
    }

    private boolean ipInCidr(String ip, String cidr) {
        try {
            String[] parts = cidr.split("/");
            String cidrIp = parts[0];
            int prefix = parts.length < 2 ? 32 : Integer.parseInt(parts[1]);

            byte[] address = InetAddress.getByName(ip).getAddress();
            byte[] network = InetAddress.getByName(cidrIp).getAddress();

            int mask = -1 << (32 - prefix);
            int ipInt = byteArrayToInt(address);
            int netInt = byteArrayToInt(network);

            return (ipInt & mask) == (netInt & mask);
        } catch (Exception e) {
            return false;
        }
    }

    private int byteArrayToInt(byte[] bytes) {
        int result = 0;
        for (byte b : bytes) {
            result = (result << 8) | (b & 0xFF);
        }
        return result;
    }
}
