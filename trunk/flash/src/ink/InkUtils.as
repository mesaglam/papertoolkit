package ink {
	public class InkUtils {
		
		// converts a value string that is passed
		// handles scientific notation!
		public static function getCoordinateValueFromString(valStr:String):Number {
			var retVal:Number = 0;
			var exp:String = "";
			var expIndex:int = valStr.indexOf("E");
			// handle scientific notation
			if (expIndex > -1) {
				exp = valStr.substr(expIndex+1);
				valStr = valStr.substr(0, expIndex);
				retVal = parseFloat(valStr)*Math.pow(10, parseInt(exp)); // scientific notation
			} else {
				retVal = parseFloat(valStr);				
			}
			return retVal;			
		}
	}
}