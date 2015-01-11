package negotiator.groupn;

import java.util.List;

import negotiator.issue.ValueDiscrete;

public class IssueModel{

	private List<ValueDiscrete> values;
	private float value;
	private String name;
	
	public IssueModel(String name, List<ValueDiscrete> values){
		this.name = name;
		this.values = values;
	}
	
	public void setValue(float a){
		value = a;
	}
	
	public float getValue(){
		return value;
	}

}
