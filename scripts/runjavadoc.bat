@echo off
echo Make sure 'javadoc' is in your PATH
javadoc -d ..\docs\javadocs\ -sourcepath ..\src\ -subpackages edu.stanford.hci
pause