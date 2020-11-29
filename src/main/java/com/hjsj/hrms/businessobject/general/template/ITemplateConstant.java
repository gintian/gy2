package com.hjsj.hrms.businessobject.general.template;

public interface ITemplateConstant {
	/**
	 * 业务模板根据单位性质进行划分，主要为了开发商可以根据
	 * 单位性质内置一些业务模板
	 */
    public static final int NATION=1; //国家机关
    public static final int CAREER=2; //事业单位
    public static final int ENT=3;	  //企业单位
    public static final int ARMY=4;   //军队
    public static final int OTHER=5;  //其它    
    /**
     * 业务模板再进一些细分，可按业务类型
     */
    public static final int COMM=1;    //日常管理
    public static final int SALARY=2;  //工资管理 
    public static final int RM=3;      //警衔管理
    public static final int INS=8;     //保险管理
    public static final int GM=4;      //关衔管理    
    public static final int ARC=9;     //档案转递    

    
}
