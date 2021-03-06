import subprocess
import os
import sys
import re
import globals.constants as gc
import globals.utils as gu

class dot_graph:
	def __init__(self, title, f_name, v, maxiter, epsilon):
		self.graph_name = title
		self.verbose = v
		self.file_name = '%s.dot' % f_name
		gu.clean_file(self.file_name)
		self.fh = open(self.file_name, 'w+')
		self.fh.write('digraph {\n')
		self.fh.write('\tlabelloc=\"t\";\n')
		self.fh.write('\tlabel=\"%s\";\n' % self.graph_name) 
		self.fh.write('\toverlap=false;')
		self.fh.write('\tconcentrate=true;')
		self.fh.write('\tsplines=true;')
		self.fh.write('\tdirected=true;')
		self.fh.write('\tmaxiter=%d;' % maxiter)
		self.fh.write('\tepsilon=%f;' % epsilon)
		self.nodes = {}
		self.edges = []

	def add_node(self, class_name, skip):
		if class_name not in self.nodes:
			if not skip:
				name_match = re.match(gc.NAME_REGEX, class_name)
				if name_match:
					short_name = name_match.group(1)
					self.nodes[class_name] = short_name
					self.fh.write('\t%s [label=\"%s\"];\n' % (short_name, short_name))
				else: 
					gu.tprint('ERROR name \'%s\' is malformed. skipping' % class_name)
			else:
				name_match = re.match(gc.SKIP_NODE_ADD_REGEX, class_name)
				if name_match:
					short_name = name_match.group(1)
					self.nodes[class_name] = short_name
					self.fh.write('\t%s [label=\"%s\"];\n' % (short_name, short_name))
				else:
					gu.tprint('ERROR name \'%s\' is malformed. skipping' % class_name)

	def add_edge(self, edge):
		if edge[0] not in self.nodes.keys():
			if self.verbose:
				gu.tprint('verbose --> WARNING (from) node \'%s\' does not exist. skipping.' % edge[0])
		elif edge[1] not in self.nodes.keys():
			if self.verbose:
				gu.tprint('verbose --> WARNING (to) node \'%s\' does not exist. skipping.' % edge[1])
		else:
			line = '%s -> %s' % (self.nodes[edge[0]], self.nodes[edge[1]])
			if line not in self.edges:
				self.edges.append(line)
				self.fh.write('\t%s;\n' % line)

	def render(self):
		self.fh.write('}\n')
		self.fh.close()
		# Verbose
		if self.verbose:
			command = ['neato', self.file_name, '-v', '-Tsvg', '-o %s.svg' % self.file_name]
		else:
			command = ['neato', self.file_name, '-Tsvg', '-o %s.svg' % self.file_name]
		gu.tprint('Calling: %s' % ' '.join(command))
		sp = subprocess.Popen(command, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
		for line in iter(sp.stdout.readline, ''):
			sys.stdout.write(line)



