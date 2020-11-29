package com.hjsj.hrms.transaction.report.report_collect;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class ValidateTsortTrans extends IBusiness{
	public void execute() throws GeneralException{
		String unitcode=(String)this.getFormHM().get("unitcode");
		String tsort=(String)this.getFormHM().get("tsort");
		String unitcodes=(String)this.getFormHM().get("selectUnitcodes");
		String sql="select count(*) from treport_ctrl where tabid in (select tabid from tname where TSortId='"+tsort+"') and unitcode='"+unitcode+"' and status=1";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		int k=0;
		try {
			this.frowset=dao.search(sql);
			if(this.frowset.next()){
				String count=this.frowset.getString(1);
				if("0".equals(count)){
					this.getFormHM().put("pass", "true");
				}else{
					this.getFormHM().put("pass", "false");
				}
			}
			this.getFormHM().put("tsort", tsort);
			unitcodes=unitcodes.substring(0,unitcodes.length()-1);
			String un[]=unitcodes.split(",");
			for(int i=0;i<un.length;i++){
				if(this.isReject(un[i], tsort))
					k++;
			}
			if(k==0){
				this.getFormHM().put("filter", "false");
			}else{
				this.getFormHM().put("filter", "true");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public boolean isReject(String unitcode,String tsort){
		boolean reject=false;
		String sql="select count(*) from treport_ctrl where tabid in (select tabid from tname where TSortId='"+tsort+"') and unitcode='"+unitcode+"' and status=1";
		ContentDAO dao=new ContentDAO(this.frameconn);
	
		try {
			this.frowset=dao.search(sql);
			if(this.frowset.next()){
				int n=this.frowset.getInt(1);
				if(n==0){
					
				}else{
					reject=true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reject;
		
	}
}
