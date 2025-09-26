#!/bin/bash

# === CONFIGURATION ===
PROJECT_NAME="inverted-index-java"
JAR_NAME="inverted-index-java-1.0.jar"
MAIN_CLASS="it.unipi.adrien.koumgang.invertedindex.Main"

INPUT_LOCAL_DIR="input"
OUTPUT_LOCAL_DIR="output"
INPUT_HDFS_DIR="/Users/adrienkoumgangtegantchouang/IdeaProjects/inverted-index-java/input"
OUTPUT_HDFS_DIR="/Users/adrienkoumgangtegantchouang/IdeaProjects/inverted-index-java/output"

# === BUILD WITH MAVEN ===
echo "Building project with Maven..."
mvn clean package
echo ""

if [ $? -ne 0 ]; then
  echo "Build failed."
  exit 1
fi

# === PREPARE HDFS ===
echo "Creating input directory in HDFS..."
hdfs dfs -rm -r "$INPUT_HDFS_DIR" > /dev/null 2>&1
hdfs dfs -mkdir -p "$INPUT_HDFS_DIR"
hdfs dfs -put -f "$INPUT_LOCAL_DIR"/* "$INPUT_HDFS_DIR"
echo ""

# === CLEAN OUTPUT DIR ===
echo "Cleaning old output directory in HDFS..."
hdfs dfs -rm -r "$OUTPUT_HDFS_DIR" > /dev/null 2>&1
echo ""

# === RUN THE JOB ===
echo "Running MapReduce Job..."
hadoop jar "target/$JAR_NAME" "$INPUT_HDFS_DIR" "$OUTPUT_HDFS_DIR"
echo ""

if [ $? -ne 0 ]; then
  echo "MapReduce job failed."
  exit 1
fi

# === FETCH THE RESULTS ===
echo "Job completed. Output:"
hdfs dfs -cat "$OUTPUT_HDFS_DIR/part-r-00000"
echo ""

# === SAVE TO LOCAL FILE ===
mkdir -p "$OUTPUT_LOCAL_DIR"
hdfs dfs -get "$OUTPUT_HDFS_DIR/part-r-00000" "$OUTPUT_LOCAL_DIR/result.txt"
echo ""

echo "Output saved to $OUTPUT_LOCAL_DIR/result.txt"
