package tourGuide.service;

import java.util.Comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;

import tourGuide.user.RecommandedAttraction;

public class RecommandedAttractionDistanceComparator implements Comparator<RecommandedAttraction> {

	@Override
	public int compare(RecommandedAttraction arg0, RecommandedAttraction arg1) {
		 return new CompareToBuilder()
	                .append(arg0.getDistance(),arg1.getDistance()).toComparison();
	 
	}

}
