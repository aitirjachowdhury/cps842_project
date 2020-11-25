import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;

public class Scanning {

	public static HashMap<String, Integer> dictionary = new HashMap<String, Integer>();
	public static TreeMap<String, HashMap<String, Integer>> postings = new TreeMap<String, HashMap<String, Integer>>();
	public static boolean stop_words = false;
	public static void main(String args[]) throws IOException{
		
		FileWriter myDictionary = new FileWriter("src//dictionary.txt");
		FileWriter myPostings = new FileWriter("src//postings.txt");
		
		
		String word;
		ArrayList<String> stopList = new ArrayList<String>();
		Scanner stopScan = new Scanner(new BufferedReader(new FileReader("src//common_words")));
		Scanner input = new Scanner(System.in);

		//adding stop_words in stopList
		while(stopScan.hasNext())
		{
			word = stopScan.next();
			stopList.add(word);
		}

		stopScan.close();

		//User input for stop word removal
		System.out.println("Do you want to turn on the stop word removal? (y/n)");
		String answer = input.next();
		while(!answer.equals("y") || !answer.equals("n"))
		{
			if(answer.equals("y"))
			{
				stop_words = true;
				System.out.println("Stop word removal: ON");
				break;
			}
			else if(answer.equals("n"))
			{
				stop_words = false;
				System.out.println("Stop word removal: OFF");
				break;
			}
			else
			{
				System.out.println("Invalid answer. Do you want to turn on the stop word removal? (y/n)");
				answer = input.next();
			}
		}
		
		input.close();
		
		File[] fileArray1=new File("src//bbc").listFiles();    
		//FileWriter writer;
		//System.out.println(fileArray1.length);
		int count = 0;
		
		
		for(File f1: fileArray1)
		{
			String category = "";
			count++;
			//System.out.println(count);
			if(count == 1)
			{
				category = "b";
			}
			else if(count == 2)
			{
				category = "e";
			}
			else if(count == 3)
			{
				category = "p";
			}
			else if(count == 4)
			{
				category = "s";
			}
			else if(count == 5)
			{
				category = "t";
			}
			File[] fileArray2=new File("" + f1).listFiles();      
			
			for(File f2: fileArray2) // loop thru all files
			{
				if(f2.getName().endsWith(".txt")) // to deal with the .txt files.
				{                 
					Scanner s=new Scanner(f2); // to read the files
					//count++;
					HashMap<String, Integer> pDictionary = new HashMap<String, Integer>();
					ArrayList<String> temp = new ArrayList<String>();
					while(s.hasNext())
					{
						String next = s.next();
						String next2=next.replaceAll("[^a-zA-Z]", "").toLowerCase();
						
						if(next2.length() > 2)
						{
						if(!temp.contains(next2))
						{
							if(!dictionary.containsKey(next2))
							{

								dictionary.put(next2, 1);
								temp.add(next2);

							}

							else
							{

								//int freq = postings.get(term).size();
								int num = dictionary.get(next2);
								dictionary.remove(next2);
								dictionary.put(next2, num+1);
								temp.add(next2);
							}			
						}
						
						if(!pDictionary.containsKey(next2))
						{
							pDictionary.put(next2, 1);
						}
						else if(pDictionary.containsKey(next2))
						{
							int num = pDictionary.get(next2);
							pDictionary.remove(next2);
							pDictionary.put(next2, num+1);
						}
						//System.out.println(s.next() + " " + count);
					}
					}
					String docID = category + f2.getName();
					docID = docID.replace(".txt", "");
					//System.out.println(docID);
					postings.put(docID, pDictionary);
					s.close();
				}
			}

		}
		
		if(stop_words == true)
		{
			for(int i=0; i < stopList.size(); i++)
			{
				if(dictionary.containsKey(stopList.get(i)))
				{
					dictionary.remove(stopList.get(i));
					postings.remove(stopList.get(i));
				}
			}
		}

		ArrayList<String> listVal2 = new ArrayList<String>(postings.keySet());
		if(stop_words == true)
		{
			for(int i=0; i < postings.size(); i++)
			{
				for(int j=0; j < stopList.size(); j++)
				{
					if(postings.get(listVal2.get(i)).containsKey(stopList.get(j)))
					{
						postings.get(listVal2.get(i)).remove(stopList.get(j));
					}
				}
			}
		}
		
		ArrayList<String> listVal = new ArrayList<String>(dictionary.keySet());
		Collections.sort(listVal);
		for (int i = 1; i < listVal.size(); i++) {
			myDictionary.write("\n" + listVal.get(i) + "  " + dictionary.get(listVal.get(i)));
		}
		
		
		System.out.println(listVal2.size());
		for(int i = 0; i<listVal2.size(); i++)
		{
			ArrayList<String> listVal3 = new ArrayList<String>(postings.get(listVal2.get(i)).keySet());
			Collections.sort(listVal3);
			myPostings.write("\n\n.D " + listVal2.get(i));
			for(int j = 1; j<listVal3.size(); j++)
			{
				myPostings.write("\n" + listVal3.get(j) + "  " + postings.get(listVal2.get(i)).get(listVal3.get(j)));
			}
		}

		myDictionary.close();
		myPostings.close();
		
	}


}
