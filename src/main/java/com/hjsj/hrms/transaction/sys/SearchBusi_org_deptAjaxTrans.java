package com.hjsj.hrms.transaction.sys;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchBusi_org_deptAjaxTrans extends IBusiness {


	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String username=(String)this.getFormHM().get("username");
		String busi_org_dept = "";
		try{
			if(username!=null&&username.length()>0){
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				String sql = "select busi_org_dept from operuser where username='"+username+"'";
				this.frecset = dao.search(sql);
				while(this.frecset.next()){
					busi_org_dept = Sql_switcher.readMemo(this.frecset, "busi_org_dept");
				}
				if(busi_org_dept.length()>0){
					String str[] = busi_org_dept.split("\\|");
					for(int i=0;i<str.length;i++){//1,UNxxx`UM9191`
						String tmp = str[i];
						String ts[] = tmp.split(",");
						if(ts.length==2){
							this.getFormHM().put("busi_org_dept"+ts[0], ts[1]);
							StringBuffer sb = new StringBuffer();
							if(ts[1].length()>0){//UNxxx`UM9191`
								String tt[] = ts[1].split("`");
								for(int n=0;n<tt.length;n++){
									String ttt = tt[n];//UNxxx
									if(ttt.length()>2){
										String codesetid = ttt.substring(0, 2);
										String codeitemid = ttt.substring(2);
										sql = "select codeitemdesc from organization where codesetid='"+codesetid+"' and codeitemid='"+codeitemid+"'";
										this.frecset = dao.search(sql);
										while(this.frecset.next()){
											sb.append(this.getFrecset().getString("codeitemdesc")+",");
										}
									}else{
										continue;
									}
								}
							}
							this.getFormHM().put("busi_org_dept"+ts[0]+"view", sb.toString());
						}else{
							continue;
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{//1,UNxxx`UM9191`|2,UNxxx`UM9191`
			
		}
	}

}
