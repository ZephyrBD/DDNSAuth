# DDNSAuth

DDNSAuth is a plugin based on the Velocity proxy server. It restricts server access through methods such as DDNS domain names, internal network CIDR segments, and regional whitelists, enhancing the security of server access control.

## Feature Introduction

- **Domain Name Verification**: Only connections from the DDNS domain names specified in the configuration are allowed.
- **Internal Network Restriction**: Allows the configuration of specific internal network CIDR segments for direct access.
- **Loopback Address Control**: It can be configured whether to allow access from loopback addresses such as 127.0.0.1 or 0.0.0.0.
- **Regional Whitelist**: Verifies the region to which the client IP belongs through the GeoIP2 database, and only allows access from regions within the whitelist.
- **i18n support**: You can change the prompt language by modifying the messages.

## Installation Steps

1. **Environmental Requirements**:
   - Velocity proxy server (version 3.1.1 and above)
   - Java version 17 and above

2. **Install the Plugin**:
   - Place the compiled `DDNSAuth.jar` in the `plugins` directory of the Velocity server.
   - Start the server, and the plugin will automatically generate a default configuration file.

3. **Configure the GeoIP2 Database** (Optional, for the regional restriction feature):
   - Download the [GeoLite2 - Country database](https://support.maxmind.com/hc/en-us/articles/4408216129947-Download-and-Update-Databases).
   - Place the database file (`GeoLite2 - Country.mmdb`) in the `plugins/ddnsauth` directory (or the path specified in the configuration file).

## Configuration Instructions

The configuration file is located at `plugins/ddnsauth/config.toml`, and the default content is as follows:

```toml
# Allowed DDNS domain names (the domain names used when clients connect)
allowedDomains = ["example.com"]

# Allowed internal network segments (in CIDR format, such as "192.168.1.0/24")
allowedCidrs = ["192.168.1.0/24"]

# Whether to allow access from loopback addresses such as 127.0.0.1 and 0.0.0.0
allowLoopback = true

# Allowed countries (using ISO country codes, e.g., CN for China, US for the United States)
allowedCountries = ["CN"]

# Path to the GeoLite2 - Country.mmdb database file
geoipDatabase = "plugins/ddnsauth/GeoLite2 - Country.mmdb"
```

- `allowedDomains`: Clients must connect to the server through the domain names in the list; otherwise, they will be rejected.
- `allowedCidrs`: IPs belonging to these CIDR segments will be directly allowed to access (skipping domain name and country checks).
- `allowLoopback`: When enabled, it allows access from loopback addresses (commonly used for local testing).
- `allowedCountries`: Only allows access from IPs in these countries (requires configuration of the GeoIP2 database).
- `geoipDatabase`: The path to the GeoIP2 database file. If the file does not exist, the country restriction feature will not work.

## Usage

1. Modify the `config.toml` configuration file according to your needs.
2. Restart the Velocity server to make the configuration take effect.
3. When clients connect, the plugin will automatically perform verification:
   - If it does not meet any of the allowed conditions, the client will receive an appropriate rejection message (such as "Please connect using the correct domain name!" or "Your region is not allowed to access the server").
   - If the verification passes, the client can enter the server normally.

## License

This project is open - sourced under the [GNU General Public License v2](LICENSE).

## Acknowledgments

- Depends on the [Velocity API](https://velocitypowered.com/) for proxy server integration.
- Depends on [MaxMind GeoIP2](https://www.maxmind.com/) to provide the country location function of IP addresses.
