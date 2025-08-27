package com.wuledi.kafka.consumer;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义消费者分区器
 *
 * @author wuledi
 */
public class CustomerPartitioner implements Partitioner {

    // 下一个分区
    private final AtomicInteger nextPartition = new AtomicInteger(0);

    /**
     * 分区方法重写
     *
     * @param topic      topic名称
     * @param key        key
     * @param keyBytes   key字节数组
     * @param value      value
     * @param valueBytes value字节数组
     * @param cluster    集群信息
     * @return 分区号
     */
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic); // 获取分区信息
        int numPartitions = partitions.size(); // 获取分区数量

        // 如果key为null，使用轮询方式选择分区，见RoundRobinPartitioner
        if (key == null) {
            int next = nextPartition.getAndIncrement(); // 获取下一个分区号
            if (next >= numPartitions) { // 如果下一个分区号大于等于分区数量，则重置为0
                nextPartition.compareAndSet(next, 0);
            }
            System.out.println("分区值：" + next); // 打印分区值
            return next;
        } else { // 如果key不为null，则使用默认的分区策略，见BuiltInPartitioner
            return Utils.toPositive(Utils.murmur2(keyBytes)) % numPartitions;
        }
    }

    /**
     * 关闭方法
     */
    @Override
    public void close() {
    }

    /**
     * 配置方法
     *
     * @param configs 配置参数
     */
    @Override
    public void configure(Map<String, ?> configs) {
    }
}
