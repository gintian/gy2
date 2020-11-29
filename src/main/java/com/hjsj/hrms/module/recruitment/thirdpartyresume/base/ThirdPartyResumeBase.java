package com.hjsj.hrms.module.recruitment.thirdpartyresume.base;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.recruitment.thirdpartyresume.beisen.EmployResumeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.htmlparser.Parser;
import org.htmlparser.visitors.TextExtractingVisitor;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 第三方简历导入基类
 * <p>
 * Title: ThirdPartyResumeBase
 * </p>
 * <p>
 * Description: 第三方简历导入基类
 * </p>
 * <p>
 * Company: hjsj
 * </p>
 * <p>
 * create time: 2016-5-25 下午01:51:08
 * </p>
 * 
 * @author zhaoxj
 * @version 1.0
 */
public abstract class ThirdPartyResumeBase {
    public Connection conn;
    private UserView userView;
    private Document doc;
    private String xml;
    // 黑名单log
    private String blacklistLog = ""; 
    // 人员库log
    private String PlistLog = ""; 
    // 代码对应log
    private String ClistLog = ""; 
    // 解析不正确的文件(文件解析错误,或标识指标,次关键指标未解析)
    private String FlistLog = ""; 
    // 导入简历提示
    private String showInforLog = ""; 
    //成功导入人员数
    private int importNum = 0;
    // 简历将要导入的目标人员库
    protected String destNbase;
    
    private HashMap param = null;
    
    private String guidkey = "";
    //是否导入成功
    private boolean importFlag = true;
    //导入的子集
    private String fieldSet = "";
    //导入子集的第几条记录
    private String fieldSetI9999 = "";

    private Category cat = Category.getInstance(this.getClass());

    protected ThirdPartyResumeBase() {

    }

    public ThirdPartyResumeBase(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
    }

