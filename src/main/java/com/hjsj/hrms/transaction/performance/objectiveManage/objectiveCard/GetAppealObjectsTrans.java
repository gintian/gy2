package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Hashtable;

public class GetAppealObjectsTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String body_id=(String)this.getFormHM().get("body_id");  // 5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
			String mainbodyid=(String)this.getFormHM().get("mainbodyid");
			String object_id=(String)this.getFormHM().get("object_id");
			String planid=(String)this.getFormHM().get("planid");
			
			ObjectCardBo bo=new  ObjectCardBo(this.getFrameconn(),this.userView,planid);
			LoadXml loadxml=new LoadXml(this.getFrameconn(),planid);
			Hashtable planParam=loadxml.getDegreeWhole();
			String SpByBodySeq="False";
			if(planParam.get("SpByBodySeq")!=null)
				SpByBodySeq=(String)planParam.get("SpByBodySeq");
			StringBuffer appealObjectStr=new StringBuffer("");
			if("true".equalsIgnoreCase(SpByBodySeq))
			{
				ArrayList appealObjectList=bo.getAppealObjectInfoBySeq(planid, object_id, this.userView.getA0100());
				for(int i=0;i<appealObjectList.size();i++)
				{
					appealObjectStr.append("&#&"+(String)appealObjectList.get(i));
				}
				if(appealObjectStr.length()==0)
					this.getFormHM().put("info","ok");
				else
					this.getFormHM().put("info","");
			}else{
			//			 目标卡制订支持几级审批
				String targetMakeSeries =(String)planParam.get("targetMakeSeries");
				String targetAppMode=(String)planParam.get("targetAppMode");  //目标管理审批模式:0-考核关系 1-汇报关系. 默认为 0
				String followBodyId="";
				
				if("1".equals((String)planParam.get("targetMakeSeries")))
					followBodyId=bo.getfollowBodyid("5");
				else
					followBodyId=bo.getfollowBodyid(body_id);
				
				ArrayList appealObjectList=null;
				String posID="";
				
				LazyDynaBean _bean=getObjectInfo(object_id,planid);
				String objectFlag=(String)_bean.get("sp_flag");
				String kh_relations=(String)_bean.get("kh_relations");//=1非标准。=0标准
				if("1".equals(targetAppMode))
				{
					posID=this.userView.getUserPosId();
				
				}
				LazyDynaBean abean=getMainbodyBean(planid,object_id,this.userView.getA0100());
				LazyDynaBean functionary=bo.getMainbodyBean(planid,object_id);
				String perMainBody_body_id="";
				if(abean!=null)
					perMainBody_body_id=(String)abean.get("body_id");
				String aObject_id=object_id;
				if(bo.getPlan_vo().getInt("object_type")==1||bo.getPlan_vo().getInt("object_type")==3||bo.getPlan_vo().getInt("object_type")==4)
				{
						if(functionary!=null)
							aObject_id=(String)functionary.get("mainbody_id");
				}
				
				if("01".equals(objectFlag)&&aObject_id.equalsIgnoreCase(this.userView.getA0100()))
				{
					if(bo.getPlan_vo().getInt("object_type")==1||bo.getPlan_vo().getInt("object_type")==3||bo.getPlan_vo().getInt("object_type")==4)
					{
						if(functionary==null)
							throw GeneralExceptionHandler.Handle(new Exception("目标对象没有定义部门负责人!"));
					}
					if("1".equals(targetAppMode))
						posID=getPosIDbya0100("Usr",aObject_id);
					else if(!this.userView.getA0100().equalsIgnoreCase(aObject_id))
					{
						followBodyId=bo.getfollowBodyid("5");
					}
				}
				
				appealObjectList=bo.getAppealObjectInfo(followBodyId, "Usr", aObject_id, planid, posID, targetAppMode,object_id);
				for(int i=0;i<appealObjectList.size();i++)
				{
					appealObjectStr.append("&#&"+(String)appealObjectList.get(i));
				}
				if(appealObjectStr.length()==0&&("0".equals(targetAppMode)|| "1".equals(kh_relations)))
				{
					int currentLevel= bo.getCurrentLevel(followBodyId);
				
					for(int i=currentLevel+1;i<=Integer.parseInt(targetMakeSeries);i++)
					{
							followBodyId=bo.getfollowBodyid(followBodyId);
							appealObjectList=bo.getAppealObjectInfo(followBodyId, "Usr", aObject_id, planid, "", "0",object_id);		
							for(int j=0;j<appealObjectList.size();j++)
							{
								appealObjectStr.append("&#&"+(String)appealObjectList.get(j));
							}
							if(appealObjectStr.length()>0)
							{
								break;
							}
					}
				}
				if((aObject_id.equalsIgnoreCase(mainbodyid)|| "-1".equals(perMainBody_body_id))&&appealObjectStr.length()==0)
					this.getFormHM().put("info","考核对象审批关系没有定义，无审批人可报批!");
				else
					this.getFormHM().put("info","");
			}
			if(appealObjectStr.length()>0)
				this.getFormHM().put("appealObjectStr", appealObjectStr.substring(3));
			else
				this.getFormHM().put("appealObjectStr", appealObjectStr.toString());
			this.getFormHM().put("body_id",body_id);
			this.getFormHM().put("mainbodyid",mainbodyid);
			this.getFormHM().put("object_id",object_id);
			this.getFormHM().put("planid",planid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	
	
	public String getPosIDbya0100(String nbase,String a0100)
	{
		String posID="";
		try
		{
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rowSet=dao.search("select * from "+nbase+"A01 where a0100='"+a0100+"'");
			if(rowSet.next())
			{
				posID=rowSet.getString("e01a1");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return posID;
	}
	
	
	
	public LazyDynaBean getObjectInfo(String object_id,String plan_id)
	{
		String sp_flag="01";
		String kh_relations="0";
		LazyDynaBean abean=new LazyDynaBean();
		try
		{
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rowSet=dao.search("select * from per_object where plan_id="+plan_id+" and object_id='"+object_id+"'");
			if(rowSet.next())
			{
				if(rowSet.getString("sp_flag")!=null)
					sp_flag=rowSet.getString("sp_flag");
				if(rowSet.getString("kh_relations")!=null)
					kh_relations=rowSet.getString("kh_relations");
			}
			abean.set("sp_flag", sp_flag);
			abean.set("kh_relations", kh_relations);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return abean;
	}
	
	
	
	public LazyDynaBean getMainbodyBean(String plan_id,String object_id,String mainbody_id)
	{
		LazyDynaBean abean=null;
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rowSet=dao.search("select * from per_mainbody where plan_id="+plan_id+" and object_id='"+object_id+"' and mainbody_id='"+mainbody_id+"'");
			if(rowSet.next())
			{
				abean=new LazyDynaBean();
				abean.set("id",isNull(rowSet.getString("id")));
				abean.set("body_id",isNull(rowSet.getString("body_id")));
				abean.set("object_id",isNull(rowSet.getString("object_id")));
				abean.set("mainbody_id",isNull(rowSet.getString("mainbody_id")));
				abean.set("status",isNull(rowSet.getString("status")));
				abean.set("a0101",isNull(rowSet.getString("a0101")));
				abean.set("know_id",isNull(rowSet.getString("know_id")));
				abean.set("whole_grade_id",isNull(rowSet.getString("whole_grade_id")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return abean;
	}
	
	public String isNull(String str) 
	{
		if (str == null || str.trim().length() <= 0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str))
			return "";
		else
			return str;
	}	

}
