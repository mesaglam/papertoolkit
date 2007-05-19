USAGE:

  tiled_page_generator.py [output file] [pattern directory] \ 
       [page width (points)] [page height (points)] \
       [page border (points)] [starting pattern]

NOTES:

  output file: the filename to write postscript to
  pattern directory: the path to the directory containing "#.pattern" files
  page width/height: the size of anoto pattern to generate (in points).  Does not include border
  page border: the width of the border (in points)
  starting pattern: the index of the pattern to start with
  
  
AUTHORS:
  Joel Brandt 	(wrote the original version based on PS files created by Ron Yeh) 
  Ron Yeh 		(modified the script file, and included its functionality into the Sheet Renderer of the R3 Toolkit)