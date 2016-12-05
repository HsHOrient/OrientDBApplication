package de.hsh.inform.orientdb_project;

import java.io.EOFException;
import java.util.concurrent.TimeoutException;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapNativeException;

import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import de.hsh.inform.orientdb_project.netdata.AbstractNetdataImportService;
import de.hsh.inform.orientdb_project.orientdb.HighPerformanceKappaOrientDbNetdataImportService;
import de.hsh.inform.orientdb_project.orientdb.OrientDbHelperService;

public class Main {

	public static void main(String[] args) {
		// TODO: Make this configurable or easy to exchange.
		String filename = "/home/jpt/Temp/tcpdump_2";
		OrientDbHelperService odhs = new OrientDbHelperService("192.168.0.110", "hshtest", "root", "root");

		// Clean up existing database and set up schema from scratch
		odhs.cleanUpServer();
		odhs.setupSchema();

		// Get "handle" for database to pass to import service
		OrientGraphNoTx ogf = odhs.getOrientGraphNoTx();
		
		//AbstractNetdataImportService importService = new DummyImportService(filename);
		//AbstractNetdataImportService importService = new LowPerformanceOrientDbNetdataImportService(filename, ogf);
		AbstractNetdataImportService importService = new HighPerformanceKappaOrientDbNetdataImportService(filename, ogf);
		
		// Go go gadget import service!
		try {
			System.out.println(System.currentTimeMillis()/1000L + ": Begin import of data ...");
			importService.partialRun(12000);
			System.out.println("Import of data done!");
		} catch (EOFException | PcapNativeException | TimeoutException | NotOpenException e) {
			e.printStackTrace();
		}
		// Done
		odhs.close();
		System.out.println("End of program.");
	}
	
}
