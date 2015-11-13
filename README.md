# Yelp Data Analysis with Hive and Scalding in Azure
Running hive queries, scalding jobs on [hdinsight](https://azure.microsoft.com/en-us/services/hdinsight/)
HDinsight can be used for quickly starting up with Hadoop and anlysing data.

Requirements
=============
* Azure Account
* Internet access

Dataset
========
* Yelp dataset https://www.yelp.com/dataset_challenge/
* [businesses table](https://hdinisght.blob.core.windows.net/data/business.tsv)
* reviews table

Software
=========
* [Putty (Windows only)](http://www.chiark.greenend.org.uk/~sgtatham/putty/download.html) for ssh
* [InteliJ Idea](https://www.jetbrains.com/idea)
* [Cyberduck](https://cyberduck.io) for uploading data to azure storage

Getting started
===============
1. Create HDinisght cluster 
[Marketplace -> HDinsight](https://azure.microsoft.com/en-us/documentation/articles/hdinsight-hadoop-linux-tutorial-get-started/)


2. Login to Ambari cluster management
https://CLUSTER.azurehdinsight.net/#/main/dashboard/metrics

2. ssh to edge node
```bash
ssh [ssh_username]@[cluster_name]-ssh.azurehdinsight.net 
```

\[UPDATE: Skip this step for now\] Normalize data
==============
Get Yelp dataset https://www.yelp.com/dataset_challenge/
Clean the data with https://github.com/vsmida/hdinsight-demo/blob/develop/scripts/convert.py

1. Extract data.
```
tar -xvf yelp_phoenix_academic_dataset.tar
cd yelp_phoenix_academic_dataset
wget https://raw.github.com/vsmida/hdinsight-demo/blob/develop/scripts/convert.py
yelp_phoenix_academic_dataset$ ls
convert.py notes.txt READ_FIRST-Phoenix_Academic_Dataset_Agreement-3-11-13.pdf yelp_academic_dataset_business.json yelp_academic_dataset_checkin.json yelp_academic_dataset_review.json yelp_academic_dataset_user.json
```

2. Convert it to TSV.
```
chmod +x convert.py
./convert.py
```

3. The column headers will be printed by the above script.
<pre>["city", "review_count", "name", "neighborhoods", "type", "business_id", "full_address", "state", "hours", "longitude", "stars", "latitude", "attributes", "open", "categories"]
["funny", "useful", "cool", "user_id", "review_id", "text", "business_id", "stars", "date", "type"]</pre>

Upload dataset to cluster
==========================
1. Get dataset to cluster and upload to DFS
In your ssh session on edge node, download dataset to local filesystem
<pre>
wget http://comiithdinsight.blob.core.windows.net/public/business.tsv
ls
</pre>

2. Upload your data to DFS, in our case we have 2 filesystems mounted on our edge: local HDFS and "remote" Windows Azure Storage Blob (wasb). We will upload to wasb:
 <pre>
 hadoop fs -ls
 hadoop fs -ls /
 hadoop fs -mkdir /data
 hadoop fs -copyFromLocal ./business.tsv / 
</pre>

3. Check you can access data on edge node
``` hadoop fs -ls /data```
or by using any third-party Azure storage explorer, e.g. [Cyberduck](https://cyberduck.io)

Hive
=====
In your open ssh connection, type `hive` to get into Hive CLI and wait ... wait ...

##Create Tables
Create the Hive tables using HQL (Hive Query Language)

###Business
1. Create table business
<pre>
CREATE EXTERNAL TABLE business (
city string,
review_count int, 
name string,
neighborhoods string,
type string,
business_id string,
full_address string,
hours string,
state string,
longitude float,
stars float,
latitude float,
attributes string,
open boolean,
categories string
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE
LOCATION '/data';
</pre>

2. Validate table columns, types *(by default, hive won't give you warning)*
<pre>
SELECT *
FROM business
LIMIT BY 15
</pre>

###reviews - use public blob

1. Create table reviews
<pre>
CREATE EXTERNAL TABLE review (
funny int, 
useful int,
cool int,
user_id string,
review_id string,
text string,
business_id string, 
stars int, 
date string,
type string
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE
LOCATION 'wasb://data@hdinisght.blob.core.windows.net/';
</pre>

2. Validate table columns, types *(by default, hive won't give you warning)*
<pre>
SELECT *
FROM review
LIMIT BY 15
</pre>

##Explore data
Open up Hue's Hive editor named Beeswax and run:

1. **Top 25: business with most of the reviews**
<pre>
SELECT name, review_count
FROM business
ORDER BY review_count DESC
LIMIT 25
</pre>

2. **Top 25: coolest restaurants**
<pre>
SELECT name, full_address, SUM(cool) AS coolness
FROM review r JOIN business b
ON (r.business_id = b.business_id)
WHERE categories LIKE '%Restaurants%'
GROUP BY r.business_id, name
ORDER BY coolness DESC
LIMIT 25;
</pre>

3. Where are they?
<pre>
SELECT b1.name, b1.full_address, b2.coolness
FROM business b1
JOIN(
SELECT r.business_id, SUM(cool) AS coolness
FROM review r JOIN business b
ON (r.business_id = b.business_id)
WHERE categories LIKE '%Restaurants%'
GROUP BY r.business_id
ORDER BY coolness DESC
LIMIT 25
) b2 ON b1.business_id = b2.business_id
ORDER BY coolness DESC;
</pre>

Let your imagination run wild!


Mapreduce jobs with Scalding
======
0. *(Optional)*
Check latitude, longitude on google maps
https://www.google.com/maps

1. Create java project in IntelliJ Idea

2. Configure gradle \& dependencies

3. Create dummy program to filter fields

4. Build and package to fatJar
 
5. Run on cluster

6. Run locally

For inspration
http://chapeau.freevariable.com/2013/12/a-simple-machine-learning-app-with-spark.html

Before you leave
=================
Remember to **DELETE your cluster**. Otherwise you get a fat bill at the end of subscription period.  If you want to keep it running, you can scale down \# of nodes to save some resources (money). 

Disclaimer
===========
Based on https://github.com/romainr/yelp-data-analysis