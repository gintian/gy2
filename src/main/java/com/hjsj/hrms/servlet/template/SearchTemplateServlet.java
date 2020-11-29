package com.hjsj.hrms.servlet.template;

import com.hjsj.hrms.businessobject.sys.options.param.SubsysOperation;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.RowSet;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Title:SearchTemplateServlet</p>
 * <p>Description:查询业务模板列表</p> 
 * <p>Company:hjsj</p> 
 * create time at:Sep 26, 20061:31:11 PM
 * @author chenmengqing
 * @version 4.0
 */
public class SearchTemplateServlet extends HttpServlet {

	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		doPost(arg0, arg1);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String type=(String)req.getParameter("type");
		String module=(String)req.getParameter("module");
		String res_flag=(String)req.getParameter("res_flag");
		String sorttype = (String)req.getParameter("sorttype");
		String history=(String)req.getParameter("history");
		//人事异动历史数据
		String history_param = "";
		if(history!=null&& "2".equals(history))
			history_param="&history=2";
		
    	StringBuffer treexml = new StringBuffer();	
        req.setCharacterEncoding("GBK");
		try {
				if(sorttype==null)
					treexml.append(loadAllNodes(type,module,res_flag,req, history_param));
				else
					treexml.append(loadSortNodes(type,res_flag,req));
		} catch (Exception e) {
			e.printStackTrace();
		}			
		resp.setContentType("text/xml;charset=UTF-8");
		resp.getWriter().println(treexml.toString());           
	}
	/**
	 * 装载业务模板节点,分业务类型加载模板
	 * =1,国家机关
	 * =2,事业单位
	 * =3,企业单位
	 * =4,军队使用
	 * =5,其    它
	 * @param type   1:人事异动2：薪资变动3：警衔4：法官8：保险
	 * @param module //标识业务子类号
	 * @return
	 */
	private String loadNodes(String type,String module,String res_flag,HttpServletRequest req)
	{
		StringBuffer strcontent=new StringBuffer();
		StringBuffer strsql=new StringBuffer();
		String href=(String)req.getParameter("href");
		boolean bflag=true;
		if(href==null|| "".equals(href))//未定义此参数时,控制是否要超链接操作
			bflag=false;
		
		 
		Connection conn = null;	
		RowSet rset=null;
        UserView userView = (UserView) req.getSession().getAttribute(WebConstant.userView);	
		try
		{
			conn = (Connection) AdminDb.getConnection();
			ContentDAO dao=new ContentDAO(conn);
			String unit_type=null;
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
			unit_type=sysbo.getValue(Sys_Oth_Parameter.UNITTYPE,"type");
			if(unit_type==null|| "".equals(unit_type))
				unit_type="3";
			if(type!=null&&("3".equals(type)|| "4".equals(type)|| "5".equals(type)|| "6".equals(type)))
				unit_type ="0";
			String operationcode=sysbo.getValue(Sys_Oth_Parameter.GOBROAD,"operationcode");

	        Element root = new Element("TreeNode");
	        root.setAttribute("id","$$00");
	        root.setAttribute("text","root");
	        root.setAttribute("title","root");
	        Document myDocument = new Document(root);
	        
			if("-1".equals(module))
			{
				String _static="static";
				if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
					_static="static_o";
				}
				strsql.append("select distinct a.operationcode,b.operationname ,operationid from ");
				strsql.append("template_table a ,operation b where a.operationcode=b.operationcode and b."+_static+"=");
				strsql.append(type);
				strsql.append(" and (");
				String units[]=unit_type.split(",");
				for(int i=0;i<units.length;i++)
				{
					strsql.append("a.flag ="+Integer.parseInt(units[i]));
					if(i<units.length-1)
						strsql.append(" or ");
				}			
				strsql.append(")");
				strsql.append(" order by operationid");
			}
			else
			{
				strsql.append("select tabid,name from template_table where operationcode='");
				strsql.append(module);
				strsql.append("' and (");
				String units[]=unit_type.split(",");
				for(int i=0;i<units.length;i++)
				{
					strsql.append(" flag ="+Integer.parseInt(units[i]));
					if(i<units.length-1)
						strsql.append(" or ");
				}			
				strsql.append(")");
			}
			rset=dao.search(strsql.toString());			
			while(rset.next())
			{
			
	            Element child = new Element("TreeNode");
	            /**业务分类*/
	            if("-1".equals(module))
	            {
            		if (!userView.isHaveResource(Integer.parseInt(res_flag), rset.getString("operationcode")))
            			continue;	
            		if(!bflag&&operationcode.equals(rset.getString("operationcode")))
            			continue;
		            child.setAttribute("id", rset.getString("operationcode"));
		            child.setAttribute("text", rset.getString("operationname"));
		            child.setAttribute("title", rset.getString("operationname"));
	            	if(!bflag)
	            		child.setAttribute("xml","/template/search_template?module="+rset.getString("operationcode")+"&type="+type+"&res_flag="+res_flag);
            		else
	            		child.setAttribute("xml","/template/search_template?module="+rset.getString("operationcode")+"&type="+type+"&res_flag="+res_flag+"&href=1");
	    			child.setAttribute("icon","/images/open.png");
		            root.addContent(child);
	            }
	            else/**列出模板*/
	            {
	            	
            		/*if (!userView.isHaveResource(Integer.parseInt(res_flag), rset.getString("tabid")))
            			continue;*/		            	
		            child.setAttribute("id", rset.getString("tabid"));
		            child.setAttribute("text", rset.getString("tabid")+":"+rset.getString("name"));
		            child.setAttribute("title", rset.getString("name"));
		            child.setAttribute("target", "mil_body");	
	    			child.setAttribute("icon","/images/overview_obj.gif");
	    			if(!bflag)//加上超链接
	    				child.setAttribute("href", "/general/template/edit_form.do?b_query=link&returnflag=1&business_model=0&sp_flag=1&ins_id=0&tabid="+rset.getString("tabid"));	//bug35049 点击返回应该返回代办任务，现在放回我的申请
	    			root.addContent(child);
	            }
	        }
			XMLOutputter outputter = new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			strcontent.append(outputter.outputString(myDocument));		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				if(rset!=null)
					rset.close();
				if(conn!=null&&(!conn.isClosed()))
					conn.close();
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		return strcontent.toString();
	}


	/**
	 * 消息库中是否存在对此模板的消息
	 * @return
	 */
	private boolean isHaveMsg(UserView userView,String tabid,Connection conn)
	{
		boolean bflag=false;
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			StringBuffer buf=new StringBuffer();
			DbWizard dbw = new DbWizard(conn);
			buf.append("select count(*) as nmax from tmessage where state=0 and noticetempid=");
			buf.append(tabid);
	//		buf.append(" and (b0110='");
	//		buf.append(userView.getUserOrgId());
	//		buf.append("' or b0110 is null or b0110='')");
			
			String filter_by_manage_priv="0"; //接收通知单数据方式：0接收全部数据，1接收管理范围内数据
			RowSet rset=dao.search("select ctrl_para from template_table where tabid="+tabid);
			if(rset.next())
			{
						String sxml=Sql_switcher.readMemo(rset,"ctrl_para");       
						Document doc=null;
						Element element=null;
						if(sxml!=null&&sxml.trim().length()>0)
						{
							doc=PubFunc.generateDom(sxml);
							String xpath="/params/receive_notice";
							XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
							List childlist=findPath.selectNodes(doc);			
							if(childlist!=null&&childlist.size()>0)
							{
								element=(Element)childlist.get(0);
								 filter_by_manage_priv=(String)element.getAttributeValue("filter_by_manage_priv");
							}
							
						}
			}
			if(!userView.isSuper_admin()&& "1".equals(filter_by_manage_priv))
			{
				/*
				buf.append(" and (tmessage.b0110 like '");
				if((userView.getManagePrivCodeValue()==null||userView.getManagePrivCodeValue().trim().length()==0)&&userView.getManagePrivCode().length()==0)
					buf.append("##");
				else
					buf.append(userView.getManagePrivCodeValue());
				buf.append("%' or tmessage.b0110 is null or tmessage.b0110='')");
				*/
				
				
				String operOrg = userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板  
				if(operOrg==null||!"UN`".equalsIgnoreCase(operOrg))
				{
					buf.append(" and ( ");
					if(operOrg!=null && operOrg.length() >3)
					{
						StringBuffer tempSql = new StringBuffer(""); 
						String[] temp = operOrg.split("`");
						for (int j = 0; j < temp.length; j++) { 
							 if (temp[j]!=null&&temp[j].length()>0)
								tempSql.append(" or  tmessage.b0110 like '" + temp[j].substring(2)+ "%'");				
						}
						if(tempSql.length()>0)
						{
							buf.append(tempSql.substring(3));
						}
						else
							buf.append(" tmessage.b0110='##'");
					}
					else
						buf.append(" tmessage.b0110='##'");
					
					buf.append(" or nullif(tmessage.b0110,'') is null )"); 
				}
				
			}
			if(dbw.isExistField("tmessage", "receivetype", false)){
				buf.append(" and (nullif(username,'') is null or (lower(username)='"+userView.getUserName().toLowerCase()+"' and (receivetype='4' or nullif(receivetype,'') is null)) ");
				if(this.getRoleArr(userView).length()>0)
					buf.append(" or (username in("+this.getRoleArr(userView)+") and receivetype='2'))");
				else
					buf.append(" )");
			}else
				buf.append(" and ( nullif(username,'') is null  or lower(username)='"+userView.getUserName().toLowerCase()+"')");

			rset=dao.search(buf.toString());
			int nrec=0;
			if(rset.next())
				nrec=rset.getInt("nmax");
			if(nrec!=0)
				bflag=true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return bflag;
	}	
	private String getRoleArr(UserView userView) {
		ArrayList rolelist= userView.getRolelist();//角色列表
	 	StringBuffer strrole=new StringBuffer();
	 	for(int i=0;i<rolelist.size();i++)
	 	{
	 		strrole.append("'");
	 		strrole.append((String)rolelist.get(i));
	 		strrole.append("'");
 			strrole.append(",");	 		
	 	}
	 	if(rolelist.size()>0)
	 	{
	 		strrole.setLength(strrole.length()-1);
	 	}
		return strrole.toString();
	}
	/**检查是否是考勤使用的模板 及模板显示方式 为了提高效率 单独处理
	 * @param ctrl_para
	 * @return
	 * @throws GeneralException
	 */
	private HashMap getTemplateParam(String ctrl_para)  throws GeneralException
	{
		HashMap map =new HashMap();
		try
		{
			map.put("isKq", "false");
			map.put("view", "list");				
			if(ctrl_para!=null && ctrl_para.trim().length()>0){		
				Document doc=null;
				Element element=null;
				String xpath="/params/sp_flag";
				doc=PubFunc.generateDom(ctrl_para);
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
				List childlist=findPath.selectNodes(doc);	
				if(childlist!=null&&childlist.size()>0)
				{
					element=(Element)childlist.get(0);
					if(element.getAttribute("kq_type")!=null&&element.getAttribute("kq_field_mapping")!=null)
					{
						String _kq_type=((String)element.getAttributeValue("kq_type")).trim();
						String _kq_field_mapping=(String)element.getAttributeValue("kq_field_mapping"); 
						if(_kq_type!=null&&_kq_type.trim().length()>0)
						{ 
							if(_kq_field_mapping!=null&&_kq_field_mapping.trim().length()>0)
							{
								map.put("isKq", "true");
							}
						}
					}
				}
				
				xpath="/params/init_view";
				findPath = XPath.newInstance(xpath);
				childlist=findPath.selectNodes(doc);	
				if(childlist!=null&&childlist.size()>0)
				{
					element=(Element)childlist.get(0);
					if(element.getAttribute("view")!=null)
						map.put("view", (String)element.getAttributeValue("view"));
				}
			}
		 
		}
		catch(Exception ex)
		{
				ex.printStackTrace();
				throw GeneralExceptionHandler.Handle(ex);
		}
		return map;
	}
	
	private boolean getTemplates(String type,String module,String res_flag,String unit_type,UserView userView,boolean bflag, Element parent,Connection conn, String history_param)
	{
		StringBuffer strsql=new StringBuffer();
		strsql.append("select tabid,name,ctrl_para from template_table where operationcode='");
		strsql.append(module);
		strsql.append("' and (");
		String units[]=unit_type.split(",");
		for(int i=0;i<units.length;i++)
		{
			strsql.append(" flag ="+Integer.parseInt(units[i]));
			if(i<units.length-1)
				strsql.append(" or ");
		}			
		strsql.append(") order by tabid");
		RowSet rset=null;
		boolean bhave=false;
		try
		{
			ContentDAO dao=new ContentDAO(conn);			
			rset=dao.search(strsql.toString());			
			while(rset.next())
			{			
				if (!userView.isHaveResource(Integer.parseInt(res_flag), rset.getString("tabid")))
					continue;	
				HashMap paramMap = getTemplateParam(Sql_switcher.readMemo(rset,"ctrl_para"));				
				String isKq =(String)paramMap.get("isKq");
				if ("1".equals(type)){
					if ("true".equals(isKq)){
						continue;
					}
				}
				String view =(String)paramMap.get("view");
	
				Element child = new Element("TreeNode");    		
				child.setAttribute("id", rset.getString("tabid"));
	            child.setAttribute("text", rset.getString("tabid")+":"+rset.getString("name"));
	            child.setAttribute("title", rset.getString("name"));
	            child.setAttribute("target", "mil_body");	

				if(!bflag)//加上超链接
				{
					//TemplateTableBo templateTableBo = new  TemplateTableBo( conn, rset.getInt("tabid"), userView);
					//String view = templateTableBo.getView();
					
				    //zxj 20160613 人事异动不在区分标准版专业版
/*					if(userView.getVersion_flag()==0)
					{
						if(history_param.length()>0)
						{
							child.setAttribute("href", "javascript:showTemplateList('T','"+rset.getString("tabid")+"')");
							child.setAttribute("target", "_self");
						}
						else
							child.setAttribute("href", "/general/template/edit_form.do?b_query=link&business_model=0&businessModel=0&returnflag=3&sp_flag=1&ins_id=0&tabid="+rset.getString("tabid"));//xyy 20141127 businessModel=0 防止进入报备后其他模板的状态也变成报备
					}
					else
*/					{
						if(view!=null&& "list".equalsIgnoreCase(view)){
							 child.setAttribute("href", "javascript:showTemplateList('T','"+rset.getString("tabid")+"')");
					 			child.setAttribute("target", "_self");
						}else if(view!=null&& "card".equalsIgnoreCase(view)){
							if(history_param.length()>0)
							{
								child.setAttribute("href", "javascript:showTemplateList('T','"+rset.getString("tabid")+"')");
								child.setAttribute("target", "_self");
							}
							else
								child.setAttribute("href", "/general/template/edit_form.do?b_query=link&returnflag=1&business_model=0&businessModel=0&sp_flag=1&ins_id=0&tabid="+rset.getString("tabid"));//bug35049 点击返回应该返回代办任务，现在放回我的申请
						
						}else{
							if(userView.getVersion()>=50)
							{//版本号大于等于50才显示这些功能
					 	       child.setAttribute("href", "javascript:showTemplateList('T','"+rset.getString("tabid")+"')");
					 			child.setAttribute("target", "_self");
							 }
							else   
							{
								if(history_param.length()>0)
								{
									child.setAttribute("href", "javascript:showTemplateList('T','"+rset.getString("tabid")+"')");
									child.setAttribute("target", "_self");
								}
								else
									child.setAttribute("href", "/general/template/edit_form.do?b_query=link&returnflag=1&business_model=0&businessModel=0&sp_flag=1&ins_id=0&tabid="+rset.getString("tabid"));//bug35049 点击返回应该返回代办任务，现在放回我的申请
							}
						}
					}
					
					if(isHaveMsg(userView,rset.getString("tabid"),conn))
						child.setAttribute("icon","/images/overview_n_obj.gif");
					else
						child.setAttribute("icon","/images/overview_obj.gif");					
				}
				else
					child.setAttribute("icon","/images/overview_obj.gif");					
				parent.addContent(child);
				bhave=true;
			}
		}
		catch(Exception ex)
		{
			
		}
		return bhave;
	}
	/**
	 * 装载业务模板节点,分业务类型加载模板
	 * =1,国家机关
	 * =2,事业单位
	 * =3,企业单位
	 * =4,军队使用
	 * =5,其    它
	 * @param type   1:人事异动2：薪资变动3：警衔4：法官8：保险  22:资格评审
	 * @param module //标识业务子类号
	 * @param history_param 
	 * @return
	 */	
	private String loadAllNodes(String type,String module,String res_flag,HttpServletRequest req, String history_param)
	{
		StringBuffer strcontent=new StringBuffer();
		StringBuffer strsql=new StringBuffer();
		String href=(String)req.getParameter("href");
		boolean bflag=true;
		if(href==null|| "".equals(href))//未定义此参数时,控制是否要超链接操作
			bflag=false;
		
		 
		Connection conn = null;	
		RowSet rset=null;

        UserView userView = (UserView) req.getSession().getAttribute(WebConstant.userView);	
		try
		{
			conn = (Connection) AdminDb.getConnection();
			ContentDAO dao=new ContentDAO(conn);
			String unit_type=null;
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
			unit_type=sysbo.getValue(Sys_Oth_Parameter.UNITTYPE,"type");
			if(unit_type==null|| "".equals(unit_type))
				unit_type="3";
			if(type!=null&&("3".equals(type)|| "4".equals(type)|| "5".equals(type)|| "6".equals(type)))
				unit_type="0";
			String operationcode=sysbo.getValue(Sys_Oth_Parameter.GOBROAD,"operationcode");

	        Element root = new Element("TreeNode");
	        root.setAttribute("id","$$00");
	        root.setAttribute("text","root");
	        root.setAttribute("title","root");
	        Document myDocument = new Document(root);
	        
	        
	        
	        if("22".equals(type))  //资格评审
	        {
	        	SubsysOperation so = new SubsysOperation(conn,userView);
	        	ArrayList sortlist = so.getView_tag("55");
	        	StringBuffer sql=new StringBuffer("");
	        	for(int i=0;i<sortlist.size();i++){
					String sortname = sortlist.get(i).toString();
					String select_id = so.getView_value("55",sortname);
					String[] select_ids = select_id.split(",");
					StringBuffer sortvalue = new StringBuffer();
					sql.setLength(0);
					
					
					Element child = new Element("TreeNode");
			        child.setAttribute("id", "a"+i);
			        child.setAttribute("text",sortname);
			        child.setAttribute("title",sortname);
		    		child.setAttribute("icon","/images/open.png");
					
					
					sql.append("select TabId,Name from template_table  ");
					int nn=0;
					if(select_ids!=null&&select_ids.length>0)
					{
						sql.append(" where TabId in (");
						for(int j=0;j<select_ids.length;j++){
							sql.append("'"+select_ids[j]+"',");
							nn++;
						}
						sql.setLength(sql.length()-1);
						sql.append(")");
					}
					
					if(nn==0)
						continue;
					
					sql.append(" order by tabid");
					RowSet rs = dao.search(sql.toString());
					int n=0;
					while(rs.next()){
						 
						Element _child = new Element("TreeNode");    		
						_child.setAttribute("id", rs.getString("tabid"));
						_child.setAttribute("text", rs.getString("tabid")+":"+rs.getString("name"));
						_child.setAttribute("title", rs.getString("name"));
						_child.setAttribute("target", "mil_body");	
						
						_child.setAttribute("href", "javascript:showTemplateList('T','"+rs.getString("tabid")+"')");
						_child.setAttribute("target", "_self");
						
						_child.setAttribute("icon","/images/overview_obj.gif");			
						
						child.addContent(_child);
						n++;
					}
					if(rs!=null)
						rs.close();
					if(n>0)
						root.addContent(child);	 
				}
	        }
	        else if("21".equals(type))  //劳动合同
	        {
	        	SubsysOperation so = new SubsysOperation(conn,userView);
	        	ArrayList sortlist = so.getView_tag("38");
	        	StringBuffer sql=new StringBuffer("");
	        	for(int i=0;i<sortlist.size();i++){
					String sortname = sortlist.get(i).toString();
					String select_id = so.getView_value("38",sortname);
					String[] select_ids = select_id.split(",");
					StringBuffer sortvalue = new StringBuffer();
					sql.setLength(0);
					
					
					Element child = new Element("TreeNode");
			        child.setAttribute("id", "a"+i);
			        child.setAttribute("text",sortname);
			        child.setAttribute("title",sortname);
		    		child.setAttribute("icon","/images/open.png");
					
					
					sql.append("select TabId,Name from template_table  ");
					int nn=0;
					if(select_ids!=null&&select_ids.length>0)
					{
						sql.append(" where TabId in (");
						for(int j=0;j<select_ids.length;j++){
							sql.append("'"+select_ids[j]+"',");
							nn++;
						}
						sql.setLength(sql.length()-1);
						sql.append(")");
					}
					
					if(nn==0)
						continue;
					
					sql.append(" order by tabid");
					RowSet rs = dao.search(sql.toString());
					int n=0;
					while(rs.next()){
						 
						Element _child = new Element("TreeNode");    		
						_child.setAttribute("id", rs.getString("tabid"));
						_child.setAttribute("text", rs.getString("tabid")+":"+rs.getString("name"));
						_child.setAttribute("title", rs.getString("name"));
						_child.setAttribute("target", "mil_body");	
						
						_child.setAttribute("href", "javascript:showTemplateList('T','"+rs.getString("tabid")+"')");
						_child.setAttribute("target", "_self");
						
						_child.setAttribute("icon","/images/overview_obj.gif");			
						
						child.addContent(_child);
						n++;
					}
					if(rs!=null)
						rs.close();
					if(n>0)
						root.addContent(child);	 
				}
	        }
	        else
	        { 
	        	String _static="static";
	        	if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
	        		_static="static_o";
	        	}
				strsql.append("select distinct a.operationcode,b.operationname ,operationid from ");
				strsql.append("template_table a ,operation b where a.operationcode=b.operationcode and b."+_static+"=");
				strsql.append(type);
				strsql.append(" and (");			
				String units[]=unit_type.split(",");
				for(int i=0;i<units.length;i++)
				{
					strsql.append("a.flag ="+Integer.parseInt(units[i]));
					if(i<units.length-1)
						strsql.append(" or ");
				}			
				strsql.append(")");
				strsql.append(" order by a.operationcode, operationid");
				rset=dao.search(strsql.toString());
				/**业务分类*/
				while(rset.next())
				{
	        		//if (!userView.isHaveResource(Integer.parseInt(res_flag), rset.getString("operationcode")))
	        		//	continue;	
	        		if(!bflag&&operationcode.equals(rset.getString("operationcode")))
	        			continue;
		            Element child = new Element("TreeNode");
		            child.setAttribute("id", rset.getString("operationcode"));
		            child.setAttribute("text", rset.getString("operationname"));
		            child.setAttribute("title", rset.getString("operationname"));
		            /*
	            	if(!bflag)
	            		child.setAttribute("xml","/template/search_template?module="+rset.getString("operationcode")+"&type="+type+"&res_flag="+res_flag);
	        		else
	            		child.setAttribute("xml","/template/search_template?module="+rset.getString("operationcode")+"&type="+type+"&res_flag="+res_flag+"&href=1");
	            	*/
	    			child.setAttribute("icon","/images/open.png");
	    			if(getTemplates(type,rset.getString("operationcode"),res_flag,unit_type,userView,bflag,child,conn, history_param))
	    				root.addContent(child);				
				}
	        }
			XMLOutputter outputter = new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			strcontent.append(outputter.outputString(myDocument));		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				if(rset!=null)
					rset.close();
				if(conn!=null&&(!conn.isClosed()))
					conn.close();
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		return strcontent.toString();
	}
	/**
	 * 资源分配
	 * @param type 各标签类，10=登记表，11=统计表，14=常用花名册，15=高级花名册
	 * @param res_flag	资源类型
	 * @param req	
	 * @return
	 */
	private String loadSortNodes(String type,String res_flag,HttpServletRequest req)
	{
		StringBuffer strcontent=new StringBuffer();
		StringBuffer strsql=new StringBuffer();
		String href=(String)req.getParameter("href");
		boolean bflag=true;
		if(href==null|| "".equals(href))//未定义此参数时,控制是否要超链接操作
			bflag=false;
		
		 
		Connection conn = null;	
		RowSet rset=null;

        UserView userView = (UserView) req.getSession().getAttribute(WebConstant.userView);	
		try
		{
			conn = (Connection) AdminDb.getConnection();
			ContentDAO dao=new ContentDAO(conn);
			
	        Element root = new Element("TreeNode");
	        root.setAttribute("id","$$00");
	        root.setAttribute("text","root");
	        root.setAttribute("title","root");
	        Document myDocument = new Document(root);
	        if("10".equalsIgnoreCase(type)){
	        	strsql.append("select sortid,sortname from RSort");
	        }else if("11".equalsIgnoreCase(type)){
	        	strsql.append("select TSortId sortid,Name sortname from TSort");
	        }else if("14".equalsIgnoreCase(type)){
	        	strsql.append("select styleid sortid,styledesc sortname from lstyle");
	        }else if("15".equalsIgnoreCase(type)){
	        	strsql.append("select * from muster_sort");
	        }

	        /*存放所有的sortid*/
	        ArrayList list = new ArrayList();
			rset=dao.search(strsql.toString());
			while(rset.next())
			{
				String sortid = rset.getString("sortid");
				list.add(sortid);
			}
			/**业务分类*/
			Element childUN = null;//单位
			Element childUM = null;//职位
			Element childK = null;//人员
			Element childGZFX = null;//工资分析名册
			Element childGZDY = null;//工资自定义报表
			Element childGZTZ = null;//工资台帐报表
			Element childBXFX = null;//保险分析花名册
			Element childBXZDY = null;//保险自定义花名册
			Element childBXTZ = null;//保险台帐
			Element childGRSDS = null;//个人所得税花名册
			Element childKQ = null;//考勤花名册
			Element childPX = null;//培训花名册
			if("15".equalsIgnoreCase(type)){//高级花名册
				childUN = new Element("TreeNode");
				
				childUN.setAttribute("id", "UN");
				childUN.setAttribute("text", "单位");
				childUN.setAttribute("title", "单位");
				childUN.setAttribute("icon","/images/open.png");
	            childUM = new Element("TreeNode");
	            childUM.setAttribute("id", "UM");
	            childUM.setAttribute("text", "职位");
	            childUM.setAttribute("title", "职位");
	            childUM.setAttribute("icon","/images/open.png");
	            childK = new Element("TreeNode");
	            childK.setAttribute("id", "K");
	            childK.setAttribute("text", "人员");
	            childK.setAttribute("title", "人员");
	            childK.setAttribute("icon","/images/open.png");
	            childGZFX = new Element("TreeNode");
	            childGZFX.setAttribute("id", "GZ"); 
	            childGZFX.setAttribute("text", "薪资分析名册");
	            childGZFX.setAttribute("title", "薪资分析名册");
	            childGZFX.setAttribute("icon","/images/open.png");
	            childGZDY = new Element("TreeNode");
	            childGZDY.setAttribute("id", "GZDY");
	            childGZDY.setAttribute("text", "薪资自定义报表");
	            childGZDY.setAttribute("title", "薪资自定义报表");
	            childGZDY.setAttribute("icon","/images/open.png");
	            childGZTZ = new Element("TreeNode");
	            childGZTZ.setAttribute("id", "GZDY");
	            childGZTZ.setAttribute("text", "薪资台帐");
	            childGZTZ.setAttribute("title", "薪资台帐");
	            childGZTZ.setAttribute("icon","/images/open.png");
	            childBXFX = new Element("TreeNode");
	            childBXFX.setAttribute("id", "BX");
	            childBXFX.setAttribute("text", "保险分析花名册");
	            childBXFX.setAttribute("title", "保险分析花名册");
	            childBXFX.setAttribute("icon","/images/open.png");
	            childBXZDY = new Element("TreeNode");
	            childBXZDY.setAttribute("id", "BXDY");
	            childBXZDY.setAttribute("text", "保险自定义花名册");
	            childBXZDY.setAttribute("title", "保险自定义花名册");
	            childBXZDY.setAttribute("icon","/images/open.png");
	            childBXTZ = new Element("TreeNode");
	            childBXTZ.setAttribute("id", "BXDY");
	            childBXTZ.setAttribute("text", "保险台帐");
	            childBXTZ.setAttribute("title", "保险台帐");
	            childBXTZ.setAttribute("icon","/images/open.png");
	            childGRSDS = new Element("TreeNode");
	            childGRSDS.setAttribute("id", "BXDY");
	            childGRSDS.setAttribute("text", "个人所得税花名册");
	            childGRSDS.setAttribute("title", "个人所得税花名册");
	            childGRSDS.setAttribute("icon","/images/open.png");
	            childPX = new Element("TreeNode");
	            childPX.setAttribute("id", "BXDY");
	            childPX.setAttribute("text", "培训花名册");
	            childPX.setAttribute("title", "培训花名册");
	            childPX.setAttribute("icon","/images/open.png");
	            childKQ = new Element("TreeNode");
	            childKQ.setAttribute("id", "BXDY");
	            childKQ.setAttribute("text", "考勤花名册");
	            childKQ.setAttribute("title", "考勤花名册");
	            childKQ.setAttribute("icon","/images/open.png");
			}
			rset=dao.search(strsql.toString());
			ArrayList nmodulelist = new ArrayList();
			while(rset.next())
			{
				String sortid = rset.getString("sortid");
				if("15".equalsIgnoreCase(type)){
					RowSet rsetm=null;
					String nmodule = rset.getString("nmodule");
					nmodulelist.add(nmodule);
					if("21".equalsIgnoreCase(nmodule)){
			            Element child1 = new Element("TreeNode");
			            child1.setAttribute("id", sortid);
			            child1.setAttribute("text", rset.getString("sortname"));
			            child1.setAttribute("title", rset.getString("sortname"));
			            child1.setAttribute("icon","/images/open.png");
			            if(getSortTemplates(sortid,nmodule,res_flag,userView,bflag,child1,conn))
			            	childUN.addContent(child1);
			            //addUnSortMusterNode(childUN,list,res_flag,nmodule,userView,bflag,conn);
					}else if("41".equalsIgnoreCase(nmodule)){
			            Element child1 = new Element("TreeNode");
			            child1.setAttribute("id", sortid);
			            child1.setAttribute("text", rset.getString("sortname"));
			            child1.setAttribute("title", rset.getString("sortname"));
			            child1.setAttribute("icon","/images/open.png");
			            if(getSortTemplates(sortid,nmodule,res_flag,userView,bflag,child1,conn))
			            	childUM.addContent(child1);
			            //addUnSortMusterNode(childUM,list,res_flag,nmodule,userView,bflag,conn);
					}else if("3".equalsIgnoreCase(nmodule)){//人员
			            Element child1 = new Element("TreeNode");
			            child1.setAttribute("id", sortid);
			            child1.setAttribute("text", rset.getString("sortname"));
			            child1.setAttribute("title", rset.getString("sortname"));
			            child1.setAttribute("icon","/images/open.png");
			            if(getSortTemplates(sortid,nmodule,res_flag,userView,bflag,child1,conn))
			            	childK.addContent(child1);
			            /*Element child = new Element("TreeNode");
			            child.setAttribute("id", "$$");
			            child.setAttribute("text", "未分类");
			            child.setAttribute("title", "未分类");
			            child.setAttribute("icon","/images/open.png");
			            if(getMusterNotSortTemplates(list,res_flag,nmodule,userView,bflag,child,conn))
			            	childK.addContent(child);*/
			            //addUnSortMusterNode(childK,list,res_flag,nmodule,userView,bflag,conn);
					}else if("6".equalsIgnoreCase(nmodule)){
			            Element child1 = new Element("TreeNode");
			            child1.setAttribute("id", sortid);
			            child1.setAttribute("text", rset.getString("sortname"));
			            child1.setAttribute("title", rset.getString("sortname"));
			            child1.setAttribute("icon","/images/open.png");
			            if(getSortTemplates(sortid,nmodule,res_flag,userView,bflag,child1,conn))
			            	childGZFX.addContent(child1);
			            //addUnSortMusterNode(childGZFX,list,res_flag,nmodule,userView,bflag,conn);
					}else if("14".equalsIgnoreCase(nmodule)){//工资自定义报表
						rsetm = dao.search("select salaryid tabid,cname sortname from salarytemplate where (cstate is null or cstate='')");
						while(rsetm.next()){
							if (!userView.isHaveResource(IResourceConstant.GZ_SET, rsetm.getString("tabid")))
								continue;								
							Element child1 = new Element("TreeNode");
				            child1.setAttribute("id", rsetm.getString("tabid"));
				            child1.setAttribute("text", rsetm.getString("sortname"));
				            child1.setAttribute("title", rsetm.getString("sortname"));
				            child1.setAttribute("icon","/images/open.png");
				            if(getMusterSortTemplates(sortid,rsetm.getString("tabid"),nmodule,res_flag,userView,bflag,child1,conn))
				            	childGZDY.addContent(child1);
				            //addUnSortMusterNode(childGZDY,list,res_flag,nmodule,userView,bflag,conn,rsetm.getString("tabid"));
						}
					}else if("8".equalsIgnoreCase(nmodule)){
			            Element child1 = new Element("TreeNode");
			            child1.setAttribute("id", sortid);
			            child1.setAttribute("text", rset.getString("sortname"));
			            child1.setAttribute("title", rset.getString("sortname"));
			            child1.setAttribute("icon","/images/open.png");
			            if(getSortTemplates(sortid,nmodule,res_flag,userView,bflag,child1,conn))
			            	childBXFX.addContent(child1);
			            //addUnSortMusterNode(childBXFX,list,res_flag,nmodule,userView,bflag,conn);
					}else if("11".equalsIgnoreCase(nmodule)){//保险自定义花名册
						rsetm = dao.search("select salaryid tabid,cname sortname from salarytemplate where cstate='1'");
						while(rsetm.next()){
							if (!userView.isHaveResource(IResourceConstant.INS_SET, rsetm.getString("tabid")))
								continue;								
				            Element child1 = new Element("TreeNode");
				            child1.setAttribute("id", rsetm.getString("tabid"));
				            child1.setAttribute("text", rsetm.getString("sortname"));
				            child1.setAttribute("title", rsetm.getString("sortname"));
				            child1.setAttribute("icon","/images/open.png");
				            if(getMusterSortTemplates(sortid,rsetm.getString("tabid"),nmodule,res_flag,userView,bflag,child1,conn))
				            	childBXZDY.addContent(child1);
				            //addUnSortMusterNode(childBXZDY,list,res_flag,nmodule,userView,bflag,conn,rsetm.getString("tabid"));
						}
					}else if("15".equalsIgnoreCase(nmodule)){//个人所得税花名册
						Element child1 = new Element("TreeNode");
			            child1.setAttribute("id", sortid);
			            child1.setAttribute("text", rset.getString("sortname"));
			            child1.setAttribute("title", rset.getString("sortname"));
			            child1.setAttribute("icon","/images/open.png");
			            if(getSortTemplates(sortid,nmodule,res_flag,userView,bflag,child1,conn))
			            	childGRSDS.addContent(child1);
			            //addUnSortMusterNode(childGRSDS,list,res_flag,nmodule,userView,bflag,conn);
					}else if("61".equalsIgnoreCase(nmodule)){//培训花名册
						Element child1 = new Element("TreeNode");
			            child1.setAttribute("id", sortid);
			            child1.setAttribute("text", rset.getString("sortname"));
			            child1.setAttribute("title", rset.getString("sortname"));
			            child1.setAttribute("icon","/images/open.png");
			            if(getSortTemplates(sortid,nmodule,res_flag,userView,bflag,child1,conn))
			            	childPX.addContent(child1);
			            //addUnSortMusterNode(childPX,list,res_flag,nmodule,userView,bflag,conn);
					}else if("81".equalsIgnoreCase(nmodule)){//考勤花名册
						Element child1 = new Element("TreeNode");
			            child1.setAttribute("id", sortid);
			            child1.setAttribute("text", rset.getString("sortname"));
			            child1.setAttribute("title", rset.getString("sortname"));
			            child1.setAttribute("icon","/images/open.png");
			            if(getSortTemplates(sortid,nmodule,res_flag,userView,bflag,child1,conn))
			            	childKQ.addContent(child1);
			            //addUnSortMusterNode(childKQ,list,res_flag,nmodule,userView,bflag,conn);
					}else if("1".equalsIgnoreCase(nmodule)){//保险台帐
						Element child1 = new Element("TreeNode");
			            child1.setAttribute("id", sortid);
			            child1.setAttribute("text", rset.getString("sortname"));
			            child1.setAttribute("title", rset.getString("sortname"));
			            child1.setAttribute("icon","/images/open.png");
			            if(getSortTemplates(sortid,nmodule,res_flag,userView,bflag,child1,conn))
			            	childBXTZ.addContent(child1);
			            //addUnSortMusterNode(childBXTZ,list,res_flag,nmodule,userView,bflag,conn);
					}else if("4".equalsIgnoreCase(nmodule)){//工资台帐
						Element child1 = new Element("TreeNode");
			            child1.setAttribute("id", sortid);
			            child1.setAttribute("text", rset.getString("sortname"));
			            child1.setAttribute("title", rset.getString("sortname"));
			            child1.setAttribute("icon","/images/open.png");
			            if(getSortTemplates(sortid,nmodule,res_flag,userView,bflag,child1,conn))
			            	childGZTZ.addContent(child1);
			            //addUnSortMusterNode(childGZTZ,list,res_flag,nmodule,userView,bflag,conn);
					}
				}else{
					Element child = new Element("TreeNode");
		            
		            child.setAttribute("id", sortid);
		            child.setAttribute("text", rset.getString("sortname"));
		            child.setAttribute("title", rset.getString("sortname"));
	    			child.setAttribute("icon","/images/open.png");
	    			if(getSortTemplates(sortid,"#",res_flag,userView,bflag,child,conn))
	    				root.addContent(child);
				}
			}
			
			if("15".equalsIgnoreCase(type)){
				addUnSortMusterNode(childUN,list,res_flag,"21",userView,bflag,conn);
				addUnSortMusterNode(childUM,list,res_flag,"41",userView,bflag,conn);
				addUnSortMusterNode(childK,list,res_flag,"3",userView,bflag,conn);
				addUnSortMusterNode(childGZFX,list,res_flag,"6",userView,bflag,conn);
				addUnSortMusterNode(childBXFX,list,res_flag,"8",userView,bflag,conn);
				addUnSortMusterNode(childGRSDS,list,res_flag,"15",userView,bflag,conn);
				addUnSortMusterNode(childPX,list,res_flag,"61",userView,bflag,conn);
				addUnSortMusterNode(childKQ,list,res_flag,"81",userView,bflag,conn);
				addUnSortMusterNode(childBXTZ,list,res_flag,"1",userView,bflag,conn);
				addUnSortMusterNode(childGZTZ,list,res_flag,"4",userView,bflag,conn);
				addUnSortMusterNode2(childGZDY,res_flag,"14",userView,bflag,conn);
				addUnSortMusterNode2(childBXZDY,res_flag,"11",userView,bflag,conn);
				root.addContent(childUN);
				root.addContent(childUM);
				root.addContent(childK);
				root.addContent(childGZFX);
				root.addContent(childGZDY);
				root.addContent(childGZTZ);
				root.addContent(childBXFX);
				root.addContent(childBXZDY);
				root.addContent(childBXTZ);
				root.addContent(childGRSDS);
				root.addContent(childPX);
				root.addContent(childKQ);
			}else{
				Element child = new Element("TreeNode");
	            child.setAttribute("id", "$$");
	            child.setAttribute("text", "未分类");
	            child.setAttribute("title", "未分类");
	            child.setAttribute("icon","/images/open.png");
	            if(getNotSortTemplates(list,res_flag,userView,bflag,child,conn))
	            	root.addContent(child);
			}
			XMLOutputter outputter = new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			strcontent.append(outputter.outputString(myDocument));		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				if(rset!=null)
					rset.close();
				if(conn!=null&&(!conn.isClosed()))
					conn.close();
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		return strcontent.toString();
	}
	/**
	 * 加上子节点
	 * @param sortid	各模板表所对应的分类号
	 * @param nmodule	用来判断是否为高级花名册
	 * @param res_flag	资源类型
	 * @param userView	
	 * @param bflag	是否加超链接
	 * @param parent	父节点
	 * @param conn	
	 * @return
	 */
	private boolean getSortTemplates(String sortid,String nmodule,String res_flag,UserView userView,boolean bflag, Element parent,Connection conn)
	{
		RowSet rset=null;
		boolean bhave=false;
		
		if(res_flag==null|| "".equals(res_flag))
			res_flag="0";
		/**资源类型*/
		int res_type=Integer.parseInt(res_flag);
		try
		{
			ContentDAO dao=new ContentDAO(conn);			

			rset=dao.search(getResourceSql(sortid,nmodule,res_type));
			while(rset.next())
			{			
				if (!userView.isHaveResource(res_type, rset.getString("tabid")))
					continue;	
				Element child = new Element("TreeNode");    		
				child.setAttribute("id", rset.getString("tabid"));
	            child.setAttribute("text", rset.getString("tabid")+":"+rset.getString("name"));
	            child.setAttribute("title", rset.getString("name"));
	            child.setAttribute("target", "mil_body");	

				if(!bflag)//加上超链接
				{
					child.setAttribute("href", "/general/template/edit_form.do?b_query=link&returnflag=3&business_model=0&sp_flag=1&ins_id=0&tabid="+rset.getString("tabid"));
					if(isHaveMsg(userView,rset.getString("tabid"),conn))
						child.setAttribute("icon","/images/overview_n_obj.gif");
					else
						child.setAttribute("icon","/images/overview_obj.gif");					
				}
				else
					child.setAttribute("icon","/images/overview_obj.gif");					
				parent.addContent(child);
				bhave=true;
			}
		}
		catch(Exception ex)
		{
			
		}
		return bhave;
	}
	private boolean getMusterSortTemplates(String sortid,String nprint,String nmodule,String res_flag,UserView userView,boolean bflag, Element parent,Connection conn)
	{
		RowSet rset=null;
		boolean bhave=false;
		
		if(res_flag==null|| "".equals(res_flag))
			res_flag="0";
		/**资源类型*/
		int res_type=Integer.parseInt(res_flag);
		try
		{
			ContentDAO dao=new ContentDAO(conn);			

			rset=dao.search(getMusterResourceSql(sortid,nprint,nmodule));
			while(rset.next())
			{			
				if (!userView.isHaveResource(res_type, rset.getString("tabid")))
					continue;	
				Element child = new Element("TreeNode");    		
				child.setAttribute("id", rset.getString("tabid"));
	            child.setAttribute("text", rset.getString("tabid")+":"+rset.getString("name"));
	            child.setAttribute("title", rset.getString("name"));
	            child.setAttribute("target", "mil_body");	

				if(!bflag)//加上超链接
				{
					child.setAttribute("href", "/general/template/edit_form.do?b_query=link&returnflag=1&business_model=0&sp_flag=1&ins_id=0&tabid="+rset.getString("tabid"));//bug35049 点击返回应该返回代办任务，现在放回我的申请
					if(isHaveMsg(userView,rset.getString("tabid"),conn))
						child.setAttribute("icon","/images/overview_n_obj.gif");
					else
						child.setAttribute("icon","/images/overview_obj.gif");					
				}
				else
					child.setAttribute("icon","/images/overview_obj.gif");					
				parent.addContent(child);
				bhave=true;
			}
		}
		catch(Exception ex)
		{
			
		}
		return bhave;
	}
	/**
	 * 
	 * @param sortid	在各个sort表中的sortid
	 * @param nmodule	判断是否是高级花名册
	 * @param res_type	1=统计表Tsort，0=登记表Rsort，4=常用花名册lstyle，5=高级花名册muster_sort
	 * @return
	 */
	private String getResourceSql(String sortid,String nmodule,int res_type)
	{
		StringBuffer strsql=new StringBuffer();
		switch(res_type)
		{
		case 1:
			strsql.append("select tabid,name from tname where tsortid='"+sortid+"'");
			break;
		case 0:
			strsql.append("select tabid,name from rname where moduleflag ='"+sortid+"'");
			break;
		case 4:
			switch(Sql_switcher.searchDbServer())
			{
				case Constant.MSSQL:
				{
					strsql.append("select tabid,hzname name from lname where SubString(moduleflag, 2, 2) ='"+sortid+"'");
				}
				break;
				case Constant.DB2:
				{
					strsql.append("select tabid,hzname name from lname where SubStr(moduleflag, 2, 2) ='"+sortid+"'");
				}
				break;
				case Constant.ORACEL:
				{
					strsql.append("select tabid,hzname name from lname where SubStr(moduleflag, 2, 2) ='"+sortid+"'");
				}
				break;
			}
			break;
		case 5:
			if("14".equalsIgnoreCase(nmodule)|| "11".equalsIgnoreCase(nmodule)|| "5".equalsIgnoreCase(nmodule)){//工资自定义报表，和保险自定义表
				strsql.append("select tabid,cname name from muster_name where nmodule='"+nmodule+"' and nprint ='"+sortid+"'");
			}else{
				strsql.append("select tabid,cname name from muster_name where sortid ='"+sortid+"' and nmodule='"+nmodule+"'" );
			}
			break;
		}

		return strsql.toString();
	}
	private String getMusterResourceSql(String sortid,String nprint,String nmodule)
	{
		StringBuffer strsql=new StringBuffer();
		if("14".equalsIgnoreCase(nmodule)|| "11".equalsIgnoreCase(nmodule)|| "5".equalsIgnoreCase(nmodule)){//工资自定义报表，和保险自定义表
			strsql.append("select tabid,cname name from muster_name where nmodule='"+nmodule+"' and nprint ='"+nprint+"' and sortid ='"+sortid+"'");
		}else{
			strsql.append("select tabid,cname name from muster_name where sortid ='"+sortid+"' and nmodule='"+nmodule+"'" );
		}
		return strsql.toString();
	}
	/**
	 * 
	 * @param list	存放的是sortid
	 * @param res_flag	资源类型
	 * @param userView	
	 * @param bflag	是否加超链接
	 * @param parent	父节点
	 * @param conn	
	 * @return
	 */
	private boolean getNotSortTemplates(ArrayList list,String res_flag,UserView userView,boolean bflag, Element parent,Connection conn)
	{
		RowSet rset=null;
		boolean bhave=false;
		
		if(res_flag==null|| "".equals(res_flag))
			res_flag="0";
		/**资源类型*/
		int res_type=Integer.parseInt(res_flag);
		try
		{
			ContentDAO dao=new ContentDAO(conn);			

			rset=dao.search(getNotSortResourceSql(list,res_type));
			while(rset.next())
			{			
				if (!userView.isHaveResource(Integer.parseInt(res_flag), rset.getString("tabid")))
					continue;	
				Element child = new Element("TreeNode");    		
				child.setAttribute("id", rset.getString("tabid"));
	            child.setAttribute("text", rset.getString("tabid")+":"+rset.getString("name"));
	            child.setAttribute("title", rset.getString("name"));
	            child.setAttribute("target", "mil_body");	

				if(!bflag)//加上超链接
				{
					child.setAttribute("href", "/general/template/edit_form.do?b_query=link&returnflag=1&business_model=0&sp_flag=1&ins_id=0&tabid="+rset.getString("tabid"));//bug35049 点击返回应该返回代办任务，现在放回我的申请
					if(isHaveMsg(userView,rset.getString("tabid"),conn))
						child.setAttribute("icon","/images/overview_n_obj.gif");
					else
						child.setAttribute("icon","/images/overview_obj.gif");					
				}
				else
					child.setAttribute("icon","/images/overview_obj.gif");					
				parent.addContent(child);
				bhave=true;
			}
		}
		catch(Exception ex)
		{
			
		}
		return bhave;
	}
	/**
	 * 
	 * @param list	存放的是sortid
	 * @param res_type	1=统计表Tsort，0=登记表Rsort，4=常用花名册lstyle，5=高级花名册muster_sort
	 * @return
	 */
	private String getNotSortResourceSql(ArrayList list,int res_type)
	{
		StringBuffer strsql=new StringBuffer();
		switch(res_type)
		{
		case 1:
			strsql.append("select tabid,name from tname where tsortid not in(");
			for(int i=0;i<list.size();i++){
				strsql.append("'"+list.get(i)+"',");
			}
			strsql.setLength(strsql.length()-1);
			strsql.append(")");
			break;
		case 0:
			strsql.append("select tabid,name from rname where moduleflag not in(");
			for(int i=0;i<list.size();i++){
				strsql.append("'"+list.get(i)+"',");
			}
			strsql.setLength(strsql.length()-1);
			strsql.append(")");
			break;
		case 4:
			switch(Sql_switcher.searchDbServer())
			{
				case Constant.MSSQL:
				{
					strsql.append("select tabid,hzname name from lname where SubString(moduleflag, 2, 2) =");
					/*for(int i=0;i<list.size();i++){
						strsql.append("'"+list.get(i)+"',");
					}*/
					strsql.append("'00'");
					//strsql.setLength(strsql.length()-1);
					//strsql.append(")");
				}
				break;
				case Constant.DB2:
				{
					strsql.append("select tabid,hzname name from lname where SubStr(moduleflag, 2, 2) =");
					/*for(int i=0;i<list.size();i++){
						strsql.append("'"+list.get(i)+"',");
					}*/
					strsql.append("'00'");
					//strsql.setLength(strsql.length()-1);
					//strsql.append(")");
				}
				break;
				case Constant.ORACEL:
				{
					strsql.append("select tabid,hzname name from lname where SubStr(moduleflag, 2, 2) =");
					/*for(int i=0;i<list.size();i++){
						strsql.append("'"+list.get(i)+"',");
					}*/
					strsql.append("'00'");
					//strsql.setLength(strsql.length()-1);
					//strsql.append(")");
				}
				break;
			}
			break;
		/*case 5:
			strsql.append("select tabid,cname name from muster_name where nmodule not in(");
			for(int i=0;i<list.size();i++){
				strsql.append("'"+list.get(i)+"',");
			}
			strsql.setLength(strsql.length()-1);
			strsql.append(")");
			break;*/
		}

		return strsql.toString();
	}
	/**高级花名册专用
	 * 加入未在模板中的节点
	 */
	private boolean getMusterNotSortTemplates(ArrayList list,String res_flag,String nmodule,UserView userView,boolean bflag, Element parent,Connection conn)
	{
		RowSet rset=null;
		boolean bhave=false;
		
		if(res_flag==null|| "".equals(res_flag))
			res_flag="0";
		/**资源类型*/
		int res_type=Integer.parseInt(res_flag);
		try
		{
			ContentDAO dao=new ContentDAO(conn);			

			rset=dao.search(getMusterNotSortResourceSql(list,nmodule));
			while(rset.next())
			{			
				if (!userView.isHaveResource(Integer.parseInt(res_flag), rset.getString("tabid")))
					continue;	
				Element child = new Element("TreeNode");    		
				child.setAttribute("id", rset.getString("tabid"));
	            child.setAttribute("text", rset.getString("tabid")+":"+rset.getString("name"));
	            child.setAttribute("title", rset.getString("name"));
	            child.setAttribute("target", "mil_body");	

				if(!bflag)//加上超链接
				{
					child.setAttribute("href", "/general/template/edit_form.do?b_query=link&returnflag=1&business_model=0&sp_flag=1&ins_id=0&tabid="+rset.getString("tabid"));//bug35049 点击返回应该返回代办任务，现在放回我的申请
					if(isHaveMsg(userView,rset.getString("tabid"),conn))
						child.setAttribute("icon","/images/overview_n_obj.gif");
					else
						child.setAttribute("icon","/images/overview_obj.gif");					
				}
				else
					child.setAttribute("icon","/images/overview_obj.gif");					
				parent.addContent(child);
				bhave=true;
			}
		}
		catch(Exception ex)
		{
			
		}
		return bhave;
	}
	private boolean getMusterNotSortTemplates2(String res_flag,String nmodule,UserView userView,boolean bflag, Element parent,Connection conn)
	{
		RowSet rset=null;
		boolean bhave=false;
		
		if(res_flag==null|| "".equals(res_flag))
			res_flag="0";
		/**资源类型*/
		int res_type=Integer.parseInt(res_flag);
		try
		{
			ContentDAO dao=new ContentDAO(conn);			

			rset=dao.search(getMusterNotSortResourceSql2(nmodule));
			while(rset.next())
			{			
				if (!userView.isHaveResource(Integer.parseInt(res_flag), rset.getString("tabid")))
					continue;	
				Element child = new Element("TreeNode");    		
				child.setAttribute("id", rset.getString("tabid"));
	            child.setAttribute("text", rset.getString("tabid")+":"+rset.getString("name"));
	            child.setAttribute("title", rset.getString("name"));
	            child.setAttribute("target", "mil_body");	

				if(!bflag)//加上超链接
				{
					child.setAttribute("href", "/general/template/edit_form.do?b_query=link&returnflag=1&business_model=0&sp_flag=1&ins_id=0&tabid="+rset.getString("tabid"));//bug35049 点击返回应该返回代办任务，现在放回我的申请
					if(isHaveMsg(userView,rset.getString("tabid"),conn))
						child.setAttribute("icon","/images/overview_n_obj.gif");
					else
						child.setAttribute("icon","/images/overview_obj.gif");					
				}
				else
					child.setAttribute("icon","/images/overview_obj.gif");					
				parent.addContent(child);
				bhave=true;
			}
		}
		catch(Exception ex)
		{
			
		}
		return bhave;
	}
	/**高级花名册专用
	 * 加入未在模板中的节点
	 */
	private boolean getMusterNotSortTemplates(ArrayList list,String res_flag,String nmodule,UserView userView,boolean bflag, Element parent,Connection conn,String nprintid)
	{
		RowSet rset=null;
		boolean bhave=false;
		
		if(res_flag==null|| "".equals(res_flag))
			res_flag="0";
		/**资源类型*/
		int res_type=Integer.parseInt(res_flag);
		try
		{
			ContentDAO dao=new ContentDAO(conn);			

			rset=dao.search(getMusterNotSortResourceSql(list,nprintid,nmodule));
			while(rset.next())
			{			
				if (!userView.isHaveResource(Integer.parseInt(res_flag), rset.getString("tabid")))
					continue;	
				Element child = new Element("TreeNode");    		
				child.setAttribute("id", rset.getString("tabid"));
	            child.setAttribute("text", rset.getString("tabid")+":"+rset.getString("name"));
	            child.setAttribute("title", rset.getString("name"));
	            child.setAttribute("target", "mil_body");	

				if(!bflag)//加上超链接
				{
					child.setAttribute("href", "/general/template/edit_form.do?b_query=link&returnflag=1&sp_flag=1&business_model=0&ins_id=0&tabid="+rset.getString("tabid"));//bug35049 点击返回应该返回代办任务，现在放回我的申请
					if(isHaveMsg(userView,rset.getString("tabid"),conn))
						child.setAttribute("icon","/images/overview_n_obj.gif");
					else
						child.setAttribute("icon","/images/overview_obj.gif");					
				}
				else
					child.setAttribute("icon","/images/overview_obj.gif");					
				parent.addContent(child);
				bhave=true;
			}
		}
		catch(Exception ex)
		{
			
		}
		return bhave;
	}
	/**高级花名册专用
	 * 查询未在模板中的节点语句
	 */
	private String getMusterNotSortResourceSql(ArrayList list,String nmodule)
	{
		StringBuffer strsql=new StringBuffer();
		strsql.append("select tabid,cname name from muster_name where nmodule ="+nmodule+" and sortid= ");
		/*for(int i=0;i<list.size();i++){
			strsql.append("'"+list.get(i)+"',");
		}*/
		strsql.append("'0'");
		//strsql.setLength(strsql.length()-1);
		//strsql.append(")");
		return strsql.toString();
	}
	private String getMusterNotSortResourceSql2(String nmodule)
	{
		StringBuffer strsql=new StringBuffer();
		strsql.append("select tabid,cname name from muster_name where nmodule='"+nmodule+"' and nprint ='-1'");
		return strsql.toString();
	}
	/**增加未分类
	 * @param node 父节点
	 * @param list	存放的是sortid
	 * @param res_flag	资源类型
	 * @param nmodule 分类号
	 * @param userView	
	 * @param bflag	是否加超链接
	 * @param conn	
	 */
	private void addUnSortMusterNode(Element node,ArrayList list,String res_flag,String nmodule,UserView userView,boolean bflag,Connection conn){
		Element child = new Element("TreeNode");
        child.setAttribute("id", "$$");
        child.setAttribute("text", "未分类");
        child.setAttribute("title", "未分类");
        child.setAttribute("icon","/images/open.png");
        if(getMusterNotSortTemplates(list,res_flag,nmodule,userView,bflag,child,conn))
        	node.addContent(child);
	}
	private void addUnSortMusterNode2(Element node,String res_flag,String nmodule,UserView userView,boolean bflag,Connection conn){
		Element child = new Element("TreeNode");
        child.setAttribute("id", "$$");
        child.setAttribute("text", "公用表");
        child.setAttribute("title", "公用表");
        child.setAttribute("icon","/images/open.png");
        if(getMusterNotSortTemplates2(res_flag,nmodule,userView,bflag,child,conn))
        	node.addContent(child);
	}
	/**自定义表增加未分类节点
	 * 
	 * @param node 父节点
	 * @param list	存放的是sortid
	 * @param res_flag	资源类型
	 * @param nmodule 分类号
	 * @param userView	
	 * @param bflag	是否加超链接
	 * @param conn	
	 */
	private void addUnSortMusterNode(Element node,ArrayList list,String res_flag,String nmodule,UserView userView,boolean bflag,Connection conn,String nprintid){
		Element child = new Element("TreeNode");
        child.setAttribute("id", "$$");
        child.setAttribute("text", "未分类");
        child.setAttribute("title", "未分类");
        child.setAttribute("icon","/images/open.png");
        if(getMusterNotSortTemplates(list,res_flag,nmodule,userView,bflag,child,conn,nprintid))
        	node.addContent(child);
	}
	
	private String getMusterNotSortResourceSql(ArrayList list,String nprint,String nmodule)
	{
		StringBuffer strsql=new StringBuffer();
		if("14".equalsIgnoreCase(nmodule)|| "11".equalsIgnoreCase(nmodule)|| "5".equalsIgnoreCase(nmodule)){//工资自定义报表，和保险自定义表
			strsql.append("select tabid,cname name from muster_name where nmodule ="+nmodule+" and nprint ='"+nprint+"' and sortid =");
		}else{
			strsql.append("select tabid,cname name from muster_name where nmodule ="+nmodule+" and sortid =");
		}
		/*for(int i=0;i<list.size();i++){
			strsql.append("'"+list.get(i)+"',");
		}*/
		//strsql.setLength(strsql.length()-1);
		//strsql.append(")");
		strsql.append("'0'");
		return strsql.toString();
	}
}
