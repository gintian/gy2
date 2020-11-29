package com.hjsj.hrms.transaction.sys.cms;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
public class DeleteContentChannelDetailTrans extends IBusiness{
	public void execute() throws GeneralException{
		try{
			ArrayList list=(ArrayList)this.getFormHM().get("selectedlist");
			if(list==null||list.size()==0)
				return;
			ArrayList paralist=new ArrayList();
			for(int i=0;i<list.size();i++)
			{
				LazyDynaBean dynabean=(LazyDynaBean)list.get(i);
				ArrayList temp=new ArrayList();
				temp.add(dynabean.get("content_id"));
				paralist.add(temp);
			}
			StringBuffer sql = new StringBuffer();
			ContentDAO dao = new ContentDAO(this.getFrameconn());	
			sql.append("delete from t_cms_content where content_id=?");
			/**批量执行SQL语句*/
			dao.batchUpdate(sql.toString(), paralist);
			/*
			String[] content_id =(String[])this.getFormHM().get("selected_content_id_array");
			StringBuffer sql = new StringBuffer();
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			for(int i= 0;i<content_id.length;i++){
				sql.append("or content_id = "+content_id[i]);
			}
			dao.delete("delete from t_cms_content where "+sql.substring(3),new ArrayList());
			*/
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
