To run the project you need to install docker-compose

Once installed go to root of project and run "sudo docker-compose up --build".

After that, navigate to "usage", create venv, install requirements.txt and run main.py

In the usage folder, you will find "adapter.py" file demonstrating api calls to various services.

Sample config file

```
{
	"USE_HASHING": true,
	"HASHING_THRESHOLD": 1,
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

Steps to add comparison for a new element:

1. add DTOs: VenDiagramFor<Element>DTO.java and Matched<Element>DTO.java
2. 