import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;


public class EMAlgorithm {

	public static class WordPair {
		public String e;
		public String f;

		public WordPair(String e, String f) {
			this.e = e;
			this.f = f;
		}

		public String toString() {
			return e + "|" + f;
		}

		@Override
		public int hashCode() {
			return (e + "|" + f).hashCode();
		}

		@Override
		public boolean equals(Object obj) {

			WordPair wp=(WordPair)obj;
			return this.e.equals(wp.e) && this.f.equals(wp.f);
		}


	}

	public static String EnglishFile="train_eng_500.txt";
	public static String GermanFile="train_ger_500.txt";
	public static int NUM_ITERATIONS=100;


	public static HashSet<String> EnglishWords=new HashSet<String>();
	public static HashSet<String> GermanWords=new HashSet<String>();
	public static HashMap<WordPair,Double> t=new HashMap<WordPair,Double>();
	


	public static void main(String args[]) throws IOException{

		//Runs EM Algorithm on the training files, and produces the 't(e|f)' table
		initialiseWords();


		//Initialise t(e|f) uniformly
		initialiseTMap();
		/*Double initialValue=(double) (1.0/GermanWords.size());
		for (String e:EnglishWords){
			for(String f:GermanWords){
				t.put(new WordPair(e, f), initialValue);
			}
		}*/

		//While not converges do
		double previousPerplexity=0;
		double perplexity=0;


		outer:for(int i=0;i<NUM_ITERATIONS;i++){

			//System.out.println("Previous perplexity was "+previousPerplexity);
			//System.out.println("Diff is "+(previousPerplexity-perplexity));
			Double diff=Math.abs(previousPerplexity)-Math.abs(perplexity);

			if(i>1){
				System.out.println("Perplexity decreased by "+Math.abs(diff));
				if(!Double.isInfinite(diff) && Math.abs(diff)-5<0.1){
					System.out.println("Algorithm converged");
					break outer;
				}
			}
			BufferedReader englishFile=new BufferedReader(new FileReader(new File(EnglishFile)));
			BufferedReader germanFile=new BufferedReader(new FileReader(new File(GermanFile)));

			
			double perplexitySum=0;	
			System.out.println("Iteration : "+i);

			HashMap<WordPair,Double> count=new HashMap<WordPair,Double>();
			initCountMap(count);
			HashMap<String,Double> total=new HashMap<String,Double>();
			HashMap<String,Double> s_total=new HashMap<String,Double>();

			
			// count(e|f) = 0 for all e,f
/*			for (String e:EnglishWords){
				for(String f:GermanWords){
					count.put(new WordPair(e, f), (double) 0);
				}
			}*/

			// total(f) = 0 for all f
			for(String f:GermanWords){
				total.put(f, (double)0);
			}

			String englishLine=englishFile.readLine();
			String germanLine=germanFile.readLine();

			while(englishLine!=null && germanLine!=null){

				englishLine=englishLine.replaceAll("\\p{Punct}|\\d","").toLowerCase();
				germanLine=germanLine.replaceAll("\\p{Punct}|\\d","").toLowerCase();

				String E[]=englishLine.split(" ");
				String F[]=germanLine.split(" ");

				for (String e:E){
					s_total.put(e, (double) 0);

					for(String f:F){

						Double tVal=t.get(new WordPair(e, f));
						if(tVal==null)
							tVal=(double) 0;
						s_total.put(e, s_total.get(e)+tVal);
					}
				}
				for(String e:E){
					for(String f:F){
						count.put(new WordPair(e, f), count.get(new WordPair(e, f))+(t.get(new WordPair(e, f))/s_total.get(e)));
						total.put(f, total.get(f)+(t.get(new WordPair(e, f))/s_total.get(e)));
					}
				}
				englishLine=englishFile.readLine();
				germanLine=germanFile.readLine();

			}
			//Estimate probabilities
			for(String f:GermanWords){
				for(String e:EnglishWords){
					if(t.get(new WordPair(e, f))!=null){
						//System.out.println(count.get(new WordPair(e, f)));
						t.put(new WordPair(e, f), count.get(new WordPair(e, f))/total.get(f));
					}
				}
			}

			/******Print out the t(e|f) values*****************/
			/*for(String e:EnglishWords){
				for(String f:GermanWords){
					WordPair idx=new WordPair(e, f);
					if(t.get(idx)!=null)
						System.out.println("t ( "+e+" | "+f+" )= "+t.get(idx));
				}
				System.out.println();
			}
			System.out.println("t table computed!");*/

			//Computation of perplexity
			englishFile.close();
			germanFile.close();

			englishFile=new BufferedReader(new FileReader(new File(EnglishFile)));
			germanFile=new BufferedReader(new FileReader(new File(GermanFile)));

			englishLine=englishFile.readLine();
			germanLine=germanFile.readLine();
			previousPerplexity=perplexity;
			
			while(englishLine!=null && germanLine!=null){

				englishLine=englishLine.replaceAll("\\p{Punct}|\\d","").toLowerCase();
				germanLine=germanLine.replaceAll("\\p{Punct}|\\d","").toLowerCase();

				String englishWords[]=englishLine.split(" ");
				String germanWords[]=germanLine.split(" ");

				double power=(Math.pow(germanWords.length, englishWords.length));

				double probability=(double)1.0;
				//System.out.println("Init with "+probability);

				for(String eng:englishWords){
					double sum=0;
					for(String ger:germanWords){
						if(t.get(new WordPair(eng, ger))!=null){
							sum+=t.get(new WordPair(eng, ger));
							//System.out.print(sum+"\t");

						}else
							System.out.println("oops!");

					}
					probability*=sum;
				}
				double mul=Math.log10(probability);
				mul=Math.abs(mul);
				//System.out.println("eps="+Math.pow(10, mul-1));
				//probability*=Math.pow(10, mul-1);
			
				/***** Print out probability values **********/
				//System.out.println("p( "+englishLine+" | "+germanLine+"="+probability);
				int pow=((int)Math.log10(probability));
				pow++;
				//probability*=Math.pow(10, pow);
				
				
				perplexitySum+=Math.log10(probability)/Math.log10(2);

				englishLine=englishFile.readLine();
				germanLine=germanFile.readLine();

			}
			perplexitySum=0-perplexitySum;
			
			
			perplexity=perplexitySum;
		
			//perplexity=Math.pow(2, perplexitySum);
			System.out.println("log(Perplexity)= "+perplexity);
			englishFile.close();
			germanFile.close();
		}
	}

