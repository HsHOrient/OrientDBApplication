package de.hsh.inform.orientdb_project.UNUSED;

import org.pcap4j.packet.ArpPacket;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IcmpV4CommonPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;

import de.hsh.inform.orientdb_project.netdata.AbstractNetdataImportService;

/**
 * For benchmarking purposes only. Does not do a thing!
 */
public class DummyImportService extends AbstractNetdataImportService {

	
	public DummyImportService(String filename) {
		super(filename);
	}
	
	public void handleEthernetPacket(EthernetPacket ether, long ts, int ms) {
		super.handleEthernetPacket(ether, ts, ms);
	}
	
	public void handleArpPacket(ArpPacket arp, long ts, int ms) {}
	
	public void handleIpV4Packet(IpV4Packet ipv4, long ts, int ms) {
		super.handleIpV4Packet(ipv4, ts, ms);
	}
	
	public void handleUdpPacket(UdpPacket udp, long ts, int ms) {}
	
	public void handleTcpPacket(TcpPacket tcp, long ts, int ms) {}
	
	public void handleIcmpPacket(IcmpV4CommonPacket icmp, long ts, int ms) {}

	@Override
	public void afterImport() {}


}
