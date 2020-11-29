package com.hjsj.hrms.module.recruitment.thirdpartyresume.transaction;

import com.hjsj.hrms.module.recruitment.thirdpartyresume.base.ThirdPartyResumeBase;
import com.hjsj.hrms.module.recruitment.thirdpartyresume.base.ThirdPartyResumeSourceFactory;
import com.hjsj.hrms.module.recruitment.thirdpartyresume.beisen.EmployResumeBo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * 类名称：DefineResumeImportSchemeTrans 类描述：定义简历解析导入方案 Company:HJSJ 创建人：zhaocq
 * 创建时间：2016-6-8
 * 
 * @version
 * 
 */
public class DefineResumeImportSchemeTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        // TODO Auto-generated method stub
        HashMap hm = this.getFormHM();
        String thirdpartName = (String) hm.get("name");
        ThirdPartyResumeBase base = ThirdPartyResumeSourceFactory.getThirdPartyResumeBo(this.frameconn, thirdpartName, this.userView);
        ArrayList contentList = new ArrayList();
        EmployResumeBo bo = new EmployResumeBo(this.getFrameconn());
        // 标识指标和次关键指标
        ArrayList itemIDList = new ArrayList();
        // 更新方式
        ArrayList modeList = new ArrayList();
        // 人员库指标集
        ArrayList flist = new ArrayList(); 
        // 人员库指标集
        ArrayList userList = new ArrayList(); 
        //同步标识指标（关联45号代码类指标）
        ArrayList synchronousList = new ArrayList(); 
        ArrayList fieldSetList = new ArrayList();
        String state = null; // 选择的人员指标集
        int resumeSize;
        StringBuffer dataStore = new StringBuffer();
        try {
            HashMap schemeParamMap = base.getResumeParam();
            ArrayList<LazyDynaBean> resumeList = (ArrayList<LazyDynaBean>) schemeParamMap.get("fieldset");
            ArrayList fieldList = (ArrayList) schemeParamMap.get("fieldset");
            ArrayList dataList = new ArrayList();
            ArrayList nameList = new ArrayList();
            LazyDynaBean paramBean = (LazyDynaBean) schemeParamMap.get("thirdPartyParm");
            fieldSetList = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.EMPLOY_FIELD_SET);// 所有已构库人员库子集
            String fieldset = "";
            // /flist.add(new CommonData("", "请选择..."));
            dataStore.append("Ext.create('Ext.data.Store', { fields: ['id', 'name'],");
            dataStore.append("data : [{'id':'#','name':'请选择'},");
            for (int i = 0; i < fieldSetList.size(); i++) {
                FieldSet set = (FieldSet) fieldSetList.get(i);
                HashMap<String, String> hashMap = new HashMap<String, String>();
                dataStore.append("{'id':'" + set.getFieldsetid() + "', 'name':'" + set.getFieldsetid() + ":" + set.getFieldsetdesc() + "'},");
                fieldset = set.getFieldsetid();
                hashMap.put("id", set.getFieldsetid());
                hashMap.put("name", set.getFieldsetdesc());
                flist.add(hashMap);
            }
            
            if (dataStore.toString().endsWith(","))
                dataStore.setLength(dataStore.length() - 1);
            
            dataStore.append("]})");
            StringBuffer jsonInfo = new StringBuffer("[");
            for (int i = 0; i < resumeList.size(); i++) {
                LazyDynaBean bean = (LazyDynaBean) resumeList.get(i);
                if (bean.get("resumeset") != null) {
                    String resumeset = (String) bean.get("resumeset");
                    String resumesetId = (String) bean.get("resumesetId");
                    String fselected = (String) bean.get("ehrset");
                    String ehrset = (String) bean.get("ehrset");
                    fselected = fselected == null ? "" : fselected;
                    resumeset = resumeset == null ? "" : resumeset;
                    resumesetId = resumesetId == null ? "" : resumesetId;
                    LazyDynaBean contentBean = new LazyDynaBean();
                    contentBean.set("resumeset", resumeset);
                    contentBean.set("fselected", fselected); // 选中
                    contentBean.set("ehrset", ehrset);
                    contentBean.set("resumesetid", resumesetId);
                    contentList.add(contentBean);
                    if (StringUtils.isNotEmpty(ehrset)) {
                        FieldSet fieldSet = DataDictionary.getFieldSetVo(ehrset);
                        if (fieldSet != null) {
                            String ehrsetName = fieldSet.getFieldsetdesc();
                            ehrset = ehrset + "`" + ehrset + ":" + ehrsetName;
                        }
                    }
                    
                    jsonInfo.append("{resumeItem:'" + resumeset + "',userItem:'" + ehrset + "',codeIndex:''},");
                }
            }
            
            if (jsonInfo.toString().endsWith(","))
                jsonInfo.setLength(jsonInfo.length() - 1);

            jsonInfo.append("]");
            hm.put("resumeList", resumeList);
            hm.put("rzValue", jsonInfo.toString());
            StringBuffer rzColumn = new StringBuffer("[");
            rzColumn.append("{text:'简历指标集',width:440,locked:false,align:'center',dataIndex:'resumeItem'},");
            rzColumn.append("{text:'人员库指标集',width:220,align:'center',dataIndex:'userItem', renderer :defineResumeScheme.changeCombo,"
                     + "editor:new  EHR.extWidget.field.CodeSelectField({id : 'combo',autoLoad : true,triggerAction: 'all',"
                     + "mode: 'local',store: " + dataStore + ","
                     + "displayField: 'name',valueField: 'id'})},");
            rzColumn.append("{text:'指标对应',width:220,align:'center',dataIndex:'codeIndex',xtype: "
                    + "'actioncolumn',icon:'/images/edit.gif',iconCls:'bik',handler:defineResumeScheme.thirdPartyRensumeManagePage}");
            rzColumn.append("]");
            itemIDList = bo.getitemIDList();
            modeList = bo.getModeList();
            userList = bo.getuserList();
            hm.put("itemID", paramBean.get("identifyfld"));
            hm.put("dbname", paramBean.get("dbname"));
            hm.put("secitemID", paramBean.get("sencondfld"));
            hm.put("mode", paramBean.get("imptype"));
            hm.put("rzColumn", rzColumn.toString());
            hm.put("synchronousFlag", paramBean.get("synchronousFlag"));
            
            synchronousList = bo.getSynchronousList();
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        
        hm.put("itemIDList", itemIDList);
        hm.put("modelist", modeList);
        hm.put("flist", flist);
        hm.put("codelist", contentList);
        hm.put("userList", userList);
        hm.put("synchronousList", synchronousList);
        hm.put("name", thirdpartName);

    }

}
