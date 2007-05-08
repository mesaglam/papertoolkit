package ink {		
	
	import flash.display.Sprite;
	import flash.display.Graphics;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.display.SpreadMethod;

	// As a display object, it can be added to an Ink object
	// As a representation of an Ink Stroke, it can be used for calcuation/clustering purposes
	public class InkStroke extends Sprite {

		// static constants
		private static const DEFAULT_COLOR:uint = 0xDADADA;
		private static const HIGHLIGHT_COLOR:uint = 0xFF99AA;

		// the samples are stored in two arrays
		private var xSamples:Array = new Array();
		private var ySamples:Array = new Array();
		
		// statistics for this stroke
		private var xMin:Number = Number.MAX_VALUE;
		private var xMax:Number = Number.MIN_VALUE;
		private var xAvg:Number = 0;

		private var yMin:Number = Number.MAX_VALUE;
		private var yMax:Number = Number.MIN_VALUE;
		private var yAvg:Number = 0;

		// the display object's graphics
		// each stroke is a Flash Sprite		
		private var g:Graphics = graphics;

		// draw with this width and color
		private var strokeWidth:Number = 1.5;
		private var color:uint = DEFAULT_COLOR;
		
		// whether this stroke should be highlighted, and rendered using the highlight color
		private var highlight:Boolean = false;

		// timestamps
		private var begin:Number;
		private var end:Number;
		
		// the last sample we received. Helps us for rendering with splines
		private var lastXVal:Number = -1;
		private var lastYVal:Number = -1;
		
		// two samples ago. It allows us to render the strokes more cleanly... using splines
		private var lastLastXVal:Number = -1;
		private var lastLastYVal:Number = -1;

		private var lastControlX:Number = -1;
		private var lastControlY:Number = -1;
		
		// constructor
		public function InkStroke(beginTS:String="0", endTS:String="0"):void {			
			//trace(beginTS + " to " + endTS);
			begin = parseInt(beginTS);
			end = parseInt(endTS);
			buttonMode = true;
		}		

		// add an ink sample...
		public function addPoint(xVal:Number, yVal:Number, f:Number):void {
			// consider modifying the color and width based on force
			g.lineStyle(strokeWidth, color);
			
			
			if (xSamples.length == 0) {
				lastXVal = xVal;
				lastYVal = yVal;
				lastLastXVal = xVal;
				lastLastYVal = yVal;
				lastControlX = xVal;
				lastControlY = yVal;
			}

			// check to see if it's a large jump
			// we'll want to subdivide and add new points (or use splines), so the lineTo looks smoother :)
			var dX:Number = Math.abs(xVal - lastXVal);
			var dY:Number = Math.abs(yVal - lastYVal);
			var dDist:Number = Math.sqrt(dX * dX + dY * dY);
			
			// if it's an UBER JUMP... then this may be the problem where anoto notebooks 
			// have noncontiguous pattern. In that case, just disregard this whole point
			if (dDist > 500) {
				// trace("InkStroke.as: Disregarding a Large Jump in Ink Samples...");
				return;
			}
			
			// trace("InkStroke.as: dX,dY from last sample: " + dX + ", " + dY);
			renderWithLines(xVal, yVal);

			// advance the last values
			lastLastXVal = lastXVal;
			lastLastYVal = lastYVal;
			lastXVal = xVal;
			lastYVal = yVal;
			
			// add this sample
			xSamples.push(xVal);
			ySamples.push(yVal);

			updateStatistics(xVal, yVal, f);
		}

		// render with either lineTos or curveTos, depending on the spacing of the samples
		private function renderWithLines(xVal:Number, yVal:Number):void {
			if (xSamples.length == 0) {
				g.moveTo(xVal, yVal);
			}
			else {
				// use simple linetos..
				g.lineTo(xVal, yVal);
			}
		}

		public function rerenderWithCurves():void {
			trace("Rerender");
			g.clear();
			var crspline:CatmullRomSpline = new CatmullRomSpline(xSamples, ySamples);
			g.lineStyle(strokeWidth, color);
			crspline.plotAll(g);
		}

		// todo: read
		// http://www.protopark.com/fish/linedrawing/
		// http://motiondraw.com/md/as_samples/t/CatmullRomSpline/tween.html
		// 
		private function renderWithCurves(xVal:Number, yVal:Number):void {
			if (xSamples.length == 0) {
				g.moveTo(xVal, yVal);
			}
			else {
				g.lineStyle(1.6, 0x995555);

				// calculate the Control Point
				
				// as a stub implementation, just choose the midpoint
				// var ctlptX:Number = (lastX + xVal)/2;
				// var ctlptY:Number = (lastY + yVal)/2;

				// We need a vector to push the control point outward
				var vX:Number = lastXVal - lastLastXVal;
				var vY:Number = lastYVal - lastLastYVal;

				// add the vector
				trace("Vectors: " + vX + ", " + vY);
				var ctlptX:Number = lastX + vX/2;
				var ctlptY:Number = lastY + vY/2;
				
				// add a control point so we can see where it is...
				renderPoint(0xAA2222, ctlptX, ctlptY);

				// add a point so we can see where it is
				renderPoint(0x2222AA, xVal, yVal);

				lastControlX = ctlptX;
				lastControlY = ctlptY;				
				g.curveTo(ctlptX, ctlptY, xVal, yVal);
			}
		}
		
		private function renderPoint(color:uint, x:Number, y:Number):void {
			var point:Sprite = new Sprite();
			point.graphics.beginFill(color, 0.5);
			point.graphics.drawCircle(0, 0, 3);
			point.x = x;
			point.y = y;
			addChild(point);
		}
		
		
		// track statistics on this ink stroke
		private function updateStatistics(xVal:Number, yVal:Number, f:Number):void {
			
			var weightForPrevSamples:Number = (numSamples-1) / numSamples;
			
			xMin = Math.min(xMin, xVal);
			xMax = Math.max(xMax, xVal);
			xAvg = (xAvg * weightForPrevSamples) + (xVal / numSamples);

			yMin = Math.min(yMin, yVal);
			yMax = Math.max(yMax, yVal);
			yAvg = (yAvg * weightForPrevSamples) + (yVal / numSamples);
		}

		///////////////// GETTERS ////////////////////////		
		public function get numSamples():Number {
			return xSamples.length;
		}
		public function get beginTimestamp():Number {
			return begin;
		}
		public function get endTimestamp():Number {
			return end;
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
		public function get lastX():Number {
			return lastXVal;
		}
		public function get lastY():Number {
			return lastYVal;
		}
		///////////////// END GETTERS ////////////////////////		
		
		
		///////////////// SETTERS ////////////////////////		
		public function set inkColor(c:uint):void {
			color = c;
		}
		public function set inkWidth(w:Number):void {
			strokeWidth = w;
		}
		// determines whether this stroke should be highlighted on screen
		public function set highlighted(value:Boolean):void {
			highlight=value;
			if (highlight) {
				color = HIGHLIGHT_COLOR;
			} else {
				color = DEFAULT_COLOR;
			}
		}
		///////////////// END SETTERS ////////////////////////		


	}
}