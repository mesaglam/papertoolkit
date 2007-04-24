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

		// the average x, y value of all the samples
		
		private var xAvg:Number = 0;
		private var yAvg:Number = 0;
		private var numSamples:Number = 0;
		
		
		public function InkCluster():void {
			
		}
		
		public function addStroke(stroke:InkStroke):void {
			strokes.push(stroke);
			
			// calculate max, min, and average...
			var weightForPrevSamples:Number = numSamples / (numSamples + stroke.numSamples);
			var weightForCurrSamples:Number = stroke.numSamples / (numSamples + stroke.numSamples);
			xAvg = (xAvg * weightForPrevSamples) + (stroke.avgX * weightForCurrSamples);
			yAvg = (yAvg * weightForPrevSamples) + (stroke.avgY * weightForCurrSamples);

			xMin = Math.min(xMin, stroke.minX);
			yMin = Math.min(yMin, stroke.minY);

			xMax = Math.max(xMax, stroke.maxX);
			yMax = Math.max(yMax, stroke.maxY);

			numSamples += stroke.numSamples;
		}

		public function get minX():Number {
			return xMin;
		}
		public function get minY():Number {
			return yMin;
		}
		public function get maxX():Number {
			return xMax;
		}
		public function get maxY():Number {
			return yMax;
		}
		public function get avgX():Number {
			return xAvg;
		}
		public function get avgY():Number {
			return yAvg;
		}

	}
}