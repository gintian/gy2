package com.hjsj.hrms.transaction.stat;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class CheckCanArchiveTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		/*
		String id=(String)this.getFormHM().get("id");
		try{
			Integer.parseInt(id);
		}catch(Exception e){
			return;
		}
		String sql = "select type from sname where id="+id;
		String msg="error";
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frecset = dao.search(sql);
			if(this.frecset.next()){
				if(this.frecset.getString("type").equals("1")){
					msg = "ok";
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.getFormHM().put("msg", msg);
		}
		*/
		//liuy 2014-11-14 修改信息集设置可修改的范围 start
		String id = (String)this.getFormHM().get("id");
		String msg = "";
		try{
			Integer.parseInt(id);
			String sql = "select type from sname where id="+id;
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frecset = dao.search(sql);
			if(this.frecset.next()){
				if(this.frecset.getString("type")!=null&&!"".equals(this.frecset.getString("type"))){//一维统计
					msg = this.frecset.getString("type");
				}else{
					msg = "error";
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			msg = "error";
		}finally{
			this.getFormHM().put("msg", msg);
		}
		//liuy 2014-11-14 end
	}

}
