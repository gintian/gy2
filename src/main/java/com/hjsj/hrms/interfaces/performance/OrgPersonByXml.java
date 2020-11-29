package com.hjsj.hrms.interfaces.performance;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * <p>Title:OrgPersonByXml.java</p>
 * <p>Description:绩效手工选择通用树</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-07-17 10:05:18</p>
 * @author JinChunhai
 * @version 1.0
 */

public class OrgPersonByXml
{
	public String nbase;

	public String flag;

	public String id;

	public String opt;

	public UserView userview;

	public String planid;

	public int object_type;

	public String objWhere = "";

	public String plan_b0110 = "";
	
	public String khObjCopyed="";
	
	public String accordPriv="";
	
	public String oldPlan_id="";

	/**
	 * opt=0 考核实施/考核对象/手工选择 登录用户权限范围内的考核对象（包括选人和选机构）非公共资源计划还要在计划b0110内 
	 * opt=6 考核关系/手工选择考核对象 	   选择用户权限内的人 且不显示出已经存在的考核对象	 
	 * opt=1 考核实施/指定考核主体/手工选择 选择用户权限内的人（通过参数accordPriv来控制是否限制用户管理范围）
	 * opt=9 考核关系/指定考核主体/手工选择 选择用户权限内的人（通过参数accordPriv来控制是否限制用户管理范围）
	 * opt=2 绩效评估/显示/手工选择 选择计划中考核对象且在登录用户范围内的对象
	 * opt=5 考核表分发/复制主体给  选择计划中考核对象且在登录用户范围内的对象 且不包括被复制的考核对象
	 * opt=3 绩效实施/设置动态主体权重 选择计划中考核对象且在登录用户范围内的对象
	 * opt=4 绩效实施/设置动态指标权重 选择计划中考核对象且在登录用户范围内的对象
	 * opt=7 绩效实施/目标卡制定      选择计划中考核对象且在登录用户范围内的对象
	 * opt=8 自助服务/绩效考评/目标考核/目标卡制定/复制目标卡至     选择计划中考核对象且在登录用户范围内的对象 且不包括被复制的考核对象
	 * opt=10 绩效实施/设置动态指标权重 考核对象类别
	 * opt=12 人事异动/自定义审批流程 选择审批人 //wangrd 2013-12-19
	 * opt=13 审批关系
	 */
	public OrgPersonByXml(String flag, String id, String _opt, String _planid, UserView _userview)
	{
		this.planid = _planid;  
		this.nbase = "Usr";
		this.flag = flag;
		this.id = id;
		this.userview = _userview;
		this.opt = _opt;
		if ("12".equals(this.opt)){//人事异动
		    if ("-1".equals(flag)){	//首次 加载人员库列表	        
		        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
		        String usrbase="";
		        if(login_vo!=null) 
		            usrbase = login_vo.getString("str_value").toLowerCase();  
		        this.nbase = usrbase;
		    }
		    else {//从id参数中取得当前节点所在的人员库
		        String[] temps = this.id.split("`");		        
		        if (temps.length>0){
		            if ("0".equals(flag)){
		                this.nbase =temps[0]; 
		            }
		            else {
		                this.nbase =temps[1]; 
		            }
		        }
		    }
		}
		else if ("13".equals(this.opt)){//审批关系
			 if ("-1".equals(flag)){	//首次 加载人员库列表	        
			        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
			        String usrbase="";
			        if(login_vo!=null) 
			            usrbase = login_vo.getString("str_value").toLowerCase();  
			        this.nbase = usrbase;
		     }
		}

		Connection conn = null;
		try
		{
			conn = AdminDb.getConnection();
			PerformanceImplementBo pb=new PerformanceImplementBo(conn,this.userview,this.planid);	
			this.objWhere = pb.getPrivWhere(this.userview);// 根据用户权限先得到一个考核对象的范围
			
			if (this.planid != null && this.planid.length() > 0)
			{
				
				RecordVo vo = new RecordVo("per_plan");
				vo.setString("plan_id", planid);
				ContentDAO dao = new ContentDAO(conn);
				
				vo = dao.findByPrimaryKey(vo);
				object_type = vo.getInt("object_type");
				plan_b0110 = vo.getString("b0110");
				
			}
		} catch (Exception e)
		{
			e.printStackTrace();

		} finally {
			try {
								
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public String getKhObjCopyed()
	{
		return khObjCopyed;
	}

	/**考核表分发 被复制的考核对象*/
	public void setKhObjCopyed(String khObjCopyed)
	{
		this.khObjCopyed = khObjCopyed;
	}


	public String getAccordPriv() {
		return accordPriv;
	}

	/**考核实施和考核关系指定考核主体手工选人时候 可以设置是否按照登录用户的权限来显示组织结构树*/
	public void setAccordPriv(String accordPriv) {
		this.accordPriv = accordPriv;
	}
    /**代码类*/
    private String codesetid;
    /**代码项*/
    private String codeitemid;
    /*
     * 为了显示第一层为管理的最高层的节点
     * */
    private String isfirstnode;
    /**根据登录用户权限展示代码类型为单位 部门 职位的机构树*/
	public OrgPersonByXml(String _codesetid, String _codeitemid, String _isfirstnode, UserView _userview)
	{
		this.codeitemid=_codeitemid;
		this.codesetid=_codesetid;
		this.isfirstnode=_isfirstnode;
		this.userview=_userview;
	}
	 /**求查询代码的字符串*/
    private String getQueryString()
    {
        StringBuffer str=new StringBuffer();
        if("UN".equalsIgnoreCase(this.codesetid)|| "UM".equalsIgnoreCase(this.codesetid)|| "@K".equalsIgnoreCase(this.codesetid))
        {
            if("UN".equalsIgnoreCase(this.codesetid))
            {
                str.append("select codesetid,codeitemid,codeitemdesc,childid from organization where codesetid='");
                str.append(this.codesetid);
                str.append("'");
            }
            else if("UM".equalsIgnoreCase(this.codesetid))
            {
                str.append("select codesetid,codeitemid,codeitemdesc,childid from organization where (codesetid='");
                str.append(this.codesetid);
                str.append("' or codesetid='UN') ");
            }  
            else if ("@K".equalsIgnoreCase(this.codesetid))
            {
                str.append("select codesetid,codeitemid,codeitemdesc,childid from organization where (codesetid='");
                str.append(this.codesetid);
                str.append("' or codesetid='UN' or codesetid='UM') ");
            }               
        }
        else
        {
            str.append("select codesetid,codeitemid,codeitemdesc,childid from codeitem where codesetid='");
            str.append(this.codesetid);
            str.append("'");            
        }
        /**所有的第一层代码值列表*/
        if(this.isfirstnode!=null && "1".equals(this.isfirstnode))
        {
            str.append(this.getRootOrgNodeStr(this.userview, "HJSJ"));		
        }else
        {
	        if(this.codeitemid==null|| "".equals(this.codeitemid)|| "ALL".equals(this.codeitemid))
		     {
		          str.append(" and parentid=codeitemid");
		     }
		     else
		     {
		            str.append(" and parentid<>codeitemid and parentid='");
		            str.append(codeitemid);
		            str.append("'");
		     }
        } 

        if("UN".equalsIgnoreCase(this.codesetid)|| "UM".equalsIgnoreCase(this.codesetid)|| "@K".equalsIgnoreCase(this.codesetid))
        {
	          /**组织机构历史点控制-20091130*/
	        String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
	        str.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
	          //end.	      	
        	str.append(" ORDER BY a0000,codeitemid ");
        }
        return str.toString();
    }
    
    public String outCodeTree()throws GeneralException
    {
        StringBuffer xmls = new StringBuffer();
        StringBuffer strsql = new StringBuffer();
        ResultSet rset = null;
        Connection conn = AdminDb.getConnection();
        Element root = new Element("TreeNode");
        
        root.setAttribute("id","$$00");
        root.setAttribute("text","root");
        root.setAttribute("title","codeitem");
        Document myDocument = new Document(root);
        String theaction=null;
        try
        {          
          strsql.append(getQueryString());
          ContentDAO dao = new ContentDAO(conn);
          rset = dao.search(strsql.toString());
          while (rset.next())
          {
            Element child = new Element("TreeNode");
            String itemid=rset.getString("codeitemid");
            if(itemid==null)
            	itemid="";
            itemid=itemid.trim();
            String codesetid=rset.getString("codesetid");
            child.setAttribute("id", codesetid+itemid);            
            String codeitemdesc=rset.getString("codeitemdesc");            
            child.setAttribute("text", codeitemdesc);
            child.setAttribute("title", itemid+":"+codeitemdesc);
            if(!itemid.equalsIgnoreCase(rset.getString("childid")))            
            	child.setAttribute("xml", "/performance/kh_plan/get_code_treeinputinfo.jsp?codesetid=" + this.codesetid + "&isfirstnode=2&codeitemid="+itemid);
            if("UN".equals(codesetid))
            	child.setAttribute("icon","/images/unit.gif");
            else if("UM".equals(codesetid))
            	child.setAttribute("icon","/images/dept.gif");
            else if("@K".equals(codesetid))
            	child.setAttribute("icon","/images/pos_l.gif");
            else
            	child.setAttribute("icon","/images/table.gif");
            root.addContent(child);
          }

          XMLOutputter outputter = new XMLOutputter();
          Format format=Format.getPrettyFormat();
          format.setEncoding("UTF-8");
          outputter.setFormat(format);
          xmls.append(outputter.outputString(myDocument));
        }
        catch (SQLException ee)
        {
          ee.printStackTrace();
          GeneralExceptionHandler.Handle(ee);
        }
        finally
        {
          try
          {
            if (rset != null)
            {
              rset.close();
            }
            if (conn != null)
            {
              conn.close();
            }
          }
          catch (SQLException ee)
          {
            ee.printStackTrace();
          }
          
      }
      return xmls.toString();        
    }
    
	/**
	 * 取得节点下的信息
	 * 
	 * @return
	 */
	public ArrayList getList()
	{
		ArrayList list = new ArrayList();
		// DB相关
		ResultSet rs = null;
		Connection conn = null;
		try
		{
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			String bosdate = DateStyle.dateformat(new Date(), "yyyy-MM-dd");
			if ("-1".equals(flag))// 先显示人员库
			{
				String[] temps = this.nbase.split(",");
				String str = "";

				for (int i = 0; i < temps.length; i++)
				{
					if (temps[i].trim().length() > 0)
						str += ",'" + temps[i].toLowerCase() + "'";

				}
				if (str.length() > 0)
				{
					rs = dao.search("select * from dbname where lower(pre) in (" + str.substring(1) + ") order by dbid");
					while (rs.next())
					{
						LazyDynaBean abean = new LazyDynaBean();
						abean.set("flag", "0");
						abean.set("codeitemid", rs.getString("Pre") + "`0");
						abean.set("codeitemdesc", rs.getString("dbname"));
						list.add(abean);
					}
				}
			} else
			// 从组织机构开始显示
			{
				String sql = "";
				StringBuffer sql2 = new StringBuffer();
				String likeSQL = " parentid=codeitemid  ";

				if ("0".equals(this.flag))// 显示顶层组织机构
				{
					sql = "select * from organization where " + likeSQL + " and " + Sql_switcher.dateValue(bosdate) + " between start_date and end_date ";

					if ("0".equals(this.opt))// 考核实施/考核对象/手工选择 登录用户权限范围内的考核对象（包括选人和选机构）非公共资源计划还要在计划b0110内
					{
						// 考核实施/考核对象/手工选择 顶层机构显示计划所属机构确定的机构或者用户权限确定的机构
						sql = "select * from organization where 1=1 " + this.getRootOrgNodeStr(this.userview, this.plan_b0110);
					} else if((("1".equals(this.opt)  || "13".equals(this.opt)|| "9".equals(this.opt)|| "12".equals(this.opt)) && "true".equalsIgnoreCase(this.getAccordPriv())) || "6".equals(this.opt))// 考核实施/指定考核主体/手工选择 考核关系/指定考核主体/手工选择  考核关系/手工选择 选择用户权限内的人  人事异动
					{
						sql = "select * from organization where 1=1 "+this.getRootOrgNodeStr(this.userview, "HJSJ");					
					} else if ("2".equals(this.opt) || "5".equals(this.opt) || "8".equals(this.opt) || "3".equals(this.opt) || "4".equals(this.opt) || "7".equals(this.opt))// 绩效评估/显示/手工选择 绩效实施/设置动态主体权重 选择计划中考核对象且在登录用户范围内的对象
					{
						sql = "select * from organization where 1=1 " + this.getRootOrgNodeStr(this.userview, this.plan_b0110);
					}else if("10".equals(this.opt)){
						sql="select po.kh_relations,po.id,po.b0110,po.e0122,po.e01a1,po.object_id,po.a0101,po.body_id,pmb.name ";
						sql+=" from per_object po left join	per_mainbodyset pmb on  po.body_id=pmb.body_id ";
						sql+= " where pmb.name is not null and po.plan_id=" + this.planid;
					}else if("11".equals(this.opt)){
						sql="select po.kh_relations,po.id,po.b0110,po.e0122,po.e01a1,po.object_id,po.a0101,po.body_id,pmb.name ";
						sql+=" from per_object po left join	per_mainbodyset pmb on  po.body_id=pmb.body_id ";
						sql+= " where pmb.name is not null and po.plan_id=" + this.planid;
					}
					if(!"10".equals(this.opt)&&!"11".equals(this.opt))
					sql += " order by a0000,codeitemid";
					rs = dao.search(sql);
					if(!"10".equals(this.opt)&&!"11".equals(this.opt)){
					while (rs.next())
					{
						LazyDynaBean abean = new LazyDynaBean();
						abean.set("flag", rs.getString("codesetid"));
						abean.set("codeitemid", rs.getString("codeitemid") + "`" + this.nbase + "`" + rs.getString("codeitemdesc") + "`" + rs.getString("codesetid"));
						abean.set("codeitemdesc", rs.getString("codeitemdesc"));
						list.add(abean);
					}
					}else{
						HashMap map = new HashMap();
					//rs = stmt.executeQuery(sql);
					
					while (rs.next())
					{   
						
						if(map.get(rs.getString("body_id"))==null){//去重复类别
						String codeitemid = rs.getString("body_id");						
                        
						LazyDynaBean abean = new LazyDynaBean();
						abean.set("flag","lb");
						abean.set("codeitemid", rs.getString("body_id") + "`" + this.nbase + "`" + rs.getString("name") + "`" + "lb");
						abean.set("codeitemdesc", rs.getString("name"));
						list.add(abean);
						map.put( rs.getString("body_id"),"1");
						}
					}
					}
				} else
				// 显示中间组织机构
				{
					String[] temps = this.id.split("`");
					String _codeitemid = temps[0];
					int _len = _codeitemid.length();
					sql = "select * from organization where parentid='" + temps[0] + "' and parentid<>codeitemid and " + Sql_switcher.dateValue(bosdate) + " between start_date and end_date ";
					sql += " order by a0000,codeitemid";

					if ("2".equals(this.opt) || "5".equals(this.opt) || "8".equals(this.opt) || "3".equals(this.opt) || "4".equals(this.opt) || "7".equals(this.opt))// 绩效评估/显示/手工选择 绩效实施/设置动态主体权重 选择计划中考核对象且在登录用户范围内的对象
					{

						HashMap map = new HashMap();// 可显示的机构节点
						String sql0 = "select  b0110,e0122,e01a1 from per_object where plan_id=" + this.planid + " " + this.objWhere;
						if("5".equals(this.opt))//zzk 2013-11-28 自助平台/绩效考评/考核实施 复制考核主体到此功能选择考核对象的时候过滤掉当前复制的这个对象
							sql0 = "select  b0110,e0122,e01a1 from per_object where plan_id=" + this.planid + " " + this.objWhere+" and object_id <>'"+khObjCopyed+"'";
						rs = dao.search(sql0);
						while (rs.next())
						{
							if (rs.getString(1) != null)
							{
								String temp = rs.getString(1);
								if (temp.length() > _len && temp.substring(0, _len).equalsIgnoreCase(_codeitemid))
									map.put(temp, "");
							}

							if (rs.getString(2) != null)
							{
								String temp = rs.getString(2);
								if (temp.length() > _len && temp.substring(0, _len).equalsIgnoreCase(_codeitemid))
									map.put(temp, "");
							}
							if ((rs.getString(3) != null) && (!"7".equals(this.opt)) && (!"2".equals(this.opt)))
							{
								String temp = rs.getString(3);
								if (temp.length() > _len && temp.substring(0, _len).equalsIgnoreCase(_codeitemid))
									map.put(temp, "");
							}
						}

						HashMap parentMap = new HashMap();
						sql0 = "select  codeitemid,parentid from organization ";
						rs = dao.search(sql0);
						while (rs.next())
							parentMap.put(rs.getString(1), rs.getString(2));

						Set keyset = map.keySet();
						HashMap midOrgs = new HashMap();
						for (Iterator iter = keyset.iterator(); iter.hasNext();)
						{
							String org = (String) iter.next();
							String parentid = (String) parentMap.get(org);
							while (parentid!=null && !parentid.equalsIgnoreCase(_codeitemid) && map.get(parentid) == null)
							{
								midOrgs.put(parentid, "");// 添加中间机构节点
								org = parentid;
								parentid = (String) parentMap.get(org);
							}
						}

						rs = dao.search(sql);
						while (rs.next())
						{
							String codeitemid = rs.getString("codeitemid");

							if (map.get(codeitemid) == null && midOrgs.get(codeitemid) == null)
								continue;

							LazyDynaBean abean = new LazyDynaBean();
							abean.set("flag", rs.getString("codesetid"));
							abean.set("codeitemid", rs.getString("codeitemid") + "`" + this.nbase + "`" + rs.getString("codeitemdesc") + "`" + rs.getString("codesetid"));
							abean.set("codeitemdesc", rs.getString("codeitemdesc"));
							list.add(abean);
						}
					} else if ("0".equals(this.opt))// 考核实施/考核对象/手工选择 登录用户权限范围内的考核对象（包括选人和选机构）非公共资源计划还要在计划b0110内
					{
						HashMap map = new HashMap();
						String sql0 = "select codeitemid from organization where 1=1 " + this.getOrgWhere(this.userview, this.plan_b0110);
						if (this.object_type != 2)
							sql0 += " and codesetid in ('UN','UM') ";
						 
						rs = dao.search(sql0);
						while (rs.next())
						{
							map.put(rs.getString(1), "");
						}
						rs = dao.search(sql);
						while (rs.next())
						{
							String codeitemid = rs.getString("codeitemid");
							if (map.get(codeitemid) == null)
								continue;
							LazyDynaBean abean = new LazyDynaBean();
							abean.set("flag", rs.getString("codesetid"));
							abean.set("codeitemid", rs.getString("codeitemid") + "`" + this.nbase + "`" + rs.getString("codeitemdesc") + "`" + rs.getString("codesetid"));
							abean.set("codeitemdesc", rs.getString("codeitemdesc"));
							list.add(abean);
						}
					} else if("1".equals(this.opt)|| "13".equals(this.opt) || "9".equals(this.opt) || "6".equals(this.opt))// 考核实施/指定考核主体/手工选择 考核关系/指定考核主体/手工选择 考核关系/手工选择 选择用户权限内的人
					{
						HashMap map = new HashMap();
						String sql0 = "select codeitemid from organization where 1=1 ";
						if((("1".equals(this.opt) || "13".equals(this.opt) || "9".equals(this.opt)) && "true".equalsIgnoreCase(this.getAccordPriv())) || "6".equals(this.opt))
							sql0+=this.getOrgWhere(this.userview, "HJSJ");
						rs = dao.search(sql0);
						while (rs.next())
						{
							map.put(rs.getString(1), "");
						}
						rs = dao.search(sql);
						while (rs.next())
						{
							String codeitemid = rs.getString("codeitemid");
							if (map.get(codeitemid) == null)
								continue;							

							LazyDynaBean abean = new LazyDynaBean();
							abean.set("flag", rs.getString("codesetid"));
							abean.set("codeitemid", rs.getString("codeitemid") + "`" + this.nbase + "`" + rs.getString("codeitemdesc") + "`" + rs.getString("codesetid"));
							abean.set("codeitemdesc", rs.getString("codeitemdesc"));
							list.add(abean);
						}
					}
					else if("12".equals(this.opt) )//
                    {
					    /*
                        HashMap map = new HashMap();
                        String sql0 = "select codeitemid from organization where 1=1 ";
                        if(((this.opt.equals("1") || this.opt.equals("9")) && this.getAccordPriv().equalsIgnoreCase("true")) || this.opt.equals("6"))
                            sql0+=this.getOrgWhere(this.userview, "HJSJ");
                        rs = stmt.executeQuery(sql0);
                        while (rs.next())
                        {
                            map.put(rs.getString(1), "");
                        }
                        */
                        rs = dao.search(sql);
                        while (rs.next())
                        {
                            String codeitemid = rs.getString("codeitemid");
                           /* if (map.get(codeitemid) == null)
                                continue;   
                                */                        

                            LazyDynaBean abean = new LazyDynaBean();
                            abean.set("flag", rs.getString("codesetid"));
                            abean.set("codeitemid", rs.getString("codeitemid") + "`" + this.nbase + "`" + rs.getString("codeitemdesc") + "`" + rs.getString("codesetid"));
                            abean.set("codeitemdesc", rs.getString("codeitemdesc"));
                            list.add(abean);
                        }
                    }
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (rs != null)
				{
					rs.close();
				}
				if (conn != null)
				{
					conn.close();
				}
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		boolean canSelEmp = true;
		if ("2".equals(this.opt) || "5".equals(this.opt) || "8".equals(this.opt) || "3".equals(this.opt) || "4".equals(this.opt) || "7".equals(this.opt) || "0".equals(this.opt))// 绩效评估/显示/手工选择 绩效实施/设置动态主体权重  考核实施/考核对象/手工选择
		{
			if (this.object_type != 2)
				canSelEmp = false;
		}

		if (canSelEmp && !"0".equals(this.flag) && !"-1".equals(this.flag)&&!"10".equals(this.opt)&&!"11".equals(this.opt))
		{
			String[] temps = this.id.split("`");
			ArrayList personList = getPersonList(temps[0], temps[3]);
			list.addAll(personList);
		}

		return list;
	}

	/**
	 * 取得机构下的人员信息
	 * 
	 * @param codeitemid
	 * @param codesetid
	 * @return
	 */
	public ArrayList getPersonList(String codeitemid, String codesetid)
	{
		ArrayList list = new ArrayList();
		// DB相关
		ResultSet rs = null;
		Connection conn = null;
		try
		{

			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
			String cloumn = "";
			
			if(onlyname!=null && onlyname.trim().length()>0 && !"#".equals(onlyname))
			{			
				FieldItem fielditem = DataDictionary.getFieldItem(onlyname);
				String useFlag = fielditem.getUseflag(); 
				if("0".equalsIgnoreCase(useFlag))
					throw new GeneralException("定义的唯一性指标未构库,请构库后再进行此操作！");	
				
				cloumn = "," + onlyname;
			}
			
			HashMap e01a1NullMap = new HashMap();
			String sql = "";
			if ("UN".equalsIgnoreCase(codesetid))
			{
				sql = "select a0100,a0101" + cloumn + " from " + this.nbase + "A01 where b0110='" + codeitemid + "' and ( e0122 is null or e0122='' ) ";//and ( e01a1 is null or e01a1='' ) 
			} else if ("UM".equalsIgnoreCase(codesetid))
			{
				if("7".equals(this.opt) || "2".equals(this.opt))
				{
					sql = "select a0100,a0101" + cloumn + " from " + this.nbase + "A01 where e0122='" + codeitemid + "' ";
				}else
				{
				//	sql = "select a0100,a0101" + cloumn + " from " + this.nbase + "A01 where e0122='" + codeitemid + "' and ( e01a1 is null or e01a1='' ) ";
					sql = "select a0100,e01a1,a0101" + cloumn + " from " + this.nbase + "A01 where e0122='" + codeitemid + "' ";
					
					// 获得部门下的岗位信息  JinChunhai 2012.10.30  解决岗位已经撤销但人员还挂在此岗位下
					e01a1NullMap = getE01a1NullMap(codeitemid);					
				}
			} else if ("@K".equalsIgnoreCase(codesetid))
			{
				sql = "select a0100,a0101" + cloumn + " from " + this.nbase + "A01 where e01a1='" + codeitemid + "' ";
			}

			if ("5".equals(this.opt) || "8".equals(this.opt) || "3".equals(this.opt) || "4".equals(this.opt) || "7".equals(this.opt))// 绩效评估/显示/手工选择 绩效实施/设置动态主体权重 选择计划中考核对象且在登录用户范围内的对象
				sql += " and a0100  in (select object_id from per_object where plan_id=" + this.planid + this.objWhere + ")";
			if ("2".equals(this.opt))
				sql += " and a0100  in (select object_id from per_object where plan_id=" + this.planid + this.objWhere + " and object_id in (select object_id from per_object where plan_id=" + this.oldPlan_id + " " + this.objWhere + ")) ";
			else if ("0".equals(this.opt))// 考核实施/考核对象/手工选择 登录用户权限范围内的考核对象（包括选人和选机构）非公共资源计划还要在计划b0110内
			{
				sql += this.objWhere;
				if (!"HJSJ".equalsIgnoreCase(this.plan_b0110))
				{
					if (AdminCode.getCode("UM", this.plan_b0110) != null)
						sql += " and e0122 like '" + this.plan_b0110 + "%'";
					if (AdminCode.getCode("UN", this.plan_b0110) != null)
						sql += " and b0110 like '" + this.plan_b0110 + "%'";
				}
			} else if((("1".equals(this.opt)|| "13".equals(this.opt) || "9".equals(this.opt)) && "true".equalsIgnoreCase(this.getAccordPriv())) || "6".equals(this.opt))// 考核实施/指定考核主体/手工选择 考核关系/指定考核主体/手工选择 考核关系/手工选择 选择用户权限内的人
			{
				sql += this.objWhere;
			}

			if ("0".equals(this.opt))
			{
				sql +=" and a0100 not in (select object_id from per_object where plan_id="+this.planid+")";
			}
			else if("6".equals(this.opt))
			{
				sql +=" and a0100 not in (select object_id from per_object_std )";
			}
			
			sql += " order by a0000";

			rs = dao.search(sql);
			FieldItem item = DataDictionary.getFieldItem(onlyname);
			while (rs.next())
			{
				if ("UM".equalsIgnoreCase(codesetid))
				{
					if("7".equals(this.opt) || "2".equals(this.opt))
					{
						
					}else
					{
						String e01a1 = rs.getString("e01a1") == null ? "" : rs.getString("e01a1");						
						if(e01a1NullMap!=null && e01a1NullMap.get(e01a1)!=null)
							continue;																			
					}
				}				
				
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("flag", "p");

				abean.set("codeitemdesc", rs.getString("a0101") == null ? "" : rs.getString("a0101"));
				String value = "";
				String str = "";
				if (onlyname != null && !"".equals(onlyname))
				{
					value = rs.getString(onlyname) == null ? "" : rs.getString(onlyname);
					if (item != null)
					{
						if ("A".equalsIgnoreCase(item.getItemtype()) && !"0".equalsIgnoreCase(item.getCodesetid()))
						{
							value = AdminCode.getCodeName(item.getCodesetid(), value);
						}
					}
					if (value != null && !"".equals(value))
						str = "(" + value + ")";
				}
				abean.set("codeitemid", rs.getString("a0100") + "`" + this.nbase + "`" + (rs.getString("a0101") == null ? "" : rs.getString("a0101")) + str + "`p");
				abean.set("onlyvalue", value);
				list.add(abean);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (rs != null)
				{
					rs.close();
				}
				if (conn != null)
				{
					conn.close();
				}
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}

	public String outPutXml() throws GeneralException
	{
		Connection conn = null;
		ResultSet rs = null;
		StringBuffer xmls = new StringBuffer();
		try
		{
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);

			HashMap tempMap = new HashMap();// 可选择的考核对象
			if ("2".equals(this.opt) || "5".equals(this.opt))// 绩效评估/显示/手工选择 选择计划中考核对象且在登录用户范围内的对象
			{
				String sql0 = "";
				if("2".equals(this.opt))
					sql0 = "select object_id from per_object where plan_id=" + this.planid + " " + this.objWhere + " and object_id in (select object_id from per_object where plan_id=" + this.oldPlan_id + " " + this.objWhere + ") " ;
				else
					sql0 = "select object_id from per_object where plan_id=" + this.planid + " " + this.objWhere +" and object_id <> '"+khObjCopyed+"'";//zzk 2013-11-28 自助平台/绩效考评/考核实施 复制考核主体到此功能选择考核对象的时候过滤掉当前复制的这个对象
				rs = dao.search(sql0);				
				while (rs.next())
				{
					String object = rs.getString("object_id");
					tempMap.put(object, "");
				}
			}
			if ("8".equals(this.opt)) // 目标卡制定 复制目标卡对象 只显示起草和驳回状态的目标卡信息  JinChunhai 2010.11.23
			{
				String sql0 = "select  object_id from per_object where plan_id=" + this.planid + " " + this.objWhere +" and (sp_flag not in(01,07) and sp_flag is not null)";
				rs = dao.search(sql0);
				while (rs.next())
					tempMap.put(rs.getString(1), "");
			}

			HashMap existObjsMap = this.getExistObjs();//已经存在的考核对象 （选择是不出现在被选择的对象中）			
			
			// 生成的XML文件
			// 创建xml文件的根元素
			Element root = new Element("TreeNode");
			// 设置根元素属性
			root.setAttribute("id", "");
			root.setAttribute("text", "root");
			root.setAttribute("title", "organization");
			// 创建xml文档自身
			Document myDocument = new Document(root);
			// 设置跳转字符串
			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
			FieldItem item = DataDictionary.getFieldItem(onlyname);
			ArrayList list = getList();
			for (Iterator t = list.iterator(); t.hasNext();)
			{
				LazyDynaBean abean = (LazyDynaBean) t.next();

				// 创建子元素
				Element child = new Element("TreeNode");
				// 设置子元素属性
				String codeitemid = (String) abean.get("codeitemid");
				String codeitemdesc = (String) abean.get("codeitemdesc");
				String aflag = (String) abean.get("flag");

				String sttr = codeitemdesc;
				child.setAttribute("id", codeitemid);

				if ("UN".equalsIgnoreCase(aflag))
					child.setAttribute("icon", "/images/unit.gif");
				else if ("UM".equalsIgnoreCase(aflag))
					child.setAttribute("icon", "/images/dept.gif");
				else if ("@K".equalsIgnoreCase(aflag))
					child.setAttribute("icon", "/images/pos_l.gif");
				else if ("lb".equalsIgnoreCase(aflag))
					child.setAttribute("icon", "/images/pos_l.gif");
				else if ("p".equals(aflag))
				{
					if (item != null && abean.get("onlyvalue") != null && !"".equals((String) abean.get("onlyvalue")))
					{
						sttr += "(" + (String) abean.get("onlyvalue") + ")";
					}
					child.setAttribute("icon", "/images/man.gif");
					// type = "true";
				} else
					child.setAttribute("icon", "/images/add_all.gif");

				// 设置可选对象
				String type = "false";
				if ("2".equals(this.opt) || "5".equals(this.opt))// 绩效评估/显示/手工选择 选择计划中考核对象且在登录用户范围内的对象
				{
					String[] temp = codeitemid.split("`");
					String orgid = temp[0];
					
					int cott = 0;
					Set keySet=tempMap.keySet();
					java.util.Iterator iterator=keySet.iterator(); 
			        while (iterator.hasNext())
			        {   			        
			        	String strKey = (String)iterator.next();  //键值	    
						String strValue = (String)tempMap.get(strKey);   //value值   
						cott++;
			        }  					
					if (tempMap.get(orgid) != null)
						type = "true";
					if((orgid.equals(this.khObjCopyed)) && (cott<=1))
						continue;

				} else if ("0".equals(this.opt))// 考核实施/考核对象/手工选择 登录用户权限范围内的考核对象（包括选人和选机构）非公共资源计划还要在计划b0110内
				{
					if (this.object_type == 2 && "p".equals(aflag))
						type = "true";
					else if (this.object_type == 1 && ("UN".equalsIgnoreCase(aflag) || "UM".equalsIgnoreCase(aflag)))
						type = "true";
					else if (this.object_type == 3 && "UN".equalsIgnoreCase(aflag))
						type = "true";
					else if (this.object_type == 4 && "UM".equalsIgnoreCase(aflag))
						type = "true";
					
					if (this.object_type == 3 && "UM".equalsIgnoreCase(aflag))//选择单位 部门节点不出来
						continue;
					
				} else if ("1".equals(this.opt)  || "13".equals(this.opt)|| "9".equals(this.opt) || "6".equals(this.opt)|| "12".equals(this.opt))// 考核实施/指定考核主体/手工选择  考核关系/指定考核主体/手工选择 考核关系/手工选择 选择用户权限内的人
				{
					if ("p".equals(aflag))
						type = "true";
				} else if ("8".equals(this.opt) )
				{
					String[] temp = codeitemid.split("`");
					String orgid = temp[0];
					if (tempMap.get(orgid) != null)				
						continue;
					if("01".equalsIgnoreCase(this.khObjCopyed))
					{
						if (existObjsMap.get(orgid) != null)					
							type = "false";
					}else{
						if(orgid.equals(this.khObjCopyed))
							continue;
					}					
					if("01".equals(orgid))
					{
						if (existObjsMap.get(orgid) != null)					
							type = "false";
					}else{
						if (existObjsMap.get(orgid) != null)					
							type = "true";
					}
				}
				
				if ("0".equals(this.opt)  || "6".equals(this.opt))// 考核实施/考核对象/手工选择 考核关系/手工选择 选择用户权限内的人
				{
					String[] temp = codeitemid.split("`");
					String orgid = temp[0];
					if (existObjsMap.get(orgid) != null)					
						type = "false";
				}
					
				child.setAttribute("type", type);

				child.setAttribute("text", sttr);
				child.setAttribute("title", sttr);
				child.setAttribute("target", "_self");
				String a_nbase = "";
				if ("-1".equals(this.flag))// 点击人员库
					a_nbase = codeitemid.split("`")[0];
				else
					// 点击组织机构
					a_nbase = codeitemid.split("`")[1];
				String a_xml = "/performance/kh_plan/handImportObjs.jsp?flag=" + aflag + "&id=" + URLEncoder.encode(SafeCode.encode(codeitemid),"GBK") + "&nbase=" + a_nbase + "&planid=" + this.planid + "&opt=" + this.opt;
				if("5".equals(this.opt) || "8".equals(this.opt))
					a_xml+="&khObjCopyed="+this.khObjCopyed;
				else if("2".equals(this.opt))
					a_xml+="&oldPlan_id="+this.oldPlan_id;
				else if("1".equals(this.opt) || "13".equals(this.opt)|| "9".equals(this.opt)|| "12".equals(this.opt))
					a_xml+="&accordPriv="+this.accordPriv;

				//考核主题类别没必要加载下级 haosl 2019年6月21日 bug 49173
				if (!"p".equals(aflag) && !"lb".equals(aflag))
					child.setAttribute("xml", a_xml);
				String theaction="";
				
				String[]  temp=codeitemid.split("`");
				String code="";
				if ("-1".equals(this.flag)){//显示人员库 wangrd 2013-12-19				    
				    code = temp[1]+temp[0];		
				}
				else {				    
				     code = temp[3]+temp[0];		
				}
	
				if("3".equals(this.opt) || "4".equals(this.opt))//绩效实施/设置动态主体权重 绩效实施/设置动态指标权重 绩效实施/制定目标卡  触发显示选中节点的权重界面
				{								
					if("3".equals(this.opt))
						theaction="/performance/implement/kh_mainbody/set_dyna_main_rank/searchdynamainbodypropotion.do?b_search=link";
					else if("4".equals(this.opt))
						theaction="/performance/implement/kh_object/set_dyna_target_rank/searchdynatargetpropotion.do?b_search=link";
					
					theaction+="&planid=" + this.planid+"&codeid="+code;
					child.setAttribute("href", theaction);
					child.setAttribute("target", "mil_body");
				}else if("7".equals(this.opt))
				{
					if(existObjsMap.get(temp[0])!=null)
					{
						theaction="/performance/implement/performanceImplement/targetCardSet.do?b_query=link";
						theaction+="&planid=" + this.planid+"&codeid="+code;
					}
					else
//						theaction="/performance/implement/kh_mainbody/set_dyna_main_rank/welcome.html";
						theaction="javascropt:void(0)";
					child.setAttribute("href", theaction);
					child.setAttribute("target", "mil_body");
				}
				else if("10".equals(this.opt)){
					theaction="/performance/implement/kh_object/set_dyna_target_rank/searchdynatargetpropotion.do?b_search=link";
					theaction+="&planid=" + this.planid+"&codeid="+code;	
					child.setAttribute("href", theaction);
					child.setAttribute("target", "mil_body");
				}else if("11".equals(this.opt)){
					theaction="/performance/implement/kh_mainbody/set_dyna_main_rank/searchdynamainbodypropotion.do?b_search=link";
					theaction+="&planid=" + this.planid+"&codeid="+code;	
					child.setAttribute("href", theaction);
					child.setAttribute("target", "mil_body");
				}
				
				
				// 将子元素作为内容添加到根元素
				root.addContent(child);
			}
			XMLOutputter outputter = new XMLOutputter();
			// 格式化输出类
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);

			// 将生成的XML文件作为字符串形式
			xmls.append(outputter.outputString(myDocument));
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (rs != null)
				{
					rs.close();
				}
				if (conn != null)
				{
					conn.close();
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		// System.out.println(xmls.toString());
		return xmls.toString();
	}

	/**
	 * 根据用户权限和计划所属机构 获得机构的范围 先看操作单位 再看管理范围
	 * 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
	 */
	public String getOrgWhere(UserView userView, String plan_b0110)
	{
		String str = "";
		String logo = "false";
		String sgin = "false";
//		if (!userView.isSuper_admin())
		{
			StringBuffer buf = new StringBuffer();
			String operOrg = userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			if("12".equals(this.opt)){//人事异动			    
			    operOrg=this.userview.getUnitIdByBusi("8");
			}
			if (operOrg!=null && operOrg.length() > 3)
			{
				StringBuffer tempSql = new StringBuffer("");
				String[] temp = operOrg.split("`");
				for (int i = 0; i < temp.length; i++)
				{
					if ("UN".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or  codeitemid like '" + temp[i].substring(2) + "%'");
					else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or  codeitemid like '" + temp[i].substring(2) + "%'");
				}
				buf.append(" and ( " + tempSql.substring(3) + " ) ");
				
			}
			else if((!userView.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
			{
				String code = "-1";
				if (userView.getManagePrivCodeValue() != null && userView.getManagePrivCodeValue().length() > 0)// 管理范围
				{
					code = userView.getManagePrivCodeValue();
					if ("UN".equalsIgnoreCase(code))
					{
						code = "-1";
						sgin = "true";
					}
				}
				if (!"-1".equals(code))
				{
					if (AdminCode.getCodeName("UN", code) != null && AdminCode.getCodeName("UN", code).length() > 0)
						buf.append(" and codeitemid like '" + code + "%'");
					else if (AdminCode.getCodeName("UM", code) != null && AdminCode.getCodeName("UM", code).length() > 0)
						buf.append(" and codeitemid like '" + code + "%'");
				}else if("false".equalsIgnoreCase(sgin))
					logo = "true";
			}
			str += buf.toString();
		}
		if (!"HJSJ".equalsIgnoreCase(plan_b0110))
			str += " and codeitemid like '" + this.plan_b0110 + "%' ";
		else if("true".equalsIgnoreCase(logo))
			str += " and 1=1 ";
		return str;
	}

	/** 展示顶层机构范围用等于 */
	public String getRootOrgNodeStr(UserView userView, String plan_b0110)
	{
		String temp = "";
		StringBuffer rootOrgid = new StringBuffer();
		String sql = "select codeitemid from organization where codesetid in ('UM','UN') " + this.getOrgWhere(userView, plan_b0110);

		String sqlstr = this.getOrgWhere(userView, plan_b0110);
		if(!"and 1=1".equalsIgnoreCase(sqlstr.trim()))
		{
			ArrayList list = new ArrayList();
			ResultSet rs = null;
			Connection conn = null;
			try
			{
				conn = AdminDb.getConnection();
				ContentDAO dao = new ContentDAO(conn);
				String bosdate = DateStyle.dateformat(new Date(), "yyyy-MM-dd");
				sql += " and " + Sql_switcher.dateValue(bosdate) + " between start_date and end_date  order by a0000,codeitemid";
				rs = dao.search(sql);
				while (rs.next())
					list.add(rs.getString(1));
	
				for (int i = list.size() - 1; i > 0; i--)
				{
					String x = (String) list.get(i);
					boolean isRoot = true;
					for (int j = i - 1; j >= 0; j--)
					{
						String y = (String) list.get(j);
						if (x.length() >= y.length() && x.substring(0, y.length()).equalsIgnoreCase(y))// x包含y
						{
							isRoot = false;
							break;
						}
					}
					if (!isRoot)// 不是根元素 设置这个元素为null
						list.set(i, null);
				}
	
				for (int i = 0; i < list.size(); i++)
				{
					if (list.get(i) != null)
					{
						String x = (String) list.get(i);
						rootOrgid.append(" or codeitemid='" + x + "' ");
					}
				}
	
			} catch (Exception e)
			{
				e.printStackTrace();
			} finally
			{
				try
				{
					if (rs != null)
					{
						rs.close();
					}
					if (conn != null)
					{
						conn.close();
					}
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
	
			}
			
			if (rootOrgid.length() > 0)
				temp = " and (" + rootOrgid.substring(3) + ")";
		}else{
			temp = " and codeitemid=parentid ";
		}
		
		return temp;
	}
	/**获得已经存在的考核对象
	 * opt=0 考核实施/考核对象/手工选择 登录用户权限范围内的考核对象（包括选人和选机构）非公共资源计划还要在计划b0110内 
	 * opt=6 考核关系/手工选择考核对象	   选择用户权限内的人 
	 *  */
	public HashMap getExistObjs()
	{
		HashMap map = new HashMap();
		String sql ="";
		if("0".equals(this.opt)|| "7".equals(this.opt)|| "8".equals(this.opt))
			sql = "select object_id from per_object where plan_id="+this.planid;
		else if("6".equals(this.opt))
			sql = "select object_id from per_object_std ";
		else
			return map;
		
		ResultSet rs = null;
		Connection conn = null;
		try
		{
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			while (rs.next())
				map.put(rs.getString(1), "");
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (rs != null)
				{
					rs.close();
				}
				if (conn != null)
				{
					conn.close();
				}
			} catch (SQLException e)
			{
				e.printStackTrace();
			}

		}

		return map;
	}
	
	/**
	 * 获得部门下的岗位信息 
	 */
	public HashMap getE01a1NullMap(String codeitemid)
	{
		HashMap map = new HashMap();				
		ResultSet rs = null;
		Connection conn = null;
		try
		{
			String sql = "select codeitemid from organization where codesetid = '@K' and parentid = '" + codeitemid + "' ";
			
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			while (rs.next())
			{
				map.put(rs.getString("codeitemid"), "1");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (rs != null)
				{
					rs.close();
				}
				if (conn != null)
				{
					conn.close();
				}
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return map;
	}
	
	
	public String getOldPlan_id() {
		return oldPlan_id;
	}

	public void setOldPlan_id(String oldPlan_id) {
		this.oldPlan_id = oldPlan_id;
	}
	
}
