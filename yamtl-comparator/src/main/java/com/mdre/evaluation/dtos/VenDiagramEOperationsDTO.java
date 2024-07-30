package com.mdre.evaluation.dtos;
import java.util.ArrayList;
import org.eclipse.emf.ecore.EOperation;
import com.mdre.evaluation.dtos.MatchedEOperationsDTO;

public class VenDiagramEOperationsDTO {
	public ArrayList<MatchedEOperationsDTO> matched;
	public ArrayList<EOperation> onlyInModel1;
	public ArrayList<EOperation> onlyInModel2;
}