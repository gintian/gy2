package com.hjsj.hrms.transaction.general.inform.emp;

import com.hjsj.hrms.businessobject.ht.inform.ContracInforBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>
 * Title:saveEmpSubSet.java
 * </p>
 * <p>
 * Description:人员相关子集的保存操作
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-04-11 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class saveEmpSubSet extends IBusiness {
    public void execute() throws GeneralException {
        try {
            String a0100 = (String) this.getFormHM().get("a0100");

            String i9999 = (String) this.getFormHM().get("i9999");

            String dbname = (String) this.getFormHM().get("dbname");
            
            String flag = (String) this.getFormHM().get("flag");

            String itemtable = (String) this.getFormHM().get("itemtable");
            
            if(!"train".equalsIgnoreCase(flag)){
                itemtable = PubFunc.decrypt(itemtable);
                dbname = PubFunc.decrypt(dbname);
                a0100 = PubFunc.decrypt(a0100);
            }
            if (null != itemtable && 3 == itemtable.length())
                itemtable = dbname + itemtable;

            String curri9999 = (String) this.getFormHM().get("curri9999");

            if (curri9999 != null && "0".equals(i9999))//插入方式新增记录
                i9999 = DbNameBo.insertSubSetA0100(itemtable, a0100, curri9999, getFrameconn());

            ArrayList fieldlist = (ArrayList) this.getFormHM().get("SubFlds");

            RecordVo vo = new RecordVo(itemtable);
            vo.setString("a0100", a0100);
            FieldItem fieldItem1 = null;
            for (int i = 0; i < fieldlist.size(); i++) {
                FieldItem fieldItem = (FieldItem) fieldlist.get(i);

                if ("orgname".equalsIgnoreCase(fieldItem.getItemid())) {
                    fieldItem1 = fieldItem;
                    vo.setString(fieldItem.getItemid(), fieldItem.getViewvalue());
                    continue;
                }
                
                String itemid = fieldItem.getItemid();
                String value = fieldItem.getValue();

                if ("D".equals(fieldItem.getItemtype())) {
                    vo.setDate(itemid, value);
                } else if ("N".equals(fieldItem.getItemtype()))// 对于数值类型，在前后台都要进行控制,前台验证是整数还是小数类型，后台修正小数位数
                {
                    value = PubFunc.round(value, fieldItem.getDecimalwidth());
                    vo.setString(itemid, value);
                } else
                    vo.setString(itemid, value);
            }
            
            if ("t_vorg_staff".equalsIgnoreCase(itemtable)) {
                vo.setString("dbase", dbname);
                vo.setString("b0110", fieldItem1.getValue());
            }
            
            boolean isadd = false;
            if ("0".equals(i9999)) {
                ContracInforBo bo = new ContracInforBo(this.getFrameconn());
                i9999 = bo.getI9999(itemtable, a0100);
                isadd = true;
            }
            
            vo.setInt("i9999", Integer.parseInt(i9999));
            
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            if (isadd) {
                dao.addValueObject(vo);
            } else {
                dao.updateValueObject(vo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
