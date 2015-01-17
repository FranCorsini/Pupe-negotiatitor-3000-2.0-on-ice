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
	//private HashMap<String, Party> parties = new HashMap<String,Party>();
	private HashMap<Bid,Double> bidMap = new HashMap<Bid,Double>(); //unused bids
	private HashMap<Bid,Double> alredyUsedBids = new HashMap<Bid,Double>();
	private ArrayList<Party> parties = new ArrayList<Party>();
	private int turnsLeft;
	private Double divisionUs = 0.75, divisionOther = 0.25; //they have to sum up to 1.00
	private Double chosenIssuePercentage;
	
	private Random random = new Random();
	

	public BidGenerator(Groupn temp, HashMap<Bid,Double> map,int deadline) {
		threshold = temp.getThreshold();
		highestBid = temp.getHighestBid();
		groupn = temp;
		//parties = temp.getParties();
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
		for (int i = 0; i< parties.size(); i++ ){
			for(Entry<String, Double> e : 
		}
		
	}
	

	/*it returns the index of the issue picked. It also updates the chosenIssuePercentage with
	 * the percentage it has been chosen with 
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
		Double diceThrow = Math.random();
		Double tempWeight = 0.0;
		Integer issueNumber = null;
		for(int i=0; i< finalWeights.size();i++){
			tempWeight += finalWeights.get(i);
			if(tempWeight >= diceThrow && issueNumber == null){
				issueNumber = i; 
				chosenIssuePercentage = finalWeights.get(i);
			}
		}

		return (int)issueNumber;
	}
	
	
	/*
	 * The best overall bid(even those alredy offered)
	 */
	public Bid generateBestOverallBid(){
		Bid bestBid = null;
		
		if(alredyUsedBids.size() == 0){
			bestBid = getBestUtilityBid(bidMap);
		}
		else {
			bestBid = getBestUtilityBid(alredyUsedBids);
		}
		
		return bestBid;
	}
	
	/*
	 * The best not already offered bid
	 */
	public Bid generateBestNotAlredyUsedBid(){
		Bid bestBid = getBestUtilityBid(bidMap);
		return bestBid;
	}
	
	//bidWithConditions: it get the best bid following the partial bid given(one condition)
	public Bid generateBid(int indexOfIssue, ValueDiscrete valueOfIssue) throws Exception{
		HashMap<Bid,Double> bids = new HashMap<Bid,Double>();
		
		bids = getBidsWithCondition(indexOfIssue,valueOfIssue);
		
		Bid bestBid = getBestUtilityBid(bids);
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
		return bestBid;
	}
	
	// Generates the best bid with all issues set, except for one.
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
						// TODO Auto-generated catch block
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
		
		//updates in alredy used(if not alredy used)
		if(!(bids.equals(alredyUsedBids))){
			alredyUsedBids.put(bestBid, bestUtility);
			bidMap.remove(bestBid);
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
	
	private Bid getBestIssueValue(int issueNr, double chance) {
		Bid finalBid = null;
		double randomD = random.nextDouble();
		Bid lastGivenBid = groupn.getLastGivenBid();
		HashMap<Integer, Value> lastValues = lastGivenBid.getValues();
		lastValues.remove(issueNr);
		
		// They win by chance
		if (chance <= randomD) {
			
		} 
		// We win by chance
		else {
			finalBid = generateBidOneOut(lastValues);
		}
		
		return finalBid;
	}
	
	
}
