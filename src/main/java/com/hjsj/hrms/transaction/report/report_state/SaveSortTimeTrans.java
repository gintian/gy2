package com.hjsj.hrms.transaction.report.report_state;

import com.hjsj.hrms.businessobject.report.auto_fill_report.AnalyseParams;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveSortTimeTrans extends IBusiness{
	public void execute() throws GeneralException{
		String sortAndTime=(String)this.getFormHM().get("valueAndTime");
		if(sortAndTime!=null)
			sortAndTime=PubFunc.keyWord_reback(sortAndTime);
		try{
		if(sortAndTime!=null&&sortAndTime.length()!=0){
			String[] sortTime=sortAndTime.split(";");
			if(sortTime!=null&&sortTime.length!=0){
				for(int i=0;i<sortTime.length;i++){
					String st=sortTime[i];
					if(st!=null&&st.length()>0){
						String stA[]=st.split("`");
						if(stA.length==1){
							this.saveSortTime(stA[0], "");
						}else
							this.saveSortTime(stA[0], stA[1]);
					}
				}
			}
			
		}else{
			
		}
		this.getFormHM().put("ok", "ok");
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public boolean saveSortTime(String type,String time){
		boolean falg=true;
		ArrayList list=new ArrayList();
		list.add("belongdate ");
		String xxml = "";
		String temp="";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset=dao.search("select STR_VALUE  from CONSTANT where CONSTANT='RP_PARAM'");
			if(this.frowset.next()){
				xxml = Sql_switcher.readMemo(this.frowset,"STR_VALUE");
			}
			AnalyseParams ap = new AnalyseParams(xxml);
			if(xxml == null || "".equals(xxml)){
				temp=ap.createBelongDate(type, time);
			}
			else{
				if(ap.isExitsNode("belongdates")){
					if(ap.checkBelongdateSortid(type)){
						temp=ap.updateBelongDate(type, time);
					}else{
						temp=ap.addBelongDate(type, time);
					}
				}else{
					temp=ap.createBelongDate(type, time);
				}
			}
			StringBuffer sql = new StringBuffer();
			sql.append("update CONSTANT set STR_VALUE='");
			sql.append(temp);
			sql.append("' where CONSTANT = 'RP_PARAM'");
			dao.update(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return falg;
	}
}
