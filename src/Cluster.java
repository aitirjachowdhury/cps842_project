import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Cluster {
	TreeMap<String,HashMap<String, Double>> centroid = new TreeMap<String,HashMap<String, Double>>();
	HashMap<String, HashMap<String, Double>> docWeight = new HashMap<String, HashMap<String, Double>>();
	ArrayList<String> cluster1 = new ArrayList<String>();
	ArrayList<String> cluster2 = new ArrayList<String>();
	ArrayList<String> cluster3 = new ArrayList<String>();
	ArrayList<String> cluster4 = new ArrayList<String>();
	ArrayList<String> cluster5 = new ArrayList<String>();
	
	ArrayList<ArrayList<String>> clusterList = new ArrayList<ArrayList<String>>();
	ArrayList<Integer> maxNums = new ArrayList<Integer>();
	double maxCount = 0;
	
	
	double distance1 = 0;
	double distance2 = 0;
	double distance3 = 0;
	double distance4 = 0;
	double distance5 = 0;
	
	int count = 0;
	
	ArrayList<HashMap<String, Double>> storage = new ArrayList<HashMap<String, Double>>();

	public Cluster(HashMap<String, HashMap<String, Double>> docWeightInput) {
		FileWriter myEval = null;
		try {
			myEval = new FileWriter("src//eval.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		docWeight = docWeightInput;
		//Use document 1 of each category as centroid
		centroid.put("centroid1", docWeight.get("b001"));
		centroid.put("centroid2", docWeight.get("e001"));
		centroid.put("centroid3", docWeight.get("p001"));
		centroid.put("centroid4", docWeight.get("s001"));
		centroid.put("centroid5", docWeight.get("t001"));
		//Store first document
		storage.add(docWeight.get("b001"));
		storage.add(docWeight.get("e001"));
		storage.add(docWeight.get("p001"));
		storage.add(docWeight.get("s001"));
		storage.add(docWeight.get("t001"));
		//Add each document to respective cluster
		cluster1.add("b001");
		cluster2.add("e001");
		cluster3.add("p001");
		cluster4.add("s001");
		cluster5.add("t001");
		//Remove document in the first interaction
		docWeight.remove("b001");
		docWeight.remove("e001");
		docWeight.remove("p001");
		docWeight.remove("s001");
		docWeight.remove("t001");		
		//First iteration clustering
		firstIteration();
		//Add removed documents back to the list
		docWeight.put("b001", storage.get(0));
		docWeight.put("e001", storage.get(1));
		docWeight.put("p001", storage.get(2));
		docWeight.put("s001", storage.get(3));
		docWeight.put("t001", storage.get(4));
		//Do clustering 5 more times
		for(int i = 0; i < 6; i++)
		{
			otherIteration();
		}
		
		//Evaluation
		clusterList.add(cluster1);
		clusterList.add(cluster2);
		clusterList.add(cluster3);
		clusterList.add(cluster4);
		clusterList.add(cluster5);
		
		for(ArrayList<String> list : clusterList)
		{
			int bCount = 0;
			int eCount = 0;
			int pCount = 0;
			int sCount = 0;
			int tCount = 0;
			ArrayList<Integer> countList = new ArrayList<Integer>();
			for(String docID : list)
			{
				if(docID.startsWith("b"))
				{
					bCount++;
				}
				if(docID.startsWith("e"))
				{
					eCount++;
				}
				if(docID.startsWith("p"))
				{
					pCount++;
				}
				if(docID.startsWith("s"))
				{
					sCount++;
				}
				if(docID.startsWith("t"))
				{
					tCount++;
				}
			}
			countList.add(bCount);
			countList.add(eCount);
			countList.add(pCount);
			countList.add(sCount);
			countList.add(tCount);
			maxCount = maxCount + Collections.max(countList);
			//System.out.println(maxCount);
		}
		
		
		double purity = maxCount/2225;
		
		System.out.println("Purity is " + purity + "\n");
		try {
			myEval.write("Purity is " + purity + "\n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		System.out.println("Tighteness of Cluster 1 = " + distance1);
		System.out.println("Tighteness of Cluster 2 = " + distance2);
		System.out.println("Tighteness of Cluster 3 = " + distance3);
		System.out.println("Tighteness of Cluster 4 = " + distance4);
		System.out.println("Tighteness of Cluster 5 = " + distance5);
		
		try {
			myEval.write("\nTighteness of Cluster 1 = " + distance1 + "\nTighteness of Cluster 2 = " 
			+ distance2 + "\nTighteness of Cluster 3 = " + distance3 + "\nTighteness of Cluster 4 = " 
			+ distance4 + "\nTighteness of Cluster 5 = " + distance5);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			myEval.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("\nCluster 1" + cluster1);
		System.out.println("Cluster 2" + cluster2);
		System.out.println("Cluster 3" + cluster3);
		System.out.println("Cluster 4" + cluster4);
		System.out.println("Cluster 5" + cluster5);
		
	}

	private void firstIteration() {
		//Loop through all the documents
		for (Map.Entry<String, HashMap<String, Double>> doc : docWeight.entrySet()) 
		{
			//Empty similarity for each document
			ArrayList<Double> simScore = new ArrayList<Double>();
			//Loop through all the centroids
        	for (Map.Entry<String,HashMap<String, Double>> currCentroid : centroid.entrySet()) 
			{	
				double result = calculateSim(currCentroid.getValue(), doc.getValue());
				simScore.add(result);
			}
			int cluster = findCluster(simScore);
			//Add document to cluster based on which pos is max value
			if(cluster == 1){cluster1.add(doc.getKey());}
			if(cluster == 2){cluster2.add(doc.getKey());}
			if(cluster == 3){cluster3.add(doc.getKey());}
			if(cluster == 4){cluster4.add(doc.getKey());}
			if(cluster == 5){cluster5.add(doc.getKey());}
        } 
	}

	public void otherIteration() {
		count++;
		//recalculate centroid value based on the document weight in cluster
		recalculateCentroid();
		//Empty arraylist before clustering again
		cluster1 = new ArrayList<String>();
		cluster2 = new ArrayList<String>();
		cluster3 = new ArrayList<String>();
		cluster4 = new ArrayList<String>();
		cluster5 = new ArrayList<String>();

		//Loop through all the documents
		for (Map.Entry<String, HashMap<String, Double>> doc : docWeight.entrySet()) 
		{
			//Empty similarity for each document
			ArrayList<Double> simScore = new ArrayList<Double>();
			//Loop through all the centroids
        	for (Map.Entry<String,HashMap<String, Double>> currCentroid : centroid.entrySet()) 
			{	
				double result = calculateSim(currCentroid.getValue(), doc.getValue());
				simScore.add(result);
			}
			int cluster = findCluster(simScore);
			//Add document to cluster based on which pos is max value
			if(cluster == 1){cluster1.add(doc.getKey());}
			if(cluster == 2){cluster2.add(doc.getKey());}
			if(cluster == 3){cluster3.add(doc.getKey());}
			if(cluster == 4){cluster4.add(doc.getKey());}
			if(cluster == 5){cluster5.add(doc.getKey());}
			
			if(count == 6)
			{
				double minDistance = 1 - (simScore.get(cluster-1));
				if(cluster == 1){distance1 = distance1 + minDistance*minDistance;}
				if(cluster == 2){distance2 = distance2 + minDistance*minDistance;}
				if(cluster == 3){distance3 = distance3 + minDistance*minDistance;}
				if(cluster == 4){distance4 = distance4 + minDistance*minDistance;}
				if(cluster == 5){distance5 = distance5 + minDistance*minDistance;}
			}
        } 
	}
	
	//Calculate similarity between centroid and each document
	public double calculateSim(HashMap<String, Double> centr, HashMap<String, Double> docW){
		double dotP = 0.0;
		
		//Find the shorter one and do weight calculate based on that
		if(centr.size() < docW.size()){
			//Loop and find dot product
			for (Map.Entry<String, Double> term : centr.entrySet()) 
			{	
				String word = term.getKey();
				if(docW.containsKey(word)){
					dotP += term.getValue() * docW.get(word);
				}
			}
		}
		else{
			//Loop and find dot product
			for (Map.Entry<String, Double> term : docW.entrySet()) 
			{	
				String word = term.getKey();
				if(centr.containsKey(word)){
					dotP += term.getValue() * centr.get(word);
				}
			}
		}
		
		//normalize with magnitude
		double centrMag = 0.0;
		double docWMag = 0.0;
		for (Map.Entry<String, Double> term : centr.entrySet()) 
		{	
			centrMag += Math.pow(term.getValue(), 2);
		}
		centrMag = Math.sqrt(centrMag);
		for (Map.Entry<String, Double> term : docW.entrySet()) 
		{	
			docWMag += Math.pow(term.getValue(), 2);
			
		}
		
		docWMag = Math.sqrt(docWMag);
		
		
		double deno = centrMag * docWMag;
		double sim = dotP / deno;
		
		
		return sim;
	}

	//Find max value between the 5 similarity scores
	public int findCluster(ArrayList<Double> simScore){
		int index = simScore.indexOf(Collections.max(simScore));
		return index+1;
	}

	public void recalculateCentroid(){
		//Empty centroid values
		centroid = new TreeMap<String,HashMap<String, Double>>();

		int c1Size = cluster1.size();
		int c2Size = cluster2.size();
		int c3Size = cluster3.size();
		int c4Size = cluster4.size();
		int c5Size = cluster5.size();

		//Hashmap to store centroid weight
		HashMap<String, Double> input = new HashMap<String, Double>();
		//Hashmap to store values after dividing by size
		HashMap<String, Double> finalCent = new HashMap<String, Double>();
		//loop through first cluster		
		for(int i = 0; i < c1Size; i++){
			//get the weight vector of each document in cluster 1
			for (Map.Entry<String, Double> term : docWeight.get(cluster1.get(i)).entrySet()) 
			{	
				//If term exists, add the new weight to the stored one
				if(input.containsKey(term.getKey())){
					double og = input.get(term.getKey());
					double add = term.getValue();
					input.put(term.getKey(), og+add);
				}
				//If does not exist, put the term and weight directly
				else{
					input.put(term.getKey(), term.getValue());
				}
			}
			//Loop through input and divide by cluster size
			for (Map.Entry<String, Double> term : input.entrySet()) 
			{	
				double result = term.getValue()/c1Size;
				finalCent.put(term.getKey(), result);
			}
			//Put in centroid TreeMap
			centroid.put("centroid1", finalCent);
		}
		//System.out.println(centroid);

		input = new HashMap<String, Double>();
		finalCent = new HashMap<String, Double>();
		//loop through second cluster				
		for(int i = 0; i < c2Size; i++){
			//get the weight vector of each document in cluster 2
			for (Map.Entry<String, Double> term : docWeight.get(cluster2.get(i)).entrySet()) 
			{	
				//If term exists, add the new weight to the stored one
				if(input.containsKey(term.getKey())){
					double og = input.get(term.getKey());
					double add = term.getValue();
					input.put(term.getKey(), og+add);
				}
				//If does not exist, put the term and weight directly
				else{
					input.put(term.getKey(), term.getValue());
				}
			}
			//Loop through input and divide by cluster size
			for (Map.Entry<String, Double> term : input.entrySet()) 
			{	
				double result = term.getValue()/c2Size;
				finalCent.put(term.getKey(), result);
			}
			//Put in centroid TreeMap
			centroid.put("centroid2", finalCent);
		}

		input = new HashMap<String, Double>();
		finalCent = new HashMap<String, Double>();
		//loop through third cluster		
		for(int i = 0; i < c3Size; i++){
			//get the weight vector of each document in cluster 2
			for (Map.Entry<String, Double> term : docWeight.get(cluster3.get(i)).entrySet()) 
			{	
				//If term exists, add the new weight to the stored one
				if(input.containsKey(term.getKey())){
					double og = input.get(term.getKey());
					double add = term.getValue();
					input.put(term.getKey(), og+add);
				}
				//If does not exist, put the term and weight directly
				else{
					input.put(term.getKey(), term.getValue());
				}
			}	
			//Loop through input and divide by cluster size
			for (Map.Entry<String, Double> term : input.entrySet()) 
			{	
				double result = term.getValue()/c3Size;
				finalCent.put(term.getKey(), result);
			}
			//Put in centroid TreeMap
			centroid.put("centroid3", finalCent);			
		}

		input = new HashMap<String, Double>();
		finalCent = new HashMap<String, Double>();
		//loop through fourth cluster		
		for(int i = 0; i < c4Size; i++){
			//get the weight vector of each document in cluster 2
			for (Map.Entry<String, Double> term : docWeight.get(cluster4.get(i)).entrySet()) 
			{	
				//If term exists, add the new weight to the stored one
				if(input.containsKey(term.getKey())){
					double og = input.get(term.getKey());
					double add = term.getValue();
					input.put(term.getKey(), og+add);
				}
				//If does not exist, put the term and weight directly
				else{
					input.put(term.getKey(), term.getValue());
				}
			}
			//Loop through input and divide by cluster size
			for (Map.Entry<String, Double> term : input.entrySet()) 
			{	
				double result = term.getValue()/c4Size;
				finalCent.put(term.getKey(), result);
			}
			//Put in centroid TreeMap
			centroid.put("centroid4", finalCent);			
		}

		input = new HashMap<String, Double>();
		finalCent = new HashMap<String, Double>();
		//loop through fifth cluster		
		for(int i = 0; i < c5Size; i++){
			//get the weight vector of each document in cluster 2
			for (Map.Entry<String, Double> term : docWeight.get(cluster5.get(i)).entrySet()) 
			{	
				//If term exists, add the new weight to the stored one
				if(input.containsKey(term.getKey())){
					double og = input.get(term.getKey());
					double add = term.getValue();
					input.put(term.getKey(), og+add);
				}
				//If does not exist, put the term and weight directly
				else{
					input.put(term.getKey(), term.getValue());
				}
			}	
			//Loop through input and divide by cluster size
			for (Map.Entry<String, Double> term : input.entrySet()) 
			{	
				double result = term.getValue()/c5Size;
				finalCent.put(term.getKey(), result);
			}
			//Put in centroid TreeMap
			centroid.put("centroid5", finalCent);
		}
	}
}

