package negotiator.groupn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import negotiator.Bid;
import negotiator.DeadlineType;
import negotiator.actions.Offer;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Objective;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;
import negotiator.utility.Evaluator;
import negotiator.utility.UtilitySpace;


public class BidGenerator {

	private Double threshold;
	private Bid highestBid;
	private Groupn groupn;
	
	// Unused bids
	private HashMap<Bid,Double> bidMap = new HashMap<Bid,Double>();
	
	private HashMap<Bid,Double> alreadyUsedBids = new HashMap<Bid,Double>();
	private ArrayList<Party> parties = new ArrayList<Party>();
	private int turnsLeft;
	
	// Based on chance, so they have to sum up to 1.00
	private Double divisionUs = 0.75;
	private Double divisionOther = 1 - divisionUs;
	
	private Double chosenIssuePercentage;
	
	private Random random = new Random();
	

	public BidGenerator(Groupn temp, HashMap<Bid,Double> map,int deadline) {
		threshold = temp.getThreshold();
		highestBid = temp.getHighestBid();
		groupn = temp;
		bidMap = map;
		turnsLeft = deadline;
		HashMap<String, Party> tempParties = new HashMap<String,Party>();
		tempParties = temp.getParties();
		for(Entry<String, Party> e : tempParties.entrySet()){
			parties.add(e.getValue());
		}
		
	}
	
	public Bid generateBid(){
		
		
		return null;
	}
	

	private ValueDiscrete getTheirBestValue(int issueNumber){
		HashMap<String, Double> valueWeights = new HashMap<String, Double>();
		boolean isFirst = true;
		
		//sum up all the values of all the parties
		for (int i = 0; i< parties.size(); i++ ){
			for(Entry<String, Double> e :  parties.get(i).getIssueModels().get(issueNumber).getUtility().entrySet()){
				if(isFirst == true){
					valueWeights.put(e.getKey(), e.getValue());
					isFirst = false;
				}
				else{
					valueWeights.put(e.getKey(),valueWeights.get(e.getKey()) + e.getValue());
				}
			}
		}
		
		//take out best value
		String bestValueKey = null;
		Double bestValueWeight = 0.0;
		for(Entry<String, Double> e : valueWeights.entrySet()){
			if(e.getValue() > bestValueWeight){
				bestValueWeight = e.getValue();
				bestValueKey = e.getKey();
			}
		}
		
		//get the ValueDiscrete     -----i use the first party since everyone has same discrete issues
		ValueDiscrete value = null;
		for(int i = 0; i< parties.get(0).getIssueModels().size(); i++){
			if(parties.get(0).getIssueModels().get(issueNumber).getValues().get(i).getValue() == bestValueKey){
				value = parties.get(0).getIssueModels().get(issueNumber).getValues().get(i);
			}
		}
		
		return value;
	}
	

	/**
	 * Returns the index of the issue picked and updates the chosenIssuePercentage with the 
	 * percentage.
	 * @return IssueNumber		The chosen issue to be changed in the next bid.
	 */
	private int getWeightedRandomIssueIndex(){
		HashMap<IssueModel,Double> issueWeightsOfOtherParties = new HashMap<IssueModel,Double>();
		
		//gets the sum of all weights of all parties for the issues
		Iterator<Party> partIt = parties.iterator();
		boolean isFirst = true;
		while(partIt.hasNext()){
			Iterator<IssueModel> itr = ((Party)partIt.next()).getIssueModels().iterator();
			if(isFirst == true){
				while(itr.hasNext()){
					IssueModel issue = (IssueModel)itr.next();
					issueWeightsOfOtherParties.put(issue, issue.getValue());
					isFirst = false; 
				}
			if(isFirst == false){
				while(itr.hasNext()){
					IssueModel issue = (IssueModel)itr.next();
					issueWeightsOfOtherParties.put(issue, issueWeightsOfOtherParties.get(issue) + issue.getValue());
				}
			}
			}
		}
		//get totalweight to normalize it
		Double totalWeight = 0.0;
		for(Entry<IssueModel,Double> e : issueWeightsOfOtherParties.entrySet()){
			totalWeight += e.getValue();
		}
		//generate other parties weights
		ArrayList<Double> otherWeights = new ArrayList<Double>();
		for(Entry<IssueModel,Double> e : issueWeightsOfOtherParties.entrySet()){
			Double temp = e.getValue();
			e.setValue(temp / totalWeight);
			otherWeights.add(temp / totalWeight);
		}
		//get my weights
		ArrayList<Double> myWeights = new ArrayList<Double>();
		for(Entry<Objective,Evaluator> e : groupn.getUtilitySpace().getEvaluators()){
			myWeights.add(e.getValue().getWeight());
		}
		//get the array of overall weights
		ArrayList<Double> finalWeights = new ArrayList<Double>();
		for(int i=0; i< myWeights.size();i++){
			Double mine =  myWeights.get(i) * divisionUs;
			Double other = otherWeights.get(i) * divisionOther;
			finalWeights.add(mine + other);
		}
		
		//pick the Issue by throwing the dice
		Double diceThrow = random.nextDouble();
		Double tempWeight = 0.0;
		Integer issueNumber = null;
		for(int i=0; i< finalWeights.size();i++){
			tempWeight += finalWeights.get(i);
			if(tempWeight >= diceThrow){
				issueNumber = i; 
				chosenIssuePercentage = finalWeights.get(i);
				break;
			}
		}

		return (int)issueNumber;
	}
	
	
	/**
	 * Generate the best overall bid, even those already offered.
	 * @return Bid	The generated bid.
	 */
	public Bid generateBestOverallBid(){
		Bid bestBid = null;
		
		if(alreadyUsedBids.size() == 0){
			bestBid = getBestUtilityBid(bidMap);
		}
		else {
			bestBid = getBestUtilityBid(alreadyUsedBids);
		}
		
		return bestBid;
	}
	
