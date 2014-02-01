import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;




public class MainDriver {
	
	public static Set<EMAlgorithm.WordPair> potentialMatches;
	
	public static void main(String args[]) throws IOException{
		System.out.println("*** IBM Model 1 ***");
		System.out.println("Step 1: Training ");
		System.out.print("Foreign language file name: \t");
		String englishFile,foreignFile;
		Scanner in=new Scanner(System.in);
		foreignFile=in.next();
		System.out.print("English language file name: \t");
		englishFile=in.next();
		
		System.out.println("Training model.. ");
		long st=System.currentTimeMillis();
		String args1[]=new String[2];
		args1[0]=englishFile;
		args1[1]=foreignFile;//"train_eng_5k.txt";
		EMAlgorithm.train(args1);
		System.out.println("************Time for training: "+(System.currentTimeMillis()-st)/1000+" s***********");
		
		System.out.println("Step 2: Decoding ");
		System.out.println("Enter the name of the file containing foreign sentences to be translated");
		String translationFile=in.next();
		doTranslation(translationFile);
		System.out.println();
		in.close();
	}
	
	public static void doTranslation(String inputFile) throws IOException{
		
		potentialMatches=EMAlgorithm.t.keySet();
		
		BufferedReader in=new BufferedReader(new FileReader(new File(inputFile)));
		String line=in.readLine();
		
		while(line!=null){
			
			line=line.replaceAll("\\p{Punct}|\\d","").toLowerCase();
			System.out.println(line+ " -> ");
			String words[]=line.split(" ");
			
			for(String word : words){
				translateWord(word);
			}
			System.out.println();
			line=in.readLine();
		}
		
		in.close();
	}
	
	public static void translateWord(String word){
		
		double maxProb=0.0;
		String translatedWord=null;
		
		
		for(EMAlgorithm.WordPair prob: potentialMatches){
			
			if(prob.f.equalsIgnoreCase(word)){
				//System.out.println(prob.e);
				double probability=EMAlgorithm.t.get(prob);
				if(probability-maxProb>0.0){
					maxProb=probability;
					translatedWord=prob.e;
		
				}
			}
		}
		if(maxProb==0.0){
			System.out.println(word+" -> NULL\t");
		}
		else{
			System.out.println(word+" -> "+translatedWord+"("+maxProb+")  ");
		}
	}
}
