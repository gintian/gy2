package com.hjsj.hrms.module.recruitment.headhuntermanage.transaction;

import com.hjsj.hrms.module.recruitment.headhuntermanage.businessobject.HeadHunterManageBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class DeleteHunterGroupTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		
        String datas = (String)this.getFormHM().get("deletedata");
        String[] datalist = datas.split("`");
        ArrayList groupids = new ArrayList();
        Boolean result = Boolean.FALSE;
        String hinttext = "";
        int num = 0;
        try{
        	HeadHunterManageBo hmb = new HeadHunterManageBo();
	        for(int i=0;i<datalist.length;i++){
	        	//DynaBean re = (DynaBean)datas.get(i);
	        	String groupid = datalist[i].toString();
	        	groupid = PubFunc.decrypt(groupid);
	        	if(hmb.isHunterGroupCanBeDelete(groupid)){
	        		ArrayList list = new ArrayList();
	        		list.add(groupid);
	        		groupids.add(list);
	        	}else{
	        		num++;
	        		//groupids.clear();
	        		//break;
	        	}
	        }
        
	        if(!groupids.isEmpty()){
	        	String sql = " delete z60 where z6000=?";
	        		ContentDAO dao =  new ContentDAO(frameconn);
	        		int[] i =  dao.batchUpdate(sql, groupids);
	        		if(i.length>0)
					 result = Boolean.TRUE;
	        }
	        if(num>0)
	        {
        		hinttext = "已自动过滤"+num+"条含分配账号的记录！";
	        	result = Boolean.FALSE;
	        }
        }catch(Exception e){
        	e.printStackTrace();
        	result = Boolean.FALSE;
        }finally{
        	this.getFormHM().put("result",result);
        	this.getFormHM().put("hinttext",hinttext);
        }
	}
	
}
