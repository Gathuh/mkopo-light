package com.tezzar.mkopo.light.fees;

import com.tezzar.mkopo.light.fees.request.FeesRequest;
import com.tezzar.mkopo.light.fees.request.UpdateFeeRequest;
import com.tezzar.mkopo.light.fees.response.FeeResponse;

import java.util.List;

public interface FeeService {
    FeeResponse createFee(FeesRequest request);
    FeeResponse findById(String id);
    List<FeeResponse> findAll();
    FeeResponse updateFee(String id, UpdateFeeRequest request);
    void softDelete(String id);
    List<FeeEntity> findAllByIds(List<String> ids);
}
