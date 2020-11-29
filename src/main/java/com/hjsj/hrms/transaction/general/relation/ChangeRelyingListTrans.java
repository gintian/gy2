package com.hjsj.hrms.transaction.general.relation;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;

public class ChangeRelyingListTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try {
		ArrayList relyingList=new ArrayList();
		String actor_type=(String) this.getFormHM().get("actor_type");
		String relation_id=(String) this.getFormHM().get("relation_id");
		String sql="";
		if(actor_type!=null){
			if(relation_id==null || relation_id.length()==0){
				if("1".equals(actor_type)){
					sql="select * from t_wf_relation where validflag=1 and Actor_type=1   order by seq";
				}
				if("4".equals(actor_type)){
					sql="select * from t_wf_relation where validflag=1 and Actor_type=4   order by seq";
				}
			}else{
				if("1".equals(actor_type)){
					sql="select * from t_wf_relation where validflag=1 and Actor_type=1   and relation_id <>'"+relation_id+"'order by seq";
				}
				if("4".equals(actor_type)){
					sql="select * from t_wf_relation where validflag=1 and Actor_type=4   and relation_id <>'"+relation_id+"' order by seq";
				}
			}
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql);
			CommonData temp = new CommonData("0", "");
			relyingList.add(temp); 
			while(this.frowset.next())
			{
				temp = new CommonData(this.frowset.getString("relation_id"),this.frowset.getString("cname"));
				relyingList.add(temp);
			} 
			/*
			String ssql="select * from t_wf_relation where validflag=1 and Actor_type='"+actor_type+"' and default_line=1 ";
			ResultSet rs=null;
			rs=dao.search(ssql);
			while(rs.next()){
				temp = new CommonData("-1", "default");
				relyingList.add(temp);
			}*/
			this.getFormHM().put("relyingList",relyingList);
		}
	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
