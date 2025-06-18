

# Inverted Index

**Final cloud computing project for single students, a.y. 2024/2025**

## Content

The inverted index is the foundational data structure used in information retrieval systems like Google Search.
Given a large collection of text files (e.g., articles, books, web pages),
the inverted index maps each detected word to the files in which it appears.
This kind of data structure is fundamental to enable quick search of files containing a specific term.


## Goal of the project

In this project, you will build a basic system constructing an inverted index from a collection of files.
Specifically, in order to get up to 4 points, you need to:

- Install Hadoop in **pseudo distributed** mode (a.k.a. single-node cluster).
For installation, the student needs to follow the tutorial at: [SingleCuster](https://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-common/SingleCluster.html).
Besides, they may find useful information on the tutorial that I shaowed in class for the fully distributed installation mode.

- Implement a solution in **Hadoop** (Java) producing an inverted index given a collection of files.
For each detected word, the inverted index should report any filename where that word is found. The produced inverted index may for example be in the following form: 
![example result.png](images/example%20result.png)

- Implement a **combiner** logic

- Use **setup()** and **cleanup()** methods, only if appropriate

- Choose **your own file collection** (e.g., articles, books, web pages).
Besides, consider **different sizes** of file collection, spanning from **few KBs to some GBs**

- Perform a **comprehensive performance evaluation**, collecting statistics on execution aspects such as execution time, memory usage, etc.

- Write a brief (maximum 4-5 pages) **report** to present the MapReduce pseudocode of the solution, description of datasets, and experimental results.

**In order to get up furthur 3 points (for a total of 7 points):**

- Implement **in-mapper combining**

- Develop (and evaluate over the different datasets and file sizes) an equivalent **Python non-parallel** solution to build the inverted index

- Increase the **number of reducers** and collect execution statistics


