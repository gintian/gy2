package com.hjsj.hrms.businessobject.workplan;


/**
 * 工作计划任务总结常量类
 * <p>
 * Title: KqConstant
 * </p>
 * <p>
 * Description: 工作计划任务总结常量
 * </p>
 * <p>
 * Company: hjsj
 * </p>
 * <p>
 * create time: 2014-6-9 上午11:51:38
 * </p>
 * 
 * @author zhaoxj
 * @version 1.0
 */

public interface WorkPlanConstant {
    final static String[] NUM_DESC = {"一","二","三","四","五","六","七","八","九","十"};

	final static String DEPTlEADERFld = "B0199";
	
	//人力地图每页几个
	final static int PAGESIZE = 8;
	//姓名汉字截取长度
	final static int HZA0101TRUNCATELEN = 3;

	// 计划、总结
	final static class WorkType {
		// 工作计划
		public final static String PLAN = "1";
		// 工作总结
		public final static String SUMMARY = "2";

	}

	// 计划流程状态：指定、跟踪
	final static class PlanFlowStatus {
		// 计划制定
		public final static String DESIGN = "1";
		// 计划跟踪
		public final static String TRANCE = "2";
	}

	// 计划、任务、总结可见范围
	final static class Scope {
		// 单位可见
		public final static String UNIT = "1";
		// 部门可见
		public final static String DEPARTMENT = "2";
		// 所有可见
		public final static String ALL = "3";
		// 上级可见
		public final static String SUPERIOR = "4";
		// 下级可见
		public final static String CHILDREN = "5";
	}

	// 周期 wangrd 更改2014-07-01 同p07表的周期一致
	final static class Cycle {
		// 年
		public final static String YEAR = "1";

		// 半年
		public final static String HALFYEAR = "2";

		// 季度
		public final static String QUARTER = "3";

		// 月
		public final static String MONTH = "4";

		// 周
		public final static String WEEK = "5";

		// 日
		public final static String Day = "0";

		// 不定期
		public final static String NON_PERIODIC = "6";

	}
	
	//zxj 20140809 由于总结与计划周期不一致，单独为总结增加一套周期常量 
    final static class SummaryCycle {
        // 年
        public final static String YEAR = "4";

        // 半年
        public final static String HALFYEAR = "5";

        // 季度
        public final static String QUARTER = "3";

        // 月
        public final static String MONTH = "2";

        // 周
        public final static String WEEK = "1";

        // 日
        public final static String Day = "0";

        // 不定期
        public final static String NON_PERIODIC = "6";
    }


	// 成员类型
	final static class MemberType {
		// 负责人
		public final static String LEADER = "1";

		// 参与人(协办人)
		public final static String MEMBER = "2";

		// 关注人
		public final static String FOLLOWER = "3";

		// 审批人
		public final static String APPROVER = "4";
        //创建人
        public final static String CREATOR = "5";
	}

	// 总结状态
	final static class SummaryStatus {
		// 未提交
		public final static String UNCOMMITTED = "01";

		// 已提交
		public final static String COMMITTED = "02";

		// 已评价
		public final static String EVALUATED = "03";
	}

	// 计划类型
	final static class PlanType {
		// 我的计划
		public final static String SELFPLAN = "1";

		// 我团队的计划
		public final static String TEAMPLAN = "2";

		// 我的部门计划
		public final static String DEPTPLAN = "3";
		
		// 我下属的部门计划
		public final static String SUBDEPTPLAN = "4";
	}
	
	// 人力地图类型
	final static class HumanMap {
	    // 我的部门
	    public final static String DEPT = "1";
	    
	    // 团队成员
	    public final static String TEAM = "2";
	    
	    // 我的下属部门
	    public final static String SUB_DEPT = "3";
	    
	    // 我关注的
	    public final static String ME_CONCERNED = "4";
	}
	
	// 计划审批状态
	final static class PlanApproveStatus {
	    /** 起草 */
	    public final static int Draft = 0;	    
	    /** 报批 */
	    public final static int HandIn = 1;
	    
	    /** 已批 */
	    public final static int Pass = 2;
	    /** 驳回 */
	    public final static int Reject = 3;
	    
	    /** 启动 */
	    public final static int Startup = 4;	    
	    /** 关闭 */
	    public final static int Finish = 5;
	}
	
	/** p0811 审批状态 (01:起草, 02:待批准, 03:已批准, 07:驳回) */
	public static class TaskStatus {
		
		/** 起草 */
		public static final String DRAFT = "01";
		
		/** 待批准 */
		public static final String APPROVE = "02";
		
		/** 已批准 */
		public static final String APPROVED = "03";
		
		/** 驳回 */
//		public static final String REJECT = "07";
	}
	

	/** p0809 任务执行状态 (1:未开始, 2:进行中, 3:完成, 4:暂缓, 5:取消) */
	public static class TaskExecuteStatus {
		
		/** 未开始 */
		public static final String BEFORE_START = "1";
		
		/** 进行中 */
		public static final String UNDERWAY = "2";
		
		/** 完成 */
		public static final String COMPLETE = "3";
		
		/** 暂缓 */
		public static final String SUSPEND = "4";
		
		/** 取消 */
		public static final String CANCEL = "5";
	}

	   // 任务变更状态 p0833
    final static class TaskChangedStatus {
        // 未变更
        public final static int Normal = 0;        
        // 新增
        public final static int add = 1; 
        // 取消
        public final static int Cancel = 2;        
        // 已变更
        public final static int Changed = 3;
        // 其他修改
        public final static int OtherChanged = 4;        

    }
	
	/** 任务相关 */
	final static class TaskInfo {
		/** 任务编辑查看界面需要排除的字段(不会动态展现的字段需要排除) */
		public final static String TASK_EXCLUDE_FIELD =
				"/P0800/P0700/P0801/P0803/P0809/P0811/P0813/P0815/P0823/P0827/P0829/P0831/CREATE_TIME/CREATE_USER/CREATE_FULLNAME/P0833/P0835/P0841/RANK";
		
		/** 修改时需啊哟同步修改变更状态的字段 */
		public final static String TASK_CHANGE_STATUS_FIELD = "/RANK/P0841/DIRECTOR/MEMBER/P0813/P0815/P0823";//linbz 23483 修改任务描述p0803字段不需变更状态
		
		/** 候选人最大数量(选人提示框最多容纳的数量) */
		public static int MAX_CANDIDATE_NUMBER = 20;
	}
	
	
}
