
//package cps842f20_prj_AryaChowdhuryLevina;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Scanning {

	public static void main(String args[]) throws IOException {
		File[] fileArray1 = new File("src/bbc").listFiles();
		// FileWriter writer;
		//System.out.println(fileArray1.length);
		for (File f1 : fileArray1) 
		{
			System.out.println(f1);
			File[] fileArray2 = new File("" + f1).listFiles();
			int count = 0;
			for (File f2 : fileArray2) // loop thru all files
			{
				if (f2.getName().endsWith(".txt")) // to deal with the .txt files.
				{
					Scanner s = new Scanner(f2); // to read the files
					count++;
					System.out.println(s.next() + " " + count);
					s.close();
				}
			}
		}
		// writer.close();
	}
}
