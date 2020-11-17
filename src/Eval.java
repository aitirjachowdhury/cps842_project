import java.io.*;
import java.util.*;

public class Eval {
    static HashMap<Integer, ArrayList<Integer>> qrel = new HashMap<Integer, ArrayList<Integer>>();
    static LinkedHashMap<Integer, String> queries = new LinkedHashMap<Integer, String>();
    static LinkedHashMap<Integer, Double> topK = new LinkedHashMap<Integer, Double>();
    static ArrayList<String> stopwordList = new ArrayList<String>();
    static ArrayList<Double> aPrecision = new ArrayList<Double>();
    static ArrayList<Double> rPrecision = new ArrayList<Double>();
    static Scanner scan = new Scanner(System.in);
    static Search searching = new Search();
    static String stop, stem;
    static Scanner docScan;

	public static void main(String[] args) {        
        //Turn on or off stopword filter
        System.out.println("Query stopword removal: on / off");
        //Get user input
        stop = scan.nextLine();
        //Check if user input is valid
        while(!stop.equals("on")){
            if(!stop.equals("off"))
            {
                System.out.println("Invalid stopword input. Try again");
                stop = scan.nextLine();
            }
            else{break;}}
        //Turn on or off stemming filter
        System.out.println("Query stemming: on / off");
        //Get user input
        stem = scan.nextLine();
        //Check if user input is valid
        while(!stem.equals("on")){
            if(!stem.equals("off"))
            {
                System.out.println("Invalid stemming input. Try again");
                stem = scan.nextLine();
            }
            else{break;}}

        if(stop.equals("on"))
        {
            try {
                File docs = new File(".\\src\\common_words");
                docScan = new Scanner(docs);
                while(docScan.hasNext()){
                    String insert = docScan.next();
                    stopwordList.add(insert);}
                docScan.close();}
            catch(FileNotFoundException e) {
                System.out.println("File not found");}
        }
        //Scan qrels.text to find relevant documents based on user perspective
        try {
            File docs = new File(".\\src\\qrels.text");
            docScan = new Scanner(docs);
        	int keyword = docScan.nextInt();
        	
            while(docScan.hasNext()){
            	if(qrel.containsKey(keyword)) {
            		int num = docScan.nextInt();
            		qrel.get(keyword).add(num);
            		docScan.next();
            		docScan.next();}
            	else {
            		qrel.put(keyword, new ArrayList<Integer>());
            		int num = docScan.nextInt();
            		qrel.get(keyword).add(num);
            		docScan.next();
            		docScan.next();}   
            	
            	if(docScan.hasNext()) {keyword = docScan.nextInt();}
	        }}
        catch(FileNotFoundException e) {System.out.println("File not found");}
        //Scan query.text and store them in query LinkedHashMap
        try {
            File docs = new File(".\\src\\query.text");
            docScan = new Scanner(docs);

            String keyword = docScan.next();
            String query = "";
            int id = 0;
            while(docScan.hasNext()){
            	if(keyword.equals(".I")) {            		
            		id = Integer.parseInt(docScan.next());
    	            if(id == 0) {break;} 
            		query = "";
            		docScan.next();
            		keyword = docScan.next();}
            	
            	if(qrel.containsKey(id)) {
	            while(!keyword.equals(".A") && !keyword.equals(".N")) {
	            	query += keyword + " ";
	            	keyword = docScan.next();}
	            query = query.substring(0,query.length()-1);
	            if(stop.equals("on")) {query = removeStopword(query);}
		        if(stem.equals("on"))
		        {
		            String[] stemming = query.split(" ");
		            query = "";
		            for(int i = 0; i < stemming.length; i++)
		            {
		            	query += stem(stemming[i]);
		            	query += " ";
		            }
		        	query = query.substring(0, query.length()-1);
		        }
	            queries.put(id, query);
            	}
	            while(!keyword.equals(".I")) {keyword = docScan.next();}}}
        catch(FileNotFoundException e) {System.out.println("File not found");}
        
        scan.close(); 
        docScan.close();
        findRelDocs();
	}
    //Find the relevant documents and calculate the AP and R-Precision for each query
	public static void findRelDocs() {	
        for (Map.Entry<Integer, String> query : queries.entrySet()) 
        {
    		double ap = 0;
    		double rp = 0;		
    		double found = 0; // Calculate how many relevant document is retrieved
    		double counter = 0;	//Calculate how many documents have been passed
    		double relDocSize = 0; //Calculate total relevant document according to user
    		
    		relDocSize = qrel.get(query.getKey()).size(); 	    		
    		searching.setQuery(query.getValue());
	        topK = searching.calculateSim();
	            
	            for (Map.Entry<Integer, Double> retrieved : topK.entrySet()) 
	            {
	            	counter++;
	            	//If it is relevant according to user, 
	            	//Increment found and add precision value
	            	if(qrel.get(query.getKey()).contains(retrieved.getKey())) {
	            		found++;
	            		double apVal = found/counter;
	            		ap += apVal;}
	            	//Find the total relevant documents in retrieved
	            	//Based on the size of user relevant documents
	            	if(counter == relDocSize) {
	            		rp = found/relDocSize;
	            		rPrecision.add(rp);}
	            }
	            ap = ap/relDocSize;
	            aPrecision.add(ap);
	            
	        	System.out.printf("A-Precision of query %d = %.3f\n", query.getKey(), ap);
	        	System.out.printf("R-Precision of query %d = %.3f\n", query.getKey(), rp);
	        	System.out.println("--------------------------------");
        } 
		        
        double map = 0.0;
        double arp = 0.0;
        
        if(aPrecision.size() == rPrecision.size()) {
	        //Loop through the array and sum the AP and R-Precision
	        for(int i = 0; i < aPrecision.size(); i++) 
	        {
	        	map += aPrecision.get(i);
	        	arp += rPrecision.get(i);     
	        }
        }
        else
        {
	        //Loop through the array and sum the AP
	        for(int i = 0; i < aPrecision.size(); i++) 
	        {
	        	map += aPrecision.get(i);
	        }
	        //Loop through the array and sum the R-Precision
	        for(int i = 0; i < rPrecision.size(); i++) 
	        {
	        	arp += rPrecision.get(i);     
	        }        		
        }
                
        map = map / queries.size();
        arp = arp/queries.size();

        System.out.printf("MAP over all values = %.3f\n", map);
        System.out.printf("ARP over all values = %.3f", arp);      
	}
	//Method to remove stopword
	public static String removeStopword(String input) {
		String[] output = input.split(" ");
		String result = "";
		
		for(int i = 0; i < output.length; i++) {
			String check = output[i];
	        check = check.toLowerCase();
	        check = check.replaceAll("[^-a-z]", "");
			
	        if(check.contains("-"))
	        {
	        	String[] filter = check.split("-");
	        	check = "";
	        	for(int j = 0; j < filter.length; j++) {
	    			if(!stopwordList.contains(filter[j]))
	    			{
	    				result += filter[j];
	    				result += " ";
	    			}}
	        }
	        else {
				if(!stopwordList.contains(check))
				{
					result += check;
					result += " ";
				}}
		}
		
		if(result.length() != 0)
		{return result.substring(0, result.length()-1);}
		return "";
	}
	
