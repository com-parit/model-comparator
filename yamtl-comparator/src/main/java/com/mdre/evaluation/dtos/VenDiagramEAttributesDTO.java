package com.mdre.evaluation.dtos;
import java.util.ArrayList;
import org.eclipse.emf.ecore.EAttribute;
import com.mdre.evaluation.dtos.MatchedEAttributesDTO;

public class VenDiagramEAttributesDTO {
	public ArrayList<MatchedEAttributesDTO> matched;
	public ArrayList<EAttribute> onlyInModel1;
	public ArrayList<EAttribute> onlyInModel2;
}