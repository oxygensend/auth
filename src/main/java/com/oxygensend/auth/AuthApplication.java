package com.oxygensend.auth;

import com.oxygensend.commons_jdk.exception.ExceptionConfiguration;
import com.oxygensend.commons_jdk.feign.CommonFeignConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import( {ExceptionConfiguration.class, CommonFeignConfiguration.class})
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

}
