package com.hjsj.hrms.transaction.selfinfo;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class AppealAllTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		UserView uv=this.getUserView();
		ArrayList setlist=uv.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET,0);
		ContentDAO dao =new ContentDAO(this.getFrameconn());
//		String  pdbflag=(String) hm.get("pdbflag");
		String pdbflag=(String)uv.getDbname();
		String  a0100=(String)hm.get("a0100");
		ArrayList sqls=new ArrayList();
		for(Iterator it=setlist.iterator();it.hasNext();)
		{
			FieldSet fs=(FieldSet) it.next();
			String sql1="update "+pdbflag+fs.getFieldsetid()+" set state='1' where  state='2'   and a0100='"+a0100+"'";
			String sql2="update "+pdbflag+fs.getFieldsetid()+" set state='1' where   state='5'  and a0100='"+a0100+"'";
			String sql="update "+pdbflag+fs.getFieldsetid()+" set state='1' where   state='0'  and a0100='"+a0100+"'";
			sqls.add(sql1);
			sqls.add(sql);
			sqls.add(sql2);
		}
		if(sqls.size()>0){
			try{
			dao.batchUpdate(sqls);
			}catch(Exception e){
				throw GeneralExceptionHandler.Handle(new GeneralException("","信息报批失败！","",""));
			}
		}

	}

}
