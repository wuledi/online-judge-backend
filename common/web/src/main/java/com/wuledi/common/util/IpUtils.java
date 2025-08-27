package com.wuledi.common.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

/**
 * IP工具类（合并优化版）
 */
@Slf4j
public class IpUtils {

    /**
     * 地理位置响应数据传输对象
     *
     * @author wuledi
     */
    @Data
    public static class LocationResponseDTO {
        /** 状态 */
        private String status;
        /** 国家名称 */
        private String country;
        /** 国家代码 */
        private String countryCode;
        /** 地区代码（如省份代码） */
        private String region;
        /** 地区名称（如省份名称） */
        private String regionName;
        /** 城市名称 */
        private String city;
        /** 邮政编码 */
        private String zip;
        /** 纬度坐标 */
        @JsonProperty("lat") // 映射为 "lat"，与 JSON 中的字段名对应
        private Double latitude;
        /** 经度坐标 */
        @JsonProperty("lon")
        private Double longitude;
        /** 时区 */
        private String timezone;
        /** 网络运营商名称 */
        private String isp;
        /** 组织名称 */
        private String org;
        /** AS 编号及描述 */
        @JsonProperty("as") // 映射为 "as"，与 JSON 中的字段名对应
        private String asNumber; // "as" 是保留关键字，改名为 asNumber
        /** 请求查询的 IP 地址 */
        private String query;
    }

    private static final String LOCAL_IP = "127.0.0.1";
    private static final Pattern IP_PATTERN = Pattern.compile(
            "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}"
    );

    private static volatile Searcher regionSearcher;
    private static final ReentrantLock initLock = new ReentrantLock();
    private static final RestTemplate restTemplate = new RestTemplate();

    /**
     * 获取客户端IP地址
     *
     * @param request HttpServletRequest对象
     * @return 客户端IP地址
     */
    public static String getIpAddress(HttpServletRequest request) {
        // 获取请求中的IP地址
        if (request == null) {
            return "unknown";
        }

        // 获取请求中的IP地址
        String[] headers = {
                "x-forwarded-for",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "X-Real-IP"
        };

        // 遍历请求头，获取IP地址
        for (String header : headers) {
            String ip = request.getHeader(header); // 获取请求头中的IP地址
            if (isValidIp(ip)) {
                return normalizeIp(ip);
            }

        }

        return normalizeIp(request.getRemoteAddr());
    }

    private static boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip);
    }

    /**
     * 标准化IP地址
     *
     * @param ip IP地址
     * @return 标准化后的IP地址
     */
    private static String normalizeIp(String ip) {
        if ("0:0:0:0:0:0:0:1".equals(ip)) return LOCAL_IP;
        if (ip == null) return LOCAL_IP;

        // 处理多IP情况（取第一个有效IP）
        int commaIndex = ip.indexOf(',');
        if (commaIndex > 0) {
            ip = ip.substring(0, commaIndex);
        }
        return ip.trim();
    }

    /**
     * 初始化IP地域查询器（线程安全）
     *
     */
    private static void initRegionSearcher() {
        if (regionSearcher != null) return;

        initLock.lock();
        try {
            if (regionSearcher == null) {
                ClassPathResource resource = new ClassPathResource("ip2region.xdb");
                try (InputStream inputStream = resource.getInputStream()) {
                    byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
                    regionSearcher = Searcher.newWithBuffer(bytes);
                    log.info("IP地域数据库加载成功");
                }
            }
        } catch (Exception e) {
            log.error("初始化IP地域数据库失败", e);
        } finally {
            initLock.unlock();
        }
    }

    /**
     * 获取IP地域信息
     */
    public static String getIpRegion(String ip) {
        if (isNotValidIpFormat(ip)) return "非法IP";

        try {
            initRegionSearcher();
            if (regionSearcher == null) return "数据库未加载";

            String regionInfo = regionSearcher.search(ip);
            return parseRegion(regionInfo);
        } catch (Exception e) {
            log.warn("IP地域查询失败: {}", ip, e);
            return "未知";
        }
    }

    private static boolean isNotValidIpFormat(String ip) {
        return ip == null || !IP_PATTERN.matcher(ip).matches();
    }

    private static String parseRegion(String regionInfo) {
        if (regionInfo == null) return "未知";

        String[] segments = regionInfo.split("\\|");
        if (segments.length < 5) return "未知";

        String nation = segments[0];
        String region = segments[2];
        String isp = segments[4];

        if ("0".equals(nation)) {
            return "内网IP".equals(isp) ? "内网" : "未知";
        }
        return "中国".equals(nation) ? region : nation;
    }

    /**
     * 查询IP地理位置（API方式）
     */
    public static LocationResponseDTO getIpLocation(String ip) {
        if (isNotValidIpFormat(ip)) return null;

        String apiUrl = "http://ip-api.com/json/" + ip + "?lang=zh-CN";
        try {
            ResponseEntity<LocationResponseDTO> response =
                    restTemplate.getForEntity(apiUrl, LocationResponseDTO.class);

            LocationResponseDTO result = response.getBody();
            return (result != null && "success".equals(result.getStatus()))
                    ? result : null;
        } catch (RestClientException e) {
            log.warn("IP定位服务调用失败: {}", ip, e);
            return null;
        }
    }
}