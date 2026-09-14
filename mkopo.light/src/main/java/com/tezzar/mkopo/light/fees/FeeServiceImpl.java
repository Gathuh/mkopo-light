package com.tezzar.mkopo.light.fees;

import com.tezzar.mkopo.light.fees.request.FeesRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeeServiceImpl implements FeeService {

    private final FeeRepository feeRepository;

    @Override
    public Fee createFee(FeesRequest request) {
        Fee fee = Fee.builder()
                .feeType(request.feeType())
                .calculationType(request.calculationType())
                .amount(request.amount())
                .rate(request.rate())
                .timing(request.timing())
                .triggerDays(request.triggerDays())
                .build();
        return feeRepository.save(fee);
    }

    @Override
    public Fee findById(String id) {
        return feeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fee not found with id: " + id));
    }

    @Override
    public List<Fee> findAllByIds(List<String> ids) {
        return feeRepository.findAllByIdIn(ids);
    }
}
