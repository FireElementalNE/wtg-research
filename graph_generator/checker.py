import os
import re
import mmap
import globals.constants as gc
import globals.utils as gu
from gen_graph import process_file as pf

def process_file_checker(in_fh):
	content = in_fh.read().split('\n')
	edges = []
	count = 0
	for line in content:
		edge_line = re.match(gc.EDGE_REGEX, line)
		if edge_line:
			to_add = [edge_line.group(1), edge_line.group(3)]
			if to_add not in edges:
				edges.append(to_add)
		count += 1
	return edges

def check_results(input_file):
	fh = open(input_file, 'r+')
	edges = process_file_checker(fh)
	fh.close()
	for edge in edges:
		edge[1] = edge[1].replace('.', os.sep)
	for edge in edges:
		filename = edge[0] + ".jimple" 
		if os.path.isfile(os.path.join(gc.DEFAULT_SOOT_OUTPUT, filename)):
			# thanks stack overflow!
			# http://stackoverflow.com/a/4944929
			f = open(os.path.join(gc.DEFAULT_SOOT_OUTPUT, filename))
			s = mmap.mmap(f.fileno(), 0, access=mmap.ACCESS_READ)
			if not s.find(edge[1]) != -1:
				gu.tprint('Edge: %s --> %s does not exist' % (edge[0], edge[1]))
				return False
			f.close()
	return True