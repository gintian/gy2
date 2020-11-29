package com.hjsj.hrms.interfaces.train;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TrainPlanTree {
	private UserView userView;
	private String classId;
	public UserView getUserView() {
		return userView;
	}
	public void setUserView(UserView userView) {
		this.userView = userView;
	}
	public TrainPlanTree(String classId) {
		this.classId = classId;
	}
	public String outPutXmlStr() {
		StringBuffer xml = new StringBuffer();
		ResultSet rs = null;
		Connection conn = null;
		RowSet rsc = null;
		try {
			String b0110 = "";
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			if(classId!=null&&!"".equals(classId)){
				rsc = dao.search("select b0110 from r31 where r3101="+classId);
				if(rsc.next()){
					b0110 = rsc.getString("b0110");
				}
			}
			// 创建xml文件的根元素
			Element root = new Element("TreeNode");
			// 设置根元素属性
			root.setAttribute("id", "");
			root.setAttribute("text", "root");
			root.setAttribute("title", "trainType");
			// 创建xml文档自身
			Document myDocument = new Document(root);
			 StringBuffer sql=new StringBuffer();
			 sql.append("select R2501,R2502 from R25 where R2509 in ('03','04')");
			 if(b0110 == null || "".equals(b0110)){
				 ArrayList list=new ArrayList();
				 if(this.userView!=null&&!this.userView.isSuper_admin())
				 {
	//				 String a_code = this.userView.getUnit_id();
	//				 a_code = PubFunc.getTopOrgDept(a_code);
					 TrainCourseBo bo = new TrainCourseBo(this.userView);
					 String a_code = bo.getUnitIdByBusi();
					 if(a_code!=null&&a_code.length()>3)
					 {
						 String[] strS=StringUtils.split(a_code,"`");
						 for(int i=0;i<strS.length;i++){
							 String code=strS[i];
							 if("UN".equalsIgnoreCase(code.substring(0,2)))
							 {
								 list.add("b0110 like '"+code.substring(2)+"%'");
							 }else if("UM".equalsIgnoreCase(code.substring(0,2)))
							 {
								 list.add("e0122 like '"+code.substring(2)+"%'");
							 }
						 }
					 }//else if(this.userView.getManagePrivCode().equalsIgnoreCase("UN"))
	//				 {
	//					 list.add("b0110='"+this.userView.getManagePrivCodeValue()+"'");
	//				 }else if(this.userView.getManagePrivCode().equalsIgnoreCase("UM"))
	//				 {
	//					 list.add("e0122='"+this.userView.getManagePrivCodeValue()+"'");
	//				 }
				 }
				 if(list.size()>0)
				 {
					 sql.append(" and (");
					 for(int i=0;i<list.size();i++)
					 {
						 sql.append(" "+list.get(i)+" or ");
					 }
					 sql.setLength(sql.length()-3);
					 sql.append(")");
				 }
			 } else {
				 sql.append(" and b0110='"+b0110+"'");
			 }
		     rs=dao.search(sql.toString());
			 while (rs.next()){
				String r2501 = rs.getString("R2501");
				String R2502 = rs.getString("R2502");
				R2502 = StringUtils.isEmpty(R2502) ? "" : R2502;
				
				Element child = new Element("TreeNode");

				 child.setAttribute("id",r2501);
		         child.setAttribute("text",R2502);
		         child.setAttribute("title",R2502);
		         child.setAttribute("xml", "aa");
		         child.setAttribute("icon","/images/table.gif");
		         root.addContent(child);
			}

			XMLOutputter outputter = new XMLOutputter();
			// 格式化输出类
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);

			// 将生成的XML文件作为字符串形式
			xml.append(outputter.outputString(myDocument));

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
            try{
            	
	              if (rs != null){
	            	  rs.close();
	              }
	            
	              if (conn != null){
	                conn.close();
	              }
	            }catch (SQLException ee){
	              ee.printStackTrace();
	            }
	            
	        }
		return xml.toString();
	}
}
