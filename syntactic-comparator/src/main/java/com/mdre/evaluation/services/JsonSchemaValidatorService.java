package com.mdre.evaluation.services;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.io.FileReader;

import com.mdre.evaluation.config.Constants;

@Service
public class JsonSchemaValidatorService {

    private final Map<String, Schema> schemas = new HashMap<>();

    public JsonSchemaValidatorService() {
        loadSchema(Constants.CONFIGURATION_SCHEMA_KEY, Constants.JSON_SCHEMA);
        System.out.println("Schema loaded as " + Constants.CONFIGURATION_SCHEMA_KEY);
    }

    private void loadSchema(String key, String schemaString) {
        try {
            JSONObject rawSchema = new JSONObject(new JSONTokener(schemaString));
            Schema schema = SchemaLoader.load(rawSchema);
            schemas.put(key, schema);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load JSON schema: " + schemaString, e);
        }
    }

    public void validate(String jsonString, String schemaKey) {
        Schema schema = schemas.get(schemaKey);
        if (schema == null) {
            throw new IllegalArgumentException("No schema found for key: " + schemaKey);
        }

        JSONObject jsonObject = new JSONObject(jsonString);
        try {
            schema.validate(jsonObject);
        } catch (ValidationException e) {
            throw new IllegalArgumentException("JSON validation error: " + e);
        }
    }
}
