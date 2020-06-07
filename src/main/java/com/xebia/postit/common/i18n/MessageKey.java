package com.xebia.postit.common.i18n;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class MessageKey {

    private String messageCode;
    
    private String defaultMessage;
}
