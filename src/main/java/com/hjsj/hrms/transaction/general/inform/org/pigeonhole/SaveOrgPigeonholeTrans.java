/*
 * Created on 2006-3-4
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.general.inform.org.pigeonhole;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SaveOrgPigeonholeTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String historyorgname=(String)this.getFormHM().get("historyorgname");
		String archive_date=(String)this.getFormHM().get("archive_date");
		if(archive_date==null||archive_date.length()==0){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			archive_date=sdf.format(new Date());
		}
		String description=(String)this.getFormHM().get("description");
		if(description.length()>=200)
			description  = description.substring(0,199);
		/*保存历史机构信息*/
		SaveOrgPigeonhole(historyorgname,archive_date,description);
		this.getFormHM().put("scceeddesc",ResourceFactory.getProperty("general.inform.org.savescceed"));
	}
	/*保存历史机构信息*/
	private void SaveOrgPigeonhole(String historyorgname,String archive_date,String description) throws GeneralException
	{
		StringBuffer sql=new StringBuffer();
		sql.append("delete from hr_org_catalog where catalog_id='");
		String catalog_id=getCatalog_id(archive_date);
		sql.append(catalog_id);
		sql.append("'");
		cat.debug("insert hr_org_catalog sql=>" + sql.toString());
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{
			//System.out.println(sql.toString());
		  dao.delete(sql.toString(),new ArrayList());
		  sql.setLength(0);
		  RecordVo vo = new RecordVo("hr_org_catalog");
		  vo.setString("catalog_id",catalog_id);
		  vo.setString("name",historyorgname);
		  vo.setDate("archive_date",DateStyle.parseDate(archive_date));
		  vo.setString("description",description);
		  dao.addValueObject(vo);
		  /*sql.append("insert into hr_org_catalog(catalog_id,name,archive_date,description) values('");
		  sql.append(catalog_id);
		  sql.append("','");
		  
		  sql.append(historyorgname);
		  sql.append("',");
		  sql.append(Sql_switcher.charToDate("'" + archive_date + "'"));
		  //sql.append(archive_date);
		  sql.append(",'");
		  sql.append(description);
		  sql.append("')");
		  ArrayList values=new ArrayList(); 
		 // System.out.println(sql.toString());
		  dao.insert(sql.toString(),values);*/
		  sql.setLength(0);
		  sql.append("delete from hr_org_history where catalog_id='");
		  sql.append(catalog_id);
		  sql.append("'");
		  dao.delete(sql.toString(),new ArrayList());
		  //new ExecuteSQL().execUpdate(sql.toString());
		  sql.setLength(0);
		  sql.append("insert into hr_org_history(catalog_id,codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,A0000,groupid) select '");
		  sql.append(catalog_id);
		  sql.append("',codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,A0000,groupid from (select ");
		  sql.append("codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,A0000,groupid from organization ");
		  sql.append("where "+Sql_switcher.dateValue(archive_date)+" between start_date and end_date ");
		  sql.append("union select ");
		  sql.append("codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,A0000,groupid from vorganization ");
		  sql.append("where "+Sql_switcher.dateValue(archive_date)+" between start_date and end_date) tt");
		  dao.insert(sql.toString(),new ArrayList());
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/*生成历史机构的ID*/
	private String getCatalog_id(String archive_date)
	{
		if(archive_date!=null && archive_date.length()>=10)
			return archive_date.substring(0,4) + archive_date.substring(5,7) + archive_date.substring(8,10);
		return Calendar.getInstance().get(Calendar.YEAR) + "" +   (Calendar.getInstance().get(Calendar.MONTH)+1) + Calendar.getInstance().get(Calendar.DATE);
	}

}
