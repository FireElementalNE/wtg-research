import datetime
import time
import os
import sys
import re

def timestamp():
	ts = time.time()
	return datetime.datetime.fromtimestamp(ts).strftime('%Y-%m-%d %H:%M:%S')

def tprint(msg):
	print '%s: %s' % (timestamp(), msg)

def clean_line(line):
	tmp1 = re.sub('[\d\$]', '',line)
	tmp2 = re.sub('[\\\/]', '.', tmp1)
	return tmp2

def clean_file(f_name):
	if os.path.exists(f_name):
		if os.path.isfile(f_name):
			os.remove(f_name)
		else:
			print '%s: ERROR %s exists and is not a file. exiting.' % (timestamp(), f_name)
			sys.exit(0)