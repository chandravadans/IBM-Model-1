import java.io.IOException;
import java.util.Scanner;


public class MainDriver {
	
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
		args1[1]=foreignFile;
		EMAlgorithm.train(args1);
		System.out.println("************Time for training: "+(System.currentTimeMillis()-st)/1000+" s***********");
		
		System.out.println("Step 2: Decoding ");
		System.out.println("Enter the name of the file containing foreign sentences to be translated");
		String translationFile=in.next();
		
		in.close();
		
		
		
		
	
	}

}
