package com.wuledi.feignclient.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "service-user", // 服务名称
        contextId = "service-user", // 客户端名称
        path = "/api/inner/users"
)
public interface UserFeignClient {

    @GetMapping("/getUser")
    String getUser(Long id);
}
