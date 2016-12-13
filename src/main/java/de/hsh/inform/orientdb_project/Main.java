package de.hsh.inform.orientdb_project;

import java.util.List;

import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import de.hsh.inform.orientdb_project.model.EthernetFrameModel;
import de.hsh.inform.orientdb_project.model.HostModel;
import de.hsh.inform.orientdb_project.model.TcpConnectionModel;
import de.hsh.inform.orientdb_project.orientdb.OrientDbHelperService;
import de.hsh.inform.orientdb_project.repository.EthernetFrameRepository;
import de.hsh.inform.orientdb_project.repository.HostRepository;
import de.hsh.inform.orientdb_project.repository.TcpConnectionRepository;
import de.hsh.inform.orientdb_project.util.ConfigPropertiesReader;

public class Main {

	public static void main(String[] args) {
		ConfigPropertiesReader config = new ConfigPropertiesReader();
		OrientDbHelperService odhs = new OrientDbHelperService(config.dbhost, config.dbname, config.dbuser,
				config.dbpass);
		System.out.println("Using database: " + odhs.getDbUri(true));

		// Get "handle" for database to pass to import service
		OrientGraphNoTx ogf = odhs.getOrientGraphNoTx();
		
		TcpConnectionRepository tcr = new TcpConnectionRepository(ogf);
		List<TcpConnectionModel> result = tcr.findByActiveWhen(901713642);
		for(TcpConnectionModel m : result) {
			System.out.println(m.toString());
		}
		
		long r = tcr.getTotalDataVolumePerMinuteBetweenHosts("172.16.114.207", "206.251.19.72");
		System.out.println("Bytes per Second: " + r);

		HostRepository hr = new HostRepository(ogf);
		for(HostModel hm : hr.findByConnectionsTo("197.218.177.69", 25)) {
			System.out.println(hm);
		}
		
		for(HostModel hm : hr.findAllByConnectionsToOutsideHosts()) {
			System.out.println(hm);
		}
		
		EthernetFrameRepository efr = new EthernetFrameRepository(odhs.getDatabaseDocument());
		List<EthernetFrameModel> efrbyteResult = efr.findAllByRawData(new byte[] {
			(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
		});
		for(EthernetFrameModel em : efrbyteResult) {
			System.out.println(em);
		}
		
		// Done
		odhs.close();
	}

}
