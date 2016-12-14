package de.hsh.inform.orientdb_project;

import de.hsh.inform.orientdb_project.orientdb.OrientDbHelperService;
import de.hsh.inform.orientdb_project.util.ConfigPropertiesReader;

public class Main {

	public static void main(String[] args) {
		ConfigPropertiesReader config = new ConfigPropertiesReader();
		OrientDbHelperService odhs = new OrientDbHelperService(config.dbhost, config.dbname, config.dbuser,
				config.dbpass);
		System.out.println("Using database: " + odhs.getDbUri(true));

		CommandLineInterface cli = new CommandLineInterface(odhs);
		cli.run();

		// Done
		odhs.close();
	}

}
