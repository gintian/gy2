package com.hjsj.hrms.transaction.performance.commend.insupportcomend;

import com.hjsj.hrms.businessobject.performance.commend.CommendXMLBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class CtrlParamSetTrans extends IBusiness{
     public void execute() throws GeneralException{
    	try{
    		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
    		ContentDAO dao = new ContentDAO(this.getFrameconn());
    		ArrayList list = new ArrayList();
    		CommendXMLBo bo = new CommendXMLBo(this.getFrameconn());
    		if(hm == null)
    			return;
    		String oper=(String)hm.get("oper");
    		String ids = (String)hm.get("selectIds");
    		if(oper != null && "1".equals(oper)){
    			if(ids != null&& ids.trim().length()>0){
    				String[] str_Arr = ids.substring(1).split(",");
    				for(int i=0;i<str_Arr.length;i++){
    					DynaBean bean = new LazyDynaBean();
    					String sql = "select p0203 from p02 where p0201 = "+str_Arr[i];
    					this.frowset=dao.search(sql);
    					while(this.frowset.next()){
    						bean.set("p0203",this.frowset.getString("p0203"));
    					}
    					String id ="";
    					if(bo.getCtrl_paraValue(str_Arr[i],CommendXMLBo.vote_count) != null)
    						id = bo.getCtrl_paraValue(str_Arr[i],CommendXMLBo.vote_count);
    					bean.set("ctrl_param",id);
    					bean.set("p0201",str_Arr[i]);
    					list.add(bean);
    				}
    			}
    			this.getFormHM().put("parameterSetList",list);
    		}else if("2".equals(oper)){
    			ArrayList parameterSetList = (ArrayList)this.getFormHM().get("parameterSetList");
    			if(parameterSetList != null && parameterSetList.size()!=0){
    				bo.setCtrl_paramValue(parameterSetList,CommendXMLBo.vote_count);
    			}
    			
    		}
    		
    		
    	 }catch(Exception e){
    		 e.printStackTrace();
    	 }
    	 
     }

}
