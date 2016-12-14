package de.hsh.inform.orientdb_project;

import java.math.BigInteger;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import de.hsh.inform.orientdb_project.model.EthernetFrameModel;
import de.hsh.inform.orientdb_project.model.Model;
import de.hsh.inform.orientdb_project.orientdb.OrientDbHelperService;
import de.hsh.inform.orientdb_project.repository.EthernetFrameRepository;
import de.hsh.inform.orientdb_project.repository.HostRepository;
import de.hsh.inform.orientdb_project.repository.TcpConnectionRepository;

public class CommandLineInterface {
	private static final Logger log = Logger.getLogger(CommandLineInterface.class.getName());
	private Options options = new Options();
	
	private OrientDbHelperService odhs;

	private OrientGraphNoTx ogf;
	
	private TcpConnectionRepository tcpConnectionRepository;
	private HostRepository hostRepository;
	private EthernetFrameRepository ethernetFrameRepository;

	private boolean keepGoing;

	public CommandLineInterface(OrientDbHelperService odhs) {
		this.odhs = odhs;
		this.ogf = odhs.getOrientGraphNoTx();
		this.tcpConnectionRepository = new TcpConnectionRepository(this.ogf);
		this.hostRepository = new HostRepository(this.ogf);
		this.ethernetFrameRepository = new EthernetFrameRepository(this.odhs.getDatabaseDocument());
		
		options.addOption("e", "ethernetFramesByBytes", false, "Find ethernet frames that contain a given byte sequence.");

		options.addOption("htoipp", "hostsByIpAndPort", false, "Find hosts that have tcp connections to a given ip address and port.");
		options.addOption("htoex", "hostsByConnToExternalHosts", false, "Find hosts that have tcp connections to external hosts.");
		options.addOption("hinw", "hostsWithIncomingOnWellKnownPorts", false, "Find hosts that have incoming tcp connections on well known ports.");
		
		options.addOption("ta", "tcpConnectionActiveAt", false, "Find tcp connections that were active at a given timestamp.");
		options.addOption("tbpm", "tcpConnectionBytesPerMinuteBetween", false, "Get datavolume (bytes per minute) between two given ip addresses.");
		
		options.addOption("h", "help", false, "show help.");
		options.addOption("q", "quit", false, "quit the program.");
	}

	public void parse(String arguments) {
		String[] args = arguments.split(" ");
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
			if(cmd.hasOption("h"))
				this.help();
			if(cmd.hasOption("q"))
				this.quit();
			
			if(cmd.hasOption("e")) {
				String allBytes = "";
				for(String byteValue : cmd.getArgs()) {
					allBytes += byteValue;
				}
				byte[] needle = new BigInteger(allBytes, 16).toByteArray();

				List<EthernetFrameModel> result = this.ethernetFrameRepository.findAllByRawData(needle);
				System.out.println("EthernetFrames that contain the given bytes:");
				this.printResults(result);
			}
			
			if(cmd.hasOption("htoipp")) {
				
			}
		} catch (ParseException e) {
			//log.log(Level.SEVERE, "Failed to parse comand line properties", e);
			log.log(Level.SEVERE, "I did not understand that. Sorry.");
			this.help();
		}
	}

	private void printResults(List<? extends Model> result) {
		for(Object o : result) {
			System.out.println(o);
		}
		System.out.println("End of result list.");
	}

	private void quit() {
		this.keepGoing = false;
		System.out.println("Bye bye.");
	}

	private void help() {
		HelpFormatter formater = new HelpFormatter();
		formater.printHelp(" ", options);
	}

	public void run() {
		this.keepGoing = true;
		Scanner s = new Scanner(System.in);
		while(this.keepGoing) {
			System.out.print("> ");
			String arguments = s.nextLine();
			System.out.println("");
			this.parse(arguments);
		}
		System.out.println("End of Program");
		s.close();
	}
}
