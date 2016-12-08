package de.hsh.inform.orientdb_project.UNUSED;

import java.util.HashMap;
import java.util.Map;

import org.pcap4j.packet.ArpPacket;
import org.pcap4j.packet.IcmpV4CommonPacket;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;

import com.orientechnologies.orient.graph.batch.OGraphBatchInsert;

import de.hsh.inform.orientdb_project.netdata.AbstractNetdataImportService;

public class BatchNetdataImportService extends AbstractNetdataImportService {

	public BatchNetdataImportService(String filename) {
		super(filename);
	}

	@Override
	public void handleArpPacket(ArpPacket arp, long timestamp, int milliseconds) {

	}

	@Override
	public void handleTcpPacket(TcpPacket tcp, long timestamp, int milliseconds) {

	}

	@Override
	public void handleUdpPacket(UdpPacket udp, long timestamp, int milliseconds) {

	}

	@Override
	public void handleIcmpPacket(IcmpV4CommonPacket icmp, long timestamp, int milliseconds) {

	}

	@Override
	public void afterImport() {
		/*
		OGraphBatchInsert batch = new OGraphBatchInsert("plocal:your/db", "admin", "admin");
		//phase 1: begin
		batch.begin();
		//phase 2: create edges
		Map<String, Object> edgeProps = new HashMap<String, Object>();
		edgeProps.put("foo", "bar");
		batch.createEdge(0L, 1L, edgeProps);
		batch.createVertex(2L);
		batch.createEdge(3L, 4L, null);
		//phase 3: set properties on vertices, THIS CAN BE DONE ONLY AFTER EDGE AND VERTEX CREATION
		Map<String, Object> vertexProps = new HashMap<String, Object>();
		vertexProps.put("foo", "bar");
		batch.setVertexProperties(0L, vertexProps);
		//phase 4: end
		batch.end();
		//There is no need to create vertices before connecting them:
		batch.createVertex(0L);
		batch.createVertex(1L);
		batch.createEdge(0L, 1L, props);
		// is equivalent to (but less performing than)
		batch.createEdge(0L, 1L, props);
		//batch.createVertex(Long) is needed only if you want to create unconnected vertices
		*/
	}

}
