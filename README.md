# Yelp Data Analysis with Hive and Scalding in Azure
Running hive queries, scalding jobs on hdinsight

Hdinsight
==========
HDinsight can be used for quickly starting up with Hadoop and anlysing data.

Requirements
=============
Azure Account
Internet access

Dataset
========
Yelp dataset https://www.yelp.com/dataset_challenge/
[businesses](https://hdinisght.blob.core.windows.net/data/yelp_academic_dataset_business_clean.tsv)
[reviews]()

Software
=========
[Putty (Windows only)](http://www.chiark.greenend.org.uk/~sgtatham/putty/download.html) for ssh
[InteliJ Idea](https://www.jetbrains.com/idea)
[Cyberduck](https://cyberduck.io) for uploading data to azure storage

Getting started
===============
1. Create HDinisght cluster 
Marketplace -> HDinsight

2. Login to Ambari cluster management
https://CLUSTER.azurehdinsight.net/#/main/dashboard/metrics

2. ssh to edge node
```bash
ssh \[ssh_username\]@\[cluster_name\]-ssh.azurehdinsight.net 
``` 

3. (Optional) Get dataset to cluster and upload to DFS
```
wasb:
```

3. Check you can access data on edge node
``` hadoop fs -ls ```

Normalize data
==============
Clean the data with https://github.com/vsmida/hdinsight-demo/blob/develop/scripts/convert.py

1. Extract data.
<pre>
tar -xvf yelp_phoenix_academic_dataset.tar
</pre>

<pre>
cd yelp_phoenix_academic_dataset
wget https://raw.github.com/vsmida/hdinsight-demo/blob/develop/scripts/convert.py
</pre>

<pre>
yelp_phoenix_academic_dataset$ ls
convert.py notes.txt READ_FIRST-Phoenix_Academic_Dataset_Agreement-3-11-13.pdf yelp_academic_dataset_business.json yelp_academic_dataset_checkin.json yelp_academic_dataset_review.json yelp_academic_dataset_user.json
</pre>

2. Convert it to TSV.
<pre>
chmod +x convert.py
./convert.py
</pre>

3. The column headers will be printed by the above script.
<pre>
["city", "review_count", "name", "neighborhoods", "type", "business_id", "full_address", "state", "hours", "longitude", "stars", "latitude", "attributes", "open", "categories"]
["funny", "useful", "cool", "user_id", "review_id", "text", "business_id", "stars", "date", "type"]
</pre>

Hive
=====
In your open ssh connection, type `hive` to get into Hive CLI and wait ... wait ...

##Create Table
Create the Hive tables using HQL (Hive Query Language)
```sql
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
LOCATION '/test/business';
```

###Use public blob
```
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
```
##Queries
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


Spark
======


Check latitude, longitude on google maps
https://www.google.com/maps

For inspration
http://chapeau.freevariable.com/2013/12/a-simple-machine-learning-app-with-spark.html

Before you leave
=================
Remember to **DELETE your cluster**. Otherwise you get a fat bill at the end of subscription period.  If you want to keep it running, you can scale down \# of nodes to save some resources (money). 

Disclaimer
===========
Based on https://github.com/romainr/yelp-data-analysis