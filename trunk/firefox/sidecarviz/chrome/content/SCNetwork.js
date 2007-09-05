var SCNetwork = {
	// the server socket
	serverSocket : null, // always address as this.serverSocket

	// array of streams for outputting to our clients
	clientOutputStreamArray : null,


	init: function() {
		this.clientOutputStreamArray = new Array(); /* of outputstreams to our client socket(s) */
	
	    // open a server socket
		try {
			alert("Starting Server Socket");
		
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

				var stream = transport.openInputStream(0,0,0);
				var scriptablestream = Components.classes["@mozilla.org/scriptableinputstream;1"].createInstance(Components.interfaces.nsIScriptableInputStream);
				scriptablestream.init(stream);

				var pump = Components.classes["@mozilla.org/network/input-stream-pump;1"].createInstance(Components.interfaces.nsIInputStreamPump);
				pump.init(stream, -1, -1, 0, 0, false);
				var dataListener = {
					data : "",
					onStartRequest: function(request, context) {},
					onStopRequest: function(request, context, status){
						scriptablestream.close();
						outStream.close();
					},
					onDataAvailable: function(request, context, inputStream, offset, count){
						this.data += scriptablestream.read(count);
						alert(this.data);
					},
				};
				pump.asyncRead(dataListener,null);

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
