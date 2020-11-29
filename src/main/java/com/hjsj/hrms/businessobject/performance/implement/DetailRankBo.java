package com.hjsj.hrms.businessobject.performance.implement;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * <p>Title:DetailRankBo.java</p>
 * <p>Description>:考核实施/评价关系明细及权重</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Sep 13, 2011 09:15:57 AM</p>
 * <p>@author: JinChunhai </p>
 * <p>@version: 5.0</p>
 */

public class DetailRankBo 
{
	private Connection conn = null;
	private UserView userView = null;
	
	private String plan_id = "";    // 考核计划号
	private RecordVo planVo = null;  // 考核计划信息
	private String template_id = "";  // 计划对应模板号
	private RecordVo templateVo = null; // 考核计划对应模板信息
	private String object_ids = "";    // 符合条件的考核对象id串	
	private String tablename = "";    // 临时表名
	private String mainbodyRank = "";  // 考核主体权重串
	
	private ArrayList dataList=new ArrayList();  // 表体数据
	private ArrayList objectTypeList=new ArrayList();  // 取得计划对应的对象类别列表
	private ArrayList mainbodyTypeList=new ArrayList();  // 取得主体类别列表
	private HashMap mainbodyMap = new HashMap(); // 取得范围内考核对象对应的考核主体
	private HashMap mainbodyDefaultRankMap = new HashMap(); // 取得设置的主体默认权重
	private HashMap mainbodyRankMap = new HashMap(); // 取得设置的动态主体权重
	private int columnWidth = 100; // 列宽
	
