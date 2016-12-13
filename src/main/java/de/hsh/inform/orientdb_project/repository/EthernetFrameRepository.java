package de.hsh.inform.orientdb_project.repository;

import java.util.ArrayList;
import java.util.List;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.iterator.ORecordIteratorClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

import de.hsh.inform.orientdb_project.model.EthernetFrameModel;

public class EthernetFrameRepository {
	private ODatabaseDocumentTx db;
	
	public EthernetFrameRepository(ODatabaseDocumentTx oDatabaseDocumentTx) {
		this.db = oDatabaseDocumentTx;
	}
	
	public List<EthernetFrameModel> findAllByRawData(byte[] needle) {
		ORecordIteratorClass<ODocument> resultIterator = db.browseClass("EthernetFrame");
		List<EthernetFrameModel> result = new ArrayList<EthernetFrameModel>();
		for(ODocument doc : resultIterator) {
			int found = -1;
			byte[] rawData = (byte[]) doc.field("rawData");
			// Manually compare bytes... yay! \o/
			String bigStr = new String(rawData);
			String smallStr = new String(needle);
			found = bigStr.indexOf(smallStr);
			if(found != -1) {
				result.add(new EthernetFrameModel(doc));
			}
		}
		return result;
	}
	
}
