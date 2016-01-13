package analysis;

import java.io.*;
import java.util.ArrayList;
import java.util.zip.*;

import javax.swing.filechooser.FileSystemView;

public class Analysis2 {

	/*
	 * IL = Ilumina
	 * CG = Complete Genomics
	 * Input: .vcf files
	 * Output: list of unique positions along the genome where passed (high quality) variants were located.
	 */
	
	public static void main(String[] args) throws IOException {
		FileSystemView org = FileSystemView.getFileSystemView();
		File dir = new File("J:/Genetic Analysis/genomes");
		File[] chldrn = org.getFiles(dir, true);
		
		/*
		 * 17 .vcf files analyzed. Hard-coded for convenience.
		 */

		int genomes = 17;
		
		
		PrintWriter wIL = new PrintWriter("AllposIL.txt");
		PrintWriter wCG = new PrintWriter("AllposCG.txt");

		for (int j = 1; j < 23; j++) {

			for (int i = 0; i < genomes; i++) {

				CharSequence id = Integer.toString(12877 + i);

				BufferedReader readerCG = null;
				BufferedReader readerIL = null;
				
				for (File entry : chldrn) {
					if (entry.getName().contains(id)) {
						/*
						 * CG files were tagged with 'Beta' in the file name.
						 */
						if (entry.getName().contains("Beta")) {
							readerCG = new BufferedReader(new InputStreamReader(
									new GZIPInputStream(new FileInputStream(entry.getAbsolutePath()))));
						} else {
							readerIL = new BufferedReader(new InputStreamReader(
									new GZIPInputStream(new FileInputStream(entry.getAbsolutePath()))));
						}
					}
				}

				String lineCG = readerCG.readLine();
				String lineIL = readerIL.readLine();
				
				CharSequence chr = Integer.toString(j);
	
				ArrayList<String> callsCG = new ArrayList<String>();
				ArrayList<String> callsIL = new ArrayList<String>();
				callsCG.ensureCapacity(10000000);
				callsIL.ensureCapacity(10000000);

				/*
				 * Skip headers, then skip to chromosome entry.
				 * All 17 .vcfs must be opened 23 times to compare all their calls for each chromosome.
				 */
				
				while (!lineCG.split("\\s")[0].contains(chr) || lineCG.split("\\s")[0].contains("#")) {
				}
				while (lineCG.split("\\s")[0].contains(chr)) {
					if (lineCG.contains("PASS")) {
						if (callsCG.contains(lineCG.split("\\s")[1]) == false) {
							callsCG.add(lineCG.split("\\s")[1]);
							lineCG = readerCG.readLine();
						}
					}
				}

				while (!lineIL.split("\\s")[0].contains(chr) || lineIL.split("\\s")[0].contains("#")) {
				}	
				while (lineIL.split("\\s")[0].contains(chr)) {
					if (lineIL.contains("PASS")) {	
						if (callsIL.contains(lineIL.split("\\s")[1]) == false) {
							callsIL.add(lineIL.split("\\s")[1]);
							lineIL = readerIL.readLine();
						}
					}
				}
				
				for (String line : callsIL) {
					wIL.println(line);
				}
				for (String line : callsCG) {
					wCG.println(line);
				}
				readerCG.close();
				readerIL.close();
			}
		}
		wIL.close();
		wCG.close();
	}
}
