package negotiator.groupn;

import java.util.HashMap;
import java.util.List;

import negotiator.issue.ValueDiscrete;

public class IssueModel{

	private List<ValueDiscrete> values;
	private HashMap<ValueDiscrete, Double> utility;
	private double value;
	private String name;
	
	public IssueModel(String name, List<ValueDiscrete> values){
		this.setName(name);
		this.values = values;
		
		this.utility = new HashMap<ValueDiscrete, Double>();
		for (ValueDiscrete option: values) {
			utility.put(option, 1.0);
		}
	}
	
	public double getUtility(ValueDiscrete option){
		return utility.get(option);
	}
	
	public void updateUtility(ValueDiscrete option, double change){
		utility.put(option, utility.get(option) + change);
		//normalisation
		double max=0;
		for(ValueDiscrete o: values){
			max=Math.max(max, utility.get(o));
		}
		for(ValueDiscrete o: values){
			utility.put(o, Math.max(0, utility.get(o))/max);
		}
	}
	
	public void setValue(double a){
		value = a;
	}
	
	public double getValue(){
		return value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
