import os
# Default values
DEFAULT_INPUT_LOG = os.path.join('..', 'inference_transformer_out.log')
DEFAULT_OUTPUT_NAME = 'out'
DEFAULT_VERBOSE = True

# regex
ACTIVITY_REGEX = '^Activity: ([\w\.]+)$'
EDGE_REGEX = '([\w\.\\\/]+(\$\d+)?)\s-->\s([\w\.\\\/]+(\$\d+)?)'
NAME_REGEX = '^.*\.(\w+)$'