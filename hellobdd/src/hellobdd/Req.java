package hellobdd;

import java.util.ArrayList;
import java.util.List;

import net.sf.javabdd.BDD;

public class Req implements Comparable<Req> {	
	public BDD Bdd;
	public BDD uncov;
	public List<String> IgnoreVars;
	
	public Req(BDD b){
		Bdd = b;
	}
	
	public Req(BDD b,List<String> ignoreVars){
		Bdd = b;
		IgnoreVars = ignoreVars;
	}

	@Override
	public int compareTo(Req another) {
		if (this.uncov.pathCount() < another.uncov.pathCount()){
            return -1;
        }else{
            return 1;
        }
	}
}
