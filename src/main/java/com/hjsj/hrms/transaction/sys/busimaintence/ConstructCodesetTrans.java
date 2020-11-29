package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * 构建
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 12, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class ConstructCodesetTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {	
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String id=(String)this.getFormHM().get("id");
		String sql="select customdesc,fieldsetid,useflag from t_hr_busitable where id='"+id+"' and useflag='0' order by displayorder";
		try{
			CommonData da=null; 
			ArrayList syselist=new ArrayList();
			da=new CommonData();
			da.setDataName("");
			da.setDataValue("###");
			syselist.add(da);
			String fieldsetdesc="";
			String fieldsetid="";
			this.frowset=dao.search(sql);
			while(this.frowset.next()){
				fieldsetdesc = this.frowset.getString("customdesc");
				fieldsetid = this.frowset.getString("fieldsetid");
				da=new CommonData();
				da.setDataName(fieldsetdesc);
				da.setDataValue(fieldsetid);
				syselist.add(da);
			}
//			String sql="select customdesc,fieldsetid,useflag from t_hr_busitable where id='"+id+"'";
//			ArrayList contractedFieldList=new ArrayList();
//			ArrayList uncontractedFiledList=new ArrayList();
//			ContentDAO dao=new ContentDAO(this.getFrameconn());
//			try{
//				
//				String desc="";
//				String setid="";
//				this.frowset=dao.search(sql);
//				CommonData da=null; 
//				
//				while(this.frowset.next())
//				{
//					desc=this.frowset.getString("customdesc");
//					setid=this.frowset.getString("fieldsetid");
//					da=new CommonData();
//					da.setDataName(desc);
//					da.setDataValue(setid);
//					if(this.frowset.getString("useflag")!=null&&this.frowset.getString("useflag").equals("1"))
//					{
//						contractedFieldList.add(da);
//					}else
//					{
//						uncontractedFiledList.add(da);
//					}
//				}
//			}catch(Exception e)
//			{
//				e.printStackTrace();
//			}
//			BusiSelStr bss=new BusiSelStr();
//			ArrayList syselist=bss.getSubsys(dao,id);
			this.getFormHM().put("id",id);
			this.getFormHM().put("syselist",syselist);
//			this.getFormHM().put("contractedFieldList", contractedFieldList);
//			this.getFormHM().put("uncontractedFiledList", uncontractedFiledList);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
