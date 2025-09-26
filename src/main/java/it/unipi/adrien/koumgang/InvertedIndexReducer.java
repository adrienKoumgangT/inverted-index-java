package it.unipi.adrien.koumgang;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class InvertedIndexReducer extends Reducer<Text, Text, Text, Text> {
    private Text result = new Text();

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        Set<String> uniqueFiles = new HashSet<>();
        for (Text val : values) {
            // uniqueFiles.add(val.toString());
            String[] files = val.toString().split(" ");
            uniqueFiles.addAll(Arrays.asList(files));
        }

        StringBuilder fileList = new StringBuilder();
        for (String file : uniqueFiles) {
            fileList.append(file).append(" ");
        }

        result.set(fileList.toString().trim());
        context.write(key, result);
    }
}