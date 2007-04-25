package tools {
	import java.JavaIntegration;
	
	public interface Tool {
        function processMessage(msgText:String):void;
        function set javaBackend(j:JavaIntegration):void;
        function showExitButton():void;
	}
}