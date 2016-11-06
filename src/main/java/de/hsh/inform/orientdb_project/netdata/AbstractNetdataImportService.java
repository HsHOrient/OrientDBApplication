package de.hsh.inform.orientdb_project.netdata;

import java.io.EOFException;
import java.util.concurrent.TimeoutException;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.ArpPacket;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.FragmentedPacket;
import org.pcap4j.packet.IcmpV4CommonPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.packet.namednumber.IpNumber;

/**
 * Contains the logic to extract all the detailed stuff
 */
public abstract class AbstractNetdataImportService implements NetdataResultObserver {

	private String filename;
	
	public AbstractNetdataImportService(String filename) {
		this.filename = filename;
	}
	
	public final void run() throws PcapNativeException, EOFException, TimeoutException, NotOpenException {
		PcapHandle handle = Pcaps.openOffline(this.filename);
		for (;;) {
			Packet packet = handle.getNextPacketEx();
			if(packet == null) break;
			long ts = handle.getTimestampInts();
			int ms = handle.getTimestampMicros();
			EthernetPacket ether = packet.get(EthernetPacket.class);
			this.handleEthernetPacket(ether, ts, ms);
		}	
	}
	
	public void handleEthernetPacket(EthernetPacket ether, long ts, int ms) {
		EtherType etherType = ether.getHeader().getType();
		if (etherType.equals(EtherType.IPV4)) {
			IpV4Packet ipv4 = ether.getPayload().get(IpV4Packet.class);
			this.handleIpV4Packet(ipv4, ts, ms);
		} else if (ether.getHeader().getType().equals(EtherType.ARP)) {
			ArpPacket arp = ether.getPayload().get(ArpPacket.class);
			this.handleArpPacket(arp, ts, ms);
		} else {
			//System.out.println("Unknown ethernet frame type thing!");
		}
	}
	
	public void handleIpV4Packet(IpV4Packet ipv4, long ts, int ms) {
		IpNumber ipnum = ipv4.getHeader().getProtocol();
		if (ipv4.getPayload() instanceof FragmentedPacket) {
			System.out.println("Fragmented IP Packet!");
		} else if (ipnum.equals(IpNumber.TCP)) {
			TcpPacket tcp = ipv4.getPayload().get(TcpPacket.class);
			this.handleTcpPacket(tcp, ts, ms);
		} else if (ipnum.equals(IpNumber.UDP)) {
			UdpPacket udp = ipv4.getPayload().get(UdpPacket.class);
			this.handleUdpPacket(udp, ts, ms);
		} else if (ipnum.equals(IpNumber.ICMPV4)) {
			IcmpV4CommonPacket icmp = ipv4.getPayload().get(IcmpV4CommonPacket.class);
			this.handleIcmpPacket(icmp, ts, ms);
		} else {
			//System.out.println("Unknown IP Packet!");
		}
	}

}
