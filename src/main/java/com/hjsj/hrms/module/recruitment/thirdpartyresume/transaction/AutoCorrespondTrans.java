package com.hjsj.hrms.module.recruitment.thirdpartyresume.transaction;

import com.hjsj.hrms.module.recruitment.thirdpartyresume.base.ThirdPartyResumeBase;
import com.hjsj.hrms.module.recruitment.thirdpartyresume.base.ThirdPartyResumeSourceFactory;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * @ClassName: AutoCorrespondTrans 
 * @Description: TODO代码自动对应
 * @author zhangcq
 * @date 2016-06-14 上午11:10:00 
 *
 */
public class AutoCorrespondTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM();
		String thirdpartName = (String) this.getFormHM().get("name");
		String resumeID = (String) hm.get("resumeID");
		String commonvalue = (String) hm.get("commonvalue");
		String itemname = "";
		String ehrfld = "";
		LazyDynaBean codesetBean = new LazyDynaBean();
		ArrayList<String> listValue =new ArrayList<String>();
		ArrayList<LazyDynaBean> list =new ArrayList<LazyDynaBean>();
		if(StringUtils.isNotEmpty(commonvalue)) {
		    String[] value = commonvalue.split(",");
		    for (int i = 0 ;i< value.length; i++)
		        listValue.add(value[i]);		    
		}
		
		ThirdPartyResumeBase base = ThirdPartyResumeSourceFactory.getThirdPartyResumeBo(this.frameconn, thirdpartName, this.userView);
		HashMap param = base.getResumeParam();
		ArrayList<LazyDynaBean> codesetList = (ArrayList<LazyDynaBean>) param.get("codesets");
		if(codesetList == null || codesetList.size() < 1)
		    return;
		
		HashMap<String , ArrayList<LazyDynaBean>> codetitems = (HashMap<String, ArrayList<LazyDynaBean>>) param.get("codeitems");
		ArrayList<LazyDynaBean> itemList = new ArrayList<LazyDynaBean>();
		String resumecodesetid = "0";
		for(int i = 0; i < codesetList.size(); i++){
		    codesetBean = codesetList.get(i);
		    String resumefld = (String) codesetBean.get("ehrcodeset");
		    if(resumeID != null && resumeID.equalsIgnoreCase(resumefld)) {
		        ehrfld = (String) codesetBean.get("ehrcodeset");
		        resumecodesetid = (String) codesetBean.get("resumecodesetid");
		        itemList = codetitems.get(ehrfld);
		        list.add(codesetBean);
		        break;
		    }
		}
		
		HashMap<String, ArrayList<HashMap>> resumecodeitems = (HashMap<String, ArrayList<HashMap>>) param.get("resumecodeitems");
		if(resumecodeitems != null){
		    listValue.clear();
		    if(StringUtils.isNotEmpty(resumecodesetid) && !"0".equalsIgnoreCase(resumecodesetid)) {
		        ArrayList<HashMap> resumecodeitemList = resumecodeitems.get(resumecodesetid);
		        getResumeCodeitems(resumecodeitemList, listValue);
		    }
		}
		
		HashMap map = new HashMap();
		
		FieldItem fi = DataDictionary.getFieldItem(ehrfld);
		String codesetid = fi.getCodesetid();
		 
		for (int i = 0; i < itemList.size(); i++) {
		    LazyDynaBean  bean = (LazyDynaBean) itemList.get(i);
            String ehritemid = (String) bean.get("ehritemid");
            itemname = AdminCode.getCodeName(codesetid, ehritemid);
            for (int j = 0; j < listValue.size(); j++) {
                String value = listValue.get(j);
                String codeName = value;
                boolean flag = false;
                if(value.indexOf(":") > -1) {
                    flag = true;
                    codeName = value.substring(value.indexOf(":") + 1);
                    value = value.substring(0, value.indexOf(":"));
                }
                
                bean.set("resumeitemid", "");
                
                if (codeName.contains(itemname) || itemname.contains(codeName)) {
                    if("AB".equalsIgnoreCase(codesetid) && !codeName.equalsIgnoreCase(itemname))
                        continue;
                    
                    String str = (String) bean.get("resumeitemid");
                    str = PubFunc.keyWord_reback(str);
                    if ("".equals(str) || str == null) {
                        if(!"".equals(value))
                            bean.set("resumeitemid", value);
                        str = value;
                        if(flag)
                            break;
                    } else {
                        if (!(str + ";").contains(value + ";")) 
                            bean.set("resumeitemid", str + ";" + value);
                        str = str + ";" + value;
                    }
                }
            }
        }
		map.put("codesets", list);
		HashMap<String , ArrayList<LazyDynaBean>> itemsMap = new HashMap<String, ArrayList<LazyDynaBean>>();
		itemsMap.put(ehrfld, itemList);
		map.put("codeitems", itemsMap);
		base.saveResumeCode(map); 
	
	}

	private String getResumeCodeitems(ArrayList<HashMap> resumecodeitemList, ArrayList<String> listValue) {
        StringBuffer data = new StringBuffer();
        
        for(int i = 0; i < resumecodeitemList.size(); i++) {
            HashMap resumecodeitemMap = resumecodeitemList.get(i);
            Iterator iter = resumecodeitemMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String) entry.getKey();
                if("children".equalsIgnoreCase(key)){
                    ArrayList<HashMap> childList = (ArrayList<HashMap>) entry.getValue();
                    data.append("," + getResumeCodeitems(childList, listValue));
                } else {
                    String value = (String) entry.getValue();
                    listValue.add(key + ":" + value);
                }
                
            }
        }
        
        if(data.toString().endsWith(","))
            data.setLength(data.length() - 1);
        
        return data.toString();
    }
}
