package it.unipi.adrien.koumgang;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class InvertedIndexInMapper extends Mapper<LongWritable, Text, Text, Text> {
    private Map<String, Set<String>> wordToFiles;
    private String filename;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        wordToFiles = new HashMap<>();
        FileSplit fileSplit = (FileSplit) context.getInputSplit();
        filename = fileSplit.getPath().getName();
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        StringTokenizer tokenizer = new StringTokenizer(value.toString());
        while (tokenizer.hasMoreTokens()) {
            String cleaned = TextUtils.cleanToken(tokenizer.nextToken());
            if (!cleaned.isEmpty()) {
                wordToFiles.computeIfAbsent(cleaned, k -> new HashSet<>()).add(filename);
            }
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        Text word = new Text();
        Text file = new Text();
        for (Map.Entry<String, Set<String>> entry : wordToFiles.entrySet()) {
            word.set(entry.getKey());
            for (String fname : entry.getValue()) {
                file.set(fname);
                context.write(word, file);
            }
        }
    }
}
