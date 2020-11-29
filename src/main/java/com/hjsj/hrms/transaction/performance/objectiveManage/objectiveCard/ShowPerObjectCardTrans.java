package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.interview.PerformanceInterviewBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ShowPerObjectCardTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)hm.get("opt");
			String planids=(String)hm.get("planids");
			String object_id=(String)hm.get("object_id");
			String model=(String)hm.get("model");
			String body_id=(String)hm.get("body_id");
			String relatingTargetCard=(String)hm.get("relatingTargetCard");
			String planid=(String)this.getFormHM().get("planid");
			ArrayList planList=getPlanList(planids);
			
			if(object_id!=null && object_id.trim().length()>0 && "~".equalsIgnoreCase(object_id.substring(0,1))) // JinChunhai 2012-06-26 如果是通过转码传过来的需解码
	        { 
	        	String _temp = object_id.substring(1); 
	        	object_id = PubFunc.convert64BaseToString(SafeCode.decode(_temp));
	        }
			
			String operator="0";
			if(hm.get("operator")!=null&& "1".equalsIgnoreCase((String)hm.get("operator")))
			{
				operator="1";
				planid=((CommonData)planList.get(0)).getDataValue();
				body_id="1";
				model="3";
				if(SystemConfig.getPropertyValue("show_objectcard_result")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("show_objectcard_result")))
					model="5";
				
				hm.remove("operator");
			}
			if(relatingTargetCard!=null && relatingTargetCard.trim().length()>0 && "3".equalsIgnoreCase(relatingTargetCard))
			{
				model = "4";
				opt = "2";
			}
			if(hm.get("is360")!=null&& "1".equalsIgnoreCase((String)hm.get("is360")))
			{
				operator="1";
				hm.remove("is360");
			}
			
			//ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),planid,object_id,this.getUserView(),Integer.parseInt(opt),model);
			ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),planid,object_id,this.getUserView(),model,body_id,opt);
			bo.setIsPerformanceShow(operator);
			
			bo.setIsShowRelationCard("1");
			bo.setBody_id(body_id);
			String html=bo.getObjectCardHtml();
			//opt=String.valueOf(bo.getOpt());
			
			//生成客户描述信息
			String desc="";
			LazyDynaBean personnelInfo=null;
			if("2".equals(String.valueOf(bo.getPlan_vo().getInt("object_type"))))
			{
				personnelInfo=bo.getPersonnelInfo("Usr",object_id);
				if(((String)personnelInfo.get("b0110")).length()>0)
					desc+=AdminCode.getCodeName("UN",(String)personnelInfo.get("b0110"))+"&nbsp;&nbsp;&nbsp;";
				if(((String)personnelInfo.get("e0122")).length()>0)
					desc+=AdminCode.getCodeName("UM",(String)personnelInfo.get("e0122"))+"&nbsp;&nbsp;&nbsp;";
				if(((String)personnelInfo.get("e01a1")).length()>0)
					desc+=AdminCode.getCodeName("@K",(String)personnelInfo.get("e01a1"))+"&nbsp;&nbsp;&nbsp;";
				desc+=(String)personnelInfo.get("a0101");
				
				if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))
				{
					if(SystemConfig.getPropertyValue("per_object_infofield")!=null&&SystemConfig.getPropertyValue("per_object_infofield").trim().length()>0)
					{
						String per_object_infofieldContent="";
						String per_object_infofield=SystemConfig.getPropertyValue("per_object_infofield");
						desc+="&nbsp;&nbsp;&nbsp;职级:";
						StringBuffer sql = new StringBuffer();
						StringBuffer leftjoin = new StringBuffer("");
						HashMap fieldSetMap = new HashMap();
						String[] arr = per_object_infofield.split(",");
						boolean flag = false;
						for(int i=0;i<arr.length;i++)
						{
								if(arr[i]==null|| "".equals(arr[i]))
									continue;
								FieldItem item = DataDictionary.getFieldItem(arr[i].toLowerCase());
								if(item!=null&& "1".equals(item.getUseflag()))
								{
									 
									if("2".equals(String.valueOf(bo.getPlan_vo().getInt("object_type")))&&item.getFieldsetid().startsWith("A"))
									{
										sql.append(" "+item.getItemid()+",");
							     		if(!"A01".equalsIgnoreCase(item.getFieldsetid())&&fieldSetMap.get(item.getFieldsetid().toUpperCase())==null)
							     		{
									  //   	leftjoin.append(" left join USR"+item.getFieldsetid()+" on USRA01.a0100=USR"+item.getFieldsetid()+".A0100 and ");
									  //   	leftjoin.append("USR"+item.getFieldsetid()+".i9999=(select max(i9999) from USR"+item.getFieldsetid());
									  //   	leftjoin.append(" a where a.a0100=USR"+item.getFieldsetid()+".A0100)"); 	
									     	leftjoin.append(" left join  (select * from  USR"+item.getFieldsetid()+" b where b.i9999=(select max(i9999) from USR"+item.getFieldsetid()+" a where a.a0100=b.A0100 )  )  USR"+item.getFieldsetid()+"   on USRA01.a0100=USR"+item.getFieldsetid()+".A0100 ");
									     	fieldSetMap.put(item.getFieldsetid().toUpperCase(), "1");
							     		}
							     		
									}
								}
						}
					  
					    ContentDAO dao  = new ContentDAO(this.getFrameconn());
					    StringBuffer sql_sb = new StringBuffer();
						if(sql.toString().length()>0){
								sql.setLength(sql.length()-1);
						     	sql_sb.append(" select "+sql.toString()+" from ");
								if("2".equals(String.valueOf(bo.getPlan_vo().getInt("object_type"))))
								{
									sql_sb.append(" USRA01 ");
									sql_sb.append(leftjoin);
									sql_sb.append(" where USRA01.A0100='"+object_id+"'");
								}
								 
					    		this.frowset= dao.search(sql_sb.toString());
					    	    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					        	while(this.frowset.next())
					         	{
				  		    		StringBuffer value = new StringBuffer();
						    		for(int i=0;i<arr.length;i++)
						    		{
							    		if(arr[i]==null|| "".equals(arr[i]))
							    			continue;
							    		FieldItem item = DataDictionary.getFieldItem(arr[i].toLowerCase());
						    			if(item!=null&& "1".equals(item.getUseflag()))
						    			{
									    	  if("A".equalsIgnoreCase(item.getItemtype()))
							    			  {
										    	  if(this.frowset.getString(item.getItemid())!=null)
										    	  {
											          if(item.isCode())
										    	    	  value.append(AdminCode.getCodeName(item.getCodesetid(), this.frowset.getString(item.getItemid())));
										    	      else
										    	    	  value.append(this.frowset.getString(item.getItemid()));
									    		  }
									    	  }else if("D".equalsIgnoreCase(item.getItemtype())){
									    		  if(this.frowset.getDate(item.getItemid())!=null)
									    			  value.append(format.format(this.frowset.getDate(item.getItemid())));
									    	  }else if("N".equalsIgnoreCase(item.getItemtype())){
										     	  if(this.frowset.getString(item.getItemid())!=null)
										    	  {
									        		  if(item.getDecimalwidth()==0)
									    	    		  value.append(this.frowset.getInt(item.getItemid()));
									    	    	  else
									    	    		  value.append(PubFunc.round(this.frowset.getString(item.getItemid()), item.getDecimalwidth()));
								    			  }
								    		  }else
								    		  {
								    			  if(this.frowset.getString(item.getItemid())!=null)
								    				  value.append(this.frowset.getString(item.getItemid()));
							    			  }
							    		}
							    	}
							    	per_object_infofieldContent=value.toString();
				    			}
							}
						   desc+=per_object_infofieldContent;
					}
					
				}
				
			}
			else if("1".equals(String.valueOf(bo.getPlan_vo().getInt("object_type"))))
			{
				if(AdminCode.getCodeName("UM", object_id)!=null&&AdminCode.getCodeName("UM", object_id).length()>0)
					desc+=AdminCode.getCodeName("UM", object_id);
				if(AdminCode.getCodeName("UN", object_id)!=null&&AdminCode.getCodeName("UN", object_id).length()>0)
					desc+=AdminCode.getCodeName("UN", object_id);
			}
			
			
			
			
			String personalComment="";  //个人总结脚本
			personalComment=bo.getPersonalCommentScript(0);
			String personalComment2="";  //个人总结脚本
			personalComment2=bo.getPersonalCommentScript(1);
			String targetDeclare="";    //指标说明脚本
			targetDeclare=bo.getTargetDeclareScript();
			this.getFormHM().put("desc",desc);
			this.getFormHM().put("personalComment", personalComment);
			this.getFormHM().put("personalComment2",personalComment2);
			this.getFormHM().put("targetDeclare",targetDeclare);
			this.getFormHM().put("objectSpFlag",bo.getObjectSpFlag());
			this.getFormHM().put("editOpt",bo.getMainbodyBean()!=null&& "2".equals((String)bo.getMainbodyBean().get("status"))?"0":"1");
			this.getFormHM().put("perPointNoGrade",bo.getPerPointNoGrade());
			this.getFormHM().put("noGradeItem",bo.getNoGradeItem());
			this.getFormHM().put("status",bo.get_TemplateVo().getString("status"));
			this.getFormHM().put("leafItemList", bo.getLeafItemList());
			this.getFormHM().put("plan_objectType",String.valueOf(bo.getPlan_vo().getInt("object_type")));
			this.getFormHM().put("model",model);  // 1:团对  2:我的目标   3:目标制订  4.目标评估
			this.getFormHM().put("opt", opt!=null?opt:"0");
			this.getFormHM().put("planid", planid);
			
			String tabids = bo.getTabids(planid);
			this.getFormHM().put("isCard", "".equals(tabids.trim())?"0":"1");
			this.getFormHM().put("tabIDs", tabids);
			if("1".equals((String)this.getFormHM().get("isCard")))
			{
				PerformanceInterviewBo ab = new PerformanceInterviewBo(this.getFrameconn());
				ArrayList tabList = ab.getTabids(planid,this.userView);
				this.getFormHM().put("tabList", tabList);
			}
			else
				this.getFormHM().put("tabList",new ArrayList());
			
			this.getFormHM().put("object_id", object_id);
			this.getFormHM().put("cardHtml",html);
			this.getFormHM().put("isEntireysub",((String)bo.getPlanParam().get("isEntireysub")).toLowerCase());
			this.getFormHM().put("body_id",body_id);
			this.getFormHM().put("planParam", bo.getPlanParam());
			
			if(bo.getPerObject_vo().getString("currappuser")!=null&&bo.getPerObject_vo().getString("currappuser").length()>0)
				this.getFormHM().put("currappuser",bo.getPerObject_vo().getString("currappuser"));
			else
				this.getFormHM().put("currappuser","");
			this.getFormHM().put("isApprove",bo.getIsApproveFlag());
			if(bo.getIsAdjustPoint())
				this.getFormHM().put("isAdjustPoint","True");
			else
				this.getFormHM().put("isAdjustPoint","False");
			this.getFormHM().put("planList", planList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public ArrayList getPlanList(String planids)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rowSet=dao.search("select * from per_plan where plan_id in ("+planids.substring(1)+")");
			while(rowSet.next())
			{
				CommonData d=new CommonData(rowSet.getString("plan_id"),rowSet.getString("name"));
				list.add(d);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
}
