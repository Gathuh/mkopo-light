package com.tezzar.mkopo.light.client;

import com.tezzar.mkopo.light.client.dto.ClientApiResponse;
import com.tezzar.mkopo.light.client.dto.UserResponse;
import com.tezzar.mkopo.light.exception.ServiceUnavailableException;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient {

    @Override
    public ClientApiResponse<UserResponse> getUserById(String id) {
        throw new ServiceUnavailableException("Security service", "Cannot validate user with id: " + id);
    }
}
