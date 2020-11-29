package com.hjsj.hrms.transaction.performance.evaluation;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ImportVoteTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList implist=(ArrayList)this.getFormHM().get("implist");
		StringBuffer sql=new StringBuffer();
		sql.append("delete from per_result_correct");
		ContentDAO dao=new ContentDAO(this.frameconn);
		StringBuffer tem=new StringBuffer();
		ArrayList list=new ArrayList();
		ArrayList inlist=new ArrayList();
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String plan_id = (String) hm.get("plan_id");
		try {
			//dao.delete(sql.toString(), new ArrayList());
			String id=this.getmaxId();
			int temid=0;
			if(implist!=null&&implist.size()>0){
				sql.append(" where plan_id=? and object_id=?");
				for(int i=0;i<implist.size();i++){
					ArrayList tempo=new ArrayList();
					ArrayList intempo=new ArrayList();
					LazyDynaBean bean=(LazyDynaBean)implist.get(i);
					if(bean!=null){
						id=String.valueOf(Integer.parseInt(id)+1);
						intempo.add(String.valueOf(id));
						String a0101=(String)bean.get("a0101");
						//String plan_id=(String)bean.get("plan_id");
						tempo.add(plan_id);
						intempo.add(plan_id);

						String objid=(String)bean.get("object_id");
						tempo.add(objid);
						intempo.add(objid);
						intempo.add(a0101);
						String correct=(String)bean.get("修正分值");
						intempo.add(correct);
						String reason=(String)bean.get("修正原因");
						intempo.add(reason);
						
					}
					inlist.add(intempo);
					
					dao.delete(sql.toString(), tempo);
				}
				
				sql.setLength(0);
				sql.append("insert into per_result_correct(id,plan_id,object_id,a0101,score,correct_reason)values(?,?,?,?,?,?) ");
				dao.batchInsert(sql.toString(), inlist);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.getFormHM().put("flag", "ok");
	}
	public String getmaxId(){
		String maxid="";
		String sql="select max(id) from per_result_correct";
		ContentDAO dao=new ContentDAO(this.frameconn);
		try {
			this.frowset=dao.search(sql);
			if(this.frowset.next()){
				maxid=this.frowset.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return maxid;
	}

}
 