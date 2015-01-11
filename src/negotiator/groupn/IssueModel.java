package negotiator.groupn;

import negotiator.issue.ISSUETYPE;
import negotiator.issue.Issue;
import negotiator.issue.Value;

public class IssueModel extends Issue{

	private String name;
	private float value;
	
	public IssueModel(String tempName, int n){
		super(tempName,n);
		name = tempName;
		
	}
	
	public void setValue(float a){
		value = a;
	}
	
	public float getValue(){
		return value;
	}

	@Override
	public boolean checkInRange(Value value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String convertToString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISSUETYPE getType() {
		// TODO Auto-generated method stub
		return null;
	}
}
