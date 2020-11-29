package com.hjsj.hrms.transaction.general.deci.definition;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DeleteDefinitionTypeTran extends IBusiness{
	
	public void execute() throws GeneralException {	
		String typeidss = (String)this.getFormHM().get("typeid");
		String typeids = typeidss.substring(0,typeidss.length()-1);		
		String [] temp = typeids.split("/");
		
		boolean b = this.checkFactor(temp);
		if(b){//列表中不存在关联记录
			this.deleteFactor(temp);
			this.getFormHM().put("info","true");
		}else{
			String result = this.getTypeNames(temp);
			result = result.substring(0,result.length()-1);
			this.getFormHM().put("info",result);
		}
	}

	public String getTypeNames(String [] temp){
		StringBuffer str = new StringBuffer();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		for(int i=0; i< temp.length; i++){
			String sql="select * from ds_key_factor where typeid='"+temp[i]+"'";
			try {
				this.frowset = dao.search(sql);
				if(this.frowset.next()){//存在关联记录
					String tsql="select name from ds_key_factortype where typeid='"+temp[i]+"'";
					RowSet rs = dao.search(tsql);
					if(rs.next()){
						String t = rs.getString("name");
						str.append(t);
						str.append("/");
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return str.toString();
	}

	public void deleteFactor(String [] temp){
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		for(int i=0; i< temp.length; i++){
			String delsql="delete from ds_key_factortype where typeid ='"+temp[i]+"'";
			try {
				dao.delete(delsql,new ArrayList());
			} catch (SQLException e) {
				e.printStackTrace();
			}		
		}
	}

	public boolean checkFactor(String [] temp){
		boolean b = true;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		for(int i=0; i< temp.length; i++){
			String sql="select * from ds_key_factor where typeid='"+temp[i]+"'";
			try {
				this.frowset = dao.search(sql);
				if(this.frowset.next()){//存在关联记录
					b = false;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return b;
	}
}
