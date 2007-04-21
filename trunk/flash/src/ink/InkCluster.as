package ink {
	
	// helps us do calculations on the ink, etc...
	// does not allow display of the ink strokes
	// in the future, there should be a nicer way to separate the display of Ink from
	// the calculations and the data... =\
	public class InkCluster {
		
		private var strokes:Array = new Array(); // of InkStroke objects

		private var xMin:Number = Number.MAX_VALUE;
		private var yMin:Number = Number.MAX_VALUE;
		private var xMax:Number = Number.MIN_VALUE;
		private var yMax:Number = Number.MIN_VALUE;
		
		public function InkCluster():void {
			
		}
		
		public function addStroke(stroke:InkStroke):void {
			strokes.push(stroke);
			xxx
		}
	}
}