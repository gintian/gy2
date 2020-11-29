package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.Field;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author JinChunhai
 * @version 5.0
 * 
 */

public class ItemsSumTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");		
		String salaryid = (String) hm.get("salaryid");
		String sp=(String) hm.get("sp");
		String a_code=(String) hm.get("a_code");
		String ssql=(String)this.getFormHM().get("sql");
		ArrayList fieldlist=(ArrayList)this.getFormHM().get("fieldlist");
		String names = "";
		for(int i=0;i<fieldlist.size();i++)
		{
			Field field=(Field)fieldlist.get(i);
			names += ","+field.getName().toUpperCase();			
		}
		String[] items = names.split(",");
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < items.length; i++)
			buf.append(",'" + items[i].toUpperCase() + "'");
		
		ArrayList itemSumList=new ArrayList();
		RowSet rowSet;
		try{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			LazyDynaBean abean=null;			
			String sql=("select itemid,itemdesc from salaryset where salaryid="+salaryid+" and itemtype='N' AND upper(itemid) IN ("
						+ buf.substring(1) + ") and upper(itemid) not in ('A0000','A00Z3','A00Z1') order by sortid");			
			rowSet=dao.search(sql);
			
			String itemid="";
			String itemdesc="";
			while(rowSet.next())
			{
				abean=new LazyDynaBean();
				itemid=rowSet.getString("itemid");
				itemdesc=rowSet.getString("itemdesc");
				if("0".equalsIgnoreCase(this.userView.analyseFieldPriv(itemid)))
					continue;
				abean.set("itemid",itemid);
				abean.set("itemdesc",itemdesc);
				itemSumList.add(abean);
			}	
			if(rowSet!=null)
				rowSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		this.getFormHM().put("itemSumList", itemSumList);
		this.getFormHM().put("sp", sp);	
		this.getFormHM().put("a_code", a_code);	
	}	
}
