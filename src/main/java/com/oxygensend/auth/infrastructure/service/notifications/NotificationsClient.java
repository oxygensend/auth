package com.oxygensend.auth.infrastructure.service.notifications;

import com.oxygensend.auth.infrastructure.service.notifications.dto.MailRequest;
import com.oxygensend.auth.infrastructure.service.notifications.dto.NotificationResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface NotificationsClient {

    @RequestMapping(method = RequestMethod.POST, value = "/v1/notifications/mailAsync")
    NotificationResponse sendMailAsync(@RequestBody MailRequest request);

}
