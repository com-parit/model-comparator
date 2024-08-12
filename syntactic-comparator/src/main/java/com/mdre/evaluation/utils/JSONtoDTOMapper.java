package com.mdre.evaluation.utils;
import com.mdre.evaluation.dtos.ModelComparisonConfigurationDTO;
import com.mdre.evaluation.dtos.HashingConfigurationDTO;
import com.mdre.evaluation.dtos.DigestConfigurationDTO;
import org.json.JSONObject;


public class JSONtoDTOMapper {
	public static ModelComparisonConfigurationDTO mapToModelComparisonConfigurationDTO(JSONObject jsonObject) {
		ModelComparisonConfigurationDTO modelComparisonConfigurationDTO = new ModelComparisonConfigurationDTO();
		HashingConfigurationDTO hashingConfiguration = new HashingConfigurationDTO();
		DigestConfigurationDTO digestConfiguration = new DigestConfigurationDTO();

		try {
			modelComparisonConfigurationDTO.USE_HASHING = jsonObject.getBoolean("USE_HASHING");
		} catch (Exception e) {
			System.out.println("Incorrect configuration provided for USE_HASHING; using default configuration");
		}

		try {
			modelComparisonConfigurationDTO.INCLUDE_CLASS_ATTRIBUTES = jsonObject.getBoolean("INCLUDE_CLASS_ATTRIBUTES");
		} catch (Exception e) {
			System.out.println("Incorrect configuration provided for INCLUDE_CLASS_ATTRIBUTES; using default configuration");
		}

		try {
			modelComparisonConfigurationDTO.INCLUDE_CLASS_SUPERTYPES = jsonObject.getBoolean("INCLUDE_CLASS_SUPERTYPES");
		} catch (Exception e) {
			System.out.println("Incorrect configuration provided for INCLUDE_CLASS_SUPERTYPES; using default configuration");
		}

		try {
			modelComparisonConfigurationDTO.INCLUDE_ENUMS = jsonObject.getBoolean("INCLUDE_ENUMS");
		} catch (Exception e) {
			System.out.println("Incorrect configuration provided for INCLUDE_ENUMS; using default configuration");
		}

		try {
			modelComparisonConfigurationDTO.INCLUDE_CLASS_OPERATIONS = jsonObject.getBoolean("INCLUDE_CLASS_OPERATIONS");
		} catch (Exception e) {
			System.out.println("Incorrect configuration provided for INCLUDE_CLASS_OPERATIONS; using default configuration");
		}


		try {
			modelComparisonConfigurationDTO.INCLUDE_CLASS_REFERENCES = jsonObject.getBoolean("INCLUDE_CLASS_REFERENCES");
		} catch (Exception e) {
			System.out.println("Incorrect configuration provided for INCLUDE_CLASS_REFERENCES; using default configuration");
		}

		if (modelComparisonConfigurationDTO.USE_HASHING) {
			try {
				hashingConfiguration.INCLUDE_CLASS_PARAMETERS = jsonObject.getBoolean("INCLUDE_CLASS_PARAMETERS");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_CLASS_PARAMETERS; using default configuration");
			}

			try {
				hashingConfiguration.HASHING_THRESHOLD = jsonObject.getDouble("HASHING_THRESHOLD");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for HASHING_THRESHOLD; using default configuration");
			}


			try {
				hashingConfiguration.INCLUDE_ENUM_NAME = jsonObject.getBoolean("INCLUDE_ENUM_NAME");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_ENUM_NAME; using default configuration");
			}

			try {
				hashingConfiguration.INCLUDE_ATTRIBUTE_NAME = jsonObject.getBoolean("INCLUDE_ATTRIBUTE_NAME");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_ATTRIBUTE_NAME; using default configuration");
			}

			try {
				hashingConfiguration.INCLUDE_ATTRIBUTE_CONTAINING_CLASS = jsonObject.getBoolean("INCLUDE_ATTRIBUTE_CONTAINING_CLASS");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_ATTRIBUTE_CONTAINING_CLASS; using default configuration");
			}

			try {
				hashingConfiguration.INCLUDE_ATTRIBUTE_TYPE = jsonObject.getBoolean("INCLUDE_ATTRIBUTE_TYPE");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_ATTRIBUTE_TYPE; using default configuration");
			}

			try {
				hashingConfiguration.INCLUDE_ATTRIBUTE_LOWER_BOUND = jsonObject.getBoolean("INCLUDE_ATTRIBUTE_LOWER_BOUND");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_ATTRIBUTE_LOWER_BOUND; using default configuration");
			}

			try {
				hashingConfiguration.INCLUDE_ATTRIBUTE_UPPER_BOUND = jsonObject.getBoolean("INCLUDE_ATTRIBUTE_UPPER_BOUND");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_ATTRIBUTE_UPPER_BOUND; using default configuration");
			}

			try {
				hashingConfiguration.INCLUDE_ATTRIBUTE_IS_ORDERED = jsonObject.getBoolean("INCLUDE_ATTRIBUTE_IS_ORDERED");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_ATTRIBUTE_IS_ORDERED; using default configuration");
			}

			try {
				hashingConfiguration.INCLUDE_ATTRIBUTE_IS_UNIQUE = jsonObject.getBoolean("INCLUDE_ATTRIBUTE_IS_UNIQUE");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_ATTRIBUTE_IS_UNIQUE; using default configuration");
			}

			try {
				hashingConfiguration.INCLUDE_REFERENCES_NAME = jsonObject.getBoolean("INCLUDE_REFERENCES_NAME");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_REFERENCES_NAME; using default configuration");
			}

			try {
				hashingConfiguration.INCLUDE_REFERENCES_CONTAINING_CLASS = jsonObject.getBoolean("INCLUDE_REFERENCES_CONTAINING_CLASS");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_REFERENCES_CONTAINING_CLASS; using default configuration");
			}

			try {
				hashingConfiguration.INCLUDE_REFERENCES_IS_CONTAINMENT = jsonObject.getBoolean("INCLUDE_REFERENCES_IS_CONTAINMENT");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_REFERENCES_IS_CONTAINMENT; using default configuration");
			}

			try {
				hashingConfiguration.INCLUDE_REFERENCES_LOWER_BOUND = jsonObject.getBoolean("INCLUDE_REFERENCES_LOWER_BOUND");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_REFERENCES_LOWER_BOUND; using default configuration");
			}

			try {
				hashingConfiguration.INCLUDE_REFERENCES_UPPER_BOUND = jsonObject.getBoolean("INCLUDE_REFERENCES_UPPER_BOUND");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_REFERENCES_UPPER_BOUND; using default configuration");
			}

			try {
				hashingConfiguration.INCLUDE_REFERENCES_IS_ORDERED = jsonObject.getBoolean("INCLUDE_REFERENCES_IS_ORDERED");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_REFERENCES_IS_ORDERED; using default configuration");
			}

			try {
				hashingConfiguration.INCLUDE_REFERENCES_IS_UNIQUE = jsonObject.getBoolean("INCLUDE_REFERENCES_IS_UNIQUE");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_REFERENCES_IS_UNIQUE; using default configuration");
			}

			try {
				hashingConfiguration.INCLUDE_OPERATION_NAME = jsonObject.getBoolean("INCLUDE_OPERATION_NAME");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_OPERATION_NAME; using default configuration");
			}

			try {
				hashingConfiguration.INCLUDE_OPERATION_CONTAINING_CLASS = jsonObject.getBoolean("INCLUDE_OPERATION_CONTAINING_CLASS");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_OPERATION_CONTAINING_CLASS; using default configuration");
			}

			try {
				hashingConfiguration.INCLUDE_OPERATION_PARAMETERS = jsonObject.getBoolean("INCLUDE_OPERATION_PARAMETERS");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_OPERATION_PARAMETERS; using default configuration");
			}

			try {
				hashingConfiguration.INCLUDE_PARAMETER_NAME = jsonObject.getBoolean("INCLUDE_PARAMETER_NAME");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_PARAMETER_NAME; using default configuration");
			}

			try {
				hashingConfiguration.INCLUDE_PARAMETER_TYPE = jsonObject.getBoolean("INCLUDE_PARAMETER_TYPE");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_PARAMETER_TYPE; using default configuration");
			}

			try {
				hashingConfiguration.INCLUDE_PARAMETER_OPERATION_NAME = jsonObject.getBoolean("INCLUDE_PARAMETER_OPERATION_NAME");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_PARAMETER_OPERATION_NAME; using default configuration");
			}
		}

		if (!modelComparisonConfigurationDTO.USE_HASHING) {
			try {
				digestConfiguration.INCLUDE_CLASS_PARAMETERS = jsonObject.getBoolean("INCLUDE_CLASS_PARAMETERS");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_CLASS_PARAMETERS; using default configuration");
			}

			try {
				digestConfiguration.INCLUDE_ENUM_NAME = jsonObject.getBoolean("INCLUDE_ENUM_NAME");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_ENUM_NAME; using default configuration");
			}

			try {
				digestConfiguration.INCLUDE_ATTRIBUTE_NAME = jsonObject.getBoolean("INCLUDE_ATTRIBUTE_NAME");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_ATTRIBUTE_NAME; using default configuration");
			}

			try {
				digestConfiguration.INCLUDE_ATTRIBUTE_CONTAINING_CLASS = jsonObject.getBoolean("INCLUDE_ATTRIBUTE_CONTAINING_CLASS");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_ATTRIBUTE_CONTAINING_CLASS; using default configuration");
			}

			try {
				digestConfiguration.INCLUDE_ATTRIBUTE_TYPE = jsonObject.getBoolean("INCLUDE_ATTRIBUTE_TYPE");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_ATTRIBUTE_TYPE; using default configuration");
			}

			try {
				digestConfiguration.INCLUDE_ATTRIBUTE_LOWER_BOUND = jsonObject.getBoolean("INCLUDE_ATTRIBUTE_LOWER_BOUND");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_ATTRIBUTE_LOWER_BOUND; using default configuration");
			}

			try {
				digestConfiguration.INCLUDE_ATTRIBUTE_UPPER_BOUND = jsonObject.getBoolean("INCLUDE_ATTRIBUTE_UPPER_BOUND");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_ATTRIBUTE_UPPER_BOUND; using default configuration");
			}

			try {
				digestConfiguration.INCLUDE_ATTRIBUTE_IS_ORDERED = jsonObject.getBoolean("INCLUDE_ATTRIBUTE_IS_ORDERED");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_ATTRIBUTE_IS_ORDERED; using default configuration");
			}

			try {
				digestConfiguration.INCLUDE_ATTRIBUTE_IS_UNIQUE = jsonObject.getBoolean("INCLUDE_ATTRIBUTE_IS_UNIQUE");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_ATTRIBUTE_IS_UNIQUE; using default configuration");
			}

			try {
				digestConfiguration.INCLUDE_REFERENCES_NAME = jsonObject.getBoolean("INCLUDE_REFERENCES_NAME");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_REFERENCES_NAME; using default configuration");
			}

			try {
				digestConfiguration.INCLUDE_REFERENCES_CONTAINING_CLASS = jsonObject.getBoolean("INCLUDE_REFERENCES_CONTAINING_CLASS");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_REFERENCES_CONTAINING_CLASS; using default configuration");
			}

			try {
				digestConfiguration.INCLUDE_REFERENCES_IS_CONTAINMENT = jsonObject.getBoolean("INCLUDE_REFERENCES_IS_CONTAINMENT");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_REFERENCES_IS_CONTAINMENT; using default configuration");
			}

			try {
				digestConfiguration.INCLUDE_REFERENCES_LOWER_BOUND = jsonObject.getBoolean("INCLUDE_REFERENCES_LOWER_BOUND");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_REFERENCES_LOWER_BOUND; using default configuration");
			}

			try {
				digestConfiguration.INCLUDE_REFERENCES_UPPER_BOUND = jsonObject.getBoolean("INCLUDE_REFERENCES_UPPER_BOUND");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_REFERENCES_UPPER_BOUND; using default configuration");
			}

			try {
				digestConfiguration.INCLUDE_REFERENCES_IS_ORDERED = jsonObject.getBoolean("INCLUDE_REFERENCES_IS_ORDERED");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_REFERENCES_IS_ORDERED; using default configuration");
			}

			try {
				digestConfiguration.INCLUDE_REFERENCES_IS_UNIQUE = jsonObject.getBoolean("INCLUDE_REFERENCES_IS_UNIQUE");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_REFERENCES_IS_UNIQUE; using default configuration");
			}

			try {
				digestConfiguration.INCLUDE_OPERATION_NAME = jsonObject.getBoolean("INCLUDE_OPERATION_NAME");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_OPERATION_NAME; using default configuration");
			}

			try {
				digestConfiguration.INCLUDE_OPERATION_CONTAINING_CLASS = jsonObject.getBoolean("INCLUDE_OPERATION_CONTAINING_CLASS");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_OPERATION_CONTAINING_CLASS; using default configuration");
			}

			try {
				digestConfiguration.INCLUDE_OPERATION_PARAMETERS = jsonObject.getBoolean("INCLUDE_OPERATION_PARAMETERS");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_OPERATION_PARAMETERS; using default configuration");
			}

			try {
				digestConfiguration.INCLUDE_PARAMETER_NAME = jsonObject.getBoolean("INCLUDE_PARAMETER_NAME");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_PARAMETER_NAME; using default configuration");
			}

			try {
				digestConfiguration.INCLUDE_PARAMETER_TYPE = jsonObject.getBoolean("INCLUDE_PARAMETER_TYPE");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_PARAMETER_TYPE; using default configuration");
			}

			try {
				digestConfiguration.INCLUDE_PARAMETER_OPERATION_NAME = jsonObject.getBoolean("INCLUDE_PARAMETER_OPERATION_NAME");
			} catch (Exception e) {
				System.out.println("Incorrect configuration provided for INCLUDE_PARAMETER_OPERATION_NAME; using default configuration");
			}
		}
		modelComparisonConfigurationDTO.hashingConfiguration = hashingConfiguration;
		modelComparisonConfigurationDTO.digestConfiguration = digestConfiguration;
		return modelComparisonConfigurationDTO;
	}
}