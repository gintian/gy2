package com.hjsj.hrms.transaction.orginfo.leader;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveLeaderParameterTrans extends IBusiness {

	/**
	 * <?xml version="1.0" encoding="GB2312"?>
		<param >#
			<org_m>B0x</ org _m >#班子基本情况信息集
			<org_c>Bxx,Bxy,</ org _c>#班子其它信息集
			<emp_e link_field="Axxxx">Axx</ emp _e>#班子成员信息集
		</param>

	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub

		String org_m = (String)this.getFormHM().get("org_m");
		org_m=org_m==null?"":org_m;
		String org_c=(String)this.getFormHM().get("org_c");
		org_c=org_c==null?"":org_c;
		String emp_e=(String)this.getFormHM().get("emp_e");
		emp_e=emp_e==null?"":emp_e;
		String link_field = (String)this.getFormHM().get("link_field");
		link_field=link_field==null?"":link_field;
		String b0110 = (String)this.getFormHM().get("b0110");
		b0110=b0110==null?"":b0110;
		String orderby = (String)this.getFormHM().get("order_by");
		orderby=orderby==null?"":orderby;
		String leaderType = PubFunc.nullToStr((String)this.getFormHM().get("leaderType"));
		String sessionitem = PubFunc.nullToStr((String)this.getFormHM().get("sessionitem"));
		try{
			ConstantXml xml = new ConstantXml(this.frameconn,"ORG_LEADER_STRUCT");
			xml.ifNoParameterInsert("ORG_LEADER_STRUCT");
			xml.setValue("org_m", org_m);
			xml.setValue("org_c", org_c);
			xml.setValue("emp_e", emp_e);
			xml.setAttributeValue("/param/emp_e", "i9999", link_field.replaceAll("#", ""));
			xml.setAttributeValue("/param/emp_e", "b0110", b0110.replaceAll("#", ""));
			xml.setAttributeValue("/param/emp_e", "orderby", orderby.replaceAll("#", ""));
			xml.setAttributeValue("/param/org_m", "team_type", leaderType);
			xml.setAttributeValue("/param/org_m", "term", sessionitem);
			xml.saveStrValue();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
