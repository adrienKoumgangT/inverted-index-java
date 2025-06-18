# Inverted Index with Hadoop MapReduce

This project implements an **Inverted Index** using the Hadoop MapReduce framework. It demonstrates both a **basic version (v1)** and an **optimized version (v2)** with in-mapper combining to improve performance.

---

## 📚 Project Overview

An *inverted index* maps each word to the list of documents (and optionally, positions) in which it occurs. It is a key component in search engines and information retrieval systems.

This project:
- Processes text files from HDFS.
- Generates an index of words → files in which they appear.
- Includes two implementations:
    - **v1**: standard MapReduce with mappers and reducers
    - **v2**: optimized version using in-mapper combining to reduce shuffle cost

---

## 📁 Project Structure

```bash
inverted-index-java/
│
├── pom.xml                       # Maven project descriptor
├── run_demo.sh                   # Script to build and run
├── README.md                     # Project documentation
│
├── hadoop-configs/               # Custom Hadoop XML configs (core-site.xml, yarn-site.xml, etc.)
│
├── input/                        # Local input data directory
│   ├── test1/                    # First test dataset
│   └── test2/                    # Second test dataset
│
└── src/
    └── main/
        ├── java/
        │   └── it/unipi/adrien/koumgang/
        │       ├── Main.java                     # Entry point
        │       ├── InvertedIndexMapper.java      # v1 Mapper
        │       ├── InvertedIndexReducer.java     # Reducer
        │       ├── InvertedIndexCombiner.java    # Combiner
        │       ├── InvertedIndexInMapperV2.java  # v2 Mapper (with in-mapper combining)
        │       └── TextUtils.java                # Utility functions
        └── resources/
```

---

## Version 1: Standard MapReduce with Combiner

### Overview

This version implements a traditional Hadoop MapReduce inverted index:

- Mapper emits each word with its filename.

- A Combiner locally reduces duplicates.

- A Reducer finalizes aggregation and emits `unique word -> [files]` mapping.

### Architecture Diagram

```
Input (Text Files)
    ┌──────────────┐
    │  doc1.txt    │  "Cloud computing is powerful."
    └──────────────┘
          │
          ▼
      [Mapper]
     (cleanToken)
  "cloud" → doc1.txt
  "computing" → doc1.txt
          │
          ▼
     [Combiner]
   (deduplicate locally)
          │
          ▼
     [Shuffle & Sort]
     "cloud" → [doc1.txt, doc2.txt]
          │
          ▼
      [Reducer]
 "cloud" → doc1.txt doc2.txt
          │
          ▼
       Output
```

### Mapper Behavior

Each input line is tokenized. For each token:
- It is cleaned using `TextUtils.cleanToken()` (lowercase, remove punctuation, strip accents).
- The mapper emits: `<word, filename>`.

### Combiner Behavior

Eliminates duplicates per mapper. Receives all `<word, filename>` pairs and emits:
- `<word, [unique filenames]>`

### Reducer Behavior

Aggregates values for each word key across all input splits:
- Deduplicates filenames.
- Emits: `<word, doc1 doc2 doc3>`

### Pros

- Simpler to understand
- Native Hadoop pattern

### Cons

- Emits duplicate (word, file) pairs.
- Higher network and shuffle cost

### Example

We have this 3 files:

- doc1.txt:
```
Cloud computing is the future of technology.
Cloud services are widely adopted.
```

- doc2.txt:
```
The future of computing is in the cloud.
Many companies invest in cloud computing.
```

- doc3.txt
```
Technology evolves rapidly with new computing paradigms.
Cloud-native applications are becoming standard.
```

Example output:
```
adopted doc1.txt
applications    doc3.txt
are     doc3.txt doc1.txt
becoming        doc3.txt
cloud   doc1.txt doc2.txt
cloudnative     doc3.txt
companies       doc2.txt
computing       doc3.txt doc1.txt doc2.txt
evolves doc3.txt
future  doc1.txt doc2.txt
in      doc2.txt
invest  doc2.txt
is      doc1.txt doc2.txt
many    doc2.txt
new     doc3.txt
of      doc1.txt doc2.txt
paradigms       doc3.txt
rapidly doc3.txt
services        doc1.txt
standard        doc3.txt
technology      doc3.txt doc1.txt
the     doc1.txt doc2.txt
widely  doc1.txt
with    doc3.txt
```

---


## Version 2: Optimized In-Mapper Combining

### Overview

This version applies an optimization by **aggregating within the Mapper**.

- Instead of emitting on every token, it builds a `Map<String, Set<String>>` in memory.

- Emits only unique `<word, filename>` pairs at the end of `cleanup()`.

### Architecture Diagram

```
Input (Text Files)
    ┌──────────────┐
    │  doc2.txt    │  "Cloud is everywhere."
    └──────────────┘
          │
          ▼
    [In-Mapper Combining]
  Builds: {
    "cloud": [doc2.txt],
    "is": [doc2.txt],
    "everywhere": [doc2.txt]
  }
          │
          ▼
     [Shuffle & Sort]
     "cloud" → [doc1.txt, doc2.txt]
          │
          ▼
      [Reducer]
 "cloud" → doc1.txt doc2.txt
          │
          ▼
       Output
```

### Mapper Behavior (Optimized)

- During `map()`, all cleaned tokens are stored in an in-memory `HashMap<word, Set<filenames>>`.

- In `cleanup()`, emits only unique pairs.

### Reducer Behavior

Same as v1:
- Collects `<word, list_of_files>.

### Pros

- Greatly reduces emitted records.
- Lower shuffle I/O and network usage.
- Faster job execution for large data with repeated words.

### Cons

- Slightly increased memory usage per Mapper.
- Not suitable if dataset doesn't fit in mapper memory.


---

## Hadoop: Setting up a Single Node Cluster

For installation, follow the tutorial at: [Single Node Cluster](https://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-common/SingleCluster.html)

---

## How to Run


