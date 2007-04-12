Java & Serial COM Ports
Note that you should either copy win32com.dll into your JRE/bin directory, or put it in the main rundir of your project. Otherwise, an UnsatisfiedLinkError will be thrown when the PenStreamingConnection tries to load the native COM port code!

PaperToolkit.xml
This is an optional configuration file that overrides the settings in PaperToolkit/data/config/PaperToolkit.xml.
If the local file does not exist, then we use the settings in the PaperToolkit's version. Feel free to edit that version, if you want to change the defaults globally. If you want to have per-project settings, then use a local PaperToolkit.xml, placed in the "rundir" of your program.