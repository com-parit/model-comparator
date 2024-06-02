    // create genmodel file from ecore model
    private void ecoreToGenModel() {
        ResourceSet resourceSet = new ResourceSetImpl();

        // Register the appropriate resource factory to handle all file extensions
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());

        // Load the Ecore model
        Resource ecoreResource = resourceSet.getResource(org.eclipse.emf.common.util.URI.createFileURI(ecoreModelFile), true);
        EPackage ePackage = (EPackage) ecoreResource.getContents().get(0);

        // Create Generator
        Generator generator = new Generator();
		String[] genmodelArgs = new String[]{"-ecore2GenModel", this.ecoreModelFile, this.javaProject, "class3"};
        generator.run(genmodelArgs);

        // Create a GenModel for the Ecore model
        // GenModel genModel = org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage.eINSTANCE.getGenModelFactory().createGenModel();
        // genModel.setModelDirectory(javaProject);
        // genModel.setOperationReflection(true);
        // genModel.setInputModelURI(ePackage);

        // Save the GenModel to a file
        // Resource genModelResource = resourceSet.createResource(org.eclipse.emf.common.util.URI.createFileURI(genModelFile));
        // genModelResource.getContents().add(genModel);

        // try {
        //     genModelResource.save(null);
        // } catch (java.io.IOException e) {
        //     e.printStackTrace();
        // }
    }

        private void genModelToCode(String genModelFilePath) {
        // // Load the GenModel
        // ResourceSet resourceSet = new ResourceSetImpl();

        // // Register the appropriate resource factory to handle all file extensions
        // resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());

        // // load gen model
        // // Resource genModelResource = resourceSet.getResource(org.eclipse.emf.common.util.URI.createFileURI(this.genModelFilePath), true);
        // Resource genModelResource = a;
        // GenModel genModel = (GenModel) genModelResource.getContents().get(0);
        // System.out.println("abcd" + genModel);

        // Generate code
        // genModel.setCanGenerate(true);
        // genModel.setFacadeHelperClass(null);
        // genModel.setRootExtendsClass(null);
        // genModel.reconcile();       
        try {
            // Load the GenModel
            ResourceSet resourceSet = new ResourceSetImpl();

            // Register the appropriate resource factory to handle all file extensions
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
            resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE); 
    		resourceSet.getPackageRegistry().put(GenModelPackage.eNS_URI, GenModelPackage.eINSTANCE); 

            // load gen model
            Resource genModelResource = resourceSet.getResource(org.eclipse.emf.common.util.URI.createFileURI(this.genModelFilePath), true);
            GenModel genModel = (GenModel) genModelResource.getContents().get(0);
            System.out.println("abcd" + genModel);

            genModel.validate();
            genModel.reconcile();		
            // genModel.setValidateModel(true); // The more checks the better
            // genModel.setCodeFormatting(true); // Normalize layout
            genModel.setForceOverwrite(true); // Don't overwrite read-only files
            genModel.setCanGenerate(true);
            // genModel.setFacadeHelperClass(null); // Non-null gives JDT default NPEs
            // genModel.setFacadeHelperClass(StandaloneASTFacadeHelper.class.getName()); // Bug 308069
            // genModel.setBundleManifest(false); // New manifests should be generated manually
            // genModel.setUpdateClasspath(false); // New class-paths should be generated manually

            if (genModel.getComplianceLevel().compareTo(GenJDKLevel.JDK50_LITERAL) < 0) {
                genModel.setComplianceLevel(GenJDKLevel.JDK50_LITERAL);
            }

            Diagnostic diagnostic = genModel.diagnose();
            // Globally register the default generator adapter factory for 
            // GenModel 
            // elements (only needed in standalone). 
            // 
            GeneratorAdapterFactory.Descriptor.Registry.INSTANCE.addDescriptor( 
                    GenModelPackage.eNS_URI, 
                    GenModelGeneratorAdapterFactory.DESCRIPTOR); 

            Generator generator = new Generator(); 

            // Create the generator and set the model-level input object. 
            // 
            generator.setInput(genModel); 


            // Generator model code. 
            generator.generate(genModel, 
                    GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE, 
                    new BasicMonitor.Printing(System.out)); 
        } catch (Exception e) {
            System.out.println(e);
        }
        // GeneratorAdapterFactory.Descriptor.Registry.INSTANCE.addDescriptor(GenModelPackage.eNS_URI, GenModelGeneratorAdapterFactory.DESCRIPTOR);
        // // Create the generator and set the model-level input object.
        // Generator generator = new Generator();
        // System.out.println(genModel);
        // generator.setInput(genModel);
        // // Generator model code.
        // generator.generate(genModel, GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE, new BasicMonitor.Printing(System.out));
        // System.out.println(generator.getGeneratedOutputs());

		// Generator codegen = new Generator();
        // codegen.setInput(genModel);
        // codegen.generate(genModel, GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE, new BasicMonitor.Printing(System.out));

        // Save generated code
        // genModel.generate(new BasicMonitor.Printing(System.out));
        // genModel.ge
    }