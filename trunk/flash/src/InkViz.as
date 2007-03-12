package {		
	import ink.Ink;
	import ink.InkXMLParser;

	import flash.display.Sprite;
	import flash.display.LoaderInfo;
	import flash.events.Event;
	
	public class InkViz extends Sprite {
		
		private var path:String;

		private var pathParent:String = "../../penSynch/data/XML/";
		//private var pathParent:String = "../data/";

		public function InkViz() {			
			
			//trace("InkViz Constructor");
			//trace(path1);

			try {
				var keyStr:String;
				var valueStr:String;
				var paramObj:Object = LoaderInfo(this.root.loaderInfo).parameters;
				for (keyStr in paramObj) {
					valueStr = String(paramObj[keyStr]);
					//trace(keyStr + ":\t" + valueStr);
					
					if (keyStr == "fileName") {
						trace("File: " + valueStr);
						path = pathParent + valueStr;
					}
				}
			} catch (error:Error) {
				trace(error);
			}

			
			if (path==null) {
				trace("Path is null. Not loading any XML today!");
				return;
			}
			
			var inkXML:InkXMLParser = new InkXMLParser(path);
			this.addChild(inkXML.ink);
		}
		
	}
}