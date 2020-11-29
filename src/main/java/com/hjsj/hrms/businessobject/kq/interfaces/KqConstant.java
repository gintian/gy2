package com.hjsj.hrms.businessobject.kq.interfaces;

import com.hjsj.hrms.utils.ResourceFactory;

/**
 * 
 * <p>Title: KqConstant </p>
 * <p>Description: 考勤模块常量</p>
 * <p>Company: hjsj</p>
 * <p>create time: 2013-8-21 上午11:51:38</p>
 * @author zhaoxj
 * @version 1.0
 */
public interface KqConstant {
    //考勤方式
    final static class KqType {
        /**手工考勤*/
        public final static String MANUAL  = "01";
        /**机器考勤*/
        public final static String MACHINE = "02";
        /**不考勤勤*/
        public final static String NO      = "03";
        /**暂停考勤*/
        public final static String STOP    = "04";
    }

    //数据处理类别
    final static class AnalyseType {
        /**非机考人员*/
        public final static String NOT_MACHINE     = "0";
        /**机考人员*/
        public final static String MACHINE         = "1";
        /**全部考勤人员*/
        public final static String ALL             = "100";
        /**集中处理模式机考人员*/
        public final static String MACHINE_CENTRAL = "101";
    }

    //申请类型
    final static class AppType {
        /**请假**/
        public static final int LEAVE = 0;
        /**加班**/
        public static final int OVERTIME = 1;
        /**公出**/
        public static final int OFFICE_LEAVE = 3;
        
        /**平时加班*/
        public static final int OVERTIME_IS_NORMAL  = 12;
        /** 公休日加班*/
        public static final int OVERTIME_IS_REST    = 10; 
        /** 节假日加班*/
        public static final int OVERTIME_IS_FEAST   = 11; 
    }
    
    //刷卡数据
    final static class CardData {
        /**进卡*/
        public static final int INOUT_IN = 1;
        /**出卡*/
        public static final int INOUT_OUT = -1;
        /**不限*/
        public static final int INOUT_ALL = 0;
    }
    
    //考勤规则单位
    final static class Unit {
        /**天*/
        public static final String DAY = "02";
        public static final String DAY_DESC = ResourceFactory.getProperty("kq.time.unit.day");
        /**小时*/
        public static final String HOUR = "01";
        public static final String HOUR_DESC = ResourceFactory.getProperty("kq.time.unit.hour");
        /**分钟*/
        public static final String MINUTE = "03";
        public static final String MINUTE_DESC = ResourceFactory.getProperty("kq.time.unit.minute");
        /**次*/
        public static final String TIMES = "04";
        public static final String TIMES_DESC = ResourceFactory.getProperty("kq.time.unit.times");
    }
    
    //移动端考勤常量
    final static class Mobile {
        /**查询我的假期*/
        public static final int SEARCH_HOLIDAY = 1;
        /**查询我的调休*/
        public static final int SEARCH_OVERTIME_FOR_LEAVE = 2;
        /**查询我的休假明细*/
        public static final int SEARCH_HOLIDAY_DETAIL = 3;
        /**同时查询以上三种数据*/
        public static final int SEARCH_HOLIDAY_ALL = 0;
    }
    
    //考勤模块版本
    final static class Version {
        /** 标准版考勤休假 **/
        public static final int STANDARD = 0;
        /** 新般高校医院考勤 **/
        public static final int UNIVERSITY_HOSPITAL = 1;
    }
}
