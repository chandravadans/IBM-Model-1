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
		String args1[]=new String[2];
		args1[0]=englishFile;
		args1[1]=foreignFile;
		EMAlgorithm.train(args1);
		
		
		
		
	
	}

}
