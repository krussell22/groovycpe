package org.groovycpe

import java.io.Serializable

/**
 * @author anders
 *
 */
public class CpeConfDB implements Serializable {
	
	Map confs = [:]
	
	CpeConfDB(){}
		def confDB = new CpeConfDB()
		
			//println "Looking at ${it.name}"
			//println "Looking at ${it.name} - ${it.value}"
			if(! confDB.confs.containsKey(it.name) ){
				if(it.name.endsWith('.')){
					confDB.confs.put(it.name, new ConfObject(name: it.name, writable: 0))
				} else {
					confDB.confs.put(it.name, new ConfParameter(name: it.name, writable: 0))
				}
			}
		def keys = confDB.confs.keySet().asList().sort()
		
		//keys.each{ if(confDB.confs[it] instanceof ConfParameter){ println "${it} ${confDB.confs[it].value}" } }
				
		return confDB
	
	
				println "Looking at object $it"
		println "Deserialized $res"
	
	static void main(args){
		println 'Starting CpeConfDB'
		
		def c = CpeConfDB.readFromGetMessages('testfiles/parameters/')
		println c.confs.size()
	}	
}