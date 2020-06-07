package com.xebia.postit.infra.config.jackson;

import java.io.IOException;
import java.time.LocalTime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.xebia.postit.common.util.DateTimeHelper;

public class LocalTimeSerializer extends JsonSerializer<LocalTime> {

    @Override
    public void serialize(LocalTime value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        jgen.writeString(DateTimeHelper.formatTime(value));
    }
}
