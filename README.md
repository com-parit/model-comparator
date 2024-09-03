## Steps to Run via docker

The services developed as part of the tool have been packaged as Docker images and made public on Docker Hub. The table below describes the Docker images.

### Docker Images

| **Image Name**                         | **Tag** | **Description**        |
|----------------------------------------|---------|------------------------|
| jawad571/comparit-syntactic            | 1.0.0   | Syntactic Comparator    |
| jawad571/comparit-semantic             | 1.0.0   | Semantic Comparator     |
| jawad571/comparit-user-interface       | 1.0.0   | User Interface          |

### System Requirements

- **OS:** Linux
- **Memory:** 2GB Minimum (4GB Recommended)
- **Storage:** 10GB
- **Python version:** 3.8  
- **docker-compose version:** v2.17.2  
- **docker version:** 27.1.1  

### Steps to Run Using Docker Compose

To run the tool:

- Run docker-compose up --build --pull always.
- Access the user interface in your browser at http://localhost:8501.

## Steps to Run in Development Environment
The following steps demonstrate how to run this project in a development environment on a Linux system.

- Clone the repository https://github.com/jawad571/model-comparator. Note: This repository will be made public after the dissertation has been graded.
- Enter the command: cd syntactic-similarity
- Enter the command: ./gradlew bootrun. This will start the Spring Boot service on port 8080.
- Open a new terminal and navigate to the root directory of the project, then enter the command: cd semantic-simiarlity.
- Create a folder named archive inside this directory.
- Download the Google News word2vec embedding model (GoogleNews-vectors-negative300.bin) from this repository and place it in the archive folder.
- Create a subdirectory inside the archive folder named glove.6B and download the glove.6B.50d.txt embedding from this page into this folder.
- You can specify which embedding model you want to use in the .env file with the variable name EMBEDDING_MODEL; it can hold two values: gnews and glove6b50d.
- Enter the command: python3 -m venv env to create a new virtual environment.
- Enter the command: source env/bin/activate to activate the environment.
- Enter the command: pip install -r requirements.txt to install the libraries, then enter python app.py. This will start the Flask-based service on port 9090.
- Open a new terminal and navigate to the root directory of the project, then enter the command: cd user-interface.
- Repeat steps 5-7, and run streamlit run main.py. This will run the user interface.

As a result of performing these steps, you should have 3 terminal windows running:
- Syntactic-comparator on port 8080.
- Semantic-comparator on port 9090.
- User-interface on port 8501.

The specification of configuration file is described in the schema at syntactic-comparator/src/main/java/com/mdre/evaluation/schemas/configuration.json. Here is a sample config.json file.

```
{
	"USE_HASHING": true,
	"HASHING_THRESHOLD": 1,
	"MODEL_LEVEL_COMPARISON_DERIVED_FROM_CLASS_LEVEL_COMPARISON": true,
	"INCLUDE_CLASS_ATTRIBUTES": true,
	"INCLUDE_CLASS_REFERENCES": true,
	"INCLUDE_CLASS_OPERATIONS": true,
	"INCLUDE_CLASS_SUPERTYPES": true,
	"INCLUDE_ENUMS": true,
	
	"INCLUDE_ATTRIBUTE_NAME": true,
	"INCLUDE_ATTRIBUTE_CONTAINING_CLASS": true,	
	"INCLUDE_ATTRIBUTE_TYPE": true,
	"INCLUDE_ATTRIBUTE_LOWER_BOUND": true,
	"INCLUDE_ATTRIBUTE_UPPER_BOUND": true,
	"INCLUDE_ATTRIBUTE_IS_ORDERED": true,
	"INCLUDE_ATTRIBUTE_IS_UNIQUE": true,

	"INCLUDE_REFERENCES_NAME": true,
	"INCLUDE_REFERENCES_CONTAINING_CLASS": true,	
	"INCLUDE_REFERENCES_IS_CONTAINMENT": true,
	"INCLUDE_REFERENCES_LOWER_BOUND": true,
	"INCLUDE_REFERENCES_UPPER_BOUND": true,
	"INCLUDE_REFERENCES_IS_ORDERED": true,
	"INCLUDE_REFERENCES_IS_UNIQUE": true,

	"INCLUDE_OPERATION_NAME": true,
	"INCLUDE_OPERATION_CONTAINING_CLASS": true,
	"INCLUDE_OPERATION_PARAMETERS": true,

	"INCLUDE_PARAMETER_NAME": true,
	"INCLUDE_PARAMETER_TYPE": true,
	"INCLUDE_PARAMETER_OPERATION_NAME": true
}
```

