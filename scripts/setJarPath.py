import os, re, subprocess, sys, time
from optparse import OptionParser

parser = OptionParser()
parser.add_option("-b", "--base-path", dest="base_path", action="append")
parser.add_option("-s", "--separator", dest="sep", default=" ")
parser.add_option("-o", "--out-file", dest="out_file", default=False)

(opts, args) = parser.parse_args()

wout = sys.stdout
if(opts.out_file):
  wout = open(opts.out_file, "w")

dirs = opts.base_path

jar_paths = []
for dir in dirs :
  for dirpath, folders, files in os.walk(dir):
    sub_dirs = [os.path.join(dirpath, folder) for folder in folders]
    for sd in sub_dirs:
      dirs.append(sd)

    for name in files:
      if name.endswith(".jar"):
        jar_paths.append(os.path.join(dirpath, name))

if(len(jar_paths) > 0):
  wout.write(opts.sep.join(jar_paths))

