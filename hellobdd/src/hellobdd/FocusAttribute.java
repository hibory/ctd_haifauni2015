package hellobdd;

import java.util.ArrayList;
import java.util.Collections;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;

public class FocusAttribute {
	public String Name;
	public ArrayList<String> Values;
	public ArrayList<BDD> Variables;
	public BDDFactory BddFactory;
	
	public FocusAttribute(String name){
		Name = name;
		Values = new ArrayList<String>();
	}
	
	public int VarCount(){
		double u = log(Values.size(),2);
		return (int) Math.ceil(u);
	}
	
	public void CovertToBinary(){
		
		for(int i=0; i<Values.size(); i++){
			
			String binary = Integer.toBinaryString(i);
			
			int mm = 33;
		}
	
	}
	
	static double log(int x, int base)
	{
	    return (double) (Math.log(x) / Math.log(base));
	}

	public BDD GetBdd(String value) {
		
		int index = Values.indexOf(value);
		String binary = Integer.toBinaryString(index);
		
		Debugger.log("Binary of " + Name + "is:" + binary);
		
		int j=0;
		BDD bdd = BddFactory.one();
		
		//assign true/false values to variables 
		//according to binary value
		for (int i = (binary.length()-1); i >= 0; i--) {
		    char character = binary.charAt(i); 
		    
		    BDD v = Variables.get(j);
		    if(character  == '1'){
		    	bdd = bdd.and(v);
		    }
		    else
		    {
		    	bdd = bdd.and(v.not());
		    }
		    j++;
		}
		
		//assign remaining variables to false
		for(int i=j; i<Variables.size(); i++){
			BDD v = Variables.get(j);
			bdd = bdd.and(v.not());
		}
		
		return bdd;
	}

	public void PrintVariables() {
		Debugger.log("Variables of " + Name + "are:");
		for(BDD b : Variables){
			Debugger.logSameLine("," + b.id() );
		}
		
	}
	
}