	public DetailRankBo(Connection a_con,UserView userView, String planid)
	{
		this.plan_id = planid;
		this.conn = a_con;			
		this.planVo = getPerPlanVo(this.plan_id);
		this.template_id = this.planVo.getString("template_id");
		this.templateVo = getTemplateVo(this.template_id);
		this.userView=userView;			
	}
	
	
	/**
     * 取得 评价关系明细及权重页面信息
     * @return
     */
	public String getDetailHeadHtml(String code)
	{	
		StringBuffer html = new StringBuffer("<table id='tbl' style='border-top:none;border-left:none;border-right:none;' width='100%' border='0' cellspacing='0' align='center' cellpadding='0' class='ListTableF' >");		
		try
		{	
		
			ContentDAO dao = new ContentDAO(this.conn);
			
			/** 取得计划对应的对象类别列表 */
			ArrayList objectTypeList = getPerObjectSetList(code); 
//			this.objectTypeList = objectTypeList;			
			
			/** 取得范围内考核对象对应的考核主体 */
//			HashMap mainbodyMap = getMainBodyMap();	
			this.mainbodyMap = getMainBodyMap();
			
			/** 取得设置的主体默认权重 */
//			HashMap mainbodyDefaultRankMap = getMainBodyDefaultRankMap();	
			this.mainbodyDefaultRankMap = getMainBodyDefaultRankMap();
			
		    /** 取得设置的动态主体权重 */
//			HashMap mainbodyRankMap = getMainBodyRankMap();
			this.mainbodyRankMap = getMainBodyRankMap();
			
			/** 取得主体类别列表 */
//			ArrayList mainbodyTypeList = getPerMainBodySetList(); 
			this.mainbodyTypeList = getPerMainBodySetList();
			
			
			//  新建临时表 
			this.tablename = "t#_"+this.userView.getUserName()+"_per_dr";			
			DbWizard dbWizard = new DbWizard(this.conn);	
			// 此临时表若存在就先drop掉
			if(dbWizard.isExistTable(this.tablename,false))
			{
				dbWizard.dropTable(this.tablename);				
			}			
			Table table = new Table(this.tablename);	
			
		    table.addField(getField("plan_id", "I", 4, false));
		    table.addField(getField("groupCom", "A", 20, false));
		    table.addField(getField("b0110", "A", 30, false));
		    table.addField(getField("e0122", "A", 30, false));
		    table.addField(getField("e01a1", "A", 50, false));		    
		    table.addField(getField("object_id", "A", 50, false));	
		    table.addField(getField("object_name", "A", 100, false));
		    table.addField(getField("mainbody_id", "M", 3000, false));
		    table.addField(getField("objBody_id", "A", 50, false));	
		    table.addField(getField("objbody_name", "A", 50, false));
		    table.addField(getField("rank", "A", 100, false));
		    table.addField(getField("A0000", "I", 8, false));
		    
			dbWizard.createTable(table);
			
			// 向临时表中添加数据
			StringBuffer sql = new StringBuffer("select ");
			sql.append(" po.plan_id,'01',po.b0110,po.e0122,po.e01a1,po.object_id,po.a0101,po.a0000,pm.body_id,pm.name ");
			sql.append(" from per_object po LEFT JOIN per_mainbodyset pm ON pm.body_id=po.body_id ");
			sql.append(" where plan_id=" + this.plan_id);
			sql.append(getPrivObjWhere(this.userView));
			if(code!=null && code.trim().length()>0)
			{
				if(AdminCode.getCodeName("UN",code)!=null&&AdminCode.getCodeName("UN",code).length()>0) {
                    sql.append(" and po.b0110 like '"+code+"%'");
                } else if(AdminCode.getCodeName("UM",code)!=null&&AdminCode.getCodeName("UM",code).length()>0) {
                    sql.append(" and po.e0122 like '"+code+"%'");
                }
			}
			sql.append(" order by po.a0000,po.object_id ");
						
			StringBuffer bufSql = new StringBuffer();	
			bufSql.append("insert into "+ this.tablename +" (plan_id,groupCom,b0110,e0122,e01a1,object_id,object_name,A0000,objBody_id,objbody_name) "); // values (?,?"+ sqlValue +") ");
			bufSql.append(sql.toString());						
			dao.insert(bufSql.toString(), new ArrayList());	
			 						
			// 向临时表中添加 mainbody_id		
			HashMap objectMainBodysMap = getObjectMainBodysMap();
			Set keySet=objectMainBodysMap.keySet();
			java.util.Iterator t=keySet.iterator();
            List values = new ArrayList();
			while(t.hasNext())
			{
				String strKey = (String)t.next();  //键值	    
				String strValue = (String)objectMainBodysMap.get(strKey);   //value值   
                String updateSql = "update " + this.tablename + " set mainbody_id=? where plan_id = ? and object_id = ? ";
                values.clear();
                values.add(strValue);
                values.add(this.plan_id);
                values.add(strKey);
                dao.update(updateSql,values);
			}
			
			// 向临时表中添加 主体权重	
			String updateSql = "update " + this.tablename + " set rank='" + this.mainbodyRank + "' where plan_id = " + this.plan_id;
			dao.update(updateSql);
			
			// 向临时表中添加 动态主体权重			
			HashMap mainRankMap = getMainRankMap();
			Set keymapSet=mainRankMap.keySet();
			java.util.Iterator te=keymapSet.iterator();
			while(te.hasNext())
			{
				String strKey = (String)te.next();  //键值	    
				String strValue = (String)mainRankMap.get(strKey);   //value值   
				
				String strSql = "update " + this.tablename + " set rank='" + strValue + "' where plan_id = " + this.plan_id + " and (object_id = '" + strKey + "' or e01a1 = '" + strKey + "' or e0122 = '" + strKey + "' or b0110 = '" + strKey + "' or groupCom = '" + strKey + "' ) ";
				dao.update(strSql);
			}			
			
			// 分组查询考核对象
			ArrayList perObjectList = getPerObjectList();			
			this.objectTypeList = perObjectList;	
			
		    // 表头Html
		    html.append(getTableHeadHtml(perObjectList));		    

/*		    
		    // 表体信息				    
		    if(objectTypeList.size()>0)
			{	    		    		    		    		    		    
			    for (int i = 0; i < mainbodyTypeList.size(); i++)
			    {
			    	LazyDynaBean abean = (LazyDynaBean) mainbodyTypeList.get(i);
					String body_id = (String) abean.get("body_id");
					String number = (String) abean.get("number");  // 序号
					String name = (String) abean.get("name");     // 考核主体类别
											
					float defaultBodyRank = ((Float) mainbodyDefaultRankMap.get(body_id)).floatValue(); 														
					
					LazyDynaBean zbean=null;
					for(int j=0;j<objectTypeList.size();j++)
					{
						float bodyRank = defaultBodyRank;
						String mainbodyRank = "";
						
						zbean=(LazyDynaBean)objectTypeList.get(j);
						String object_id=(String)zbean.get("object_id");
						String a0101=(String)zbean.get("a0101");	
											
						String mainbodyName = (String) mainbodyMap.get(object_id + ":" + body_id); // 考核主体名单	
						String mainbodyNum = "";
						if(mainbodyName!=null && mainbodyName.trim().length()>0)
						{
							String[] items = mainbodyName.split(",");
							mainbodyNum = String.valueOf(items.length);   // 考核主体人数	
						}else
						{
							mainbodyName = "";
						}									
						
						Float rank = (Float) mainbodyRankMap.get(object_id + ":" + body_id);					
						if (rank != null && rank.toString().trim().length()>0) //设置了动态主体权重
							bodyRank = rank.floatValue();						
						if(Float.toString(bodyRank)!=null && Float.toString(bodyRank).trim().length()>0 && !Float.toString(bodyRank).equalsIgnoreCase("0.0"))					
							mainbodyRank = Float.toString(bodyRank);					
											
																					
					}
					
					LazyDynaBean dataBean = new LazyDynaBean();
					dataBean.set("object_id",object_id);
					dataBean.set("director",director);
					dataBean.set("a0101",a0101);
					dataBean.set("score_adjust",score_adjust);
					dataBean.set("a_pointList",a_pointList);			
					dataBean.set("planid",this.plan_id);	
					dataBean.set("planlist",planlist);	
					dataBean.set("planObjectScoreMap",planObjectScoreMap);	
					dataBean.set("columnWidth",String.valueOf(columnWidth));	
					dataBean.set("isCorrectScoreObj",isCorrectScoreObj);	
					
					dataBean.set("original_score", (String) abean.get("original_score"));
					dataBean.set("score", (String) abean.get("score"));
					dataBean.set("umPaiMing", (String) abean.get("org_ordering")+"/"+(String) abean.get("org_GrpNum"));
					dataBean.set("exs_grpavg", (String) abean.get("exs_grpavg"));
					
					dataBean.set("exS_GrpMax", (String) abean.get("exS_GrpMax"));
					dataBean.set("exS_GrpMin", (String) abean.get("exS_GrpMin"));
					dataBean.set("paiming", (String) abean.get("ordering")+"/"+(String) abean.get("ex_GrpNum"));			
					
					dataBean.set("exx_object", (String) abean.get("exX_object"));
					dataBean.set("desc", (String) abean.get("resultdesc"));
					dataBean.set("addScore", (String) abean.get("addScore"));
					dataBean.set("minusScore", (String) abean.get("minusScore"));
					dataBean.set("evalremark", "");
			
					this.dataList.add(dataBean);
				
				
				}
		    }
*/						
			
		} catch (Exception e)
		{
		    e.printStackTrace();
		}		
		
		return html.toString();		
	}
	
