import os

# Default values
DEFAULT_INPUT_LOG = os.path.join('..', 'inference_transformer_out.log')
DEFAULT_OUTPUT_NAME = 'out'
DEFAULT_VERBOSE = False
DEFAULT_SOOT_OUTPUT = os.path.join('..', 'sootOutput')

# regex
CLASS_NAME_REGEX = '([\w\.\\\/]+(\$\d+)?)'
ACTIVITY_REGEX = '^Activity: ([\w\.]+)$'
EDGE_REGEX = '^%s\s-->\s%s$' % (CLASS_NAME_REGEX, CLASS_NAME_REGEX)
NAME_REGEX = '^.*\.(\w+)$'