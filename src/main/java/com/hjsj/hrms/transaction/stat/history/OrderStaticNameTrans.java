package com.hjsj.hrms.transaction.stat.history;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 统计条件调整
 * <p>Title:OrderStaticItemTrans.java</p>
 * <p>Description>:OrderStaticItemTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 28, 2010 9:21:41 AM</p>
 * <p>@version: 4.0</p>
 * <p>@author: s.xin
 */
public class OrderStaticNameTrans extends IBusiness{
	
	 
	  public void execute() throws GeneralException 
	  {
		  HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		  String infokind=(String)hm.get("infor_Flag");
		  String statid = (String)hm.get("statid");
		  infokind=infokind==null||infokind.length()<=0?"1":infokind;
		  String sql = "";
		  if (statid == null || "".equals(statid) || "-1".equals(statid))
			  sql="select id,name from hr_hisdata_sname where infokind='"+infokind+"' order by snorder";
		  else 
			  sql="select norder,legend from HR_HISDATA_SLEGEND where id='"+statid+"' order by norder";
		 ContentDAO dao=new ContentDAO(this.getFrameconn());
		 ArrayList list=new ArrayList();
		 try {
			this.frowset=dao.search(sql);
			while(this.frowset.next())
			{
					CommonData da=new CommonData();
					if (statid == null || "".equals(statid) || "-1".equals(statid)){
						da.setDataName(this.frowset.getString("name"));
						da.setDataValue(this.frowset.getString("id"));
					}
					else{
						da.setDataName(this.frowset.getString("legend"));
						da.setDataValue(this.frowset.getString("norder"));
					}
					list.add(da);
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.getFormHM().put("infor_Flag", infokind);
		this.getFormHM().put("orderlist", list);
	  }

}
