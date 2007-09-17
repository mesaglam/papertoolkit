/**
	@author			Modified by Ron B. Yeh to work for ActionScript 3.0, and inclusion in the R3 PaperToolkit
	Version			1.2		Added Types and Modified API for AS3 compatibility and inclusion into R3
  
	Original Author:
	@author 		Andreas Weber, webweber@motiondraw.com
	Version 		1.1
	License 		Free for whatever. Giving credit would be nice. Sending a link to what you do with it would be nice.
					Want to make a donation?  http://www.icrc.org/

					
	http://www.motiondraw.com/md/as_samples/t/CatmullRomSpline/tween.html

	Catmull-Rom is a fast spline algorithm. 
	A spline connects a number of points ('vertices') with one curve
	
	More on Catmull-Rom splines: http://www.mvps.org/directx/articles/catmull/
	
	Alternatives:
	An ActionSpript implementation of a Parametric (Natural) Spline by Jim Armstrong: 
	http://www.2112fx.com/blog/pivot/entry.php?id=37#body
	Produces perfect, beautiful splines which connect the points in a less direct, kind of 'rounder' style than the Catmull-Rom splines
	Downside: much slower than Catmull-Rom, especially when the spline is defined by a large number of vertices

	
	Constructor:
	CatmullRomSpline(vertexPoints:Array)
	
			vertexPoints 		an Array of points (objects with x and y properties). 
								All these points will be connected through the spline
								When the first and the last point have the same coordinates, the class automatically treats the path as closed
			
			example
					import ink.CatmullRomSpline;
					var points:Array = new Array({x:20, y:50}, {x:200, y:100}, {x:50, y:20}, {x:50, y:100});
					var spline:CatmullRomSpline = new CatmullRomSpline(points);
			
	
	plotAll(g:Graphics [, approxLineLength:Number]):Void;
			Easy way to render the spline
			
			g					a Graphics instance to draw on. Make sure to call lineStyle() on the graphics before passing it to this function.
			approxLineLength	the spline is rendered by connecting a big number of points with straight lines.
								approxLineLength is the approximate length of one of these lines in pixels.
								Optional, Default is 5 (i.e. a spline with a total length of 500 will be represented by about 100 straight lines)
								The smaller this value (e.g. 1) the less 'jaggy' the spline, but also the slower as more lines are drawn.
			returns				nothing
			
			example
					import ink.CatmullRomSpline;
					var points:Array = new Array({x:20, y:50}, {x:200, y:100}, {x:50, y:20}, {x:50, y:100});
					var spline:CatmullRomSpline = new CatmullRomSpline(points);
					// create a clip to draw on
					this.createEmptyMovieClip('canvas', this.getNextHighestDepth());
					canvas._x = canvas._y = 100;
					// don't forget: first define the lineStyle
					canvas.lineStyle(3, 0);
					// the shorter the lines the nicer - but also the slower
					var approxLineLength:Number = 5;
					spline.plotAll(canvas, approxLineLength);
		
		
	getAllPoints([approxLineLength:Number]):Array
			Get points on the whole spline (from t=0 to t=1).
			Handy to plot the spline in one fell swoop
			
			approxLineLength	see above 'plotAll' for explanation 
			returns				an Array with all the points (objects with x and y properties) on the spline
			
			example:			see the plotAll method 
			
			
	getPointsInRange(fromT:Number, toT:Number, approxLineLength:Number):Array
			Get points on a segment of the spline (from t=fromT to t=toT)
			Handy to plot the spline piece by piece, e.g. to mimic a drawing hand or to apply some easing.
			Typically called in an interval.
			
			fromT				the starting t (range 0 to 1)
			toT					the ending t  (range 0 to 1, must be greater than fromT)
			approxLineLength	see above 'plotAll' for explanation 
			returns				an Array with all the points (objects with x and y properties) in the defined range of the spline
			
			example:
					import com.motiondraw.geometry.CatmullRomSpline;
					var numPoints = 15;
					
					numIntervals = 50;
					tweenIntervals = new Array();
					for(var i=0; i<=numIntervals; i++){
						// a linear tween - use any other fancy tween instead (see Robert Penner functions)
						tweenIntervals[i] = i * (1/50);
					}
					
					var points:Array = new Array({x:20, y:50}, {x:200, y:100}, {x:50, y:20}, {x:50, y:100});
					var spline:CatmullRomSpline = new CatmullRomSpline(points);
					
					this.createEmptyMovieClip('canvas', this.getNextHighestDepth());
					canvas._x = canvas._y = 0;
					canvas.lineStyle(1, 0);
					
					canvas.moveTo(points[0].x, points[0].y);
					
					count = 1;
					this.onEnterFrame = function(){
						if(count > numIntervals){delete this.onEnterFrame;}
						var fromT = tweenIntervals[count-1];
						var toT = tweenIntervals[count];
						var approxLineLength = 5;
						var points = spline.getPointsInRange(fromT, toT, approxLineLength);
						
						for ( var i=0; i<points.length; i++ ){
							canvas.lineTo(points[i].x, points[i].y);
						}
						count++;
					}
					
	getPointAtT(t:Number):Object
			get one point (object with x and y properties) at the specified t of the spline
	
	getTOfVertex(index:Number):Number
			index		index of the vertext as in the vertexPoints Array passed in to the constructor
			returns		t, a number in the range between 0 and 1
*/
package ink {
	import flash.display.Sprite;
	import flash.display.Graphics;
	
 	
	internal class CatmullRomSpline {
		private var xSamples:Array;
		private var ySamples:Array;
		private var tSamples:Array;
		private var numVertices:int = 0;

		private var lengths:Array;		// the distances between neighboring vertices
		private var totalLength:Number;	// the total length of the spline
		private var closedPath:Boolean = false;
		private const APPROX_LINE_LENGTH:Number = 5;	// default value
		private var verticesPlus:Array; // Array of Objects {x:Number, y:Number}. Like vertices, but with double start and end points
		
		
		public function CatmullRomSpline(xVals:Array, yVals:Array):void {
			xSamples = xVals;
			ySamples = yVals;			
			numVertices = xSamples.length;
			tSamples = new Array(numVertices);
			
			// trace("Num Vertices: " + numVertices);
			if (numVertices == 1) {
				// this is a bad condition, so we will duplicate the point
				xSamples.push(xSamples[0]);
				ySamples.push(ySamples[0]);
				numVertices = xSamples.length;
				trace("Bad condition encountered. Duplicated Point. Num Vertices is now: " + numVertices);
			}
			
			
			var first:int = 0;
			var last:int = numVertices - 1;
			if (xSamples[first] == xSamples[last] && ySamples[first] == ySamples[last]){
				closedPath = true;
			}
		}
		
		// here's this class' beef
		// calculates a curve between the second (P1) and the third (P2) point.
		// Returns: a point on this curve at the specified t
		// t: ranges between 0 and 1
		// PO, P1, P2, P3: points (objects with x and y properties)
		private function catmullRom(t:Number, P0:Object, P1:Object, P2:Object, P3:Object):Object{
			var t2:Number = (t*t);
			var t3:Number = t*t2;
			return {x: 0.5 *((2 * P1.x) + (-P0.x + P2.x) * t +(2*P0.x - 5*P1.x + 4*P2.x - P3.x) * t2 + (-P0.x + 3*P1.x- 3*P2.x + P3.x) * t3),
					y: 0.5 *((2 * P1.y) + (-P0.y + P2.y) * t +(2*P0.y - 5*P1.y + 4*P2.y - P3.y) * t2 + (-P0.y + 3*P1.y- 3*P2.y + P3.y) * t3)};
		}
		
		// uses lineTos, once we have subdivided enough
		public function plotAll(g:Graphics, approxLineLength:Number=APPROX_LINE_LENGTH):void{
			var p:Array = getAllPoints(approxLineLength);
			g.moveTo(p[0].x, p[0].y);
			for (var i:int = 1; i < p.length; i++) {
				// trace("Plotting");
				g.lineTo(p[i].x,p[i].y);
			}
			// trace("Plotted " + p.length + " points.");
		}
		
		private function getAllPoints(approxLineLength:Number = APPROX_LINE_LENGTH):Array{
			if (verticesPlus == null){
				// trace("Making Vertices Plus");
				makeVerticesPlus();
			}
			if (lengths == null) { getSegmentLengths(); }
			
			
			var v:Array = verticesPlus;
			var p:Array = new Array();
			var c:Number = 0;
			
			// trace("Vertices Plus: " + v);
			
			for(var i:int=0, len:int=v.length; i<len-3; i++){
				var p1:Object = v[i+1];
				var p2:Object = v[i+2];
				var pMid:Object = catmullRom(0.5, v[i], p1, p2, v[i+3]);
				
				// trace("Points P1: " + p1 + "   P2: " + p2 + "   PMID: " + pMid);
				
				// an approximate mid-point length calculation
				var dist:Number =  	Math.sqrt(Math.abs(Math.pow(pMid.x-p1.x ,2)) + 
									Math.abs(Math.pow(pMid.y - p1.y ,2))) + 
									Math.sqrt(Math.abs(Math.pow(pMid.x-p2.x ,2)) + 
									Math.abs(Math.pow(pMid.y - p2.y ,2)));
									
				// trace("Lengths: " + lengths);				
									
				lengths[i] = dist;
				var t:Number = 1/(dist/approxLineLength);
				
				// trace("Delta T: " + t);
				
				for(var j:Number=0; j<1; j+=t){
					// trace("Blending");
					p[c] = catmullRom(j, v[i], p1, p2, v[i+3]);
					p[c++].vertex = i;
				}
			}
			p[c++] = catmullRom(1, v[i-1], v[i], v[i+1], v[i+2]);
			// trace("Returning Points");
			return p;
		}
		
		// not used inside this class
		public function getPointsInRange(fromT:Number, toT:Number, approxLineLength:Number = APPROX_LINE_LENGTH):Array{
			if (lengths == null) { getSegmentLengths(); }
			
			var v:Array = verticesPlus;
			var p:Array = new Array();
			var c:Number = 0;
			
			for(var i:int=0, l1:int=v.length; i<l1-3; i++){
				var p1:Object = v[i+1];
				var p2:Object = v[i+2];
				
				if(fromT >= p1.t && fromT < p2.t){
					var dist:Number = lengths[i];
					var t:Number = 1/(dist/approxLineLength);
					var l2:Number = (toT > p2.t) ? 1 :  (toT-p1.t)/(p2.t-p1.t);
					for(var j:Number=  (fromT - p1.t)/(p2.t-p1.t); j<l2; j+=t){
						p[c] = catmullRom(j, v[i], p1, p2, v[i+3]);
						p[c++].vertex = i;
						
					}
	
					p[c] = catmullRom(l2, v[i], p1, p2, v[i+3]);
				    p[c++].vertex = i;			
					
					if(toT > p2.t){
						fromT = p2.t;
					}else if(l2 <= 1){
						break;
					}
				}
			}
			return p;
		}
		
		// not used in this class
		public function getPointAtT(t:Number):Object{
			if (lengths == null) { getSegmentLengths(); }
		
			var v:Array = verticesPlus;
			var c:Number = 0;
			
			for(var i:int=0, l1:int=v.length; i<l1-3; i++){
				var p1:Object = v[i+1];
				var p2:Object = v[i+2];
				if(t >= p1.t && t <= p2.t){
					var t2:Number = (t-p1.t)/(p2.t-p1.t);
					return catmullRom(t2, v[i], p1, p2, v[i+3]);
				}
			}
			trace("Error Condition: Returning nothing from getPointAtT()");
			return null;
		}
		
		public function getTOfVertex(index:Number):Number{
			if (lengths == null) { getSegmentLengths(); }
			return tSamples[index];
		}
		
		private function getSegmentLengths():void{
			if(verticesPlus == null){makeVerticesPlus();}
			
			var v:Array = verticesPlus;
			lengths = new Array();
			totalLength = 0;
			
			for(var i:int=0, len:int=v.length; i<len-3; i++){
				var p1:Object = v[i+1];
				var p2:Object = v[i+2];
				var pMid:Object = catmullRom(0.5, v[i], p1, p2, v[i+3]);
				
				// an _approximate_ mid-point length calculation
				var dist:Number =  Math.sqrt(Math.abs(Math.pow(pMid.x-p1.x ,2)) + 
											 Math.abs(Math.pow(pMid.y - p1.y ,2))) + 
								   Math.sqrt(Math.abs(Math.pow(pMid.x-p2.x ,2)) + 
								   			 Math.abs(Math.pow(pMid.y - p2.y ,2)));
				lengths[i] = dist;
				totalLength += dist;
			}
			
			tSamples[0] = 0;
			var sum:Number = 0;
			for(var j:int=0, lenj:int=lengths.length; j<lenj; j++){
				sum +=  lengths[j];
				tSamples[j+1] = sum /  totalLength;
			}
		}
		
		private function makeVerticesPlus():void{
			// duplicate the vertices first
			verticesPlus = new Array();
			for(var i:int = 0; i<xSamples.length; i++) {
				verticesPlus.push({x:xSamples[i], y:ySamples[i]});
			}

			// trace("Duplicating Vertices: " + verticesPlus);
			
			if(!closedPath){
				// the assumed 2 extra points at start and finish
				verticesPlus.splice(0,0,verticesPlus[0]);
				verticesPlus.push(verticesPlus[verticesPlus.length-1]);
			} else {
				verticesPlus.splice(0,0,verticesPlus[verticesPlus.length-2]);
				verticesPlus.push(verticesPlus[2]);
			}
		}
	}

}

