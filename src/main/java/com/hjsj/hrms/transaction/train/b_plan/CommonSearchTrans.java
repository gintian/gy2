package com.hjsj.hrms.transaction.train.b_plan;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class CommonSearchTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
		String type = (String)reqhm.get("type");
		type=type!=null&&type.trim().length()>0?type:"1";
		reqhm.remove("type");
		
		this.getFormHM().put("titlelist",searchTable(type));
		this.getFormHM().put("type",type);
	}
	/**
	 * 查询条件
	 * @param type
	 * @return
	 */
	private ArrayList searchTable(String type){
		ArrayList titlelist = new ArrayList();
		String strsql = "select id,name from lexpr where Type="+type;
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search(strsql);
			int n=1;
			while(this.frowset.next()){
				String id = this.frowset.getString("id");
				String name = this.frowset.getString("name");
				
				if(!(this.userView.isHaveResource(IResourceConstant.LEXPR,id)))
                	continue;
				CommonData obj=new CommonData(id+":"+name,id+"."+name);
				titlelist.add(obj);
				n++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return titlelist;
	}
	/**
	 * 最大id
	 * @param type
	 * @return
	 */
	private int maxId(String type){
		int id = 1;
		String strsql = "select max(id) as id from lexpr";
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search(strsql);
			while(this.frowset.next()){
				id = this.frowset.getInt("id")+1;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}
}
