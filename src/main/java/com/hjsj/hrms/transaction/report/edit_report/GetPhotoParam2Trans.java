package com.hjsj.hrms.transaction.report.edit_report;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 13, 2010</p> 
 *@author xieguiquan
 *@version 4.2
 */
public class GetPhotoParam2Trans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
//			Sys_Infom_Parameter sys_Infom_Parameter=new Sys_Infom_Parameter(this.getFrameconn(),"INFOM");
//			String photo_maxsize=sys_Infom_Parameter.getValue(Sys_Infom_Parameter.PHOTO,"MaxSize");
//			photo_maxsize=photo_maxsize!=null&&photo_maxsize.length()>=0?photo_maxsize:"-1";
//			this.getFormHM().put("photo_maxsize", photo_maxsize);
			String tabid =(String) hm.get("tabid");
			String gridno =(String) hm.get("gridno");
			String pathname =(String) hm.get("pathname");
			String tablename = (String) hm.get("tablename");
			pathname =SafeCode.decode(pathname);
			pathname = PubFunc.decrypt(pathname);//wangcq 2014-12-27
//			System.out.println(tabid+":"+gridno+":"+pathname);
//			String basepre=(String)hm.get("basepre");
//			String a0100=(String)hm.get("a0100");	
			//传图片路径和名称
//			String tablename=new String(((String)hm.get("tablename")).getBytes("ISO-8859-1"), "GB2312");
//			String ins_id=(String)hm.get("ins_id"); 
//			ContentDAO dao=new ContentDAO(this.getFrameconn());
//			String sql="select photo,ext from "+tablename+" where  a0100='"+a0100+"' and basepre='"+basepre+"' ";
//			if(!(ins_id==null||ins_id.length()==0||ins_id.equalsIgnoreCase("0")))
//				sql+=" and ins_id="+ins_id;
//			RowSet rowSet=dao.search(sql);
//			if(rowSet.next())
//			{
//				String filename=ServletUtilities.createOleFile("photo","ext",rowSet);
				this.getFormHM().put("photofile",pathname);
				this.getFormHM().put("tabid",tabid);
				this.getFormHM().put("gridno",gridno);
				this.getFormHM().put("tablename",tablename);
				
//			}
//			else
//			{
//				this.getFormHM().put("photofile","");
//			}
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		 
	}

}
