package com.hjsj.hrms.transaction.orginfo.leader;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;

public class InitParameterTrans extends IBusiness {

	public void execute() throws GeneralException {
		String leader = (String)this.getFormHM().get("leader");
		if("leader".equalsIgnoreCase(leader)){
			String org_m="";
		    String org_c="";
		    String emp_e="";
		    String link_field="";
		    String b0110="";
		    String orderby="";
		    //班子类别
		    ArrayList leaderTypeList = new ArrayList();
		    //届次
		    //ArrayList sessionitemList = new ArrayList();
			try{
				ConstantXml xml = new ConstantXml(this.frameconn,"ORG_LEADER_STRUCT");
				org_m = xml.getValue("org_m");
				org_m=org_m==null?"":org_m;
				if(org_m.length()==0)
					throw GeneralExceptionHandler.Handle(new GeneralException("",
							"请您在班子参数配置中设置班子信息集！", "", ""));
				org_c = xml.getValue("org_c");
				org_c = org_c==null?"":org_c;
				emp_e = xml.getValue("emp_e");
				emp_e = emp_e==null?"":emp_e;
				if(emp_e.length()==0)
					throw GeneralExceptionHandler.Handle(new GeneralException("",
							"请您在班子参数配置中设置班子成员信息！", "", ""));
				link_field = xml.getNodeAttributeValue("/param/emp_e", "i9999");
				link_field=link_field==null?"":link_field;
				b0110 = xml.getNodeAttributeValue("/param/emp_e", "b0110");
				b0110=b0110==null?"":b0110;
				orderby = xml.getNodeAttributeValue("/param/emp_e", "orderby");
				orderby=orderby==null?"":orderby;
				if(link_field.length()==0||b0110.length()==0)
					throw GeneralExceptionHandler.Handle(new GeneralException("",
							"请您在班子参数配置中设置班子成员信息关联指标！", "", ""));
				if(orderby.length()==0)
					throw GeneralExceptionHandler.Handle(new GeneralException("",
							"请您在班子参数配置中设置班子成员信息成员顺序指标！", "", ""));
				String leaderType = (String)xml.getNodeAttributeValue("/param/org_m", "team_type");
				leaderType = leaderType==null||leaderType.trim().length()<1?"":leaderType;
				this.getFormHM().put("leaderType", leaderType);
				String sessionitem = (String)xml.getNodeAttributeValue("/param/org_m", "term");
				sessionitem = sessionitem==null || sessionitem.trim().length()<1?"":sessionitem;
				this.getFormHM().put("sessionitem", sessionitem);
				
				getleaderTypeList(leaderTypeList,leaderType);
			}finally{
				this.getFormHM().put("org_m", org_m);
				this.getFormHM().put("org_c", org_c);
				this.getFormHM().put("emp_e", emp_e);
				this.getFormHM().put("link_field", link_field);
				this.getFormHM().put("b0110", b0110);
				this.getFormHM().put("order_by", orderby);
				
				this.getFormHM().put("leaderTypeList", leaderTypeList);
				this.getFormHM().put("leaderTypeValue", "all");
				this.getFormHM().put("sessionValue", "max");
				
			}
		}else if("org".equalsIgnoreCase(leader)){
			String org_m="";
		    String org_c="";
			try{
				ConstantXml xml = new ConstantXml(this.frameconn,"ORG_LEADER_STRUCT");
				org_m = xml.getValue("org_m");
				org_m=org_m==null?"":org_m;
				org_c = xml.getValue("org_c");
				org_c = org_c==null?"":org_c;
			}finally{
				this.getFormHM().put("org_m", org_m);
				this.getFormHM().put("org_c", org_c);
			}
		}
	}

	
	
	private void getleaderTypeList(ArrayList leaderTypeList,String typeitem){
		leaderTypeList.add(new CommonData("all", "全部"));
		FieldItem fi = DataDictionary.getFieldItem(typeitem);
		if(fi==null || "0".equals(fi.getUseflag())){
			return ;
		}
		
		String codesetid = fi.getCodesetid();
		
		String sql = "select codeitemid,codeitemdesc from codeitem where codesetid='"+codesetid+"'";
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				CommonData cd = new CommonData();
				cd.setDataName(this.frowset.getString("codeitemdesc"));
				cd.setDataValue(this.frowset.getString("codeitemid"));
				leaderTypeList.add(cd);
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