	//Stemming Algorithm
	public static String stem(String keyword){
	    return filterOne(keyword);}
	
	//Step one, get rid of plurals, -ed or -ing 
	public static String filterOne(String keyword) {
	    if(keyword.endsWith("sses"))
	    {
	        keyword = keyword.substring(0, keyword.length()-4);
	        keyword = keyword + "ss";
	        return keyword;
	    }
	    else if(keyword.endsWith("ies") && keyword.length() > 6)
	    {
	    	keyword = keyword.substring(0, keyword.length()-3);
	    	keyword = keyword + "y";
	        return keyword;
	    }
	    else if(keyword.endsWith("ed") && keyword.length() > 2)
	    {
	    	keyword = keyword.substring(0, keyword.length()-2);
	    	if(keyword.endsWith("at")) {
	    		keyword = keyword.substring(0, keyword.length()-2);
	    		keyword = keyword + "ate";
	    		return keyword;
	    	}
	    	else if(keyword.endsWith("bl")) {
	    		keyword = keyword.substring(0, keyword.length()-2);
	    		keyword = keyword + "ble";
	    		return keyword;
	    	}
	    	else if(keyword.endsWith("iz")) {
	    		keyword = keyword.substring(0, keyword.length()-2);
	    		keyword = keyword + "ize";
	    		return keyword;
	    	}
	    	else if(keyword.endsWith("v")) {
	    		keyword = keyword + "e";
	    		return keyword;
	    	}
	    }
	    else if(keyword.endsWith("ing") && containVow(keyword) && keyword.length() > 3)
	    {
	    	keyword = keyword.substring(0, keyword.length()-3);
	    	if(keyword.endsWith("at")) {
	    		keyword = keyword.substring(0, keyword.length()-2);
	    		keyword = keyword + "ate";
	    		return keyword;
	    	}
	    	else if(keyword.endsWith("bl")) {
	    		keyword = keyword.substring(0, keyword.length()-2);
	    		keyword = keyword + "ble";
	    		return keyword;
	    	}
	    	else if(keyword.endsWith("iz")) {
	    		keyword = keyword.substring(0, keyword.length()-2);
	    		keyword = keyword + "ize";
	    		return keyword;
	    	}
	    	else if(keyword.endsWith("v")) {
	    		keyword = keyword + "e";
	    		return keyword;
	    	}
	    }
	    
	    keyword = filterTwo(keyword);
		return keyword;
	}
	
