package com.hjsj.hrms.transaction.train.resource;

import java.util.ArrayList;
import java.util.HashMap;

import com.hjsj.hrms.businessobject.train.resource.TrainProjectBo;
import com.hjsj.hrms.businessobject.train.resource.TrainResourceBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * <p>
 * Title:SaveTrainProTrans.java
 * </p>
 * <p>
 * Description:保存培训项目交易类
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-07-29 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SaveTrainProTrans extends IBusiness
{
    @Override
    public void execute() throws GeneralException
    {
        try {
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String priFldValue = (String) hm.get("priFldValue");
            if(priFldValue != null && priFldValue.length()>1 && !"null".equalsIgnoreCase(priFldValue)) {
                priFldValue = PubFunc.decrypt(SafeCode.decode(priFldValue));
            }
            hm.remove("priFldValue");
            String oper = (String)hm.get("oper");
            //上级项目代码
            String pcodeitem = "";
            //新增项目名称
            String codename = "";
            //新增项目代码
            String codeitem = "";
            
            TrainResourceBo bo = new TrainResourceBo(this.frameconn, "6");
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            
            TrainProjectBo bo1 = new TrainProjectBo(this.frameconn);
            
            boolean isNew = bo.isNew(priFldValue);
            ArrayList fieldlist = (ArrayList) this.getFormHM().get("fields");
    
            String i9999="";
            RecordVo vo = new RecordVo("r13");
            for (int i = 0; i < fieldlist.size(); i++)
            {
                FieldItem fieldItem = (FieldItem) fieldlist.get(i);
                
                String itemid = fieldItem.getItemid();
                String value = fieldItem.getValue();
    
                // jazz 56592 r1301：fielditem中的值时而加密，时而未加密，无法判断。改为直接取priFldValue。
                if ("r1301".equalsIgnoreCase(itemid)) {
                    value = priFldValue;
                }
    
                if("r1308".equalsIgnoreCase(itemid)) {
                    value = PubFunc.decrypt(SafeCode.decode(value));
                }
                
                if("D".equals(fieldItem.getItemtype())) {
                    // 65939 zxj 20200926 此处不支持.分隔符
                    vo.setDate(itemid, value.replace(".", "-"));
                }
                else
                {
                    vo.setString(itemid, value);
                }       
                if(itemid.equalsIgnoreCase("b0110")) {
                    i9999 = bo.getI9999(value);
                }
                
                if(itemid.equalsIgnoreCase("r1308")) {
                    pcodeitem = value;
                }
                
                if(itemid.equalsIgnoreCase("r1301")) {
                    codeitem=value;
                }
            }
            
            vo.setString("i9999", i9999);
            
            if (isNew)
            {       
                dao.addValueObject(vo);
                if(oper.equalsIgnoreCase("savecontinue")) {
                    hm.put("priFldValue","");
                }
                //新增时候添加代码
                codename=bo1.getR1308(codeitem);
                CodeItem code=new CodeItem();
                if(!pcodeitem.equals(""))
                {
                 code.setCcodeitem(pcodeitem);
                 code.setPcodeitem(pcodeitem);
                }      
                code.setCodeid("1_06");
                code.setCodeitem(codeitem);
                code.setCodename(codename);
              
                AdminCode.addCodeItem(code);
            } else {
                dao.updateValueObject(vo);
                codename=bo1.getR1308(codeitem);
                AdminCode.updateCodeItemDesc("1_06",codeitem,codename);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
