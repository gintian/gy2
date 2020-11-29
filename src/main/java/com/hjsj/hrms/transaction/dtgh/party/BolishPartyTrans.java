package com.hjsj.hrms.transaction.dtgh.party;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 * @author xujian
 *Jan 19, 2010
 */
public class BolishPartyTrans extends IBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		String end_date = (String)this.getFormHM().get("end_date");
		String codeitemid = (String)this.getFormHM().get("codeitemid");
		String[] codeitemlist = codeitemid.split("`");
		String a_code = (String)this.getFormHM().get("a_code");
		ArrayList codeitemidlist = new ArrayList();
		try{
				for(int i=0;i<codeitemlist.length;i++){
		      	   	codeitemid=codeitemlist[i];
					doBolish(end_date,a_code.substring(0,2),codeitemid);
					codeitemidlist.add(a_code.substring(0,2)+codeitemid);
				}
				
		}catch(Exception e){
			this.getFormHM().put("isrefresh", "");
			this.getFormHM().put("codeitemidlist",new ArrayList());
			throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("isrefresh", "delete");
		this.getFormHM().put("codeitemidlist", codeitemidlist);
	}
	private void doBolish(String end_date,String codesetid,String codeitemid) throws Exception {
		if(codeitemid==null||codeitemid.length()<=0)
			return;
		String sql = "update codeitem set end_date="+Sql_switcher.dateValue(end_date)+" where codesetid='"+codesetid+"' and codeitemid like '"+codeitemid+"%'";
		ContentDAO dao = new ContentDAO(this.frameconn);
		if(dao.update(sql)>0){
			/*CodeItem item = new CodeItem();
			item.setCodeid(codesetid);
			item.setCodeitem(codeitemid);
			AdminCode.removeCodeItem(item);*/
		}
	}
}
