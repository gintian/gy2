package com.hjsj.hrms.transaction.report.report_collect;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ShowSubUnits extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String unitcodes=(String)hm.get("unitcodes");
		String tabid=(String)hm.get("tabid");
		StringBuffer sql=new StringBuffer();
		sql.append("select tt.unitname,tn.Name from tname tn left join treport_ctrl tc on tn.tabid=tc.tabid left join tt_organization tt on tc.unitcode=tt.unitcode where tc.tabid='");
		sql.append(tabid);
		sql.append("' and tt.unitcode in(");
		//add by wangchaoqun on 2014-9-24 begin  直接传数据进入sql，没加单引号在oracle数据库中报‘无效数字’异常
		if(unitcodes!=null && !"".equals(unitcodes)){
			String[] newunitcodes = unitcodes.split(",");
			for(int i=0; i<newunitcodes.length; i++){
				if(i!=newunitcodes.length-1){
					sql.append("'"+newunitcodes[i]+"',");
				}else{
					sql.append("'"+newunitcodes[i]+"'");
				}
				
			}
		}
//		sql.append(unitcodes.substring(0,unitcodes.length()-1));
		//add by wangchaoqun on 2014-9-24 end
		sql.append(")");
		StringBuffer html=null;
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next()){
				html=new StringBuffer();
				html.append(this.frowset.getString(1));
				html.append("  未上报 ");
				html.append(this.frowset.getString(2));
				list.add(html.toString());
			}
			this.getFormHM().put("subunitsInfo", list);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
