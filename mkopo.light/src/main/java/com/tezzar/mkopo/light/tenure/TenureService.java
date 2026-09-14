package com.tezzar.mkopo.light.tenure;

import com.tezzar.mkopo.light.tenure.request.TenureRequest;

import java.util.List;

public interface TenureService {
    TenureEntity createTenure(TenureRequest request);
    TenureEntity findById(String id);
    List<TenureEntity> findAllByIds(List<String> ids);
}
