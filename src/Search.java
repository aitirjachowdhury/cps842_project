import java.util.*;
import java.math.*;
import java.io.*;

public class Search {
	HashMap<Integer, HashMap<String, Double>> weight = new HashMap<Integer, HashMap<String, Double>>();
    HashMap<String, ArrayList<Integer>> termId = new HashMap<String, ArrayList<Integer>>();
	HashMap<Integer, Double> magnitude = new HashMap<Integer, Double>();
	HashMap<Integer, Double> cosSim = new HashMap<Integer, Double>();
    HashMap<String, Integer> df= new HashMap<String, Integer>();
    HashMap<String, Double> idf= new HashMap<String, Double>();
    HashMap<String, Double> tf = new HashMap<String, Double>();
	ArrayList<Integer> relDocs = new ArrayList<Integer>();
	ArrayList<String> words = new ArrayList<String>();
	String uQuery = "";
    Scanner scan;
	int id = 0;
	
	public Search() {
		//Scan dictionary file to find the idf values
        try {
            File docs = new File(".\\src\\dictionary.txt");
            scan = new Scanner(docs);
            while(scan.hasNext()){
                String term = scan.next();
                int freq = Integer.parseInt(scan.next());
                df.put(term, freq);
                //Change according to number of docs
                double calc = Math.log10(3204.0/freq);
                idf.put(term, calc);}}
        catch(FileNotFoundException e) {System.out.println("File not found");}
        //Scan posting file to find the tf values
        try {
            File docs = new File(".\\src\\postings.txt");
            scan = new Scanner(docs);
            int counter;
            String original;
            while(scan.hasNextLine()){
            	String term = scan.nextLine();
            	original = term;
            	counter = df.get(term);
            	
            	for(int i = 0; i < counter; i++) {
            		term = scan.nextLine();           		
            		String[] idtf = term.split(" ");
            		//Scan the ID
            		id = Integer.parseInt(idtf[0]);
            		//Scan the Frequency
            		double tFreq = Double.parseDouble(idtf[1]);
            		tFreq = 1.0 + Math.log10(tFreq);
            		//Add doc id if term exist in a particular document 
            		if(!termId.containsKey(original)) {
                    	termId.put(original, new ArrayList<Integer>());
                		termId.get(original).add(id);}
                	else {termId.get(original).add(id);}
                	//Store logged tf to a hashmap
                	String concat = original + " " + id;
                	tf.put(concat, tFreq);}}}
        catch(FileNotFoundException e) {System.out.println("File not found");}
        //Scan optimize to find the term each document has
        try {
            File docs = new File(".\\src\\optimize.txt");
            scan = new Scanner(docs);
            String keyword = scan.next();
            //Get the weight vector value for each documents
            while(scan.hasNext()){
                //Get ID Information and create new hashmap for weight 
                if(keyword.equals(".I")) {
                	id = scan.nextInt();
                    weight.put(id, new HashMap<String, Double>());
                    keyword = scan.next();}
                
                while(!keyword.equals(".I") && scan.hasNext())
                {
                	//If weight is not calculated yet for a particular term,
                	//calculate weight by multiplying idf and tf values
                	if(!weight.get(id).containsKey(keyword))
                	{            		
	                	String key = keyword + " " + id;
	                	double w = idf.get(keyword) * tf.get(key);
	                	weight.get(id).put(keyword, w);

	                	if(scan.hasNext())
	                	{keyword = scan.next();}
                	}
                	//if it was calculated previously, scan the next word
                	else {
                		if(scan.hasNext())
                		{keyword = scan.next();}
                	}
                }               
            }     
            //added for to check the last keyword
            if(!weight.get(id).containsKey(keyword))
        	{            	
            	String key = keyword + " " + id;
            	double w = idf.get(keyword) * tf.get(key);
            	weight.get(id).put(keyword, w);            	
        	}  
        }
        catch(FileNotFoundException e) {
            System.out.println("File not found");
        } 
        //Calculate magnitude and add to magnitude hashmap
        double calcMag = 0;
        //Traverse through weight hashmap to get weight vector of each doc ID
        for (Map.Entry<Integer, HashMap<String, Double>> info : weight.entrySet()) 
        {
        	//Loop through every number in the vector, sum each squared value and squareroot the sum
            for (Map.Entry<String, Double> info2 : info.getValue().entrySet()) 
            {calcMag += Math.pow(info2.getValue(),2);} 
            calcMag = Math.sqrt(calcMag);
            magnitude.put(info.getKey(), calcMag);
            calcMag = 0;
        } 
		scan.close();
	}
	//Method to set the query
	public void setQuery(String uQuery) {
		this.uQuery = uQuery;
		words = new ArrayList<String>();
		relDocs = new ArrayList<Integer>();
        cosSim = new HashMap<Integer, Double>();
        weight.put(0, new HashMap<String, Double>());
		
		String[] userQuery = uQuery.split(" ");
		//scan each word and add to the arraylist
		for(int i = 0; i < userQuery.length; i++) {words.add(userQuery[i]);}	
		//Caculate the term frequency for each word in the query
	    HashMap<String, Integer> queryWordsCount = new HashMap<String, Integer>();
	    for (String s : userQuery){
	        if (queryWordsCount.containsKey(s)) queryWordsCount.replace(s, queryWordsCount.get(s) + 1);
	        else queryWordsCount.put(s, 1);}    
	    //Loop through the query tf hashmap and calculate the logged tf and include in the hashmap with id 0
        for (Map.Entry<String, Integer> info : queryWordsCount.entrySet()) 
        {
        	double tFreq = 1.0 + Math.log10(info.getValue());
        	String concat = info.getKey() + " " + 0;
        	tf.put(concat, tFreq);  
        	//Create a weight vector value for the query
            if(!weight.get(0).containsKey(info.getKey()))
        	{            		
            	if(idf.get(info.getKey()) != null) {
                	double w = idf.get(info.getKey()) * tf.get(concat);
                	weight.get(0).put(info.getKey(), w);}
        	}
        } 
        //Calculate the magnitude value for the query
        double calcMag = 0;
        
        for (Map.Entry<String, Double> info : weight.get(0).entrySet()) 
        {calcMag += Math.pow(info.getValue(),2);} 
        calcMag = Math.sqrt(calcMag);
        magnitude.put(0, calcMag);
	}
	//Calculate the cosine similarity value and store it in LinkedHashMap to maintain order
	public LinkedHashMap<Integer, Double> calculateSim(){
		for(int i = 0; i < words.size(); i++) 
		{
			//Get all the documents where each term occurs in
			ArrayList<Integer> storage = termId.get(words.get(i));
			if(storage != null) {
				for(int j = 0; j < storage.size(); j++) {
					//Check if the same doc id has been stored
					if(!relDocs.contains(storage.get(j))){
						relDocs.add(storage.get(j));}}}
		}
		
		Collections.sort(relDocs);
		//Calculate the dot product between each potential relevant document and query
		for(int i = 0; i < relDocs.size(); i++) {
			double dotP = 0;

			for(int j = 0; j < words.size();j++)
			{
				String key = words.get(j) + " " + relDocs.get(i);
				
				if(tf.containsKey(key)) {
					
					dotP += weight.get(relDocs.get(i)).get(words.get(j)) * weight.get(0).get(words.get(j)) ;}
			}
			
			dotP = dotP / (magnitude.get(0)*magnitude.get(relDocs.get(i)));
			cosSim.put(relDocs.get(i), dotP);}	
		
		List<Double> mapValues = new ArrayList<Double>(cosSim.values());
		//Sort the cosine similarity value
		Collections.sort(mapValues);
		Collections.reverse(mapValues);

		LinkedHashMap<Integer, Double> topK = new LinkedHashMap<Integer, Double>();		
		//Get the top 51 documents with highest cosine similarity values and add to LinkedHashMap
		if(mapValues.size() > 51) {
			for(int i = 0; i < 51; i++) {
				//find the corresponding document ID with that cosine similarity
		        for (Map.Entry<Integer, Double> info : cosSim.entrySet()) 
		        {
					if(mapValues.get(i) == info.getValue()) {
						topK.put(info.getKey(), info.getValue());}
		        }}}
		else {
			//If less than 51, get all documents and add to LinkedHashMap
			for(int i = 0; i < mapValues.size(); i++) {
		        for (Map.Entry<Integer, Double> info : cosSim.entrySet()) 
		        {
					if(mapValues.get(i) == info.getValue()) {
						topK.put(info.getKey(), info.getValue());}
		        }}}

		return topK;	
	}
}
