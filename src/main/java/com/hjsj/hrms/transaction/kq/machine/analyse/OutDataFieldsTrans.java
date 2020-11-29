package com.hjsj.hrms.transaction.kq.machine.analyse;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class OutDataFieldsTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
        ArrayList fieldlist = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
        ArrayList list = new ArrayList();
        
        KqParameter para = new KqParameter(this.userView, "", this.getFrameconn());
        HashMap hashmap = para.getKqParamterMap();
        String kq_g_no = (String) hashmap.get("g_no");
        
        KqUtilsClass kq = new KqUtilsClass(this.frameconn, this.userView);
        
        CommonData bc = new CommonData();
        bc.setDataName("班次");
        bc.setDataValue("name");
        list.add(bc);
        
        for (int i = 0; i < fieldlist.size(); i++)
        {
            FieldItem fielditem = (FieldItem) fieldlist.get(i);
            if ("A".equals(fielditem.getItemtype()) || "N".equals(fielditem.getItemtype()))
            {
                if ("state".equals(fielditem.getItemid()) || "i9999".equals(fielditem.getItemid()) || "q03z2".equals(fielditem.getItemid()) || (kq.isIntoField("Q03", kq_g_no) && kq_g_no.equalsIgnoreCase(fielditem.getItemid())))
                    continue;
                if (!"i9999".equals(fielditem.getItemid()) && !"state".equals(fielditem.getItemid()) && !"q03z3".equals(fielditem.getItemid()) && !"q03z5".equals(fielditem.getItemid()))
                {
                    if ("q03z0".equals(fielditem.getItemid()) || "a0101".equals(fielditem.getItemid()) || "a0100".equals(fielditem.getItemid()) || "nbase".equals(fielditem.getItemid()) || "b0110".equals(fielditem.getItemid()) || "e0122".equals(fielditem.getItemid()) || "e01a1".equals(fielditem.getItemid()))
                    {
                        continue;
                    }
                    else
                    {
                        CommonData da = new CommonData();
                        da.setDataName(fielditem.getItemdesc());
                        da.setDataValue(fielditem.getItemid());
                        list.add(da);

                    }
                }
            }
        }
        this.getFormHM().put("outfieldlist", list);
        this.getFormHM().put("outfieldsname", "");
    }

}
