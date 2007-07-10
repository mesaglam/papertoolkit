To test the registration of batched monitors, move any of these apps up one directory, to the RegisteredBatchdMonitors/ directory.

All (exe, bat, jar) apps in that directory will be invoked with a single argument (the path pointing to the most-recently synchronized pen XML file).

A *.monitor file is a config file that can point to an Eclipse project. PaperToolkit will parse this file and launch your project programmatically.