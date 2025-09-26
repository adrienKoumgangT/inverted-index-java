FROM ubuntu:24.04

# Set environment variables
ENV DEBIAN_FRONTEND=noninteractive
ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-arm64
ENV HADOOP_VERSION=3.4.1
ENV HADOOP_HOME=/opt/hadoop
ENV PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
ENV HDFS_NAMENODE_USER=root
ENV HDFS_DATANODE_USER=root
ENV HDFS_SECONDARYNAMENODE_USER=root
ENV HDFS_JOURNALNODE_USER=root
ENV YARN_RESOURCEMANAGER_USER=root
ENV YARN_NODEMANAGER_USER=root
ENV HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop

# Install Java 8, dependencies and utilities
RUN apt-get update && \
    apt-get install -y sudo openssh-server openjdk-8-jdk curl wget vim nano ssh pdsh rsync net-tools iputils-ping && \
    mkdir -p /opt && \
    rm -rf /var/lib/apt/lists/*

# Configure SSH
RUN ssh-keygen -t rsa -P '' -f /root/.ssh/id_rsa && \
    cat /root/.ssh/id_rsa.pub >> /root/.ssh/authorized_keys && \
    chmod 0600 /root/.ssh/authorized_keys

# RUN service ssh start

# Download and extract Hadoop
RUN wget https://downloads.apache.org/hadoop/common/hadoop-${HADOOP_VERSION}/hadoop-${HADOOP_VERSION}.tar.gz -P /opt && \
    tar -xzf /opt/hadoop-${HADOOP_VERSION}.tar.gz -C /opt && \
    mv /opt/hadoop-${HADOOP_VERSION} $HADOOP_HOME && \
    rm /opt/hadoop-${HADOOP_VERSION}.tar.gz

# Configure Hadoop for pseudo-distributed mode
COPY hadoop-configs/* $HADOOP_HOME/etc/hadoop/

# Create data directories
RUN mkdir -p /data/hdfs/namenode && \
    mkdir -p /data/hdfs/datanode && \
    mkdir -p /opt/hadoop/logs

# Set JAVA_HOME in hadoop-env.sh
# RUN sed -i "s|^\(export JAVA_HOME=\).*|\1$JAVA_HOME|" $HADOOP_HOME/etc/hadoop/hadoop-env.sh

# Set Hadoop workers file (single-node mode)
# RUN echo "localhost" > $HADOOP_HOME/etc/hadoop/slaves
# RUN echo "localhost" > $HADOOP_HOME/etc/hadoop/workers

# Format the Namenode
RUN $HADOOP_HOME/bin/hdfs namenode -format

# Expose necessary ports
EXPOSE 19888 9870 9867 9866 9864 9000 8088 8042 8040 8033 8032 8031 8030 8020

CMD ["/bin/bash"]
