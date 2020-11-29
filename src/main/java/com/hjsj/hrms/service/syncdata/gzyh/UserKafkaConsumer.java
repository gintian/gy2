package com.hjsj.hrms.service.syncdata.gzyh;

import kafka.admin.AdminUtils;
import kafka.admin.TopicCommand;
import kafka.server.ConfigType;
import kafka.utils.ZkUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.security.JaasUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

public class UserKafkaConsumer extends Thread {

    public static void main(String[] args) {

//        createTopic();
//        listAllTopic("172.31.120.2:2181");
        KafkaConsumerTest();
//        deleteTopic("topicnamehaha");
    }

    /**
     * 模拟消费者获取topic同步数据
     */
    private static void KafkaConsumerTest() {
        System.out.println("--------------------消费者客户端测试--------------------------");
        Properties properties = new Properties();
        //服务器集群的ip
        //liantiao:172.31.120.2:9092   sit:172.31.134.2:9092  uat:172.31.126.230:9092
        properties.put("bootstrap.servers", "172.31.134.2:9092");
        properties.put("group.id", "HRS_7000610"); //填各自系统简称
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("auto.offset.reset", "earliest");
        properties.put("session.timeout.ms", "30000");
        //properties.put("max.partition.fetch.bytes", "20000000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);
        kafkaConsumer.subscribe(Arrays.asList("hrs_complete"));
        while (true) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(1000);
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("-----------------");
                System.out.println("CreateTime:" + record.timestamp());
                //# 控制打印获取到的数据  record.value()
                System.out.println("offset = " + record.offset() + ", key = " + record.key() + ", value = " + record.value());
            }
            System.out.println("-----------------结束-----------------");
        }
    }

    /*
    创建主题
    kafka-topics.sh --zookeeper localhost:2181 --create
    --topic kafka-action --replication-factor 2 --partitions 3
     */
    public static void createTopic() {

        ZkUtils zkUtils = null;
        try {
            zkUtils = ZkUtils.apply("172.31.120.2:2181", 30000, 30000, JaasUtils.isZkSecurityEnabled());
            if (!AdminUtils.topicExists(zkUtils, "topicnamehaha")) {
                AdminUtils.createTopic(zkUtils, "topicnamehaha", 1, 1, new Properties(), AdminUtils.createTopic$default$6());
                System.out.println("messages:successful create!");
            } else {
                System.out.println("topicnamehaha is exits!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (zkUtils != null) {
                zkUtils.close();
            }
        }
    }

    /**
     * 判断某个topic是否存在
     *
     * @param topicName
     * @return
     */
    public static boolean topicExists(String topicName) {
        ZkUtils zkUtils = ZkUtils.apply("172.31.120.2:2181", 30000, 30000, JaasUtils.isZkSecurityEnabled());
        boolean exists = AdminUtils.topicExists(zkUtils, topicName);
        return exists;
    }

    /**
     * 创建主题（采用TopicCommand的方式）
     *
     * @param config String s = "--zookeeper localhost:2181 --create --topic kafka-action " +
     *               "  --partitions 3 --replication-factor 1" +
     *               "  --if-not-exists --config max.message.bytes=204800 --config flush.messages=2";
     *               执行：TopicsController.createTopic(s);
     */
    public static void createTopic(String config) {
        String[] args = config.split(" ");
        System.out.println(Arrays.toString(args));
        TopicCommand.main(args);
    }


    /**
     * 修改主题配置
     * kafka-config --zookeeper localhost:2181 --entity-type topics --entity-name kafka-action
     * --alter --add-config max.message.bytes=202480 --alter --delete-config flush.messages
     *
     * @param topicName
     * @param properties
     */
    public static void alterTopicConfig(String topicName, Properties properties) {
        ZkUtils zkUtils = null;
        try {
            zkUtils = ZkUtils.apply("localhost:2181", 30000, 30000, JaasUtils.isZkSecurityEnabled());
            //先取得原始的参数，然后添加新的参数同时去除需要去除的参数
            Properties oldproperties = AdminUtils.fetchEntityConfig(zkUtils, ConfigType.Topic(), topicName);
            properties.putAll(new HashMap<>(oldproperties));
            properties.remove("max.message.bytes");
            AdminUtils.changeTopicConfig(zkUtils, topicName, properties);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (zkUtils != null) {
                zkUtils.close();
            }
        }
    }

    /**
     * 删除某主题
     * kafka-topics.sh --zookeeper localhost:2181 --topic kafka-action --delete
     *
     * @param topic
     */
    public static void deleteTopic(String topic) {
        ZkUtils zkUtils = null;
        try {
            zkUtils = ZkUtils.apply("172.31.120.2:2181", 30000, 30000, JaasUtils.isZkSecurityEnabled());
            AdminUtils.deleteTopic(zkUtils, topic);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (zkUtils != null) {
                zkUtils.close();
            }
        }
    }


}
