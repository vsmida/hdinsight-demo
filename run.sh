#!/usr/bin/env bash

# params
INPUT=data/yelp_academic_dataset_business_clean.json
OUTPUT=output/out.txt

ssh sshadmin@comiit-ssh.azurehdinsight.net "hdfs dfs -rm -r ${OUTPUT}"
set -e

yarn jar clustering-1.0-SNAPSHOT-fat.jar clustering.Main --hdfs --input data/yelp_academic_dataset_business_clean.json --output output/out.txt

yarn jar clustering-1.0-SNAPSHOT-fat.jar clustering.Main --hdfs --input /user/smida/fun/inpt.tsv --output /user/smida/fun/output/out.tsv

scripts/scalding-hdfs.sh clustering.Main --input /user/smida/fun/inpt.tsv --output /user/smida/fun/output/out.tsv


####### BUILD AND UPLOAD #######
echo "BUILD: Build and uload started..."

rm -R -f derived-data-job/build/
./gradlew :derived-data-job:distTargz
tar xvvzf derived-data-job/build/distributions/derived-data-job-2.0.2-SNAPSHOT-distribution.tar.gz -C derived-data-job/build/distributions/
rsync -a -P --delete derived-data-job/build/distributions/derived-data-job smida@fenix-dedicated:.

echo "Build and uload finished."


####### RUN ON HADOOP EDGE #######
echo "RUNNING: Run on hadoop edge started..."

ssh smida@fenix-dedicated "cd derived-data-job ; ./scripts/hscld.sh com.apple.geo.neutron.derived.nameprioritization.jobs.PrioritizeNamesJob --input ${INPUT} --output ${OUTPUT} --stats ${STATS}"

echo "FINISHED: run-on-fenix-shared-all finished!"