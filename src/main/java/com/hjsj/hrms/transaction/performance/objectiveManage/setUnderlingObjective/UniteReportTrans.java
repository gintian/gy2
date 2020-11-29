package com.hjsj.hrms.transaction.performance.objectiveManage.setUnderlingObjective;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.SetUnderlingObjectiveBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Hashtable;

public class UniteReportTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String planid=(String)this.getFormHM().get("planid");
			String record=(String)this.getFormHM().get("record");
			String model=(String)this.getFormHM().get("model");
			String url_p=(String)this.getFormHM().get("url_p");
			String[] arr=record.split(",");
			SetUnderlingObjectiveBo suob=new SetUnderlingObjectiveBo(this.getFrameconn());
			String info="";  //
			for(int i=0;i<arr.length;i++)
			{
				if(arr[i]==null|| "".equals(arr[i]))
					continue;
				String object_id=arr[i].replaceAll("／", "/").split("/")[0];
				String body_id=arr[i].replaceAll("／", "/").split("/")[1];
	    		ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),planid,object_id,this.getUserView(),model,body_id,"1");
		    	String isEmail="0";
		    	if(this.getFormHM().get("isEmail")!=null)
		         	isEmail=(String)this.getFormHM().get("isEmail");
		     	bo.setIsEmail(isEmail);
		//		LoadXml loadxml=new LoadXml(this.getFrameconn(),planid);
				Hashtable planParam=bo.getPlanParam();//loadxml.getDegreeWhole();
				String targetMakeSeries =(String)planParam.get("targetMakeSeries");
				String targetAppMode=(String)planParam.get("targetAppMode");  //目标管理审批模式:0-考核关系 1-汇报关系. 默认为 0
				ArrayList itemPointInfo=bo.getItemPointInfo();
				String scoreflag=(String)bo.getPlanParam().get("scoreflag");  //=2混合，=1标度(默认值=混合)=4加扣分
				// 报批、批准进行总分校验True, False, 默认为 True;(目标卡-流程控制中)
				if(planParam.get("ProcessNoVerifyAllScore")!=null && "true".equalsIgnoreCase((String)planParam.get("ProcessNoVerifyAllScore")))
					info=bo.validateTaskScore();
                if(info.length()==0)
                	info=bo.validateFollowPointMustFill(2);
                if(info.length()!=0)
                {
                	if("2".equals(bo.getPlan_vo().getString("object_type")))///人员
                	{
                		LazyDynaBean _bean=getObjectInfo(object_id,planid);
                		info="【"+(String)_bean.get("a0101")+"】 在考核计划 【"+bo.getPlan_vo().getString("name")+"】中\r\n"+info;
                	}
                	else
                	{
                		String org=AdminCode.getCodeName("UN",object_id);
                		if(org==null|| "".equals(org))
                			org=AdminCode.getCodeName("UM", object_id);
                		info="【"+org+"】 在考核计划 【"+bo.getPlan_vo().getString("name")+"】中\r\n"+info;	
                	}
                	break;
                }
			}
			if(info.length()==0)
			{
				for(int i=0;i<arr.length;i++)
				{
					if(arr[i]==null|| "".equals(arr[i]))
						continue;
					String object_id=arr[i].replaceAll("／", "/").split("/")[0];
					String body_id=arr[i].replaceAll("／", "/").split("/")[1];
		    		ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),planid,object_id,this.getUserView(),model,body_id,"1");
			    	String isEmail="0";
			    	if(this.getFormHM().get("isEmail")!=null)
			         	isEmail=(String)this.getFormHM().get("isEmail");
			     	bo.setIsEmail(isEmail);
			     	//ObjectCardBo bo=new  ObjectCardBo(this.getFrameconn(),planid);
					LoadXml loadxml=new LoadXml(this.getFrameconn(),planid);
					Hashtable planParam=loadxml.getDegreeWhole();
					String SpByBodySeq="False";
					if(planParam.get("SpByBodySeq")!=null)
						 SpByBodySeq=(String)planParam.get("SpByBodySeq");
					if("true".equalsIgnoreCase(SpByBodySeq))
					{
						if("1".equals(bo.getIsApproveFlag())){//批准
							bo.approveSpObject(object_id,this.userView.getA0100(), planid, "USR");
						}else{
							ArrayList appealObjectList=bo.getAppealObjectInfoBySeq(planid, object_id, this.userView.getA0100());
							if(appealObjectList!=null&&appealObjectList.size()>0)
		                    {
		                        	String str=(String)appealObjectList.get(0);
		                        	//b0110+"^"+e0122+"^"+e01a1+"^"+a0101+"^"+a0100+"/"+body_id
		                        	String appealObject_id=str.substring(str.indexOf("/")-8, str.indexOf("/"));
		                        	bo.appealSpObject(object_id,appealObject_id,this.userView.getA0100(),planid,"Usr",url_p);
		                    }
							else{
								bo.approveSpObject(object_id,this.userView.getA0100(), planid, "USR");
								bo.optPersonalComment("2");
							}
						}
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
				    	String kh_relations=(String)_bean.get("kh_relations");
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
				    	String sql="select kh_relations from per_object where plan_id="+planid+" and object_id='"+object_id+"'";
				    	int setLevel = suob.getMainBodySetLevel(Integer.parseInt(targetMakeSeries));
				    	if(setLevel==Integer.parseInt(body_id))
						{
				    		bo.approveSpObject(object_id,this.userView.getA0100(),planid,"Usr");
						}
						else
						{
				        	appealObjectList=bo.getAppealObjectInfo(followBodyId, "Usr", aObject_id, planid, posID, targetAppMode,object_id);
	                        if(appealObjectList!=null&&appealObjectList.size()>0)
	                       {
	                        	String str=(String)appealObjectList.get(0);
	                        	//b0110+"^"+e0122+"^"+e01a1+"^"+a0101+"^"+a0100+"/"+body_id
	                        	String appealObject_id=str.substring(str.indexOf("/")-8, str.indexOf("/"));
	                        	bo.appealSpObject(object_id,appealObject_id,this.userView.getA0100(),planid,"Usr",url_p);
	                       }else {
	                           if("0".equals(targetAppMode)|| "1".equals(kh_relations))
	                           {
	                        	   int currentLevel= bo.getCurrentLevel(followBodyId);
	                   			 boolean flag=true;
	               				 for(int j=currentLevel+1;j<=Integer.parseInt(targetMakeSeries);j++)
	               				 {
	               					followBodyId=bo.getfollowBodyid(followBodyId);
	               					appealObjectList=bo.getAppealObjectInfo(followBodyId, "Usr", aObject_id, planid, "", "0",object_id);		
	               					if(appealObjectList!=null&&appealObjectList.size()>0)
	                                {
	                                    String str=(String)appealObjectList.get(0);
	                                  	//b0110+"^"+e0122+"^"+e01a1+"^"+a0101+"^"+a0100+"/"+body_id
	                                  	String appealObject_id=str.substring(str.indexOf("/")-8, str.indexOf("/"));
	                                  	bo.appealSpObject(object_id,appealObject_id,this.userView.getA0100(),planid,"Usr",url_p);
	                                  	flag=false;
	                                  	break;
	                                }
	               				}
	               				if(flag)
	               				{
	               					bo.approveSpObject(object_id,this.userView.getA0100(),planid,"Usr");
	               				    bo.optPersonalComment("2");
	               				}
	               				
	                          }
	                          else
	                          {
	                            	bo.approveSpObject(object_id,this.userView.getA0100(),planid,"Usr");
	                            	bo.optPersonalComment("2");
	                          }
	                       }
						}
					}
	    		}
			}
			this.getFormHM().put("info", SafeCode.encode("".equals(info)?"1":info));
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
			rowSet.close();
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
		String a0101="";
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
				if(rowSet.getString("a0101")!=null)
					a0101=rowSet.getString("a0101");
			}
			rowSet.close();
			abean.set("sp_flag", sp_flag);
			abean.set("kh_relations", kh_relations);
			abean.set("a0101",a0101);
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
				abean.set("id",rowSet.getString("id"));
				abean.set("body_id",rowSet.getString("body_id"));
				abean.set("object_id",rowSet.getString("object_id"));
				abean.set("mainbody_id",rowSet.getString("mainbody_id"));
				abean.set("status",rowSet.getString("status")!=null?rowSet.getString("status"):"");
				abean.set("a0101",rowSet.getString("a0101"));
				abean.set("know_id",rowSet.getString("know_id")!=null?rowSet.getString("know_id"):"");
				abean.set("whole_grade_id",rowSet.getString("whole_grade_id")!=null?rowSet.getString("whole_grade_id"):"");
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return abean;
	}
	
	
	
	

}
