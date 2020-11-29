package com.hjsj.hrms.module.recruitment.thirdpartyresume.transaction;

import com.hjsj.hrms.module.recruitment.thirdpartyresume.base.ThirdPartyResumeBase;
import com.hjsj.hrms.module.recruitment.thirdpartyresume.base.ThirdPartyResumeSourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @ClassName: SavevalidTrans
 * @Description: TODO保存指标对应设置到数据xml
 * @author zhangcq
 * @date 2016-06-14 上午11:35:21
 * 
 */
public class SavevalidTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {

        HashMap hm = (HashMap) this.getFormHM();
        String thirdpartName = (String) this.getFormHM().get("name");//第三方简历名称
        String resumeset = (String) hm.get("resumeset");//人员库指标集
        ArrayList baseitems = (ArrayList) hm.get("baseitems");//简历指标集
        ArrayList itemvalues = (ArrayList) hm.get("itemvalues");//选中的人员库指标集
        ArrayList resumefldids = (ArrayList) hm.get("resumefldids");//简历指标Id
        ThirdPartyResumeBase base = ThirdPartyResumeSourceFactory.getThirdPartyResumeBo(this.frameconn, thirdpartName, this.userView);
        HashMap<String, ArrayList<LazyDynaBean>> hashmap = new HashMap<String, ArrayList<LazyDynaBean>>();
        ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
        for (int i = 0; i < itemvalues.size(); i++) {
            LazyDynaBean contentBean = new LazyDynaBean();
            String item = (String) itemvalues.get(i);
            item = item == null || "#".equals(item)? "" : item;
            contentBean.set("ehrfld", item);
            String name = (String) baseitems.get(i);
            name = name == null ? "" : name;
            String resumefldid = (String) resumefldids.get(i);
            resumefldid = resumefldid == null ? "" : resumefldid;
            contentBean.set("resumefld", name);
            contentBean.set("resumefldid", resumefldid);
            list.add(contentBean);
        }
        hashmap.put(resumeset, list);
        base.saveResumefielditemsParam(hashmap);

    }
}
