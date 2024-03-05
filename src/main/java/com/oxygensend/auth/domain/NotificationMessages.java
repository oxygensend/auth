package com.oxygensend.auth.domain;

public class NotificationMessages {
    public static final String ACTIVATE_ACCOUNT_BY_PASSWORD_CHANGE_SUBJECT = "Activate your account";
    public static final String ACTIVATE_ACCOUNT_BY_PASSWORD_CHANGE_MESSAGE = "To activate your account, please click the link below:\n%s".formatted( "%s");

    public static final String ACTIVATE_ACCOUNT_BY_EMAIL_VERIFICATION_SUBJECT = "Activate your account";
    public static final String ACTIVATE_ACCOUNT_BY_EMAIL_VERIFICATION_MESSAGE = "To activate your account, please click the link below:\n%s".formatted( "%s");

    public static final String PASSWORD_RESET_SUBJECT = "Password change link";
    public static final String PASSWORD_RESET_MESSAGE = "To change your password, please click the link below:\n%s".formatted("%s", "%s");


    public static final String EMAIL_VERIFICATION_SUBJECT = "Email verification";
    public static final String EMAIL_VERIFICATION_MESSAGE = "To verify your identity, please click the link below:\n%s".formatted( "%s");
}
