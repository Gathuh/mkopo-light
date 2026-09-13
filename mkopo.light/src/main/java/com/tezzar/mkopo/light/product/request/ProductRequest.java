package com.tezzar.mkopo.light.product.request;

import com.tezzar.mkopo.light.fees.request.FeesRequest;
import com.tezzar.mkopo.light.tenure.request.TenureRequest;

public record ProductRequest(
        TenureRequest tenureRequest,
        FeesRequest feesRequest

) {
}
