package com.hjsj.hrms.businessobject.competencymodal.personPostModal;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;

/**
 * <p>Title:PersonPostModalBo.java</p>
 * <p>Description:人岗匹配、岗人匹配</p>
 * <p>Company:hjsj</p>
 * <p>create time:2012-01-11 10:23:41</p>
 * @author JinChunhai
 * @version 5.0
 */

public class PersonPostModalBo 
{

	private Connection con=null;
	private UserView userView=null;
	
	public PersonPostModalBo(Connection a_con)
	{
		this.con=a_con;
	}
	
	public PersonPostModalBo(Connection a_con,UserView userView)
	{
		this.con=a_con;
		this.userView=userView;
	}	
	
	/**
	 * 查询符合条件的能力素质考核计划
	 * return
	 */
    public ArrayList searchPlanList(String planid) throws GeneralException
    {    
    	ArrayList planList = new ArrayList();
		RowSet rowSet = null;
		try
		{	
			ContentDAO dao = new ContentDAO(this.con);										
				
			StringBuffer strSql = new StringBuffer();
			strSql.append("select plan_id,name from per_plan where busitype=1 ");
			if(planid!=null && planid.trim().length()>0)
				strSql.append("  and plan_id="+planid);
			else
				strSql.append(" and status in(7) ");
			strSql.append(" order by " + Sql_switcher.isnull("a0000", "999999999") + " asc,plan_id desc ");	
			
			rowSet=dao.search(strSql.toString());					
			ExamPlanBo exbo = new ExamPlanBo(this.con);
			HashMap exmap = exbo.getPlansByUserView(this.userView, "");			
			while(rowSet.next())
			{
//				if (!userView.isSuper_admin())
				{
					if(exmap!=null && exmap.get(rowSet.getString("plan_id"))!=null){
						
					}else
					{
						continue;
					}
				}					
				String name=rowSet.getString("name");
				String plan_id=rowSet.getString("plan_id");
				String number=(plan_id+"."+name);				
				planList.add(new CommonData(rowSet.getString("plan_id"),number));									
			}						
		    
		    if(rowSet!=null)
		    	rowSet.close();
	
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return planList;
    }
	
    /**
	 * 查询符合条件的评估结构表中包含人员基本子集的代码类指标（单位、部门、岗位指标除外）和对象类别
	 * return
	 */
    public ArrayList searchPlanSubSetMenuList(String plan_id)
    {    
    	ArrayList subSetMenuList = new ArrayList();
    	
    	subSetMenuList.add(new CommonData("-1",""));
    	subSetMenuList.add(new CommonData("body_id","对象类别"));
		try
		{										
			LoadXml loadxml = new LoadXml(this.con, plan_id);
		    String subsetMenus = loadxml.getRelatePlanSubSetMenuValue();
			if(subsetMenus!=null && subsetMenus.trim().length()>0)
			{
				String[] temps=subsetMenus.split(",");
				for(int j=0;j<temps.length;j++)
				{
					String temp=temps[j].trim();
					if(temp.length()==0)
						continue;
				    FieldItem fielditem = DataDictionary.getFieldItem(temp);				    
				    
				    if("A".equalsIgnoreCase(fielditem.getItemtype()) && !"0".equals(fielditem.getCodesetid()))
				    	subSetMenuList.add(new CommonData(fielditem.getItemid(),fielditem.getItemdesc()));				    				    
				}
			}			
	
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return subSetMenuList;
    }
    
    /**
	 * 查询代码型指标的层级
	 * return
	 */
    public ArrayList searchPlanCodeLayer(String subSetMenu)
    {    
    	ArrayList codeItemList = new ArrayList();
		RowSet rowSet = null;
		try
		{	
			ContentDAO dao = new ContentDAO(this.con);										
				
			StringBuffer strSql = new StringBuffer();
			strSql.append("select DISTINCT ct.layer from fielditem ft,codeitem ct where ft.useflag='1' and ft.codesetid=ct.codesetid ");	
			strSql.append(" and ft.itemid='" + subSetMenu + "' ");	
			strSql.append(" order by ct.layer ");	
			
			rowSet=dao.search(strSql.toString());										
			while(rowSet.next())
			{
				String layer = isNull(rowSet.getString("layer"));
				if(layer!=null && layer.trim().length()>0)
					codeItemList.add(new CommonData(layer,"第"+layer+"层"));	
			}
		    
		    if(rowSet!=null)
		    	rowSet.close();
	
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return codeItemList;
    }
    
    /**
	 * 查询代码型指标的层级
	 * return
	 */
/*  public ArrayList searchPlanCodeItemList(String subSetMenu,String layer)
    {    
    	ArrayList codeItemList = new ArrayList();
		RowSet rowSet = null;
		try
		{	
			ContentDAO dao = new ContentDAO(this.con);										
				
			StringBuffer strSql = new StringBuffer();
			strSql.append("select ft.codeitemid,ft.codeitemdesc from fielditem ft,codeitem ct where ft.useflag='1' and ft.codesetid=ct.codesetid ");	
			strSql.append(" and ft.itemid='" + subSetMenu + "' ");
			strSql.append(" and ct.layer='" + layer + "' ");
		//	strSql.append(" order by A0000 ");	
			
			rowSet=dao.search(strSql.toString());										
			while(rowSet.next())
			{					
				String codeitemid = isNull(rowSet.getString("codeitemid"));
				String codeitemdesc = isNull(rowSet.getString("codeitemdesc"));				
								
				codeItemList.add(new CommonData(codeitemid,codeitemdesc));									
			}						
		    
		    if(rowSet!=null)
		    	rowSet.close();
	
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return codeItemList;
    }
*/    
    /**
	 * 封装统计图数据
	 * @param planid
	 * @return
	 */
	public ArrayList getDataPictrueList(String plan_id,String chart_type,String a_code)
	{
		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.con);
			HashMap map = new HashMap();
			
			StringBuffer strSql = new StringBuffer();
			strSql.append(" select resultdesc,count(*) num from per_result_" + plan_id + " where 1=1 ");						
			strSql.append(getUserViewWhere(this.userView)); // 操作单位或管理范围内的人	
			if(a_code!=null && a_code.trim().length()>2)
			{								
				if(a_code.indexOf("UN")!=-1)
				{
					strSql.append(" and b0110 like '" + a_code.substring(2, a_code.length()) + "%'");
				}else if(a_code.indexOf("UM")!=-1)
				{
					strSql.append(" and e0122 like '" + a_code.substring(2, a_code.length()) + "%'");
				}else if(a_code.indexOf("Usr")!=-1)
				{
					strSql.append(" and a0100 like '" + a_code.substring(3, a_code.length()) + "%'");
				}else if(a_code.indexOf("@K")!=-1 || a_code.indexOf("@k")!=-1)
				{
					strSql.append(" and e01a1 like '" + a_code.substring(2, a_code.length()) + "%'");
				}
			}			
			strSql.append(" group by resultdesc ");
						
			rowSet = dao.search(strSql.toString());	
			while(rowSet.next())
			{				
				String resultdesc = isNull(rowSet.getString(1));
				String num = isNull(rowSet.getString(2));
				resultdesc = warpRowStr(resultdesc,10);
				CommonData data = new CommonData(num,resultdesc);
				list.add(data);												
			}
/*			
			LoadXml loadxml = new LoadXml(this.con,plan_id);
			Hashtable htxml = new Hashtable();		
			htxml = loadxml.getDegreeWhole();
			String gradeClass = (String)htxml.get("GradeClass"); //等级分类ID
			
			StringBuffer sql = new StringBuffer();
			sql.append("select pds.id,pds.itemname from per_degree pd,per_degreedesc pds where pd.degree_id=pds.degree_id and pd.degree_id="+gradeClass);									
			rowSet = dao.search(sql.toString());
			while(rowSet.next())
			{
				String id = rowSet.getString(1);
				String itemname = rowSet.getString(2);
				itemname = warpRowStr(itemname,10);
				CommonData data = null;
				if(map.get(id)!=null)				
					data = new CommonData((String)map.get(id),itemname);				
				else
					data = new CommonData("0",itemname);
				list.add(data);
			}
*/			
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
	 * 封装有代码类统计图数据
	 * @param planid
	 * @return
	 */
	public ArrayList getCodeDataPictrueList(String plan_id,String chart_type,String a_code,String subSetMenu,String layer)
	{
		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.con);
			
			ArrayList codeList = new ArrayList();
			StringBuffer sqlStr = new StringBuffer();
			sqlStr.append(" select DISTINCT " + subSetMenu + " from per_result_" + plan_id + " where 1=1 ");						
			sqlStr.append(getUserViewWhere(this.userView)); // 操作单位或管理范围内的人	
			if(a_code!=null && a_code.trim().length()>2)
			{								
				if(a_code.indexOf("UN")!=-1)
				{
					sqlStr.append(" and b0110 like '" + a_code.substring(2, a_code.length()) + "%'");
				}else if(a_code.indexOf("UM")!=-1)
				{
					sqlStr.append(" and e0122 like '" + a_code.substring(2, a_code.length()) + "%'");
				}else if(a_code.indexOf("Usr")!=-1)
				{
					sqlStr.append(" and a0100 like '" + a_code.substring(3, a_code.length()) + "%'");
				}else if(a_code.indexOf("@K")!=-1 || a_code.indexOf("@k")!=-1)
				{
					sqlStr.append(" and e01a1 like '" + a_code.substring(2, a_code.length()) + "%'");
				}
			}			
			sqlStr.append(" ");
							
			HashMap objectTypeMap = new HashMap();
			FieldItem fielditem = null;
			if("body_id".equalsIgnoreCase(subSetMenu))
				objectTypeMap = getObjectTypeMap();				
			else
				fielditem = DataDictionary.getFieldItem(subSetMenu);
			rowSet = dao.search(sqlStr.toString());	
			while(rowSet.next())
			{	
				String subCode = rowSet.getString(1);
				if(subCode!=null && subCode.trim().length()>0)
				{
					if("body_id".equalsIgnoreCase(subSetMenu))
						codeList.add(new CommonData(subCode,(String)objectTypeMap.get(subCode)));
					else
						codeList.add(new CommonData(subCode,AdminCode.getCode(fielditem.getCodesetid(), subCode) != null ? AdminCode.getCode(fielditem.getCodesetid(), subCode).getCodename()+"（"+subCode+"）" : "" ));			
				}
			}
			
			
			HashMap dataMap = new HashMap();			
			StringBuffer strSql = new StringBuffer();
			strSql.append(" select resultdesc," + subSetMenu + ",count(*) num from per_result_" + plan_id + " where 1=1 ");						
			strSql.append(getUserViewWhere(this.userView)); // 操作单位或管理范围内的人	
			if(a_code!=null && a_code.trim().length()>2)
			{								
				if(a_code.indexOf("UN")!=-1)
				{
					strSql.append(" and b0110 like '" + a_code.substring(2, a_code.length()) + "%'");
				}else if(a_code.indexOf("UM")!=-1)
				{
					strSql.append(" and e0122 like '" + a_code.substring(2, a_code.length()) + "%'");
				}else if(a_code.indexOf("Usr")!=-1)
				{
					strSql.append(" and a0100 like '" + a_code.substring(3, a_code.length()) + "%'");
				}else if(a_code.indexOf("@K")!=-1 || a_code.indexOf("@k")!=-1)
				{
					strSql.append(" and e01a1 like '" + a_code.substring(2, a_code.length()) + "%'");
				}
			}			
			strSql.append(" group by resultdesc," + subSetMenu + " ");
						
			rowSet = dao.search(strSql.toString());	
			while(rowSet.next())
			{								
				String resultdesc = isNull(rowSet.getString("resultdesc"));												
				if(dataMap.get(resultdesc)!=null)
				{
					LazyDynaBean abean = (LazyDynaBean)dataMap.get(resultdesc);
					abean.set(isNull(rowSet.getString(subSetMenu)), isNull(rowSet.getString("num")));
					dataMap.put(resultdesc, abean);	
				}
				else
				{
					LazyDynaBean abean = new LazyDynaBean();
					abean.set(isNull(rowSet.getString(subSetMenu)), isNull(rowSet.getString("num")));
					dataMap.put(resultdesc,abean);
				}

			}
			
			
/*			
			LoadXml loadxml = new LoadXml(this.con,plan_id);
			Hashtable htxml = new Hashtable();		
			htxml = loadxml.getDegreeWhole();
			String gradeClass = (String)htxml.get("GradeClass"); //等级分类ID
			
			HashMap dataMap = new HashMap();
			StringBuffer sql = new StringBuffer();
			sql.append("select pds.id,pds.itemname from per_degree pd,per_degreedesc pds where pd.degree_id=pds.degree_id and pd.degree_id="+gradeClass);									
			rowSet = dao.search(sql.toString());
			while(rowSet.next())
			{
				LazyDynaBean bean=new LazyDynaBean();												    
				ArrayList subCodeList = (ArrayList)map.get(rowSet.getString("id")); 
				
				if(subCodeList!=null && subCodeList.size()>0)
				{					
				 	for(int i=0;i<subCodeList.size();i++)
				 	{
				 		LazyDynaBean abean=(LazyDynaBean)subCodeList.get(i);
		 				String grade_id=(String)abean.get("grade_id");
		 				String subCode=(String)abean.get("subCode");
		 				String number=(String)abean.get("number");
		 				
		 			//	if(subCode!=null && subCode.trim().length()>0)
		 					bean.set(subCode, number);
				 	}
				 	dataMap.put(rowSet.getString("itemname"),bean);		
				}
			}
*/			
			
			
			ArrayList tempList = new ArrayList();
			for(int j=0;j<codeList.size();j++)
			{
				CommonData data=(CommonData)codeList.get(j);
				String categoryName =data.getDataName();
				categoryName = warpRowStr(categoryName,10);
				
				tempList = new ArrayList();				
				Set keySet=dataMap.keySet();
				java.util.Iterator t=keySet.iterator();
				while(t.hasNext())
				{
					String strKey = (String)t.next();  //键值	    
					LazyDynaBean ldbean = (LazyDynaBean)dataMap.get(strKey);   //value值   
				
					if(isNull((String)ldbean.get(data.getDataValue()))!=null && isNull((String)ldbean.get(data.getDataValue())).trim().length()>0)
						tempList.add(new CommonData(isNull((String)ldbean.get(data.getDataValue())),strKey));
					else
						tempList.add(new CommonData(isNull("0"),strKey));
				}								
				 
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("categoryName", isNull(categoryName));
				abean.set("dataList",tempList);
				list.add(abean);
				 
			}						
			
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
	 * 考核等级数据
	 * @param planid
	 * @return
	 */
	public ArrayList getDegreeList(String plan_id)
	{
		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.con);

			LoadXml loadxml = new LoadXml(this.con,plan_id);
			Hashtable htxml = new Hashtable();		
			htxml = loadxml.getDegreeWhole();
			String gradeClass = (String)htxml.get("GradeClass"); //等级分类ID
			
			StringBuffer sql = new StringBuffer();
			sql.append("select pds.id,pds.itemname from per_degree pd,per_degreedesc pds where pd.degree_id=pds.degree_id and pd.degree_id="+gradeClass);									
			rowSet = dao.search(sql.toString());
			while(rowSet.next())
			{				
				LazyDynaBean bean = new LazyDynaBean();		
		    	bean.set("gradeId", isNull(rowSet.getString(1)));
		    	bean.set("gradeName", isNull(rowSet.getString(2)));								
				list.add(bean);
			}
			
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
     * 取得考核对象类别列表
     * @return
     */
	public HashMap getObjectTypeMap()
	{	
		HashMap dataMap = new HashMap();
		RowSet rowSet = null;
		try
		{
		    ContentDAO dao = new ContentDAO(this.con);
		    String sql = "select body_id,name from per_mainbodyset where body_type=1 and status=1";				
				sql+=" and (object_type=2 or object_type is null)";				
			//	sql+=" order by seq ";		    
		    
		    rowSet = dao.search(sql);
		    while(rowSet.next())
		    {
		    	dataMap.put(rowSet.getString("body_id"), rowSet.getString("name")+"（"+rowSet.getString("body_id")+"）");				
		    }
		    if(rowSet!=null)
				rowSet.close();
		
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return dataMap;
	}

	/**
	 * 反查统计图数据
	 * @param planid
	 * @return
	 */
	public ArrayList getReverseResultList(String plan_id,String orgCode,String degreeName,String flag,String onlyFild,String subSetMenu,String greeName)
	{
		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.con);			
			LoadXml loadxml = new LoadXml(this.con,plan_id);
			Hashtable htxml = new Hashtable();		
			htxml = loadxml.getDegreeWhole();
			String gradeClass = (String)htxml.get("GradeClass"); //等级分类ID			
			
			StringBuffer sql = new StringBuffer();
			sql.append("select pr.b0110,pr.e0122,pr.e01a1,pr.object_id,pr.a0101,pr.mateSurmise,pr.resultdesc ");			
			if(onlyFild!=null && onlyFild.trim().length()>0)
				sql.append(",ua." + onlyFild + " ");						
			sql.append(" from per_result_" + plan_id + " pr,usra01 ua where pr.object_id=ua.a0100 ");
			
			if(subSetMenu!=null && subSetMenu.trim().length()>0 && !"-1".equalsIgnoreCase(subSetMenu))
		    {
			//	sql.append(" and pr.grade_id=(select id from per_degreedesc where degree_id='" + gradeClass + "' and itemname like '" + SafeCode.decode(greeName) + "%') ");
				/**
				 * 说明：选择了按照代码类型分析时，greeName为等级名称；degreeName为分类名称
				 * 之前代码在这个if分支中greeName和degreeName写反了
				 * haosl
				 */
				
				degreeName = SafeCode.decode(degreeName);
				greeName = SafeCode.decode(greeName);
				if(greeName==null || greeName.trim().length()<=0)
		    		sql.append(" and (pr.resultdesc is null or pr.resultdesc='') ");
		    	else
		    		sql.append(" and pr.resultdesc = '" + greeName + "' ");
				
				sql.append(" and pr."+ subSetMenu +"='" + degreeName.substring(degreeName.indexOf("（")+1,degreeName.indexOf("）")) + "' ");	
				
		    }else
		    {
		    	/**
				 * 说明：未选择按照代码类型分析时degreeName为等级名称
				 * haosl
				 */
		    	if(SafeCode.decode(degreeName)==null || SafeCode.decode(degreeName).trim().length()<=0)
		    		sql.append(" and (pr.resultdesc is null or pr.resultdesc='') ");
		    	else
		    		sql.append(" and pr.resultdesc = '" + SafeCode.decode(degreeName) + "' ");
		    //	sql.append(" and pr.grade_id=(select id from per_degreedesc where degree_id='" + gradeClass + "' and itemname like '" + SafeCode.decode(degreeName) + "%') ");		    	
		    }			
			sql.append(getUserViewUsrWhere(this.userView)); // 操作单位或管理范围内的人	
			if(orgCode!=null && orgCode.trim().length()>2)
			{								
				if(orgCode.indexOf("UN")!=-1)
				{
					sql.append(" and pr.b0110 like '" + orgCode.substring(2, orgCode.length()) + "%'");
				}else if(orgCode.indexOf("UM")!=-1)
				{
					sql.append(" and pr.e0122 like '" + orgCode.substring(2, orgCode.length()) + "%'");
				}else if(orgCode.indexOf("Usr")!=-1)
				{
					sql.append(" and pr.a0100 like '" + orgCode.substring(3, orgCode.length()) + "%'");
				}else if(orgCode.indexOf("@K")!=-1 || orgCode.indexOf("@k")!=-1)
				{
					sql.append(" and pr.e01a1 like '" + orgCode.substring(2, orgCode.length()) + "%'");
				}
			}			
			sql.append(" order by pr.a0000 ");
						
			rowSet = dao.search(sql.toString());
			while(rowSet.next())
			{				
				LazyDynaBean bean = new LazyDynaBean();		
		    	bean.set("b0110", AdminCode.getCodeName("UN",isNull(rowSet.getString("b0110"))));
		    	bean.set("e0122", AdminCode.getCodeName("UM",isNull(rowSet.getString("e0122"))));	
		    	bean.set("e01a1", AdminCode.getCodeName("@K",isNull(rowSet.getString("e01a1"))));	
		    	bean.set("object_id", isNull(rowSet.getString("object_id")));	
		    	bean.set("a0101", isNull(rowSet.getString("a0101")));	
		    	if (isNull(rowSet.getString("mateSurmise")) == null || isNull(rowSet.getString("mateSurmise")).trim().length()<=0)
				    bean.set("mateSurmise", "");
				else
				    bean.set("mateSurmise",PubFunc.multiple(isNull(rowSet.getString("mateSurmise")),"100", 2)+"%");		    		
		    	bean.set("resultdesc", isNull(rowSet.getString("resultdesc")));	
		    	if(onlyFild!=null && onlyFild.trim().length()>0)
		    		bean.set("onlyFild", isNull(rowSet.getString(onlyFild)));
		    	
				list.add(bean);
			}
			
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}	
	
	public String isNull(String str)
    {
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str))
		    str = "";
		return str;
    }
    
	/**换行处理横坐标*/
	public String warpRowStr(String name,int num)
	{
		if(name!=null && name.trim().length()>0 && (name.indexOf(":")!=-1))
			name = name.substring(0,name.indexOf(":"));
		else if(name!=null && name.trim().length()>0 && (name.indexOf("：")!=-1))
			name = name.substring(0,name.indexOf("："));		
/*		
		if(name.length()>num)
		{
			int div = name.length()/num;
			String temp = "";
			for(int index=0;index<div;index++)
			{
				temp+=name.substring(index*num, (index+1)*num)+"\r";
			}
			temp+=name.substring(div*num);
			name=temp;
		}
*/		
		return name;
	}
	
	/**
	 * 根据用户权限获得计划列表 先看操作单位 再看管理范围
	 * 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
	 */
	public String getUserViewWhere(UserView userView)
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
						tempSql.append(" or b0110 like '" + temp[i].substring(2) + "%'");
					else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or e0122 like '" + temp[i].substring(2) + "%'");
				}
				if(tempSql!=null && tempSql.toString().trim().length()>0)
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
						buf.append(" and b0110 like '" + codevalue + "%'");
					else if("UM".equalsIgnoreCase(codeid))
						buf.append(" and e0122 like '" + codevalue + "%'");
					else if("@K".equalsIgnoreCase(codeid))
						buf.append(" and e01a1 like '" + codevalue + "%'");
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
	 * 根据用户权限获得计划列表 先看操作单位 再看管理范围
	 * 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
	 */
	public String getUserViewUsrWhere(UserView userView)
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
						tempSql.append(" or pr.b0110 like '" + temp[i].substring(2) + "%'");
					else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or pr.e0122 like '" + temp[i].substring(2) + "%'");
				}
				if(tempSql!=null && tempSql.toString().trim().length()>0)
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
						buf.append(" and pr.b0110 like '" + codevalue + "%'");
					else if("UM".equalsIgnoreCase(codeid))
						buf.append(" and pr.e0122 like '" + codevalue + "%'");
					else if("@K".equalsIgnoreCase(codeid))
						buf.append(" and pr.e01a1 like '" + codevalue + "%'");
					else
						buf.append(" and pr.b0110 like '" + codevalue + "%'");
						
				} else
					buf.append(" and 1=2 ");
			}
			str = buf.toString();
		}

		return str;		
	}
	
	// 根据模板编号或指标编号判断是能力素质模块还是绩效模块
	public boolean getComOrPer(String tempoint_id,String opt)
	{
		boolean json = false;
		RowSet rowSet = null;
		String sql = "";
		try
		{
			ContentDAO dao = new ContentDAO(this.con);	
			if("temp".equalsIgnoreCase(opt))
				sql = "select pts.subsys_id from per_template_set pts,per_template pt where pts.template_setid=pt.template_setid and pt.template_id='" + tempoint_id + "' ";
			else
				sql = "select pts.subsys_id from per_pointset pts,per_point pt where pts.pointsetid=pt.pointsetid and pt.point_id='" + tempoint_id + "' ";
			rowSet = dao.search(sql);
			while(rowSet.next())
		    {
				String subsys_id = rowSet.getString("subsys_id");
		    	if(subsys_id!=null && subsys_id.trim().length()>0 && "35".equalsIgnoreCase(subsys_id))
		    		json = true;
		    }		
			if(rowSet!=null)
				rowSet.close();			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return json;
	}
}