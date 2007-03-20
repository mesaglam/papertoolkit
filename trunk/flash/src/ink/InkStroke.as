package ink {		
	
	import flash.display.Sprite;
	import flash.display.Graphics;
	import flash.events.Event;
	import flash.events.MouseEvent;

	public class InkStroke extends Sprite {

		private static var defaultColor:uint = 0xDADADA;
		private static var highlightColor:uint = 0xFF99AA;

		private var xSamples:Array = new Array();
		private var ySamples:Array = new Array();
		
		private var xMin:Number = Number.MAX_VALUE;
		private var yMin:Number = Number.MAX_VALUE;
		private var xMax:Number = Number.MIN_VALUE;
		private var yMax:Number = Number.MIN_VALUE;

		private var g:Graphics = graphics;

		private var strokeWidth:Number = 1.6;
		
		private var color:uint = defaultColor;
		
		private var highlight:Boolean = false;

		private var begin:Number;
		private var end:Number;
		
		private var lastXVal:Number = -1;
		private var lastYVal:Number = -1;
		
		private var lastLastXVal:Number = -1;
		private var lastLastYVal:Number = -1;

		
		public function InkStroke(beginTS:String="0", endTS:String="0"):void {			
			//trace(beginTS + " to " + endTS);
			begin = parseInt(beginTS);
			end = parseInt(endTS);
			buttonMode = true;
		}		

		public function set highlighted(value:Boolean):void {
			highlight=value;
			
			if (highlight) {
				color = highlightColor;
			} else {
				color = defaultColor;
			}
		}

		public function get beginTimestamp():Number {
			return begin;
		}

		public function addPoint(xVal:Number, yVal:Number, f:Number):void {
			// assume f is 0 to 128
			// later: cap it to 0 to 128
			
			// obsolete directions: =)
			// vary opacity and width based on the force
			// from about 70 to 100, treat the width the same, but vary opacity
			// from about 0 to 70, vary width
			// from about 100 to 128, vary width
			
			var modifiedStrokeWidth:Number = strokeWidth;
			var delta:Number = 50 - f;
			modifiedStrokeWidth -= delta * 0.02;
			// trace("New Stroke Width: " + modifiedStrokeWidth);
			
			// don't modify the alpha!
			// var modifiedAlpha:Number = .95;
			// modifiedAlpha -= delta * .01;
			
			g.lineStyle(modifiedStrokeWidth, color);
			if (xSamples.length == 0) {
				g.moveTo(xVal, yVal);				
				lastXVal = xVal;
				lastYVal = yVal;
				lastLastXVal = xVal;
				lastLastYVal = yVal;
			}
			else {

/*
				if ((Math.abs(xVal - lastXVal) > 10) && (Math.abs(yVal - lastYVal) > 10)) {
					trace("big hop in x and y");
						
					// curve to... with a control point computed from the last two points!
					var vX:Number = xVal - lastLastXVal;
					var vY:Number = yVal - lastLastYVal;
					
					var controlX:Number = lastXVal + vX/3;
					var controlY:Number = lastYVal + vY/3;
						
					// g.drawCircle(controlX, controlY, 2);
					// g.curveTo(controlX, controlY, xVal, yVal);
					g.curveTo(controlX, controlY, xVal, yVal);
				} else {
					// regular
					g.lineTo(xVal, yVal);
				}
*/
				// reverted to simple linetos..
				g.lineTo(xVal, yVal);
				
				// advance the last values
				lastLastXVal = lastXVal;
				lastLastYVal = lastYVal;
				lastXVal = xVal;
				lastYVal = yVal;
			}
			xSamples.push(xVal);
			ySamples.push(yVal);

			
			
			xMin = Math.min(xMin, xVal);
			xMax = Math.max(xMax, xVal);
			yMin = Math.min(yMin, yVal);
			yMax = Math.max(yMax, yVal);
			
			//trace(xVal + " " + yVal);
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
	}
}