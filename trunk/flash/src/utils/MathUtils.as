package utils {
	public class MathUtils {
		public static function distance(x1:Number, y1:Number, x2:Number, y2:Number):Number {
			var dX:Number = Math.abs(x1 - x2);
			var dY:Number = Math.abs(y1 - y2);
			return Math.sqrt(dX * dX + dY * dY);
		}
	}
}