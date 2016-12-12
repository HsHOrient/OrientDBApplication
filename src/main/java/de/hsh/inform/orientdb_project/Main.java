package de.hsh.inform.orientdb_project;

import java.util.List;

import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import de.hsh.inform.orientdb_project.model.TcpConnectionModel;
import de.hsh.inform.orientdb_project.orientdb.OrientDbHelperService;
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

		// Done
		odhs.close();
	}

}
