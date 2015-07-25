package hellobdd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.w3c.dom.*;

public class myJavaBdd {

	private static BDDFactory nextFactory(){
		BDDFactory f = BDDFactory.init(1000, 1000);
        f.reset();
        return f;
	}
	
	public static BDDFactory GetBddFactory(int n){
		BDDFactory bdd = nextFactory();
		if (bdd.varNum() < n) bdd.setVarNum(n);
		
		int[] nodes = new int[n];
		for(int i=0; i<n; i++)
			nodes[i] = i;
		
		bdd.setVarOrder(nodes);
		
		return bdd;
	}
	
	int sizeThresld = 100;
	int NumOfNodesSimple = 4;
	int NumOfNodesPaypal = 8;
	List<String> joinVars;
	BDDFactory bdd;
	
	public void Test(){
		// TODO Auto-generated method stub
		System.out.println("Hello, World");
		
	}
	
	public void run(){
		
		bdd = GetBddFactory(NumOfNodesSimple);
		
		BDD d = GetVar('d');
		BDD a = GetVar('a');
		BDD b = GetVar('b');
		BDD c = GetVar('c');
		
		BDD e1 =   (  d.not().and(a).and(a))
				.or(  d.and(a).and(b) )
				.or(  d.not().and(a).and(b.not())  )
				.or(  d.and(a.not()).and(b) );
		
		
		List<Req> r1 = new ArrayList<Req>();
		r1.add(new Req(a.and(b.not())));
		r1.add(new Req(a.and(b)));			
		
		BDD e2 = ( b.and(c.not()) )
			  .or( b.not().and(c.not()) );
				
		List<Req> r2 = new ArrayList<Req>();
		r2.add(new Req(b.and(c.not())));

		joinVars = new ArrayList<String>();
		joinVars.add("b");
		
		Computation(e1,r1,e2,r2);
	}
	
	public void runPaypal(){
		
		bdd = GetBddFactory(NumOfNodesPaypal);
		
		BDD x1 = GetVar('1');
		BDD x2 = GetVar('2');
		BDD x3 = GetVar('3');
		BDD x4 = GetVar('4');
		BDD x5 = GetVar('5');
		BDD x6 = GetVar('6');
		BDD x7 = GetVar('7');
		BDD x8 = GetVar('8');
		
		BDD e1 =   (  x1.not().and(x2.not()).and(x3.not()).and(x4.not()).and(x5.not()).and(x6.not())   )
				.or(  x1.not().and(x2.not()).and(x3.not()).and(x4).and(x5.not()).and(x6.not()) )
				.or(  x1.not().and(x2).and(x3.not()).and(x4).and(x5.not()).and(x6.not())  )
				.or(  x1.not().and(x2).and(x3).and(x4.not()).and(x5.not()).and(x6) )
				.or(  x1.not().and(x2).and(x3.not()).and(x4).and(x5).and(x6.not()) )
				.or(  x1.not().and(x2).and(x3).and(x4.not()).and(x5).and(x6.not()) );
		
		BDD e2 =   (  x5.not().and(x6.not()).and(x7.not()).and(x8.not()))
				.or(  x5.not().and(x6.not()).and(x7).and(x8) )
				.or(  x5.not().and(x6).and(x7.not()).and(x8)  )
				.or(  x5.not().and(x6).and(x7).and(x8) )
				.or(  x5.and(x6.not()).and(x7).and(x8))
				.or(  x5.and(x6.not()).and(x7.not()).and(x8.not()) );

		List<String> ignoreVars1 = new ArrayList<String>();
		ignoreVars1.add("7");
		ignoreVars1.add("8");
		List<Req> r1 = new ArrayList<Req>();
		r1.add(new Req( x1.not().and(x2.not()).and(x3.not()).and(x4.not()) , ignoreVars1 ));
		r1.add(new Req( x1.not().and(x2.not()).and(x5.not()).and(x6.not() ), ignoreVars1 ));
		r1.add(new Req( x3.not().and(x4).and(x5).and(x6.not()) , ignoreVars1 ));
		
		List<String> ignoreVars2 = new ArrayList<String>();
		ignoreVars2.add("1");
		ignoreVars2.add("2");
		ignoreVars2.add("3");
		ignoreVars2.add("4");
		List<Req> r2 = new ArrayList<Req>();
		r2.add(new Req( x5.and(x6.not()).and(x7.not()).and(x8.not()), ignoreVars2 ));
		r2.add(new Req( x5.not().and(x6.not()).and(x7).and(x8), ignoreVars2 ));
		
		joinVars = new ArrayList<String>();
		joinVars.add("5");
		joinVars.add("6");
		
		Computation(e1,r1,e2,r2);
	}
	
	public void Computation(BDD e1, List<Req> r1, BDD e2, List<Req> r2){
		
		BDD T1 = bdd.zero(),T2 = bdd.zero() ,T21 = bdd.zero();
		
		//Init: for t in R1 do uncov1(t) = Projt(E1)
		for(Req t : r1){
			t.uncov = e1.and(t.Bdd);
			PrintAsDot("uncov",t.uncov);
		}
		
		//Init: for t in R2 do uncov2(t) = Projt(E2)
		for(Req t : r2){
			t.uncov = e2.and(t.Bdd);
		}
		
		while(!r1.isEmpty() || !r2.isEmpty()){
			
			if(!r1.isEmpty()){
				
				BDD chosen = ChosenTest(r1,e1);
				T1 = T1.or(chosen);		
				
				//inner join
				
				BDD bOnly = GetVarValue(joinVars,chosen);
				BDD chosenAc = (chosen.restrict(bOnly))
						       .and(e2.restrict(bOnly));
				
				PrintAsDot("ChosenAC:",chosenAc);
				T21 = T21.or(chosenAc);
				
				CleanUncov(r1,chosen);
			}
			
			if(!r2.isEmpty()){
				
				BDD chosen = ChosenTest(r2,e2);
				T2 = T2.or(chosen);		
				
				BDD bOnly = GetVarValue(joinVars,chosen);
				
				//inner join
				BDD chosenAc = (chosen.restrict(bOnly))
						       .and(e1.restrict(bOnly));
				
				PrintAsDot("ChosenAC:",chosenAc);
				
				//TODO:how to eliminate the b part from chosenAC
				T21 = T21.or(chosenAc); //is this OR or UNION ? 
				
				CleanUncov(r2,chosen);
			}
		}
		
		PrintAsDot("T1",T1);
		PrintAsDot("T2",T2);
		PrintAsDot("T21",T21);
	}
	
