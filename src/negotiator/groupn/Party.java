package negotiator.groupn;
import java.util.ArrayList;
import java.util.List;

import negotiator.Bid;
import negotiator.Domain;
import negotiator.actions.Action;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.ValueDiscrete;

public class Party {

	private String name;
	private ArrayList<IssueModel> issueModels = new ArrayList<IssueModel>();
	
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
		//TODO update somehow the expected weighs
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
