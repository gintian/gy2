package com.hjsj.hrms.transaction.org.autostatic.confset;

import com.hjsj.hrms.businessobject.gz.GrossManagBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dao.tablemodel.TableModel;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class SaveDataScanTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		//String name=(String)hm.get("position_set_table");
		ArrayList list=(ArrayList)hm.get("position_set_record");
		/**数据集字段列表*/
		ContentDAO dao=null;
		try{
	            dao=new ContentDAO(this.getFrameconn());
				for(int i=0;i<list.size();i++){
						RecordVo vo=(RecordVo)list.get(i);
						
						DBMetaModel dbmeta = new DBMetaModel();
					    TableModel tableModel = dbmeta.searchTable(vo.getModelName());
					    String sql = tableModel.getUpdateSql(vo, true);
					    String tablename = vo.getModelName();
					    
					    if("K".equalsIgnoreCase(tablename.substring(0,1)))
					    	sql=sql.substring(0,sql.lastIndexOf("where"))+" where "+vo.getModelName()+".e01a1= ?"+" and "+vo.getModelName()+".i9999= ?";
					    else
					    	sql=sql.substring(0,sql.lastIndexOf("where"))+" where "+vo.getModelName()+".b0110= ?"+" and "+vo.getModelName()+".i9999= ?";
					    GrossManagBo gross = new GrossManagBo();
					    ArrayList values = gross.getUpdateValues(vo,tableModel);
					    if("K".equalsIgnoreCase(tablename.substring(0,1)))
					    	values.add(values.size(),vo.getString("e01a1"));
					    else
					    	values.add(values.size(),vo.getString("b0110"));
					    values.add(values.size(),vo.getString("i9999"));
					    
						dao.update(sql,values);
				}
		}
		catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
			
		}
		
	}
}
