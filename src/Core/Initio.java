/**
 * 
 */
package Core;

import java.io.File;

/**
 * @author Valeria Soto-Mendoza
 * @email vsoto@cicese.edu.mx
 * 21/05/2015
 */
public class Initio {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		args = new String[3];
		args[0] = "/Users/valentina/Documents/workspace/PoliciesComposition/data/L1.ttl";
		args[1] = "/Users/valentina/Documents/workspace/PoliciesComposition/data/L2.ttl";
		args[2] = "/Users/valentina/Documents/workspace/PoliciesComposition/data/L3.ttl";
		String resulted_filename = null;
		// TODO Auto-generated method stub
		if (args == null)
			System.out.println("Input arguments are missing");
		else
		{
			File input_file;
			LicenseComposition lcomp = new LicenseComposition();
			for (int i = 0; i < args.length; i++)
			{
				input_file = new File(args[i]);
				if (input_file.exists())
				{
					System.out.println("++++++++++ Iteration #" + (i+1) + "   ++++++++++");
					if (resulted_filename == null)
						resulted_filename = lcomp.analyzeFile(input_file, null, "TTL");
					else
					{
						File output_file = new File(resulted_filename);
						resulted_filename = lcomp.analyzeFile(input_file, output_file, "TTL");
					}
				}
				else
					System.err.println("File " + input_file.getAbsolutePath() + " doesn't exist.");
				System.out.println();
			}
			System.out.println("\n++++++++++ Resulted license ++++++++++\n");
			lcomp.printFile(resulted_filename, "TTL");
		}
	}

}
