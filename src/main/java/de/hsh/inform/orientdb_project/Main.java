package de.hsh.inform.orientdb_project;

import java.io.EOFException;
import java.util.concurrent.TimeoutException;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapNativeException;

import de.hsh.inform.orientdb_project.netdata.AbstractNetdataImportService;
import de.hsh.inform.orientdb_project.orientdb.LowPerformanceOrientDbNetdataImportService;
import de.hsh.inform.orientdb_project.orientdb.OrientDbHelperService;

public class Main {

	public static void main(String[] args) {
		OrientDbHelperService odhs = new OrientDbHelperService("192.168.0.110", "hshtest", "root", "root");
		odhs.cleanUpServer();
		odhs.setupSchema();
		
		String filename = "/home/jpt/Temp/tcpdump_2";
		//AbstractNetdataImportService importService = new DummyImportService(filename);
		AbstractNetdataImportService importService = new LowPerformanceOrientDbNetdataImportService(filename, odhs.getOrientGraphFactory().getNoTx());
		try {
			System.out.println(System.currentTimeMillis() + ": Begin import of data ...");
			importService.run();
			System.out.println("Import of data done!");
		} catch (EOFException | PcapNativeException | TimeoutException | NotOpenException e) {
			e.printStackTrace();
		}
	}
	
}
