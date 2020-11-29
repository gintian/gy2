package com.hjsj.hrms.businessobject.performance;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse.ExprUtil;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>Title:PerformanceImplementBo.java</p>
 * <p>Description:考核实施</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-06-10 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class PerformanceImplementBo
{
	
    Connection conn = null;
    UserView userView = null;   
    RecordVo planVo = null;
    private ExprUtil exprUtil=new ExprUtil();
    public String object_id="";
    public PerformanceImplementBo(Connection conn)
    {
    	this.conn = conn;
    }

    public PerformanceImplementBo(Connection conn, UserView userView)
    {
		this.conn = conn;
		this.userView = userView;
    }

    public PerformanceImplementBo(Connection conn, UserView userView,String plan_id)
    {
		this.conn = conn;
		this.userView = userView;
		if(plan_id.trim().length()>0) {
            this.planVo = this.getPlanVo(plan_id);
        }
    }   
    
    // 取得计划信息
    public RecordVo getPlanVo(String planid)
    {
		RecordVo vo = new RecordVo("per_plan");
		try
		{
		    vo.setInt("plan_id", Integer.parseInt(planid));
		    ContentDAO dao = new ContentDAO(this.conn);
		    vo = dao.findByPrimaryKey(vo);
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return vo;
    }

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

    public String getPrivCode(UserView userView)
    {

		String code = "";
		if (userView.isSuper_admin()) {
            code = "-1";
        } else if (userView.getManagePrivCodeValue() != null && userView.getManagePrivCodeValue().length() > 0)
		{    
			code = userView.getManagePrivCodeValue();
			if("UN".equalsIgnoreCase(code)) {
                code="-1";
            }
		}
		else
		{
		    String userDeptId = userView.getUserDeptId();
		    String userOrgId = userView.getUserOrgId();
		    if (userDeptId != null && !"null".equalsIgnoreCase(userDeptId) && userDeptId.trim().length() > 0)
		    {
		    	code = userDeptId;
		    } else if (userOrgId != null && userOrgId.trim().length() > 0)
		    {
		    	code = userOrgId;
		    }
		}
		return code;
    }
    
    public boolean testCanSetDynaItem() throws GeneralException
    {
    	boolean flag = false; 
    	String whl = this.getPrivWhere(this.userView);
    	String sql = "select count(*) from per_object where plan_id="+this.planVo.getInt("plan_id")+" and body_id is not null "+whl;
    	ContentDAO dao = new ContentDAO(this.conn);
    	try
		{
			RowSet rs = dao.search(sql);
			if(rs.next()) {
                if(rs.getInt(1)>0) {
                    flag = true;
                }
            }
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
    		
    	return flag;
    }
    
    /**先看操作单位 再看管理范围 再看用户所在单位部门
     * 该方法没有考虑高级授权
     * 目前绩效模块暂时不考虑高级授权 所以还是用这个方法来控制
     * 该方法适用所有考核对象类型的计划包括人员和团队的计划
     * 
     * 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
     * */
    public String getPrivWhere(UserView userView)
    {
		StringBuffer buf = new StringBuffer();
//		if (!userView.isSuper_admin())
		{
			String operOrg = userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			if(operOrg!=null && operOrg.length() > 3)
			{
				StringBuffer tempSql = new StringBuffer("");
				String[] temp = operOrg.split("`");
				for (int i = 0; i < temp.length; i++) {
	
					if ("UN".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        tempSql.append(" or b0110 like '" + temp[i].substring(2)
                                + "%'");
                    } else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        tempSql.append(" or e0122 like '" + temp[i].substring(2)
                                + "%'");
                    }
				}
				buf.append(" and ( " + tempSql.substring(3) + " ) ");
			}
			else if((!userView.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
			{			
				//按管理范围
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
                            buf.append(" and b0110 like '"+codevalue+"%'");
                        } else if(AdminCode.getCodeName("UM",codevalue)!=null&&AdminCode.getCodeName("UM",codevalue).length()>0) {
                            buf.append(" and e0122 like '"+codevalue+"%'");
                        }
					}
				}else {
                    buf.append(" and 1=2 ");
                }
			}
//			if(buf.length()==0)//没有设置任何权限
//				buf.append(" and 1=2 ");
		}
		return buf.toString();
    }
    /**先看操作单位 再看管理范围 再看用户所在单位部门
     * 该方法没有考虑高级授权
     * 目前绩效模块暂时不考虑高级授权 所以还是用这个方法来控制
     * 该方法适用所有考核对象类型的计划包括人员和团队的计划
     * 
     * 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    xieguiquan 2011.11.11
     * */
    public String getPrivWhere(UserView userView,String tablename)
    {
		StringBuffer buf = new StringBuffer();
//		if (!userView.isSuper_admin())
		{
			String operOrg = userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			if(operOrg!=null && operOrg.length() > 3)
			{
				StringBuffer tempSql = new StringBuffer("");
				String[] temp = operOrg.split("`");
				for (int i = 0; i < temp.length; i++) {
	
					if ("UN".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        tempSql.append(" or "+tablename+".b0110 like '" + temp[i].substring(2)
                                + "%'");
                    } else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        tempSql.append(" or "+tablename+".e0122 like '" + temp[i].substring(2)
                                + "%'");
                    }
				}
				buf.append(" and ( " + tempSql.substring(3) + " ) ");
			}
			else if((!userView.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
			{			
				//按管理范围
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
                            buf.append(" and "+tablename+".b0110 like '"+codevalue+"%'");
                        } else if(AdminCode.getCodeName("UM",codevalue)!=null&&AdminCode.getCodeName("UM",codevalue).length()>0) {
                            buf.append(" and "+tablename+".e0122 like '"+codevalue+"%'");
                        }
					}
				}else {
                    buf.append(" and 1=2 ");
                }
			}
//			if(buf.length()==0)//没有设置任何权限
//				buf.append(" and 1=2 ");
		}
		return buf.toString();
    } 
    /**先看操作单位 再看管理范围(包括高级授权) 
     * 该方法暂时没用 等以后扩展用
     * @throws GeneralException 
     * 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
     * */
    public String getPrivWhere() throws GeneralException
    {
		StringBuffer buf = new StringBuffer();
//		if (!this.userView.isSuper_admin())
		{
			String operOrg = this.userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			if(operOrg!=null && operOrg.length() > 3)
			{
				StringBuffer tempSql = new StringBuffer("");
				String[] temp = operOrg.split("`");
				for (int i = 0; i < temp.length; i++) {
	
					if ("UN".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        tempSql.append(" or b0110 like '" + temp[i].substring(2)
                                + "%'");
                    } else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        tempSql.append(" or e0122 like '" + temp[i].substring(2)
                                + "%'");
                    }
				}
				buf.append(" and ( " + tempSql.substring(3) + " ) ");
			}
			else if((!this.userView.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
			{
				//管理范围机构树的授权
				String manageWhlByOrgTree = this.getManageWhlByOrgTree();
				if(this.planVo.getInt("object_type")!=2)
				{
					buf.append(manageWhlByOrgTree);
				}else if(this.planVo.getInt("object_type")==2)//对人员的限制要考虑高级授权
				{
					if(this.userView.getPrivExpression()!=null)//设置了高级授权
					{
						 String conditionSql = " select UsrA01.A0100 " + this.userView.getPrivSQLExpression("Usr", true);
						 buf.append(" and object_id in (" + conditionSql + " )");
					}else {
                        buf.append(manageWhlByOrgTree);
                    }
				}			
			}
//			if(buf.length()==0)//没有设置任何权限
//				buf.append(" and 1=2 ");
		}	
		return buf.toString();
    }
    /**得到管理范围来自机构树的设置*/
    private String getManageWhlByOrgTree()
    {
    	StringBuffer buf = new StringBuffer();
//    	按管理范围
		String codeid=this.userView.getManagePrivCode();
		String codevalue=this.userView.getManagePrivCodeValue();
		String a_code=codeid+codevalue;
		
		if(a_code.trim().length()>0)//说明授权了
		{
			if("UN".equalsIgnoreCase(a_code))//说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串
            {
                buf.append(" and 1=1 ");
            } else
			{
				if(AdminCode.getCodeName("UN",codevalue)!=null&&AdminCode.getCodeName("UN",codevalue).length()>0) {
                    buf.append(" and b0110 like '"+codevalue+"%'");
                } else if(AdminCode.getCodeName("UM",codevalue)!=null&&AdminCode.getCodeName("UM",codevalue).length()>0) {
                    buf.append(" and e0122 like '"+codevalue+"%'");
                }
			}
		}else {
            buf.append(" and 1=2 ");
        }
    	return buf.toString();
    }
    
    /**
     * 基于考核关系生成下属记录
     * @param plan_id
     * @param objs
     */
    public void executeSubordinateRecord(String plan_id,String objs)
    {
    	RowSet rowSet = null;
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.conn);	
    	 
    		String sql="select * from per_mainbodyset where body_type=0 ";
	    		if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                    sql += " and level_o=3";
                } else {
                    sql += " and level=3";
                }
				sql += " and  body_id in (select body_id from per_plan_body where plan_id="+plan_id+" ) ";			
    		rowSet = dao.search(sql.toString());
    		if(rowSet.next())
    		{
    			String body_id=rowSet.getString("body_id");
    			sql="select per_mainbody_std.mainbody_id,per_mainbody_std.object_id,UsrA01.b0110,UsrA01.e0122,UsrA01.e01a1,UsrA01.a0101 "
    				+" from  per_mainbody_std,usra01  where per_mainbody_std.object_id=UsrA01.a0100 "
    				+" and mainbody_id in ("+objs+") and body_id in (select body_id from per_mainbodyset where body_type=0 ";
		    			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                            sql += " and level_o=1";
                        } else {
                            sql += "and level=1 )";
                        }
		    	sql += " )";
    			
    			rowSet=dao.search(sql);
    			ArrayList objectList = new ArrayList();
    			ArrayList prePointPrivList = new ArrayList();
    			DbWizard dbWizard = new DbWizard(this.conn);
    			
    			String old_objectid=""; 
    			String mainbodys="";
    			while(rowSet.next())
    			{
    				String object_id=rowSet.getString("mainbody_id");
    				String mainbody_id=rowSet.getString("object_id");
    				if(old_objectid.length()==0) {
                        old_objectid=object_id;
                    }
    				if(object_id.equals(old_objectid))
    				{
    					mainbodys+=",'"+mainbody_id+"'";
    				}
    				else
    				{
    					selMainBody(mainbodys.substring(1), plan_id, body_id, old_objectid,"false");
    					mainbodys=",'"+mainbody_id+"'";
    					old_objectid=object_id;
    				} 
		        }	
    			if(mainbodys.length()>0) {
                    selMainBody(mainbodys.substring(1), plan_id, body_id, old_objectid,"false");
                }
    			
    			
    		
    				
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    }
    
    
    
    
    // 获得考核对象设置的考核关系
    public HashMap getObjectidMainbodyidMap(String plan_id)
	{
    	HashMap objectidMap = new HashMap();
    	RowSet rowSet = null;
		try
		{	
			ContentDAO dao = new ContentDAO(this.conn);			
		//	String sql = "select body_id from per_plan_body where plan_id=" + plan_id;
			StringBuffer sql = new StringBuffer("");
			sql.append("select ");
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                sql.append(" pmb.level_o");
            } else {
                sql.append(" pmb.level ");
            }
			sql.append(" ,pms.* from per_mainbody_std pms,per_mainbodyset pmb where ");
			sql.append(" pms.body_id in (select body_id from per_plan_body where plan_id="+ plan_id +") ");
			sql.append(" and pms.body_id=pmb.body_id order by pms.object_id ");			 			
			rowSet = dao.search(sql.toString());
			while (rowSet.next())
			{	
				String object_id = isNull(rowSet.getString("object_id"));   
				String level = isNull(rowSet.getString(1));
				LazyDynaBean abean = new LazyDynaBean();
				
				String bodyLevel = "";
				if(level!=null && level.trim().length()>0)
				{
					if("1".equalsIgnoreCase(level)) {
                        bodyLevel = "1";
                    } else if("0".equalsIgnoreCase(level)) {
                        bodyLevel = "2";
                    } else if("-1".equalsIgnoreCase(level)) {
                        bodyLevel = "3";
                    } else if("-2".equalsIgnoreCase(level)) {
                        bodyLevel = "4";
                    }
				}				
				abean.set("level", bodyLevel);				
				abean.set("object_id", isNull(rowSet.getString("object_id")));     
				abean.set("mainbody_id", isNull(rowSet.getString("mainbody_id")));   
				abean.set("b0110", isNull(rowSet.getString("b0110")));	
				abean.set("e0122", isNull(rowSet.getString("e0122"))); 
				abean.set("e01a1", isNull(rowSet.getString("e01a1"))); 
				abean.set("a0101", isNull(rowSet.getString("a0101"))); 
				abean.set("body_id", isNull(rowSet.getString("body_id"))); 
												
				if(objectidMap.get(object_id)!=null)
				{
					ArrayList list = (ArrayList)objectidMap.get(object_id);
					list.add(abean);
					objectidMap.put(object_id, list);	
				}
				else
				{
					ArrayList list = new ArrayList();
					list.add(abean);
					objectidMap.put(object_id,list);
				}
								
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return objectidMap;
	}
    
    // 判断此考核对象是否已设置某主体类别的主体
    public HashMap getObjectidMainbodysetMap(String plan_id,String object_id)
	{
    	HashMap bodyidMap = new HashMap();
    	RowSet rowSet = null;
		try
		{	
			ContentDAO dao = new ContentDAO(this.conn);			
			StringBuffer sql = new StringBuffer("");
			sql.append("select body_id from per_mainbody where plan_id="+ plan_id +" and object_id='"+ object_id +"' group by body_id ");					 			
			rowSet = dao.search(sql.toString());
			while (rowSet.next())
			{	   				
				bodyidMap.put(isNull(rowSet.getString("body_id")),"1");												
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return bodyidMap;
	}
    
    // 获得选择的考核主体的level
    public HashMap getMainbodyLevelMap(String body_id)
	{
    	HashMap levelMap = new HashMap();
    	RowSet rowSet = null;
		try
		{	
			ContentDAO dao = new ContentDAO(this.conn);			
			StringBuffer sql = new StringBuffer("");	
			sql.append("select body_id,");
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                sql.append(" level_o");
            } else {
                sql.append(" level ");
            }
			sql.append(" from per_mainbodyset where body_id = '"+ body_id +"' ");			
			rowSet = dao.search(sql.toString());
			while (rowSet.next())
			{	
				String level = isNull(rowSet.getString(2));				
				String bodyLevel = "";
				if(level!=null && level.trim().length()>0)
				{
					if("1".equalsIgnoreCase(level)) {
                        bodyLevel = "1";
                    } else if("0".equalsIgnoreCase(level)) {
                        bodyLevel = "2";
                    } else if("-1".equalsIgnoreCase(level)) {
                        bodyLevel = "3";
                    } else if("-2".equalsIgnoreCase(level)) {
                        bodyLevel = "4";
                    }
				}				
				levelMap.put(isNull(rowSet.getString("body_id")),bodyLevel);												
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return levelMap;
	}
    
    /**
         * 手工选择 考核对象
         * 
         * @param a0100s
         * @param planid
         * @param objectType
         *                1:部门 2:人员
         */
    public void handInsertObjects(String a0100s, String planid, String objectType)   // 修改为：如果模板里共性项目为2级，且一级项目下也有指标，项目权限里把1级项目显示出来  JinChunhai  2011.03.11
    {
    	
    	a0100s = PubFunc.keyWord_reback(a0100s);
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);		
		    
		    // 获得需要的计划参数
			LoadXml loadXml = null; //new LoadXml();
	    	if(BatchGradeBo.getPlanLoadXmlMap().get(planid)==null)
			{							
				loadXml = new LoadXml(this.conn,planid);
				BatchGradeBo.getPlanLoadXmlMap().put(planid,loadXml);
			}
			else
			{
				loadXml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(planid);
			}
            Hashtable htxml = loadXml.getDegreeWhole();           
            String spByBodySeq = (String)htxml.get("SpByBodySeq"); // 按考核主体顺序号控制审批流程(True, False默认为False)
            HashMap objectidMap = null;		    
            if(spByBodySeq!=null && spByBodySeq.trim().length()>0 && "true".equalsIgnoreCase(spByBodySeq)) {
                objectidMap = getObjectidMainbodyidMap(planid); // 获得考核对象设置的考核关系
            }
		    		    
		    
		    StringBuffer sql = new StringBuffer("");		   
		    if ("2".equals(objectType))
		    {
				sql.append("select B0110,E0122,E01A1,A0100,A0101,a0000 from usra01 where a0100 in (" + a0100s + ") ");
				sql.append(" and a0100 not in (select object_id from per_object where plan_id=" + planid + ")");
		    } else
		    {
				sql.append("select codeitemid,codeitemdesc,parentid,a0000 from organization where codeitemid  in (" + a0100s + ") ");
				sql.append(" and codeitemid not in (select object_id from per_object where plan_id=" + planid + ")");
		    }
		    RowSet rowSet = dao.search(sql.toString());
		    ArrayList recordList = new ArrayList();
		    ArrayList recordMainbodyList = new ArrayList();
		    String sql2 = "insert into per_object (id,b0110,e0122,e01a1,object_id,a0101,plan_id,a0000)values(?,?,?,?,?,?,?,?)";
		    String sqlMainbody = "insert into per_mainbody (id,b0110,e0122,e01a1,object_id,mainbody_id,a0101,body_id,plan_id,status,sp_seq,seq)values(?,?,?,?,?,?,?,?,?,?,?,?)";
		    StringBuffer a_a0100s = new StringBuffer("");
		    List _ids = new ArrayList(); // 选中人员的a0100 lium
		    while (rowSet.next())
		    {
				ArrayList tempList = new ArrayList();
				IDGenerator idg = new IDGenerator(2, conn);
				String id = idg.getId("per_object.id");
				tempList.add(new Integer(id));
				if ("2".equals(objectType))
				{
				    tempList.add(rowSet.getString("b0110"));
				    tempList.add(rowSet.getString("e0122"));
				    tempList.add(rowSet.getString("e01a1"));
				    tempList.add(rowSet.getString("a0100"));
				    tempList.add(rowSet.getString("a0101"));
					
				    // 若设置了按考核主体顺序号控制审批流程参数 则在选择考核对象时一同把考核关系中定义的考核主体给添加上
				    if(spByBodySeq!=null && spByBodySeq.trim().length()>0 && "true".equalsIgnoreCase(spByBodySeq))
				    {
					    if(objectidMap.get(rowSet.getString("a0100"))!=null)
					    {
					    	ArrayList mainbodyidList = (ArrayList)objectidMap.get(rowSet.getString("a0100"));   //value值   						 
					 		for(int i=0;i<mainbodyidList.size();i++)
					 		{
					 			LazyDynaBean abean=(LazyDynaBean)mainbodyidList.get(i);		
					 			String level = (String)abean.get("level");
			 					ArrayList tempList2 = new ArrayList();		 			
			 					IDGenerator idgm = new IDGenerator(2, conn);
			 					String idm = idgm.getId("per_mainbody.id");
			 					tempList2.add(new Integer(idm));
			 					tempList2.add((String)abean.get("b0110"));
			 					tempList2.add((String)abean.get("e0122"));
			 					tempList2.add((String)abean.get("e01a1"));
			 					tempList2.add(rowSet.getString("a0100"));
			 					tempList2.add((String)abean.get("mainbody_id"));
			 					tempList2.add((String)abean.get("a0101"));
			 					tempList2.add(new Integer((String)abean.get("body_id")));
			 					tempList2.add(new Integer(planid));
			 					tempList2.add(new Integer(0));
			 					if(level!=null && level.trim().length()>0){
			 						tempList2.add(new Integer(level));
			 						tempList2.add((new Integer(level))+1);
			 					}
			 					else{
			 						tempList2.add(null);
			 						tempList2.add(null);
			 					}
			 					recordMainbodyList.add(tempList2);		 							 					
					 		}				    					    	
					    }
				    }
				    
				    a_a0100s.append(",'" + rowSet.getString("a0100") + "'");
				    _ids.add(rowSet.getString("a0100"));
				} else
				{
					if(AdminCode.getCodeName("UN",rowSet.getString("codeitemid"))!=null&&AdminCode.getCodeName("UN",rowSet.getString("codeitemid")).length()>0) {
                        tempList.add(rowSet.getString("codeitemid"));
                    } else {
                        tempList.add(getB0110(rowSet.getString("parentid")));
                    }
				    tempList.add(rowSet.getString("codeitemid"));
				    tempList.add("");
				    tempList.add(rowSet.getString("codeitemid"));
				    if(AdminCode.getCodeName("UN",rowSet.getString("codeitemid"))!=null&&AdminCode.getCodeName("UN",rowSet.getString("codeitemid")).length()>0) {
                        tempList.add(AdminCode.getCodeName("UN", rowSet.getString("codeitemid")));
                    } else {
                        tempList.add(AdminCode.getCodeName("UM", rowSet.getString("codeitemid")));
                    }
				    
				}
				tempList.add(new Integer(planid));
				tempList.add(new Integer(rowSet.getInt("a0000")));
				recordList.add(tempList);
		    }
		    dao.batchInsert(sql2, recordList);
		    dao.batchInsert(sqlMainbody, recordMainbodyList);
		    
		    String method = getPlanVo(planid).getString("method");
		    DbWizard dbWizard = new DbWizard(this.conn);
		    if("2".equals(method))//目标管理计划要生成项目权限数据
		    {
		    	String str="select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+") and child_id is null";
				rowSet = dao.search(str);
				HashMap keyMap=new HashMap();
				while (rowSet.next())
				{
					keyMap.put(rowSet.getString("item_id"),"");			    			   
				}
		    	
				String strSql="select item_id from per_template_point where item_id in(select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+"))";
				rowSet = dao.search(strSql);
				while (rowSet.next())
				{
					keyMap.put(rowSet.getString("item_id"),"");			    			   
				}
				StringBuffer buf = new StringBuffer();
				Set keySet=keyMap.keySet();
				java.util.Iterator t=keySet.iterator();
				while(t.hasNext())
				{
					String strKey = (String)t.next();  //键值				
					buf.append("," + strKey);
				}
		    	
		    	if (!dbWizard.isExistTable("PER_ITEMPRIV_" + planid, false))
				{
				    Table table = new Table("PER_ITEMPRIV_" + planid);
					
					Field obj = new Field("object_id");
					obj.setDatatype(DataType.STRING);
					obj.setLength(30);
					obj.setNullable(false);
					obj.setKeyable(true);
					table.addField(obj);
					
					obj = new Field("body_id");
					obj.setDatatype(DataType.INT);
					obj.setLength(10);
					obj.setNullable(false);
					obj.setKeyable(true);
					table.addField(obj);	
					
					try
					{
						String sqlStr = "";
						if(buf!=null && buf.length()>0)	
				    	{	    			
			    			sqlStr = "select item_id from per_template_item where item_id in("+ buf.substring(1) + ")";
				    	}else {
                            sqlStr = "select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+") and child_id is null";
                        }

					    rowSet = dao.search(sqlStr);
					    while(rowSet.next())	
					    {
					        obj = new Field("C_"+rowSet.getString("item_id"));
					        obj.setDatatype(DataType.INT);
					        obj.setKeyable(false);
					        table.addField(obj);
					    }
					} catch (SQLException e)
					{
					    e.printStackTrace();
					    throw GeneralExceptionHandler.Handle(e);
					}	
					dbWizard.createTable(table);
				}	
				
				String delSql = "DELETE FROM PER_ITEMPRIV_"+planid+" WHERE body_id NOT IN (SELECT body_id FROM per_plan_body WHERE plan_id="+planid+")";
				dao.delete(delSql, new ArrayList());
				 
				delSql = "DELETE FROM PER_ITEMPRIV_"+planid+" WHERE object_id NOT IN (SELECT object_id FROM per_object WHERE plan_id="+planid+")";
				dao.delete(delSql, new ArrayList());		 
				 
				String itemstr="";
				String itemValStr="";
				String sqlStr = "";
				if(buf!=null && buf.length()>0)	
		    	{	    			
	    			sqlStr = "select item_id from per_template_item where item_id in("+ buf.substring(1) + ")";
		    	}else {
                    sqlStr = "select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+") and child_id is null";
                }

				rowSet = dao.search(sqlStr);
				while(rowSet.next())	
				{	
				    itemstr+="C_"+rowSet.getString("item_id")+",";
				    itemValStr+="1-" + Sql_switcher.isnull("b.opt", "0") + ","; // 根据打分确认标识确定权限的值（相反） by 刘蒙
				}
				String itemfld="";
				String itemVal="";
				if (itemstr.length()>0){//兼容没设考核项目的情况，工作计划推送时可能有。
				    itemfld=","+itemstr.substring(0,itemstr.length()-1);
				    itemVal=","+itemValStr.substring(0,itemValStr.length()-1);
				}
				
				// 首次引入考核对象
				sqlStr="INSERT INTO PER_ITEMPRIV_"+planid+"(object_id,body_id"+itemfld+") ";
				sqlStr+="SELECT O.object_id,B.body_id"+itemVal+" FROM PER_OBJECT O LEFT JOIN PER_PLAN_BODY B ON O.plan_id = B.plan_id WHERE O.plan_id="+planid+" and B.plan_id="+planid;
				sqlStr+=" AND NOT EXISTS (SELECT 1 FROM PER_ITEMPRIV_"+planid+" P WHERE P.object_id=O.object_id AND P.body_id=B.body_id)";
				dao.insert(sqlStr, new ArrayList());
		    }	    
		    
		    /** 判断计划主体类别中是否包含本人，如果包含则自动插入主体和相应的权限指标 */
		    if ("2".equals(objectType))
		    {
				boolean isSelf = false;
				rowSet = dao.search("select body_id from per_plan_body where body_id=5 and plan_id=" + planid);
				if (rowSet.next())
				{
				    isSelf = true;
				}
				
				// 将数量过多的a0100拆分为每1000个访问一次数据库 lium
				if (_ids.size() > 0) {
					StringBuffer sIds = new StringBuffer();
					for (int i = 0, len = _ids.size(); i < len; i++) {
						String _id = (String) _ids.get(i);
						if (_id == null || "".equals(_id)) {
							continue;
						}
						
						sIds.append(",'").append(_id).append("'");
						if ((i + 1) % 500 == 0 || i == len - 1) {
							// 非机读计划
							if (isSelf && !"1".equals(this.getPlanVo(planid).getString("gather_type"))) {
								saveMainBody2(sIds.substring(1), planid, 5);
							}
							
							// 引入考核关系类别
							StringBuffer sqlStr = new StringBuffer();
							sqlStr.append("update per_object set body_id=(select obj_body_id from per_object_std where object_id=per_object.object_id) where plan_id=");
							sqlStr.append(planid).append(" and object_id in (" + sIds.substring(1) + ")");
							dao.update(sqlStr.toString());
							
							sIds.setLength(0);
						}
					}
				}
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
    }
    
    // 获得考核主体类别评分顺序 
    public HashMap getMainbody_idScoreSortMap(String plan_id)
	{
    	HashMap mainbodyidMap = new HashMap();
    	RowSet rowSet = null;
		try
		{	
			ContentDAO dao = new ContentDAO(this.conn);			
			String sql = "select body_id,grade_seq from per_plan_body where plan_id=" + plan_id;
			rowSet = dao.search(sql);
			while (rowSet.next())
			{	
				String body_id = isNull(rowSet.getString("body_id"));   
				String grade_seq = isNull(rowSet.getString("grade_seq"));				
				if(grade_seq==null || grade_seq.trim().length()<=0) {
                    continue;
                }
				mainbodyidMap.put(body_id,grade_seq);												
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return mainbodyidMap;
	}
    
    /**
         * 保存考核主体 和 考核权限 目标管理计划不设指标权限只是设置项目权限
         * 
         * @param a0100s
         * @param plan_id
         */
    public void saveMainBody2(String a0100s, String plan_id, int body_id)
    {
    	a0100s = PubFunc.keyWord_reback(a0100s);
		try
		{
		    String method = getPlanVo(plan_id).getString("method");
		    ContentDAO dao = new ContentDAO(this.conn);
		    StringBuffer sql = new StringBuffer("");
		    sql.append("select B0110,E0122,E01A1,A0100,A0101 from usra01 where a0100 in (" + a0100s + ") ");
	
		    // 获得考核主体类别评分顺序 
		    HashMap mainbodyScoreSortMap = null;
		    if(this.planVo.getInt("method")==2 && this.planVo.getInt("object_type")==2) {
                mainbodyScoreSortMap = getMainbody_idScoreSortMap(plan_id);
            }
		    
		    String sql2 = "insert into per_mainbody (id,b0110,e0122,e01a1,object_id,mainbody_id,a0101,body_id,plan_id,status)values(?,?,?,?,?,?,?,?,?,?)";
		    if(mainbodyScoreSortMap!=null && mainbodyScoreSortMap.get(String.valueOf(body_id).trim())!=null) {
                sql2 = "insert into per_mainbody (id,b0110,e0122,e01a1,object_id,mainbody_id,a0101,body_id,plan_id,status,seq)values(?,?,?,?,?,?,?,?,?,?,?)";
            }
		    ArrayList pointList = getPerPointList(plan_id);
		    
		    DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel("per_pointpriv_"+plan_id);	
		    StringBuffer sql3 = new StringBuffer("insert into per_pointpriv_" + plan_id + " (id,b0110,e0122,e01a1,object_id,mainbody_id,bodyname");
		    StringBuffer sql_extend3 = new StringBuffer("?,?,?,?,?,?,?");
		    for (int i = 0; i < pointList.size(); i++)
		    {
				sql3.append("," + pointList.get(i));
				sql_extend3.append(",?");
		    }
		    RowSet rowSet = dao.search(sql.toString());
	
		    ArrayList recordList2 = new ArrayList();
		    ArrayList recordList3 = new ArrayList();
		    while (rowSet.next())
		    {
				ArrayList tempList2 = new ArrayList();
				ArrayList tempList3 = new ArrayList();
		
				IDGenerator idg = new IDGenerator(2, conn);
				String id = idg.getId("per_mainbody.id");
				tempList2.add(new Integer(id));
				tempList2.add(rowSet.getString("b0110"));
				tempList2.add(rowSet.getString("e0122"));
				tempList2.add(rowSet.getString("e01a1"));
				tempList2.add(rowSet.getString("a0100"));
				tempList2.add(rowSet.getString("a0100"));
				tempList2.add(rowSet.getString("a0101"));
				tempList2.add(new Integer(body_id));
				tempList2.add(new Integer(plan_id));
				tempList2.add(new Integer(0));
				if(mainbodyScoreSortMap!=null && mainbodyScoreSortMap.get(String.valueOf(body_id).trim())!=null) {
                    tempList2.add(new Integer((String)mainbodyScoreSortMap.get(String.valueOf(body_id).trim())));
                }
				recordList2.add(tempList2);
		
				tempList3.add(new Integer(id));
				tempList3.add(rowSet.getString("b0110"));
				tempList3.add(rowSet.getString("e0122"));
				tempList3.add(rowSet.getString("e01a1"));
				tempList3.add(rowSet.getString("a0100"));
				tempList3.add(rowSet.getString("a0100"));
				tempList3.add(rowSet.getString("a0101"));
				for (int i = 0; i < pointList.size(); i++) {
                    tempList3.add(new Integer(1));
                }
				recordList3.add(tempList3);
		    }
		    dao.batchInsert(sql2, recordList2);
		    if("1".equals(method)) {
                dao.batchInsert(sql3.toString() + ")values(" + sql_extend3.toString() + ")", recordList3);
            }
		  
	
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
    }

    /**
         * 根据指标权限表取得 计划中的指标字段
         * 
         * @param planid
         * @return
         */
    public ArrayList getPerPointList(String planid)
    {

		ArrayList list = new ArrayList();
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    RowSet rowSet = dao.search("select * from per_pointpriv_" + planid + " where 1=2");
		    ResultSetMetaData mt = rowSet.getMetaData();
		    for (int i = 0; i < mt.getColumnCount(); i++)
		    {
				String columnName = mt.getColumnName(i + 1);
				if (columnName.length() > 2 && "C_".equalsIgnoreCase(columnName.substring(0, 2))) {
                    list.add(columnName);
                }
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return list;
    }

    public String getB0110(String codeitemid)
    {

		String b0110 = "";
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    RowSet rowSet = null;
		    String acodeitemid = codeitemid;
		    while (true)
		    {
				rowSet = dao.search("select * from organization where codeitemid='" + acodeitemid + "'");
				if (rowSet.next())
				{
				    if ("UN".equals(rowSet.getString("codesetid")))
				    {
						b0110 = acodeitemid;
						break;
				    } else {
                        acodeitemid = rowSet.getString("parentid");
                    }
				}
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return b0110;
    }

    /**
         * 校验考核对象是否有主体
         * 
         * @param planid
         * @return
         */
    public boolean validateObjectIsMainbody(String planid)
    {

	boolean flag = true;
	try
	{
	    ContentDAO dao = new ContentDAO(this.conn);
	    StringBuffer sql = new StringBuffer("select object_id,a0101,sum(acount) from ");
	    sql.append(" ( select per_object.object_id,per_object.a0101,");
	    sql.append(" case when per_mainbody.mainbody_id is null then 0 else 1 end as acount ");
	    sql.append(" from  per_object left  join  per_mainbody on   per_object.object_id=per_mainbody.object_id ");
	    sql.append(" and per_mainbody.plan_id=" + planid + " where per_object.plan_id=" + planid + " ) a ");
	    sql.append(" group by a.object_id,a.a0101 having sum(acount)=0");
	    RowSet rowSet = dao.search(sql.toString());
	    if (rowSet.next()) {
            flag = false;
        }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return flag;
    }

    /**
         * 校验主体和权限指标是否对应
         * 
         * @param planid
         * @return
         */
    public boolean validateMainbody_Priv(String planid)
    {

	boolean flag = false;
	try
	{
	    ContentDAO dao = new ContentDAO(this.conn);
	    StringBuffer sql = new StringBuffer(" select count(per_mainbody.object_id) from per_mainbody , per_pointpriv_" + planid + " ");
	    sql.append(" where per_mainbody.object_id=per_pointpriv_" + planid + ".object_id and per_mainbody.mainbody_id=per_pointpriv_" + planid + ".mainbody_id ");
	    sql.append(" and  per_mainbody.plan_id=" + planid);
	    RowSet rowSet = dao.search(sql.toString());
	    int count = 0;
	    if (rowSet.next()) {
            count = rowSet.getInt(1);
        }

	    sql.setLength(0);
	    int mainbodyCount = 0;
	    sql.append("select count(mainbody_id) from per_mainbody where plan_id=" + planid);
	    rowSet = dao.search(sql.toString());
	    if (rowSet.next()) {
            mainbodyCount = rowSet.getInt(1);
        }

	    sql.setLength(0);
	    int privCount = 0;
	    sql.append("select count(mainbody_id) from per_pointpriv_" + planid);
	    rowSet = dao.search(sql.toString());
	    if (rowSet.next()) {
            privCount = rowSet.getInt(1);
        }

	    if (privCount == count && count == mainbodyCount) {
            flag = true;
        }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return flag;
    }

    /**
         * 校验是否有考核对象
         * 
         * @param planid
         * @return
         */
    public boolean validateIsObject(String planid)
    {

	boolean flag = false;
	try
	{
	    ContentDAO dao = new ContentDAO(this.conn);
	    RowSet rowSet = dao.search("select count(object_id) from per_object where plan_id=" + planid);
	    if (rowSet.next())
	    {
		if (rowSet.getInt(1) > 0) {
            flag = true;
        }
	    }

	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return flag;
    }

    public String getNameByStatus(int status)
    {

	String temp = "";
	switch (status)
	{
	case 3:
	    temp = ResourceFactory.getProperty("org.performance.Published");
	    break;
	case 4:
	    temp = ResourceFactory.getProperty("kh.field.started");
	    break;
	case 5:
	    temp = ResourceFactory.getProperty("kh.field.pause");
	    break;
	case 6:
	    temp = ResourceFactory.getProperty("kh.field.startEvaluation");
	    break;
	case 7:
	    temp = ResourceFactory.getProperty("kh.field.finished");
	    break;
	case 8:
	    temp = ResourceFactory.getProperty("performance.plan.distribute");
	    break;
	}
	return temp;
    }

    /**
         * 取得考核计划信息
         * 
         * @param planid
         * @return
         */
    public RecordVo getPerPlanVo(String planid)
    {

	RecordVo vo = new RecordVo("per_plan");
	try
	{
		if(planid.trim().length()==0) {
            return null;
        }
	    ContentDAO dao = new ContentDAO(this.conn);
	    vo.setInt("plan_id", Integer.parseInt(planid));
	    vo = dao.findByPrimaryKey(vo);
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return vo;
    }

    public String getPlanStatus(String planid)
    {

	String status = "";
	try
	{
	    if (planid != null && planid.length() > 0)
	    {
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer sql = new StringBuffer("select plan_id,name,status from per_plan where plan_id=" + planid);
		RowSet rowSet = dao.search(sql.toString());
		if (rowSet.next()) {
            status = rowSet.getString("status");
        }
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return status;
    }

    /**
         * 取得需数据采集 的考核计划
         * 
         * @return
         */
    public ArrayList getGatherPlanList(String manage_id)
    {

	ArrayList list = new ArrayList();
	try
	{
	    ContentDAO dao = new ContentDAO(this.conn);
	    StringBuffer sql = new StringBuffer("select plan_id,name,status,parameter_content from per_plan where ( status=6 or status=4 ) ");
	    if(!"-1".equals(manage_id))
	    {
	    	sql.append(" and ( b0110 like '"+manage_id+"%' ) ");
	    }
	    sql.append(" order by "+Sql_switcher.isnull("a0000", "999999999")+" asc,plan_id desc");
	    RowSet rowSet = dao.search(sql.toString());
	    LoadXml xmlBo = null;
	    while (rowSet.next())
	    {
		String parameter_content = Sql_switcher.readMemo(rowSet, "parameter_content");
		if (parameter_content.length() > 0)
		{
		    xmlBo = new LoadXml(this.conn, parameter_content, 1);
		    Hashtable ht = xmlBo.getDegreeWhole();
		    if (ht != null && ht.get("scoreWay") != null && "0".equals((String) ht.get("scoreWay")))
		    {
		    	
		    	if(ht.get("HandEval") != null && "FALSE".equalsIgnoreCase((String) ht.get("HandEval")))
		    	{
		    		CommonData vo = new CommonData(rowSet.getString("plan_id"), rowSet.getString("plan_id") + ":" + rowSet.getString("name") + "(" + getNameByStatus(rowSet.getInt("status")) + ")");
		    		list.add(vo);
		    	}
		    }
		}
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return list;
    }
    
    
    /**
     * 取得考核计划列表
     * 
     * @param opt
     *                0:all 1:发布，启动，暂停
     * 
     * @return
     */
	public ArrayList getPlanList(int opt,String codeid)
	{
	
	ArrayList list = new ArrayList();
	try
	{
	    ContentDAO dao = new ContentDAO(this.conn);
	    StringBuffer sql = new StringBuffer("select plan_id,name,status from per_plan");
	    if (opt == 1) {
            sql.append(" where status in (3,4,5,6,8) ");
        }
	    
	    if(!"-1".equals(codeid) && !"0".equals(codeid)) {
            sql.append(" and (b0110='HJSJ' or b0110 like '"+codeid+"%')");
        }
	    	    
	    sql.append(" order by "+Sql_switcher.isnull("a0000", "999999999")+" asc,plan_id desc");
	    
	    RowSet rowSet = dao.search(sql.toString());
	    while (rowSet.next())
	    {
		CommonData vo = new CommonData(rowSet.getString("plan_id"), rowSet.getString("plan_id") + ":" + rowSet.getString("name") + "(" + getNameByStatus(rowSet.getInt("status")) + ")");
		list.add(vo);
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return list;
	}
    
    

    /**
         * 取得考核计划列表
         * 
         * @param opt
         *                0:all 1:发布，启动，暂停
         * 
         * @return
         */
    public ArrayList getPlanList(int opt)
    {

	ArrayList list = new ArrayList();
	try
	{
	    ContentDAO dao = new ContentDAO(this.conn);
	    StringBuffer sql = new StringBuffer("select plan_id,name,status from per_plan");
	    if (opt == 1)
	    {
		sql.append(" where status>2 and status<=6 ");
	    }
	    sql.append(" order by a0000 asc,plan_id desc");
	    RowSet rowSet = dao.search(sql.toString());
	    while (rowSet.next())
	    {
		CommonData vo = new CommonData(rowSet.getString("plan_id"), rowSet.getString("plan_id") + ":" + rowSet.getString("name") + "(" + getNameByStatus(rowSet.getInt("status")) + ")");
		list.add(vo);
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return list;
    }

    /**
         * 取得考核计划列表
         * 
         * @param scope
         *                all,start,evaluation,finished
         * 
         * @return
         */
    public ArrayList getPlanListByScope(String scope)
    {

	ArrayList list = new ArrayList();
	try
	{
	    ContentDAO dao = new ContentDAO(this.conn);
	    StringBuffer sql = new StringBuffer("select plan_id,name,status from per_plan");
	    if ("all".equals(scope))
	    {
		sql.append(" where ( status=4 or status=6 or status=7 ) ");
	    } else
	    {
		if ("start".equals(scope)) {
            sql.append(" where status=4");
        }
		if ("evaluation".equals(scope)) {
            sql.append(" where status=6");
        }
		if ("finished".equals(scope)) {
            sql.append(" where status=7");
        }
	    }
	    // sql.append(" and ( Method=1 or method is null ) ");
	    sql.append(" order by "+Sql_switcher.isnull("a0000", "999999999")+" asc,plan_id desc");
	    RowSet rowSet = dao.search(sql.toString());
	    while (rowSet.next())
	    {
		CommonData vo = new CommonData(rowSet.getString("plan_id"), rowSet.getString("plan_id") + ":" + rowSet.getString("name") + "(" + getNameByStatus(rowSet.getInt("status")) + ")");
		list.add(vo);
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return list;
    }

    //取得考核等级分类信息
    public ArrayList getPerDegreeList(String GradeClass,String busitype)
    {
    	ArrayList list=new ArrayList();
    	StringBuffer buf=new StringBuffer();
    	try
    	{
    		if("-1".equalsIgnoreCase(GradeClass)) {
                list.add(new CommonData("-1","无"+"(当前值)"));
            } else {
                list.add(new CommonData("-1","无"));
            }
    		ContentDAO dao = new ContentDAO(this.conn);
    		
    		buf.append("select * from per_degree where used=1 ");    		   		    		    		
    		if(busitype==null || busitype.trim().length()<=0 || "0".equalsIgnoreCase(busitype)) {
                buf.append(" and flag in(0,1,2,3) ");
            } else {
                buf.append(" and flag in(4,5) ");
            }
    		buf.append(" order by degree_id ");
    		
    		RowSet rowSet=dao.search(buf.toString());
    		while(rowSet.next())
    		{
    			if(GradeClass.equalsIgnoreCase(rowSet.getString("degree_id"))) {
                    list.add(new CommonData(rowSet.getString("degree_id"),rowSet.getString("degreename")+"(当前值)"));
                } else {
                    list.add(new CommonData(rowSet.getString("degree_id"),rowSet.getString("degreename")));
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
         * 取得考核 对象/主体 类别列表
         * 
         * @param type
         *                1:对象 0：主体
         * @return
         */
    public ArrayList getObjectTypeList(int type,String planid)
    {

	ArrayList list = new ArrayList();
	try
	{
	    ContentDAO dao = new ContentDAO(this.conn);
	    String sql = "";
	    if (type == 0) {
            sql = "select body_id,name from per_mainbodyset where ( body_type=0 or body_type is null )  and status=1  order by seq";
        } else if(type==1)
	    {
	    	RecordVo vo=this.getPerPlanVo(planid);		
			int object_type=vo.getInt("object_type");   
			sql = "select body_id,name from per_mainbodyset where body_type=1  and status=1";
			if(object_type==2) {
                sql+=" and (object_type=2 or object_type is null)";
            } else {
                sql+=" and (object_type=1 or object_type is null)";
            }
			sql+=" order by seq ";
	    }
	    
	    RowSet rowSet = dao.search(sql);
	    CommonData vo = new CommonData("null", "  ");
	    list.add(vo);
	    while (rowSet.next())
	    {
		vo = new CommonData(rowSet.getString("body_id"), rowSet.getString("name"));
		list.add(vo);
	    }

	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return list;
    }

    /**
         * 根据考核计划取得考核对象列表
         * 
         * @param planid
         * @param object_type
         *                //1团队 2：人员 3.单位 4.部门
         * @return
         */
    public ArrayList getPerObjectDataList(String planid, String object_type, String objWhere,  String orderSql,String queryA0100)
    {

		ArrayList list = new ArrayList();
	
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    StringBuffer sql = new StringBuffer("select po.kh_relations,po.id,po.b0110,po.e0122,po.e01a1,po.object_id,po.a0101,po.body_id,pmb.name ");
		    sql.append(" from per_object po left join	per_mainbodyset pmb on  po.body_id=pmb.body_id ");
		    sql.append(" where po.plan_id=" + planid);
		    
		    
	//	    if (code != null && !code.equals("-1"))
	//	    {
	//		if (codeset.equalsIgnoreCase("UN"))
	//		    sql.append(" and po.b0110 like '" + code + "%' ");
	//		else if (codeset.equalsIgnoreCase("UM"))
	//		    sql.append(" and po.e0122 like '" + code + "%' ");
	//	    }
		    if(objWhere.length()>0)
		    {
		    	sql.append(" and po.object_id in (select object_id from per_object where plan_id="+planid+" "+objWhere+")");
		    }
		    
		    
		    if(queryA0100!=null && queryA0100.trim().length()>0) {
                sql.append(" and po.a0101 like '%" + PubFunc.getStr(queryA0100.trim()) + "%' ");
            }
		    	
		    if (orderSql == null || orderSql.length() == 0)
		    {
	//		if (object_type.equals("1") || object_type.equals("3") || object_type.equals("4"))
	//		    sql.append(" order by object_id");
	//		else if (object_type.equals("2"))
			    sql.append(" order by a0000, object_id");
		    } else
		    {
		    	orderSql=orderSql.replace("body_id", "po.body_id");
		    	sql.append(" " + orderSql);
		    }
		
		    RowSet rowSet = dao.search(sql.toString());
		    int count = 1;
		    LazyDynaBean abean = null;
		    while (rowSet.next())
		    {
				String b0110 = rowSet.getString("b0110");
				String e0122 = rowSet.getString("e0122");
				String e01a1 = rowSet.getString("e01a1");
				abean = new LazyDynaBean();
				abean.set("count", String.valueOf(count));
				abean.set("id", rowSet.getString("id"));
				abean.set("b0110", b0110 != null ? AdminCode.getCodeName("UN", b0110) : "");
				abean.set("e0122", e0122 != null ? AdminCode.getCodeName("UM", e0122) : "");
				abean.set("e01a1", e01a1 != null ? AdminCode.getCodeName("@K", e01a1) : "");
				abean.set("b0110value", b0110 != null ?  b0110 : "");
				abean.set("e0122value", e0122 != null ? e0122 : "");
				abean.set("e01a1value", e01a1 != null ? e01a1 : "");
				abean.set("object_id", rowSet.getString("object_id"));
				abean.set("a0101", rowSet.getString("a0101")!=null? rowSet.getString("a0101"):"");
				abean.set("body_id", rowSet.getString("body_id"));
				abean.set("objectTypeName", rowSet.getString("name")==null?"":rowSet.getString("name"));
				abean.set("kh_relations", rowSet.getObject("kh_relations")==null?"0":rowSet.getInt("kh_relations")+"");
				
				String kh_relations = rowSet.getObject("kh_relations")==null?"0":rowSet.getInt("kh_relations")+"";
				if("0".equals(kh_relations)) {
                    abean.set("kh_relations_name", "标准");
                } else {
                    abean.set("kh_relations_name", "非标准");
                }
		//		abean.set("kh_relations_name", rowSet.getObject("kh_relations")==null || (rowSet.getObject("kh_relations")!=null && rowSet.getObject("kh_relations").equals("0"))?"标准":"非标准");
				count++;
				list.add(abean);
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return list;
    }

    /**
         * 根据考核对象得到考核主体列表
         * 
         * @param objectid
         * @return
         */
    public ArrayList getPerMainBodyList(String objectid, String planid)
    {

	ArrayList list = new ArrayList();
	try
	{
	    if (planid != null && planid.length() > 0 && objectid != null && objectid.length() > 0)
	    {
		ContentDAO dao = new ContentDAO(this.conn);
		/*StringBuffer sql = new StringBuffer("select pmb.*,pms.name,ppb.isgrade,ppb.opt from per_mainbody pmb,PER_MAINBODYSET pms,per_plan_body ppb,usra01 ua ");
		sql.append(" where pmb.body_id = pms.body_id and pmb.plan_id=" + planid + " and pmb.plan_id=ppb.plan_id and pmb.body_id = ppb.body_id ");
		sql.append(" and pmb.object_id='" + objectid + "' and pmb.mainbody_id=ua.a0100 ");
		sql.append(" order by pms.body_id,ua.a0000");*/
		StringBuffer sql = new StringBuffer();//缺陷 2541 zgd 2014-7-8 考核实施/考核主体中显示已移库和删除的对象
		sql.append(" select pmb.*,pms.name,ppb.isgrade,ppb.opt from per_mainbody pmb inner join per_mainbodyset pms on pmb.body_id = pms.body_id inner join per_plan_body ppb on pmb.plan_id=ppb.plan_id and pmb.body_id = ppb.body_id left join usra01 ua on pmb.mainbody_id=ua.a0100 and pmb.a0101=ua.a0101");
		sql.append(" where pmb.plan_id="+planid+" and pmb.object_id='"+objectid+"'");
		sql.append(" order by pms.body_id,ua.a0000");

		RowSet rowSet = dao.search(sql.toString());
		LazyDynaBean abean = null;
		while (rowSet.next())
		{
		    String b0110 = rowSet.getString("b0110");
		    String e0122 = rowSet.getString("e0122");
		    String e01a1 = rowSet.getString("e01a1");
		    String fillctrl = rowSet.getString("fillctrl");
		    String seq = rowSet.getString("seq");
		    String sp_seq = rowSet.getString("sp_seq");
		    String isgrade = rowSet.getString("isgrade");
		    String opt = rowSet.getString("opt"); // 打分确认标识: 空指定为“0” by 刘蒙
		    opt = opt != null && opt.trim().length() > 0 ? opt : "0";
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
		    abean.set("sp_seq", sp_seq != null ? sp_seq : "");
		    if(isgrade!=null && isgrade.trim().length()>0 && "1".equalsIgnoreCase(isgrade)) {
                abean.set("isgradesc", "不评分");
            } else {
                abean.set("isgradesc", "0".equals(opt) ? "评分" : "不评分"); // 参与评分但只有确认权限的，依然“不评分” by 刘蒙
            }
		    abean.set("opt", opt);
		    list.add(abean);
		}
	    }

	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return list;
    }
    /**
     * 根据考核对象得到标准考核关系中的考核主体列表
     * 
     * @param objectid
     * @return
     */
public ArrayList getKhRelaMainbody(String objectid,String planid)
{

	ArrayList list = new ArrayList();
	try
	{
	    if (objectid != null && objectid.length() > 0)
	    {
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer sql = new StringBuffer("");		
		
		RecordVo vo= getPlanVo(planid);
		if(!"2".equals(vo.getString("object_type")))//非人员考核对象类型的目标管理
		{
		    sql.append("select distinct per_mainbody_std.b0110,per_mainbody_std.e0122,per_mainbody_std.e01a1,per_mainbody_std.a0101,per_mainbodyset.name from per_mainbody_std  join  per_mainbodyset ");
		    sql.append("on per_mainbodyset.body_id=per_mainbody_std.body_id and per_mainbody_std.object_id in(select mainbody_id from per_mainbody where plan_id="+planid+" and body_id=-1 and object_id='"+objectid+"') ");
		    sql.append("and per_mainbody_std.body_id in (select body_id  from per_plan_body where plan_id="+planid+")");  
		}else//人员目标管理
		{
		    sql.append("select per_mainbody_std.b0110,per_mainbody_std.e0122,per_mainbody_std.e01a1,per_mainbody_std.a0101,per_mainbodyset.name from per_mainbody_std  join  per_mainbodyset ");
		    sql.append("on per_mainbodyset.body_id=per_mainbody_std.body_id and per_mainbody_std.object_id='"+objectid+"' ");
		    sql.append("and per_mainbody_std.body_id in (select body_id  from per_plan_body where plan_id="+planid+") order by per_mainbodyset.body_id");  
		}
		RowSet rowSet = dao.search(sql.toString());
		LazyDynaBean abean = null;
		while (rowSet.next())
		{
		    String b0110 = rowSet.getString("b0110");
		    String e0122 = rowSet.getString("e0122");
		    String e01a1 = rowSet.getString("e01a1");
		    abean = new LazyDynaBean();
		    abean.set("a0101", rowSet.getString("a0101"));
		    abean.set("b0110", b0110 != null ? AdminCode.getCodeName("UN", b0110) : "");
		    abean.set("e0122", e0122 != null ? AdminCode.getCodeName("UM", e0122) : "");
		    abean.set("e01a1", e01a1 != null ? AdminCode.getCodeName("@K", e01a1) : "");
		    abean.set("bodyTypeName", rowSet.getString("name"));
		    list.add(abean);
		}
	    }

	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return list;
}
/**如果项目权限表不存在就新建这个表 并且初始化*/
public void initItemPrivTable(String planid) throws GeneralException
{
    RowSet rowSet;
	String tableName = "PER_ITEMPRIV_" + planid;
	try
	{
		 DbWizard dbWizard = new DbWizard(this.conn);
		 ContentDAO dao = new ContentDAO(this.conn);
		 if (!dbWizard.isExistTable(tableName, false))
		 {
		    Table table = new Table(tableName);
			
			Field obj = new Field("object_id");
			obj.setDatatype(DataType.STRING);
			obj.setLength(30);
			obj.setNullable(false);
			obj.setKeyable(true);
			table.addField(obj);
			
			obj = new Field("body_id");
			obj.setDatatype(DataType.INT);
			obj.setLength(10);
			obj.setNullable(false);
			obj.setKeyable(true);
			table.addField(obj);	
			
			String str="select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+") and child_id is null";
			rowSet = dao.search(str);
			HashMap keyMap=new HashMap();
			while (rowSet.next())
			{
				keyMap.put(rowSet.getString("item_id"),"");			    			   
			}
	    	
			String strSql="select item_id from per_template_point where item_id in(select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+"))";
			rowSet = dao.search(strSql);
			while (rowSet.next())
			{
				keyMap.put(rowSet.getString("item_id"),"");			    			   
			}
			StringBuffer buf = new StringBuffer();
			Set keySet=keyMap.keySet();
			java.util.Iterator t=keySet.iterator();
			while(t.hasNext())
			{
				String strKey = (String)t.next();  //键值				
				buf.append("," + strKey);
			}
			
			String sqlStr = "";
    		if(buf!=null && buf.length()>0)	
	    	{	    			
    			sqlStr = "select item_id from per_template_item where item_id in("+ buf.substring(1) + ")";
	    	}else {
                sqlStr = "select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+") and child_id is null";
            }
			
			rowSet = dao.search(sqlStr);
			while(rowSet.next())	
			{
			    obj = new Field("C_"+rowSet.getString("item_id"));
			    obj.setDatatype(DataType.INT);
			    obj.setKeyable(false);
			    table.addField(obj);
			}		
			dbWizard.createTable(table);		  	
		
			String delSql = "DELETE FROM PER_ITEMPRIV_"+planid+" WHERE body_id NOT IN (SELECT body_id FROM per_plan_body WHERE plan_id="+planid+")";
			dao.delete(delSql, new ArrayList());
			 
			delSql = "DELETE FROM PER_ITEMPRIV_"+planid+" WHERE object_id NOT IN (SELECT object_id FROM per_object WHERE plan_id="+planid+")";
			dao.delete(delSql, new ArrayList());		 
			 
			String itemstr="";
			String itemValStr="";
			if(buf!=null && buf.length()>0)	
	    	{	    			
    			sqlStr = "select item_id from per_template_item where item_id in("+ buf.substring(1) + ")";
	    	}else {
                sqlStr = "select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+") and child_id is null";
            }
			rowSet = dao.search(sqlStr);
			while(rowSet.next())	
			{	
			    itemstr+="C_"+rowSet.getString("item_id")+",";
			    itemValStr+="1-" + Sql_switcher.isnull("b.opt", "0") + ","; // 根据打分确认标识确定权限的值（相反） by 刘蒙
			}
			sqlStr="INSERT INTO PER_ITEMPRIV_"+planid+"(object_id,body_id,"+itemstr.substring(0,itemstr.length()-1)+") ";
			sqlStr+="SELECT O.object_id,B.body_id,"+itemValStr.substring(0,itemValStr.length()-1)+" FROM PER_OBJECT O LEFT JOIN PER_PLAN_BODY B ON O.plan_id = B.plan_id WHERE O.plan_id="+planid+" and B.plan_id="+planid;
			sqlStr+=" AND NOT EXISTS (SELECT 1 FROM PER_ITEMPRIV_"+planid+" P WHERE P.object_id=O.object_id AND P.body_id=B.body_id)";
			dao.insert(sqlStr, new ArrayList());	
			if(rowSet!=null) {
                rowSet.close();
            }
		 }
	 } catch (SQLException e)
	 {
		 	e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
	 }
}


/**
 * 目标管理计划的项目权限
 * 
 * @param objectid
 * @return
 */
public HashMap getItemPriv(String objectid,String planid)    // 修改为：如果模板里共性项目为2级，且一级项目下也有指标，项目权限里把1级项目显示出来  JinChunhai  2011.03.11
{
    HashMap map = new HashMap();
    StringBuffer sql = new StringBuffer("");
    RowSet rowSet;
    String tableName = "PER_ITEMPRIV_" + planid;
	try
	{
	    DbWizard dbWizard = new DbWizard(this.conn);
	    ContentDAO dao = new ContentDAO(this.conn);
	    
	    String str="select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+") and child_id is null";
		rowSet = dao.search(str);
		HashMap keyMap=new HashMap();
		while (rowSet.next())
		{
			keyMap.put(rowSet.getString("item_id"),"");			    			   
		}
    	
		String strSql="select item_id from per_template_point where item_id in(select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+"))";
		rowSet = dao.search(strSql);
		while (rowSet.next())
		{
			keyMap.put(rowSet.getString("item_id"),"");			    			   
		}
		StringBuffer buf = new StringBuffer();
		Set keySet=keyMap.keySet();
		java.util.Iterator t=keySet.iterator();
		while(t.hasNext())
		{
			String strKey = (String)t.next();  //键值				
			buf.append("," + strKey);
		}	    
	    
	    if (!dbWizard.isExistTable(tableName, false))
	    {
	    	this.initItemPrivTable(planid);	 
	    }else
	    {	    	
//	    	RecordVo vo = new RecordVo(tableName);  
			Table table = new Table(tableName);
	    	boolean flag = false;
	    	try
			{
	    		String sqlStr = "";
	    		if(buf!=null && buf.length()>0)	
		    	{	    			
	    			sqlStr = "select item_id from per_template_item where item_id in("+ buf.substring(1) + ")";
		    	}else {
                    sqlStr = "select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+") and child_id is null";
                }
			    rowSet = dao.search(sqlStr);
			    while(rowSet.next())	
			    {
			        String field = "c_"+rowSet.getString("item_id").toLowerCase();
//			        if(!vo.hasAttribute(field))
			    	if (!dbWizard.isExistField(tableName, field))
			        {
			    	    Field obj = new Field(field);
			    	    obj = new Field("C_"+rowSet.getString("item_id"));
				        obj.setDatatype(DataType.INT);
				        obj.setKeyable(false);
				        table.addField(obj);
					    flag = true;
			        }
			    }
				    	
			    if (flag) {
                    dbWizard.addColumns(table);// 更新列
                }
			
			    sqlStr = "select count(*) from PER_ITEMPRIV_"+planid;
			    rowSet = dao.search(sqlStr);
			    if(rowSet.next())
			    {
			    	if(rowSet.getInt(1)==0)//里面没有记录就新增
			    	{
			    		String itemstr="";
			    		String itemValStr="";
			    		
			    		if(buf!=null && buf.length()>0)	
				    	{	    			
			    			sqlStr = "select item_id from per_template_item where item_id in("+ buf.substring(1) + ")";
				    	}else {
                            sqlStr = "select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+") and child_id is null";
                        }
			    		
			    		rowSet = dao.search(sqlStr);
			    		while(rowSet.next())	
			    		{	
			    		    itemstr+="C_"+rowSet.getString("item_id")+",";
			    		    itemValStr+="1-" + Sql_switcher.isnull("b.opt", "0") + ","; // 根据打分确认标识确定权限的值（相反） by 刘蒙
			    		}
			    		
			    		// 引入过考核对象之后的再次查看
			    		sqlStr="INSERT INTO PER_ITEMPRIV_"+planid+"(object_id,body_id,"+itemstr.substring(0,itemstr.length()-1)+") ";
			    		sqlStr+="SELECT O.object_id,B.body_id,"+itemValStr.substring(0,itemValStr.length()-1)+" FROM PER_OBJECT O LEFT JOIN PER_PLAN_BODY B ON O.plan_id = B.plan_id WHERE O.plan_id="+planid+" and B.plan_id="+planid;
			    		sqlStr+=" AND NOT EXISTS (SELECT 1 FROM PER_ITEMPRIV_"+planid+" P WHERE P.object_id=O.object_id AND P.body_id=B.body_id)";
			    		dao.insert(sqlStr, new ArrayList());	
			    	}
			    }
			} catch (SQLException e)
			{
			    e.printStackTrace();
			    throw GeneralExceptionHandler.Handle(e);
			}
	    }
	
	
	    if (objectid != null && objectid.length() > 0)
	    {			
	    	
			ArrayList pointItemList = new ArrayList();
			LazyDynaBean abean = null;
	    	if(buf!=null && buf.length()>0)	
	    	{
	    		sql.append("select item_id,itemdesc from per_template_item where item_id in("+ buf.substring(1) + ")");
	    	}else {
                sql.append("select item_id,itemdesc from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+") and child_id is null");
            }
			
			rowSet = dao.search(sql.toString());								
			while (rowSet.next())
			{
				abean = new LazyDynaBean();
				abean.set("item_id", rowSet.getString("item_id"));
				abean.set("itemdesc", rowSet.getString("itemdesc"));
				pointItemList.add(abean);
			}
			
			ArrayList itemPrivList = new ArrayList();
			sql.setLength(0);
			sql.append("select a.*,b.a0101,c.name from (select * from PER_ITEMPRIV_"+planid+" where object_id='"+objectid+"')");
			sql.append(" a left join PER_object b on a.object_id=b.object_id and b.plan_id="+planid);
			sql.append("  left join per_mainbodyset c on a.body_id=c.body_id");
			rowSet = dao.search(sql.toString());
			while (rowSet.next())
			{
			    LazyDynaBean myBean = new LazyDynaBean();
			    myBean.set("body_id", rowSet.getString("body_id"));
			    myBean.set("bodyName", rowSet.getString("name"));
			    myBean.set("object_id", rowSet.getString("object_id"));
			    
			    for(int i=0;i<pointItemList.size();i++)
			    {
				  abean = (LazyDynaBean)pointItemList.get(i);
				  String item_id =(String)abean.get("item_id");
				  myBean.set(item_id, rowSet.getString("C_" + item_id) != null ? rowSet.getString("C_" + item_id) : "0");
			    }
			    itemPrivList.add(myBean);
			}		
			
			map.put("pointItemList", pointItemList);
			map.put("itemPrivList", itemPrivList);
			// 查找制定计划中主体对应的评分确认标识 by 刘蒙
			map.put("optMap", this.getOptMap(planid));
			
	    }

	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return map;
}

	/**
	 * 查询计划中所有主体的打分确认标识
	 * @param planid 计划编号
	 * @return optMap
	 * @author 刘蒙
	 */
	public Map getOptMap(String planid) {
		Map optMap = new HashMap();
		
		StringBuffer sql = new StringBuffer();
		sql.append("select body_id,opt from per_plan_body where plan_id=");
		sql.append(planid);
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try {
			rowSet = dao.search(sql.toString());
			while (rowSet.next()) {
				String opt = rowSet.getString("opt"); // 打分确认标识
				opt = opt == null || "0".equals(opt) ? "" : "disabled";
				optMap.put(rowSet.getString("body_id"), opt);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rowSet != null) {
				try {
					rowSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return optMap;
	}

/**
 * 目标管理计划的项目权限
 * 
 * @param objectids
 * @return
 */
public HashMap getItemPriv2(String objectids,String planid,String  khKey,String khObject)
{
    HashMap map = new HashMap();
    StringBuffer sql = new StringBuffer("");
    RowSet rowSet;
	try
	{
			this.initItemPrivTable(planid);
			ContentDAO dao = new ContentDAO(this.conn);		

			String str="select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+") and child_id is null";
			rowSet = dao.search(str);
			HashMap keyMap=new HashMap();
			while (rowSet.next())
			{
				keyMap.put(rowSet.getString("item_id"),"");			    			   
			}
	    	
			String strSql="select item_id from per_template_point where item_id in(select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+"))";
			rowSet = dao.search(strSql);
			while (rowSet.next())
			{
				keyMap.put(rowSet.getString("item_id"),"");			    			   
			}
			StringBuffer buf = new StringBuffer();
			Set keySet=keyMap.keySet();
			java.util.Iterator t=keySet.iterator();
			while(t.hasNext())
			{
				String strKey = (String)t.next();  //键值				
				buf.append("," + strKey);
			}
			ArrayList pointItemList = new ArrayList();
			LazyDynaBean abean = null;
	    	if(buf!=null && buf.length()>0)	
	    	{
	    		sql.append("select item_id,itemdesc from per_template_item where item_id in("+ buf.substring(1) + ")");
	    	}else {
                sql.append("select item_id,itemdesc from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+") and child_id is null");
            }
						
			rowSet = dao.search(sql.toString());
			while (rowSet.next())
			{
			    abean = new LazyDynaBean();
			    abean.set("item_id", rowSet.getString("item_id"));
			    abean.set("itemdesc", rowSet.getString("itemdesc"));
			    pointItemList.add(abean);
			}
			ArrayList itemPrivList = new ArrayList();
			sql.setLength(0);
			sql.append("select distinct per.*,pers.name,perobj.A0101,keyid.opt as opt from per_object perobj,PER_ITEMPRIV_"+planid+"");
			sql.append(" per left join (select perm.*,ppb.opt as opt from  per_object pero ,per_mainbody perm ");
			// 需要主体对应的opt modify by 刘蒙
			sql.append("LEFT JOIN per_plan_body ppb on ppb.body_id=perm.body_id and ppb.plan_id=perm.plan_id");
			sql.append(" where perm.plan_id = '"+planid+"'" );
			sql.append(" and pero.plan_id = '"+planid+"'");
			sql.append(" and perm.object_id = pero.object_id   ");
			if(khKey!=null&&!"".equals(khKey)&&!"all".equals(khKey)) {
                sql.append(" and  perm.body_id = '"+khKey+"'");
            }
			sql.append(" ) keyid  on keyid.body_id = per.body_id ");
			sql.append(" left join per_mainbodyset pers on pers.body_id=keyid.body_id ");
			sql.append(" where perobj.plan_id ='"+planid+"'");

			if(khObject!=null&&!"".equals(khObject)&&!"all".equals(khObject)) {
                sql.append(" and perobj.body_id = '"+khObject+"'");
            }
			sql.append(" and perobj.object_id = per.object_id");
			sql.append(" and keyid.body_id = per.body_id");
			sql.append(" order by per.object_id");
			
			rowSet = dao.search(sql.toString());
			while (rowSet.next())
			{
			    LazyDynaBean myBean = new LazyDynaBean();
			    myBean.set("body_id", rowSet.getString("body_id"));
			    myBean.set("bodyName", rowSet.getString("name"));
			    myBean.set("object_id", rowSet.getString("object_id"));
			    myBean.set("a0101", rowSet.getString("a0101"));
			    
			    String planBodyOpt = rowSet.getInt("opt") + ""; // 主体对应opt modify by 刘蒙
			    myBean.set("planBodyOpt", planBodyOpt);
			    
			    for(int i=0;i<pointItemList.size();i++)
			    {
				  abean = (LazyDynaBean)pointItemList.get(i);
				  String item_id =(String)abean.get("item_id");
				  myBean.set(item_id, rowSet.getString("C_" + item_id) != null ? rowSet.getString("C_" + item_id) : "0");
			    }
			    itemPrivList.add(myBean);
			}		
			map.put("pointItemList", pointItemList);
			map.put("itemPrivList", itemPrivList);

	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return map;
}
    /**
         * 根据考核计划得到指标权限表头
         * 
         * @param planid
         * @return
         */
    public ArrayList getPointPowerHeadList(String template_id)
    {

	ArrayList list = new ArrayList();
	try
	{
		HashMap map = new HashMap();
		ArrayList seqList = new ArrayList();
		ArrayList tempPointList = new ArrayList();
	    ContentDAO dao = new ContentDAO(this.conn);
	    StringBuffer sql = new StringBuffer("select pp.point_id,pointname,pti.item_id from per_template_item pti,per_template_point ptp ,per_point pp");
	    sql.append(" where pti.item_id=ptp.item_id and ptp.point_id=pp.point_id and pti.template_id='" + template_id + "' ");
	    sql.append(" order by pp.seq");
	    RowSet rowSet = dao.search(sql.toString());
	    LazyDynaBean abean = null;
	    while (rowSet.next())
	    {
		abean = new LazyDynaBean();
		abean.set("point_id", rowSet.getString("point_id"));
		abean.set("pointname", rowSet.getString("pointname"));
//		list.add(abean);
		map.put(rowSet.getString("point_id").toLowerCase(), abean);
		
		String[] temp = new String[11];
		temp[0] = rowSet.getString("point_id");
		temp[3] = rowSet.getString("item_id");
		tempPointList.add(temp);
		
	    }
		BatchGradeBo bo = new BatchGradeBo(this.conn);
	    bo.get_LeafItemList(template_id, tempPointList, seqList);
	    for (int i = 0; i < seqList.size(); i++)
	    {
		String pointId = (String) seqList.get(i);
		list.add(map.get(pointId.toLowerCase()));
	    }
	    
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return list;
    }
    
    
    /**
     * 根据考核计划得到指标权限表头
     * 
     * @param planid
     * @return
     */
public ArrayList getPointPowerHeadList2(String template_id,String planid)
{

ArrayList list = new ArrayList();
try
{
    ContentDAO dao = new ContentDAO(this.conn);
    BatchGradeBo bb = new BatchGradeBo(this.conn, planid);
    ArrayList pointList = bb.getPerPointList(template_id,planid);
    ArrayList perPointList = (ArrayList) pointList.get(1);
    LazyDynaBean abean=null;
    for (Iterator t = perPointList.iterator(); t.hasNext();)
	{
	    String[] temp = (String[]) t.next();
	     
		abean = new LazyDynaBean();
		abean.set("point_id", temp[0]);
		abean.set("pointname", temp[1]);
		list.add(abean);
 
	    
	}
    
    
} catch (Exception e)
{
    e.printStackTrace();
}
return list;
}
    
    
    

    /**
         * 根据考核对象 、计划涉及到的指标数据，得到相应的指标权限列表
         * 
         * @param objectid
         * @param pointPowerHeadList
         * @return
         */
    public ArrayList getPointPowerList(String objectid, String planid, ArrayList pointPowerHeadList)
    {

	ArrayList list = new ArrayList();
	try
	{
	    DbWizard dbWizard = new DbWizard(this.conn);
	    if (dbWizard.isExistTable("per_pointpriv_" + planid, false))
	    {
	    	ContentDAO dao = new ContentDAO(this.conn);
	    	//2013.11.13 begin
			//判断指标列是否存在，不存在则创建
	    	LazyDynaBean dynaBean = null;
			for (int i = 0; i < pointPowerHeadList.size(); i++)
			{
				
				dynaBean = (LazyDynaBean) pointPowerHeadList.get(i);
				String col = ((String) dynaBean.get("point_id")).toUpperCase();
				col = "C_"+col;
				Table table = new Table("per_pointpriv_" + planid);
				boolean flag = false;
				if (!dbWizard.isExistField("per_pointpriv_" + planid, col,false))
				{
					Field obj = new Field(col);
					obj.setDatatype(DataType.INT);
					obj.setKeyable(false);
					table.addField(obj);
					flag = true;
				}
				if (flag){
					dbWizard.addColumns(table);// 更新列
					String tempsql = "update per_pointpriv_"+planid+" set "+col+" = 1";
					try {
						dao.update(tempsql);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}                        
			}
			//2013.11.13 end
		StringBuffer sql = new StringBuffer("select ppr.*,pms.name from per_pointpriv_" + planid + " ppr ");
		sql.append(" left join (select * from per_mainbody where plan_id=" + planid + " and object_id='" + objectid + "') pmb on ppr.mainbody_id=pmb.mainbody_id ");
		sql.append(" left join  per_mainbodyset pms on pmb.body_id=pms.body_id where ppr.object_id='" + objectid + "' ");
		sql.append(" order by pms.seq,ppr.mainbody_id");
		RowSet rowSet = dao.search(sql.toString());
		LazyDynaBean abean = null;
		while (rowSet.next())
		{
		    abean = new LazyDynaBean();
		    abean.set("mainbody_id", isNull(rowSet.getString("mainbody_id")));
		    abean.set("objectid", objectid);
		    abean.set("bodyname", isNull(rowSet.getString("bodyname")));
		    abean.set("bodyType", isNull(rowSet.getString("name")));
		    LazyDynaBean pointBean = null;
		    for (int i = 0; i < pointPowerHeadList.size(); i++)
		    {
			pointBean = (LazyDynaBean) pointPowerHeadList.get(i);
			String point_id = ((String) pointBean.get("point_id")).toLowerCase();
			abean.set(point_id, rowSet.getString("c_" + point_id) != null ? rowSet.getString("c_" + point_id) : "0");
		    }
		    list.add(abean);
		}
	    }

	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return list;
    }

    /*
         * 根据考核对象 、计划涉及到的指标数据，得到相应的指标权限列表 @param objectid @param
         * pointPowerHeadList @return
         */
    public ArrayList getPointPowerList2(String objectid, String planid, ArrayList pointPowerHeadList,String khKey,String khObject)
    {

	ArrayList list = new ArrayList();
	try
	{
	    DbWizard dbWizard = new DbWizard(this.conn);
	    if (dbWizard.isExistTable("per_pointpriv_" + planid, false))
	    {
	    StringBuffer tempSql=new StringBuffer();
	    tempSql.append("select object_id from per_object where  plan_id="+planid+" ");
	    if(khObject!=null&&!"all".equals(khObject)){
	    	tempSql.append(" and body_id='"+khObject+"' ");
	    }
	    if(khKey!=null&&!"all".equals(khKey)){
	    	tempSql.append("  and object_id in (select object_id from per_mainbody where  plan_id="+planid+" and  body_id='"+khKey+"')");
	    }
	    ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer sql = new StringBuffer();
		sql.append("select  *  from per_pointpriv_"+planid+"  pp ,per_mainbody pmb left join  per_mainbodyset pms on pmb.body_id=pms.body_id ");
		sql.append(" where pp.mainbody_id=pmb.mainbody_id  and pp.object_id=pmb.object_id and pmb.plan_id="+planid+" and  pp.object_id in("+tempSql+") ");
	    if(khKey!=null&&!"all".equals(khKey)){
	    	sql.append(" and pmb.body_id="+khKey+" ");
	    }
	    //【2902】绩效管理：指标权限划分，主体类别选全部时，显示顺序乱，应该和考核主体这边保持一致   jingq upd 2014.12.11
	    //sql.append(" order by pmb.mainbody_id");
	    sql.append(" order by pms.body_id");
		RowSet rowSet = dao.search(sql.toString());
		LazyDynaBean abean = null;
		while (rowSet.next())
		{
		    abean = new LazyDynaBean();
		    abean.set("mainbody_id", isNull(rowSet.getString("mainbody_id")));
		    abean.set("objectid", isNull(rowSet.getString("object_id")));
		    abean.set("objectname", this.getNameByCode(rowSet.getString("object_id"),planid));
		    abean.set("bodyname", isNull(rowSet.getString("bodyname")));
		    abean.set("bodyType", isNull(rowSet.getString("name")));
		    LazyDynaBean pointBean = null;
		    for (int i = 0; i < pointPowerHeadList.size(); i++)
		    {
			pointBean = (LazyDynaBean) pointPowerHeadList.get(i);
			String point_id = ((String) pointBean.get("point_id")).toLowerCase();
			abean.set(point_id.toLowerCase(), rowSet.getString("c_" + point_id) != null ? rowSet.getString("c_" + point_id) : "0");
		    }
		    list.add(abean);
		}
	    }

	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return list;
    }

    public String isNull(String str)
    {
    	if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str)) {
            str = "";
        }
		return str;
    }

    public String getNameByCode(String code,String planid)
    {

	String name = "";
	try
	{
	    ContentDAO dao = new ContentDAO(this.conn);
	    RowSet rs = null;

	    rs = dao.search("select * from per_object where plan_id=" + planid + " and object_id='"+code+"'");
	    if (rs.next())
	    {
		name = rs.getString("a0101");
	    }

	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return name;
    }

    /**
         * 取得考核主体面板html
         * 
         * @param perMainBodyList
         * @return
         */
    public String getMainBodyHtml(ArrayList perMainBodyList)
    {

	StringBuffer html = new StringBuffer("");
	for (int i = 0; i < perMainBodyList.size(); i++)
	{
	    LazyDynaBean abean = (LazyDynaBean) perMainBodyList.get(i);
	    String fillctrl = (String) abean.get("fillctrl");

	    html.append("#" + (String) abean.get("mainbody_id") + "`" + (String) abean.get("b0110") + "`" + (String) abean.get("e0122"));
	    html.append("`" + (String) abean.get("e01a1") + "`" + (String) abean.get("a0101"));
	    html.append("`" + (String) abean.get("bodyTypeName") + "`" + (String) abean.get("object_id") + "/" + (String) abean.get("mainbody_id") + "/" + (String) abean.get("plan_id") + "/"
		    + fillctrl);
	}
	if (html.length() > 0) {
        return html.substring(1);
    }
	return html.toString();
    }

    /**
         * 取得考核主体面板html
         * 
         * @param perMainBodyList
         * @return
         */
    public String getPointPrivHtml(ArrayList pointPowerHeadList, ArrayList pointPowerList)
    {

	StringBuffer html = new StringBuffer("");
	html.append(ResourceFactory.getProperty("kh.field.serialMainbody"));
	for (int j = 0; j < pointPowerHeadList.size(); j++)
	{
	    LazyDynaBean abean = (LazyDynaBean) pointPowerHeadList.get(j);
	    String pointname = (String) abean.get("pointname");
	    html.append("/" + pointname);
	}
	html.append("/" + ResourceFactory.getProperty("lable.performance.evaluateMan"));
	for (int i = 0; i < pointPowerList.size(); i++)
	{
	    LazyDynaBean aabean = (LazyDynaBean) pointPowerList.get(i);
	    html.append("#");
	    html.append((i + 1));

	    html.append("/" + aabean.get("bodyType"));
	    html.append("/" + (String) aabean.get("bodyname"));

	    String mainbody_id = (String) aabean.get("mainbody_id");
	    String objectid = (String) aabean.get("objectid");

	    for (int j = 0; j < pointPowerHeadList.size(); j++)
	    {
		LazyDynaBean abean = (LazyDynaBean) pointPowerHeadList.get(j);
		String point_id = (String) abean.get("point_id");
		String temp = (String) aabean.get(point_id);
		html.append("/");

		html.append(mainbody_id + "`" + objectid + "`" + point_id + "`");
		html.append(temp);
		/*
                 * if(temp.equals("1")) html.append("有"); else html.append("无");
                 */
	    }
	    html.append("/" + (String) aabean.get("bodyname"));
	}
	return html.toString();
    }

    /**
         * 恢复默认指标权限
         * @param power_type: point 指标权限 item 项目权限
         * @param plainid
         * @param objectid
         */
    public void recoverPriv(String planid, String objectid,String power_type)
    {

	ContentDAO dao = new ContentDAO(this.conn);
	try
	{
		String tableName = "";
		if("point".equalsIgnoreCase(power_type)){
			tableName = "per_pointpriv_"+planid;  
			//恢复默认指标权限 使主体个数与指标权限中的主体个数一致
			this.agreeSubjectNumber(planid, objectid, tableName);
		}
		else if("item".equalsIgnoreCase(power_type)) {
            tableName ="per_itempriv_"+planid;
        }
		StringBuffer buf = new StringBuffer();
		buf.append("select * from ");
		buf.append(tableName);
		buf.append( " where 1=2");		
		
	    RowSet rowSet = dao.search(buf.toString());
	    ResultSetMetaData md = rowSet.getMetaData();
	    StringBuffer fields = new StringBuffer("");
	    for (int i = 0; i < md.getColumnCount(); i++)
	    {
		String columnName = md.getColumnName(i + 1);
		if (columnName.length() > 2 && "C_".equalsIgnoreCase(columnName.substring(0, 2))) {
            fields.append("," + columnName + "=1");
        }
	    }

	    if (fields.length() > 0)
	    {

		dao.update("update " + tableName + " set " + fields.substring(1) + " where object_id='" + objectid + "'");
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}

    }
    /**
     * 恢复默认指标权限 使主体个数与指标权限中的主体个数一致
     * @return
     */
    public void agreeSubjectNumber(String planid,String objectid,String tableName){
    	ContentDAO dao = new ContentDAO(this.conn);
		String sll="delete from "+tableName+" where object_id='"+objectid+"' and not exists(select * from per_mainbody where per_mainbody.id="+tableName+".id)";
		String sol="insert into "+tableName+" (id,b0110,e0122,e01a1,object_id,mainbody_id,bodyname) select id,b0110,e0122,e01a1,object_id,mainbody_id,a0101 from per_mainbody " +
		"where plan_id='"+planid+"' and object_id='"+objectid+"' and not exists(select * from "+tableName+" where "+tableName+".id=per_mainbody.id)";
		try {
		    ArrayList pointList = getPerPointList(planid);
		    StringBuffer sql3 = new StringBuffer();
		    StringBuffer sql_extend3 = new StringBuffer();
		    for (int i = 0; i < pointList.size(); i++)
		    {
				sql3.append("," + pointList.get(i));
				sql_extend3.append(",'1'");
		    }
		    sol="insert into "+tableName+" (id,b0110,e0122,e01a1,object_id,mainbody_id,bodyname"+sql3.toString()+") select id,b0110,e0122,e01a1,object_id,mainbody_id,a0101"+sql_extend3.toString()+" from per_mainbody " +
			"where plan_id='"+planid+"' and object_id='"+objectid+"' and not exists(select * from "+tableName+" where "+tableName+".id=per_mainbody.id)";
			dao.delete(sll, new ArrayList());
			dao.insert(sol, new ArrayList());

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
    }

    /*
         * 获得人员条件查询的表集合
         */
    public ArrayList getTablelist()
    {

	ArrayList list = new ArrayList();
	ArrayList fieldsetlist = userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
	StringBuffer fieldSetId = new StringBuffer();
	for (Iterator it = fieldsetlist.iterator(); it.hasNext();)
	{
	    FieldSet fs = (FieldSet) it.next();
	    fieldSetId.append("'" + fs.getFieldsetid() + "',");
	}
	if (fieldSetId.length() < 1) {
        return list;
    }
	String strSql = "select fieldsetid,customdesc from fieldset where fieldsetid in(" + fieldSetId.substring(0, fieldSetId.length() - 1) + ") order by displayorder";
	try
	{
	    ContentDAO dao = new ContentDAO(this.conn);
	    RowSet rset = dao.search(strSql);
	    while (rset.next())
	    {
		CommonData temp = new CommonData(rset.getString("fieldsetid"), rset.getString("customdesc"));
		list.add(temp);
	    }
	} catch (Exception ex)
	{
	    ex.printStackTrace();
	}
	return list;
    }

    public ArrayList getFieldlist(String tablename)
    {

	ArrayList list = new ArrayList();
	if (tablename == null || "".equals(tablename)) {
        tablename = "A01";
    }
	ArrayList fieldsetlist = userView.getPrivFieldList(tablename);
	StringBuffer fieldSetId = new StringBuffer();
	for (Iterator it = fieldsetlist.iterator(); it.hasNext();)
	{
	    FieldItem fi = (FieldItem) it.next();
	    // 条件选人界面过滤掉备注型指标 lium
	    if ("M".equals(fi.getItemtype())) {
	    	continue;
	    }
	    CommonData temp = new CommonData(fi.getItemid() + "<@>" + fi.getItemdesc() + "<@>" + fi.getItemtype() + "<@>" + fi.getCodesetid() + "<@>" + tablename, fi.getItemdesc());
	    list.add(temp);
	}

	return list;
    }

    public ArrayList getKhObjectsList(String objectIds, String planid) throws GeneralException
    {

	ArrayList list = new ArrayList();
	if (objectIds == null || "".equals(objectIds)) {
        return list;
    }
	ContentDAO dao = new ContentDAO(this.conn);
	String[] array = objectIds.split("@");
	StringBuffer sql_where = new StringBuffer();
	sql_where.append(" and object_id in (");
	for (int i = 0; i < array.length; i++)
	{
	    if ("".equals(array[i])) {
            continue;
        }
	    sql_where.append("'" + array[i] + "',");
	}
	sql_where = new StringBuffer(sql_where.substring(0, sql_where.length() - 1) + ")");
	String strSql = "select * from per_object where plan_id=" + planid + sql_where.toString()+" order by a0000 ";
	try
	{
	    RowSet rset = dao.search(strSql);
	    while (rset.next())
	    {
		CommonData temp = new CommonData(rset.getString("object_id"), rset.getString("a0101"));
		list.add(temp);
	    }
	} catch (Exception ex)
	{
	    ex.printStackTrace();
	}
	return list;

    }

    
    
    
    /*
    *取得当前计划内涉及到的考核主体的类别   慧聪网需求   zhaoxg add
    *
    */
    public ArrayList getKhKyeClassList(String planid) throws GeneralException
    {
    	ContentDAO dao = new ContentDAO(this.conn);
		ArrayList khKeyClasslist = new ArrayList();
		String keyclass="select body_id,name from per_mainbodyset where   body_id in (select distinct body_id as body_id from per_mainbody where  plan_id="+planid+") and (body_type = '0' or body_type is null)";
	  try{
		  RowSet rset = dao.search(keyclass);
		    while (rset.next())
		    {
				CommonData temp = new CommonData(rset.getString("body_id"), rset.getString("name"));
				khKeyClasslist.add(temp);
		    }
 			CommonData temp = new CommonData("all", ResourceFactory.getProperty("edit_report.All"));
 			khKeyClasslist.add(temp);
		} catch (Exception ex)
		{
		    ex.printStackTrace();
		}
			return khKeyClasslist;
    
    }
    
    
    
    /*
     *取得当前计划内涉及到的考核对象的类别     慧聪网需求  zhaoxg add
     *
     *
     */
     public ArrayList getKhObjectsClassList(String planid) throws GeneralException
     {
      	ContentDAO dao = new ContentDAO(this.conn);
 		ArrayList KhObjectsClassList = new ArrayList();
 		
 	  try{
 		  String objclass="select body_id,name from per_mainbodyset where body_type = '1' and body_id in (select distinct body_id as body_id from per_object where  plan_id="+planid+")";
 		  RowSet rset = dao.search(objclass);
 		  CommonData temp = new CommonData("all", ResourceFactory.getProperty("edit_report.All"));
 		  KhObjectsClassList.add(temp);
		  while (rset.next())
		  {
 			CommonData temp1 = new CommonData(rset.getString("body_id"), rset.getString("name"));
 			KhObjectsClassList.add(temp1);
		  }
 		} catch (Exception ex)
 		{
 		    ex.printStackTrace();
 		}
 			return KhObjectsClassList;
     
     }
     
    /*
         * 根据考核对象取考核主体
         */
    public ArrayList getMainBodyList(String objectIds, String plan_id) throws GeneralException
    {

	ArrayList list = new ArrayList();
	if (objectIds == null || "".equals(objectIds)) {
        return list;
    }

	String[] array = objectIds.split("@");

	StringBuffer sql_where = new StringBuffer();
	sql_where.append(" and object_id in (");
	for (int i = 0; i < array.length; i++)
	{
	    if ("".equals(array[i])) {
            continue;
        }
	    sql_where.append("'" + array[i] + "',");
	}
	sql_where = new StringBuffer(sql_where.substring(0, sql_where.length() - 1) + ")");

	String strSql = "SELECT distinct mainbody_id, A0101 FROM per_mainbody where plan_id=" + plan_id + sql_where.toString();
	boolean flag = true;
	try
	{
	    ContentDAO dao = new ContentDAO(this.conn);
	    RowSet rset = dao.search(strSql);

	    while (rset.next())
	    {
		CommonData temp = new CommonData(rset.getString("mainbody_id"), rset.getString("A0101"));
		list.add(temp);
		flag = false;
	    }
	} catch (Exception ex)
	{
	    ex.printStackTrace();
	}
	if (flag) {
        throw new GeneralException(ResourceFactory.getProperty("lable.performance.objectNoMainbody") + "!");
    }
	return list;
    }

    /*
         * 取得指标名称
         */
    public String getPointName(String point_id)
    {

	String pointName = "";
	if (point_id == null || "".equals(point_id)) {
        return pointName;
    }
	ContentDAO dao = new ContentDAO(this.conn);
	try
	{
	    RowSet rs = dao.search("select pointname from per_point where point_id='" + point_id + "'");
	    if (rs.next()) {
            pointName = rs.getString("pointname");
        }
	} catch (SQLException e)
	{
	    e.printStackTrace();
	}

	return pointName;
    }

    /*
         * 取考核指标
         */
    public ArrayList getPointList(String planid)
    {

	ArrayList list = new ArrayList();
	ContentDAO dao = new ContentDAO(this.conn);
	try
	{
	    RowSet rowSet = dao.search("select * from per_pointpriv_" + planid + " where 1=2");
	    ResultSetMetaData md = rowSet.getMetaData();
	    StringBuffer fields = new StringBuffer("");
	    for (int i = 0; i < md.getColumnCount(); i++)
	    {
		String columnName = md.getColumnName(i + 1);
		if (columnName.length() > 2 && "C_".equalsIgnoreCase(columnName.substring(0, 2)))
		{
		    CommonData temp = new CommonData(columnName, this.getPointName(columnName.substring(2)));
		    list.add(temp);
		}
	    }

	} catch (Exception e)
	{
	    e.printStackTrace();
	}

	return list;
    }

    /*
         * 根据考核计划,考核对象,考核主体类别查询考核主体
         */
    public ArrayList getMainBodyList2(String plan_id, HashMap objs, String body_id)
    {

		ArrayList list = new ArrayList();
	
		if (plan_id == null) {
            return list;
        }
	
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    StringBuffer sql = new StringBuffer();
		    sql.append("select a.mainbody_id,a.b0110,a.e0122,a.e01a1,a.a0101,a.body_id,b.name,a.object_id from per_mainbody a  ");
		    sql.append("left join PER_MAINBODYSET b on a.body_id=b.body_id ");
		    sql.append("where a.plan_id=" + plan_id);
	
		    RowSet rowSet = null; 
		    StringBuffer buf = new StringBuffer();
		    Set objSet = objs.keySet();
		    Iterator it = objSet.iterator();
		    while (it.hasNext())
		    {
				String objId = (String) it.next();
				buf.append(",'" + objId + "'");
		    }
	
		    if (buf.length() > 0) {
                sql.append(" and a.object_id in(" + buf.substring(1) + ")");
            }
	
		    if (!"all".equals(body_id)) {
                sql.append(" and a.body_id='" + body_id + "'");
            } else if ("all".equals(body_id))
		    {
				String strSql = "Select P.body_id, B.Name FROM per_plan_body P, per_mainbodyset B WHERE P.body_id = B.body_id and plan_id =" + plan_id + " ORDER BY b.seq ";// and
				// P.body_id<>5
				// System.out.println(strSql);
				rowSet = dao.search(strSql.toString());
				StringBuffer temp = new StringBuffer();
				while (rowSet.next())
				{
				    temp.append(",'" + rowSet.getString("body_id") + "'");
				}
		
				if(temp.length()>0) {
                    sql.append(" and a.body_id in (" + temp.substring(1) + ") ");
                }
		    }
		    sql.append(" and a.mainbody_id not in (select mainbody_id from per_mainbody ");
		    sql.append(" where plan_id = " + plan_id + " and id = mainbody_id ");
		    sql.append(" and B0110 is null and E0122 is null and E01A1 is null and A0101 = ' ') ");
		   
		    sql.append(" order by a.object_id,b.seq,a.b0110,a.e0122,a.e01a1");	
		    rowSet = dao.search(sql.toString());
		    LazyDynaBean abean = null;
		    while (rowSet.next())
		    {
				String b0110 = rowSet.getString("b0110");
				String e0122 = rowSet.getString("e0122");
				String e01a1 = rowSet.getString("e01a1");
				String a0101 = rowSet.getString("a0101");
				abean = new LazyDynaBean();
				abean.set("mainbody_id", rowSet.getString("mainbody_id"));
				String objectID = rowSet.getString("object_id");
				String objectName = (String) objs.get(objectID);
				abean.set("objectID", objectID);
				abean.set("objectName", objectName == null ? "" : objectName);
				abean.set("b0110", b0110 != null ? AdminCode.getCodeName("UN", b0110) : "");
				abean.set("e0122", e0122 != null ? AdminCode.getCodeName("UM", e0122) : "");
				abean.set("e01a1", e01a1 != null ? AdminCode.getCodeName("@K", e01a1) : "");
				abean.set("a0101", rowSet.getString("a0101")==null?"":rowSet.getString("a0101"));
				abean.set("bodyTypeName", rowSet.getString("name")==null?"":rowSet.getString("name"));
				abean.set("bodyid", rowSet.getString("body_id"));
				list.add(abean);
		    }   
	
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return list;
    }

    public void delKhMainBody(String[] ids, String planID)
    {

	ContentDAO dao = new ContentDAO(this.conn);
	ArrayList list = new ArrayList();
	ArrayList listl = new ArrayList();
	try
	{
		RowSet rs = null;
		boolean isSelf=false;
		rs=dao.search("select body_id from per_plan_body where body_id=5 and plan_id="+planID);
		if(rs.next())
		{
			isSelf=true;
		}		
		
		// 删除考核主体表
		StringBuffer strSql = new StringBuffer();
		strSql.append("delete from per_mainbody where mainbody_id=? and object_id=? and body_id=?");
		strSql.append(" and plan_id=" + planID);

		// 删除权限表
		StringBuffer strSql2 = new StringBuffer();
		strSql2.append("delete from per_pointpriv_");
		strSql2.append(planID);
		strSql2.append(" where mainbody_id=? and object_id=?");

		for (int i = 0; i < ids.length; i++)
		{	
			String id = ids[i];// mainbody_id:objectID:body_id
			String[] temp = id.split(":");
			String mainBody = temp[0];
			String object_id = temp[1];
			String body_id = temp[2];
	    
			if("5".equals(body_id) && isSelf) {
                continue;
            }
			
			ArrayList list1 = new ArrayList();
			list1.add(mainBody);
			list1.add(object_id);
			list.add(list1);
	    
			ArrayList list2 = new ArrayList();
			list2.add(mainBody);
			list2.add(object_id);
			list2.add(body_id);
			listl.add(list2);
		}
	
	    dao.batchUpdate(strSql.toString(), listl);
	    dao.batchUpdate(strSql2.toString(), list);
	    
	    RecordVo vo = this.getPlanVo(planID);
	    String method = vo.getString("method");
	    //删除考核信息表的打分记录 目标和360计划的打分表不一样
	    StringBuffer delBuf = new StringBuffer();
	    if("1".equals(method))
	    {
	    	PendingTask pt = new PendingTask();
	    	String mainbodyStr = this.isHaveObject(ids, planID);
			for (int i = 0; i < ids.length; i++)
			{	
				String id = ids[i];// mainbody_id:objectID:body_id
				String[] temp = id.split(":");
				String mainBody = temp[0];
				if(mainbodyStr.indexOf("'"+mainBody+"'")==-1){
					String sql = "select * from t_hr_pendingtask where pending_type='33' and ext_flag like 'PERPF_"+planID+"%' and  receiver='Usr"+mainBody+"' and pending_status='9'";
					RowSet rs1=dao.search(sql);
					while(rs1.next()){					
						String pending_id = rs1.getString("pending_id");
						dao.update("update t_hr_pendingtask set pending_status='4' where pending_id='"+pending_id+"'");
						pt.updatePending("P", "PER"+pending_id, 100, "主体删除", this.userView);
					}
				}
			}
	    	
	    	delBuf.append("DELETE FROM PER_TABLE_"+planID+" WHERE NOT EXISTS (SELECT * FROM per_mainbody WHERE plan_id ="+planID);
	 	    delBuf.append(" AND object_id = PER_TABLE_"+planID+".object_id AND mainbody_id = PER_TABLE_"+planID+".mainbody_id)");
	    }else if("2".equals(method))
	    {
	    	delBuf.append("DELETE FROM per_target_evaluation WHERE plan_id ="+planID+" and NOT EXISTS (SELECT * FROM per_mainbody WHERE plan_id ="+planID);
	 	    delBuf.append(" AND per_mainbody.plan_id = per_target_evaluation.plan_id  AND per_mainbody.object_id = per_target_evaluation.object_id ");	
	 	    delBuf.append("  AND per_mainbody.mainbody_id = per_target_evaluation.mainbody_id)");
	    }	   
	    
	    dao.delete(delBuf.toString(), new ArrayList());
	    //删除面谈结果记录表
	    delBuf.setLength(0);
	    delBuf.append("DELETE FROM per_interview WHERE plan_id ="+planID+" AND NOT EXISTS(SELECT * FROM per_mainbody B WHERE B.plan_id="+planID);
	    delBuf.append(" AND per_interview.plan_id = B.plan_id AND per_interview.mainbody_id = B.mainbody_id  AND per_interview.object_id = B.object_id)");
	    dao.delete(delBuf.toString(), new ArrayList());
	    //以上为默认删除操作 对于目标计划还有如下考虑
	    //1. 如当前要删除的主体的对象的审批流程为空或已批准. 则只做默认操作
	    //2. 如要删除的主体的对象的审批流程在流程中, 则检查对象的当前审批员是不是选定主体,
	    //2.1 是, 则要初始化对象及其所有主体的审批状态等.
	    //2.2 不是, 则不影响. 只做默认操作.
	    if("2".equals(method))
	    {  
	    	for (int i = 0; i < ids.length; i++)
			{	
				String id = ids[i];// mainbody_id:objectID:body_id
				String[] temp = id.split(":");
				String mainBody = temp[0];
				String object_id = temp[1];
				
			    delBuf.setLength(0);
			    delBuf.append("select * from per_object where plan_id="+planID);
			    delBuf.append(" and object_id='"+object_id+"'");
			    delBuf.append(" and currappuser='"+mainBody+"'");
			    delBuf.append(" and  NOT (sp_flag IS NULL) AND (sp_flag<>'03')");
			    rs = dao.search(delBuf.toString());
			    if(rs.next())
			    {
			    	//初始化对象
			    	ClearTargetEvaluation(object_id,planID);
			    }
			}
	    	if(rs!=null) {
                rs.close();
            }
	    }	
	    
	    //删除待办记录 chent 20160220 start
	    for (int i = 0; i < ids.length; i++)
		{
	    	String id = ids[i];// mainbody_id:objectID:body_id
			String[] temp = id.split(":");
			String mainBody = temp[0];
		    //查看删除的考核主体在当前计划下是否存在考核对象
			String sql = "select count(object_id) as count from per_mainbody where plan_id="+planID+" and mainbody_id='"+mainBody+"'";
			RowSet rs1 = dao.search(sql);
		    while(rs1.next()){
		    	int count = rs1.getInt("count");
		    	if(count == 0) {//不存在、删除待办
		    		String delSql = "delete from t_hr_pendingtask where pending_type='33' and receiver='Usr"+mainBody+"' and ext_flag like 'PERPF_"+planID+"%'";
		    		dao.update(delSql);
		    	} else{//存在，不删除待办
		    		break;
		    	}
		    }
		    if(rs1!=null) {
                rs1.close();
            }
		}
	  //删除待办记录 chent 20160220 end
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
    }
    /**
     * 取得删除的主体在此计划下还存在别的对象不，存在则返回主体号
     * @param ids
     * @param planID
     * @return
     */
    public String isHaveObject(String[] ids, String planID){
    	StringBuffer mainbodystr = new StringBuffer();
    	try{
    		ContentDAO dao = new ContentDAO(this.conn);
	    	
	    	StringBuffer str = new StringBuffer();
			for (int i = 0; i < ids.length; i++)
			{	
				String id = ids[i];// mainbody_id:objectID:body_id
				String[] temp = id.split(":");
				String mainBody = temp[0];
				
				str.append(",'");
				str.append(mainBody);
				str.append("'");
			}
			String sql = "select mainbody_id from per_mainbody where mainbody_id in ("+str.substring(1)+") and plan_id="+planID+"";
    		RowSet rs = dao.search(sql);
    		while(rs.next()){
    			mainbodystr.append(",'");
    			mainbodystr.append(rs.getString("mainbody_id"));
    			mainbodystr.append("'");
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return mainbodystr.length()==0?"''":mainbodystr.toString().substring(1);
    }
    /**初始化目标计划的流程操作*/
    public void ClearTargetEvaluation(String object_id,String planID)
    {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs=null;
		//1. 清除 per_target_evaluation 表本计划这个对象所有记录.
		// 可以避免有任表作废了, 但打分信息还在.
		StringBuffer delBuf = new StringBuffer();
		delBuf.append("delete from per_target_evaluation where plan_id="+planID);
		if(object_id!=null && ! "".equals(object_id)) {
            delBuf.append(" AND object_id='"+object_id+"'");
        }

		try{
			dao.delete(delBuf.toString(), new ArrayList());
			//2. 置per_object 表的相应记录 sp_flag,
			StringBuffer strSql=new StringBuffer();
			ArrayList list=new ArrayList();
			list.add(planID);
			strSql.append("select currappuser from per_object where plan_id=?");//取所有审批人
			if(object_id!=null && ! "".equals(object_id)) {
				strSql.append(" AND object_id=?");
				list.add(object_id);
			}
			ArrayList<String> currappuserList=new ArrayList<String>();
			rs=dao.search(strSql.toString(),list);
			while(rs.next()) {
                currappuserList.add(rs.getString("currappuser"));
            }

			delBuf.setLength(0);
			delBuf.append("update per_object set sp_flag=NULL,currappuser=NULL where plan_id=?");
			if(object_id!=null && ! "".equals(object_id)) {
                delBuf.append(" AND object_id=?");
            }
			dao.update(delBuf.toString(),list);

			delBuf.setLength(0);
			delBuf.append("update per_mainbody set sp_flag=NULL,sp_date=NULL,reasons=NULL,status=NULL where plan_id=?");
			if(object_id!=null && ! "".equals(object_id)) {
                delBuf.append(" AND object_id=?");
            }
			dao.update(delBuf.toString(),list);

			if(currappuserList.size()>0) {//清除无效的目标卡审批代办 zhanghua 2018-3-6
				strSql.setLength(0);
				list.clear();
				list.add(planID);
				strSql.append("select pm.mainbody_id from per_mainbody pm INNER JOIN per_object po ON pm.mainbody_id=po.currappuser AND pm.plan_id=po.plan_id where po.sp_flag ='02' and pm.plan_id=?");
				strSql.append(" and pm.mainbody_id in(");
				for (String currappuser : currappuserList) {
					strSql.append("?,");
					list.add(currappuser);
				}
				strSql.deleteCharAt(strSql.length() - 1);
				strSql.append(") group by pm.mainbody_id having count(1) >0");//判断审批人是否还存在需要审批的记录
				rs = dao.search(strSql.toString(),list);
				while (rs.next()){
					int num=-1;
					for(int i=0;i<currappuserList.size();i++){
						if(currappuserList.get(i).equalsIgnoreCase(rs.getString("mainbody_id"))){
							num=i;
							break;
						}
					}
					if(num!=-1) {
                        currappuserList.remove(num);
                    }
				}
				for(String currappuser:currappuserList) {
					PendingTask pt = new PendingTask();
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("oper", "distribute");
					bean.set("sql", strSql.toString());
					bean.set("title", "清除目标卡审批代办");
					bean = this.updatePendingTask(this.conn, this.userView, "Usr" + currappuser, planID, bean, "100");
					if (bean.getMap().containsKey("pending_id") && "update".equals(bean.get("flag"))) {
						pt.updatePending("P", "PER" + bean.get("pending_id"), 100, "清除目标卡审批代办", this.userView);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
	}
    /*
         * 指定考核主体
         */
    public void selMainBody(String a0100s, String planid, String mainBodyType, String object,String accordByDepartment) throws GeneralException
    {

    	a0100s = PubFunc.keyWord_reback(a0100s);
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
	
		    this.planVo = this.getPlanVo(planid);
		    // 获得需要的计划参数
			LoadXml loadXml = null; //new LoadXml();
	    	if(BatchGradeBo.getPlanLoadXmlMap().get(planid)==null)
			{							
				loadXml = new LoadXml(this.conn,planid);
				BatchGradeBo.getPlanLoadXmlMap().put(planid,loadXml);
			}
			else
			{
				loadXml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(planid);
			}
            Hashtable htxml = loadXml.getDegreeWhole();           
            String spByBodySeq = (String)htxml.get("SpByBodySeq"); // 按考核主体顺序号控制审批流程(True, False默认为False)
            String gradeByBodySeq = (String)htxml.get("GradeByBodySeq"); // 按考核主体顺序号控制评分流程(True, False默认为False)
            HashMap objectidMap = null;	
            HashMap bodyidMap = null;	
            HashMap levelMap = null;	
            if((spByBodySeq!=null && spByBodySeq.trim().length()>0 && "true".equalsIgnoreCase(spByBodySeq)) && (this.planVo.getInt("method")==2 && this.planVo.getInt("object_type")!=2) && ("-1".equalsIgnoreCase(mainBodyType))) // 团队负责人
            {	
            	objectidMap = getObjectidMainbodyidMap(planid); // 获得考核对象设置的考核关系            	
            	bodyidMap = getObjectidMainbodysetMap(planid,object); // 判断此考核对象是否已设置某主体类别的主体            	            	
            }
            if((spByBodySeq!=null && spByBodySeq.trim().length()>0 && "true".equalsIgnoreCase(spByBodySeq)) && (this.planVo.getInt("method")==2)) {
                levelMap = getMainbodyLevelMap(mainBodyType); // 获得选择的考核主体的level
            }
		    
            // 获得考核主体类别评分顺序 
		    HashMap mainbodyScoreSortMap = null;
		    if((gradeByBodySeq!=null && gradeByBodySeq.trim().length()>0 && "true".equalsIgnoreCase(gradeByBodySeq)) && this.planVo.getInt("method")==2) {
                mainbodyScoreSortMap = getMainbody_idScoreSortMap(planid);
            }
            
		    HashMap codesetMap = new HashMap();
		    HashMap parentMap = new HashMap();
		    HashMap upperE0122Map = new HashMap();
		    StringBuffer sqlStr = new StringBuffer();
		    RowSet rs = null;
		    if("true".equalsIgnoreCase(accordByDepartment))//人员类别计划 条件选择 按部门匹配 ---- 和考核对象是同一个部门或者是考核对象的上级部门
		    {
		    	sqlStr.append( "select codeitemid,parentid,codesetid from organization where upper(codesetid) ='UM' or upper(codesetid)='UN' order by codesetid ");
		    	rs = dao.search(sqlStr.toString());
		 		while (rs.next())
		 		{
		 			codesetMap.put(rs.getString("codeitemid"), rs.getString("codesetid"));
		 			parentMap.put(rs.getString("codeitemid"), rs.getString("parentid"));
		 		}  
		 		
			    sqlStr.setLength(0);
			    sqlStr.append("select e0122 from per_object where plan_id="+planid+" and object_id='"+object+"'");
			   	rs = dao.search(sqlStr.toString());
			   	if(rs.next())
			   	{
			   		String e0122 = rs.getString(1);
			   		upperE0122Map.put(e0122, e0122);
			   		
			   		while(parentMap.get(e0122)!=null && "UM".equalsIgnoreCase((String)codesetMap.get((String)parentMap.get(e0122))))
			   		{
			   			e0122 = (String)parentMap.get(e0122);
			   			upperE0122Map.put(e0122, e0122);
			   		}
			   	}
		    }	     
		    
		    boolean pSelf=false;//本人的标志，有本人了，就不能再把自己以非本人的类别作为考核主体
		    sqlStr.setLength(0);
		    sqlStr.append("select a.* from ");
		    sqlStr.append("(select * from per_mainbodyset where status=1  and (body_type=0 or body_type is null))");
		    sqlStr.append(" a join per_plan_body b on a.body_id=b.body_id  and b.plan_id=");
		    sqlStr.append(planid);
		    sqlStr.append(" and b.body_id=5");
		    rs = dao.search(sqlStr.toString());
		    if(rs.next()) {
                pSelf=true;
            }
		    
		    StringBuffer sql = new StringBuffer("");
		    sql.append("select B0110,E0122,E01A1,A0100,A0101 from usra01 where a0100 in (" + a0100s + ") ");
		    String sql2 = "insert into per_mainbody (id,b0110,e0122,e01a1,object_id,mainbody_id,a0101,body_id,plan_id,status,seq)values(?,?,?,?,?,?,?,?,?,?,?)";
		    if((spByBodySeq!=null && spByBodySeq.trim().length()>0 && "true".equalsIgnoreCase(spByBodySeq)) && (this.planVo.getInt("method")==2)) {
                sql2 = "insert into per_mainbody (id,b0110,e0122,e01a1,object_id,mainbody_id,a0101,body_id,plan_id,status,sp_seq,seq)values(?,?,?,?,?,?,?,?,?,?,?,?)";
            }
		    
		    ArrayList pointList = getPerPointList(planid);
		    StringBuffer sql3 = new StringBuffer("insert into per_pointpriv_" + planid + " (id,b0110,e0122,e01a1,object_id,mainbody_id,bodyname");
		    StringBuffer sql_extend3 = new StringBuffer("?,?,?,?,?,?,?");
		    for (int i = 0; i < pointList.size(); i++)
		    {
				sql3.append("," + pointList.get(i));
				sql_extend3.append(",?");
		    }
		    // 先删除该对象已存在的重复主体(本人不能删除)
		    String delStr = "delete from per_mainbody where object_id = '" + object + "' and mainbody_id in (" + a0100s + ") and body_id!=5 and plan_id="+planid;
		    dao.delete(delStr, new ArrayList());
		    if(a0100s.indexOf(object)!=-1)
		    {
			//    delStr = "delete from per_pointpriv_" + planid + " where object_id = '" + object + "' and mainbody_id in (" + a0100s + ") ";//and mainbody_id<>'"+ object + "'";//要删除本人的权限
			//    dao.delete(delStr, new ArrayList());
		    }else
		    {
		    	delStr = "delete from per_pointpriv_" + planid + " where object_id = '" + object + "' and mainbody_id in (" + a0100s + ") and mainbody_id<>'"+ object + "'";//不要删除本人的权限
			    dao.delete(delStr, new ArrayList());
		    }
		    
	
		    // 若设置了按考核主体顺序号控制审批流程参数并且是目标计划并且是非人员 则在选择团队负责人时一同把团队负责人在考核关系中定义的考核主体给对象添加上
		    if((spByBodySeq!=null && spByBodySeq.trim().length()>0 && "true".equalsIgnoreCase(spByBodySeq)) && (this.planVo.getInt("method")==2 && this.planVo.getInt("object_type")!=2) && ("-1".equalsIgnoreCase(mainBodyType))) // 团队负责人
		    {	
		    	ArrayList recordMainbodyList = new ArrayList();
		    	ArrayList recordList3 = new ArrayList();
		    	String sqlMainbody = "insert into per_mainbody (id,b0110,e0122,e01a1,object_id,mainbody_id,a0101,body_id,plan_id,status,sp_seq,seq)values(?,?,?,?,?,?,?,?,?,?,?,?)";
		    	
		    	String[] mainbody_ids = a0100s.split(",");
		    	String mainbody_id = "";
		    	for (int i = 0; i < mainbody_ids.length; i++)
				{					
					if(objectidMap.get(mainbody_ids[i].substring(1, mainbody_ids[i].length()-1))!=null)
					{
						mainbody_id = mainbody_ids[i].substring(1, mainbody_ids[i].length()-1);
						break;
					}					
				}
		    			    	
		    	if(objectidMap.get(mainbody_id)!=null)
			    {
			    	ArrayList mainbodyidList = (ArrayList)objectidMap.get(mainbody_id);   //value值   						 
			 		for(int i=0;i<mainbodyidList.size();i++)
			 		{
			 			LazyDynaBean abean=(LazyDynaBean)mainbodyidList.get(i);	
			 			String level = (String)abean.get("level");
			 			// 判断此考核对象是否已设置某主体类别的主体  若设置了则不再添加
			 			if(bodyidMap.get((String)abean.get("body_id"))!=null) {
                            continue;
                        }
			 							 			
	 					ArrayList tempList2 = new ArrayList();		 			
	 					IDGenerator idgm = new IDGenerator(2, conn);
	 					String idm = idgm.getId("per_mainbody.id");
	 					tempList2.add(new Integer(idm));
	 					tempList2.add((String)abean.get("b0110"));
	 					tempList2.add((String)abean.get("e0122"));
	 					tempList2.add((String)abean.get("e01a1"));
	 					tempList2.add(object);
	 					tempList2.add((String)abean.get("mainbody_id"));
	 					tempList2.add((String)abean.get("a0101"));
	 					tempList2.add(new Integer((String)abean.get("body_id")));
	 					tempList2.add(new Integer(planid));
	 					tempList2.add(new Integer(0));
	 					if(level!=null && level.trim().length()>0) {
                            tempList2.add(new Integer(level));
                        } else {
                            tempList2.add(null);
                        }
	 					if(mainbodyScoreSortMap!=null && mainbodyScoreSortMap.get(String.valueOf(mainBodyType).trim())!=null) {
                            tempList2.add(new Integer((String)mainbodyScoreSortMap.get(String.valueOf(mainBodyType).trim())));
                        } else {
                            tempList2.add(null);
                        }
	 					recordMainbodyList.add(tempList2);	
	 					
	 					ArrayList tempList3 = new ArrayList();
	 					tempList3.add(new Integer(idm));
	 					tempList3.add((String)abean.get("b0110"));
	 					tempList3.add((String)abean.get("e0122"));
	 					tempList3.add((String)abean.get("e01a1"));
	 					tempList3.add(object);
	 					tempList3.add((String)abean.get("mainbody_id"));
	 					tempList3.add((String)abean.get("a0101"));
	 					for (int j = 0; j < pointList.size(); j++) {
                            tempList3.add(new Integer(1));
                        }
	 					recordList3.add(tempList3);
			 		}				    					    	
			    }		    			    	
		    	dao.batchInsert(sqlMainbody, recordMainbodyList);
		    	dao.batchInsert(sql3.toString() + ")values(" + sql_extend3.toString() + ")", recordList3);
		    }
		    
		    
		    
			ArrayList recordList2 = new ArrayList();
			ArrayList recordList3 = new ArrayList();
			rs = dao.search(sql.toString());
			while (rs.next())
			{
				String a0100 = rs.getString("a0100");
				if(pSelf && a0100.equals(object))//当前计划设置了本人类别，就不能再把自己以非本人的类别作为考核主体
                {
                    continue;
                }
				if("true".equalsIgnoreCase(accordByDepartment))//人员类别计划 条件选择 按部门匹配 ---- 和考核对象是同一个部门或者是考核对象的上级部门
				{
					String e0122  =  rs.getString("e0122");
					if(upperE0122Map.get(e0122)==null) {
                        continue;
                    }
				}		
					
				ArrayList tempList2 = new ArrayList();
				ArrayList tempList3 = new ArrayList();
			
				IDGenerator idg = new IDGenerator(2, conn);
				String id = idg.getId("per_mainbody.id");
				tempList2.add(new Integer(id));
				tempList2.add(rs.getString("b0110"));
				tempList2.add(rs.getString("e0122"));
				tempList2.add(rs.getString("e01a1"));
				tempList2.add(object);
				tempList2.add(rs.getString("a0100"));
				tempList2.add(rs.getString("a0101"));
				tempList2.add(new Integer(mainBodyType));
				tempList2.add(new Integer(planid));
				tempList2.add(new Integer(0));
				if((spByBodySeq!=null && spByBodySeq.trim().length()>0 && "true".equalsIgnoreCase(spByBodySeq)) && (this.planVo.getInt("method")==2))
				{
					String level = (String)levelMap.get(mainBodyType);
					if(level!=null && level.trim().length()>0) {
                        tempList2.add(new Integer(level));
                    } else {
                        tempList2.add(null);
                    }
				}
				if(mainbodyScoreSortMap!=null && mainbodyScoreSortMap.get(String.valueOf(mainBodyType).trim())!=null) {
                    tempList2.add(new Integer((String)mainbodyScoreSortMap.get(String.valueOf(mainBodyType).trim())));
                } else {
                    tempList2.add(null);
                }
				recordList2.add(tempList2);
			
				tempList3.add(new Integer(id));
				tempList3.add(rs.getString("b0110"));
				tempList3.add(rs.getString("e0122"));
				tempList3.add(rs.getString("e01a1"));
				tempList3.add(object);
				tempList3.add(rs.getString("a0100"));
				tempList3.add(rs.getString("a0101"));
				for (int i = 0; i < pointList.size(); i++) {
                    tempList3.add(new Integer(1));
                }
				recordList3.add(tempList3);
			}
			dao.batchInsert(sql2, recordList2);
			dao.batchInsert(sql3.toString() + ")values(" + sql_extend3.toString() + ")", recordList3);
		    		    
		    if(rs!=null) {
                rs.close();
            }
		    
		} catch (Exception e)
		{
			e.printStackTrace();
		    throw new GeneralException(e.getMessage());	    
		}
    }
    /*
     * 指定考核主体
     */
public void selMainBody2(String a0100s, String planid, String mainBodyType, String object,String accordByDepartment,boolean pSelf) throws GeneralException
{
	a0100s = PubFunc.keyWord_reback(a0100s);
	try
	{
	    ContentDAO dao = new ContentDAO(this.conn);

	    this.planVo = this.getPlanVo(planid);
	    // 获得需要的计划参数
		LoadXml loadXml = null; //new LoadXml();
    	if(BatchGradeBo.getPlanLoadXmlMap().get(planid)==null)
		{							
			loadXml = new LoadXml(this.conn,planid);
			BatchGradeBo.getPlanLoadXmlMap().put(planid,loadXml);
		}
		else
		{
			loadXml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(planid);
		}
        Hashtable htxml = loadXml.getDegreeWhole();           
        String spByBodySeq = (String)htxml.get("SpByBodySeq"); // 按考核主体顺序号控制审批流程(True, False默认为False)
//      HashMap objectidMap = null;	
//      HashMap bodyidMap = null;	
        HashMap levelMap = null;	
//      if((spByBodySeq!=null && spByBodySeq.trim().length()>0 && spByBodySeq.equalsIgnoreCase("true")) && (this.planVo.getInt("method")==2 && this.planVo.getInt("object_type")!=2) && (mainBodyType.equalsIgnoreCase("-1"))) // 团队负责人
//      {	
//       	objectidMap = getObjectidMainbodyidMap(planid); // 获得考核对象设置的考核关系            	
//        	bodyidMap = getObjectidMainbodysetMap(planid,object); // 判断此考核对象是否已设置某主体类别的主体            	            	
//      }
        if((spByBodySeq!=null && spByBodySeq.trim().length()>0 && "true".equalsIgnoreCase(spByBodySeq)) && (this.planVo.getInt("method")==2)) {
            levelMap = getMainbodyLevelMap(mainBodyType); // 获得选择的考核主体的level
        }
	    	    
	    HashMap codesetMap = new HashMap();
	    HashMap parentMap = new HashMap();
	    HashMap upperE0122Map = new HashMap();
	    StringBuffer sqlStr = new StringBuffer();
	    RowSet rs = null;
	    
	    String mainbody_ids = "";
	    rs = dao.search(a0100s);
	    while(rs.next())
	   	{
	   		String a0100 = rs.getString("a0100");
	   		mainbody_ids += "," + a0100;
	   	}
	    
//	    if(accordByDepartment.equalsIgnoreCase("true"))//人员类别计划 条件选择 按部门匹配 ---- 和考核对象是同一个部门或者是考核对象的上级部门
//	    {
//	    	sqlStr.append( "select codeitemid,parentid,codesetid from organization where upper(codesetid) ='UM' or upper(codesetid)='UN' order by codesetid ");
//	    	rs = dao.search(sqlStr.toString());
//	 		while (rs.next())
//	 		{
//	 			codesetMap.put(rs.getString("codeitemid"), rs.getString("codesetid"));
//	 			parentMap.put(rs.getString("codeitemid"), rs.getString("parentid"));
//	 		}  
//	 		
//		    sqlStr.setLength(0);
//		    sqlStr.append("select e0122 from per_object where plan_id="+planid+" and object_id='"+object+"'");
//		   	rs = dao.search(sqlStr.toString());
//		   	if(rs.next())
//		   	{
//		   		String e0122 = rs.getString(1);
//		   		upperE0122Map.put(e0122, e0122);
//		   		
//		   		while(parentMap.get(e0122)!=null && ((String)codesetMap.get((String)parentMap.get(e0122))).equalsIgnoreCase("UM"))
//		   		{
//		   			e0122 = (String)parentMap.get(e0122);
//		   			upperE0122Map.put(e0122, e0122);
//		   		}
//		   	}
//	    }	     
	    
	    
	    StringBuffer sql = new StringBuffer("");
	    sql.append("select B0110,E0122,E01A1,A0100,A0101 from usra01 where exists(select * from  (" + a0100s + ") temp where  usra01.a0100=temp.a0100  )  ");
	    String sql2 = "insert into per_mainbody (id,b0110,e0122,e01a1,object_id,mainbody_id,a0101,body_id,plan_id,status)values(?,?,?,?,?,?,?,?,?,?)";
	    if((spByBodySeq!=null && spByBodySeq.trim().length()>0 && "true".equalsIgnoreCase(spByBodySeq)) && (this.planVo.getInt("method")==2)) {
            sql2 = "insert into per_mainbody (id,b0110,e0122,e01a1,object_id,mainbody_id,a0101,body_id,plan_id,status,sp_seq)values(?,?,?,?,?,?,?,?,?,?,?)";
        }
	    
	    ArrayList pointList = getPerPointList(planid);
	    StringBuffer sql3 = new StringBuffer("insert into per_pointpriv_" + planid + " (id,b0110,e0122,e01a1,object_id,mainbody_id,bodyname");
	    StringBuffer sql_extend3 = new StringBuffer("?,?,?,?,?,?,?");
	    for (int i = 0; i < pointList.size(); i++)
	    {
			sql3.append("," + pointList.get(i));
			sql_extend3.append(",?");
	    }
	    // 先删除该对象已存在的重复主体(本人不能删除)
	    String delStr = "delete from per_mainbody where object_id = '" + object + "' and  exists(select * from  (" + a0100s + ") temp where  per_mainbody.mainbody_id=temp.a0100  ) and body_id!=5 and plan_id="+planid;
	    dao.delete(delStr, new ArrayList());
	    
	    {
	    	delStr = "delete from per_pointpriv_" + planid + " where object_id = '" + object + "' and exists(select * from  (" + a0100s + ") temp where  per_pointpriv_" + planid + ".mainbody_id=temp.a0100  ) and mainbody_id<>'"+ object + "'";//不要删除本人的权限
		    dao.delete(delStr, new ArrayList());
	    }
	    
	  //获得最大值id
//	    int id =0;
//	    rs = dao.search(" select max(id)+1 as id from per_mainbody ");
//	    if(rs.next())
//	    id = rs.getInt("id");
	    int id_num=20;
	    IDGenerator idg = new IDGenerator(2, this.conn);
	    ArrayList idlist = idg.getId("per_mainbody.id",id_num);
	    rs = dao.search(sql.toString());

	    ArrayList recordList2 = new ArrayList();
	    ArrayList recordList3 = new ArrayList();
	    
	    int index=0;
	    String id ="0";
	    while (rs.next())
	    {
			String a0100 = rs.getString("a0100");
			if(pSelf && a0100.equals(object))//当前计划设置了本人类别，就不能再把自己以非本人的类别作为考核主体
            {
                continue;
            }
//			if(accordByDepartment.equalsIgnoreCase("true"))//人员类别计划 条件选择 按部门匹配 ---- 和考核对象是同一个部门或者是考核对象的上级部门
//			{
//				String e0122  =  rs.getString("e0122");
//				if(upperE0122Map.get(e0122)==null)
//				    continue;
//			}		
			
			ArrayList tempList2 = new ArrayList();
			ArrayList tempList3 = new ArrayList();
	
			if(index<20) {
                id=(String)idlist.get(index++);
            } else
			{
				idlist= idg.getId("per_mainbody.id",id_num);
				index=0;
				id=(String)idlist.get(index++);
			}
			tempList2.add(new Integer(id));
			tempList2.add(rs.getString("b0110"));
			tempList2.add(rs.getString("e0122"));
			tempList2.add(rs.getString("e01a1"));
			tempList2.add(object);
			tempList2.add(rs.getString("a0100"));
			tempList2.add(rs.getString("a0101"));
			tempList2.add(new Integer(mainBodyType));
			tempList2.add(new Integer(planid));
			tempList2.add(new Integer(0));
			if((spByBodySeq!=null && spByBodySeq.trim().length()>0 && "true".equalsIgnoreCase(spByBodySeq)) && (this.planVo.getInt("method")==2))
			{
				String level = (String)levelMap.get(mainBodyType);
				if(level!=null && level.trim().length()>0) {
                    tempList2.add(new Integer(level));
                } else {
                    tempList2.add(null);
                }
			}
			recordList2.add(tempList2);
	
			tempList3.add(new Integer(id));
			tempList3.add(rs.getString("b0110"));
			tempList3.add(rs.getString("e0122"));
			tempList3.add(rs.getString("e01a1"));
			tempList3.add(object);
			tempList3.add(rs.getString("a0100"));
			tempList3.add(rs.getString("a0101"));
			for (int i = 0; i < pointList.size(); i++) {
                tempList3.add(new Integer(1));
            }
			recordList3.add(tempList3);
			if("-1".equals(mainBodyType)){//团队负责人只用一个
				break;
				
			}
			
	    }
	    dao.batchInsert(sql2, recordList2);
	    dao.batchInsert(sql3.toString() + ")values(" + sql_extend3.toString() + ")", recordList3);

	    if(rs!=null) {
            rs.close();
        }
	} catch (Exception e)
	{
		e.printStackTrace();
	    throw new GeneralException(e.getMessage());	    
	}
}

    /*
         * 粘贴考核主体
         */
    public void pasteKhMainBody(HashMap mainBodyCopyed, String planIdPasted, String objectId) throws GeneralException
    {

		String planIdCopyed = (String) mainBodyCopyed.get("planID");
		String objectIdCopyed = (String) mainBodyCopyed.get("objectID");
		
		if(planIdCopyed.equalsIgnoreCase(planIdPasted) && objectIdCopyed.equals(objectId))//如果是给自己粘贴考核主体
        {
            return;
        }
		
		// 得到要粘贴的考核主体
		StringBuffer strSql = new StringBuffer();
		strSql.append("select b0110,e0122,e01a1,object_id,mainbody_id,a0101,body_id,plan_id,status,seq,sp_seq from per_mainbody where plan_id=");
		strSql.append(planIdCopyed);
		strSql.append(" and body_id not in (5,-1) and body_id in (select body_id from per_plan_body where plan_id="+planIdPasted+" and body_id not in (5,-1)) and object_id = '");
		strSql.append(objectIdCopyed);
		strSql.append("' and mainbody_id not in (select distinct mainbody_id from per_mainbody where plan_id=");
		strSql.append(planIdPasted);
		strSql.append(" and object_id = '");
		strSql.append(objectId);
		strSql.append("')");
	
		String sql2 = "insert into per_mainbody (id,b0110,e0122,e01a1,object_id,mainbody_id,a0101,body_id,plan_id,status,seq,sp_seq)values(?,?,?,?,?,?,?,?,?,?,?,?)";
		ArrayList pointList = getPerPointList(planIdPasted);
		StringBuffer sql3 = new StringBuffer("insert into per_pointpriv_" + planIdPasted + " (id,b0110,e0122,e01a1,object_id,mainbody_id,bodyname");
		StringBuffer sql_extend3 = new StringBuffer("?,?,?,?,?,?,?");
		for (int i = 0; i < pointList.size(); i++)
		{
		    sql3.append("," + pointList.get(i));
		    sql_extend3.append(",?");
		}
	
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList recordList2 = new ArrayList();
		ArrayList recordList3 = new ArrayList();
		ArrayList mainbodys = new ArrayList();
		try
		{
		    RowSet rowSet = dao.search(strSql.toString());
		
		    while (rowSet.next())
		    {
				ArrayList tempList2 = new ArrayList();
				ArrayList tempList3 = new ArrayList();
		
				IDGenerator idg = new IDGenerator(2, conn);
				String id = idg.getId("per_mainbody.id");
				tempList2.add(new Integer(id));
				tempList2.add(rowSet.getString("b0110"));
				tempList2.add(rowSet.getString("e0122"));
				tempList2.add(rowSet.getString("e01a1"));
				tempList2.add(objectId);
				tempList2.add(rowSet.getString("mainbody_id"));
				tempList2.add(rowSet.getString("a0101"));
				tempList2.add(rowSet.getString("body_id"));
				tempList2.add(planIdPasted);
				tempList2.add("0");
				tempList2.add(rowSet.getString("seq"));
				tempList2.add(rowSet.getString("sp_seq"));
				recordList2.add(tempList2);
		
				mainbodys.add(rowSet.getString("mainbody_id"));
				
				tempList3.add(new Integer(id));
				tempList3.add(rowSet.getString("b0110"));
				tempList3.add(rowSet.getString("e0122"));
				tempList3.add(rowSet.getString("e01a1"));
				tempList3.add(objectId);
				tempList3.add(rowSet.getString("mainbody_id"));
				tempList3.add(rowSet.getString("a0101"));
		
				for (int i = 0; i < pointList.size(); i++) {
                    tempList3.add(new Integer(1));
                }
		
				recordList3.add(tempList3);
		    }
		    
		    if(mainbodys.size()==0)//如果将要粘贴的考核主体为空
            {
                return;
            }
		    
		    dao.batchInsert(sql2, recordList2);
		    dao.batchInsert(sql3.toString() + ")values(" + sql_extend3.toString() + ")", recordList3);
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		
		//如果是在同一个计划里面粘贴考核主体，还要精确复制考核主体的指标权限；否则就将被粘贴主体的指标权限按默认的赋值
		if(!planIdPasted.equals(planIdCopyed)) {
            return;
        }
		StringBuffer buf = new StringBuffer();
		buf.append("update per_pointpriv_"+planIdPasted+" set ");
		for (int i = 0; i < pointList.size(); i++) {
            buf.append(pointList.get(i)+"=?,");
        }
		buf.setLength(buf.length()-1);
		buf.append(" where object_id=? and mainbody_id=?");
		
		StringBuffer buf2 = new StringBuffer();
		buf2.append("select ");
		for (int i = 0; i < pointList.size(); i++) {
            buf2.append(pointList.get(i)+",");
        }
		buf2.append("mainbody_id");
		buf2.append(" from per_pointpriv_"+planIdCopyed);
		buf2.append(" where mainbody_id in (");
		for (int i = 0; i < mainbodys.size(); i++) {
            buf2.append("'"+(String)mainbodys.get(i)+"',");
        }
		buf2.setLength(buf2.length()-1);
		buf2.append(") and object_id='"+objectIdCopyed+"'") ;
		
		try
		{
		    RowSet rowSet = dao.search(buf2.toString());
		    ArrayList updatePris=new ArrayList();
		    while (rowSet.next())
		    {
				ArrayList list = new ArrayList();
				for (int i = 0; i < pointList.size(); i++) {
                    list.add(rowSet.getString((String)pointList.get(i)));
                }
				list.add(objectId);
				list.add(rowSet.getString("mainbody_id"));
				updatePris.add(list);
		    }
		    if(pointList.size()>0) {
                dao.batchUpdate(buf.toString(), updatePris);
            }
		}catch (Exception e)
		{
		    e.printStackTrace();
		}		
	
    }

    /*
         * 清除考核主体
         */
    public void clearKhMainBody(String[] objects, String plan_id)
    {

		ContentDAO dao = new ContentDAO(this.conn);
	
		StringBuffer ids = new StringBuffer();
		for (int i = 0; i < objects.length; i++)
		{
		    if ("".equals(objects[i])) {
                continue;
            }
		    ids.append("'" + objects[i] + "'");
		    ids.append(",");
		}
		ids.setLength(ids.length() - 1);
	
		StringBuffer strSql = new StringBuffer();
		strSql.append("delete from per_mainbody where object_id  in (");
		strSql.append(ids.toString());
		strSql.append(") and plan_id=");
		strSql.append(plan_id);
		strSql.append(" and (body_id<>5)");
	
		try
		{
		    dao.delete(strSql.toString(), new ArrayList());
		    boolean isHaveSel = false;//这个计划是否设置了本人的主体类别
		    String sql = "select body_id  from per_plan_body where plan_id="+plan_id+" and body_id=5";
		    RowSet rs=dao.search(sql);
		    if(rs.next()) {
                isHaveSel=true;
            }
		    	    
		    strSql = new StringBuffer();
		    strSql.append("delete from PER_POINTPRIV_" + plan_id);
		    strSql.append(" where object_id in (");
		    strSql.append(ids.toString());
		    strSql.append(") ");
		    if(isHaveSel) {
                strSql.append("and mainbody_id!=object_id");
            }
		    dao.delete(strSql.toString(), new ArrayList());
	
		    strSql = new StringBuffer();
		    strSql.append("delete from PER_TABLE_" + plan_id);
		    strSql.append(" where object_id in (");
		    strSql.append(ids.toString());
		    strSql.append(") ");
		    if(isHaveSel) {
                strSql.append("and mainbody_id!=object_id");
            }
		    dao.delete(strSql.toString(), new ArrayList());
	
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
    }

    /**
         * 根据考核对象和考核计划得到对应的主体信息
         * 
         * @param objectid
         * @param planid
         * 
         * //考核主体对考核对象数据采集的状态： //（0，1，2，3, 4）=（未打分，正在编辑，已提交，部分提交, 不打分）
         * PerObject_Blank = 0; PerObject_Editing = 1; PerObject_Commit = 2;
         * PerObject_PartCommit = 3; PerObject_NoMarking = 4;
         * PerObject_CommitNoMarking = 7; // 不打分且已提交了（BS不能再编辑了）
         * 
         * @return
         */
    public ArrayList getMainbodyListByObject(String objectid, String planid)
    {
		ArrayList list = new ArrayList();
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    
		    RecordVo vo= getPlanVo(planid);
		    LoadXml loadXml=new LoadXml(this.conn, planid);
			Hashtable params = loadXml.getDegreeWhole();
			String readerType = (String)params.get("ReaderType");//机读类型:0光标阅读机(默认),1扫描仪
				
		    String gather_type = vo.getString("gather_type");//0 网上 1 机读 2:网上+机读
		    int index = 0;
		    StringBuffer strSql = new StringBuffer();
			strSql.append("select mainbody_id,a0101," + Sql_switcher.isnull("per_mainbody.status", "0") + " status,per_mainbodyset.name ");
			strSql.append(" from per_mainbody,per_mainbodyset where per_mainbody.body_id=per_mainbodyset.body_id ");
			strSql.append(" and plan_id=" + planid + " and object_id='" + objectid + "' ");			
			if(!"0".equalsIgnoreCase(gather_type))
			{
				strSql.append(" and per_mainbody.mainbody_id in (select mainbody_id from per_mainbody ");
				strSql.append(" where plan_id = " + planid + " and id = mainbody_id ");
			    strSql.append(" and B0110 is null and E0122 is null and E01A1 is null and (A0101 = ' ' or A0101 IS NULL)) ");
			}						
			strSql.append(" order by per_mainbodyset.seq ");
		    RowSet rowSet = dao.search(strSql.toString());
		    while (rowSet.next())
		    {
				int status = rowSet.getInt("status");
				String temp = "";
				if("0".equals(gather_type))
				{
					switch (status)
					{
					case 0:
					    temp = ResourceFactory.getProperty("lable.performance.notMark"); // 未打分
					    break;
					case 1:
					    temp = ResourceFactory.getProperty("edit_report.status.zzbj"); // 正在编辑
					    break;
					case 2:
					    temp = ResourceFactory.getProperty("edit_report.status.subed"); // 已提交
					    break;
					case 3:
					    temp = ResourceFactory.getProperty("edit_report.status.partSubed"); // 部分提交
					    break;
					case 4:
					    temp = ResourceFactory.getProperty("lable.performnace.noMark"); // 不打分
					    break;
					}
				}else if(!"0".equals(gather_type))
				{
					switch (status)
					{
					case 0:
					    temp = ResourceFactory.getProperty("lable.performance.notMark"); // 未打分
					    break;
					case 3:
					    temp = ResourceFactory.getProperty("lable.performance.ballot.part"); // 部分打分
					    break;
					case 2:			
					case 1:			
					case 4:
						temp = "";
					    break;
					}
				}
				
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("body_id", PubFunc.encrypt(rowSet.getString(1)));
				String context=rowSet.getString(2);
				if(vo.getInt("plan_type")==0 || (!"0".equals(gather_type) && "1".equals(readerType) && vo.getInt("plan_type")==1))// 不记名 或者是 机读计划 扫描仪 记名
                {
                    context = rowSet.getString("name");
                }
				temp=temp.length()>0?"(" + temp + ")":"";
				String tempStr = (context!=null?context:"") +  temp ;
				if(vo.getInt("plan_type")==0 || (!"0".equals(gather_type) && "1".equals(readerType) && vo.getInt("plan_type")==1))//计划类型 0 不记名 1 记名
                {
                    tempStr= ++index+"."+tempStr;
                }
				abean.set("context",tempStr);
				list.add(abean);
		    }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return list;
    }
    
    public void moveRecord2(String num1,String move) throws GeneralException
    {
    	HashMap map = new HashMap();
    	HashMap map2 = new HashMap();
    	int count = 0;
    	try
    	{
    	    ContentDAO dao = new ContentDAO(this.conn);
    	    StringBuffer sql = new StringBuffer("");
    	    sql.append("select num1,seq from degree_highset order by seq");

    	    RowSet rowSet = dao.search(sql.toString());
    	    while (rowSet.next())
    	    {    	
    	    	String num=rowSet.getString("num1");
    	    	String seq=rowSet.getString("seq");
    	    	if(seq==null) {
                    continue;
                }
    	    	
    	    	count++;
    	    	map.put("num"+num, Integer.toString(count));
    	    	map.put(Integer.toString(count), seq); 
    	    	map2.put("seq"+seq, "num"+num);
    	    }
    	    String num1Index  =(String)map.get("num"+num1);
    	    String num1_seq=(String)map.get(num1Index);
    	    if(num1Index==null || num1_seq==null) {
                throw new GeneralException("排序字段为空值,无法重新排序！");
            }
    	    if("up".equalsIgnoreCase(move) && "1".equals(num1Index)) {
                throw new GeneralException("已经是第一条记录,不允许上移！");
            }
    	    if("down".equalsIgnoreCase(move) && Integer.parseInt(num1Index)==count) {
                throw new GeneralException("已经是最后一条记录,不允许下移！");
            }
    	    
    	    String objid2="";//用于交换的对象
    	    String objid2_seq="";
    	    
    	    if("up".equalsIgnoreCase(move)) {
                objid2_seq = (String)map.get(Integer.toString(Integer.parseInt(num1Index)-1));
            } else  if("down".equalsIgnoreCase(move)) {
                objid2_seq = (String)map.get(Integer.toString(Integer.parseInt(num1Index)+1));
            }
    	  
    	    objid2 =  ((String)map2.get("seq"+objid2_seq)).substring(3);
    	    //相邻对象交换排序字段 a0000
    	    dao.update("update degree_highset set seq="+objid2_seq+" where num1='"+num1+"' ");
    	    dao.update("update degree_highset set seq="+num1_seq+" where num1='"+objid2+"' ");    	    
   	    
    	} catch (Exception e)
    	{
    	    e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    }
    
    
/**
 * 考核实施 移动记录
 * @throws GeneralException 
 */
    public void moveRecord(String planid,String object_id,String move,String code,String codeset) throws GeneralException
    {
    	HashMap map = new HashMap();
    	HashMap map2 = new HashMap();
    	int count = 0;
    	try
    	{
    	    ContentDAO dao = new ContentDAO(this.conn);
    	    StringBuffer sql = new StringBuffer("select po.object_id,po.a0000 ");
    	    sql.append(" from per_object po ");
    	    sql.append(" where po.plan_id=" + planid);
    	    if (code != null && !"-1".equals(code))
    	    {
    		if ("UN".equalsIgnoreCase(codeset)) {
                sql.append(" and po.b0110 like '" + code + "%' ");
            } else if ("UM".equalsIgnoreCase(codeset)) {
                sql.append(" and po.e0122 like '" + code + "%' ");
            }
    	    }
 
    		sql.append(" order by a0000");

    	    RowSet rowSet = dao.search(sql.toString());
    	    while (rowSet.next())
    	    {    	
    	    	String objid=rowSet.getString("object_id");
    	    	String a0000=rowSet.getString("a0000");
    	    	if(a0000==null) {
                    continue;
                }
    	    	
    	    	count++;
    	    	map.put(objid, Integer.toString(count));
    	    	map.put(Integer.toString(count), a0000); 
    	    	map2.put(a0000, objid);
    	    }
    	    String objIndex =(String)map.get(object_id);
    	    String objid_a0000=(String)map.get(objIndex);
    	    if(objIndex==null || objid_a0000==null) {
                throw new GeneralException("排序字段为空值,无法重新排序！");
            }
    	    if("up".equalsIgnoreCase(move) && "1".equals(objIndex)) {
                throw new GeneralException("已经是第一条记录,不允许上移！");
            }
    	    if("down".equalsIgnoreCase(move) && Integer.parseInt(objIndex)==count) {
                throw new GeneralException("已经是最后一条记录,不允许下移！");
            }
    	    
    	    String objid2="";//用于交换的对象
    	    String objid2_a0000="";
    	    
    	    if("up".equalsIgnoreCase(move)) {
                objid2_a0000 = (String)map.get(Integer.toString(Integer.parseInt(objIndex)-1));
            } else  if("down".equalsIgnoreCase(move)) {
                objid2_a0000 = (String)map.get(Integer.toString(Integer.parseInt(objIndex)+1));
            }
    	  
    	    objid2 =  (String)map2.get(objid2_a0000);
    	    //相邻对象交换排序字段 a0000
    	    dao.update("update per_object set a0000="+objid2_a0000+" where object_id='"+object_id+"' and plan_id='"+planid+"'");
    	    dao.update("update per_object set a0000="+objid_a0000+" where object_id='"+objid2+"' and plan_id='"+planid+"'");    	    
   	    
    	} catch (Exception e)
    	{
    	    e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    }
    /**目标卡制定中 得到可以引入的上期目标卡计划
     * @throws GeneralException */          //  JinChunhai  2011.03.01 修改
    public ArrayList getLastRelaPlans(String planid,String privWhl,String noApproveTargetCanScore) throws GeneralException
    {
    	ArrayList list = new ArrayList();
        RowSet rowSet =null;
        RowSet rowSet1 =null;
        String object_type = "";
        String template_id = "";
        String cycle="";
        String month = "";
        String year = "";
        String quarter = "";
        java.sql.Date startdate = null;
        java.sql.Date enddate = null;
    	try
    	{
    	    ContentDAO dao = new ContentDAO(this.conn);
    	    
    	    StringBuffer sql = new StringBuffer("select * from per_plan where plan_id="+planid);
    	    rowSet = dao.search(sql.toString());
    	    if (rowSet.next())
    	    {    	
    	    	 object_type=rowSet.getString("object_type");	 
    	    	 cycle=rowSet.getString("cycle");
    	    	 month=rowSet.getString("themonth"); 
    	    	 year=rowSet.getString("theyear");
    	    	 template_id=rowSet.getString("template_id"); 
    	    	 quarter=rowSet.getString("thequarter");
    	    	 startdate=rowSet.getDate("start_date");
    	    	 enddate=rowSet.getDate("end_date");
    	    }
    	    
    	    sql.setLength(0);
    	    sql.append("select * from per_plan where method=2 ");
    	    sql.append(" and template_id='"+template_id+"' ");
    	    sql.append(" and cycle="+cycle+" ");
    	    if("7".equals(cycle))
    	    {    	    	
    			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    			String date=format.format(startdate);
    			sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+date.substring(0, 4));
    			sql.append(" or ( "+Sql_switcher.year("start_date")+"="+date.substring(0, 4)+" and "+Sql_switcher.month("start_date")+"<"+date.substring(date.indexOf("-")+1, date.lastIndexOf("-"))+" ) ");
    			sql.append(" or ( "+Sql_switcher.year("start_date")+"="+date.substring(0, 4)+" and "+Sql_switcher.month("start_date")+"="+date.substring(date.indexOf("-")+1, date.lastIndexOf("-"))+" and "+Sql_switcher.day("start_date")+"<"+date.substring(date.lastIndexOf("-")+1)+" ) ) ");    			
    			sql.append(" order by start_date desc ");
    	    }
//    	        sql.append(" and end_date>=(select start_date from per_plan where plan_id="+planid+")");
    	    else {
                sql.append(ExamPlanBo.getLasyPeriod(cycle, year, month, quarter));
            }
//    	    sql.append(" order by "+Sql_switcher.isnull("a0000", "999999999")+" asc,plan_id desc");
    	    rowSet = dao.search(sql.toString());
    	    int i=0;
    	    StringBuffer str = new StringBuffer();
    	    while (rowSet.next())
    	    {
    	    	if(i>500){
	       	    	 sql.setLength(0);
	    	    	 sql.append("select plan_id from per_object where plan_id in ("+str.substring(1)+") "+privWhl+" ");
	    	    	 if("false".equalsIgnoreCase(noApproveTargetCanScore)){//上期目标卡所在计划启动时选择了“允许对未批准的目标卡进行评分”，就无需判断目标卡是否已批了 zhaoxg add 2016-8-16
	    	    		 sql.append(" and (sp_flag='03' or sp_flag='06') ");
	    	    	 }
	    	    	 sql.append(" and object_id in (");
	    	      	 sql.append("select object_id from per_object where  plan_id in ("+str.substring(1)+") "+privWhl+") group by plan_id");
	    	      	 rowSet1 = dao.search(sql.toString());
	    	    	 while(rowSet1.next())
	    	    	 {
	    	    		list.add(rowSet1.getString("plan_id")); 
	    	    	 }
	    	    	 str = new StringBuffer();
	    	    	 i=0;
    	    	}
    	    	str.append(",");
    	    	str.append(rowSet.getString("plan_id"));
    	    	i++;
    	    }
    	    if(str.length()>0){
      	    	 sql.setLength(0);
    	    	 sql.append("select plan_id from per_object where plan_id in ("+str.substring(1)+") "+privWhl+" ");
    	    	 if("false".equalsIgnoreCase(noApproveTargetCanScore)){//上期目标卡所在计划启动时选择了“允许对未批准的目标卡进行评分”，就无需判断目标卡是否已批了 zhaoxg add 2016-8-16
    	    		 sql.append(" and (sp_flag='03' or sp_flag='06') ");
    	    	 }
    	    	 sql.append(" and object_id in (");
    	      	 sql.append("select object_id from per_object where  plan_id in ("+str.substring(1)+") "+privWhl+") group by plan_id");
    	      	 rowSet1 = dao.search(sql.toString());
    	    	 while(rowSet1.next())
    	    	 {
    	    		list.add(rowSet1.getString("plan_id")); 
    	    	 }
    	    }
    	} catch (Exception e)
    	{
    	    e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    	return list;
    }
    public ArrayList getLastRelaPlans1(String planid,String privWhl,String noApproveTargetCanScore) throws GeneralException
    {
    	ArrayList list = new ArrayList();
        RowSet rowSet =null;
        RowSet rowSet1 =null;
        RowSet rowSet2 =null;
        String object_type = "";
        String template_id = "";
        String cycle="";
        String month = "";
        String year = "";
        String quarter = "";
        java.sql.Date startdate = null;
        java.sql.Date enddate = null;
    	try
    	{
    	    ContentDAO dao = new ContentDAO(this.conn);
    	    
    	    StringBuffer sql = new StringBuffer("select * from per_plan where plan_id="+planid);
    	    rowSet = dao.search(sql.toString());
    	    if (rowSet.next())
    	    {    	
    	    	 object_type=rowSet.getString("object_type");	 
    	    	 cycle=rowSet.getString("cycle");
    	    	 month=rowSet.getString("themonth"); 
    	    	 year=rowSet.getString("theyear");
    	    	 template_id=rowSet.getString("template_id"); 
    	    	 quarter=rowSet.getString("thequarter");
    	    	 startdate=rowSet.getDate("start_date");
    	    	 enddate=rowSet.getDate("end_date");
    	    }
    	    
    	    sql.setLength(0);
    	    sql.append("select * from per_plan where method=2 ");
    	    sql.append(" and template_id='"+template_id+"' ");
    	    sql.append(" and cycle="+cycle+" ");
    	    if("7".equals(cycle))
    	    {    	    	
    			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    			String date=format.format(startdate);
    			sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+date.substring(0, 4));
    			sql.append(" or ( "+Sql_switcher.year("start_date")+"="+date.substring(0, 4)+" and "+Sql_switcher.month("start_date")+"<"+date.substring(date.indexOf("-")+1, date.lastIndexOf("-"))+" ) ");
    			sql.append(" or ( "+Sql_switcher.year("start_date")+"="+date.substring(0, 4)+" and "+Sql_switcher.month("start_date")+"="+date.substring(date.indexOf("-")+1, date.lastIndexOf("-"))+" and "+Sql_switcher.day("start_date")+"<"+date.substring(date.lastIndexOf("-")+1)+" ) ) ");    			
    			sql.append(" order by start_date desc ");
    	    }
//    	        sql.append(" and end_date>=(select start_date from per_plan where plan_id="+planid+")");
    	    else {
                sql.append(ExamPlanBo.getLasyPeriod(cycle, year, month, quarter));
            }
//    	    sql.append(" order by "+Sql_switcher.isnull("a0000", "999999999")+" asc,plan_id desc");
    	    rowSet = dao.search(sql.toString());
    	    int i=0;
    	    StringBuffer str = new StringBuffer();
    	    while (rowSet.next())
    	    {
    	    	
    	    	
    	    	String a_plan_id = rowSet.getString("plan_id");

				// 获得绩效参数
				LoadXml parameter_content = null;
				if (BatchGradeBo.getPlanLoadXmlMap().get(a_plan_id) == null) {
					parameter_content = new LoadXml(this.conn, a_plan_id + "");
					BatchGradeBo.getPlanLoadXmlMap().put(a_plan_id + "", parameter_content);
				} else {
					parameter_content = (LoadXml) BatchGradeBo.getPlanLoadXmlMap().get(a_plan_id + "");
				}
				Hashtable params = parameter_content.getDegreeWhole();

				String sql0 = "select count(p0400) from P04 where plan_id=" + a_plan_id + "  and ";
				if ("1".equals(object_type) || "3".equals(object_type) || "3".equals(object_type)) // 团队
				{
					sql0 += " b0110='" + object_id + "'";
				} else {
					sql0 += " a0100='" + object_id + "'";
				}
				rowSet2 = dao.search(sql0);
				if (rowSet2.next() && rowSet2.getInt(1) > 0) {
					list.add(a_plan_id);
					break;
				}
    	    	
    	    }
    	} catch (Exception e)
    	{
    	    e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	} finally {
    		PubFunc.closeDbObj(rowSet);
    		PubFunc.closeDbObj(rowSet1);
    		PubFunc.closeDbObj(rowSet2);
    	}
    	return list;}
    /**先看操作单位 再看管理范围 再看用户所在单位部门
     * 该方法没有考虑高级授权
     * 目前绩效模块暂时不考虑高级授权 所以还是用这个方法来控制
     * 该方法适用所有考核对象类型的计划包括人员和团队的计划
     * 
     * 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
     * */
    public String getPrivReverseWhere(UserView userView)
    {
		StringBuffer buf = new StringBuffer();
//		if (!userView.isSuper_admin())
		{
			String operOrg = userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			if(operOrg!=null && operOrg.length() > 3)
			{
				StringBuffer tempSql = new StringBuffer("");
				String[] temp = operOrg.split("`");
				for (int i = 0; i < temp.length; i++) 
				{	
					if ("UN".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        tempSql.append(" or po.b0110 like '" + temp[i].substring(2)
                                + "%'");
                    } else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        tempSql.append(" or po.e0122 like '" + temp[i].substring(2)
                                + "%'");
                    }
				}
				buf.append(" and ( " + tempSql.substring(3) + " ) ");
			}
			else if((!userView.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
			{			
				//按管理范围
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
//			if(buf.length()==0)//没有设置任何权限
//				buf.append(" and 1=2 ");
		}
		return buf.toString();
    }
    
    /**
	 * 根据条件标识创建临时表
	 * 
	 * @param factorSet
	 * @return list:临时表列表项集合
	 */
	public ArrayList creatTempTable(String username,ArrayList factorList,int object_type)throws GeneralException
	{		
		
		String info="0";                      // 是否有指标没有构库
		ArrayList lists=new ArrayList();
		DBMetaModel dbmodel=null;
		try
		{
			DbWizard dbWizard=new DbWizard(this.conn);
			Table table=null;	
/*					  
			table=new Table("t#"+username+"per_mainbody");
			table.setCreatekey(false);
			table.addField(getField1("A0100","hmuster.label.machineNo","DataType.STRING",8));		
			table.addField(getField1("B0110","hmuster.label.unitNo","DataType.STRING",30));
			table.addField(getField1("E0122","hmuster.label.departmentNo","DataType.STRING",30));
			table.addField(getField1("E01A1","e01a1.label","DataType.STRING",30));
			table.addField(getField1("khB0110","hmuster.label.unitNo","DataType.STRING",30));
			table.addField(getField1("khtopE0122","hmuster.label.departmentNo","DataType.STRING",30));
			table.addField(getField1("khE0122","hmuster.label.departmentNo","DataType.STRING",30));
										
			Field temp21=new Field("I9999",ResourceFactory.getProperty("hmuster.label.no"));
			temp21.setDatatype(DataType.INT);
			temp21.setKeyable(false);	
			temp21.setNullable(true);
			temp21.setVisible(false);			
			table.addField(temp21);
					
			if(dbWizard.isExistTable(table.getName(),false))
			{						
				dbWizard.dropTable(table);				
			}
			dbWizard.createTable(table);	
			if(dbmodel==null)
				dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(table.getName());
			//if(factorList.size()==0)
*/					
			HashSet aSet=(HashSet)factorList.get(0);
			HashSet bSet=(HashSet)factorList.get(1);
			table=new Table("t#"+username+"per_mainbody");
			table.setCreatekey(false);
			ArrayList fieldlist=getTableFields(aSet);							
				
			for(int b=0;b<fieldlist.size();b++)
			{
				table.addField((Field)fieldlist.get(b));						
			}					
			if(dbWizard.isExistTable(table.getName(),false))
			{						
				dbWizard.dropTable(table);				
			}
			dbWizard.createTable(table);	
			if(dbmodel==null) {
                dbmodel=new DBMetaModel(this.conn);
            }
			dbmodel.reloadTableModel(table.getName());
				
			if(object_type!=2)
			{
/*
				table=new Table("t#"+username+"_unit_per_mainbody");
				table.setCreatekey(false);
				table.addField(getField1("B0110","hmuster.label.machineNo","DataType.STRING",8));		
				table.addField(getField1("khB0110","hmuster.label.unitNo","DataType.STRING",30));
				table.addField(getField1("khtopE0122","hmuster.label.departmentNo","DataType.STRING",30));
				table.addField(getField1("khE0122","hmuster.label.departmentNo","DataType.STRING",30));
				if(dbWizard.isExistTable(table.getName(),false))
				{						
					dbWizard.dropTable(table);				
				}
				dbWizard.createTable(table);	
				if(dbmodel==null)
					dbmodel=new DBMetaModel(this.conn);
				dbmodel.reloadTableModel(table.getName());
*/							
				table=new Table("t#"+username+"_unit_per_mainbody");
				table.setCreatekey(false);
				fieldlist=getTableFields(bSet);
				for(int b=0;b<fieldlist.size();b++)
				{
					table.addField((Field)fieldlist.get(b));						
				}							
				if(dbWizard.isExistTable(table.getName(),false))
				{						
					dbWizard.dropTable(table);				
				}
				dbWizard.createTable(table);	
				if(dbmodel==null) {
                    dbmodel=new DBMetaModel(this.conn);
                }
				dbmodel.reloadTableModel(table.getName());
							
				aSet.add("A0100");
				aSet.add("B0110");
				aSet.add("E0122");
				aSet.add("E01A1");
				bSet.add("B0110");
				bSet.add("khB0110");
				bSet.add("khtopE0122");
				bSet.add("khE0122");
			}else
			{
				aSet.add("A0100");
				aSet.add("B0110");
				aSet.add("E0122");
				aSet.add("E01A1");
				aSet.add("khB0110");
				aSet.add("khtopE0122");
				aSet.add("khE0122");
			}
		
		return lists;		
		}catch(Exception e)
		{		
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	public Field getField1(String name,String describe,String datatype,int length )
	{
		String a_describe=describe;
		if(describe.indexOf(".")!=-1) {
            a_describe=ResourceFactory.getProperty(describe);
        }
		Field temp=new Field(name,a_describe);
		temp.setDatatype(datatype);
		temp.setKeyable(false);			
		temp.setVisible(false);
		if(length!=0) {
            temp.setLength(length);
        }
		return temp;
	}
		/**
		 * 得到临时表中列的集合
		 * 
		 * @param aSet
		 * @param aVarSet
		 * @param flag
		 * @return
		 */
		public ArrayList getTableFields(HashSet aSet)
		{
			ArrayList fieldsList=new ArrayList();	
			HashSet fieldNameList=new HashSet();
			
				fieldsList.add(getField1("A0100","hmuster.label.machineNo","DataType.STRING",8));
				fieldNameList.add("A0100");			
				fieldsList.add(getField1("B0110","hmuster.label.unitNo","DataType.STRING",30));
				fieldNameList.add("B0110");
				fieldsList.add(getField1("E0122","hmuster.label.departmentNo","DataType.STRING",30));
				fieldNameList.add("E0122");
				fieldsList.add(getField1("E01A1","e01a1.label","DataType.STRING",30));
				fieldNameList.add("E01A1");		
				fieldsList.add(getField1("khB0110","hmuster.label.unitNo","DataType.STRING",30));
				fieldNameList.add("khB0110");
				fieldsList.add(getField1("khtopE0122","hmuster.label.departmentNo","DataType.STRING",30));
				fieldNameList.add("khtopE0122");
				fieldsList.add(getField1("khE0122","hmuster.label.departmentNo","DataType.STRING",30));
				fieldNameList.add("khE0122");
				
			Field temp21=new Field("I9999",ResourceFactory.getProperty("hmuster.label.no"));
			temp21.setDatatype(DataType.INT);
			temp21.setKeyable(false);	
		//	temp21.setNullable(true);
			temp21.setVisible(false);			
			fieldsList.add(temp21);
			fieldNameList.add("I9999");
			
			
		    for(Iterator t=aSet.iterator();t.hasNext();)
		    {
		    	String fieldname=((String)t.next()).trim();
		    	/* 判断该指标是否已被删除或还没构库 */
		    	FieldItem item=DataDictionary.getFieldItem(fieldname);	
				if(item==null)
				{
					continue;
				}
				if("0".equals(item.getUseflag()))
				{
					continue;
				}

		    	String fieldSet=item.getFieldsetid();
		    	Field obj=getField2(fieldname,item.getItemdesc(),item.getItemtype());
		    	if(!"A0100".equalsIgnoreCase(fieldname)&&!"B0110".equalsIgnoreCase(fieldname)&&!"E0122".equalsIgnoreCase(fieldname)&&!"E01A1".equalsIgnoreCase(fieldname))
		    	{
		    		fieldsList.add(obj);
		    		fieldNameList.add(fieldname);
		    	}
				
			}
		   
	    
			return fieldsList;			
		}
		/**
		 * 取得条件指标
		 * @param planid
		 * @param mainBodylist
		 * @param object_type
		 * @return
		 * @throws GeneralException
		 */
		 
		public ArrayList getFactor(String planid,ArrayList mainBodylist,int object_type)throws GeneralException
		{
			ArrayList list=new ArrayList();	
			HashSet aSet=new HashSet();   		 // 条件中包含的指标除中括号里的
			HashSet bSet=new HashSet();   		 // 高级条件中括号里的指标
			try
			{		
				
				for(int i=0;i<mainBodylist.size();i++)
				{
					RecordVo vo=(RecordVo)mainBodylist.get(i);
					String body_id = vo.getString("body_id");
					String cond =vo.getString("cond");
					String cexpr =vo.getString("cexpr");
					if(cexpr!=null&&cexpr.trim().length()>0){	//简单条件						
						addFactor(aSet,cond);
					}else{//高级带修改
						addFactor2(aSet,bSet,cond,userView,object_type);
					}
					
			}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
			list.add(aSet);
			list.add(bSet);

			return list;
		}
		
		
	
		/**
		 * 简单条件
		 * @param rowSet
		 * @param temp
		 */
		
		public void addFactor(HashSet rowSet,String temp)
		{
			if(temp!=null&&temp.length()>0)
			{
				String[] tempArr=temp.split("`");
				for(int ii=0;ii<tempArr.length;ii++)
				{
				
					int a=0;
					String subTemp=tempArr[ii];
					if(subTemp.indexOf("=")!=-1&&!"<".equals(subTemp.substring(subTemp.indexOf("=")-1,subTemp.indexOf("=")))&&!">".equals(subTemp.substring(subTemp.indexOf("=")-1,subTemp.indexOf("=")))) {
                        a=subTemp.indexOf("=");
                    } else if(subTemp.indexOf(">")!=-1&&!"<".equals(subTemp.substring(subTemp.indexOf(">")-1,subTemp.indexOf(">")))&&!"=".equals(subTemp.substring(subTemp.indexOf(">")+1,subTemp.indexOf(">")+2))) {
                        a=subTemp.indexOf(">");
                    } else if(subTemp.indexOf("<")!=-1&&!"=".equals(subTemp.substring(subTemp.indexOf("<")+1,subTemp.indexOf("<")+2))&&!">".equals(subTemp.substring(subTemp.indexOf("<")+1,subTemp.indexOf("<")+2))) {
                        a=subTemp.indexOf("<");
                    } else if(subTemp.indexOf(">=")!=-1) {
                        a=subTemp.indexOf(">=");
                    } else if(subTemp.indexOf("<=")!=-1) {
                        a=subTemp.indexOf("<=");
                    } else if(subTemp.indexOf("<>")!=-1) {
                        a=subTemp.indexOf("<>");
                    }
					if(a!=0)
					{
							rowSet.add(subTemp.substring(0,a).toUpperCase());
					}
				}
				
			}
		}
		/**
		 * 高级条件
		 * @param rowSet
		 * @param bSet
		 * @param temp
		 * @param userView
		 * @param object_type
		 * @throws GeneralException 
		 */
		public void addFactor2(HashSet rowSet,HashSet bSet,String temp,UserView userView,int object_type) throws GeneralException
		{
			try {
				ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
				YksjParser yp = new YksjParser(userView, alUsedFields, YksjParser.forSearch, 8,0, "Ht", "");
				yp.setCon(conn);
				boolean b=yp.Verify_where(temp.trim());
				HashMap map=yp.getMapUsedFieldItems();  //获得设计到的所有指标			
				HashMap map2=yp.getBracketsFieldMap();  //获得中括号里的指标 {[性别]:FieldItem}
				String keystr =",";	
				Set keySet=map2.keySet();
	 		    for(Iterator t=keySet.iterator();t.hasNext();)
	 		    {
	 		    	String key=(String)t.next();
	 		    	//FieldItem item=DataDictionary.getFieldItem(temp.toLowerCase());
	 		    	FieldItem item =(FieldItem)map2.get(key);
	 		    	if(item!=null){
	 		    	bSet.add(item.getItemid().toUpperCase());
	 		    	keystr+= item.getItemid()+",";
	 		    	}
	 		    }
			   keySet=map.keySet();
	 		    for(Iterator t=keySet.iterator();t.hasNext();)
	 		    {
	 		    	String key=(String)t.next();
	 		    	if(object_type!=2&&keystr.indexOf(key)!=-1){
	 		    		continue;
	 		    	}
	 		    	rowSet.add(key.toUpperCase());
	 		    }
			}catch(Exception e)
			{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
		 		   
//			yp.setBracketsFieldValueMap(HashMap bracketsFieldValueMap)  //赛中括号里指标的值   {[性别]: 1}    日期型格式为：2009.01.01
//			b=yp.Verify_where(temp.trim());
//			if (b) {// 校验不通过
//				flag="ok";
//			}else{
//				flag = yp.getStrError();
//			} 
//			yp.getSQL();	

		}
		public Field getField2(String fieldname,String desc,String type)
		{
			Field obj=new Field(fieldname,desc);	
			if("A".equals(type))
			{	
				obj.setDatatype(DataType.STRING);
				obj.setKeyable(false);			
				obj.setVisible(false);
				obj.setLength(255);
				obj.setAlign("left");
			}
			else if("M".equals(type))
			{
				obj.setDatatype(DataType.CLOB);
				obj.setKeyable(false);			
				obj.setVisible(false);
				obj.setAlign("left");					
			}
			else if("D".equals(type))
			{
				
				obj.setDatatype(DataType.DATE);
				obj.setKeyable(false);			
				obj.setVisible(false);				
				obj.setAlign("right");						
			}	
			else if("N".equals(type))
			{
				obj.setDatatype(DataType.FLOAT);
				obj.setDecimalDigits(6);
				obj.setLength(15);							
				obj.setKeyable(false);			
				obj.setVisible(false);							
				obj.setAlign("left");
				
			}	
			else if("I".equals(type))
			{		
				obj.setDatatype(DataType.INT);
				obj.setKeyable(false);			
				obj.setVisible(false);	

			}		
			return obj;
		}
		/**
		 * 往临时表插入数据
		 * @param username
		 * @param factorList
		 * @param flags
		 * @param perObjectDataList
		 * @param organizationmap
		 * @param compmap
		 * @return
		 */
		public boolean insertTempDate(String username,ArrayList factorList,int flags,ArrayList perObjectDataList,HashMap organizationmap,HashMap compmap,String plan_id)
		{
			boolean flag=true;
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet recset=null;
			HashSet a_fieldNameList = (HashSet)factorList.get(0);
			try
			{
				String tableName="";
				String indexColumn="";
//				if(flags==1)
//				{
					tableName="t#"+username+"per_mainbody";
					indexColumn="A0100";
//				}
				
				StringBuffer sql=new StringBuffer("insert into "+tableName+" (");
				if(a_fieldNameList.size()>0)
				{
					HashMap fieldSetMap=getFieldSetMap(a_fieldNameList);	
					//得到与列相对应表信息的hashmap				
					StringBuffer sql1=new StringBuffer("");
					for(Iterator t1=a_fieldNameList.iterator();t1.hasNext();)
					{
						String temp=(String)t1.next();	
						if("khB0110".equals(temp)|| "khtopE0122".equals(temp)|| "khE0122".equals(temp)) {
                            continue;
                        }
						if("A0100".equals(temp)|| "B0110".equals(temp)|| "E0122".equals(temp)|| "E01A1".equals(temp)|| "I9999".equals(temp)){
							
						}
						else{
						if(fieldSetMap.get(temp.toLowerCase())==null) {
                            continue;
                        }
						}
						sql1.append(",");
						sql1.append(temp);
										
					}			
					sql.append(sql1.substring(1));
					sql.append(" ) ");
					sql.append(getATempSQL(username,a_fieldNameList,fieldSetMap));			
					
				}
				//System.out.println(sql.toString());
				//dao.delete("delete from "+tableName, new ArrayList());
				dao.insert(sql.toString(),new ArrayList());	
				//更新主体范围
				String body_id="";
				String b0110="";
				String e0122="";
				String tope0122="";
				ArrayList list = new ArrayList();
				if(flags==2){
				for(int i=0;i<perObjectDataList.size();i++){
					ArrayList listtemp = new ArrayList();
					
					LazyDynaBean abean = (LazyDynaBean)perObjectDataList.get(i);
					 body_id = (String)abean.get("object_id");
					 b0110 = 	(String)abean.get("b0110value");
					 e0122 = (String)abean.get("e0122value");
					
					 listtemp.add(b0110);
					 listtemp.add(e0122);
//					 if(e0122.length()>b0110.length()){
//						 String temp = e0122.substring(b0110.length());
//						 for(int j=0;j<temp.length();j++){
//							 if(organizationmap!=null&&organizationmap.get(b0110+temp.substring(0,j+1))!=null){
//								 tope0122 = b0110+temp.substring(0,j+1);
//								 break;
//							 }
//						 }
//						  
//					 }else{
//						 tope0122= e0122;
//					 }
					 listtemp.add(tope0122);
					 listtemp.add(body_id);
					 list.add(listtemp);
					 
				}
				dao.batchUpdate(" update "+tableName+" set khB0110=?,khE0122=?,khtopE0122=? where a0100=? ", list);
				}
				
				
				dao.update("create index "+tableName+"_index on "+tableName+" ("+indexColumn+")");
				//考核对象是组织插入临时表信息
				if(flags!=2){
					tableName="t#"+username+"_unit_per_mainbody";
					indexColumn="A0100";
					a_fieldNameList = (HashSet)factorList.get(1);
				 sql=new StringBuffer("insert into "+tableName+" (");
				if(a_fieldNameList.size()>0)
				{
					HashMap fieldSetMap=getFieldSetMap(a_fieldNameList);	
					StringBuffer sql1=new StringBuffer("");
					for(Iterator t1=a_fieldNameList.iterator();t1.hasNext();)
					{
						String temp=(String)t1.next();	
						if("khB0110".equals(temp)|| "khtopE0122".equals(temp)|| "khE0122".equals(temp)) {
                            continue;
                        }
						if("B0110".equals(temp)|| "I9999".equals(temp)){
							
						}
						else{
						if(fieldSetMap.get(temp.toLowerCase())==null|| "E0122".equalsIgnoreCase(temp)) {
                            continue;
                        }
						String fieldSet =(String)fieldSetMap.get(temp.toLowerCase());
						if(fieldSet.charAt(0)=='A'||fieldSet.charAt(0)=='K') {
                            continue;
                        }
						}
						sql1.append(",");
						sql1.append(temp);
										
					}			
					sql.append(sql1.substring(1));
					sql.append(" ) ");
					sql.append(getBTempSQL(username,a_fieldNameList,fieldSetMap));			
					
					dao.insert(sql.toString(),new ArrayList());
					dao.batchUpdate(" update "+tableName+" set khB0110=?,khE0122=?,khtopE0122=? where b0110=? ", list);
				}
				//System.out.println(sql.toString());
				//dao.delete("delete from "+tableName, new ArrayList());
				
				
				}
				sql = new StringBuffer();
				if(flags!=2){
					tableName="t#"+username+"_unit_per_mainbody";
					a_fieldNameList = (HashSet)factorList.get(1);
					if(a_fieldNameList.size()>0)
					{
						HashMap fieldSetMap=getFieldSetMap(a_fieldNameList);	
						StringBuffer sql1=new StringBuffer("");
						sql1.append(",");
						sql1.append("b0110");
						for(Iterator t1=a_fieldNameList.iterator();t1.hasNext();)
						{
							String temp=(String)t1.next();	
							if("B0110".equals(temp)|| "I9999".equals(temp)){
								
							}
							else{
							if(fieldSetMap.get(temp.toLowerCase())==null|| "E0122".equalsIgnoreCase(temp)) {
                                continue;
                            }
							String fieldSet =(String)fieldSetMap.get(temp.toLowerCase());
							if(fieldSet.charAt(0)=='A'||fieldSet.charAt(0)=='K') {
                                continue;
                            }
							}
							sql1.append(",");
							sql1.append(temp);
											
						}			
						sql.append(sql1.substring(1));
						recset  = dao.search(" select "+sql+" from "+tableName+" ");
						
					}
				}else{
					tableName="t#"+username+"per_mainbody";
					a_fieldNameList = (HashSet)factorList.get(0);
					if(a_fieldNameList.size()>0)
					{
						HashMap fieldSetMap=getFieldSetMap(a_fieldNameList);	
						StringBuffer sql1=new StringBuffer("");
						sql1.append(",");
						sql1.append("a0100");
						sql1.append(",");
						sql1.append("khB0110");
						sql1.append(",");
						sql1.append("khtopE0122");
						sql1.append(",");
						sql1.append("khE0122");
						for(Iterator t1=a_fieldNameList.iterator();t1.hasNext();)
						{
							String temp=(String)t1.next();	
							if("B0110".equals(temp)|| "I9999".equals(temp)){
								
							}
							else{
							if(fieldSetMap.get(temp.toLowerCase())==null) {
                                continue;
                            }
							}
							sql1.append(",");
							sql1.append(temp);
											
						}			
						sql.append(sql1.substring(1));
						recset  = dao.search(" select "+sql+" from "+tableName+" where exists (select null from per_object where plan_id="+plan_id+" and "+tableName+".A0100=per_object.object_id)");
				}
				}
					String columns[] = sql.toString().split(",");
					if(recset!=null){
					while(recset.next()){
						for(int i =0;i<columns.length;i++){
							if(flags!=2){
								compmap.put(recset.getString("b0110")+columns[i], recset.getString(columns[i]));
							}else{
								compmap.put(recset.getString("a0100")+columns[i], recset.getString(columns[i]));
							}
							
						}
					}
					}
			}
			catch(Exception e)
			{
				flag=false;
				e.printStackTrace();
			}
		
			return flag;
		}
		/**
		 * 得到与列相对应表信息的hashmap
		 * 
		 * @param fieldNameList
		 *            列集合
		 * @return
		 */
		public HashMap getFieldSetMap(HashSet fieldNameList)throws GeneralException
		{
			HashMap map=new HashMap();
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet recset=null;
			try
			{
				StringBuffer sql=new StringBuffer("select itemid,fieldsetid from fielditem where itemid in(");
				StringBuffer sql_temp=new StringBuffer("");
				for(Iterator t=fieldNameList.iterator();t.hasNext();)
				{
					String temp=(String)t.next();
					sql_temp.append(",'");
					sql_temp.append(temp);
					sql_temp.append("'");			
				}		
				sql.append(sql_temp.substring(1));
				sql.append(")");
				recset=dao.search(sql.toString());
				while(recset.next())
				{
					map.put(recset.getString(1).toLowerCase(),recset.getString(2));		
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
		/*
		 * finally { try { if(recset!=null) recset.close();
		 *  } catch(Exception e) { e.printStackTrace(); } }
		 */
			return map;
		}
		
		/**
		 * 产生扫描人员库临时表数据的sql语句
		 * @param userName
		 * @param a_fieldNameList
		 * @param fieldSetMap
		 * @return
		 */
		public String getATempSQL(String userName,HashSet a_fieldNameList,HashMap fieldSetMap)
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet recset=null;
			StringBuffer sql=new StringBuffer("");
			try
			{
					HashSet set=new HashSet();                                      //涉及到的表名集合
					String pre="usr";
					StringBuffer sql_sub=new StringBuffer(" select ");
					//产生select 前缀子句
					StringBuffer sql_sub_str=new StringBuffer("");				
					for(Iterator t1=a_fieldNameList.iterator();t1.hasNext();)
					{
						String temp=(String)t1.next();
						String fieldSet="";
						if("khB0110".equals(temp)|| "khtopE0122".equals(temp)|| "khE0122".equals(temp)) {
                            continue;
                        }
						if("A0100".equals(temp)|| "B0110".equals(temp)|| "E0122".equals(temp)|| "E01A1".equals(temp)|| "I9999".equals(temp)) {
                            fieldSet="A01";
                        } else
						{
						if(fieldSetMap.get(temp.toLowerCase())==null) {
                            continue;
                        }
							fieldSet=(String)fieldSetMap.get(temp.toLowerCase());				//表名							
						}
						if("NBASE".equals(temp))
						{
							sql_sub_str.append(",'");
							sql_sub_str.append(pre+"'");
						}
						else if("I9999".equals(temp))
						{
							sql_sub_str.append(",1");
						}
						else 
						{
							sql_sub_str.append(",");
							
							StringBuffer temp_sub=new StringBuffer("");
							if(fieldSet.charAt(0)=='A') {
                                temp_sub.append(pre+fieldSet);
                            } else {
                                temp_sub.append(fieldSet);
                            }
							temp_sub.append(".");
							temp_sub.append(temp);
							FieldItem item=DataDictionary.getFieldItem(temp.toLowerCase());
							if(item!=null&& "N".equalsIgnoreCase(item.getItemtype()))
							{
								sql_sub_str.append(Sql_switcher.isnull(temp_sub.toString(),"0"));
							}
							else {
                                sql_sub_str.append(temp_sub.toString());
                            }
							
						}	
						set.add(fieldSet);
					}		
					
					sql_sub.append(sql_sub_str.substring(1));
					//产生from where 子句
					sql_sub.append(" from "+pre+"A01");
					for(Iterator t1=set.iterator();t1.hasNext();)
					{	
						String tempTable=(String)t1.next();				
						if(!"A01".equals(tempTable))
						{					
							if(tempTable.charAt(2)=='1'&&tempTable.charAt(1)=='0')
							{
								if(tempTable.charAt(0)=='A') {
                                    sql_sub.append(" left join   "+pre+tempTable+" on "+pre+"A01.A0100="+pre+tempTable+".A0100 ");
                                } else if(tempTable.charAt(0)=='B') {
                                    sql_sub.append(" left join   "+tempTable+" on "+pre+"A01.B0110="+tempTable+".B0110 ");
                                } else if(tempTable.charAt(0)=='K') {
                                    sql_sub.append(" left join "+tempTable+" on "+pre+"A01.E01A1="+tempTable+".E01A1 ");
                                }
							}
							else
							{
								if(tempTable.charAt(0)=='A')
								{
									sql_sub.append(" left join   ( select a.* from "+pre+tempTable+"  a where a.I9999=(select max( b.I9999 ) from "+pre+tempTable+" b where a.a0100=b.a0100)) "+pre+tempTable+" on "+pre+"A01.A0100="+pre+tempTable+".A0100 ");							
								}	
								else if(tempTable.charAt(0)=='B')
								{
									sql_sub.append(" left join  ( select a.* from "+tempTable+"  a where a.I9999=(select max( b.I9999 ) from "+tempTable+" b where a.B0110=b.B0110)) "+tempTable+" on "+pre+"A01.B0110="+tempTable+".B0110 ");								
								}
								else if(tempTable.charAt(0)=='K')
								{
									sql_sub.append(" left join  ( select a.* from "+tempTable+"  a where a.I9999=(select max( b.I9999 ) from "+tempTable+" b where a.E01A1=b.E01A1))  "+tempTable+" on "+pre+"A01.E01A1="+tempTable+".E01A1 ");						
								}
							}
						}
					}
					sql_sub.append(" where 1=1 ");
					
					sql.append(" union "+sql_sub.toString());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			return sql.substring(6);
		}
		/**
		 * 产生扫描单位库临时表数据的sql语句
		 * @param userName
		 * @param a_fieldNameList
		 * @param fieldSetMap
		 * @return
		 */
		public String getBTempSQL(String userName,HashSet a_fieldNameList,HashMap fieldSetMap)
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet recset=null;
			StringBuffer sql_sub=new StringBuffer(" select ");
			try
			{
		
					HashSet set=new HashSet();                                      //涉及到的表名集合
					//产生select 前缀子句
					StringBuffer sql_sub_str=new StringBuffer("");				
					for(Iterator t1=a_fieldNameList.iterator();t1.hasNext();)
					{
						String temp=(String)t1.next();
						String fieldSet="";
						if("khB0110".equals(temp)|| "khtopE0122".equals(temp)|| "khE0122".equals(temp)|| "E0122".equalsIgnoreCase(temp)) {
                            continue;
                        }
						if("B0110".equals(temp)|| "I9999".equals(temp)) {
                            fieldSet="B01";
                        } else{
							if(fieldSetMap.get(temp.toLowerCase())==null) {
                                continue;
                            }
							
							fieldSet=(String)fieldSetMap.get(temp.toLowerCase());	
							if(fieldSet.charAt(0)=='A'||fieldSet.charAt(0)=='K') {
                                continue;
                            }
						}
						
						if("I9999".equals(temp))
						{
							sql_sub_str.append(",1");
						}
						else 
						{
							sql_sub_str.append(","+fieldSet);
							sql_sub_str.append(".");
							sql_sub_str.append(temp);
						}	
						//表名					
						set.add(fieldSet);
					}			
					sql_sub.append(sql_sub_str.substring(1));
					//产生from where 子句
					sql_sub.append(" from "+"B01");
					for(Iterator t1=set.iterator();t1.hasNext();)
					{	
						String tempTable=(String)t1.next();				
						if(!"B01".equals(tempTable))
						{					
							sql_sub.append(" left join  ( select a.* from "+tempTable+"  a where a.I9999=(select max( b.I9999 ) from "+tempTable+" b where a.B0110=b.B0110)) "+tempTable+" on B01.B0110="+tempTable+".B0110 ");															
						}
					}	
					sql_sub.append(" where 1=1 ");
					
					
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return sql_sub.toString();
		}
	    public void aotoDelKhMainBody(String planID)
	    {

		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			// 删除考核主体表
			String strSql = "";
			strSql ="delete from per_mainbody  where plan_id="+planID+" and body_id<>5 ";
			dao.delete(strSql, new ArrayList());
			// 删除权限表
			StringBuffer strSql2 = new StringBuffer();
			strSql2.append("delete from per_pointpriv_");
			strSql2.append(planID);
			strSql2.append(" where mainbody_id<>object_id");
		    dao.delete(strSql2.toString(), new ArrayList());
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
	    }
	    
	    /**	
		 * 新建临时表并进行操作
		 */
		public void creatTempTable(String plan_id)
		{
			RowSet rowSet=null;
			try
			{
				ContentDAO dao = new ContentDAO(this.conn);
				
				//  新建临时表 
				String tablename = "t#_per_Email";			
				DbWizard dbWizard = new DbWizard(this.conn);	
				// 此临时表若存在就先drop掉
				if(dbWizard.isExistTable(tablename,false))
				{
					dbWizard.dropTable(tablename);				
				}						
				
				Table table = new Table(tablename);
				table.addField(getField("plan_id", "I", 4, false));
			    table.addField(getField("B0110", "A", 100, false));
			    table.addField(getField("E0122", "A", 100, false));
			    table.addField(getField("object_id", "A", 100, false));
//			    table.addField(getField("mainbody_id", "A", 100, false));
			    table.addField(getField("status", "I", 8, false));
			    table.addField(getField("sp_flag", "A", 2, false));
			    dbWizard.createTable(table);
										
				// 向临时表中写入记录
				String insertStr = "insert into " + tablename + " (b0110,e0122,object_id,sp_flag,plan_id)"
		 						 +" select b0110,e0122,object_id,sp_flag,'" + plan_id + "' " 
		 						 +" from per_object where plan_id='" + plan_id + "' "
		 						 +" group by object_id,b0110,e0122,sp_flag";
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
					if("1".equalsIgnoreCase(num)) {
                        list.add(status);
                    } else {
                        list.add("1");
                    }
					
					list.add(object_id);             				
					beList.add(list);				
				}
				if(beList.size()>0)
				{
					// 批量更新临时表中的status
					String updSql = "update " + tablename + " set status=? where object_id = ? ";			  			  
					dao.batchUpdate(updSql, beList);		
				}
				
				// 把临时表中object_id的status为null的全部修改为:0(未评分)
				String updateSql = "update " + tablename + " set status=0 where status is null ";
				dao.update(updateSql);	
																	
				if(rowSet!=null) {
                    rowSet.close();
                }
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
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
			    obj.setDecimalDigits(3);
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
	  * 新建根据计算公式计算出总体评价的临时表并进行操作
	  */
	public void creatTempWholeEvalTable(String plan_id)
	{
		RowSet rowSet=null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
				
			//  新建临时表 
			String tablename = "t#_per_app_"+plan_id;			
			DbWizard dbWizard = new DbWizard(this.conn);	
			// 此临时表若存在就先drop掉
			if(dbWizard.isExistTable(tablename,false))
			{
				dbWizard.dropTable(tablename);				
			}
		//	if(dbWizard.isExistTable(tablename, false))
  		//	  	dao.update("drop table "+tablename);
			
			// 获得当前考核计划关联考核模板的所有指标
			ArrayList pointList = getPointidList(plan_id);	
			
			Table table = new Table(tablename);				
			table.addField(getField("object_id", "A", 30, true));
			table.addField(getField("mainbody_id", "A", 30, true));
			
			for (int i = 0; i < pointList.size(); i++)
		    {		    	
				String point_id = (String) pointList.get(i);
				table.addField(getField(point_id, "N", 9, false));
		    }
			    
			table.addField(getField("totalscore", "N", 9, false));
			table.addField(getField("pointnumber", "I", 8, false));
			table.addField(getField("gradedesc", "A", 50, false));
			dbWizard.createTable(table);
										
			// 向临时表中写入记录
			String insertStr = "insert into " + tablename + " (object_id,mainbody_id,pointnumber)"
		 					 +" select object_id,mainbody_id," + pointList.size() + " " 
		 					 +" from per_mainbody where plan_id='" + plan_id + "' ";
			dao.insert(insertStr, new ArrayList());
			
			// 添加索引
		//	dao.update("create index " + tablename+"_idx on " + tablename+" (object_id,mainbody_id)");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
		
	/**
     * 获得当前考核计划关联考核模板的所有指标
     * @return
     */
    public ArrayList getPointidList(String plan_id)
    {

    	ArrayList pointList = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{		    
			String sql = "select e.point_id from per_template a,per_plan b ,per_template_item c,per_template_point d ,per_point e"
				       + " where a.template_id=b.template_id and a.template_id=c.template_id and c.item_id=d.item_id and d.point_id=e.point_id and b.plan_id=" + plan_id + " order by e.point_id ";
			rowSet = dao.search(sql);
			while (rowSet.next())
			{
				pointList.add(rowSet.getString(1).toUpperCase()); 
			}
	
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return pointList;
    }
    /**
	 * 获得当前计划的所有考核主体
	 * @param id
	 * @return
	 */
	public HashMap getMainbodyidMap(String plan_id)
	{
		HashMap mainbodyMap = new HashMap();  
		RowSet rowSet = null;		
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);	
									  
	        String str = "select pm.mainbody_id, pm.body_id from per_mainbody pm,per_plan_body ppb where pm.plan_id='" + plan_id + "' "	
	                     +" and pm.body_id=ppb.body_id " 
	                     + " and pm.plan_id=ppb.plan_id";
	        
	        rowSet = dao.search(str);
		    while (rowSet.next())	
		    {			    			    	
	    		mainbodyMap.put(rowSet.getString("mainbody_id"), "true");	    			
		    }			
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return mainbodyMap;
	}
    /**
	 * 如果只有自评，（包括团队负责人），即不需要给其它对象评分的标志
	 * @param id
	 * @return
	 */
	public HashMap getSelfMainbodyMap(String plan_id)
	{
		HashMap mainbodyMap = new HashMap();  
		RowSet rowSet = null;		
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);	
									  
	        String str = "select mainbody_id, body_id from per_mainbody where plan_id='" + plan_id + "' ";			        
	        rowSet = dao.search(str);
		    while (rowSet.next())	
		    {			    	
		    	String body_id = rowSet.getString("body_id");
	    		if(("5".equalsIgnoreCase(body_id)) || ("-1".equalsIgnoreCase(body_id)))
	    		{
	    			mainbodyMap.put(rowSet.getString("mainbody_id"), "true");
	    		}	
		    }			
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return mainbodyMap;
	}
	/**
	 * 同步推送待办表   zhaoxg add 2014-8-28
	 * @param conn
	 * @param userview
	 * @param receiver 接收者名
	 * @param plan_id
	 * @param flag    1:新增  2：修改  3:用于暂停(此时plan_id传待办号) 4：用于启动和分发 5:清除评价人代办(此时plan_id传待办号) 6:用于报批时添加评价人代办 100:清除代办使用，7 360考核计划下对其他人打分发送代办的标记
	 * @param bean   发放日期、次数   待办模块 名称 等信息
	 */
	synchronized static  public LazyDynaBean updatePendingTask(Connection conn,UserView userview,String receiver,String plan_id,LazyDynaBean bean,String flag) {
		LazyDynaBean _bean=new LazyDynaBean();
		if (receiver == null || receiver.length() ==0) {
            return _bean;
        }
		
		RowSet rs = null;
		try {
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
			String date = df.format(calendar.getTime());

			String sender = userview.getDbname()+userview.getA0100();
			if(sender!=null&&sender.length()<1){
				sender=userview.getUserName();
			}

			ContentDAO dao = new ContentDAO(conn);
			StringBuffer sql=new StringBuffer();
			String name=(String) bean.get("title");
			String ext_flag="";
			if("start".equals(bean.get("oper"))){//启动
				if("7".equals(flag)) {
					ext_flag="PERPF_"+plan_id+"_SELF";
				}else {
					ext_flag="PERPF_"+plan_id;
				}
			}else if("distribute".equals(bean.get("oper"))){//交办
				ext_flag="PERSP_"+plan_id;
			}else if("distribute1".equals(bean.get("oper"))){//分发,制定
				ext_flag="PERZD_"+plan_id;
				//分发产生的待办，待办表中ext_flag字段改为“PERZD_planId_object_id(加密)”。同时要兼容老的“PERZD_planId”的情况。chent 20170725 start
				Object _object_id = bean.get("object_id");
				if(_object_id != null){
					String object_id = (String)_object_id;
					ext_flag += ("_"+PubFunc.encrypt(object_id));
				}
				//分发产生的待办，待办表中ext_flag字段改为“PERZD_planId_object_id(加密)”。同时要兼容老的“PERZD_planId”的情况。chent 20170725 end
			}else if("grade".equals(bean.get("oper"))){//zhanghua 添加对多评分人代办的支持
				ext_flag="PERPJ_"+plan_id;
				Object _object_id = bean.get("object_id");
				if(_object_id != null){
					String object_id = (String)_object_id;
					ext_flag += ("_"+PubFunc.encrypt(object_id));
				}
			}
			HashMap map=isHavePendingtask(receiver,conn,ext_flag);
			String pending_type="33";
			if ((map==null||map.size()==0)&&("1".equals(flag)|| "4".equals(flag)|| "6".equals(flag) || "7".equals(flag))) {//在待办任务表中新增待办数据
				IDGenerator idg = new IDGenerator(2, conn);
				String url=(String) bean.get("url");
				String pending_id = idg.getId("pengdingTask.pengding_id");
				RecordVo vo = new RecordVo("t_hr_pendingtask");
				vo.setString("pending_id", pending_id);
				vo.setDate("create_time", date);
				vo.setDate("lasttime", date);
				vo.setString("sender", sender);
				vo.setString("pending_type", pending_type);
				vo.setString("pending_title",name);
				vo.setString("pending_url", url);
				vo.setString("pending_status", "0");
				vo.setString("pending_level", "1");
				vo.setInt("bread", 0);
				vo.setString("receiver", receiver);
				vo.setString("ext_flag", ext_flag);
				dao.addValueObject(vo);
				_bean.set("receiver", receiver);
				_bean.set("pending_id", pending_id.replaceAll("^(0+)", ""));//去掉前面的0，因为入库以后是int型的
				_bean.set("flag", "add");
				_bean.set("url", url);
			} else if((map!=null&&map.size()>0)&&("1".equals(flag)|| "6".equals(flag))) {
				String url = (String) bean.get("url");
				sql.setLength(0);
				if(!"0".equals(map.get("pending_status"))) {
					sql.append("update t_hr_pendingtask set Pending_status='1' ");
					if (url != null && url.length() > 0) {
						sql.append(" ,pending_url='" + url + "',");
					}
					if (name != null && name.length() > 0) {
						sql.append(" pending_title='" + name + "',");
					}
					sql.append(" sender='" + sender + "'");
					sql.append(" where Pending_type='" + pending_type + "'");
					sql.append(" and pending_id=" + map.get("pending_id") + "");
					dao.update(sql.toString());
//改成创建新的代办任务，清除旧任务，以满足第三方代办 zhanghua 2018-10-18
					IDGenerator idg = new IDGenerator(2, conn);
					String pending_id = idg.getId("pengdingTask.pengding_id");
					RecordVo vo = new RecordVo("t_hr_pendingtask");
					vo.setString("pending_id", pending_id);
					vo.setDate("create_time", date);
					vo.setDate("lasttime", date);
					vo.setString("sender", sender);
					vo.setString("pending_type", pending_type);
					vo.setString("pending_title",name);
					vo.setString("pending_url", url);
					vo.setString("pending_status", "0");
					vo.setString("pending_level", "1");
					vo.setInt("bread", 0);
					vo.setString("receiver", receiver);
					vo.setString("ext_flag", ext_flag);
					dao.addValueObject(vo);
					_bean.set("receiver", receiver);
					_bean.set("pending_id", pending_id.replaceAll("^(0+)", ""));//去掉前面的0，因为入库以后是int型的
					_bean.set("flag", "add");
					_bean.set("url", url);
				}else{
					_bean.set("pending_id", map.get("pending_id"));
					_bean.set("flag", "update");
				}


			}else if((map!=null&&map.size()>0)&& "100".equals(flag)){
				sql.delete(0, sql.length());
				sql.append("update t_hr_pendingtask set Pending_status='1',Lasttime="+Sql_switcher.dateValue(date)+"");
				sql.append(" where Pending_type='"+pending_type+"'");
				sql.append(" and Receiver='" + receiver + "'");
				sql.append(" and (Pending_status='0' or Pending_status='9') and pending_id="+map.get("pending_id")+"");
				dao.update(sql.toString());
				_bean.set("pending_id", map.get("pending_id"));
				_bean.set("flag", "update");
			}
			if("3".equals(flag)){
				dao.update("update t_hr_pendingtask set pending_status='9' where pending_id='"+plan_id+"' and pending_status<>'1'");
				_bean.set("selfpending_id", plan_id);
				_bean.set("selfflag", "update");
			}
			if("5".equals(flag)){
				dao.update("update t_hr_pendingtask set pending_status='1' where pending_id='"+plan_id+"' and pending_status<>'1'");
				_bean.set("selfpending_id", plan_id);
				_bean.set("selfflag", "update");
			}
			
			
			if((map!=null&&map.size()!=0)&&("4".equals(flag)||"7".equals(flag))){
				sql.delete(0, sql.length());
				String url=(String) bean.get("url");
				if(!"0".equals(map.get("pending_status"))) {
					sql.append("update t_hr_pendingtask set Pending_status='1',Lasttime=" + Sql_switcher.dateValue(date) + ",");
					if (url != null && url.length() > 0) {
						sql.append(" pending_url='" + url + "',");
					}
					if (name != null && name.length() > 0) {
						sql.append(" pending_title='" + name + "',");
					}
					sql.append(" sender='" + sender + "'");
					sql.append(" where Pending_type='" + pending_type + "'");
					sql.append(" and pending_id=" + map.get("pending_id") + "");
					dao.update(sql.toString());
//改成创建新的代办任务，清除旧任务，以满足第三方代办 zhanghua 2018-10-18
					IDGenerator idg = new IDGenerator(2, conn);
					String pending_id = idg.getId("pengdingTask.pengding_id");
					RecordVo vo = new RecordVo("t_hr_pendingtask");
					vo.setString("pending_id", pending_id);
					vo.setDate("create_time", date);
					vo.setDate("lasttime", date);
					vo.setString("sender", sender);
					vo.setString("pending_type", pending_type);
					vo.setString("pending_title",name);
					vo.setString("pending_url", url);
					vo.setString("pending_status", "0");
					vo.setString("pending_level", "1");
					vo.setInt("bread", 0);
					vo.setString("receiver", receiver);
					vo.setString("ext_flag", ext_flag);
					dao.addValueObject(vo);
					_bean.set("receiver", receiver);
					_bean.set("pending_id", pending_id.replaceAll("^(0+)", ""));//去掉前面的0，因为入库以后是int型的
					_bean.set("flag", "add");
					_bean.set("url", url);
				}else{
					_bean.set("pending_id", map.get("pending_id"));
					_bean.set("flag", "update");
				}
			}
			if("true".equals(bean.get("appeal"))){//报批的时候自己的待办是制定状态
				ext_flag="PERZD_"+plan_id;
				//分发产生的待办，待办表中ext_flag字段改为“PERZD_planId_object_id(加密)”。同时要兼容老的“PERZD_planId”的情况。chent 20170725 start
				Object _object_id = bean.get("object_id");
				if(_object_id != null){
					String object_id = (String)_object_id;
					ext_flag += ("_"+PubFunc.encrypt(object_id));
				}
				//分发产生的待办，待办表中ext_flag字段改为“PERZD_planId_object_id(加密)”。同时要兼容老的“PERZD_planId”的情况。chent 20170725 end
			}
			HashMap _map=isHavePendingtask(sender,conn,ext_flag);
			if(_map!=null&&"0".equals(_map.get("pending_status"))&&bean.get("sql")!=null&&bean.get("sql").toString().length()>0){//如果是驳回再报，那么自己肯定也有条对应待办   改成已办
				// 是否需要清除待办标识
				boolean needToUpdatePending = false;
				
				RowSet _rs=dao.search((String) bean.get("sql"));
				if(!_rs.next()){
					needToUpdatePending = true;
				}
				// 判断该主体对当前能打分的对象是否都已评完分 ，如果评完了也要清除待办 chent 20171222 add start
				ObjectCardBo objectCardBo = new ObjectCardBo(conn, userview, plan_id);
				if("start".equals(bean.get("oper")) && objectCardBo.exsitObjectNoScore(plan_id)) {
					needToUpdatePending = true;
				}
				// 判断该主体对当前能打分的对象是否都已评完分 ，如果评完了也要清除待办 chent 20171222 add end
				
				// 待办置删除，因为每次报批都是发送信的待办
				if(needToUpdatePending) {
					sql.delete(0, sql.length());
					sql.append("delete from t_hr_pendingtask");
					sql.append(" where Pending_type='"+pending_type+"'");
					sql.append(" and Receiver='" + sender + "'");
					sql.append(" and Pending_status='0' and pending_id="+_map.get("pending_id")+"");
					dao.update(sql.toString());
					_bean.set("selfpending_id", _map.get("pending_id"));
					_bean.set("selfflag", "update");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if (rs != null) {
                    rs.close();
                }
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return _bean;
	}
	/**
	 * 判断是否已有待办，且返回待办状态   zhaoxg add 2014-8-28
	 * @param receiver 接收者名
	 * @param conn
	 * @param ext_flag  扩展标记  用来区分已有待办
	 * @return
	 */
	public static HashMap isHavePendingtask(String receiver,Connection conn,String ext_flag){
		HashMap map=new HashMap();
		try{
			ContentDAO dao = new ContentDAO(conn);
			String sql="select pending_id,ext_flag,pending_status,bread,receiver from t_hr_pendingtask where Pending_type='33' and receiver='"+receiver+"' and ext_flag='"+ext_flag+"' order by pending_id desc";
			RowSet rs=dao.search(sql);
			//使用while循换往 map中put 数据是有问题的，拿到的始终是最后一条的数据。 haosl 2019年5月17日
		//while(rs.next()){
            if(rs.next()){
				map.put("pending_status", rs.getString("pending_status"));//待办处理状态
				map.put("bread", rs.getString("bread"));//是否阅读
				map.put("receiver", rs.getString("receiver"));//接收者账号
				map.put("pending_id", rs.getString("pending_id"));//主键
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
}
