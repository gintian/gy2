package com.hjsj.hrms.transaction.info;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 获取权限内子集的交易类
 * @Title:        DeleteSubsetDataSetTrans.java
 * @Description:  批量删除设置页面获取权限内所有的子集（不包括a01）
 * @Company:      hjsj     
 * @Create time:  2018年5月29日 上午10:58:51
 * @author        chenxg
 * @version       1.0
 */
public class DeleteSubsetDataSetTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
        ArrayList<CommonData> fieldSetList = new ArrayList<CommonData>();
        ArrayList tableList = userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
        CommonData fieldData = new CommonData("#", "");
        fieldSetList.add(fieldData);
        if(tableList != null && !tableList.isEmpty()) {
            for(int i = 0; i < tableList.size(); i++) {
                FieldSet fieldSet = (FieldSet) tableList.get(i);
                int privStatus = fieldSet.getPriv_status();
                if("0".equalsIgnoreCase(fieldSet.getUseflag()) || "A01".equalsIgnoreCase(fieldSet.getFieldsetid())
                        || (!this.userView.isSuper_admin() && 1 == privStatus))
                    continue;
                
                fieldData = new CommonData(fieldSet.getFieldsetid(), fieldSet.getFieldsetdesc());
                fieldSetList.add(fieldData);
                
            }
        }
        
        this.getFormHM().put("fieldSetDataList", fieldSetList);
    }

}
