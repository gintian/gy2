package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;

public class ShowDelUnitsTrans extends IBusiness{
	public void execute() throws GeneralException{
		HashMap hm= (HashMap)this.getFormHM().get("requestPamaHM");
		String unitcode=(String)hm.get("unitcode");
		if(unitcode.indexOf("UM")!=-1||unitcode.indexOf("UN")!=-1){
			unitcode=unitcode.substring(2);
		}
		ArrayList nlist =new ArrayList();
		try {
			AnalysePlanParameterBo appb=new AnalysePlanParameterBo(this.getFrameconn());
			appb.init();
			appb.setReturnHt(null);
			Hashtable ht=appb.analyseParameterXml();
			String pointset_menu=(String)ht.get("pointset_menu");
			nlist=this.searchlist(pointset_menu,unitcode);
			this.getFormHM().put("unitlist", nlist);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public ArrayList searchlist(String pointset_menu,String unitcode) throws SQLException{
		ArrayList hlist=new ArrayList();
		LazyDynaBean bean;
		ResultSet rs=null;
		Connection con=null;
		 String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
			String sql="select codeitemid ,codeitemdesc from  organization where parentid like'"+unitcode+"%' and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date and codesetid<>'@K' and codeitemid not in (select distinct(b0110) from "+pointset_menu+") order by codeitemid";
			try {
				con=AdminDb.getConnection();
				ContentDAO dao = new ContentDAO(con);
				rs=dao.search(sql);
				while(rs.next()){
					bean=new LazyDynaBean();
					bean.set("itemid", rs.getString("codeitemid"));
					bean.set("itemdesc", rs.getString("codeitemdesc"));
					hlist.add(bean);
				}
			} catch (GeneralException e) {
				e.printStackTrace();
			}finally{
				PubFunc.closeResource(rs);
				PubFunc.closeResource(con);
			}
		
		return hlist;
	
	}
}
