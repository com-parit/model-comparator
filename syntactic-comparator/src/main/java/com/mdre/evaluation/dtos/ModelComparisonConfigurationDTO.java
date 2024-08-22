package com.mdre.evaluation.dtos;
import com.mdre.evaluation.dtos.HashingConfigurationDTO;
import com.mdre.evaluation.dtos.DigestConfigurationDTO;

public class ModelComparisonConfigurationDTO {
	public Boolean USE_HASHING = false;
	public Boolean INCLUDE_ENUMS = true;
	public Boolean INCLUDE_CLASS_ATTRIBUTES = true;
	public Boolean INCLUDE_CLASS_OPERATIONS = true;
	public Boolean INCLUDE_CLASS_SUPERTYPES = true;
	public Boolean INCLUDE_CLASS_REFERENCES = true;
	public Boolean INCLUDE_DEPENDENCIES = true;
    public Boolean MODEL_LEVEL_COMPARISON_DERIVED_FROM_CLASS_LEVEL_COMPARISON = false;
	public HashingConfigurationDTO hashingConfiguration;	
	public DigestConfigurationDTO digestConfiguration;
}