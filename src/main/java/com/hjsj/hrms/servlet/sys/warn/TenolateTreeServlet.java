package com.hjsj.hrms.servlet.sys.warn;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.RowSet;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;

public class TenolateTreeServlet extends HttpServlet {

	 private String priv="1";
	 private String select_id="";
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		doPost(arg0, arg1);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String type=(String)req.getParameter("type");
		String module=(String)req.getParameter("module");
		String res_flag=(String)req.getParameter("res_flag");
		String priv=(String)req.getParameter("priv");	
		String dr=(String)req.getParameter("dr");
		String confine=(String)req.getParameter("confine");
		this.priv=priv;
    	StringBuffer treexml = new StringBuffer();	
        req.setCharacterEncoding("GBK");
		try {
			    if(type==null||type.length()<=0)
			    {
			    	type="-1";
			    }
			    if("-1".equals(type))
			    {
			    	Element root = new Element("TreeNode");
			        root.setAttribute("id","$$00");
			        root.setAttribute("text","root");
			        root.setAttribute("title","root");
			        Document myDocument = new Document(root);
			        Element child = new Element("TreeNode");
		            child.setAttribute("id","1");
		            child.setAttribute("text", "人事异动");
		            child.setAttribute("title", "人事异动");
		            child.setAttribute("href", "javascript:void(0)");  
		    		String url="/sys/warn/search_template?module=-1&type=1&res_flag="+res_flag+"&select_id="+select_id+"&dr="+dr+"&confine="+confine;   	
		    		child.setAttribute("xml", url);	 
		           /* 
		            if(a_codesetid.equals("UN"))
		                child.setAttribute("icon","/images/unit.gif");
		            else if(a_codesetid.equals("UM"))
			             child.setAttribute("icon","/images/dept.gif");
		            else if(a_codesetid.equals("@K"))
			        	 child.setAttribute("icon","/images/pos_l.gif");
		            else	*/
		    		
		            child.setAttribute("icon","/images/open.png");  
		            root.addContent(child); 
		            child = new Element("TreeNode");
		            child.setAttribute("id","2");
		            child.setAttribute("text", "薪资变动");
		            child.setAttribute("title", "薪资变动");
		            child.setAttribute("href", "javascript:void(0)");  
		    		url="/sys/warn/search_template?module=-1&type=2&res_flag="+res_flag+"&select_id="+select_id+"&dr="+dr+"&confine="+confine;    	
		    		child.setAttribute("xml", url);
		    		child.setAttribute("icon","/images/open.png");  
			        root.addContent(child);
			        
			        child = new Element("TreeNode");
		            child.setAttribute("id","3");
		            child.setAttribute("text", "保险变动");
		            child.setAttribute("title", "保险变动");
		            child.setAttribute("href", "javascript:void(0)");  
		    		url="/sys/warn/search_template?module=-1&type=8&res_flag="+res_flag+"&select_id="+select_id+"&dr="+dr+"&confine="+confine;    	
		    		child.setAttribute("xml", url);
		    		child.setAttribute("icon","/images/open.png");  
			        root.addContent(child); 
			        
			        if(SystemConfig.getPropertyValue("unit_property")!=null)
			        {
			        	 String str=(String)SystemConfig.getPropertyValue("unit_property");
			        	 if(str.toLowerCase().indexOf("psorgans")!=-1)
			        	 {
			        		    child = new Element("TreeNode");
					            child.setAttribute("id","4");
					            child.setAttribute("text", "警衔管理");
					            child.setAttribute("title", "警衔管理");
					            child.setAttribute("href", "javascript:void(0)");  
					    		url="/sys/warn/search_template?module=-1&type=3&res_flag="+res_flag+"&select_id="+select_id+"&dr="+dr+"&confine="+confine;    	
					    		child.setAttribute("xml", url);
					    		child.setAttribute("icon","/images/open.png");  
						        root.addContent(child); 
			        		  
			        	 }
			        	
			        	 if("psorgans_fg".equalsIgnoreCase(str))
			        	 {
			        		    child = new Element("TreeNode");
					            child.setAttribute("id","5");
					            child.setAttribute("text", "法官管理");
					            child.setAttribute("title", "法官管理");
					            child.setAttribute("href", "javascript:void(0)");  
					    		url="/sys/warn/search_template?module=-1&type=4&res_flag="+res_flag+"&select_id="+select_id+"&dr="+dr+"&confine="+confine;    	
					    		child.setAttribute("xml", url);
					    		child.setAttribute("icon","/images/open.png");  
						        root.addContent(child); 
			        	 }
			        }
			        
			        
			        
		            XMLOutputter outputter = new XMLOutputter();
		            Format format=Format.getPrettyFormat();
		            format.setEncoding("UTF-8");
		            outputter.setFormat(format);
		            treexml.append(outputter.outputString(myDocument));
			        
			    }else
				   treexml.append(loadAllNodes(type,module,res_flag,req));
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
	private String loadAllNodes(String type,String module,String res_flag,HttpServletRequest req)
	{
		StringBuffer strcontent=new StringBuffer();
		String href=req.getParameter("href");
		this.select_id=req.getParameter("select_id");
		boolean bflag=true;
		if(href==null|| "".equals(href))//未定义此参数时,控制是否要超链接操作
			bflag=false;
		String dr=req.getParameter("dr");
		Connection conn;

        UserView userView = (UserView) req.getSession().getAttribute(WebConstant.userView);	
		try
		{
			conn =  AdminDb.getConnection();
			ContentDAO dao=new ContentDAO(conn);
			String unit_type=null;
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
			unit_type=sysbo.getValue(Sys_Oth_Parameter.UNITTYPE,"type");//单位性质
			if(unit_type==null|| "".equals(unit_type))
				unit_type="3";
			//String operationcode=sysbo.getValue(Sys_Oth_Parameter.GOBROAD,"operationcode");//单位性质&自动计算公式--出国政审业务模板,如果选中则模板设置中不显示
	        Element root = new Element("TreeNode");
	        root.setAttribute("id","$$00");
	        root.setAttribute("text","root");
	        root.setAttribute("title","root");
	        Document myDocument = new Document(root);

			String units[]=unit_type.split(",");
			ArrayList<Element> elementArrayList=this.getChildOperation(units, type, dr, dao, bflag, res_flag, userView);

			for (Element element : elementArrayList) {
				root.addContent(element);
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
		return strcontent.toString();
	}
	
	private ArrayList<Element> getChildOperation(String[] units,String type,String dr,ContentDAO dao,boolean bflag,String res_flag,UserView userView){
		StringBuffer strsql=new StringBuffer();
		ArrayList<Element> element_list=new ArrayList<Element>();
		ArrayList<Object> parameter_list=new ArrayList<Object>();
		strsql.append(" SELECT operationcode,operationname ,operationid FROM operation  where ");
		if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
			strsql.append(" static_o =? ");
		}else {
			strsql.append(" static =? ");
		}
		parameter_list.add(type);

		/**
		 * 控制是否去掉人员调入业务分类
		 *=0去掉
		 *=1保留
		 *=2只保留业务参数
		 */
		if("0".equalsIgnoreCase(dr))
			strsql.append(" and operationtype<>0 ");
		else if("2".equalsIgnoreCase(dr))
			strsql.append(" and operationtype=1" );

		strsql.append(" and operationcode in ( SELECT OperationCode FROM Template_table WHERE 1=1  ");
		if(units.length>0) {
            strsql.append(" and  flag in (");
            for (String unit : units) {
                strsql.append("?,");
                parameter_list.add(unit);
            }
            strsql.append("0) ");
        }
		RowSet rset=null;
		strsql.append(" ) order by operationcode, operationid");
		try{
			rset=dao.search(strsql.toString(),parameter_list);
			/**业务分类*/
			while(rset.next())
			{
	            Element child = new Element("TreeNode");
	            child.setAttribute("id", rset.getString("operationcode"));
	            child.setAttribute("text", rset.getString("operationname"));
	            child.setAttribute("title", rset.getString("operationname"));
				child.setAttribute("icon","/images/prop_ps.gif");
				if(getTemplates(units,rset.getString("operationcode"),res_flag,userView,bflag,child,dao)){
					element_list.add(child);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
            PubFunc.closeDbObj(rset);
		}
		return element_list;
	}
	private boolean getTemplates(String[] units,String module,String res_flag,UserView userView,boolean bflag, Element parent,ContentDAO dao)
	{
		StringBuffer strsql=new StringBuffer();
		strsql.append("select tabid,name from template_table where operationcode='");
		strsql.append(module);
		strsql.append("' and (");
		for(int i=0;i<units.length;i++)
		{
			strsql.append(" flag ="+Integer.parseInt(units[i]));
			if(i<units.length-1)
				strsql.append(" or ");
		}	
		if(units.length>0)
			strsql.append(" or flag =0 "); //不分单位性质
		
		strsql.append(") order by tabid");
		RowSet rset=null;
		boolean bhave=false;
		try
		{
			rset=dao.search(strsql.toString());
			while(rset.next())
			{			
				//资源权限校验不对
				String tabid = rset.getString("tabid");
				//if (!userView.isHaveResource(Integer.parseInt(res_flag), rset.getString("tabid")))
					//continue;
				boolean flag = false;
				if (userView.isHaveResource(IResourceConstant.RSBD,tabid)) {
					flag = true;
				}else if (!userView.isHaveResource(IResourceConstant.GZBD,tabid)) {
					flag = true;
				}else if (!userView.isHaveResource(IResourceConstant.INS_BD,tabid)) {
					flag = true;
		      	}else if (!userView.isHaveResource(IResourceConstant.PSORGANS,tabid)) {
		      		flag = true;
		      	}else if (!userView.isHaveResource(IResourceConstant.PSORGANS_FG, tabid)) {
		      		flag = true;
		      	}else if (!userView.isHaveResource(IResourceConstant.PSORGANS_GX,tabid)) {
		      		flag = true;
		      	}else if (!userView.isHaveResource(IResourceConstant.PSORGANS_JCG,tabid)) {
		      		flag = true;
		      	}else if (!userView.isHaveResource(IResourceConstant.ORG_BD, tabid)) {
		      		flag = true;
		        }else if (!userView.isHaveResource(IResourceConstant.POS_BD, tabid)) {
		        	flag = true;
		        }
				if(!flag){
					continue;
				}
				Element child = new Element("TreeNode");    		
				child.setAttribute("id", rset.getString("tabid"));
	            child.setAttribute("text", rset.getString("tabid")+":"+rset.getString("name"));
	            child.setAttribute("title", rset.getString("name"));
	            child.setAttribute("target", "mil_body");	

				if(!bflag)//加上超链接
				{
					child.setAttribute("href", "javascript:void(0)");					
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
            ex.printStackTrace();
		}
		finally {
            PubFunc.closeDbObj(rset);
        }
		return bhave;
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
			StringBuffer buf=new StringBuffer();
			buf.append("select count(*) as nmax from tmessage where state=0 and noticetempid=");
			buf.append(tabid);
			buf.append(" and (b0110='");
			buf.append(userView.getUserOrgId());
			buf.append("' or b0110 is null or b0110='')");
			ContentDAO dao=new ContentDAO(conn);
			RowSet rset=dao.search(buf.toString());
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
}
