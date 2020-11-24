import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;

public class Search {

	public static HashMap<String, Double> IDF = new HashMap<String, Double>();
	public static HashMap<String, HashMap<String, Double>> docWeight = new HashMap<String, HashMap<String, Double>>();
	public static HashMap<Integer, Double> magnitudes = new HashMap<Integer, Double>();


	public static void main(String args[]) throws IOException{

		Scanner scanD = new Scanner(new BufferedReader(new FileReader("src//dictionary.txt")));
		Scanner scanP = new Scanner(new BufferedReader(new FileReader("src//postings.txt")));

		while(scanD.hasNext())
		{

			String term = scanD.next();

			//Finding IDF
			double frequency = scanD.nextInt();
			double div = 2225/frequency;
			double termIDF = Math.log10(div);

			IDF.put(term, termIDF);
			//System.out.println("term: " + term + "\n IDF: " + termIDF);

		}
		String next = scanP.next();
		while(scanP.hasNext())
		{

			String tempDocID = "";
			HashMap<String, Double> weightList = new HashMap<String, Double>();

			if(next.equals(".D"))
			{
				tempDocID = scanP.next();
				String next2 = scanP.next();
				while(!(next2).equals(".D")) {
					String word = next2;
					//scanP.next();
					double freq = scanP.nextInt();
					double termFreq = 1 + Math.log10(freq);
					//System.out.println("IDF: " + IDF.get(word) + "\n term freq: " + termFreq);
					double weight = (IDF.get(word))*termFreq;
					/*if(weight < 0)
					{
						System.out.println("DocID: " + tempDocID + "\nWord: " + word + "\nWeight: " + weight);
					}*/
					weightList.put(word, weight);
					if(scanP.hasNext())
					{
						next2 = scanP.next();
					}
					else
					{
						break;
					}
				}
			}

			docWeight.put(tempDocID, weightList);
			System.out.println("DocID: " + tempDocID + "\n" + weightList);

		}
		scanP.close();
		scanD.close();

	}
}