package negotiator.groupn;

import java.util.List;

import negotiator.issue.ValueDiscrete;

public class IssueModel{

	private List<ValueDiscrete> values;
	private double value;
	private String name;
	
	public IssueModel(String name, List<ValueDiscrete> values){
		this.name = name;
		this.values = values;
	}
	
	public void setValue(double a){
		value = a;
	}
	
	public double getValue(){
		return value;
	}

}
