package negotiator.groupn;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import negotiator.Bid;
import negotiator.actions.Action;
import negotiator.issue.Objective;
import negotiator.utility.Evaluator;
import negotiator.utility.UtilitySpace;

public class Party {

	private String name;
	private List<IssueModel> issueModels;
	
	public Party(String name,UtilitySpace utility){
		this.name = name;
		createIssueModels(utility);
		setInitialWeights();
	}
	
	private void createIssueModels(UtilitySpace utility){
		int i = 0;
		for( Entry<Objective, Evaluator> e : utility.getEvaluators()) {
			String IssueModelName = e.getKey().getName().toString();
			IssueModel iss = new IssueModel(IssueModelName,i);
			issueModels.add(iss);
			
			//for ( ){
			//need to populate the discrete values here
			//}
			i++;
		}
	}
	
	private void setInitialWeights(){
		int n = issueModels.size();
		float averageweight = 1/n;
		for (int i = 0; i < n; i++){
			issueModels.get(i).setValue(averageweight);
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
