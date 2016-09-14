# SparkRepro

A simple project that demonstrates the filesystem providers 
not showing up when running via Spark.


## Building


```
mvn package
```

## Running

I've been running this on Google Dataproc, 
putting the files on a Google Cloud Storage bucket.

Set `$MYBUCKET` to a Google Cloud Storage bucket you control, for example
`gs://mybucket/mystagingfolder/`.

Set `$CLUSTER` to the name of the cluster you set up with Google Dataproc, for example
`my-3-machine-spark-cluster`.

Build, then copy the jar to your bucket.

```
$ mvn package
$ gsutil cp target/spark-repro-1.0-SNAPSHOT.jar $MYBUCKET
```

Run the program.

```
$ gcloud beta dataproc jobs submit spark --cluster $CLUSTER --jar ${MYBUCKET}spark-repro-1.0-SNAPSHOT.jar SparkRepro
(...)
Worker 0 installed filesystem providers: file jar
Worker 1 installed filesystem providers: file jar 
(...)
```

## Broken expectation

We're depending on the `gcloud-java-nio` Maven artifact, which normally adds
a "gs" filesystem via Java's filesystem extension mechanism.

The "gs" filesystem shows up if we run the `listFS` function locally.
However, it is not listed when we run Spark on Dataproc (not sure about Spark
on a dedicated cluster).