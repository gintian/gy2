package com.hjsj.hrms.service.syncdata.gzyh;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class JConsumer implements Runnable {

    private KafkaConsumer<String, String> consumer;

    private JConsumer() {
        Properties props = new Properties();

        props.put("bootstrap.servers", "172.31.120.2:9092");
        props.put("group.id", "hrs_complete");
        props.put("enable.auto.commit", true);
        props.put("auto.commit.interval.ms", 1000);
        props.put("session.timeout.ms", 30000);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");

        consumer = new KafkaConsumer<String, String>(props);
        consumer.subscribe(Arrays.asList("hrs_complete")); // 多个topic逗号隔开

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(1000);
            for (ConsumerRecord<String, String> record : records) {

                handleMeg(record.key(),record.value(),record.timestamp());
            }
        }
    }

    private void handleMeg(String key,String record,long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String format = sdf.format(new Date(time));
        System.out.println("time:"+format);
        System.out.println(key+"="+record);

    }

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        threadPool.execute(new JConsumer());
        threadPool.shutdown();
    }
}
