package com.hjsj.hrms.transaction.general.salarychange;

import com.hjsj.hrms.businessobject.general.salarychange.ChangeFormulaBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class OutItemTableTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String tableid = (String)this.getFormHM().get("tableid");
		tableid=tableid!=null&&tableid.trim().length()>0?tableid:"";
		
		String flag = (String)this.getFormHM().get("flag");
		flag=flag!=null&&flag.trim().length()>0?flag:"";
		
		String item = (String)this.getFormHM().get("item");
		item=item!=null&&item.trim().length()>0?item:"";
		item = SafeCode.decode(item);

		String itemid = (String)this.getFormHM().get("itemid");
		itemid=itemid!=null&&itemid.trim().length()>0?itemid:"";
		
		String cfactor = (String)this.getFormHM().get("cfactor");
		cfactor=cfactor!=null&&cfactor.trim().length()>0?cfactor:"";
		cfactor = SafeCode.decode(cfactor);
		item = PubFunc.keyWord_reback(item);
		cfactor = PubFunc.keyWord_reback(cfactor);
		ChangeFormulaBo formulabo = new ChangeFormulaBo();
		item=item.replace("START*DATE", "START_DATE");
		item=item.replace("start*date", "start_date");
		if("save".equalsIgnoreCase(flag)){

			String name = (String)this.getFormHM().get("name");
			name=name!=null&&name.trim().length()>0?name:"";
			name = PubFunc.keyWord_reback(name);
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String id = formulabo.saveItem(dao,tableid,name,item,cfactor,this.getFrameconn());
			this.getFormHM().put("id",id);
			this.getFormHM().put("info","ok");
		}else if("alert".equalsIgnoreCase(flag)){
			String id = (String)this.getFormHM().get("id");
			id=id!=null&&id.trim().length()>0?id:"";
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			formulabo.alertItem(dao,tableid,id,item,cfactor);
			this.getFormHM().put("info","ok");
		}
	}
}
