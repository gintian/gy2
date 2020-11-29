package com.hjsj.hrms.businessobject.kq.team;

public interface KqClassArrayConstant {

    /*	周期班对应基本班次信息*/
	public static String kq_shift_class_table="kq_shift_class";
	public static String kq_shift_class_shiftID="shift_id";
	public static String kq_shift_class_classID="class_id";
	public static String kq_shift_class_seq="seq";//基本班次的排列顺序
	/*****周期班次信息*****/
	public static String kq_shift_table="kq_shift";
	public static String kq_shift_ID="shift_id";
	public static String kq_shift_name="name";
	public static String kq_shift_week_flag="week_flag";//休息公休日
	public static String kq_shift_feast_flag="feast_flag";//休息节假日
	public static String kq_shift_shift_days="shift_days";//周期天数
    /****员工排班信息****/
	public static String kq_employ_shift_table="kq_employ_shift";	
	public static String kq_org_dept_shift_table="kq_org_dept_shift";
	public static String kq_employ_shift_table_arc="kq_employ_shift_arc";	//员工排班归档表
	public static String kq_org_dept_shift_table_arc="kq_org_dept_shift_arc"; //部门排班归档表
	public static String kq_employ_shift_q03z0="q03z0";//工作日期
	public static String kq_employ_shift_kqyear="ke_year";
	public static String kq_employ_shift_kqduration="kq_duration";
	public static String kq_employ_shift_classid="class_id";//班次序号
	public static String kq_employ_shift_id="shift_id";
	public static String kq_employ_shift_status="status";
	/************班组信息**************/
	public static String kq_shift_group_table="kq_shift_group";
	public static String kq_shift_group_Id="group_id";
	public static String kq_shift_group_name="name";
	public static String kq_shift_org_id="org_id";
	public static String kq_group_emp_table="kq_group_emp";//班组人员对应关系表
	/*************不定排班信息******************/
	public static String kq_org_dept_able_shift_table="kq_org_dept_able_shift";
	public static String org_dept_id="org_dept_id";
	public static String org_dept_class_id ="class_id"; 
	public static String org_dept_codesetid="codesetid";  
	
	
	
	

	}
