package negotiator.groupn;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import negotiator.Bid;
import negotiator.Domain;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.ValueDiscrete;

public class Party {

	private String name;
	private ArrayList<IssueModel> issueModels = new ArrayList<IssueModel>();
	private double rateOfChange = 0.1;
	private HashMap<IssueModel, List<Integer>> accepted = new HashMap<IssueModel, List<Integer>>();
	
	public Party(String name, Domain domain){
		this.name = name;
		createIssueModels(domain);
		setInitialWeights();
	}
	
	private void createIssueModels(Domain domain){		
		for (Issue issue : domain.getIssues()) {
			List<ValueDiscrete> values = ((IssueDiscrete) issue).getValues();
			IssueModel im = new IssueModel(issue.getName(), values);
			issueModels.add(im);
			accepted.put(im, new ArrayList<Integer>());
			for(int i=0; i<values.size(); i++){
				accepted.get(im).add(0);
			}
		}
		
	}
	
	private void setInitialWeights(){
		int n = issueModels.size();
		double averageWeight = 1/n;
		
		for (IssueModel issue : issueModels) {
			issue.setValue(averageWeight);
		}
	}
	
	
	public void updateWithBid(Bid bid,Action action){
		// Update utility
		for (IssueModel issue: issueModels){
			int i=0;
			for (Issue iss : bid.getIssues()){
				if (issue.getName()==iss.getName()){
					ValueDiscrete value = new ValueDiscrete();
					try {
						value = (ValueDiscrete) bid.getValue(i);
					} catch (Exception e) {
						// won't happen
					}
					if (action instanceof Accept){
						issue.updateUtility(value, rateOfChange);
						
						List<Integer> acc = accepted.get(issue);
						acc.set(i, acc.get(i)+1);
						accepted.put(issue, acc);
					} else if (action instanceof Offer){
						issue.updateUtility(value, -rateOfChange);
						Bid newBid = Action.getBidFromAction(action);
						updateWithBid(newBid, new Accept());
						
						List<Integer> acc = accepted.get(issue);
						acc.set(i, acc.get(i)-1);
						accepted.put(issue, acc);
					}
					break;
				}
				i++;
			}
		}
		
		//update weights
		int sumSpread=0;
		int spread=0;
		for (IssueModel issue: issueModels){
			int max=0;
			int min=(int)Double.POSITIVE_INFINITY;
			for(int p: accepted.get(issue)){
				max=Math.max(max, p);
				min=Math.min(min, p);
			}
			spread=max-min;
			sumSpread=sumSpread+spread;
		}
		for (IssueModel issue: issueModels){
			int max=0;
			int min=(int)1e20;
			for(int p: accepted.get(issue)){
				max=Math.max(max, p);
				min=Math.min(min, p);
			}
			spread=max-min;
			issue.setValue(spread/(sumSpread+1));
		}
	}
	
	public Double estimateUtility(Bid bid){
		double utility=0;
		
		for ( IssueModel issue: issueModels){
			double weight = issue.getValue();
			ValueDiscrete value = new ValueDiscrete();
			for (Issue iss : bid.getIssues()){
				if (issue.getName()==iss.getName()){
					int i = iss.getNumber();
					try {
						value = (ValueDiscrete) bid.getValue(i);
					} catch (Exception e) {
						// TODO Auto-generated catch block\
					}
					break;
				}
			}
			double util = issue.getUtility(value);
			utility = utility + util*weight;
		}
		
		return utility;
	}
	
	
}
