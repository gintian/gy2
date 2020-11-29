package com.hjsj.hrms.transaction.kq.team.array_group;

import com.hjsj.hrms.businessobject.kq.team.KqClassArrayConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class DeleteGroupsTrans  extends IBusiness implements KqClassArrayConstant{

	public void execute() throws GeneralException
	{
		ArrayList selected_vo_list=(ArrayList)this.getFormHM().get("selected_vo_list");
        if(selected_vo_list==null||selected_vo_list.size()<=0)
        {
        	return;
        }
        ArrayList dellist=new ArrayList();
        for(int i=0;i<selected_vo_list.size();i++)
        {
        	ArrayList list=new ArrayList();
        	RecordVo vo=(RecordVo)selected_vo_list.get(i);
        	list.add(vo.getString(kq_shift_group_Id));
        	dellist.add(list);
        }
        String del="delete from "+kq_shift_group_table+" where "+kq_shift_group_Id+"=?";
        try
        {
        	ContentDAO dao=new ContentDAO(this.getFrameconn());
        	dao.batchUpdate(del,dellist);
        	del="delete from "+kq_group_emp_table+" where "+kq_shift_group_Id+"=?";
        	dao.batchUpdate(del,dellist);
        }catch(Exception e)
        {
           e.printStackTrace();	
        }
	}
}
