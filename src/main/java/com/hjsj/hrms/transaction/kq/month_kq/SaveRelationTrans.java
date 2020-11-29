package com.hjsj.hrms.transaction.kq.month_kq;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
/**
 * 保存参数设置
 * <p>Title:SaveRelationTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:</p>
 * @author jinjiawei
 * @version 1.0
 * */
public class SaveRelationTrans extends IBusiness{

	private String xml;
	private Document doc;
	
	public void execute() throws GeneralException {
		String relationFlag = this.getFormHM().get("relationFlag").toString();
		String defaultFlag = this.getFormHM().get("defaultFlag").toString();
		
		ConstantXml constant = new ConstantXml(this.frameconn, "kq_monthly");
		try {
			// 保存审批关系

			//constant.setAttributeValue("/param/Kq_Parameters", "sp_relation",
			//		relationFlag);
			/// 保存考勤项目默认值
			//constant
			//		.setAttributeValue("/param/Kq_Parameters", "def_value", defaultFlag);

			// 保存
			//constant.saveStrValue();
			//this.saveXML(relationFlag, defaultFlag);
			saveOrDelParam(relationFlag,defaultFlag);
			this.getFormHM().put("saveStatus", "1"); //1 代表保存成功 
		} catch (Exception e) {
			e.printStackTrace();
			this.getFormHM().put("saveStatus", "2");//2 代表保存失败
		}
	}
	
	private void initXML(){
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rs = null;
		try{	
			//常量表中查找rp_param常量
			rs=dao.search("select str_value  from CONSTANT where UPPER(CONSTANT)='KQ_MONTHLY'");
			//if(rs.next()){
				//获取XML文件
			//	xml = Sql_switcher.readMemo(rs,"STR_VALUE");
				//System.out.println(xml);
			//}
		//	else
		//	{
				StringBuffer strxml=new StringBuffer();
				strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
				strxml.append("<param>");	
				strxml.append("</param>");
				xml=strxml.toString();
		//	}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			PubFunc.closeDbObj(rs);
		}
	}
	
	private void init() throws GeneralException{
		try {
			doc = PubFunc.generateDom(xml);
		} catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
	}
	public String getParam()
	{
		String str="";
		try{
			this.initXML();
			this.init();
			String path="/param";
			XPath xPath = XPath.newInstance(path);
			Element element=(Element)xPath.selectSingleNode(this.doc);
			if(element!=null)
			{
				str=element.getText();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	public void saveOrDelParam(String relation,String defValue)
	{
		PreparedStatement pstmt = null;	
		DbSecurityImpl dbs = new DbSecurityImpl(); 
		try
		{
			this.initXML();
			this.init();
			String path="/param";
			XPath xPath = XPath.newInstance(path);
			Element element=(Element)xPath.selectSingleNode(this.doc);
			if(element!=null)
			{
				this.doc.getRootElement().removeContent(element);
			}
			element = new Element("Kq_Parameters");
			element.setAttribute("sp_relation", relation);
			element.setAttribute("def_value",defValue);
			//element.setText("sp_relation = "+relation );
			this.doc.getRootElement().addContent(element);
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			String str_value= outputter.outputString(doc);
			StringBuffer strsql = new StringBuffer("");
			if(!isHave())
			{
				strsql.append("update constant set str_value=? where constant='KQ_MONTHLY'");
				System.out.println(strsql.toString());
				pstmt = this.frameconn.prepareStatement(strsql.toString());	
				switch(Sql_switcher.searchDbServer())
				{
				  case Constant.MSSQL:
				  {
					  pstmt.setString(1, str_value);
					  break;
				  }
				  case Constant.ORACEL:
				  {
					  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(str_value.
					          getBytes())), str_value.length());
					  break;
				  }
				  case Constant.DB2:
				  {
					  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(str_value.
					          getBytes())), str_value.length());
					  break;
				  }
			   }
			}else
			{
				strsql.append("insert into constant(constant,type,describe,str_value) values(?,?,?,?)");	
				pstmt = this.frameconn.prepareStatement(strsql.toString());				
				pstmt.setString(1, "KQ_MONTHLY");
				pstmt.setString(2, "A");
				pstmt.setString(3,"月度考勤期间");
				pstmt.setString(4,str_value);
			}
			dbs.open(this.frameconn, strsql.toString());
			pstmt.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
		    PubFunc.closeResource(pstmt);
			dbs.close(this.frameconn);
		}
	}
	
	public boolean isHave(){
		String sql = " select * from constant where constant = 'KQ_MONTHLY'";
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
}
