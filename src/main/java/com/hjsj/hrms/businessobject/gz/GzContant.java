/**
 * 
 */
package com.hjsj.hrms.businessobject.gz;

/**
 *<p>Title:薪资常量</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-8-8:上午11:58:36</p> 
 *@author cmq
 *@version 4.0
 */
public interface GzContant {
	/**起草*/
	public static final String SP_STATE_DRAFT="01"; 
	/**报批*/
	public static final String SP_STATE_HANDIN="02";
	/**已批*/
	public static final String SP_STATE_PASS="03";
	/**拒绝*/
	public static final String SP_STATE_REJECT="04";
	/**结束*/
	public static final String SP_STATE_FINISH="05";
	/**正常发薪*/
	public static final String PAY_FLAG_NORMAL="0";
	/**当月补发*/
	public static final String PAY_CURMON_REISSUE="1";
	/**全月补发*/
	public static final String PAY_FULLMON_REISSUE="2";
	/**半月补发*/
	public static final String PAY_HALFMON_REISSUE="3";
	
}
