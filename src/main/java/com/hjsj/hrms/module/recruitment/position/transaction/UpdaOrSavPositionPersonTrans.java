package com.hjsj.hrms.module.recruitment.position.transaction;

import com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class UpdaOrSavPositionPersonTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try{
            String func = (String)this.getFormHM().get("func");
            String nbsa0100=PubFunc.decrypt((String)this.getFormHM().get("id"));
            String memberType=(String)this.getFormHM().get("type");
            String z0301 = PubFunc.decrypt((String)this.getFormHM().get("z0301"));
            String b0110 =PubFunc.decrypt((String)this.getFormHM().get("b0110"));
            String e0122 =PubFunc.decrypt((String)this.getFormHM().get("e0122"));
            String e01a1 =PubFunc.decrypt((String)this.getFormHM().get("e01a1"));
            String a0101 = (String)this.getFormHM().get("a0101");
            String photo = (String)this.getFormHM().get("photo");
            String memberId = (String)this.getFormHM().get("memberId");
            String id ="";
            PositionBo pobo= new PositionBo(this.getFrameconn(), new ContentDAO(this.getFrameconn()), this.getUserView());
    
            if("update".equals(func)){
                if(memberId!=null&&memberId.length()>0){
                    pobo.updateMenber(nbsa0100,Integer.parseInt(memberType),z0301,b0110,e0122,e01a1,a0101,memberId);
                    id=memberId;
                }else{
                    id = pobo.saveTheMenber(nbsa0100, Integer.parseInt(memberType), z0301);
                }
            }else if("insert".equals(func)){
                id = pobo.saveTheMenber(nbsa0100, Integer.parseInt(memberType), z0301);
            }
            this.getFormHM().put("name", a0101);
            this.getFormHM().put("photo", photo);
            this.getFormHM().put("func", func);
            this.getFormHM().put("memid", id);
        }catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e); 
        }
        
    }

}
