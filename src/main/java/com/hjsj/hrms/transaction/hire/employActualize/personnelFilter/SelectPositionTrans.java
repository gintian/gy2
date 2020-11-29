package com.hjsj.hrms.transaction.hire.employActualize.personnelFilter;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

public class SelectPositionTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		ArrayList selectedList=(ArrayList)this.getFormHM().get("selectedList");
		String z0301=(String)this.getFormHM().get("z0301");
		try
		{
			StringBuffer whl=new StringBuffer("");
			StringBuffer whl2=new StringBuffer("");
			for(int i=0;i<selectedList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)selectedList.get(i);
				whl2.append(" or a0100='"+(String)abean.get("a0100")+"'");
				whl.append(" or  (a0100='"+(String)abean.get("a0100")+"' and zp_pos_id='"+z0301+"')");
			}
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.delete("delete from zp_pos_tache where "+whl2.substring(3),new ArrayList()); //删除所有以前选的职位
			dao.delete("delete from zp_test_template where "+whl2.substring(3),new ArrayList());
			{
				this.frowset=dao.search("select * from constant where constant='ZP_DBNAME'");
				String dbname="";
				if(this.frowset.next())
				{
					dbname=this.frowset.getString("str_value");
				}
				dao.update("update "+dbname+"A01 set state='10' where "+whl2.substring(3));
				dao.delete("delete from z05 where "+whl2.substring(3),new ArrayList());
			}
			
			String sql="insert into zp_pos_tache (a0100,zp_pos_id,thenumber,apply_date,status)"
					+" values(?,?,?,?,?)";
			ArrayList values = new ArrayList();
			//PreparedStatement stm=this.getFrameconn().prepareStatement(sql);
			Calendar calendar=Calendar.getInstance();
			Date date=new Date(new java.util.Date().getTime());
			
			for(int i=0;i<selectedList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)selectedList.get(i);
				values.add((String)abean.get("a0100"));
				values.add(z0301);
				values.add(1);
				values.add(date);
				values.add("0");
				dao.insert(sql,values);
				values.clear();
				/*stm.setString(1,(String)abean.get("a0100"));
				stm.setString(2,z0301);
				stm.setInt(3,1);
				stm.setDate(4,date);
				stm.setString(5,"0");
				stm.execute();*/
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		

	}

}
