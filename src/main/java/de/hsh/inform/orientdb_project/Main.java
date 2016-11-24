package de.hsh.inform.orientdb_project;

import java.io.EOFException;
import java.util.concurrent.TimeoutException;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapNativeException;

import com.tinkerpop.blueprints.impls.orient.OrientConfigurableGraph.THREAD_MODE;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import de.hsh.inform.orientdb_project.netdata.AbstractNetdataImportService;
import de.hsh.inform.orientdb_project.orientdb.HighPerformanceKappaOrientDbNetdataImportService;
import de.hsh.inform.orientdb_project.orientdb.OrientDbHelperService;

public class Main {

	public static void main(String[] args) {
		OrientDbHelperService odhs = new OrientDbHelperService("127.0.0.1", "hshtest", "root", "root");
		odhs.cleanUpServer();
		odhs.setupSchema();
		
		String filename = "/home/jpt/Temp/tcpdump_2";
		OrientGraphNoTx ogf = odhs.getOrientGraphFactory().getNoTx();
		ogf.setThreadMode(THREAD_MODE.MANUAL);

		//AbstractNetdataImportService importService = new DummyImportService(filename);
		//AbstractNetdataImportService importService = new LowPerformanceOrientDbNetdataImportService(filename, ogf);
		AbstractNetdataImportService importService = new HighPerformanceKappaOrientDbNetdataImportService(filename, ogf);
		try {
			System.out.println(System.currentTimeMillis() + ": Begin import of data ...");
			importService.run();
			System.out.println("Import of data done!");
		} catch (EOFException | PcapNativeException | TimeoutException | NotOpenException e) {
			e.printStackTrace();
		}
	}
	
}
