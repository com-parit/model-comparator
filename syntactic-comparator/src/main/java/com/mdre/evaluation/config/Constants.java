package com.mdre.evaluation.config;

public final class Constants {
    // Prevent instantiation
    private Constants() {}
	public static final String CONFIGURATION_SCHEMA_KEY = "configuration_schema";
    public static final String ATTRIBUTES_IDENTIFIER = "attributes";
    public static final String REFERENCES_IDENTIFIER = "references";
    public static final String OPERATIONS_IDENTIFIER = "operations";
    public static final String SUPERTYPES_IDENTIFIER = "superTypes";
    public static final String ENUMS_IDENTIFIER = "enumerations";
    public static final String CLASSES_IDENTIFIER = "classes";
    public static final String JSON_SCHEMA = """
        {
            \"$schema\": \"http://json-schema.org/draft-07/schema#\",
            \"title\": \"ModelComparisonConfigurationDTO\",
            \"type\": \"object\",
            \"properties\": {
                \"USE_HASHING\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"INCLUDE_DEPENDENCIES\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"MODEL_LEVEL_COMPARISON_DERIVED_FROM_CLASS_LEVEL_COMPARISON\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"HASHING_THRESHOLD\": {
                    \"type\": \"number\",
                    \"default\": 0.5
                },
                \"INCLUDE_ENUMS\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"INCLUDE_ENUM_NAME\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"INCLUDE_CLASS_ATTRIBUTES\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"INCLUDE_CLASS_OPERATIONS\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"INCLUDE_CLASS_PARAMETERS\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"INCLUDE_CLASS_REFERENCES\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"INCLUDE_CLASS_SUPERTYPES\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"INCLUDE_ATTRIBUTE_NAME\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"INCLUDE_ATTRIBUTE_CONTAINING_CLASS\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"INCLUDE_ATTRIBUTE_TYPE\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"INCLUDE_ATTRIBUTE_LOWER_BOUND\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"INCLUDE_ATTRIBUTE_UPPER_BOUND\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"INCLUDE_ATTRIBUTE_IS_ORDERED\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"INCLUDE_ATTRIBUTE_IS_UNIQUE\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"INCLUDE_REFERENCES_NAME\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"INCLUDE_REFERENCES_CONTAINING_CLASS\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"INCLUDE_REFERENCES_IS_CONTAINMENT\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"INCLUDE_REFERENCES_LOWER_BOUND\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"INCLUDE_REFERENCES_UPPER_BOUND\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"INCLUDE_REFERENCES_IS_ORDERED\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"INCLUDE_REFERENCES_IS_UNIQUE\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"INCLUDE_OPERATION_NAME\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"INCLUDE_OPERATION_CONTAINING_CLASS\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"INCLUDE_OPERATION_PARAMETERS\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"INCLUDE_PARAMETER_NAME\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"INCLUDE_PARAMETER_TYPE\": {
                    \"type\": \"boolean\",
                    \"default\": true
                },
                \"INCLUDE_PARAMETER_OPERATION_NAME\": {
                    \"type\": \"boolean\",
                    \"default\": true
                }
            },
            \"required\": [
                \"USE_HASHING\"
            ]
        }    
    """;
}