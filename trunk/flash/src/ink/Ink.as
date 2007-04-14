package ink {		
	
	import flash.display.Sprite;
	import flash.display.Graphics;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.display.Stage;

	
	public class Ink extends Sprite {
	
		private var strokes:Array = new Array(); // of InkStroke objects
		
		
		private var xMin:Number = Number.MAX_VALUE;
		private var yMin:Number = Number.MAX_VALUE;
		private var xMax:Number = Number.MIN_VALUE;
		private var yMax:Number = Number.MIN_VALUE;

		private var mostRecentStroke:InkStroke = new InkStroke();

		public function setColor():void {
			// for each stroke, set the color!
			
		}

		public function getNumStrokes():Number {
			return strokes.length;
		}

		public function Ink() {			
			buttonMode = true;			

			// add drag support
			addEventListener(MouseEvent.MOUSE_DOWN, onMouseDown);
			addEventListener(MouseEvent.MOUSE_UP, onMouseUp);
		}
		
        private function onMouseDown(evt:Event):void {
            this.startDrag();
        }
        private function onMouseUp(evt:Event):void {
			this.stopDrag();
        }
		
		// add an ink stroke
		// we keep around the most recent stroke, so that we can do cool calculations, like recentering
		public function addStroke(stroke:InkStroke):void {
			//trace("Add Stroke");
			strokes.push(stroke);			
			addChild(stroke);
			
			if (mostRecentStroke != null) {
				if (Math.abs(stroke.lastX - mostRecentStroke.lastX) > 800) {
					// if it's > 800 anoto dots away from the last ink stroke, we assume it's in a different cluster (i.e., page)
					trace("New Cluster");
				}
			}
			
			mostRecentStroke = stroke;
			
			xMin = Math.min(xMin, stroke.minX);
			yMin = Math.min(yMin, stroke.minY);
			xMax = Math.max(xMax, stroke.maxX);
			yMax = Math.max(yMax, stroke.maxY);			
		}
		
        
        // move the ink so that we can see it!
        // move the origin to near the top left of the display...
        public function recenter():void {
        	x = -xMin + 30;
        	y = -yMin + 50;
        }

		
        
        // move the ink so that we can see the most recent ink strokes!
        // basically, we should recenter() first, and then find the delta
        // to the most recent stroke
        //
        // Actually, we should check to see if the most recent samples are already visible
        // if they are, we need not change the view!
        public function recenterMostRecent(stage:Stage):void {
        	if (mostRecentStroke == null) {
        		return;
        	}
        	
        	trace(stage);
        	trace(stage.stageWidth);
        	
        	// are these visible?
        	var dXFromOrigin:Number = (mostRecentStroke.lastX - xMin);
        	var dYFromOrigin:Number = (mostRecentStroke.lastY - yMin);
        	
        	var lastSampleXNotVisible:Boolean = dXFromOrigin > stage.stageWidth;
        	var lastSampleYNotVisible:Boolean = dYFromOrigin > stage.stageHeight; 
        	
        	trace("dX,dY from Origin: " + dXFromOrigin + ", " + dYFromOrigin);
        	// trace("Stage Size: " + stage.stageWidth + " " + stage.stageHeight);
        	
    		x = -xMin + 30;
    		y = -yMin + 50;

        	if (lastSampleXNotVisible) {
        		x -= (dXFromOrigin - stage.stageWidth + 60);
        	} 
        	
        	if (lastSampleYNotVisible) {
        		y -= (dYFromOrigin - stage.stageHeight + 100);
			}

        }
		
	}
}