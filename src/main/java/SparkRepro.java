import org.apache.spark.api.java.JavaSparkContext;

import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.List;

/**
 * Run this like this:
 *
 * set $MYBUCKET to a Google Cloud Storage bucket you control, for example
 * gs://mybucket/mystagingfolder/
 * set $CLUSTER to the name of the cluster you set up with Google Dataproc, for example
 * my-3-machine-spark-cluster
 *
 * gsutil cp target/spark-repro-1.0-SNAPSHOT.jar $MYBUCKET
 * gcloud beta dataproc jobs submit spark --cluster $CLUSTER --jar ${MYBUCKET}spark-repro-1.0-SNAPSHOT.jar SparkRepro
 */
public final class SparkRepro {

    public static void main(String[] args) throws Exception {
        // We're listing gcloud-java-nio as a dependency, which provides the "gs" filesystem.
        // We expect to see it listed here, so:
        // expected: "Worker 0 installed filesystem providers: file jar gs"
        // observed: "Worker 0 installed filesystem providers: file jar"
        System.out.println(listFS(0));
        JavaSparkContext ctx = new JavaSparkContext();
        List<Integer> chunks = new ArrayList<>();
        chunks.add(1);
        List<String> result = ctx.parallelize(chunks).map(index -> listFS(index)).collect();
        for (String s : result) {
            System.out.println(s);
        }
    }

    public static String listFS(Integer index) {
        String s = "Worker "+index+" installed filesystem providers:";
        for (FileSystemProvider p : FileSystemProvider.installedProviders()) {
            s += " " + p.getScheme();
        }
        return s;
    }
}
