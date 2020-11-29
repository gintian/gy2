package com.hjsj.hrms.transaction.general.deci.leader;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;

public class GetCodeSetNameTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		try
		{
			ArrayList list=new ArrayList();
			CommonData dataobj = new CommonData();
			dataobj.setDataName("请选择");
			dataobj.setDataValue("");
			list.add(dataobj);
			String setname=(String)this.getFormHM().get("tablename");
			String filedname=(String)this.getFormHM().get("filedname");
			String idv=(String)this.getFormHM().get("idv");
			this.getFormHM().put("idv",idv);
			String flag_code="false";
					
			if(setname==null||setname.length()<=0)
			{
				this.getFormHM().put("flag_code",flag_code);
				return;
			}
			if(filedname==null||filedname.length()<=0)
			{
				this.getFormHM().put("flag_code",flag_code);
				return;
			}
			String filed_desc="";				
			StringBuffer sqlstr=new StringBuffer();
			sqlstr.append("select codesetid,itemdesc from fielditem"); 
			sqlstr.append(" where fieldsetid='"+setname+"'");
			sqlstr.append(" and UPPER(itemid)='"+filedname.toUpperCase()+"'");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sqlstr.toString());
			String codesetid="";
			if(this.frowset.next())			{
				filed_desc=this.frowset.getString("itemdesc");
				codesetid=this.frowset.getString("codesetid");
				//ArrayList codeid_list=AdminCode.getCodeItemList(codesetid);
				sqlstr=new StringBuffer();
				sqlstr.append("select codeitemid,codeitemdesc from codeitem");
				sqlstr.append(" where UPPER(codesetid)='"+codesetid.toUpperCase()+"'");
				sqlstr.append(" order by parentid,childid");
				RowSet rs=dao.search(sqlstr.toString());				
				String codeitemid="";
				String codeitemdesc="";
				while(rs.next())
				{
					flag_code="true";					
					codeitemid=rs.getString("codeitemid");
					codeitemdesc=rs.getString("codeitemdesc");
				    dataobj = new CommonData(codeitemid, codeitemid.toUpperCase()+ ":"+ codeitemdesc);
				    list.add(dataobj);
				}
			}
			this.getFormHM().put("filed_desc",filed_desc);
			this.getFormHM().put("flag_code",flag_code);
			this.getFormHM().put("codeitemlist",list);	
		}
		
		catch(Exception ex)
		{
			ex.printStackTrace();
   	        throw GeneralExceptionHandler.Handle(ex);   
		}
	}


}
