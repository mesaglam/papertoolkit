@echo off
echo Make sure 'javadoc' is in your PATH
javadoc -d ..\docs\javadocs\ -sourcepath ..\src\ -subpackages edu.stanford.hci -stylesheetfile javadocstyles.css
copy inherit.gif ..\docs\javadocs\resources
pause