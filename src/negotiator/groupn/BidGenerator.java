package negotiator.groupn;

import java.util.HashMap;
import java.util.Map.Entry;

import negotiator.Bid;
import negotiator.actions.Offer;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;

public class BidGenerator {

	private Double threshold;
	private Bid highestBid;
	private Groupn groupn;
	private HashMap<String, Party> parties = new HashMap<String,Party>();
	private HashMap<Bid,Double> bidMap = new HashMap<Bid,Double>();
	

	public BidGenerator(Groupn temp, HashMap<Bid,Double> map) {
		threshold = temp.getThreshold();
		highestBid = temp.getHighestBid();
		groupn = temp;
		parties = temp.getParties();
		bidMap = map;
	}
	
	public void generateBid(){
		//which kind of bid do we want?
	}
	
	public Bid getBestUtilityBid(){
		Double bestUtility = 0.0;
		Bid bestBid = null;
		
		for (Entry<Bid, Double> e : bidMap.entrySet()){
			if (e.getValue() > bestUtility){
				bestUtility = e.getValue();
				bestBid = e.getKey();
			}
		}
		return bestBid;
		
	}
	/*
	public HashMap<Bid,Double> getBidsWithCondition( int indexOfIssue, ValueDiscrete valueOfIssue){
		HashMap<Bid,Double> conditionBids = new HashMap<Bid,Double>();
		HashMap<Integer,Value> bids = new HashMap<Integer,Value>();
		
		for (Entry<Bid, Double> e : bidMap.entrySet()){	
			bids = e.getKey().getValues();
			
			
			Issue temp = e.getKey().getIssues().get(indexOfIssue);
			((IssueDiscrete)temp).getValues();
		
		
		
		
		}
		return conditionBids;
		
	}*/
	
	
}
