/**
 *	Cate Controller
 *  Copyright 2015 blebson
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "Gate Controller", author: "blebson") {
		capability "Switch"
		attribute "hubactionMode", "string"
		attribute "switch2", "string"
    command "gateOpen"
    command "gateClose"        
        
	}

    preferences {
    input("GateIP", "string", title:"Gate Controller Local IP Address", description: "Please enter your Gate Controller's IP Address", required: true, displayDuringSetup: true)
    input("GatePort", "string", title:"Gate Controller Port", description: "Please enter your Gate Controller's Port", defaultValue: 80 , required: true, displayDuringSetup: true)
    //input("CameraUser", "string", title:"Camera User", description: "Please enter your camera's username", required: false, displayDuringSetup: true)
    //input("CameraPassword", "password", title:"Camera Password", description: "Please enter your camera's password", required: false, displayDuringSetup: true)
	}
    
	simulator {
    
	}

    tiles {
      standardTile("open", "device.switch", width: 3, height: 2, canChangeIcon: false) {
			state "closed", label: 'Open', action: gateOpen, icon: "st.Outdoor.outdoor8", backgroundColor: "#00E500"
		}         
       standardTile("close", "device.switch2", width: 3, height: 2, canChangeIcon: false) {
			state "open", label: 'Close', action: gateClose, icon: "st.Outdoor.outdoor8", backgroundColor: "#EE0000"			          
		}  
        main "open"
      details(["open", "close"])
    }
}

def parse(String description) {
    log.debug "Parsing '${description}'"
    def map = [:]
	def retResult = []
	def descMap = parseDescriptionAsMap(description)
    def msg = parseLanMessage(description)
    //log.debug "status ${msg.status}"
    //log.debug "data ${msg.data}"
}

// handle commands


def gateCmd(int command)
{
	//def userpassascii = "${CameraUser}:${CameraPassword}"
	//def userpass = "Basic " + userpassascii.encodeAsBase64().toString()
    def host = GateIP 
    def hosthex = convertIPtoHex(host)
    def porthex = convertPortToHex(GatePort)
    device.deviceNetworkId = "$hosthex:$porthex" 
    
    log.debug "The device id configured is: $device.deviceNetworkId"
    
    def headers = [:] 
    headers.put("HOST", "$host:$GatePort")
    //headers.put("Authorization", userpass)
    
    log.debug "The Header is $headers"
    
    if(command == 1){
  def path = "/axis-cgi/io/port.cgi?action=2:/"
  log.debug "path is: $path"
  try {
    def hubAction = new physicalgraph.device.HubAction(
    	method: "GET",
    	path: path,
    	headers: headers
        )
        	
   
    log.debug hubAction
    return hubAction
    
    }
    catch (Exception e) {
    	log.debug "Hit Exception $e on $hubAction"
    }
    }
    else if(command == 0)
    {
    def path = "/axis-cgi/io/port.cgi?action=2:\\"
  log.debug "path is: $path"
  try {
    def hubAction = new physicalgraph.device.HubAction(
    	method: "GET",
    	path: path,
    	headers: headers
        )
        	
   
    log.debug hubAction
    return hubAction
    
    }
    catch (Exception e) {
    	log.debug "Hit Exception $e on $hubAction"
    }
    }
  }
  
def parseDescriptionAsMap(description) {
	description.split(",").inject([:]) { map, param ->
		def nameAndValue = param.split(":")
		map += [(nameAndValue[0].trim()):nameAndValue[1].trim()]
	}
}

private String convertIPtoHex(ipAddress) { 
    String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02x', it.toInteger() ) }.join()
    log.debug "IP address entered is $ipAddress and the converted hex code is $hex"
    return hex

}

private String convertPortToHex(port) {
	String hexport = port.toString().format( '%04x', port.toInteger() )
    log.debug hexport
    return hexport
}

private Integer convertHexToInt(hex) {
	Integer.parseInt(hex,16)
}


private String convertHexToIP(hex) {
	log.debug("Convert hex to ip: $hex") 
	[convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}

private getHostAddress() {
    def parts = device.deviceNetworkId.split(":")
    log.debug device.deviceNetworkId
    def ip = convertHexToIP(parts[0])
    def port = convertHexToInt(parts[1])
    return ip + ":" + port
}


def gateOpen() {
	log.debug "Opening Gate."
    return gateCmd(1)    
}

def gateClose() {
	log.debug "Closing Gate."
    return gateCmd(0)    
}
