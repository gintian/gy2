/**
 * 
 */
package com.hjsj.hrms.businessobject.general.template;

/**
 * <p>Title:SubSetUpdateType</p>
 * <p>Description:子集记录更新方式</p> 
 * <p>Company:hjsj</p> 
 * create time at:Sep 19, 20069:47:14 AM
 * @author chenmengqing
 * @version 4.0
 */
public interface SubSetUpdateType {
	public static final int NOCHANGE=0;
	public static final int APPEND=1;
	public static final int UPDATE=2;
	public static final int COND_UPDATE=3; //条件更新
	public static final int COND_APPEND=4; //条件新增
	
}
