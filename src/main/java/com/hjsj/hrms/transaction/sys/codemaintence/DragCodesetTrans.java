package com.hjsj.hrms.transaction.sys.codemaintence;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class DragCodesetTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String msg = "ok";
		String to="";
		String status ="";
		try{
			String fromid = (String)this.getFormHM().get("fromid");
			String toid = (String)this.getFormHM().get("toid");
			String toname = (String)this.getFormHM().get("toname");
			
			RecordVo vo = new RecordVo("codeset");
			ContentDAO dao = new ContentDAO(this.frameconn);
			vo.setString("codesetid", fromid);
			vo = dao.findByPrimaryKey(vo);
			//status = vo.getString("status");
			//status = status==null||status.length()==0?"0":status;
			/*if(!"0".equals(status)){
				if("02".equals(toid)&&ResourceFactory.getProperty("codemaintence.code.user").equals(toname)){
					msg="系统代码不能修改到用户代码下!";
					return;
				}
				if("00".equals(toid)&&ResourceFactory.getProperty("conlumn.codeitemid.caption").equals(toname)){
					vo.setString("categories", "");
					dao.updateValueObject(vo);
					to="`";
					return;
				}
				String sql = "select codesetid from codeset where status in('1','2') and categories='"+toid+"'";
				this.frowset = dao.search(sql);
				if(frowset.next()){
					vo.setString("categories", toid);
					dao.updateValueObject(vo);
					to=toid;
				}else{
					msg = "请将代码类修改到系统代码下的分类下！";
					return;
				}
			}else{
				if("00".equals(toid)&&ResourceFactory.getProperty("conlumn.codeitemid.caption").equals(toname)){
					msg="系统代码不能修改到用户代码下!";
					return;
				}
				if("02".equals(toid)&&ResourceFactory.getProperty("codemaintence.code.user").equals(toname)){
					vo.setString("categories", "");
					dao.updateValueObject(vo);
					to="``";
					return;
				}
				String sql = "select codesetid from codeset where (status not in('1','2') or status is null) and categories='"+toid+"'";
				this.frowset = dao.search(sql);
				if(frowset.next()){
					vo.setString("categories", toid);
					dao.updateValueObject(vo);
					to=toid;
				}else{
					msg = "请将代码类修改到用户代码下的分类下！";
					return;
				}
			}*/
			if("root".equals(toid)){
				vo.setString("categories", "");
				dao.updateValueObject(vo);
			}else{
				vo.setString("categories", toid);
				dao.updateValueObject(vo);
				to=toid;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.formHM.put("msg", msg);
			//this.formHM.put("to", to);
			//this.formHM.put("status", status);
		}
	}

}
