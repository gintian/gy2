package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;

public class ChangePointTrans extends IBusiness{
	public void execute()throws GeneralException{
		ContentDAO dao=new ContentDAO(this.frameconn);
		CommonData cd;
		ArrayList plist=new ArrayList();
		ArrayList alllist=new ArrayList();
		String lag=(String)this.getFormHM().get("lag");
		String points=(String)this.getFormHM().get("kpoints");
		String[] temp=points.split(",");
		String sql="select * from fielditem where fieldsetid='"+temp[0]+"' and USEFLAG='1'";
		try {
			this.frowset=dao.search(sql);
			StringBuffer innerhtml=new StringBuffer();
			CommonData cdq=new CommonData("-1","请选择");
			plist.add(cdq);
			innerhtml.append(" 	<fieldset align=\"center\" style=\"width:100%\"><legend>其他显示指标</legend><div style='height:140;width:100%; overflow: auto;' ><table width=\"100%\" border='0' cellspacing=\"0\"  align=\"center\"  valign=\"top\" cellpadding=\"0\" class=\"ListTable\" id='targetCollectTable'>");
			while(this.frowset.next()){
				cd=new CommonData();
				cd.setDataName(this.frowset.getString("itemdesc"));
				cd.setDataValue(this.frowset.getString("itemid"));
				if("a".equalsIgnoreCase(this.frowset.getString("itemtype"))&& "0".equals(this.frowset.getString("codesetid"))){
					if("2".equalsIgnoreCase(this.userView.analyseFieldPriv(this.frowset.getString("itemid")))){
						plist.add(cd);
					}else{
						
					}
				}
				if((this.getFrowset().getString("itemid").equals(temp[1])||this.getFrowset().getString("itemid").equals(temp[2]))&&!"1".equalsIgnoreCase(lag)){
					continue;
				}
				if("2".equalsIgnoreCase(this.userView.analyseFieldPriv(this.frowset.getString("itemid")))){
					innerhtml.append("<tr>");
					innerhtml.append("<td align='center' class=\"RecordRow_right\" nowrap width=\"15%\">");
					innerhtml.append("<input type=\"checkbox\" name=\"allitems\" value=\""+this.getFrowset().getString("itemid")+"\"/>");
					innerhtml.append("</td>");
					innerhtml.append("<td align='left' class=\"RecordRow_left\" nowrap >");
					innerhtml.append(this.frowset.getString("itemdesc"));
					innerhtml.append("</td></tr>");
				}else{
					
				}
			}
			innerhtml.append("</table></div></fieldset>");
			this.getFormHM().put("innerhtml", SafeCode.encode(innerhtml.toString()));
			if("1".equals(lag)){
				this.getFormHM().put("khpidlist", plist);
				this.getFormHM().put("khpnamelist", plist);
			}
			this.getFormHM().put("klag", lag);
			this.getFormHM().put("alllist", alllist);
		} catch (SQLException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
