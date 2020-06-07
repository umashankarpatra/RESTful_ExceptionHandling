package com.xebia.postit.common.i18n;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MessageProvider {

    @NonNull
    private static MessageSource messageSource;

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        MessageProvider.messageSource = messageSource;
    }

    public static String getMessage(final String messageKey) {
        return messageSource.getMessage(messageKey, null, Locale.ENGLISH);
    }

    public static String getMessage(final String messageKey, final Object... params) {
        return messageSource.getMessage(messageKey, params, Locale.ENGLISH);
    }

    public static String getMessage(final MessageKey messageKey, final Object... params) {
        return messageSource.getMessage(messageKey.getMessageCode(), params, messageKey.getDefaultMessage(),
                Locale.ENGLISH);
    }

    public static String getMessage(final MessageCodeType messageCode) {
        return messageSource.getMessage(messageCode.key(), null, messageCode.defaultMessage(), Locale.ENGLISH);
    }

    public static String getMessage(final MessageCodeType messageCode, final Object... params) {
        return messageSource.getMessage(messageCode.key(), params, messageCode.defaultMessage(), Locale.ENGLISH);
    }

    public static String getMessage(final MessageCodeType messageCode, final Locale locale) {
        return messageSource.getMessage(messageCode.key(), null, messageCode.defaultMessage(), locale);
    }

    public static String getMessage(final MessageCodeType messageCode, final Locale locale, final Object... params) {
        return messageSource.getMessage(messageCode.key(), params, messageCode.defaultMessage(), locale);
    }

    public static String getMessageForFieldError(final FieldError fieldError) {
        return getMessageForFieldError(fieldError, Locale.ENGLISH);
    }

    public static String getMessageForFieldError(final FieldError fieldError, final Locale locale) {
        String message = null;
        fieldError.getCode();
        for (String code : fieldError.getCodes()) {
            message = messageSource.getMessage(code, fieldError.getArguments(), null, locale);
            if (message != null) {
                break;
            }
        }
        if (message == null) {
            message = messageSource.getMessage(fieldError.getDefaultMessage(), fieldError.getArguments(),
                    fieldError.getDefaultMessage(), locale);
        }
        fieldError.getDefaultMessage();
        return message;
    }
}
