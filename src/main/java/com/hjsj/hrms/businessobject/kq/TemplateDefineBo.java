package com.hjsj.hrms.businessobject.kq;

import com.hjsj.hrms.businessobject.general.template.workflow.NodeType;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

public class TemplateDefineBo {
   private Connection conn;
   private String tabid;
   private UserView userview;
   public  TemplateDefineBo(Connection conn, String tabid,UserView userview)
   {
	   this.conn = conn;
		this.tabid = tabid;
		this.userview=userview;
   }
   public Define_WF_Node getWF_StartNode()
   {
	   ContentDAO dao=new ContentDAO(this.conn);
	   Define_WF_Node start_node=null;
	   RowSet rs=null;
		try
		{
			start_node=new Define_WF_Node(this,this.conn);
			StringBuffer strsql=new StringBuffer();
			strsql.append("select * from t_wf_node where nodetype='");
			strsql.append(NodeType.START_NODE);
			strsql.append("' and tabid=");
			strsql.append(this.tabid);
			rs=dao.search(strsql.toString());
			if(rs.next())
			{
				
				start_node.setNode_id(rs.getInt("node_id"));
				start_node.setNodename(rs.getString("nodename"));
				start_node.setNodetype(Integer.parseInt(rs.getString("nodetype")));
				start_node.setExt_param(Sql_switcher.readMemo(rs,"ext_param"));		
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			
		}finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return start_node;
   }
   /**
    * 得到业务url
    * @param param_name
    * @return
    */
   public String getDefineUrl(String param_name)
	{
		StringBuffer sql=new StringBuffer();		
		sql.append("select ctrl_para from t_wf_define ");
		sql.append(" where tabid ='"+this.tabid+"'");
		ContentDAO dao=new ContentDAO(this.conn);
		String upl_addr="";
		String ctrl_para="";
		RowSet rs=null;
		try
		{
			rs=dao.search(sql.toString());
			if(rs.next())
				ctrl_para=rs.getString("ctrl_para");
			if(ctrl_para!=null&&ctrl_para.length()>0)
			{
				Document doc=PubFunc.generateDom(ctrl_para);
				String xpath="/params/"+param_name+"";
				XPath reportPath = XPath.newInstance(xpath);//取得子集结点
				List childlist=reportPath.selectNodes(doc);
			    Iterator t = childlist.iterator();
			    if(t.hasNext())
			    {
			    	Element element=(Element)t.next();
			    	upl_addr=element.getAttributeValue("url");			    				    	
			    }
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return upl_addr;
	}
public String getTabid() {
	return tabid;
}
public void setTabid(String tabid) {
	this.tabid = tabid;
}
}
