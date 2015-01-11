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
		//setInitialWeights();
	}
	
	private void createIssueModels(Domain domain){		
		for (Issue i : domain.getIssues()) {
			List<ValueDiscrete> values = ((IssueDiscrete) i).getValues();
			issueModels.add(new IssueModel(i.getName(), values));
		}
	}
	
//	private void setInitialWeights(){
//		int n = issueModels.size();name = tempName;
//		float averageweight = 1/n;
//		for (int i = 0; i < n; i++){
//			issueModels.get(i).setValue(averageweight);
//		}
//	}
	
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
