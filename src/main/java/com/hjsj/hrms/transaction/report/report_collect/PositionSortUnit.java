package com.hjsj.hrms.transaction.report.report_collect;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class PositionSortUnit extends IBusiness{
	public void execute()throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		 String unitcode=(String)hm.get("unitcode");
		 String tabid=(String)hm.get("tabid");
		 if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
		 String tsort=(String)hm.get("tsort");
		 String selfunitcode=(String)hm.get("selfUnitcode");
		 ArrayList list=new ArrayList();
		 String sql="select * from tt_organization where unitcode='"+selfunitcode+"' or unitcode='"+unitcode+"'";
		 String reporttypes="";
		 String unitname="";
		 ContentDAO dao=new ContentDAO(this.getFrameconn());
		 CommonData da=null;
		 LazyDynaBean ba=null;
		 String dmlsort="";
		 String undername="";
		 String underreport="";
		 boolean flag=false;
		 try {
			this.frowset=dao.search(sql);
			while(this.frowset.next()){
				if(this.frowset.getString("unitcode").equals(selfunitcode)){
					reporttypes=Sql_switcher.readMemo(this.frowset, "reporttypes");
					unitname=this.frowset.getString("unitname");
				}
				if(this.frowset.getString("unitcode").equals(unitcode)){
					undername=this.frowset.getString("unitname");
					if(Sql_switcher.readMemo(this.frowset, "reporttypes").length()!=0){
						underreport=Sql_switcher.readMemo(this.frowset, "reporttypes");
					}else{
						
					}
				}
			}
			sql="select * from tsort where TSortId in ("+reporttypes.substring(0,reporttypes.length()-1)+")";
			this.frowset=dao.search(sql);
			while(this.frowset.next()){
				da=new CommonData();
				ba=new LazyDynaBean();
				da.setDataName(this.frowset.getString("Name"));
				ba.set("dataName", this.frowset.getString("TsortId")+":"+this.frowset.getString("Name"));
				ba.set("dataValue", this.frowset.getString("TsortId"));
				da.setDataValue(this.frowset.getString("TsortId"));
				//list.add(da);
				list.add(ba);
			}
			if(tabid==null||tabid.length()==0){
				if(underreport.length()!=0){
					underreport=","+underreport;
					if(underreport.indexOf(","+tsort+",")!=-1){
						flag=true;
					}
				}
			}else{
				sql="select * from tname where tabid='"+tabid+"'";
				this.frowset=dao.search(sql);
				if(this.frowset.next()){
					dmlsort=this.frowset.getString("TsortId");
				}
				if(underreport.length()!=0){
					underreport=","+underreport;
					if(underreport.indexOf(","+dmlsort+",")!=-1){
						flag=true;
					}
				}
			}
			
			if(flag=true)
				this.getFormHM().put("selectunit", unitcode+"/"+undername);
			else{
				this.getFormHM().put("selectunit", "");
			}
			this.getFormHM().put("sortlist", list);
			if(tabid==null||tabid.length()==0){
				this.getFormHM().put("dmlsort", tsort);
			}else
				this.getFormHM().put("dmlsort", dmlsort);
			this.getFormHM().put("dmlunit", selfunitcode);
			this.getFormHM().put("dmlunitname", unitname);
			this.getFormHM().put("tabid", tabid);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
