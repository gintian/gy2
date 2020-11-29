package com.hjsj.hrms.transaction.report.report_collect;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.HashMap;
public class GetReportReturnDescTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String tabid=(String)this.getFormHM().get("tabid");
		String unitcode=(String)this.getFormHM().get("unitcode");		
		String desc="";
		ContentDAO dao=new ContentDAO(this.frameconn);
		RowSet recset=null;
		try
		{	//liuy 2015-1-22 6881：精算报表/编辑报表：查看被上级驳回的表，点击驳回后台报错，且看不到驳回原因 start
			//if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
				//throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
			//liuy 2015-1-22 end
			//dml flag=1 单独驳回flag=2 表类批量驳回；
			if((hm.get("flag")!=null&& "1".equals((String)hm.get("flag")))||(hm.get("flag")==null)){
				if(hm.get("bopt")!=null&& "1".equals((String)hm.get("bopt")))
				{
					recset=dao.search("select description from treport_ctrl where unitcode=(select unitcode from operuser where username='"+this.getUserView().getUserName()+"') and tabid="+tabid);
					hm.remove("bopt");
					if(recset.next())
						desc=Sql_switcher.readMemo(recset,"description");	
				}
				else if(hm.get("bopt")!=null&& "3".equals((String)hm.get("bopt")))
				{
					String report_id=(String)hm.get("report_id");
					unitcode=(String)hm.get("unitcode");
					String cycle_id=(String)hm.get("cycle_id");
					
					RecordVo vo=new RecordVo("tt_calculation_ctrl");
					vo.setInt("id", Integer.parseInt(cycle_id));
					vo.setString("unitcode",unitcode);
					vo.setString("report_id",report_id);
					vo=dao.findByPrimaryKey(vo);
					desc=vo.getString("description");
				}
				else
				{	recset=dao.search("select description from treport_ctrl where unitcode='"+unitcode+"' and tabid="+tabid);
					if(recset.next())
						desc=Sql_switcher.readMemo(recset,"description");			
				}
			}else{
				
			}
			desc=desc.replaceAll("\r\n","&&");
			this.getFormHM().put("desc",desc);
			this.getFormHM().put("tabid",tabid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
