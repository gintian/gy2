package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class EditRelatingcodeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		RecordVo t_hr_relatingcode=new RecordVo("t_hr_relatingcode");
		if(reqhm.containsKey("del")){
			reqhm.remove("del");
//			删除相关指标
			ArrayList selitem=(ArrayList) hm.get("selitem");
			for(int i=0;i<selitem.size();i++){
				RecordVo relatingcode=new RecordVo("t_hr_relatingcode");
				DynaBean dynabean=(DynaBean) selitem.get(i);
				String codesetid=(String)dynabean.get("codesetid");
				relatingcode.setString("codesetid",codesetid);
				if(this.isUnused(dao,codesetid)){
					try {
						dao.deleteValueObject(relatingcode);
					} catch (GeneralException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					throw GeneralExceptionHandler.Handle(new GeneralException("","关联代码<"+codesetid+">已使用，禁止删除！","",""));
				}
			}
		}
		if(reqhm.containsKey("update")){
//			修改相关指标
			reqhm.remove("update");
			t_hr_relatingcode=(RecordVo) hm.get("relatingcode");
			try {
				dao.updateValueObject(t_hr_relatingcode);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		if(reqhm.containsKey("add")){
			reqhm.remove("add");
			t_hr_relatingcode=(RecordVo) hm.get("relatingcode");
			try {
				dao.addValueObject(t_hr_relatingcode);
			} catch (GeneralException e) {
				throw GeneralExceptionHandler.Handle(new GeneralException("","关联代码“代码编号”已存在！","",""));
			}
//			增加相关指标
		}
		//刷新代码，以便立即生效使用
		AdminCode.refreshCodeTable();
	}
	public boolean isUnused(ContentDAO dao,String itemid) throws GeneralException{
		boolean flag=true;
		String[] temp=itemid.split(":");
		String sql="select * from t_hr_busifield where  codesetid='"+temp[0]+"' and codeflag='1'";
		ArrayList templist=dao.searchDynaList(sql);
		if(templist.size()>0){
			flag=false;
		}
		return flag;
	}

}
