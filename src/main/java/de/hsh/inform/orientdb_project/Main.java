package de.hsh.inform.orientdb_project;

import java.io.EOFException;
import java.util.concurrent.TimeoutException;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapNativeException;

import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import de.hsh.inform.orientdb_project.netdata.AbstractNetdataImportService;
import de.hsh.inform.orientdb_project.orientdb.NodeBasedImportService;
import de.hsh.inform.orientdb_project.orientdb.OrientDbHelperService;
import de.hsh.inform.orientdb_project.util.ConfigPropertiesReader;

public class Main {

	public static void main(String[] args) {
		ConfigPropertiesReader config = new ConfigPropertiesReader();
		String filename = config.filename;
		OrientDbHelperService odhs = new OrientDbHelperService(config.dbhost, config.dbname, config.dbuser, config.dbpass);
		System.out.println("Using database: " + odhs.getDbUri(true));

		// Clean up existing database and set up schema from scratch
		odhs.cleanUpServer();
		odhs.setupSchema();

		// Get "handle" for database to pass to import service
		OrientGraphNoTx ogf = odhs.getOrientGraphNoTx();
		
		//AbstractNetdataImportService importService = new DummyImportService(filename); // Only for comparison reasons
		AbstractNetdataImportService importService = new NodeBasedImportService(filename, ogf);
		
		// Go go gadget import service!
		try {
			System.out.println(System.currentTimeMillis()/1000L + ": Begin import of data ...");
			if(config.limitedImport) {
				importService.partialRun(config.importLimit);
			} else {
				importService.run();
			}
			System.out.println(System.currentTimeMillis()/1000L + ": Import of data done!");
		} catch (EOFException | PcapNativeException | TimeoutException | NotOpenException e) {
			e.printStackTrace();
		}
		
		// Done
		odhs.close();
		System.out.println(System.currentTimeMillis()/1000L + ": End of program.");
	}
	
}
