package com.hjsj.hrms.transaction.general.statics;

import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveTwoDimTrans extends IBusiness {
	
	private String getMaxId()throws GeneralException
	{
		int nid=-1;
		StringBuffer sql=new StringBuffer("select max(id)+1 as nmax from sname");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next())
			{
				nid=this.frowset.getInt("nmax");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
   	       throw GeneralExceptionHandler.Handle(ex);			
		}
		return String.valueOf(nid);
	}
	
	
	
	private void deleteSName(String id)throws GeneralException
	{
		StringBuffer sql=new StringBuffer();

		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			sql.append("delete from sname where id=");
			sql.append(id);			
			dao.update(sql.toString());
			sql.setLength(0);
			sql.append("delete from slegend where id=");
			sql.append(id);	
			dao.update(sql.toString());			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
	   	    throw GeneralExceptionHandler.Handle(ex);					
		}
	}

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String nam=(String)this.getFormHM().get("title");
		String find=(String)this.getFormHM().get("find");
		String result = (String)this.getFormHM().get("result");
		String infor_Flag = (String)this.getFormHM().get("infor_Flag");
        String id = (String)this.getFormHM().get("hvalue");
        String sone=(String)this.getFormHM().get("selOne");
        String stwo=(String)this.getFormHM().get("selTwo");
        String nbase = (String)this.getFormHM().get("userbases");
        if(nbase==null)
        	nbase=(String)this.getFormHM().get("userbase");
        if(nbase!=null&&nbase.indexOf("`")!=-1)
        	nbase=nbase.replaceAll("`", ",");
		ArrayList plist=new ArrayList();	
		StringBuffer sql=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.getFrameconn());	
		if(id==null|| "".equals(id))
		   id=getMaxId();
		    else
		    {
		       deleteSName(id);        	
		    }
	   try{
			sql.append("insert into sname(Id,Name,Flag,Type,HV,InfoKind,nbase)values(?,?,?,'2',?,?,?)");
			plist.add(new Integer(id));
			if(nam.getBytes().length>30)
				throw new GeneralException("统计条件名称太长,只能保存10个汉字!");
			plist.add(nam);
			//plist.add(find);
			plist.add(result);
			plist.add((sone+","+stwo).toString());
			plist.add(new Integer(infor_Flag));
			plist.add(nbase);
		    dao.update(sql.toString(),plist);
		    /**保存资源*/
			UserObjectBo user_bo=new UserObjectBo(this.getFrameconn());
			user_bo.saveResource(id,this.userView,IResourceConstant.STATICS);
			
		}catch (Exception exp){
			exp.printStackTrace();
     	     throw GeneralExceptionHandler.Handle(exp);
			
		}

	}

}
