# DDNSAuth

DDNSAuth 是一个基于 Velocity 代理服务器的插件，用于通过 DDNS 域名、内网 CIDR 网段、地区白名单等方式限制服务器访问，增强服务器的访问控制安全性。


## 功能介绍

- **域名验证**：仅允许来自配置中指定的 DDNS 域名的连接
- **内网限制**：允许配置特定的内网 CIDR 网段直接访问
- **环回地址控制**：可配置是否允许 127.0.0.1 或 0.0.0.0 等环回地址访问
- **地区白名单**：通过 GeoIP2 数据库验证客户端 IP 所属地区，仅允许白名单内地区的访问


## 安装步骤

1. **环境要求**：
   - Velocity 代理服务器（3.1.1 及以上版本）
   - Java 17 及以上版本

2. **安装插件**：
   - 将编译好的 `DDNSAuth.jar` 放入 Velocity 服务器的 `plugins` 目录
   - 启动服务器，插件会自动生成默认配置文件

3. **配置 GeoIP2 数据库**（可选，用于地区限制功能）：
   - 下载 [GeoLite2-Country 数据库](https://support.maxmind.com/hc/en-us/articles/4408216129947-Download-and-Update-Databases)
   - 将数据库文件（`GeoLite2-Country.mmdb`）放入 `plugins/ddnsauth` 目录（或配置文件中指定的路径）


## 配置说明

配置文件位于 `plugins/ddnsauth/config.toml`，默认内容如下：

```toml
# 允许的 DDNS 域名（客户端连接时使用的域名）
allowedDomains = ["example.com"]

# 允许的内网网段（CIDR 格式，如 "192.168.1.0/24"）
allowedCidrs = ["192.168.1.0/24"]

# 是否允许 127.0.0.1 和 0.0.0.0 等环回地址访问
allowLoopback = true

# 允许的国家（使用 ISO 国家代码，如 CN 表示中国，US 表示美国）
allowedCountries = ["CN"]

# GeoLite2-Country.mmdb 数据库文件路径
geoipDatabase = "plugins/ddnsauth/GeoLite2-Country.mmdb"
```

- `allowedDomains`：客户端必须通过列表中的域名连接服务器，否则会被拒绝
- `allowedCidrs`：属于这些 CIDR 网段的 IP 会被直接允许访问（跳过域名和国家检查）
- `allowLoopback`：开启后允许环回地址（本地测试常用）访问
- `allowedCountries`：仅允许来自这些国家的 IP 访问（需配置 GeoIP2 数据库）
- `geoipDatabase`：GeoIP2 数据库文件的路径，若文件不存在则国家限制功能失效


## 使用方法

1. 根据需求修改 `config.toml` 配置文件
2. 重启 Velocity 服务器使配置生效
3. 客户端连接时，插件会自动进行验证：
   - 若不符合任何允许条件，会收到相应的拒绝提示（如"请使用正确的域名连接！"或"你的地区不允许访问服务器"）
   - 验证通过则正常进入服务器


## 许可证

本项目基于 [GNU General Public License v2](LICENSE) 开源。


## 致谢

- 依赖 [Velocity API](https://velocitypowered.com/) 实现代理服务器集成
- 依赖 [MaxMind GeoIP2](https://www.maxmind.com/) 提供 IP 地址的国家定位功能
