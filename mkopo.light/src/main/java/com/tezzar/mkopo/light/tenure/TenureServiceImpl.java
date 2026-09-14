package com.tezzar.mkopo.light.tenure;

import com.tezzar.mkopo.light.fees.Fee;
import com.tezzar.mkopo.light.fees.FeeRepository;
import com.tezzar.mkopo.light.fees.TenureFee;
import com.tezzar.mkopo.light.tenure.request.TenureRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TenureServiceImpl implements TenureService {

    private final TenureRepository tenureRepository;
    private final FeeRepository feeRepository;

    @Override
    public TenureEntity createTenure(TenureRequest request) {
        TenureEntity tenure = TenureEntity.builder()
                .tenureValue(request.tenureValue())
                .tenureType(request.tenureType())
                .repaymentStructure(request.repaymentStructure())
                .installmentCount(request.installmentCount())
                .capitalized(request.capitalized())
                .minimumProductAmount(request.minimumProductAmount())
                .maximumProductAmount(request.maximumProductAmount())
                .build();

        TenureEntity savedTenure = tenureRepository.save(tenure);

        if (request.feeIds() != null && !request.feeIds().isEmpty()) {
            List<Fee> fees = feeRepository.findAllByIdIn(request.feeIds());
            List<TenureFee> tenureFees = new ArrayList<>();
            for (Fee fee : fees) {
                TenureFee tenureFee = TenureFee.builder()
                        .tenure(savedTenure)
                        .fee(fee)
                        .attachedAt(LocalDateTime.now())
                        .build();
                tenureFees.add(tenureFee);
            }
            savedTenure.setTenureFees(tenureFees);
            return tenureRepository.save(savedTenure);
        }

        return savedTenure;
    }

    @Override
    public TenureEntity findById(String id) {
        return tenureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenure not found with id: " + id));
    }

    @Override
    public List<TenureEntity> findAllByIds(List<String> ids) {
        return tenureRepository.findAllByIdIn(ids);
    }
}