    /**
     * 读取并解析XXX_RESUME_PARAM参数
     * 
     * @Title: getResumeParam
     * @Description:
     * @return
     */
    public HashMap getResumeParam() {
        try {
            if (param != null)
                return param;
            
            param = new HashMap();
            UpdateImportScheme();
            
            if (StringUtils.isEmpty(xml))
                xml = loadResumeParamFromDB();
            
            if (StringUtils.isEmpty(xml)){
                if (StringUtils.isEmpty(xml))
                    return param;
            } else {
                doc = PubFunc.generateDom(xml);
            }
            
            //查询第三方参数
            XPath xPath = XPath.newInstance("/scheme");
            Element schemes = (Element) xPath.selectSingleNode(this.doc);
            List childList = schemes.getChildren();
            getThirdPartyParm(childList, param);
            //查询指标集及指标对应
            xPath = XPath.newInstance("/scheme/sets");
            Element sets = (Element) xPath.selectSingleNode(this.doc);
            if(sets != null) {
                List list = (List) sets.getChildren();
                getResumeXmlList(list, param);
            }
            //查询代码对应
            xPath = XPath.newInstance("/scheme/codesets");
            Element codes = (Element) xPath.selectSingleNode(this.doc);
            if(codes != null) {
                List list = (List) codes.getChildren();
                getResumeCodesList(list, param);
            }

            param.put("resumecodeitems", getResumecodeitem());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return param;
    }

    private void getThirdPartyParm(List list, HashMap param) {
        LazyDynaBean bean = new LazyDynaBean();
        for (int i = 0; i < list.size(); i++) {
            Element ele = (Element) list.get(i);
            String key = ele.getName();
            if ("sets".equalsIgnoreCase(key) || "codesets".equalsIgnoreCase(key))
                continue;

            String value = ele.getText();
            bean.set(key, value);
        }

        param.put("thirdPartyParm", bean);
    }

    /**
     * 得到数据库中xml的指标集
     * 
     * @param list
     *            xml中的节点
     * @param param
     *            保存的xml参数的map
     * @throws GeneralException
     */
    private void getResumeXmlList(List list, HashMap param) throws GeneralException {
        ArrayList<LazyDynaBean> resumefieldsetList = new ArrayList<LazyDynaBean>();
        HashMap<String, ArrayList<LazyDynaBean>> resumefieldMap = new HashMap<String, ArrayList<LazyDynaBean>>();
        try {
            for (int i = 0; i < list.size(); i++) {
                Element element = (Element) list.get(i);
                LazyDynaBean bean = new LazyDynaBean();
                String resumeset = element.getAttributeValue("resumeset");
                bean.set("resumeset", resumeset);
                if (element.getAttribute("ehrset") != null)
                    bean.set("ehrset", element.getAttributeValue("ehrset"));
                else
                    bean.set("ehrset", "");

                if (element.getAttribute("resumesetId") != null)
                    bean.set("resumesetId", element.getAttributeValue("resumesetId"));
                else
                    bean.set("resumesetId", "");
                
                if (element.getAttribute("resumecodesetid") != null)
                    bean.set("resumecodesetid", element.getAttributeValue("resumecodesetid"));
                else
                    bean.set("resumecodesetid", "0");

                resumefieldsetList.add(bean);
                resumefieldMap.put(resumeset, getSchemeitemXmlList(resumeset));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        param.put("fieldset", resumefieldsetList);
        param.put("fielditem", resumefieldMap);
    }

    /**
     * 得到数据库中xml的menu（指标）
     * 
     * @param resumeset
     *            指标集的名称
     * @return
     * @throws GeneralException
     */
    private ArrayList<LazyDynaBean> getSchemeitemXmlList(String resumeset) throws GeneralException {
        ArrayList<LazyDynaBean> schemeParameterList = new ArrayList<LazyDynaBean>();
        try {
            XPath xPath = XPath.newInstance("/scheme/sets/set[@resumeset='" + resumeset + "']/menus");
            Element menus = (Element) xPath.selectSingleNode(this.doc);
            List list = (List) menus.getChildren();
            for (int i = 0; i < list.size(); i++) {
                Element element = (Element) list.get(i);
                LazyDynaBean bean = new LazyDynaBean();
                bean.set("resumefld", element.getAttributeValue("resumefld"));
                if (element.getAttribute("ehrfld") != null)
                    bean.set("ehrfld", element.getAttributeValue("ehrfld"));
                else
                    bean.set("ehrfld", "");
                
                if (element.getAttribute("resumefldid") != null)
                    bean.set("resumefldid", element.getAttributeValue("resumefldid"));
                else
                    bean.set("resumefldid", "");
                
                if (element.getAttribute("resumecodesetid") != null)
                    bean.set("resumecodesetid", element.getAttributeValue("resumecodesetid"));
                else
                    bean.set("resumecodesetid", "0");
                
                schemeParameterList.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return schemeParameterList;
    }
    
    /**
     * 得到数据库中xml的代码项
     * 
     * @param list
     *            xml中的节点
     * @param param
     *            保存的xml参数的map
     * @throws GeneralException
     */
    private void getResumeCodesList(List list, HashMap param) throws GeneralException {
        ArrayList<LazyDynaBean> resumecodesetList = new ArrayList<LazyDynaBean>();
        HashMap<String, ArrayList<LazyDynaBean>> resumeitemsMap = new HashMap<String, ArrayList<LazyDynaBean>>();
        HashMap<String, LazyDynaBean> resumeCommonValueMap = new HashMap<String, LazyDynaBean>();
        try {
            for (int i = 0; i < list.size(); i++) {
                Element element = (Element) list.get(i);
                LazyDynaBean bean = new LazyDynaBean();
                String ehrcodeset = element.getAttributeValue("ehrcodeset");
                bean.set("ehrcodeset", ehrcodeset);
                if (element.getAttribute("resumecodeset") != null)
                    bean.set("resumecodeset", element.getAttributeValue("resumecodeset"));
                else
                    bean.set("resumecodeset", "");
                
                if (element.getAttribute("resumeset") != null)
                    bean.set("resumeset", element.getAttributeValue("resumeset"));
                else
                    bean.set("resumeset", "");
                
                if (element.getAttribute("resumefldid") != null)
                    bean.set("resumefldid", element.getAttributeValue("resumefldid"));
                else
                    bean.set("resumefldid", "");

                if (element.getAttribute("resumecodesetid") != null)
                    bean.set("resumecodesetid", element.getAttributeValue("resumecodesetid"));
                else
                    bean.set("resumecodesetid", "0");
                
                resumecodesetList.add(bean);
                resumeitemsMap.put(ehrcodeset, getSchemecodeitemsXmlList(ehrcodeset));
                String resumeset = element.getAttributeValue("resumeset");
                String resumecodeset = element.getAttributeValue("resumecodeset");
                resumeCommonValueMap.put(ehrcodeset, getCommonvalueXmlList(resumeset, resumecodeset));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        param.put("codesets", resumecodesetList);
        param.put("codeitems", resumeitemsMap);
        param.put("codeCommonValue", resumeCommonValueMap);
    }
    
    /**
     * 检测数据库中的简历指标集列表与xml中的是否一致
     * 如不一致则删除数据库中的数据重建
     */
    private void UpdateImportScheme(){
        List list = null;
        try{
            ArrayList<String> schemeXmlList = getResumeFld();
            ArrayList<String> schemeXmlList1 = (ArrayList<String>) schemeXmlList.clone();

            if (StringUtils.isEmpty(xml))
                xml = loadResumeParamFromDB();
            
            if (StringUtils.isNotEmpty(xml)) {
                doc = PubFunc.generateDom(xml);
                XPath xPath = XPath.newInstance("/scheme/sets");
                Element sets = (Element) xPath.selectSingleNode(this.doc);
                if(sets != null)
                    list = (List) sets.getChildren();
                // 数据库中的xml和resumeFld.xml中的set节点比较
                if(list != null) {
                    for (int i = 0; i < list.size(); i++) {
                        Element element = (Element) list.get(i);
                        String set = element.getAttributeValue("resumeset");
                        String setid = element.getAttributeValue("resumesetId");
                        schemeXmlList.remove(set + "=" + setid);
                    }
                
                    for (int i = 0; i < schemeXmlList1.size(); i++) {
    
                        for (int j = 0; j < list.size(); j++) {
                            Element element = (Element) list.get(j);
                            String set = element.getAttributeValue("resumeset");
                            String setid = element.getAttributeValue("resumesetId");
                            if (schemeXmlList1.contains(set + "=" + setid)) {
                                list.remove(j);
                            }
                        }
    
                    }
                }
            }
            
            if(schemeXmlList!=null&&schemeXmlList.size()>0)
                AddImportScheme(schemeXmlList);
            
            if(list!=null&&list.size()>0)
                DeleteImportScheme(list);
            
            checkMenuXml(schemeXmlList1);
            checkcodexml();
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    private void checkcodexml() {
        try{
            if (StringUtils.isEmpty(xml))
                xml = loadResumeParamFromDB();
            
            doc = PubFunc.generateDom(xml);
            XPath xPath = XPath.newInstance("/scheme/codesets");
            Element codesets = (Element) xPath.selectSingleNode(this.doc);
            if(codesets == null)
                return;
            
            List codesetList = codesets.getChildren();
            // 检查codeset中的指标在resumeFld.xml中是否存在
            for (int i = 0; i < codesetList.size(); i++) {
                Element element = (Element) codesetList.get(i);
                
                String resumeset = element.getAttributeValue("resumeset");
                
                checkMenu(resumeset);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 增加数据库中xml的set节点
     * @param list
     */
    private void AddImportScheme(ArrayList<String> list){
        Element sets = null;
        XPath xPath = null;
        try{
            
            if (StringUtils.isEmpty(xml)){
                Element root = new Element("scheme");
                doc = new Document(root);
            } else {
                doc = PubFunc.generateDom(xml);
                xPath = XPath.newInstance("/scheme/sets");
                sets = (Element) xPath.selectSingleNode(this.doc);
            }
            
            if(sets == null) {
                xPath = XPath.newInstance("/scheme");
                Element scheme = (Element) xPath.selectSingleNode(this.doc);
                sets = new Element("sets");
                scheme.addContent(sets);
            }
            
            for(int i=0;i<list.size();i++){
                String[] resumes = list.get(i).split("=");
                String resumeset = resumes[0];
                String resumesetId = "";
                if(resumes.length == 2)
                    resumesetId = resumes[1];
                
                Element set = new Element("set");
                set.addContent(new Element("menus"));
                set.setAttribute("resumeset", resumeset);
                set.setAttribute("resumesetId", resumesetId);
                set.setAttribute("ehrset", "");
                sets.addContent(set);
            }

            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            XMLOutputter XMLOut = new XMLOutputter(FormatXML());
            
            XMLOut.output(doc, bo);
            UpdateConstantXml(bo.toString());

        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    /**
     * 删除set节点
     * @param list
     */
    private void DeleteImportScheme(List list){
        try{
            doc = PubFunc.generateDom(xml);
            XPath xPath = XPath.newInstance("/scheme/sets");
            Element sets = (Element) xPath.selectSingleNode(this.doc);
            List set = (List) sets.getChildren();
            for(int i=0;i<list.size();i++){
                Element e = (Element) list.get(i);
                for(int j=0;j<set.size();j++){
                    Element child = (Element) set.get(j);
                    String x=child.getAttribute("resumeset").getValue();
                    String y=e.getAttribute("resumeset").getValue();
                    if(x.equals(y))
                        sets.removeContent(child);
                }
            }
            
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            XMLOutputter XMLOut = new XMLOutputter(FormatXML());
            
            XMLOut.output(doc, bo);
            UpdateConstantXml(bo.toString());

        }catch(Exception e){
            e.printStackTrace();
        }
        
    }

    private void checkMenuXml(ArrayList<String> list){
        if(list == null || list.size() < 1)
            return;
        
        for(int i = 0; i < list.size(); i++){
            String resumeset = list.get(i);
            if(resumeset.indexOf("=") > -1)
                resumeset = resumeset.substring(0, resumeset.indexOf("="));
            
            checkMenu(resumeset);
        }
    }
    /**
     * 检查子集指标是否改变
     * @param resumeset
     */
    private void checkMenu(String resumeset) {
        try {
            ArrayList<String> schemeXmlList = getResumeFielditem(resumeset);// 取之resumeFld.xml中的数据
            ArrayList<String> schemeXmlList1 = (ArrayList<String>) schemeXmlList.clone();
                               
            if (StringUtils.isEmpty(xml))
                xml = loadResumeParamFromDB();
            
            doc = PubFunc.generateDom(xml);
            XPath xPath = XPath.newInstance("/scheme/sets/set[@resumeset='" + resumeset + "']/menus");
            Element menus = (Element) xPath.selectSingleNode(this.doc);// 取之数据库中的字段数据
            if (menus != null) {

                List list = (List) menus.getChildren();
                // 数据库中的xml和resumeFld.xml中的menu节点比较
                for (int i = 0; i < list.size(); i++) {
                    Element element = (Element) list.get(i);
                    String menu = element.getAttributeValue("resumefld");
                    String menuID = element.getAttributeValue("resumefldid");
                    String resumecodesetid = element.getAttributeValue("resumecodesetid");
                    resumecodesetid = StringUtils.isEmpty(resumecodesetid) ? "0" : resumecodesetid;
                    schemeXmlList.remove(menu + "=" + menuID + ":" + resumecodesetid);
                }

                for (int i = 0; i < schemeXmlList1.size(); i++) {
                    for (int j = list.size() - 1; j >= 0; j--) {
                        Element element = (Element) list.get(j);
                        String menu = element.getAttributeValue("resumefld");
                        String menuID = element.getAttributeValue("resumefldid");
                        String resumecodesetid = element.getAttributeValue("resumecodesetid");
                        resumecodesetid = StringUtils.isEmpty(resumecodesetid) ? "0" : resumecodesetid;
                        if (schemeXmlList1.contains(menu + "=" + menuID + ":" + resumecodesetid))
                            list.remove(j);
                    }

                }

                if (schemeXmlList != null && schemeXmlList.size() > 0)
                    addSchemeMenu(resumeset, schemeXmlList);

                if (list != null && list.size() > 0)
                    deleteSchemeMenu(resumeset, list);

            } else {
                XPath xPath1 = XPath.newInstance("/scheme/codesets/codeset[@resumeset='" + resumeset + "']");
                List codeset = xPath1.selectNodes(this.doc);
                if (codeset != null) {
                    for (int i = 0; i < codeset.size(); i++) {
                        Element child = (Element) codeset.get(i);
                        child.getParentElement().removeContent(child);
                    }
                }

                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                XMLOutputter XMLOut = new XMLOutputter(FormatXML());

                XMLOut.output(doc, bo);
                xml = bo.toString();
                UpdateConstantXml(bo.toString());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 增加子集的指标
     * 
     * @param resumeset
     * @param menulist
     * @throws GeneralException
     */
    private void addSchemeMenu(String resumeset,ArrayList<String> menulist) throws GeneralException {
        ArrayList schemeParameterList = new ArrayList();
        try{
            doc = PubFunc.generateDom(xml);
            XPath xPath = XPath.newInstance("/scheme/sets/set[@resumeset='"+resumeset+"']/menus");
            Element menus = (Element) xPath.selectSingleNode(this.doc);
            List list = menus.getChildren();
                for(int i=0;i<menulist.size();i++){
                    String resumefld = menulist.get(i).split("=")[0];
                    String resumefldId = menulist.get(i).split("=")[1];
                    String resumecodesetid = resumefldId.split(":")[1];
                    resumefldId = resumefldId.split(":")[0];
                    Element element = new Element("menu");
                    element.setAttribute("resumefld", resumefld);
                    element.setAttribute("ehrfld", "");
                    element.setAttribute("resumefldid", resumefldId);
                    element.setAttribute("resumecodesetid", resumecodesetid);
                    menus.addContent(element);
                }

            
            
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            XMLOutputter XMLOut = new XMLOutputter(FormatXML());
            
            XMLOut.output(doc, bo);
            xml = bo.toString();
            UpdateConstantXml(bo.toString());
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * 删除menu节点
     * @param list
     */
    private void deleteSchemeMenu(String resumeset,List list){
        try{
            doc = PubFunc.generateDom(xml);
            XPath xPath = XPath.newInstance("/scheme/sets/set[@resumeset='"+resumeset+"']/menus");
            Element menus = (Element) xPath.selectSingleNode(this.doc);
            List menu = (List) menus.getChildren();
            for(int i=0;i<list.size();i++){
                Element e = (Element) list.get(i);
                for(int j=0;j<menu.size();j++){
                    Element child = (Element) menu.get(j);
                    String x=child.getAttribute("resumefld").getValue();
                    String y=e.getAttribute("resumefld").getValue();
                    if(x.equals(y)){
                        menus.removeContent(child);
                    }
                }
                XPath xPath1 = XPath.newInstance("/scheme/codesets/codeset[@resumeset='"+resumeset+"' and @resumecodeset='"+e.getAttribute("resumefld").getValue()+"']");
                Element codeset = (Element) xPath1.selectSingleNode(this.doc);
                if(codeset!=null){
                    codeset.getParentElement().removeContent(codeset);
                }
            }
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            XMLOutputter XMLOut = new XMLOutputter(FormatXML());
            
            XMLOut.output(doc, bo);
            UpdateConstantXml(bo.toString());

        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    /**
     * 得到数据库中xml的代码项对应（codeitems）
     * 
     * @param resumeset
     *            代码项的名称
     * @return
     * @throws GeneralException
     */
    private ArrayList<LazyDynaBean> getSchemecodeitemsXmlList(String ehrcodeset) throws GeneralException {
        ArrayList<LazyDynaBean> schemeParameterList = new ArrayList<LazyDynaBean>();
        try {
            doc = PubFunc.generateDom(xml);
            XPath xPath = XPath.newInstance("/scheme/codesets/codeset[@ehrcodeset='" + ehrcodeset + "']/codeitems");
            Element menus = (Element) xPath.selectSingleNode(this.doc);
            if(menus == null)
                return schemeParameterList;
            
            List list = (List) menus.getChildren();
            for (int i = 0; i < list.size(); i++) {
                Element element = (Element) list.get(i);
                LazyDynaBean bean = new LazyDynaBean();
                bean.set("ehritemid", element.getAttributeValue("ehritemid"));
                if (element.getAttribute("resumeitemid") != null)
                    bean.set("resumeitemid", element.getAttributeValue("resumeitemid"));
                else
                    bean.set("resumeitemid", "");

                schemeParameterList.add(bean);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return schemeParameterList;
    }

    /**
     * 保存第三方简历参数
     * 
     * @Title: saveResumeParam
     * @Description:
     * @param param
     */
    public void saveResumeParam(HashMap<String, String> param) {
        try {
            if (StringUtils.isEmpty(xml))
                xml = loadResumeParamFromDB();
            
            if (StringUtils.isEmpty(xml)){
                Element root = new Element("scheme");
                doc = new Document(root);
            } else {
                doc = PubFunc.generateDom(xml);
            }
            
            XPath xPath = XPath.newInstance("/scheme");
            Element schemes = (Element) xPath.selectSingleNode(this.doc);
            
            Iterator it = param.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                
                xPath = XPath.newInstance("/scheme/" + key);
                Element ele = (Element) xPath.selectSingleNode(this.doc);
                if(ele == null) {
                    ele = new Element(key);
                    schemes.addContent(ele);
                }
                
                ele.setText(value);
            }
            
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            XMLOutputter XMLOut = new XMLOutputter(FormatXML());

            XMLOut.output(this.doc, bo);
            UpdateConstantXml(bo.toString());
            this.xml = loadResumeParamFromDB();
            reloadParam();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存指标集对应
     * @param param 第三方简历与ehr中的指标集对应关系
     */
    public void saveResumefieldsetParam(ArrayList<LazyDynaBean> param) {
        try {
            if (StringUtils.isEmpty(xml))
                xml = loadResumeParamFromDB();
            
            if (StringUtils.isEmpty(xml)){
                Element root = new Element("scheme");
                doc = new Document(root);
            } else {
                doc = PubFunc.generateDom(xml);
            }
            
            XPath xPath = XPath.newInstance("/scheme/sets");
            Element sets = (Element) xPath.selectSingleNode(this.doc);
            if(sets == null) {
                xPath = XPath.newInstance("/scheme");
                Element scheme = (Element) xPath.selectSingleNode(this.doc);
                sets = new Element("sets");
                scheme.addContent(sets);
            }
            
            for(int i = 0; i < param.size(); i++) {
                LazyDynaBean bean = param.get(i);
                String resumeset = (String) bean.get("resumeset");
                String resumesetId = (String) bean.get("resumesetId");
                String ehrset = (String) bean.get("ehrset");
                resumesetId = resumesetId == null ? "" : resumesetId;
                ehrset = ehrset == null ? "" : ehrset;
                xPath = XPath.newInstance("/scheme/sets/set[@resumeset='" + resumeset + "']");
                Element set = (Element) xPath.selectSingleNode(this.doc);
                if(set == null) {
                    set = new Element("set");
                    sets.addContent(set);
                }
                set.setAttribute("resumeset", resumeset);
                set.setAttribute("resumesetId", resumesetId);
                set.setAttribute("ehrset", ehrset);
            }
            
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            XMLOutputter XMLOut = new XMLOutputter(FormatXML());

            XMLOut.output(this.doc, bo);
            UpdateConstantXml(bo.toString());
            this.xml = loadResumeParamFromDB();
            reloadParam();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 保存指标对应
     * @param param 
     */
    public void saveResumefielditemsParam(HashMap<String, ArrayList<LazyDynaBean>> param) {
        RowSet rs = null;
        try {
            if (StringUtils.isEmpty(xml))
                xml = loadResumeParamFromDB();
                
            if (StringUtils.isEmpty(xml)){
                Element root = new Element("scheme");
                doc = new Document(root);
            } else {
                doc = PubFunc.generateDom(xml);
            }
            
            XPath xPath = XPath.newInstance("/scheme/sets");
            Element sets = (Element) xPath.selectSingleNode(this.doc);
            if(sets == null) {
                xPath = XPath.newInstance("/scheme");
                Element scheme = (Element) xPath.selectSingleNode(this.doc);
                sets = new Element("sets");
                scheme.addContent(sets);
            }
            
            ContentDAO dao = new ContentDAO(this.conn);
            ArrayList<LazyDynaBean> codesets = new ArrayList<LazyDynaBean>();
            HashMap<String, ArrayList<LazyDynaBean>> itemMap = new HashMap<String, ArrayList<LazyDynaBean>>();
            Iterator it = param.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                String resumeset = (String) entry.getKey();
                ArrayList<LazyDynaBean> items = (ArrayList<LazyDynaBean>) entry.getValue();
                
                xPath = XPath.newInstance("/scheme/sets/set[@resumeset='" + resumeset + "']");
                Element set = (Element) xPath.selectSingleNode(this.doc);
                if(set == null)
                    return;
                
                xPath = XPath.newInstance("/scheme/sets/set[@resumeset='" + resumeset + "']/menus");
                Element menus = (Element) xPath.selectSingleNode(this.doc);
                if(menus == null) {
                    menus = new Element("menus");
                    set.addContent(menus);
                }

                String fieldset = set.getAttributeValue("ehrset");
                for (int i = 0; i < items.size(); i++) {
                    LazyDynaBean bean = items.get(i);
                    String ehrfld = (String) bean.get("ehrfld");
                    String resumefld = (String) bean.get("resumefld");
                    String resumefldid = (String) bean.get("resumefldid");
                    ehrfld = ehrfld == null ? "" : ehrfld;
                    resumefld = resumefld == null ? "" : resumefld;
                    resumefldid = resumefldid == null ? "" : resumefldid;
                    xPath = XPath.newInstance("/scheme/sets/set[@resumeset='" + resumeset
                            + "']/menus/menu[@resumefld='" + resumefld  + "']");
                    Element menu = (Element) xPath.selectSingleNode(this.doc);
                    if(menu == null) {
                        menu = new Element("menu");
                        menus.addContent(menu);
                    }
                    
                    menu.setAttribute("ehrfld", ehrfld);
                    menu.setAttribute("resumefld", resumefld);
                    menu.setAttribute("resumefldid", resumefldid);
                    
                    if(StringUtils.isNotEmpty(ehrfld) && StringUtils.isNotEmpty(fieldset)) {
                        FieldItem fi = DataDictionary.getFieldItem(ehrfld, fieldset);
                        if(fi != null && !"0".equalsIgnoreCase(fi.getCodesetid())) {
                            String resumecodesetid = menu.getAttributeValue("resumecodesetid");
                            resumecodesetid = StringUtils.isEmpty(resumecodesetid) ? "0" : resumecodesetid;
                            ArrayList<LazyDynaBean> codeItmes = new ArrayList<LazyDynaBean>();
                            LazyDynaBean codeset = new LazyDynaBean();
                            codeset.set("ehrcodeset", ehrfld);
                            codeset.set("resumecodeset", resumefld);
                            codeset.set("resumefldid", resumefldid);
                            codeset.set("resumeset", resumeset);
                            codeset.set("resumecodesetid", resumecodesetid);
                            codesets.add(codeset);
                            
                            String sql = "SELECT CODEITEMID FROM CODEITEM WHERE CODESETID=?";
                            if("UN".equalsIgnoreCase(fi.getCodesetid()) || "UM".equalsIgnoreCase(fi.getCodesetid())
                            		|| "@K".equalsIgnoreCase(fi.getCodesetid())) {
                            	String nowDate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
                            	sql = "SELECT CODEITEMID FROM ORGANIZATION WHERE CODESETID=?";
                            	sql += " and " + Sql_switcher.dateValue(nowDate)+" between start_date and end_date";
                            	sql += " ORDER BY a0000,codeitemid ";
                            }
                            
                            ArrayList<String> valueList = new ArrayList<String>();
                            valueList.add(fi.getCodesetid());
                            rs = dao.search(sql, valueList);
                            while (rs.next()) {
                                LazyDynaBean codeitem = new LazyDynaBean();
                                String codeitemid = rs.getString("CODEITEMID");
                                codeitem.set("ehritemid", codeitemid);
                                codeitem.set("resumeitemid", "");
                                codeItmes.add(codeitem);
                            }
                            
                            itemMap.put(ehrfld, codeItmes);
                        }
                    }
                }
                
                xPath = XPath.newInstance("/scheme/codesets/codeset[@resumeset='" + resumeset + "']");
                ArrayList<Element> codesetList = (ArrayList<Element>) xPath.selectNodes(this.doc);
                
                Element codeset = (Element) XPath.newInstance("/scheme/codesets").selectSingleNode(this.doc);
                
                for(int i = 0; i < codesetList.size(); i++){
                    Element e = codesetList.get(i);
                    codeset.removeContent(e);
                }
                
            }
            
            HashMap codeParam = new HashMap();
            codeParam.put("codesets", codesets);
            codeParam.put("codeitems", itemMap);
            
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            XMLOutputter XMLOut = new XMLOutputter(FormatXML());

            XMLOut.output(this.doc, bo);
            UpdateConstantXml(bo.toString());
            this.xml = loadResumeParamFromDB();
            reloadParam();
            saveResumeCode(codeParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 保存代码对应
     * @param param
     */
    public void saveResumeCode(HashMap param) {
        try {
            if (StringUtils.isEmpty(xml))
                xml = loadResumeParamFromDB();
            
            if (StringUtils.isEmpty(xml)){
                Element root = new Element("scheme");
                doc = new Document(root);
            } else {
                doc = PubFunc.generateDom(xml);
            }
            
            XPath xPath = XPath.newInstance("/scheme/codesets");
            Element codesets = (Element) xPath.selectSingleNode(this.doc);
            if(codesets == null) {
                xPath = XPath.newInstance("/scheme");
                Element scheme = (Element) xPath.selectSingleNode(this.doc);
                codesets = new Element("codesets");
                scheme.addContent(codesets);
            }
            
            ArrayList<LazyDynaBean> codeSets = (ArrayList<LazyDynaBean>) param.get("codesets");
            HashMap<String, ArrayList<LazyDynaBean>> codeitemMap = (HashMap<String, ArrayList<LazyDynaBean>>) param.get("codeitems");
            for (int i = 0; i < codeSets.size(); i++) {
                LazyDynaBean bean = codeSets.get(i);
                String ehrcodeset = (String) bean.get("ehrcodeset");
                String resumecodeset = (String) bean.get("resumecodeset");
                resumecodeset = resumecodeset == null ? "" : resumecodeset;
                String resumeset = (String) bean.get("resumeset");
                resumeset = resumeset == null ? "" : resumeset;
                String resumecodesetid = (String) bean.get("resumecodesetid");
                resumecodesetid = StringUtils.isEmpty(resumecodesetid) ? "0" : resumecodesetid;
                String resumefldid = (String) bean.get("resumefldid");
                resumefldid = resumefldid == null ? "" : resumefldid;
                xPath = XPath.newInstance("/scheme/codesets/codeset[@ehrcodeset='" + ehrcodeset + "']");
                Element codeset = (Element) xPath.selectSingleNode(this.doc);
                if(codeset == null) {
                    codeset = new Element("codeset");
                    codesets.addContent(codeset);
                }
                    
                codeset.setAttribute("ehrcodeset", ehrcodeset);
                codeset.setAttribute("resumecodeset", resumecodeset);
                codeset.setAttribute("resumefldid", resumefldid);
                codeset.setAttribute("resumeset", resumeset);
                codeset.setAttribute("resumecodesetid", resumecodesetid);
                
                ArrayList<LazyDynaBean> codeitems = codeitemMap.get(ehrcodeset);
                if(codeitems != null){
                    xPath = XPath.newInstance("/scheme/codesets/codeset[@ehrcodeset='" + ehrcodeset
                            + "']/codeitems");
                    Element codeItems = (Element) xPath.selectSingleNode(this.doc);
                    if(codeItems == null) {
                        codeItems = new Element("codeitems");
                        codeset.addContent(codeItems);
                    }
                    
                    for (int m = 0; m < codeitems.size(); m++) {
                        LazyDynaBean itemBean = codeitems.get(m);
                        String ehritemid = (String) itemBean.get("ehritemid");
                        String resumeitemid = (String) itemBean.get("resumeitemid");
                        resumeitemid = resumeitemid == null ? "" : resumeitemid;
                        
                        xPath = XPath.newInstance("/scheme/codesets/codeset[@ehrcodeset='" + ehrcodeset
                                + "']/codeitems/codeitem[@ehritemid='" + ehritemid  + "']");
                        Element codeItem = (Element) xPath.selectSingleNode(this.doc);
                        if(codeItem == null) {
                            codeItem = new Element("codeitem");
                            codeItems.addContent(codeItem);
                        }
                        
                        codeItem.setAttribute("ehritemid", ehritemid);
                        codeItem.setAttribute("resumeitemid", resumeitemid);
                    }
                }
                
            }
            
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            XMLOutputter XMLOut = new XMLOutputter(FormatXML());
            XMLOut.output(this.doc, bo);
            UpdateConstantXml(bo.toString());
            this.xml = loadResumeParamFromDB();
            reloadParam();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 读取并解析xxx_resume_fld.xml文件
     * 
     * @Title: getResumeFld
     * @Description:
     * @return
     * @throws GeneralException
     */
    private ArrayList<String> getResumeFld() throws GeneralException {
        ArrayList<String> schemeXmlList = new ArrayList<String>();
        InputStream ip = null;
        Document doc = null;
        try {
            ip = this.getClass().getResourceAsStream("/com/hjsj/hrms/module/recruitment/thirdpartyresume/config/"
                    + this.getThirdPartyName().toLowerCase() + "_resume_fld.xml");
            doc = PubFunc.generateDom(ip);
            XPath xPath = XPath.newInstance("/scheme/sets");
            Element sets = (Element) xPath.selectSingleNode(doc);
            List list = (List) sets.getChildren();
            for (int i = 0; i < list.size(); i++) {
                Element element = (Element) list.get(i);
                schemeXmlList.add(element.getAttributeValue("setname") + "=" + element.getAttributeValue("setid"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(ip);
        }
        return schemeXmlList;
    }

    /**
     * 获取resumeFld.xml子集中的指标
     * @param resumeset
     * @return
     * @throws GeneralException
     */
    private ArrayList<String> getResumeFielditem(String resumeset) throws GeneralException {
        ArrayList<String> menulist = new ArrayList<String>();
        InputStream ip = null;
        try {
            ip = this.getClass().getResourceAsStream("/com/hjsj/hrms/module/recruitment/thirdpartyresume/config/"
                    + this.getThirdPartyName().toLowerCase() + "_resume_fld.xml");
            doc = PubFunc.generateDom(ip);
            XPath xPath = XPath.newInstance("/scheme/sets/set[@setname='" + resumeset + "']/menus");
            Element menus = (Element) xPath.selectSingleNode(this.doc);

            if (menus != null) {
                List list = (List) menus.getChildren();
                for (int i = 0; i < list.size(); i++) {
                    Element element = (Element) list.get(i);
                    String itemid = element.getAttributeValue("itemid");
                    String codesetid = element.getAttributeValue("codesetid");
                    codesetid = StringUtils.isEmpty(codesetid) ? "0" : codesetid;
                    menulist.add(element.getAttributeValue("itemname") + "=" + itemid + ":" + codesetid);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            if(ip != null)
                PubFunc.closeResource(ip);
        }
        return menulist;
    }
    
    /**
     * 获取代码项Commonvalue
     * 
     * @param resumeset
     * @param itemname
     * @return
     * @throws GeneralException
     */
    private LazyDynaBean getCommonvalueXmlList(String resumeset, String itemname)
            throws GeneralException {
        LazyDynaBean bean = new LazyDynaBean();
        InputStream ip = null;
        try {
            ip = this.getClass().getResourceAsStream("/com/hjsj/hrms/module/recruitment/thirdpartyresume/config/"
                            + this.getThirdPartyName().toLowerCase() + "_resume_fld.xml");
            doc = PubFunc.generateDom(ip);
            XPath xPath = XPath.newInstance("/scheme/sets/set[@setname='" + resumeset + "']/menus/menu[@itemname='" + itemname + "']");
            XPath xPath1 = XPath.newInstance("/scheme/sets/set[@setname='" + resumeset + "']");
            Element set = (Element) xPath1.selectSingleNode(this.doc);
            Element menu = (Element) xPath.selectSingleNode(this.doc);

            bean.set("setid", set.getAttributeValue("setid"));
            bean.set("itemid", menu.getAttributeValue("itemid"));
            bean.set("itemname", menu.getAttributeValue("itemname"));
            
            if (menu.getAttribute("commonvalue") != null)
                bean.set("commonvalue", menu.getAttributeValue("commonvalue"));
            else
                bean.set("commonvalue", "");

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(ip);
        }
        return bean;
    }
    
    /**
     * 执行导入简历操作
     * 
     * @Title: importResume
     * @Description:
     */
    protected void importResume() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Format FormatXML() {
        // 格式化生成的xml文件，如果不进行格式化的话，生成的xml文件将会是很长的一行...
        Format format = Format.getCompactFormat();
        format.setEncoding("UTF-8");
        format.setIndent(" ");
        return format;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    /**
     * 更新Constant STR_VALUE大文本xml
     * 
     * @param StrValue
     * @throws SQLException
     */
    private void UpdateConstantXml(String StrValue) throws SQLException {
        try {
            ContentDAO dao = new ContentDAO(conn);
            ArrayList<String> list = new ArrayList();
            String sql = "update CONSTANT set STR_VALUE=?  where UPPER(CONSTANT)='"
                    + this.getThirdPartyName().toUpperCase() + "_RESUME_PARAM'";
            list.add(StrValue);
            dao.update(sql, list);
            xml = StrValue;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从第三方获取简历数据，此方法由子类具体实现
     * 
     * @Title: getResumeFromThirdParty
     * @Description:
     * @param params
     */
    public abstract ArrayList getResumeFromThirdParty(HashMap params);

    /**
     * 取到第三方简历来源简称，比如beisen,caizhi，此方法有子类具体实现
     * 
     * @Title: getThirdPartyName
     * @Description:
     * @return
     */
    protected abstract String getThirdPartyName();

    /**
     * 记录导入过程日志
     * 
     * @Title: recordLog
     * @Description:
     * @param info
     */
    /**
     * protected void recordLog(String info) {
     * 
     * }
     * 
     * /** 从数据库加载导入参数
     * 
     * @Title: loadResumeParamFromDB
     * @Description:
     * @return
     */
    private String loadResumeParamFromDB() {
        String xml = "";
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            // 常量表中查找XXXX_RESUME_PARAM常量
            String sql = "select str_value  from CONSTANT where UPPER(CONSTANT)='"
                    + this.getThirdPartyName().toUpperCase() + "_RESUME_PARAM'";
            rs = dao.search(sql);
            if (rs.next()) {
                // 获取XML文件
                xml = Sql_switcher.readMemo(rs, "STR_VALUE");
            } else {
                ArrayList<String> list = new ArrayList<String>();
                list.add(this.getThirdPartyName().toUpperCase() + "_RESUME_PARAM");
                list.add("A");
                list.add("简历导入—导入方案");
                list.add("");
                dao.insert("insert into CONSTANT values(?,?,?,?)", list);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return xml;
    }

    /**
     * 加载第三方简历
     * 
     * @Title: loadResumeFldXML
     * @Description:
     * @return
     */
    private String loadResumeFldXML() {
        String xml = "";

        return xml;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public UserView getUserView() {
        return userView;
    }

    public void setUserView(UserView userView) {
        this.userView = userView;
    }

    /**
     * 导入人员数据
     * @param resumeMap
     * @throws GeneralException
     */
    public void addResunme(HashMap resumeMap) throws GeneralException {
    	this.cat.debug("开始导入人员数据………………");
        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        // 增加人员库标志0为人员库不存在,1,2,3为人员库中存在,分别为不导入,替换,追加
        String flag = "0"; 
        // 标识指标对应的简历值
        String mainItem = ""; 
        // 次关键指标对应的值
        String secItem = ""; 
        //文件名称或简历中的人员姓名
        String fileName = "";
        try {
            this.fieldSet = "";
            this.fieldSetI9999 = "0";
            fileName = (String) resumeMap.get("personID");
            mainItem = (String) resumeMap.get("mainItemValue");
            secItem = (String) resumeMap.get("secItemValue");
            ArrayList<LazyDynaBean> ResumeInfoList = (ArrayList<LazyDynaBean>) resumeMap.get("ResumeInfoList");
            Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
            String blacklist_per = sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST, "base");// 黑名单人员库
            String blacklist_field = sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST, "field");// 黑名单人员指标
            // 获取应聘人员库
            RecordVo vo = ConstantParamter.getConstantVo("ZP_DBNAME");
            String dbName = (String) ((LazyDynaBean)param.get("thirdPartyParm")).get("dbname");

            if (vo != null && StringUtils.isEmpty(dbName))
                dbName = vo.getString("str_value");
            
            if (StringUtils.isEmpty(dbName))
                throw GeneralExceptionHandler.Handle(new Exception("请在定义方案页面设置人员库或在招聘管理-参数设置中配置招聘人才库！"));

            StringBuffer sb = this.userView.getDbpriv();// 用户人员库权限
            if (!this.userView.isSuper_admin() && !sb.toString().contains(dbName)) 
                throw GeneralExceptionHandler.Handle(new Exception("您没有操作应聘人员库权限!"));

            /** 黑名单检查 */
            if (blacklist_field != null && !"".equals(blacklist_field) && blacklist_per != null
                    && !"".equals(blacklist_per)) {
                String blacklist_value = (String) resumeMap.get("blacklist_value");
                if (isBlackPerson(blacklist_field, blacklist_per, blacklist_value)) {
                    blacklistLog += mainItem + "\t" + secItem + ";";
                }
            }
            String A0100 = "";

            LazyDynaBean paramBean = (LazyDynaBean) this.param.get("thirdPartyParm");
            String identifyfld = "";
            String sencondfld = "";
            identifyfld = (String) paramBean.get("identifyfld"); // 标识指标
            sencondfld = (String) paramBean.get("sencondfld"); // 次关键指标
            String nbase = (String) paramBean.get("dbname"); // 目标人员库
            
            String tableName = nbase + "A01"; // 人才库表名

            StringBuffer Str = new StringBuffer("select a0100 from " + tableName);
            for (int i = 0; i < ResumeInfoList.size(); i++) {
                LazyDynaBean bean = (LazyDynaBean) ResumeInfoList.get(i);
                if (bean.get("ehrfld").equals(identifyfld) && bean.get("value") != null
                        && !"".equals(bean.get("value")))
                    mainItem = (String) bean.get("value");
                else if (bean.get("ehrfld").equals(sencondfld) && bean.get("value") != null
                        && !"".equals(bean.get("value")))
                    secItem = (String) bean.get("value");
            }
            
            ArrayList valueList = new ArrayList();
            Str.append(" where " + identifyfld + "=?");
            valueList.add(mainItem);
            // 次关键指标不为空
            if (!"".equals(sencondfld)){
                Str.append(" and " + sencondfld + "=?");
                valueList.add(secItem);
            } 
            
            // 判断次关键指标
            boolean xflag = false;
            if (!"".equals(sencondfld))
                xflag = true;

            String imptype = (String) paramBean.get("imptype");
            if("1".equalsIgnoreCase(imptype)) {
            	String sql = Str.toString();
            	EmployResumeBo bo = new EmployResumeBo(this.conn);
            	ArrayList dbnameList = bo.getuserList();
            	for (int i = 0; i < dbnameList.size(); i ++) {
            	    HashMap<String,String> map = (HashMap<String,String>) dbnameList.get(i);
            		String dbname = map.get("id");
            		if(StringUtils.isEmpty(dbname) || nbase.equalsIgnoreCase(dbname))
            			continue;
            		
            		String table = dbname + "A01";
            		Str.append(" UNION ALL ");
            		Str.append(sql.replace(tableName, table));
            		valueList.add(mainItem);
                    // 次关键指标不为空
                    if (!"".equals(sencondfld))
                        valueList.add(secItem);
            	}
            }
            
            this.cat.debug("查询是否是已导入的sql：" + Str.toString());
            // 标识指标和标识指标对应简历的值不为空;若次关键指标不为空,则指标值也不为空
            if ((!"".equals(identifyfld) && !"".equals(mainItem)) || xflag) {
                rs = dao.search(Str.toString(), valueList);
                if (rs.next()) {
                    // 判断更新方式 imptype 不导入,替换,追加
                    if ("1".equals(imptype) || "".equals(imptype))
                        flag = "1";
                    else if ("2".equals(imptype)) {
                        A0100 = rs.getString("a0100");
                        flag = "2";
                        importNum++;
                    } else if ("3".equals(imptype)) {
                        flag = "3";
                        importNum++;
                    } else if ("4".equals(imptype)) {
                        A0100 = rs.getString("a0100");
                        flag = "5";
                        importNum++;
                    }

                } else {
                    if ("4".equals(imptype))
                        flag = "5";
                    else{
                        flag = "0";
                        importNum++;
                    }
                }
            } else {
                // 没有定义标识指标或标识指标未对应,不导入
                flag = "4";
                FlistLog += fileName + ";";
                importFlag = false;
            }

            if ("1".equalsIgnoreCase(flag)) {
                // 不导入
                PlistLog += " " + mainItem + " " + secItem + ";";
                importFlag = false;
            } else if ("2".equalsIgnoreCase(flag) || "5".equalsIgnoreCase(flag)) {
                if(StringUtils.isEmpty(A0100)){
                    // 新增人员
                    // 这时的tableName是nbse+'A01'
                    RecordVo resumeVo = new RecordVo(tableName);
                    A0100 = DbNameBo.insertMainSetA0100(tableName, conn);
                    resumeVo.setString("a0100", A0100);
                    resumeVo = dao.findByPrimaryKey(resumeVo);
                    //人员主集添加guidkey指标的值
                    UUID uuid = UUID.randomUUID();
                    this.guidkey = uuid.toString().toUpperCase(); 
                    resumeVo.setString("guidkey", this.guidkey);
                    dao.updateValueObject(resumeVo);
                    
                    valueList.clear();
                    StringBuffer sql = new StringBuffer();
                    sql.append("update " + tableName);
                    sql.append(" set CreateUserName=?,");
                    sql.append(" CreateTime=?,");
                    sql.append(" ModUserName=?,");
                    sql.append(" ModTime=?");
                    sql.append(" where a0100=?");
                    valueList.add(this.userView.getUserName());
                    valueList.add(new Timestamp(new Date().getTime()));
                    valueList.add(this.userView.getUserName());
                    valueList.add(new Timestamp(new Date().getTime()));
                    valueList.add(A0100);
                    dao.update(sql.toString(), valueList);
                }
                
                RecordVo recVo = new RecordVo(tableName);
                recVo.setString("a0100", A0100);
                recVo = dao.findByPrimaryKey(recVo);
                if("5".equalsIgnoreCase(flag)){
                    String synchronousFlag = (String) paramBean.get("synchronousFlag");
                    recVo.setString(synchronousFlag, "1");
                }
                
                dao.updateValueObject(recVo);
                
                StringBuffer sql = new StringBuffer();
                sql.append("update " + tableName);
                sql.append(" set ModUserName=?,");
                sql.append(" ModTime=?");
                sql.append(" where a0100=?");
                valueList.clear();
                valueList.add(this.userView.getUserName());
                valueList.add(new Timestamp(new Date().getTime()));
                valueList.add(A0100);
                dao.update(sql.toString(), valueList);
                    
                for (int i = 0; i < ResumeInfoList.size(); i++) {
                    LazyDynaBean bean = (LazyDynaBean) ResumeInfoList.get(i);
                    String itemid = (String) bean.get("ehrfld");

                    // 判断是主集和子集
                    rs = dao.search("select fieldsetid from fielditem where Lower(itemid) ='"
                            + itemid.toLowerCase() + "'");
                    if (rs.next()) {
                        String setID = rs.getString("fieldsetid");
                        this.cat.error("开始导入简历信息；姓名=" + fileName + ", 简历子集：" + setID
                                + "，指标：" + bean.get("ehrfld") + "，指标的值：" + bean.get("value"));
                        AddResumeInfo(dbName, A0100, setID, bean, mainItem, secItem);
                        this.cat.error("导入简历信息成功 ；姓名=" + fileName + ", 子集：" + setID
                                + "，指标：" + bean.get("ehrfld") + "，指标的值：" + bean.get("value"));
                    }
                }
                //此处代码为外网应聘人员简历导入时更新应聘职位关联表的代码，由于去掉了才智创鑫的接口所有代码注释掉
//                if(nbase.equalsIgnoreCase(dbName)) {
//                    // 删除该人员应聘岗位
//                    valueList.clear();
//                    valueList.add(A0100);
//                    dao.delete("delete from zp_pos_tache where a0100=?", valueList);
//    
//                    String now = new SimpleDateFormat("yyyyMMdd").format(new Date());
//                    StringBuffer str = new StringBuffer("select * from z03 where ");
//                    str.append(Sql_switcher.year("Z0329") + "*10000+" + Sql_switcher.month("Z0329")
//                            + "*100+" + Sql_switcher.day("Z0329") + "<=?");
//                    str.append(" and " + Sql_switcher.year("Z0331") + "*10000+"
//                            + Sql_switcher.month("Z0331") + "*100+" + Sql_switcher.day("Z0331") + ">=?");
//                    str.append(" and (Z0351 is null or z0351='')");
//                    valueList.clear();
//                    valueList.add(now);
//                    valueList.add(now);
//                    rs = dao.search(str.toString(), valueList);
//                    //增加应聘岗位
//                    if (rs.next()) {
//                        RecordVo zpVo = new RecordVo("zp_pos_tache");
//                        zpVo.setString("a0100", A0100);
//                        zpVo.setString("zp_pos_id", rs.getString("z0301"));
//                        zpVo.setString("thenumber", "1");
//                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
//                        zpVo.setDate("apply_date", df.format(new Date()));
//                        zpVo.setString("resume_flag", "10");
//                        zpVo.setString("status", "0");
//                        dao.addValueObject(zpVo);
//                    }
//                }

            } else if (("0".equalsIgnoreCase(flag) || "3".equalsIgnoreCase(flag)) && !"5".equalsIgnoreCase(flag)) {
                // 新增人员
                RecordVo resumeVo = new RecordVo(tableName);// 这时的tableName是nbse+'A01'
                String a0100 = DbNameBo.insertMainSetA0100(tableName, conn);
                resumeVo.setString("a0100", a0100);
                resumeVo = dao.findByPrimaryKey(resumeVo);
                //人员主集添加guidkey指标的值
                UUID uuid = UUID.randomUUID();
                this.guidkey = uuid.toString().toUpperCase(); 
                resumeVo.setString("guidkey", this.guidkey);
                dao.updateValueObject(resumeVo);
                
                StringBuffer sql = new StringBuffer();
                sql.append("update " + tableName);
                sql.append(" set CreateUserName=?,");
                sql.append(" CreateTime=?,");
                sql.append(" ModUserName=?,");
                sql.append(" ModTime=?");
                sql.append(" where a0100=?");
                valueList.clear();
                valueList.add(this.userView.getUserName());
                valueList.add(new Timestamp(new Date().getTime()));
                valueList.add(this.userView.getUserName());
                valueList.add(new Timestamp(new Date().getTime()));
                valueList.add(a0100);
                dao.update(sql.toString(), valueList);
                
                for (int i = 0; i < ResumeInfoList.size(); i++) {
                    LazyDynaBean bean = (LazyDynaBean) ResumeInfoList.get(i);
                    String itemid = (String) bean.get("ehrfld");
                    // 判断是主集和子集
                    rs = dao.search("select fieldsetid from fielditem where Lower(itemid) ='"
                             + itemid.toLowerCase() + "'");
                    if (rs.next()) {
                        String setID = rs.getString("fieldsetid");
                        this.cat.error("开始导入简历信息；姓名=" + fileName + ", 简历子集：" + setID 
                                + "，指标：" + bean.get("ehrfld") + "，指标的值：" + bean.get("value"));
                        AddResumeInfo(nbase, a0100, setID, bean, mainItem, secItem);
                        this.cat.error("导入简历信息成功 ；姓名=" + fileName + ", 子集：" + setID
                                + "，指标：" + bean.get("ehrfld") + "，指标的值：" + bean.get("value"));
                    }
                }
                
                if(nbase.equalsIgnoreCase(dbName)) {
                    String now = new SimpleDateFormat("yyyyMMdd").format(new Date());
                    StringBuffer str = new StringBuffer("select z0301 from z03 where ");
                    str.append(Sql_switcher.year("Z0377") + "*10000+" + Sql_switcher.month("Z0377")
                            + "*100+" + Sql_switcher.day("Z0377") + "<=?");
                    str.append(" and " + Sql_switcher.year("Z0379") + "*10000+"
                            + Sql_switcher.month("Z0379") + "*100+" + Sql_switcher.day("Z0379") + ">=?");
                    str.append(" and Z0351=''");
                    valueList.clear();
                    valueList.add(now);
                    valueList.add(now);
                    rs = dao.search(str.toString(), valueList);
                    if (rs.next()) {
                        RecordVo zpVo = new RecordVo("zp_pos_tache");
                        zpVo.setString("a0100", a0100);
                        zpVo.setString("zp_pos_id", rs.getString("z0301"));
                        zpVo.setString("thenumber", "1");
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
                        zpVo.setDate("apply_date", df.format(new Date()));
                        zpVo.setString("status", "0");
                        dao.addValueObject(zpVo);
                    }
                }
            }
        } catch (SQLException e) {
            importFlag = false;
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }

    }

    /**
     * 
     * @Title: AddResumeInfo
     * @Description: TODO 插入简历信息到人才库
     * @param dbname 人员库
     * @param a0100  人员编号
     * @param setID  表名
     * @param bean
     * @param ResumeName
     * @throws GeneralException void
     * @throws
     */
    private void AddResumeInfo(String dbname, String a0100, String setID, LazyDynaBean bean,
            String mainItem, String secItem) throws GeneralException {
        ContentDAO dao = new ContentDAO(conn);
        DbNameBo bo = new DbNameBo(conn);
        EmployNetPortalBo employNetPortalBo = new EmployNetPortalBo(conn);
        try {
            String tableName = dbname.toLowerCase() + setID; // 人才库表名

            ArrayList valueList = new ArrayList();
            RecordVo vo = new RecordVo(tableName);
            vo.setString("a0100", a0100);
            // 增加主集信息
            if ("a01".equalsIgnoreCase(setID)) {
                vo = dao.findByPrimaryKey(vo);
                vo = getRecordVo(vo, bean, mainItem, secItem);
                dao.updateValueObject(vo);
                
                StringBuffer sql = new StringBuffer();
                sql.append("update " + tableName);
                sql.append(" set ModUserName=?,");
                sql.append(" ModTime=?");
                sql.append(" where a0100=?");
                valueList.add(this.userView.getUserName());
                valueList.add(new Timestamp(new Date().getTime()));
                valueList.add(a0100);
                dao.update(sql.toString(), valueList);

            } else {
                //更新子集信息
                int i9999 = employNetPortalBo.getI9999(setID, a0100, dbname);
                String i99 = (String) bean.get("i9999");
                int I9999 = 0;
                if (StringUtils.isNotEmpty(i99))
                    I9999 = Integer.parseInt(i99);
                //判断子集记录是否是同一条数据
                boolean flag = true;
                //判断子集记录是否是同一条数据
                if(!this.fieldSet.equalsIgnoreCase(setID) || !this.fieldSetI9999.equalsIgnoreCase(i99) ) {
                    this.fieldSet =  setID;
                    this.fieldSetI9999 = StringUtils.isEmpty(i99) ? "0" : i99;
                    flag = false;
                }
                
                if ((i9999 > 0 && (I9999 + 1) == i9999) || flag) {
                    //更新子集中的对应指标的值
                    vo.setInt("i9999", i9999);
                    vo = dao.findByPrimaryKey(vo);
                    vo = getRecordVo(vo, bean, mainItem, secItem);
                    if (vo != null){
                        dao.updateValueObject(vo);
                        
                        StringBuffer sql = new StringBuffer();
                        sql.append("update " + tableName);
                        sql.append(" set ModUserName=?,");
                        sql.append(" ModTime=?");
                        sql.append(" where a0100=? and i9999=?");
                        valueList.clear();
                        valueList.add(this.userView.getUserName());
                        valueList.add(new Timestamp(new Date().getTime()));
                        valueList.add(a0100);
                        valueList.add(i9999);
                        dao.update(sql.toString(), valueList);
                    }

                } else {
                    //在人员子集中新增一条记录
                    I9999 = Integer.valueOf(DbNameBo.insertSubSetA0100(tableName, a0100, conn));
                    vo = getRecordVo(vo, bean, mainItem, secItem);
                    vo.setInt("i9999", I9999);
                    // 判断人员子集是否有guidkey指标，若有则添加，没有不添加
                    DbWizard dbWizard = new DbWizard(conn);
                    boolean guidFlag = dbWizard.isExistField(tableName, "guidkey", false);
                    if (guidFlag)
                        vo.setString("guidkey", this.guidkey);
                    
                    vo.setString("state", "3");
                    dao.updateValueObject(vo);
                    
                    StringBuffer sql = new StringBuffer();
                    sql.append("update " + tableName);
                    sql.append(" set CreateUserName=?,");
                    sql.append(" CreateTime=?,");
                    sql.append(" ModUserName=?,");
                    sql.append(" ModTime=?");
                    sql.append(" where a0100=? and i9999=?");
                    valueList.clear();
                    valueList.add(this.userView.getUserName());
                    valueList.add(new Timestamp(new Date().getTime()));
                    valueList.add(this.userView.getUserName());
                    valueList.add(new Timestamp(new Date().getTime()));
                    valueList.add(a0100);
                    valueList.add(I9999);
                    dao.update(sql.toString(), valueList);
                    
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }

    /**
     * 
     * @Title: getRecordVo
     * @Description: TODO 插入子集信息
     * @param vo
     * @param bean
     * @param ResumeName
     * @return
     * @throws GeneralException
     *             RecordVo
     * @throws
     */
    private RecordVo getRecordVo(RecordVo vo, LazyDynaBean bean, String mainItem, String secItem)
            throws GeneralException {
        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        try {
            // 字段的itemid
            String itemid = (String) bean.get("ehrfld");
            // 字段对应的值
            String value = (String) bean.get("value");
            // 判断指标是否为代码型
            ArrayList<String> valueList = new ArrayList<String>();
            valueList.add(itemid.toUpperCase());
            rs = dao.search("select codesetid from FIELDITEM where UPPER(ITEMID)=? and UPPER(CODESETID)<>'0'", valueList);
            if (rs.next()) {
                String codesetid = rs.getString("codesetid");
                String resumefld = (String) bean.get("resumefld");// 字段的描述
                String resumeset = (String) bean.get("resumeset");// 指标集描述
                String CodeItem = "";
                if (!"".equals(value))
                    CodeItem = getCodeItem(resumeset, resumefld, value, codesetid);

                // 若codeitem为空则代码型指标未对应
                if ("".equals(CodeItem) && !"".equals(value)) {
                    vo.setString(((String) bean.get("ehrfld")).toLowerCase(), CodeItem);
                    ClistLog += (mainItem + "\t" + secItem + "(" + resumeset + ")" + ":" + resumefld + "未对应" + "\r\n");
                } else 
                    vo.setString(((String) bean.get("ehrfld")).toLowerCase(), CodeItem);
                
            } else { // 不为代码型指标
                String itemtype = (String) bean.get("itemtype");
                if ("A".equals(itemtype)) {
                    vo.setString(((String) bean.get("ehrfld")).toLowerCase(), value);
                } else if ("D".equals(itemtype)) {
                	if(StringUtils.isEmpty(value)) {
                		vo.setDate(((String) bean.get("ehrfld")).toLowerCase(), "");
                	} else {
	                    value = value.replaceAll("\\.", "-");
	                    value = value.replace("年", "-");
	                    value = value.replace("月", "-");
	                    value = value.replace("日", "-");
	                    if (value.indexOf("\\'") > -1) 
	                    	value = value.replaceAll("\\'", "");
	                    
	                    if(StringUtils.isNotEmpty(value) && value.length() > 10)
	                        value = value.substring(0, 10);
	                        
	                    String[] dd = value.split("-");
	                    String year = dd[0];
	                    String month = "01";
	                    String day = "01";
	                    if (dd.length >= 2 && dd[1] != null && dd[1].trim().length() > 0)
	                        month = dd[1];
	                    
	                    if (dd.length >= 3 && dd[2] != null && dd[2].trim().length() > 0)
	                        day = dd[2];
	                    
	                    Calendar d = Calendar.getInstance();
	                    if ("至今".equals(value)) {
	                        value = null;
	                        vo.setDate(((String) bean.get("ehrfld")).toLowerCase(), value);
	                    } else if (year != null && !"".equals(year)) {
	                        d.set(Calendar.YEAR, Integer.parseInt(year));
	                        d.set(Calendar.MONTH, Integer.parseInt(month) - 1);
	                        d.set(Calendar.DATE, Integer.parseInt(day));
	                        vo.setDate(((String) bean.get("ehrfld")).toLowerCase(), year + "-" + month + "-" + day);
	                    }
                	}

                } else if ("M".equals(itemtype)) {
                    vo.setString(((String) bean.get("ehrfld")).toLowerCase(), value);
                } else if ("N".equals(itemtype)) {
                	int num = 0;
                	if(StringUtils.isNotEmpty(value))
                		num = Integer.parseInt(value);
                	
                    vo.setInt(itemid.toLowerCase(), num);

                } else
                    vo.setString(itemid.toLowerCase(), null);

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }

        return vo;

    }
    /**
     * 校验黑名单
     * @param blackField 黑名单指标
     * @param blackNbase 黑名单人员库
     * @param blackValue 黑名单指标的之
     * @return
     */
    private boolean isBlackPerson(String blackField, String blackNbase, String blackValue) {
        boolean flag = false;
        RowSet rs = null;
        try {
            StringBuffer sql = new StringBuffer("");
            sql.append("select * from " + blackNbase + "A01 ");
            sql.append("where UPPER(" + blackField + ") = ?");
            ArrayList<String> valueList = new ArrayList<String>();
            valueList.add( blackValue.toUpperCase());
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString(), valueList);
            if (rs.next())
                flag = true;
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return flag;
    }
    
    /**
     * 记录导入信息
     * 
     * @throws GeneralException
     */
    public String WriteImportDetail(HashMap<String, String> logMap) throws GeneralException {
        File file = null;
        BufferedWriter output = null;
        String outname = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = sdf.format(new Date());
            outname = "";
            String pathFile = System.getProperty("java.io.tmpdir");
            pathFile += "\\"+ this.getThirdPartyName() + "ResumeImportLog.txt";
            file = new File(pathFile);
            output = new BufferedWriter(new FileWriter(file,true));

            StringBuffer Log = new StringBuffer();
            int Num = Integer.valueOf(logMap.get("Num"));
            showInforLog = userView.getUserName()+"\t"+date+"\t"+"共解析"+Num+"份简历,成功"+importNum+"份简历";
            Log.append(showInforLog);
            if(Num-importNum>0&&importNum>0){
                Log.append(","+(Num-importNum)+"份导入不成功");
                outname="导入部分成功!";
            } else if(Num-importNum==0)
                outname="导入成功!";
            else if(importNum==0)
                outname="导入不成功!";
            
            Log.append("\r\n");
            String Clist = logMap.get("clist");
            if(Num>importNum)   
                Log.append("详细信息:\r\n");

            Iterator it = logMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                String key = (String) entry.getKey();
                if("Num".equalsIgnoreCase(key))
                    continue;
            }
            
            if (!"".equals(blacklistLog)) 
                Log.append("以下记录在黑名单库中存在,不能导入:"+blacklistLog+"\r\n");

            if (!"".equals(PlistLog)) {
                Log.append("以下记录人员库中已存在,不能导入:" + "\r\n");
                Log.append(PlistLog + "\r\n");
            }

            output.write(Log.toString() + "\r\n");
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(output);
            PubFunc.closeResource(file);
        }
        return outname;
    }
    /**
     * 获得代码项
     * @param resumeset：指标集描述
     * @param resumefld：字段的描述
     * @param resumeitemid：itemid所对应的value 字段对应的值
     * @param codesetid 代码类的名称（AM...）
     * @return
     * @throws GeneralException
     */
    public String getCodeItem(String resumeset,String resumefld,String resumeitemid,String codesetid) throws GeneralException {
        String CodeItem="";
        try{
            doc = PubFunc.generateDom(xml);
            XPath xPath = XPath.newInstance("/scheme/codesets/codeset[@resumecodeset='"+resumefld+"']/codeitems");
            Element codeitems = (Element) xPath.selectSingleNode(this.doc);
            if(codeitems == null)
                return CodeItem;
            
            List codeitem = codeitems.getChildren();
            
            if(StringUtils.isNotEmpty(resumeitemid)) {
            	resumeitemid = PubFunc.hireKeyWord_filter(resumeitemid+";");
            }
            
            for(int i=0;i<codeitem.size();i++){//挨着循环当前代码类
                Element element = (Element) codeitem.get(i);
                //人员库代码名称+自定义代码名称
                String reitemid = element.getAttributeValue("resumeitemid");//这个是在代码对应中配置的简历信息
                if(reitemid==null)
                    reitemid = "";
                
                String itemname = AdminCode.getCodeName(codesetid, element.getAttributeValue("ehritemid"))+";"+reitemid+";";
                itemname = PubFunc.hireKeyWord_filter(itemname);
                
                if(itemname.contains(resumeitemid)&&!"".equals(resumeitemid)){
                    CodeItem = element.getAttributeValue("ehritemid");
                    break;
                }
                
                if("籍贯".equals(resumefld)&&!"".equals(resumeitemid)){
                    String[] jgArr=resumeitemid.split("-");
                    boolean flag = true;
                    for(int j=0;j<jgArr.length;j++){
                        if(!itemname.contains(jgArr[j]))
                            flag =false;
                    }
                    
                    if(flag){
                        CodeItem = element.getAttributeValue("ehritemid");
                        break;
                    }
                }
            }
            
        }catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e); 
        }
        
        return CodeItem;
    }
    
    /**
     * 读取并解析xxx_resume_codeitem.xml文件
     * @return
     * @throws GeneralException
     */
    private HashMap<String, ArrayList<HashMap>> getResumecodeitem() throws GeneralException {
        HashMap<String, ArrayList<HashMap>> codeItemsMap = new HashMap<String, ArrayList<HashMap>>();
        InputStream ip = null;
        try {
            ip = this.getClass().getResourceAsStream("/com/hjsj/hrms/module/recruitment/thirdpartyresume/config/"
                    + this.getThirdPartyName().toLowerCase() + "_resume_codeitem.xml");
            if(ip == null)
                return null;
            
            Document doc = PubFunc.generateDom(ip);
            XPath xPath = XPath.newInstance("/codeitems");
            Element codesets = (Element) xPath.selectSingleNode(doc);

            if (codesets != null) {
                List list = (List) codesets.getChildren();
                for (int i = 0; i < list.size(); i++) {
                    Element element = (Element) list.get(i);
                    String codesetid = element.getAttributeValue("codesetid");
                    List childList = element.getChildren();
                    String codeSetDesc = "";
                    if("0/4".equals(codesetid))
                        codeSetDesc = " ";
                    
                    ArrayList<HashMap> codeItemList = readResumecodeitem(childList, codeSetDesc);
                    codeItemsMap.put(codesetid, codeItemList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            if(ip != null)
                PubFunc.closeResource(ip);
        }
        return codeItemsMap;
    }
    /**
     * 解析代码项的子节点中的信息
     * @param list 代码项的子节点
     * @param parentName 代码项的名称
     * @param codeItemList 保存解析后的代码项信息
     * @throws GeneralException
     */
    private ArrayList<HashMap> readResumecodeitem(List list, String parentName)
        throws GeneralException {
        ArrayList<HashMap> codeItemList = new ArrayList<HashMap>();
        try {
            if (list != null) {
                String cities = ",北京市,天津市,上海市,重庆市,";
                for (int i = 0; i < list.size(); i++) {
                    HashMap codeItemMap = new HashMap();
                    Element element = (Element) list.get(i);
                    String codesetid = element.getAttributeValue("codeitemid");
                    String codeitemdesc = element.getAttributeValue("codeitemdesc");
                    if(StringUtils.isNotEmpty(parentName) && parentName.length() > 1)
                        codeitemdesc = parentName + codeitemdesc;
                    
                    codeItemMap.put(codesetid, codeitemdesc);
                    
                    List childList = element.getChildren();
                    if(childList != null && childList.size() > 0) {
                        String itemDesc = "";
                        if((StringUtils.isNotEmpty(parentName) && parentName.length() > 1) || cities.indexOf("," + codeitemdesc + ",") == -1)
                            itemDesc = codeitemdesc.replace("市辖区", "");
                        
                        codeItemMap.put("children", readResumecodeitem(childList, itemDesc));
                    }
                    
                    codeItemList.add(codeItemMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } 
        
        return codeItemList;
    }
    
    /**
     * 去除html标签
     * @param html
     * @return
     * @throws GeneralException
     */
    public String getTextByHtml(String html) throws GeneralException{
        if(StringUtils.isEmpty(html))
            return "";
        
        Parser parser;
        try {
            //不加div标签会报错
            html = "<div>"+html+"</div>";
            parser = new Parser(html);
            TextExtractingVisitor visitor = new  TextExtractingVisitor();
            parser.visitAllNodesWith(visitor);
            return visitor.getExtractedText(); 
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        
    }
    
    private void reloadParam() {
        this.param = null;
        param = getResumeParam();
    }
    

    public String getPlistLog() {
        return PlistLog;
    }

    public void setPlistLog(String plistLog) {
        PlistLog = plistLog;
    }

    public String getClistLog() {
        return ClistLog;
    }

    public void setClistLog(String clistLog) {
        ClistLog = clistLog;
    }

    public String getFlistLog() {
        return FlistLog;
    }

    public void setFlistLog(String flistLog) {
        FlistLog = flistLog;
    }

    public String getBlacklistLog() {
        return blacklistLog;
    }

    public void setBlacklistLog(String blacklistLog) {
        this.blacklistLog = blacklistLog;
    }

    public String getShowInforLog() {
        return showInforLog;
    }

    public void setShowInforLog(String showInforLog) {
        this.showInforLog = showInforLog;
    }
    
    public boolean isImportFlag() {
        return importFlag;
    }

    public void setImportFlag(boolean importFlag) {
        this.importFlag = importFlag;
    }

    /**
     * 判断简历导入是否成功
     * @return
     */
    public abstract String getMsg();
}
