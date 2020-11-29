package com.hjsj.hrms.businessobject.performance.objectiveManage;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;

import javax.sql.RowSet;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>Title:SetUnderlingObjectiveBo.java</p>
 * <p>Description>:SetUnderlingObjectiveBo.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-5-30 下午03:56:27</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */

public class SetUnderlingObjectiveBo 
{
	private Connection conn;
	private UserView view;
	private int maxLeaderLay;//审批主体最高层级数
	
	public int getMaxLeaderLay() {
		return maxLeaderLay;
	}
	public void setMaxLeaderLay(int maxLeaderLay) {
		this.maxLeaderLay = maxLeaderLay;
	}
	public SetUnderlingObjectiveBo(Connection conn)
	{
		this.conn = conn;		
	}
	public SetUnderlingObjectiveBo(Connection conn,UserView view)
	{
		this.conn = conn;
		this.view=view;
	}
	public boolean isMarket(String object_id,String plan_id)
	{
		boolean flag=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select plan_id from per_mainbody where plan_id="+plan_id+" and object_id='"+object_id+"' and (status=2 or status=1)");
			while(rowSet.next())
			{
				flag=true;
				break;
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return flag;
	}
	public HashMap getSubscore(String plan_id)
	{
		HashMap map = new HashMap();
		RowSet rowSet=null;
		try
		{

			ContentDAO dao=new ContentDAO(this.conn);
			rowSet=dao.search("select plan_id,object_id from per_mainbody where plan_id="+plan_id+" and (status=2 or status=1)");
			while(rowSet.next())
			{
				map.put(rowSet.getString("object_id"), "1");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(rowSet!=null)
			{
				try
				{
					rowSet.close();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return map;
	}
	
	// 获得每一个考核对象的所有考核主体的打分状态
	public HashMap getObject_mainbodyType(String plan_id)
	{
		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try{
			
			StringBuffer sql = new StringBuffer();						
			sql.append("select object_id,status from per_mainbody where plan_id='"+plan_id+"' GROUP BY object_id,status order by object_id,status ");															
			rowSet = dao.search(sql.toString());
			while(rowSet.next())
			{	
				String object_id = rowSet.getString("object_id");
				String status = rowSet.getString("status");
				if(status==null || status.trim().length()<=0)
					status = "0";
				map.put(object_id,status);				
			}
			if(rowSet!=null)    		
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	public HashMap getSelfRejectObject(UserView userView,String planid)
	{
	    	HashMap map =new HashMap();
	    	RowSet rs = null;
	    	try
	    	{
	    		StringBuffer buf = new StringBuffer();
	    		buf.append("select object_id,mainbody_id,plan_id ");
	    		buf.append("from per_mainbody where ");
	    		buf.append("sp_flag='07' and mainbody_id='"+userView.getA0100()+"'");
	    		buf.append(" and body_id<>'-1' and body_id<>'5' ");//本人和团队负责人不要
	    		if(!"-1".equals(planid))
	    			buf.append(" and plan_id="+planid);
	    		ContentDAO dao = new ContentDAO(this.conn);
	    		rs = dao.search(buf.toString());
	    		while(rs.next())
	    		{
	    			String object_id=rs.getString("object_id");
	    			String mainbody_id=rs.getString("mainbody_id");
	    			String plan_id=rs.getString("plan_id");
	    			map.put(object_id+mainbody_id+plan_id, "1");
	    		}	
	    	}
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	    	finally{
	    		try
	    		{
	    			if(rs!=null)
	    			   rs.close();
	    		}
	    		catch(Exception e)
	    		{
	    			e.printStackTrace();
	    		}
	    	}
	    	return map;
	    }
	private ArrayList deptList;
	public ArrayList getInPlanSubordinateStaff(String posID,String a0100,String plan_id,ArrayList dbname,String sp_flag,String year,int type,UserView userView,String deptid)
	{
		ArrayList list = new ArrayList();
		try
		{
			StringBuffer sql = new StringBuffer();
			LoadXml parameter_content = null;
	        if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"")==null)
			{
					
	         	parameter_content = new LoadXml(this.conn,plan_id+"");
				BatchGradeBo.getPlanLoadXmlMap().put(plan_id+"",parameter_content);
			}
			else
			{
				parameter_content=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"");
			}
			Hashtable params = parameter_content.getDegreeWhole();
			/**调整即新建参数*/
			String taskAdjustNeedNew=(String)params.get("taskAdjustNeedNew");
			/**任何领导都可以调整目标卡*/
			String allowLeadAdjustCard=(String)params.get("allowLeadAdjustCard");
			if(allowLeadAdjustCard==null)
				allowLeadAdjustCard="false";
			if(taskAdjustNeedNew==null)
				taskAdjustNeedNew="false";
		    String targetMakeSeries=(String)params.get("targetMakeSeries");
		    String SpByBodySeq="False";
			if(params.get("SpByBodySeq")!=null)
				SpByBodySeq=(String)params.get("SpByBodySeq");
		    AnalysePlanParameterBo appbo=new AnalysePlanParameterBo(this.conn);
    		Hashtable ht_table=appbo.analyseParameterXml();
    		String AllowLeaderTrace="false";
    		//trace_sp_flag
    		if(params!=null)
    			AllowLeaderTrace=(String)params.get("AllowLeaderTrace");
    		if(ht_table!=null&&ht_table.get("AllowLeaderTrace")!=null&& "false".equalsIgnoreCase(AllowLeaderTrace))
				AllowLeaderTrace=(String)ht_table.get("AllowLeaderTrace");
			sql.append("select po.kh_relations,po.b0110,po.e0122,po.e01a1,po.a0101,po.object_id,po.sp_flag,po.trace_sp_flag,po.plan_id,pp.status,po.currappuser from per_object po left join per_plan pp on po.plan_id=pp.plan_id where 1=1 ");//left join per_plan_body ppb on pp.plan_id=ppb.plan_id 
			if(!"-1".equals(plan_id))
			{
				sql.append(" and po.plan_id="+plan_id);
			}
			if("-1".equals(sp_flag))
			{
				
			} else if("-2".equals(sp_flag))
			{
				sql.append(" and (po.sp_flag<>'06' or po.sp_flag is null)");
			}
			else
			{
				
				if("01".equals(sp_flag))
	    			sql.append("and (po.sp_flag='"+sp_flag+"' or po.sp_flag is null)");
				else
					sql.append(" and po.sp_flag='"+sp_flag+"'");
			}
			if(!"-1".equals(deptid))
				sql.append(" and po.e0122 like '"+deptid+"%' ");
			sql.append(" order by po.b0110,po.e0122,po.e01a1,po.A0000");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			ArrayList deptList = new ArrayList();
			deptList.add(new CommonData("-1","全部"));
			rs = dao.search(sql.toString());
			ArrayList dbnameList = new ArrayList();
			dbnameList.add("USR");
			RenderRelationBo bo = new RenderRelationBo(conn,userView);
			HashMap mainbodyMap=getMainbodyBean(plan_id);
			HashMap infomap=bo.getPer_MainBodyInfo(dbnameList, posID, 3, plan_id);
			HashMap p04map=this.getp04(plan_id, dao);
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				display_e0122="0";
			HashMap mMap=null;
			HashMap iMap=null;
			HashMap pMap=null;
			boolean priv=false;
			HashMap objMap =new HashMap();
			FieldItem aitem  = DataDictionary.getFieldItem("score_org");
			if(aitem!=null&& "1".equals(aitem.getState()))
			{
				priv=true;
			}
			String TargetDefineItem="";
		    if(ht_table!=null)
		    {
		        if(ht_table.get("TargetDefineItem")!=null&&((String)ht_table.get("TargetDefineItem")).trim().length()>0)
			     	TargetDefineItem=(","+(String)ht_table.get("TargetDefineItem")+",").toUpperCase();
		    }
		    if(params.get("TargetTraceEnabled")!=null&& "True".equalsIgnoreCase((String)params.get("TargetTraceEnabled")))
			{
				if(params.get("TargetDefineItem")!=null&&((String)params.get("TargetDefineItem")).trim().length()>0)
					TargetDefineItem=(","+((String)params.get("TargetDefineItem")).trim()+",").toUpperCase();   //目标卡指标
			}
			HashMap fieldMap = new HashMap();
			HashMap subscoremap =null;
			if(!"-1".equals(plan_id))
				subscoremap=this.getSubscore(plan_id);
			HashMap selfMap = this.getSelfRejectObject(userView, plan_id);
			HashMap e0122Map = new HashMap();			
			// 获得每一个考核对象的所有考核主体的打分状态
			HashMap obj_mainMap = this.getObject_mainbodyType(plan_id);
			
			
			
			//==============================目标卡多评分人=======================
			
			
			ObjectCardBo cardbo=new ObjectCardBo(this.conn, userView, plan_id);
			
			
			HashMap raterMap=new HashMap<String, String>();
			if(cardbo.isOpenGrade_Members()){//找到所有评价人是当前用户的考核对象
				//获取当前审批人不是自己 并且评价人指定了自己的考核对象
				StringBuffer strSql=new StringBuffer();
				strSql.append(" select po.object_id,pp.status from per_object po inner join per_plan pp on po.plan_id=pp.plan_id ");
				strSql.append(" inner join p04 on po.plan_id=p04.plan_id and po.object_id=p04.A0100  inner join per_grade_members on p04.p0400=per_grade_members.P0400");
				strSql.append(" where po.plan_id="+plan_id+" and method='2' and  ( status='5' or status='8') and (po.sp_flag='02' or po.sp_flag='07') and upper(per_grade_members.NBASE)='USR'");
				strSql.append(" and per_grade_members.A0100='"+userView.getA0100()+"' and "+Sql_switcher.isnull("po.currappuser", "''")+"<>'"+userView.getA0100()+"' group by po.object_id,pp.status ");
				
				RowSet rowset=dao.search(strSql.toString());
				while(rowset.next()){
					raterMap.put(rowset.getString("object_id"), rowset.getString("status"));
				}
				
			}
			
			
			
			
			
			while(rs.next())
			{
				//boolean isgrade=false;
				String a_a0100=rs.getString("object_id");
				String planid=rs.getString("plan_id");
				String status=rs.getString("status")==null?"":rs.getString("status");
		        if(BatchGradeBo.getPlanLoadXmlMap().get(planid+"")==null)
				{
						
		         	parameter_content = new LoadXml(this.conn,planid+"");
					BatchGradeBo.getPlanLoadXmlMap().put(planid+"",parameter_content);
				}
				else
				{
					parameter_content=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(planid+"");
				}
				 params = parameter_content.getDegreeWhole();
				 if(params.get("SpByBodySeq")!=null)
					 SpByBodySeq=(String)params.get("SpByBodySeq");
				if("true".equalsIgnoreCase(SpByBodySeq)){
					HashMap mm=getObjectBySeq(planid, 1);
					objMap.put(planid, mm);
				}
				boolean isSubScore=false;
				if(subscoremap!=null&&subscoremap.get(a_a0100)!=null)
					isSubScore=true;
				String market=rs.getString("kh_relations");
				if("-1".equals(plan_id))
				{
					if(mMap==null)
						mMap = new HashMap();
					if(iMap == null)
						iMap = new HashMap();
					if(pMap == null)
						pMap = new HashMap();
					if(mMap.get(planid)==null)
					{
						mainbodyMap = getMainbodyBean(planid);
						mMap.put(planid, mainbodyMap);
					}
					else
					{
						mainbodyMap=(HashMap)mMap.get(planid);
					}
					if(iMap.get(planid)==null)
					{
						infomap=bo.getPer_MainBodyInfo(dbnameList, posID, 3, planid);
						iMap.put(planid, infomap);
					}
					else
					{
						infomap=(HashMap)iMap.get(planid);
					}
					if(pMap.get(planid)==null)
					{
						p04map=this.getp04(planid, dao);
						pMap.put(planid, p04map);
					}
					else
					{
						p04map=(HashMap)pMap.get(planid);
					}
				}
				/**=1是非标准的考核关系*/
				if(market==null)
					market="0";
				int tzCount=0;
				if(p04map.get(a_a0100+plan_id)!=null&&!"".equals((String)p04map.get(a_a0100+plan_id))&&!"null".equalsIgnoreCase((String)p04map.get(a_a0100+plan_id)))
					tzCount=Integer.parseInt(((String)p04map.get(a_a0100+plan_id)));
				/**isSP=0是只能打分而不参与审批=1是参与审批*/
				String isSP="0";
				String currsp="0";
				for(int i=0;i<dbname.size();i++)
				{
					String nbase = (String)dbname.get(i);
					String level="";
					if("true".equalsIgnoreCase(SpByBodySeq)){
						HashMap mm=(HashMap)objMap.get(planid);
						if(mm.get(a_a0100)!=null)
						{
						   LazyDynaBean abean=(LazyDynaBean)mm.get(a_a0100);
						   isSP=(String)abean.get("isSP");
						   level=(String)abean.get("level");
						}else{
							if(raterMap.containsKey(a_a0100)){//判断是否为被制定为评价人
								isSP="0";
								level="-5";//为评价人
								currsp="0";
							}else
								continue;
						}
					}
					else if(("0".equals(market)&&infomap.containsKey(nbase+a_a0100))||("1".equals(market)&&mainbodyMap.containsKey(userView.getA0100()+a_a0100)&&this.isCanSP(plan_id, a_a0100, userView.getA0100())))
					{
						
						if("0".equals(market))
						{
							LazyDynaBean abean=(LazyDynaBean)infomap.get(nbase+a_a0100);
							level=(String)abean.get("level");
							isSP=(String)abean.get("isSP");
						}
						else
						{
							LazyDynaBean abean=(LazyDynaBean)mainbodyMap.get(userView.getA0100()+a_a0100);
							level=(String)abean.get("level");
							int mlevel=this.getTargetMakeSeriesLevel(Integer.parseInt(level));
							if(mlevel<=Integer.parseInt(targetMakeSeries))
						    	isSP="1";
						}
					}else if(raterMap.containsKey(a_a0100)){//判断是否为被制定为评价人
						isSP="0";
						level="-5";//为评价人
						currsp="0";
					}else{
					
						continue;
					}
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("level", level);
					bean.set("b0110",AdminCode.getCodeName("UN",rs.getString("b0110")));
					bean.set("e01a1",AdminCode.getCodeName("@K",rs.getString("e01a1")));
					if("0".equals(display_e0122))
					{
					    bean.set("e0122",AdminCode.getCodeName("UM",rs.getString("e0122")));
					}
					else
					{
						CodeItem item=AdminCode.getCode("UM",rs.getString("e0122")==null?"":rs.getString("e0122"),Integer.parseInt(display_e0122));
			    	    if(item!=null)
			    	    {
			    	    	bean.set("e0122",item.getCodename());
			        	}
			    	    else
			    	    {
			    	    	bean.set("e0122",AdminCode.getCodeName("UM",rs.getString("e0122")));
			    	    }
					}
					
					String currappuser=rs.getString("currappuser");
					if(currappuser!=null&&!"".equals(currappuser)&&currappuser.equals(userView.getA0100()))
						currsp="1";
					
					/**启用考核机构时，不再打分状态，不是考核对象的上级，上上级，三级，四级的考核主体，不能看见该考核对象*/
					if("0".equals(isSP)||(priv&&TargetDefineItem!=null&&TargetDefineItem.trim().length()>0&&TargetDefineItem.toUpperCase().indexOf("SCORE_ORG")!=-1&&(!"1".equals(level)&&!"0".equals(level)&&!"-1".equals(level)&&!"-2".equals(level))&&!"4".equals(status)&&!"6".equals(status)&&!"7".equals(status)))
					{
						if("1".equals(isSP)&& "true".equalsIgnoreCase(SpByBodySeq)){
							
						}else{
							if(raterMap.containsKey(a_a0100)){//判断是否为被制定为评价人
								isSP="0";
								level="-5";//为评价人
								currsp="0";
							}else
					    	 continue;
						}
					}
					String spf=rs.getString("sp_flag");
						/**前台页面展示
						 * flag=6：查看
						 * flag=7：布置
						 * flag=8：布置（调整）*/
					if(spf==null)
						spf="01";
					
						/*********************************************************************/
						/***/
					String cardEdit = "0";
					String opt="0";
					String flag="6";
					String spFlagDesc=MyObjectiveBo.getSpflagDesc(spf);//AdminCode.getCodeName("23",spf);
    				if("07".equals(spf))//spFlagDesc="退回/意见";
    					spFlagDesc+="/意见";  
    				String isReject="0";
    				if(userView.getA0100().equalsIgnoreCase(currappuser)&&selfMap.get(a_a0100+userView.getA0100()+planid)!=null)
    				{
    					spFlagDesc=MyObjectiveBo.getSpflagDesc("07");
    					spFlagDesc+="/意见";  
    					isReject="1";
    				}
    				if(isSubScore)
    				{
    					if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
			    		{
				    		spFlagDesc=spFlagDesc+"(调整后)";
	    				}
    					currsp="0";
						opt="0";
						flag="6";
    				}
    				else if("8".equals(status))
					{
						if("true".equalsIgnoreCase(allowLeadAdjustCard))
						{
							// 是否有主体给当前对象打过分
							String editCard = (String)obj_mainMap.get(a_a0100);	
							String targetAllowAdjustAfterApprove =(String)params.get("TargetAllowAdjustAfterApprove");
							if("01".equals(spf)&& "1".equals(isSP))
							{
								if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
					    		{
						    		spFlagDesc=spFlagDesc+"(调整后)";
			    				}
			    				opt="1";
			    				flag="7";
			     				currsp="1";
							}
							else if("02".equals(spf))
							{
								if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
					    		{
						    		spFlagDesc=spFlagDesc+"(调整后)";
			    				}
								if(currappuser!=null&&!"".equals(currappuser)&&currappuser.equals(userView.getA0100()))
								{
									opt="1";
				    				flag="7";
				    				currsp="1";
								}
								else
								{
									opt="0";
				    				flag="6";
								}
							}
							// 已批准的目标卡也允许调整  JinChunhai 2013.03.25
							else if("true".equalsIgnoreCase(targetAllowAdjustAfterApprove) && ("03".equals(spf)) && ((editCard==null || (editCard!=null && "0".equals(editCard)))))
							{																
								cardEdit = "1";
							}
							else
							{
								if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
					    		{
						    		spFlagDesc=spFlagDesc+"(调整后)";
			    				}
			    				opt="0";
			    				flag="6";
							}
						}
						else
						{
							if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
				    		{
					    		spFlagDesc=spFlagDesc+"(调整后)";
		    				}
							if(currappuser!=null&&!"".equals(currappuser)&&currappuser.equals(userView.getA0100()))
							{
								opt="1";
			    				flag="7";
			    				currsp="1";
							}
							else
							{
								opt="0";
			    				flag="6";
							}			
						}
				    }
					/**暂停只能查看*/
					else if("5".equals(status))
					{
						opt="0";
						flag="6";
						currsp="0";
					}
					else
					{
						if(tzCount>0&& "false".equalsIgnoreCase(taskAdjustNeedNew))
 	    	         	{
 	    	         		spFlagDesc=spFlagDesc+"(调整后)";
 	    	         	}
						if(!"03".equals(spf))
							currsp="0";
						opt="0";
						flag="6";
							
					}
						/**********************************************************************/
    				
    				if(!"5".equals(status)&&("0".equals(currsp)&&raterMap.containsKey(a_a0100))){//如果具有查看权限　并且被指定为了评价人　那么显示评价按钮　zhanghua
    					currsp="2";
    					opt="3";
    				}
    				
    				bean.set("cardEdit", cardEdit);
					bean.set("currsp", currsp);
					bean.set("flag",flag);
					bean.set("opt", opt);
				    bean.set("sp_flag", spFlagDesc);
					bean.set("sp", spf);
					String traceFlagDesc="";
					String trace_flag="";
                    if("03".equals(spf))
                    {
                       if("true".equalsIgnoreCase(AllowLeaderTrace))
                       {
                         	trace_flag=rs.getString("trace_sp_flag");
                         	if(trace_flag==null)
                         		trace_flag="01";
                         	if("07".equals(trace_flag))
                         		traceFlagDesc="退回";
                         	else
                         		traceFlagDesc=AdminCode.getCodeName("23", trace_flag);
                         	traceFlagDesc="跟踪指标"+traceFlagDesc;
                        }
                        else
                        {
                          traceFlagDesc=spFlagDesc;
                          trace_flag=spf;
                        }
                    }
                    bean.set("trace_flag", trace_flag);
                    bean.set("tsp", traceFlagDesc);
					bean.set("a0101",rs.getString("a0101"));
					bean.set("planid",rs.getString("plan_id"));
					bean.set("a0100",rs.getString("object_id"));
					bean.set("mda0100", PubFunc.encryption(rs.getString("object_id")));
					bean.set("mdplanid", PubFunc.encryption(rs.getString("plan_id")));
					String xx=PubFunc.encrypt(rs.getString("plan_id"))+"`"+PubFunc.encrypt(rs.getString("object_id"))+"`"+spf+"`"+(rs.getString("currappuser")==null?"":rs.getString("currappuser"))+"`"+level+"`"+status;
		         	bean.set("bacthdata", xx);
		         	bean.set("isReject", isReject);
						/**员工目标不显示*/
						/*if(priv&&TargetDefineItem!=null&&TargetDefineItem.trim().length()>0&&TargetDefineItem.toUpperCase().indexOf("SCORE_ORG")!=-1)
						{
						    if(bo.isByOrg(userView.getA0100(), rs.getString("object_id"), "", Integer.parseInt(status)))
							{
								HashMap amap = null;
                 				if(fieldMap.get(planid+"")!=null)
                 				{
                 					amap=(HashMap)fieldMap.get(planid+"");
                 				}
                 				else
                 				{
                 					amap=bo.getKhOrgField(planid+"", userView);
                 					fieldMap.put(planid+"", amap);
                 				}
                 				if(amap.get(plan_id+rs.getString("object_id"))==null)
                 					continue;
							}
						}*/
					list.add(bean);
					String e0122=rs.getString("e0122")==null?"":rs.getString("e0122");
					if(e0122Map.get(e0122)==null)
					{
						e0122Map.put(e0122, "1");
						deptList.add(new CommonData(rs.getString("e0122")==null?"":rs.getString("e0122"),AdminCode.getCodeName("UM", rs.getString("e0122"))));
					}
				}
			}
			this.setDeptList(deptList);
			rs.close();
			
			//
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} 
		return list;
	}
	
	private String objMainbodys = "";
	public ArrayList getInPlanObjectStaff(String plan_id,ArrayList dbname,String sp_flag,String object_type,UserView userView)
	{
		ArrayList list = new ArrayList();
		HashMap hashmap = new HashMap();
		String objMainbodys = "";
		try
		{
			// 取某考核计划下的考核对象对应的考核主体
//			HashMap mainbodyNumap = getPlanMainbodyNum(plan_id);
			
			HashMap mainbodyNumap = getPlanObjectMainbody(plan_id);
			
			LoadXml loadxml=null;
			if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id)==null)
			{
				loadxml=new LoadXml(this.conn,plan_id);
				BatchGradeBo.getPlanLoadXmlMap().put(plan_id,loadxml);
			}
			else
				loadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id);		
			Hashtable planParam=loadxml.getDegreeWhole();
			
						
			StringBuffer sql = new StringBuffer();
					
		    AnalysePlanParameterBo appbo=new AnalysePlanParameterBo(this.conn);
    		Hashtable ht_table=appbo.analyseParameterXml();
    		String AllowLeaderTrace="false";
    	
    		if(ht_table!=null&&ht_table.get("AllowLeaderTrace")!=null&& "false".equalsIgnoreCase(AllowLeaderTrace))
				AllowLeaderTrace=(String)ht_table.get("AllowLeaderTrace");
			sql.append("select kh_relations,b0110,e0122,e01a1,a0101,object_id,sp_flag,trace_sp_flag,plan_id,currappuser,summarizes from per_object where 1=1 ");//left join per_plan_body ppb on pp.plan_id=ppb.plan_id 
			if(!"-1".equals(plan_id))
			{
				sql.append(" and plan_id="+plan_id+" ");
			}		
				
			if("-2".equals(sp_flag)){
//				sql.append(" and (sp_flag='06' or sp_flag ='03')");//状态：03已批06结束 

			}else if("03".equals(sp_flag))
	    		sql.append("and (sp_flag='"+sp_flag+"' )");
			else if("06".equals(sp_flag))
	    		sql.append("and (sp_flag='"+sp_flag+"' )");
			else if("01".equals(sp_flag))
	    		sql.append("and (sp_flag='01' or sp_flag is null )");
			else
				sql.append("and (sp_flag='"+sp_flag+"' )");
//			if(!userView.isSuper_admin())
//    	    {
//				sql.append(this.getPrivWhereSQL(userView, object_type, "usr"));
//    	    }
				
			//登录用户权限范围内考核对象或者是登录用户的考核对象
			StringBuffer objWhl = new StringBuffer(" and (");
			PerformanceImplementBo pb = new PerformanceImplementBo(this.conn);
			String _str=pb.getPrivWhere(userView);
			if(_str.trim().length()>0)
			{
				if(_str.trim().startsWith("and"))
				{
					objWhl.append("( "+_str.replaceFirst("and", "")+")");
					objWhl.append(" or ");
				}else{
					objWhl.append(" 1=1 or  ");
				}
			}else{
				objWhl.append(" 1=1 or  ");
			}
			objWhl.append("( exists (select object_id from per_mainbody where plan_id="+plan_id+" and mainbody_id='"+userView.getA0100()+"' and per_object.object_id=per_mainbody.object_id)");
			objWhl.append(")) ");
				
			sql.append(objWhl.toString());
			sql.append(" order by A0000,object_id");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			RowSet rowSet = null;
			ByteArrayInputStream input=null;
			Element root=null;
			Document a_doc = null;
			 
//			rs = dao.search(sql.toString());
			ArrayList dbnameList = new ArrayList();
			dbnameList.add("USR");
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				display_e0122="0";
			
			
			HashMap hmap = new HashMap();
	        String tableName = "per_result_"+plan_id;
	        DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(tableName);
	        DbWizard dbWizard=new DbWizard(this.conn);  		
	    	Table table=new Table(tableName);
	    	if(dbWizard.isExistTable(table.getName(),false))
	    	{
	    		RowSet rs2=dao.search(" select resultdesc,object_id from  "+tableName);
	    		while(rs2.next())
	    		{
		    		hmap.put(rs2.getString("object_id"),rs2.getString("resultdesc")==null?"":rs2.getString("resultdesc"));
	    		}
	    		if(rs2!=null)
	    			rs2.close();	    			
	    	}
			
			rs = dao.search(sql.toString());
			LazyDynaBean bean=null;
			while(rs.next())
			{
				String planid = rs.getString("plan_id");
				String object_id = rs.getString("object_id");
				
				for(int i=0;i<dbname.size();i++)
				{					
					bean = new LazyDynaBean();
					if("2".equals(object_type))
						bean.set("b0110",AdminCode.getCodeName("UN",rs.getString("b0110")));
					else
					{
						String b0110 = AdminCode.getCodeName("UN",rs.getString("object_id"));
	                    if(b0110==null|| "".equals(b0110))
	                    	b0110=AdminCode.getCodeName("UM",rs.getString("object_id"));
	        	        bean.set("b0110",b0110);
					}						
						
					bean.set("e01a1",AdminCode.getCodeName("@K",rs.getString("e01a1")));
					if("0".equals(display_e0122))
					    bean.set("e0122",AdminCode.getCodeName("UM",rs.getString("e0122")));					
					else
					{
						CodeItem item=AdminCode.getCode("UM",rs.getString("e0122")==null?"":rs.getString("e0122"),Integer.parseInt(display_e0122));
			    	    if(item!=null)			    	    
			    	    	bean.set("e0122",item.getCodename());			        	
			    	    else			    	    
			    	    	bean.set("e0122",AdminCode.getCodeName("UM",rs.getString("e0122")));			    	    
					}
					
					/*************************  统计回顾情况 begin  *********************************/
					HashMap amap = new HashMap(); // 已回顾的主体map
					String alreadyCaseMainbody = ""; // 已回顾的主体编号串
					if(mainbodyNumap!=null && mainbodyNumap.size()>0)
					{												
						if(mainbodyNumap.get(object_id)!=null)						
						{
							HashMap mainMap = (HashMap) mainbodyNumap.get(object_id); // 考核对象对应的考核主体map
							boolean isMainbody = this.isMainbody(object_id, plan_id);
							String mainNumber = "";
							if(isMainbody)
								mainNumber = String.valueOf(mainMap.size()); // 考核对象对应的考核主体数
							else
								mainNumber = String.valueOf(mainMap.size()+1); // 考核对象对应的考核主体数
							//String mainNumber =(String) ((HashMap)getPlanMainbodyNum(plan_id)).get(object_id); // 考核对象对应的考核主体数
							// 总体回顾
							String desc=Sql_switcher.readMemo(rs,"summarizes");
							if(desc!=null && desc.trim().length()>0)
							{
								a_doc = PubFunc.generateDom(desc);
								root = a_doc.getRootElement();
									
								List arlist=root.getChildren();
								for(Iterator t=arlist.iterator();t.hasNext();)
								{
									Element element=(Element)t.next();									
									if(amap.get(element.getAttributeValue("a0100"))==null && (mainMap.get(element.getAttributeValue("a0100"))!=null || object_id.equals(element.getAttributeValue("a0100"))))	
									{
									    amap.put(element.getAttributeValue("a0100"), "1");	
									    alreadyCaseMainbody += "/" + element.getAttributeValue("a0100");
									}
								}
							}
							
							// 任务回顾
							if(planParam!=null && ((String)planParam.get("taskNeedReview"))!=null && "True".equalsIgnoreCase((String)planParam.get("taskNeedReview")))
							{
								StringBuffer sqlStr=new StringBuffer("select summarizes from p04 where plan_id='" + plan_id + "' ");
								if("2".equals(object_type))
									sqlStr.append(" and a0100 = '" + object_id + "'");
								else
									sqlStr.append(" and b0110 = '" + object_id + "'");
								rowSet = dao.search(sqlStr.toString());
								while(rowSet.next())
								{
									String strDesc=Sql_switcher.readMemo(rowSet,"summarizes");
									if(strDesc!=null && strDesc.trim().length()>0)
									{
										a_doc = PubFunc.generateDom(strDesc);
										root = a_doc.getRootElement();
											
										List arlist=root.getChildren();
										for(Iterator t=arlist.iterator();t.hasNext();)
										{
											Element element=(Element)t.next();									
											if(amap.get(element.getAttributeValue("a0100"))==null && mainMap.get(element.getAttributeValue("a0100"))!=null)	
											{
											    amap.put(element.getAttributeValue("a0100"), "1");	
											    alreadyCaseMainbody += "/" + element.getAttributeValue("a0100");
											}
										}
									}									
								}								
							}
							int c = Integer.parseInt(mainNumber)-amap.size();
							
							bean.set("alreadyPerformCase", String.valueOf(amap.size()));
							bean.set("noAlreadyPerformCase", String.valueOf(c));
							bean.set("alreadyCaseMainbody", alreadyCaseMainbody); // 已回顾的主体编号串
							
						}
					}					
					if(hashmap.get(object_id)==null)	
					{	    				   				
						hashmap.put(object_id, amap);
						
						if(alreadyCaseMainbody!=null && alreadyCaseMainbody.trim().length()>0)
							objMainbodys += "&" + object_id + "`" + alreadyCaseMainbody.substring(1);
						else
							objMainbodys += "&" + object_id + "`" + alreadyCaseMainbody;
					}
					
					/*************************  统计回顾情况 end  *********************************/
					
					String currsp="0";
					String currappuser=rs.getString("currappuser");
					if(currappuser!=null&&!"".equals(currappuser)&&currappuser.equals(userView.getA0100()))
						currsp="1";
						
					String spf=rs.getString("sp_flag");
					/**前台页面展示
					  * flag=6：查看
					  * flag=7：布置
					  * flag=8：布置（调整）
					* */
					if(spf==null)
						spf="01";
						
					/*********************************************************************/
					/***/
					String opt="0";
					String flag="6";
					String spFlagDesc=MyObjectiveBo.getSpflagDesc(spf);//AdminCode.getCodeName("23",spf);   					
						
					currsp="0";						
					
					/**********************************************************************/
					bean.set("currsp", currsp);
					bean.set("flag",flag);
					bean.set("opt", opt);
					bean.set("sp_flag", spFlagDesc);
					bean.set("sp", spf);
					String traceFlagDesc="";
					String trace_flag="";
                    if("03".equals(spf))
                    {
                    	if("true".equalsIgnoreCase(AllowLeaderTrace))
                        {
                    		trace_flag=rs.getString("trace_sp_flag");
                         	if(trace_flag==null)
                         		trace_flag="01";
                         	if("07".equals(trace_flag))
                         		traceFlagDesc="退回";
                         	else
                         		traceFlagDesc=AdminCode.getCodeName("23", trace_flag);
                         	traceFlagDesc="跟踪指标"+traceFlagDesc;
                        }
                        else
                        {
                        	traceFlagDesc=spFlagDesc;
                        	trace_flag=spf;
                        }
                    }
                    if(hmap!=null&&hmap.get(rs.getString("object_id"))!=null)
                        bean.set("gradedesc",""+hmap.get(rs.getString("object_id")));
                    else
                        bean.set("gradedesc","");                   
                        
                    bean.set("trace_flag", trace_flag);
                    bean.set("tsp", traceFlagDesc);
					bean.set("a0101",rs.getString("a0101"));
					bean.set("planid",rs.getString("plan_id"));
					bean.set("a0100",rs.getString("object_id"));
					bean.set("m_a0100",rs.getString("object_id"));
					bean.set("mda0100", PubFunc.encryption(rs.getString("object_id")));
					bean.set("mdplanid",PubFunc.encryption(rs.getString("plan_id")));
					bean.set("level","");
					bean.set("object_type",object_type);
					String xx=PubFunc.encrypt(rs.getString("plan_id"))+"`"+PubFunc.encrypt(rs.getString("object_id"));
                    bean.set("bacthdata", xx);
					list.add(bean);
					
				}
			}			
			this.objMainbodys = objMainbodys;
			
			if(rs!=null)
				rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} 
		return list;
	}	
	/**
	 * 判断该考核对象是否同时为考核主体
	 */
	public boolean isMainbody(String object_id,String plan_id){
		boolean b = false;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		StringBuffer buf = new StringBuffer();
		buf.append(" select mainbody_id from per_mainbody where plan_id="+plan_id+" and object_id=mainbody_id and object_id="+object_id);
		try {
			rs = dao.search(buf.toString());
			while(rs.next()){
				String mainbody_id = (String) rs.getString("mainbody_id");
				if(mainbody_id!=null &&  !"".equals(mainbody_id))
					b=true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}
	
	/**
     * 取某考核计划下的考核对象对应的能回顾的考核主体数
     * @param plan_id
     * @return
     */
    public HashMap getPlanMainbodyNum(String plan_id)
    {
    	HashMap mainbodyNum = new HashMap();
    	RowSet rs = null;
        try
         {     
        	ContentDAO dao = new ContentDAO(this.conn);
        	
        	LoadXml loadxml=null;
			if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id)==null)
			{
				loadxml=new LoadXml(this.conn,plan_id);
				BatchGradeBo.getPlanLoadXmlMap().put(plan_id,loadxml);
			}
			else
				loadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id);	
			Hashtable planParam=loadxml.getDegreeWhole();
    		String SpByBodySeq="False";
			if(planParam.get("SpByBodySeq")!=null)
				 SpByBodySeq=(String)planParam.get("SpByBodySeq");
        	
    		StringBuffer buf = new StringBuffer();
    		buf.append(" select object_id,count(mainbody_id) mainbodyNum from per_mainbody,per_mainbodyset ");
    		buf.append(" where plan_id='"+plan_id+"'");
    		buf.append(" and per_mainbody.body_id=per_mainbodyset.body_id ");
    		if("true".equalsIgnoreCase(SpByBodySeq))
    		{
    			buf.append(" and sp_seq is not null ");
    		}
    		else{
    			buf.append(" and sp_flag is not null ");
    		}
    		/*if(Sql_switcher.searchDbServer()==Constant.ORACEL)
    			buf.append(" and per_mainbodyset.level_o in(0,1,5) ");
			else
				buf.append(" and per_mainbodyset.level in(0,1,5) ");*/
    		buf.append(" group by object_id ");
    			
    		rs = dao.search(buf.toString());
    		while(rs.next())
    		{
    			String num = rs.getString("mainbodyNum")==null ? "0" : (String) rs.getString("mainbodyNum");
    			mainbodyNum.put(rs.getString("object_id"), String.valueOf(Integer.parseInt(num)+1)) ;
    		}
    		
    		if(rs!=null)
    			rs.close();
         }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
    	return mainbodyNum;
    }
    /**
     * 取某考核计划下的考核对象对应的考核主体
     * @param plan_id
     * @return
     */
    public HashMap getPlanObjectMainbody(String plan_id)
    {
    	HashMap map = new HashMap();
    	HashMap hashmap = new HashMap();
    	RowSet rs = null;
        try
         {     
        	ContentDAO dao = new ContentDAO(this.conn);
        	LoadXml loadxml=null;
			if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id)==null)
			{
				loadxml=new LoadXml(this.conn,plan_id);
				BatchGradeBo.getPlanLoadXmlMap().put(plan_id,loadxml);
			}
			else
				loadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id);	
			Hashtable planParam=loadxml.getDegreeWhole();
    		String SpByBodySeq="False";
			if(planParam.get("SpByBodySeq")!=null)
				 SpByBodySeq=(String)planParam.get("SpByBodySeq");
    		StringBuffer buf = new StringBuffer();
    		buf.append(" select object_id,mainbody_id from per_mainbody ");
    		buf.append(" where plan_id='"+plan_id+"'");
    		if("true".equalsIgnoreCase(SpByBodySeq))
    		{
    			buf.append(" and ((sp_seq is not null and  "+Sql_switcher.datalength("sp_seq")+">0) or body_id=5) ");
    		}
    		else{
    			buf.append(" and ((sp_flag is not null and  "+Sql_switcher.datalength("sp_flag")+">0) or body_id=5) ");
    		}
    		buf.append(" order by object_id,mainbody_id ");
    			
    		rs = dao.search(buf.toString());
    		while(rs.next())
    		{   			
    			if(map.get(rs.getString("object_id"))==null)	
				{
    				hashmap = new HashMap();
    				hashmap.put(rs.getString("mainbody_id"), "123");    				
    				map.put(rs.getString("object_id"), hashmap);
    				
				}else
				{
					hashmap.put(rs.getString("mainbody_id"), "123");  
				}    			
    		}
    		
    		if(rs!=null)
    			rs.close();
         }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
    	return map;
    }
    /**
     * 根据考核对象得到考核主体列表
     * @param objectid
     * @return
     */
	public ArrayList getPerMainBodyList(String planid, String objectid, String alreadyCaseMainbody, String opt)
	{	
		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		try
		{
		    if (planid != null && planid.length() > 0 && objectid != null && objectid.length() > 0)
		    {
				ContentDAO dao = new ContentDAO(this.conn);
				LoadXml parameter_content = null;
    	        if(BatchGradeBo.getPlanLoadXmlMap().get(planid)==null)
				{
						
    	         	parameter_content = new LoadXml(conn,planid);
					BatchGradeBo.getPlanLoadXmlMap().put(planid,parameter_content);
				}
				else
				{
					parameter_content=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(planid);
				}
				Hashtable params = parameter_content.getDegreeWhole();
				String SpByBodySeq="False";
    			if(params.get("SpByBodySeq")!=null)
    				SpByBodySeq=(String)params.get("SpByBodySeq");
				StringBuffer sql = new StringBuffer("select pmb.*,pms.name from per_mainbody pmb,PER_MAINBODYSET pms ");
				sql.append(" where pmb.body_id = pms.body_id and pmb.plan_id=" + planid + " and pmb.object_id='" + objectid + "' ");
				if("already".equalsIgnoreCase(opt))
				{
					if(alreadyCaseMainbody!=null && alreadyCaseMainbody.trim().length()>0)
						sql.append(" and pmb.mainbody_id in(" + alreadyCaseMainbody.substring(1) + ") ");
					else
						sql.append(" and 1=2 ");
				}else
				{
					if(alreadyCaseMainbody!=null && alreadyCaseMainbody.trim().length()>0)
						sql.append(" and pmb.mainbody_id not in(" + alreadyCaseMainbody.substring(1) + ") ");
					else
						sql.append(" and 1=1 ");
					if("true".equalsIgnoreCase(SpByBodySeq)){
						sql.append(" and sp_seq is not null and "+Sql_switcher.datalength("sp_seq")+">0 ");
					}
					else {
						sql.append(" and sp_flag is not null and "+Sql_switcher.datalength("sp_flag")+">0 ");
					}
				}
				/*if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					sql.append(" and pms.level_o in(0,1,5) ");
				else
					sql.append(" and pms.level in(0,1,5) ");*/
				sql.append(" order by pms.seq,pmb.b0110,pmb.e0122,pmb.e01a1");
			
				rowSet = dao.search(sql.toString());
				LazyDynaBean abean = null;
				int count=0;
				String sumary = "";
				int sum = 0;
				int sumAleady = 0;//已回顾
				if(alreadyCaseMainbody!=null && alreadyCaseMainbody.trim().length()>0){
					sumary = alreadyCaseMainbody.substring(1);
					String[] total = sumary.split(",");
					sumAleady = total.length;
				}
	    		HashMap mainbodyNumap = getPlanObjectMainbody(planid);
	    		String mainNumber = "";
	    		if(mainbodyNumap!=null && mainbodyNumap.size()>0)
				{												
					if(mainbodyNumap.get(objectid)!=null)						
					{
						HashMap mainMap = (HashMap) mainbodyNumap.get(objectid); // 考核对象对应的考核主体map							
						boolean isMainbody = this.isMainbody(objectid, planid);
						if(isMainbody)
							mainNumber = String.valueOf(mainMap.size()); // 考核对象对应的考核主体数
						else
							mainNumber = String.valueOf(mainMap.size()+1); // 考核对象对应的考核主体数
					}
				}
	    		if("already".equalsIgnoreCase(opt))
		    		sum = sumAleady;
	    		else
	    			sum = Integer.parseInt(mainNumber)-sumAleady;
		    	
				while (rowSet.next())
				{
				    String b0110 = rowSet.getString("b0110");
				    String e0122 = rowSet.getString("e0122");
				    String e01a1 = rowSet.getString("e01a1");
				    String fillctrl = rowSet.getString("fillctrl");
				    String seq = rowSet.getString("seq");
				    abean = new LazyDynaBean();
				    abean.set("id", rowSet.getString("id"));
				    abean.set("b0110", b0110 != null ? AdminCode.getCodeName("UN", b0110) : "");
				    abean.set("e0122", e0122 != null ? AdminCode.getCodeName("UM", e0122) : "");
				    abean.set("e01a1", e01a1 != null ? AdminCode.getCodeName("@K", e01a1) : "");
				    abean.set("plan_id", rowSet.getString("plan_id"));
				    abean.set("object_id", rowSet.getString("object_id"));
				    abean.set("mainbody_id", rowSet.getString("mainbody_id"));
				    abean.set("a0101", rowSet.getString("a0101")==null?"":rowSet.getString("a0101"));
				    abean.set("body_id", rowSet.getString("body_id"));
				    abean.set("bodyTypeName", rowSet.getString("name"));
				    abean.set("fillctrl", fillctrl != null ? fillctrl : "0");
				    abean.set("seq", seq != null ? seq : "");
				    list.add(abean);
				    count++;
				}
				 if(sum>count){
				    	sql.setLength(0);
				    	sql.append(" select * from per_object where object_id="+objectid+" and plan_id="+planid);
				    	rowSet = dao.search(sql.toString());
				    	while(rowSet.next()){
				    		String b0110 = rowSet.getString("b0110");
						    String e0122 = rowSet.getString("e0122");
						    String e01a1 = rowSet.getString("e01a1");
						    abean = new LazyDynaBean();
						    abean = new LazyDynaBean();
						    abean.set("id", rowSet.getString("id"));
						    abean.set("b0110", b0110 != null ? AdminCode.getCodeName("UN", b0110) : "");
						    abean.set("e0122", e0122 != null ? AdminCode.getCodeName("UM", e0122) : "");
						    abean.set("e01a1", e01a1 != null ? AdminCode.getCodeName("@K", e01a1) : "");
						    abean.set("plan_id", rowSet.getString("plan_id"));
						    abean.set("object_id", rowSet.getString("object_id"));
						    abean.set("mainbody_id", rowSet.getString("object_id"));
						    abean.set("a0101", rowSet.getString("a0101")==null?"":rowSet.getString("a0101"));
						    abean.set("body_id", "5");
						    abean.set("bodyTypeName", "本人");
						    abean.set("fillctrl", "0");
						    abean.set("seq", "");
						    list.add(abean);
						    count++;
				    	}
				 }
		    }
		    if(rowSet !=null)
		    	rowSet.close();
		
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return list;
	}
		
	 /**
     * 取考核等级
     * @param degreeid
     * @return
     */
    public String getDegreeDesc(String plan_id,String object_id)
    {
    	String desc = "";
        try
         {
        	//DBMetaModel dbmodel=new DBMetaModel(this.conn);
			//dbmodel.reloadTableModel();
        	String tableName = "per_result_"+plan_id;
        	DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(tableName);
        	DbWizard dbWizard=new DbWizard(this.conn);  		
    		Table table=new Table(tableName);
    		if(dbWizard.isExistTable(table.getName(),false))
    		{
    			StringBuffer buf = new StringBuffer();
    			buf.append(" select resultdesc from ");
    			buf.append(tableName+" where object_id='"+object_id+"'");
    			ContentDAO dao = new ContentDAO(this.conn);
    			RowSet rs = dao.search(buf.toString());
    			while(rs.next())
    			{
    				desc=rs.getString("resultdesc")==null?"":rs.getString("resultdesc");
    			}
    		}
    		if(desc==null)
    			desc="";
         }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
    	return desc;
    }
	public HashMap getp04(String planid,ContentDAO dao)
	{
		HashMap map=new HashMap();
	    	try
	    	{
	    		String sql="select count(*) total,a0100 from p04 where plan_id="+planid+" and state=-1 group by a0100";
	    	    RowSet rs =null;
	    	    rs=dao.search(sql);
	     	    while(rs.next())
	    	    {
	    	    	int i=rs.getInt("total");
	    	    	String a0100=rs.getString("a0100");
	    	    	map.put(a0100+planid, i+"");
	    	    	
	    	    }
	    	   rs.close();
 	    	}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
		
		return map;
	}
	private static ArrayList statusList=null;
	public ArrayList getStatusList()
	{
		if(statusList==null)
		{
			statusList = new ArrayList();
    		try
	    	{
	    		String sql = "select codeitemid,codeitemdesc from codeitem where codesetid='23' and codeitemid in('01','02','03','06','07')";
	    		ContentDAO dao = new ContentDAO(this.conn);
	    		RowSet rs = null;
		    	rs = dao.search(sql);
		    	statusList.add(new CommonData("-1",ResourceFactory.getProperty("label.all")));
		    	statusList.add(new CommonData("-2","执行中"));
		    	while(rs.next())
		    	{
		    		statusList.add(new CommonData(rs.getString("codeitemid"),MyObjectiveBo.getSpflagDesc(rs.getString("codeitemid"))));
	    		}
		    	rs.close();
	    	}
	     	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	    	return statusList;
		}
		else
		{
			return statusList;
		}
	}
	private static ArrayList allStatusList=null;
	public ArrayList getAllStatusList()
	{
		if(allStatusList==null)
		{
			allStatusList = new ArrayList();
     		try
    		{
    			String sql = "select codeitemid,codeitemdesc from codeitem where codesetid='23' and codeitemid in('01','02','03','07')";
	    		ContentDAO dao = new ContentDAO(this.conn);
    			RowSet rs = null;
    			rs = dao.search(sql);
    			allStatusList.add(new CommonData("-1",ResourceFactory.getProperty("label.all")));
	    		while(rs.next())
	    		{
	    			
	    			allStatusList.add(new CommonData(rs.getString("codeitemid"),MyObjectiveBo.getSpflagDesc(rs.getString("codeitemid"))));
     			}
	    		rs.close();
    		}
    		catch(Exception e)
    		{
    	 		e.printStackTrace();
    		}
    		return allStatusList;
		}
		else
		{
			return allStatusList;
		}
	}
	
	public HashMap getMainBodyStatus(String year)
	{
		HashMap map = new HashMap();
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append(" select plan_id,object_id from per_mainbody p where ");
			
			buf.append(" (status=2 or status=1 )");
			buf.append(" and p.plan_id in (");
			buf.append(" select pp.plan_id  from per_plan pp where method='2'");
			if(year!=null&&!"".equals(year))
			{
				buf.append(" and pp.theyear="+year);
			}
			buf.append(" and pp.cycle<>'7' ");
			buf.append(" and pp.object_type='2')");
		
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs=dao.search(buf.toString());
			while(rs.next())
			{
				map.put(rs.getString("plan_id")+rs.getString("object_id"),"1");
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/************************************************************************************************
	 *  目标卡状态监控
	 ************************************************************************************************/
	public ArrayList getPlanList()
	{
		ArrayList  list = new ArrayList();
		try
		{
			ExamPlanBo bo = new ExamPlanBo(this.conn);
			HashMap map = bo.getPlansByUserView(view, "and method=2  and (status='4' or status='5' or status='6' or status='8' ) "); // or status='7' 
			String sql ="select object_type,plan_id,name,"+Sql_switcher.isnull("a0000", "999999")+" as norder from per_plan where method=2  and (status='4' or status='5' or status='6' or status='8' ) order by norder asc,plan_id desc"; // or status='7' 
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			HashMap leaderMap = this.getAllHaveLeaderPlan();
			while(rs.next())
			{
				if(map.get(rs.getString("plan_id"))==null)
					continue;
				String object_type=rs.getString("object_type");
				if(!"2".equals(object_type))//团队计划
				{
					if(leaderMap.get(rs.getString("plan_id"))==null)//没有团队负责人考核主体类别的计划，在目标卡状态中，不显示
						continue;
				}
				list.add(new CommonData(rs.getString("plan_id"),rs.getString("plan_id")+"."+rs.getString("name")));
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 取得所有有团队负责人考核主题的计划
	 * @return
	 */
	public HashMap getAllHaveLeaderPlan()
	{
		HashMap map = new HashMap();
		RowSet rs = null;
		try
		{
			ContentDAO dao  = new ContentDAO(this.conn);
			String sql = "select plan_id from per_plan_body where body_id=-1";
			rs=dao.search(sql);
			while(rs.next())
			{
				map.put(rs.getString("plan_id"), rs.getString("plan_id"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return map;
	}
	public HashMap getP04Info(String plan_id,boolean isORG)
	{
		HashMap map = new HashMap();
		try
		{
			String sql="select a0100,b0110 from p04 where plan_id="+plan_id+" and state='-1'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs =dao.search(sql);
			while(rs.next())
			{
				if(isORG)
				{
					map.put(rs.getString("b0110")+plan_id, "1");
				}
				else
				{
	    			map.put(rs.getString("a0100")+plan_id, "1");
				}
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public String getPrivWhereSQL(UserView view,String objecttype,String nbase)
	{
		String str="";
		RowSet rs = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			/**团队*/
			if(!"2".equals(objecttype))
			{
				StringBuffer sql = new StringBuffer();
				if(view.getUnitIdByBusi("5")!=null&&!"".equals(view.getUnitIdByBusi("5"))&&!"UN".equalsIgnoreCase(view.getUnitIdByBusi("5")))
				{
					String code=view.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
					String[] arr=code.split("`");
					StringBuffer sb=new StringBuffer();
					for(int i=0;i<arr.length;i++)
					{
						if(arr[i]==null|| "".equals(arr[i]))
							continue;
						String co=arr[i].substring(0,2);
						String value=arr[i].substring(2);
						if("UN".equalsIgnoreCase(co))
						{
							sb.append(" or (b0110 like '"+value+"%'");
							if("".equals(value))
								sb.append(" or b0110 is null ");
							sb.append(")");
						}
						if("UM".equalsIgnoreCase(co))
						{
							sb.append(" or (e0122 like '"+value+"%'");
							if("".equals(value))
								sb.append(" or e0122 is null ");
							sb.append(")");
						}
					}
					String ss=sb.toString().substring(3);
					sql.append(" and ("+ss+")");
				}
				else
				{
		    		String code=view.getManagePrivCode();
    	    		String value=view.getManagePrivCodeValue();
    	    		if(code==null|| "".equals(code))
    		    	{
    	    			if(!view.isSuper_admin())//超级用户，默认所有
    		    	    	sql.append(" and 1=2 ");
    		    	}
    		    	else
    	    		{
    		    		if("UN".equalsIgnoreCase(code))
    		    		{
    			    		sql.append(" and (b0110 like '"+value+"%'");
    			    		if("".equals(value))
    				    		sql.append(" or b0110 is null");
    				    	sql.append(")");
    		    		}
    		    		if("UM".equalsIgnoreCase(code))
    		    		{
    		    			sql.append(" and (e0122 like '"+value+"%'");
    		    			if("".equals(value))
    			    			sql.append(" or e0122 is null");
    			     		sql.append(")");
    		    		}
    		    		if("@K".equalsIgnoreCase(code))
    		    		{
    			    		sql.append(" and (e01a1 like '"+value+"%'");
    			    		if("".equals(value))
    			    			sql.append(" or e01a1 is null");
    			    		sql.append(")");
    		    		}
    		    	}
				}
    			str=sql.toString();
			}
			else
			{
				if(view.getUnitIdByBusi("5")!=null&&!"".equals(view.getUnitIdByBusi("5"))&&!"UN".equalsIgnoreCase(view.getUnitIdByBusi("5")))
				{
					String code=view.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
					String[] arr=code.split("`");
					StringBuffer sb=new StringBuffer();
					for(int i=0;i<arr.length;i++)
					{
						if(arr[i]==null|| "".equals(arr[i]))
							continue;
						String co=arr[i].substring(0,2);
						String value=arr[i].substring(2);
						if("UN".equalsIgnoreCase(co))
						{
							sb.append(" or (b0110 like '"+value+"%'");
							if("".equals(value))
								sb.append(" or b0110 is null ");
							sb.append(")");
						}
						if("UM".equalsIgnoreCase(co))
						{
							sb.append(" or (e0122 like '"+value+"%'");
							if("".equals(value))
								sb.append(" or e0122 is null ");
							sb.append(")");
						}
					}
					String ss=sb.toString().substring(3);
					str=" and ("+ss+")";
				}
				else
				{
					if(view.isSuper_admin())
					{
						StringBuffer sql = new StringBuffer();
						String code=view.getManagePrivCode();
						String value=view.getManagePrivCodeValue();
						if(code!=null&&code.trim().length()>0)
						{
							if("UN".equalsIgnoreCase(code))
	    		    		{
	    			    		sql.append(" and (b0110 like '"+value+"%'");
	    			    		if("".equals(value))
	    				    		sql.append(" or b0110 is null");
	    				    	sql.append(")");
	    		    		}
	    		    		if("UM".equalsIgnoreCase(code))
	    		    		{
	    		    			sql.append(" and (e0122 like '"+value+"%'");
	    		    			if("".equals(value))
	    			    			sql.append(" or e0122 is null");
	    			     		sql.append(")");
	    		    		}
	    		    		if("@K".equalsIgnoreCase(code))
	    		    		{
	    			    		sql.append(" and (e01a1 like '"+value+"%'");
	    			    		if("".equals(value))
	    			    			sql.append(" or e01a1 is null");
	    			    		sql.append(")");
	    		    		}
						}
						str=sql.toString();
					}
					else
					{
						
					
		        		String priStrSql = InfoUtils.getWhereINSql(view, nbase);
			        	StringBuffer buf = new StringBuffer("");
			        	buf.append(" select a0100 "+(priStrSql.length()>0?priStrSql:" from "+nbase+"a01 "));
			        	str=" and object_id in ("+buf.toString()+")";
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return str;
	}
	public static String toUpperNumber(int number){
		String upperNumber="";
		switch(number){
		case 1:
			upperNumber="一";
			break;
		case 2:
			upperNumber="二";
			break;
		case 3:
			upperNumber="三";
			break;
		case 4:
			upperNumber="四";
			break;
		case 5:
			upperNumber="五";
			break;
		case 6:
			upperNumber="六";
			break;
		case 7:
			upperNumber="七";
			break;
		case 8:
			upperNumber="八";
			break;
		case 9:
			upperNumber="九";
			break;
		case 10:
			upperNumber="十";
			break;
		case 11:
			upperNumber="十一";
			break;
		case 12:
			upperNumber="十二";
			break;
		case 13:
			upperNumber="十三";
			break;
		case 14:
			upperNumber="十四";
			break;
		case 15:
			upperNumber="十五";
			break;
			
		}
		return upperNumber;
	}
/**
 * 
 * @param plan_id
 * @param a0101
 * @param e0122
 * @param status
 * @param type 目标管理审批模式:0-考核关系 1-汇报关系. 默认为 0
 * @param view
 * @param targetMakeSeries
 * @param objectID
 * @param warn
 * @return
 */
	public ArrayList getObjectInfoList(String plan_id,String a0101,String e0122,String status,int type,UserView view,String targetMakeSeries,String objectID,String warn)
	{
		ArrayList infoList = new ArrayList();
		try
		{
			StringBuffer sql = new StringBuffer("");
			ContentDAO dao = new ContentDAO(this.conn);
			/**部门负责人object_id+plan_id---->bean(a0100,e01a1)*/
			RecordVo vo = new RecordVo("per_plan");
			vo.setInt("plan_id", Integer.parseInt(plan_id));
			vo=dao.findByPrimaryKey(vo);
			String object_type=vo.getString("object_type");
			LoadXml parameter_content = null;
	        if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"")==null)
			{
					
	         	parameter_content = new LoadXml(this.conn,plan_id+"");
				BatchGradeBo.getPlanLoadXmlMap().put(plan_id+"",parameter_content);
			}
			else
			{
				parameter_content=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"");
			}
			Hashtable params = parameter_content.getDegreeWhole();
			String SpByBodySeq="False";
			if(params.get("SpByBodySeq")!=null)
				SpByBodySeq=(String)params.get("SpByBodySeq");
			HashMap leaderMap = null;
			boolean isORG=false;
			/**团队考核*/
			if(!"2".equals(object_type)&&!"true".equalsIgnoreCase(SpByBodySeq))
			{
				isORG=true;
				leaderMap=this.getOrgLeader(plan_id,objectID);
			}
			sql.append(" from per_object where plan_id="+plan_id);
			
			if(warn!=null && warn.trim().length()>0 && "warn".equalsIgnoreCase(warn))
				sql.append(" and object_id in ( select object_id from per_mainbody where plan_id="+plan_id+"  ");			
			if(objectID!=null&&!"".equals(objectID))
				sql.append(" and object_id='"+objectID+"'");
			else
			{
		    	
		    	String privSQL=this.getPrivWhereSQL(view, object_type, "usr");
    		    sql.append(privSQL);
    	    	
		    	if(!(a0101==null|| "".equals(a0101)))
		    	{
		     		sql.append(" and a0101 like '"+a0101+"%'");
		    	}
		    	if(!"-1".equals(e0122))
		    	{
			    	if(e0122==null|| "".equals(e0122))
    		    	{
    		     		sql.append(" and 1=2 ");
    		    	}
    		    	else
    		    	{
    		    		String code=e0122.substring(0,2);
    		    		String value=e0122.substring(2);
    		    		if("UN".equalsIgnoreCase(code))
    		    		{
    			    		sql.append(" and (b0110 like '"+value+"%'");
    			    		if("".equals(value))
    				    		sql.append(" or b0110 is null");
    			    		sql.append(")");
    			     	}
    			     	if("UM".equalsIgnoreCase(code))
    			    	{
    			    		sql.append(" and (e0122 like '"+value+"%'");
    			    		if("".equals(value))
    			    			sql.append(" or e0122 is null");
    			    		sql.append(")");
    			    	}
    		    		if("@K".equalsIgnoreCase(code))
    		    		{
    		    			sql.append(" and (e01a1 like '"+value+"%'");
    		    			if("".equals(value))
    		     				sql.append(" or e01a1 is null");
    		    			sql.append(")");
    	    			}
    	    		}
		    	}
	    		if(!"-1".equals(status))
		    	{
		    		if("01".equals(status))
		    			sql.append(" and  (sp_flag is null or sp_flag='01')");
		    		else
		    			sql.append(" and sp_flag = '"+status+"'");
		    	}
	    		
	    		if(warn!=null && warn.trim().length()>0 && "warn".equalsIgnoreCase(warn))
					sql.append(" ) ");
			}
			//sql.append(" order by a0000");
			ArrayList objectList = new ArrayList();
			RowSet rs = null;
			StringBuffer objectSQL=new StringBuffer();
			objectSQL.append("select object_id,a0101,kh_relations,sp_flag,");
		    if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
		    	objectSQL.append("to_char(report_date,'yyyy-mm-dd hh24:mi') as report_date ");
			}
		   else
		   {
			   objectSQL.append("convert(varchar(16),report_date,20) as report_date ");
		   }
		    objectSQL.append(",currappuser,b0110,e0122 "+sql.toString()+" order by a0000");
			rs = dao.search(objectSQL.toString());
			HashMap map =null;
			/**按考核关系，所有考核主体*/
			if(!"true".equalsIgnoreCase(SpByBodySeq)){//不按照序号审批，才查一下数据，按序号审批，下面的数据就没用拉
				if(type==0)
				{
					String condSQL ="";
					if(isORG)
					{
						sql.setLength(0);
						sql.append("select mainbody_id as object_id from per_mainbody where plan_id="+plan_id);
						sql.append(" and body_id='-1'");
						if(objectID!=null&&!"".equals(objectID))
					    	sql.append(" and object_id='"+objectID+"'");
						condSQL=sql.toString();
					}
					else
					{
						condSQL+="select object_id "+sql.toString();
					}
					if(isORG)
					{
						map=getObjectAndMainBodyRelationForORG(condSQL, plan_id, targetMakeSeries,leaderMap);
					}
					else
			    		map=this.getObjectAndMainBodyRelation(condSQL, plan_id, targetMakeSeries);
				
				}
				/**按汇报关系,所有上级*/
				else
				{
					String condSQL ="";
					if(isORG)
					{
						sql.setLength(0);
						sql.append("select mainbody_id as a0100,object_id from per_mainbody where plan_id="+plan_id);
						sql.append(" and body_id='-1'");
						if(objectID!=null&&!"".equals(objectID))
							sql.append(" and object_id='"+objectID+"'");
						condSQL=sql.toString();
					}
					else
					{
						condSQL+="select object_id as a0100 "+sql.toString();
					}
					map=this.getObjectRelation(condSQL,plan_id,targetMakeSeries,isORG);
				
				}
			}else{
				String condSQL="select object_id "+sql.toString();
				map=this.getMainbodyBySeq(plan_id, condSQL);
			}
			SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd hh:mm");
			HashMap p04Map = this.getP04Info(plan_id,isORG);
			String taskAdjustNeedNew = (String) params.get("taskAdjustNeedNew");
			if (taskAdjustNeedNew == null)
				taskAdjustNeedNew = "false";
			
			LazyDynaBean _bean=new LazyDynaBean();
			while(rs.next())
			{
				ArrayList appealList=new ArrayList();
				ArrayList appealList2=new ArrayList();
				ArrayList appealList3=new ArrayList(); 
				ArrayList rejectList=new ArrayList();
				LazyDynaBean bean = new LazyDynaBean();
				String object_id=rs.getString("object_id");
				String _b0110=rs.getString("b0110")!=null?rs.getString("b0110"):"";
				String _e0122=rs.getString("e0122")!=null?rs.getString("e0122"):"";
				if(_b0110.length()>0)
					_b0110=AdminCode.getCodeName("UN",_b0110);
				if(_e0122.length()>0)
					_e0122=AdminCode.getCodeName("UM",_e0122);
				String currappuser=rs.getString("currappuser")!=null?rs.getString("currappuser"):"";
				if(isORG)
				{
					// 去掉此过滤条件 没有团队负责人的部门也让其显示出来  JinChunhai 2011.08.08
//					if(leaderMap.get(object_id+plan_id)==null)
//						continue;
				}
				String kh_relations=rs.getString("kh_relations");
				if(kh_relations==null)
					kh_relations="0";
				bean.set("object_id",rs.getString("object_id"));
				bean.set("a0101", rs.getString("a0101"));
				bean.set("_b0110",_b0110);
				bean.set("_e0122",_e0122);
				{
					_bean=new LazyDynaBean();
					_bean.set("level","5");
					_bean.set("level_name","考核对象");
					_bean.set("a0100",rs.getString("object_id"));
					_bean.set("a0101",rs.getString("a0101"));
					rejectList.add(_bean);
				}
				String report_date=rs.getString("report_date");
				bean.set("report_date", report_date==null?"":report_date);
				if(p04Map.get(object_id+plan_id)!=null&& "false".equalsIgnoreCase(taskAdjustNeedNew))
				{
					//one+="(调整后)";
					String sp_flag=rs.getString("sp_flag")==null?"01":rs.getString("sp_flag");
					String spFlagDesc=MyObjectiveBo.getSpflagDesc(sp_flag);//AdminCode.getCodeName("23",spf);
					bean.set("sp_flag",spFlagDesc+"(调整后)");
				}
				else
				{
					String sp_flag=rs.getString("sp_flag")==null?"01":rs.getString("sp_flag");
					String spFlagDesc=MyObjectiveBo.getSpflagDesc(sp_flag);//AdminCode.getCodeName("23",spf);
					bean.set("sp_flag",spFlagDesc);
					
				}
				bean.set("flag", (rs.getString("sp_flag")==null?"01":rs.getString("sp_flag")));
				if("true".equalsIgnoreCase(SpByBodySeq))//按序号审批计划
				{
					int curr_seq=0;
					if(this.currMap.get(object_id)!=null)
					{
						LazyDynaBean currentOptObj=(LazyDynaBean)this.currMap.get(object_id);
						bean.set("currentOptObj",currentOptObj);
						curr_seq=Integer.parseInt(((String)currentOptObj.get("sp_seq")));
					}
					int aseq=0;
					for(int i=1;i<=this.maxLeaderLay;i++){
						ArrayList mainbodyList=(ArrayList)map.get(object_id+i);
						String str="";
						String str_date="";
						if(mainbodyList!=null){
							for(int j=0;j<mainbodyList.size();j++){
								LazyDynaBean abean =(LazyDynaBean)mainbodyList.get(j);
								String level=(String)abean.get("level");
								String sp_flag=(String)abean.get("sp_flag");
								String sp_date=(String)abean.get("sp_date");
								String mainName=(String)abean.get("a0101");
								String mainbody_id=(String)abean.get("mainbody_id");
								String sp_seq=(String)abean.get("sp_seq");
								int seq=Integer.parseInt(sp_seq);
								if(currappuser!=null&&currappuser.equalsIgnoreCase(mainbody_id))
								{
									mainName+=" <img src='/images/mail2.gif' onclick='sendMail(\""+rs.getString("object_id")+"\",\""+mainbody_id+"\")' />"; 
									bean.set("mainbody_id",mainbody_id);
								}
								
								str+="/"+mainName;
								if(!"".equals(sp_flag))
								{
									str+=":"+sp_flag;
									if(p04Map.get(object_id+plan_id)!=null&& "false".equalsIgnoreCase(taskAdjustNeedNew))
									{
										str+="(调整后)";
									}
								}
								if(!"".equals(sp_date))
								{
									str_date=sp_date;
								}
								if(curr_seq>seq&&!"".equals(sp_flag)){
									_bean=new LazyDynaBean();
									_bean.set("level",level);
									_bean.set("level_name","第"+SetUnderlingObjectiveBo.toUpperNumber(i)+"级");
									_bean.set("a0100",mainbody_id);
									_bean.set("a0101",mainName);
									rejectList.add(_bean);
								}
								if(curr_seq<seq){
									if(appealList.size()==0)
										aseq=seq;
									if(aseq==seq){
										CommonData data=new CommonData(mainbody_id+"/"+level,mainName);
										appealList.add(data);
									}
								}
							}
						}
						bean.set(i+"", "".equals(str)?"":str.substring(1));
						bean.set(i+"date", "".equals(str_date)?"":str_date);
					}
					bean.set("rejectlist",rejectList);
					bean.set("appealList",appealList);
				}
				else
				{
					ArrayList mainbodyList = null;
					if("1".equals(kh_relations))
						mainbodyList=this.getNotStandardObjectMainbody(plan_id, object_id, targetMakeSeries);
					else
					{
						if(isORG)
						{
							LazyDynaBean lbean=(LazyDynaBean)leaderMap.get(object_id+plan_id);
							// 删除以下无效代码，定义了a0100但是没有使用，并且lbean为空时还会导致报错 chent 20180330 delete
							// String a0100=(String)lbean.get("a0100");
							mainbodyList=(ArrayList)map.get(object_id);
						}
						else
						{
					    	mainbodyList=(ArrayList)map.get(object_id);
						}
					}
					String one="";
					String onesp_date="";
					String two="";
					String twosp_date="";
					String three="";
					String threesp_date="";
					String four="";
					String foursp_date="";
					int current_opt_level=100;
					if(mainbodyList!=null)
					{
						for(int i=0;i<mainbodyList.size();i++)
						{
							LazyDynaBean abean =(LazyDynaBean)mainbodyList.get(i);
							String level=(String)abean.get("level");
							String sp_flag=(String)abean.get("sp_flag");
							String sp_date=(String)abean.get("sp_date");
							String mainName=(String)abean.get("a0101");
							String a0100=(String)abean.get("a0100");
							String mainbody_id=(String)abean.get("mainbody_id");
							if(type==1&&mainbody_id==null)
								mainbody_id=a0100;
							if(currappuser!=null&&currappuser.equalsIgnoreCase(mainbody_id))
							{
								 
								_bean=new LazyDynaBean();
								_bean.set("a0100",mainbody_id);
								_bean.set("a0101",mainName);
								String _desc="直接上级";
								if("0".equals(level))
									_desc="主管领导";
								else if("-1".equals(level))
									_desc="第三级领导";
								else if("-2".equals(level))
									_desc="第四级领导";
								_bean.set("lay_desc", _desc);
								_bean.set("level", level);
								bean.set("currentOptObj", _bean);
								current_opt_level=Integer.parseInt(level);
								break;
							}
						}
						for(int i=0;i<mainbodyList.size();i++)
						{
							LazyDynaBean abean =(LazyDynaBean)mainbodyList.get(i);
							String level=(String)abean.get("level");
							String sp_flag=(String)abean.get("sp_flag");
							String sp_date=(String)abean.get("sp_date");
							String mainName=(String)abean.get("a0101");
							String a0100=(String)abean.get("a0100");
							String mainbody_id=(String)abean.get("mainbody_id");
							if(type==1&&mainbody_id==null)
								mainbody_id=a0100;
							if(currappuser!=null&&currappuser.equalsIgnoreCase(mainbody_id))
							{
								mainName+=" <img src='/images/mail2.gif' onclick='sendMail(\""+rs.getString("object_id")+"\",\""+mainbody_id+"\")' />"; 
								bean.set("mainbody_id",mainbody_id);
							}
							if("1".equals(level))
							{
								one+="/"+mainName;
								if(!"".equals(sp_flag))
								{
									one+=":"+sp_flag;
									if(p04Map.get(object_id+plan_id)!=null&& "false".equalsIgnoreCase(taskAdjustNeedNew))
									{
										one+="(调整后)";
									}
									if(current_opt_level<1)
									{
										_bean=new LazyDynaBean();
										_bean.set("level","1");
										_bean.set("level_name",getMainbodyName(plan_id,rs.getString("object_id"),mainbody_id));
										_bean.set("a0100",mainbody_id);
										_bean.set("a0101",mainName);
										rejectList.add(_bean);
									}
								}
								if(!"".equals(sp_date))
								{
									onesp_date=sp_date;
								}	
							}
							else if("0".equals(level))
							{
								if(current_opt_level==1)
								{
									CommonData data=new CommonData(mainbody_id+"/"+level,mainName);
									appealList.add(data);
								}	
								two+="/"+mainName;
								if(!"".equals(sp_flag))
								{
									two+=":"+sp_flag;
									if(p04Map.get(object_id+plan_id)!=null&& "false".equalsIgnoreCase(taskAdjustNeedNew))
									{
										two+="(调整后)";
									}	
									if(current_opt_level<0)
									{
										_bean=new LazyDynaBean();
										_bean.set("level","0");
										_bean.set("a0100",mainbody_id);
										_bean.set("a0101",mainName);
										_bean.set("level_name",getMainbodyName(plan_id,rs.getString("object_id"),mainbody_id));
										rejectList.add(_bean);
									}	
								}
								if(!"".equals(sp_date))
								{
									twosp_date=sp_date;
								}
							}
							else if("-1".equals(level))
							{
								three+="/"+mainName;
									
								if(current_opt_level==0)
								{
									CommonData data=new CommonData(mainbody_id+"/"+level,mainName);
									appealList.add(data);
								}
								if(current_opt_level==1)
								{
									CommonData data=new CommonData(mainbody_id+"/"+level,mainName);
									appealList2.add(data);
								}	
								if(!"".equals(sp_flag))
								{
									three+=":"+sp_flag;
									if(p04Map.get(object_id+plan_id)!=null&& "false".equalsIgnoreCase(taskAdjustNeedNew))
									{
										three+="(调整后)";
									}
									if(current_opt_level<-1)
									{
										_bean=new LazyDynaBean();
										_bean.set("level","-1");
										_bean.set("a0100",mainbody_id);
										_bean.set("a0101",mainName);
										_bean.set("level_name",getMainbodyName(plan_id,rs.getString("object_id"),mainbody_id));
										rejectList.add(_bean);
									}
								}
								if(!"".equals(sp_date))
								{
									threesp_date=sp_date;
								}
							}
							else
							{
								four+="/"+mainName;
									
								if(current_opt_level==-1)
								{
									CommonData data=new CommonData(mainbody_id+"/"+level,mainName);
									appealList.add(data);
								}
								if(current_opt_level==0)
								{
									CommonData data=new CommonData(mainbody_id+"/"+level,mainName);
									appealList2.add(data);
								}
								if(current_opt_level==1)
								{
									CommonData data=new CommonData(mainbody_id+"/"+level,mainName);
									appealList3.add(data);
								}
									
								if(!"".equals(sp_flag))
								{
									four+=":"+sp_flag;
									if(p04Map.get(object_id+plan_id)!=null&& "false".equalsIgnoreCase(taskAdjustNeedNew))
									{
										four+="(调整后)";
									}
								}
								if(!"".equals(sp_date))
								{
									foursp_date=sp_date;
								}
							}
						}
					}
					int plan_level=Integer.parseInt(targetMakeSeries);
					for(int i=1;i<=plan_level;i++)
					{
						if(i==1)
						{
							bean.set(i+"", "".equals(one)?"":one.substring(1));
							bean.set(i+"date", "".equals(one)?"":onesp_date);
						}
						if(i==2)
						{
							bean.set(i+"", "".equals(two)?"":two.substring(1));
							bean.set(i+"date", "".equals(two)?"":twosp_date);
						}
						if(i==3)
						{
							bean.set(i+"", "".equals(three)?"":three.substring(1));
							bean.set(i+"date", "".equals(three)?"":threesp_date);
						}
						if(i==4)
						{
							bean.set(i+"", "".equals(four)?"":four.substring(1));
							bean.set(i+"date", "".equals(four)?"":foursp_date);
						}
					}
					if(appealList.size()==0)
					{
						if(appealList2.size()>0)
							appealList=appealList2;
						else if(appealList3.size()>0)
							appealList=appealList3; 
					}	
					if(plan_level==1)
					{
						appealList=new ArrayList();
					}
					else if(plan_level==2&&(current_opt_level==0||current_opt_level==-1||current_opt_level==-2))
					{
						appealList=new ArrayList();
					}
					else if(plan_level==3&&(current_opt_level==-1||current_opt_level==-2))
					{
						appealList=new ArrayList();
					}
					ArrayList rejectlist=new ArrayList();
					if(rejectList.size()>0)
					{
						rejectlist.add((LazyDynaBean)rejectList.get(0));
						for(int i=1;i<rejectList.size();i++)
						{
							_bean=(LazyDynaBean)rejectList.get(i);
							int level=Integer.parseInt((String)_bean.get("level"));
							if(i==1)
								rejectlist.add(_bean);
							else
							{
								int n=100;
								for(int j=1;j<rejectlist.size();j++)
								{
									LazyDynaBean abean=(LazyDynaBean)rejectlist.get(j);
									int _level=Integer.parseInt((String)abean.get("level"));
									if(level<_level)
									{
										n=j;
										break;
									}
								}
								if(n==100)
									rejectlist.add(_bean);
								else
									rejectlist.add(n,_bean);
							}	
						}
					}
					bean.set("rejectlist",rejectlist);
					bean.set("appealList",appealList);
				}
				infoList.add(bean);
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return infoList;
	}
	
	
	public String getMainbodyName(String plan_id,String object_id,String mainbody_id)
	{
		String name="";
		try
		{
			  ContentDAO dao = new ContentDAO(this.conn);
			  RowSet rowSet=dao.search("select pms.name  from per_mainbody pm,per_mainbodyset pms where pm.body_id=pms.body_id and pm.plan_id="+plan_id+" and pm.object_id='"+object_id+"' and pm.mainbody_id='"+mainbody_id+"'");
			  if(rowSet.next())
				  name=rowSet.getString("name");
		}
		catch(Exception e)
		{
			   e.printStackTrace();
	     }
		return name;
	}
	
	
	/***
	 * 取得考核对象和考核主体的对应考核关系
	 * @param sqls
	 * @param plan_id
	 * @param targetMakeSeries
	 * @return
	 */
   public HashMap getObjectAndMainBodyRelation(String sqls,String plan_id,String targetMakeSeries)
   {
	   HashMap map = new HashMap();
	   try
	   {
		   StringBuffer sql = new StringBuffer();
		   sql.append("select a.mainbody_id,a.object_id,a.b0110,a.e0122,a.e01a1,a.a0101,a.body_id,");
		   sql.append("b.sp_flag,");
		   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
		       sql.append("to_char(b.sp_date,'yyyy-mm-dd hh24:mi') as sp_date");
			}
		   else
		   {
			   sql.append("convert(varchar(16),b.sp_date,20) as sp_date");
		   }
		   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
		       sql.append(",c.level_o ");
			}
		   else
		   {
			   sql.append(",c.level ");
		   }
		   sql.append(" from per_mainbody_std a left join (select mainbody_id,object_id,sp_date,sp_flag from per_mainbody where plan_id="+plan_id+") b on a.object_id=b.object_id ");
		   sql.append(" and a.mainbody_id=b.mainbody_id left join per_mainbodyset c on a.body_id=c.body_id where a.object_id in ("+sqls+") ");
		   sql.append(" and a.body_id in (select body_id from per_mainbodyset where ");
		   if("1".equals(targetMakeSeries))
		   {
			   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				{
					sql.append(" level_o ='1' ");
				}
				else
				{
					sql.append(" level='1' ");
				}
		   }
		   else if("2".equals(targetMakeSeries))
		   {
			   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				{
					sql.append(" level_o ='1' or level_o ='0' ");
				}
				else
				{
					sql.append(" level='1' or level='0' ");
				}
		   }
		   else if("3".equals(targetMakeSeries))
		   {
			   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				{
					sql.append(" level_o ='1' or level_o ='0' or level_o ='-1'");
				}
				else
				{
					sql.append(" level='1' or level='0' or level='-1' ");
				}
		   }
		   else if("4".equals(targetMakeSeries))
		   {
			   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				{
					sql.append(" level_o ='1' or level_o ='0' or level_o ='-1' or level_o ='-2'");
				}
				else
				{
					sql.append(" level='1' or level='0' or level='-1' or level='-2' ");
				}
		   }
		   sql.append(") and a.body_id in (select body_id from per_plan_body where plan_id="+plan_id+")  order by a.object_id,a.body_id");
		   ContentDAO dao = new ContentDAO(this.conn);
		   RowSet rs = dao.search(sql.toString());
		   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		   while(rs.next())
		   {
			   String object_id=rs.getString("object_id");
			   LazyDynaBean bean = new LazyDynaBean();
			   bean.set("object_id",object_id);
			   bean.set("mainbody_id",rs.getString("mainbody_id"));
			   bean.set("a0101",rs.getString("a0101"));
			   String sp_flag=rs.getString("sp_flag")==null?"":rs.getString("sp_flag");
			   if("".equals(sp_flag))
				   bean.set("sp_flag", "");
			   else {
					String spFlagDesc=MyObjectiveBo.getSpflagDesc(sp_flag);//AdminCode.getCodeName("23",spf);
					bean.set("sp_flag",spFlagDesc);
			   }
			   bean.set("spitem", sp_flag);
			  // bean.set("sp_flag", rs.getString("sp_flag")==null?"":AdminCode.getCodeName("23",rs.getString("sp_flag")));
			   String sp_date=rs.getString("sp_date");
			  
			   bean.set("sp_date",sp_date==null?"":sp_date);
			   bean.set("body_id", rs.getString("body_id"));
			   bean.set("level",Sql_switcher.searchDbServer()==Constant.ORACEL?rs.getString("level_o"):rs.getString("level"));
			   if(map.get(object_id)==null)
			   {
				   ArrayList mainbodylist = new ArrayList();
				   mainbodylist.add(bean);
				   map.put(object_id, mainbodylist);
			   }
			   else
			   {
				   ArrayList mainbodylist=(ArrayList)map.get(object_id);
				   mainbodylist.add(bean);
				   map.put(object_id, mainbodylist);
			   }
		   }
		   rs.close();
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   return map;
   }
   public HashMap getObjectAndMainBodyRelationForORG(String sqls,String plan_id,String targetMakeSeries,HashMap leaderMap)
   {
	   HashMap rmap = new HashMap();
	   try
	   {
		   HashMap map = new HashMap();
		   StringBuffer sql = new StringBuffer();
		   sql.append("select a.*,");
		   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
		       sql.append("c.level_o ");
			}
		   else
		   {
			   sql.append("c.level ");
		   }
		   sql.append(" from per_mainbody_std a ");
		   sql.append(" left join per_mainbodyset c on a.body_id=c.body_id where a.object_id in ("+sqls+") ");
		   sql.append(" and a.body_id in (select body_id from per_mainbodyset where ");
		   if("1".equals(targetMakeSeries))
		   {
			   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				{
					sql.append(" level_o ='1' ");
				}
				else
				{
					sql.append(" level='1' ");
				}
		   }
		   else if("2".equals(targetMakeSeries))
		   {
			   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				{
					sql.append(" level_o ='1' or level_o ='0' ");
				}
				else
				{
					sql.append(" level='1' or level='0' ");
				}
		   }
		   else if("3".equals(targetMakeSeries))
		   {
			   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				{
					sql.append(" level_o ='1' or level_o ='0' or level_o ='-1'");
				}
				else
				{
					sql.append(" level='1' or level='0' or level='-1' ");
				}
		   }
		   else if("4".equals(targetMakeSeries))
		   {
			   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				{
					sql.append(" level_o ='1' or level_o ='0' or level_o ='-1' or level_o ='-2'");
				}
				else
				{
					sql.append(" level='1' or level='0' or level='-1' or level='-2' ");
				}
		   }
		   sql.append(") and a.body_id in(select body_id from per_plan_body where plan_id="+plan_id+") order by a.object_id,a.body_id");
		   ContentDAO dao = new ContentDAO(this.conn);
		   RowSet rs = dao.search(sql.toString());
		   //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		  // HashMap spMap = this.getMainBody(plan_id, dao);
		   while(rs.next())
		   {
			   String object_id=rs.getString("object_id");
			   LazyDynaBean bean = new LazyDynaBean();
			  // bean.set("object_id",object_id);
			   bean.set("mainbody_id",rs.getString("mainbody_id"));
			   bean.set("a0101",rs.getString("a0101"));
			   bean.set("body_id", rs.getString("body_id"));
			   bean.set("level",Sql_switcher.searchDbServer()==Constant.ORACEL?rs.getString("level_o"):rs.getString("level"));
			   bean.set("sp_flag","");
			   bean.set("sp_date","");
			   bean.set("spitem","");
			  if(map.get(object_id)==null)
			   {
				   ArrayList mainbodylist = new ArrayList();
				   mainbodylist.add(bean);
				   map.put(object_id, mainbodylist);
			   }
			   else
			   {
				   ArrayList mainbodylist=(ArrayList)map.get(object_id);
				   mainbodylist.add(bean);
				   map.put(object_id, mainbodylist);
			   }
			   //map.put(object_id+"-"+rs.getString("mainbody_id"), bean);
		   }
		   rs.close();
		   StringBuffer buf = new StringBuffer();
		   buf.append("select sp_flag,object_id,mainbody_id,");
		   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
			   buf.append("to_char(sp_date,'yyyy-mm-dd hh24:mi') as sp_date");
			}
		   else
		   {
			   buf.append("convert(varchar(16),sp_date,20) as sp_date");
		   }
		   buf.append(",body_id from per_mainbody where plan_id="+plan_id+" and body_id<>'-1'");
		   RowSet rowSet =dao.search(buf.toString());
		   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		   HashMap noMap=new HashMap();
		   while(rowSet.next())
		   {
			   String objectid=rowSet.getString("object_id");
			   String mainbody_id=rowSet.getString("mainbody_id");
			   LazyDynaBean bean = new LazyDynaBean();
			   String sp_flag= rowSet.getString("sp_flag")==null?"":rowSet.getString("sp_flag");
			   if("".equals(sp_flag))
		    	   bean.set("sp_flag", "");
			   else
				   bean.set("sp_flag",MyObjectiveBo.getSpflagDesc(sp_flag));
			   bean.set("spitem",  rowSet.getString("sp_flag")==null?"":rowSet.getString("sp_flag"));
			   String sp_date=rowSet.getString("sp_date");
			   bean.set("sp_date",sp_date==null?"":sp_date);
			   bean.set("object_id",objectid);
			   bean.set("mainbody_id", mainbody_id);
			   bean.set("body_id",rowSet.getString("body_id"));
			   if(noMap.get(objectid)==null)
			   {
				   ArrayList list = new ArrayList();
				   list.add(bean);
				   noMap.put(objectid, list);
			   }
			   else
			   {
				   ArrayList list = (ArrayList)noMap.get(objectid);
				   list.add(bean);
				   noMap.put(objectid, list);
			   }
		   }
		   rowSet.close();
		   buf.setLength(0);
		   buf.append("select object_id from per_object where plan_id="+plan_id);
		   rowSet = dao.search(buf.toString());
		   ArrayList mblist = null;
		   HashMap mm=new HashMap();
		   while(rowSet.next())
		   {
			   mblist = new ArrayList();
			   String object_id=rowSet.getString("object_id");
			   LazyDynaBean lbean = (LazyDynaBean)leaderMap.get(object_id+plan_id);
			   if(lbean==null)
				   continue;
			   String leaderid=(String)lbean.get("a0100");
			   ArrayList yplist=null;
			   if(noMap.get(object_id)!=null)
			   {
				    yplist = (ArrayList)noMap.get(object_id);
			   }
			   else
				   yplist = new ArrayList();
			   ArrayList mainbodylist = (ArrayList)map.get(leaderid);
			   if(mainbodylist!=null)
			   {
			        for(int i=0;i<mainbodylist.size();i++)
		    	   {
				       LazyDynaBean bean = (LazyDynaBean)mainbodylist.get(i);
	    			   String mainbody_id=(String)bean.get("mainbody_id");
		    		   boolean isadd=false;
		    		   for(int j=0;j<yplist.size();j++)
		    		   {
			    		   LazyDynaBean ypbean=(LazyDynaBean)yplist.get(j);
			    		   String oid=(String)ypbean.get("object_id");
				    	   String mid=(String)ypbean.get("mainbody_id");
			    	       if(oid.equals(object_id)&&mid.equals(mainbody_id))
				           {
				        		 LazyDynaBean newbean=new LazyDynaBean();
					    	     newbean.set("sp_flag",(String)ypbean.get("sp_flag"));
					        	 newbean.set("sp_date",(String)ypbean.get("sp_date"));
					         	 newbean.set("mainbody_id",(String)bean.get("mainbody_id"));
					    		 newbean.set("a0101",(String)bean.get("a0101"));
					    		 newbean.set("body_id", (String)bean.get("body_id"));
						    	 newbean.set("level",(String)bean.get("level"));
						    	 newbean.set("spitem",(String)bean.get("spitem"));
						    	 mblist.add(newbean);
						    	 isadd=true;
				            }
	     		   	   }
			    	   if(!isadd)
			    	     mblist.add(bean);
		    	   }
			   }
			   rmap.put(object_id, mblist);
		   }
		   rowSet.close();
		   
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   return rmap;
   }
   public HashMap getMainBody(String plan_id,ContentDAO dao)
   {
	   HashMap map = new HashMap();
	   try
	   {
		   String sql = "select sp_flag,sp_date,mainbody_id,object_id,body_id from per_mainbody where plan_id="+plan_id;
	       RowSet rs = dao.search(sql);
	       SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	       while(rs.next())
	       {
	    	   String sp_flag=rs.getString("sp_flag")==null?"":AdminCode.getCodeName("23",rs.getString("sp_flag"));
	    	   Date sp_date=rs.getDate("sp_date");
	    	   String mainbody_id=rs.getString("mainbody_id");
	    	   String object_id=rs.getString("object_id");
	    	   String body_id=rs.getString("body_id");
	    	   LazyDynaBean bean = new LazyDynaBean();
			   bean.set("sp_date",sp_date==null?"":format.format(sp_date));
			   bean.set("sp_flag",sp_flag);
			   map.put(mainbody_id+object_id+body_id, bean);
	       }
	       rs.close();
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   return map;
   }
   private HashMap currMap = new HashMap();//存放每个对象的当前审批主体
   public HashMap getMainbodyBySeq(String plan_id,String condSql){
	   HashMap map = new HashMap();
	   RowSet rs = null;
	   try{
		   StringBuffer sql = new StringBuffer();
		   String colum="level";
		   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			   colum="level_o";
		   sql.append("select a.object_id,a.mainbody_id,a.a0101,a.sp_flag,a.body_id,a.sp_seq,");
		   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
			   sql.append("to_char(a.sp_date,'yyyy-mm-dd hh24:mi') as sp_date ");
			}
		   else
		   {
			   sql.append("convert(varchar(16),a.sp_date,20) as sp_date ");
		   }
		   sql.append(" ,b."+colum+",c.currappuser from per_mainbody a  left join per_mainbodyset b on a.body_id=b.body_id ");
		   sql.append(" left join per_object c on a.plan_id=c.plan_id and a.object_id=c.object_id and a.mainbody_id=c.currappuser ");
		   sql.append(" where  a.plan_id="+plan_id+" and a.sp_seq is not null ");
		   sql.append(" and a.object_id in ("+condSql+")");
		   sql.append(" order by a.object_id,a.sp_seq ");
		   ContentDAO dao = new ContentDAO(this.conn);
		   rs=dao.search(sql.toString());
		   int maxLeaderLay=0;
		   int lay=1;
		   String aobject_id="";
		   int i=0;
		   int a_seq=0;
		   while(rs.next()){
			   LazyDynaBean bean = new LazyDynaBean();
			   if(i==0)
			   {
				   aobject_id=rs.getString("object_id");
				   a_seq=rs.getInt("sp_seq");
			   }
			   if(!aobject_id.equalsIgnoreCase(rs.getString("object_id")))
			   {
				   aobject_id=rs.getString("object_id");
				   a_seq=rs.getInt("sp_seq");
				   if(maxLeaderLay<lay)
					   maxLeaderLay=lay;
				   lay=1;
			   }
			   if(a_seq==rs.getInt("sp_seq"))//序号一样，是同一层级的
			   {
				   
			   }else{
				   lay++;
				   a_seq=rs.getInt("sp_seq");
			   }
			   bean.set("lay", lay+"");
			   String object_id=rs.getString("object_id");
			   int sp_seq = rs.getInt("sp_seq");
			   bean.set("sp_seq", sp_seq+"");
			   bean.set("mainbody_id",rs.getString("mainbody_id"));
			   bean.set("a0101",rs.getString("a0101"));
			   String sp_flag=rs.getString("sp_flag")==null?"":rs.getString("sp_flag");
			   if("".equals(sp_flag))
				   bean.set("sp_flag", "");
			   else {
				   String spFlagDesc=MyObjectiveBo.getSpflagDesc(sp_flag);
				   bean.set("sp_flag",spFlagDesc);
			   }
			   bean.set("spitem",sp_flag);
			   String sp_date = rs.getString("sp_date");
			   bean.set("sp_date",sp_date==null?"":sp_date);
			   bean.set("body_id", rs.getString("body_id"));
			   bean.set("level",rs.getString(colum)==null?"1000":rs.getString(colum));
			   if(map.get(object_id+lay)==null){
				   ArrayList mainbodyList = new ArrayList();
				   mainbodyList.add(bean);
				   map.put(object_id+lay, mainbodyList);
			   }else{
				   ArrayList mainbodyList=(ArrayList)map.get(object_id+lay);
				   mainbodyList.add(bean);
				   map.put(object_id+lay, mainbodyList);
			   } 
			   i++;
			   String currappuser=rs.getString("currappuser");
			   if(currappuser!=null&&!"".equals(currappuser.trim()))
			   {
				   LazyDynaBean _bean=new LazyDynaBean();
					_bean.set("a0100",rs.getString("mainbody_id"));
					_bean.set("a0101",rs.getString("a0101"));
					String _desc="第"+SetUnderlingObjectiveBo.toUpperNumber(lay)+"级";
					_bean.set("lay_desc", _desc);
					_bean.set("level", rs.getString(colum)==null?"1000":rs.getString(colum));
					_bean.set("sp_seq",sp_seq+"");
					currMap.put(object_id, _bean);
			   }
		   }
		   if(maxLeaderLay<lay)
	     	   maxLeaderLay=lay;
		   this.setMaxLeaderLay(maxLeaderLay);
	   }catch(Exception e){
		   e.printStackTrace();
	   }finally{
		   try{
			   if(rs!=null)
				   rs.close();
		   }catch(Exception e){
			   e.printStackTrace();
		   }
	   }
	   return map;
   }
   public ArrayList getNotStandardObjectMainbody(String plan_id,String object_id,String targetMakeSeries)
   {
	   ArrayList list = new ArrayList();
	   try
	   {
		   StringBuffer sql = new StringBuffer();
		   sql.append("select a.object_id,a.mainbody_id,a.a0101,a.sp_flag,a.body_id,");
		   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
			   sql.append("to_char(a.sp_date,'yyyy-mm-dd hh24:mi') as sp_date ");
			}
		   else
		   {
			   sql.append("convert(varchar(16),a.sp_date,20) as sp_date ");
		   }
		   sql.append(" from per_mainbody a where a.body_id in (select body_id from per_mainbodyset where ");
		   if("1".equals(targetMakeSeries))
		   {
			   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				{
					sql.append(" level_o ='1' ");
				}
				else
				{
					sql.append(" level='1' ");
				}
		   }
		   else if("2".equals(targetMakeSeries))
		   {
			   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				{
					sql.append(" level_o ='1' or level_o ='0' ");
				}
				else
				{
					sql.append(" level='1' or level='0' ");
				}
		   }
		   else if("3".equals(targetMakeSeries))
		   {
			   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				{
					sql.append(" level_o ='1' or level_o ='0' or level_o ='-1'");
				}
				else
				{
					sql.append(" level='1' or level='0' or level='-1' ");
				}
		   }
		   else if("4".equals(targetMakeSeries))
		   {
			   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				{
					sql.append(" level_o ='1' or level_o ='0' or level_o ='-1' or level_o ='-2'");
				}
				else
				{
					sql.append(" level='1' or level='0' or level='-1' or level='-2' ");
				}
		   }
		   sql.append(") and a.plan_id="+plan_id+" and a.object_id='"+object_id+"'");
		   ContentDAO dao = new ContentDAO(this.conn);
		   RowSet rs = dao.search(sql.toString());
		   HashMap map = this.getMainbodySet("0");
		   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		   while(rs.next())
		   {
			  if(!this.isCanSP(plan_id, object_id, rs.getString("mainbody_id")))
				  continue;
			   LazyDynaBean bean = new LazyDynaBean();
			   bean.set("object_id",object_id);
			   bean.set("mainbody_id",rs.getString("mainbody_id"));
			   bean.set("a0101",rs.getString("a0101"));
			   String sp_flag=rs.getString("sp_flag")==null?"":rs.getString("sp_flag");
			   if("".equals(sp_flag))
				   bean.set("sp_flag", "");
			   else {
				   String spFlagDesc=MyObjectiveBo.getSpflagDesc(sp_flag);//AdminCode.getCodeName("23",spf);
				   bean.set("sp_flag",spFlagDesc);
			   }
			   bean.set("spitem",sp_flag);
			   String sp_date = rs.getString("sp_date");
			   bean.set("sp_date",sp_date==null?"":sp_date);
			   bean.set("body_id", rs.getString("body_id"));
			   bean.set("level",(String)map.get(rs.getString("body_id")));
		       list.add(bean);
		   }
		   rs.close();
			 
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   return list;
   }
   public HashMap getMainbodySet(String bodytype)
	{
		HashMap map = new HashMap();
		try
		{
			String sql ="select body_id,";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			   sql+="level_o"; 
			else
				sql+="level";
			sql+=" as lv from per_mainbodyset where body_type='"+bodytype+"' or body_type is null";
		    ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				map.put(rs.getString("body_id"),rs.getString("lv"));
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
   /**
    * 求得所有考核对象汇报关系
    * @param sqls
    * @return
    */
  public HashMap getObjectRelation(String sqls,String plan_id,String targetMakeSeries,boolean isORG)
  {
	  HashMap map = new HashMap();
	  try
	  {
		  StringBuffer buf = new StringBuffer();
		  if(isORG)
		  {
			  buf.append("select a.e01a1,b.object_id as a0100 from usra01 a,("+sqls+") b where a.a0100=b.a0100");
		  }
		  else
		  {
		      buf.append("select a.a0100,a.e01a1 from usra01 a where a.a0100 in ("+sqls+")");
		  }
		  ContentDAO dao = new ContentDAO(this.conn);
		  RowSet rs = dao.search(buf.toString());
		  RenderRelationBo bo = new RenderRelationBo(this.conn);
		  ArrayList dbnameList=new ArrayList();
		  dbnameList.add("USR");
		  HashMap mainBodyMap = this.getHasActionToObject(plan_id);
		  while(rs.next())
		  {
			  String e01a1=rs.getString("e01a1");
			  String object_id=rs.getString("a0100");
			  ArrayList posid = new ArrayList();
			  posid.add(e01a1);
			  HashMap leaderMap = bo.getReportLeaderMap(posid, Integer.parseInt(targetMakeSeries), dbnameList);
			  Set set=leaderMap.keySet();
			  ArrayList list = new ArrayList();
			  for(Iterator t=set.iterator();t.hasNext();)
			  {
				  String key=(String)t.next();
				/*  bean.set("a0101",rs.getString("a0101"));
			    	bean.set("a0100", rs.getString("a0100"));
			    	bean.set("e01a1",rs.getString("e01a1"));
			    	bean.set("b0110", rs.getString("b0110"));
			    	bean.set("e0122",rs.getString("e0122"));
			    	if(n==1)
			    		bean.set("level","1");
			    	if(n==2)
			    		bean.set("level","0");
			    	if(n==3)
			    		bean.set("level","-1");
			    	if(n==4)
			    		bean.set("level","-2");*/
				  LazyDynaBean a_bean = new LazyDynaBean();
				  LazyDynaBean bean=(LazyDynaBean)leaderMap.get(key);
				  a_bean.set("a0101",(String)bean.get("a0101"));
				  a_bean.set("a0100",(String)bean.get("a0100"));
				  a_bean.set("e01a1",(String)bean.get("e01a1"));
				  a_bean.set("b0110",(String)bean.get("b0110"));
				  a_bean.set("e0122",(String)bean.get("e0122"));
				  a_bean.set("level",(String)bean.get("level"));
				  String mainbody_id=(String)bean.get("a0100");
				  if(mainBodyMap.get(object_id+mainbody_id)!=null)
				  {
					  LazyDynaBean abean = (LazyDynaBean)mainBodyMap.get(object_id+mainbody_id);
					  a_bean.set("sp_flag",(String)abean.get("sp_flag"));
					  a_bean.set("sp_date", (String)abean.get("sp_date"));
					  a_bean.set("body_id", (String)abean.get("body_id"));
					  a_bean.set("spitem", (String)abean.get("spitem"));
				  }
				  else
				  {
					  a_bean.set("sp_flag","");
					  a_bean.set("sp_date", "");
					  a_bean.set("body_id", "");
					  a_bean.set("spitem", "");
				  }
				  list.add(a_bean);
			  }
			  map.put(rs.getString("a0100"),list);
		  }
		  rs.close();
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  return map;
  }
  /**
   * 取得考核计划下已经对考核对象有动作的考核主体
   * @param plan_id
   * @return
   */
  public HashMap getHasActionToObject(String plan_id)
  {
	  HashMap map= new HashMap();
	  try
	  {
		  StringBuffer mainbody=new StringBuffer();
		  mainbody.append("select object_id,mainbody_id,sp_flag,");
		  if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
			  mainbody.append("to_char(sp_date,'yyyy-mm-dd hh24:mi') as sp_date");
			}
		   else
		   {
			   mainbody.append("convert(varchar(16),sp_date,20) as sp_date");
		   }
		  mainbody.append(" ,body_id from per_mainbody where plan_id="+plan_id);
		  ContentDAO dao = new ContentDAO(this.conn);
		  RowSet rs = dao.search(mainbody.toString());
		 // SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		  while(rs.next())
		  {
			  LazyDynaBean bean = new LazyDynaBean();
			  String sp_flag=rs.getString("sp_flag")==null?"":rs.getString("sp_flag");
			  if("".equals(sp_flag))
				  bean.set("sp_flag", "");
			  else {
					String spFlagDesc=MyObjectiveBo.getSpflagDesc(sp_flag);//AdminCode.getCodeName("23",spf);
					bean.set("sp_flag",spFlagDesc);
			  }
			  bean.set("spitem", sp_flag);
			  //bean.set("sp_flag",AdminCode.getCodeName("23", rs.getString("sp_flag")));
			  String sp_date = rs.getString("sp_date");
			  bean.set("sp_date",sp_date==null?"":sp_date);
			  bean.set("body_id", rs.getString("body_id")==null?"":rs.getString("body_id"));
			  map.put(rs.getString("object_id")+rs.getString("mainbody_id"),bean);
		  }
		  rs.close();
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  return map;
  }
  public ArrayList getLeaderColoumnList(int level)
  {
	  ArrayList list = new ArrayList();
	  try
	  {
		  LazyDynaBean bean=null;
		  if(level>10000)
		  {
			  int alevel=level-10000;
			  for(int i=1;i<=alevel;i++){
				  String dstr="第"+SetUnderlingObjectiveBo.toUpperNumber(i)+"级审批时间";
				  String cstr="第"+SetUnderlingObjectiveBo.toUpperNumber(i)+"级审批";
				  bean = new LazyDynaBean();
				  bean.set("sp",cstr);
				  bean.set("spd",dstr);
				  list.add(bean);
			  }
		  }else{
			  for(int i=1;i<=level;i++)
			  {
				  String dstr="";
				  String cstr="";
				  if(i==1)
				  {
					  dstr="直接上级审批时间";
					  cstr="直接上级审批";
				  }
				  if(i==2)
				  {
					  dstr="间接上级审批时间";
					  cstr="间接上级审批";
				  }
				  if(i==3)
				  {
					  dstr="第三级上级审批时间";
					  cstr="第三级上级审批";
				  }
				  if(i==4)
				  {
					  dstr="第四级上级审批时间";
					  cstr="第四级上级审批";
				  }
				  bean = new LazyDynaBean();
				  bean.set("sp",cstr);
				  bean.set("spd",dstr);
				  list.add(bean);
			  }
		  }
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  return list;
  }
  public HashMap getAllMainBodyList(String plan_id,String object_id,boolean isORG,String orgid)
  {
	  HashMap allMainBodyInfo = new HashMap();
	  try
	  {
		  ArrayList list = new ArrayList();
		  String market="1";//=1自定义=0考核关系或者汇报关系
		  StringBuffer sql = new StringBuffer("");
		  StringBuffer orgInfo=new StringBuffer("");
		  ContentDAO dao = new ContentDAO(this.conn);
		  if(isORG)
		  {
			  sql.append("select a.codeitemdesc as uncode,b.codeitemdesc as umcode,po.kh_relations  from per_object po");
			  sql.append(" left join organization a on po.b0110=a.codeitemid left join organization b on po.e0122=b.codeitemid ");
		      sql.append(" where object_id='"+orgid+"' and (UPPER(b.codesetid)='UN' or UPPER(b.codesetid)='UM') and plan_id="+plan_id);
		  }
		  else
		  {
			  sql.append("select a.codeitemdesc as uncode,b.codeitemdesc as umcode,c.codeitemdesc as pcode,po.a0101,po.kh_relations  from per_object po");
			  sql.append(" left join organization a on po.b0110=a.codeitemid left join organization b on po.e0122=b.codeitemid ");
	     	  sql.append(" left join organization c on po.e01a1 = c.codeitemid ");
		      sql.append(" where object_id='"+object_id+"' and plan_id="+plan_id);
		  }
		  
		  RowSet rs = dao.search(sql.toString());
		  while(rs.next())
		  {
			  market=rs.getString("kh_relations");
			  orgInfo.append(rs.getString("uncode")==null?"":rs.getString("uncode")+"  ");
			  if(isORG&&rs.getString("uncode").equalsIgnoreCase(rs.getString("umcode")))
			  {
				  
			  }
			  else
			  {
		    	  orgInfo.append(rs.getString("umcode")==null?"":rs.getString("umcode")+"  ");
			  }
			  if(!isORG)
			  {
		    	  orgInfo.append(rs.getString("pcode")==null?"":rs.getString("pcode")+"  ");
			      orgInfo.append(rs.getString("a0101"));
			  }
		  }
		  rs.close();
		  if(market==null|| "".equals(market))
			  market="0";
		  LoadXml parameter_content = null;
		  if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"")==null)
			{
					
	         	parameter_content = new LoadXml(this.conn,plan_id+"");
				BatchGradeBo.getPlanLoadXmlMap().put(plan_id+"",parameter_content);
			}
			else
			{
				parameter_content=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"");
			}
		  Hashtable params = parameter_content.getDegreeWhole();
		// 目标管理审批模式:0-考核关系 1-汇报关系. 默认为 0
		  String  targetAppMode=(String)params.get("targetAppMode"); 
		  String targetMakeSeries=(String)params.get("targetMakeSeries");
		  if(targetAppMode==null)
			  targetAppMode="0";
		  int maxLevel=0;
		  if("0".equals(targetAppMode))
		  {
			  int level=1;
			  level=Integer.parseInt((targetMakeSeries==null|| "".equals(targetMakeSeries))?"1":targetMakeSeries);
			  for(int i=1;i<=level;i++)
			  {
				  HashMap map =this.getLeaderInfoByLevel(plan_id, object_id, i, market, dao, level,isORG,orgid);
				  String isAction=(String)map.get("isAction");
				  ArrayList leaderList = (ArrayList)map.get("leaderList");
				  LazyDynaBean bean = new LazyDynaBean();
				  bean.set("level",i+"");
				  if(leaderList.size()>0)
					  maxLevel=i;
				  bean.set("sublist",leaderList);
				  bean.set("isAction",isAction);
				  bean.set("spa0100",(String)map.get("spa0100"));
				  bean.set("sp_flag",(String)map.get("sp_flag"));
				  list.add(bean);
			  }
		  }
		  else
		  {
			  int level=1;
			  level=Integer.parseInt((targetMakeSeries==null|| "".equals(targetMakeSeries))?"1":targetMakeSeries);
			  for(int i=1;i<=level;i++)
			  {
				  HashMap map =this.getRelation(plan_id, object_id, market, dao,i,isORG,orgid);
				  String isAction=(String)map.get("isAction");
				  ArrayList leaderList = (ArrayList)map.get("leaderList");
				  LazyDynaBean bean = new LazyDynaBean();
				  bean.set("level",i+"");
				  bean.set("sublist",leaderList);
				  if(leaderList.size()>0)
					  maxLevel=i;
				  bean.set("isAction",isAction);
				  bean.set("spa0100",(String)map.get("spa0100"));
				  bean.set("sp_flag",(String)map.get("sp_flag"));
				  list.add(bean);
			  }
		  }
		  allMainBodyInfo.put("maxLevel", maxLevel+"");
		  allMainBodyInfo.put("list",list);
		  allMainBodyInfo.put("orgInfo",orgInfo.toString());
		  allMainBodyInfo.put("targetAppMode",targetAppMode);
		  allMainBodyInfo.put("targetMakeSeries", targetMakeSeries);
		  allMainBodyInfo.put("market", market);
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  return allMainBodyInfo;
  }
  /**
   * 取得第几级领导列表
   * @param plan_id
   * @param object_id
   * @param level
   * @param market =1自定义=0考核关系或者汇报关系
   * @param dao
   * @param maxLevel
   * @return
   */
  public HashMap getLeaderInfoByLevel(String plan_id,String object_id,int level,String market,ContentDAO dao,int maxLevel,boolean isORG,String orgid)
  {
	  HashMap map = new HashMap();
	  try
	  {
		  StringBuffer sql = new StringBuffer("");
		  String isAction="0";//该级领导是否对考核对象进行过审批=0没有=1进行过
		  ArrayList leaderList = new ArrayList();
		  String spa0100="-1";
		  String sped_flag="-1";
		  if("1".equals(market))
		  {
			  level=this.getMainBodySetLevel(level);
			  String id="";
			  if(isORG)
				  id=orgid;
			  else
				  id=object_id;
			  sql.append("select mainbody_id,a0101,sp_flag,body_id from per_mainbody where object_id='"+id+"'");
			  sql.append(" and plan_id="+plan_id+" and body_id in (select body_id from per_mainbodyset where ");
			  if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			     sql.append("level_o="+level+")");
			  else
				  sql.append("level="+level+")");
			  RowSet rs = dao.search(sql.toString());
			  while(rs.next())
			  {
				  String sp_flag=rs.getString("sp_flag");
				  if(sp_flag!=null&&("02".equals(sp_flag)|| "03".equals(sp_flag)|| "07".equals(sp_flag)))
				  {
					  sped_flag=sp_flag;
					  spa0100=rs.getString("mainbody_id");
					  isAction="1";
				  }
				  LazyDynaBean bean = new LazyDynaBean();
				  bean.set("a0100",rs.getString("mainbody_id"));
				  bean.set("a0101",rs.getString("a0101"));
				  bean.set("body_id", level+"");
				  leaderList.add(bean);
			  }
		  }
		  else
		  {
			  level=this.getMainBodySetLevel(level);
			  if(isORG)
			  {
				  sql.append(" select a.mainbody_id,a.a0101,b.sp_flag,a.body_id from per_mainbody_std a ");
		    	  sql.append("left join (select mainbody_id,object_id,sp_flag from per_mainbody where plan_id="+plan_id+" and object_id='"+orgid+"' and mainbody_id<>'"+object_id+"') b on a.mainbody_id=b.mainbody_id");
		    	  sql.append(" where a.object_id='"+object_id+"' and a.body_id in (");
		    	  sql.append("select body_id from per_mainbodyset where ");
			  }
			  else
			  {
		    	  sql.append(" select a.mainbody_id,a.a0101,b.sp_flag,a.body_id from per_mainbody_std a ");
		    	  sql.append("left join (select mainbody_id,object_id,sp_flag from per_mainbody where plan_id="+plan_id+") b on a.mainbody_id=b.mainbody_id and a.object_id=b.object_id");
		    	  sql.append(" where a.object_id='"+object_id+"' and a.body_id in (");
		    	  sql.append("select body_id from per_mainbodyset where ");
			  }
			  if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				    sql.append("level_o="+level+")");
			  else
					sql.append("level="+level+")");
			  sql.append(" and a.body_id in(select body_id from per_plan_body where plan_id="+plan_id+")");
			  RowSet rs = dao.search(sql.toString());
			  while(rs.next())
			  {
				  String sp_flag=rs.getString("sp_flag");
				  if(sp_flag!=null&&("02".equals(sp_flag)|| "03".equals(sp_flag)|| "07".equals(sp_flag)))
				  {
					  sped_flag=sp_flag;
					  spa0100=rs.getString("mainbody_id");
					  isAction="1";
				  }
				  LazyDynaBean bean = new LazyDynaBean();
				  bean.set("a0100",rs.getString("mainbody_id"));
				  bean.set("a0101",rs.getString("a0101"));
				  bean.set("body_id",level+"");
				  leaderList.add(bean);
			  }
			  rs.close();
		  }
		  map.put("isAction",isAction);
		  map.put("leaderList",leaderList);
		  map.put("spa0100",spa0100);
		  map.put("sp_flag",sped_flag);
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  return map;
  }
  public HashMap getRelation(String plan_id,String object_id,String market,ContentDAO dao,int level,boolean isORG,String orgid)
  {
	  HashMap map = new HashMap();
	  try
	  {
		  StringBuffer sql = new StringBuffer("");
		  String isAction="0";//该级领导是否对考核对象进行过审批=0没有=1进行过
		  ArrayList leaderList = new ArrayList();
		  String spa0100="-1";
		  String sped_flag="-1";
		  if("1".equals(market))
		  {
			  level=this.getMainBodySetLevel(level);
			  String id="";
			  if(isORG)
				  id=orgid;
			  else
				  id=object_id;
			  sql.append("select mainbody_id,a0101,sp_flag,body_id from per_mainbody where object_id='"+id+"'");
			  sql.append(" and plan_id="+plan_id);
			  if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				  sql.append(" and body_id in (select body_id from per_mainbodyset where level_o='"+level+"')");
			  else
				  sql.append(" and body_id in (select body_id from per_mainbodyset where level='"+level+"')");
			  RowSet rs = dao.search(sql.toString());
			  while(rs.next())
			  {
				  String sp_flag=rs.getString("sp_flag")==null?"01":rs.getString("sp_flag");
				  if(sp_flag!=null&&("02".equals(sp_flag)|| "03".equals(sp_flag)|| "07".equals(sp_flag)))
				  {
					  spa0100=rs.getString("mainbody_id");
					  sped_flag=sp_flag;
					  isAction="1";
				  }
				  LazyDynaBean bean = new LazyDynaBean();
				  bean.set("a0100",rs.getString("mainbody_id"));
				  bean.set("a0101",rs.getString("a0101"));
				  bean.set("body_id", level+"");
				  leaderList.add(bean);
			  }
			  rs.close();
		  }
		  else
		  {
			  String str = "select e01a1 from per_object where object_id='"+object_id+"' and plan_id="+plan_id;
			  RowSet rowSet  = dao.search(str);
			  String posid="";
			  while(rowSet.next())
			  {
				  posid=rowSet.getString("e01a1");
			  }
			  rowSet.close();
			  if(isORG)
			  {
				  HashMap m=this.getOrgLeader(plan_id,null);
				  LazyDynaBean bb=(LazyDynaBean)m.get(orgid+plan_id);
				  posid=(String)bb.get("e01a1");
			  }
			  RenderRelationBo bo = new RenderRelationBo(this.conn);
			  ArrayList dbnameList = new ArrayList();
			  dbnameList.add("usr");
			  ArrayList list = new ArrayList();
			  list.add(posid);
			  HashMap mainbodymap = bo.getReportLeaderMap2(list, level, dbnameList);
			  Set set = mainbodymap.keySet();
			  StringBuffer mainbody_id = new StringBuffer("");
			  for(Iterator t=set.iterator();t.hasNext();)
			  {
				  String key=(String)t.next();
				  LazyDynaBean bean = (LazyDynaBean)mainbodymap.get(key);
				  LazyDynaBean abean= new LazyDynaBean();
				  abean.set("a0100", (String)bean.get("a0100"));
				  abean.set("a0101", (String)bean.get("a0101"));
				  abean.set("body_id", (String)bean.get("level"));
				  leaderList.add(abean);
				  mainbody_id.append(","+(String)bean.get("a0100"));
			  }
			  String mainbody="''";
			  if(mainbody_id.toString().length()>0)
				  mainbody=mainbody_id.toString().substring(1);
			  sql.setLength(0);
			  sql.append("select mainbody_id,sp_flag from per_mainbody where plan_id="+plan_id);
			  sql.append(" and object_id='"+(isORG?orgid:object_id)+"'"+" and mainbody_id in ("+mainbody+")");
			  rowSet = dao.search(sql.toString());
			  while(rowSet.next())
			  {
				  String sp_flag=rowSet.getString("sp_flag")==null?"01":rowSet.getString("sp_flag");
				  if(sp_flag!=null&&("02".equals(sp_flag)|| "03".equals(sp_flag)|| "07".equals(sp_flag)))
				  {
			    	  spa0100=rowSet.getString("mainbody_id");
			    	  sped_flag=rowSet.getString("sp_flag")==null?"01":rowSet.getString("sp_flag");
			    	  isAction="1";
		    		  break;
				  }
			  }
			  rowSet.close();
		  }
		  map.put("isAction",isAction);
		  map.put("leaderList",leaderList);
		  map.put("spa0100", spa0100);
		  map.put("sp_flag",sped_flag);
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  return map;
  }
  public HashMap getMainbody(String plan_id)
  {
	  HashMap map = new HashMap();
	  try
	  {
		  String sql = "select mainbody_id,object_id from per_mainbody where plan_id="+plan_id;
		  ContentDAO dao = new ContentDAO(this.conn);
		  RowSet rs = dao.search(sql);
		  while(rs.next())
		  {
			  map.put(rs.getString("mainbody_id")+rs.getString("object_id"), "1");
		  }
		  rs.close();
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  return map;
  }
  /**
   * 
   * @param plan_id
   * @return
   */
  public HashMap getMainbodyBean(String plan_id)
  {
	  HashMap map = new HashMap();
	  try
	  {
		  LoadXml parameter_content = null;
	      if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"")==null)
		  {
					
	           parameter_content = new LoadXml(this.conn,plan_id+"");
			  BatchGradeBo.getPlanLoadXmlMap().put(plan_id+"",parameter_content);
		  }
		  else
		  {
			  parameter_content=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"");
		  }
		  Hashtable params = parameter_content.getDegreeWhole();
		  String targetMakeSeries=(String)params.get("targetMakeSeries");
		  String sql = "select a.mainbody_id,a.object_id,";
		  if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				  sql+="b.level_o ";
		  else
			  sql+="b.level ";
		  sql+=" from per_mainbody a,per_mainbodyset b where plan_id="+plan_id;
		  sql+=" and a.body_id=b.body_id ";
		  StringBuffer buf = new StringBuffer();
		  if(targetMakeSeries!=null&& "1".equals(targetMakeSeries))
	       {
	    	   buf.append(" and a.body_id in(");
	    	   buf.append("select distinct body_id from per_mainbodyset where ");
	    	   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
   			{
	    			buf.append(" level_o ='1' ");
	    		}
	    		else
	    		{
	    			buf.append(" level='1' ");
	    		}
	    	   buf.append(")");
	       }
	       else if(targetMakeSeries!=null&& "2".equals(targetMakeSeries))
	       {
	    	   buf.append(" and a.body_id in(");
	    	   buf.append("select distinct body_id from per_mainbodyset where ");
	    	   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
   			{
	    			buf.append(" level_o ='1' or level_o='0'");
	    		}
	    		else
	    		{
	    			buf.append(" level='1' or level='0'");
	    		}
	    	   buf.append(")");
	       }
	       else if(targetMakeSeries!=null&& "3".equals(targetMakeSeries))
	       {
	    	   buf.append(" and a.body_id in(");
	    	   buf.append("select distinct body_id from per_mainbodyset where ");
	    	   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
   			{
	    			buf.append(" level_o ='1' or level_o='0' or level_o='-1'");
	    		}
	    		else
	    		{
	    			buf.append(" level='1' or level='0' or level='-1'");
	    		}
	    	   buf.append(")");
	       }
	       else
	       {
	    	   buf.append(" and a.body_id in(");
	    	   buf.append("select distinct body_id from per_mainbodyset where ");
	    	   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
   			{
	    			buf.append(" level_o ='1' or level_o='0' or level_o='-1' or level_o='-2'");
	    		}
	    		else
	    		{
	    			buf.append(" level='1' or level='0' or level='-1' or level='-2'");
	    		}
	    	   buf.append(")");
	       }
		  sql+=buf.toString();
		  ContentDAO dao = new ContentDAO(this.conn);
		  RowSet rs = dao.search(sql);
		  while(rs.next())
		  {
			  LazyDynaBean bean = new LazyDynaBean();
			  String level="";
			  if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				  level=rs.getString("level_o")==null?"1000":rs.getString("level_o");
			  else
				  level=rs.getString("level")==null?"1000":rs.getString("level");
			  int mlevel=this.getTargetMakeSeriesLevel(Integer.parseInt(level));
			  if(targetMakeSeries==null||mlevel>Integer.parseInt(targetMakeSeries))
				  continue;
			  bean.set("level",level);
			  map.put(rs.getString("mainbody_id")+rs.getString("object_id"), bean);
		  }
		  rs.close();
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  return map;
  }
  public String getAgentInfo(UserView view)
  {
	  StringBuffer buf = new StringBuffer("");
	  try
	  {
		  String sql ="select b0110,e0122,e01a1,a0101 from "+view.getDbname()+"a01 where a0100='"+view.getA0100()+"'";
		  ContentDAO dao = new ContentDAO(this.conn);
		  RowSet rs = dao.search(sql);
		  while(rs.next())
		  {
			  String b0110=AdminCode.getCodeName("UN",rs.getString("b0110"));
			  String e0122=AdminCode.getCodeName("UM",rs.getString("e0122"));
			  String e01a1=AdminCode.getCodeName("@K",rs.getString("e01a1"));
			  buf.append((b0110==null|| "".equals(b0110))?"":(b0110+"/"));
			  buf.append((e0122==null|| "".equals(e0122))?"":(e0122+"/"));
			  buf.append((e01a1==null|| "".equals(e01a1))?"":(e01a1+"/"));
			  buf.append(rs.getString("a0101"));
		  }
		  rs.close();
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  return buf.toString();
  }
  public int getMainBodySetLevel(int level)
  {
	  int mbsl=1;
	  switch(level)
	  {
	  case 1:
		  mbsl=1;
		  break;
	  case 2:
		  mbsl=0;
		  break;
	  case 3:
		  mbsl=-1;
		  break;
	  case 4:
		  mbsl=-2;
		  break;
	  }
	  return mbsl;
  }
  public int getTargetMakeSeriesLevel(int level)
  {
	  int mbsl=1;
	  switch(level)
	  {
	  case 1:
		  mbsl=1;
		  break;
	  case 0:
		  mbsl=2;
		  break;
	  case -1:
		  mbsl=3;
		  break;
	  case -2:
		  mbsl=4;
		  break;
	  }
	  return mbsl;
  }
  public void saveNewXML(String object_id,String plan_id,String newXML)
	{
		try
		{
			String sql = "update per_object set reasons=? where object_id='"+object_id+"' and plan_id="+plan_id;
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList list = new ArrayList();
			list.add(newXML);
			dao.update(sql,list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

	/**
	 *
	 * @param object_id
	 * @param plan_id
	 * @param view
	 * @param opt 1:初始化 2:状态初始化
	 */
	public void initObjectiveCardState(String object_id,String plan_id,UserView view,String opt)
	{
		RowSet rs = null;
		try {
			if (object_id == null || "".equals(object_id))
				return;
			String[] arr = object_id.split("/");
			StringBuffer sql = new StringBuffer("");
			// String agent_name=getAgentInfo(view);
			ContentDAO dao = new ContentDAO(this.conn);
			ParseXmlBo bo = new ParseXmlBo(this.conn);
			ArrayList objectList = new ArrayList();
			ArrayList mainbodyList = new ArrayList();
			ArrayList<String> sendWaitTaskList = new ArrayList<String>();
			String agent_name = "";
			LoadXml parameter_content = null;
			if (BatchGradeBo.getPlanLoadXmlMap().get(plan_id + "") == null) {
				parameter_content = new LoadXml(this.conn, plan_id + "");
				BatchGradeBo.getPlanLoadXmlMap().put(plan_id + "", parameter_content);
			} else {
				parameter_content = (LoadXml) BatchGradeBo.getPlanLoadXmlMap().get(plan_id + "");
			}
			Hashtable params = parameter_content.getDegreeWhole();

			if (view.getStatus() == 4) {
				agent_name = getAgentInfo(view);
			} else {
				agent_name = view.getUserFullName();
			}

			StringBuffer buf = new StringBuffer();
			String targetTraceItem = "";
			String publicPointCannotEdit = ""; // 不允许修改目标卡中的共性指标 lium
			if ("1".equals(opt)) {

				targetTraceItem = (String) params.get("TargetTraceItem");
				// 不允许修改目标卡中的共性指标 lium
				publicPointCannotEdit = (String) params.get("PublicPointCannotEdit");

				AnalysePlanParameterBo appbo = new AnalysePlanParameterBo(this.conn);
				Hashtable ht_table = appbo.analyseParameterXml();
				if (ht_table != null && ht_table.get("TargetTraceItem") != null && (targetTraceItem == null || targetTraceItem.trim().length() <= 0))
					targetTraceItem = (String) ht_table.get("TargetTraceItem");
			}
			if (targetTraceItem != null && targetTraceItem.trim().length() > 0) {
				String[] items = targetTraceItem.split(",");
				for (int i = 0; i < items.length; i++) {
					// 排除重复指标。如果出现指标有重复的情况，下面指标初始化时就会出现重复列问题。
					// 并且排除调整后分值(P0421)和调整后权重(P0423)，因为后面代码默认会初始化这两个字段。如果不排除，跟踪指标也设置了这两个指标的时候同样会出现重复列问题。 chent 20171122 add
					if (buf.indexOf("," + items[i].toUpperCase()) > -1 || "P0421".equals(items[i].toUpperCase()) || "P0423".equals(items[i].toUpperCase())) {
						continue;
					}
					buf.append("," + items[i].toUpperCase() + "=null ");
				}
			}

			RecordVo vo = new RecordVo("per_plan");
			vo.setInt("plan_id", Integer.parseInt(plan_id));
			vo = dao.findByPrimaryKey(vo);

			for (int i = 0; i < arr.length; i++) {
				if (arr[i] == null || "".equals(arr[i]))
					continue;
				/**初始化时，将有效的任务也初始化，将以删除的任务真正删除 2011-04-11 浪潮*/

				if ("1".equals(opt)) {
					sql.setLength(0);
					sql.append(" delete from p04 where plan_id=");
					sql.append(plan_id);
					sql.append(" and ");
					if (vo.getInt("object_type") == 2)
						sql.append(" a0100=");
					else
						sql.append(" b0110=");
					sql.append("'" + arr[i] + "'");
					sql.append(" and chg_type=3 and state=-1 ");
					dao.delete(sql.toString(), new ArrayList());
					sql.setLength(0);
					sql.append("update p04 set chg_type=null,state=null,processing_state=0,p0421=null,p0423=null,f_p0400=null" + buf.toString() + " where  plan_id=");
					sql.append(plan_id + " and ");
					if (vo.getInt("object_type") == 2)
						sql.append(" a0100=");
					else
						sql.append(" b0110=");
					sql.append("'" + arr[i] + "'");
					if ("true".equalsIgnoreCase(publicPointCannotEdit)) { // 不允许修改目标卡中的共性指标 lium
						sql.append(" AND EXISTS ( SELECT 1 FROM per_template_item WHERE p04.item_id=item_id AND kind=2)");
					}
					dao.update(sql.toString());
				}
				sql.setLength(0);
				if ("1".equals(opt)) {
					sql.append("update per_object set sp_flag='01', trace_sp_flag='01',report_date=null,currappuser=null where plan_id=" + plan_id + " and object_id='" + arr[i] + "'");
				} else {
					sql.append("update per_object set sp_flag='01', trace_sp_flag='01',report_date=null,currappuser=null where plan_id=" + plan_id + " and object_id='" + arr[i] + "'");
				}
				objectList.add(sql.toString());
				sql.setLength(0);
				sql.append("update per_mainbody set sp_flag='', sp_date=null,status='0',score=0 where plan_id=" + plan_id + " and object_id='" + arr[i] + "'");
				mainbodyList.add(sql.toString());
				sql.setLength(0);
				sql.append(" delete from per_target_evaluation where plan_id=" + plan_id + " and object_id='" + arr[i] + "'");
				dao.delete(sql.toString(), new ArrayList());
				String newXML = "";
				if ("2".equals(opt))
					newXML = bo.produceRecord(arr[i], plan_id, view.getA0100(), "usr", "", "-01", "0", "", agent_name, "");
				else
					newXML = bo.produceRecord(arr[i], plan_id, view.getA0100(), "usr", "", "01", "0", "", agent_name, "");
				this.saveNewXML(arr[i], plan_id, newXML);

				sendWaitTaskList.add(arr[i]);
			}

			dao.batchUpdate(objectList);
			dao.batchUpdate(mainbodyList);

			// 主体类别没有选中本人时，给最近的打分的主体发送待办 zhanghua 2018-3-9
			String object_type = String.valueOf(vo.getInt("object_type"));   //1部门 2：人员
			String temp = Sql_switcher.searchDbServer() == Constant.ORACEL ? " pms.level_o" : " pms.level ";
			ArrayList<String> objectName=new ArrayList<String>();
			// 判断主体为本人的记录是否存在
			String mainBodySelfSql = null;
			if ("2".equals(object_type)) {
				mainBodySelfSql = "SELECT 1 FROM per_mainbody pm,per_mainbodyset pms,per_plan_body ppb WHERE pm.body_id=pms.body_id AND " + temp + "=5 AND pm.plan_id=? and pm.plan_id=ppb.plan_id  and pm.body_id=ppb.body_id ";
			} else {
				mainBodySelfSql = "SELECT 1 FROM per_mainbody pm,per_plan_body ppb WHERE pm.body_id=-1 AND pm.plan_id=? and pm.plan_id=ppb.plan_id and pm.body_id=ppb.body_id ";
			}
			rs = dao.search(mainBodySelfSql, Arrays.asList(new Object[]{Integer.valueOf(plan_id)}));
			if (!rs.next()) {
				int property = 10;
				String targetMakeSeries = (String) params.get("targetMakeSeries"); // 目标卡制订支持几级审批
				if (targetMakeSeries != null && targetMakeSeries.trim().length() > 0) {
					property = Integer.parseInt(targetMakeSeries);
				}

				String level_str = "";
				switch (property) { // 不是本人，不在审批流程中的主体
					case 1:
						level_str = "1";
						break;
					case 2:
						level_str = "1,0";
						break;
					case 3:
						level_str = "1,0,-1";
						break;
					case 4:
						level_str = "1,0,-1,-2";
						break;
					default:
						level_str = "1,0,-1,-2";
				}
				StringBuffer strSql = new StringBuffer("");
				ArrayList dataList=new ArrayList();
				strSql.append("select pm.mainbody_id,pm.object_id,pro.A0101 as object_name," + temp);
				// "确认"的主体无需待办 lium
				strSql.append(" from per_mainbody pm LEFT JOIN per_plan_body ppb ON pm.plan_id = ppb.plan_id AND pm.body_id = ppb.body_id ");
				strSql.append(" inner join per_object pro ON pm.plan_id = pro.plan_id AND pm.object_id = pro.object_id,");
				strSql.append(" per_mainbodyset pms where pm.body_id=pms.body_id ");
				strSql.append(" AND " + Sql_switcher.isnull("ppb.opt", "0") + " <> 1 and (" + Sql_switcher.isnull("ppb.isgrade", "0") + " <>'1') ");
				strSql.append(" and pm.plan_id=? and " + temp + " in (" + level_str + ") ");
				strSql.append(" and pm.object_id in(");
				dataList.add(plan_id);
				for(String str :sendWaitTaskList){
					strSql.append("?,");
					dataList.add(str);
				}
				sendWaitTaskList.clear();
				strSql.deleteCharAt(strSql.length()-1);
				strSql.append(") ");
				switch (Sql_switcher.searchDbServer()) {
					case Constant.MSSQL: {
						strSql.append(" and (" + Sql_switcher.isnull("pm.STATUS", "0") + "=0 or pm.status=0 or pm.status='1') order by pms.level desc");
						break;
					}
					case Constant.DB2: {
						strSql.append(" and (" + Sql_switcher.isnull("pm.STATUS", "0") + "=0 or pm.status=0 or pm.status='1') order by pms.level desc");
						break;
					}
					case Constant.ORACEL: {
						strSql.append(" and (" + Sql_switcher.isnull("pm.STATUS", "0") + "=0 or pm.status=0 or pm.status='1') order by pms.level_o desc");
						break;
					}
				}

				rs = dao.search(strSql.toString(),dataList);
				while(rs.next()) {
					sendWaitTaskList.add(rs.getString("mainbody_id"));
					objectName.add(rs.getString("object_name")+":"+rs.getString("object_id"));
				}
			}
			for (int i=0;i< sendWaitTaskList.size();i++) {
				String to_id=sendWaitTaskList.get(i);
				String objid=objectName.size()>0?objectName.get(i).split(":")[1]:to_id;
				/**给考核对象发代办*/
				String title = "";
				if(objectName.size()>0)
					title=vo.getString("name") + " 考核指标已被初始化，请重新设定。("+objectName.get(i).split(":")[0]+"的目标卡)";
				else
					title=vo.getString("name") + " 考核指标已被初始化，请重新设定。";
				String url = "";
				if ("2".equals(object_type)) {
					url = "/performance/objectiveManage/objectiveCard.do?b_query=query&zglt=0&returnflag=menu&entranceType=0&body_id=5&model=2&opt=1&planid=" + PubFunc.encryption(plan_id) + "&object_id=" + PubFunc.encryption(objid);
				} else {
					url = "/performance/objectiveManage/objectiveCard.do?b_query=query&zglt=0&returnflag=menu&entranceType=0&body_id=5&model=1&opt=1&planid=" + PubFunc.encryption(plan_id) + "&object_id=" + PubFunc.encryption(objid);
					if(objectName.size()==0) {
						rs = dao.search("select mainbody_id from per_mainbody where plan_id=" + plan_id + " and object_id='" + objid + "' and body_id=-1");
						while (rs.next()) {
							to_id = rs.getString(1);
						}
					}
				}
				if (to_id != null && to_id.trim().length() > 0) {
					ObjectCardBo cbo = new ObjectCardBo(this.conn, plan_id, objid, view);
					cbo.insertPending(title, "", to_id, "USR", url, "6");
				}
			}
		}
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }finally{
		  try
		  {
			  if(rs!=null)
				  rs.close();
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
	  }
  }
  /**
   * 取每个部门对应的部门负责人
   * @param planid
   * @return
   */
   public HashMap getOrgLeader(String planid,String objectID)
   {
   	HashMap map = new HashMap();
       try
   	{
   		StringBuffer buf = new StringBuffer();
   		buf.append("select object_id,plan_id,mainbody_id,u.e01a1,u.a0101 from per_mainbody ,usra01 u where ");
   		buf.append(" body_id='-1' and plan_id ="+planid);
   		buf.append(" and per_mainbody.mainbody_id=u.a0100 ");
   		if(objectID!=null&&!"".equals(objectID))
   			buf.append(" and per_mainbody.object_id='"+objectID+"' ");
   		ContentDAO dao = new ContentDAO(this.conn);
   		RowSet rs = dao.search(buf.toString());
   		while(rs.next())
   		{
   			String object_id = rs.getString("object_id");
   			String plan_id = rs.getString("plan_id");
   			String mainbody_id = rs.getString("mainbody_id");
   			String e01a1=rs.getString("e01a1");
   			LazyDynaBean bean = new LazyDynaBean();
   			bean.set("a0100",mainbody_id);
   			bean.set("e01a1",e01a1);
   			bean.set("a0101", rs.getString("a0101"));
   			map.put(object_id+plan_id,bean);
   		}
   		rs.close();
   	}
   	catch(Exception e)
   	{
   		e.printStackTrace();
   	}
   	return map;
   }
   public ArrayList getPlanList(String year,UserView userView)
   {
	   ArrayList rlist = new ArrayList();
	   RowSet rs = null;
		RowSet rs_sub=null;
	   try
	   {
		    StringBuffer sql = new StringBuffer();
			SetUnderlingObjectiveBo suob=new SetUnderlingObjectiveBo(conn);
			ArrayList list = new ArrayList();
			String posid=userView.getUserPosId();
			
			HashMap map = new HashMap();
			/**=0按汇报关系，=1按定义的考核关系*/
			int type=0;
			list.add("USR");
			ContentDAO dao = new ContentDAO(conn);
			map = new HashMap();
			HashMap map1=new HashMap();
			HashMap map2=new HashMap();
			RenderRelationBo bo = new RenderRelationBo(conn,userView);
			sql.append(" select * from (");
			sql.append(" (select "+Sql_switcher.isnull("pp.a0000", "999999")+" as norder,pp.status,pp.plan_id,pp.themonth,pp.thequarter,pp.name,pp.theyear,pp.cycle,"+Sql_switcher.dateToChar("pp.start_date", "yyyy:mm:dd")+" as start_date,"+Sql_switcher.dateToChar("pp.end_date", "yyyy:mm:dd")+" as end_date ");
			sql.append(" from per_plan pp where pp.method='2' and cycle<>'7' and pp.status<>'0'");
			if("zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
				sql.append(" and pp.status<>'7' ");
			if(!"-1".equals(year))
				sql.append(" and pp.theyear="+year);
			sql.append(" and pp.object_type='2')");
			sql.append(" ) temp order by norder asc,plan_id desc");
			rs = dao.search(sql.toString());
			int x=1;
			int y=1;
			while(rs.next())
			{
				String plan_id = rs.getString("plan_id");
				int status=rs.getInt("status");
				HashMap infomap=null;//bo.getPer_MainBodyInfo(list, posid, 3, plan_id);
				if(map1.get(posid+plan_id)!=null)
				{
					infomap=(HashMap)map1.get(posid+plan_id);
				}
				else
				{
	    			infomap =bo.getPer_MainBodyInfo(list, posid, 3, plan_id);
	    			map1.put(posid+plan_id, infomap);
				}
				String theyear=rs.getString("theyear");
				String cycle=rs.getString("cycle");
				String name=rs.getString("name");
				LoadXml parameter_content = null;
    	        if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"")==null)
				{
						
    	         	parameter_content = new LoadXml(conn,plan_id+"");
					BatchGradeBo.getPlanLoadXmlMap().put(plan_id+"",parameter_content);
				}
				else
				{
					parameter_content=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"");
				}
				Hashtable params = parameter_content.getDegreeWhole();
			    String targetMakeSeries=(String)params.get("targetMakeSeries");
				StringBuffer sub_buf = new StringBuffer();
				HashMap mainbodyMap =null;//suob.getMainbodyBean(plan_id);
				if(map2.get(plan_id)!=null)
				{
					mainbodyMap=(HashMap)map2.get(plan_id);
				}
				else
				{
				    mainbodyMap = suob.getMainbodyBean(plan_id);
				    map2.put(plan_id,mainbodyMap);
				}
				sub_buf.append(" select object_id,kh_relations from per_object where plan_id='"+plan_id+"'");
				rs_sub = dao.search(sub_buf.toString());
				boolean flag=false;
				while(rs_sub.next())
				{
					if(flag)
					{
						break;
					}
					String object_id = rs_sub.getString("object_id");
					String market=(rs_sub.getString("kh_relations")==null?"0":rs_sub.getString("kh_relations"));
					if("1".equals(market))
			    	{
						if(mainbodyMap.containsKey(userView.getA0100()+object_id))
		    			{
							LazyDynaBean abean=(LazyDynaBean)mainbodyMap.get(userView.getA0100()+object_id);
							String level=(String)abean.get("level");
							int mlevel=suob.getTargetMakeSeriesLevel(Integer.parseInt(level));
							if(mlevel>Integer.parseInt(targetMakeSeries))
						      continue;
		    				flag=true;
	    					break;
	    				}
			    	}
			    	else
			    	{
			    		for(int i=0;i<list.size();i++)
			    		{
			    			String nbase = (String)list.get(i);
			    			if(infomap.containsKey(nbase+object_id))
			    			{
			    				LazyDynaBean abean=(LazyDynaBean)infomap.get(nbase.toUpperCase()+object_id);
								String isSP=(String)abean.get("isSP");
								if(!"1".equals(isSP))
									continue;
			    				flag=true;
		    					break;
		    				}
		    			}
					}
					
				}
				if(flag)
				{
					if(map.get(plan_id)!=null)
						continue;
					map.put(plan_id, "1");
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("plan_id",plan_id);
					bean.set("name", name);
					bean.set("status", status+"");
					bean.set("a0100", userView.getA0100());
					bean.set("posid",userView.getUserPosId());
					if(status==4||status==6||status==7)
						x++;
					y++;
					rlist.add(bean);
					/*Element child = new Element("TreeNode");
					child.setAttribute("id",plan_id);
					if(cycle.equals("3"))//按月度
		    		{
			     		child.setAttribute("text",name+"("+theyear+ResourceFactory.getProperty("datestyle.year")+" "+rs.getString("themonth")+ResourceFactory.getProperty("datestyle.month")+")");
			    		child.setAttribute("title",name+"("+theyear+ResourceFactory.getProperty("datestyle.year")+" "+rs.getString("themonth")+ResourceFactory.getProperty("datestyle.month")+")");	
		    		}else if(cycle.equals("0"))//年度
	        		{
		    			child.setAttribute("text",name+"("+theyear+ResourceFactory.getProperty("datestyle.year")+")");
			    		child.setAttribute("title",name+"("+theyear+ResourceFactory.getProperty("datestyle.year")+")");	
	        		}
		    		else if(cycle.equals("1"))//半年度
	        		{
		    			String half_year=rs.getString("themonth");
		    			if(half_year.equals("1"))
		    			{
	    	    			child.setAttribute("text",name+"("+theyear+ResourceFactory.getProperty("datestyle.year")+ResourceFactory.getProperty("report.pigeonhole.uphalfyear")+")");
		    	    		child.setAttribute("title",name+"("+theyear+ResourceFactory.getProperty("datestyle.year")+ResourceFactory.getProperty("report.pigeonhole.uphalfyear")+")");
		    			}else
		    			{
		    				child.setAttribute("text",name+"("+theyear+ResourceFactory.getProperty("datestyle.year")+ResourceFactory.getProperty("report.pigeonhole.downhalfyear")+")");
		    	    		child.setAttribute("title",name+"("+theyear+ResourceFactory.getProperty("datestyle.year")+ResourceFactory.getProperty("report.pigeonhole.downhalfyear")+")");
		    			}
	        		}else if(cycle.equals("7"))//不定期
	        		{
	        			String startd=PubFunc.DoFormatDate(rs.getString("start_date"));
	        			String endd=PubFunc.DoFormatDate(rs.getString("end_date"));
	        			child.setAttribute("text",name+"("+startd+"-"+endd+")");
	    	    		child.setAttribute("title",name+"("+startd+"-"+endd+")");
	        		}
	        		else if(cycle.equals("2"))
	        		{
	        			String quarter = rs.getString("thequarter");
	        			child.setAttribute("text",name+"("+AdminCode.getCodeName("12",quarter)+")");
	    	    		child.setAttribute("title",name+"("+AdminCode.getCodeName("12",quarter)+")");
	        		}
		     		//child.setAttribute("xml","/servlet/performance/UnderlingObjectiveServlet?year="+theyear+"&posid="+posid+"&a0100="+a0100);						
		     	    child.setAttribute("target","mil_body");
                    child.setAttribute("href","/performance/objectiveManage/setUnderlingObjective/underling_objective_list.do?b_init=init&opt=1&posid="+posid+"&a0100="+a0100+"&plan_id="+plan_id);
				    child.setAttribute("icon","/images/icon_wsx.gif");	
					root.addContent(child);*/
				}
				if(x==y)
					rlist=new ArrayList();
			}
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   finally
	   {
		   if(rs!=null)
		   {
			   try
			   {
				   rs.close();
			   }
			   catch(Exception e)
			   {
				   e.printStackTrace();
			   }
		   }
		   if(rs_sub!=null)
		   {
			   try
			   {
				  rs_sub.close(); 
			   }
			   catch(Exception e)
			   {
				   e.printStackTrace();
			   }
		   }
	   }
	   return rlist;
   }
   /**
    * 审批过程查看
    * @param plan_id
    * @param a0101
    * @param e0122
    * @param status
    * @param type
    * @param view
    * @param targetMakeSeries
    * @param objectID
    * @return
    */
   public ArrayList getObjectInfoList2(String plan_id,String a0101,String e0122,String status,int type,UserView view,String targetMakeSeries,String objectID)
	{
		ArrayList infoList = new ArrayList();
		try
		{
			StringBuffer sql = new StringBuffer("");
			ContentDAO dao = new ContentDAO(this.conn);
			/**部门负责人object_id+plan_id---->bean(a0100,e01a1)*/
			RecordVo vo = new RecordVo("per_plan");
			vo.setInt("plan_id", Integer.parseInt(plan_id));
			vo=dao.findByPrimaryKey(vo);
			String object_type=vo.getString("object_type");
			HashMap leaderMap = null;
			boolean isORG=false;
			/**团队考核*/
			if(!"2".equals(object_type))
			{
				isORG=true;
				leaderMap=this.getOrgLeader(plan_id,objectID);
			}
			sql.append(" from per_object  where plan_id="+plan_id);
			if(objectID!=null&&!"".equals(objectID))
				sql.append(" and object_id='"+objectID+"'");
			else
			{
		    	if(!view.isSuper_admin())
   	        	{
		    		String privSQL=this.getPrivWhereSQL(view, object_type, "usr");
   		        	sql.append(privSQL);
   	        	}
		    	if(!(a0101==null|| "".equals(a0101)))
		    	{
		     		sql.append(" and a0101 like '"+a0101+"%'");
		    	}
		    	if(!"-1".equals(e0122))
		    	{
			    	if(e0122==null|| "".equals(e0122))
   		    	{
   		     		sql.append(" and 1=2 ");
   		    	}
   		    	else
   		    	{
   		    		String code=e0122.substring(0,2);
   		    		String value=e0122.substring(2);
   		    		if("UN".equalsIgnoreCase(code))
   		    		{
   			    		sql.append(" and (b0110 like '"+value+"%'");
   			    		if("".equals(value))
   				    		sql.append(" or b0110 is null");
   			    		sql.append(")");
   			     	}
   			     	if("UM".equalsIgnoreCase(code))
   			    	{
   			    		sql.append(" and (e0122 like '"+value+"%'");
   			    		if("".equals(value))
   			    			sql.append(" or e0122 is null");
   			    		sql.append(")");
   			    	}
   		    		if("@K".equalsIgnoreCase(code))
   		    		{
   		    			sql.append(" and (e01a1 like '"+value+"%'");
   		    			if("".equals(value))
   		     				sql.append(" or e01a1 is null");
   		    			sql.append(")");
   	    			}
   	    		}
		    	}
	    		if(!"-1".equals(status))
		    	{
		    		if("01".equals(status))
		    			sql.append(" and  (sp_flag is null or sp_flag='01')");
		    		else
		    			sql.append(" and sp_flag = '"+status+"'");
		    	}
			}
			//sql.append(" order by a0000");
			ArrayList objectList = new ArrayList();
			RowSet rs = null;
			StringBuffer objectSQL=new StringBuffer();
			objectSQL.append("select object_id,a0101,kh_relations,sp_flag,");
		    if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
		    	objectSQL.append("to_char(report_date,'yyyy-mm-dd hh24:mi') as report_date ");
			}
		   else
		   {
			   objectSQL.append("convert(varchar(16),report_date,20) as report_date ");
		   }
		    objectSQL.append(sql.toString()+" order by a0000");
			rs = dao.search(objectSQL.toString());
			HashMap map =null;
			/**按考核关系，所有考核主体*/
			if(type==0)
			{
				String condSQL ="";
				if(isORG)
				{
					sql.setLength(0);
					sql.append("select mainbody_id as object_id from per_mainbody where plan_id="+plan_id);
					sql.append(" and body_id='-1'");
					if(objectID!=null&&!"".equals(objectID))
				    	sql.append(" and object_id='"+objectID+"'");
					condSQL=sql.toString();
				}
				else
				{
					condSQL+="select object_id "+sql.toString();
				}
				if(isORG)
				{
					map=getObjectAndMainBodyRelationForORG(condSQL, plan_id, targetMakeSeries,leaderMap);
				}
				else
		    		map=this.getObjectAndMainBodyRelation(condSQL, plan_id, targetMakeSeries);
			
			}
			/**按汇报关系,所有上级*/
			else
			{
				String condSQL ="";
				if(isORG)
				{
					sql.setLength(0);
					sql.append("select mainbody_id as a0100,object_id from per_mainbody where plan_id="+plan_id);
					sql.append(" and body_id='-1'");
					if(objectID!=null&&!"".equals(objectID))
						sql.append(" and object_id='"+objectID+"'");
					condSQL=sql.toString();
				}
				else
				{
					condSQL+="select object_id as a0100 "+sql.toString();
				}
				map=this.getObjectRelation(condSQL,plan_id,targetMakeSeries,isORG);
			
			}
			SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd hh:mm");
			HashMap p04Map = this.getP04Info(plan_id,isORG);
			LoadXml parameter_content = null;
	        if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"")==null)
			{
					
	         	parameter_content = new LoadXml(this.conn,plan_id+"");
				BatchGradeBo.getPlanLoadXmlMap().put(plan_id+"",parameter_content);
			}
			else
			{
				parameter_content=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"");
			}
			Hashtable params = parameter_content.getDegreeWhole();
			String taskAdjustNeedNew = (String) params.get("taskAdjustNeedNew");
			/* if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				{
				  mainbody.append("to_char(sp_date,'yyyy-mm-dd hh24:mi') as sp_date");
				}
			   else
			   {
				   mainbody.append("convert(varchar(16),sp_date,20) as sp_date");
			   }*/
			if (taskAdjustNeedNew == null)
				taskAdjustNeedNew = "false";
			while(rs.next())
			{
				
				LazyDynaBean bean = new LazyDynaBean();
				String object_id=rs.getString("object_id");
				if(isORG)
				{
					if(leaderMap.get(object_id+plan_id)==null)
						continue;
				}
				String kh_relations=rs.getString("kh_relations");
				if(kh_relations==null)
					kh_relations="0";
				bean.set("object_id",rs.getString("object_id"));
				bean.set("a0101", rs.getString("a0101"));
				String report_date=rs.getString("report_date");
				
				bean.set("report_date", report_date==null?"":report_date);
				if(p04Map.get(object_id+plan_id)!=null&& "false".equalsIgnoreCase(taskAdjustNeedNew))
				{
					//one+="(调整后)";
					String sp_flag=rs.getString("sp_flag")==null?"01":rs.getString("sp_flag");
					String spflagDesc=MyObjectiveBo.getSpflagDesc(sp_flag);
					/*if(sp_flag.equals("07"))
					{
						if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("bjpt"))
						{
						bean.set("sp_flag", spflagDesc+"(调整后)");
						}
						else
						{
			    			bean.set("sp_flag", "退回(调整后)");
						}
					}
					else if(sp_flag.equals("02"))
					{
						bean.set("sp_flag", spflagDesc+"(调整后)");
					}
					else if(sp_flag.equals("03"))
					{
						bean.set("sp_flag", "已办理(调整后)");
					}
					else
				    	bean.set("sp_flag", AdminCode.getCodeName("23", sp_flag)+"(调整后)");*/
					bean.set("sp_flag", spflagDesc+"(调整后)");
				}
				else
				{
					String sp_flag=rs.getString("sp_flag")==null?"01":rs.getString("sp_flag");
					String spflagDesc=MyObjectiveBo.getSpflagDesc(sp_flag);
					/*if(sp_flag.equals("07"))
					{
						if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("bjpt"))
						{
							bean.set("sp_flag", "退回");
						}
						else
						{
					    	bean.set("sp_flag", "退回");
						}
					}
					else if(sp_flag.equals("02"))
					{
						bean.set("sp_flag", "已交办");
					}
					else if(sp_flag.equals("03"))
					{
						bean.set("sp_flag", "已办理");
					}
					else
				    	bean.set("sp_flag", AdminCode.getCodeName("23", sp_flag));*/
					bean.set("sp_flag", spflagDesc);
					
				}
			
				bean.set("flag", (rs.getString("sp_flag")==null?"01":rs.getString("sp_flag")));
				ArrayList mainbodyList = null;
				if("1".equals(kh_relations))
					mainbodyList=this.getNotStandardObjectMainbody(plan_id, object_id, targetMakeSeries);
				else
				{
					if(isORG)
					{
						LazyDynaBean lbean=(LazyDynaBean)leaderMap.get(object_id+plan_id);
						String a0100=(String)lbean.get("a0100");
						mainbodyList=(ArrayList)map.get(object_id);
					}
					else
					{
				    	mainbodyList=(ArrayList)map.get(object_id);
					}
				}
				/*if(type==0)
				{*/
					ArrayList list1=new ArrayList();
					ArrayList list2=new ArrayList();
					ArrayList list3=new ArrayList();
					ArrayList list4=new ArrayList();
					HashMap levelMap=null;
					if(vo.getInt("plan_type")==0)
					{
						levelMap=this.getLevelName();
					}
					if(mainbodyList!=null)
					{
			    		for(int i=0;i<mainbodyList.size();i++)
			    		{
			     			LazyDynaBean abean =(LazyDynaBean)mainbodyList.get(i);
			     			String body_id=(String)abean.get("body_id");
			    			String level=(String)abean.get("level");
			    			String sp_flag=(String)abean.get("sp_flag");
				    		String sp_date=(String)abean.get("sp_date");
			    			String mainName=(String)abean.get("a0101");
			    			String spitem=(String)abean.get("spitem");
			    			if("1".equals(kh_relations))
			    			{
			    				String mainbodyid=(String)abean.get("mainbody_id");
			    				if(!this.isCanSP(plan_id, object_id, mainbodyid))
			    				{
			    					continue;
			    				}
			    			}
							/*if(spitem.equals("07"))
							{
								sp_flag="退回";
							}
							if(spitem.equals("02"))
							{
								sp_flag="已交办";
							}
							if(spitem.equals("03"))
							{
								sp_flag="已办理";
							}*/
							sp_flag=MyObjectiveBo.getSpflagDesc(spitem) ;
				    		if("1".equals(level))
				    		{
					    		LazyDynaBean bean1=new LazyDynaBean();
				    			if(!"".equals(sp_flag))
						    	{
					    			if(p04Map.get(object_id+plan_id)!=null&& "false".equalsIgnoreCase(taskAdjustNeedNew))
						    		{
						    			sp_flag+="(调整后)";
						    		}
						    	}
				    			if(levelMap!=null)
				    			{
				    				if(levelMap.get(body_id)!=null)
				    			    	mainName=(String)levelMap.get(body_id);
				    				else
				    					mainName="";
				    			}
					    		bean1.set("a0101", mainName);
					    		bean1.set("sp_flag", sp_flag);
					    		bean1.set("sp_date", sp_date);
					    		list1.add(bean1);
				    		}
				    		else if("0".equals(level))
				    		{
					    		LazyDynaBean bean2=new LazyDynaBean();
					    		if(!"".equals(sp_flag))
					    		{
					    			if(p04Map.get(object_id+plan_id)!=null&& "false".equalsIgnoreCase(taskAdjustNeedNew))
					    			{
					    				sp_flag+="(调整后)";
					    			}
					    		}
					    		if(levelMap!=null)
				    			{
					    			if(levelMap.get(body_id)!=null)
				    			    	mainName=(String)levelMap.get(body_id);
				    				else
				    					mainName="";
				    			}
					    		bean2.set("a0101", mainName);
					    		bean2.set("sp_flag", sp_flag);
					    		bean2.set("sp_date", sp_date);
					    		list2.add(bean2);
					    	}
				    		else if("-1".equals(level))
				    		{
				    			LazyDynaBean bean3=new LazyDynaBean();
					    		if(!"".equals(sp_flag))
					    		{
					    			if(p04Map.get(object_id+plan_id)!=null&& "false".equalsIgnoreCase(taskAdjustNeedNew))
					    			{
					    				sp_flag+="(调整后)";
					    			}
					    		}
					    		if(levelMap!=null)
					    		{
					    			if(levelMap.get(body_id)!=null)
				    			    	mainName=(String)levelMap.get(body_id);
				    				else
				    					mainName="";
					    		}
					    		bean3.set("a0101", mainName);
					    		bean3.set("sp_flag", sp_flag);
					     		bean3.set("sp_date", sp_date);
					    		list3.add(bean3);
					    	}
					    	else
				    		{
				    			LazyDynaBean bean4=new LazyDynaBean();
					    		if(!"".equals(sp_flag))
					    		{
						    		if(p04Map.get(object_id+plan_id)!=null&& "false".equalsIgnoreCase(taskAdjustNeedNew))
						    		{
					 	    			sp_flag+="(调整后)";
					    			}
					    		}
					    		if(levelMap!=null)
					    		{
					    			if(levelMap.get(body_id)!=null)
				    			    	mainName=(String)levelMap.get(body_id);
				    				else
				    					mainName="";
					    		}
					     		bean4.set("a0101", mainName);
				    			bean4.set("sp_flag", sp_flag);
				    			bean4.set("sp_date", sp_date);
					    		list4.add(bean4);
				    		}
				    	}
					}
					int plan_level=Integer.parseInt(targetMakeSeries);
					for(int i=1;i<=plan_level;i++)
					{
						if(i==1)
						{
							bean.set(i+"desc","第一级评委");
							bean.set(i+"rowspan", (list1.size()+1)+"");
							bean.set(i+"",list1);
						}
						if(i==2)
						{
							bean.set(i+"desc","第二级评委");
							bean.set(i+"rowspan", (list2.size()+1)+"");
							bean.set(i+"",list2);
						}
						if(i==3)
						{
							bean.set(i+"desc","第三级评委");
							bean.set(i+"rowspan", (list3.size()+1)+"");
							bean.set(i+"",list3);
						}
						if(i==4)
						{
							bean.set(i+"desc","第四级评委");
							bean.set(i+"rowspan", (list4.size()+1)+"");
							bean.set(i+"",list4);
						}
					}
				infoList.add(bean);
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return infoList;
	}
   /**
    * 取主体类别名称
    * @return
    */
   public HashMap getLevelName()
   {
	   HashMap map = new HashMap();
	   try
	   {
		   String sql="select * from per_mainbodyset where body_id!=-1";
		   ContentDAO dao = new ContentDAO(this.conn);
		   RowSet rs=dao.search(sql);
		   String cName="level";
		   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			   cName="level_o";
		   while(rs.next())
		   {
			   String level=rs.getString("body_id");
			   if(level!=null&&!"".equals(level))
			   {
				   map.put(level, rs.getString("name"));
			   }
		   }
		   rs.close();
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   return map;
   }
   /**
    * 暂时没用
    * @param plan_id
    * @param object_id
    * @return
    */
   public ArrayList getPointList(String plan_id,String object_id)
	{
		ArrayList pointList = new ArrayList();
		try
		{
			RecordVo vo = new RecordVo("per_plan");
			ContentDAO dao = new ContentDAO(this.conn);
			vo.setInt("plan_id", Integer.parseInt(plan_id));
			vo=dao.findByPrimaryKey(vo);
			RecordVo vo2=new RecordVo("per_template");
			vo2.setString("template_id", vo.getString("template_id"));
			vo2=dao.findByPrimaryKey(vo);
			StringBuffer sql = new StringBuffer();
			sql.append("select p0400,p0415,p0413 from p04 where ");
			sql.append("plan_id="+plan_id+" and (state!=-1 or state is null)");
			if("2".equals(vo.getString("object_type")))
			{
				sql.append(" and a0100='"+object_id+"'");
			}
			else
			{
				sql.append(" and b0110='"+object_id+"'");
			}
			RowSet rs= dao.search(sql.toString());
			while(rs.next())
			{
				String temp="";
				if("1".equals(vo2.getString("status")))//权重
				{
					temp=rs.getString("p0400")+"/"+(rs.getString("p0415")==null?"0":String.valueOf((rs.getFloat("p0415")*100)));
				}
				else
				{
					temp=rs.getString("p0400")+"/"+(rs.getString("p0413")==null?"0":rs.getString("p0413"));
				}
				pointList.add(temp);
			}
			sql.setLength(0);
			sql.append("select p0400,p0421,p0423 from p04 where ");
			sql.append("plan_id="+plan_id+" and state=-1 and ( Chg_type is null or Chg_type!=3 )");
			if("2".equals(vo.getString("object_type")))
			{
				sql.append(" and a0100='"+object_id+"'");
			}
			else
			{
				sql.append(" and b0110='"+object_id+"'");
			}
		    rs= dao.search(sql.toString());
			while(rs.next())
			{
				String temp="";
				if("1".equals(vo2.getString("status")))//权重
				{
					temp=rs.getString("p0400")+"/"+(rs.getString("p0423")==null?"0":String.valueOf((rs.getFloat("p0423")*100)));
				}
				else
				{
					temp=rs.getString("p0400")+"/"+(rs.getString("p0421")==null?"0":rs.getString("p0421"));
				}
				pointList.add(temp);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return pointList;
	}
   /**
    * 当非标准考核关系时，如果定义主体顺序号，则序号最小的主体才可以参与审批
    * @param plan_id
    * @param object_id
    * @param mainbodyid
    * @return
    */
   public boolean isCanSP(String plan_id,String object_id,String mainbodyid)
   {
	   boolean flag=true;
	   RowSet rs=null;
	   try
	   {
		   StringBuffer buf = new StringBuffer("");
		   ContentDAO dao = new ContentDAO(this.conn);
		   String cloumn="level";
		   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			   cloumn="level_o";
		   buf.append("select min(sp_seq) sp_seq from per_mainbody where body_id in (");
		   buf.append(" select body_id from per_mainbodyset where "+cloumn+"=(");
		   buf.append("select distinct(b."+cloumn+") from per_mainbody a left join per_mainbodyset b on a.body_id=b.body_id ");
		   buf.append(" where a.plan_id="+plan_id+" and a.object_id='"+object_id+"'");
		   buf.append(" and a.mainbody_id='"+mainbodyid+"'");
		   buf.append("))");
		   buf.append(" and plan_id="+plan_id+" and object_id='"+object_id+"'");
		   rs = dao.search(buf.toString());
		   String seq="";
		   while(rs.next())
		   {
			   seq=rs.getString("sp_seq")==null?"":rs.getString("sp_seq");
		   }
		   buf.setLength(0);
		   if("".equals(seq))
			   return flag;
		   buf.append(" select sp_seq from per_mainbody ");
		   buf.append(" where plan_id="+plan_id+" and object_id='"+object_id+"'");
		   buf.append(" and mainbody_id='"+mainbodyid+"'");
		   rs=dao.search(buf.toString());
		   String myseq="";
		   while(rs.next())
		   {
			   myseq=rs.getString("sp_seq")==null?"":rs.getString("sp_seq");
		   }
		   if("".equals(myseq))
			   flag=false;
		   if(!myseq.equalsIgnoreCase(seq))
			   flag=false;
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   finally
	   {
		   if(rs!=null)
		   {
			   try
			   {
				   rs.close();
			   }catch(Exception e)
			   {
				   e.printStackTrace();
			   }
		   }
	   }
	   return flag;
   	}
	public ArrayList getDeptList() {
		return deptList;
	}
	public void setDeptList(ArrayList deptList) {
		this.deptList = deptList;
	}
	public String getObjMainbodys() {
		return objMainbodys;
	}
	public void setObjMainbodys(String objMainbodys) {
		this.objMainbodys = objMainbodys;
	}	
	
	/**
	 * plan_id 计划号
	 * 获得某编号的考核计划的所有信息
	 */
	public RecordVo getPlanVo(String plan_id)
	{
		RecordVo vo=new RecordVo("per_plan");
		try
		{
			vo.setInt("plan_id",Integer.parseInt(plan_id));
			ContentDAO dao = new ContentDAO(this.conn);
			vo=dao.findByPrimaryKey(vo);					
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}
	
	public String isNull(String str)
    {
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str))
		    str = "";
		return str;
    }
	/***
	 * 
	 * @param plan_id
	 * @param type type=1没序号的不算
	 * @return
	 */
	public HashMap getObjectBySeq(String plan_id,int type){
		HashMap map = new HashMap();
		RowSet rs = null;
		try{
			String column="level";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				column="level_o";
			StringBuffer buf = new StringBuffer("");
			ContentDAO dao = new ContentDAO(this.conn);
			if(type==3){//当允许领导指定目标卡时，首页代办用，只找起草的考核对象
				buf.append(" select object_id,per_mainbody.sp_seq ,"+column+" from ");
				buf.append(" per_mainbody,per_mainbodyset where ");
				buf.append(" per_mainbody.body_id=per_mainbodyset.body_id ");
				buf.append(" and plan_id="+plan_id);
				buf.append(" and object_id<>'"+this.view.getA0100()+"' and mainbody_id='"+this.view.getA0100()+"' ");
				buf.append(" and object_id in (select object_id from per_object");
				buf.append(" where plan_id="+plan_id+" and (sp_flag='01' or sp_flag='07' or sp_flag is null))");
				rs=dao.search(buf.toString());
				while(rs.next()){
					 String seq=rs.getString("sp_seq")==null?"":rs.getString("sp_seq");
					   String object_id=(String)rs.getString("object_id");
					   String level=rs.getString(column)==null?"1000":rs.getString(column);
					   String isSp="1";
					   if((seq==null|| "".equals(seq)))
						   continue;
					   LazyDynaBean bean = new LazyDynaBean();
					   bean.set("level",level);
					   bean.set("isSP",isSp);
					   bean.set("seq",seq);
					   map.put(object_id, bean);
				}
			}else{
				RecordVo vo = new RecordVo("per_plan");
				vo.setInt("plan_id",Integer.parseInt(plan_id));
				vo = dao.findByPrimaryKey(vo);
				int obj_type=vo.getInt("object_type");
				buf.append(" select object_id,per_mainbody.sp_seq ,"+column+",per_mainbody.body_id from ");
				buf.append(" per_mainbody,per_mainbodyset where ");
				buf.append(" per_mainbody.body_id=per_mainbodyset.body_id ");
				buf.append(" and plan_id="+plan_id);
				buf.append(" and "+column+"<>5 and mainbody_id='"+this.view.getA0100()+"' ");
				rs=dao.search(buf.toString());
				while(rs.next()){
					   String seq=rs.getString("sp_seq")==null?"":rs.getString("sp_seq");
					   String object_id=(String)rs.getString("object_id");
					   String level=rs.getString(column)==null?"1000":rs.getString(column);
					   String body_id=rs.getString("body_id")==null?"":rs.getString("body_id");
					   String isSp="1";
					   if(type==1&&(seq==null|| "".equals(seq))){
						   if((obj_type==1||obj_type==3||obj_type==4)&& "-1".equals(body_id)){//解决团队负责人
							   
						   }else
					    	   continue;
					   }
					   if(seq==null|| "".equals(seq))
						   isSp="0";
					   LazyDynaBean bean = new LazyDynaBean();
					   bean.set("level",level);
					   bean.set("isSP",isSp);
					   bean.set("seq",seq);
					   map.put(object_id, bean);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return map;
	}
	
	/**
	 * 判断计划下，非自评的的记录是否存在
	 * @return
	 */
	public HashMap getObjectBySeq(){
		
		HashMap rmap = new HashMap();
		RowSet rs = null;
		try{
			String column="level";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				column="level_o";
			StringBuffer buf = new StringBuffer("");
			ContentDAO dao = new ContentDAO(this.conn);
			buf.append(" select per_plan.object_type,per_mainbody.plan_id,object_id,per_mainbody.sp_seq ,"+column+",per_mainbody.body_id from ");
			buf.append(" per_mainbody,per_mainbodyset,per_plan where ");
			buf.append(" per_mainbody.body_id=per_mainbodyset.body_id ");
			buf.append(" and "+column+"<>5 and mainbody_id='"+this.view.getA0100()+"' and per_plan.plan_id=per_mainbody.plan_id");
			rs=dao.search(buf.toString()); 
			while(rs.next()){
				HashMap map = new HashMap();
				String plan_id = rs.getString("plan_id");
				if(rmap.get(plan_id) == null){
					String seq=rs.getString("sp_seq")==null?"":rs.getString("sp_seq"); 
					String body_id=rs.getString("body_id")==null?"":rs.getString("body_id"); 
					if(seq==null|| "".equals(seq)){
						int obj_type = rs.getInt("object_type");
						if((obj_type==1||obj_type==3||obj_type==4)&& "-1".equals(body_id)){//解决团队负责人
							
						}else
							continue;
					} 
					rmap.put(plan_id, "1");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return rmap;
	}
}

