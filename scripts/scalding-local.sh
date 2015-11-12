#!/bin/bash
CURR_DIR=$(dirname $0)

cls=$1
shift
java -cp .:`python $CURR_DIR/setJarPath.py -b lib/ -s :` com.twitter.scalding.Tool $cls --local $@
