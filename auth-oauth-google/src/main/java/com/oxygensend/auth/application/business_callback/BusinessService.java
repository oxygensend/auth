package com.oxygensend.auth.application.business_callback;

import com.oxygensend.auth.domain.model.identity.BusinessId;

public interface BusinessService {
    BusinessId sendData(BusinessRequestDto requestDto);
}
