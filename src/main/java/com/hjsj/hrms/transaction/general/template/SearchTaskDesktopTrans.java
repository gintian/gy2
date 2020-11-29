/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:SearchTaskDesktopTrans</p>
 * <p>Description:查询主控任务</p> 
 * <p>Company:hjsj</p> 
 * create time at:Oct 27, 20069:36:39 AM
 * @author chenmengqing
 * @version 4.0
 */
public class SearchTaskDesktopTrans extends IBusiness {

	public void execute() throws GeneralException {
		//人事异动兼容以前记录

		DbWizard dbWizard=new DbWizard(this.getFrameconn());
		DBMetaModel dbmodel=null;
		
		HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
		if(map.get("modeType")!=null&&((String)map.get("modeType")).length()>0)
		{
			this.getFormHM().put("type",(String)this.getFormHM().get("modeType"));
			map.remove("modeType");
		}
		
//		if(!dbWizard.isExistField("t_wf_task_objlink", "submitflag",false)){
//			
//			Table table=new Table("t_wf_task_objlink");
//			Field temp21=new Field("state","记录处理状态");
//				 temp21.setDatatype(DataType.INT);
//					temp21.setNullable(true);
//					temp21.setVisible(false);			
//				table.addField(temp21);
//				temp21=new Field("submitflag","记录选中状态");
//				 temp21.setDatatype(DataType.INT);
//					temp21.setNullable(true);
//					temp21.setVisible(false);			
//				table.addField(temp21);
//				temp21=new Field("count","驳回次数");
//				 temp21.setDatatype(DataType.INT);
//					temp21.setNullable(true);
//					temp21.setVisible(false);			
//				table.addField(temp21);
//				temp21=new Field("flag","驳回标记");
//				 temp21.setDatatype(DataType.INT);
//					temp21.setNullable(true);
//					temp21.setVisible(false);			
//				table.addField(temp21);
//				 
//				try {
//					dbWizard.addColumns(table);
//					if(dbmodel==null)
//						dbmodel=new DBMetaModel(this.getFrameconn());
//					dbmodel.reloadTableModel(table.getName());
//					/*具体操作
//					 * state  0: task_state 1,2,3
//					 * state  1: task_state 4,5 已处理
//					 * state  2: state 07 驳回
//					 * 
//					 * 
//					 */
//					
//				} catch (GeneralException e) {
//					e.printStackTrace();
//				}
//		}
	
	}

}
