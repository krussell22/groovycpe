package org.groovycpe

 * @author anders
 *
 */
public class CpeConnectionRequestServer implements Runnable {
	
	
			int port = new URL(cpeActions.confdb.confs['InternetGatewayDevice.ManagementServer.ConnectionRequestURL'].value).getPort()
}