import java.io.*;
import java.util.*;

public class UI {
	
    static ArrayList<String> stopwordList = new ArrayList<String>();
    static HashMap<Integer, ArrayList<String>> titleAuthor = new HashMap<Integer, ArrayList<String>>();
    static LinkedHashMap<Integer, Double> topK = new LinkedHashMap<Integer, Double>();
    static Scanner scan = new Scanner(System.in);
    static Scanner docScan;
    static String stop, stem, query; 
    static Search searching = new Search();
	
    public static void main(String[] args) {    
    	//Scan title.txt to get the title and author of each documents
        try {
            File docs = new File(".\\src\\title.txt");
            docScan = new Scanner(docs);
            String keyword = docScan.next();
            int id = 0;
            String title = "";
            String author = "";
            
            while(docScan.hasNext())
            {
                //Get ID Information
                if(keyword.equals(".I")) {
                	title = "";
                	author = "";
                	id = docScan.nextInt();
                    keyword = docScan.next();
                    titleAuthor.put(id, new ArrayList<String>());}
                
                while(!keyword.equals(".I") && !keyword.equals(".A"))
                {
                	title += keyword + " ";
                	if(docScan.hasNext()) {
                	keyword = docScan.next();}
                	else {break;}
                }

                if(keyword.equals(".A")) {
                    while(!keyword.equals(".I") && docScan.hasNext())
                    {
                    	author += keyword + " ";
                    	keyword = docScan.next();
                    }} 
                titleAuthor.get(id).add(title);
                if(!author.equals("")) {titleAuthor.get(id).add(author);}
            }   
            docScan.close();}
        catch(FileNotFoundException e) {System.out.println("File not found");}

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
        //Inform user if inputs are valid
        System.out.println("Input your query or 'exit':");   
        //Get query from user input
        query = scan.nextLine();
 
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
        

        while(!query.equals("exit")) {
	        //Check query input is not empty after processing
	        if(stop.equals("on")){query = removeStopword(query);}
	        //If it becomes blank, ask for a different query    
	        while(query.isBlank()){
	        	System.out.println("Try another query:");
	            query = scan.nextLine();
	            query = removeStopword(query);
	            if(query.equals("exit")) {break;}}
	        
	        if(query.equals("exit")) {break;}
	        
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
	        
	        searching.setQuery(query);
	        topK = searching.calculateSim();
	        
	        //Print the document information
	        if(topK.size() == 0) {System.out.println("Documents not found");}
	        else {
		        int rank = 1;
		        for (Map.Entry<Integer, Double> info : topK.entrySet()) 
		        {
		        	System.out.println("Ranking: "+ rank);
		        	rank++;
		        	
		        	System.out.println("Document ID: " + info.getKey());
		        	System.out.printf("Similarity Score: %.3f\n", info.getValue());
		        	System.out.println("Title: " + titleAuthor.get(info.getKey()).get(0));
		        	if(titleAuthor.get(info.getKey()).size() > 1) {
		        	System.out.println("Author: " + titleAuthor.get(info.getKey()).get(1));
			        System.out.println("------------------------------");
		        	}
		        	else {
			        	System.out.println("Author: Unknown");
				        System.out.println("------------------------------");}
		        }}
	        System.out.println("Input another query or 'exit':");
	        query = scan.nextLine();
        }      
        System.out.println("Exit successful");
        scan.close();
	}
	//Method to remove stopword from the query
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
