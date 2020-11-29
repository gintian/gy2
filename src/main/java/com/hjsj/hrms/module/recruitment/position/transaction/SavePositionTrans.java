package com.hjsj.hrms.module.recruitment.position.transaction;

import com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

public class SavePositionTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try {
        	String datastr = SafeCode.decode((String) this.getFormHM().get("datastr"));
            String perJson = (String) this.getFormHM().get("perJson");
            ArrayList dataList = (ArrayList) this.getFormHM().get("dataList"); // 需要保存的字段
            String type = (String) this.getFormHM().get("type");
            
            datastr = PubFunc.keyWord_reback(datastr);
            
            JSONObject memberdata=null;
            if(perJson!=null&&perJson.length()>0){
                perJson = PubFunc.keyWord_reback(perJson);
                memberdata = JSONObject.fromObject(perJson);
            }   
            JSONObject objdata = JSONObject.fromObject(datastr); // z03表中的数据
            PositionBo pobo = new PositionBo(this.getFrameconn(),
                    new ContentDAO(this.getFrameconn()), this.getUserView());
            //招聘渠道
            Object codeIds = objdata.get("z0336");
            if(codeIds!=null)
            	pobo.checkPrivChannel((String) codeIds);
            //其他招聘渠道
            codeIds = objdata.get("z0384");
            if(codeIds!=null&& StringUtils.isNotEmpty((String) codeIds))
            	pobo.checkPrivChannel((String) codeIds);
            String z0301 = pobo.savePosition(dataList, objdata, type); // 保存z03表中的字段
            if(!"edit".equals(type))
                pobo.savePosMember(memberdata, type, z0301);

            this.getFormHM().put("type", type);

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }

}
