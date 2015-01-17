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
	}
	
	public Bid generateBid(){
		
		
		return null;
	}
	

	private ValueDiscrete getTheirBestValue(int issueNumber){
		HashMap<String, Double> valueWeights = new HashMap<String, Double>();
		
		// Zero-based index
		issueNumber = issueNumber - 1;
		
		//sum up all the values of all the parties
		for (int i = 0; i< parties.size(); i++ ){
			for(Entry<String, Double> e :  parties.get(i).getIssueModels().get(issueNumber).getUtility().entrySet()){
				Double issWeight = parties.get(i).getIssueModels().get(issueNumber).getValue();
				if(valueWeights.get(e.getKey()) == null){
					valueWeights.put(e.getKey(), e.getValue() * issWeight);
				}
				else{
					valueWeights.put(e.getKey(),valueWeights.get(e.getKey()) + (e.getValue() * issWeight));
				}
			}
		}
		
		//take out best value
		String bestValueKey = null;
		Double bestValueWeight = 0.0;
		ValueDiscrete previousValue = null;
		
		try {
			previousValue = (ValueDiscrete) groupn.getLastGivenBid().getValue(issueNumber-1);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		for(Entry<String, Double> e : valueWeights.entrySet()){
			if(e.getValue() > bestValueWeight){
				if (!previousValue.getValue().equals(e.getValue())) {
					bestValueWeight = e.getValue();
					bestValueKey = e.getKey();
				}
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
		HashMap<String,Double> issueWeightsOfOtherParties = new HashMap<String,Double>();
		
		//gets the sum of all weights of all parties for the issues
		Iterator<Party> partIt = parties.iterator();
		boolean isFirst = true;
		while(partIt.hasNext()){
			Iterator<IssueModel> itr = ((Party)partIt.next()).getIssueModels().iterator();
			if(isFirst == true){
				while(itr.hasNext()){
					IssueModel issue = (IssueModel)itr.next();
					issueWeightsOfOtherParties.put(issue.getName(), issue.getValue());
					isFirst = false; 
				}
			}
			if(isFirst == false){
				while(itr.hasNext()){
					IssueModel issue = (IssueModel)itr.next();
					issueWeightsOfOtherParties.put(issue.getName(), issueWeightsOfOtherParties.get(issue.getName()) + issue.getValue());
				}
			}
		}
		//get totalweight to normalize it
		Double totalWeight = 0.0;
		for(Entry<String,Double> e : issueWeightsOfOtherParties.entrySet()){
			totalWeight += e.getValue();
		}
		//generate other parties weights
		ArrayList<Double> otherWeights = new ArrayList<Double>();
		for(Entry<String,Double> e : issueWeightsOfOtherParties.entrySet()){
			Double temp = e.getValue();
			e.setValue(temp / totalWeight);
			otherWeights.add(temp / totalWeight);
		}
		//make other party weights the inverse
		otherWeights = getInverseValues(otherWeights);
		
		//get my weights
		ArrayList<Double> myWeights = new ArrayList<Double>();
		for(Entry<Objective,Evaluator> e : groupn.getUtilitySpace().getEvaluators()){
			myWeights.add(e.getValue().getWeight());
		}
		//make my party weights the inverse
		myWeights = getInverseValues(myWeights);
		
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
			issueNumber = i; 
			chosenIssuePercentage = finalWeights.get(i);
			if(tempWeight >= diceThrow){
				break;
			}
		}

		return (int)issueNumber + 1;
	}
	
	private ArrayList<Double> getInverseValues(ArrayList<Double> values){
		ArrayList<Double> returnValues = new ArrayList<Double>(); 
		Double sumOfDivision = 0.0;
		for(int i = 0; i<values.size();i++){
			sumOfDivision += 1/values.get(i);
		}
		for(int i = 0; i<values.size();i++){
			returnValues.add(values.get(i)/sumOfDivision);
		}
		return returnValues;
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
	 * Gets the best value for an issue, with all other issues set.
	 * @param condition			The pre-set issues
	 * @return	ValueDiscrete	The best value.
	 */
	// TODO Take into account the bids that have already been offered.
	public ValueDiscrete getValueBidOneOut(HashMap<Integer, Value> condition, int issueNr) {
		ValueDiscrete bestValue = null;
		double maxUtility = 0.0;
		UtilitySpace us = groupn.getUtilitySpace();
		ValueDiscrete previousValue = null;

		try {
			previousValue = (ValueDiscrete) groupn.getLastGivenBid().getValue(issueNr);
		} catch (Exception e1) {
			e1.printStackTrace();
		}		

		IssueDiscrete issue = (IssueDiscrete) us.getDomain().getIssues().get(issueNr-1);
		
		for (int j = 0; j < issue.getNumberOfValues(); j++) {
			if (!previousValue.equals(issue.getValue(j))) {
				HashMap<Integer, Value> values = (HashMap<Integer, Value>) condition.clone();
				values.put(issueNr, issue.getValue(j));
				
				try {
					Bid currentBid = new Bid(us.getDomain(), values);
					
					if (maxUtility < us.getUtility(currentBid)) {
						maxUtility = us.getUtility(currentBid);
						bestValue = issue.getValue(j);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}						
		return bestValue;
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
		
		if (parties.size() < groupn.getNumberOfParties()) {
			parties.clear();
			for (Entry<String, Party> e : groupn.getParties().entrySet()){
				parties.add(e.getValue());
			}
		}
		
		int issueNr = getWeightedRandomIssueIndex();
		Bid finalBid = null;
		double randomD = random.nextDouble();
		Bid lastGivenBid = groupn.getLastGivenBid();
		HashMap<Integer, Value> lastValues = lastGivenBid.getValues();
		
		
		// They win by chance
		if (chosenIssuePercentage >= randomD) {
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
			try {
				HashMap<Integer, Value> condition = (HashMap<Integer, Value>) lastValues.clone();
				condition.remove(issueNr);
				ValueDiscrete value = getValueBidOneOut(condition, issueNr);
				condition.put(issueNr, value);
				finalBid = new Bid(groupn.getUtilitySpace().getDomain(), condition);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return finalBid;
	}
	
	
}
