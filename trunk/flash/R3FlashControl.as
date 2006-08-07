package {	

	import flash.events.*;
	import flash.net.*;
	
	public class R3FlashControl {
		
		private var sock:Socket;
		
		public function R3FlashControl() {
			trace("R3 Flash Control Created.");
		}
		
		public function startListening() {
			trace("R3 Started Listening");
			
			sock = new Socket();
			sock.addEventListener(ProgressEvent.SOCKET_DATA, socketDataHandler);			
			sock.connect("localhost", 6543);
			sock.writeUTFBytes("Hello");
		}

		// callback
		private function socketDataHandler(event:ProgressEvent):void {
			trace("[" + event + "]");
			var str:String = sock.readUTFBytes(sock.bytesAvailable);
			
			// where to move the main timeline to
			var destinationFrameLabel:String = str.substring(str.indexOf("[[")+2, str.indexOf("]]"));
			
			// moves the document to the frame with label == str
			trace("[" + destinationFrameLabel + "]");
			trace(destinationFrameLabel.length);
			MainController.getInstance().gotoAndPlay(destinationFrameLabel);
			MainController.getInstance().gotoAndPlay("Show Map");
			
			
		}
	}
}