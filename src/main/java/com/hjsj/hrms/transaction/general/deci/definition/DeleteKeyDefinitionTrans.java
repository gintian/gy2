package com.hjsj.hrms.transaction.general.deci.definition;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class DeleteKeyDefinitionTrans extends IBusiness {


	public void execute() throws GeneralException {	
		
		String factoridss = (String)this.getFormHM().get("factorid");//指标ID
		String factorids = factoridss.substring(0,factoridss.length()-1);
		
		//System.out.println(factorids);
		
		String [] temp = factorids.split("/");
		
		boolean b = this.checkFactor(temp);
		//System.out.println(b);
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
		StringBuffer resultstr = new StringBuffer();
		
		StringBuffer str = new StringBuffer();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String sql="select * from ds_key_item";
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next()){//存在关联记录
				String tt = this.frowset.getString("key_factors");
				str.append(tt);
				str.append(",");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for(int i=0; i< temp.length; i++){
			if(str.toString().indexOf(temp[i])!= -1){
				String sql1="select * from ds_key_factor where factorid='"+temp[i]+"'";
				try {
					this.frowset = dao.search(sql1);
					if(this.frowset.next()){//存在关联记录
						String t = this.getFrowset().getString("name");
						resultstr.append(t);
						resultstr.append("/");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return resultstr.toString();
	}

	public void deleteFactor(String [] temp){
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		for(int i=0; i< temp.length; i++){
			String delsql="delete from ds_key_factor where factorid ='"+temp[i]+"'";
			try {
				dao.delete(delsql,new ArrayList());
			} catch (SQLException e) {
				e.printStackTrace();
			}		
		}
	}

	/**
	 * 判断集合中是否存在关联（与统计图例关联）
	 * @param temp
	 * @return
	 */
	public boolean checkFactor(String [] temp){
		boolean b = true;
		StringBuffer str = new StringBuffer();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String sql="select * from ds_key_item";
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next()){//存在关联记录
				String tt = this.frowset.getString("key_factors");
				str.append(tt);
				str.append(",");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for(int i=0; i< temp.length; i++){
			if(str.toString().indexOf(temp[i])!= -1){
				b=false;
				break;
			}
		}
		return b;
	}
	
}
