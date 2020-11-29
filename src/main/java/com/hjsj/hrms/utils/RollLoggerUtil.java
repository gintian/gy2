package com.hjsj.hrms.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.CompositeTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * 日志滚动工具类，代码生成滚动日志
 */
public class RollLoggerUtil {
    private static void start(String name) {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        RollingFileAppender rollingFileAppender = getAppender(name,config);
        rollingFileAppender.start();
        config.addAppender(rollingFileAppender);
        AppenderRef ref = AppenderRef.createAppenderRef(name, null, null);
        AppenderRef[] refs = new AppenderRef[]{ref};
        LoggerConfig loggerConfig = LoggerConfig.createLogger(false, Level.ALL,name,"true",refs,null,config,null);
        loggerConfig.addAppender(rollingFileAppender, null, null);
        config.addLogger(name, loggerConfig);
        ctx.updateLoggers();
    }

    private static RollingFileAppender getAppender(String name, Configuration config) {

        RollingFileAppender.Builder builder = RollingFileAppender.newBuilder();
        //设置按天滚动
        TimeBasedTriggeringPolicy tbtp = TimeBasedTriggeringPolicy.createPolicy("1", "true");
        //设置按大小滚动
        TriggeringPolicy tp = SizeBasedTriggeringPolicy.createPolicy("100M");
        CompositeTriggeringPolicy policyComposite = CompositeTriggeringPolicy.createPolicy(tbtp, tp);
        //设置日志打印格式
        Layout layout = PatternLayout.newBuilder()
                .withConfiguration(config).withPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level %class{36} %L [%t] %M - %msg%xEx%n").build();
        builder.withFileName("../logs/HRS/hrs.log");
        builder.withLocking(false).setName(name);
        builder.withBufferedIo(true);
        builder.withLayout(layout);
        builder.withFilePattern("../logs/HRS/hrs.log.%d{yyyy-MM-dd}.%i.gz");
        builder.withPolicy(policyComposite);
        RollingFileAppender rollingFileAppender = builder.build();
        return rollingFileAppender;
    }
    public static void stop(String name) {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        config.getAppender(name).stop();
        config.getLoggerConfig(name).removeAppender(name);
        config.removeLogger(name);
        ctx.updateLoggers();
    }
    /**
     * 获取Logger
     *
     * 如果不想使用slf4j,那这里改成直接返回Log4j的Logger即可
     * @param name
     * @return
     */
    public static Logger createLogger(String name) {
        start(name);
        return LogManager.getLogger(name);
    }
}
