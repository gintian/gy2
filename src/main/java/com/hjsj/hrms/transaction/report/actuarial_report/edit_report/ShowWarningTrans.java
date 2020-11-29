package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class ShowWarningTrans  extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			TTorganization ttOrganization=new TTorganization(this.getFrameconn());
			String unitcode=(String)this.getFormHM().get("unitcode");
			String id=(String)this.getFormHM().get("id");
			RecordVo vo = new RecordVo("u01");
			vo.setInt("id", Integer.parseInt(id));
			vo.setString("unitcode", unitcode);
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			vo=dao.findByPrimaryKey(vo);
			ArrayList listw = new ArrayList();
			LazyDynaBean bean = new LazyDynaBean(); 
			bean.set("unitcode", unitcode);
			bean.set("t3_desc", vo.getString("t3_desc"));
			bean.set("u0101", vo.getString("u0101"));
			bean.set("u0103", vo.getString("u0103"));
			bean.set("unitname", ttOrganization.getSelfUnit2(unitcode).getString("unitname"));
			listw.add(bean);
			
		
			ArrayList list =ttOrganization.getAllSubUnit(unitcode);
			for(int i=0;i<list.size();i++){
				String str =(String)list.get(i);
				if(str.indexOf("§")!=-1){
					LazyDynaBean	bean2=new LazyDynaBean(); 
					unitcode = str.substring(0,str.indexOf("§"));
					RecordVo vo2 = new RecordVo("u01");
					vo2.setInt("id", Integer.parseInt(id));
					vo2.setString("unitcode", unitcode);
					try{
					vo2=dao.findByPrimaryKey(vo2);
					}catch(Exception e){
						continue;
					}
					bean2.set("unitcode", unitcode);
					bean2.set("t3_desc", vo2.getString("t3_desc"));
					bean2.set("u0101", vo2.getString("u0101"));
					bean2.set("u0103", vo2.getString("u0103"));
					bean2.set("unitname", str.substring(str.indexOf("§")+1));
					listw.add(bean2);
				}
			}
			
			this.getFormHM().put("warninglist",listw);
		}
		catch(Exception e)
		{
			ArrayList listw = new ArrayList();
			LazyDynaBean bean = new LazyDynaBean(); 
			bean.set("unitcode", "");
			bean.set("t3_desc","");
			bean.set("u0101","");
			bean.set("u0103","");
			bean.set("unitname", "");
			listw.add(bean);
			this.getFormHM().put("warninglist",listw);
		}
	}
    
}
