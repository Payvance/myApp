package com.payvance.erp_saas.core.convertor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.payvance.erp_saas.core.enums.NotificationChannel;

import java.util.HashSet;
import java.util.Set;

@Converter
public class NotificationChannelConverter
        implements AttributeConverter<Set<NotificationChannel>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Set<NotificationChannel> attribute) {
        try {
            if (attribute == null) return "[]";
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Error converting channels to JSON", e);
        }
    }

    @Override
    public Set<NotificationChannel> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isBlank()) return new HashSet<>();
            return objectMapper.readValue(
                    dbData,
                    new TypeReference<Set<NotificationChannel>>() {}
            );
        } catch (Exception e) {
            throw new RuntimeException("Error reading channels JSON", e);
        }
    }
}