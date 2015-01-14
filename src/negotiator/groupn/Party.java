package negotiator.groupn;
import java.util.ArrayList;
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
	
	public Party(String name, Domain domain){
		this.name = name;
		createIssueModels(domain);
		setInitialWeights();
	}
	
	private void createIssueModels(Domain domain){		
		for (Issue issue : domain.getIssues()) {
			List<ValueDiscrete> values = ((IssueDiscrete) issue).getValues();
			issueModels.add(new IssueModel(issue.getName(), values));
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
					} else if (action instanceof Offer){
						issue.updateUtility(value, -rateOfChange);
						Bid newBid = Action.getBidFromAction(action);
						updateWithBid(newBid, new Accept());
					}
					break;
				}
				i++;
			}
			
			
			
		}
	}
	
	public Double estimateUtility(Bid b){
		
		for ( int i = 0; i< issueModels.size(); i++){
			//b.getIssues().get(i).
			//TODO
		}
		Double d = null;
		
		return d;
	}
	
	
}