	public static String filterTwo(String keyword) {
    	if(keyword.endsWith("tional"))
    	{
    		keyword = keyword.replace("tional", "tion");
    		return keyword;
    	}
    	else if(keyword.endsWith("izer"))
    	{
    		keyword = keyword.replace("izer", "ize");
    		return keyword;
    	}
    	else if(keyword.endsWith("bly"))
    	{
    		keyword = keyword.replace("bly", "ble");
    		return keyword;
    	}
    	else if(keyword.endsWith("ally"))
    	{
    		keyword = keyword.replace("ally", "al");
    		return keyword;
    	}
    	else if(keyword.endsWith("ently"))
    	{
    		keyword = keyword.replace("ently", "ent");
    		return keyword;
    	}
    	else if(keyword.endsWith("ely"))
    	{
    		keyword = keyword.replace("ely", "e");
    		return keyword;
    	}
    	else if(keyword.endsWith("ously"))
    	{
    		keyword = keyword.replace("ously", "ous");
    		return keyword;
    	}
    	else if(keyword.endsWith("ization"))
    	{
    		keyword = keyword.replace("ization", "ize");
    		return keyword;
    	}
    	else if(keyword.endsWith("ation"))
    	{
    		keyword = keyword.replace("ation", "ate");
    		return keyword;
    	}
    	else if(keyword.endsWith("ator"))
    	{
    		keyword = keyword.replace("ator", "ate");
    		return keyword;
    	}
    	else if(keyword.endsWith("alism"))
    	{
    		keyword = keyword.replace("alism", "al");
    		return keyword;
    	}
    	else if(keyword.endsWith("tly"))
    	{
    		keyword = keyword.replace("tly", "t");
    		return keyword;
    	}
    	else if(keyword.endsWith("iveness"))
    	{
    		keyword = keyword.replace("iveness", "ive");
    		return keyword;
    	}
    	else if(keyword.endsWith("fulness"))
    	{
    		keyword = keyword.replace("fulness", "ful");
    		return keyword;
    	}
    	else if(keyword.endsWith("ousness"))
    	{
    		keyword = keyword.replace("ousness", "ous");
    		return keyword;
    	}
    	else if(keyword.endsWith("ality"))
    	{
    		keyword = keyword.replace("ality", "al");
    		return keyword;
    	}
    	else if(keyword.endsWith("ivity"))
    	{
    		keyword = keyword.replace("ivity", "ive");
    		return keyword;
    	}
    	else if(keyword.endsWith("bility"))
    	{
    		keyword = keyword.replace("bility", "ble");
    		return keyword;
    	}
    	    	return keyword;
	}

    public static boolean containVow(String check) {
    	int a = check.indexOf('a');
    	int b = check.indexOf('e');
    	int c = check.indexOf('i');
    	int d = check.indexOf('o');
    	int e = check.indexOf('u');

    	if(a != -1 || b != -1|| c != -1 || d != -1 || e != -1)
    	{return true;}
    	return false;
    }
}
