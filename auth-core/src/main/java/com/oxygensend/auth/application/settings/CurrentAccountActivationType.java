package com.oxygensend.auth.application.settings;

import com.oxygensend.auth.domain.model.identity.AccountActivationType;

public class CurrentAccountActivationType {

    private final AccountActivationType accountActivationType;

    public CurrentAccountActivationType(AccountActivationType accountActivationType) {
        this.accountActivationType = accountActivationType;
    }


    public AccountActivationType get() {
        return accountActivationType;
    }
}
