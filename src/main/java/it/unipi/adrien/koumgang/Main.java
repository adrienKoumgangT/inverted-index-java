package it.unipi.adrien.koumgang;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.util.Locale;

public class Main {
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

        if (otherArgs.length != 3) {
            System.err.println("Usage: InvertedIndex <version> <input path> <output path>");
            System.err.println("version v1: combiner logic");
            System.err.println("version v2 (default): in-mapper combiner logic");
            System.exit(-1);
        }

        System.out.println("args[0]: <version> = "+otherArgs[0]);
        System.out.println("args[1]: <input> = "+otherArgs[1]);
        System.out.println("args[2]: <output> = "+otherArgs[2]);
        System.out.println();

        FileSystem fs = FileSystem.get(conf);

        Path inputPath = new Path(otherArgs[1]);
        Path outputPath = new Path(otherArgs[2]);

        if (!fs.exists(inputPath)) {
            System.err.println("Input path does not exist: " + inputPath);
            System.exit(-1);
        }

        if (fs.exists(outputPath)) {
            fs.delete(outputPath, true);
        }

        Job job = Job.getInstance(conf, "InvertedIndex");
        job.setJarByClass(Main.class);

        // set mapper/combiner/reducer
        if(otherArgs[0].toLowerCase(Locale.ROOT).equals("v1")) {
            job.setMapperClass(InvertedIndexMapper.class);
        } else {
            job.setMapperClass(InvertedIndexInMapper.class);
        }
        job.setCombinerClass(InvertedIndexCombiner.class);
        job.setReducerClass(InvertedIndexReducer.class);

        // define reducer's output key-value
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        // define I/O
        FileInputFormat.addInputPath(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}