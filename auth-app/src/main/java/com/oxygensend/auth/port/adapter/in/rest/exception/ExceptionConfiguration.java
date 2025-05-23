package com.oxygensend.auth.port.adapter.in.rest.exception;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.oxygensend.common.ExcludeFromJacocoGeneratedReport;

@ExcludeFromJacocoGeneratedReport
@Configuration
class ExceptionConfiguration {


    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    ResponseEntityExceptionHandler exceptionHandler() {
        return new ApiExceptionHandler();
    }
}
