package org.groovycpe
import javax.xml.namespace.QName;

import org.dslforum.cwmp_1_0.GetParameterAttributes
import org.dslforum.cwmp_1_0.ID;
import org.dslforum.cwmp_1_0.ParameterAttributeStruct


public class CpeActions {
	
	CpeConfDB confdb;
	
	def doInform(eventKeyList){
		def inform = new Inform()
		
		inform.deviceId = new DeviceIdStruct()
		inform.deviceId.manufacturer = confdb.confs['InternetGatewayDevice.DeviceInfo.Manufacturer'].value
		inform.deviceId.serialNumber = confdb.confs['InternetGatewayDevice.DeviceInfo.SerialNumber'].value
	}
				confdb.confs[confkey] instanceof ConfParameter &&
				confdb.confs[confkey].notification != null				
	
	def inEnvelope( cwmpObject, headerID ){		
		ID id = new ID(value: headerID, mustUnderstand: Boolean.TRUE )
		Header header = new Header(any: [ id ])
		Envelope envelope = new Envelope(body: new Body(any: [cwmpObject]), header: header);		
		return envelope
	}
}