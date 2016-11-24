package de.hsh.inform.orientdb_project.netdata;

import org.pcap4j.packet.ArpPacket;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IcmpV4CommonPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;

public interface NetdataResultObserver {

	public abstract void handleEthernetPacket(EthernetPacket ether, long timestamp, int milliseconds);
	
	public abstract void handleArpPacket(ArpPacket arp, long timestamp, int milliseconds);
	
	public abstract void handleTcpPacket(TcpPacket tcp, long timestamp, int milliseconds);
	
	public abstract void handleUdpPacket(UdpPacket udp, long timestamp, int milliseconds);
	
	public abstract void handleIcmpPacket(IcmpV4CommonPacket icmp, long timestamp, int milliseconds);

	public abstract void handleIpV4Packet(IpV4Packet ipv4, long ts, int ms);
	
	public abstract void afterImport();
	
}
