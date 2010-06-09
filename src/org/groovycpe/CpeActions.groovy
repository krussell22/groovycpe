package org.groovycpe
import javax.xml.namespace.QName;

import org.dslforum.cwmp_1_0.GetParameterAttributesimport org.dslforum.cwmp_1_0.SetParameterAttributesimport org.dslforum.cwmp_1_0.GetParameterValuesimport org.dslforum.cwmp_1_0.SetParameterValuesimport org.dslforum.cwmp_1_0.GetParameterNamesimport org.dslforum.cwmp_1_0.RebootResponseimport org.dslforum.cwmp_1_0.Rebootimport org.dslforum.cwmp_1_0.EventStructimport org.xmlsoap.schemas.soap.envelope.Bodyimport org.xmlsoap.schemas.soap.envelope.Envelopeimport org.xmlsoap.schemas.soap.envelope.Header;
import org.dslforum.cwmp_1_0.ID;
import org.dslforum.cwmp_1_0.ParameterAttributeStructimport org.dslforum.cwmp_1_0.ParameterAttributeListimport org.dslforum.cwmp_1_0.GetParameterAttributesResponseimport org.dslforum.cwmp_1_0.SetParameterAttributesResponseimport org.dslforum.cwmp_1_0.AccessList;import org.dslforum.cwmp_1_0.GetParameterValuesResponseimport org.dslforum.cwmp_1_0.SetParameterValuesResponseimport org.dslforum.cwmp_1_0.ParameterInfoStructimport org.dslforum.cwmp_1_0.ParameterInfoListimport org.dslforum.cwmp_1_0.GetParameterNamesResponseimport org.dslforum.cwmp_1_0.EventListimport org.dslforum.cwmp_1_0.ParameterValueStructimport org.dslforum.cwmp_1_0.ParameterValueListimport org.dslforum.cwmp_1_0.DeviceIdStructimport org.dslforum.cwmp_1_0.Inform;import org.dslforum.cwmp_1_0.AddObject;import org.dslforum.cwmp_1_0.AddObjectResponse;import org.dslforum.cwmp_1_0.DeleteObject;import org.dslforum.cwmp_1_0.DeleteObjectResponse;import org.dslforum.cwmp_1_0.Download;import org.dslforum.cwmp_1_0.DownloadResponse;


public class CpeActions {
	
	CpeConfDB confdb;
	
	def doInform(eventKeyList){
		def inform = new Inform()
		
		inform.deviceId = new DeviceIdStruct()
		inform.deviceId.manufacturer = confdb.confs['InternetGatewayDevice.DeviceInfo.Manufacturer'].value		inform.deviceId.oui 		 = confdb.confs['InternetGatewayDevice.DeviceInfo.ManufacturerOUI'].value
		inform.deviceId.serialNumber = confdb.confs['InternetGatewayDevice.DeviceInfo.SerialNumber'].value		inform.deviceId.productClass = confdb.confs['InternetGatewayDevice.DeviceInfo.ModelName'].value				inform.parameterList = new ParameterValueList()				// use a static list, for now		def pList = [		             'InternetGatewayDevice.DeviceInfo.HardwareVersion',		             'InternetGatewayDevice.DeviceInfo.ProvisioningCode',		             'InternetGatewayDevice.DeviceInfo.SoftwareVersion',		             'InternetGatewayDevice.DeviceInfo.SpecVersion',		             		             'InternetGatewayDevice.DeviceSummary',		             'InternetGatewayDevice.ManagementServer.ConnectionRequestURL',		             'InternetGatewayDevice.ManagementServer.ParameterKey',		             'InternetGatewayDevice.WANDevice.1.WANConnectionDevice.1.WANPPPConnection.1.ExternalIPAddress'		             ]				pList.each{ //println it + ' ' + confdb.confs[it].value			inform.parameterList.any.add(new ParameterValueStruct(name: it, value: confdb.confs[it].value))		}				inform.event = new EventList()		eventKeyList.each{ inform.event.any.add(new EventStruct(eventCode: it))}				return inEnvelope(inform, "00001")
	}		def doGetParameterNames( GetParameterNames getParameterNames ){				def gpnr = new GetParameterNamesResponse()		def pil = new ParameterInfoList()				confdb.confs.keySet().findAll{ it.startsWith( getParameterNames.parameterPath ) }.each({				pil.any.add(new ParameterInfoStruct(name: it))			})		gpnr.parameterList = pil		return inEnvelope(gpnr)	}		def doGetParameterValues( GetParameterValues getParameterValues ){		def pvl = new ParameterValueList()		def nameList = getParameterValues.parameterNames.getAny()		confdb.confs.keySet().findAll{ confkey ->  			nameList.any{confkey.startsWith(it)} && confdb.confs[confkey] instanceof ConfParameter 		}.each{			pvl.any.add(new ParameterValueStruct(name: it, value: confdb.confs[it].value) )		}		return inEnvelope(new GetParameterValuesResponse(parameterList: pvl))	}		def doGetParameterAttributes( GetParameterAttributes getParameterAttributes ){						def nameList = getParameterAttributes.parameterNames.any		def attrs = confdb.confs.keySet().findAll{ confkey ->  				nameList.any{confkey.startsWith(it)} &&
				confdb.confs[confkey] instanceof ConfParameter &&
				confdb.confs[confkey].notification != null							}.collect{				new ParameterAttributeStruct(						name: it, 						notification: Integer.parseInt(confdb.confs[it].notification),						accessList: new AccessList(any: confdb.confs[it].accessList.split(','))  ) 			}				return inEnvelope(new GetParameterAttributesResponse(parameterList: new ParameterAttributeList(any: attrs)))	}		def doReboot(Reboot reboot){				return inEnvelope( new RebootResponse() )	}		def doSetParameterValues(SetParameterValues setParameterValues){		// add error handling		setParameterValues.parameterList.getAny().each{			def conf = confdb.confs[it.name]						conf.value = it.value		}				return inEnvelope(new SetParameterValuesResponse(status: 0))	}	def doSetParameterAttributes(SetParameterAttributes setParameterAttributes){		// add error handling		setParameterAttributes.parameterList.getAny().each{			def conf = confdb.confs[it.name]						conf.accessList = it.accessList.getAny().join(',')			conf.notification = it.notification.toString()		}				return inEnvelope(new SetParameterValuesResponse(status: 0))	}		def doDownload(Download download){		new CpeDownloadJobServer(download: download).start()				return inEnvelope( new DownloadResponse() )	}		def doAddObject(AddObject addObject){		return inEnvelope( new AddObjectResponse() )		}		def doDeleteObject(DeleteObject deleteObject){		return inEnvelope( new DeleteObjectResponse() )		}		def inEnvelope( cwmpObject ){		return new Envelope(body: new Body(any: [cwmpObject]));	}
	
	def inEnvelope( cwmpObject, headerID ){		
		ID id = new ID(value: headerID, mustUnderstand: Boolean.TRUE )
		Header header = new Header(any: [ id ])
		Envelope envelope = new Envelope(body: new Body(any: [cwmpObject]), header: header);		
		return envelope
	}
}