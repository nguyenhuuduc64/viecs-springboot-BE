package com.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class JsonConverter implements AttributeConverter<Object, String> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Object attribute) {
        try { return mapper.writeValueAsString(attribute); }
        catch (Exception e) { return null; }
    }

    @Override
    public Object convertToEntityAttribute(String dbData) {
        try { return mapper.readValue(dbData, Object.class); }
        catch (Exception e) { return null; }
    }
}
