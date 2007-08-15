BatchImporter.jar is just an empty file. It tricks 
launch4j into thinking that it will wrap a real jar.
Instead, we specify a custom classpath that will 
point to the PaperToolkit bin/ directory.

Get Launch4J to recompile the exe, if you need to 
customize this for any reason. These resulting exe
should be placed in PaperToolkit/penSynch/bin/.

Point Launch4J to the BatchedDataImporter.xml to configure it...