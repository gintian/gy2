package com.hjsj.hrs;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.util.Arrays;
import java.util.Properties;

/**
 * 测试 拉取kafka数据
 *
 * @author fyx
 */
public class InitHrsKafkaData extends Thread {

    private static Logger log = LoggerFactory.getLogger(InitHrsKafkaData.class);

    public static void main(String[] args) throws Exception {

//		String servers = Global.getConfig("bootstrap.servers");
//		String acks = Global.getConfig("producer.acks");
//		String retries = Global.getConfig("producer.retries");
//		String batchSize = Global.getConfig("producer.batch.size");
//		String linger = Global.getConfig("producer.linger.ms");
//		String memory = Global.getConfig("producer.buffer.memory");
//		String serializer = Global.getConfig("key.serializer");
//		
//		Properties properties1 = new Properties();
//		properties1.put("bootstrap.servers", servers);
//		properties1.put("producer.acks", acks);
//		properties1.put("producer.retries", retries);
//		properties1.put("producer.batch.size", batchSize);
//		properties1.put("producer.linger.ms", linger);
//		properties1.put("producer.buffer.memory", memory);
//		properties1.put("key.serializer", serializer);
//		properties1.put("value.serializer", serializer);
//		
//		KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(properties1);
//		kafkaProducer.send(new ProducerRecord<String, String>("organization_update", "test"));
//		System.out.println("send:test");


        Properties properties = new Properties();
        // 服务器集群的ip
        properties.put("bootstrap.servers", "172.31.120.2:9092");
//		properties.put("bootstrap.servers", "172.31.126.230:9092");
        properties.put("group.id", "CAP_60001900");// 请使用各自系统系统缩写_系统ID的格式
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("auto.offset.reset", "earliest");
        properties.put("session.timeout.ms", "30000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        FileWriter writer = null;

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<String, String>(properties);
        kafkaConsumer.subscribe(Arrays.asList("hrs_complete","hrs_increment"));

        while (true) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(6000);
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("-----------------");
                System.out.println("CreateTime:" + record.timestamp());
                // # 控制打印获取到的数据 record.value()
                //log.info("key={}, value={}", record.key(), JsonUtil.formatJson(record.value()));

                //if ("organizations_full".equalsIgnoreCase(record.key())) {
                    writer = new FileWriter("D:\\" + record.key() + ".json");
                    writer.write(record.value());
                    writer.flush();
                    writer.close();
                //}

            }
        }
    }
}
