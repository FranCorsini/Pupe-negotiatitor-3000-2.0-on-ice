package negotiator.groupn;

import java.util.ArrayList;
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
	private ArrayList<Bid> alredyUsedBids = new ArrayList<Bid>();
	

	public BidGenerator(Groupn temp, HashMap<Bid,Double> map) {
		threshold = temp.getThreshold();
		highestBid = temp.getHighestBid();
		groupn = temp;
		parties = temp.getParties();
		bidMap = map;
	}
	
	//standard bid: it gets the best one
	public Bid generateBid(){
		Bid bestBid = getBestUtilityBid(bidMap);
		alredyUsedBids.add(bestBid);
		return bestBid;
	}
	
	//bidWithConditions: it get the best bid following the partial bid given(one condition)
	public Bid generateBid(int indexOfIssue, ValueDiscrete valueOfIssue) throws Exception{
		HashMap<Bid,Double> bids = new HashMap<Bid,Double>();
		
		bids = getBidsWithCondition(indexOfIssue,valueOfIssue);
		
		Bid bestBid = getBestUtilityBid(bids);
		alredyUsedBids.add(bestBid);
		return bestBid;
	}
	
	//bidWithConditions: it get the best bid following the partial bid given(multiple conditions)
	//the condition are those of the Bid passed
	public Bid generateBid(Bid condition) throws Exception{
		HashMap<Bid,Double> bids = new HashMap<Bid,Double>();
		HashMap<Integer,Value> temp = new HashMap<Integer,Value>();
		
		temp = condition.getValues();
		for(Entry<Integer,Value> e : temp.entrySet()){
			bids.putAll(getBidsWithCondition(e.getKey(),(ValueDiscrete)e.getValue()));
		}
		
		Bid bestBid = getBestUtilityBid(bids);
		alredyUsedBids.add(bestBid);
		return bestBid;
	}
	
	private Bid getBestUtilityBid(HashMap<Bid,Double> bids){
		Double bestUtility = 0.0;
		Bid bestBid = null;
		
		for (Entry<Bid, Double> e : bids.entrySet()){
			if (e.getValue() > bestUtility){
				bestUtility = e.getValue();
				bestBid = e.getKey();
			}
		}
		return bestBid;
		
	}
	
	private HashMap<Bid,Double> getBidsWithCondition( int indexOfIssue, ValueDiscrete valueOfIssue) throws Exception {
		HashMap<Bid,Double> conditionBids = new HashMap<Bid,Double>();
		//HashMap<Integer,Value> bids = new HashMap<Integer,Value>();
		
		for (Entry<Bid, Double> e : bidMap.entrySet()){	
			Value temp = e.getKey().getValue(indexOfIssue); //IDK here it requires the Exception
			if(temp.equals(valueOfIssue)){
				conditionBids.put(e.getKey(), e.getValue());
			}
			//Issue asd = e.getKey().getIssues().get(indexOfIssue);
			//((IssueDiscrete)asd).getValues();
		}
		return conditionBids;
		
	}
	
	
}
