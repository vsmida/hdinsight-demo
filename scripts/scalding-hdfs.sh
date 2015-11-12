#!/bin/bash
CURR_DIR=$(dirname $0)

export HADOOP_CLASSPATH=$HADOOP_CLASSPATH:`python $CURR_DIR/setJarPath.py -b lib/ -s :`
cls=$1
shift
hadoop com.twitter.scalding.Tool -libjars `python $CURR_DIR/setJarPath.py -b lib/ -s ,` $cls --hdfs  $@
