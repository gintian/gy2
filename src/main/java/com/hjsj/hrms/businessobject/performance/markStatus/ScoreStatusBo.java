package com.hjsj.hrms.businessobject.performance.markStatus;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * <p>Title:ScoreStatusBo.java</p>
 * <p>Description:展示打分状态的统计结果</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-07-09 09:45:56</p>
 * @author JinChunhai
 * @version 5.0
 */

public class ScoreStatusBo 
{
	private Connection conn=null;
	private UserView userView=null;
	private String level = "1";    // 部门层级
	
	public ScoreStatusBo(Connection conn)
	{
		this.conn=conn;
	}
	
	public ScoreStatusBo(Connection conn,UserView auserView)
	{
		this.conn=conn;
		this.userView=auserView;
	}
	
	
	
	/**	
	 * 当按考核主体统计时新建临时表并进行操作
	 */
	public void operaTempTable(String plan_id , String level)
	{
		this.level = level;
		RowSet rowSet=null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			
			//  新建临时表 
			String tablename = "per_statistic";			
			DbWizard dbWizard = new DbWizard(this.conn);			
			if(!dbWizard.isExistTable(tablename,false))
			{
				Table table = new Table(tablename);
		    	table.addField(getField("B0110", "A", 100, false));
		    	table.addField(getField("E0122", "A", 100, false));
		    	table.addField(getField("personUint_id", "A", 100, false));
		    	table.addField(getField("status", "I", 8, false));
		    	table.addField(getField("username", "A", 100, false));
		    	dbWizard.createTable(table);
			}
			// 删掉登录人自己的记录
			String strSql = "delete from per_statistic where username='" + this.userView.getUserName() + "'";
			dao.delete(strSql, new ArrayList());	

						
			// 向临时表中写入记录
			String insertStr = "insert into per_statistic (personUint_id,b0110,e0122,username)"
	 						 +" select distinct pm.mainbody_id,pm.b0110,pm.e0122,'" + this.userView.getUserName() + "' " 
	 						 +" from per_mainbody pm,per_object po where pm.plan_id='"+plan_id+"' and po.object_id=pm.object_id "+this.getUserViewPrivWhere(this.userView);
			dao.insert(insertStr, new ArrayList());
			
			// 向临时表中补充记录
			ArrayList beList = new ArrayList();
			String selSql = "SELECT MAINBODY_ID,COUNT(STATUS) NUM,MAX(STATUS) STATUS FROM ("
				 		  +" SELECT MAINBODY_ID,"+ Sql_switcher.isnull("STATUS", "0") +" STATUS " 
				 		  +" FROM (SELECT MAINBODY_ID,CASE WHEN (status=2 or status=3 or status=4 or status=7) then 2 " 
				 		  +" when ("+ Sql_switcher.isnull("STATUS", "0") +"=0 or status=0) then 0 else status end as status from per_mainbody " 
					      +" where plan_id='"+plan_id+"') X  "
					      +" GROUP BY MAINBODY_ID,"+ Sql_switcher.isnull("STATUS", "0" ) +" ) t GROUP BY MAINBODY_ID ";
			rowSet = dao.search(selSql);
			while(rowSet.next())
			{
				ArrayList list = new ArrayList();
				String mainbody_id = rowSet.getString("MAINBODY_ID");
				String num = rowSet.getString("NUM");
				String status = rowSet.getString("STATUS");
				if("1".equalsIgnoreCase(num))
					list.add(status);
				else
					list.add("1");
				
				list.add(mainbody_id);             				
				beList.add(list);				
			}
			if(beList.size()>0)
			{
				// 批量更新临时表per_statistic中的status
				String updSql = "update per_statistic set status=? where username='" + this.userView.getUserName() + "' " 
				  			  +" and personUint_id = ? " ;			  			  
				dao.batchUpdate(updSql, beList);		
			}						
			
/*						
			// 向临时表中写入记录
			String insertSql = "insert into per_statistic (b0110,e0122,personUint_id,status,username)"
	 						 +" select pm.b0110,pm.e0122,pm.mainbody_id,(case when (pm.status=2 or pm.status=3 or pm.status=4 or pm.status=7) then 2 when pm.status is null then 0 else pm.status end) status,'" + this.userView.getUserName() + "' " 
	 						 +" from per_mainbody pm,per_object po where pm.plan_id='"+plan_id+"' and po.object_id=pm.object_id "+this.getUserViewPrivWhere(this.userView)
	 						 +" group by pm.mainbody_id,pm.status,pm.b0110,pm.e0122";
			dao.insert(insertSql, new ArrayList());
			
			
			// 把临时表per_statistic中的mainbody_id相同的记录的status最小的记录的status修改为5
			String updateSql = "update per_statistic set status=5 where username='" + this.userView.getUserName() + "' " 
				             +" and per_statistic.status=(select min(a.status) from per_statistic a where username='" + this.userView.getUserName() + "' and a.personUint_id=per_statistic.personUint_id )" 
				             +" and personUint_id in (select personUint_id from per_statistic where username='" + this.userView.getUserName() + "' group by personUint_id having count(personUint_id)>1 )";
			dao.update(updateSql);	
			
			
			// 删掉临时表per_statistic中的mainbody_id相同的status!=5的记录
			String delSql = "delete from per_statistic where username='" + this.userView.getUserName() + "' " 
						  +" and status<5 and personUint_id in (select personUint_id from per_statistic where username='" + this.userView.getUserName() + "' and status=5 )";
			dao.delete(delSql, new ArrayList());
*/			
			
			
			if(!"all".equalsIgnoreCase(level))
			{
				// 查找临时表per_statistic中的e0122不在部门层级范围内的记录
				ArrayList valueList = new ArrayList();
				String seleSql = "select distinct e0122 from per_statistic where username='" + this.userView.getUserName() + "' " 
							  	 +" and e0122 not in (select codeitemid from organization where codesetid='UM' and layer<=" + level + ") " ;
				rowSet = dao.search(seleSql);
				while(rowSet.next())
				{
					ArrayList list = new ArrayList();
					String parentid = getParentid(rowSet.getString("e0122")); // 查找符合条件的上级或下级部门
					list.add(parentid);             
					list.add(rowSet.getString("e0122"));
					valueList.add(list);
				}
				if(valueList.size()>0)
				{
					// 批量修改临时表per_statistic中的e0122不在部门层级范围内的记录
					String updSql = "update per_statistic set e0122=? where username='" + this.userView.getUserName() + "' " 
					  			  +" and e0122 = ? " ;			  			  
					dao.batchUpdate(updSql, valueList);		
				}
			}
			
			
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**	
	 * 考核主体时：统计未评分、正评分、已评分的记录
	 */
	public LinkedHashMap getMainbodyMap()
	{
		LinkedHashMap map = new LinkedHashMap();
		RowSet rowSet=null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("SELECT B0110,E0122,SUM(A) allScore,SUM(B) noScore, SUM(C) nowScore, SUM(D) endScore FROM " );
			sqlstr.append(" (SELECT B0110,E0122,status,CASE WHEN STATUS IS NOT NULL THEN count(personUint_id) ELSE 0 END A," );
			sqlstr.append(" CASE STATUS WHEN 0 THEN count(personUint_id) ELSE 0 END B," );
			sqlstr.append(" CASE STATUS WHEN 1 THEN count(personUint_id) ELSE 0 END C," );
			sqlstr.append(" CASE STATUS WHEN 2 THEN count(personUint_id) ELSE 0 END D " );
			sqlstr.append(" FROM per_statistic where username='" + this.userView.getUserName() + "' GROUP BY b0110,e0122,status) A " );			
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				sqlstr.append(" GROUP BY ROLLUP (b0110,e0122) ");
			else
				sqlstr.append(" GROUP BY b0110,e0122 with ROLLUP ");
			
			rowSet = dao.search(sqlstr.toString());			
			while(rowSet.next())
			{		
				LazyDynaBean abean = new LazyDynaBean();
//				abean.set("b0110", AdminCode.getCodeName("UN", isNull(rowSet.getString("B0110"))));
//				abean.set("e0122", AdminCode.getCodeName("UM", isNull(rowSet.getString("E0122"))));
				String B0110 = isNull(rowSet.getString("B0110"));
				String E0122 = isNull(rowSet.getString("E0122"));
				
				if(E0122!=null && E0122.trim().length()>0)				
					abean.set("e0122", isNull(rowSet.getString("E0122")));
				else
				{
					if(rowSet.next()!=false)
					{
						if(B0110.equalsIgnoreCase(isNull(rowSet.getString("B0110"))))
							abean.set("e0122", "e0122");
						else
							abean.set("e0122", isNull(E0122));
						
					}else 
					{
						abean.set("e0122", isNull(E0122));
					}
					rowSet.previous();
				}				
				
				abean.set("b0110", isNull(rowSet.getString("B0110")));				
				abean.set("allScore", rowSet.getString("allScore"));     // 部门考核主体总数
				abean.set("noScore", rowSet.getString("noScore"));      // 0:未评分考核主体人数
				abean.set("nowScore", rowSet.getString("nowScore"));	// 1:正评分考核主体人数
				abean.set("endScore", rowSet.getString("endScore"));    // 2:已评分考核主体人数
								
				if(B0110!=null && B0110.trim().length()>0)
				{
					if(map.get(B0110)!=null)
					{
						ArrayList list = (ArrayList)map.get(B0110);
						list.add(abean);
						map.put(B0110, list);	
					}
					else
					{
						ArrayList list = new ArrayList();
						list.add(abean);
						map.put(B0110,list);
					}
					
				}else
				{
					ArrayList list = new ArrayList();
					list.add(abean);
					if(rowSet.next()!=false)
						map.put("unit",list);						
					else 
						map.put("b0110",list);
					
					rowSet.previous();
				}
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
	
	/**	
	 * 当按考核对象统计时新建临时表并进行操作
	 */
	public void operaTempObjectTable(String plan_id , String level)
	{
		this.level = level;
		RowSet rowSet=null;
		RowSet rs=null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			
			//  新建临时表 
			String tablename = "per_statistic";			
			DbWizard dbWizard = new DbWizard(this.conn);			
			if(!dbWizard.isExistTable(tablename,false))
			{
				Table table = new Table(tablename);
		    	table.addField(getField("B0110", "A", 100, false));
		    	table.addField(getField("E0122", "A", 100, false));
		    	table.addField(getField("personUint_id", "A", 100, false));
//		    	table.addField(getField("mainbody_id", "A", 100, false));
		    	table.addField(getField("status", "I", 8, false));
		    	table.addField(getField("username", "A", 100, false));
		    	dbWizard.createTable(table);
			}
			// 删掉登录人自己的记录
			String strSql = "delete from per_statistic where username='" + this.userView.getUserName() + "'";
			dao.delete(strSql, new ArrayList());	

						
			// 向临时表中写入记录
			String insertStr = "insert into per_statistic (b0110,e0122,personUint_id,username)"
	 						 +" select po.b0110,po.e0122,po.object_id,'" + this.userView.getUserName() + "' " 
	 						 +" from per_object po where po.plan_id='"+plan_id+"' "+this.getUserViewPrivWhere(this.userView)
	 						 +" group by po.object_id,po.b0110,po.e0122";
			dao.insert(insertStr, new ArrayList());
			
			// 向临时表中补充记录
			ArrayList beList = new ArrayList();
			String selSql = "SELECT OBJECT_ID,COUNT(STATUS) NUM,MAX(STATUS) STATUS FROM ("
				 		  +" SELECT OBJECT_ID,"+ Sql_switcher.isnull("STATUS", "0") +" STATUS " 
				 		  +" FROM (SELECT OBJECT_ID,CASE WHEN (status=2 or status=3 or status=4 or status=7) then 2 " 
				 		  +" when ("+ Sql_switcher.isnull("STATUS", "0") +"=0 or status=0) then 0 else status end as status from per_mainbody " 
					      +" where plan_id='"+plan_id+"') X  "
					      +" GROUP BY object_id,"+ Sql_switcher.isnull("STATUS", "0") +" ) t GROUP BY OBJECT_ID ";
			rowSet = dao.search(selSql);
			while(rowSet.next())
			{
				ArrayList list = new ArrayList();
				String object_id = rowSet.getString("OBJECT_ID");
				String num = rowSet.getString("NUM");
				String status = rowSet.getString("STATUS");
				if("1".equalsIgnoreCase(num))
					list.add(status);
				else
					list.add("1");
				
				list.add(object_id);             				
				beList.add(list);				
			}
			if(beList.size()>0)
			{
				// 批量更新临时表per_statistic中的status
				String updSql = "update per_statistic set status=? where username='" + this.userView.getUserName() + "' " 
				  			  +" and personUint_id = ? " ;			  			  
				dao.batchUpdate(updSql, beList);		
			}
			
			// 把临时表per_statistic中object_id的status为null的全部修改为:1(正评分)
			String updateSql = "update per_statistic set status=1 where username='" + this.userView.getUserName() + "' " 
				             +" and status is null " ;
			dao.update(updateSql);	
				
			
			if(!"all".equalsIgnoreCase(level))
			{
				// 查找临时表per_statistic中的e0122不在部门层级范围内的记录
				ArrayList valueList = new ArrayList();
				String seleSql = "select distinct e0122 from per_statistic where username='" + this.userView.getUserName() + "' " 
							  	 +" and e0122 not in (select codeitemid from organization where codesetid='UM' and layer<=" + level + ") " ;
				rowSet = dao.search(seleSql);
				while(rowSet.next())
				{
					ArrayList list = new ArrayList();
					String parentid = getParentid(rowSet.getString("e0122")); // 查找符合条件的上级部门
					list.add(parentid);             
					list.add(rowSet.getString("e0122"));
					valueList.add(list);
				}
				if(valueList.size()>0)
				{
					// 批量修改临时表per_statistic中的e0122不在部门层级范围内的记录
					String updSql = "update per_statistic set e0122=? where username='" + this.userView.getUserName() + "' " 
					  			  +" and e0122 = ? " ;			  			  
					dao.batchUpdate(updSql, valueList);		
				}
			}
						
			if(rowSet!=null)
				rowSet.close();
			if(rs!=null)
				rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**	
	 * 考核对象时：统计未评分、正评分、已评分的记录
	 */
	public LinkedHashMap getObjectMap()
	{
		LinkedHashMap map = new LinkedHashMap();
		RowSet rowSet=null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("SELECT B0110,E0122,SUM(A) allScore,SUM(B) noScore, SUM(C) nowScore, SUM(D) endScore FROM " );
			sqlstr.append(" (SELECT B0110,E0122,status,CASE WHEN STATUS IS NOT NULL THEN count(personUint_id) ELSE 0 END A," );
			sqlstr.append(" CASE STATUS WHEN 0 THEN count(personUint_id) ELSE 0 END B," );
			sqlstr.append(" CASE STATUS WHEN 1 THEN count(personUint_id) ELSE 0 END C," );
			sqlstr.append(" CASE STATUS WHEN 2 THEN count(personUint_id) ELSE 0 END D " );
			sqlstr.append(" FROM per_statistic where username='" + this.userView.getUserName() + "' GROUP BY b0110,e0122,status) A " );			
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				sqlstr.append(" GROUP BY ROLLUP (b0110,e0122) ");
			else
				sqlstr.append(" GROUP BY b0110,e0122 with ROLLUP ");
			
			rowSet = dao.search(sqlstr.toString());			
			while(rowSet.next())
			{		
				LazyDynaBean abean = new LazyDynaBean();
//				abean.set("b0110", AdminCode.getCodeName("UN", isNull(rowSet.getString("B0110"))));
//				abean.set("e0122", AdminCode.getCodeName("UM", isNull(rowSet.getString("E0122"))));
				String B0110 = isNull(rowSet.getString("B0110"));
				String E0122 = isNull(rowSet.getString("E0122"));
				
				if(E0122!=null && E0122.trim().length()>0)				
					abean.set("e0122", isNull(rowSet.getString("E0122")));
				else
				{
					if(rowSet.next()!=false)
					{
						if(B0110.equalsIgnoreCase(isNull(rowSet.getString("B0110"))))
							abean.set("e0122", "e0122");
						else
							abean.set("e0122", isNull(E0122));
						
					}else 
					{
						abean.set("e0122", isNull(E0122));
					}
					rowSet.previous();
				}				
				
				abean.set("b0110", isNull(rowSet.getString("B0110")));				
				abean.set("allScore", rowSet.getString("allScore"));     // 部门考核主体总数
				abean.set("noScore", rowSet.getString("noScore"));      // 0:未评分考核主体人数
				abean.set("nowScore", rowSet.getString("nowScore"));	// 1:正评分考核主体人数
				abean.set("endScore", rowSet.getString("endScore"));    // 2:已评分考核主体人数
								
				if(B0110!=null && B0110.trim().length()>0)
				{
					if(map.get(B0110)!=null)
					{
						ArrayList list = (ArrayList)map.get(B0110);
						list.add(abean);
						map.put(B0110, list);	
					}
					else
					{
						ArrayList list = new ArrayList();
						list.add(abean);
						map.put(B0110,list);
					}
					
				}else
				{
					ArrayList list = new ArrayList();
					list.add(abean);
					if(rowSet.next()!=false)
						map.put("unit",list);						
					else 
						map.put("b0110",list);
					
					rowSet.previous();
				}
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
	
	
	/**
	 * 新建临时表字段
	 */
	public Field getField(String fieldname, String a_type, int length, boolean key)
    {
		Field obj = new Field(fieldname, fieldname);
		if ("A".equals(a_type))
		{
		    obj.setDatatype(DataType.STRING);
		    obj.setLength(length);
		} else if ("M".equals(a_type))
		{
		    obj.setDatatype(DataType.CLOB);
		} else if ("I".equals(a_type))
		{
		    obj.setDatatype(DataType.INT);
		    obj.setLength(length);
		} else if ("N".equals(a_type))
		{
		    obj.setDatatype(DataType.FLOAT);
		    obj.setLength(length);
		    obj.setDecimalDigits(5);
		} else if ("D".equals(a_type))
		{
		    obj.setDatatype(DataType.DATE);
		} else
		{
		    obj.setDatatype(DataType.STRING);
		    obj.setLength(length);
		}
		if(key)
		    obj.setNullable(false);
		obj.setKeyable(key);	
		return obj;
    }
	/**	
	 * 获得organization表部门的信息
	 */
	public LazyDynaBean getE0122List(String codeitemid)
	{
		LazyDynaBean abean=new LazyDynaBean();
		RowSet rowSet=null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rowSet=dao.search("select codeitemid,codeitemdesc,parentid,childid,layer from organization where codesetid='UM' and codeitemid='" + codeitemid + "'");
			while(rowSet.next())
		    {
		    	abean.set("codeitemid",rowSet.getString("codeitemid"));
		    	abean.set("codeitemdesc",rowSet.getString("codeitemdesc"));
		    	abean.set("parentid",rowSet.getString("parentid")!=null?rowSet.getString("parentid"):"");
		    	abean.set("childid",rowSet.getString("childid")!=null?rowSet.getString("childid"):"");		    	
		    	abean.set("layer",rowSet.getString("layer"));		    			    	
		    }
			
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return abean;
	}
	/**
	 * 部门对应的继承关系
	 * @return
	 */
	public String getParentid(String codeitemid)
	{
		String str = "";
		String parentid = "";
		String childid = "";
		String layer = "";
		LazyDynaBean abean = null;
		try
		{
			abean = (LazyDynaBean)getE0122List(codeitemid);			
			parentid = (String)abean.get("parentid");
			childid = (String)abean.get("childid");
			layer = (String)abean.get("layer");
			
			if(((parentid!=null) && parentid.trim().length()>0) && ((childid!=null) && childid.trim().length()>0) && ((layer!=null) && layer.trim().length()>0))
			{							
				if(layer.equalsIgnoreCase(this.level))	
				{
					str = codeitemid;
					return str;
				}
				else
				{	
					ArrayList linkList=new ArrayList();
					if(Integer.parseInt(this.level)<Integer.parseInt(layer))
						getParent_id(linkList,parentid);
					else
						getParent_id(linkList,childid);									
					for(int i=0;i<linkList.size();i++)
					{
						str=(String)linkList.get(0);
					}				
				}
			}else
			{
				str = codeitemid;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}	
	public void getParent_id(ArrayList list,String codeitemid)
	{
		String parentid = "";
		String childid = "";
		String layer = "";
		LazyDynaBean abean = null;
		try
		{
			abean = (LazyDynaBean)getE0122List(codeitemid);			
			parentid = (String)abean.get("parentid");
			childid = (String)abean.get("childid");
			layer = (String)abean.get("layer");
				
			if(layer.equalsIgnoreCase(this.level))	
			{
				list.add(codeitemid);
				return;
			}
			else
			{
				if(Integer.parseInt(this.level)<Integer.parseInt(layer))
					getParent_id(list,parentid);
				else
					getParent_id(list,childid);	
			}			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 获得部门层级
	 */
	public ArrayList getE0122LevelList()
	{
		ArrayList checkCycleList = new ArrayList();		
		try
		{															
			checkCycleList.add(new CommonData("all","全部"));
			checkCycleList.add(new CommonData("1", ResourceFactory.getProperty("hrms.interfaces.sys.level1")));
			checkCycleList.add(new CommonData("2", ResourceFactory.getProperty("hrms.interfaces.sys.level2")));
			checkCycleList.add(new CommonData("3", ResourceFactory.getProperty("hrms.interfaces.sys.level3")));
			checkCycleList.add(new CommonData("4", ResourceFactory.getProperty("hrms.interfaces.sys.level4")));
			checkCycleList.add(new CommonData("5", ResourceFactory.getProperty("hrms.interfaces.sys.level5")));						
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		return checkCycleList;				
	}
	
	/**
	 * 获得评分状态
	 */
	public ArrayList getScoreTypeList()
	{
		ArrayList checkCycleList = new ArrayList();		
		try
		{															
			checkCycleList.add(new CommonData("all","全部"));
			checkCycleList.add(new CommonData("01", ResourceFactory.getProperty("lable.performnace.wpf")));
			checkCycleList.add(new CommonData("02", ResourceFactory.getProperty("lable.performnace.nowpingscore")));
			checkCycleList.add(new CommonData("03", ResourceFactory.getProperty("lable.performnace.havepingscore")));								
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		return checkCycleList;				
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
			if(vo.getInt("method")==0)
				vo.setInt("method",1);
		
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
	
	/**
	 * 根据用户权限获得计划列表 先看操作单位 再看管理范围
	 * 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
	 */
	public String getUserViewPrivWhere(UserView userView)
	{
		String str = "";
//		if (!userView.isSuper_admin())
		{
			StringBuffer buf = new StringBuffer();
			String operOrg = userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			if (operOrg!=null && operOrg.length() > 3)
			{
				StringBuffer tempSql = new StringBuffer("");
				String[] temp = operOrg.split("`");
				for (int i = 0; i < temp.length; i++)
				{
					if ("UN".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or  po.b0110 like '" + temp[i].substring(2) + "%'");
					else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or  po.e0122 like '" + temp[i].substring(2) + "%'");
				}
				buf.append(" and ( " + tempSql.substring(3) + " ) ");
			} 
			else if((!userView.isAdmin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
			{
				String codeid = userView.getManagePrivCode();
				String codevalue = userView.getManagePrivCodeValue();
				String a_code = codeid + codevalue;

				if (a_code.trim().length() > 0)// 说明授权了
				{
					if ("UN".equalsIgnoreCase(a_code))// 说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串
						buf.append(" and 1=1 ");
					else if("UN".equalsIgnoreCase(codeid))
						buf.append(" and po.b0110 like '" + codevalue + "%'");
					else if("UM".equalsIgnoreCase(codeid))
						buf.append(" and po.e0122 like '" + codevalue + "%'");
					else if("@K".equalsIgnoreCase(codeid))
						buf.append(" and po.e01a1 like '" + codevalue + "%'");
					else
						buf.append(" and po.b0110 like '" + codevalue + "%'");
						
				} else
					buf.append(" and 1=2 ");
			}
			str = buf.toString();
		}

		return str;		
	}
	
}
