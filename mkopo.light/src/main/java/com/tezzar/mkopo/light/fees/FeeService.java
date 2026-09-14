package com.tezzar.mkopo.light.fees;

import com.tezzar.mkopo.light.fees.request.FeesRequest;

import java.util.List;

public interface FeeService {
    Fee createFee(FeesRequest request);
    Fee findById(String id);
    List<Fee> findAllByIds(List<String> ids);
}
