package hellobdd;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FocusRestriction {
	public String Name;
	public String Expression;
	public ArrayList<KeyValuePair> Rules;
	
	public FocusRestriction(String name, String expression){
		Name=name;
		Expression=expression;
		ParseExp();
	}
	
	public void ParseExp(){
		String[] parts = Expression.split(" ");
		
		KeyValuePair rule1 = GetKeyValue(parts[0]);
		KeyValuePair rule2 = GetKeyValue(parts[2]);

		Rules = new ArrayList<KeyValuePair>();
		Rules.add(rule1);
		Rules.add(rule2);
	}

	private KeyValuePair GetKeyValue(String p1) {
		String attr = p1.substring(0, p1.indexOf('.'));
		String equals = p1.substring(p1.indexOf('.')+1);
				
		Pattern p = Pattern.compile("equals\\(\\\"(.*?)\\\"\\)");
		Matcher m = p.matcher(equals);
		String value = null;
		while(m.find()) {
		   System.out.println(m.group(0));
		   value = m.group(1);
		}
		return new KeyValuePair(attr,value);
	}
}
