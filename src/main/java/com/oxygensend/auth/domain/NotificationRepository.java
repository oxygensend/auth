package com.oxygensend.auth.domain;

import com.oxygensend.auth.context.SendMailCommand;

public interface NotificationRepository {

    void sendMail(SendMailCommand sendMailCommand);
}

