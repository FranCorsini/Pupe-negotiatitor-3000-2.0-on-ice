package negotiator.groupn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import negotiator.Bid;
import negotiator.DeadlineType;
import negotiator.Domain;
import negotiator.Timeline;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.utility.UtilitySpace;

/**
 * This is your negotiation party.
 */
public class Groupn extends AbstractNegotiationParty {
	
	private Double currentUtility = 0.0;
	private Double threshold = 0.75;

	private Bid highestBid;
	private Bid lastGivenBid;
	private HashMap<String, Party> parties = new HashMap<String, Party>();
	private HashMap<Bid, Double> possibleBids = new HashMap<Bid, Double>();
	private ArrayList<List<ValueDiscrete>> values = new ArrayList<List<ValueDiscrete>>(); 
	private BidGenerator bidGenerator;
	private boolean isFirstturn;
	

	
	/**
	 * Please keep this constructor. This is called by genius.
	 *
	 * @param utilitySpace Your utility space.
	 * @param deadlines The deadlines set for this negotiation.
	 * @param timeline Value counting from 0 (start) to 1 (end).
	 * @param randomSeed If you use any randomisation, use this seed for it.
	 */
	public Groupn(UtilitySpace utilitySpace,
				  Map<DeadlineType, Object> deadlines,
				  Timeline timeline,
				  long randomSeed) {
		// Make sure that this constructor calls it's parent.
		super(utilitySpace, deadlines, timeline, randomSeed);
		
		//it's the first turn
		isFirstturn = true;
		
		
		for (Issue issue : utilitySpace.getDomain().getIssues()) {
			values.add(((IssueDiscrete) issue).getValues());	
		}
		possibleBids = generatePossibleBids(utilitySpace.getDomain());
		
		//creates the generator
		bidGenerator = new BidGenerator(this, possibleBids);
	}
	
	
	
	private HashMap<Bid, Double> generatePossibleBids(Domain domain) {
		HashMap<Bid, Double> bids = new HashMap<Bid, Double>();
		ArrayList<Issue> issues = domain.getIssues();
		for (int i = 0; i < issues.size(); i++) {
		}
		return bids;
	}
	

	/**
	 * Each round this method gets called and ask you to accept or offer. The first party in
	 * the first round is a bit different, it can only propose an offer.
	 *
	 * @param validActions Either a list containing both accept and offer or only offer.
	 * @return The chosen action.
	 */
	@Override
	public Action chooseAction(List<Class> validActions) {

		// with 50% chance, counter offer
		// if we are the first party, also offer.
		if (!validActions.contains(Accept.class) || currentUtility<threshold) {
			Bid b = null;
			//if it's first turn, get out with best possible bid
			if(isFirstturn == true){
				b= bidGenerator.generateBestOverallBid();
				isFirstturn = false;
			}
			
			//do something to get the bid as answer
			else{
				//it generates the best not used bid
				b = bidGenerator.generateBestNotAlredyUsedBid();
			}
			
			return new Offer(b);
					
			
		}
		else {
			return new Accept();
		}
	}



	/**
	 * All offers proposed by the other parties will be received as a message.
	 * You can use this information to your advantage, for example to predict their utility.
	 *
	 * @param sender The party that did the action.
	 * @param action The action that party did.
	 */
	@Override
	public void receiveMessage(Object sender, Action action) {
		
		//if you recieve an offer it means you are not the first to play
		if (isFirstturn == true){
			isFirstturn = false;
		}
		
		if (!parties.containsKey(sender.toString())){
			Party party = new Party(sender.toString(), utilitySpace.getDomain());
			parties.put(sender.toString(), party);
		}
		
		if(action instanceof Offer){
			lastGivenBid = Action.getBidFromAction(action);
			currentUtility = getUtility(lastGivenBid);
			updateHighestBid(lastGivenBid);
		}
		else if (action instanceof Accept) {
			
		}
		
		
	}

	private void updateHighestBid(Bid b){
		//TODO it check if it is the highest and in case update it
	}
	
	
	
	//*******GETTER AND SETTER********
	
	public Double getCurrentUtility() {
		return currentUtility;
	}

	public void setCurrentUtility(Double currentUtility) {
		this.currentUtility = currentUtility;
	}

	public Double getThreshold() {
		return threshold;
	}

	public void setThreshold(Double threshold) {
		this.threshold = threshold;
	}
	
	public Bid getHighestBid() {
		return highestBid;
	}

	public void setHighestBid(Bid highestBid) {
		this.highestBid = highestBid;
	}

	public Bid getLastGivenBid() {
		return lastGivenBid;
	}

	public void setLastGivenBid(Bid lastGivenBid) {
		this.lastGivenBid = lastGivenBid;
	}

	public HashMap<String, Party> getParties() {
		return parties;
	}

	public void setParties(HashMap<String, Party> parties) {
		this.parties = parties;
	}
	

}
