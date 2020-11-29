package com.hjsj.hrms.transaction.kq.feast_manage;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GetCodeSetNameTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		RowSet rs = null;
		try
		{
			ArrayList list=new ArrayList();
			String setname=(String)this.getFormHM().get("tablename");  //子集
			String filedname=(String)this.getFormHM().get("filedname"); //要的代码
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
			String codesetid="";
			StringBuffer sqlstr=new StringBuffer();
			if("b0110".equalsIgnoreCase(filedname)){
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				filed_desc=ResourceFactory.getProperty("b0110.label");
				codesetid = "UN";
				sqlstr=new StringBuffer();
				sqlstr.append("SELECT codeitemid, codeitemdesc FROM organization");
				sqlstr.append(" WHERE codeSetid ='"+codesetid.toUpperCase()+"'");
				rs=dao.search(sqlstr.toString());
				CommonData dataobj = new CommonData();
				dataobj.setDataName("");
				dataobj.setDataValue("");
				list.add(dataobj);
				String codeitemid="";
				String codeitemdesc="";
				while(rs.next())
				{
					flag_code="true";					
					codeitemid=rs.getString("codeitemid");  //编号
					codeitemdesc=rs.getString("codeitemdesc"); // 名字
				    dataobj = new CommonData(codeitemid, codeitemid.toUpperCase()+ ":"+ codeitemdesc);
				    list.add(dataobj);
				}
			}else if("e01a1".equalsIgnoreCase(filedname)){
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				filed_desc=ResourceFactory.getProperty("e01a1.label");
				codesetid = "@K";
				sqlstr=new StringBuffer();
				sqlstr.append("SELECT codeitemid, codeitemdesc FROM organization");
				sqlstr.append(" WHERE codeSetid ='"+codesetid.toUpperCase()+"'");
				rs=dao.search(sqlstr.toString());
				CommonData dataobj = new CommonData();
				dataobj.setDataName("");
				dataobj.setDataValue("");
				list.add(dataobj);
				String codeitemid="";
				String codeitemdesc="";
				while(rs.next())
				{
					flag_code="true";					
					codeitemid=rs.getString("codeitemid");  //编号
					codeitemdesc=rs.getString("codeitemdesc"); // 名字
				    dataobj = new CommonData(codeitemid, codeitemid.toUpperCase()+ ":"+ codeitemdesc);
				    list.add(dataobj);
				}
			}else if("e0122".equalsIgnoreCase(filedname)){
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				filed_desc=ResourceFactory.getProperty("e0122.label");
				codesetid = "UM";
				sqlstr=new StringBuffer();
				sqlstr.append("SELECT codeitemid, codeitemdesc FROM organization");
				sqlstr.append(" WHERE codeSetid ='"+codesetid.toUpperCase()+"'");
				rs=dao.search(sqlstr.toString());
				CommonData dataobj = new CommonData();
				dataobj.setDataName("");
				dataobj.setDataValue("");
				list.add(dataobj);
				String codeitemid="";
				String codeitemdesc="";
				while(rs.next())
				{
					flag_code="true";					
					codeitemid=rs.getString("codeitemid");  //编号
					codeitemdesc=rs.getString("codeitemdesc"); // 名字
				    dataobj = new CommonData(codeitemid, codeitemid.toUpperCase()+ ":"+ codeitemdesc);
				    list.add(dataobj);
				}
			}else{
				
				sqlstr.append("select codesetid,itemdesc from fielditem"); 
				sqlstr.append(" where fieldsetid='"+setname+"'");
				sqlstr.append(" and UPPER(itemid)='"+filedname.toUpperCase()+"'");
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				this.frowset=dao.search(sqlstr.toString());
//				String codesetid="";
				if(this.frowset.next())			{
					filed_desc=this.frowset.getString("itemdesc");  //名字
					codesetid=this.frowset.getString("codesetid");  // 代号
					//ArrayList codeid_list=AdminCode.getCodeItemList(codesetid);
					sqlstr=new StringBuffer();
					sqlstr.append("select codeitemid,codeitemdesc from codeitem");
					sqlstr.append(" where UPPER(codesetid)='"+codesetid.toUpperCase()+"'");
					sqlstr.append(" order by parentid,childid");
					rs=dao.search(sqlstr.toString());
					CommonData dataobj = new CommonData();
					dataobj.setDataName("");
					dataobj.setDataValue("");
					 list.add(dataobj);
					String codeitemid="";
					String codeitemdesc="";
					while(rs.next())
					{
						flag_code="true";					
						codeitemid=rs.getString("codeitemid");  //编号
						codeitemdesc=rs.getString("codeitemdesc"); // 名字
					    dataobj = new CommonData(codeitemid, codeitemid.toUpperCase()+ ":"+ codeitemdesc);
					    list.add(dataobj);
					}
				}
			}
			
			this.getFormHM().put("filed_desc",filed_desc);
			this.getFormHM().put("flag_code",flag_code);
			this.getFormHM().put("left_codes",list);	
			
		}
		
		catch(Exception ex)
		{
			ex.printStackTrace();
   	        throw GeneralExceptionHandler.Handle(ex);   
		}finally{
        	if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }
	}


}
