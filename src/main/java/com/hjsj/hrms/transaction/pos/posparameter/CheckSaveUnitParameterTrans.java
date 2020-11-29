package com.hjsj.hrms.transaction.pos.posparameter;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class CheckSaveUnitParameterTrans extends IBusiness {

	public void execute() throws GeneralException {
		StringBuffer msg=new StringBuffer();
		try{
			String planitem = (String)this.getFormHM().get("planitem");
			if(planitem!=null&&planitem.length()>0){
				String[] planitems = planitem.split("/");
				String staticitem = (String)this.getFormHM().get("staticitems");
				String[] staticitems = staticitem.split("/");
				ContentDAO dao = new ContentDAO(this.frameconn);
				if(planitems.length==staticitems.length){
					for(int i=0;i<planitems.length;i++){
						String staticitemid = (String)staticitems[i];
						if(staticitemid==null||staticitemid.length()==0)
							continue;
						if(!this.checkExist(staticitemid, dao)){
							msg.append(ResourceFactory.getProperty("org.static.item.setup.static")+(i+1)+ResourceFactory.getProperty("org.static.item.setup.static.m")+"\n");
						}
					}
				}else{
					msg.append(ResourceFactory.getProperty("org.static.item.setup"));
				}
				if(msg.length()==0)
					msg.append("ok");
			}else
				msg.append("ok");
		}catch(Exception e){
			msg.append(ResourceFactory.getProperty("org.static.item.setup"));
			e.printStackTrace();
		}finally{
			this.getFormHM().put("msg", com.hrms.frame.codec.SafeCode.encode(msg.toString()));
		}
	}
	
	public boolean checkExist(String id,ContentDAO dao) throws SQLException{
		boolean flag = true;
		String sql="select id,name from LExpr where Type='1' and id='"+id+"'";
		this.frowset = dao.search(sql);
		if(!this.frowset.next())
			flag = false;
		return flag;
	}

}