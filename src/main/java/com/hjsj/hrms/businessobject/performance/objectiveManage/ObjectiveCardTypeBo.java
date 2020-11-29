package com.hjsj.hrms.businessobject.performance.objectiveManage;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:ObjectiveCardTypeBo.java</p>
 * <p>Description>:ObjectiveCardTypeBo.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2011-4-26 下午03:56:27</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class ObjectiveCardTypeBo 
{
	private Connection conn;
	private UserView view;
	
	public ObjectiveCardTypeBo(Connection conn)
	{
		this.conn = conn;		
	}
	public ObjectiveCardTypeBo(Connection conn,UserView view)
	{
		this.conn = conn;
		this.view=view;
	}

	/**
	 * 获得 =2"MBO目标总结考评进度统计表" 需页面展现的计划列表 
	 */
	public ArrayList getMBOTableList(String orgCode , String strsql)
	{
		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		RowSet rs = null;
		try
		{															
			StringBuffer buf=new StringBuffer();
			buf.append(" select plan_id,name,b0110,theyear from per_plan where 1=1 ");			
//			buf.append( getUserViewPrivWhere() );                   // 获得登录用户的控制权限
				
			String value = "";
			if(!"-1".equals(orgCode))
	    	{		    	
		    	value = orgCode.substring(2);		    			    		
	    	}
			StringBuffer whlSql = new StringBuffer();
			whlSql.append(" and Method=2 and (status=5 or status=8) ");
			whlSql.append(" and ((B0110 = 'HJSJ' or B0110 = '' ) ");
			whlSql.append(" or (B0110 like '" + value + "%' ");
			whlSql.append(")) ");
			
			if(strsql!=null&&strsql.trim().length()>0)
				whlSql.append(strsql);
			
			ExamPlanBo bo = new ExamPlanBo(this.conn);
			HashMap map = bo.getPlansByUserView(this.view, whlSql.toString());
			
			buf.append(whlSql.toString());					
			buf.append("order by "+Sql_switcher.isnull("a0000", "999999999")+" asc,plan_id desc");
		
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(buf.toString());
			while(rs.next())
			{				
				if(map.get(rs.getString("plan_id"))==null)
		    		continue;	   
				
				LazyDynaBean bean = new LazyDynaBean();
				String plan_id = rs.getString("plan_id");								
				
				// 查询某考核计划下的考核对象的状态数据
				StringBuffer strSql=new StringBuffer();
				strSql.append("select count(object_id) num,'all' type from per_object where plan_id=" +plan_id+ " union all ");
				strSql.append(" select count(object_id) num,'01' type from per_object where plan_id=" +plan_id+ " and (sp_flag='01' or sp_flag is null or sp_flag='') union all ");
				strSql.append(" select count(object_id) num,'02' type from per_object where plan_id=" +plan_id+ " and sp_flag='02' union all ");
				strSql.append(" select count(object_id) num,'03' type from per_object where plan_id=" +plan_id+ " and sp_flag='03' ");
				
				rowSet = dao.search(strSql.toString());
				ArrayList numList = new ArrayList();
				while(rowSet.next())
				{
					String num = rowSet.getString("num");
					String type = rowSet.getString("type");
					
					LazyDynaBean abean = new LazyDynaBean();
					abean.set("type", type);
					abean.set("num", num);
					
					numList.add(abean);														
				}				
				
				bean.set("plan_id", plan_id);
				bean.set("plan_name", rs.getString("name"));
				bean.set("b0110", rs.getString("b0110"));				
				bean.set("numList", numList);
				
				list.add(bean);								
			}			
			
			if(rs!=null)
				rs.close();
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 获得 =3"MBO目标总结考评进度统计表" 需页面展现的计划列表 
	 */
	public ArrayList getMBOScoreList(String orgCode , String strsql)
	{
		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		RowSet rs = null;
		try
		{															
			StringBuffer buf=new StringBuffer();
			buf.append(" select plan_id,name,b0110,theyear from per_plan where 1=1 ");			
//			buf.append( getUserViewPrivWhere() );                   // 获得登录用户的控制权限
				
			String value = "";
			if(!"-1".equals(orgCode))
	    	{		    	
		    	value = orgCode.substring(2);		    			    		
	    	}
			StringBuffer whlSql = new StringBuffer();
			whlSql.append(" and Method=2 and (status=4 or status=5 or status=6) ");
			whlSql.append(" and ((B0110 = 'HJSJ' or B0110 = '' ) ");
			whlSql.append(" or (B0110 like '" + value + "%' ");
			whlSql.append(")) ");
			
			if(strsql!=null&&strsql.trim().length()>0)
				whlSql.append(strsql);
			
			ExamPlanBo bo = new ExamPlanBo(this.conn);
			HashMap map = bo.getPlansByUserView(this.view, whlSql.toString());
			
			buf.append(whlSql.toString());					
			buf.append("order by "+Sql_switcher.isnull("a0000", "999999999")+" asc,plan_id desc");
		
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(buf.toString());
			while(rs.next())
			{				
				if(map.get(rs.getString("plan_id"))==null)
		    		continue;	   
				
				LazyDynaBean bean = new LazyDynaBean();
				String plan_id = rs.getString("plan_id");								
				
				
				ArrayList numList = new ArrayList();
				LazyDynaBean abean = null;
				// 查询某考核计划下的考核对象个数
				StringBuffer strSql=new StringBuffer();
				strSql.append("select count(object_id) num from per_object where plan_id =" +plan_id );
				rowSet = dao.search(strSql.toString());
				String totalNum = "";
				while(rowSet.next())
				{
					totalNum = rowSet.getString("num");
					abean = new LazyDynaBean();					
					abean.set("num", rowSet.getString("num"));					
					numList.add(abean);														
				}
				
				// 某考核计划下的每个考核对象拥有的考核主体个数
				StringBuffer str=new StringBuffer();
				str.append("select count(mainbody_id) mianbodyNum ,object_id from per_mainbody where plan_id=" +plan_id+ " ");	
				str.append(" group by object_id ");	
				rowSet = dao.search(str.toString());
				HashMap keyMap=new HashMap();
				while(rowSet.next())
				{
					String mianbodyNum = rowSet.getString("mianbodyNum");	
					String object_id = rowSet.getString("object_id");						
					keyMap.put(object_id,mianbodyNum);																		
				}				
				
				int count = 0;
				// 某考核计划下的每个考核对象拥有的未打分的考核主体个数
				StringBuffer strs=new StringBuffer();
				strs.append("select count(mainbody_id) mianbodyNum ,object_id from per_mainbody where plan_id=" +plan_id+ " ");								
				strs.append(" and (status=0 or status is null ) group by object_id ");												
				rowSet = dao.search(strs.toString());				
				while(rowSet.next())
				{
					String object_id = rowSet.getString("object_id");
					String mianbodyNum = rowSet.getString("mianbodyNum");	
					int noScore=Integer.parseInt(mianbodyNum);
					
					if(keyMap.get(object_id)!=null)	
					{
						String strValue = (String)keyMap.get(object_id);   //value值   						
						int sumScore=Integer.parseInt(strValue);
						if(noScore == sumScore)
							count++;						
					}					
				}
				abean = new LazyDynaBean();					
				abean.set("num", String.valueOf(count));					
				numList.add(abean);	
								
				int cot = 0;
				// 某考核计划下的每个考核对象拥有的已打分的考核主体个数
				StringBuffer sts=new StringBuffer();
				sts.append("select count(mainbody_id) mianbodyNum ,object_id from per_mainbody where plan_id=" +plan_id+ " ");								
				sts.append(" and (status=2 or status=3 ) group by object_id  ");												
				rowSet = dao.search(sts.toString());				
				while(rowSet.next())
				{
					String object_id = rowSet.getString("object_id");
					String mianbodyNum = rowSet.getString("mianbodyNum");	
					int haveScore=Integer.parseInt(mianbodyNum);
					
					if(keyMap.get(object_id)!=null)	
					{
						String strValue = (String)keyMap.get(object_id);   //value值   						
						int sumScore=Integer.parseInt(strValue);
						if(haveScore == sumScore)
							cot++;						
					}					
				}
				
				// 某考核计划下的每个考核对象拥有的正在打分的考核主体个数
				int totalNumber=Integer.parseInt(totalNum); // 总考核对象数
				int nowScore = (totalNumber-count-cot);				
				
				
				abean = new LazyDynaBean();					
				abean.set("num", String.valueOf(nowScore));					
				numList.add(abean);
								
				abean = new LazyDynaBean();					
				abean.set("num", String.valueOf(cot));					
				numList.add(abean);
								
				bean.set("plan_id", plan_id);
				bean.set("plan_name", rs.getString("name"));
				bean.set("b0110", rs.getString("b0110"));				
				bean.set("numList", numList);
				
				list.add(bean);								
			}			
			
			if(rs!=null)
				rs.close();
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
		
	/**
	 * 根据用户权限获得计划列表 先看操作单位 再看管理范围
	 * 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
	 */
	public String getUserViewPrivWhere()
	{
		String str = "";
//		if (!this.view.isSuper_admin())
		{
			StringBuffer buf = new StringBuffer();
			String operOrg = this.view.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			if (operOrg!=null && operOrg.length() > 3)
			{
				StringBuffer tempSql = new StringBuffer("");
				String[] temp = operOrg.split("`");
				for (int i = 0; i < temp.length; i++)
				{
					if ("UN".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or  b0110 like '" + temp[i].substring(2) + "%'");
					else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or  b0110 like '" + temp[i].substring(2) + "%'");
				}
				buf.append(" and ( " + tempSql.substring(3) + " ) ");
				
			} 
			else if((!this.view.isAdmin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
			{
				String codeid = this.view.getManagePrivCode();
				String codevalue = this.view.getManagePrivCodeValue();
				String a_code = codeid + codevalue;

				if (a_code.trim().length() > 0)// 说明授权了
				{
					if ("UN".equalsIgnoreCase(a_code))// 说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串
						buf.append(" and 1=1 ");
					else
						buf.append(" and b0110 like '" + codevalue + "%'");
				} else
					buf.append(" and 1=2 ");
			}
			str = buf.toString();
		}

		return str;
	}
	
	/**
	 * 获得 =2"MBO目标总结考评进度统计表" 以及 =3"MBO目标总结考评进度统计表" 的考核周期 
	 */
	public ArrayList getCheckCycleList()
	{
		ArrayList checkCycleList = new ArrayList();
		
		try
		{															
			checkCycleList.add(new CommonData("all","全部"));
			checkCycleList.add(new CommonData("0", ResourceFactory.getProperty("jx.khplan.yeardu")));
			checkCycleList.add(new CommonData("1", ResourceFactory.getProperty("jx.khplan.halfyear")));
			checkCycleList.add(new CommonData("2", ResourceFactory.getProperty("jx.khplan.quarter")));
			checkCycleList.add(new CommonData("3", ResourceFactory.getProperty("jx.khplan.monthdu")));
			checkCycleList.add(new CommonData("7", ResourceFactory.getProperty("jx.khplan.indefinetime")));			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return checkCycleList;				
	}
	
	/**
	 * 获得 =2"MBO目标总结考评进度统计表" 以及 =3"MBO目标总结考评进度统计表" 的考核周期下的数据
	 */
	public ArrayList getChangeCycleList(String convertPageEntry)
	{
		ArrayList changeCycleList = new ArrayList();
		
		StringBuffer sqlStr = new StringBuffer();				
		ContentDAO dao = new ContentDAO(this.conn);
		
		RowSet rs = null;
		try
		{																		
			sqlStr.append("select distinct ");
			
			if("2".equalsIgnoreCase(convertPageEntry))
				sqlStr.append(" theyear from per_plan where Method=2 and (status=5 or status=8) order by theyear desc ");
			else
				sqlStr.append(" theyear from per_plan where Method=2 and (status=4 or status=5 or status=6) order by theyear desc ");
				
			rs = dao.search(sqlStr.toString());
			changeCycleList.add(new CommonData("all","全部"));
			while(rs.next())
			{
				String theYear = rs.getString("theyear");						   
				changeCycleList.add(new CommonData(theYear,theYear));
			}																	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return changeCycleList;				
	}
	
	/**
	 * 获得 =2"MBO目标总结考评进度统计表" 以及 =3"MBO目标总结考评进度统计表" 的考核周期非年度下的数据
	 */
	public ArrayList getNoYearCycleList(String checkCycle)
	{
		ArrayList checkCycleList = new ArrayList();
						
		try
		{															
			if("0".equalsIgnoreCase(checkCycle)) //年度
			{				
				
			}else if("1".equalsIgnoreCase(checkCycle)) //半年
			{
				checkCycleList.add(new CommonData("all","全部"));
				checkCycleList.add(new CommonData("1", ResourceFactory.getProperty("report.pigeonhole.uphalfyear")));
				checkCycleList.add(new CommonData("2", ResourceFactory.getProperty("report.pigeonhole.downhalfyear")));					
				
			}else if("2".equalsIgnoreCase(checkCycle)) //季度
			{
				checkCycleList.add(new CommonData("all","全部"));
				checkCycleList.add(new CommonData("01", ResourceFactory.getProperty("report.pigionhole.oneQuarter")));
				checkCycleList.add(new CommonData("02", ResourceFactory.getProperty("report.pigionhole.twoQuarter")));
				checkCycleList.add(new CommonData("03", ResourceFactory.getProperty("report.pigionhole.threeQuarter")));
				checkCycleList.add(new CommonData("04", ResourceFactory.getProperty("report.pigionhole.fourQuarter")));								
				
			}else if("3".equalsIgnoreCase(checkCycle)) //月度
			{		
				checkCycleList.add(new CommonData("all","全部"));
				checkCycleList.add(new CommonData("01", ResourceFactory.getProperty("date.month.january")));
				checkCycleList.add(new CommonData("02", ResourceFactory.getProperty("date.month.february")));
				checkCycleList.add(new CommonData("03", ResourceFactory.getProperty("date.month.march")));
				checkCycleList.add(new CommonData("04", ResourceFactory.getProperty("date.month.april")));	
				checkCycleList.add(new CommonData("05", ResourceFactory.getProperty("date.month.may")));
				checkCycleList.add(new CommonData("06", ResourceFactory.getProperty("date.month.june")));
				checkCycleList.add(new CommonData("07", ResourceFactory.getProperty("date.month.july")));
				checkCycleList.add(new CommonData("08", ResourceFactory.getProperty("date.month.auguest")));	
				checkCycleList.add(new CommonData("09", ResourceFactory.getProperty("date.month.september")));
				checkCycleList.add(new CommonData("10", ResourceFactory.getProperty("date.month.october")));
				checkCycleList.add(new CommonData("11", ResourceFactory.getProperty("date.month.november")));
				checkCycleList.add(new CommonData("12", ResourceFactory.getProperty("date.month.december")));					
				
			}else if("7".equalsIgnoreCase(checkCycle)) //不定期
			{
				
				
				
			}
						
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return checkCycleList;				
	}		
	
	
}
