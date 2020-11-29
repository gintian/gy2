package com.hjsj.hrms.module.recruitment.thirdpartyresume.transaction;

import com.hjsj.hrms.module.recruitment.thirdpartyresume.base.ThirdPartyResumeBase;
import com.hjsj.hrms.module.recruitment.thirdpartyresume.base.ThirdPartyResumeSourceFactory;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @ClassName: SetResumeItemTrans 
 * @Description: TODO保存代码对应设置参数
 * @author zhangcq
 * @date 2016-12-27 
 *
 */
public class SetResumeItemTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM();
		String thirdpartName = (String) this.getFormHM().get("name");
		String resumeID = (String) hm.get("resumeID");
		String resumeitem = (String) hm.get("list");
		resumeitem = PubFunc.keyWord_reback(resumeitem);
		
		String ehrfld = "";
		LazyDynaBean codesetBean = new LazyDynaBean();
		ArrayList<LazyDynaBean> list =new ArrayList<LazyDynaBean>();
		
		if(resumeitem==null||resumeitem.trim().length()<=0){
			throw new GeneralException(ResourceFactory.getProperty("label.hireemploye.no.codeitem.and.no.corresponding"));
		}
		ThirdPartyResumeBase base = ThirdPartyResumeSourceFactory.getThirdPartyResumeBo(this.frameconn, thirdpartName, this.userView);
		HashMap param = base.getResumeParam();
        ArrayList<LazyDynaBean> codesetList = (ArrayList<LazyDynaBean>) param.get("codesets");
        ArrayList<LazyDynaBean> itemList = new ArrayList<LazyDynaBean>();
        
        for(int i = 0; i < codesetList.size(); i++){
            codesetBean = codesetList.get(i);
            String resumefld = (String) codesetBean.get("ehrcodeset");
            if(resumeID != null && resumeID.equalsIgnoreCase(resumefld)) {
                ehrfld = (String) codesetBean.get("ehrcodeset");
                list.add(codesetBean);
                break;
            }
        }
        
		ArrayList<String>  listResume =new ArrayList<String>();
		String[] resumeArray = resumeitem.split("\\|");
		for(int i = 0;i< resumeArray.length; i++)
			listResume.add(resumeArray[i]);
		
		HashMap map = new HashMap();
        
        for(int i = 0;i< listResume.size(); i++){
			LazyDynaBean  bean = new LazyDynaBean();
			String codeitem = listResume.get(i);
			String[] codeitems = codeitem.split("=");
			String ehritemid = codeitems[0]; 
			
			String resumeitemid ="";
			if(codeitems.length > 1)
			    resumeitemid = codeitems[1]; 
			
			resumeitemid = resumeitemid == null ? "" : resumeitemid;
			if(resumeitemid.indexOf("`") > -1)
			    resumeitemid = resumeitemid.substring(0, resumeitemid.indexOf("`"));
			
			if(resumeitemid.indexOf(":") > -1)
                resumeitemid = resumeitemid.substring(0, resumeitemid.indexOf(":"));
			
			bean.set("ehritemid", ehritemid);
			bean.set("resumeitemid", resumeitemid);
			itemList.add(bean);
		}
		
		map.put("codesets", list);
        HashMap<String , ArrayList<LazyDynaBean>> itemsMap = new HashMap<String, ArrayList<LazyDynaBean>>();
        itemsMap.put(ehrfld, itemList);
        map.put("codeitems", itemsMap);
		base.saveResumeCode(map); 

	}
}
