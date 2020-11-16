package com.demo.cqrs.undo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;

public class UndoAttributeConverter implements AttributeConverter<Undo, String> {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Undo attribute) {
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Undo convertToEntityAttribute(String dbData) {
        try {
            return MAPPER.readValue(dbData, Undo.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