	//Reads both files and fills up the words into the two Sets
	public static void initialiseWords() throws IOException{

		BufferedReader englishFile=new BufferedReader(new FileReader(new File(EnglishFile)));
		BufferedReader germanFile=new BufferedReader(new FileReader(new File(GermanFile)));

		//Read a line, remove all junk and make it lower case
		String engLine=englishFile.readLine();
		//System.out.println(engLine);
		while(engLine!=null){
			engLine=engLine.replaceAll("\\p{Punct}|\\d","").toLowerCase();
			//Put all words in the line into the set
			String engWords[]=engLine.split(" ");
			for(String s:engWords)
				EnglishWords.add(s);
			engLine=englishFile.readLine();
		}

		//Read a line, remove all junk and make it lower case
		String germanLine=germanFile.readLine();
		while(germanLine!=null){
			germanLine=germanLine.replaceAll("\\p{Punct}|\\d","").toLowerCase();
			//Put all words in the line into the set
			String gerWords[]=germanLine.split(" ");
			for(String s:gerWords)
				GermanWords.add(s);
			germanLine=germanFile.readLine();
		}

		System.out.println("There are "+EnglishWords.size()+" English and "+GermanWords.size()+" German words in vocabulary");
		englishFile.close();
		germanFile.close();
	}
	
	public static void initialiseTMap() throws IOException{
		
		BufferedReader englishFile=new BufferedReader(new FileReader(new File(EnglishFile)));
		BufferedReader germanFile=new BufferedReader(new FileReader(new File(GermanFile)));

		//Read a line, remove all junk and make it lower case
		String engLine=englishFile.readLine();
		String germanLine=germanFile.readLine();
		Double initialValue=(double) (1.0/GermanWords.size());
		//System.out.println(engLine);
		while(engLine!=null && germanLine!=null){
			engLine=engLine.replaceAll("\\p{Punct}|\\d","").toLowerCase();
			germanLine=germanLine.replaceAll("\\p{Punct}|\\d","").toLowerCase();
			//Put all words in the line into the set
			String engWords[]=engLine.split(" ");
			String gerWords[]=germanLine.split(" ");
			int numGerman=gerWords.length;
			
			for(int i=0;i<engWords.length;i++){
				
				for (int j=0;j<numGerman;j++){
					t.put(new WordPair(engWords[i], gerWords[j]),initialValue);
				}
			}
			engLine=englishFile.readLine();
			germanLine=germanFile.readLine();
		}
		System.out.println("Initialised tmap with "+t.size()+" pairs");
		englishFile.close();
		germanFile.close();

	}
	public static void initCountMap(HashMap<WordPair,Double> count) throws IOException{
		
		BufferedReader englishFile=new BufferedReader(new FileReader(new File(EnglishFile)));
		BufferedReader germanFile=new BufferedReader(new FileReader(new File(GermanFile)));

		//Read a line, remove all junk and make it lower case
		String engLine=englishFile.readLine();
		String germanLine=germanFile.readLine();
		Double initialValue=(double) (1.0/GermanWords.size());
		//System.out.println(engLine);
		while(engLine!=null && germanLine!=null){
			engLine=engLine.replaceAll("\\p{Punct}|\\d","").toLowerCase();
			germanLine=germanLine.replaceAll("\\p{Punct}|\\d","").toLowerCase();
			//Put all words in the line into the set
			String engWords[]=engLine.split(" ");
			String gerWords[]=germanLine.split(" ");
			int numGerman=gerWords.length;
			
			for(int i=0;i<engWords.length;i++){
				
				for (int j=0;j<numGerman;j++){
					count.put(new WordPair(engWords[i], gerWords[j]),(double) 0);
				}
			}
			engLine=englishFile.readLine();
			germanLine=germanFile.readLine();
		}
		//System.out.println("Initialised count with "+count.size()+" pairs");
		englishFile.close();
		germanFile.close();

		
		
	}
	

}
