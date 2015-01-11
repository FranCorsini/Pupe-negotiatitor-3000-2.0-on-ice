package negotiator.groupn;

import java.util.List;
import java.util.Map;

import negotiator.Bid;
import negotiator.DeadlineType;
import negotiator.Timeline;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.utility.UtilitySpace;

/**
 * This is your negotiation party.
 */
public class Groupn extends AbstractNegotiationParty {
	
	private Double currentUtility = 0.0;
	private Double threshold = 0.6;
	private Bid highestBid;
	private Bid lastGivenBid;
	private List<Groupn> parties;
	
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
			BidGenerator bidGenerator = new BidGenerator(this);
			//do something to get the bid as answer
			
			//placeholder to get answer
			Bid b=generateRandomBid();
			while (currentUtility<threshold) {
				b = generateRandomBid();
				currentUtility=getUtility(b);
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
		
		if(action instanceof Offer){
			lastGivenBid = Action.getBidFromAction(action);
		}
		
		
		Bid b = Action.getBidFromAction(action);
		currentUtility = getUtility(b);
		
		updateHighestBid(b);
		
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
	

}
