package com.hjsj.hrms.transaction.kq.query;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.options.UserManager;
import com.hjsj.hrms.businessobject.kq.query.CodingAnalytical;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.set.AdjustCode;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class QueryField extends IBusiness
{
    private static final long serialVersionUID = 1L;

    public void execute() throws GeneralException {
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String table = (String) hm.get("table");// 表名
        ArrayList fieldlist = new ArrayList();
        FieldItem item = new FieldItem();
        if ("userManager".equals(table)){
            KqParameter kq_paramter = new KqParameter(this.userView, "UN", this.frameconn);
            HashMap hashmap = kq_paramter.getKqParamterMap();
            String kq_type = (String) hashmap.get("kq_type");
            String kq_cardno = (String) hashmap.get("cardno");
            String kq_gno = (String) hashmap.get("g_no");
            
    		UserManager userManager=new UserManager(this.userView,this.getFrameconn());
    		fieldlist=userManager.getFieldList(kq_type,kq_cardno,kq_gno);
    		
    		for (Iterator it = fieldlist.iterator(); it.hasNext();) {
                item = (FieldItem) it.next();
                if ("nbase".equalsIgnoreCase(item.getItemid())) {
                    it.remove();
                }
            }
    		
        } else {
            fieldlist = new AdjustCode().getFieldByView(table);
            for (Iterator it = fieldlist.iterator(); it.hasNext();) {
                item = (FieldItem) it.next();
                if ("nbase".equalsIgnoreCase(item.getItemid())) {
                    it.remove();
                }
            }
        }
        ArrayList newList = new CodingAnalytical().getFields(fieldlist, table);
        this.formHM.put("fieldlist", newList);

        /* 考勤班次 */
        if ("q11".equalsIgnoreCase(table) || "q15".equalsIgnoreCase(table)){
            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn());
            ArrayList class_list = kqUtilsClass.getKqClassList();
            for (Iterator it = class_list.iterator(); it.hasNext();){
                CommonData class_id = (CommonData) it.next();
                String classValue = class_id.getDataValue();
                if ("#".equals(classValue)){
                    it.remove();
                    // } else if
                    // (!userView.isHaveResource(IResourceConstant.KQ_BASE_CLASS,
                    // classValue)){
                    // it.remove();
                }
            }
            this.formHM.put("class_list", class_list);
        }
		String temp="";
		String privCode=RegisterInitInfoData.getKqPrivCode(userView);
    	String privCodeValue=RegisterInitInfoData.getKqPrivCodeValue(userView);
		temp = privCode + privCodeValue + "`";
		this.getFormHM().put("orgparentcode",temp);
    }
}
