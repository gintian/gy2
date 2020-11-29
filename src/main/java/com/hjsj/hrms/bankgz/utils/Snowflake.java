package com.hjsj.hrms.bankgz.utils;

import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Twitter的Snowflake 算法<br>
 * 分布式系统中，有一些需要使用全局唯一ID的场景，有些时候我们希望能使用一种简单一些的ID，并且希望ID能够按照时间有序生成。
 *
 * <p>
 * snowflake的结构如下(每部分用-分开):<br>
 *
 * <pre>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 * </pre>
 * <p>
 * 第一位为未使用(符号位表示正数)，接下来的41位为毫秒级时间(41位的长度可以使用69年)<br>
 * 然后是5位datacenterId和5位workerId(10位的长度最多支持部署1024个节点）<br>
 * 最后12位是毫秒内的计数（12位的计数顺序号支持每个节点每毫秒产生4096个ID序号）
 * <p>
 * 并且可以通过生成的id反推出生成时间,datacenterId和workerId
 * <p>
 *
 * @since 3.0.1
 */
public class Snowflake implements Serializable {

    private static Logger log = LoggerFactory.getLogger(Snowflake.class);
    private static final long serialVersionUID = 20200909L;

    //private final long twepoch = 1288834974657L; // 04 Nov 2010 01:42:54
    private final long twepoch = 1599610088000L; // 2020-09-09 08:08:08
    private final long workerIdBits = 5L;
    private final long dataCenterIdBits = 5L;
    //// 最大支持机器节点数0~31，一共32个
    // 最大支持数据中心节点数0~31，一共32个
    @SuppressWarnings({"PointlessBitwiseExpression", "FieldCanBeLocal"})
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    @SuppressWarnings({"PointlessBitwiseExpression", "FieldCanBeLocal"})
    private final long maxDataCenterId = -1L ^ (-1L << dataCenterIdBits);
    // 序列号12位
    private final long sequenceBits = 12L;
    // 机器节点左移12位
    private final long workerIdShift = sequenceBits;
    // 数据中心节点左移17位
    private final long dataCenterIdShift = sequenceBits + workerIdBits;
    // 时间毫秒数左移22位
    private final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;
    @SuppressWarnings({"PointlessBitwiseExpression", "FieldCanBeLocal"})
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);// 4095

    private final long workerId;
    private final long dataCenterId;
    private final boolean useSystemClock;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    static long customWorkerId; //# 分布式时，每个阶段从0-31依次配置
    static long customDatacenterId; //#分布式时，每个阶段从0-31依次配置
    static final Long DEFAULT_ID = 9L; //# 默认id

    static {
        try {
            customWorkerId = Long.valueOf(SystemConfig.getProperty("custom.snowflake.id"));
        } catch (GeneralException e) {
            log.error("read snowflake config error,");

            //# 不在system.propteries里配置custom.snowflake.id参数或者不满足【0-31】，则使用默认值 DEFAULT_ID
        }

        //工作机器节点id 0-31
        if (customWorkerId > 31 || customWorkerId < 0) {
            customWorkerId = DEFAULT_ID;
        }

        // 数据中心ID  0~31
        customDatacenterId = customWorkerId;
        log.warn("snowflake config: customWorkerId={},customDatacenterId={}", customWorkerId, customDatacenterId);
    }

    /**
     * @param workerId         工作机器节点id
     * @param dataCenterId     数据中心ID  0~31
     * @param isUseSystemClock 是否使用{@link SystemClock} 获取当前时间戳
     * @since 5.1.3
     */
    private Snowflake(long workerId, long dataCenterId, boolean isUseSystemClock) {

        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than {} or less than 0", maxWorkerId));
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than {} or less than 0", maxDataCenterId));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
        this.useSystemClock = isUseSystemClock;
    }

    private Snowflake(long workerId, long dataCenterId) {
        this(workerId, dataCenterId, true);
    }

    /**
     * 根据Snowflake的ID，获取机器id
     *
     * @param id snowflake算法生成的id
     * @return 所属机器的id
     */
    public long getWorkerId(long id) {
        return id >> workerIdShift & ~(-1L << workerIdBits);
    }

    /**
     * 根据Snowflake的ID，获取数据中心ID  0~31
     *
     * @param id snowflake算法生成的id
     * @return 所属数据中心
     */
    public long getDataCenterId(long id) {
        return id >> dataCenterIdShift & ~(-1L << dataCenterIdBits);
    }


    /**
     * 下一个ID
     *
     * @return ID
     */
    public synchronized long nextId() {
        long timestamp = genTime();
        if (timestamp < lastTimestamp) {
            // 如果服务器时间有问题(时钟后退) 报错。
            throw new IllegalStateException(String.format("Clock moved backwards. Refusing to generate id for {}ms", lastTimestamp - timestamp));
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - twepoch) << timestampLeftShift) | (dataCenterId << dataCenterIdShift) | (workerId << workerIdShift) | sequence;
    }

    /**
     * 下一个ID（字符串形式）
     *
     * @return ID 字符串形式
     */
    public String nextIdStr() {
        return Long.toString(nextId());
    }

    // ------------------------------------------------------------------------------------------------------------------------------------ Private method start

    /**
     * 循环等待下一个时间
     *
     * @param lastTimestamp 上次记录的时间
     * @return 下一个时间
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = genTime();
        while (timestamp <= lastTimestamp) {
            timestamp = genTime();
        }
        return timestamp;
    }

    /**
     * 生成时间戳
     *
     * @return 时间戳
     */
    private long genTime() {
        return this.useSystemClock ? SystemClock.now() : System.currentTimeMillis();
    }

    private static Snowflake createSnowflake() {
        /**
         * @param workerId     终端ID 0~31
         * @param datacenterId 数据中心ID  0~31
         */
        return new Snowflake(customWorkerId, customDatacenterId);
    }

    //# 单例模式
    public enum Singleton {
        SNOWFLAKE;

        public Snowflake get() {
            return Snowflake.createSnowflake();
        }
    }

    static class SystemClock {
        /**
         * 时钟更新间隔，单位毫秒
         */
        private final long period;
        /**
         * 现在时刻的毫秒数
         */
        private volatile long now;

        /**
         * 构造
         *
         * @param period 时钟更新间隔，单位毫秒
         */
        public SystemClock(long period) {
            this.period = period;
            this.now = System.currentTimeMillis();
            scheduleClockUpdating();
        }

        /**
         * 开启计时器线程
         */
        private void scheduleClockUpdating() {
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
                Thread thread = new Thread(runnable, "System Clock");
                thread.setDaemon(true);
                return thread;
            });
            scheduler.scheduleAtFixedRate(() -> now = System.currentTimeMillis(), period, period, TimeUnit.MILLISECONDS);
        }

        /**
         * @return 当前时间毫秒数
         */
        private long currentTimeMillis() {
            return now;
        }

        //------------------------------------------------------------------------ static

        /**
         * 单例
         *
         * @author Looly
         */
        private static class InstanceHolder {
            public static final SystemClock INSTANCE = new SystemClock(1);
        }

        /**
         * 单例实例
         *
         * @return 单例实例
         */
        private static SystemClock instance() {
            return SystemClock.InstanceHolder.INSTANCE;
        }

        /**
         * @return 当前时间
         */
        public static long now() {
            return instance().currentTimeMillis();
        }

        /**
         * @return 当前时间字符串表现形式
         */
        public static String nowDate() {
            return new Timestamp(instance().currentTimeMillis()).toString();
        }

        // ------------------------------------------------------------------------------------------------------------------------------------ Private method end

    }
}

