package negotiator.groupn;

import java.util.HashMap;

import negotiator.Bid;
import negotiator.actions.Offer;

public class BidGenerator {

	private Double threshold;
	private Bid highestBid;
	private Groupn groupn;
	private HashMap<String, Party> parties;
	

	public BidGenerator(Groupn temp) {
		threshold = temp.getThreshold();
		highestBid = temp.getHighestBid();
		groupn = temp;
		parties = temp.getParties();
	}
	
	public void generateBid(){
		
	}
	
}
