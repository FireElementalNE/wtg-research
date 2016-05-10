#!/usr/bin/env python2
import argparse
import re
import globals.constants as gc
import globals.utils as gu
import checker
from dot_graph import dot_graph

def process_file(in_fh, skip):
	content = in_fh.read().split('\n')
	activities = []
	edges = []
	count = 0
	for line in content:
		# TODO: fix this, it is messy
		if(len(line) < 1):
			continue
		# TODO: fix this, it is messy
		line = line.split(gc.MESSY_SPLIT_TOKEN)[1]
		if not skip and len(line) > 1:
			activity_line = re.match(gc.ACTIVITY_REGEX, line)
			edge_line = re.match(gc.EDGE_REGEX, line)
			if activity_line:
				activities.append(activity_line.group(1))
			elif edge_line:
				edges.append([gu.clean_line(edge_line.group(1)), gu.clean_line(edge_line.group(3))])
			else:
				if not line.startswith('Skipped:'):
					gu.tprint('WARNING line %d malformed. %s' % (count, line))
		else:
			if '-->' not in line:
				activities.append(line)
			else:
				edge_line_skip = re.match(gc.SKIP_NODE_REGEX, line) 
				if edge_line_skip:
					edges.append([edge_line_skip.group(1), edge_line_skip.group(2)])
				else:
					gu.tprint('WARNING line %d malformed. %s' % (count, line))
		count += 1
	return [activities, edges]

def gen_dot_file(nodes, edges, output_name, verbose, skip, maxiter, epsilon):
	dot = dot_graph('My WTG', output_name, verbose, maxiter, epsilon)
	for node in nodes:
		dot.add_node(node, skip)
	for edge in edges:
		dot.add_edge(edge)
	dot.render()

def main(input_file, output_name, verbose, skip, maxiter, epsilon):
	if skip:
		in_fh = open(input_file, 'r')
		nodes, edges = process_file(in_fh, skip)
		in_fh.close()
		gen_dot_file(nodes, edges, output_name, verbose, skip, maxiter, epsilon)
	elif checker.check_results(input_file):
		gu.tprint('Checker Passed!')
		gu.tprint('Input File: %s' % input_file)
		in_fh = open(input_file, 'r')
		activities, edges = process_file(in_fh, skip)
		in_fh.close()
		gen_dot_file(activities, edges, output_name, verbose, skip, maxiter, epsilon)
	else:
		gu.tprint('Checker Failed.')


if __name__ == '__main__':
	parser = argparse.ArgumentParser(description='Generate dot and png file for basic wtg')
	parser.add_argument('-s', '--skip', action='store_true', 
		help='Ignore complex regex, just make connections', required=False, default=gc.DEFAULT_SKIP)
	parser.add_argument('-i', '--input', help='input log file', 
		required=False,  default=gc.DEFAULT_INPUT_LOG)
	parser.add_argument('-o', '--output', 
		help='output file name, (without file extension)', 
		required=False,  default=gc.DEFAULT_OUTPUT_NAME)
	parser.add_argument('-v', '--verbose', help='verbose output', 
		required=False, action='store_true', default=gc.DEFAULT_VERBOSE)
	parser.add_argument('-m', '--maxiter', type=int, required=False,
		help='The maxiter arg to neato', default=gc.DEFAULT_MAXITER)
	parser.add_argument('-e', '--epsilon', type=float, required=False,
		help='The epsilon arg to neato', default=gc.DEFAULT_EPSILON)
	args = parser.parse_args()
	main(args.input, args.output, args.verbose, args.skip, args.maxiter, args.epsilon)
