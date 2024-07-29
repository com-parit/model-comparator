package com.mdre.evaluation.dtos;

public class HashingConfigurationDTO {
	public double HASHING_THRESHOLD = 0.5;
	public Boolean INCLUDE_ENUMS = true;
	public Boolean INCLUDE_ENUM_NAME = true;

	public Boolean INCLUDE_CLASS_ATTRIBUTES = true;
	public Boolean INCLUDE_CLASS_OPERATIONS = true;
	public Boolean INCLUDE_CLASS_PARAMETERS = true;
	public Boolean INCLUDE_CLASS_REFERENCES = true;

	public Boolean INCLUDE_ATTRIBUTE_NAME = true;
	public Boolean INCLUDE_ATTRIBUTE_CONTAINING_CLASS = true;	
	public Boolean INCLUDE_ATTRIBUTE_TYPE = true;
	public Boolean INCLUDE_ATTRIBUTE_LOWER_BOUND = true;
	public Boolean INCLUDE_ATTRIBUTE_UPPER_BOUND = true;
	public Boolean INCLUDE_ATTRIBUTE_IS_ORDERED = true;
	public Boolean INCLUDE_ATTRIBUTE_IS_UNIQUE = true;

	public Boolean INCLUDE_REFERENCES_NAME = true;
	public Boolean INCLUDE_REFERENCES_CONTAINING_CLASS = true;	
	public Boolean INCLUDE_REFERENCES_IS_CONTAINMENT = true;
	public Boolean INCLUDE_REFERENCES_LOWER_BOUND = true;
	public Boolean INCLUDE_REFERENCES_UPPER_BOUND = true;
	public Boolean INCLUDE_REFERENCES_IS_ORDERED = true;
	public Boolean INCLUDE_REFERENCES_IS_UNIQUE = true;

	public Boolean INCLUDE_OPERATION_NAME = true;
	public Boolean INCLUDE_OPERATION_CONTAINING_CLASS = true;
	public Boolean INCLUDE_OPERATION_PARAMETERS = true;

	public Boolean INCLUDE_PARAMETER_NAME = true;
	public Boolean INCLUDE_PARAMETER_TYPE = true;
	public Boolean INCLUDE_PARAMETER_OPERATION_NAME = true;
}