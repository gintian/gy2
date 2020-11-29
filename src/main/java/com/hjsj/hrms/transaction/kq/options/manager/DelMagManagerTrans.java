package com.hjsj.hrms.transaction.kq.options.manager;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
/**
 * 删除发卡子集
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jun 16, 2008</p> 
 *@author sunxin
 *@version 4.0
 *
 *zxj 20170527
 *注意：该类删除人员信息动作严重错误！！！请勿调用！！！
 */
@Deprecated
public class DelMagManagerTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		 ArrayList selectedinfolist=(ArrayList)this.getFormHM().get("selectedinfolist");
		 String magcard_setid=(String)this.getFormHM().get("magcard_setid");
		 String select_pre=(String)this.getFormHM().get("select_pre");
		 String table=select_pre+magcard_setid;
		 ArrayList paralist=new ArrayList();
		 for(int i=0;i<selectedinfolist.size();i++)
         {
     		    LazyDynaBean rec=(LazyDynaBean)selectedinfolist.get(i); 
     		    ArrayList list=new ArrayList();
     		    list.add(rec.get("a0100").toString());
     		    // 不是人员主集时，才读取i9999
     		    if ( !"A01".equalsIgnoreCase(magcard_setid)) {
     		    	list.add(rec.get("i9999").toString());
     		    }
     		    paralist.add(list);         		   
        }
		StringBuffer sql=new StringBuffer();
		if ( !"A01".equalsIgnoreCase(magcard_setid)) {
			sql.append("delete from "+table+" where a0100=? and i9999=?");
		} else {
			sql.append("delete from "+table+" where a0100=?");
		}
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			//dao.batchUpdate(sql.toString(), paralist);
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException("","删除数据失败","","")); 
		}
		
	}

}
