package com.tezzar.mkopo.light.tenure;

import com.tezzar.mkopo.light.fees.FeeEntity;
import com.tezzar.mkopo.light.fees.FeeService;
import com.tezzar.mkopo.light.jointables.TenureFee;
import com.tezzar.mkopo.light.tenure.enums.TenureStatus;
import com.tezzar.mkopo.light.tenure.request.TenureRequest;
import com.tezzar.mkopo.light.tenure.request.UpdateTenureRequest;
import com.tezzar.mkopo.light.tenure.response.TenureResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TenureServiceImpl implements TenureService {

    private final TenureRepository tenureRepository;
    private final FeeService feeService;

    @Override
    @Transactional
    public TenureResponse createTenure(TenureRequest request) {
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
            List<FeeEntity> fees = feeService.findAllByIds(request.feeIds());
            List<TenureFee> tenureFees = new ArrayList<>();
            for (FeeEntity fee : fees) {
                TenureFee tenureFee = TenureFee.builder()
                        .tenure(savedTenure)
                        .fee(fee)
                        .build();
                tenureFees.add(tenureFee);
            }
            savedTenure.setTenureFees(tenureFees);
            return TenureResponse.fromEntity(tenureRepository.save(savedTenure));
        }

        return TenureResponse.fromEntity(savedTenure);
    }

    @Override
    public TenureResponse findById(String id) {
        return TenureResponse.fromEntity(getTenureOrThrow(id));
    }

    @Override
    public List<TenureResponse> findAll() {
        return tenureRepository.findByStatusNot(TenureStatus.DELETED)
                .stream()
                .map(TenureResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public TenureResponse updateTenure(String id, UpdateTenureRequest request) {
        TenureEntity tenure = getTenureOrThrow(id);

        tenure.setTenureValue(request.tenureValue());
        tenure.setTenureType(request.tenureType());
        tenure.setRepaymentStructure(request.repaymentStructure());
        tenure.setInstallmentCount(request.installmentCount());
        tenure.setCapitalized(request.capitalized());
        tenure.setMinimumProductAmount(request.minimumProductAmount());
        tenure.setMaximumProductAmount(request.maximumProductAmount());

        if (request.feeIds() != null && !request.feeIds().isEmpty()) {
            tenure.getTenureFees().clear();
            List<FeeEntity> fees = feeService.findAllByIds(request.feeIds());
            List<TenureFee> tenureFees = new ArrayList<>();
            for (FeeEntity fee : fees) {
                TenureFee tenureFee = TenureFee.builder()
                        .tenure(tenure)
                        .fee(fee)
                        .build();
                tenureFees.add(tenureFee);
            }
            tenure.setTenureFees(tenureFees);
        }

        return TenureResponse.fromEntity(tenureRepository.save(tenure));
    }

    @Override
    @Transactional
    public void softDelete(String id) {
        TenureEntity tenure = getTenureOrThrow(id);
        tenure.setStatus(TenureStatus.DELETED);
        tenureRepository.save(tenure);
    }

    @Override
    public List<TenureEntity> findAllByIds(List<String> ids) {
        return tenureRepository.findAllByIdIn(ids);
    }

    private TenureEntity getTenureOrThrow(String id) {
        return tenureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenure not found with id: " + id));
    }
}
