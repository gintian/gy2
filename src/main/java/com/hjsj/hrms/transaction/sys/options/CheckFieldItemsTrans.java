package com.hjsj.hrms.transaction.sys.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class CheckFieldItemsTrans extends IBusiness {


	public void execute() throws GeneralException {
		String info = "";
		String fieldItems = (String) this.getFormHM().get("items");
		
		//System.out.println(fieldItems);
		
		String [] fieldItem = fieldItems.split("`");
		for(int i = 0; i< fieldItem.length ; i++){
			String itemId = fieldItem[i];
			String desc = this.checkFieldItem(itemId);
			if(!"".equals(desc)){
				info +=desc;
				info +=",";
			}
		}
		
		if("".equals(info)){
			info="ok";
		}else{
			info = info.substring(0,info.length()-1);
		}
		
		//System.out.println(info);
		
		this.getFormHM().put("info",info);

	}
	
	/**
	 * 效验指标是否是数值型
	 * @param itemId
	 * @return
	 */
	public String checkFieldItem(String itemId){
		String itemDesc = "";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql="select itemdesc , itemtype from  fielditem where useflag=1 and itemid='"+itemId+"'";
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				String itemtype = this.frowset.getString("itemtype");
				if(!"N".equalsIgnoreCase(itemtype)){
					itemDesc = this.frowset.getString("itemdesc");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return itemDesc;
	}

}
