var SCNetwork = {
	// the server socket
	serverSocket : null, // always address as this.serverSocket

	// array of streams for outputting to our clients
	clientOutputStreamArray : null,

	init: function() {
		this.clientOutputStreamArray = new Array(); /* of outputstreams to our client socket(s) */
	
	    // open a server socket
		try {
		    this.serverSocket = Components.classes["@mozilla.org/network/server-socket;1"].createInstance(Components.interfaces.nsIServerSocket);

			// true means only connections from this machine
			// false manes connections from anywhere in the world
		    this.serverSocket.init(54321, false, -1);
		    this.serverSocket.asyncListen(this.socketListener);
		} catch (exception) {
			alert(exception);
		}
	},

	// since javascript cannot do multiple threads, we use event listeners to handle clients
	socketListener : {
		onSocketAccepted : function(sSocket, transport) {
			try {
				var outputString = "Connected to the SideCar Firefox Plugin\n";
				var outStream = transport.openOutputStream(0,0,0);
				outStream.write(outputString, outputString.length);

				alert("Client " + SCNetwork.clientOutputStreamArray.length + " connected");

				// add it to the end of the array (grow the array automatically)
				SCNetwork.clientOutputStreamArray[SCNetwork.clientOutputStreamArray.length] = outStream;
			} catch(exception) {
				alert(exception);
			}
		},
		onStopListening : function(sSocket, status) {
			alert("Client Disconnected");
		}
	},
	
	//
	// stops the server
	// 
	stopInformationServer : function() {
		alert("Stopping Server: " + gBrowser.currentURI.spec);
	
		// close all output streams
		for (var i=0; i<this.clientOutputStreamArray.length;i++) {
			this.clientOutputStreamArray[i].stream.close();
		}
	
		if (this.serverSocket) {
			this.serverSocket.close();
		}
	},
};
