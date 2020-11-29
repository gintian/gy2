package com.hjsj.hrms.businessobject.performance.objectiveManage;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
/**
 * <p>Title:OrgPerformanceBo.java</p>
 * <p>Description>:OrgPerformanceBo.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-5-29 上午09:08:53</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class OrgPerformanceBo {
	private Connection conn;
	private HashMap map;
	private HashMap quarter;
    public OrgPerformanceBo(Connection conn)
    {
    	this.conn=conn;
    	quarter = new HashMap();
    }
    /**
     * 按层级驳回。找到自己作为考核主体的驳回记录
     * @param userView
     * @param planid
     * @param year
     * @return
     */
    public HashMap getSelfRejectObject(UserView userView,String planid,String year)
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
    			buf.append(" and plan_id in("+planid+")");
    		else{
        		buf.append(" and plan_id in(");
        		buf.append(" select plan_id from per_plan where method=2 and (object_type='1' or object_type='3' or object_type='4')");
        		buf.append(" and (status='5'  or status='4' or status='6' or status='7' or status='8')");
        		if(!"-1".equals(year))
          		{
    	    		buf.append(" and (theyear='"+year+"' or "+Sql_switcher.year("end_date")+"='"+year+"')");
    	    	}
        		buf.append(")");
    		}
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
    /**
     * 取得与登录用户关联的团队计划
     * @param a0100
     * @param year
     * @param quarter
     * @param month
     * @param statue
     * @return
     */
    public HashMap getOrgPlanList(String a0100,String year,String quarter,String month,String status,UserView userView,String sp_flagSQL,String planid,String spStatus)
    {
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");//设置日期格式
    	HashMap returnMap = new HashMap();
    	try
    	{
    		ArrayList list = new ArrayList();
    		ArrayList yearlist = new ArrayList();
    		yearlist.add(new CommonData("-1",ResourceFactory.getProperty("label.all")));
    		HashMap yearMap=new HashMap();
    		HashMap fieldMap=new HashMap();
    		StringBuffer buf = new StringBuffer();
    		buf.append(" select "+Sql_switcher.isnull("pp.a0000", "999999")+" as norder,pp.method,pp.plan_id,pp.theyear,pp.name,pp.thequarter,pp.themonth,pp.status,pp.b0110,pp.parameter_content,pp.cycle,");
    		buf.append(Sql_switcher.year("pp.start_date")+" as sy,"+Sql_switcher.month("pp.start_date")+" as sm,"+Sql_switcher.day("pp.start_date")+" as sd,");
    		buf.append(Sql_switcher.year("pp.end_date")+" as ey,"+Sql_switcher.month("pp.end_date")+" as em,"+Sql_switcher.day("pp.end_date")+" as ed");
    		buf.append(" ,po.object_id,po.sp_flag,po.trace_sp_flag,po.kh_relations,po.currappuser,"+Sql_switcher.isnull("po.seq","0")+" as seq,po.resn,po.status as scorestatus,po.isgrade,po.score,po.opt,po.pgflag,po.pmflag from per_plan pp,");
    		buf.append(" (");
    		buf.append(" select po.*,pm.seq,pm.reasons as resn,pm.status,pm.isgrade,pm.score,pm.opt,pm.pgflag,pm.pmflag from per_object po left join ");
    		buf.append("(select CASE "+Sql_switcher.isnull("pm.object_id", "'-100'")+" WHEN '-100' THEN pg.object_Id ELSE pm.object_id END AS  object_id,CASE "+Sql_switcher.isnull("pm.mainbody_id", "'-100'")+" WHEN '-100' THEN pg.mainbody_id ELSE pm.mainbody_id END AS  mainbody_id, CASE "+Sql_switcher.isnull("pm.plan_id", "-100")+" ");
    		buf.append(	" WHEN -100 THEN pg.plan_id ELSE pm.plan_id END AS  plan_id,pm.reasons,pm.seq ,"+Sql_switcher.isnull("pm.status", "-100")+" AS status ,pm.score,CASE "+Sql_switcher.isnull("pm.plan_id", "-100")+" WHEN -100 THEN 1 ELSE ppb.Isgrade END AS Isgrade,ppb.opt,CASE "+Sql_switcher.isnull("pg.plan_id", "-100")+" WHEN -100 THEN 0 ELSE 1 END AS pgflag ,");
    		//****************** 匹配多评分人插入sql************************* 
    		//作用 全连接 per_grade_members表，使得此sql可以同时查询出 当前用户做为考核主体和评价人的所有计划。
    		buf.append(" CASE "+Sql_switcher.isnull("pm.plan_id", "-100")+" WHEN -100 THEN 0 ELSE 1  END AS pmflag" );
    		buf.append(" from per_mainbody pm left join per_plan_body ppb on pm.plan_id=ppb.plan_id and pm.body_id=ppb.body_id ");
    		buf.append(" FULL JOIN ( SELECT  p04.b0110 AS object_Id,p04.plan_id,pg.A0100 AS mainbody_id FROM per_grade_members pg INNER JOIN P04 ON pg.P0400=P04.P0400 INNER JOIN per_plan ON per_plan.plan_id=p04.plan_id ");  
    		buf.append(" INNER JOIN per_object pobject ON p04.B0110=pobject.object_id AND pobject.sp_flag='02' OR pobject.sp_flag='07' ");
    		buf.append(" WHERE pg.A0100='"+userView.getA0100()+"' AND p04.A0100 IS NULL  and (per_plan.object_type='1' or per_plan.object_type='3' or per_plan.object_type='4') and per_plan.method='2' ");
    		buf.append(" AND (per_plan.status='5'  or per_plan.status='8')  GROUP BY p04.b0110,p04.plan_id,pg.A0100 ) pg "); 
    		buf.append(" ON pg.object_Id=pm.object_id AND pg.plan_id=pm.plan_id AND pg.mainbody_id=pm.mainbody_id ");
    		//************************************************************
    		
    		
    		buf.append(") pm on po.plan_id=pm.plan_id and po.object_id=pm.object_id where pm.mainbody_id='"+userView.getA0100()+"'");
    		
    		buf.append(" ) po where pp.plan_id=po.plan_id ");
    		buf.append("  and (pp.object_type='1' or pp.object_type='3' or pp.object_type='4') ");//and UPPER(pm.mainbody_id)='"+a0100.toUpperCase()+"'	
    		buf.append(" and pp.method='2'  and ");//and pp.cycle<>'7'要求兼容不定期考核
    		buf.append(" (pp.status='5'  or pp.status='4' or pp.status='6' or pp.status='7' or pp.status='8')");
    		if(sp_flagSQL!=null&&!"".equals(sp_flagSQL))
    			buf.append(" and ("+sp_flagSQL+")");
    		if(!"-1".equals(planid))
    			buf.append(" and pp.plan_id="+planid);
    		if(!"-1".equals(year))
    		{
    			buf.append(" and (pp.theyear='"+year+"' or "+Sql_switcher.year("pp.end_date")+"='"+year+"')");
    		}
    		if("-1".equals(spStatus))
			{
				
			} else if("-2".equals(spStatus))
			{
				buf.append(" and (po.sp_flag<>'06' or po.sp_flag is null)");
			}
			else
			{
				
				if("01".equals(spStatus))
	    			buf.append("and (po.sp_flag='"+spStatus+"' or po.sp_flag is null)");
				else
					buf.append(" and po.sp_flag='"+spStatus+"'");
			}
    		buf.append(" order by norder asc,pp.plan_id desc,po.A0000 asc");//,po.b0110,po.e0122
    		ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rs = null;
    		HashMap eMap=null;
    		AnalysePlanParameterBo appbo=new AnalysePlanParameterBo(this.conn);
    		Hashtable ht_table=appbo.analyseParameterXml();
    		String AllowLeaderTrace="false";
    		rs = dao.search(buf.toString());
    		/**---------------------*/
    		String planidStr = "''";
    		while(rs.next()) {
    			String _planid = rs.getString("plan_id");
    			if(planidStr.indexOf(","+_planid+",") > -1) {
    				continue ;
    			}
    			planidStr += (","+_planid);
    		}
    		rs.beforeFirst();
    		/**---------------------*/
    		HashMap orgLeaderMap = this.getOrgLeader(planidStr);
    		HashMap p04Map = this.getp04(planidStr, dao);
    		HashMap mainbodyBeanMap = getMainbodyBean(planidStr, dao);
    		
    		SetUnderlingObjectiveBo suob=new SetUnderlingObjectiveBo(conn,userView);
    		boolean priv=false;
			FieldItem item  = DataDictionary.getFieldItem("score_org");
			if(item!=null&& "1".equals(item.getState()))
			{
				priv=true;
			}
			
			
			RenderRelationBo bo = new RenderRelationBo(conn,userView);
			HashMap selfMap = this.getSelfRejectObject(userView, planidStr, year);
			HashMap eqMap = new HashMap();
			HashMap objMap = new HashMap();
			HashMap noScoreMap = new HashMap();
			ArrayList searchedPlanIdList = new ArrayList();// 已经查询过的计划号集合
    		while(rs.next())
    		{
    		    String cycle=rs.getString("cycle"); 
    		    int plan_id = rs.getInt("plan_id");
    		    String object_id=rs.getString("object_id");
    		    String market=rs.getString("kh_relations");
    		    
    		    ObjectCardBo objectCardBo=new ObjectCardBo();
    			objectCardBo.setPlan_id(String.valueOf(plan_id));
    			objectCardBo.setConn(conn);
    			int pgflag=rs.getInt("pgflag");//是否做为评分人标识 0 不是 1是
    			int pmflag=rs.getInt("pmflag");//是否是考核主体标识 0 不是 1是
    			boolean ispg=false;//是否由于是评分人被加入的
    			
    			//如果没有启动多评价人 并且是由于做为评分人被加入循环的
    			if(!objectCardBo.isOpenGrade_Members()&&pmflag==0)
    				continue;
    		    
    		    
    		    
    		    boolean isScore=suob.isMarket(object_id, plan_id+"");
    		    if(market==null)
					market="0";
    		    boolean isHaveLeader=false;
    		    String reasons=Sql_switcher.readMemo(rs,"resn");
    		    LazyDynaBean leaderBean=null;
    		    HashMap infomap=null;
    		    String leaderid="";
    		    String theyear=rs.getString("theyear");
    		    String spf=rs.getString("sp_flag"); 
    		    if(spf==null|| "".equals(spf))
    		    	spf="01";
    		    if("3".equals(cycle))//按月度
    		    {
    		    	if((!"-1".equals(year)&&rs.getString("theyear").equals(year))|| "-1".equals(year))
    		    	{
    		    		int qu=1;
    		    		if(!"-1".equals(quarter))
    		    		{
    		    			qu=this.getFirstMonthInQuarter(quarter);
    		    		}
    		    		String a_month=rs.getString("themonth");
    		    		int int_month=Integer.parseInt(a_month);
    		    		if("-1".equals(quarter)||(int_month==qu||int_month==(qu+1)||int_month==(qu+2)))
    		    		{
    		    			if("-1".equals(month)||Integer.parseInt(month)==int_month)
    		    			{
    		    				if("-1".equals(status)||status.equalsIgnoreCase(rs.getString("status"))||("-2".equals(status)&&!"7".equals(rs.getString("status"))))
		    	    			{
    		    					
		    	    			}
    		    				else{
    		    					continue;
    		    				}
    		    			}else{
    		    				continue;
    		    			}
    		    		}
    		    		else{
    		    			continue;
    		    		}
    		    	}
    		    	else{
    		    		continue;
    		    	}
        		}else if("0".equals(cycle))//年度
        		{
        			if("-1".equals(year)||rs.getString("theyear").equals(year))
        			{
        				if("-1".equals(status)||status.equalsIgnoreCase(rs.getString("status"))||("-2".equals(status)&&!"7".equals(rs.getString("status"))))
    	    			{

    	    			}else{
    	    				continue;
    	    			}
        			}else{
        				continue;
        			}
        		}
        		else if("1".equals(cycle))//半年度
        		{
        			if("-1".equals(year)||year.equals(rs.getString("theyear")))
        			{
        				String a_quarter = rs.getString("thequarter");
        				if("1".equals(a_quarter))//上半年，算1，2季度,算1，2，3，4，5，6月份
        				{
        					if("-1".equals(quarter)|| "1".equals(quarter)|| "01".equals(quarter)|| "2".equals(quarter)|| "02".equals(quarter))
        					{
        						if("-1".equals(month)||(Integer.parseInt(month)<=6))
        						{
        					    	if("-1".equals(status)||status.equalsIgnoreCase(rs.getString("status"))||("-2".equals(status)&&!"7".equals(rs.getString("status"))))
		    	    		    	{

		    	    		    	}else{
		    	    		    		continue;
		    	    		    	}
        						}else{
        							continue;
        						}
        					}else{
        						continue;
        					}
        				}
        				else//下半年算3，4季度,算7,8,9,10,11,12月份
        				{
        					if("-1".equals(quarter)|| "03".equals(quarter)|| "3".equals(quarter)|| "4".equals(quarter)|| "04".equals(quarter))
        					{
        						if("-1".equals(month)||(Integer.parseInt(month)>6))
        						{
        					    	if("-1".equals(status)||status.equalsIgnoreCase(rs.getString("status"))||("-2".equals(status)&&!"7".equals(rs.getString("status"))))
		    	    		    	{
        				
		    	    	    		}else{
		    	    		    		continue;
		    	    	    		}
        						}else{
        							continue;
        						}
        					}else{
        						continue;
        					}
        				}
        			}else{
        				continue;
        			}
        		}
        		else if("7".equals(cycle))//不定期
        		{
        			
        			String sy=rs.getString("sy");
        			String sm=rs.getString("sm");
        			String ey=rs.getString("ey");
        			String em=rs.getString("em");
        			theyear=sy;
        			int int_year = 0;
        			int int_sy=0;
        			int int_sm=0;
        			int int_ey=0;
        			int int_em=0;
        			
        			int_year=Integer.parseInt(year);
        			int_sy=Integer.parseInt(sy);
        			int_sm=Integer.parseInt(sm);
        			int_ey=Integer.parseInt(ey);
        			int_em=Integer.parseInt(em);
        			
        			if("-1".equals(year)||(!"-1".equals(year)&&int_sy<=int_year&&int_ey>=int_year))
        			{
        				int int_month=0;
        				if(!"-1".equals(month))
        				{
        					int_month=Integer.parseInt(month);
        				}
        				int qu=1;
    		    		if(!"-1".equals(quarter))
    		    		{
    		    			qu=this.getFirstMonthInQuarter(quarter);
    		    		}
        				if(("-1".equals(month)||(!"-1".equals(month)&&(int_sm<=int_month&&int_em>=int_month))))
        				{
        					if("-1".equals(quarter)||(int_sm==qu||int_sm==(qu+1)||int_sm==(qu+2)||int_em==qu||int_em==(qu+1)||int_em==(qu+2)))
        					{
        			    		if("-1".equals(status)||status.equalsIgnoreCase(rs.getString("status"))||("-2".equals(status)&&!"7".equals(rs.getString("status"))))
	    	    	    		{
        						
	    	    		    	}else{
	    	    		    		continue;
	    	    	      		}
        					}else{
        						continue;
        					}
        				}else{
        					continue;
        				}
        			
        			}else {
        				continue;
        			}
        		}
        		else if("2".equals(cycle))//季度
        		{
        			if("-1".equals(year)||year.equals(rs.getString("theyear")))
        			{
        				int qu=1;
    		    		if(!"-1".equals(month))
    		    		{
    		    			qu=this.getFirstMonthInQuarter(rs.getString("thequarter"));
    		    		}
    		    		int int_month=Integer.parseInt(month);
    		    		
        				if("-1".equals(quarter)||quarter.equals(rs.getString("thequarter"))||quarter.equals("0"+rs.getString("thequarter")))
        				{
        					if(int_month==-1||(int_month==qu||int_month==(qu+1)||int_month==(qu+2)))
        					{
        			    		if("-1".equals(status)||status.equalsIgnoreCase(rs.getString("status"))||("-2".equals(status)&&!"7".equals(rs.getString("status"))))
	    	    	    		{
      
	    	    		    	}else{
	    	    	 	    		continue;
	    	    			  }
        					}
        					else
        					{
        						continue;
        					}
        				}else{
        					continue;
        				}
        			}else{
        				continue;
        			}
        		}
					
			   	/*考核对象状态：01	起草 02	已报批 03	已批04	已发布05	执行中06	结束	07	驳回	08	报审09	暂停*/	
  	           /*考核主体对考核对象的打分状态：0:未打分 1:正在编辑 2:已提交 3:不打分*/	
//计划状态(0,           1,    2    ,3,    4,                    5,                     6,            7)=
//(起草[可以编辑和删除], 报批,  已批,已发布, 启动[不能编辑和删除],//暂停[不能编辑和删除]，评估[不能编辑,可以删除 ]，结束) 
  	         	/*判断规则：要根据考核计划，对象，主体来判断，
  	         	考核对象状态为非已批（就起草）为制定，
  	         	打分：当考核对象状态为已批，考核计划为非结束，考核主体状态对对象的打分状态为非提交，可以打分，
  	         	否则为查看*/
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
    			String NoApproveTargetCanScore="";
    			if(params.get("NoApproveTargetCanScore")!=null)
    				NoApproveTargetCanScore=(String)params.get("NoApproveTargetCanScore");
    			if(params.get("AllowLeaderTrace")!=null)
					AllowLeaderTrace=(String)params.get("AllowLeaderTrace");
    			if(ht_table!=null&&ht_table.get("AllowLeaderTrace")!=null&& "false".equalsIgnoreCase(AllowLeaderTrace))
    				AllowLeaderTrace=(String)ht_table.get("AllowLeaderTrace");
    			/**按序号审批（如果考核主体没序号的，不参与审批）*/
    			String SpByBodySeq="False";
    			if(params.get("SpByBodySeq")!=null)
    				SpByBodySeq=(String)params.get("SpByBodySeq");
    			if("true".equalsIgnoreCase(SpByBodySeq))//按序号审批
    			{
    				if(objMap.get(plan_id+"")==null)
    				{
    					HashMap mm=suob.getObjectBySeq(plan_id+"",1);
    					objMap.put(plan_id+"",mm);
    				}
    			}
    			/**调整即新建*/
    			String taskAdjustNeedNew=(String)ht_table.get("taskAdjustNeedNew");
    			if(taskAdjustNeedNew==null)
    				taskAdjustNeedNew="false";
    			/**已批后是否可调整*/
    			String TargetAllowAdjustAfterApprove=(String) params.get("TargetAllowAdjustAfterApprove");
			    if(TargetAllowAdjustAfterApprove==null|| "".equals(TargetAllowAdjustAfterApprove))
			    	TargetAllowAdjustAfterApprove="true";
			    /**支持几级审批*/
    		    String targetMakeSeries=(String)params.get("targetMakeSeries");
    		 // 目标管理审批模式:0-考核关系 1-汇报关系. 默认为 0
    		    String targetAppMode=(String)params.get("targetAppMode"); 
    		    String allowLeadAdjustCard=(String)params.get("allowLeadAdjustCard");
    			if(allowLeadAdjustCard==null)
    				allowLeadAdjustCard="false";
    			String GradeByBodySeq="false";//按考核主体顺序号控制评分流程(True, False默认为False)
  				if(params.get("GradeByBodySeq")!=null)
  					GradeByBodySeq=(String)params.get("GradeByBodySeq");
  				boolean isCanScore=true;
  				String seq=rs.getString("seq");
  				String pstatus = rs.getString("status");
  				if("true".equalsIgnoreCase(GradeByBodySeq))//按顺序来评分
  				{
  					String isGrade=rs.getString("isgrade")==null?"0":rs.getString("isgrade");
  					if("1".equals(isGrade)&&!"8".equalsIgnoreCase(pstatus)){
  							continue;
  					}
  					HashMap amap=null;
  					if(eqMap.get(plan_id+"")!=null)
  					{
  						amap=(HashMap)eqMap.get(plan_id+"");
  					}else{
  						ObjectiveEvaluateBo oeb=new ObjectiveEvaluateBo(this.conn);
  						amap=oeb.getObjectEvalInfo(plan_id+"",null);
  						eqMap.put(plan_id+"", amap);
  					}
  					if("0".equals(seq))
  						isCanScore=true;
  					else if(amap!=null&&amap.get(object_id)!=null)
  					{
  						String currseq=(String)amap.get(object_id);
  						if(seq.equals(currseq))
  							isCanScore=true;
  						else
  							isCanScore=false;
  					}
  				}
  				
    			int type=Integer.parseInt((targetAppMode==null|| "".equals(targetAppMode))?"0":targetAppMode);
    		    /**如果存在部门负责人，*/
    		    if(orgLeaderMap.get(object_id+plan_id)!=null)
    		    {
    		    	isHaveLeader=true;
    		    	leaderBean=(LazyDynaBean)orgLeaderMap.get(object_id+plan_id);
    		    	/**mainbody_id+object_id----->>bean(level)*/
    				ArrayList dbnameList = new ArrayList();
    				dbnameList.add("USR");
    				String posID=(String)leaderBean.get("e01a1");
    				leaderid=(String)leaderBean.get("a0100");
    				/***/
    				//infomap =bo.getPer_MainBodyInfo(dbnameList, posID, 3, plan_id+"");
    				infomap = bo.getPer_MainBodyInfoForOrg(dbnameList, posID, 3, plan_id+"", object_id,leaderid,targetAppMode,targetMakeSeries);
    		    }
    		    /**不存在部门负责人，直接从考核主体表取考核主体*/
    		    else
    		    {
    		    }
    		    
				String level="";
				/**考核主体是否可以参与审批*/
				String isSP="0";
				
				if("true".equalsIgnoreCase(SpByBodySeq)&& "8".equalsIgnoreCase(pstatus))
				{
					HashMap mm=(HashMap)objMap.get(plan_id+"");
					boolean isObj=false;
					if(("01".equals(spf)|| "07".equals(spf))&&userView.getA0100().equals(leaderid))
						isObj=true;
					if(mm.get(object_id)==null&&!isObj){
						if(pgflag==1)
							ispg=true;
						else
							continue;
					}
						
					
					LazyDynaBean bean = (LazyDynaBean)mm.get(object_id);
					if(bean==null){
						level="5";
							isSP="1";
					}else{
						level=(String)bean.get("level");
						isSP=(String)bean.get("isSP");
					}
					if(userView.getA0100().equals(leaderid))
						isSP="1";
				}else{
					if(isHaveLeader)
					{
						/**当为非标准考核对象，从考核主体表查考核主体，，当是标准考核对象时，从考核主体表加上部门负责人的考核关系中查*/
						if("1".equals(market)&&!mainbodyBeanMap.containsKey(plan_id+userView.getA0100()+object_id)){
							if(pgflag==1)
								ispg=true;
							else
								continue;
						}
						if("0".equals(market))
						{
							
							if(!infomap.containsKey(userView.getA0100()+object_id)&&!infomap.containsKey(userView.getA0100()+leaderid))
								if(pgflag==1)
									ispg=true;
								else
									continue;
						}
						if("1".equals(market))
						{
							LazyDynaBean bean =(LazyDynaBean)mainbodyBeanMap.get(plan_id+userView.getA0100()+object_id);
							if(bean!=null)
							{
								level=(String)bean.get("level");
								isSP=(String)bean.get("isSP");
								if(!suob.isCanSP(plan_id+"", object_id, userView.getA0100()))
								{
									isSP="0";
								}
							}
						}
						else
						{
						    LazyDynaBean bean=(LazyDynaBean)infomap.get(userView.getA0100()+object_id);
						    if(bean==null)
						    	bean=(LazyDynaBean)infomap.get(userView.getA0100()+leaderid);
						    if(bean!=null)
						    {
							    level=(String)bean.get("level");
							    isSP=(String)bean.get("isSP");
						    }
						}
						if(userView.getA0100().equals(leaderid))
							isSP="1";
					}
					else
					{
						/**没有团队负责人的对象，只一下3个状态可见*/
						if(!"4".equals(pstatus)&&!"6".equals(pstatus)&&!"7".equals(pstatus))
						{
							continue;
						}
						if(!mainbodyBeanMap.containsKey(plan_id+userView.getA0100()+object_id))
						{
							continue;
						}
						LazyDynaBean bean=(LazyDynaBean)mainbodyBeanMap.get(plan_id+userView.getA0100()+object_id);
						if(bean!=null)
						{
							level=(String)bean.get("level");
							isSP=(String)bean.get("isSP");
							if(!suob.isCanSP(plan_id+"", object_id, userView.getA0100()))
							{
								isSP="0";
							}
						}
					}
				}
				
				if(priv&&TargetDefineItem!=null&&TargetDefineItem.trim().length()>0&&TargetDefineItem.toUpperCase().indexOf("SCORE_ORG")!=-1)
         		{
         			if("0".equals(isSP)&&((Integer.parseInt(pstatus)!=4)&&!"6".equals(pstatus)&&!"7".equals(pstatus)))
         				continue;
         			/**启用考核机构时，不再打分状态，不是考核对象的上级，上上级，三级，四级的考核主体，不能看见该考核对象*/
         			if(((!"1".equals(level)&&!"5".equals(level)&&!"0".equals(level)&&!"-1".equals(level)&&!"-2".equals(level))&&!"4".equals(pstatus)&&!"6".equals(pstatus)&&!"7".equals(pstatus)&& "false".equalsIgnoreCase(SpByBodySeq)))
         				if(pgflag==1)
							ispg=true;
						else
							continue;
         				
         			if(bo.isByOrg2(a0100, object_id, leaderid, Integer.parseInt(pstatus)))
         			{
         				HashMap amap = null;
         				if(fieldMap.get(plan_id+"")!=null)
         				{
         					amap=(HashMap)fieldMap.get(plan_id+"");
         				}
         				else
         				{
         					amap=bo.getKhOrgField(plan_id+"", userView);
         					fieldMap.put(plan_id+"", amap);
         				}
         				if(amap.get(plan_id+object_id)==null)
         					if(pgflag==1)
    							ispg=true;
    						else
    							continue;
         			}
         		}
				if(ispg){
					level="2";
					isSP="0";
				}

				boolean isSubmit=this.isSubmit(object_id, plan_id, dao,userView);
				int tzCount= 0;
				if(p04Map.get(plan_id+"_"+object_id) != null) {
					tzCount = (Integer)p04Map.get(plan_id+"_"+object_id);
				}
				String spFlagDesc=MyObjectiveBo.getSpflagDesc(spf);//AdminCode.getCodeName("23",spf);
				if("07".equals(spf))//spFlagDesc="退回/意见";
					spFlagDesc+="/意见";
        	    LazyDynaBean bean = new LazyDynaBean();
         		String gradedesc=this.getDegreeDesc(plan_id, object_id);
         		bean.set("gradedesc",gradedesc);
    	    	bean.set("planid",rs.getString("plan_id"));
	            bean.set("name", rs.getString("name"));
 	         	String currsp="0";
 	         	String opt="0";
				String flag="6";
				String bs="0";
				/**opt=0,不可操作，=1可操作，flag=6查看，=7布置，=8打分*/
				String currappuser=rs.getString("currappuser");
				String isReject="0";
				if(userView.getA0100().equalsIgnoreCase(currappuser)&&selfMap.get(object_id+userView.getA0100()+plan_id)!=null)//object_id+mainbody_id+plan_id
				{
					spFlagDesc=MyObjectiveBo.getSpflagDesc("07");
					spFlagDesc+="/意见";
					isReject="1";
				}
				String scoreStatusflag=rs.getString("scorestatus");
				String isScoreUntread="0";//评分被驳回
				if("1".equals(scoreStatusflag)&&reasons.length()>0)
				{
					isScoreUntread="1";
				}
				if(isHaveLeader)
				{
					/**暂停和分发状态*/
					if("8".equals(pstatus))
					{
						if(isScore)
						{
							if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
							{
								spFlagDesc=spFlagDesc+"(调整后)";
							}
						}
						else
						{
				     		if("true".equalsIgnoreCase(allowLeadAdjustCard))
				    		{
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
						    	else if(!isSubmit&& "03".equals(spf)&& "true".equalsIgnoreCase(TargetAllowAdjustAfterApprove)&&userView.getA0100().equals(leaderid))
					    		{
					    			if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
					    			{
					    				spFlagDesc=spFlagDesc+"(调整后)";
					    			}
					    			opt="0";
					     			flag="6";
		    	        		    bs="1";
				      			}
				     			else if("07".equals(spf)&&userView.getA0100().equals(leaderid))
					    		{
					    			if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
					    			{
					    				spFlagDesc=spFlagDesc+"(调整后)";
					    			}
					    			opt="1";
					    			flag="7";
					    			currsp="1";
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
					     		if("01".equals(spf)&& "1".equals(isSP))
				    			{
				    				if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
					     			{
					    				spFlagDesc=spFlagDesc+"(调整后)";
				    				}
				    				opt="1";
				    				flag="7";
					    			if(userView.getA0100().equalsIgnoreCase(leaderid))
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
					    		else if(!isSubmit&& "03".equals(spf)&& "true".equalsIgnoreCase(TargetAllowAdjustAfterApprove)&&userView.getA0100().equals(leaderid))
					    		{
					     			if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
				    				{
				    					spFlagDesc=spFlagDesc+"(调整后)";
					    			}
					    			opt="0";
						    		flag="6";
		    		         	    bs="1";
				    			}
					    		else if("07".equals(spf)&&userView.getA0100().equals(leaderid))
					    		{
					    			if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
					    			{
					    				spFlagDesc=spFlagDesc+"(调整后)";
					    			}
					    			opt="1";
					    			flag="7";
					    			currsp="1";
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
				    	}
					}
					else if("5".equals(pstatus))
					{
						opt="0";
    			    	flag="6";
					}
					else
					{
						if(eMap==null || !searchedPlanIdList.contains(plan_id)) {
							// 已查询过的计划不再查询 chent add 20170115
							searchedPlanIdList.add(plan_id);
							eMap=this.getPerMainBodyData(userView,plan_id);
						}
						LazyDynaBean abean = (LazyDynaBean)eMap.get(plan_id+object_id+userView.getA0100());
						if(abean!=null)
						{
							// 判断主体类别是否参与评分 JinChunhai 2013.01.08
			  				String body_id = (String)abean.get("body_id"); 
			  				boolean isScoreOrNo = true;
			  				HashMap noBodymap = null;
							if(noScoreMap.get(plan_id+"")!=null)
							{
								noBodymap = (HashMap)noScoreMap.get(plan_id+"");
							}else{
								noBodymap = this.getObjectIsScoreOrNo(plan_id+"");
								noScoreMap.put(plan_id+"", noBodymap);
							}
			  				if(noBodymap!=null && noBodymap.size()>0)
			  				{
			  					String noScore = (String)noBodymap.get(body_id);
			  					if(noScore!=null && "1".equalsIgnoreCase(noScore))
			  						isScoreOrNo = false;
			  				}							
														
				    		String scoreStatus=(String)abean.get("status");
					    	if(("4".equals(pstatus)|| "6".equals(pstatus))&&("03".equals(spf)|| "true".equalsIgnoreCase(NoApproveTargetCanScore))&&!"2".equals(scoreStatus)&&isCanScore && isScoreOrNo)
     	            	    {
					    	   opt="2";
	    		    		   flag="8";
     		                }
					    	else
					    	{
					    		opt="0";
		    				    flag="6";
					    	}
						}
						else
						{
							
						    opt="0";
	    				    flag="6";
						}
						if(isScore&&Integer.parseInt(pstatus)<4)
						{
							opt="0";
		    				flag="6";
						}
					}
		    	}
		    	else
		    	{
			    	boolean sub = this.isSubmit(object_id, plan_id, dao, userView);
			    	/**	没团队负责人的对象，只要是启动和评估状态，并且没提交分呢，就是打分，其余全部查看*/
			    	if(("4".equals(pstatus)|| "6".equals(pstatus))&&!sub&&isCanScore)
			    	{
		     			opt="2";
		    			flag="8";
		    		}
		     		else
		    		{
		    			opt="0";
		    			flag="6";
		    		}
		    	}
				bean.set("bs", bs);
				bean.set("opt",opt);
				bean.set("flag", flag);
				if("8".equals(pstatus)&&("0".equals(currsp)&&pgflag==1&& "02".equals(spf)))//如果处于分发状态，且具有查看权限  且是评价人 那么显示评价按钮
					currsp="2";
				bean.set("currsp", currsp);
				bean.set("level", level);
				bean.set("isScoreUntread",isScoreUntread);
				String kd = "2";
				if( params.get("KeepDecimal")!=null)
				{
					kd=(String) params.get("KeepDecimal");
				}
				int KeepDecimal = Integer.parseInt(kd); // 保留小数位	
				String score= rs.getString("score")==null?"":rs.getString("score");
	    		score=PubFunc.round(score,KeepDecimal);
	    		bean.set("score", score);
	    		
	    		// 打分确认标识 add by 刘蒙
	    		int pbOpt = rs.getInt("opt");
	    		bean.set("pbOpt", new Integer(pbOpt));
	    		
        	    String b0110 = AdminCode.getCodeName("UN",rs.getString("object_id"));
             	if(b0110==null|| "".equals(b0110))
            		b0110=AdminCode.getCodeName("UM",rs.getString("object_id"));
    	    	bean.set("b0110",b0110);
    	    	bean.set("a0100",rs.getString("object_id"));
    	    	bean.set("mdplanid",PubFunc.encryption((rs.getString("plan_id"))));
    	    	bean.set("mda0100",PubFunc.encryption(rs.getString("object_id")));
    	    	if("3".equals(cycle))
    	    	{
    	    		bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year")+" "+rs.getString("themonth")+ResourceFactory.getProperty("datestyle.month"));
    	    	}
    	    	else if("0".equals(cycle))
    	    	{
    	    		bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year"));
    	    	}else if("1".equals(cycle))
    	    	{
    	    		String a_quarter = rs.getString("thequarter");
	    			if("1".equals(a_quarter))
	    			{
	    				bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year")+" "+ResourceFactory.getProperty("report.pigeonhole.uphalfyear"));
	    			}
	    			else
	    			{
	    				bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year")+" "+ResourceFactory.getProperty("report.pigeonhole.downhalfyear"));
	    			}
    	    	}else if("2".equals(cycle))
    	    	{
    	    		bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year")+" "+AdminCode.getCodeName("12",rs.getString("thequarter")));
    	    	}
    	    	else if("7".equals(cycle))
    	    	{
    	    		bean.set("evaluate",rs.getString("sy")+ResourceFactory.getProperty("datestyle.year")+""+rs.getString("sm")+ResourceFactory.getProperty("datestyle.month")+rs.getString("sd")+ResourceFactory.getProperty("datestyle.day")+"  至  "+rs.getString("ey")+ResourceFactory.getProperty("datestyle.year")+""+rs.getString("em")+ResourceFactory.getProperty("datestyle.month")+rs.getString("ed")+ResourceFactory.getProperty("datestyle.day"));
    	    	}
    	    	else
    	    	{
         	    	bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year")+" "+rs.getString("themonth")+ResourceFactory.getProperty("datestyle.month"));
    	    	}
         		bean.set("sp_flag",spFlagDesc);
         		bean.set("spf",spf);
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
         		bean.set("objecttype","1");
         		bean.set("records", PubFunc.encrypt(object_id)+"-"+PubFunc.encrypt(String.valueOf(plan_id)));
         		String xx=PubFunc.encrypt(rs.getString("plan_id"))+"`"+PubFunc.encrypt(object_id)+"`"+spf+"`"+(rs.getString("currappuser")==null?"":rs.getString("currappuser"))+"`"+level+"`"+pstatus;
         		bean.set("bacthdata", xx);
         		bean.set("isReject", isReject);
         		if(yearMap.get(theyear)==null)
				{
					yearlist.add(new CommonData(theyear,theyear));
					yearMap.put(theyear, "1");
				}
         		list.add(bean);
    		}
    		returnMap.put("list", list);
    		returnMap.put("yearlist", yearlist);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return returnMap;
    }
    
    // 是否参与评分
	public HashMap getObjectIsScoreOrNo(String plan_id)
	{
		RowSet rs = null;	
		HashMap map = new HashMap();
		try
		{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select body_id from per_plan_body where plan_id="+plan_id+" and isgrade=1");			
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString());
			while(rs.next())
			{
				String body_id = rs.getString("body_id")==null?"":rs.getString("body_id");
				map.put(body_id, "1");
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
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return map;
	}
    
    public HashMap getOrgPlanWelcomeList(String a0100,String year,String quarter,String month,String status,UserView userView)
    {
    	HashMap returnMap = new HashMap();
    	try
    	{
    		ArrayList list = new ArrayList();
    		ArrayList list2=new ArrayList();
    		ArrayList list3=new ArrayList();
    		ArrayList list4=new ArrayList();
    		HashMap map1=new HashMap();
    		HashMap map2=new HashMap();
    		StringBuffer buf = new StringBuffer();
    		StringBuffer scorebuf = new StringBuffer("");
    		buf.append(" select "+Sql_switcher.isnull("pp.a0000", "999999")+" as norder,pp.method,pp.plan_id,pp.theyear,pp.name,pp.thequarter,pp.themonth,pp.status,pp.b0110,pp.parameter_content,pp.cycle,");
    		buf.append(Sql_switcher.year("pp.start_date")+" as sy,"+Sql_switcher.month("pp.start_date")+" as sm,"+Sql_switcher.day("pp.start_date")+" as sd,");
    		buf.append(Sql_switcher.year("pp.end_date")+" as ey,"+Sql_switcher.month("pp.end_date")+" as em,"+Sql_switcher.day("pp.end_date")+" as ed");
    		buf.append(" ,po.object_id,po.sp_flag,po.kh_relations,po.currappuser from per_plan pp,");
    		buf.append(" per_object po where pp.plan_id=po.plan_id ");
    		buf.append("  and (pp.object_type='1' or pp.object_type='3' or pp.object_type='4') ");//and UPPER(pm.mainbody_id)='"+a0100.toUpperCase()+"'	
    		buf.append(" and pp.method='2' and pp.cycle<>'7' and ");
    		buf.append(" (pp.status='5'  or pp.status='4' or pp.status='6' or pp.status='8'");
    		if(!"zglt".equals(SystemConfig.getPropertyValue("clientName")))
    		    buf.append(" or pp.status='7' ");
    		buf.append(" )order by norder asc,pp.plan_id desc,po.sp_flag ");
    		ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rs = null;
    		HashMap orgLeaderMap = this.getOrgLeader("");
    		HashMap eMap=null;
    		rs = dao.search(buf.toString());
    		while(rs.next())
    		{
    		    String cycle=rs.getString("cycle"); 
    		    int plan_id = rs.getInt("plan_id");
    		    String object_id=rs.getString("object_id");
    		    String market=rs.getString("kh_relations");
    		    if(market==null)
					market="0";
    		    boolean isHaveLeader=false;
    		    LazyDynaBean leaderBean=null;
    		    HashMap mainbodyMap=null;
    		    HashMap infomap=null;
    		    String leaderid="";
    		    String theyear=rs.getString("theyear");
    		    String spf=rs.getString("sp_flag"); 
    		    if(spf==null|| "".equals(spf))
    		    	spf="01";
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
    			/**调整即新建*/
    			String taskAdjustNeedNew=(String)params.get("taskAdjustNeedNew");
    			if(taskAdjustNeedNew==null)
    				taskAdjustNeedNew="false";
    			/**已批后是否可调整*/
    			String TargetAllowAdjustAfterApprove=(String) params.get("TargetAllowAdjustAfterApprove");
			    if(TargetAllowAdjustAfterApprove==null|| "".equals(TargetAllowAdjustAfterApprove))
			    	TargetAllowAdjustAfterApprove="true";
			    /**支持几级审批*/
			    String NoApproveTargetCanScore="";
				if(params.get("NoApproveTargetCanScore")!=null)
					NoApproveTargetCanScore=(String)params.get("NoApproveTargetCanScore");
    		    String targetMakeSeries=(String)params.get("targetMakeSeries");
    		 // 目标管理审批模式:0-考核关系 1-汇报关系. 默认为 0
    		    String targetAppMode=(String)params.get("targetAppMode"); 
    		    String allowLeadAdjustCard=(String)params.get("allowLeadAdjustCard");
    			if(allowLeadAdjustCard==null)
    				allowLeadAdjustCard="false";
    			int type=Integer.parseInt((targetAppMode==null|| "".equals(targetAppMode))?"0":targetAppMode);
                boolean flag01=false;
    		    /**如果存在部门负责人，*/
    		    if(orgLeaderMap.get(object_id+plan_id)!=null)
    		    {
    		    	isHaveLeader=true;
    		    	leaderBean=(LazyDynaBean)orgLeaderMap.get(object_id+plan_id);
    		    	/**mainbody_id+object_id----->>bean(level)*/
    		        mainbodyMap = getMainbodyBean(plan_id+"",market,allowLeadAdjustCard);
    				RenderRelationBo bo = new RenderRelationBo(conn,userView);
    				ArrayList dbnameList = new ArrayList();
    				dbnameList.add("USR");
    				String posID=(String)leaderBean.get("e01a1");
    				leaderid=(String)leaderBean.get("a0100");
    				if(leaderid.equals(userView.getA0100()))
    					flag01=true;
    				infomap =bo.getPer_MainBodyInfoForOrg(dbnameList, posID, 3, plan_id+"", object_id,leaderid,targetAppMode,targetMakeSeries);
    		    }
    		    /**不存在部门负责人，直接从考核主体表取考核主体*/
    		    else
    		    {
    		    	 mainbodyMap = getMainbodyBean(plan_id+"",market,allowLeadAdjustCard);
    		    	 if(mainbodyMap.containsKey(userView.getA0100()+object_id))
    		    		 flag01=true;
    		    }
				
    		    if("3".equals(cycle))//按月度
    		    {
    		    	if((!"-1".equals(year)&&rs.getString("theyear").equals(year))|| "-1".equals(year))
    		    	{
    		    		int qu=0;
    		    		if(!"-1".equals(quarter))
    		    		{
    		    			qu=this.getFirstMonthInQuarter(quarter);
    		    		}
    		    		String a_month=rs.getString("themonth");
    		    		int int_month=Integer.parseInt(a_month);
    		    		if("-1".equals(quarter)||(int_month==qu||int_month==(qu+1)||int_month==(qu+2)))
    		    		{
    		    			if("-1".equals(month)||month.equals(rs.getString("themonth")))
    		    			{
    		    				if("-1".equals(status)||status.equalsIgnoreCase(rs.getString("status"))||("-2".equals(status)&&!"7".equals(rs.getString("status"))))
		    	    			{
    		    					
    		    				   	/*考核对象状态：01	起草 02	已报批 03	已批04	已发布05	执行中06	结束	07	驳回	08	报审09	暂停*/	
 	         	    	           /*考核主体对考核对象的打分状态：0:未打分 1:正在编辑 2:已提交 3:不打分*/	
 //计划状态(0,           1,    2    ,3,    4,                    5,                     6,            7)=
 //(起草[可以编辑和删除], 报批,  已批,已发布, 启动[不能编辑和删除],//暂停[不能编辑和删除]，评估[不能编辑,可以删除 ]，结束) 
 	         	    	         	/*判断规则：要根据考核计划，对象，主体来判断，
 	         	    	         	考核对象状态为非已批（就起草）为制定，
 	         	    	         	打分：当考核对象状态为已批，考核计划为非结束，考核主体状态对对象的打分状态为非提交，可以打分，
 	         	    	         	否则为查看*/
    		    					String pstatus = rs.getString("status");
    		    					String level="";
    		    					/**考核主体是否可以参与审批*/
    		    					String isSP="0";
    		    					if(isHaveLeader)
    		    					{
    		    						/**当为非标准考核对象，从考核主体表查考核主体，，当是标准考核对象时，从考核主体表加上部门负责人的考核关系中查*/
    		    						if("1".equals(market)&&!mainbodyMap.containsKey(userView.getA0100()+object_id))
    		    							continue;
    		    						if("0".equals(market))
    		    						{
    		    							/**考核关系*//*
    		    							if(type==0)
    		    							{*/
    		    								if(!infomap.containsKey(userView.getA0100()+object_id)&&!infomap.containsKey(userView.getA0100()+leaderid))
    		    									continue;
    		    							/*}
    		    							*//**汇报关系*//*
    		    							else
    		    							{
    		    								if(!infomap.containsKey(userView.getA0100()+object_id)&&!infomap.containsKey(userView.getA0100()+leaderid))
    		    									continue;
    		    							}*/
    		    						}
    		    						if("1".equals(market))
    		    						{
    		    							LazyDynaBean bean =(LazyDynaBean)mainbodyMap.get(userView.getA0100()+object_id);
    		    							if(bean!=null)
    		    							{
    		    								level=(String)bean.get("level");
    		    								isSP=(String)bean.get("isSP");
    		    							}
    		    						}
    		    						else
    		    						{
    		    							/*if(type==0)
    		    							{*/
    		    						    	LazyDynaBean bean=(LazyDynaBean)infomap.get(userView.getA0100()+object_id);
    		    						    	if(bean==null)
    		    						    		bean=(LazyDynaBean)infomap.get(userView.getA0100()+leaderid);
    		    						    	if(bean!=null)
    		    						    	{
    		    							    	level=(String)bean.get("level");
    		    							    	isSP=(String)bean.get("isSP");
    		    						    	}
    		    							/*}
    		    							else
    		    							{
    		    								LazyDynaBean bean=(LazyDynaBean)infomap.get(userView.getDbname().toUpperCase()+userView.getA0100());
    		    								if(bean!=null)
    		    								{
    		    							    	level=(String)bean.get("level");
    		    							    	isSP=(String)bean.get("isSP");
    		    								}
    		    							}*/
    		    						}
    		    						if(userView.getA0100().equals(leaderid))
    		    							isSP="1";
    		    					}
    		    					else
    		    					{
    		    						if(!"4".equals(pstatus)&&!"6".equals(pstatus))
    		    						{
    		    							continue;
    		    						}
    		    						if(!mainbodyMap.containsKey(userView.getA0100()+object_id))
    		    						{
    		    							continue;
    		    						}
    		    						LazyDynaBean bean=(LazyDynaBean)mainbodyMap.get(userView.getA0100()+object_id);
    		    						if(bean!=null)
    		    						{
		    								level=(String)bean.get("level");
		    								isSP=(String)bean.get("isSP");
    		    						}
    		    					}
    		    					
    		    					boolean isSubmit=this.isSubmit(object_id, plan_id, dao,userView);
    		    					int tzCount=this.getp04(object_id, rs.getString("plan_id"), dao);
    		    					String spFlagDesc=AdminCode.getCodeName("23",spf);
    		    					if("07".equals(spf))
    		    						spFlagDesc+="/意见";
             	           	    	LazyDynaBean bean = new LazyDynaBean();
        	                 		String gradedesc=this.getDegreeDesc(plan_id, object_id);
        	                 		bean.set("gradedesc",gradedesc);
    	                	    	bean.set("planid",rs.getString("plan_id"));
         	    	             	bean.set("name", rs.getString("name"));
         	        	         	
         	        	         	String currsp="0";
         	        	         	String opt="0";
         							String flag="6";
         							String bs="0";
         							/**opt=0,不可操作，=1可操作，flag=查看，=7布置，=8打分*/
         							if(isHaveLeader)
         							{
         							String currappuser=rs.getString("currappuser");
         							
         							/**暂停和分发状态*/
         							if("8".equals(pstatus))
         							{
         								if("true".equalsIgnoreCase(allowLeadAdjustCard))
         								{
         									if("01".equals(spf)&& "1".equals(isSP))
         									{
         										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
             									{
             										spFlagDesc=spFlagDesc+"(调整后)";
             									}
             									opt="1";
             									flag="7";
             									//if(userView.getA0100().equalsIgnoreCase(leaderid))
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
         									else if(!isSubmit&& "03".equals(spf)&& "true".equalsIgnoreCase(TargetAllowAdjustAfterApprove)&&userView.getA0100().equals(leaderid))
         									{
         										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
             									{
             										spFlagDesc=spFlagDesc+"(调整后)";
             									}
         										opt="0";
             									flag="6";
							    			    bs="1";
         									}
         									else if("07".equals(spf)&&userView.getA0100().equals(leaderid))
         									{
         										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
             									{
             										spFlagDesc=spFlagDesc+"(调整后)";
             									}
         										opt="1";
             									flag="7";
             									currsp="1";
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
         									if("01".equals(spf)&& "1".equals(isSP))
         									{
         										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
             									{
             										spFlagDesc=spFlagDesc+"(调整后)";
             									}
             									opt="1";
             									flag="7";
             									if(userView.getA0100().equalsIgnoreCase(leaderid))
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
         									else if(!isSubmit&& "03".equals(spf)&& "true".equalsIgnoreCase(TargetAllowAdjustAfterApprove)&&userView.getA0100().equals(leaderid))
         									{
         										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
             									{
             										spFlagDesc=spFlagDesc+"(调整后)";
             									}
         										opt="0";
             									flag="6";
							    			    bs="1";
         									}
         									else if("07".equals(spf)&&userView.getA0100().equals(leaderid))
         									{
         										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
             									{
             										spFlagDesc=spFlagDesc+"(调整后)";
             									}
         										opt="1";
             									flag="7";
             									currsp="1";
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
         							}
         							else if("5".equals(pstatus))
         							{
         								opt="0";
					    				flag="6";
         							}
         							else
         							{
         								if(eMap==null)
         								{
         									eMap=this.getPerMainBodyData(userView,plan_id);
         								}
         								LazyDynaBean abean = (LazyDynaBean)eMap.get(plan_id+object_id+userView.getA0100());
         								if(abean!=null)
         								{
         						    		String scoreStatus=(String)abean.get("status");
         							    	if(("4".equals(pstatus)|| "6".equals(pstatus))&&("03".equals(spf)|| "true".equalsIgnoreCase(NoApproveTargetCanScore))&&!"2".equals(scoreStatus))
        	             	            	{
         							    		opt="1";
						    		    		flag="8";
        	             		            }
         							    	else
         							    	{
         							    		opt="0";
    						    				flag="6";
         							    	}
         								}
         								else
         								{
         									opt="0";
						    				flag="6";
         								}
         							}
		    	    			}
    		    				else
    		    				{
    		    					boolean sub = this.isSubmit(object_id, plan_id, dao, userView);
    		    					if(!"5".equals(pstatus)&&!sub)
    		    					{
    		    						opt="1";
    		    						flag="8";
    		    					}
    		    					else
    		    					{
    		    						opt="0";
    		    						flag="6";
    		    					}
    		    				}
         							
         							bean.set("bs", bs);
         							bean.set("opt",opt);
         							bean.set("flag", flag);
         							bean.set("currsp", currsp);
         							bean.set("level", level);
    		                	    String b0110 = AdminCode.getCodeName("UN",rs.getString("object_id"));
    		                     	if(b0110==null|| "".equals(b0110))
    		                    		b0110=AdminCode.getCodeName("UM",rs.getString("object_id"));
    	                	    	bean.set("b0110",b0110);
    	                	    	bean.set("a0100",rs.getString("object_id"));
        	                 		bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year")+" "+rs.getString("themonth")+ResourceFactory.getProperty("datestyle.month"));
        	                		//bean.set("body_id",rs.getString("body_id"));
        	                 		bean.set("sp_flag",spFlagDesc);
        	                 		bean.set("spf",spf);
        	                 		bean.set("objecttype","1");
        	                 		if(/*spf.equals("01")||spf.equals("07")||spf.equals("02")*/"8".equals(pstatus))
        	                 		{
        	                 			if(flag01)
        	                 		    	list.add(bean);
        	                 		}
        	                 		if("03".equals(spf))
        	                 		{
        	                 			if(flag01)
        	                 			{
        	                 		         list2.add(bean);
        	                 			}
        	                 		}
        	                 		if(("1".equals(currsp)|| "03".equals(spf))&&!"4".equals(pstatus)&&!"6".equals(pstatus)&& "1".equals(isSP))
        	                 		{
        	                 			if(map1.get(plan_id+"")==null)
        	                 			{
        	                 				LazyDynaBean abean = new LazyDynaBean();
        	                 				abean.set("a0100", userView.getA0100());
        	                 				abean.set("posid",userView.getUserPosId());
        	                 				abean.set("name",rs.getString("name"));
        	                 				abean.set("plan_id", plan_id+"");
        	                 				abean.set("status", pstatus);
        	                 				if(!leaderid.equals(userView.getA0100()))
        	                 				{
        	                 	    			map1.put(plan_id+"", plan_id+"");
        	                 		         	list3.add(abean);
        	                 				}
        	                 			}
        	                 		}
        	                 		if("8".equals(flag))
        	                 		{
        	                 			if(map2.get(plan_id+"")==null)
        	                 			{
        	                 				LazyDynaBean abean = new LazyDynaBean();
        	                 				abean.set("name",rs.getString("name"));
        	                 				abean.set("plan_id", plan_id+"");
        	                 				map2.put(plan_id+"", plan_id+"");
        	                 				list4.add(abean);
        	                 				scorebuf.append(plan_id+",");
        	                 			}
        	                 		}
		    	    			}
    		    			}
    		    		}
    		    	}
        		}else if("0".equals(cycle))//年度
        		{
        			if("-1".equals(year)||rs.getString("theyear").equals(year))
        			{
        				if("-1".equals(status)||status.equalsIgnoreCase(rs.getString("status"))||("-2".equals(status)&&!"7".equals(rs.getString("status"))))
    	    			{
        					if(isHaveLeader)
	    					{
	    						if("1".equals(market)&&!mainbodyMap.containsKey(userView.getA0100()+object_id)
	    								|| "0".equals(market)&&(!infomap.containsKey(userView.getA0100()+object_id)&&!infomap.containsKey(userView.getA0100()+leaderid)))
	    							continue;
	    					}
        					
        					String pstatus = rs.getString("status");
 	    	             	String level="";
 	    	             	String isSP="0";
	    					if(isHaveLeader)
	    					{
	    						/**当为非标准考核对象，从考核主体表查考核主体，，当是标准考核对象时，从考核主体表加上部门负责人的考核关系中查*/
	    						if("1".equals(market)&&!mainbodyMap.containsKey(userView.getA0100()+object_id))
	    							continue;
	    						if("0".equals(market))
	    						{
	    							/**考核关系*//*
	    							if(type==0)
	    							{*/
	    								if(!infomap.containsKey(userView.getA0100()+object_id)&&!infomap.containsKey(userView.getA0100()+leaderid))
	    									continue;
	    							/*}
	    							*//**汇报关系*//*
	    							else
	    							{
	    								if(!infomap.containsKey(userView.getDbname().toUpperCase()+userView.getA0100()))
	    									continue;
	    							}*/
	    						}
	    						if("1".equals(market))
	    						{
	    							LazyDynaBean bean =(LazyDynaBean)mainbodyMap.get(userView.getA0100()+object_id);
	    							if(bean!=null)
	    							{
	    								level=(String)bean.get("level");
	    								isSP=(String)bean.get("isSP");
	    							}
	    						}
	    						else
	    						{
	    							/*if(type==0)
	    							{*/
	    						    	LazyDynaBean bean=(LazyDynaBean)infomap.get(userView.getA0100()+object_id);
	    						    	if(bean==null)
	    						    		bean=(LazyDynaBean)infomap.get(userView.getA0100()+leaderid);
	    						    	if(bean!=null)
	    						    	{
	    							    	level=(String)bean.get("level");
	    							    	isSP=(String)bean.get("isSP");
	    						    	}
	    							/*}
	    							else
	    							{
	    								LazyDynaBean bean=(LazyDynaBean)infomap.get(userView.getDbname().toUpperCase()+userView.getA0100());
	    								if(bean!=null)
	    								{
	    							    	level=(String)bean.get("level");
	    							    	isSP=(String)bean.get("isSP");
	    								}
	    							}*/
	    						}
	    						if(userView.getA0100().equals(leaderid))
	    							isSP="1";
	    					}
	    					else
	    					{
	    						if(!"4".equals(pstatus)&&!"6".equals(pstatus))
	    						{
	    							continue;
	    						}
	    						if(!mainbodyMap.containsKey(userView.getA0100()+object_id))
	    						{
	    							continue;
	    						}
	    						LazyDynaBean bean=(LazyDynaBean)mainbodyMap.get(userView.getA0100()+object_id);
	    						if(bean!=null)
	    						{
    								level=(String)bean.get("level");
    								isSP=(String)bean.get("isSP");
	    						}
	    					}
	    					
	    					boolean isSubmit = this.isSubmit(object_id, plan_id, dao,userView);
	    					int tzCount=this.getp04(object_id, rs.getString("plan_id"), dao);
	    					String spFlagDesc=AdminCode.getCodeName("23",spf);
	    					if("07".equals(spf))
	    						spFlagDesc+="/意见";
     	           	    	LazyDynaBean bean = new LazyDynaBean();
	                 		String gradedesc=this.getDegreeDesc(plan_id, object_id);
	                 		bean.set("gradedesc",gradedesc);
                	    	bean.set("planid",rs.getString("plan_id"));
 	    	             	bean.set("name", rs.getString("name"));
 	        	         	String currsp="0";
 	        	         	String opt="0";
 							String flag="6";
 							String bs="0";
 							/**opt=0,不可操作，=1可操作，flag=查看，=7布置，=8打分*/
 							if(isHaveLeader)
 							{
 							String currappuser=rs.getString("currappuser");
 							/*if(currappuser!=null&&!currappuser.equals("")&&currappuser.equals(userView.getA0100()))
 								currsp="1";*/
 							/**暂停和分发状态*/
 							if("8".equals(pstatus))
 							{
 								if("true".equalsIgnoreCase(allowLeadAdjustCard))
 								{
 									if("01".equals(spf)&& "1".equals(isSP))
 									{
 										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
     									{
     										spFlagDesc=spFlagDesc+"(调整后)";
     									}
     									opt="1";
     									flag="7";
     									//if(userView.getA0100().equalsIgnoreCase(leaderid))
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
 									else if(!isSubmit&& "03".equals(spf)&& "true".equalsIgnoreCase(TargetAllowAdjustAfterApprove)&&userView.getA0100().equals(leaderid))
 									{
 										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
     									{
     										spFlagDesc=spFlagDesc+"(调整后)";
     									}
 										opt="0";
     									flag="6";
					    			    bs="1";
 									}
 									else if("07".equals(spf)&&userView.getA0100().equals(leaderid))
 									{
 										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
     									{
     										spFlagDesc=spFlagDesc+"(调整后)";
     									}
 										opt="1";
     									flag="7";
     									currsp="1";
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
 									if("01".equals(spf)&& "1".equals(isSP))
 									{
 										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
     									{
     										spFlagDesc=spFlagDesc+"(调整后)";
     									}
     									opt="1";
     									flag="7";
     									if(userView.getA0100().equalsIgnoreCase(leaderid))
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
 									else if(!isSubmit&& "03".equals(spf)&& "true".equalsIgnoreCase(TargetAllowAdjustAfterApprove)&&userView.getA0100().equals(leaderid))
 									{
 										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
     									{
     										spFlagDesc=spFlagDesc+"(调整后)";
     									}
 										opt="0";
     									flag="6";
					    			    bs="1";
 									}
 									else if("07".equals(spf)&&userView.getA0100().equals(leaderid))
 									{
 										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
     									{
     										spFlagDesc=spFlagDesc+"(调整后)";
     									}
 										opt="1";
     									flag="7";
     									currsp="1";
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
 							}
 							else if("5".equals(pstatus))
 							{
 								opt="0";
			    				flag="6";
 							}
 							else
 							{
 								if(eMap==null)
 								{
 									eMap=this.getPerMainBodyData(userView,plan_id);
 								}
 								LazyDynaBean abean = (LazyDynaBean)eMap.get(plan_id+object_id+userView.getA0100());
 								if(abean!=null)
 								{
 						    		String scoreStatus=(String)abean.get("status");
 						    		if(("4".equals(pstatus)|| "6".equals(pstatus))&&("03".equals(spf)|| "true".equalsIgnoreCase(NoApproveTargetCanScore))&&!"2".equals(scoreStatus))
	             	            	{
 							    		opt="1";
				    		    		flag="8";
	             		            }
 							    	else
 								   {
 							    		opt="0";
				    		    		flag="6";
 							    	}
 								}else
 								{
 									opt="0";
			    		    		flag="6";
 								}
 							}
    	    			}
        				else
	    				{
        					boolean sub = this.isSubmit(object_id, plan_id, dao, userView);
	    					if(!"5".equals(pstatus)&&!sub)
	    					{
	    						opt="1";
	    						flag="8";
	    					}
	    					else
	    					{
	    						opt="0";
	    						flag="6";
	    					}
	    				}
 							bean.set("bs", bs);
 							bean.set("opt",opt);
 							bean.set("flag", flag);
 							bean.set("currsp", currsp);
 							bean.set("level", level);
	            	        String b0110 = AdminCode.getCodeName("UN",rs.getString("object_id"));
	                    	if(b0110==null|| "".equals(b0110))
	                    		b0110=AdminCode.getCodeName("UM",rs.getString("object_id"));
                	    	bean.set("b0110",b0110);
                	    	bean.set("a0100",rs.getString("object_id"));
	                 		bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year"));
	                 		//bean.set("body_id",rs.getString("body_id"));
	                 		bean.set("sp_flag",spFlagDesc);
	                 		bean.set("spf",spf);
	                 		bean.set("objecttype","1");
	                 		if(/*spf.equals("01")||spf.equals("07")||spf.equals("02")*/"8".equals(pstatus))
	                 		{
	                 			if(flag01)
	                 		    	list.add(bean);
	                 		}
	                 		if("03".equals(spf))
	                 		{
	                 			if(flag01)
	                 			{
	                 		         list2.add(bean);
	                 				
	                 			}
	                 		}
	                 		if(("1".equals(currsp)|| "03".equals(spf))&&!"4".equals(pstatus)&&!"6".equals(pstatus)&& "1".equals(isSP))
	                 		{
	                 			if(map1.get(plan_id+"")==null)
	                 			{
	                 				LazyDynaBean abean = new LazyDynaBean();
	                 				abean.set("a0100", userView.getA0100());
	                 				abean.set("posid",userView.getUserPosId());
	                 				abean.set("name",rs.getString("name"));
	                 				abean.set("plan_id", plan_id+"");
	                 				abean.set("status", pstatus);
	                 				if(!leaderid.equals(userView.getA0100()))
	                 				{
	                 	    			map1.put(plan_id+"", plan_id+"");
	                 		         	list3.add(abean);
	                 				}
	                 			}
	                 		}
	                 		if("8".equals(flag))
	                 		{
	                 			if(map2.get(plan_id+"")==null)
	                 			{
	                 				LazyDynaBean abean = new LazyDynaBean();
	                 				abean.set("name",rs.getString("name"));
	                 				abean.set("plan_id", plan_id+"");
	                 				map2.put(plan_id+"", plan_id+"");
	                 				list4.add(abean);
	                 				scorebuf.append(plan_id+",");
	                 			}
	                 		}
    	    			}
        			}
        		}
        		else if("1".equals(cycle))//半年度
        		{
        			if("-1".equals(year)||year.equals(rs.getString("theyear")))
        			{
        				String a_quarter = rs.getString("thequarter");
        				if("1".equals(a_quarter))//上半年，算1，2季度
        				{
        					if("-1".equals(quarter)||Integer.parseInt(quarter)==1||Integer.parseInt(quarter)==2)
        					{
        						if("-1".equals(status)||status.equalsIgnoreCase(rs.getString("status"))||("-2".equals(status)&&!"7".equals(rs.getString("status"))))
		    	    			{
        							String pstatus = rs.getString("status");
        							String level="";
        							String isSP="0";
		    					if(isHaveLeader)
		    					{
		    						/**当为非标准考核对象，从考核主体表查考核主体，，当是标准考核对象时，从考核主体表加上部门负责人的考核关系中查*/
		    						if("1".equals(market)&&!mainbodyMap.containsKey(userView.getA0100()+object_id))
		    							continue;
		    						if("0".equals(market))
		    						{
		    							/**考核关系*//*
		    							if(type==0)
		    							{*/
		    								if(!infomap.containsKey(userView.getA0100()+object_id)&&!infomap.containsKey(userView.getA0100()+leaderid))
		    									continue;
		    						/*	}
		    							*//**汇报关系*//*
		    							else
		    							{
		    								if(!infomap.containsKey(userView.getDbname().toUpperCase()+userView.getA0100()))
		    									continue;
		    							}*/
		    						}
		    						if("1".equals(market))
		    						{
		    							LazyDynaBean bean =(LazyDynaBean)mainbodyMap.get(userView.getA0100()+object_id);
		    							if(bean!=null)
		    							{
		    								level=(String)bean.get("level");
		    								isSP=(String)bean.get("isSP");
		    							}
		    						}
		    						else
		    						{
		    							/*if(type==0)
		    							{*/
		    						    	LazyDynaBean bean=(LazyDynaBean)infomap.get(userView.getA0100()+object_id);
		    						    	if(bean==null)
		    						    		bean=(LazyDynaBean)infomap.get(userView.getA0100()+leaderid);
		    						    	if(bean!=null)
		    						    	{
		    							    	level=(String)bean.get("level");
		    							    	isSP=(String)bean.get("isSP");
		    						    	}
		    							/*}
		    							else
		    							{
		    								LazyDynaBean bean=(LazyDynaBean)infomap.get(userView.getDbname().toUpperCase()+userView.getA0100());
		    								if(bean!=null)
		    								{
		    							    	level=(String)bean.get("level");
		    							    	isSP=(String)bean.get("isSP");
		    								}
		    							}*/
		    						}
		    						if(userView.getA0100().equals(leaderid))
		    							isSP="1";
		    					}
		    					else
		    					{
		    						if(!"4".equals(pstatus)&&!"6".equals(pstatus))
		    						{
		    							continue;
		    						}
		    						if(!mainbodyMap.containsKey(userView.getA0100()+object_id))
		    						{
		    							continue;
		    						}
		    						LazyDynaBean bean=(LazyDynaBean)mainbodyMap.get(userView.getA0100()+object_id);
		    						if(bean!=null)
		    						{
		    							isSP=(String)bean.get("isSP");
		    							level=(String)bean.get("level");
		    						}
	    								
		    					}
		    					
		    					boolean isSubmit=this.isSubmit(object_id, plan_id, dao,userView);
		    					int tzCount=this.getp04(object_id, rs.getString("plan_id"), dao);
		    					String spFlagDesc=AdminCode.getCodeName("23",spf);
		    					if("07".equals(spf))
		    						spFlagDesc+="/意见";
         	           	    	LazyDynaBean bean = new LazyDynaBean();
    	                 		String gradedesc=this.getDegreeDesc(plan_id, object_id);
    	                 		bean.set("gradedesc",gradedesc);
	                	    	bean.set("planid",rs.getString("plan_id"));
     	    	             	bean.set("name", rs.getString("name"));
     	        	         	String currsp="0";
     	        	         	String opt="0";
     							String flag="6";
     							String bs="0";
     							/**opt=0,不可操作，=1可操作，flag=查看，=7布置，=8打分*/
     							if(isHaveLeader)
     							{
     							String currappuser=rs.getString("currappuser");
     							/*if(currappuser!=null&&!currappuser.equals("")&&currappuser.equals(userView.getA0100()))
     								currsp="1";*/
     							/**暂停和分发状态*/
     							if("8".equals(pstatus))
     							{
     								if("true".equalsIgnoreCase(allowLeadAdjustCard))
     								{
     									if("01".equals(spf)&& "1".equals(isSP))
     									{
     										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
         									{
         										spFlagDesc=spFlagDesc+"(调整后)";
         									}
         									opt="1";
         									flag="7";
         									//if(userView.getA0100().equalsIgnoreCase(leaderid))
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
     									else if(!isSubmit&& "03".equals(spf)&& "true".equalsIgnoreCase(TargetAllowAdjustAfterApprove)&&userView.getA0100().equals(leaderid))
     									{
     										opt="0";
         									flag="6";
						    			    bs="1";
     									}
     									else if("07".equals(spf)&&userView.getA0100().equals(leaderid))
     									{
     										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
         									{
         										spFlagDesc=spFlagDesc+"(调整后)";
         									}
     										opt="1";
         									flag="7";
         									currsp="1";
     									}
     									else
     									{
     										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
         									{
         										spFlagDesc=spFlagDesc+"(调整后)";
         									}
     									}
     								}
     								else
     								{
     									if("01".equals(spf)&& "1".equals(isSP))
     									{
     										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
         									{
         										spFlagDesc=spFlagDesc+"(调整后)";
         									}
         									opt="1";
         									flag="7";
         									if(userView.getA0100().equalsIgnoreCase(leaderid))
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
     									else if(!isSubmit&& "03".equals(spf)&& "true".equalsIgnoreCase(TargetAllowAdjustAfterApprove)&&userView.getA0100().equals(leaderid))
     									{
     										opt="0";
         									flag="6";
						    			    bs="1";
     									}
     									else if("07".equals(spf)&&userView.getA0100().equals(leaderid))
     									{
     										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
         									{
         										spFlagDesc=spFlagDesc+"(调整后)";
         									}
     										opt="1";
         									flag="7";
         									currsp="1";
     									}
     									else
     									{
     										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
         									{
         										spFlagDesc=spFlagDesc+"(调整后)";
         									}
     									}
     								}
     							}
     							else if("5".equals(pstatus))
     							{
     								opt="0";
				    				flag="6";
     							}
     							else
     							{
     								if(eMap==null)
     								{
     									eMap=this.getPerMainBodyData(userView,plan_id);
     								}
     								LazyDynaBean abean = (LazyDynaBean)eMap.get(plan_id+object_id+userView.getA0100());
     								if(abean!=null)
     								{
     						    		String scoreStatus=(String)abean.get("status");
     					    			if(("4".equals(pstatus)|| "6".equals(pstatus))&& "03".equals(spf)&&!"2".equals(scoreStatus))
    	             	            	{
     							    		opt="1";
					    	    			flag="8";
    	             		            }
     							    	else
     							    	{
     						    			opt="0";
					    	    			flag="6";
     						    		}
     								}
     								else
     								{
     									opt="0";
				    		    		flag="6";
     								}
     							}
     							}
     							else
    		    				{
     								boolean sub = this.isSubmit(object_id, plan_id, dao, userView);
    		    					if(!"5".equals(pstatus)&&!sub)
    		    					{
    		    						opt="1";
    		    						flag="8";
    		    					}
    		    					else
    		    					{
    		    						opt="0";
    		    						flag="6";
    		    					}
    		    				}
     							bean.set("bs", bs);
     							bean.set("opt",opt);
     							bean.set("flag", flag);
     							bean.set("currsp", currsp);
     							bean.set("level", level);
        	            	        String b0110 = AdminCode.getCodeName("UN",rs.getString("object_id"));
        	                     	if(b0110==null|| "".equals(b0110))
        	                    		b0110=AdminCode.getCodeName("UM",rs.getString("object_id"));
                    	        	bean.set("b0110",b0110);
                    	        	//bean.set("body_id",rs.getString("body_id"));
                        	    	bean.set("a0100",rs.getString("object_id"));
        	                 		bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year")+" "+ResourceFactory.getProperty("report.pigeonhole.uphalfyear"));
        	                 		bean.set("sp_flag",spFlagDesc);
        	                 		bean.set("spf",spf);
        	                 		bean.set("objecttype","1");
        	                 		if(/*spf.equals("01")||spf.equals("07")||spf.equals("02")*/"8".equals(pstatus))
        	                 		{
        	                 			if(flag01)
        	                 		    	list.add(bean);
        	                 		}
        	                 		if("03".equals(spf))
        	                 		{
        	                 			if(flag01)
        	                 			{
        	                 			
        	                 		         	list2.add(bean);
        	                 				
        	                 			}
        	                 		}
        	                 		if(("1".equals(currsp)|| "03".equals(spf))&&!"4".equals(pstatus)&&!"6".equals(pstatus)&& "1".equals(isSP))
        	                 		{
        	                 			if(map1.get(plan_id+"")==null)
        	                 			{
        	                 				LazyDynaBean abean = new LazyDynaBean();
        	                 				abean.set("a0100", userView.getA0100());
        	                 				abean.set("posid",userView.getUserPosId());
        	                 				abean.set("name",rs.getString("name"));
        	                 				abean.set("plan_id", plan_id+"");
        	                 				abean.set("status", pstatus);
        	                 				if(!leaderid.equals(userView.getA0100()))
        	                 				{
        	                 	    			map1.put(plan_id+"", plan_id+"");
        	                 		         	list3.add(abean);
        	                 				}
        	                 			}
        	                 		}
        	                 		if("8".equals(flag))
        	                 		{
        	                 			if(map2.get(plan_id+"")==null)
        	                 			{
        	                 				LazyDynaBean abean = new LazyDynaBean();
        	                 				abean.set("name",rs.getString("name"));
        	                 				abean.set("plan_id", plan_id+"");
        	                 				map2.put(plan_id+"", plan_id+"");
        	                 				list4.add(abean);
        	                 				scorebuf.append(plan_id+",");
        	                 			}
        	                 		}
		    	    			}
        					}
        				}
        				else//下半年算3，4季度
        				{
        					if("-1".equals(quarter)||Integer.parseInt(quarter)==3||Integer.parseInt(quarter)==4)
        					{
        						if("-1".equals(status)||status.equalsIgnoreCase(rs.getString("status"))||("-2".equals(status)&&!"7".equals(rs.getString("status"))))
		    	    			{
        							String pstatus = rs.getString("status");
        							String level="";
        							String isSP="0";
    		    					if(isHaveLeader)
    		    					{
    		    						/**当为非标准考核对象，从考核主体表查考核主体，，当是标准考核对象时，从考核主体表加上部门负责人的考核关系中查*/
    		    						if("1".equals(market)&&!mainbodyMap.containsKey(userView.getA0100()+object_id))
    		    							continue;
    		    						if("0".equals(market))
    		    						{
    		    							/**考核关系*//*
    		    							if(type==0)
    		    							{*/
    		    								if(!infomap.containsKey(userView.getA0100()+object_id)&&!infomap.containsKey(userView.getA0100()+leaderid))
    		    									continue;
    		    							/*}
    		    							*//**汇报关系*//*
    		    							else
    		    							{
    		    								if(!infomap.containsKey(userView.getDbname().toUpperCase()+userView.getA0100()))
    		    									continue;
    		    							}*/
    		    						}
    		    						if("1".equals(market))
    		    						{
    		    							LazyDynaBean bean =(LazyDynaBean)mainbodyMap.get(userView.getA0100()+object_id);
    		    							if(bean!=null)
    		    							{
    		    								level=(String)bean.get("level");
    		    								isSP=(String)bean.get("isSP");
    		    							}
    		    						}
    		    						else
    		    						{
    		    							/*if(type==0)
    		    							{*/
    		    						    	LazyDynaBean bean=(LazyDynaBean)infomap.get(userView.getA0100()+object_id);
    		    						    	if(bean==null)
    		    						    		bean=(LazyDynaBean)infomap.get(userView.getA0100()+leaderid);
    		    						    	if(bean!=null)
    		    						    	{
    		    							    	level=(String)bean.get("level");
    		    							    	isSP=(String)bean.get("isSP");
    		    						    	}
    		    							
    		    						}
    		    						if(userView.getA0100().equals(leaderid))
    		    							isSP="1";
    		    					}
    		    					else
    		    					{
    		    						if(!"4".equals(pstatus)&&!"6".equals(pstatus))
    		    						{
    		    							continue;
    		    						}
    		    						if(!mainbodyMap.containsKey(userView.getA0100()+object_id))
    		    						{
    		    							continue;
    		    						}
    		    						LazyDynaBean bean=(LazyDynaBean)mainbodyMap.get(userView.getA0100()+object_id);
    		    						if(bean!=null)
    		    						{
		    								level=(String)bean.get("level");
		    								isSP=(String)bean.get("isSP");
    		    						}
    		    					}
    		    					boolean isSubmit = this.isSubmit(object_id, plan_id, dao,userView);
    		    					int tzCount=this.getp04(object_id, rs.getString("plan_id"), dao);
    		    					String spFlagDesc=AdminCode.getCodeName("23",spf);
    		    					if("07".equals(spf))
    		    						spFlagDesc+="/意见";
             	           	    	LazyDynaBean bean = new LazyDynaBean();
        	                 		String gradedesc=this.getDegreeDesc(plan_id, object_id);
        	                 		bean.set("gradedesc",gradedesc);
    	                	    	bean.set("planid",rs.getString("plan_id"));
         	    	             	bean.set("name", rs.getString("name"));
         	        	         	String currsp="0";
         	        	         	String opt="0";
         							String flag="6";
         							String bs="0";
         							/**opt=0,不可操作，=1可操作，flag=查看，=7布置，=8打分*/
         							if(isHaveLeader)
         							{
         							String currappuser=rs.getString("currappuser");
         							/*if(currappuser!=null&&!currappuser.equals("")&&currappuser.equals(userView.getA0100()))
         								currsp="1";*/
         							/**暂停和分发状态*/
         							if("8".equals(pstatus))
         							{
         								if("true".equalsIgnoreCase(allowLeadAdjustCard))
         								{
         									if("01".equals(spf)&& "1".equals(isSP))
         									{
         										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
             									{
             										spFlagDesc=spFlagDesc+"(调整后)";
             									}
             									opt="1";
             									flag="7";
             									//if(userView.getA0100().equalsIgnoreCase(leaderid))
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
         									else if(!isSubmit&& "03".equals(spf)&& "true".equalsIgnoreCase(TargetAllowAdjustAfterApprove)&&userView.getA0100().equals(leaderid))
         									{
         										opt="0";
             									flag="6";
							    			    bs="1";
         									}
         									else if("07".equals(spf)&&userView.getA0100().equals(leaderid))
         									{
         										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
             									{
             										spFlagDesc=spFlagDesc+"(调整后)";
             									}
         										opt="1";
             									flag="7";
             									currsp="1";
         									}
         									else
         									{
         										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
             									{
             										spFlagDesc=spFlagDesc+"(调整后)";
             									}
         									}
         								}
         								else
         								{
         									if("01".equals(spf)&& "1".equals(isSP))
         									{
         										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
             									{
             										spFlagDesc=spFlagDesc+"(调整后)";
             									}
             									opt="1";
             									flag="7";
             									if(userView.getA0100().equalsIgnoreCase(leaderid))
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
         									else if(!isSubmit&& "03".equals(spf)&& "true".equalsIgnoreCase(TargetAllowAdjustAfterApprove)&&userView.getA0100().equals(leaderid))
         									{
         										opt="0";
             									flag="6";
							    			    bs="1";
         									}
         									else if("07".equals(spf)&&userView.getA0100().equals(leaderid))
         									{
         										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
             									{
             										spFlagDesc=spFlagDesc+"(调整后)";
             									}
         										opt="1";
             									flag="7";
             									currsp="1";
         									}
         									else
         									{
         										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
             									{
             										spFlagDesc=spFlagDesc+"(调整后)";
             									}
         									}
         								}
         							}
         							else if("5".equals(pstatus))
         							{
         								opt="0";
					    				flag="6";
         							}
         							else
         							{
         								if(eMap==null)
         								{
         									eMap=this.getPerMainBodyData(userView,plan_id);
         								}
         								LazyDynaBean abean = (LazyDynaBean)eMap.get(plan_id+object_id+userView.getA0100());
         								if(abean!=null)
         								{
         				    				String scoreStatus=(String)abean.get("status");
         					    			if(("4".equals(pstatus)|| "6".equals(pstatus))&&("03".equals(spf)|| "true".equalsIgnoreCase(NoApproveTargetCanScore))&&!"2".equals(scoreStatus))
        	             	            	{
         						    			opt="1";
						        				flag="8";
        	             		            }
         						    		else
         						    		{
         							    		opt="0";
						    	    			flag="6";
         							    	}
         								}
         								else
         								{
         									opt="0";
    				    		    		flag="6";
         								}
         							}
         							}
         							else
        		    				{
         								boolean sub = this.isSubmit(object_id, plan_id, dao, userView);
        		    					if(!"5".equals(pstatus)&&!sub)
        		    					{
        		    						opt="1";
        		    						flag="8";
        		    					}
        		    					else
        		    					{
        		    						opt="0";
        		    						flag="6";
        		    					}
        		    				}
         							bean.set("bs", bs);
         							bean.set("opt",opt);
         							bean.set("flag", flag);
         							bean.set("currsp", currsp);
         							bean.set("level", level);
         	    	            	String b0110 = AdminCode.getCodeName("UN",rs.getString("object_id"));
        	                    	if(b0110==null|| "".equals(b0110))
        	                    		b0110=AdminCode.getCodeName("UM",rs.getString("object_id"));
                    	        	bean.set("b0110",b0110);
                    	        	//bean.set("body_id",rs.getString("body_id"));
                    	        	bean.set("a0100",rs.getString("object_id"));
        	                 		bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year")+" "+ResourceFactory.getProperty("report.pigeonhole.downhalfyear"));
        	                 		bean.set("sp_flag",spFlagDesc);
        	                 		bean.set("spf",spf);
        	                 		bean.set("objecttype","1");
        	                 		if(/*spf.equals("01")||spf.equals("07")||spf.equals("02")*/"8".equals(pstatus))
        	                 		{
        	                 			if(flag01)
        	                 		    	list.add(bean);
        	                 		}
        	                 		if("03".equals(spf))
        	                 		{
        	                 			if(flag01)
        	                 			{
        	                 				
        	                 		         	list2.add(bean);
        	                 				
        	                 			}
        	                 		}
        	                 		if(("1".equals(currsp)|| "03".equals(spf))&&!"4".equals(pstatus)&&!"6".equals(pstatus)&& "1".equals(isSP))
        	                 		{
        	                 			if(map1.get(plan_id+"")==null)
        	                 			{
        	                 				LazyDynaBean abean = new LazyDynaBean();
        	                 				abean.set("a0100", userView.getA0100());
        	                 				abean.set("posid",userView.getUserPosId());
        	                 				abean.set("name",rs.getString("name"));
        	                 				abean.set("plan_id", plan_id+"");
        	                 				abean.set("status", pstatus);
        	                 				if(!leaderid.equals(userView.getA0100()))
        	                 				{
        	                 	    			map1.put(plan_id+"", plan_id+"");
        	                 		         	list3.add(abean);
        	                 				}
        	                 			}
        	                 		}
        	                 		if("8".equals(flag))
        	                 		{
        	                 			if(map2.get(plan_id+"")==null)
        	                 			{
        	                 				LazyDynaBean abean = new LazyDynaBean();
        	                 				abean.set("name",rs.getString("name"));
        	                 				abean.set("plan_id", plan_id+"");
        	                 				map2.put(plan_id+"", plan_id+"");
        	                 				list4.add(abean);
        	                 				scorebuf.append(plan_id+",");
        	                 			}
        	                 		}
		    	    			}
        					}
        				}
        			}
        		}
        		else if("7".equals(cycle))//不定期
        		{
        			String sy=rs.getString("sy");
        			String sm=rs.getString("sm");
        			String ey=rs.getString("ey");
        			String em=rs.getString("em");
        			int int_year = 0;
        			int int_sy=0;
        			int int_sm=0;
        			int int_ey=0;
        			int int_em=0;
        			if(!"-1".equals(year))
        			{
        				int_year=Integer.parseInt(year);
        				int_sy=Integer.parseInt(sy);
        				int_sm=Integer.parseInt(sm);
        				int_ey=Integer.parseInt(ey);
        				int_em=Integer.parseInt(em);
        			}
        			if("-1".equals(year)||(!"-1".equals(year)&&int_sy<=int_year&&int_ey>=int_year))
        			{
        				int int_month=0;
        				if(!"-1".equals(month))
        				{
        					int_month=Integer.parseInt(month);
        				}
        				if("-1".equals(month)||(!"-1".equals(month)&&(int_sm==int_month||int_em==int_month)))
        				{
        					if("-1".equals(status)||status.equalsIgnoreCase(rs.getString("status"))||("-2".equals(status)&&!"7".equals(rs.getString("status"))))
	    	    			{
        						String pstatus = rs.getString("status");
        						String level="";
        						String isSP="0";
		    					if(isHaveLeader)
		    					{
		    						/**当为非标准考核对象，从考核主体表查考核主体，，当是标准考核对象时，从考核主体表加上部门负责人的考核关系中查*/
		    						if("1".equals(market)&&!mainbodyMap.containsKey(userView.getA0100()+object_id))
		    							continue;
		    						if("0".equals(market))
		    						{
		    							/**考核关系*//*
		    							if(type==0)
		    							{*/
		    								if(!infomap.containsKey(userView.getA0100()+object_id)&&!infomap.containsKey(userView.getA0100()+leaderid))
		    									continue;
		    							/*}
		    							*//**汇报关系*//*
		    							else
		    							{
		    								if(!infomap.containsKey(userView.getDbname().toUpperCase()+userView.getA0100()))
		    									continue;
		    							}*/
		    						}
		    						if("1".equals(market))
		    						{
		    							LazyDynaBean bean =(LazyDynaBean)mainbodyMap.get(userView.getA0100()+object_id);
		    							if(bean!=null)
		    							{
		    								level=(String)bean.get("level");
		    								isSP=(String)bean.get("isSP");
		    							}
		    						}
		    						else
		    						{
		    							/*if(type==0)
		    							{*/
		    						    	LazyDynaBean bean=(LazyDynaBean)infomap.get(userView.getA0100()+object_id);
		    						    	if(bean==null)
		    						    		bean=(LazyDynaBean)infomap.get(userView.getA0100()+leaderid);
		    						    	if(bean!=null)
		    						    	{
		    							    	level=(String)bean.get("level");
		    							    	isSP=(String)bean.get("isSP");
		    						    	}
		    						
		    						}
		    						if(userView.getA0100().equals(leaderid))
		    							isSP="1";
		    					}
		    					else
		    					{
		    						if(!"4".equals(pstatus)&&!"6".equals(pstatus))
		    						{
		    							continue;
		    						}
		    						if(!mainbodyMap.containsKey(userView.getA0100()+object_id))
		    						{
		    							continue;
		    						}
		    						LazyDynaBean bean=(LazyDynaBean)mainbodyMap.get(userView.getA0100()+object_id);
		    						if(bean!=null)
		    						{
	    								level=(String)bean.get("level");
	    								isSP=(String)bean.get("isSP");
		    						}
		    					}
		    					
		    					boolean isSubmit = this.isSubmit(object_id, plan_id, dao,userView);
		    					int tzCount=this.getp04(object_id, rs.getString("plan_id"), dao);
		    					String spFlagDesc=AdminCode.getCodeName("23",spf);
		    					if("07".equals(spf))
		    						spFlagDesc+="/意见";
         	           	    	LazyDynaBean bean = new LazyDynaBean();
    	                 		String gradedesc=this.getDegreeDesc(plan_id, object_id);
    	                 		bean.set("gradedesc",gradedesc);
	                	    	bean.set("planid",rs.getString("plan_id"));
     	    	             	bean.set("name", rs.getString("name"));
     	        	         	String currsp="0";
     	        	         	String opt="0";
     							String flag="6";
     							String bs="0";
     							/**opt=0,不可操作，=1可操作，flag=6查看，=7布置，=8打分*/
     							if(isHaveLeader)
     							{
     							String currappuser=rs.getString("currappuser");
     							/*if(currappuser!=null&&!currappuser.equals("")&&currappuser.equals(userView.getA0100()))
     								currsp="1";*/
     							/**暂停和分发状态*/
     							if("8".equals(pstatus))
     							{
     								if("true".equalsIgnoreCase(allowLeadAdjustCard))
     								{
     									if("01".equals(spf)&& "1".equals(isSP))
     									{
     										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
         									{
         										spFlagDesc=spFlagDesc+"(调整后)";
         									}
         									opt="1";
         									flag="7";
         									//if(userView.getA0100().equalsIgnoreCase(leaderid))
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
     									else if(!isSubmit&& "03".equals(spf)&& "true".equalsIgnoreCase(TargetAllowAdjustAfterApprove)&&userView.getA0100().equals(leaderid))
     									{
     										opt="0";
         									flag="6";
						    			    bs="1";
     									}
     									else if("07".equals(spf)&&userView.getA0100().equals(leaderid))
     									{
     										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
         									{
         										spFlagDesc=spFlagDesc+"(调整后)";
         									}
     										opt="1";
         									flag="7";
         									currsp="1";
     									}
     									else
     									{
     										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
         									{
         										spFlagDesc=spFlagDesc+"(调整后)";
         									}
     									}
     								}
     								else
     								{
     									if("01".equals(spf)&& "1".equals(isSP))
     									{
     										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
         									{
         										spFlagDesc=spFlagDesc+"(调整后)";
         									}
         									opt="1";
         									flag="7";
         									if(userView.getA0100().equalsIgnoreCase(leaderid))
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
     									else if(!isSubmit&& "03".equals(spf)&& "true".equalsIgnoreCase(TargetAllowAdjustAfterApprove)&&userView.getA0100().equals(leaderid))
     									{
     										opt="0";
         									flag="6";
						    			    bs="1";
     									}
     									else if("07".equals(spf)&&userView.getA0100().equals(leaderid))
     									{
     										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
         									{
         										spFlagDesc=spFlagDesc+"(调整后)";
         									}
     										opt="1";
         									flag="7";
         									currsp="1";
     									}
     									else
     									{
     										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
         									{
         										spFlagDesc=spFlagDesc+"(调整后)";
         									}
     									}
     								}
     							}
     							else if("5".equals(pstatus))
     							{
     								opt="0";
				    				flag="6";
     							}
     							else
     							{
     								if(eMap==null)
     								{
     									eMap=this.getPerMainBodyData(userView,plan_id);
     								}
     								LazyDynaBean abean = (LazyDynaBean)eMap.get(plan_id+object_id+userView.getA0100());
     								if(abean!=null)
     								{
     						    		String scoreStatus=(String)abean.get("status");
     						     		if(("4".equals(pstatus)|| "6".equals(pstatus))&& "03".equals(spf)&&!"2".equals(scoreStatus))
    	             	             	{
     						     			opt="1";
					    	    			flag="8";
    	             		            }
     							    	else
     							    	{
     							    		opt="0";
					    		    		flag="6";
     							    	}
     								}
     								else
     								{
     									opt="0";
				    		    		flag="6";
     								}
     							}
     							}else
    		    				{
     								boolean sub = this.isSubmit(object_id, plan_id, dao, userView);
    		    					if(!"5".equals(pstatus)&&!sub)
    		    					{
    		    						opt="1";
    		    						flag="8";
    		    					}
    		    					else
    		    					{
    		    						opt="0";
    		    						flag="6";
    		    					}
    		    				}
     							bean.set("bs", bs);
     							bean.set("opt",opt);
     							bean.set("flag", flag);
     							bean.set("currsp", currsp);
     							bean.set("level", level);
    	                	    String b0110 = AdminCode.getCodeName("UN",rs.getString("object_id"));
    	                    	if(b0110==null|| "".equals(b0110))
    	                    		b0110=AdminCode.getCodeName("UM",rs.getString("object_id"));
                	        	bean.set("b0110",b0110);
                	          	bean.set("a0100",rs.getString("object_id"));
                	        	//bean.set("body_id",rs.getString("body_id"));
    	                		bean.set("evaluate",sy+"."+sm+"."+rs.getString("sd")+"-"+ey+"."+em+"."+rs.getString("ed"));
    	                		bean.set("sp_flag",spFlagDesc);
    	                		bean.set("spf",spf);
    	                		bean.set("objecttype","1");
    	                		if(/*spf.equals("01")||spf.equals("07")||spf.equals("02")*/"8".equals(pstatus))
    	                 		{
    	                 			if(flag01)
    	                 		    	list.add(bean);
    	                 		}
    	                 		if("03".equals(spf))
    	                 		{
    	                 			if(flag01)
    	                 			{
    	                 				
    	                 		         	list2.add(bean);
    	                 				
    	                 			}
    	                 		}
    	                 		if(("1".equals(currsp)|| "03".equals(spf))&&!"4".equals(pstatus)&&!"6".equals(pstatus)&& "1".equals(isSP))
    	                 		{
    	                 			if(map1.get(plan_id+"")==null)
    	                 			{
    	                 				LazyDynaBean abean = new LazyDynaBean();
    	                 				abean.set("a0100", userView.getA0100());
    	                 				abean.set("posid",userView.getUserPosId());
    	                 				abean.set("name",rs.getString("name"));
    	                 				abean.set("plan_id", plan_id+"");
    	                 				abean.set("status", pstatus);
    	                 				if(!leaderid.equals(userView.getA0100()))
    	                 				{
    	                 	    			map1.put(plan_id+"", plan_id+"");
    	                 		         	list3.add(abean);
    	                 				}
    	                 			}
    	                 		}
    	                 		if("8".equals(flag))
    	                 		{
    	                 			if(map2.get(plan_id+"")==null)
    	                 			{
    	                 				LazyDynaBean abean = new LazyDynaBean();
    	                 				abean.set("name",rs.getString("name"));
    	                 				abean.set("plan_id", plan_id+"");
    	                 				map2.put(plan_id+"", plan_id+"");
    	                 				list4.add(abean);
    	                 				scorebuf.append(plan_id+",");
    	                 			}
    	                 		}
	    	    			}
        				}
        			
        			}
        		}
        		else if("2".equals(cycle))//季度
        		{
        			if("-1".equals(year)||year.equals(rs.getString("theyear")))
        			{
        				if("-1".equals(quarter)||quarter.equals(rs.getString("thequarter")))
        				{
        					if("-1".equals(status)||status.equalsIgnoreCase(rs.getString("status"))||("-2".equals(status)&&!"7".equals(rs.getString("status"))))
	    	    			{
        						String pstatus = rs.getString("status");
        						String level="";
        						String isSP="0";
		    					if(isHaveLeader)
		    					{
		    						/**当为非标准考核对象，从考核主体表查考核主体，，当是标准考核对象时，从考核主体表加上部门负责人的考核关系中查*/
		    						if("1".equals(market)&&!mainbodyMap.containsKey(userView.getA0100()+object_id))
		    							continue;
		    						if("0".equals(market))
		    						{
		    							/**考核关系*//*
		    							if(type==0)
		    							{*/
		    								if(!infomap.containsKey(userView.getA0100()+object_id)&&!infomap.containsKey(userView.getA0100()+leaderid))
		    									continue;
		    							/*}
		    							*//**汇报关系*//*
		    							else
		    							{
		    								if(!infomap.containsKey(userView.getDbname().toUpperCase()+userView.getA0100()))
		    									continue;
		    							}*/
		    						}
		    						if("1".equals(market))
		    						{
		    							LazyDynaBean bean =(LazyDynaBean)mainbodyMap.get(userView.getA0100()+object_id);
		    							if(bean!=null)
		    							{
		    								level=(String)bean.get("level");
		    								isSP=(String)bean.get("isSP");
		    							}
		    						}
		    						else
		    						{
		    							/*if(type==0)
		    							{*/
		    						    	LazyDynaBean bean=(LazyDynaBean)infomap.get(userView.getA0100()+object_id);
		    						    	if(bean==null)
		    						    		bean=(LazyDynaBean)infomap.get(userView.getA0100()+leaderid);
		    						    	if(bean!=null)
		    						    	{
		    							    	level=(String)bean.get("level");
		    							    	isSP=(String)bean.get("isSP");
		    						    	}
		    						}
		    						if(userView.getA0100().equals(leaderid))
		    							isSP="1";
		    					}
		    					else
		    					{
		    						if(!"4".equals(pstatus)&&!"6".equals(pstatus))
		    						{
		    							continue;
		    						}
		    						if(!mainbodyMap.containsKey(userView.getA0100()+object_id))
		    						{
		    							continue;
		    						}
		    						LazyDynaBean bean=(LazyDynaBean)mainbodyMap.get(userView.getA0100()+object_id);
		    						if(bean!=null)
		    						{
	    								level=(String)bean.get("level");
	    								isSP=(String)bean.get("isSP");
		    						}
		    					}
		    					
		    					boolean isSubmit=this.isSubmit(object_id, plan_id, dao,userView);
		    					int tzCount=this.getp04(object_id, rs.getString("plan_id"), dao);
		    					String spFlagDesc=AdminCode.getCodeName("23",spf);
		    					if("07".equals(spf))
		    						spFlagDesc+="/意见";
         	           	    	LazyDynaBean bean = new LazyDynaBean();
    	                 		String gradedesc=this.getDegreeDesc(plan_id, object_id);
    	                 		bean.set("gradedesc",gradedesc);
	                	    	bean.set("planid",rs.getString("plan_id"));
     	    	             	bean.set("name", rs.getString("name"));
     	        	         	String currsp="0";
     	        	         	String opt="0";
     							String flag="6";
     							String bs="0";
     							/**opt=0,不可操作，=1可操作，flag=查看，=7布置，=8打分*/
     							if(isHaveLeader)
     							{
     							String currappuser=rs.getString("currappuser");
     							/*if(currappuser!=null&&!currappuser.equals("")&&currappuser.equals(userView.getA0100()))
     								currsp="1";*/
     							/**暂停和分发状态*/
     							if("8".equals(pstatus))
     							{
     								if("true".equalsIgnoreCase(allowLeadAdjustCard))
     								{
     									if("01".equals(spf)&& "1".equals(isSP))
     									{
     										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
         									{
         										spFlagDesc=spFlagDesc+"(调整后)";
         									}
         									opt="1";
         									flag="7";
         									//if(userView.getA0100().equalsIgnoreCase(leaderid))
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
     									else if(!isSubmit&& "03".equals(spf)&& "true".equalsIgnoreCase(TargetAllowAdjustAfterApprove)&&userView.getA0100().equals(leaderid))
     									{
     										opt="0";
         									flag="6";
						    			    bs="1";
     									}
     									else if("07".equals(spf)&&userView.getA0100().equals(leaderid))
     									{
     										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
         									{
         										spFlagDesc=spFlagDesc+"(调整后)";
         									}
     										opt="1";
         									flag="7";
         									currsp="1";
     									}
     									else
     									{
     										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
         									{
         										spFlagDesc=spFlagDesc+"(调整后)";
         									}
     									}
     								}
     								else
     								{
     									if("01".equals(spf)&& "1".equals(isSP))
     									{
     										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
         									{
         										spFlagDesc=spFlagDesc+"(调整后)";
         									}
         									opt="1";
         									flag="7";
         									if(userView.getA0100().equalsIgnoreCase(leaderid))
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
     									else if(!isSubmit&& "03".equals(spf)&& "true".equalsIgnoreCase(TargetAllowAdjustAfterApprove)&&userView.getA0100().equals(leaderid))
     									{
     										opt="0";
         									flag="6";
						    			    bs="1";
     									}
     									else if("07".equals(spf)&&userView.getA0100().equals(leaderid))
     									{
     										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
         									{
         										spFlagDesc=spFlagDesc+"(调整后)";
         									}
     										opt="1";
         									flag="7";
         									currsp="1";
     									}
     									else
     									{
     										if("false".equalsIgnoreCase(taskAdjustNeedNew)&&tzCount>0)
         									{
         										spFlagDesc=spFlagDesc+"(调整后)";
         									}
     									}
     								}
     							}
     							else if("5".equals(pstatus))
     							{
     								opt="0";
				    				flag="6";
     							}
     							else
     							{
     								if(eMap==null)
     								{
     									eMap=this.getPerMainBodyData(userView,plan_id);
     								}
     								LazyDynaBean abean = (LazyDynaBean)eMap.get(plan_id+object_id+userView.getA0100());
     								
     								if(abean!=null)
     								{
     									String scoreStatus=(String)abean.get("status");
     						    		if(("4".equals(pstatus)|| "6".equals(pstatus))&&("03".equals(spf)|| "true".equalsIgnoreCase(NoApproveTargetCanScore))&&!"2".equals(scoreStatus))
    	             	            	{
     							    		opt="1";
					    		    		flag="8";
    	             		            }
     						    		else
     							    	{
     							      		opt="0";
					    		    		flag="6";
     							    	}
     								}
     						    	else
     						    	{
     						    		opt="0";
				    		    		flag="6";	
     						    	}
     							}
     							}
     							else
    		    				{
     								boolean sub = this.isSubmit(object_id, plan_id, dao, userView);
    		    					if(!"5".equals(pstatus)&&!sub)
    		    					{
    		    						opt="1";
    		    						flag="8";
    		    					}
    		    					else
    		    					{
    		    						opt="0";
    		    						flag="6";
    		    					}
    		    				}
     							bean.set("bs", bs);
     							bean.set("opt",opt);
     							bean.set("flag", flag);
     							bean.set("currsp", currsp);
     							bean.set("level", level);
     	    	            	String b0110 = AdminCode.getCodeName("UN",rs.getString("object_id"));
    	                    	if(b0110==null|| "".equals(b0110))
    	                     		b0110=AdminCode.getCodeName("UM",rs.getString("object_id"));
                	        	bean.set("b0110",b0110);
                	        	bean.set("a0100",rs.getString("object_id"));
    	                 		bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year")+" "+AdminCode.getCodeName("12",rs.getString("thequarter")));
    	                 		bean.set("sp_flag",spFlagDesc);
    	                 		bean.set("spf",spf);
    	                 		if(/*spf.equals("01")||spf.equals("07")||spf.equals("02")*/"8".equals(pstatus))
    	                 		{
    	                 			if(flag01)
    	                 		    	list.add(bean);
    	                 		}
    	                 		if("03".equals(spf))
    	                 		{
    	                 			if(flag01)
    	                 			{
    	                 		         	list2.add(bean);
    	                 			
    	                 			}
    	                 		}
    	                 		if(("1".equals(currsp)|| "03".equals(spf))&&!"4".equals(pstatus)&&!"6".equals(pstatus)&& "1".equals(isSP))
    	                 		{
    	                 			if(map1.get(plan_id+"")==null)
    	                 			{
    	                 				LazyDynaBean abean = new LazyDynaBean();
    	                 				abean.set("a0100", userView.getA0100());
    	                 				abean.set("posid",userView.getUserPosId());
    	                 				abean.set("name",rs.getString("name"));
    	                 				abean.set("plan_id", plan_id+"");
    	                 				abean.set("status", pstatus);
    	                 				if(!leaderid.equals(userView.getA0100()))
    	                 				{
    	                 	    			map1.put(plan_id+"", plan_id+"");
    	                 		         	list3.add(abean);
    	                 				}
    	                 			}
    	                 		}
    	                 		if("8".equals(flag))
    	                 		{
    	                 			if(map2.get(plan_id+"")==null)
    	                 			{
    	                 				LazyDynaBean abean = new LazyDynaBean();
    	                 				abean.set("name",rs.getString("name"));
    	                 				abean.set("plan_id", plan_id+"");
    	                 				map2.put(plan_id+"", plan_id+"");
    	                 				list4.add(abean);
    	                 				scorebuf.append(plan_id+",");
    	                 			}
    	                 		}
	    	    			}
        				}
        			}
        		}
    		}
    		returnMap.put("list", list);
    		returnMap.put("list2", list2);//本人考核指标完成情况
    		returnMap.put("list3", list3);//员工KPI指标审批
    		returnMap.put("list4", list4);
    		if(scorebuf.length()>0)
    			scorebuf.setLength(scorebuf.length()-1);
    		returnMap.put("scoreplan", scorebuf.toString());
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return returnMap;
    }
    /**
     * 取考核等级
     * @param degreeid
     * @return
     */
    public String getDegreeDesc(int plan_id,String object_id)
    {
    	String desc = "";
        try
         {
        	//DBMetaModel dbmodel=new DBMetaModel(this.conn);
			//dbmodel.reloadTableModel();
        	String tableName = "per_result_"+plan_id;
        	//DBMetaModel dbmodel=new DBMetaModel(this.conn);
			//dbmodel.reloadTableModel(tableName);
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
    /**
     * 取得评估期年列表
     * @param a0100
     * @return
     */
    public ArrayList getYearList(String a0100)
    {
    	ArrayList list = new ArrayList();
    	try
    	{
    		map= new HashMap();
    		StringBuffer buf = new StringBuffer();
    		buf.append(" select * from (");
    		buf.append(" (select theyear from per_plan where plan_id in (");
    		buf.append(" select plan_id from per_mainbody where UPPER(mainbody_id)='"+a0100.toUpperCase()+"')");
    		buf.append(" and (status='3'  or status='4' or status='7')");
    		buf.append(" and method='2' and cycle<>'7' and object_type='1')");
    		buf.append(" union ");
    		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
    		{
    			buf.append(" (select to_char("+Sql_switcher.year("start_date")+",'YYYY') as theyear from per_plan where plan_id in (");
    		}
    		else
    		{
        		buf.append(" (select "+Sql_switcher.year("start_date")+" as theyear from per_plan where plan_id in (");
    		}
      		buf.append(" select plan_id from per_mainbody where UPPER(mainbody_id)='"+a0100.toUpperCase()+"')");
      		buf.append(" and (status='4'  or status='5' or status='6')");
      		buf.append(" and method='2' and cycle='7' and object_type='1') ");
      		buf.append(" union ");
      		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
      		{
      			buf.append(" (select to_char("+Sql_switcher.year("start_date")+",'YYYY') as theyear from per_plan where plan_id in (");
      		}
      		else{
    	    	buf.append(" (select "+Sql_switcher.year("end_date")+" as theyear from per_plan where plan_id in (");
      		}
      		buf.append(" select plan_id from per_mainbody where UPPER(mainbody_id)='"+a0100.toUpperCase()+"')");
      		buf.append(" and (status='4'  or status='5' or status='6')");
      		buf.append(" and method='2' and cycle='7' and object_type='1')) temp order by theyear ");
      		
            ContentDAO dao = new ContentDAO(this.conn);
            RowSet rs = null;
            rs = dao.search(buf.toString());
            list.add(new CommonData("-1",ResourceFactory.getProperty("label.all")));
            while(rs.next())
            {
            	
            	if(map.get(rs.getString("theyear"))==null)
            	{
            	   list.add(new CommonData(rs.getString("theyear"),rs.getString("theyear")));
            	   map.put(rs.getString("theyear"),rs.getString("theyear"));
                }
            }	
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return list;
    }
    /**
     * 取得评估期季度列表
     * @param a0100
     * @param year
     * @return
     */
    public ArrayList getQuarterList(String a0100,String year)
    {
    	ArrayList list = new ArrayList();
    	try
    	{
    		StringBuffer buf = new StringBuffer();
    		buf.append("select codeitemid,codeitemdesc from codeitem where codesetid='12'");
    		ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rs = dao.search(buf.toString());
    	    list.add(new CommonData("-1",ResourceFactory.getProperty("label.all")));
    		while(rs.next())
    		{
    			list.add(new CommonData(rs.getString("codeitemid"),rs.getString("codeitemdesc")));
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return list;
    }
    public ArrayList getMonthList(String year,String quarter,String a0100)
    {
    	ArrayList list = new ArrayList();
    	try
    	{
    		StringBuffer buf = new StringBuffer();
    		buf.append("select codeitemid,codeitemdesc from codeitem where codesetid='13'");
    		ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rs = dao.search(buf.toString());
    	    list.add(new CommonData("-1",ResourceFactory.getProperty("label.all")));
    		while(rs.next())
    		{
    			list.add(new CommonData(rs.getString("codeitemid"),rs.getString("codeitemdesc")));
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return list;
    }
    public ArrayList getStatusList()
    {
    	ArrayList list = new ArrayList();
    	try
    	{
    		list.add(new CommonData("-1",ResourceFactory.getProperty("label.all")));
    		list.add(new CommonData("-2","执行中"));
    		list.add(new CommonData("8",ResourceFactory.getProperty("org.performance.Published")));
    		list.add(new CommonData("4",ResourceFactory.getProperty("org.performance.start")));
    		list.add(new CommonData("5",ResourceFactory.getProperty("label.commend.stop")));
    		list.add(new CommonData("6",ResourceFactory.getProperty("org.performance.pg")));
    		list.add(new CommonData("7",ResourceFactory.getProperty("org.performance.end")));
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return list;
    }
   /**
    * 取每个部门对应的部门负责人
    * @return
    */
    public HashMap getOrgLeader(String planidStr)
    {
    	HashMap map = new HashMap();
        try
    	{
    		StringBuffer buf = new StringBuffer();
    		buf.append("select object_id,plan_id,mainbody_id,u.e01a1 from per_mainbody ,usra01 u where ");
    		buf.append(" body_id='-1' and plan_id in (select plan_id from per_plan pp where ");
    		buf.append(" (pp.object_type='1' or pp.object_type='3' or pp.object_type='4') and ");
    		buf.append(" (pp.status='5'  or pp.status='4' or pp.status='6' or pp.status='7' or pp.status='8'))");
    		buf.append(" and per_mainbody.mainbody_id=u.a0100 ");
    		if(StringUtils.isNotEmpty(planidStr)) {
    			buf.append("and plan_id in ("+planidStr+") ");
    		}
    		ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rs = dao.search(buf.toString());
    		while(rs.next())
    		{
    			String object_id = rs.getString("object_id");
    			String plan_id = rs.getString("plan_id");
    			String mainbody_id = rs.getString("mainbody_id");
    			String e01a1=rs.getString("e01a1")==null?"":rs.getString("e01a1");
    			LazyDynaBean bean = new LazyDynaBean();
    			bean.set("a0100",mainbody_id);
    			bean.set("e01a1",e01a1);
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
    public int getFirstMonthInQuarter(String quarter)
    {
    	int month=1;
    	try
    	{
    		if("1".equals(quarter)|| "01".equals(quarter))
    		{
    			month=1;
    		}else if("2".equals(quarter)|| "02".equals(quarter))
    		{
    			month=4;
    		}if("3".equals(quarter)|| "03".equals(quarter))
    		{
    			month=7;
    		}else if("4".equals(quarter)|| "04".equals(quarter))
    		{
    			month=10;
    		}
    		/*int a_quarter = Integer.parseInt(quarter);
    		switch(a_quarter)
    		{
    		case 1:
    			month=1;
    			break;
    		case 2:
    			month=4;
    			break;
    		case 3:
    			month=7;
    			break;
    		case 4:
    			month=10;
    			break;
    		}*/
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return month;
    }
    /**
     * 取考核计划下所有的考核对象对应的考核主体
     * @param plan_id
     * @return
     */
    public HashMap getMainbodyBean(String plan_id,String market,String leaderSP)
    {
  	  HashMap map = new HashMap();
  	  try
  	  {
  		  String sql = "select a.mainbody_id,a.object_id,";
  		  if(Sql_switcher.searchDbServer()==Constant.ORACEL)
  				  sql+="b.level_o ";
  		  else
  			  sql+="b.level ";
  		  sql+=" from per_mainbody a,per_mainbodyset b where plan_id="+plan_id;
  		  sql+=" and a.body_id=b.body_id ";
  		  ContentDAO dao = new ContentDAO(this.conn);
  		  RowSet rs = dao.search(sql);
  		  while(rs.next())
  		  {
  			  LazyDynaBean bean = new LazyDynaBean();
  			  String level="";
  			  if(Sql_switcher.searchDbServer()==Constant.ORACEL)
  			  {
  				  bean.set("level",rs.getString("level_o")==null?"1000":rs.getString("level_o"));
  				  level=rs.getString("level_o")==null?"1000":rs.getString("level_o");
  			  }
  			  else
  			  {
  				  bean.set("level",rs.getString("level")==null?"1000":rs.getString("level"));
  				  level=rs.getString("level")==null?"1000":rs.getString("level");
  			  }
  			  if("1".equals(market)/*&&leaderSP.equalsIgnoreCase("true")*/&&("1".equals(level)|| "0".equals(level)|| "-1".equals(level)|| "-2".equals(level)))
  			     bean.set("isSP","1");
  			  else
  			  {
  				bean.set("isSP","0");
  			  }
  			  map.put(rs.getString("mainbody_id")+rs.getString("object_id"), bean);
  		  }
  	  }
  	  catch(Exception e)
  	  {
  		  e.printStackTrace();
  	  }
  	  return map;
    }
    /**
     * 取考核计划下所有的考核对象对应的考核主体
     * @param plan_id
     * @return
     */
    public HashMap getMainbodyBean(String planidStr, ContentDAO dao)
    {
  	  HashMap map = new HashMap();
  	  try
  	  {
  		  String sql = "select a.mainbody_id,a.object_id,c.kh_relations,a.plan_id,";
  		  if(Sql_switcher.searchDbServer()==Constant.ORACEL)
  				  sql+="b.level_o ";
  		  else
  			  sql+="b.level ";
  		  sql+=" from per_mainbody a,per_mainbodyset b ,per_object c ";
  		  sql+=" where a.plan_id in ("+planidStr+")";
  		  sql+=" and a.body_id=b.body_id ";
  		  sql+=" and a.object_id=c.object_id and a.plan_id=c.plan_id ";
  		  RowSet rs = dao.search(sql);
  		  while(rs.next())
  		  {
  			  LazyDynaBean bean = new LazyDynaBean();
  			  String level="";
  			  if(Sql_switcher.searchDbServer()==Constant.ORACEL)
  			  {
  				  bean.set("level",rs.getString("level_o")==null?"1000":rs.getString("level_o"));
  				  level=rs.getString("level_o")==null?"1000":rs.getString("level_o");
  			  }
  			  else
  			  {
  				  bean.set("level",rs.getString("level")==null?"1000":rs.getString("level"));
  				  level=rs.getString("level")==null?"1000":rs.getString("level");
  			  }
  			  String market = rs.getString("kh_relations");
  			  if(market==null)
				market="0";
  			  if("1".equals(market)/*&&leaderSP.equalsIgnoreCase("true")*/&&("1".equals(level)|| "0".equals(level)|| "-1".equals(level)|| "-2".equals(level)))
  			     bean.set("isSP","1");
  			  else
  			  {
  				bean.set("isSP","0");
  			  }
  			  map.put(rs.getString("plan_id")+rs.getString("mainbody_id")+rs.getString("object_id"), bean);
  		  }
  	  }
  	  catch(Exception e)
  	  {
  		  e.printStackTrace();
  	  }
  	  return map;
    }
    /**
     * 判断考核对象是否进行过目标调整
     * @param objectid
     * @param planid
     * @param dao
     * @return
     */
    public HashMap getp04(String planidstr,ContentDAO dao)
	{
    	HashMap map = new HashMap();
		int i=0;
		try
		{
			String sql="select b0110,plan_id,count(b0110) total from p04 where plan_id in ("+planidstr+")  and state=-1 group by b0110,plan_id";
		    RowSet rs =null;
		    rs=dao.search(sql);
		    while(rs.next())
		    {
		    	map.put(rs.getString("plan_id")+"_"+rs.getString("b0110"), rs.getInt("total"));
		    }
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
    /**
     * 判断考核对象是否进行过目标调整
     * @param objectid
     * @param planid
     * @param dao
     * @return
     */
    public int getp04(String objectid,String planid,ContentDAO dao)
   	{
   		int i=0;
   		try
   		{
   			String sql="select count(*) total from p04 where plan_id="+planid+"  and b0110='"+objectid+"'  and state=-1";
   		    RowSet rs =null;
   		    rs=dao.search(sql);
   		    while(rs.next())
   		    {
   		    	i=rs.getInt("total");
   		    }
   		
   		}
   		catch(Exception e)
   		{
   			e.printStackTrace();
   		}
   		return i;
   	}
    public HashMap getPerMainBodyData(UserView userview,int plan_id)
	{
		HashMap map = new HashMap();
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append("select a.*,");
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				buf.append("b.level_o as lv ");
			else
				buf.append("b.level as lv ");
			buf.append(" from per_mainbody a left join per_mainbodyset b on a.body_id=b.body_id where mainbody_id='"+userview.getA0100()+"' and plan_id='"+plan_id+"'");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(buf.toString());
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("status",rs.getString("status")==null?"1":rs.getString("status"));
				bean.set("object_id", rs.getString("object_id"));
				bean.set("mainbody_id",rs.getString("mainbody_id"));
				bean.set("body_id", rs.getString("body_id"));
				bean.set("level",rs.getString("lv"));
				map.put(rs.getString("plan_id")+rs.getString("object_id")+rs.getString("mainbody_id"), bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
    /**
     * 判断是否有已提交打分的考核主体
     * @param object_id
     * @param plan_id
     * @param dao
     * @return
     */
    public boolean isSubmit(String object_id,int plan_id,ContentDAO dao,UserView view)
    {
    	boolean flag = false;
    	try
    	{
    		StringBuffer buf = new StringBuffer();
    		buf.append("select * from per_mainbody where ");
    		buf.append(" plan_id="+plan_id);
    		buf.append(" and ");
    		buf.append(" object_id='"+object_id+"' and status=2");
    		buf.append(" and mainbody_id='"+view.getA0100()+"'");
    		RowSet rs = dao.search(buf.toString());
    		while(rs.next())
    		{
    			flag=true;
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return flag;
    }
    public boolean isSubmit(String object_id,int plan_id,ContentDAO dao)
    {
    	boolean flag = false;
    	try
    	{
    		StringBuffer buf = new StringBuffer();
    		buf.append("select * from per_mainbody where ");
    		buf.append(" plan_id="+plan_id);
    		buf.append(" and ");
    		buf.append(" object_id='"+object_id+"' and (status=2 or status=1)");
    		RowSet rs = dao.search(buf.toString());
    		while(rs.next())
    		{
    			flag=true;
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return flag;
    }
}