	private BDD GetVarValue(List<String> joinVars, BDD chosen) {
		// decide which value b should get
		BDD result = bdd.one();
		
		for(String var : joinVars){
			BDD varOnly = GetVar(var.toCharArray()[0]);
			if(chosen.restrict(varOnly).isZero()) 
				result = result.and(varOnly.not());
			else
				result = result.and(varOnly);
		}
		
		return result;
	}

	private BDD GetVar(char c) {
		int i=0;
		switch(c){
			case 'd': i=0; break;
			case 'a': i=1; break;
			case 'b': i=2; break;
			case 'c': i=3; break;
			
			case '1': i=0; break;
			case '2': i=1; break;
			case '3': i=2; break;
			case '4': i=3; break;
			case '5': i=4; break;
			case '6': i=5; break;
			case '7': i=6; break;
			case '8': i=7; break;
			
			default: i=-1; break;
		}
		
		return bdd.ithVar(i);
	}

	private BDD ChosenTest(List<Req> rin, BDD e) {
		
		List<Req> r = new ArrayList<Req>(rin);
		
		BDD collected = e;
		Collections.sort(r); // Sort R in decreasing order of sat(uncov(t))
		Collections.reverse(r);
		
		boolean interrupted = false;
		for(Req t : r){ 

			PrintAsDot("t.bdd",t.Bdd);
			PrintAsDot("t.uncov",t.uncov);
			PrintAsDot("collected ^ unvoct",collected.and(t.uncov));
			
			if(collected.and(t.uncov).pathCount()>0) //if (Collected ^ uncov(t)) != FALSE then
				collected = collected.and(t.uncov);  //  Collected <- Collected ^ uncov(t)
			
			if(collected.nodeCount()>sizeThresld){ 
				interrupted = true;
				break;                              
			}
		}
		
		BDD chosen;
		int n = (int) collected.pathCount();
		BDD[] candti = new BDD[n];
		int[] newCov = new int[n];
		
		if(interrupted){
			for(int i=0; i<n; i++){
				candti[i] =collected.fullSatOne(); //candti <-randSat(Collected)
				newCov[i] = newlyCovered(candti[i],r); //newCovi <-newlyCovered(candidatei,R)
			}
			int maxValueIndex = GetMaxValueIndex(newCov);
			chosen = candti[maxValueIndex]; //chosen <- {candti|ForEach j:newCovi >=newCcovj}
		}
		else{
			chosen = collected.fullSatOne(); //chosen <-randSat(Collected)
			BDD chosen1 = collected.satOne();
			PrintAsDot("chosen1",chosen1);
			PrintAsDot("chosen",chosen);
			PrintAsDot("collected",collected);
		}
		
		PrintAsDot("chosen before",chosen);
		List<String> ignoreVars = rin.get(0).IgnoreVars;
		BDD cOnly = GetVarValue(ignoreVars,chosen);
		chosen.restrictWith(cOnly);
		
		PrintAsDot("chosen after",chosen);
		return chosen;
	}

	//todo: implement this method
	private int newlyCovered(BDD bdd, List<Req> r) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	private int GetMaxValueIndex(int[] array) {
		if(array.length<1) return -1;
		
		int largest = array[0], index = 0;
		for (int i = 1; i < array.length; i++) {
		  if ( array[i] >= largest ) {
		      largest = array[i];
		      index = i;
		   }
		}
		return index;
	}

	public void CleanUncov(List<Req> r,BDD chosen){
		PrintAsDot("chosen",chosen);
		PrintAsDot("not chosen",chosen.not());
		List<Req> ToBeRemoved = new ArrayList<Req>();
		for(Req t : r){
			
			BDD remaining = t.uncov.and(chosen.not());
			PrintAsDot("t.uncov",t.uncov);
			PrintAsDot("t.uncov ^ chosen.not",remaining);
			
			t.uncov = remaining;
			if(t.uncov.pathCount()==0){
				ToBeRemoved.add(t);
			}
		}
		
		for(Req t : ToBeRemoved){
			r.remove(t);
		}
	}
	
	public static void PrintAsDot(String s, BDD b){
		Debugger.log("////////////////////////");
		Debugger.log(s+":");
		b.printDot();
		Debugger.log("***********************");
	}

	public void RunParser(String model1, String model2) {

		FocusModelParser parser1 = new FocusModelParser(model1);
		parser1.FillBdd();
	}

	public void TestDifferentBdd() {
		BDDFactory bddFactory1 = myJavaBdd.GetBddFactory(5);
		//BDDFactory bddFactory2 = myJavaBdd.GetBddFactory(10);
		
		BDD x1 = bddFactory1.ithVar(1);
		BDD x2 = bddFactory1.ithVar(2);
		BDD x4 = bddFactory1.ithVar(4);
		
		BDD res = x1.and(x2).and(x4);
		
		try {
			bddFactory1.save("C:\\temp\\bdd.txt", res);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		res.printDot();
		return;
	}

}