package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class DeleteResumeRecordTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String  currentSetID=(String)this.getFormHM().get("currentSetID");
			String  userid=(String)this.getFormHM().get("a0100");
			String  i9999=(String)hm.get("i9999");
			hm.remove("i9999");
			String dbName=(String)this.getFormHM().get("dbName");
			
			currentSetID=PubFunc.getReplaceStr(currentSetID);
			userid=PubFunc.getReplaceStr(userid);
			i9999=PubFunc.getReplaceStr(i9999);
			dbName=PubFunc.getReplaceStr(dbName);
			EmployNetPortalBo bo=new EmployNetPortalBo(this.getFrameconn());
			ArrayList list=bo.getZpFieldList();
			String workExperience=bo.getWorkExperience();
			String isDefineWorkExperience=EmployNetPortalBo.isDefineWorkExperience;
			String value="";
			if("1".equals(isDefineWorkExperience))
				value=(String)this.getFormHM().get("workExperience");
			
			//招聘渠道，如果注册时候未选择社会还是校园，默认社会
			String tem = (String)this.getFormHM().get("hireChannel");
			String hireChannel = StringUtils.isEmpty(tem) ? "02" : tem;
			//定义了工作经验参数，且注册选择的是校园  或 从校园招聘查看简历
			//headHire、猎头招聘    01、校园招聘   02、社会招聘
			if("1".equals(isDefineWorkExperience)&& "2".equals(value)||"headHire".equals(hireChannel)||"out".equalsIgnoreCase(hireChannel))
				hireChannel = "02";
			
			list=bo.getSetByWorkExprience(hireChannel);
			this.getFormHM().put("hireChannel",hireChannel);
			this.getFormHM().put("workExperience", value);
			
			LazyDynaBean abean=(LazyDynaBean)((ArrayList)list.get(0)).get(Integer.parseInt(currentSetID));
			String setID=(String)abean.get("fieldSetId");
			
			bo.deleteResumeInfo(userid,dbName,setID,i9999);
			/*ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn(),"1");
			HashMap map=xmlBo.getAttributeValues();
			String resume_state="";
			if(map.get("resume_state")!=null&&((String)map.get("resume_state")).length()>0)
			{
				resume_state=(String)map.get("resume_state");
			}
			if(resume_state==null||resume_state.equals(""))
				throw GeneralExceptionHandler.Handle(new Exception("系统运行错误，请联系系统管理员！"));
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String writeable=bo.getWriteable(dao, userid, dbName, resume_state);*/
			String writeable = (String)this.getFormHM().get("writeable");
			this.getFormHM().put("onlyField",(String)this.getFormHM().get("onlyField"));
			this.getFormHM().put("isOnlyCheck", (String)this.getFormHM().get("isOnlyCheck"));
			/**简历是否可修改*/
			this.getFormHM().put("writeable", writeable);
			if(!"0".equalsIgnoreCase(currentSetID))
			{
				ArrayList showFieldList=bo.getShowFieldList(setID,(HashMap)list.get(2),(HashMap)list.get(1),1);  //取得简历子集 列表需显示的 列指标 集合
				ArrayList showFieldDataList=bo.getShowFieldDataList(showFieldList,userid,setID,dbName);
				this.getFormHM().put("showFieldDataList",showFieldDataList);
				this.getFormHM().put("showFieldList",showFieldList);
			}
			this.getFormHM().put("fieldSetList",(ArrayList)list.get(0));
			this.getFormHM().put("fieldMap",(HashMap)list.get(1));
			ArrayList resumeFieldList=bo.getResumeFieldList((ArrayList)list.get(0),(HashMap)list.get(2),Integer.parseInt(currentSetID),(HashMap)list.get(1),userid,dbName,"0");
			this.getFormHM().put("resumeFieldList",resumeFieldList);
			this.getFormHM().put("i9999","0");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
