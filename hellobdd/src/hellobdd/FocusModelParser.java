package hellobdd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FocusModelParser {
	public FocusModelParser(String xml){
		
		readXML(xml);
	}
	
	Map<String, FocusAttribute> attributes;
	ArrayList<FocusRestriction> rests;
	
	BDD Valid;
	List<Req> Requirements;
	
   
   // read data from xml
   private void readXML(String xml) {
	   Document document = ReadXmlDoc(xml);
		   
	   attributes = GetAllAttributes(document);
	   rests = GetAllRestriction(document); 
	   
	   //test that it outputs correctly:
	   for(FocusAttribute attr : attributes.values()){
		   System.out.println(attr.Name+":");
		   for(String v : attr.Values){
			   System.out.println(v+":");
		   }
		   System.out.println("=====");
	   }
	   
	   for(FocusRestriction rest : rests){
		   System.out.println(rest.Name+":");
		   System.out.println(rest.Expression+":");
		   
		   System.out.println("=====");
	   }
	   
	   
	   int m = 1;
	   int m1 = m-1;
   }


   // read attributes from xml
   private ArrayList<FocusRestriction> GetAllRestriction(Document document) {
	   NodeList nl = document.getElementsByTagName("restriction");
	   ArrayList<FocusRestriction> rests = new ArrayList<FocusRestriction>();
	   
	   //loop through rests
	   for (int i = 0; i < nl.getLength(); i++) {
		   
		   Element attrEl =  (Element)nl.item(i);
           String name = attrEl.getAttribute("name");
           String expression = attrEl.getAttribute("expression");
           
           rests.add(new FocusRestriction(name,expression));
	   }
	   
	   return rests;
   }

   //Map<String, String> map = new HashMap<String, String>();
   // read restrictions from xml
   private Map<String, FocusAttribute> GetAllAttributes(Document document) {
	   NodeList nl = document.getElementsByTagName("attribute");
	   Map<String, FocusAttribute> attributes = new HashMap<String, FocusAttribute>();
	   
	   //loop through attributes
	   for (int i = 0; i < nl.getLength(); i++) {
		   Element attrEl =  (Element)nl.item(i);
           String name = attrEl.getAttribute("name");
           
           FocusAttribute attr = new FocusAttribute(name);
           
           //loop through values
           NodeList values = attrEl.getElementsByTagName("value"); 
           for (int j = 0; j < values.getLength(); j++) {
        	   Element valueEl = (Element)values.item(j);
        	   String valueName = valueEl.getAttribute("name");
        	   
        	   attr.Values.add(valueName);
           }
           
           attributes.put(attr.Name, attr);
	   }
	   return attributes;
	}

   private Document ReadXmlDoc(String xml) {
		File file = new File(xml);
		   DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
		           .newInstance();
		   DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   Document document = null;
		try {
			document = documentBuilder.parse(file);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return document;
	}



   public void FillBdd() {
	   
	   int varCount = GetVarCount();
	   BDDFactory bddFactory = myJavaBdd.GetBddFactory(varCount);

	   //for each attribute attach it's BDD variables
	   int i=0;
	   for(FocusAttribute attr : attributes.values()){
		   int count = attr.VarCount();
		   ArrayList<BDD> vars = new ArrayList<BDD>(); 
		   for(int j=0; j<count; j++){
			   BDD v = bddFactory.ithVar(i+j);
			   vars.add(v);
		   }
		   attr.Variables = vars;
		   attr.BddFactory = bddFactory;
		   i += count;
	   }
	   
	   //assign all requirements & valid-space
	   Valid = bddFactory.one();
	   Requirements = new ArrayList<Req>();
	   for(FocusRestriction rest : rests){
		   
		   BDD rulesBdd = bddFactory.one();
		   for(KeyValuePair rule : rest.Rules){
			   FocusAttribute attr = attributes.get(rule.Key);
			   BDD b = attr.GetBdd(rule.Value);
			   rulesBdd = rulesBdd.and(b);
			   
			   Print("b",b);
			   Print("rulesBdd",rulesBdd);
			   attr.PrintVariables();
		   }		   
		   
		   Valid = Valid.and(rulesBdd.not());
		   Print("Valid",Valid);
		   
		   Req req = new Req(rulesBdd.not()); //todo: use ignoreVars
		   Requirements.add(req);
	   }
	   
	   
   }
   
   private void Print(String s, BDD b){
	   myJavaBdd.PrintAsDot(s, b);
   }
   
	private int GetVarCount() {
		int count = 0;
		for(FocusAttribute attr : attributes.values()){
			count += attr.VarCount();
		}
		return count;
	}

}
