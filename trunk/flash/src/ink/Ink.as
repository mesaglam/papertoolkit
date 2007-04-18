package ink {		
	
	import flash.display.Sprite;
	import flash.display.Graphics;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.display.Stage;
	import flash.filters.GradientBevelFilter;
	import flash.display.DisplayObject;

	
	public class Ink extends Sprite {
	
		private var strokes:Array = new Array(); // of InkStroke objects
		
		private var padding:int = 60;
		
		private var xMin:Number = Number.MAX_VALUE;
		private var yMin:Number = Number.MAX_VALUE;
		private var xMax:Number = Number.MIN_VALUE;
		private var yMax:Number = Number.MIN_VALUE;

		private var mostRecentStroke:InkStroke = new InkStroke();

		public function setColor():void {
			// for each stroke, set the color!
			
		}
		
		public function addPageDecorations():void {
			var g:Graphics = graphics;
			g.beginFill(0x202020);
			g.lineStyle(0.5, 0x444444);
			g.drawRect(xMin-padding, yMin-padding, (xMax-xMin)+(2*padding), (yMax-yMin)+(2*padding));
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
					// we scan the current clusters, to see if it is within the 800 threshold of any of the clusters
					// we choose the closest one...
					// if not, then we add a whole new cluster!
					// TODO
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
        public function resetLocation():void {
        	x = -xMin + padding;
        	y = -yMin + padding;
        }

        
		public function rescaleAndrecenter(parent:DisplayObject):void {
        	trace("Parent Size: " + parent.width + ", " + parent.height);
        	trace("Ink Size: " + width + ", " + height);
        	trace("Current Bounds of Ink: " + xMin + ", " + yMin + " --> " + xMax + ", " + yMax + " [" + 
        	      (xMax-xMin) + ", " + (yMax - yMin) + "]");
        	if (parent.width > width) {
        		var dWidth:Number = parent.width - width;
        		var dWidth_Half:Number = dWidth/2;
				trace("Width: " + dWidth + " " + dWidth_Half);
        		x = dWidth_Half - xMin;
        		trace("Recentering... setting x to: " + x);
        	}
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
        	
        	trace("Stage Size: " + stage.stageWidth + ", " + stage.stageHeight);
        	trace("Current Location of Ink: " + x + ", " + y);
        	trace("Current Bounds of Ink: " + xMin + ", " + yMin + " --> " + xMax + ", " + yMax);
			trace("Most Recent Sample: " + mostRecentStroke.lastX + ", " + mostRecentStroke.lastY);
			
			// is the most recent sample visible?
			// i.e., is x + mostRecentStroke.lastX both > 0 and < stageWidth?
			// same for height

        	
        	// are these visible?
        	var dXFromOrigin:Number = (mostRecentStroke.lastX + x);
        	var dYFromOrigin:Number = (mostRecentStroke.lastY + y);
        	
        	var lastSampleXOffStageRight:Boolean = dXFromOrigin > (stage.stageWidth - padding);
        	var lastSampleXOffStageLeft:Boolean = dXFromOrigin < padding;

        	var lastSampleYOffStageBottom:Boolean = dYFromOrigin > stage.stageHeight - padding; 
        	var lastSampleYOffStageTop:Boolean = dYFromOrigin < padding; 
        	
        	
        	// if it's off stage, then move the writing toward the center!
        	if (lastSampleXOffStageRight) {
        		x -= (dXFromOrigin - stage.stageWidth + padding) + stage.stageWidth/3;
        	} else if (lastSampleXOffStageLeft) {
        		x = -mostRecentStroke.lastX + padding + stage.stageWidth/3;
        	}
        	
        	if (lastSampleYOffStageBottom) {
        		y -= (dYFromOrigin - stage.stageHeight + padding) + stage.stageHeight/3;
        	} else if (lastSampleYOffStageTop) {
        		y = -mostRecentStroke.lastY + padding + stage.stageHeight/3;
        	}
        }
	}
}