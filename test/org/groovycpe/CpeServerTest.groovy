package org.groovycpe
import groovy.mock.interceptor.StubFor
import org.xmlsoap.schemas.soap.envelope.Envelope

/**
 * @author anders
 *
 */
public class CpeServerTest extends TestCase{	
	void testCpeConnectionRequestServerRun(){
			def ccrst = new Thread(ccrs);
			ccrst.start()			
		
			HttpClient httpclient = new HttpClient()
	}
}