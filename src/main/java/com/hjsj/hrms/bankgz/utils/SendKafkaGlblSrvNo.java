package com.hjsj.hrms.bankgz.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SendKafkaGlblSrvNo {


    private static final Logger LOGGER = LoggerFactory.getLogger(SendKafkaGlblSrvNo.class);

    /**
     * 登记全局流水号
     *
     * @param glblSrvNo 全局流水号
     */
    public static void registerGlbSrlNo(String glblSrvNo, String sv_cod) {
        try {
            String format = FormatSER.createFormat(glblSrvNo, "9", sv_cod);
            long start = System.currentTimeMillis();
            HrsProducer.sendToKafka("Acp_ser_r3p3", format);
            long end = System.currentTimeMillis();
            LOGGER.info("{}|登记全局流水号耗时 {} ms", glblSrvNo, end - start);
        } catch (Exception e) {
            LOGGER.error("登记流水号登记异常 ErrorMessage:{}", e.getMessage());
        }

    }

    /**
     * 全局流水号更新
     *
     * @param glblSrvNo 全局流水号
     * @param state     状态
     */
    public static void updateGlbSrlNo(String glblSrvNo, String state, String repCode, String repMessage) {
        String format = FormatSER.updateFormat(glblSrvNo, state, repCode, repMessage);
        try {
            long start = System.currentTimeMillis();
            HrsProducer.sendToKafka("Acp_ser_r3p3", format);
            long end = System.currentTimeMillis();
            LOGGER.info("{}|更新全局流水号耗时{}", glblSrvNo, end - start);
        } catch (Exception e) {
            LOGGER.error("全局流水号更新登记异常 ErrorMessage:{}", e.getMessage());
        }

    }


}
