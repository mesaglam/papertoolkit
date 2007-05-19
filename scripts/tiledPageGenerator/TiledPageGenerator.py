#! /usr/bin/python

import sys
import math

# constants

WIDTH_INCHES = 8.5
HEIGHT_INCHES = 11
MARGIN_INCHES = 0

OUTPUT_FILE = "output_big.ps"
PATTERN_DIRECTORY = '../../data/pattern/default/'
PAGE_WIDTH = WIDTH_INCHES*72
PAGE_HEIGHT = HEIGHT_INCHES*72
PAGE_BORDER = MARGIN_INCHES * 72
PATTERN_START = 0

GRID_SIZE = .3 * 0.987778
LINES_PER_PATTERN = 932
DOTS_PER_LINE = 721

# helper functions

def points_to_mm(p):
    return p/2.8346456693

def mm_to_points(m):
    return m*2.8346456693

# parse command line args

if len(sys.argv) >= 2:
    OUTPUT_FILE = sys.argv[1]

if len(sys.argv) >= 3:
    PATTERN_DIRECTORY = sys.argv[2]
    if PATTERN_DIRECTORY[len(PATTERN_DIRECTORY)-1] != '/':
	PATTERN_DIRECTORY += '/'

if len(sys.argv) >= 4:
    PAGE_WIDTH = int(sys.argv[3])

if len(sys.argv) >= 5:
    PAGE_HEIGHT = int(sys.argv[4])

if len(sys.argv) >= 6:
    PAGE_BORDER = int(sys.argv[5])

if len(sys.argv) >= 7:
    PATTERN_START = int(sys.argv[6])


# output header

num_lines = int(PAGE_HEIGHT/mm_to_points(GRID_SIZE))
line_length = int(PAGE_WIDTH/mm_to_points(GRID_SIZE))

pages_per_row = line_length / DOTS_PER_LINE
if line_length % DOTS_PER_LINE > 0:
    pages_per_row += 1

print "PAGE_HEIGHT: " + str(PAGE_HEIGHT)
print "PAGE_WIDTH: " + str(PAGE_WIDTH)
print "PAGE_BORDER: " + str(PAGE_BORDER)
print "num_lines: " + str(num_lines)
print "line_length: " + str(line_length)
print "pages_per_row: " + str(pages_per_row)

current_pattern = PATTERN_START
lines_remaining = num_lines

f_out = open(OUTPUT_FILE, 'w')

# write the header

f_out.write("%!PS-Adobe-3.0\n")

f_out.write("<< /PageSize [" + str(PAGE_WIDTH+PAGE_BORDER*2) + " " + str(PAGE_HEIGHT+PAGE_BORDER*2) + "]")
f_out.write('''
   /ProcessColorModel (DeviceCMYK)
>> setpagedevice
''')

# add the font

f_font = open('AnotoFont.font', 'r')
for line in f_font:
    f_out.write(line)

# write pre-dot stuff

f_out.write('''
% BEGIN Anoto pattern
0 0 0 1 setcmykcolor % set color to black
true setoverprint % make sure we print over everything else
/AnotoFont findfont setfont % open up the font
0.987778 2.8346456692913385826771653543307 mul dup scale % scale factor for anoto font

% set location for pattern to start
''')

f_out.write(str(PAGE_BORDER) + " 0.987778 2.8346456692913385826771653543307 mul div % first item is starting x in points\n")
f_out.write(str(PAGE_HEIGHT+PAGE_BORDER) + " 0.987778 2.8346456692913385826771653543307 mul div % first item is starting y in points\n")

# write the anoto dots

while (lines_remaining > 0):
    current_lines = []
    lines_this_time = min(lines_remaining, LINES_PER_PATTERN)

    # initialize the array
    for i in range(0,lines_this_time):
	current_lines.append("")

    for j in range(0,pages_per_row):
	filename = PATTERN_DIRECTORY + str(current_pattern) + ".pattern"
	print "Reading " + filename + "..."
	page = open(filename, 'r')
	for i in range(0,lines_this_time):
	    current_lines[i] += page.readline().strip()
	page.close()
	current_pattern += 1

    # write out the lines
    for line in current_lines:
	f_out.write("(" + line[0:line_length] + ") n\n")

    lines_remaining -= lines_this_time

# write post-dot stuff

f_out.write('''
pop pop % removes starting coordinates

% END Anoto Pattern

showpage  % We're done... eject the page
''')

f_out.close()
