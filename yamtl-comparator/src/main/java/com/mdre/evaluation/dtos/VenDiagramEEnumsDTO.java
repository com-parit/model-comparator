package com.mdre.evaluation.dtos;
import java.util.ArrayList;
import org.eclipse.emf.ecore.EEnum;
import com.mdre.evaluation.dtos.MatchedEEnumsDTO;

public class VenDiagramEEnumsDTO {
	public ArrayList<MatchedEEnumsDTO> matched;
	public ArrayList<EEnum> onlyInModel1;
	public ArrayList<EEnum> onlyInModel2;
}