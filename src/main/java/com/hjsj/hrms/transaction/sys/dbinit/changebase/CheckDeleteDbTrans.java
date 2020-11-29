package com.hjsj.hrms.transaction.sys.dbinit.changebase;

import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class CheckDeleteDbTrans extends IBusiness {

	public void execute() throws GeneralException {
         String selectDb = (String)this.getFormHM().get("selectDb");
         this.getFormHM().clear();
         String msg = "";
         try{
	         String[] dbArr = selectDb.split(",");
	         //判断要删除的人员库是否在认证库中设置了
	        	 msg += loginDbChecker(dbArr);
         }catch(Exception e){
        	 	e.printStackTrace();
        	 	GeneralExceptionHandler.Handle(e);
         }
        	 if(msg.length()>0){
        		 this.formHM.put("msg", msg);
        	 }else{
        		 this.formHM.put("msg", "true");
        	 }
	}
	private String loginDbChecker(String[] db){
		StringBuffer msg = new StringBuffer();
		RecordVo vo  = ConstantParamter.getConstantVo("SS_LOGIN");
		String loginDb = vo.getString("str_value").toLowerCase();
		for(int i=0;i<db.length;i++){
			if(loginDb.indexOf(db[i].toLowerCase())!=-1){
				msg.append("<");
				msg.append(db[i]);
				msg.append("> ");
			}
			
		}
		if(msg.length()>0){
			msg.append(" 库已经在 系统管理>安全策略>认证应用库中设置，不允许删除！");
		}
		return msg.toString();
	}
}
