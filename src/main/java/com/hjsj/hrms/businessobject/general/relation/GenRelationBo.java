package com.hjsj.hrms.businessobject.general.relation;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>Title:PerRelationBo.java</p>
 * <p>Description:考核关系</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-04-15 13:00:00</p>
 * @author JinChunhai
 * @version 1.0
 * 
 * <p>modify time:2014-10-11</p>
 * @modify zxj 审批关系显示优化：组织机构名称翻译放到jsp，只翻译当前需显示的数据，不再在读取全部数据时进行；重构部分代码。
 * @version 2.0
 */

public class GenRelationBo
{
    Connection conn = null;

    private UserView userview=null;
    
    public GenRelationBo(Connection cn)
    {

    	this.conn = cn;
    }

    public GenRelationBo(Connection cn,UserView _userview)
    {

		this.conn = cn;
		this.userview=_userview;
    }
    
    /** 获得审批对象数据
     *  2013-2-22  田野修改方法：添加一个参数： String selectAllFlag
     *  （添加selectAllFlag=1,标记用于判断超级用户组是否查询所有的数据 ）
     *  
     *  */
    public ArrayList getGenObjectDataList2(String a_code, String a0101, String pre, String relation_id, String codeset,
            String actor_type, String selectAllFlag) throws GeneralException {
        //查询人员库里的信息，关联审批对象库

        ArrayList list = new ArrayList();
        String code = "";
        code = a_code;
        String operate = "";// 取sql得到连接符
        //获得用户组map
        HashMap usermap = getUsermap();
        operate = sqlConcat();
        RowSet rowSet = null;
        RowSet rowSet1 = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            StringBuffer sql = new StringBuffer();
            if (actor_type != null && "4".equals(actor_type)) {//业务用户的查询
                sql.append("select distinct a.groupid,UserName,mainbody.actor_type,mainbody.relation_id");
                sql.append(" from operuser a");
                sql.append(" left join");
                sql.append(" (select * from  t_wf_mainbody where relation_id=" + relation_id + ") mainbody");
                sql.append(" on a.UserName =  mainbody.object_id ");
                sql.append(" where a.roleid<>1 ");
                
                //使用递归查找该节点下所用的节点
                HashMap map = new HashMap();
                getUserArray(map, a_code);
                Iterator it = map.keySet().iterator();
                String groups = "";
                while (it.hasNext()) {
                    groups += (String) it.next() + ",";
                }
                
                if (groups.length() > 0) {
                    groups = groups.substring(0, groups.length() - 1);
                
                    /** 2013-2-22  田野修改
                      *存在标记为selectAllFlag且是超级用户组要查询所有所以数据 （不拼写sql语句）
                     */
                    if (null == selectAllFlag || !"1".equals(groups)) {
                        sql.append(" and a.groupid in (" + groups + ") ");
                    }
                } else {
                    sql.append(" and a.groupid in (" + a_code + ") ");
                }
                
                if (a0101 != null && a0101.trim().length() > 0) {
                    sql.append(" and UPPER(UserName) like ('%" + a0101.toUpperCase() + "%')");
                }
                
                sql.append(" order by  a.groupid,a.UserName ");
            } else {//自助用户的查询
                sql.append("select distinct a01.a0000,a01.a0100 ,a01.b0110,a01.e0122,a01.e01a1,a01.a0101 ,a01.username,");
                sql.append("mainbody.actor_type,mainbody.relation_id ");
                sql.append("FROM " + pre + "A01  a01 left join ( select * from  t_wf_mainbody where relation_id=" + relation_id
                        + ")  mainbody on '" + pre + "'" + operate + "a01.a0100=mainbody.object_id where 1=1  ");
                if (code != null && !"".equals(code)) {
                    if ("UN".equalsIgnoreCase(codeset)) {
                        sql.append(" and a01.b0110 like '" + code + "%' ");
                    } else if ("UM".equalsIgnoreCase(codeset)) {
                        sql.append(" and a01.e0122 like '" + code + "%' ");
                    } else if ("@k".equalsIgnoreCase(codeset)) {
                        sql.append(" and a01.e01a1 like '" + code + "%' ");
                    }
                }

                String userPrivwhl = getPrivWhere(this.userview, "a01");
                sql.append(userPrivwhl);

                if (a0101 != null && a0101.trim().length() > 0) {
                    sql.append(" and a01.a0101 like ('%" + a0101 + "%')");
                }
                sql.append(" order by a0000");
            }
            rowSet = dao.search(sql.toString());
            LazyDynaBean abean = null;

            //增加按组织单元定义（如部门）审批关系（只有自助用户的审批关系才有）
            if (!(actor_type != null && "4".equals(actor_type))) {
                String sqlStr = " select distinct org.codesetid,org.codeitemid,org.codeitemdesc,mainbody.actor_type,mainbody.relation_id from organization org left join (select * from  t_wf_mainbody where relation_id="
                        + relation_id
                        + ") mainbody on (org.codesetid"
                        + operate
                        + "org.codeitemid) = mainbody.object_id where org.codesetid='"
                        + codeset
                        + "' and org.codeitemid = '"
                        + code + "'";
                abean = new LazyDynaBean();
                rowSet1 = dao.search(sqlStr);
                while (rowSet1.next()) {
                    String codesetid = rowSet1.getString("codesetid");
                    String codeitemid = rowSet1.getString("codeitemid");
                    if (codesetid != null && "UN".equalsIgnoreCase(codesetid)) {
                        abean.set("b0110", codeitemid != null ? codeitemid : "");
                        abean.set("e0122", "");
                        abean.set("e01a1", "");
                    } else if (codesetid != null && "UM".equalsIgnoreCase(codesetid)) {
                        String b0110temp = null;
                        b0110temp = getOrgIdByCodeItem(codeitemid, "UN");
                        
                        if (codeitemid == null) {
                            codeitemid = "";
                        }
                        abean.set("b0110", b0110temp != null ? b0110temp : "");
                        abean.set("e0122", codeitemid);
                        abean.set("e01a1", "");
                    } else if (codesetid != null && "@K".equalsIgnoreCase(codesetid)) {
                        String b0110temp = null;
                        String tempcodeitemid = null;
                        b0110temp = getOrgIdByCodeItem(codeitemid, "UN");
                        tempcodeitemid = getOrgIdByCodeItem(codeitemid, "UM");
                        
                        abean.set("b0110", b0110temp != null ? b0110temp : "");
                        abean.set("e0122", tempcodeitemid);
                        abean.set("e01a1", codeitemid != null ? codeitemid : "");
                    }
                    abean.set("a0101", "");
                    abean.set("object_id", rowSet1.getString("codesetid") + rowSet1.getString("codeitemid"));
                    abean.set("postid", "");
                    abean.set("actor_type", rowSet1.getString("actor_type") != null ? rowSet1.getString("actor_type") : "");
                    list.add(abean);
                }
            }

            while (rowSet.next()) {
                if (actor_type != null && "4".equals(actor_type)) {
                    String actor_type2 = rowSet.getString("actor_type");
                    actor_type2 = actor_type2 != null ? actor_type2 : "";

                    abean = new LazyDynaBean();
                    if (usermap != null && usermap.get(rowSet.getString("groupid")) != null) {
                        abean.set("b0110", usermap.get(rowSet.getString("groupid")));
                    } else {
                        abean.set("b0110", "");
                    }
                    abean.set("a0101", rowSet.getString("UserName"));
                    abean.set("object_id", rowSet.getString("UserName"));
                    abean.set("actor_type", actor_type2);
                } else {
                    String b0110 = rowSet.getString("b0110");
                    String e0122 = rowSet.getString("e0122");
                    String e01a1 = rowSet.getString("e01a1");
                    String actor_type2 = rowSet.getString("actor_type");
                    actor_type2 = actor_type2 != null ? actor_type2 : "";
                    if (e0122 == null) {
                        e0122 = "";
                    }
                    abean = new LazyDynaBean();
                    abean.set("b0110", b0110);
                    abean.set("e0122", e0122);
                    abean.set("e01a1", e01a1);
                    abean.set("a0101", rowSet.getString("a0101") == null ? "" : rowSet.getString("a0101"));
                    abean.set("object_id", pre + rowSet.getString("a0100"));
                    abean.set("postid", e01a1 != null ? e01a1 : "");
                    abean.set("actor_type", actor_type2);
                }
                list.add(abean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            try {
                if (rowSet != null) {
                    rowSet.close();
                }
            } catch (Exception e) {

            }

            try {
                if (rowSet1 != null) {
                    rowSet1.close();
                }
            } catch (Exception e) {

            }
        }
        return list;
    }

    private String getOrgIdByCodeItem(String codeitemid, String codeSetId) {
        String orgId = "";
        for (int i = codeitemid.length() - 1; i > 0; i--) {
            String anOrgId = codeitemid.substring(0, i);
            String orgName = AdminCode.getCodeName(codeSetId, anOrgId);
            if (orgName != null && !"".equals(orgName)) {
                orgId = anOrgId;
                break;
            }
        }
        return orgId;
    }

    private String sqlConcat() {
        return Sql_switcher.concat();
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
        if (!userView.isSuper_admin()&&!"1".equals(userView.getGroupId()))
        {
            String operOrg = userView.getUnit_id(); 
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
            if("UN`".equalsIgnoreCase(operOrg)){//设置为全部就是UN` 此时为1=1  zhaoxg add 2015-6-2
            	buf.append(" and 1=1 ");
            }
            if(buf.length()==0)//没有设置任何权限
            {
                buf.append(" and 1=2 ");
            }
        }
        return buf.toString();
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
      if (!userView.isSuper_admin())
        {
//            String operOrg = userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
              String operOrg = userView.getUnit_id(); 
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
            if("UN`".equalsIgnoreCase(operOrg)){//设置为全部就是UN` 此时为1=1  zhaoxg add 2015-6-2
            	buf.append(" and 1=1 ");
            }
          if(buf.length()==0)//没有设置任何权限
          {
              buf.append(" and 1=2 ");
          }
        }
        return buf.toString();
    }
    
    /**
     * 获得当前用户组下得所有用户组
     */
    public HashMap getUserArray(HashMap map ,String groupid){
    	StringBuffer strsql = new StringBuffer();
    	strsql.append("select username,fullname,a.groupid as groupid,b.groupid as sss,userflag,roleid from operuser a left join usergroup b ");
		strsql.append(" on a.username=b.groupname ");		
		if("1".equals(groupid)){
			map.put(groupid, groupid);
			return map;
		}else{
			strsql.append(" where roleid=1 and a.groupid="+groupid+" order by sss");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = null;
			try {
				rowSet = dao.search(strsql.toString());
				ArrayList list = new ArrayList();
				while(rowSet.next()){
					map.put(rowSet.getString("groupid"), rowSet.getString("groupid"));
					map.put(rowSet.getString("sss"), rowSet.getString("sss"));
					list.add(rowSet.getString("sss"));
				}
				for(int i=0;i<list.size();i++){
					String id =""+list.get(i);
					if(id.length()>0) {
                        getUserArray( map , id);
                    }
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			
		}
    	return map;
    }
    /**
     * 获得业务用户的用户组map
     */
    public HashMap getUsermap(){
    	HashMap map = new HashMap();
    	 ContentDAO dao = new ContentDAO(this.conn);
    	 RowSet rowSet = null;
    	 try {
			rowSet = dao.search(" select * from usergroup ");
			  while (rowSet.next())
			    {
					String groupid = rowSet.getString("groupid");
					map.put(groupid, rowSet.getString("groupname"));
			    }
		} catch (SQLException e) {
		}
    	finally{
    		if(rowSet!=null) {
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
    	}
    	 return map;
    }
    /** 获得考核对象类别 */
    public ArrayList getObjTypes() throws GeneralException
    {

		ArrayList list = new ArrayList();
		CommonData temp = new CommonData("", "");
		list.add(temp);
		RowSet rowSet = null;
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    //只是列出人员考核对象类别
		    StringBuffer sql = new StringBuffer("select * from per_mainbodyset where body_type=1 and status=1  and (object_type=2 or object_type is null) order by seq");
		    rowSet = dao.search(sql.toString());
	
		    while (rowSet.next())
		    {
				temp = new CommonData(rowSet.getString("body_id"), rowSet.getString("name"));
				list.add(temp);
		    }
		    
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return list;
    }
    /** 获得所有考核对象类别 包括无效的*/
    public ArrayList getObjTypes2() throws GeneralException
    {

		ArrayList list = new ArrayList();
		CommonData temp = new CommonData("", "");
		list.add(temp);
		RowSet rowSet = null;
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    StringBuffer sql = new StringBuffer("select * from per_mainbodyset where body_type=1");
		    rowSet = dao.search(sql.toString());
	
		    while (rowSet.next())
		    {
				temp = new CommonData(rowSet.getString("body_id"), rowSet.getString("name"));
				list.add(temp);
		    }
		    
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return list;
    }
    /** 获得对象对应的主体 */
    public ArrayList getMainBodys(String object_id,String relation_id,String actor_type) throws GeneralException
    {

		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		try
		{
			//获得用户组map
			HashMap usermap = getUsermap();
		    ContentDAO dao = new ContentDAO(this.conn);
		    StringBuffer sql = new StringBuffer("select * from t_wf_mainbody ");
		    sql.append("where UPPER(object_id)='");
		    sql.append(object_id.toUpperCase());
		    sql.append("' and relation_id="+relation_id+" order by sp_grade,mainbody_id");
	
		    rowSet = dao.search(sql.toString());
		    LazyDynaBean abean = null;
		    while (rowSet.next())
		    {
				String b0110 = rowSet.getString("b0110");
				String e0122 = rowSet.getString("e0122");
				String e01a1 = rowSet.getString("e01a1");
				String sp_grade = rowSet.getString("sp_grade");
				if("9".equals(sp_grade)) {
                    sp_grade = "直接领导";
                }
				if("10".equals(sp_grade)) {
                    sp_grade = "主管领导";
                }
				if("11".equals(sp_grade)) {
                    sp_grade = "第三级领导";
                }
				if("12".equals(sp_grade)) {
                    sp_grade = "第四级领导";
                }
				abean = new LazyDynaBean();
			
				if(actor_type!=null&& "4".equals(actor_type)){//业务用户
					if(rowSet.getString("a0101")==null||rowSet.getString("a0101").trim().length()==0){
					    abean.set("a0101", rowSet.getString("mainbody_id"));
					    String inname =rowSet.getString("mainbody_id");
                        String username="";
                        if(inname!=null){
                            RecordVo operuser=new RecordVo("operuser");
                            operuser.setString("username",inname);
                            operuser=dao.findByPrimaryKey(operuser);
                            if(operuser!=null){
                                username=operuser.getString("fullname");
                            }
                        }
                        abean.set("username",username==null?"":username);
					}
					else{
					    abean.set("a0101", rowSet.getString("mainbody_id")==null?"":rowSet.getString("mainbody_id"));
					    String inname =rowSet.getString("mainbody_id");
                        String username="";
                        if(inname!=null){
                            RecordVo operuser=new RecordVo("operuser");
                            operuser.setString("username",inname);
                            operuser=dao.findByPrimaryKey(operuser);
                            if(operuser!=null){
                                username=operuser.getString("fullname");
                            }
                        }
                        abean.set("username",username==null?"":username);
					}
						
					if(usermap!=null&&usermap.get(rowSet.getString("groupid"))!=null) {
                        abean.set("b0110", usermap.get(rowSet.getString("groupid")));
                    } else{
							abean.set("b0110", "");
						}
				}else{
				    
					if(e0122==null){
					    e0122=""; 
					}
					String desc=this.getDepartment(e0122);
					abean.set("b0110", b0110 != null ? AdminCode.getCodeName("UN", b0110) : "");
					abean.set("e0122", desc.toString());
					abean.set("e01a1", e01a1 != null ? AdminCode.getCodeName("@K", e01a1) : "");
					abean.set("a0101", rowSet.getString("a0101")==null?"":rowSet.getString("a0101"));
				}
				
				abean.set("object_id", object_id);
				abean.set("mainbody_id", rowSet.getString("mainbody_id"));
				abean.set("sp_grade", sp_grade);
				list.add(abean);
		    }
		    
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return list;
    }

    /** 获得考核主体类别 */
    public HashMap getMainBodyTypes() throws GeneralException
    {

		HashMap map = new HashMap();
		RowSet rowSet = null;
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    StringBuffer sql = new StringBuffer("select * from per_mainbodyset where body_type=0 or body_type is null");
		    rowSet = dao.search(sql.toString());
		    while (rowSet.next()) {
                map.put(rowSet.getString("body_id"), rowSet.getString("name"));
            }
		    
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return map;
    }
    
    /** 获得考核主体类别 */
    public ArrayList getMainBodyTypes2() throws GeneralException
    {

		ArrayList list = new ArrayList();
		CommonData temp = new CommonData("", "");
		list.add(temp);
		RowSet rowSet = null;
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    StringBuffer sql = new StringBuffer("select * from per_mainbodyset where (body_type=0 or body_type is null) and status=1 and body_id!=-1 order by seq");
		    rowSet = dao.search(sql.toString());
	
		    while (rowSet.next())
		    {
				temp = new CommonData(rowSet.getString("body_id"), rowSet.getString("name"));
				list.add(temp);
		    }
		    
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return list;
    }
    /** 获得所有考核主体类别 */
    public ArrayList getMainBodyTypes3() throws GeneralException
    {

		ArrayList list = new ArrayList();
		CommonData temp = new CommonData("", "");
		list.add(temp);
		RowSet rowSet = null;
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    StringBuffer sql = new StringBuffer("select * from per_mainbodyset where (body_type=0 or body_type is null)");
		    rowSet = dao.search(sql.toString());
	
		    while (rowSet.next())
		    {
				temp = new CommonData(rowSet.getString("body_id"), rowSet.getString("name"));
				list.add(temp);
		    }
		    
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return list;
    }

    /** 手工选择 条件选择 考核对象 */
    public void handInsertObjects(String a0100s) throws GeneralException
    {
    	a0100s = PubFunc.keyWord_reback(a0100s);
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
	
		    StringBuffer insertSql = new StringBuffer();
		    insertSql.append("insert into per_object_std(object_id,b0110,e0122,e01a1,a0101,a0000)");
		    insertSql.append("select a0100,b0110,e0122,e01a1,a0101,a0000 from usra01 where a0100 in (" + a0100s + ")");
		    insertSql.append(" and a0100 not in (select object_id from per_object_std)");
	
		    dao.insert(insertSql.toString(), new ArrayList());
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
    }

    /** 取出选中的考核对象 */
    public ArrayList getKhObjectsList(String[] array,String relation_id,String dbpre,String actor_type) throws GeneralException
    {
		ArrayList list = new ArrayList();
		if (array == null) {
            return list;
        }
		
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer sqlStr = new StringBuffer();
		for (int i = 0; i < array.length; i++)
		{
		    if ("".equals(array[i])) {
                continue;
            }
		    sqlStr.append("'" + array[i] + "',");
		}
		String operate = "";// 取sql得到连接符
		operate = sqlConcat();
		StringBuffer buf = new StringBuffer();
		 if(actor_type!=null&& "4".equals(actor_type)){
			 buf.append("select groupid,UserName  from operuser where  UserName in ");
			 buf.append("  (" + sqlStr.substring(0, sqlStr.length() - 1) + ")" );
			 buf.append("  and roleid<>1  ");
			 buf.append(" order by  groupid,UserName ");
		    }else{
		buf.append("select '"+dbpre+"'"+operate+"a0100 a0100,");
		buf.append("a0101 ");
		buf.append(" from "+dbpre+"a01 ");
		buf.append("where '"+dbpre+"'"+operate+"a0100 in ");
		buf.append("  (" + sqlStr.substring(0, sqlStr.length() - 1) + ")" );
		buf.append(" order by a0000");
		    }
	
		RowSet rowSet = null;
		RowSet rowSet1 = null;
		try
		{
			rowSet = dao.search(buf.toString());
			for (int i = 0; i < array.length; i++)
			{
			    if ("".equals(array[i])||array[i].length()<=2|| (!"UN".equalsIgnoreCase(array[i].substring(0, 2)) && !"UM".equalsIgnoreCase(array[i].substring(0, 2)) && !"@K".equalsIgnoreCase(array[i].substring(0, 2)))) {
                    continue;
                }
			    String sqlStr11 = "select '"+array[i].substring(0, 2)+"'"+operate+"codeitemid a0100,codeitemdesc a0101 from organization where '"+array[i].substring(0, 2)+"'"+operate+"codeitemid ='"+array[i]+"'";
			    rowSet1 = dao.search(sqlStr11);
			    while (rowSet1.next())
			    {
			    	if(actor_type!=null&& "4".equals(actor_type)){
			    		 
			    	}else{
						CommonData temp = new CommonData(rowSet1.getString("a0100"), rowSet1.getString("a0101"));
						list.add(temp);
			    	}
			    }
			}
			
			
		    while (rowSet.next())
		    {
		    	 if(actor_type!=null&& "4".equals(actor_type)){
		    		 CommonData temp = new CommonData(rowSet.getString("UserName"), rowSet.getString("UserName"));
						list.add(temp); 
		    	 }else{
				CommonData temp = new CommonData(rowSet.getString("a0100"), rowSet.getString("a0101"));
				list.add(temp);
		    	 }
		    }
		    
		    if(rowSet!=null) {
                rowSet.close();
            }
		    if(rowSet1!=null) {
                rowSet1.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return list;

    }

    /** 根据考核对象,考核主体类别查询考核主体 */
    public ArrayList getMainBodyList(HashMap objs, String sp_grade,String relation_id,String actor_type) throws GeneralException
    {

		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    StringBuffer sql = new StringBuffer();
		    String comsql = "";
		    sql.append("select * from t_wf_mainbody where 1=1 ");
	
		    StringBuffer buf = new StringBuffer();
		    Set objSet = objs.keySet();
		    Iterator it = objSet.iterator();
		    while (it.hasNext())
		    {
				String objId = (String) it.next();
				buf.append(",'" + objId + "'");
		    }
	
		    if (buf.length() > 0) {
                sql.append(" and object_id in(" + buf.substring(1) + ") and relation_id="+relation_id +" ");
            } else {
                sql.append(" and 1=2 ");
            }
		    comsql = sql.toString();
		    if (!"all".equals(sp_grade)) {
                sql.append(" and sp_grade='" + sp_grade + "'");
            }
	
		    sql.append(" order by sp_grade,mainbody_id");
		    comsql+=" order by sp_grade,mainbody_id";
		    rowSet = dao.search(sql.toString());
		    LazyDynaBean abean = null;
		  //获得用户组map
			HashMap usermap = getUsermap();
		    while (rowSet.next())
		    {
				String b0110 = rowSet.getString("b0110");
				String e0122 = rowSet.getString("e0122");
				String e01a1 = rowSet.getString("e01a1");
				String a0101 = rowSet.getString("a0101");
				String sp_grade2 = rowSet.getString("sp_grade");
				if("9".equals(sp_grade2)) {
                    sp_grade2 = "直接领导";
                }
				if("10".equals(sp_grade2)) {
                    sp_grade2 = "主管领导";
                }
				if("11".equals(sp_grade2)) {
                    sp_grade2 = "第三级领导";
                }
				if("12".equals(sp_grade2)) {
                    sp_grade2 = "第四级领导";
                }
				abean = new LazyDynaBean();
				abean.set("mainbody_id", rowSet.getString("mainbody_id"));
				String objectID = rowSet.getString("object_id");
				String objectName = (String) objs.get(objectID);
				abean.set("objectID", objectID);
				abean.set("objectName", objectName == null ? "" : objectName);
				
				if(actor_type!=null&& "4".equals(actor_type)){
					if(usermap!=null&&usermap.get(rowSet.getString("groupid"))!=null) {
                        abean.set("b0110", ""+usermap.get(rowSet.getString("groupid")));
                    } else{
							abean.set("b0110", "");
						}
					abean.set("a0101", rowSet.getString("mainbody_id"));
					String inname =rowSet.getString("mainbody_id");
                    String username="";
                    if(inname!=null){
                        RecordVo operuser=new RecordVo("operuser");
                        operuser.setString("username",inname);
                        operuser=dao.findByPrimaryKey(operuser);
                        if(operuser!=null){
                            username=operuser.getString("fullname");
                        }
                    }
                    abean.set("username", username==null?"":username);
				}else{
					abean.set("b0110", b0110 != null ? AdminCode.getCodeName("UN", b0110) : "");
					abean.set("a0101", a0101 == null ? "" : a0101);
				}
				abean.set("e0122", e0122 != null ? AdminCode.getCodeName("UM", e0122) : "");
				abean.set("e01a1", e01a1 != null ? AdminCode.getCodeName("@K", e01a1) : "");
				
				abean.set("bodyTypeName", sp_grade2);
				abean.set("bodyid", rowSet.getString("sp_grade"));
				abean.set("relation_id", relation_id);
				list.add(abean);
		    }
		   //处理重复选中，加上提示
		    rowSet = dao.search(comsql);
		    String sqlstr="";
		    while(rowSet.next()){
		    	//格式为,特殊角色id:对象名：考核主体名,
		    	String sp_grade2 = rowSet.getString("sp_grade");
		    	String objectID = rowSet.getString("object_id");
		    	String objectName = (String) objs.get(objectID);
		    	String a0101=rowSet.getString("a0101");
		    	String mainbody_id = rowSet.getString("mainbody_id");
		    	if(actor_type!=null&& "4".equals(actor_type)){
		    		a0101 =rowSet.getString("mainbody_id");
				}else{
					
					a0101 = a0101 == null ? " " : a0101;
				}
		    	 sqlstr +=","+sp_grade2+":"+objectName+":"+a0101+":"+mainbody_id;
		    }
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return list;
    }

    /** 指定考核主体/条件（手工）选人 
     * 原则 考核关系里  考核对象不能把自己选为自己的考核主体
     * db 条件选人  人员库前缀
     * */
    public String selMainBody(String a0100s,String mainBodyType, String object,String relation_id,String dbpre,String actor_type,String db) throws GeneralException
    {	
    	
    	StringBuffer names=new StringBuffer();
    	boolean nameFlag = true;
		try
		{
			
		    ContentDAO dao = new ContentDAO(this.conn);
		    // 先删除该对象已存在的重复主体
//		    String delStr = "delete from t_wf_mainbody where object_id = '" + object + "' and mainbody_id in (" + a0100s + ") and relation_id="+relation_id;
//		    dao.delete(delStr, new ArrayList());
//	

		    if(actor_type!=null&& "1".equals(actor_type)){//如果是自助用户
		    
//		    insertSql.append("insert into t_wf_mainbody (mainbody_id,object_id,b0110,e0122,e01a1,a0101,body_id)");
//			insertSql.append("select a0100,'" + object + "',b0110,e0122,e01a1,a0101,'" + mainBodyType + "' from usra01 where a0100 in (" + a0100s + ") ");
//			insertSql.append(" and a0100<>'"+ object + "'");
		    a0100s = PubFunc.keyWord_reback(a0100s);
	    	//处理人员库，由于调用GetQuerySQLTrans下的getSQL2写死人员库。
//	    	if(!dbpre.equalsIgnoreCase("Usr")){
//	    		a0100s = a0100s.replaceAll("Usr", dbpre);
//	    		a0100s = a0100s.replaceAll("USR", dbpre);
//	    		a0100s = a0100s.replaceAll("usr", dbpre);
//	    	}
			String operate = "";// 取sql得到连接符
			operate = sqlConcat();
			String[] str=a0100s.split(",");
			String sql="select pre from dbname";
			ResultSet roes=null;
			roes=dao.search(sql);
			String pre="";
			//为了支持跨人员库选择审批主体
			while(roes.next()){
			    StringBuffer insertSql = new StringBuffer();
			    StringBuffer selectSql = new StringBuffer();
			    StringBuffer selectSql2 = new StringBuffer();
			    StringBuffer selectSql3 = new StringBuffer();
				pre=roes.getString("pre");
				if(db!=null&&!"".equals(db)){//条件选人 判断从哪个人员库取审批主体
					if(!db.equalsIgnoreCase(pre)){
						continue;
					}
				}
				StringBuffer stringBuffer=new StringBuffer();
				for(int i = 0; i < str.length; i++){
					if(str[i].indexOf("select")!=-1){//针对条件选人
						stringBuffer.append(a0100s);
					}else if(str[i].indexOf(pre)!=-1)
					{
						stringBuffer.append(str[i].replace(pre, "")+",");
					}
				}
				if(stringBuffer.length()>0){
				if(stringBuffer.toString().indexOf("select")==-1){
					a0100s=stringBuffer.toString().substring(0, stringBuffer.toString().length()-1);
				}else{
					a0100s=stringBuffer.toString();
				}
				
				insertSql.append("insert into t_wf_mainbody(mainbody_id,object_id,relation_id,b0110,e0122,e01a1,a0101,sp_grade,actor_type,create_time,create_user,mod_time,mod_user)");
				selectSql.append("select '"+pre+"'"+operate+"a0100 a0100,'");
				selectSql.append(object+"' mainbody_id,'");
				selectSql.append(relation_id);
				selectSql.append("' relation_id,b0110,e0122,e01a1,a0101,'"+mainBodyType+"' sp_grade, ");
				selectSql.append("'"+actor_type+"' actor_type,"+Sql_switcher.dateValue(DateStyle.getSystemTime())+" create_time, ");
				selectSql.append("'"+this.userview.getUserName()+"' create_user,"+Sql_switcher.dateValue(DateStyle.getSystemTime())+" mod_time,'"+this.userview.getUserName()+"' mod_user ");
				selectSql.append(" from "+pre+"a01 ");
				selectSql.append("where '"+pre+"'"+operate+"a0100 not in (select mainbody_id from t_wf_mainbody where object_id='");
				selectSql.append(object);
				selectSql.append("' and relation_id='"+relation_id+"') and '"+pre+"'"+operate+"a0100<>'"+object+"' and a0100 in (" + a0100s + ")");
				
				selectSql2.append("select '"+pre+"'"+operate+"a0100 a0100,'");
				selectSql2.append(object+"' mainbody_id,'");
				selectSql2.append(relation_id);
				selectSql2.append("' relation_id,b0110,e0122,e01a1,a0101, ");
				selectSql2.append("'"+actor_type+"' actor_type,"+Sql_switcher.dateValue(DateStyle.getSystemTime())+" create_time, ");
				selectSql2.append("'"+this.userview.getUserName()+"' create_user,"+Sql_switcher.dateValue(DateStyle.getSystemTime())+" mod_time,'"+this.userview.getUserName()+"' mod_user,a0101 ");
				selectSql2.append(" from "+pre+"a01 ");
				selectSql2.append("where '"+pre+"'"+operate+"a0100<>'"+object+"' and a0100 in (" + a0100s + ") ");
				selectSql3.append(" select a0100,a0101 from "+dbpre+"a01 where '"+dbpre+"'"+operate+"a0100='"+object+"' ");
				insertSql.append(selectSql.toString());
				
			    //处理重复选中，加上提示
			    RowSet rowSet = dao.search(selectSql.toString());
			    a0100s = a0100s.replace("'", "");
			    while(rowSet.next()){
			    	
			    	 if(actor_type!=null&& "1".equals(actor_type)){
			    		 String a0100 = rowSet.getString("a0100");
			    		 if(a0100!=null&&a0100.length()>3) {
                             a0100 =a0100.substring(3);
                         }
			    		 if(a0100s.indexOf(a0100)!=-1){
			    			 a0100s = a0100s.replaceAll(a0100, "").replace(",,", ",");
			    			
			    		 }
			    	 }else{
			    		 String a0100 = rowSet.getString("username");
			    		 if(a0100s.indexOf(a0100)!=-1){
			    			 a0100s = a0100s.replaceAll(a0100, "").replace(",,", ",");
			    			
			    		 }
			    	 }
			    }
			    dao.insert(insertSql.toString(), new ArrayList());
			    if(a0100s.length()>2){
			    	String name2 ="";
			    	rowSet = dao.search(selectSql3.toString());
			    	if(rowSet.next()){
			    		if(actor_type!=null&& "1".equals(actor_type)){
			    			 name2=rowSet.getString("a0101");
			    		}else{
			    			 name2=rowSet.getString("username");
			    		}
			    	}
			    	rowSet = dao.search(selectSql2.toString());
			    	StringBuffer namecopy = new StringBuffer();
			    	namecopy.append("审批对象:");
			    	namecopy.append(name2);
			    	namecopy.append("的");
			    	namecopy.append("领导");
			    	namecopy.append("已经设置了");
			    	 while(rowSet.next()){
					    nameFlag=false;
				    	 if(actor_type!=null&& "1".equals(actor_type)){
				    		 String a0100 = rowSet.getString("a0100");
				    		 if(a0100!=null&&a0100.length()>3) {
                                 a0100 =a0100.substring(3);
                             }
				    		 if(a0100s.indexOf(a0100)!=-1){
				    			//格式为:审批对象:张三的领导已经设置了李四,王五
				    			 
				    			 names.append(rowSet.getString("a0101"));
				    			 names.append(", ");
				    		 }
				    	 }else{
				    		 String a0100 = rowSet.getString("username");
				    		 if(a0100s.indexOf(a0100)!=-1){
				    			 if(a0100s.indexOf(a0100)!=-1){
						    			//格式为:审批对象:张三的领导已经设置了李四,王五
						    			 names.append(rowSet.getString("username"));
						    			 names.append(", ");
						    		 }
				    		 }
				    	 }
			    	 }
			    	 if(names.toString().trim().length()>0){
			    		 String str1 = names.toString();
			    		 names.setLength(0);
			    		 names.append(namecopy.toString()).append(str1);
			    	 }
			    	 if(actor_type!=null&& "1".equals(actor_type)){
			    		 String a0100 = object;
			    		 if(a0100!=null&&a0100.length()>3&&!"um".equalsIgnoreCase(a0100.substring(0, 2))&&!"un".equalsIgnoreCase(a0100.substring(0, 2))&&!"@k".equalsIgnoreCase(a0100.substring(0, 2))) {
                             a0100 =a0100.substring(3);
                         }
			    		 if(a0100s.indexOf(a0100)!=-1){
			    			//格式为:审批对象:张三不能设置自己
			    			 names.append("审批对象:");
			    			 names.append(name2);
			    			 names.append("不能设置自己为自己的领导");
			    		 }
			    	 }else{
			    		 String a0100 = object;
			    		 if(a0100s.indexOf(a0100)!=-1){
			    			 if(a0100s.indexOf(a0100)!=-1){
			    				 names.append("审批对象:");
				    			 names.append(name2);
				    			 names.append("不能设置自己为自己的领导");
					    		 }
			    		 }
			    	 }
			    }
			    
			    if(rowSet!=null) {
                    rowSet.close();
                }
				}
				
			}


		
		    }else{//如果是业务用户
			    StringBuffer insertSql = new StringBuffer();
			    StringBuffer selectSql = new StringBuffer();
			    StringBuffer selectSql2 = new StringBuffer();
			    StringBuffer selectSql3 = new StringBuffer();
		    	insertSql.append("insert into t_wf_mainbody(mainbody_id,object_id,relation_id,b0110,e0122,e01a1,a0101,sp_grade,actor_type,groupid,create_time,create_user,mod_time,mod_user)");
				selectSql.append("select username,'");
				selectSql.append(object+"' object_id,'");
				selectSql.append(relation_id);
				selectSql.append("' relation_id,null,null,null,(case when(fullname is null or fullname ='' or fullname='NULL') then username else fullname end),'"+mainBodyType+"' sp_grade, ");
				selectSql.append("'"+actor_type+"' actor_type, groupid,"+Sql_switcher.dateValue(DateStyle.getSystemTime())+" create_time, ");
				selectSql.append("'"+this.userview.getUserName()+"' create_user,"+Sql_switcher.dateValue(DateStyle.getSystemTime())+" mod_time,'"+this.userview.getUserName()+"' mod_user ");
				selectSql.append(" from operuser ");
				selectSql.append("where username not in (select mainbody_id from t_wf_mainbody where object_id='");
				selectSql.append(object);
				selectSql.append("' and relation_id='"+relation_id+"') and username <>'"+object+"' and username in (" + a0100s + ")");
				
				selectSql2.append("select username,'");
				selectSql2.append(object+"' object_id,'");
				selectSql2.append(relation_id);
				selectSql2.append("' relation_id,null,null,null,(case when(fullname is null or fullname ='' or fullname='NULL') then '"+object+"' else fullname end), ");
				selectSql2.append("'"+actor_type+"' actor_type, groupid,"+Sql_switcher.dateValue(DateStyle.getSystemTime())+" create_time, ");
				selectSql2.append("'"+this.userview.getUserName()+"' create_user,"+Sql_switcher.dateValue(DateStyle.getSystemTime())+" mod_time,'"+this.userview.getUserName()+"' mod_user ");
				selectSql2.append(" from operuser ");
				selectSql2.append("where username <>'"+object+"' and username in (" + a0100s + ")");
				selectSql3.append(" select username from operuser where username ='"+object+"' ");
				insertSql.append(selectSql.toString());
			 
				   //处理重复选中，加上提示  
			    RowSet rowSet = dao.search(selectSql.toString());
			    a0100s = a0100s.substring(a0100s.indexOf("(")+1, a0100s.length()-2);//业务用户将选择的用户截取出来
			    
			    while(rowSet.next()){
			    	
			    	 if(actor_type!=null&& "1".equals(actor_type)){
			    		 String a0100 = rowSet.getString("a0100");
			    		 if(a0100!=null&&a0100.length()>3) {
                             a0100 =a0100.substring(3);
                         }
			    		 if(a0100s.indexOf(a0100)!=-1){
			    			 a0100s = a0100s.replaceAll(a0100, "").replace(",,", ",");
			    			
			    		 }
			    	 }else{
			    		 String a0100 = rowSet.getString("username");
			    		 if(a0100s.indexOf(a0100)!=-1){
			    			 a0100s = a0100s.replaceAll(a0100, "").replace(",,", ",");
			    			
			    		 }
			    	 }
			    }
			    dao.insert(insertSql.toString(), new ArrayList());
			    if(a0100s.length()>2){
			    	String name2 ="";
			    	rowSet = dao.search(selectSql3.toString());
			    	if(rowSet.next()){
			    		if(actor_type!=null&& "1".equals(actor_type)){
			    			 name2=rowSet.getString("a0101");
			    		}else{
			    			 name2=rowSet.getString("username");
			    		}
			    	}
			    	rowSet = dao.search(selectSql2.toString());
			    	StringBuffer namecopy = new StringBuffer();
			    	namecopy.append("审批对象:");
			    	namecopy.append(name2);
			    	namecopy.append("的");
			    	namecopy.append("领导");
			    	namecopy.append("已经设置了");
			    	 while(rowSet.next()){
					    		
				    	 if(actor_type!=null&& "1".equals(actor_type)){
				    		 String a0100 = rowSet.getString("a0100");
				    		 if(a0100!=null&&a0100.length()>3) {
                                 a0100 =a0100.substring(3);
                             }
				    		 if(a0100s.indexOf(a0100)!=-1){
				    			//格式为:审批对象:张三的领导已经设置了李四,王五
				    			 
				    			 names.append(rowSet.getString("a0101"));
				    			 names.append(", ");
				    		 }
				    	 }else{
				    		 String a0100 = rowSet.getString("username");
				    		 if(a0100s.indexOf(a0100)!=-1){
				    			 if(a0100s.indexOf("'"+a0100+"'")!=-1){
						    			//格式为:审批对象:张三的领导已经设置了李四,王五
						    			 names.append(rowSet.getString("username"));
						    			 names.append(", ");
						    		 }
				    		 }
				    	 }
			    	 }
			    	 if(names.toString().trim().length()>0){
			    		 String str1 = names.toString();
			    		 names.setLength(0);
			    		 names.append(namecopy.toString()).append(str1);
			    	 }
			    	 if(actor_type!=null&& "1".equals(actor_type)){
			    		 String a0100 = object;
			    		 if(a0100!=null&&a0100.length()>3&&!"um".equalsIgnoreCase(a0100.substring(0, 2))&&!"un".equalsIgnoreCase(a0100.substring(0, 2))&&!"@k".equalsIgnoreCase(a0100.substring(0, 2))) {
                             a0100 =a0100.substring(3);
                         }
			    		 if(a0100s.indexOf("'"+a0100+"'")!=-1){
			    			//格式为:审批对象:张三不能设置自己
			    			 names.append("审批对象:");
			    			 names.append(name2);
			    			 names.append("不能设置自己为自己的领导");
			    		 }
			    	 }else{
			    		 String a0100 = object;
			    		 if(a0100s.indexOf("'"+a0100+"'")!=-1){
			    			 if(a0100s.indexOf(a0100)!=-1){
			    				 names.append("审批对象:");
				    			 names.append(name2);
				    			 names.append("不能设置自己为自己的领导");
					    		 }
			    		 }
			    	 }
			    
			    if(rowSet!=null) {
                    rowSet.close();
                }
				}
		
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw new GeneralException(e.getMessage());
		}
		if(actor_type!=null&& "1".equals(actor_type)&&db!=null){//确保是在条件选人的条件下 提示该信息 在js中发现手工选人是没有传递db过来的 2013-10-26
		    if(nameFlag){
		        names.setLength(0);
		        names.append("查询中没有选中人员");
		    }
		}
		return names.toString();
    }
    
   /**删除考核主体*/
    public void delKhMainBody(String[] ids)  throws GeneralException
    {

		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList list = new ArrayList();
		// 删除考核主体表
		StringBuffer strSql = new StringBuffer();
		strSql.append("delete from t_wf_mainbody where mainbody_id=? and object_id=? and relation_id=?");
	
		for (int i = 0; i < ids.length; i++)
		{	
		    String id = ids[i];// mainbody_id:objectID:body_id
		    String[] temp = id.split(":");
		    String mainBody = temp[0];
		    String object_id = temp[1];
		    String relation_id = temp[2];
		    
		    ArrayList list1 = new ArrayList();
		    list1.add(mainBody);
		    list1.add(object_id);
		    list1.add(relation_id);
		    list.add(list1);
		}
	
		try
		{
			
		    dao.batchUpdate(strSql.toString(), list);
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw new GeneralException(e.getMessage());
		}
    }
    
    /**粘贴考核主体
     * 原则不可以把自己做为自己的考核主体*/
    public void pasteKhMainBody(String objectPasted,String objectCopyed,String relation_id)  throws GeneralException
    {
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append("insert into t_wf_mainbody(mainbody_id,object_id,relation_id,b0110,e0122,e01a1,a0101,sp_grade,actor_type,groupid,create_time,create_user,mod_time,mod_user)");
			buf.append("select mainbody_id,'");
			buf.append(objectPasted);
			buf.append("',relation_id,b0110,e0122,e01a1,a0101,sp_grade,actor_type,groupid,create_time,create_user,mod_time,mod_user from t_wf_mainbody where object_id='");
			buf.append(objectCopyed);
			buf.append("' and mainbody_id not in (select mainbody_id from t_wf_mainbody where object_id='");
			buf.append(objectPasted);
			buf.append("' and relation_id="+relation_id+") and relation_id="+relation_id+" and mainbody_id<>'"+objectPasted+"' ");//不让把自己 作为自己的考核主体 
		
			dao.insert(buf.toString(), new ArrayList());
			
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw new GeneralException(e.getMessage());
		}
    }
    
    /**取出某个表中的字段进行单表查询*/
    public ArrayList getFieldlist(String tablename)
    {
		ArrayList list = new ArrayList();	
		ArrayList fieldList = DataDictionary.getFieldList("A01", Constant.USED_FIELD_SET);
		for (int i = 0; i < fieldList.size(); i++)
		{
		    FieldItem fi = (FieldItem) fieldList.get(i);
		    String itemid = fi.getItemid();
		    if("b0110,e0122,e01a1,a0101".indexOf(itemid)!=-1)
		    {
				CommonData temp = new CommonData(fi.getItemid() + "<@>" + fi.getItemdesc() + "<@>" + fi.getItemtype() + "<@>" + fi.getCodesetid() + "<@>" + tablename, fi.getItemdesc());
				list.add(temp);
		    }	  
		}
		CommonData temp = new CommonData( "obj_body_id<@>考核对象类别<@>N<@>0<@>" + tablename, "考核对象类别");
		list.add(temp);
		return list;
    }
    
    /**获得 汇报关系中 直接上级指标*/	
    public String getPS_SUPERIOR_value()
    {
		String fieldItem="";
		RecordVo vo=ConstantParamter.getConstantVo("PS_SUPERIOR");
		if(vo==null) {
            return fieldItem;
        }
		String param=vo.getString("str_value");
		if(param==null|| "".equals(param)|| "#".equals(param)) {
            return fieldItem;
        }
		fieldItem=param;
		return fieldItem;
    }
    
    /**获得本人考核主体类别的ID
     * @throws GeneralException */
    public String getSelfBodyID() throws GeneralException
    {
		String selbodyid = "";
		RowSet rowSet = null;
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    String level = "level";
		    if(Sql_switcher.searchDbServer() == Constant.ORACEL) {
                level = "level_o";
            }
		    String strSql="select body_id from per_mainbodyset where "+level+"=5";
		    rowSet = dao.search(strSql);
	
		    if (rowSet.next()) {
                selbodyid = rowSet.getString(1)==null?"":rowSet.getString(1);
            }
		    
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return selbodyid;
    }
    
    /**判断是否可以进行考核关系的调整
     * @throws GeneralException */
    public boolean isEnableFlag() throws GeneralException
    {
		boolean flag = false;
		RowSet rowSet = null;
		try
		{
		    ContentDAO dao = new ContentDAO(this.conn);
		    String strSql="select * from per_plan where method=2 and status in (4,8,6)";
		    rowSet = dao.search(strSql);
	
		    if (rowSet.next()) {
                flag=true;
            }
		    
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return flag;
    }
    
    /**选出参与了目标计划的考核对象，且这些计划处于分发 评估 和结束状态*/
    public HashMap getJoinedObjs()  throws GeneralException
    {
    	HashMap map = new HashMap();
    	RowSet rowSet = null;
    	try
    	{
    	    ContentDAO dao = new ContentDAO(this.conn);
    	    String strSql="select distinct object_id from per_object where plan_id in (select plan_id  from per_plan where method=2 and status in (4,8,6))";
    	    rowSet = dao.search(strSql);
    	    while (rowSet.next())
    	    {
    	    	map.put(rowSet.getString(1), "");
    	    }
    	    //团队负责人的考核关系也不允许变动
    	    strSql="select distinct mainbody_id from per_mainbody where body_id=-1 and plan_id in (select plan_id  from per_plan where method=2 and status in (4,8,6))";
    	    rowSet = dao.search(strSql);
    	    while (rowSet.next())
    	    {
    	    	map.put(rowSet.getString(1), "");
    	    }
    	    
    	    if(rowSet!=null) {
                rowSet.close();
            }
    	    
    	} catch (Exception e)
    	{
    	    e.printStackTrace();
    	    throw GeneralExceptionHandler.Handle(e);
    	}
    	return map;
    }
    /*
     * 清除审批关系表中的脏数据
     * **/
    public void deleteDataFromRelation(String relationid,String dbpre,String actor_type){
    	try{
    		String operate = "";
    		operate = sqlConcat();
    		//删除t_wf_mainbody表
    		StringBuffer sb = new StringBuffer("");
    		ContentDAO dao = new ContentDAO(this.conn);
    		if("1".equals(actor_type)){//自助用户的审批关系。不仅要考虑到人，还要考虑到单位、部门、岗位
    			//sb.append("delete from t_wf_mainbody where ( upper(mainbody_id) not in (select '"+dbpre.toUpperCase()+"'"+operate+"a0100 a0100 from "+dbpre+"a01)  or upper(object_id) not in (select '"+dbpre.toUpperCase()+"'"+operate+"a0100 a0100 from "+dbpre+"a01)) and ( upper(object_id) not in (select codesetid"+operate+"codeitemid from organization)) and "+Sql_switcher.left("upper(mainbody_id)", 3)+"='USR' and "+Sql_switcher.left("upper(object_id)", 3)+"='"+dbpre.toUpperCase()+"' and relation_id='"+relationid+"' and actor_type='1'");
    	        String []  arrPre= new String[0];
    	        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
    	        String strpres="";
    	        if(login_vo!=null) {
                    strpres = login_vo.getString("str_value").toLowerCase();
                }
    	        
    	        arrPre = strpres.split(",");
    	        if (arrPre.length > 0){
                    //删除考核对象
                    if(Sql_switcher.searchDbServer()==Constant.MSSQL) {
                        sb.append( "delete twm from t_wf_mainbody twm where exists (");
                    } else {
                        sb.append( "delete from t_wf_mainbody twm where exists (");
                    }
                    for (int i = 0; i < arrPre.length; i++) {
                        if (i>0) {
                            sb.append(" union all ");
                        }
                        sb.append(" select null from t_wf_mainbody where twm.object_id=t_wf_mainbody.object_id and ");
                        sb.append("( upper("+Sql_switcher.substr("object_id", "1", "3")+") = '"+arrPre[i].toUpperCase()+"' "
                                //+arrPre[i].toUpperCase()+"' and upper(object_id)  not in (select '"+arrPre[i].toUpperCase()+"' "+operate+" a0100 a0100 from "
                                //+arrPre[i].toUpperCase()+"a01))"); // 使用not exists代替not in，可以使得运行效率很大的提升 liuzy 20150715
                                +" and  not EXISTS (select null from "+arrPre[i].toUpperCase()+"a01 where '"+arrPre[i].toUpperCase()+"'"+operate
                                +arrPre[i].toUpperCase()+"a01.a0100=upper(t_wf_mainbody.object_id)))");
                    }
                    if(arrPre.length>0) {
                        sb.append(" union all ");
                    }
                    //兼容单位部门岗位设置审批关系的情况
                    sb.append(" select null from t_wf_mainbody where twm.object_id=t_wf_mainbody.object_id and (upper("+Sql_switcher.substr("object_id", "1", "2")+") = 'UM' and not EXISTS (select null from b01 where 'UM'"+operate+"b01.b0110=upper(t_wf_mainbody.object_id)))");
                    sb.append(" union all select null from t_wf_mainbody where twm.object_id=t_wf_mainbody.object_id and (upper("+Sql_switcher.substr("object_id", "1", "2")+") = 'UN' and not EXISTS (select null from b01 where 'UN'"+operate+"b01.b0110=upper(t_wf_mainbody.object_id)))");
                    sb.append(" union all select null from t_wf_mainbody where twm.object_id=t_wf_mainbody.object_id and (upper("+Sql_switcher.substr("object_id", "1", "2")+") = '@K' and not EXISTS (select null from k01 where '@K'"+operate+"k01.e01a1=upper(t_wf_mainbody.object_id)))");
                    sb.append(")");
                    sb.append(" and relation_id='"+relationid+"' and actor_type='1'");
                    dao.update(sb.toString());

                    //删除主体
                    sb.setLength(0);
                    if(Sql_switcher.searchDbServer()==Constant.MSSQL) {
                        sb.append( "delete twm from t_wf_mainbody twm where exists (");
                    } else {
                        sb.append( "delete from t_wf_mainbody twm where exists (");
                    }
                    for (int i = 0; i < arrPre.length; i++) {
                        if (i>0) {
                            sb.append(" union all ");
                        }
                        sb.append(" select * from t_wf_mainbody where twm.mainbody_id=t_wf_mainbody.mainbody_id and ");
                        sb.append("( upper("+Sql_switcher.substr("mainbody_id", "1", "3")+") = '"+arrPre[i].toUpperCase()+"' "
                                //+arrPre[i].toUpperCase()+"' and upper(mainbody_id)  not in (select '"+arrPre[i].toUpperCase()+"' "+operate+" a0100 a0100 from "
                                //+arrPre[i].toUpperCase()+"a01))"); // 使用not exists代替not in，可以使得运行效率很大的提升 liuzy 20150715
                                +" and  not EXISTS (select null from "+arrPre[i].toUpperCase()+"a01 where '"+arrPre[i].toUpperCase()+"'"+operate
                                +arrPre[i].toUpperCase()+"a01.a0100=upper(t_wf_mainbody.mainbody_id)))");
                    }
                    sb.append(")");
                    sb.append(" and relation_id='"+relationid+"' and actor_type='1'");
                    dao.update(sb.toString());
                }
    		}else if("4".equals(actor_type)){//业务用户的审批关系
    			sb.append("delete from t_wf_mainbody where ( upper(mainbody_id) not in (select upper(username) username from operuser)  or upper(object_id) not in (select upper(username) username from operuser)) and relation_id='"+relationid+"' and actor_type='4'");
    			dao.update(sb.toString());
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public String getDepartment(String codeitemid){
        Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
        String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
        StringBuffer desc=new StringBuffer();
        //if (Integer.parseInt(display_e0122) == 0) {
        //处理display_e0122为空时，格式化错误
        if(PubFunc.chgNullInt(display_e0122)==0){
            desc.append(AdminCode.getCodeName("UM",codeitemid));
        } else {
            CodeItem item = AdminCode.getCode("UM", codeitemid,
                    Integer.parseInt(display_e0122));
            if (item != null) {
                desc.append(item.getCodename());
            } else {
                desc.append(AdminCode.getCodeName("UM", codeitemid));
            }

        }
        return desc.toString();
        
    }

}
