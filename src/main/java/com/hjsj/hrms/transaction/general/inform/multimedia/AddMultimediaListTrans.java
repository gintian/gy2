package com.hjsj.hrms.transaction.general.inform.multimedia;

import com.hjsj.hrms.businessobject.infor.multimedia.MultiMediaBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:AddMultimediaListTrans.java</p>
 * <p>Description>:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2014-4-25 上午09:41:17</p>
 * <p>@author:wangrd</p>
 * <p>@version: 6.0</p>
 */
public class AddMultimediaListTrans extends IBusiness {

	public  void execute()throws GeneralException
	{	
	    try
        {   
    		ContentDAO dao = new ContentDAO(this.getFrameconn());
    		String kind = (String)this.getFormHM().get("kind");	
    		String filetype = (String)this.getFormHM().get("filetype");	
    		
    		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");    
    		String editflag = (String)hm.get("editflag");
    		String pk_id = (String)hm.get("pk_id");
    		pk_id = PubFunc.decrypt(pk_id);
    		hm.remove("editflag");
    		if (editflag==null) editflag="false"; 

    		String dbflag = (String)this.getFormHM().get("dbflag");
    		String nbase = (String)this.getFormHM().get("nbase");
            String A0100 = (String)this.getFormHM().get("a0100");
            CheckPrivSafeBo checkPiv = new CheckPrivSafeBo(this.frameconn, this.userView);
			nbase = checkPiv.checkDb(nbase);
            A0100 = checkPiv.checkA0100("", nbase, A0100, "");
            
            String I9999 = (String)this.getFormHM().get("i9999");
            String setid = (String)this.getFormHM().get("setid");
            String multimediaflag = (String)this.getFormHM().get("multimediaflag");   	
    		
            MultiMediaBo multiMediaBo = null;
          //信息审核：信息变动表和子集数据对应改为用guidkey对应，因此i9999有可能取到的是guidkey，因此加上是否是数字的校验
            if(!StringUtils.isNumeric(I9999)) {
            	if(I9999.indexOf("-") == -1) {
              		I9999 = PubFunc.validateNum(I9999,4)?I9999:PubFunc.decrypt(I9999);
              		multiMediaBo = new MultiMediaBo(this.frameconn,this.userView,
              				dbflag,nbase,setid,A0100,Integer.parseInt(I9999));
              	} else {
              		multiMediaBo = new MultiMediaBo(this.frameconn,this.userView,
              				dbflag,nbase,setid,A0100,I9999);
              	}
            } else {
              	multiMediaBo = new MultiMediaBo(this.frameconn,this.userView,
              			dbflag,nbase,setid,A0100,Integer.parseInt(I9999));
            }
    	
            ArrayList fileTypeList = multiMediaBo.getTypeList(dao,kind,A0100);
            this.getFormHM().put("mediaId",pk_id);            
            this.getFormHM().put("fileTypeList",fileTypeList);
            this.getFormHM().put("filetype",filetype);	
            this.getFormHM().put("editflag",editflag);    		
            this.getFormHM().put("description",""); 
            this.getFormHM().put("filetitle","");   
            if ("true".equals(editflag)){
                ArrayList list = multiMediaBo.getMultimediaRecord(pk_id);
                String filetypeValue ="";
                filetypeValue= multiMediaBo.changeFileTypeValue(pk_id);
                if ((list!=null) && (list.size()>0) ){
                    DynaBean vo = (LazyDynaBean)(list.get(0));
                    String value ="";
                    value= (String)vo.get("class");
                    if (value==null) value="";
                    if(filetypeValue!=null&&!"".equals(filetypeValue)){
                    	value = filetypeValue;
                    }
                    this.getFormHM().put("filetype",value); 
                    
                    value= (String)vo.get("topic");
                    if (value==null) value="";
                    this.getFormHM().put("filetitle",value); 
                    
                    value= (String)vo.get("description");
                    if (value==null) value="";
                    this.getFormHM().put("description",value);                     
                }   
            }        
   
        }catch(Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
	}
	
}
