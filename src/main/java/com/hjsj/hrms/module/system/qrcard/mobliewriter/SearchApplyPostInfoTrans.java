package com.hjsj.hrms.module.system.qrcard.mobliewriter;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.transaction.mobileapp.template.MobileTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchApplyPostInfoTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        String message = "";
        String succeed = "false";
        String param = (String) this.formHM.get("param");
        try {
            MobileTemplateBo bo = new MobileTemplateBo(this.frameconn,this.userView);
            String jsonstr = bo.getTemplateInfo(param);  
            succeed = "true";
            //获取多媒体文件大小
            ConstantXml constantXml = new ConstantXml(this.frameconn,"FILEPATH_PARAM");
            String photoSize = constantXml.getNodeAttributeValue("/filepath/multimedia", "maxsize");
            if ((photoSize==null) ||("".equals(photoSize))){                
                photoSize="0";
            }
            photoSize= photoSize.toUpperCase();
            int k=1;
            if (photoSize.indexOf("K")>0){
                k=1;
            }
            else if (photoSize.indexOf("M")>0){
                k=1024;
            }
            else if (photoSize.indexOf("G")>0){
                k=1024*1024;
            }
            else if (photoSize.indexOf("T")>0){
                k=1024*1024*1024;
            } 
            photoSize =photoSize.replaceAll("K", "").replaceAll("M", "")
            .replaceAll("G", "").replaceAll("T", "").replaceAll("B", "");
            if ("".equals(photoSize)){
                photoSize="0";
            }
            float maxsize = 0.0f;
            maxsize = Float.parseFloat(photoSize)*k;
            this.formHM.put("maxsize", maxsize);
            this.formHM.put("data",jsonstr);
        } catch (Exception e) {
            if(message.trim().length()==0){
                message =e.getMessage();
            }
            this.getFormHM().put("msg", message);
            this.getFormHM().put("flag",succeed);
            e.printStackTrace();
        }
        
    }
}
