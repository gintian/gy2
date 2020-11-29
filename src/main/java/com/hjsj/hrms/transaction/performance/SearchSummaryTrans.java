package com.hjsj.hrms.transaction.performance;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
public class SearchSummaryTrans extends IBusiness {

	public void execute() throws GeneralException {
		String planid=(String)this.getFormHM().get("planNum");
		if(planid==null|| "".equals(planid))
			return;
		/**考评结果表*/
		String tableName = "per_result_" + planid;
		{	
				DBMetaModel dbmodel=new DBMetaModel(this.getFrameconn());
				dbmodel.reloadTableModel(tableName);
		}
		RecordVo vo=new RecordVo(tableName);
		StringBuffer strsql=new StringBuffer();
//		如果没有该字段则动态产生
		if(!vo.hasAttribute("summarize"))
		{
			DbWizard dbWizard=new DbWizard(this.getFrameconn());
			Table table=new Table(tableName);
			
			Field obj=new Field("summarize","summarize");
			obj.setDatatype(DataType.CLOB);
			obj.setKeyable(false);			
			obj.setVisible(false);
			obj.setAlign("left");	
			
			table.addField(obj);
			dbWizard.addColumns(table);
			DBMetaModel dbmodel=new DBMetaModel(this.getFrameconn());
			dbmodel.reloadTableModel(tableName);
			
		}
		if(vo.hasAttribute("summarize"))
		{
			strsql.append("select summarize from ");
			strsql.append(tableName);
			strsql.append(" where object_id='");
			strsql.append(userView.getA0100());
			strsql.append("'");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			try
			{
				this.frowset=dao.search(strsql.toString());
				if(this.frowset.next())
				{
					this.getFormHM().put("summary",Sql_switcher.readMemo(this.frowset,"summarize"));
				}
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			    throw GeneralExceptionHandler.Handle(ee);					
			}
		}
	}

}
