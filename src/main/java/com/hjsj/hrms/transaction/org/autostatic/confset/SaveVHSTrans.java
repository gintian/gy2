package com.hjsj.hrms.transaction.org.autostatic.confset;

import com.hjsj.hrms.businessobject.general.info.EmpMaintenanBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SaveVHSTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String flag = (String)this.getFormHM().get("flag");
		flag=flag!=null&&flag.trim().length()>0?flag:"";
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		StringBuffer sqlstr =  new StringBuffer();
		if("hide".equalsIgnoreCase(flag)){
			String hideitemid = (String)this.getFormHM().get("hideitemid");
			hideitemid=hideitemid!=null&&hideitemid.trim().length()>0?hideitemid:"";
			
			String setid = (String)this.getFormHM().get("setid");
			setid=setid!=null&&setid.trim().length()>0?setid:"";
			
			ArrayList fieldlist = DataDictionary.getFieldList(setid,Constant.USED_FIELD_SET);
			String viewhide ="";
			for(int i=0;i<fieldlist.size();i++){
				FieldItem fielditem = (FieldItem)fieldlist.get(i);
				if(fielditem!=null){
					if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))){
						if(hideitemid.toUpperCase().indexOf(fielditem.getItemid().toUpperCase())==-1){
							viewhide+=fielditem.getItemid()+",0/";
						}else{
							viewhide+=fielditem.getItemid()+","+fielditem.getDisplaywidth()+"/";
						}
					}
				}
			}
			
			if(isRecord(dao,setid)){
				sqlstr.append("update t_sys_viewplan set Display_hide='");
				sqlstr.append(hideitemid);
				sqlstr.append("' where Username='");
				sqlstr.append(this.userView.getUserName());
				sqlstr.append("' and Setid='");
				sqlstr.append(setid);
				sqlstr.append("'");
			}else{
				sqlstr.append("insert into t_sys_viewplan(Username,Setid,Display_hide) values('");
				sqlstr.append(this.userView.getUserName());
				sqlstr.append("','");
				sqlstr.append(setid);
				sqlstr.append("','");
				sqlstr.append(hideitemid);
				sqlstr.append("')");
			}
			if(this.userView.isSuper_admin()){
				EmpMaintenanBo ebo = new EmpMaintenanBo(this.frameconn);
				ebo.updateDishidefield(viewhide, setid);
			}
		}else if("sort".equalsIgnoreCase(flag)){
			String sortitem = (String)this.getFormHM().get("sortitem");
			sortitem=sortitem!=null&&sortitem.trim().length()>0?sortitem:"";
			
			String setid = (String)this.getFormHM().get("setid");
			setid=setid!=null&&setid.trim().length()>0?setid:"";
			if(isRecord(dao,setid)){
				sqlstr.append("update t_sys_viewplan set display_order='");
				sqlstr.append(sortitem);
				sqlstr.append("' where Username='");
				sqlstr.append(this.userView.getUserName());
				sqlstr.append("' and Setid='");
				sqlstr.append(setid);
				sqlstr.append("'");
			}else{
				sqlstr.append("insert into t_sys_viewplan(Username,Setid,display_order) values('");
				sqlstr.append(this.userView.getUserName());
				sqlstr.append("','");
				sqlstr.append(setid);
				sqlstr.append("','");
				sqlstr.append(sortitem);
				sqlstr.append("')");
			}
			if(this.userView.isSuper_admin()){
				EmpMaintenanBo ebo = new EmpMaintenanBo(this.frameconn);
				sortitem=sortitem.toUpperCase().replace("B0110,","").replace("E01A1,","").replace("id,","");
				ebo.sortfield(sortitem.split(","),setid);
			}
		}
		if(sqlstr.toString().length()>10){
			try {
				dao.update(sqlstr.toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private boolean isRecord(ContentDAO dao,String setid){
		boolean check=false;
		StringBuffer buf =  new StringBuffer();
		buf.append("select 1 from t_sys_viewplan where Username='");
		buf.append(this.userView.getUserName());
		buf.append("' and Setid='");
		buf.append(setid);
		buf.append("'");
		
		try {
			RowSet rs = dao.search(buf.toString());
			if(rs.next())
				check=true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return check;
	}
}
