package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/** 
 *<p>Title:SaveTaskDescribeTrans.java</p> 
 *<p>Description:保存新建或编辑的目标卡任务</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 31, 2008</p> 
 *@author dengcan
 *@version 4.0
 */

public class SaveTaskDescribeTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{			
			ArrayList taskDescribeList=(ArrayList)this.getFormHM().get("taskDescribeList");
			String planid=(String)this.getFormHM().get("planid");
			String object_id=(String)this.getFormHM().get("object_id");
			String body_id=(String)this.getFormHM().get("body_id");
			String model=(String)this.getFormHM().get("model");
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String operator=(String)hm.get("operator");  
			String opt=(String)this.getFormHM().get("opt");
			String itemtype=(String)this.getFormHM().get("itemtype");
			String a_p0400="";
			if(this.getFormHM().get("a_p0400")!=null)
				a_p0400=(String)this.getFormHM().get("a_p0400");
			String isAdjustPoint=(String)this.getFormHM().get("isAdjustPoint");
			String objectSpFlag=(String)this.getFormHM().get("objectSpFlag");
			String processing_state_all=(String)this.getFormHM().get("processing_state_all");
			String raterid=(String)this.getFormHM().get("objectCardGradeMembersRater");
	//		ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),planid,object_id,this.getUserView(),model,body_id,opt);
			ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),planid,object_id,this.getUserView(),model,opt,1);
			bo.setAdjustPoint(Boolean.valueOf(isAdjustPoint).booleanValue());
			bo.setObjectSpFlag(objectSpFlag);
			bo.setProcessing_state_all(processing_state_all);
			bo.saveTask(taskDescribeList,a_p0400,operator,Integer.parseInt(itemtype));
			
			if(bo.isOpenGrade_Members()){// 编辑列表加入多评分人 zhanghua
				if(StringUtils.isNotBlank(raterid)){
					String[] idsArray = raterid.split(",");
					
					
					HashMap<String, ArrayList<String>> map=new HashMap<String, ArrayList<String>>();
					for(String userid:idsArray)
					{
						userid = PubFunc.decrypt(SafeCode.decode(userid));
						String nbase =userid.substring(0, 3);//人员库前缀
						if(map.containsKey(nbase)){
							ArrayList<String> list=(ArrayList<String>) map.get(nbase);
							list.add(userid.substring(3));
						}else{
							ArrayList<String> list=new ArrayList<String>();
							list.add(userid.substring(3));
							map.put(nbase, list);
						}
					}
					if(map.size()>0){
						LazyDynaBean abean=null;
						String p0400="";
						for(int i=0;i<taskDescribeList.size();i++){
							abean = (LazyDynaBean) taskDescribeList.get(i);
							if("p0400".equalsIgnoreCase(abean.get("itemid").toString()))
								p0400=(String)abean.get("value");
						}
						if(StringUtils.isNotBlank(p0400))
							bo.UpdateGradeMembers(p0400, map);
					}
				}
				
			}
			
	/*		if(hm.get("b_addTask1")!=null)
			{
				String p0400="";
				for(int i=0;i<taskDescribeList.size();i++)
				{
					LazyDynaBean abean=(LazyDynaBean)taskDescribeList.get(i);
					String itemid=(String)abean.get("itemid");
					if(itemid.equalsIgnoreCase("p0400"))
					{
						p0400=(String)abean.get("value");
						break;
					}
				}
				this.getFormHM().put("a_p0400", p0400);
				hm.remove("b_addTask1");
			}
			*/
			
			// 修改员工已批的目标卡时程序将自动发送邮件给员工  JinChunhai 2013.03.19
			if(objectSpFlag!=null && objectSpFlag.trim().length()>0 && "03".equalsIgnoreCase(objectSpFlag) && !object_id.equalsIgnoreCase(this.userView.getA0100()))
				bo.sendEmailObj("",operator,taskDescribeList);
						
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}			
			
}
