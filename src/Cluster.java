import java.util.*;

public class Cluster {
	TreeMap<String,HashMap<String, Double>> centroid = new TreeMap<String,HashMap<String, Double>>();
	HashMap<String, HashMap<String, Double>> docWeight = new HashMap<String, HashMap<String, Double>>();
	ArrayList<String> cluster1 = new ArrayList<String>();
	ArrayList<String> cluster2 = new ArrayList<String>();
	ArrayList<String> cluster3 = new ArrayList<String>();
	ArrayList<String> cluster4 = new ArrayList<String>();
	ArrayList<String> cluster5 = new ArrayList<String>();
	ArrayList<HashMap<String, Double>> storage = new ArrayList<HashMap<String, Double>>();

	public Cluster(HashMap<String, HashMap<String, Double>> docWeightInput) {
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
		//Remove document in the first interation
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
		//Do clustering 4 more times
		for(int i = 0; i < 4; i++)
		{
			otherIteration();
		}
		//Evaluation?

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
        } 
	}

	//Calculate similarity between centroid and each document
	public double calculateSim(HashMap<String, Double> centroid, HashMap<String, Double> docW){
		



		return 0.0;
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

		HashMap<String, Double> input = new HashMap<String, Double>();
		//loop through first cluster		
		for(int i = 0; i < c1Size; i++){


			//centroid.put("centroid1", input);
		}

		input = new HashMap<String, Double>();
		//loop through second cluster				
		for(int i = 0; i < c2Size; i++){
			

			//centroid.put("centroid2", input);
		}

		input = new HashMap<String, Double>();
		//loop through third cluster		
		for(int i = 0; i < c3Size; i++){
			

			//centroid.put("centroid3", input);			
		}

		input = new HashMap<String, Double>();
		//loop through fourth cluster		
		for(int i = 0; i < c4Size; i++){


			//centroid.put("centroid4", );			
		}

		input = new HashMap<String, Double>();
		//loop through fifth cluster		
		for(int i = 0; i < c5Size; i++){
			

			//centroid.put("centroid5", input);
		}
	}
}
