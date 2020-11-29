package com.hjsj.hrms.transaction.sys.dbinit.fielditem;

import com.hjsj.hrms.businessobject.sys.dbinit.FormationBase;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 *<p>Title:FormationBaseTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Oct 11, 2008:3:31:19 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class FormationBaseTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String fieldsetid = (String)this.getFormHM().get("setid");//子集A01,B01...
		ArrayList fieldlist = (ArrayList)this.getFormHM().get("selectedlistfield");
		if(fieldlist!=null){
			ArrayList fielditemlist = new ArrayList();//选择的所有子集
			for(int i=0;i<fieldlist.size();i++){
				RecordVo dbvo = (RecordVo)fieldlist.get(i);
				String itemid = dbvo.getString("itemid");
				fielditemlist.add(itemid);
			}
			//DataDictionary.refresh();
			ArrayList itemlist = DataDictionary.getFieldList(fieldsetid,Constant.USED_FIELD_SET);
			for(int i=0;i<itemlist.size();i++){
				FieldItem fielditem=(FieldItem)itemlist.get(i);
				fielditemlist.add(fielditem.getItemid().toString().toUpperCase());
			}
			String type = "";//判断指标是否构库
			String infor=fieldsetid.substring(0,1);//表示某一个指标 A,B,K
			ContentDAO dao = new ContentDAO(this.frameconn);
			try {
				this.frowset = dao.search("select UseFlag from fieldset where fieldSetId = '"+fieldsetid+"'");
				if(this.frowset.next()){
					type = this.frowset.getString("UseFlag");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			Connection conn = null;
			try{
				conn =com.hrms.frame.utility.AdminDb.getConnection();
				FormationBase base = new FormationBase(dao, conn);
				base.formation(type,infor,fieldsetid,fielditemlist,userView);
				DBMetaModel dbm = new DBMetaModel(conn);
				if("A".equalsIgnoreCase(infor)){
					//ArrayList dblist = userView.getPrivDbList();
					ArrayList dblist = DataDictionary.getDbpreList();
					for(int i=0;i<dblist.size();i++){
						String dbpre = (String)dblist.get(i);
						dbm.reloadTableModel(dbpre+fieldsetid);
					}
				}else{
					dbm.reloadTableModel(fieldsetid);
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(conn!=null)
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		}
		
	}
}
