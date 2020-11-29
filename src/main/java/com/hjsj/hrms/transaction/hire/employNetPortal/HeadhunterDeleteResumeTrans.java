/**   
* @Title: HeadhunterDeleteResumeTrans.java 
* @Package com.hjsj.hrms.transaction.hire.employNetPortal 
* @Description: TODO(用一句话描述该文件做什么) 
* @author xucs   
* @date 2015年2月5日 上午10:21:52 
* @version V1.0   
*/ 
package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/** 
 * @ClassName: HeadhunterDeleteResumeTrans 
 * @Description: 猎头删除简历 
 * @author xucs
 * @date 2015年2月5日 上午10:21:52 
 *  
 */
public class HeadhunterDeleteResumeTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList selectedList = (ArrayList) this.getFormHM().get("selectedlist");
		EmployNetPortalBo bo = new EmployNetPortalBo(this.frameconn);//招聘外网使用的相关BO
		ArrayList list = bo.getZpFieldList();//取得 招聘子集数据列表 和 子集对应指标的 map
		ArrayList fieldSetList = (ArrayList) list.get(0);//存放子集中对应的字段所对应的前台显示指标，是否必填等属性 key:setid value 子集字段组成的mapfieldItemMap
		String dbName =bo.getZpkdbName();
		bo.deleteResume(fieldSetList,dbName,selectedList);
		
	}

}
