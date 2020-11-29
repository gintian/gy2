package com.hjsj.hrms.transaction.sys.dbinit;

import com.hjsj.hrms.businessobject.infor.multimedia.MultiMediaBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 *<p>Title:DeleteBaseTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Sep 11, 2008:4:33:07 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class DeleteBaseTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String infor=(String)this.getFormHM().get("infor");
		ArrayList fieldsetlist = (ArrayList)this.getFormHM().get("fieldsetlist");
		DbWizard dbw = new DbWizard(this.frameconn);
		StringBuffer sql = new StringBuffer();
		if("A".equalsIgnoreCase(infor)){
			for(int i=0;i<fieldsetlist.size();i++){
				ArrayList dblist = DataDictionary.getDbpreList();
				for(int j=0;j<dblist.size();j++){
					dbw.dropTable(dblist.get(j).toString().toLowerCase()+fieldsetlist.get(i).toString().toLowerCase());
				}
			}
		}else{
			for(int i=0;i<fieldsetlist.size();i++){
				dbw.dropTable(fieldsetlist.toString().toLowerCase());
			}
		}
		MultiMediaBo mediabo= new MultiMediaBo(this.frameconn,this.userView);
		for(int i=0;i<fieldsetlist.size();i++){
			ContentDAO dao = new ContentDAO(this.frameconn);
			RecordVo vo = new RecordVo("fieldset");
			vo.setString("fieldsetid",fieldsetlist.get(i).toString());
			sql.delete(0,sql.length());
			sql.append("update fielditem set useflag = '0' where fieldsetid = '"+fieldsetlist.get(i).toString()+"'");
			try {
				vo = dao.findByPrimaryKey(vo);
				vo.setString("useflag","0");
				dao.updateValueObject(vo);
				dao.update(sql.toString());
                mediabo.deleteMultimediaFileBySetid(fieldsetlist.get(i).toString());                
				DataDictionary.getFieldSetVo(fieldsetlist.get(i).toString()).setUseflag("0");
				ArrayList fieldlist = DataDictionary.getFieldList(fieldsetlist.get(i).toString(), Constant.USED_FIELD_SET);
				for(int n=0;n<fieldlist.size();n++){
					((FieldItem)fieldlist.get(n)).setUseflag("0");
				}
			} catch (GeneralException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				//DataDictionary.refresh();
			}
		}
	}

}
