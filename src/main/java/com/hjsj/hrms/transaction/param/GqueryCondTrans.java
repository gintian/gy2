package com.hjsj.hrms.transaction.param;


import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GqueryCondTrans extends IBusiness {
	
	public void execute() throws GeneralException {
		//【8330】明星员工设置的过滤条件，显示的常用查询和员工管理中常用查询顺序不一致  jingq add 2015.04.02
		String sql="select id,name from lexpr where type='1' order by norder";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList list =new ArrayList();
		List g_conds=new ArrayList();
		
		
		String complex_expr="";
		String complex_id=(String)this.getFormHM().get("complex_id");	
		//系统管理参数设置系统参数，点击其他参数报错	jingq add 2014.09.20
		complex_id = PubFunc.keyWord_reback(complex_id);
		 if(complex_id==null||complex_id.length()<=0|| "#".equals(complex_id))
		 {
			 complex_id="";
		 }
		 if(complex_id!=null&&complex_id.length()>0)
		 {	 
			 String sql1="select id,name from lexpr where id='"+complex_id+"' order by norder";
			 try
			 {
				 this.frowset=dao.search(sql1);
				 if(this.frowset.next()){
					 complex_expr=this.frowset.getString("name");
					 complex_expr=this.frowset.getString("id");
				 }
			 }catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		 }
		/**常用查询条件列表*/
        try {
			this.frowset=dao.search(sql);
			while(this.frowset.next())
			{
				CommonData dataobj = new CommonData(this.frowset.getString("id"),this.getFrowset().getString("name"));
				list.add(dataobj);
			}
			
			String gquery_cond=(String)this.getFormHM().get("gquery_conds");
			String[] gquery_conds=gquery_cond.split(",");
			for (int i = 0; i < gquery_conds.length; i++) {
				if(gquery_conds[i]!=null&&gquery_conds[i].length()>0){
					sql="select name from lexpr where id="+gquery_conds[i];
					this.frowset=dao.search(sql);
					if(this.frowset.next())
					{	
						CommonData cd = new CommonData(gquery_conds[i],this.frowset.getString("name"));
						g_conds.add(cd);
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			this.getFormHM().put("complex_expr", complex_expr);		
			this.getFormHM().put("complex_id", complex_id);
			this.getFormHM().put("condlist", list);
			this.getFormHM().put("g_conds", g_conds);
		}
	}
}