	/**
	 * 表头Html    
     * @return
     */
	public String getTableHeadHtml(ArrayList objectTypeList)
	{	
		StringBuffer a_tableHtml = new StringBuffer("");
							
		/** 画第一层表头 */
		a_tableHtml.append("<thead><tr> ");					
		a_tableHtml.append(getTh(ResourceFactory.getProperty("jx.implement.mainbodyORobjectMessage"), 4, 1, "mainORobject","cell_locked2 common_background_color common_border_color","logo")); 
		if(objectTypeList.size()>0) {
            a_tableHtml.append(getTh(ResourceFactory.getProperty("jx.implement.objectAndPersonNumber"), 3*objectTypeList.size(), 2, "a","header_locked common_background_color common_border_color","logo"));
        }
		a_tableHtml.append("</tr> \n ");
		
		/** 画第二层表头 */
		a_tableHtml.append("<tr>");
		LazyDynaBean abean=null;
		for(int i=0;i<objectTypeList.size();i++)
		{
			abean=(LazyDynaBean)objectTypeList.get(i);
//			String body_id=(String)abean.get("body_id");
			String name=(String)abean.get("name");	
			if(name==null || name.trim().length()<=0) {
                name = "[未设]";
            }
			
			a_tableHtml.append(getTh(name, 3, 2, null,"header_locked common_background_color common_border_color","logo"));  //对象类别的名称
		}
		a_tableHtml.append("</tr>\n");
		
		
		/** 画第三层表头 */
		a_tableHtml.append("<tr>");
		for(int i=0;i<objectTypeList.size();i++)
		{						
			a_tableHtml.append(getTh("考核对象名单", 1, 2, null,"header_locked common_background_color common_border_color","logo")); 
			if(this.planVo.getInt("object_type")==2) {
                a_tableHtml.append(getTh("人数", 2, 2, null,"header_locked common_background_color common_border_color","logo"));
            } else {
                a_tableHtml.append(getTh("个数", 2, 2, null,"header_locked common_background_color common_border_color","logo"));
            }
		}
		a_tableHtml.append("</tr>\n");
		
		
		
		/** 画第四层表头 */
		a_tableHtml.append("<tr>");
		LazyDynaBean dbean=null;
		for(int i=0;i<objectTypeList.size();i++)
		{
			dbean=(LazyDynaBean)objectTypeList.get(i);
//			String object_id=(String)dbean.get("object_id");
			String a0101=(String)dbean.get("a0101");						
			String[] s_str_arr=a0101.split(",");
			
			a_tableHtml.append(getTh(a0101, 1, 2, null,"header_locked common_background_color common_border_color","sign")); 
			a_tableHtml.append(getTh(String.valueOf(s_str_arr.length), 2, 2, null,"header_locked common_background_color common_border_color","logo")); 
		}
		a_tableHtml.append("</tr>\n");
		
		
		
		/** 画第五层表头 */
		a_tableHtml.append("<tr>");
		a_tableHtml.append(getTh("序号", 1, 1, null,"cell_locked2 common_background_color common_border_color","logo")); 
		a_tableHtml.append(getTh("考核主体类别", 1, 1, null,"cell_locked2 common_background_color common_border_color","logo")); 
		for(int i=0;i<objectTypeList.size();i++)
		{											
			a_tableHtml.append(getTh("考核主体名单", 1, 1, null,"header_locked common_background_color common_border_color","logo")); 
			a_tableHtml.append(getTh("人数", 1, 1, null,"header_locked common_background_color common_border_color","logo")); 
			a_tableHtml.append(getTh("权重", 1, 1, null,"header_locked common_background_color common_border_color","logo")); 
		}
		a_tableHtml.append("</tr>\n");
		
		
		a_tableHtml.append("</thead>\n");
		
		return a_tableHtml.toString();
	}
	// 画表头
	public String getTh(String name, int lays, int opt, String idname,String className,String logoSign)
    {

		StringBuffer sb = new StringBuffer("");
		sb.append("<td class='"+className+"' valign='middle' align='center' ");
		if (idname != null && idname.length() > 0) {
            sb.append(" id='" + idname + "' ");
        }
		if (lays > 0)
		{
		    if (opt == 1)
		    {
		    	if (idname != null && idname.length() > 0 && "mainORobject".equalsIgnoreCase(idname)) {
                    sb.append("rowspan='" + lays + "' colspan='2' width='(2*" + columnWidth + ")'");
                } else {
                    sb.append("rowspan='" + lays + "' width='" + columnWidth + "'");
                }
		    }
		    else {
                sb.append("colspan='" + lays + "' height='35'");
            }
		}
		if("sign".equalsIgnoreCase(logoSign)) {
            sb.append(" > ");
        } else {
            sb.append(" nowrap> ");
        }
		sb.append(name);
		sb.append("</td>");
		
		return sb.toString();
    }
	