	/**
	 * Generates the best bid which has not been offered yet.
	 * @return Bid	The generated bid.
	 */
	public Bid generateBestNotAlreadyUsedBid(){
		Bid bestBid = getBestUtilityBid(bidMap);
		return bestBid;
	}
	
	/**
	 * Generates a bid with one issue locked as a condition
	 * @param indexOfIssue	The index of the locked in issue.
	 * @param valueOfIssue	The value of the issue.
	 * @return	Bid			The generated bid with one locked in issue.
	 */
	public Bid generateBid(int indexOfIssue, ValueDiscrete valueOfIssue){
		HashMap<Bid,Double> bids = new HashMap<Bid,Double>();
		
		bids = getBidsWithCondition(indexOfIssue,valueOfIssue);
		
		Bid bestBid = getBestUtilityBid(bids);
		return bestBid;
	}
	
	/**
	 * Generates the best bid with all issues set, except for one.
	 * @param condition		The pre-set issues
	 * @return	Bid			The generated bid.
	 */
	// TODO Take into account the bids that have already been offered.
	public Bid generateBidOneOut(HashMap<Integer, Value> condition) {
		Bid bid = null;
		double maxUtility = 0.0;
		UtilitySpace us = groupn.getUtilitySpace();
		
		for (int i = 1; i <= condition.size() + 1; i++) {
			if (!condition.containsKey(i)) {
				IssueDiscrete issue = (IssueDiscrete) us.getDomain().getIssues().get(i);
				
				for (int j = 1; j <= issue.getNumberOfValues(); j++) {
					HashMap<Integer, Value> values = (HashMap<Integer, Value>) condition.clone();
					values.put(i, issue.getValue(j));
					
					try {
						Bid currentBid = new Bid(us.getDomain(), values);
						
						if (maxUtility < us.getUtility(currentBid)) {
							maxUtility = us.getUtility(currentBid);
							bid = currentBid;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}				
				break;
			}			
		}		
		return bid;
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
		
		//updates in already used(if not already used)
		if(!(bids.equals(alreadyUsedBids))){
			alreadyUsedBids.put(bestBid, bestUtility);
			bidMap.remove(bestBid);
		}
		return bestBid;
		
	}
	
	
	private HashMap<Bid,Double> getBidsWithCondition( int indexOfIssue, ValueDiscrete valueOfIssue) {
		HashMap<Bid,Double> conditionBids = new HashMap<Bid,Double>();
		
		for (Entry<Bid, Double> e : bidMap.entrySet()){	
			try {
				Value temp = e.getKey().getValue(indexOfIssue);
				if(temp.equals(valueOfIssue)){
					conditionBids.put(e.getKey(), e.getValue());
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return conditionBids;	
	}
	
	/**
	 * Generates the best bid based on the weights of both us and the parties
	 * @param issueNr	The issue to be changed for the new bid
	 * @param chance	Chance of picking their preference
	 * @return Bid		The best bid with the new issue value.
	 */
	public Bid generateBestBid() {
		int issueNr = getWeightedRandomIssueIndex();
		Bid finalBid = null;
		double randomD = random.nextDouble();
		Bid lastGivenBid = groupn.getLastGivenBid();
		HashMap<Integer, Value> lastValues = lastGivenBid.getValues();
		lastValues.remove(issueNr);
		
		// They win by chance
		if (chosenIssuePercentage <= randomD) {
			ValueDiscrete value = getTheirBestValue(issueNr);
			lastValues.put(issueNr, value);
			try {
				finalBid = new Bid(groupn.getUtilitySpace().getDomain(), lastValues);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
		// We win by chance
		else {
			finalBid = generateBidOneOut(lastValues);
		}
		
		return finalBid;
	}
	
	
}
