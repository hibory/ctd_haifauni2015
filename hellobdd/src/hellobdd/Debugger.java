package hellobdd;

public class Debugger{
    public static boolean isEnabled(){
        return true;
    }

    public static void log(Object o){
    	if(isEnabled())
    		System.out.println(o.toString());
    }

	public static void logSameLine(String o) {
		if(isEnabled())
    		System.out.print(o.toString());
	}
}
