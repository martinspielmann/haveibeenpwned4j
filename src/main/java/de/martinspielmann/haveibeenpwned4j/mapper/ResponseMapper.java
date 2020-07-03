package de.martinspielmann.haveibeenpwned4j.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ResponseMapper {

    private static ObjectMapper MAPPER;
    
    static {
        MAPPER = new ObjectMapper();
        MAPPER.registerModule(new JavaTimeModule());
    }

    public static final <T> T mapType(String content, Class<T> valueType)
            throws JsonMappingException, JsonProcessingException {
        return MAPPER.readValue(content, valueType);
    }
}