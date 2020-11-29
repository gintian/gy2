package com.hjsj.hrms.businessobject.gz.gz_self.tax;

import com.hjsj.hrms.businessobject.sys.options.ParseSYS_OTH_PARAM;
import com.hrms.frame.dao.ContentDAO;
import org.jdom.Element;
import org.jdom.JDOMException;

import javax.sql.RowSet;
import java.io.IOException;
import java.sql.SQLException;

public class ParaseGz_tax_mode extends ParseSYS_OTH_PARAM{
	public ParaseGz_tax_mode(ContentDAO dao) throws JDOMException, IOException{
		super();
		String str="";
		try {
			str = this.getGz_tax_modeStr(dao);
			if(str==null||str.length()<1){
				StringBuffer  sbxml=new StringBuffer();
				sbxml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
				sbxml.append("<params TaxModeCodeSet=\"46\" SalaryCode=\"\" OneBonusCode=\"2\" LaborCode=\"4\"/>");
				str=sbxml.toString();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			super.getParamXml(str);
		
	}
	public String  getGz_tax_mode() throws JDOMException{
		Element el=this.getRootUri();
		
		String TaxModeCodeSet=(String)el.getAttributeValue("TaxModeCodeSet");
//		System.out.println(TaxModeCodeSet);
		return TaxModeCodeSet.toString();
	}
	private String getGz_tax_modeStr(ContentDAO dao) throws SQLException{
		String sql="select * from constant where constant ='GZ_TAX_MODE'";
		RowSet rs=dao.search(sql);
		String str_value="";
		if(rs.next()){
			str_value=rs.getString("str_value");
		}
		return str_value;
	}
}
