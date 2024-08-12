package com.mdre.evaluation.dtos;
import java.util.ArrayList;
import com.mdre.evaluation.dtos.MatchedElementsDTO;

public class VenDiagramDTO<X> {
	public ArrayList<MatchedElementsDTO<X>> matched;
	public ArrayList<X> onlyInModel1;
	public ArrayList<X> onlyInModel2;
}