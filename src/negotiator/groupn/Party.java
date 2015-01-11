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
	private List<Issue> issues;
	
	public Party(String name,UtilitySpace utility){
		this.name = name;
		//create issues
		createIssues(utility);
		setInitialWeights();
	}
	
	private void createIssues(UtilitySpace utility){
		for( Entry<Objective, Evaluator> e : utility.getEvaluators()) {
			String issueName = e.getKey().getName().toString();
			Issue iss = new Issue(issueName);
			issues.add(iss);
		}
	}
	
	private void setInitialWeights(){
		int n = issues.size();
		float averageweight = 1/n;
		for (int i = 0; i < n; i++){
			issues.get(i).setValue(averageweight);
		}
	}
	
	public void updateWithBid(Bid bid,Action action){
		//TODO update somehow the expected weighs
	}
	
	public Double estimateUtility(Bid b){
		Double d = null;
		
		return d;
	}
	
	
}
