import java.util.*;

public class Cluster {
	TreeMap<String,HashMap<String, Double>> centroid = new TreeMap<String,HashMap<String, Double>>();
	ArrayList<String> cluster1 = new ArrayList<String>();
	ArrayList<String> cluster2 = new ArrayList<String>();
	ArrayList<String> cluster3 = new ArrayList<String>();
	ArrayList<String> cluster4 = new ArrayList<String>();
	ArrayList<String> cluster5 = new ArrayList<String>();
	ArrayList<HashMap<String, Double>> storage = new ArrayList<HashMap<String, Double>>();

	public Cluster(HashMap<String, HashMap<String, Double>> docWeight) {
		centroid.put("centroid1", docWeight.get("b001"));
		centroid.put("centroid2", docWeight.get("e001"));
		centroid.put("centroid3", docWeight.get("p001"));
		centroid.put("centroid4", docWeight.get("s001"));
		centroid.put("centroid5", docWeight.get("t001"));
		
		storage.add(docWeight.get("b001"));
		storage.add(docWeight.get("e001"));
		storage.add(docWeight.get("p001"));
		storage.add(docWeight.get("s001"));
		storage.add(docWeight.get("t001"));
		
		cluster1.add("b001");
		cluster2.add("e001");
		cluster3.add("p001");
		cluster4.add("s001");
		cluster5.add("t001");
		
		docWeight.remove("b001");
		docWeight.remove("e001");
		docWeight.remove("p001");
		docWeight.remove("s001");
		docWeight.remove("t001");		

		firstIteration();
		
		docWeight.put("b001", storage.get(0));
		docWeight.put("e001", storage.get(1));
		docWeight.put("p001", storage.get(2));
		docWeight.put("s001", storage.get(3));
		docWeight.put("t001", storage.get(4));
	
		otherIteration();
	}

	private void firstIteration() {
        for (Map.Entry<String,HashMap<String, Double>> currCentroid : centroid.entrySet()) 
        {

        	
        	
        	
        	
        } 
	}
	public void otherIteration() {
        for (Map.Entry<String,HashMap<String, Double>> currCentroid : centroid.entrySet()) 
        {

        	
        	
        	
        	
        } 
	}
}
