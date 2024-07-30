package com.mdre.evaluation.dtos;
import java.util.ArrayList;
import org.eclipse.emf.ecore.EReference;
import com.mdre.evaluation.dtos.MatchedEReferencesDTO;

public class VenDiagramEReferencesDTO {
	public ArrayList<MatchedEReferencesDTO> matched;
	public ArrayList<EReference> onlyInModel1;
	public ArrayList<EReference> onlyInModel2;
}