BatchImporter.jar is just an empty file. It tricks 
launch4j into thinking that it will wrap a real jar.
Instead, we specify a custom classpath that will point
to the r3 bin/ directory.