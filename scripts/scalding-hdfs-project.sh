#!/bin/bash
CURR_DIR=$(dirname $0)
export HADOOP_CLASSPATH=$HADOOP_CLASSPATH:`python $CURR_DIR/setJarPath.py -b build/deploy/hadoop-jars/ -b build/libs/ -s :`
cls=$1
shift
hadoop com.twitter.scalding.Tool -libjars `python $CURR_DIR/setJarPath.py -b build/deploy/hadoop-jars/ -b build/libs/ -s ,` $cls --hdfs  $@
