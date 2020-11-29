package com.hjsj.hrms.bankgz.utils;

import com.hrms.struts.constant.SystemConfig;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class HrsProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(HrsProducer.class);

    public static void sendToKafka(String topic, String message) {
        Long start = System.currentTimeMillis();
        LOGGER.debug("向kafka发送的topic {} | 向kafka发送的消息{}", topic, message);
        sendKafkaMsg(topic, message);
        Long end = System.currentTimeMillis();
        LOGGER.debug("向kafka发送信息结束，耗时{}", end - start);
    }

    /**
     * 消息发布到kafka
     *
     * @param topic
     * @param message
     * @throws InterruptedException
     */
    private static void sendKafkaMsg(String topic, String message) {
        String bootstrapServers = SystemConfig.getPropertyValue("bootStrapServers");
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);//kafka地址，多个地址用逗号分割
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        p.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 20000000);
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(p);
        try {
            ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, "", message);
            kafkaProducer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception != null) {
                        // 发送异常 do something
                        LOGGER.error("sendKafkaMsg:调用kafka登记接口出错!ErrorMessage:{},bootstrapServers:{}", exception, bootstrapServers);
                    }
                }
            });

        } catch (Exception e) {
            LOGGER.error("sendKafkaMsg:调用kafka登记接口出错!,topic:{},msg:{},ErrorMessage:{}", topic, message, e);
        } finally {
            kafkaProducer.close();
        }
    }
}
