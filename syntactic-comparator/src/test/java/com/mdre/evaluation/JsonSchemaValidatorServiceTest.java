package com.mdre.evaluation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.mdre.evaluation.config.Constants;
import com.mdre.evaluation.services.JsonSchemaValidatorService;

@SpringBootTest
public class JsonSchemaValidatorServiceTest {

    @Autowired
    private JsonSchemaValidatorService jsonSchemaValidatorService;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testValidJsonAgainstSchema1() {
        String validJson = "{ \"USE_HASHING\": true }";
        assertDoesNotThrow(() -> jsonSchemaValidatorService.validate(validJson, Constants.CONFIGURATION_SCHEMA_KEY));
    }

    @Test
    public void testValidJsonAgainstSchema2() {
        String validJson = "{ \"USE_HASHING\": false, \"HASHING_THRESHOLD\": 0.7, \"INCLUDE_ENUMS\": true }";
        assertDoesNotThrow(() -> jsonSchemaValidatorService.validate(validJson, Constants.CONFIGURATION_SCHEMA_KEY));
    }

    @Test
    public void testInvalidJsonAgainstSchema1() {
        String invalidJson = "{ \"USE_HASHING\": \"true\" }"; // Incorrect type for USE_HASHING
        assertThrows(IllegalArgumentException.class, () -> jsonSchemaValidatorService.validate(invalidJson, Constants.CONFIGURATION_SCHEMA_KEY));
    }

    @Test
    public void testInvalidJsonAgainstSchema2() {
        String invalidJson = "{ \"HASHING_THRESHOLD\": 0.7 }"; // Missing required field USE_HASHING
        assertThrows(IllegalArgumentException.class, () -> jsonSchemaValidatorService.validate(invalidJson, Constants.CONFIGURATION_SCHEMA_KEY));
    }

    @Test
    public void testInvalidJsonAgainstSchema3() {
        String invalidJson = "{ \"USE_HASHING\": true, \"HASHING_THRESHOLD\": \"high\" }"; // Incorrect type for HASHING_THRESHOLD
        assertThrows(IllegalArgumentException.class, () -> jsonSchemaValidatorService.validate(invalidJson, Constants.CONFIGURATION_SCHEMA_KEY));
    }

    @Test
    public void testValidJsonWithAllDefaults() {
        String validJson = "{ \"USE_HASHING\": true }"; // Only required field, defaults for the rest
        assertDoesNotThrow(() -> jsonSchemaValidatorService.validate(validJson, Constants.CONFIGURATION_SCHEMA_KEY));
    }

    @Test
    public void testValidJsonWithSomeOptionalFields() {
        String validJson = "{ \"USE_HASHING\": false, \"INCLUDE_ENUM_NAME\": false, \"INCLUDE_ATTRIBUTE_NAME\": true }";
        assertDoesNotThrow(() -> jsonSchemaValidatorService.validate(validJson, Constants.CONFIGURATION_SCHEMA_KEY));
    }
}
