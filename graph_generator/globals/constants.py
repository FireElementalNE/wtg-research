import os

# Default values
DEFAULT_INPUT_LOG = os.path.join('..', 'InferenceTransformer_logfile.log')
DEFAULT_OUTPUT_NAME = 'out'
DEFAULT_VERBOSE = False
DEFAULT_SOOT_OUTPUT = os.path.join('..', 'sootOutput')
DEFAULT_MAXITER = 10000
DEFAULT_EPSILON = 0.01
DEFAULT_SKIP = False

# regex
SKIP_NODE_ADD_REGEX = '^(.*)$'
SKIP_NODE_REGEX = '^(.*) --> (.*)$'
CLASS_NAME_REGEX = '([\w\.\\\/]+(\$[\d\w]+)?)'
ACTIVITY_REGEX = '^Activity: ([\w\.]+)$'
EDGE_REGEX = '^%s\s-->\s%s$' % (CLASS_NAME_REGEX, CLASS_NAME_REGEX)
NAME_REGEX = '^.*\.(\w+)$'
# TODO: clean this up
MESSY_SPLIT_TOKEN = ']: '

NOPARSE = "NOPARSE"
