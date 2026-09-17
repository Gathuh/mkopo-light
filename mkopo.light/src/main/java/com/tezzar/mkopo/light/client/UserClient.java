package com.tezzar.mkopo.light.client;

import com.tezzar.mkopo.light.client.dto.ClientApiResponse;
import com.tezzar.mkopo.light.client.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "security-service", fallback = UserClientFallback.class)
public interface UserClient {

    @GetMapping("/security/users/{id}")
    ClientApiResponse<UserResponse> getUserById(@PathVariable String id);
}
