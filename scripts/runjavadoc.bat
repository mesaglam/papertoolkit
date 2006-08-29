@echo off
echo Make sure 'javadoc' is in your PATH

javadoc -d ..\docs\javadocs\ -sourcepath ..\src\ -subpackages edu.stanford.hci -doctitle "R3 Java Documentation" -footer "Copyright 2006 Stanford University" -stylesheetfile javadocstyles.css

copy inherit.gif ..\docs\javadocs\resources
pause