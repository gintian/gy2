/*
 * @(#)parseSubdomian.java 2018年6月27日上午9:54:15
 * ehr
 * Copyright 2018 HJSOFT, Inc. All rights reserved.
 * HJSOFT PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.hjsj.hrms.module.system.qrcard.mobliewriter;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @Titile: ParseSubdomain
 * @Description:
 * @Company:hjsj
 * @Create time: 2018年6月27日上午9:54:15
 * @author: wangz
 * @version 1.0
 *
 */
public class ParseSubdomain extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        MorphDynaBean data =  (MorphDynaBean) this.formHM.get("data");
        ArrayList fieldList = (ArrayList) data.get("fieldList");
        for(int i = 0;i<fieldList.size();i++) {
            MorphDynaBean fieldBean = (MorphDynaBean) fieldList.get(i);
            HashMap field = PubFunc.DynaBean2Map(fieldBean);
            if("1".equals(field.get("subflag"))) {//代表是子集
                ArrayList sub_domainList = (ArrayList) field.get("sub_domain");
                ArrayList valueList = (ArrayList) field.get("value");
                for(int j = 0;j<sub_domainList.size();j++) {
                    MorphDynaBean sub_domainBean = (MorphDynaBean) sub_domainList.get(j);
                    HashMap sub_domain = PubFunc.DynaBean2Map(sub_domainBean);
                    String code_id = (String) sub_domain.get("code_id");
                    String item_id = (String) sub_domain.get("item_id");
                    if(code_id!=null&&!"0".equals(code_id)) {//代表该指标类型为代码型指标
                    	if(valueList != null){
                    		for(int k =0;k<valueList.size();k++) {
                    			MorphDynaBean valueBean = (MorphDynaBean) valueList.get(k);
                    			HashMap value = PubFunc.DynaBean2Map(valueBean);
                    			String realValue = (String) value.get(item_id.toUpperCase());
                    			String view_value = AdminCode.getCodeName(code_id, realValue);
                    			Map<String,String> temp = new HashMap<String,String>();
                    			temp.put("realValue",'"'+realValue+'"');
                    			temp.put("view_value",'"'+view_value+'"');
                    			value.put(item_id.toUpperCase(), temp);
                    		}
                    	}
                    }
                }
            }
        }
        this.formHM.put("data",data);
    }

}
