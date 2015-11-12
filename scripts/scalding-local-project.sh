#!/bin/bash
CURR_DIR=$(dirname $0)

cls=$1
shift
java -cp .:`python $CURR_DIR/setJarPath.py -b build/deploy/hadoop-jars/ -b build/libs/ -s :` com.twitter.scalding.Tool $cls --local $@
