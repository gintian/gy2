/**
 * <p>Title:</p> 
 * <p>Description:薪资预算-计算公式-获取、保存xml数据</p> 
 * <p>Company:HJHJ</p> 
 * <p>Create time:${date}:${time}</p> 
 * <p>@version: 5.0</p>
 * <p>@author wangrd</p>
*/

package com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.formula;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BudgetFormulaXmlBo {
	private Connection conn = null;
	ContentDAO dao=null;
	private String formula_id="";//公式ID
	String rowcolflag="1";//行列标志
	String rowrange="";//行范围
	String colrange="";//列范围
	String formulacontent="";//公式内容
	String tj_where="";// 统计条件	
	
	public  BudgetFormulaXmlBo(Connection _con,String _formula_id){
		conn=_con;
		dao = new ContentDAO(this.conn);
		this.formula_id=_formula_id;
		initXmlParam();
	}

   private String getxmlcontent(){
		String xmlContext = "";
		  
		String sql =  "select extAttr from gz_budget_formula  where formula_id="+ formula_id + "";
		ContentDAO dao=new ContentDAO(this.conn);
		ResultSet rs = null;
		try
		{
		    rs = dao.search(sql);    
		
		    if (rs.next())
		    {
		    	xmlContext = Sql_switcher.readMemo(rs, "extAttr"); // PubFunc.nullToStr(rs.getString("parameter_content"));
			    }		    
		
			} catch (Exception e)
			{
			    e.printStackTrace();
			} 
		   
		return xmlContext;   
   }
	
	
	private void initXmlParam(){
		try {
			String StrValue="";
			StrValue = getxmlcontent();
	    	if (StrValue == null || StrValue.trim().length()<=5){
                 return;
	    	 } 
	    	else {
	    		Document doc = PubFunc.generateDom(StrValue);
	    		String xpath = "//formula";
	    		XPath xpath_ = XPath.newInstance(xpath);
	    		Element ele = (Element) xpath_.selectSingleNode(doc);
	    		if (ele != null)
	    		{
	    			if (ele.getChild("rowcolflag") !=null){
	    				this.rowcolflag=ele.getChild("rowcolflag").getTextTrim(); 			  
	    			}
	    		  if (ele.getChild("colrange") !=null){
	    			  this.colrange=ele.getChild("colrange").getTextTrim(); 			  
	    		  }
	    		  if (ele.getChild("rowrange") !=null){
	    			  this.rowrange=ele.getChild("rowrange").getTextTrim(); 	
	  	    			  
	    		  }
	    		  if (ele.getChild("content") !=null){
	    			  this.formulacontent=ele.getChild("content").getTextTrim(); 	 			  
	    		  }
	    		  if (ele.getChild("tj_where") !=null){
	    			  this.tj_where=ele.getChild("tj_where").getTextTrim(); 			  
	    		  }	   			
	    		}	    		
	     	}	    		

	  	 } catch(Exception e) {
				e.printStackTrace();
		   }	
	return;
  }	
	
	private void SaveXmlParam(){
		String StrValue="";
		PreparedStatement ps = null;
		DbSecurityImpl dbS = new DbSecurityImpl();
		Element ele =null;
		try {
			Element root = null;
	
			root = new Element("formula");
			
			ele = new Element("rowcolflag");
			ele.setText(getRowcolflag());
			root.addContent(ele);
			
			ele = new Element("colrange");
			ele.setText(getColrange());
			root.addContent(ele);
			
			ele = new Element("rowrange");
			ele.setText(getRowrange());
			root.addContent(ele);
			
			ele = new Element("content");
			ele.setText(getFormulacontent());
			root.addContent(ele);
			
			ele = new Element("tj_where");
			ele.setText(getTj_where());
			root.addContent(ele);      		

			Document myDocument = new Document(root);
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			StrValue = outputter.outputString(myDocument);
 			
			String sql = "update gz_budget_formula set extAttr=?  where formula_id="+ formula_id;
			ps = this.conn.prepareStatement(sql);	
			switch(Sql_switcher.searchDbServer())
			{
				case Constant.MSSQL:
					ps.setString(1, StrValue);
					break;
				case Constant.ORACEL:
					ps.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(StrValue.
					          getBytes())), StrValue.length());
					break;
				case Constant.DB2:
					ps.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(StrValue.
					          getBytes())), StrValue.length());
					break;
			}
			
			// 打开Wallet
			dbS.open(conn, sql);
			ps.executeUpdate();					
		
			if(ps!=null)
				ps.close();		
		
		
		} catch(Exception e) {
				e.printStackTrace();
		 }finally{
			 try {
				// 关闭Wallet
				dbS.close(conn);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			 PubFunc.closeResource(ps);
		 }
  }
	
	
	public String getRowrange() {
		return rowrange;
	}

	public void setRowrange(String rowrange) {
		this.rowrange = rowrange;
		SaveXmlParam();
	}

	public String getColrange() {
		return colrange;
	}

	public void setColrange(String colrange) {
		this.colrange = colrange;
		SaveXmlParam();
	}

	public String getFormulacontent() {
		return formulacontent;
	}

	public void setFormulacontent(String formulacontent) {
		this.formulacontent = formulacontent;
		SaveXmlParam();
	}

	public String getTj_where() {
		return tj_where;
	}

	public void setTj_where(String tj_where) {
		this.tj_where = tj_where;
		SaveXmlParam();
	}

	public String getRowcolflag() {
		return rowcolflag;
	}

	public void setRowcolflag(String rowcolflag) {
		this.rowcolflag = rowcolflag;
	}

}
