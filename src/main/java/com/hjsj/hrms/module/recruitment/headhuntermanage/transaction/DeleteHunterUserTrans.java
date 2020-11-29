package com.hjsj.hrms.module.recruitment.headhuntermanage.transaction;

import com.hjsj.hrms.module.recruitment.headhuntermanage.businessobject.HeadHunterManageBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.List;

public class DeleteHunterUserTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
         ArrayList datas = (ArrayList)this.getFormHM().get("deletedata");
         
         List users = new ArrayList();
         Boolean result = Boolean.FALSE;
         try{
        	 HeadHunterManageBo hmb = new HeadHunterManageBo();
 	        for(int i=0;i<datas.size();i++){
 	        	DynaBean re = (DynaBean)datas.get(i);
 	        	String hunterkey = (String)re.get("username");
 	        	if(hmb.isHunterUserCanBeDelete(hunterkey)){
 	        		ArrayList hunterkeys = new ArrayList();
 	        		hunterkeys.add(hunterkey);
 	        		users.add(hunterkeys);
 	        	}
 	        }
         
 	        if(!users.isEmpty()){
 	        	String sql = " delete zp_headhunter_login where username=?";
 	        		ContentDAO dao =  new ContentDAO(frameconn);
 	        		int[] i = dao.batchUpdate(sql, users);
 	        		if(i.length>0)
 					 result = Boolean.TRUE;
 	        }else{
 	        	this.getFormHM().put("hinttext","此用户已推荐职位，不允许删除！");
 	        }
         }catch(Exception e){
         	e.printStackTrace();
         	result = Boolean.FALSE;
         }finally{
         	this.getFormHM().put("result",result);
         }
	}

}
