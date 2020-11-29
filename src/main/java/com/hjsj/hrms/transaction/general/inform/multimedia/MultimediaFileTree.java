/**
 * <p>Title:MultimediaFileTree.java</p>
 * <p>Description>:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2014-4-22 下午01:46:40</p>
 * <p>@version: 6.0</p>
 * <p>@author:wangrd</p>
 */
package com.hjsj.hrms.transaction.general.inform.multimedia;

import com.hjsj.hrms.businessobject.infor.multimedia.MultiMediaBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:MultimediaFileTree.java</p>
 * <p>Description>:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2014-4-22 下午01:46:40</p>
 * <p>@author:wangrd</p>
 * <p>@version: 6.0</p>
 */
public class MultimediaFileTree extends IBusiness {

    public void execute() throws GeneralException 
    {
        try
        {
            HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
            String dbflag = (String)hm.get("dbflag");
            String nbase = (String)hm.get("nbase");
            String A0100 = (String)hm.get("a0100");
            //当登录用户的a0100和查看的人员的a0100一致时，默认为是查看自己的子集附件，不在校验权限
            if(!this.userView.getA0100().equalsIgnoreCase(A0100)) {
            	CheckPrivSafeBo checkPiv = new CheckPrivSafeBo(this.frameconn, this.userView);
            	nbase = checkPiv.checkDb(nbase);
            	A0100 = checkPiv.checkA0100("", nbase, A0100, "");
            }
            
            String I9999 = (String)hm.get("i9999");
            String setid = (String)hm.get("setid");
            String canEdit = (String)hm.get("canedit");
            hm.remove("canedit");        
            if(canEdit==null){
                canEdit = "true";
            }
            MultiMediaBo multiMediaBo = new MultiMediaBo(this.frameconn,this.userView );
            multiMediaBo.initParam();
            if (I9999==null)
                I9999="0";
 
            if (dbflag ==null)  dbflag="A";
            String kind="6";
            if("A".equals(dbflag)){
                kind="6";
            }
            else  if("B".equals(dbflag)){
                kind="";
            }
            if("K".equals(dbflag)){
                kind="0";
            }
            
            this.getFormHM().put("kind",kind);   
            this.getFormHM().put("dbflag",dbflag);   
            this.getFormHM().put("nbase",nbase);   
            this.getFormHM().put("a0100",A0100);   
            this.getFormHM().put("i9999",I9999);   
            this.getFormHM().put("setid",setid);   
            this.getFormHM().put("display_state","yes");   
            this.getFormHM().put("canedit",canEdit);     
            this.getFormHM().put("multimediaflag","");   
            //if(this.userView.isBbos())
            this.getFormHM().put("is_yewu","yes"); 
            this.getFormHM().put("isvisible","0");
  
        }catch(Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
