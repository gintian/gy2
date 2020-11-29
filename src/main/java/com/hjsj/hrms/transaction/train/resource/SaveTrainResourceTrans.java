package com.hjsj.hrms.transaction.train.resource;

import com.hjsj.hrms.businessobject.train.resource.TrainResourceBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:SaveTrainResourceTrans.java
 * </p>
 * <p>
 * Description:保存培训体系交易类
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-07-21 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SaveTrainResourceTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String type = (String) hm.get("type");
        String priFldValue = (String) this.getFormHM().get("primaryKeyVal");
        String primaryField = (String) this.getFormHM().get("primaryField");
        String oper = (String) hm.get("oper");

        String a_code = (String) this.getFormHM().get("a_code");
        a_code = a_code != null ? a_code : "";

        TrainResourceBo bo = new TrainResourceBo(this.frameconn, type);
        String recTable = bo.getRecTable();
        ContentDAO dao = new ContentDAO(this.getFrameconn());

        if(priFldValue != null && priFldValue.length() > 0 && !"null".equalsIgnoreCase(priFldValue))
            priFldValue = PubFunc.decrypt(SafeCode.decode(priFldValue));
        
        if(a_code != null && a_code.length() > 0 && !"null".equalsIgnoreCase(a_code))
            a_code = PubFunc.decrypt(SafeCode.decode(a_code));
        
        boolean isNew = bo.isNew(priFldValue);
        ArrayList fieldlist = (ArrayList) this.getFormHM().get("fields");

        String I9999 = "";
        RecordVo vo = new RecordVo(recTable);
        for (int i = 0; i < fieldlist.size(); i++)
        {
            FieldItem fieldItem = (FieldItem) fieldlist.get(i);

            String itemid = fieldItem.getItemid();
            String value = fieldItem.getValue();

            if ("D".equals(fieldItem.getItemtype()))
            {
                value = PubFunc.replace(value, ".", "-");
                vo.setDate(itemid, value);
            }
            else if ("N".equals(fieldItem.getItemtype()))// 对于数值类型，在前后台都要进行控制,前台验证是整数还是小数类型，后台修正小数位数
            {
                value = PubFunc.round(value, fieldItem.getDecimalwidth());
                vo.setString(itemid, value);
            }
            else if ("M".equals(fieldItem.getItemtype()))// 对于备注类型，在后台进行控制  wangb 20170911 31420
            {
            	value = fieldItem.getValue();
            	//指标长度为0和10的之后默认为不限制长度
            	if(fieldItem.getItemlength() != 0 && fieldItem.getItemlength() != 10 && fieldItem.getItemlength() < value.getBytes().length )
            		throw  GeneralExceptionHandler.Handle(new Exception("指标"+fieldItem.getItemid()+"最大长度为"+fieldItem.getItemlength()+"!"));
            	 vo.setString(itemid, value);
            }
            else
                vo.setString(itemid, PubFunc.keyWord_reback(value));
            if ("b0110".equalsIgnoreCase(itemid))
                I9999 = bo.getI9999(value);
        }
        if ("5".equals(type))
        {
            // vo.setString("r0700", a_code);
            this.getFormHM().put("a_code", SafeCode.encode(PubFunc.encrypt(a_code)));
        }
        else if ("2".equals(type))
        {
            //处理内部教师关联字段
            String nbase = (String)this.getFormHM().get("nbase");
            String a0100 = (String)this.getFormHM().get("a0100");
            if(StringUtils.isNotEmpty(nbase) && nbase.length() > 3) {
            	nbase = StringUtils.isEmpty(nbase) ? "" : PubFunc.decrypt(nbase);
            	a0100 = StringUtils.isEmpty(a0100) ? "" : PubFunc.decrypt(a0100);
            }
            
            DbWizard db = new DbWizard(this.frameconn);
          
            if(null != nbase && db.isExistField("r04", "nbase", false))
              vo.setString("nbase", nbase);
            
            if(null != a0100 && db.isExistField("r04", "a0100", false))
              vo.setString("a0100", a0100);
		}
        vo.setString("i9999", I9999);
        vo.setString(primaryField.toLowerCase(), priFldValue);
        if (isNew)
        {
            dao.addValueObject(vo);
            if ("savecontinue".equalsIgnoreCase(oper))
                hm.put("priFldValue", "");
        }
        else
            try
            {
                dao.updateValueObject(vo);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
    }

}