	/**
     * 分组查询考核对象
     * @return
     */
	public ArrayList getPerObjectList()
	{	
		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		try
		{		
			ContentDAO dao = new ContentDAO(this.conn);
			LazyDynaBean abean = null;
			String strSql="select * from " + this.tablename + " order by objBody_id desc,a0000,object_id ";
			rowSet = dao.search(strSql);
			int count = 0;
			String groupCom = "";
			String b0110 = "";
			String e0122 = "";
			String e01a1 = "";
			String object_id = "";
			String objectName = "";
			String objBody_id = "";
			String objbody_name = "";
			String mainbody_id = "";
			String rank = "";
			while(rowSet.next())
			{															
				if(count == 0)
				{
					groupCom = isNull(rowSet.getString("groupCom"));
					b0110 = isNull(rowSet.getString("b0110"));
					e0122 = isNull(rowSet.getString("e0122"));
					e01a1 = isNull(rowSet.getString("e01a1"));
					objectName = isNull(rowSet.getString("object_name"));	
					object_id = isNull(rowSet.getString("object_id"));
					objBody_id = isNull(rowSet.getString("objBody_id"));
					objbody_name = isNull(rowSet.getString("objbody_name"));
					mainbody_id = isNull(Sql_switcher.readMemo(rowSet,"mainbody_id"));
					rank = isNull(rowSet.getString("rank"));
					if(rowSet.next()==false)
					{
						abean = new LazyDynaBean();	
						abean.set("a0101", objectName);
						abean.set("body_id", objBody_id);
						abean.set("groupCom", groupCom);
						abean.set("b0110", b0110);
						abean.set("e0122", e0122);
						abean.set("e01a1", e01a1);
						abean.set("object_id", object_id);
						abean.set("name", objbody_name);						
						list.add(abean);
					}
					rowSet.previous();					
					count++;
				}else
				{
					// 1：相同的考核对象类别；2：相同的考核主体；3：相同的权重  分成一组
					if((objBody_id.equalsIgnoreCase(isNull(rowSet.getString("objBody_id")))) && (mainbody_id.equalsIgnoreCase(isNull(rowSet.getString("mainbody_id")))) && (rank.equalsIgnoreCase(isNull(rowSet.getString("rank")))))
					{
						objectName += "," + isNull(rowSet.getString("object_name"));
						
//						objectName = isNull(rowSet.getString("object_name"));
						groupCom = isNull(rowSet.getString("groupCom"));
						b0110 = isNull(rowSet.getString("b0110"));
						e0122 = isNull(rowSet.getString("e0122"));
						e01a1 = isNull(rowSet.getString("e01a1"));
						object_id = isNull(rowSet.getString("object_id"));
						objBody_id = isNull(rowSet.getString("objBody_id"));
						objbody_name = isNull(rowSet.getString("objbody_name"));
						mainbody_id = isNull(Sql_switcher.readMemo(rowSet,"mainbody_id"));
						rank = isNull(rowSet.getString("rank"));
						
						if(rowSet.next()==false)
						{
							abean = new LazyDynaBean();	
							abean.set("a0101", objectName);
							abean.set("body_id", objBody_id);
							abean.set("groupCom", groupCom);
							abean.set("b0110", b0110);
							abean.set("e0122", e0122);
							abean.set("e01a1", e01a1);
							abean.set("object_id", object_id);
							abean.set("name", objbody_name);						
							list.add(abean);
						}
						rowSet.previous();
						count++;
						
					}else
					{
						abean = new LazyDynaBean();	
						abean.set("a0101", objectName);
						abean.set("body_id", objBody_id);
						abean.set("groupCom", groupCom);
						abean.set("b0110", b0110);
						abean.set("e0122", e0122);
						abean.set("e01a1", e01a1);
						abean.set("object_id", object_id);
						abean.set("name", objbody_name);						
						list.add(abean);
						
						groupCom = isNull(rowSet.getString("groupCom"));
						b0110 = isNull(rowSet.getString("b0110"));
						e0122 = isNull(rowSet.getString("e0122"));
						e01a1 = isNull(rowSet.getString("e01a1"));
						object_id = isNull(rowSet.getString("object_id"));
						objectName = isNull(rowSet.getString("object_name"));								
						objBody_id = isNull(rowSet.getString("objBody_id"));
						objbody_name = isNull(rowSet.getString("objbody_name"));
						mainbody_id = isNull(Sql_switcher.readMemo(rowSet,"mainbody_id"));
						rank = isNull(rowSet.getString("rank"));
						
						count++;
						if(rowSet.next()==false)
						{
							abean = new LazyDynaBean();	
							abean.set("a0101", objectName);
							abean.set("body_id", objBody_id);
							abean.set("groupCom", groupCom);
							abean.set("b0110", b0110);
							abean.set("e0122", e0122);
							abean.set("e01a1", e01a1);
							abean.set("object_id", object_id);
							abean.set("name", objbody_name);						
							list.add(abean);
						}
						rowSet.previous();
					}					
				}										
			}
				    
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return list;
	}
	/**
     * 取得计划对应的对象类别列表
     * @return
     */
	public ArrayList getPerObjectSetList(String code)
	{	
		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		try
		{		
			StringBuffer sql = new StringBuffer("select ");
			sql.append(" po.object_id,po.a0101,pm.body_id,pm.name ");
			sql.append(" from per_object po LEFT JOIN per_mainbodyset pm ON pm.body_id=po.body_id ");
			sql.append(" where plan_id=" + this.plan_id);
			sql.append(getPrivObjWhere(this.userView));
			if(code!=null && code.trim().length()>0)
			{
				if(AdminCode.getCodeName("UN",code)!=null&&AdminCode.getCodeName("UN",code).length()>0) {
                    sql.append(" and po.b0110 like '"+code+"%'");
                } else if(AdminCode.getCodeName("UM",code)!=null&&AdminCode.getCodeName("UM",code).length()>0) {
                    sql.append(" and po.e0122 like '"+code+"%'");
                }
			}
			sql.append(" order by pm.seq,po.object_id ");
			
		    ContentDAO dao = new ContentDAO(this.conn);
		    rowSet = dao.search(sql.toString());
		    LazyDynaBean abean = null;
		    StringBuffer buf = new StringBuffer();
		    while (rowSet.next())
		    {
				abean = new LazyDynaBean();								
												
				String object_id = rowSet.getString("object_id") == null ? "" : rowSet.getString("object_id");				
				buf.append(",'" + object_id + "'");
				
				abean.set("object_id", rowSet.getString("object_id") == null ? "" : rowSet.getString("object_id"));
				abean.set("a0101", rowSet.getString("a0101") == null ? "" : rowSet.getString("a0101"));
				abean.set("body_id", rowSet.getString("body_id") == null ? "" : rowSet.getString("body_id"));
				abean.set("name", rowSet.getString("name") == null ? "" : rowSet.getString("name"));
				
				list.add(abean);
		    }
		    
		    if(buf.toString()!=null && buf.toString().trim().length()>0) {
                this.object_ids = buf.substring(1).toString();
            }
		    
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return list;
	}
			
	/**
     * 取得计划对应的主体类别列表
     * @param planid
     * @return
     */
	public ArrayList getPerMainBodySetList()
	{	
		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		try
		{
			StringBuffer sql = new StringBuffer("select ");
			sql.append(" pm.* ");
			sql.append(" from per_plan_body pb,per_mainbodyset pm where pb.body_id=pm.body_id ");
			sql.append(" and pb.plan_id=" + this.plan_id);
			sql.append(" order by pm.seq ");
				
		    ContentDAO dao = new ContentDAO(this.conn);
		    rowSet = dao.search(sql.toString());
		    LazyDynaBean abean = null;
		    int num = 1;
		    while (rowSet.next())
		    {
				abean = new LazyDynaBean();
				abean.set("body_id", rowSet.getString("body_id"));
				abean.set("name", rowSet.getString("name"));
				abean.set("number", String.valueOf(num));
				num++;
				if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                    abean.set("level", rowSet.getString("level_o"));
                } else {
                    abean.set("level", rowSet.getString("level"));
                }
				list.add(abean);
		    }
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return list;
	}
	
	/**
     * 取得范围内考核对象对应的考核主体
     * @return
     */
	private HashMap getMainBodyMap()
	{
		HashMap mainBodyMap = new HashMap();
		RowSet rowSet = null;
		try
		{		
			StringBuffer sql = new StringBuffer("select ");
			sql.append(" object_id,mainbody_id,a0101,body_id ");
			sql.append(" from per_mainbody where plan_id ='" + this.plan_id + "' ");
			if(this.object_ids!=null && this.object_ids.trim().length()>0) {
                sql.append(" and object_id IN (" + getObject_ids() + ") ");
            }
			sql.append(" order by object_id,body_id,mainbody_id");

			ContentDAO dao = new ContentDAO(this.conn);
			rowSet = dao.search(sql.toString());
			while (rowSet.next())
			{
				String object_id = rowSet.getString("object_id");
				String mainbody_id = rowSet.getString("mainbody_id");
				String a0101 = rowSet.getString("a0101") == null ? "" : rowSet.getString("a0101");				
				String body_id = rowSet.getString("body_id");				
				
				if(mainBodyMap.get(object_id + ":" + body_id)==null)
				{
					mainBodyMap.put(object_id + ":" + body_id,a0101);
				}
				else
				{
					String menuss_str=(String)mainBodyMap.get(object_id + ":" + body_id);
					menuss_str+=","+a0101;
					mainBodyMap.put(object_id + ":" + body_id,menuss_str);
				}								
			}
			if(rowSet!=null) {
                rowSet.close();
            }
			
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return mainBodyMap;

	}
	/**
     * 取得范围内考核对象对应的考核主体
     * @return
     */
	private HashMap getObjectMainBodysMap()
	{
		HashMap mainBodyMap = new HashMap();
		RowSet rowSet = null;
		try
		{		
			StringBuffer sql = new StringBuffer("select ");
			sql.append(" object_id,mainbody_id,a0101,body_id ");
			sql.append(" from per_mainbody where plan_id ='" + this.plan_id + "' ");
			if(this.object_ids!=null && this.object_ids.trim().length()>0) {
                sql.append(" and object_id IN (" + getObject_ids() + ") ");
            }
			sql.append(" order by object_id,body_id,mainbody_id ");
						
			ContentDAO dao = new ContentDAO(this.conn);
			rowSet = dao.search(sql.toString());
			while (rowSet.next())
			{
				String object_id = rowSet.getString("object_id");
				String mainbody_id = rowSet.getString("mainbody_id") == null ? "" : rowSet.getString("mainbody_id");					
				
				if(mainBodyMap.get(object_id)==null)
				{
					mainBodyMap.put(object_id,mainbody_id);
				}
				else
				{
					String menuss_str=(String)mainBodyMap.get(object_id);
					menuss_str+=","+mainbody_id;
					mainBodyMap.put(object_id,menuss_str);
				}								
			}
			if(rowSet!=null) {
                rowSet.close();
            }
			
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return mainBodyMap;

	}
	/**
     * 取得设置的主体默认权重
     * @return
     */
	private HashMap getMainBodyDefaultRankMap()
	{
		HashMap defaultRank = new HashMap();
		RowSet rowSet = null;
		String mainRank = "";
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer("select ");
			sql.append(" p.* from per_plan_body p,per_mainbodyset m where p.body_id=m.body_id and p.plan_id = " + this.plan_id);// 得到默认权重
			sql.append(" order by p.body_id");
			/** 得到所有主体权重 */
			rowSet = dao.search(sql.toString());
			while (rowSet.next())
			{	
//				PubFunc.round(String.valueOf(rowSet.getFloat("rank")), 2);
				defaultRank.put(isNull(rowSet.getString("body_id")), new Float(rowSet.getFloat("rank")));	
				mainRank += ("," + Double.toString(rowSet.getDouble("rank")));
			}
			
			if(mainRank!=null && mainRank.trim().length()>0) {
                this.mainbodyRank = mainRank.substring(1);
            } else {
                this.mainbodyRank = "";
            }
			
			if(rowSet!=null) {
                rowSet.close();
            }
			
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return defaultRank;

	}
	
	/**
     * 取得设置的动态主体权重
     * @return
     */
	private HashMap getMainBodyRankMap()
	{
		HashMap rank = new HashMap();
		RowSet rowSet = null;
		try
		{
			String sql = "select * from per_dyna_bodyrank where plan_id ='" + this.plan_id + "'";
			ContentDAO dao = new ContentDAO(this.conn);
			rowSet = dao.search(sql.toString());
			while (rowSet.next())
			{
				String body_id = isNull(rowSet.getString("body_id"));
				String dyna_obj = isNull(rowSet.getString("dyna_obj"));
				rank.put(dyna_obj + ":" + body_id, new Float(rowSet.getFloat("rank")));
			}
			if(rowSet!=null) {
                rowSet.close();
            }
			
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return rank;

	}
	/**
     * 取得设置的动态主体权重
     * @return
     */
	private HashMap getMainRankMap()
	{
		HashMap rank = new HashMap();
		RowSet rowSet = null;
		try
		{
			String sql = "select * from per_dyna_bodyrank where plan_id ='" + this.plan_id + "'";
			ContentDAO dao = new ContentDAO(this.conn);
			rowSet = dao.search(sql.toString());
			while (rowSet.next())
			{
				String dynaRank = isNull(Double.toString(rowSet.getDouble("rank")));
				String dyna_obj = isNull(rowSet.getString("dyna_obj"));
								
				if(rank.get(dyna_obj)==null)
				{
					rank.put(dyna_obj,dynaRank);
				}
				else
				{
					String menuss_str=(String)rank.get(dyna_obj);
					menuss_str+=","+dynaRank;
					rank.put(dyna_obj,menuss_str);
				}				
			}
			if(rowSet!=null) {
                rowSet.close();
            }
			
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return rank;

	}
	/**
	 * 新建指标计算公式临时表字段
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
		} else if ("F".equals(a_type))
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
		if(key) {
            obj.setNullable(false);
        }
		obj.setKeyable(key);	
		return obj;
    }
	/**
     * 取得考核计划信息
     * @param planid
     * @return
     */
	public RecordVo getPerPlanVo(String planid)
	{	
		RecordVo vo = new RecordVo("per_plan");
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    vo.setInt("plan_id", Integer.parseInt(planid));
		    vo = dao.findByPrimaryKey(vo);
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return vo;
	}
	
	/**
     * 取得考核模板信息
     * @param templateid
     * @return
     */
	public RecordVo getTemplateVo(String templateid)
    {
		RecordVo vo = new RecordVo("per_template");
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    vo.setString("template_id", templateid);
		    vo = dao.findByPrimaryKey(vo);
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return vo;
    }
	
	public String isNull(String str)
    {
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str)) {
            str = "";
        }
		return str;
    }
	
	/**先看操作单位 再看管理范围 再看用户所在单位部门
	 * 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
	 */
    public String getPrivObjWhere(UserView userView)
    {
    	StringBuffer buf = new StringBuffer();
    	try
    	{
//			if (!userView.isSuper_admin())
			{
				String operOrg = userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
				if(operOrg!=null && operOrg.length() > 3)
				{
					StringBuffer tempSql = new StringBuffer("");
					String[] temp = operOrg.split("`");
					for (int i = 0; i < temp.length; i++) {
		
						if ("UN".equalsIgnoreCase(temp[i].substring(0, 2))) {
                            tempSql.append(" or po.b0110 like '" + temp[i].substring(2) + "%'");
                        } else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2))) {
                            tempSql.append(" or po.e0122 like '" + temp[i].substring(2) + "%'");
                        }
					}
					buf.append(" and ( " + tempSql.substring(3) + " ) ");
				}
				else if((!userView.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
				{		 					
		//			按管理范围
					String codeid=userView.getManagePrivCode();
					String codevalue=userView.getManagePrivCodeValue();
					String a_code=codeid+codevalue;
					
					if(a_code.trim().length()>0)//说明授权了
					{
						if("UN".equalsIgnoreCase(a_code))//说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串
                        {
                            buf.append(" and 1=1 ");
                        } else
						{
							if(AdminCode.getCodeName("UN",codevalue)!=null&&AdminCode.getCodeName("UN",codevalue).length()>0) {
                                buf.append(" and po.b0110 like '"+codevalue+"%'");
                            } else if(AdminCode.getCodeName("UM",codevalue)!=null&&AdminCode.getCodeName("UM",codevalue).length()>0) {
                                buf.append(" and po.e0122 like '"+codevalue+"%'");
                            }
						}
					}else {
                        buf.append(" and 1=2 ");
                    }
					
				}
//				if(buf.length()==0)//没有设置任何权限
//					buf.append(" and 1=2 ");
			}
			
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
		return buf.toString();
    }		
    
    public ArrayList getObjectTypeList()
	{
		return objectTypeList;
	}   
    public ArrayList getMainbodyTypeList()
	{
		return mainbodyTypeList;
	}
    public HashMap getMainbodyMap()
	{
		return mainbodyMap;
	}
    public HashMap getMainbodyDefaultRankMap()
	{
		return mainbodyDefaultRankMap;
	}
    public HashMap getMainbodyRankMap()
	{
		return mainbodyRankMap;
	}   
    public ArrayList getDataList()
	{
		return dataList;
	}
    public String getObject_ids()
	{
		return object_ids;
	}
    
    
}
