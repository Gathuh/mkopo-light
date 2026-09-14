package com.tezzar.mkopo.light.fees;

import com.tezzar.mkopo.light.fees.enums.FeeStatus;
import com.tezzar.mkopo.light.fees.request.FeesRequest;
import com.tezzar.mkopo.light.fees.request.UpdateFeeRequest;
import com.tezzar.mkopo.light.fees.response.FeeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeeServiceImpl implements FeeService {

    private final FeeRepository feeRepository;

    @Override
    public FeeResponse createFee(FeesRequest request) {
        FeeEntity fee = FeeEntity.builder()
                .feeType(request.feeType())
                .calculationType(request.calculationType())
                .amount(request.amount())
                .rate(request.rate())
                .timing(request.timing())
                .triggerDays(request.triggerDays())
                .build();
        return FeeResponse.fromEntity(feeRepository.save(fee));
    }

    @Override
    public FeeResponse findById(String id) {
        return FeeResponse.fromEntity(getFeeOrThrow(id));
    }

    @Override
    public List<FeeResponse> findAll() {
        return feeRepository.findByStatusNot(FeeStatus.DELETED)
                .stream()
                .map(FeeResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public FeeResponse updateFee(String id, UpdateFeeRequest request) {
        FeeEntity fee = getFeeOrThrow(id);
        fee.setFeeType(request.feeType());
        fee.setCalculationType(request.calculationType());
        fee.setAmount(request.amount());
        fee.setRate(request.rate());
        fee.setTiming(request.timing());
        fee.setTriggerDays(request.triggerDays());
        return FeeResponse.fromEntity(feeRepository.save(fee));
    }

    @Override
    @Transactional
    public void softDelete(String id) {
        FeeEntity fee = getFeeOrThrow(id);
        fee.setStatus(FeeStatus.DELETED);
        feeRepository.save(fee);
    }

    @Override
    public List<FeeEntity> findAllByIds(List<String> ids) {
        return feeRepository.findAllByIdIn(ids);
    }

    private FeeEntity getFeeOrThrow(String id) {
        return feeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fee not found with id: " + id));
    }
}
