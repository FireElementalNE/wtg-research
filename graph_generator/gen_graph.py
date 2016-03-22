import argparse
import re
import globals.constants as gc
import globals.utils as gu
from dot_graph import dot_graph

def clean_line(line):
	tmp1 = re.sub('[\d\$]', '',line)
	tmp2 = re.sub('[\\\/]', '.', tmp1)
	return tmp2

def process_file(in_fh, verbose):
	content = in_fh.read().split('\n')
	activities = []
	edges = []
	count = 0
	for line in content:
		activity_line = re.match(gc.ACTIVITY_REGEX, line)
		edge_line = re.match(gc.EDGE_REGEX, line)
		if activity_line:
			activities.append(activity_line.group(1))
		elif edge_line:
			edges.append([clean_line(edge_line.group(1)), clean_line(edge_line.group(3))])
		else:
			print '%s: WARNING line %d malformed.' % (gu.timestamp(), count)
		count += 1
	return [activities, edges]

def gen_dot_file(activities, edges, output_name):
	dot = dot_graph('My WTG', output_name)
	for activity in activities:
		dot.add_node(activity)
	for edge in edges:
		dot.add_edge(edge)
	dot.render()


def main(input_file, output_name, verbose):
	gu.verbose_print('Input File: %s' % input_file, verbose)
	in_fh = open(input_file, 'r')
	activities, edges = process_file(in_fh, verbose)
	in_fh.close()
	gen_dot_file(activities, edges, output_name)



if __name__ == '__main__':
	parser = argparse.ArgumentParser(description='Generate dot and png file for basic wtg')
	parser.add_argument('-i', '--input', help='input log file', 
		required=False,  default=gc.DEFAULT_INPUT_LOG)
	parser.add_argument('-o', '--output', 
		help='output file name, (without file extension)', 
		required=False,  default=gc.DEFAULT_OUTPUT_NAME)
	parser.add_argument('-v', '--verbose', help='verbose output', 
		required=False, action='store_true', default=gc.DEFAULT_VERBOSE)
	args = parser.parse_args()
	main(args.input, args.output, args.verbose)