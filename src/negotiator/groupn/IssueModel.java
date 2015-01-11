package negotiator.groupn;

import negotiator.issue.ISSUETYPE;
import negotiator.issue.Issue;
import negotiator.issue.Value;
import negotiator.utility.EvaluatorDiscrete;

public class IssueModel extends EvaluatorDiscrete{

	private String name;
	private float value;
	
	public IssueModel(String tempName, int n){
		name = tempName;
	}
	
	public void setValue(float a){
		value = a;
	}
	
	public float getValue(){
		return value;
	}

}
