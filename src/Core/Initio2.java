package Core;

import java.io.File;
//import Core.LicenseCompositionPriorities;
import Core.LicenseCompositionPriorities;

public class Initio2 {
	public static void main(String[] args) {
		
		
		/*args = new String[3];
		args[0] = "/Users/valentina/Documents/workspace/PoliciesComposition/data/L1.ttl";
		args[1] = "/Users/valentina/Documents/workspace/PoliciesComposition/data/L2.ttl";
		args[2] = "/Users/valentina/Documents/workspace/PoliciesComposition/data/L3.ttl";*/
		//args = new String[1];
		//args[0] = "/Users/valentina/Documents/workspace/PoliciesComposition/data/samplesPriorities.ttl";
		//args[0] = "/Users/valentina/Documents/workspace/PoliciesComposition/data/CompositionTest_scientific1.ttl";
		//args[0] = "/Users/valentina/Documents/workspace/PoliciesComposition/data/Test4presentation.ttl";
		//args[0] = "/Users/valentina/Dropbox/module IR 2014/PrivacyLookout (dup. see on Drive)/Ontology-last version/policies/villata_approach2.ttl";
		
		
		LicenseCompositionPriorities compproc = new LicenseCompositionPriorities(new File("/Users/valentina/Documents/workspace/PoliciesComposition/data/sample1.ttl"), new File("/Users/valentina/Documents/workspace/PoliciesComposition/data/sample2.ttl"), "TTL", "Medical");
		
		
		//String resulted_filename = null;
		// TODO Auto-generated method stub
		if (args == null)
			System.out.println("Input arguments are missing.");
		if (args.length < 4)
		{
			System.out.println("More arguments are needed.\n args[0] = 'policy_1 file name'\n args[1] = 'policy_2 file name'\n args[2] = 'user_purpose:[Scientific][Medical]'\n args[3] = 'format:[TTL][N3]'");
		}
		if (args.length == 4)
		{
			//System.out.println("Two");
			
			File L1_file = new File(args[0]);
			
			File L2_file = new File(args[1]);
			
			if (verifyFile(L1_file) && verifyFile(L2_file))
				//LicenseCompositionPriorities lcomp = new LicenseCompositionPriorities(L1_file, L2_file, "ttl");
				System.out.println("files exist");
//			File input_file;
//			LicenseCompositionPriorities lcomp = new LicenseCompositionPriorities(args);
//			for (int i = 0; i < args.length; i++)
//			{
//				input_file = new File(args[i]);
//				if (input_file.exists())
//				{
//					//System.out.println("++++++++++ Iteration #" + (i+1) +" of file " + input_file.getName() + "   ++++++++++");
//					if (resulted_filename == null)
//						resulted_filename = lcomp.analyzeFile(input_file, null, "TTL");
//					else
//					{
//						File output_file = new File(resulted_filename);
//						resulted_filename = lcomp.analyzeFile(input_file, output_file, "TTL");
//					}
//				}
//				else
//					System.err.println("File " + input_file.getAbsolutePath() + " doesn't exist.");
//				System.out.println();
//			}
//			System.out.println("\n++++++++++ Resulted license ++++++++++\n");
//			lcomp.printFile(resulted_filename, "TTL");
		}
	}
	
	private static boolean verifyFile(File f)
	{
		if (!f.exists())
		{
			System.err.println("File " + f.getAbsolutePath() + " doesn't exist.");
			return false;
		}
		else
			return true;
	}
}
