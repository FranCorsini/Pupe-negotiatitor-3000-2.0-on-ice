package negotiator.group4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import negotiator.Bid;
import negotiator.DeadlineType;
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
public class Group4 extends AbstractNegotiationParty {
	
	private Double currentUtility = 0.0;
	private Double threshold;
	private final Double RESERVATION_VALUE = 0.5;

	private final Double STARTING_THRESHOLD = 0.8;
	private int turns;
	private int round = 0;

	private Bid highestBid;
	private Bid lastGivenBid;
	private Bid lastReceivedBid;
	private HashMap<String, Party> parties = new HashMap<String, Party>();
	private ArrayList<List<ValueDiscrete>> values = new ArrayList<List<ValueDiscrete>>(); 
	private BidGenerator bidGenerator;
	
	private HashMap<Bid, Double> possibleBids = new HashMap<Bid, Double>();
	private UtilitySpace utilitySpace;
	

	
	/**
	 * Please keep this constructor. This is called by genius.
	 *
	 * @param utilitySpace Your utility space.
	 * @param deadlines The deadlines set for this negotiation.
	 * @param timeline Value counting from 0 (start) to 1 (end).
	 * @param randomSeed If you use any randomisation, use this seed for it.
	 */
	public Group4(UtilitySpace utilitySpace,
				  Map<DeadlineType, Object> deadlines,
				  Timeline timeline,
				  long randomSeed) {
		// Make sure that this constructor calls it's parent.
		super(utilitySpace, deadlines, timeline, randomSeed);
		
		this.utilitySpace = utilitySpace;
		for (Issue issue : utilitySpace.getDomain().getIssues()) {
			values.add(((IssueDiscrete) issue).getValues());	
		}
		
		//creates the generator
		generatePossibleBids(0, null);
		turns = (int)deadlines.get(DeadlineType.ROUND);
		bidGenerator = new BidGenerator(this, possibleBids, turns);//30 is placeholder for turns of deadline
	}

	private void generatePossibleBids(int n, HashMap<Integer, Value> bidValues){
        if(n >= values.size()){
        	Bid b = null;
        	
			try {
				b = new Bid(utilitySpace.getDomain(), bidValues);
				possibleBids.put(b, utilitySpace.getUtility(b));
			} catch (Exception e) {
				e.printStackTrace();
			}
            return;
        }
        for(Value v : values.get(n)){      	
        	HashMap<Integer, Value> currentBid;
        	
        	if (n == 0) {
        		currentBid = new HashMap<Integer, Value>();
        	}
        	else {
        		currentBid = (HashMap<Integer, Value>) bidValues.clone();
        	}
        	 	
        	currentBid.put(n+1, v);
            generatePossibleBids(n+1, currentBid);
            
        }
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
		round++;
		threshold = STARTING_THRESHOLD-(STARTING_THRESHOLD-RESERVATION_VALUE)*((double)round/(double)turns);
		
		if (!validActions.contains(Accept.class) || currentUtility<threshold) {
			Bid b = null;
			//if it's first turn, get out with best possible bid
			if(round == 1){
				b = bidGenerator.generateBestOverallBid();
			}
			
			//do something to get the bid as answer
			else{
				//it generates the best not used bid				
				do {
					b = bidGenerator.generateBestBid();
				}
				while (getUtility(b) < threshold);
			}
			
			setLastGivenBid(b);
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
		super.receiveMessage(sender, action);
				
		if (!parties.containsKey(sender.toString())){
			Party party = new Party(sender.toString(), utilitySpace.getDomain());
			parties.put(sender.toString(), party);
		}
		
		if (lastReceivedBid!=null){
		parties.get(sender.toString()).updateWithBid(lastReceivedBid,action);
		}
		
		if(action instanceof Offer){
			lastReceivedBid = Action.getBidFromAction(action);
			currentUtility = getUtility(lastReceivedBid);
			updateHighestBid(lastReceivedBid);
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
