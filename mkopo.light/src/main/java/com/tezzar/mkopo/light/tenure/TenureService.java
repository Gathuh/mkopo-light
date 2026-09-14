package com.tezzar.mkopo.light.tenure;

import com.tezzar.mkopo.light.tenure.request.TenureRequest;
import com.tezzar.mkopo.light.tenure.request.UpdateTenureRequest;
import com.tezzar.mkopo.light.tenure.response.TenureResponse;

import java.util.List;

public interface TenureService {
    TenureResponse createTenure(TenureRequest request);
    TenureResponse findById(String id);
    List<TenureResponse> findAll();
    TenureResponse updateTenure(String id, UpdateTenureRequest request);
    void softDelete(String id);
    List<TenureEntity> findAllByIds(List<String> ids);
}
