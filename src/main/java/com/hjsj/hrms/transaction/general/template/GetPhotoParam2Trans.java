package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.sys.param.Sys_Infom_Parameter;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.HashMap;
/**
 * 
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 13, 2009</p> 
 *@author dengcan
 *@version 4.2
 */
public class GetPhotoParam2Trans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			Sys_Infom_Parameter sys_Infom_Parameter=new Sys_Infom_Parameter(this.getFrameconn(),"INFOM");
			String photo_maxsize=sys_Infom_Parameter.getValue(Sys_Infom_Parameter.PHOTO,"MaxSize");
			photo_maxsize=photo_maxsize!=null&&photo_maxsize.length()>=0?photo_maxsize:"-1";
			this.getFormHM().put("photo_maxsize", photo_maxsize);

			String basepre=(String)hm.get("basepre");
			String a0100=(String)hm.get("a0100");	
			
			String tablename=new String(((String)hm.get("tablename")).getBytes("ISO-8859-1"), "GB2312");
			String ins_id=(String)hm.get("ins_id"); 
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String sql="select photo,ext from "+tablename+" where  a0100='"+a0100+"' and basepre='"+basepre+"' ";
			if(!(ins_id==null||ins_id.length()==0|| "0".equalsIgnoreCase(ins_id)))
				sql+=" and ins_id="+ins_id;
			RowSet rowSet=dao.search(sql);
			if(rowSet.next())
			{
				String filename=ServletUtilities.createOleFile("photo","ext",rowSet);
				if(filename!=null&&filename.trim().length()>0){
				    /**人事异动安全改造，将文件名称加密**/
	                this.getFormHM().put("photofile",SafeCode.encode(PubFunc.encrypt(filename)));
				}else{
				    this.getFormHM().put("photofile","");
				}
			}
			else
			{
				this.getFormHM().put("photofile","");
			}
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		 
	}

}
