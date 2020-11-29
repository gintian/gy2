package com.hjsj.hrms.module.selfservice.usercenter.businessobject.impl;

import com.aspose.words.Document;
import com.hjsj.hrms.module.card.businessobject.YkcardOutWord;
import com.hjsj.hrms.module.selfservice.usercenter.businessobject.IUserCenterService;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.RowSet;
import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserCenterServiceImpl implements IUserCenterService {
    private Logger log = LoggerFactory.getLogger(UserCenterServiceImpl.class);
    private Connection conn;
    private UserView userView;

    public UserCenterServiceImpl(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
    }
    @Override
    public Map getFileNameMap(String cardType, String a0100, String nbase) throws GeneralException {
        Map fileNameMap = new HashMap();
        try{
            //编号（部门编号，岗位编号，人员编号）
            String nid = "";
            //模板号
            int tabid = 0;
            //用户权限 selfinfo:自助用户  noinfo:业务用户
            String userpriv = "selfinfo";
            if (StringUtils.isBlank(this.userView.getA0100())){
                userpriv = "noinfo";
            }

            String bdName = this.userView.getDbname();
            if ("employee".equalsIgnoreCase(cardType)) {
                cardType = "1";
                nid = a0100;
                bdName = nbase;
                tabid = this.getTabid("SYS_OTH_PARAM");
            } else if ("dept".equalsIgnoreCase(cardType)) {
                cardType = "2";
                if (StringUtils.isNotBlank(a0100)) {
                    nid = this.getPersonE0122AndE01a1(a0100, nbase).split("`")[0];
                }else {
                    nid = this.userView.getUserDeptId();
                }
                tabid = this.getTabid("ZP_UNIT_TEMPLATE");
            } else if ("post".equalsIgnoreCase(cardType)) {
                cardType = "4";
                if (StringUtils.isNotBlank(a0100)) {
                    nid = this.getPersonE0122AndE01a1(a0100, nbase).split("`")[1];
                }else {
                    nid = this.userView.getUserPosId();
                }
                tabid = this.getTabid("ZP_POS_TEMPLATE");
            }
            if (tabid != 0) {
                if(!"1".equalsIgnoreCase(cardType)){
                    ContentDAO dao = new ContentDAO(this.conn);
                    List sqlList = new ArrayList();
                    sqlList.add(this.userView.getUserName());
                    sqlList.add(StringUtils.equalsIgnoreCase("2", cardType) ? "UM" : "K");
                    sqlList.add(nid);
                    sqlList.add(cardType);
                    //用户状态  0：业务用户 4：自助用户
                    sqlList.add(this.userView.getStatus());
                    dao.delete("delete from t_card_result where username=? and nbase=? and objid=? and flag=? and status=?", sqlList);
                    dao.insert("insert into t_card_result (username, nbase, objid, flag, status) values (?, ?, ?, ?, ?)", sqlList);
                }

                YkcardOutWord outWord = new YkcardOutWord(this.userView, this.conn);
                String wordFileName = outWord.outWordYkcard(tabid, nid, "0", cardType, bdName, userpriv, userpriv, null);
                String filePath = System.getProperty("java.io.tmpdir") + File.separator + wordFileName;
                log.info("UserCenterServiceImpl::filePath:{},wordFileName:{}",filePath,wordFileName);
                String pdfFileName = this.wordToPdf(filePath, wordFileName);
                log.info("UserCenterServiceImpl::pdfFileName:{}",pdfFileName);
                fileNameMap.put("pdfFileName", PubFunc.encrypt(pdfFileName));
                fileNameMap.put("wordFileName", PubFunc.encrypt(wordFileName));
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new GeneralException("");
        }
        return fileNameMap;
    }
    /**
     *
     * @Author sheny
     * @param
     * @return
     * @throws GeneralException 异常信息
     * @Date 2020/5/9 14:19
     */
    private int getTabid(String constant) throws GeneralException {
        RowSet frowset = null;
        int tabid = 0;
        ContentDAO dao=new ContentDAO(conn);
        try {
            String sql="select str_value from constant where upper(constant)='"+constant+"'";
            frowset=dao.search(sql);
            if(frowset.next()){
                String tabidStr = frowset.getString("str_value");
                if ("SYS_OTH_PARAM".equalsIgnoreCase(constant)){
                    tabid = this.getTabidEmp(tabidStr);
                } else if(StringUtils.isNotEmpty(tabidStr)){
                    tabid = Integer.parseInt(tabidStr);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
            throw new GeneralException("getTabidError");
        }finally {
            PubFunc.closeDbObj(frowset);
        }
        return tabid;
    }

    private int getTabidEmp(String xmlcontent) throws GeneralException {
        int tabid=0;
        org.jdom.Document doc = this.Sys_Oth_Parameter(xmlcontent);
        try{
            String str_path="/param/browser_card";
            XPath xpath=XPath.newInstance(str_path);
            List childlist=xpath.selectNodes(doc);
            Element element=null;
            if(childlist.size()!=0){
                element=(Element)childlist.get(0);
                String value=element.getAttributeValue("emp");
                if (StringUtils.isNotEmpty(value)){
                    tabid = Integer.parseInt(value);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return tabid;
    }

    private org.jdom.Document Sys_Oth_Parameter(String xmlcontent) throws GeneralException {
        org.jdom.Document doc = null;
        StringBuffer strxml=new StringBuffer();
        strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
        strxml.append("<param>");
        strxml.append("</param>");
        if(xmlcontent==null|| "".equals(xmlcontent)){
            xmlcontent=strxml.toString();
        }
        try{
            doc= PubFunc.generateDom(xmlcontent.toString());
        }catch(Exception e){
            e.printStackTrace();
        }
        return doc;
    }

    /**
     * @param filePath
     * @param url      word文件名称
     * @return String url pdf文件名称
     * @throws GeneralException 异常信息
     * @Date 2020/5/9 13:55
     */
    private String wordToPdf(String filePath, String url) throws Exception {
        Document doc = new Document(filePath);
        int lastindex = url.lastIndexOf(".");
        url = url.substring(0, lastindex) + ".pdf";
        doc.save(System.getProperty("java.io.tmpdir") + File.separator + url);
        return url;
    }

    /**
     * 获取人员部门及岗位代码值
     * @author wangbs
     * @param a0100 人员编号
     * @param nbase 人员库前缀
     * @return java.lang.String
     * @throws GeneralException 抛出异常
     * @date 2020/6/9
     */
    private String getPersonE0122AndE01a1(String a0100, String nbase) throws GeneralException{
        String E0122AndE01a1 = "";
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.conn);
        try{
            String sql = "select e0122,e01a1 from " + nbase + "A01 where a0100='" + a0100 + "'";
            rs = dao.search(sql);
            if (rs.next()) {
                E0122AndE01a1 = rs.getString("e0122") + "`" + rs.getString("e01a1");
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new GeneralException("");
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return E0122AndE01a1;
    }


    @Override
    public HashMap<String,String> getBtnFunction(String cardType){
        HashMap btnFuncMap = new HashMap();
        boolean pdfBtn = false;
        boolean wordBtn = false;
        //岗位说明书dept
        if (this.userView.isSuper_admin()) {
            pdfBtn = true;
            wordBtn = true;
        } else if (StringUtils.equalsIgnoreCase("employee", cardType)) {
            //人员登记表
            if (this.userView.hasTheFunction("2604001") || this.userView.hasTheFunction("0315001")) {
                pdfBtn = true;
            }
            if (this.userView.hasTheFunction("2604002") || this.userView.hasTheFunction("0315002")) {
                wordBtn = true;
            }
        } else if (StringUtils.equalsIgnoreCase("dept", cardType)) {
            if (userView.hasTheFunction("2304001")) {
                pdfBtn = true;
            }
            if (userView.hasTheFunction("2304002")) {
                wordBtn = true;
            }
        } else if (StringUtils.equalsIgnoreCase("post", cardType)) {
            if (userView.hasTheFunction("2504001")) {
                pdfBtn = true;
            }
            if (userView.hasTheFunction("2504002")) {
                wordBtn = true;
            }
        }
        btnFuncMap.put("pdfBtn", pdfBtn);
        btnFuncMap.put("wordBtn", wordBtn);
        return btnFuncMap;
    }
}
