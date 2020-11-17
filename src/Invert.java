import java.util.*;
import java.io.*;

public class Invert {
    private static TreeMap<String, ArrayList<Integer>> dict = new TreeMap<String,ArrayList<Integer>>();//term and list of doc ID

    public static void main(String args[]) {
        HashMap<String, ArrayList<Integer>> posting = new HashMap<String,ArrayList<Integer>>();//term+id and term freq
        ArrayList<String> stopwordList = new ArrayList<String>();
        Scanner scan = new Scanner(System.in);
        String stop, stem, keyword, temp; 
        String[] hyphen = new String[5];
        int id = 0;

        //Turn on or off stopword filter
        System.out.println("Stopword: on / off");
        //Get user input
        stop = scan.nextLine();
        //Check if user input is valid
        while(!stop.equals("on")){
            if(!stop.equals("off"))
            {
                System.out.println("Invalid stopword input. Try again");
                stop = scan.nextLine();
            }
            else{break;}
        }
        //Turn on or off stemming filter
        System.out.println("Stemming: on / off");
        //Get user input
        stem = scan.nextLine();
        //Check if user input is valid
        while(!stem.equals("on")){
            if(!stem.equals("off"))
            {
                System.out.println("Invalid stemming input. Try again");
                stem = scan.nextLine();
            }
            else{break;}
        }
        //Inform user if inputs are valid
        System.out.println("Both inputs are valid");
        scan.close();
    
        //If stop filter on, get all the stopwords and store in array
        if(stop.equals("on"))
        {
            try {
                File docs = new File(".\\src\\common_words");
                scan = new Scanner(docs);
                while(scan.hasNext()){
                    String insert = scan.next();
                    stopwordList.add(insert);
                }
            }
            catch(FileNotFoundException e) {
                System.out.println("File not found");
            }
        }

        //scan cacm.all as input 
        try {
            File docs = new File(".\\src\\cacm.all");
            scan = new Scanner(docs);
        }
        catch(FileNotFoundException e) {
            System.out.println("File not found");
        }
        
        //Write the words into title.txt
        try {
            FileWriter writer3 = new FileWriter(".\\src\\title.txt");
            FileWriter writer4 = new FileWriter(".\\src\\optimize.txt");

        //While there are still terms keep scanning cacm.all
        while(scan.hasNext())
        {
            keyword = scan.next();
            //Get ID Information
            if(keyword.equals(".I")) {
                id = scan.nextInt();
                writer3.write("\n.I " + id + "\n");
                writer4.write("\n.I " + id + "\n");
                keyword = scan.next();
            }
            //Get title Information
            if(keyword.equals(".T")){
                keyword = scan.next();
                //scan until reaches W or B
                while(!keyword.equals(".W") && !keyword.equals(".B"))
                {
                    //write it to title.txt
                    writer3.write(keyword + " ");
                    //change everything to lower case and get words only
                    keyword = keyword.toLowerCase();
                    keyword = keyword.replaceAll("[^-a-z]", "");

                    //if keyword is empty, skip it
                    if(!keyword.equals(""))
                    {
                        //if the word contains -, split and store in array
                        if(keyword.contains("-")) {
                            hyphen = keyword.split("-");
                            for(int i = 0; i < hyphen.length; i++) {
                                //if it's not empty
                                if(!hyphen[i].equals(""))
                                {
                                	//stem words if it's on
                                	if(stem.equals("on"))
                                	{
                                		hyphen[i] = stem(hyphen[i]);
                                	}
                                	if(stop.equals("on")) {
                                		if(!stopwordList.contains(hyphen[i])) {
                                        	writer4.write(hyphen[i] + " ");
                                		}
                                	}
                                	else {
                                		writer4.write(hyphen[i] + " ");
                                	}
                                    //create key for posting
                                    temp = hyphen[i] + " " + id;
                                    //if dict has the term but from a diff doc, add doc ID
                                    if(dict.containsKey(hyphen[i]) && !dict.get(hyphen[i]).contains(id)) {
                                        dict.get(hyphen[i]).add(id);
                                        //set the posting - use term+id key, add TF
                                        posting.put(temp, new ArrayList<Integer>());
                                        posting.get(temp).add(1);
                                    }
                                    //if the doc has the same term previously
                                    else if(dict.containsKey(hyphen[i])){
                                        //add term frequency
                                        posting.get(temp).set(0,(posting.get(temp).get(0))+1);
                                    }
                                    //if it's a new term, add doc ID
                                    else {
                                        dict.put(hyphen[i], new ArrayList<Integer>());
                                        dict.get(hyphen[i]).add(id);
                                        //set the posting - use term+id key, add TF
                                        posting.put(temp, new ArrayList<Integer>());
                                        posting.get(temp).add(1);
                                    }
                                }
                            }
                        }
                        //if the word is normal, without any -
                        else{
                        	//stem words if it's on
                        	if(stem.equals("on"))
                        	{
                        		keyword = stem(keyword);
                        	}
                        	if(stop.equals("on")) {
                        		if(!stopwordList.contains(keyword)) {
                                	writer4.write(keyword + " ");
                        		}
                        	}         	
                        	else {
                        		writer4.write(keyword + " ");
                        	}
                            //create key for posting
                            temp = keyword + " " + id;
                            //if dict has the term but from a diff doc, add doc ID
                            if(dict.containsKey(keyword) && !dict.get(keyword).contains(id)){
                                dict.get(keyword).add(id);
                                //set the posting - use term+id key, add TF
                                posting.put(temp, new ArrayList<Integer>());
                                posting.get(temp).add(1);
                            }
                            //if the doc has the same term previously
                            else if (dict.containsKey(keyword))
                            {
                                //add term frequency
                                posting.get(temp).set(0,(posting.get(temp).get(0))+1);
                            }
                            //if it's a new term, add doc ID
                            else {
                                dict.put(keyword, new ArrayList<Integer>());
                                dict.get(keyword).add(id);
                                //set the posting - use term+id key, add TF
                                posting.put(temp, new ArrayList<Integer>());
                                posting.get(temp).add(1);
                            }
                        }
                    }
                    keyword = scan.next();
                }
            }
            
            if(keyword.equals(".A")) {
            	writer3.write("\n");
            	while(!keyword.equals(".N") && !keyword.equals(".C") && !keyword.equals(".K")) {
            		writer3.write(keyword + " ");
            		keyword = scan.next();
            	}
            }
                 
            //Get Abstract Information
            if(keyword.equals(".W")){
                keyword = scan.next();
                while(!keyword.equals(".B"))
                {
                    keyword = keyword.toLowerCase();
                    keyword = keyword.replaceAll("[^-a-z]", "");
                    
                    //if keyword is empty, skip it
                    if(!keyword.equals(""))
                    {
                        if(keyword.contains("-")) 
                        {
                            hyphen = keyword.split("-");

                            for(int i = 0; i < hyphen.length; i++)
                            {
                                //if it's not empty
                                if(!hyphen[i].equals(""))
                                {
                                	//stem words if it's on
                                	if(stem.equals("on"))
                                	{
                                		hyphen[i] = stem(hyphen[i]);
                                	}
                                	if(stop.equals("on")) {
                                		if(!stopwordList.contains(hyphen[i])) {
                                        	writer4.write(hyphen[i] + " ");
                                		}
                                	}
                                	else {
                                    	writer4.write(hyphen[i] + " ");
                                	}         
                                    //create key for posting
                                    temp = hyphen[i] + " " + id;
                                    //if dict has the term but from a diff doc, add doc ID
                                    if(dict.containsKey(hyphen[i]) && !dict.get(hyphen[i]).contains(id)) {
                                        dict.get(hyphen[i]).add(id);
                                        //set the posting - use term+id key, add TF
                                        posting.put(temp, new ArrayList<Integer>());
                                        posting.get(temp).add(1);
                                    }
                                    //if the doc has the same term previously
                                    else if(dict.containsKey(hyphen[i])){
                                        //add term frequency
                                        posting.get(temp).set(0,(posting.get(temp).get(0))+1);
                                    }
                                    //if it's a new term, add doc ID
                                    else {
                                        dict.put(hyphen[i], new ArrayList<Integer>());
                                        dict.get(hyphen[i]).add(id);
                                        //set the posting - use term+id key, add TF
                                        posting.put(temp, new ArrayList<Integer>());
                                        posting.get(temp).add(1);
                                    }
                                }
                            }
                        }
                        else{
                        	//stem words if it's on
                        	if(stem.equals("on"))
                        	{
                        		keyword = stem(keyword);
                        	}
                        	if(stop.equals("on")) {
                        		if(!stopwordList.contains(keyword)) {
                                	writer4.write(keyword + " ");
                        		}
                        	}
                        	else {
                            	writer4.write(keyword + " ");
                        	}     
                            //create key for posting
                            temp = keyword + " " + id;
                            //if dict has the term but from a diff doc, add doc ID
                            if(dict.containsKey(keyword) && !(dict.get(keyword).contains(id))){
                                dict.get(keyword).add(id);
                                //set the posting - use term+id key, add TF
                                posting.put(temp, new ArrayList<Integer>());
                                posting.get(temp).add(1);
                            }
                            //if the doc has the same term previously
                            else if (dict.containsKey(keyword)){
                                //add term frequency
                                posting.get(temp).set(0,(posting.get(temp).get(0))+1);
                            }
                            //if it's a new term, add doc ID
                            else{
                                dict.put(keyword, new ArrayList<Integer>());
                                dict.get(keyword).add(id);
                                //set the posting - use term+id key, add TF
                                posting.put(temp, new ArrayList<Integer>());
                                posting.get(temp).add(1);
                            }
                        }
                    }
                    keyword = scan.next();
                }
            }
        }
        scan.close();
        writer3.close();
        writer4.close();
        }
        catch (IOException e) {
            System.out.println("Fail to write");}
        
        
        //Write the result into dict.txt and postings.txt
        try {
            FileWriter writer = new FileWriter(".\\src\\dictionary.txt");
            FileWriter writer2 = new FileWriter(".\\src\\postings.txt");
            String finder = "";

            //Check whether stopword filter was turned on
            if(stop.equals("off"))
            {
                //loop through all terms stored in dict
                for (Map.Entry<String, ArrayList<Integer>> info : dict.entrySet()) 
                {
                        writer.write(info.getKey() + " " + info.getValue().size() + "\n");
                        writer2.write(info.getKey() + "\n");
                        for(int i = 0; i < info.getValue().size(); i++)
                        {
                            //Write the doc ID of where the term occurs
                            writer2.write(info.getValue().get(i)+" ");
                            //key+id for every term in every doc
                            finder = info.getKey() + " " + info.getValue().get(i);
                            //print the term frequency
                            writer2.write(posting.get(finder).get(0) + " ");
                            writer2.write("\n");
                        }               
                } 
            }
            else{
                for (Map.Entry<String, ArrayList<Integer>> info : dict.entrySet()) 
                {
                    //If the term is not a stop word, print it
                    if(!stopwordList.contains(info.getKey())){
                        writer.write(info.getKey() + " " + info.getValue().size() + "\n");
                        writer2.write(info.getKey() + "\n");
                        for(int i = 0; i < info.getValue().size(); i++)
                        {
                            //Write the doc ID of where the term occurs
                            writer2.write(info.getValue().get(i)+" ");
                            //key+id for every term in every doc
                            finder = info.getKey() + " " + info.getValue().get(i);
                            //print the term frequency
                            writer2.write(posting.get(finder).get(0) + " ");
                            writer2.write("\n");
                        }       
                    }       
                } 
            }
            writer.close();
            writer2.close();
        }
        catch (IOException e) {
            System.out.println("Fail to write");}
    }
	
	public static String stem(String keyword){
	    return filterOne(keyword);
	}
	
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
	    
	    else if(keyword.endsWith("s"))
	    {
	    	String check = keyword.substring(0,keyword.length()-1);
	    	if(dict.containsKey(check)) {keyword = check;}
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