package com.hjsj.hrms.transaction.kq.options;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class DeleteTurnRestTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList dlist = (ArrayList)this.getFormHM().get("dlist");
		if(dlist==null||dlist.size()==0)
            return;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			if(!userView.isSuper_admin())
	   	   {
			  ArrayList list=new ArrayList();
			  String sql="delete from kq_turn_rest where turn_id=? and b0110=?";
			  ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());
              for(int i=0;i<dlist.size();i++)
              {
            	ArrayList one_list=new ArrayList();
            	RecordVo vo=(RecordVo)dlist.get(i);
            	one_list.add(vo.getString("turn_id")); 
            	one_list.add(managePrivCode.getUNB0110());
            	list.add(one_list);
              }
              if(list.size()<dlist.size())
              {
            	  this.getFormHM().put("turnRest_flag","1");
              }
              dao.batchUpdate(sql,list);
	   	   }else
	   	   {
	   		  dao.deleteValueObject(dlist);
	   	   }
			
		}catch(Exception exx)
		{
			exx.printStackTrace();
		}
		
	}

}
