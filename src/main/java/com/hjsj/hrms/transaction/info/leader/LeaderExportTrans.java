package com.hjsj.hrms.transaction.info.leader;

import com.hjsj.hrms.businessobject.general.deci.leader.LeadarParamXML;
import com.hjsj.hrms.businessobject.info.leader.LeaderUtils;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class LeaderExportTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String b0110 = (String)this.formHM.get("b0110");
		String i9999 = (String)this.formHM.get("i9999");
		String leadNext = (String)this.formHM.get("leadNext");
		String leaderTypeValue = (String)this.formHM.get("leaderTypeValue");
		String sessionValue = (String)this.formHM.get("sessionValue");
		String displayField = (String)this.formHM.get("displayFields");
		
		ConstantXml xml = new ConstantXml(this.frameconn,"ORG_LEADER_STRUCT");
		String org_m = xml.getValue("org_m");
		org_m=org_m==null?"":org_m;
		String emp_e = xml.getValue("emp_e");
		emp_e = emp_e==null?"":emp_e;
		String link_field = xml.getNodeAttributeValue("/param/emp_e", "i9999");
		link_field=link_field==null?"":link_field;
		String b0110Field = xml.getNodeAttributeValue("/param/emp_e", "b0110");
		b0110Field=b0110Field==null?"":b0110Field;
		String orderbyField = xml.getNodeAttributeValue("/param/emp_e", "orderby");
		orderbyField=orderbyField==null?"":orderbyField;
		String leaderType = (String)xml.getNodeAttributeValue("/param/org_m", "team_type");
		leaderType = leaderType==null||leaderType.trim().length()<1?"":leaderType;
		String sessionitem = (String)xml.getNodeAttributeValue("/param/org_m", "term");
		sessionitem = sessionitem==null || sessionitem.trim().length()<1?"":sessionitem;
		
		LeadarParamXML leadarParamXML=new LeadarParamXML(this.getFrameconn());
		//String displayField=leadarParamXML.getTextValue(LeadarParamXML.DISPLAY);
		
		
		ArrayList browsefields = new ArrayList();
		LeaderUtils lu = new LeaderUtils();
		ArrayList dblist = userView.getPrivDbList();
		String sql = lu.createLeaderInfoSql(org_m, emp_e, link_field, b0110Field, i9999,
				                              orderbyField, displayField, b0110, leadNext, leaderType,
				                              leaderTypeValue, sessionitem, sessionValue, browsefields,dblist,userView);
		String order_by = " order by "+b0110Field+","+orderbyField;
		String fileName = lu.createExcelFile(frameconn, sql+order_by, browsefields,this.getUserView());
		
		this.formHM.put("fileName", fileName);
		
	}
}
