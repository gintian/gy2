package com.hjsj.hrms.transaction.report.edit_report;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.util.HashMap;

public class ExistWeekTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			if(hm.get("tabid")!=null)
			{
				String tabid=(String)hm.get("tabid");
//				if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
//					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
				String tableName="ta_"+tabid;
				DbWizard dbWizard=new DbWizard(this.getFrameconn());
				Table table=new Table(tableName);
				if(dbWizard.isExistTable(tableName, false))
				{
					RecordVo vo=new RecordVo(tableName);
					if(!vo.hasAttribute("weekid"))
					{
						
						Field obj=new Field("weekid","weekid");
						obj.setDatatype(DataType.INT);
						obj.setAlign("left");				
						table.addField(obj);
						dbWizard.addColumns(table);
						DBMetaModel dbmodel=new DBMetaModel(this.getFrameconn());
						dbmodel.reloadTableModel(tableName);
					}
				}
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				RowSet recset=dao.search("select tsortid,xmlstyle from tname where tabid="+tabid);
				if(recset.next())
				{
					String scope_cond = Sql_switcher.readMemo(recset, "xmlstyle");
					Document doc=null;
					String auto_arch="";
					if(scope_cond!=null&&scope_cond.trim().length()>0){
						
						doc = PubFunc.generateDom(scope_cond);
						if(doc!=null){
							XPath xPath = XPath.newInstance("/param/auto_archive");
							Element auto_archiv = (Element) xPath.selectSingleNode(doc);
							if (auto_archiv != null) {
								auto_arch=auto_archiv.getText();
							}
							if(auto_arch!=null){
								this.getFormHM().put("auto_archive", auto_arch);
							}else{
								this.getFormHM().put("auto_archive",  "0");								
							}
						}
					}else{
						this.getFormHM().put("auto_archive", "0");
					}
				}
			}
			hm.remove("tabid");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
