/**   
* @Title: HeadhunterAddResumeTrans.java 
* @Package com.hjsj.hrms.transaction.hire.employNetPortal 
* @Description: TODO(用一句话描述该文件做什么) 
* @author xucs   
* @date 2015年2月2日 下午1:44:25 
* @version V1.0   
*/ 
package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/** 
 * @ClassName: HeadhunterAddResumeTrans 
 * @Description:猎头招聘新增简历信息
 * @author xucs
 * @date 2015年2月2日 下午1:44:25 
 *  
 */
public class HeadhunterAddResumeTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
			EmployNetPortalBo bo=new EmployNetPortalBo(this.getFrameconn());//外网招聘使用的相关Bo类
			ContentDAO dao = new ContentDAO(this.frameconn);
			String dbName = bo.getZpkdbName();//招聘人才库
			String a0100="headHire";//只要进入这个交易类,表明是新增或者编辑进来的,当编辑时要从getFormHM中取  待做
			ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn(),"1");//招聘后台配置参数的BO
			HashMap map=xmlBo.getAttributeValues();
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			hm.remove("a0100");
			
			String isUpPhoto="0";   //是否必须上传照片
			if(map.get("photo")!=null&&((String)map.get("photo")).length()>0)//照片必须上传=1时为必须上传照片
				isUpPhoto=(String)map.get("photo");
			
			
			String isDefineWorkExperience=EmployNetPortalBo.isDefineWorkExperience;
			String value="";
			if("1".equals(isDefineWorkExperience))
				value=(String)this.getFormHM().get("workExperience");
			ArrayList list=bo.getZpFieldList();
			//查询简历子集参数，默认01
			String setParam = "01";
			if("1".equals(isDefineWorkExperience))
			{
				if("2".equals(value))
					setParam = "01";
//			    	list=bo.getSetByWorkExprience("01");
				else
					setParam = "02";
//					list=bo.getSetByWorkExprience("02");
			}
			list=bo.getSetByWorkExprience(setParam);
			//设置必填的子集
			ArrayList  fieldSetMustList = new ArrayList(); 
			fieldSetMustList = (ArrayList)list.get(4);
			this.getFormHM().put("fieldSetMustList",fieldSetMustList);
			ArrayList resumeFieldList=bo.getResumeFieldList((ArrayList)list.get(0),(HashMap)list.get(2),0,(HashMap)list.get(1),a0100,dbName,"1");
			String isExp="0"; //是否显示指标描述
			if(map.get("explaination")!=null&&((String)map.get("explaination")).length()>0)
				isExp=(String)map.get("explaination");
			String isAttach="0";
			if(map.get("attach")!=null&&((String)map.get("attach")).length()>0)
				isAttach=(String)map.get("attach");
			String onlyname = bo.getOnly_field();
			 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			String blacklist_per=sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST,"base");
			String blacklist_field=sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST,"field");
			if(blacklist_field==null|| "".equals(blacklist_field))
				this.getFormHM().put("blackField", "");
			else
				this.getFormHM().put("blackField", blacklist_field);
			if(blacklist_per==null|| "".equals(blacklist_per))
				this.getFormHM().put("blackNbase", "");
			else
				this.getFormHM().put("blackNbase",blacklist_per);
			this.getFormHM().put("isUpPhoto",isUpPhoto);
			this.getFormHM().put("isExp",isExp);
			this.getFormHM().put("onlyName", onlyname==null?"":onlyname);
			this.getFormHM().put("isAttach",isAttach);
			this.getFormHM().put("dbName",dbName);
			this.getFormHM().put("resumeFieldList",resumeFieldList);
			this.getFormHM().put("isPhoto","0");
			/**简历是否可修改*/
			String writeable=bo.getWriteable(dao, a0100, dbName);
			this.getFormHM().put("fieldSetList",(ArrayList)list.get(0));//招聘的人员子集
			this.getFormHM().put("fieldMap",(HashMap)list.get(1));
			this.getFormHM().put("currentSetID","0");
			this.getFormHM().put("writeable", writeable);
			this.getFormHM().put("a0100",a0100);//将登录用户的a0100更改为这个
			this.getFormHM().put("opt","1");
			this.getFormHM().put("currentSetID","0");
			//检验唯一性指标
			String isOnlyChecked="0";
			String onlyField="";
			EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.getFrameconn(),isAttach);
			if(employNetPortalBo.isOnlyChecked())
			{
				isOnlyChecked="1";
				onlyField=EmployNetPortalBo.isOnlyChecked;
			}
			this.getFormHM().put("onlyField",onlyField);
			this.getFormHM().put("isOnlyCheck", isOnlyChecked);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
