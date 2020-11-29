package com.hjsj.hrms.transaction.general.inform.informcheck;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
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
public class SetFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String infor = (String)hm.get("infor");
		infor=infor!=null&infor.trim().length()>0?infor:"1";
		hm.remove("infor");

		this.getFormHM().put("formulastr",tableStr(infor));
		this.getFormHM().put("infor",infor);
		ArrayList fieldlist = fieldList(infor);
		this.getFormHM().put("fieldlist",fieldlist);
		this.getFormHM().put("listfield",fieldlist);

	}
	private String tableStr(String infor){
		ContentDAO dao  = new ContentDAO(this.frameconn);
		StringBuffer tablestr = new StringBuffer();
		StringBuffer formulaarr = new StringBuffer();
		StringBuffer itemidarr = new StringBuffer();

		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select fieldsetid,itemid,itemtype,Expression,AuditingInformation,AuditingFormula from fielditem where fieldsetid in(");
		if("1".equals(infor)){
			sqlstr.append("select fieldsetid from fieldset where fieldsetid like 'A%' and useflag='1'");
		}else if("2".equals(infor)){
			sqlstr.append("select fieldsetid from fieldset where fieldsetid like 'B%' and useflag='1'");
		}else if("3".equals(infor)){
			sqlstr.append("select fieldsetid from fieldset where fieldsetid like 'K%' and useflag='1'");
		}
		sqlstr.append(") and (AuditingInformation is not null or AuditingFormula is not null)");
		
		tablestr.append("<table width=\"100%\" id=\"itemTr\" border=\"0\" cellspacing=\"0\" style=\"border-right:none;\" cellpadding=\"0\" class=\"ListTable1\">");
		tablestr.append("<tr class=\"fixedHeaderTr1\"><td width=\"15%\" height=\"20\" class=\"TableRow\" style=\"border-left:none;\" nowrap>&nbsp;</td>");
		tablestr.append("<td class=\"TableRow\" align=\"center\" style=\"border-right:none;\" nowrap>"+ResourceFactory.getProperty("workdiary.message.infor.review")+"</td></tr>");
		try {
			this.frowset = dao.search(sqlstr.toString());
			int i=1;
			while(this.frowset.next()){
				String expression = this.frowset.getString("Expression");
				expression=expression!=null&&expression.trim().length()>0?expression:"";
				
				String AuditingInformation = this.frowset.getString("AuditingInformation");
				AuditingInformation=AuditingInformation!=null&&AuditingInformation.trim().length()>0?AuditingInformation:"";
				
				String AuditingFormula = this.frowset.getString("AuditingFormula");
				AuditingFormula=AuditingFormula!=null&&AuditingFormula.trim().length()>0?AuditingFormula:"";
				
				if(expression.length()<1&&(AuditingInformation.length()>0||AuditingFormula.length()>0)){
					String fieldsetid = this.frowset.getString("fieldsetid");
					String itemid = this.frowset.getString("itemid");
					if("0".equals(this.userView.analyseFieldPriv(itemid)))
						continue;
					tablestr.append("<tr><td align=\"center\" class=\"RecordRow\" style=\"border-left:none;\">");
					tablestr.append(i);
					tablestr.append("</td><td class=\"RecordRow\" style=\"border-right:none;\">");
					tablestr.append("<input type=\"text\" class=\"text4\" name=\"");
					tablestr.append(itemid+"_"+i);
					tablestr.append("\"  size=\"23\" value=\"");
					tablestr.append(AuditingInformation+"\"");
					//tablestr.append(" readOnly=\"false\" ondblclick=\"onEdite(this);\"");
					//tablestr.append(" onblur=\"onLeve(this)\"");
					tablestr.append(" onkeydown=\"viewSaveButton();\" onclick=\"onSelect('"+itemid+"_"+i+"');\">");
					tablestr.append("</td></tr>");
					formulaarr.append(AuditingFormula+"`");
					itemidarr.append(fieldsetid+":"+itemid+":"+AuditingInformation+"`");
					i++;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tablestr.append("</table>");
		String formula = formulaarr.toString();
		formula=formula!=null&&formula.trim().length()>0?formula:"";
		if(formula.trim().length()>0)
			formula = formula.substring(0,formula.length()-1);
		String id = itemidarr.toString();
		id=id!=null&&id.trim().length()>0?id:"";
		if(id.trim().length()>0)
			id = id.substring(0,id.length()-1);
		this.getFormHM().put("formulaarr",SafeCode.encode(formula));
		this.getFormHM().put("itemidarr",id);
		return tablestr.toString();
	}
	private ArrayList fieldList(String infor){
		ArrayList fieldlist = new ArrayList();
		ArrayList list = new ArrayList();
		if("1".equals(infor)){
			list = this.userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
		}else if("2".equals(infor)){
			list = this.userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
		}else if("3".equals(infor)){
			list = this.userView.getPrivFieldSetList(Constant.POS_FIELD_SET);
		}else{
			list = this.userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
		}
		for(int i=0;i<list.size();i++){
			FieldSet fieldset = (FieldSet)list.get(i);
			if("A00".equalsIgnoreCase(fieldset.getFieldsetid()))
				continue;
			if("B00".equalsIgnoreCase(fieldset.getFieldsetid()))
				continue;
			if("K00".equalsIgnoreCase(fieldset.getFieldsetid()))
				continue;
			CommonData dataobj = new CommonData(fieldset.getFieldsetid(),fieldset.getCustomdesc());
			fieldlist.add(dataobj);
		}
		CommonData dataobj = new CommonData("","");
		fieldlist.add(0,dataobj);
		return fieldlist;
	}
}
