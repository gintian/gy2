package com.hjsj.hrms.transaction.general.inform.informcheck;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class ViewCheckTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String infor = "";
		if(hm!=null){	
			infor=(String)hm.get("infor");
			infor=infor!=null&&infor.trim().length()>0?infor:"1";
			hm.remove("infor");
			
			String dbname = (String)hm.get("dbname");
			dbname=dbname!=null&&dbname.trim().length()>0?dbname:"";
			hm.remove("dbname");
			
			this.getFormHM().put("dbname",dbname);
			this.getFormHM().put("tablestr",tableStr(infor));
		}else{
			infor=(String)this.getFormHM().get("infor");
			infor=infor!=null&&infor.trim().length()>0?infor:"1";
			this.getFormHM().put("tablestr",SafeCode.encode(tableStr(infor)));
		}
		this.getFormHM().put("infor",infor);
	}
	private String tableStr(String infor){
		ContentDAO dao  = new ContentDAO(this.frameconn);
		StringBuffer tablestr = new StringBuffer();

		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select itemid,Expression,AuditingInformation,AuditingFormula from fielditem where fieldsetid in(");
		if("1".equals(infor)){
			sqlstr.append("select fieldsetid from fieldset where fieldsetid like 'A%' and useflag='1'");
		}else if("2".equals(infor)){
			sqlstr.append("select fieldsetid from fieldset where fieldsetid like 'B%' and useflag='1'");
		}else if("3".equals(infor)){
			sqlstr.append("select fieldsetid from fieldset where fieldsetid like 'K%' and useflag='1'");
		}
		sqlstr.append(") and (AuditingInformation is not null or AuditingFormula is not null)");
		
		tablestr.append("<table width=\"100%\" border=\"0\">");
		try {
			this.frowset = dao.search(sqlstr.toString());
			while(this.frowset.next()){
				String expression = this.frowset.getString("Expression");
				expression=expression!=null&&expression.trim().length()>0?expression:"";
				
				String AuditingInformation = this.frowset.getString("AuditingInformation");
				AuditingInformation=AuditingInformation!=null&&AuditingInformation.trim().length()>0?AuditingInformation:"";
				
				String AuditingFormula = this.frowset.getString("AuditingFormula");
				AuditingFormula=AuditingFormula!=null&&AuditingFormula.trim().length()>0?AuditingFormula:"";
				
				if(expression.length()<1&&(AuditingInformation.length()>0||AuditingFormula.length()>0)){
					tablestr.append("<tr><td>");
					tablestr.append("<input type=\"checkbox\" name=\"");
					tablestr.append(this.frowset.getString("itemid"));
					tablestr.append("\" value=\""+this.frowset.getString("itemid"));
					tablestr.append("\" checked>");
					tablestr.append(AuditingInformation);
					tablestr.append("</td></tr>");
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tablestr.append("<tr><td>&nbsp;</td></tr>");
		tablestr.append("</table>");
		return tablestr.toString();
	}

}
