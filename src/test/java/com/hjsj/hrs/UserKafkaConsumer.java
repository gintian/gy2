package com.hjsj.hrs;

/**
 * function：description
 * datetime：2020-07-10 10:39
 * author：warne
 */

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.FileWriter;
import java.util.Arrays;
import java.util.Properties;

@Slf4j
public class UserKafkaConsumer extends Thread {

    /**
     * 重要提示：
     * 新系统第一次接入或者需要数据初始化时采用全量同步之外，
     * 其他情况下建议采用增量的方式进行数据同步，
     * 以便减少数据推送量与减低资源的消耗。
     */

    public static void main(String[] args) {
        Properties properties = new Properties();
        //服务器集群的ip
        properties.put("bootstrap.servers", "172.31.120.2:9092");  // 联调环境
        // properties.put("bootstrap.servers", "172.31.134.2:9092");  // sit主环境
        // properties.put("bootstrap.servers", "172.31.126.230:9092"); // uat主环境

        //properties.put("bootstrap.servers", "172.31.125.91:9092,172.31.125.92:9092,172.31.125.93:9092"); // sit长周期
        //properties.put("bootstrap.servers", "172.31.128.88:9092,172.31.128.89:9092,172.31.128.90:9092"); // uat长周期


        //properties.put("group.id", "HRS_60001900123456");//请使用各自系统系统缩写_系统ID的格式
        properties.put("group.id", "CAP_60001900");//请使用各自系统系统缩写_系统ID的格式
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("auto.offset.reset", "earliest");
        properties.put("session.timeout.ms", "30000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);
        kafkaConsumer.subscribe(Arrays.asList("hrs_complete", "hrs_increment"));
        FileWriter writer = null;
        while (true) {
            try {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(6000);
                for (ConsumerRecord<String, String> record : records) {
                    //log.info("CreateTime: {}", record.timestamp());
                    //# 控制打印获取到的数据  record.value()
                    //log.info("key={}, value={}", record.key(), record.value());

                    System.out.println("key=" + record.key());
                    System.out.println("value=" + record.value());
                    // System.out.println("记录数：" + JSONObject.fromObject(record.value()).getJSONArray("data").size());

                    writer = new FileWriter("D:\\02-worksp-bank\\HRS-DEV\\hrms-backend\\src\\test\\java\\com\\hjsj\\hrs\\" + record.key() + ".json");
                    writer.write(record.value());
                    writer.flush();
                    writer.close();


                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
