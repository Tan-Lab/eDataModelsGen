package echonet.datawg.main;

import java.io.File;
import java.nio.file.Path;

import echonet.datawg.modelExporters.DDGenerator;

public class DDExporterWithoutGUI {
	public static void main(String[] args) {
		String inputFile = "mra_0215.json";
		System.out.println("Converting DD from " + inputFile);
		DDGenerator ddExporter = new DDGenerator(inputFile);
		ddExporter.toDDFile(Path.of("data" + File.separator + "DD").toFile());
		System.out.println("Finished!");
		
	}
}	
