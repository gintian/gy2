package com.hjsj.hrms.module.recruitment.thirdpartyresume.dayee;

import com.hjsj.hrms.module.recruitment.thirdpartyresume.base.ThirdPartyResumeBase;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * 大易云招聘简历信息导入业务类
 * @Title:        DaYeeResumeBo.java
 * @Description:  导入大易简历并按定义方案中保存的设置解析导入简历信息
 * @Company:      hjsj     
 * @Create time:  2017-9-5 下午03:26:53
 * @author        chenxg
 * @version       1.0
 */
public class DaYeeResumeBo extends ThirdPartyResumeBase {
    public DaYeeResumeBo() {
    }

    private String msg;
    private HashMap<String, ArrayList<LazyDynaBean>> codeitems = new HashMap<String, ArrayList<LazyDynaBean>>();
    
    private Category cat = Category.getInstance(this.getClass());
    /**
     * 向指定wsdl发送请求
     * 
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public String getResumes(String dyUrl, String dyParam) {
        String result = "";
        try {
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(dyUrl);
            // WSDL里面描述的接口名称
            call.setOperationName("readEntryInformation");
            // 接口的参数
            call.addParameter("conditionXml", org.apache.axis.encoding.XMLType.XSD_STRING,
                    javax.xml.rpc.ParameterMode.IN);
            // 设置返回类型
            call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);
            // 给方法传递参数，并且调用方法
            result = (String) call.invoke(new Object[] { dyParam });
        } catch (Exception e) {
            System.out.println("获取简历出现异常！" + e);
            e.printStackTrace();
        }
        return result;
    }

    public String setResumesStatus(String dyUrl, String dyParam) {
        String result = "";
        try {
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(dyUrl);
            // WSDL里面描述的接口名称
            call.setOperationName("callbackToChangeStatus");
            // 接口的参数
            call.addParameter("conditionXml", org.apache.axis.encoding.XMLType.XSD_STRING,
                    javax.xml.rpc.ParameterMode.IN);
            // 设置返回类型
            call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);
            // 给方法传递参数，并且调用方法
            result = (String) call.invoke(new Object[] { dyParam });
        } catch (Exception e) {
            System.out.println("回调简历读取状态出现异常！" + e);
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public ArrayList getResumeFromThirdParty(HashMap params) {
        try {
            ArrayList<String> msgList = new ArrayList<String>();
            String blacklist_field = (String) params.get("blacklist_field");

            HashMap mapReseme = getResumeParam();
            this.codeitems = (HashMap<String, ArrayList<LazyDynaBean>>) mapReseme.get("codeitems");
            LazyDynaBean paramBean = (LazyDynaBean) mapReseme.get("thirdPartyParm");
            String apiurl = (String) paramBean.get("apiUrl");
            String corpcode = (String) paramBean.get("corpCode");
            String userName = (String) paramBean.get("userName");
            String passWord = (String) paramBean.get("passWord");
            	           
            if(StringUtils.isEmpty(apiurl)) {
                msgList.add("apiurl");
                return msgList;
            }
            
            if(StringUtils.isEmpty(corpcode)) {
                msgList.add("corpcode");
                return msgList;
            }
            
            if(StringUtils.isEmpty(userName)) {
                msgList.add("userName");
                return msgList;
            }
            
            if(StringUtils.isEmpty(passWord)) {
                msgList.add("passWord");
                return msgList;
            }
            
            if(apiurl.toLowerCase().endsWith("?wsdl"))
                apiurl = apiurl.substring(0, apiurl.toLowerCase().indexOf("?wsdl"));

            LazyDynaBean ThirdParamBean = (LazyDynaBean) mapReseme.get("thirdPartyParm");
            String mainItem = (String) ThirdParamBean.get("identifyfld");
            String secItem = (String) ThirdParamBean.get("sencondfld");
            ArrayList<LazyDynaBean> fieldsetList = (ArrayList<LazyDynaBean>) mapReseme.get("fieldset");
            HashMap<String, ArrayList<LazyDynaBean>> resumeXmlItem = (HashMap<String, ArrayList<LazyDynaBean>>) mapReseme.get("fielditem");

            StringBuffer param = new StringBuffer();
            param.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            param.append("<Condition>");
            param.append("<corpCode><![CDATA[" + corpcode + "]]></corpCode>");
            param.append("<userName><![CDATA[" + userName + "]]></userName>");
            param.append("<password><![CDATA[" + passWord + "]]></password>");
            param.append("<rowSize><![CDATA[10]]></rowSize>");
            param.append("<currentPage><![CDATA[1]]></currentPage>");
            //应聘简历状态：19：offer
            param.append("<applyStatus><![CDATA[19]]></applyStatus>");
            //offer状态：=7：已接收
            param.append("<offerStatus><![CDATA[7]]></offerStatus>");
            //=2：申请表；=3：offer；=4：面试评价
            param.append("<cType><![CDATA[2,3,4]]></cType>");
            param.append("</Condition>");
            
            this.cat.debug("接口参数：" + param.toString());
            HashMap<String, String> logMap = new HashMap<String, String>();
            boolean flag = true;
            int importSum = 0;
            while (flag) {
                String applyIds = "";
                this.cat.debug("开始导入简历…………");
                String resume = getResumes(apiurl, param.toString());
                this.cat.debug("简历信息：" + resume);
                if(!resume.startsWith("<?xml")) {
                    RecordError(resume, "1");
                    break;
                }
                    
                Document doc = PubFunc.generateDom(resume);
                
                XPath xPath = XPath.newInstance("/ResumeList");
                Element ResumeList = (Element) xPath.selectSingleNode(doc);
                List resumes = ResumeList.getChildren("Resume");
                
                if(resumes == null || resumes.size() < 1)
                    flag = false;
                else {
                	this.cat.debug("开始解析简历…………");
                    for (int i = 0; i < resumes.size(); i++) {
                        Element personResume = (Element) resumes.get(i);
                        String applyId = personResume.getAttributeValue("applyId");
                        importResunme(personResume, mainItem, secItem, fieldsetList, blacklist_field, resumeXmlItem);
                        
                        if(isImportFlag()) {
                            importSum++;
                            applyIds += applyId + ",";
                        }
                    }
                    this.cat.debug("解析简历结束…………");
                    this.cat.debug("解析简历的人员applyIds：" + applyIds);
                    if(StringUtils.isNotEmpty(applyIds)){
                        if(applyIds.endsWith(","))
                            applyIds = applyIds.substring(0, applyIds.length() - 1);
                        
                        StringBuffer stateParam = new StringBuffer();
                        stateParam.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                        stateParam.append("<Condition>");
                        stateParam.append("<corpCode><![CDATA[" + corpcode + "]]></corpCode>");
                        stateParam.append("<userName><![CDATA[" + userName + "]]></userName>");
                        stateParam.append("<password><![CDATA[" + passWord + "]]></password>");
                        stateParam.append("<applyIds><![CDATA[" + applyIds + "]]></applyIds>");
                        stateParam.append("</Condition>");
                        
                        String result = setResumesStatus(apiurl, stateParam.toString());
                        if(!"00".equals(result))
                            RecordError(result, "2");
                    }
                }
            }

            logMap.put("Num", importSum + "");

            WriteImportDetail(logMap);

        } catch (Exception e) {
            e.printStackTrace();
        }
        this.cat.debug("导入简历结束…………");
        return null;
    }

    @Override
    protected String getThirdPartyName() {
        return "DaYee";
    }

    /**
     * 解析简历并导入人员信息
     * 
     * @param Applicant
     *            人员简历
     * @param mainItem
     *            关键指标
     * @param secItem
     *            次关键指标
     * @param fieldsetList
     *            信息集对应
     * @param blacklist_field
     *            黑名单指标
     * @param resumeXmlItem
     *            指标对应
     */
    public void importResunme(Element Applicant, String mainItem, String secItem,
            ArrayList<LazyDynaBean> fieldsetList, String blacklist_field,
            HashMap<String, ArrayList<LazyDynaBean>> resumeXmlItem) {
        String blacklist_value = "";
        String personName = "";
        String mainItemValue = "";
        String secItemValue = "";
        ArrayList<LazyDynaBean> resumeInfoList = new ArrayList<LazyDynaBean>();
        try {

            Element resumese = Applicant.getChild("ResumeContent");
            for (int i = 0; i < fieldsetList.size(); i++) {
                LazyDynaBean bean = (LazyDynaBean) fieldsetList.get(i);
                String resumeSet = (String) bean.get("resumeset");
                String resumesetId = (String) bean.get("resumesetId");
                String ehrset = (String) bean.get("ehrset");
                if (StringUtils.isEmpty(ehrset))
                    continue;

                ArrayList<LazyDynaBean> itemXmlList = resumeXmlItem.get(resumeSet);

                List resumeseSets = resumese.getChildren(resumesetId);
                if (resumese == null)
                    continue;

                for (int m = 0; m < resumeseSets.size(); m++) {
                    Element Items = (Element) resumeseSets.get(m);
                    if (Items == null)
                        continue;

                    for (int n = 0; n < itemXmlList.size(); n++) {
                        LazyDynaBean itembean = (LazyDynaBean) itemXmlList.get(n);
                        String resumefld = (String) itembean.get("resumefld");
                        String ehrfld = (String) itembean.get("ehrfld");
                        String resumefldid = (String) itembean.get("resumefldid");
                        if (StringUtils.isEmpty(ehrfld))
                            continue;

                        FieldItem fi = DataDictionary.getFieldItem(ehrfld);
                        String value = Items.getChildText(resumefldid);

                        if ("A01".equalsIgnoreCase(ehrset)) {
                            if ("A0101".equalsIgnoreCase(ehrfld))
                                personName = value;

                            if (ehrfld.equalsIgnoreCase(mainItem))
                                mainItemValue = value;

                            if (ehrfld.equalsIgnoreCase(secItem))
                                secItemValue = value;

                        }

                        if ("D".equalsIgnoreCase(fi.getItemtype()) && StringUtils.isNotEmpty(value)
                                && !"至今".equalsIgnoreCase(value)) {
                            if (value.length() > 3) {
                                String year = value.substring(0, 4);
                                if (("1753".compareToIgnoreCase(year)) > 0)
                                    value = "";
                            } else
                                value = "";
                        }

                        if (StringUtils.isEmpty(value))
                            value = "";

                        value = getTextByHtml(value);
                        value = PubFunc.hireKeyWord_filter(value);
                        LazyDynaBean ResumeBean = new LazyDynaBean();
                        ResumeBean.set("itemtype", fi.getItemtype());
                        ResumeBean.set("itemlength", fi.getItemlength());
                        ResumeBean.set("ehrfld", ehrfld);
                        ResumeBean.set("resumefld", resumefld);
                        ResumeBean.set("resumeset", resumeSet);
                        ResumeBean.set("itemformat", fi.getFormat());
                        ResumeBean.set("setid", fi.getFieldsetid());
                        if (!"A01".equalsIgnoreCase(ehrset))
                            ResumeBean.set("i9999", m + "");

                        ResumeBean.set("value", value);// bean中获得的itemid就是resumeFLDXML中定义的itemid,而hm中存放的是才智创新返回来的值
                        // 若指标等于黑名单指标
                        if (ehrfld != null && ehrfld.equalsIgnoreCase(blacklist_field))
                            blacklist_value = value;

                        resumeInfoList.add(ResumeBean);
                    }
                }
            }

            HashMap resumeMap = new HashMap();
            resumeMap.put("personID", personName);
            resumeMap.put("mainItemValue", mainItemValue);
            resumeMap.put("secItemValue", secItemValue);
            resumeMap.put("blacklist_value", blacklist_value);
            resumeMap.put("ResumeInfoList", resumeInfoList);
            addResunme(resumeMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    private void RecordError(String errorCode, String flag){
        try {
            if("1".equals(flag))
                this.msg = "导入人员简历失败!";
            else
                this.msg = "回调简历读取状态失败!";
            
            if("00".equalsIgnoreCase(errorCode))
                this.msg = "导入人员简历成功!";
            else if("01".equalsIgnoreCase(errorCode))
                this.msg += "<br>&nbsp;&nbsp;&nbsp;&nbsp;错误描述：重复操作！";
            else if("02".equalsIgnoreCase(errorCode))
                this.msg += "<br>&nbsp;&nbsp;&nbsp;&nbsp;错误描述：无对应Id或相关信息！";
            else if("03".equalsIgnoreCase(errorCode))
                this.msg += "<br>&nbsp;&nbsp;&nbsp;&nbsp;错误描述：传入的ID为空！";
            else if("04".equalsIgnoreCase(errorCode))
                this.msg += "<br>&nbsp;&nbsp;&nbsp;&nbsp;错误描述：参数不对或必填出现空值！";
            else if("05".equalsIgnoreCase(errorCode))
                this.msg += "<br>&nbsp;&nbsp;&nbsp;&nbsp;错误描述：招聘系统内部出现异常！";
            else if("06".equalsIgnoreCase(errorCode))
                this.msg += "<br>&nbsp;&nbsp;&nbsp;&nbsp;错误描述：用户名或密码错误！";
            else if("07".equalsIgnoreCase(errorCode))
                this.msg += "<br>&nbsp;&nbsp;&nbsp;&nbsp;错误描述：企业不存在或已失效！";
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    /**
     * 返回错误信息
     */
    @Override
    public String getMsg() {
        return msg;
    }
}
