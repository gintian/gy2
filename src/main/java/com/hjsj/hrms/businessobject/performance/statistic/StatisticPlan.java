package com.hjsj.hrms.businessobject.performance.statistic;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * 本业务类用于对绩效登记表的操作
 * <p>Title:StatisticPlan.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Dec 21, 2006 2:09:13 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class StatisticPlan {
  private Connection conn;
  private UserView userView;
  public StatisticPlan()
  {
	  
  }
  public StatisticPlan(UserView userView,Connection conn)
  {
	  this.conn=conn;
	  this.userView=userView;
  }
  /**
   * 得到一个计划下的登记表
   * @param plan_id
   * @return
   * @throws GeneralException
   */
  public ArrayList getRnameListFromPlanID(String plan_id)throws GeneralException
  {
	  StringBuffer sql=new StringBuffer();	   
	  ArrayList list =new ArrayList();
	  CommonData vo=null;
	  ContentDAO dao=new ContentDAO(this.conn);
	  String template_id=getTemplate_Id(plan_id);
	  LoadXml loadxml = new LoadXml(conn, plan_id);
	  Hashtable params = loadxml.getDegreeWhole();
	  String showBackTables = (String)params.get("ShowBackTables");
	  
	  if(template_id!=null&&template_id.length()>0)
	  {
		 String tabids=getTab_Id(template_id);
		 if(tabids!=null&&tabids.length()>0)
		 {
			 try
			  {
				  sql=new StringBuffer();		  
				  sql.append("select tabid,name from rname");
				  sql.append(" where UPPER(FlagA)='P' and tabid in ("+tabids+")");
				  RowSet rs=null;
				  rs=dao.search(sql.toString());
				  while(rs.next())
				  {
					  if(this.userView!=null&&!this.userView.isSuper_admin()&&!this.userView.isHaveResource(IResourceConstant.CARD, rs.getString("tabid"))){
						  continue;
					  } else if(showBackTables !=null && !"".equals(showBackTables)) {
							String[] showTables = showBackTables.split(",");
							for(int i=0;i<showTables.length;i++) {
								if(showTables[i].equals(rs.getString("tabid"))){
									 vo=new CommonData();
									  vo.setDataName(rs.getString("name"));
									  vo.setDataValue("P"+rs.getString("tabid"));
									  list.add(vo);
									  break;
								}
							}
					  }
					 
				  }
				  
			  }catch(Exception e)
			  {
				  throw GeneralExceptionHandler.Handle(e);
			  }
		 }		  
	  }
	  
	  return list;
  }
  /**
   * 通过计划编号得到模板编号
   * @param plan_id
   * @return
   * @throws GeneralException
   */
  public String getTemplate_Id(String plan_id)throws GeneralException
  {
	  StringBuffer sql=new StringBuffer();
	  sql.append("select template_id from per_plan where plan_id="+ plan_id);	 
	  ContentDAO dao=new ContentDAO(this.conn);
	  String template_id="";
	  try
	  {
		  
		  RowSet rs=null;
		  rs=dao.search(sql.toString());
		  if(rs.next())
		  {
			  template_id=rs.getString("template_id");			  
		  }
	  }catch(Exception e)
	  {
		  throw GeneralExceptionHandler.Handle(e);
	  }
	  return template_id;
  }
  /**
   * 通过模板编号得到登记表编号
   * @param plan_id
   * @return
   * @throws GeneralException
   */
  public String getTab_Id(String template_id)throws GeneralException
  {
	  StringBuffer sql=new StringBuffer();
	  sql.append("select tabids from per_template where template_id='"+ template_id+"'");	 
	  ContentDAO dao=new ContentDAO(this.conn);
	  String tab_id="";
	  try
	  {
		  
		  RowSet rs=null;
		  rs=dao.search(sql.toString());
		  if(rs.next())
		  {
			  tab_id=Sql_switcher.readMemo(rs,"tabids");;			  
		  }
	  }catch(Exception e)
	  {
		  throw GeneralExceptionHandler.Handle(e);
	  }
	  return tab_id;
  }
  /**
   * 通过登记表的id得到计划的id
   * @param tabid
   * @return
   */
  public String getPlanIDs(String tabid)
  {
	  String sql="select template_id from per_template where tabids like '%"+tabid+"%'";
	  ContentDAO dao=new ContentDAO(this.conn);
	  String template_id="";
	  String planid="";
	  try
	  {
		  RowSet rs=dao.search(sql);
		  if(rs.next())
		  {
			  template_id=rs.getString("template_id");
		  }
	  }catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  if(template_id!=null&&template_id.length()>=0)
	  {
		  sql="select plan_id from per_plan where template_id='"+template_id+"'";
		  try
		  {
			  RowSet rs=dao.search(sql);
			  if(rs.next())
			  {
				  planid=rs.getString("plan_id");
			  }
		  }catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  
		  
	  }
	  return planid;
  }
  public String getPER_RESULT_TableName(String planid)
  {
	  return "PER_RESULT_"+planid;
  }
  /**
   * 绩效登记表处理
   * @param cexpress
   * @return
   */
  public HashMap getFiledSet(String cexpress)
  {
	  HashMap h_map=new HashMap();
	  /*<FIELDNAME></FIELDNAME>: 字段名: 指标：C_指标编号
          项目：T_项目编号
        <FUNC></FUNC>: 函数：最大MAX,最小MIN,平均AVG,个数COUNT
        <SCOPE></SCOPE>: 函数范围: 1: 全部，2: 在分组内
        <SCORETYPE>分值类型: 主体类别ID, 团队负责人为-1</SCORETYPE>
        */
	  if(cexpress==null||cexpress.length()<=0)
	  {
		  return h_map;
	  }else
	  {
			int i=-1;
	    	int s=-1;
	    	int e=-1;
	    	if(cexpress.indexOf("<FIELDNAME>")!=-1)
	    	{
	    		i=cexpress.indexOf("<FIELDNAME>");
	    		s=cexpress.indexOf(">",i);
	    		e=cexpress.indexOf("</FIELDNAME>");
	    	}
	    	String fileset=cexpress.toString().substring(s+1,e);	    	
	    	h_map.put("FIELDNAME",fileset);
	    	fileset="";
	    	if(cexpress.indexOf("<FUNC>")!=-1)
	    	{
	    		i=cexpress.indexOf("<FUNC>");
	    		s=cexpress.indexOf(">",i);
	    		e=cexpress.indexOf("</FUNC>");
	    		fileset=cexpress.toString().substring(s+1,e);	 
	    	}
	    	h_map.put("FUNC",fileset);	
	    	fileset="";
	    	if(cexpress.indexOf("<SCOPE>")!=-1)
	    	{
	    		i=cexpress.indexOf("<SCOPE>");
	    		s=cexpress.indexOf(">",i);
	    		e=cexpress.indexOf("</SCOPE>");
	    		fileset=cexpress.toString().substring(s+1,e);
	    	}	    	
	    	h_map.put("SCOPE",fileset);
	    	fileset="";
	    	if(cexpress.indexOf("<SETNAME>")!=-1)
	    	{
	    		i=cexpress.indexOf("<SETNAME>");
	    		s=cexpress.indexOf(">",i);
	    		e=cexpress.indexOf("</SETNAME>");
	    		fileset=cexpress.toString().substring(s+1,e);
	    	}
	    	h_map.put("SETNAME",fileset);
	    	fileset="";
	    	if(cexpress.indexOf("<SCORETYPE>")!=-1)
	    	{
	    		i=cexpress.indexOf("<SCORETYPE>");
	    		s=cexpress.indexOf(">",i);
	    		e=cexpress.indexOf("</SCORETYPE>");
	    		fileset=cexpress.toString().substring(s+1,e);
	    	}
	    	h_map.put("SCORETYPE",fileset);
	    	fileset="";
	    	if(cexpress.indexOf("<EXT_FLAG>")!=-1)
	    	{
	    		i=cexpress.indexOf("<EXT_FLAG>");
	    		s=cexpress.indexOf(">",i);
	    		e=cexpress.indexOf("</EXT_FLAG>");
	    		fileset=cexpress.toString().substring(s+1,e);
	    	}
	    	h_map.put("EXT_FLAG",fileset);
	    	fileset="";
	    	//<RELATE_PLAN>关联计划名</RELATE_PLAN>
	    	if(cexpress.indexOf("<RELATE_PLAN>")!=-1)
	    	{
	    		i=cexpress.indexOf("<RELATE_PLAN>");
	    		s=cexpress.indexOf(">",i);
	    		e=cexpress.indexOf("</RELATE_PLAN>");
	    		fileset=cexpress.toString().substring(s+1,e);
	    	}
	    	h_map.put("RELATE_PLAN",fileset);
	  }
	  return h_map;
  }
  
  
  public ArrayList khResultField(ArrayList fieldlist,String planId)
  {
	    FieldItem fielditem=new FieldItem("A0101","A0101");	    
		fielditem.setItemdesc("考核对象名称");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("A");
		fielditem.setItemlength(30);
		fielditem.setDecimalwidth(0);
		fieldlist.add(fielditem);
		// 考核指标
		getT_x(planId,fieldlist);
		getC_x(planId,fieldlist);
	    // 计算总分
		fielditem=new FieldItem("original_score","original_score");	    
		fielditem.setItemdesc("计算总分");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("N");
		fielditem.setItemlength(12);
		fielditem.setDecimalwidth(6);
		fieldlist.add(fielditem);

	    // 修正总分/总分
		fielditem=new FieldItem("score","score");	    
		fielditem.setItemdesc("总分");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("N");
		fielditem.setItemlength(12);
		fielditem.setDecimalwidth(6);
		fieldlist.add(fielditem);
		// 修正分值per_result_correct.score
		fielditem=new FieldItem("per_result_correct","per_result_correct.score");	    
		fielditem.setItemdesc("修正分值");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("N");
		fielditem.setItemlength(12);
		fielditem.setDecimalwidth(6);
		fieldlist.add(fielditem);
		// 修正原因per_result_correct.correct_reason
		fielditem=new FieldItem("per_result_correct","correct_reason");	    
		fielditem.setItemdesc("修正原因");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("N");
		fielditem.setItemlength(12);
		fielditem.setDecimalwidth(6);
		fieldlist.add(fielditem);	   
	    // 分组平均分
		fielditem=new FieldItem("exs_grpavg","exs_grpavg");	    
		fielditem.setItemdesc("组内平均分");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("N");
		fielditem.setItemlength(12);
		fielditem.setDecimalwidth(6);
		fieldlist.add(fielditem);	
	    // 组最高分
		fielditem=new FieldItem("exs_grpmax","exs_grpmax");	    
		fielditem.setItemdesc("组内最高分");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("N");
		fielditem.setItemlength(12);
		fielditem.setDecimalwidth(6);
		fieldlist.add(fielditem);
	    // 分组最低分
		fielditem=new FieldItem("exs_grpmin","exs_grpmin");	    
		fielditem.setItemdesc("组内最低分");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("N");
		fielditem.setItemlength(12);
		fielditem.setDecimalwidth(6);
		fieldlist.add(fielditem); 
	    // 组人数
		fielditem=new FieldItem("ex_grpnum","ex_grpnum");	    
		fielditem.setItemdesc("组内对象数");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("N");
		fielditem.setItemlength(12);
		fielditem.setDecimalwidth(6);
		fieldlist.add(fielditem); 
	    // 排名
		fielditem=new FieldItem("Ordering","Ordering");	    
		fielditem.setItemdesc("排名");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("N");
		fielditem.setItemlength(12);
		fielditem.setDecimalwidth(6);
		fieldlist.add(fielditem); 	   
	   /* //部门排名
	    if (planId!=null&&Integer.parseInt(planId <> 0) then
	    begin
	      if DBOper.FieldExist(getPerResultTabName(planId),E0122Ordering) then
	      begin
	        m_fldmenu:=TFldMenu.Create;
	        m_fldmenu.cSetName := '';
	        m_fldmenu.cFldName := E0122Ordering;
	        m_fldmenu.cHz := '部门排名';
	        m_fldmenu.cCodeid := '';
	        m_fldmenu.cFldtype := 'N';
	        list.Add(m_fldmenu);
	      end;
	      if DBOper.FieldExist(getPerResultTabName(planId),E0122GrpNum) then
	      begin
	        m_fldmenu:=TFldMenu.Create;
	        m_fldmenu.cSetName := '';
	        m_fldmenu.cFldName := E0122GrpNum;
	        m_fldmenu.cHz := '部门人数';
	        m_fldmenu.cCodeid := '';
	        m_fldmenu.cFldtype := 'N';
	        list.Add(m_fldmenu);
	      end;
	    end;*/
        //绩效系数
		fielditem=new FieldItem("exx_object","exx_object");	    
		fielditem.setItemdesc("绩效系数");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("N");
		fielditem.setItemlength(12);
		fielditem.setDecimalwidth(6);
		fieldlist.add(fielditem); 

	    // 考核等级
		fielditem=new FieldItem("resultdesc","resultdesc");	    
		fielditem.setItemdesc("考核等级");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("N");
		fielditem.setItemlength(12);
		fielditem.setDecimalwidth(6);
		fieldlist.add(fielditem); 	   
	    //考核对象类别
		fielditem=new FieldItem("body_id","body_id");	    
		fielditem.setItemdesc("对象类别");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("A");
		fielditem.setItemlength(50);		
		fieldlist.add(fielditem); 
	    
	    
	      // 考核期间
		fielditem=new FieldItem("per_plan","cycle");	    
		fielditem.setItemdesc("考核期间");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("A");
		fielditem.setItemlength(50);		
		fieldlist.add(fielditem);
	    // 评语
		fielditem=new FieldItem("appraise","appraise");	    
		fielditem.setItemdesc("评语");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("M");		
		fieldlist.add(fielditem); 

	    // 个人总结summarize
		fielditem=new FieldItem("summarize","summarize");	    
		fielditem.setItemdesc("个人总结");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("M");		
		fieldlist.add(fielditem);	   
	    
	      //总体评价
	    fielditem=new FieldItem("per_mainbody","whole_grade_id");	    
		fielditem.setItemdesc("总体评价");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("A");
		fielditem.setItemlength(500);		
		fieldlist.add(fielditem);
	      // 员工评价per_mainbody.description
		fielditem=new FieldItem("per_mainbody","description");	    
		fielditem.setItemdesc("总体评价描述");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("M");
		fieldlist.add(fielditem);
	      // 面谈记录 per_interview.interview
		fielditem=new FieldItem("per_interview","interview");	    
		fielditem.setItemdesc("面谈记录");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("M");
		fieldlist.add(fielditem);
	     // 年中回顾
		fielditem=new FieldItem("per_object","summarizes");	    
		fielditem.setItemdesc("年中回顾");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("M");
		fieldlist.add(fielditem);	   
	    return fieldlist;

  }
  /** 取所有项目号 */
	public ArrayList getT_x(String planId,ArrayList fieldlist)
	{

		ContentDAO dao = new ContentDAO(this.conn);
		RowSet frowset=null;
		String item_id="";
		String itemdesc="";
		String sql="";
		try
		{
			frowset = dao.search("select distinct item_id,itemdesc from per_template_item where Template_id=" + "(select template_id from per_plan where plan_id=" + planId + ")");
			while (frowset.next())
			{
				item_id=frowset.getString("item_id");
				itemdesc=frowset.getString("itemdesc");
				item_id="T_" + item_id;
            	FieldItem fielditem=new FieldItem(item_id,item_id);
            	fielditem.setItemtype("N");
            	fielditem.setItemlength(12);
            	fielditem.setDecimalwidth(6);
            	fielditem.setItemdesc(itemdesc);
            	fielditem.setCodesetid("0");
            	fieldlist.add(fielditem);
				/*sql="select pointname from per_point where point_id='"+item_id+"'";
				List rs=ExecuteSQL.executeMyQuery(sql, conn);
				if(!rs.isEmpty()&&rs.size()>0)
				{
					
					LazyDynaBean rec=(LazyDynaBean)rs.get(0);
                    if(rec!=null&&rec.get("pointname")!=null&&rec.get("pointname").toString().length()>0)
                    {
                    	item_id="T_" + item_id;
                    	FieldItem fielditem=new FieldItem(item_id,item_id);
                    	fielditem.setItemtype("N");
                    	fielditem.setItemlength(12);
                    	fielditem.setDecimalwidth(6);
                    	fielditem.setItemdesc(rec.get("pointname").toString());
                    	fielditem.setCodesetid("0");
                    	fieldlist.add(fielditem);
                    }
				}*/
			}
			
		} catch (SQLException e)
		{
			e.printStackTrace();
			
		}finally{
			if(frowset!=null) {
                try {
                    frowset.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		return fieldlist;
	}
	/**
	 * 得到绩效对应计划指标
	 * @param planId
	 * @param fieldlist
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getC_x(String planId,ArrayList fieldlist) 
	{

		BatchGradeBo bo = new BatchGradeBo(this.conn, planId);

		ArrayList list = new ArrayList();
		// 解决排列顺序问题
		ArrayList seqList = new ArrayList();
		ArrayList tempPointList = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet frowset=null;
		try
		{
			//per_template_point 项目模板指标
			//per_point 指标表
			String templateID = "";			
            StringBuffer sql=new StringBuffer();
            sql.append("select point_id,pointname from per_point where point_id in(");
            sql.append("select point_id from per_template_point where item_id in(");
            sql.append("select distinct item_id from per_template_item where ");
            sql.append("Template_id=(select template_id from per_plan where plan_id="+planId+"");
            sql.append(")))");
			frowset = dao.search(sql.toString());
			String point_id="";
			String pointname="";
			while (frowset.next())
			{
				
				point_id = frowset.getString("point_id");
				pointname = frowset.getString("pointname");				
				
				point_id="C_" + point_id;
            	FieldItem fielditem=new FieldItem(point_id,point_id);
            	fielditem.setItemtype("N");
            	fielditem.setItemlength(12);
            	fielditem.setDecimalwidth(6);
            	fielditem.setItemdesc(pointname);
            	fielditem.setCodesetid("0");
            	fieldlist.add(fielditem);
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
			
		}finally{
			if(frowset!=null) {
                try {
                    frowset.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		return list;
	}
}
