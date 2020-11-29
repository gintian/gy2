package com.hjsj.hrms.transaction.report.edit_report;

import com.hjsj.hrms.businessobject.report.TformulaBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p>Title:初始化报表计算页面</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 8, 2006:9:30:50 AM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class InitFormulaTran extends IBusiness {

	public void execute() throws GeneralException {
		String result_str=(String)this.getFormHM().get("reportResultData");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String flag=(String)hm.get("flag");												//a:表内计算  b：表间计算 c:总计算
		String tabid=(String)this.getFormHM().get("tabid");
		String username = (String) hm.get("username");
		hm.remove("username");
		if(username==null|| "".equals(username)){
			username = this.userView.getUserName();
			if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
		}
		
		TformulaBo formulaBo=new TformulaBo(this.getFrameconn());
		ArrayList formulaList=formulaBo.getFormulaList(tabid,flag);						//得到计算公式
		
		this.getFormHM().put("formulaList",formulaList);
		this.getFormHM().put("formulaType",flag);
		this.getFormHM().put("username",username);
		ContentDAO dao =new ContentDAO(this.getFrameconn());
		String unitcode=(String)hm.get("a_code");
		if(this.appealflag(tabid, dao, unitcode)){
			this.getFormHM().put("dmlflag", "true");
		}else{
			this.getFormHM().put("dmlflag", "false");
		}
	}
	
	/**dml 报表汇总上级单位相同表未上报可以编辑下级上报报表，如果上报则不能编辑下及上报的报表*/
	public boolean appealflag(String tabid,ContentDAO dao,String unitcode){
		boolean flag=false;
		boolean flag1=false;
		boolean flag2=false;
		String username=this.userView.getUserName();
		try {
			this.frowset=dao.search("select * from operuser where UserName='"+username+"'");
			if(this.frowset.next()){
				String unitcode1=this.frowset.getString("unitcode");
				if(unitcode!=null&&unitcode.length()!=0){
					this.frowset=dao.search("select * from treport_ctrl where unitcode='"+unitcode1+"' and tabid='"+tabid+"'");
					if(this.frowset.next()){
						String status1=this.frowset.getString("status");
						if("1".equals(status1)|| "3".equals(status1)){
							
							flag1=true;
						}
					}
				}
			}
			if(unitcode!=null&&unitcode.length()!=0){
				this.frowset=dao.search("select * from treport_ctrl where unitcode='"+unitcode+"' and tabid='"+tabid+"'");
				if(this.frowset.next()){
					String status1=this.frowset.getString("status");
					if("1".equals(status1)|| "3".equals(status1)){
						flag2=true;
					}
				}
			}
			flag=flag1&&flag2;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	

}
