package com.hjsj.hrms.transaction.dtgh.party;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class ChangePosDescTrans extends IBusiness{
	
    public void execute() throws GeneralException {
             
    	String codeitemid = this.getFormHM().get("codeitemid").toString();
    	String codeitemdesc = this.getFormHM().get("codeitemdesc").toString();
    	RecordVo vo = ConstantParamter.getRealConstantVo("PS_C_JOB");
    	if(vo==null)
    		return;
    	String itemid = vo.getString("str_value");
    	if(itemid.length()<5) //指标长度为5
    		return;
    	String sql = " update organization set codeitemdesc = '"+codeitemdesc+"' where codeitemid in (select e01a1 from k01 where "+itemid+"='"+codeitemid+"' ) ";
    	 try{
    	ContentDAO dao = new ContentDAO(frameconn);
    	dao.update(sql);
    	 }catch(Exception e){
    		 e.printStackTrace();
    	 }
    }
}
