package com.hjsj.hrms.module.recruitment.thirdpartyresume.transaction;

import com.hjsj.hrms.module.recruitment.thirdpartyresume.base.ThirdPartyResumeBase;
import com.hjsj.hrms.module.recruitment.thirdpartyresume.base.ThirdPartyResumeSourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 第三方简历导入-代码对应页面
 * 
 * @ClassName: CodeCorrespondTrans
 * @Description: TODO代码对应
 * @author zhangcq
 * @date 2016-6-8
 * 
 */
public class CodeCorrespondTrans extends IBusiness {

    private String resumeitemDesc = "";
    @Override
    public void execute() throws GeneralException {
        HashMap hm = (HashMap) this.getFormHM();
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.frameconn);

        LazyDynaBean bean = new LazyDynaBean();

        ArrayList codeList = new ArrayList();
        ArrayList resumeList = new ArrayList();

        String resumeID = (String) hm.get("resumeID");
        String from_flag = (String) hm.get("from_flag");
        String thirdName = (String) hm.get("name");
        String commonvalue = "";
        resumeID = SafeCode.decode(resumeID);
        String itemCom = "";
        try {
            ThirdPartyResumeBase base = ThirdPartyResumeSourceFactory.getThirdPartyResumeBo(this.frameconn, thirdName, this.userView);
            HashMap param = base.getResumeParam();
            ArrayList<LazyDynaBean> resumeXmlCodesets = (ArrayList<LazyDynaBean>) param.get("codesets");
            HashMap<String, ArrayList<LazyDynaBean>> resumeXmlItem = (HashMap<String, ArrayList<LazyDynaBean>>) param.get("codeitems");
            HashMap<String, LazyDynaBean> resumeCommonValueMap = (HashMap<String, LazyDynaBean>) param.get("codeCommonValue");
            HashMap<String, ArrayList<HashMap>> resumecodeitems = (HashMap<String, ArrayList<HashMap>>) param.get("resumecodeitems");
            StringBuffer jsonInfo = new StringBuffer("[");
            StringBuffer data = new StringBuffer();
            if (resumeXmlCodesets != null && resumeXmlCodesets.size() > 0) {
                for (int i = 0; i < resumeXmlCodesets.size(); i++) {
                    HashMap codeMap = new HashMap();
                    LazyDynaBean codeBean = (LazyDynaBean) resumeXmlCodesets.get(i);
                    String ehrfld = (String) codeBean.get("ehrcodeset");
                    String resumeset = (String) codeBean.get("resumecodeset");
                    String itemdesc = null;

                    rs = dao.search("select itemdesc from fielditem where itemid ='" + ehrfld.toUpperCase() + "'");
                    if (rs.next())
                        itemdesc = rs.getString("itemdesc");

                    itemdesc = itemdesc == null || "".equals(itemdesc) ? "" : itemdesc;
                    ehrfld = ehrfld == null || "".equals(ehrfld) ? "" : ehrfld;
                    resumeset = resumeset == null || "".equals(resumeset) ? "" : resumeset;

                    codeMap.put("id", ehrfld.toUpperCase());
                    codeMap.put("name", resumeset + " -> " + ehrfld.toUpperCase() + ":" + itemdesc);
                    codeList.add(codeMap);// 选择代码项
                    if (resumeID == null || "".equals(resumeID))
                        resumeID = ehrfld;

                    // 获得代码项
                    if (ehrfld.equals(resumeID)) {
                        itemCom = resumeset + " -> " + ehrfld.toUpperCase() + ":" + itemdesc;
                        String codesetid = null;
                        this.frowset = dao .search("select codesetid from FIELDITEM where UPPER(ITEMID)='"
                                + ehrfld.toUpperCase() + "' and UPPER(CODESETID)<>'0'");
                        if (frowset.next())
                            codesetid = frowset.getString("codesetid");
                        
                        // 取得commonvalue
                        bean = resumeCommonValueMap.get(ehrfld);
                        if (bean.get("commonvalue") != null)
                            commonvalue = (String) bean.get("commonvalue");
                        
                        ArrayList<HashMap> resumecodeitemList = new ArrayList<HashMap>();
                        String resumecodesetid = (String) codeBean.get("resumecodesetid");
                        if(StringUtils.isNotEmpty(resumecodesetid) && !"0".equalsIgnoreCase(resumecodesetid)) {
                            resumecodeitemList = resumecodeitems.get(resumecodesetid);
                            data.append("Ext.create('Ext.data.Store', { fields: ['id', 'name'],");
                            data.append("data : [{'id':'#','name':'请选择'},");
                            data.append(getResumeCodeitems(resumecodeitemList, ""));
                            data.append("]})");
                        }
                        
                        ArrayList<LazyDynaBean> codeitemBean = resumeXmlItem.get(ehrfld);
                        
                        for (int j = 0; j < codeitemBean.size(); j++) {
                            HashMap resumemap = new HashMap();
                            LazyDynaBean itemBean = codeitemBean.get(j);
                            String ehritemid = (String) itemBean.get("ehritemid"); // 人员库代码编号
                            String resumeitemid = null;
                            if (itemBean.get("resumeitemid") != null)
                                resumeitemid = (String) itemBean.get("resumeitemid"); // 简历信息
                            else
                                resumeitemid = ""; // 简历信息
                            
                            if(StringUtils.isNotEmpty(resumeitemid) && "DaYee".equals(thirdName)) {
                                this.resumeitemDesc = "";
                                getResumeCodeitems(resumecodeitemList, resumeitemid);
                                if(StringUtils.isNotEmpty(this.resumeitemDesc))
                                    resumeitemid = resumeitemid + "`" + this.resumeitemDesc;
                            }
                                
                            String itemname = null; // 人员库代码名称
                            itemname = AdminCode.getCodeName(codesetid, ehritemid);
                            jsonInfo.append("{resumeInfo:'" + resumeitemid + "',userItemId:'" + ehritemid + "',userItemName:'" + itemname + "'},");
                            resumemap.put("resumeID", resumeID);
                            resumemap.put("itemname", itemname);
                            resumemap.put("resumeitemid", resumeitemid);
                            resumemap.put("ehritemid", ehritemid);
                            resumeList.add(resumemap);
                            
                        }
                    }
                }
            }
            
            if (jsonInfo.toString().endsWith(","))
                jsonInfo.setLength(jsonInfo.length() - 1);
            
            jsonInfo.append("]");
            hm.put("rzValue", jsonInfo.toString());
            
            StringBuffer rzColumn = new StringBuffer("[");
            rzColumn.append("{text:'简历信息',width:200,locked:false,align:'center',dataIndex:'resumeInfo',");
            String editor = "{xtype: 'textfield',id: 'resumeInfoText',enableKeyEvents: true}";
            if(resumecodeitems != null) {
                editor = "new  EHR.extWidget.field.CodeSelectField({id : 'combo',autoLoad : true,triggerAction: 'all',"
                    + "mode: 'local',store: " + data + ","
                    + "displayField: 'name',valueField: 'id'" + "})";
                rzColumn.append("renderer :thirdPartyRensumeCodeItem.changeCombo,");
                rzColumn.append("editor: " + editor + "},");
                // editor = "new Ext.tree.Panel({store: " + data +  ",rootVisible: false})";
                
            } else
                rzColumn.append("editor: " + editor + "},");
                
            rzColumn.append("{text:'人员库代码编号',width:200,align:'center',dataIndex:'userItemId',},");
            rzColumn.append("{text:'人员库代码名称',width:200,align:'center',dataIndex:'userItemName'}");
            rzColumn.append("]");
            hm.put("rzColumn", rzColumn.toString());
            hm.put("commonvalue", commonvalue);
            hm.put("resumeID", resumeID);
            hm.put("clist", codeList);
            hm.put("resumeList", resumeList);
            hm.put("from_flag", from_flag);
            hm.put("itemCom", itemCom);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    
    private String getResumeCodeitems(ArrayList<HashMap> resumecodeitemList, String resumeCodeItem) {
        StringBuffer data = new StringBuffer();
        if(resumecodeitemList == null || resumecodeitemList.size() <0)
        	return data.toString();
        
        boolean leaf = true;
        for(int i = 0; i < resumecodeitemList.size(); i++) {
            HashMap resumecodeitemMap = resumecodeitemList.get(i);
            if(resumecodeitemMap.containsKey("children"))
                leaf = false;
            
            Iterator iter = resumecodeitemMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String) entry.getKey();
                if("children".equalsIgnoreCase(key)){
                    ArrayList<HashMap> childList = (ArrayList<HashMap>) entry.getValue();
                    data.append(getResumeCodeitems(childList,resumeCodeItem) + "," );
//                    data.append("children:[" + getResumeCodeitems(childList) + "]");
                } else {
                    String value = (String) entry.getValue();
                    data.append("{'id':'" + key + "', 'name':'" + key + ":" + value + "'},");
                    if(key.equalsIgnoreCase(resumeCodeItem))
                        this.resumeitemDesc = key + ":" + value;
//                    data.append("{id: '" + key + "',");
//                    data.append("text: '" + value +"',");
//                    data.append("leaf: " + leaf +"},");
                }
                
                if(StringUtils.isNotEmpty(this.resumeitemDesc))
                    break;
            }
        }
        
        if(data.toString().endsWith(","))
            data.setLength(data.length() - 1);
        
        return data.toString();
    }

}
