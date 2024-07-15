package com.mdre.evaluation.services;

import java.io.File;
import org.eclipse.emf.emfatic.core.generator.ecore.EcoreGenerator;

public class EmfaticToEcoreService {
    public void run(String EMFATIC_FILE_PATH) {
        try {
            File file = new File(EMFATIC_FILE_PATH);
            EcoreGenerator generator = new EcoreGenerator();
            generator.generate(file, true);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
