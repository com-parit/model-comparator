package com.mdre.evaluation;

import java.io.File;
import java.io.FileWriter; 
import org.eclipse.emf.emfatic.core.generator.emfatic.EmfaticGenerator;
import java.io.ByteArrayInputStream;
import java.util.*; 

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.emfatic.core.generator.emfatic.Writer;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.common.util.BasicMonitor;

public class EcoreToEmfaticService {

    private Resource getResource(ResourceSet resourceSet, String filePath)
    {
        URI uri = URI.createPlatformResourceURI(filePath, false);
        Resource resource = resourceSet.getResource(uri, true);
        return resource;
    }

    public void generate(String ecoreFilePath, String MODEL_OUTPUT_DIRECTORY)
    {
        try
        {
			ResourceSet resourceSet = new ResourceSetImpl();
			resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
			Resource ecoreResource = resourceSet.getResource(org.eclipse.emf.common.util.URI.createFileURI(ecoreFilePath), true);
			ecoreResource.setURI(URI.createFileURI(ecoreFilePath));
            String emfaticFilePath = MODEL_OUTPUT_DIRECTORY + ecoreFilePath.substring(ecoreFilePath.lastIndexOf("/"), ecoreFilePath.length() - 5) + "emf";
            Writer writer = new Writer();
            String emfaticText = writer.write(ecoreResource, BasicMonitor.toIProgressMonitor(new BasicMonitor.Printing(System.out)), null);
            FileWriter fw = new FileWriter(emfaticFilePath); 
            for (int i = 0; i < emfaticText.length(); i++) fw.write(emfaticText.charAt(i)); 
            System.out.println("Successfully written"); 
            fw.close(); 
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void run(String ECORE_FILE_PATH, String MODEL_OUTPUT_DIRECTORY) {
        try {
			EcoreToEmfaticService generator = new EcoreToEmfaticService();
			generator.generate(ECORE_FILE_PATH, MODEL_OUTPUT_DIRECTORY);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
