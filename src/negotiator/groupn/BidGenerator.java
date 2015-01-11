package negotiator.groupn;

import negotiator.Bid;
import negotiator.actions.Offer;

public class BidGenerator {

	private Double threshold;
	private Bid highestBid;
	private Groupn groupn;

	public BidGenerator(Groupn temp) {
		threshold = temp.getThreshold();
		highestBid = temp.getHighestBid();
		groupn = temp;
	}
	
}
