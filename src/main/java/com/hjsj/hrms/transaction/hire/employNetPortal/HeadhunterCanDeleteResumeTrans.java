/**   
* @Title: HeadhunterCanDeleteResumeTrans.java 
* @Package com.hjsj.hrms.transaction.hire.employNetPortal 
* @Description: TODO(用一句话描述该文件做什么) 
* @author xucs 
* @date 2015年2月4日 下午4:42:06 
* @version V1.0   
*/ 
package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/** 
 * @ClassName: HeadhunterCanDeleteResumeTrans 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author xucs
 * @date 2015年2月4日 下午4:42:06 
 *  
 */
public class HeadhunterCanDeleteResumeTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		String  selectA0100s=(String) this.getFormHM().get("selecta0100s");//前台被选中的那些行
		String selectA0100Array[] = selectA0100s.split(",");
		String userNames = (String) this.getFormHM().get("views");//前台被选中的那些行的人员姓名
		String userNameArray[] =userNames.split(",");
		
		EmployNetPortalBo bo = new EmployNetPortalBo(this.frameconn);
		ArrayList valueList = new ArrayList();
		StringBuffer queryBuffer = new StringBuffer();//查询信息的sql语句
		HashMap nameMap = new HashMap();//存放人员的姓名
		queryBuffer.append("select a0100,status from zp_pos_tache where a0100 in(");
		
		try{
			for(int i=0;i<selectA0100Array.length;i++){
				String a0100 = PubFunc.decrypt(selectA0100Array[i]);
				String a0101 = userNameArray[i];
				nameMap.put(a0100, a0101);
				if(i==selectA0100Array.length-1){
					queryBuffer.append("?");
				}else{
					queryBuffer.append("?,");
				}
				valueList.add(a0100);
			}
			queryBuffer.append(") and status='1'");
			String information = bo.getDeleteInformation(queryBuffer,nameMap,valueList);
			this.getFormHM().put("information", information);
			this.getFormHM().put("selecta0100s", selectA0100s);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