## Steps to replicate testing resutls

To validate the correctness of the model matching algorithms used in the tool, we prepared a benchmark dataset containing triples; base ecore model, mutant, and their similarity metrics. The tool was then executed to extract the similarity metrics for each ecore model pairs and compared against the given similarity metrics to gauge the accuracy of the tool. 

The preliminary preparation of the dataset involves downloading the modelset zip folder: https://github.com/modelset/modelset-dataset/releases. We have used the Dec 14, 2023 release of modelset (size: 104MBs). Then we extract the all the ecore models present in the repo-ecore-all folder.

For each ecore model, 10 mutants were generated using YAMTL. The details of each mutant can be found in the table below; each entry represents the percentage of similar element tags between the base model and predicted model. The mutants cover edge cases and one hybrid case described as "mutant 10" in the table. For each mutant; we computed the percentage of false positives by subtracting the specified percentage of similar elements from 100. For example, if we want 90 percent of operations to be true positives, then the number of false negatives that would be generated for that mutant would be 10 percent (100 - 90 = 10) of the total operations. The false negatives are generated by keeping the elements in the base model intact; and generating no corresponding element for the mutant. However, to avoid having equal precision and recall, we generate a non-matching element in the mutant for 40 percent of the false negatives.

### Table for Description of Mutants

Each entry represents the percentage of similar elements between the base model and the predicted model.

| **Mutant** | **Classes** | **Attributes** | **Operations** | **References** |
|------------|-------------|----------------|----------------|----------------|
| 1          | 100         | 100            | 100            | 100            |
| 2          | 0           | 0              | 0              | 0              |
| 3          | 100         | 100            | 0              | 0              |
| 4          | 100         | 0              | 0              | 100            |
| 5          | 100         | 0              | 100            | 0              |
| 6          | 100         | 100            | 100            | 0              |
| 7          | 100         | 100            | 0              | 100            |
| 8          | 100         | 100            | 0              | 100            |
| 9          | 100         | 0              | 100            | 100            |
| 10         | 100         | 70             | 90             | 80             |

Here are the steps to reproduce the evaluation results:

- Download the modelset zip folder and copy it in functional-tests/modelset folder
- Create a virtual environment, install dependencies and run migrate.py script. This will generate a folder named "ecore_models_modelset"
- copy the folder "ecore_models_modelset" in functional-tests/
- Go to syntactic-comparator/src/main/java/com/mdre/evaluation/services/modelComparisonService/ModelMutator.groovy and sepcify the directory of "ecore_models_modelset" folder inside the "run()" function.
- run "./gradlew runModelMutatorJavaWrapper: This will run the model mutator that will generate mutants within the "ecore_models_modelset" fodler. 
- To execute comparison using the tool's APIs, create a virtual env inside functional-tests folder and install dependencies using requirements.txt file. Then run functional-tests/api-tests.py. This should take 10-15 mins depending on your system's capacity. This script runs sanity tests for the available APIs for ecore model comparison, then executes model comparison for each model with its mutants. For example, if we have 5 models then the comparison would run 50 times (10 times for each model with its 10 mutants). Once the execution of this script finishes, the following csv files will be generated:
  - "evaluation_results.csv" will be produced at functional-tests/.
- To view the visualizations for results, navigate to functional-tests/ and execute "streamlit run evaluation_ui.py". This will open a user-interface providing insights into the evaluation results.


