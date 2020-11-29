package com.hjsj.hrms.transaction.org.autostatic.mainp;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchRsViewTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String b0110 = (String)this.getFormHM().get("b0110");
		try{
			String sql="select codesetid,codeitemid from organization where codeitemid='"+b0110+"'";
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sql);
			String codesetid="";
			String codeitemid="";
			if(this.frowset.next()){
				codesetid=this.frowset.getString("codesetid");
				codeitemid=this.frowset.getString("codeitemid");
			}
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			CodeItem item = null;
			String rs_view="";
			if(display_e0122==null||display_e0122.length()<1){
				item = AdminCode.getCode(codesetid, codeitemid);
			}else{
				item = AdminCode.getCode(codesetid, codeitemid, Integer.parseInt(display_e0122));
			}
			if(item!=null){
				rs_view=item.getCodename();
				String sep=sysbo.getAttributeValues(Sys_Oth_Parameter.DISPLAY_E0122, "sep");
				if("UM".equalsIgnoreCase(codesetid)){
					sql = "select codeitemdesc from organization where codeitemid="+Sql_switcher.substr("'"+codeitemid+"'","1",Sql_switcher.length("codeitemid"))+" and codesetid='UN' order by grade desc";
					this.frowset = dao.search(sql);
					if(this.frowset.next()){
						
						rs_view=this.frowset.getString("codeitemdesc")+sep+rs_view;
					}
				}
				rs_view=rs_view.replaceAll("/", sep);
				this.getFormHM().put("rs_view", rs_view);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
