/**   
* @Title: HeadhunterRecommendResumeTrans.java 
* @Package com.hjsj.hrms.transaction.hire.employNetPortal 
* @Description: TODO 查询猎头招聘录入的推荐简历
* @author xucs   
* @date 2015年1月28日 下午4:52:19 
* @version V1.0   
*/ 
package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/** 
 * @ClassName: HeadhunterRecommendResumeTrans 
 * @Description: TODO 查询猎头招聘录入的推荐简历 
 * @author xucs
 * @date 2015年1月28日 下午4:52:19 
 *  
 */
public class HeadhunterRecommendResumeTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
			
			String headHunterName = this.userView.getUserEmail();//得到猎头招聘的登录用户名
			ArrayList recommendUserList = new ArrayList();//用来存放推荐简历的相关信息
			EmployNetPortalBo bo = new EmployNetPortalBo(this.frameconn);//招聘外网使用的相关BO
			String dbName=bo.getZpkdbName();//获得招聘人才库
			
			if(dbName==null||dbName.trim().length()==0){
				throw new GeneralException("系统未配置招聘人才库,请联系系统管理员配置招聘人才库!");
			}
			ArrayList list = bo.getZpFieldList();//取得 招聘子集数据列表 和 子集对应指标的 map
			ArrayList columnList = new ArrayList();//表头list
			HashMap fieldSetMap = (HashMap) list.get(2);//存放子集中对应的字段所对应的前台显示指标，是否必填等属性 key:setid value 子集字段组成的mapfieldItemMap
			HashMap a01FiledItemMap = (HashMap) fieldSetMap.get("a01");//获得A01中已经被勾选了的字段  
			// a01FiledItemMap 存放字段的相关 value:1#0 key:字段itemid #前的数字代表是否是 前台显示指标 1：是 0：不是   #后数字是否是必填指标 1：是 0不是
			StringBuffer queryBuffer = new StringBuffer();//查询字段的buffer
			String phoneItemid ="";//系统中配置的移动电话指标
			RecordVo vo = ConstantParamter.getConstantVo("SS_MOBILE_PHONE", this.frameconn);
			if(vo!=null){
			 phoneItemid=vo.getString("str_value");
			 phoneItemid = phoneItemid!=null?phoneItemid:"";
			}
			
			String headHunterNames = bo.isLeader(headHunterName);
			queryBuffer = bo.getQuerysql(a01FiledItemMap,dbName,columnList,phoneItemid,headHunterNames);
			recommendUserList = bo.getResumesUser(queryBuffer,columnList,phoneItemid);
			this.getFormHM().put("recommendUserList", recommendUserList);
			this.getFormHM().put("recommendTbaleList", columnList);
			this.getFormHM().put("dbName", dbName);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
