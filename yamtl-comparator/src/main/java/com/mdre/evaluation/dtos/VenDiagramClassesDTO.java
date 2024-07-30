package com.mdre.evaluation.dtos;
import java.util.ArrayList;
import org.eclipse.emf.ecore.EClass;
import com.mdre.evaluation.dtos.MatchedClassesDTO;

public class VenDiagramClassesDTO {
	public ArrayList<MatchedClassesDTO> matched;
	public ArrayList<EClass> onlyInModel1;
	public ArrayList<EClass> onlyInModel2;
}