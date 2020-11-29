package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class CheckPositionTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String msg="0";
			String z0311=(String)this.getFormHM().get("z0311");
			String z0301=(String)this.getFormHM().get("z0301");
			String type=(String)this.getFormHM().get("type");
			String fromflag=(String)this.getFormHM().get("fromflag");
			/*FieldItem item = DataDictionary.getFieldItem("z0311", "z03");
			if((z0311==null||z0311.equals(""))&&!item.isVisible())
			{
				this.getFormHM().put("msg", "1");
				this.getFormHM().put("z0301", z0301);
				this.getFormHM().put("type", type);
				return;
			}*/
			String opt =(String) this.getFormHM().get("opt");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if(fromflag!=null){
				z0301=PubFunc.decrypt(z0301);
				z0311=PubFunc.decrypt(z0311);
			}else{
				String sql = (String) this.userView.getHm().get("hire_sql");
				sql = null == sql ? "" : sql;
				int index =sql.indexOf("order by");
				if(index!=-1){
					sql=sql.substring(0, index);
					sql=sql+" and z0301='"+z0301+"'";
					this.frowset=dao.search(sql);
					if(!this.frowset.next()){
						throw new GeneralException(ResourceFactory.getProperty("label.hireemploye.no.contorl"));
					}
				}
			}
			if("1".equals(type))
			{
	    		String zpName=(String)this.getFormHM().get("zpName");
	    		this.getFormHM().put("zpName", zpName);
			}
			if("2".equals(type))
			{
	    		String start_date=(String)this.getFormHM().get("sdate");
	    		String end_date=(String)this.getFormHM().get("edate");
	    		this.getFormHM().put("sdate", start_date);
	    		this.getFormHM().put("edate",end_date);
			}
			if("6".equals(type))
			{
				String posState=(String)this.getFormHM().get("posState");
				this.getFormHM().put("posState", posState);
			}
			StringBuffer buf = new StringBuffer("");
			buf.append("select codeitemid from organization where UPPER(codesetid)='@K' and codeitemid='"+z0311+"'");
			
			this.frowset=dao.search(buf.toString());
			while(this.frowset.next())
			{
				msg="1";
			}
			if(opt!=null&& "1".equals(opt)){
				this.getFormHM().put("z0301", PubFunc.encrypt(z0301));
			}else{
				this.getFormHM().put("z0301", z0301);
			}
			this.getFormHM().put("msg", msg);
			//this.getFormHM().put("z0301", z0301);
			this.getFormHM().put("type", type);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
