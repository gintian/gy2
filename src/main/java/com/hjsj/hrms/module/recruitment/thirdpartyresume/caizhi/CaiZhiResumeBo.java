package com.hjsj.hrms.module.recruitment.thirdpartyresume.caizhi;

import com.hjsj.hrms.businessobject.hire.TransResumeBo;
import com.hjsj.hrms.module.recruitment.thirdpartyresume.base.ThirdPartyResumeBase;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import org.apache.axis.encoding.Base64;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.*;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CaiZhiResumeBo extends ThirdPartyResumeBase {
    private String czUserName;
    private String czPassword;
    private String czWebServiceUrl;
    // 链接工厂类
    private SOAPConnectionFactory factory = null;
    private SOAPConnection conn = null;
    private MessageFactory reqMsgFactory = null;
    private SOAPMessage reqMsg = null;
    private String mainItem = "";
    private String mainItemValue = "";
    private String secItem = "";
    private String secItemValue = "";
    private String msg;

    private void init() throws GeneralException {
        try {
            factory = SOAPConnectionFactory.newInstance();
            conn = factory.createConnection();
            reqMsgFactory = MessageFactory.newInstance();
            reqMsg = reqMsgFactory.createMessage();

        } catch (UnsupportedOperationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } catch (SOAPException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }

    @Override
    public ArrayList getResumeFromThirdParty(HashMap params) {
        InputStream inputStream = null;
        String name = (String) params.get("fileName");
        String filePath = (String) params.get("filePath");
        String blacklist_field = (String) params.get("blacklist_field");
        OutputStream outputStream = null;
        String blacklist_value = "";

        String nameSpace = "http://tempuri.org/";
        String methodName = "TransResume";

        ZipInputStream is = null;
        BufferedReader br = null;
        BufferedReader bu = null;
        ZipInputStream zs = null;
        int Num = 0; // 简历总数

        try {
            HashMap mapReseme = getResumeParam();
            String PostID = ""; // 简历的应聘岗位

            LazyDynaBean paramBean = (LazyDynaBean) mapReseme.get("thirdPartyParm");
            // 从system中获取简历解析服务地址
            this.czWebServiceUrl = (String) paramBean.get("webserviceAddr");
            if (this.czWebServiceUrl == null || "".equals(this.czWebServiceUrl))
                this.czWebServiceUrl = "http://www.51ats.com/ResumeService.asmx";

            this.czUserName = (String) paramBean.get("userName"); // 获取简历解析服务帐号
            this.czPassword = (String) paramBean.get("passWord");
            LazyDynaBean ThirdParamBean = (LazyDynaBean) mapReseme.get("thirdPartyParm");
            this.mainItem = (String) ThirdParamBean.get("identifyfld");
            this.secItem = (String) ThirdParamBean.get("sencondfld");
            // 从服务器获得zipfile对象并解析
            ZipFile zipFile = new ZipFile(filePath + name); // 根据路径取得需要解压的Zip文件
            zipFile.setFileNameCharset("GBK");

            List fileHeaderList = zipFile.getFileHeaders();
            String ferror = "";// 格式错误
            String contentError = "";// 内容不对应
            ArrayList<LazyDynaBean> fieldsetList = (ArrayList<LazyDynaBean>) mapReseme.get("fieldset");
            HashMap<String, ArrayList<LazyDynaBean>> resumeXmlItem = (HashMap<String, ArrayList<LazyDynaBean>>) mapReseme.get("fielditem");

            for (int i = 0; i < fileHeaderList.size(); i++) {
                FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
                if (fileHeader.isDirectory())// 若是文件夹 continue
                    continue;

                String fileName = "";
                String extention = "";
                if (fileHeader.getFileName().length() > 0 && fileHeader.getFileName() != null) { // --截取文件名
                    int j = fileHeader.getFileName().lastIndexOf(".");
                    int k = fileHeader.getFileName().lastIndexOf("/");

                    if (j > -1 && j < fileHeader.getFileName().length()
                            && !fileHeader.isDirectory()) {
                        fileName = fileHeader.getFileName().substring(k + 1, j); // --文件名
                        extention = fileHeader.getFileName().substring(j + 1); // --扩展名
                    }
                }
                // System.out.println(fileName);
                String jl = "";
                ArrayList ResumeInfoList = new ArrayList(); // 个人简历信息集
                String wordString = "";

                if (!"".equals(fileName) && "txt".equalsIgnoreCase(extention)) { // 解析txt格式的简历
                    is = zipFile.getInputStream(fileHeader);
                    br = new BufferedReader(new InputStreamReader(is));
                    String str;

                    while ((str = br.readLine()) != null) {
                        jl += str;
                    }
                    Num++;

                } else if (!"".equals(fileName) // 解析htm或html格式的简历
                        && ("htm".equalsIgnoreCase(extention) || "html".equalsIgnoreCase(extention))) {
                    is = zipFile.getInputStream(fileHeader);
                    zs = zipFile.getInputStream(fileHeader);
                    // Encoding code = Encoding.GetEncoding("UTF-8");

                    br = new BufferedReader(new InputStreamReader(is));

                    String charset = "utf-8";// 默认使用utf-8编码读取文件
                    String charsetByText = "";// 通过文本前3个字节进行判断，只需要判断UTF-16的这种有可能在soap协议中出错
                    byte[] b = new byte[3];
                    is.read(b);
                    if (b[0] == -17 && b[1] == -69 && b[2] == -65) {// 特殊的三个字节
                        charsetByText = "UTF-8";
                    } else if (b[0] == -1 && b[1] == -2 && b[2] == 60) {// 特殊的三个字节
                        charsetByText = "UTF-16";
                    }
                    String line = "";
                    while ((line = br.readLine()) != null) {
                        // System.out.println(line);
                        if (line.contains("<meta") && line.contains("charset")) {
                            if (line.contains("UTF-8") || line.contains("utf-8")) {
                                charset = "utf-8";
                                break;
                            } else if (line.contains("gbk") || line.contains("GBK")) {
                                charset = "gbk";
                                break;
                            } else if (line.contains("gb2312") || line.contains("GB2312")) {
                                charset = "gb2312";
                                break;
                            }
                        }
                    }
                    if ("UTF-16".equalsIgnoreCase(charsetByText)) {// 需要判断下UTF-16，不能简单的根据html页面上设置走
                        charset = "UTF-16";
                    }
                    bu = new BufferedReader(new InputStreamReader(zs, charset));
                    String str = "";
                    while ((str = bu.readLine()) != null) {
                        jl += str;
                    }
                    jl = TransResumeBo.getPlainText(jl);
                    Num++;

                } else if (!"".equals(fileName) && "mht".equalsIgnoreCase(extention)) {
                    is = zipFile.getInputStream(fileHeader);
                    Session mailSession = Session.getDefaultInstance(System.getProperties(), null);
                    MimeMessage msg = new MimeMessage(mailSession, is);
                    Object content = msg.getContent();
                    if (content instanceof Multipart) {
                        MimeMultipart mp = (MimeMultipart) content;
                        MimeBodyPart bp1 = (MimeBodyPart) mp.getBodyPart(0);

                        // 获取mht文件内容代码的编码
                        String strEncodng = "gb2312";
                        strEncodng = TransResumeBo.getEncoding(bp1);
                        jl = TransResumeBo.getHtmlText(bp1, strEncodng);
                        jl = TransResumeBo.getPlainText(jl);
                    }

                    Num++;

                } else if (!"".equals(fileName)
                        && ("doc".equalsIgnoreCase(extention) || "docx".equalsIgnoreCase(extention))) { // 解析word文档
                    is = zipFile.getInputStream(fileHeader);

                    ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
                    byte[] fileByte = new byte[1024];
                    int len = 0;
                    while ((len = is.read(fileByte)) != -1) {
                        out.write(fileByte, 0, len);
                    }
                    Base64 bin = new Base64();
                    wordString = Base64.encode(out.toByteArray());
                    Num++;
                } else if (!"".equals(fileName) && "pdf".equalsIgnoreCase(extention)) { // 解析pdf文档

                    is = zipFile.getInputStream(fileHeader);

                    PDFParser parser = new PDFParser(is);

                    parser.parse();

                    PDDocument document = parser.getPDDocument();

                    PDFTextStripper stripper = new PDFTextStripper();
                    jl = stripper.getText(document);

                    Num++;
                } else if (!fileHeader.isDirectory()) {
                    // 格式错误
                    ferror += fileName + "." + extention + ";";
                    Num++;
                    continue;
                }

                if (jl != null && jl.length() > 0) {
                    NodeList list = getResumeList(jl, fileName + "." + extention); // 得到解析服务放回的节点集
                    ArrayList ReturnInfoList = ProceedReturnInfo(list); // 处理节点集并返回解析指标集下指标的值的集合
                    getResumeList(ReturnInfoList, fieldsetList, resumeXmlItem, PostID, blacklist_value,
                            ResumeInfoList, blacklist_field);
                    // 增加简历信息
                    if (ResumeInfoList.size() > 0) {
                        HashMap<String, Object> resumeMap = new HashMap<String, Object>();
                        resumeMap.put("ResumeInfoList", ResumeInfoList);
                        resumeMap.put("mainItemValue", this.mainItemValue);
                        resumeMap.put("secItemValue", this.secItemValue);
                        addResunme(resumeMap);
                    } else {
                        if (this.getFlistLog().indexOf(fileName) == -1) {
                            contentError += fileName + "." + extention + ";";
                        }

                    }
                } else if (wordString.trim().length() > 0) {
                    NodeList list = getResumeListForByteFile(wordString,
                            fileName + "." + extention, extention); // 得到解析服务放回的节点集
                    ArrayList ReturnInfoList = ProceedReturnInfoForFile(list); // 处理节点集并返回解析指标集下指标的值的集合
                    getResumeList(ReturnInfoList, fieldsetList,resumeXmlItem , PostID, blacklist_value,
                            ResumeInfoList, blacklist_field);
                    // 增加简历信息
                    if (ResumeInfoList.size() > 0) {
                        HashMap<String, Object> resumeMap = new HashMap<String, Object>();
                        resumeMap.put("ResumeInfoList", ResumeInfoList);
                        resumeMap.put("mainItemValue", this.mainItemValue);
                        resumeMap.put("secItemValue", this.secItemValue);
                        addResunme(resumeMap);
                    } else {
                        if (this.getFlistLog().indexOf(fileName) == -1) {
                            contentError += fileName + "." + extention + ";";
                        }
                    }
                } else {
                    if (this.getFlistLog().indexOf(fileName) == -1)
                        contentError += fileName + "." + extention + ";";
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bu != null) 
                    bu.close();
                
                if (br != null) 
                    br.close();
                
                if (inputStream != null) 
                    inputStream.close();
                
                if (outputStream != null) 
                    PubFunc.closeIoResource(outputStream);

                if (is != null) 
                    is.close(true);
                    
                if (zs != null) 
                    zs.close(true);

            } catch (IOException e) {
                // e.printStackTrace(); 关闭流时出的错误
            }
        }

        return null;
    }

    /**
     * 
     * @Title: getResumeList 获得简历解析人员的信息List
     * @Description: TODO
     * @param ReturnInfoList
     *            才智创新返还回来的人员信息list
     * @param menuList
     *            简历指标对应List
     * @param PostID
     * @param blacklist_value
     *            黑名单指标值
     * @param ResumeInfoList
     *            当前解析的人员信息List
     * @param blacklist_field
     *            黑名单人员指标
     * @throws
     */
    public void getResumeList(ArrayList ReturnInfoList, ArrayList fieldsetList,
            HashMap<String, ArrayList<LazyDynaBean>> resumeXmlItem, String PostID,
            String blacklist_value, ArrayList ResumeInfoList, String blacklist_field) {
        for (int j = 0; j < ReturnInfoList.size(); j++) {
            HashMap hm = (HashMap) ReturnInfoList.get(j);
            
            for (int i = 0; i < fieldsetList.size(); i++) {
                LazyDynaBean bean = (LazyDynaBean) fieldsetList.get(i);
                String resumeSet = (String) bean.get("resumeset");
                String resumesetId = (String) bean.get("resumesetId");
                String ehrset = (String) bean.get("ehrset");
                if (StringUtils.isEmpty(ehrset))
                    continue;
                
                if (((String) hm.get("setid")).equalsIgnoreCase(resumeSet) && ((String) hm.get("setid")).equalsIgnoreCase(resumesetId)) 
                    continue;

                ArrayList<LazyDynaBean> menuList = resumeXmlItem.get(resumeSet);
                for (int k = 0; k < menuList.size(); k++) {
                    LazyDynaBean itembean = (LazyDynaBean) menuList.get(k);
                    String resumefld = (String) itembean.get("resumefld");
                    String ehrfld = (String) itembean.get("ehrfld");
                    String resumefldid = (String) itembean.get("resumefldid");
                    if (StringUtils.isEmpty(ehrfld))
                        continue;

                    FieldItem fi = DataDictionary.getFieldItem(ehrfld);
                    // 判断setid是否相等,若为空则为基本信息
                    if (((String) hm.get("setid")).equalsIgnoreCase((String) fi.getFieldsetid())) {// 相对应的指标集
                        if (hm.containsKey(ehrfld)) {// 得到相对应的指标
                            if ("应聘岗位".equals(itembean.get("resumefld")))
                                PostID = (String) hm.get(ehrfld);

                            if (StringUtils.isNotEmpty(this.mainItem)
                                    && this.mainItem.equalsIgnoreCase((String) ehrfld))
                                this.mainItemValue = (String) hm.get(ehrfld);

                            if (StringUtils.isNotEmpty(this.secItem)
                                    && this.secItem.equalsIgnoreCase((String) ehrfld))
                                this.secItemValue = (String) hm.get(ehrfld);

                            String value = (String) hm.get(ehrfld);

                            if ("D".equalsIgnoreCase(fi.getItemtype())
                                    && StringUtils.isNotEmpty(value)
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

                            // 判断指标是否导入
                            LazyDynaBean ResumeBean = new LazyDynaBean();
                            ResumeBean.set("itemtype", fi.getItemtype());
                            ResumeBean.set("itemlength", fi.getItemlength());
                            ResumeBean.set("ehrfld", ehrfld);
                            ResumeBean.set("resumefld", resumefld);
                            ResumeBean.set("resumeset", resumeSet);
                            ResumeBean.set("itemformat", fi.getFormat());
                            ResumeBean.set("setid", fi.getFieldsetid());
                            ResumeBean.set("i9999", hm.get("i9999"));
                            ResumeBean.set("value", value);
                            // 若指标等于黑名单指标
                            if (ehrfld != null && ehrfld.equalsIgnoreCase(blacklist_field))
                                blacklist_value = value;

                            ResumeInfoList.add(ResumeBean);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected String getThirdPartyName() {
        return "CaiZhi";
    }

    /**
     * 
     * @Title: getResumeList
     * @Description: TODO返回解析简历节点集
     * @param jl
     *            简历内容的字符串
     * @return NodeList
     * @throws GeneralException
     * @throws
     */
    public NodeList getResumeList(String jl, String fileName) throws GeneralException {

        NodeList list = null;
        try {

            StringBuffer strXml = new StringBuffer();
            jl = jl.replaceAll("\r", " ");
            jl = jl.replaceAll("\n", " ");
            jl = jl.replaceAll("&", " 与 ");
            strXml.append("<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' ");
            strXml.append("xmlns:tem='http://tempuri.org/'>");
            strXml.append("<soapenv:Header/>");
            strXml.append("<soapenv:Body>");
            strXml.append("<tem:TransResume>");
            strXml.append("<tem:username>" + this.czUserName + "</tem:username>");
            strXml.append("<tem:pwd>" + this.czPassword + "</tem:pwd>");
            strXml.append("<tem:original><![CDATA[" + jl + "]]></tem:original>");
            strXml.append("</tem:TransResume>");
            strXml.append("</soapenv:Body>");
            strXml.append("</soapenv:Envelope>");
            String xmlString = strXml.toString();
            xmlString = xmlString.replaceAll("[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]", "");
            // 这些错误的发生是由于一些不可见的特殊字符的存在，而这些字符对于XMl文件来说又是非法的，
            // 所以XML解析器在解析时会发生异常，官方定义了XML的无效字符分为三段0x00 - 0x08 0x0b - 0x0c 0x0e -
            // 0x1f 防止xml编码出错
            Reader reader = new StringReader(xmlString);
            Source source = new StreamSource(reader);
            SOAPMessage reqMsg1 = reqMsgFactory.createMessage();
            SOAPPart soapPart = reqMsg1.getSOAPPart();
            soapPart.setContent(source);
            SOAPEnvelope envelope = soapPart.getEnvelope();
            envelope.setPrefix("soapenv");
            envelope.setAttribute("xmlns:urn", "urn:DefaultNamespace");
            envelope.getHeader().setPrefix("soapenv");
            SOAPBody soapBody = envelope.getBody();
            soapBody.setPrefix("soapenv");
            URL cUrl = new URL(this.czWebServiceUrl);
            SOAPMessage respMsg = conn.call(reqMsg1, cUrl);
            list = respMsg.getSOAPBody().getChildNodes();

        } catch (Exception e) {
            e.printStackTrace();
            String flistLog = this.getFlistLog();
            flistLog = flistLog + fileName + ";";
            this.setFlistLog(flistLog);
        }
        return list;

    }

    /**
     * 
     * @Title: ProceedReturnInfo
     * @Description: TODO解析简历节点集
     * @param list
     * @return 指标集下指标项对应的值
     * @throws GeneralException
     *             ArrayList
     * @throws
     */
    public ArrayList ProceedReturnInfo(NodeList list) throws GeneralException {
        ArrayList ReturnInfoList = new ArrayList();
        try {
            if (list != null) {

                for (int i = 0; i < list.getLength(); i++) {
                    Node node = (Node) list.item(i);
                    if ("TransResumeResponse".equals(node.getNodeName())) {

                        Node resultnode = node.getChildNodes().item(0);
                        if (resultnode != null
                                && "TransResumeResult".equals(resultnode.getNodeName())) {
                            for (int j = 0; j < resultnode.getChildNodes().getLength(); j++) {
                                Node childNode = resultnode.getChildNodes().item(j);
                                if ("EducationInfo".equals(childNode.getNodeName())) { // 教育信息
                                    for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
                                        HashMap hm = new HashMap();
                                        Node EduNode = childNode.getChildNodes().item(k);
                                        for (int x = 0; x < EduNode.getChildNodes().getLength(); x++) {
                                            int i9999 = k + 1;
                                            hm.put("setid", childNode.getNodeName());

                                            Node xNode = EduNode.getChildNodes().item(x);
                                            hm.put(xNode.getNodeName(), xNode.getTextContent());
                                            hm.put("i9999", String.valueOf(i9999));
                                        }
                                        ReturnInfoList.add(hm);
                                    }
                                } else if ("ExperienceInfo".equals(childNode.getNodeName())) { // 工作经历
                                    for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
                                        HashMap hm = new HashMap();
                                        Node EduNode = childNode.getChildNodes().item(k);
                                        for (int x = 0; x < EduNode.getChildNodes().getLength(); x++) {
                                            int i9999 = k + 1;
                                            hm.put("setid", childNode.getNodeName());
                                            Node xNode = EduNode.getChildNodes().item(x);
                                            hm.put(xNode.getNodeName(), xNode.getTextContent());
                                            hm.put("i9999", String.valueOf(i9999));
                                        }
                                        ReturnInfoList.add(hm);
                                    }
                                } else if ("TrainingInfo".equals(childNode.getNodeName())) { // 培训经历
                                    for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
                                        HashMap hm = new HashMap();
                                        Node EduNode = childNode.getChildNodes().item(k);
                                        for (int x = 0; x < EduNode.getChildNodes().getLength(); x++) {
                                            int i9999 = k + 1;
                                            hm.put("setid", childNode.getNodeName());
                                            Node xNode = EduNode.getChildNodes().item(x);
                                            hm.put(xNode.getNodeName(), xNode.getTextContent());
                                            hm.put("i9999", String.valueOf(i9999));
                                        }
                                        ReturnInfoList.add(hm);
                                    }
                                } else if ("ProjectInfo".equals(childNode.getNodeName())) { // 项目经验
                                    for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
                                        HashMap hm = new HashMap();
                                        Node EduNode = childNode.getChildNodes().item(k);
                                        for (int x = 0; x < EduNode.getChildNodes().getLength(); x++) {
                                            int i9999 = k + 1;
                                            hm.put("setid", childNode.getNodeName());
                                            Node xNode = EduNode.getChildNodes().item(x);
                                            hm.put(xNode.getNodeName(), xNode.getTextContent());
                                            hm.put("i9999", String.valueOf(i9999));
                                        }
                                        ReturnInfoList.add(hm);
                                    }
                                } else if ("GradeOfEnglish".equals(childNode.getNodeName())) { // 英语等级
                                    HashMap hm = new HashMap();
                                    for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {

                                        Node EduNode = childNode.getChildNodes().item(k);

                                        int i9999 = k + 1;
                                        hm.put("setid", childNode.getNodeName());
                                        hm.put(EduNode.getNodeName(), EduNode.getTextContent());
                                        hm.put("i9999", String.valueOf(i9999));
                                    }
                                    ReturnInfoList.add(hm);
                                } else if ("LanguagesSkills".equals(childNode.getNodeName())) { // 语言能力
                                    for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
                                        HashMap hm = new HashMap();
                                        Node EduNode = childNode.getChildNodes().item(k);
                                        for (int x = 0; x < EduNode.getChildNodes().getLength(); x++) {
                                            int i9999 = k + 1;
                                            hm.put("setid", childNode.getNodeName());
                                            Node xNode = EduNode.getChildNodes().item(x);
                                            hm.put(xNode.getNodeName(), xNode.getTextContent());
                                            hm.put("i9999", String.valueOf(i9999));
                                        }
                                        ReturnInfoList.add(hm);
                                    }
                                } else if ("ITSkills".equals(childNode.getNodeName())) { // IT技能
                                    for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
                                        HashMap hm = new HashMap();
                                        Node EduNode = childNode.getChildNodes().item(k);
                                        for (int x = 0; x < EduNode.getChildNodes().getLength(); x++) {
                                            int i9999 = k + 1;
                                            hm.put("setid", childNode.getNodeName());
                                            Node xNode = EduNode.getChildNodes().item(x);
                                            hm.put(xNode.getNodeName(), xNode.getTextContent());
                                            hm.put("i9999", String.valueOf(i9999));
                                        }
                                        ReturnInfoList.add(hm);
                                    }
                                } else { // 基本类型
                                    HashMap hm = new HashMap();
                                    hm.put("setid", "");
                                    hm.put("i9999", "1");
                                    // System.out.println(childNode.getNodeName()+"1"+childNode.getTextContent());
                                    hm.put(childNode.getNodeName(), childNode.getTextContent());
                                    ReturnInfoList.add(hm);
                                }
                            }
                        }
                    }

                }
            }
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(new Exception("你的帐号或密码错误,请重新输入服务密码!"));
        }

        return ReturnInfoList;
    }

    /**
     * @param extention
     *            文件类型
     * @Title: getResumeListForByteFile
     * @Description: TODO
     * @param wordBuffer
     *            Base64字节码文件
     * @param string
     *            文件名字
     * @return NodeList
     * @throws
     */
    public NodeList getResumeListForByteFile(String wordString, String fileName, String extention) {
        fileName = fileName + "." + extention;

        NodeList list = null;
        try {

            StringBuffer strXml = new StringBuffer();
            strXml.append("<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' ");
            strXml.append("xmlns:tem='http://tempuri.org/'>");
            strXml.append("<soapenv:Header/>");
            strXml.append("<soapenv:Body>");
            strXml.append("<tem:TransResumeByXmlStringForFile>");
            strXml.append("<tem:username>" + this.czUserName + "</tem:username>");
            strXml.append("<tem:pwd>" + this.czPassword + "</tem:pwd>");
            strXml.append("<tem:content>" + wordString + "</tem:content>");
            strXml.append("<tem:ext>" + "." + extention.toUpperCase() + "</tem:ext>");
            strXml.append("</tem:TransResumeByXmlStringForFile>");
            strXml.append("</soapenv:Body>");
            strXml.append("</soapenv:Envelope>");
            Reader reader = new StringReader(strXml.toString());
            Source source = new StreamSource(reader);
            SOAPMessage reqMsg1 = reqMsgFactory.createMessage();
            SOAPPart soapPart = reqMsg1.getSOAPPart();
            soapPart.setContent(source);
            SOAPEnvelope envelope = soapPart.getEnvelope();
            envelope.setPrefix("soapenv");
            envelope.setAttribute("xmlns:urn", "urn:DefaultNamespace");
            envelope.getHeader().setPrefix("soapenv");
            SOAPBody soapBody = envelope.getBody();
            soapBody.setPrefix("soapenv");
            URL cUrl = new URL(this.czWebServiceUrl);
            SOAPMessage respMsg = conn.call(reqMsg1, cUrl);

            list = respMsg.getSOAPBody().getChildNodes();

        } catch (Exception e) {
            e.printStackTrace();
            String flistLog = this.getFlistLog();
            flistLog = flistLog + fileName + ";";
            this.setFlistLog(flistLog);
        }
        return list;
    }

    /**
     * 
     * @Title: ProceedReturnInfoForFile
     * @Description: TODO解析简历节点集
     * @param list
     * @return 指标集下指标项对应的值
     * @throws GeneralException
     *             ArrayList
     * @throws
     */
    public ArrayList ProceedReturnInfoForFile(NodeList list) throws GeneralException {
        ArrayList ReturnInfoList = new ArrayList();

        try {
            if (list != null) {
                for (int i = 0; i < list.getLength(); i++) {
                    Node node = (Node) list.item(i);
                    if ("TransResumeByXmlStringForFileResponse".equals(node.getNodeName())) {
                        Node resultnode = node.getChildNodes().item(0);
                        if (resultnode != null
                                && "TransResumeByXmlStringForFileResult".equals(resultnode.getNodeName())) {
                            Node textNode = resultnode.getChildNodes().item(0);
                            String xmlstr = textNode.getTextContent();
                            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                            DocumentBuilder db = dbf.newDocumentBuilder();
                            Document domTree = db.parse(new InputSource(new StringReader(xmlstr)));
                            Element root = domTree.getDocumentElement();
                            for (int j = 0; j < root.getChildNodes().getLength(); j++) {
                                Node childNode = root.getChildNodes().item(j);
                                if (childNode.getNodeType() != Node.ELEMENT_NODE) 
                                    continue;
                                
                                if ("EducationInfo".equals(childNode.getNodeName())) { // 教育信息
                                    int i9999I = 0;
                                    for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
                                        HashMap hm = new HashMap();
                                        Node EduNode = childNode.getChildNodes().item(k);
                                        if (EduNode.getNodeType() != Node.ELEMENT_NODE) 
                                            continue;
                                        
                                        i9999I++;
                                        for (int x = 0; x < EduNode.getChildNodes().getLength(); x++) {
                                            Node xNode = EduNode.getChildNodes().item(x);
                                            if (xNode.getNodeType() != Node.ELEMENT_NODE) 
                                                continue;
                                            
                                            int i9999 = i9999I;
                                            hm.put("setid", childNode.getNodeName());
                                            hm.put(xNode.getNodeName(), xNode.getTextContent());
                                            hm.put("i9999", String.valueOf(i9999));

                                        }
                                        ReturnInfoList.add(hm);
                                    }
                                } else if ("ExperienceInfo".equals(childNode.getNodeName())) { // 工作经历
                                    int i9999I = 0;
                                    for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
                                        HashMap hm = new HashMap();
                                        Node EduNode = childNode.getChildNodes().item(k);
                                        if (EduNode.getNodeType() != Node.ELEMENT_NODE) 
                                            continue;
                                        
                                        i9999I++;
                                        for (int x = 0; x < EduNode.getChildNodes().getLength(); x++) {
                                            Node xNode = EduNode.getChildNodes().item(x);
                                            if (xNode.getNodeType() != Node.ELEMENT_NODE) 
                                                continue;
                                            
                                            int i9999 = i9999I;
                                            hm.put("setid", childNode.getNodeName());

                                            hm.put(xNode.getNodeName(), xNode.getTextContent());
                                            hm.put("i9999", String.valueOf(i9999));

                                        }
                                        ReturnInfoList.add(hm);
                                    }
                                } else if ("TrainingInfo".equals(childNode.getNodeName())) { // 培训经历
                                    int i9999I = 0;
                                    for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
                                        HashMap hm = new HashMap();
                                        Node EduNode = childNode.getChildNodes().item(k);
                                        if (EduNode.getNodeType() != Node.ELEMENT_NODE) 
                                            continue;
                                        
                                        i9999I++;
                                        for (int x = 0; x < EduNode.getChildNodes().getLength(); x++) {
                                            Node xNode = EduNode.getChildNodes().item(x);
                                            if (xNode.getNodeType() != Node.ELEMENT_NODE) 
                                                continue;
                                            
                                            int i9999 = i9999I;
                                            hm.put("setid", childNode.getNodeName());
                                            hm.put(xNode.getNodeName(), xNode.getTextContent());
                                            hm.put("i9999", String.valueOf(i9999));

                                        }
                                        ReturnInfoList.add(hm);
                                    }
                                } else if ("ProjectInfo".equals(childNode.getNodeName())) { // 项目经验
                                    int i9999I = 0;
                                    for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
                                        HashMap hm = new HashMap();
                                        Node EduNode = childNode.getChildNodes().item(k);
                                        if (EduNode.getNodeType() != Node.ELEMENT_NODE) 
                                            continue;
                                        
                                        i9999I++;
                                        for (int x = 0; x < EduNode.getChildNodes().getLength(); x++) {
                                            Node xNode = EduNode.getChildNodes().item(x);
                                            if (xNode.getNodeType() != Node.ELEMENT_NODE) 
                                                continue;
                                            
                                            int i9999 = i9999I;
                                            hm.put("setid", childNode.getNodeName());
                                            hm.put(xNode.getNodeName(), xNode.getTextContent());
                                            hm.put("i9999", String.valueOf(i9999));

                                        }
                                        ReturnInfoList.add(hm);
                                    }
                                } else if ("GradeOfEnglish".equals(childNode.getNodeName())) { // 英语等级
                                    HashMap hm = new HashMap();
                                    int i9999I = 0;
                                    for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {

                                        Node EduNode = childNode.getChildNodes().item(k);
                                        if (EduNode.getNodeType() != Node.ELEMENT_NODE) 
                                            continue;
                                        
                                        i9999I++;
                                        int i9999 = i9999I;
                                        hm.put("setid", childNode.getNodeName());
                                        hm.put(EduNode.getNodeName(), EduNode.getTextContent());
                                        hm.put("i9999", String.valueOf(i9999));

                                    }
                                    ReturnInfoList.add(hm);
                                } else if ("LanguagesSkills".equals(childNode.getNodeName())) { // 语言能力
                                    int i9999I = 0;
                                    for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
                                        HashMap hm = new HashMap();
                                        Node EduNode = childNode.getChildNodes().item(k);
                                        if (EduNode.getNodeType() != Node.ELEMENT_NODE) 
                                            continue;
                                        
                                        i9999I++;
                                        for (int x = 0; x < EduNode.getChildNodes().getLength(); x++) {
                                            Node xNode = EduNode.getChildNodes().item(x);
                                            if (xNode.getNodeType() != Node.ELEMENT_NODE) 
                                                continue;
                                            
                                            int i9999 = i9999I;
                                            hm.put("setid", childNode.getNodeName());
                                            hm.put(xNode.getNodeName(), xNode.getTextContent());
                                            hm.put("i9999", String.valueOf(i9999));

                                        }
                                        ReturnInfoList.add(hm);
                                    }
                                } else if ("ITSkills".equals(childNode.getNodeName())) { // IT技能
                                    int i9999I = 0;
                                    for (int k = 0; k < childNode.getChildNodes().getLength(); k++) {
                                        HashMap hm = new HashMap();
                                        Node EduNode = childNode.getChildNodes().item(k);
                                        if (EduNode.getNodeType() != Node.ELEMENT_NODE) 
                                            continue;
                                        
                                        i9999I++;
                                        for (int x = 0; x < EduNode.getChildNodes().getLength(); x++) {
                                            Node xNode = EduNode.getChildNodes().item(x);
                                            if (xNode.getNodeType() != Node.ELEMENT_NODE) 
                                                continue;
                                            
                                            int i9999 = i9999I;
                                            hm.put("setid", childNode.getNodeName());
                                            hm.put(xNode.getNodeName(), xNode.getTextContent());
                                            hm.put("i9999", String.valueOf(i9999));
                                        }
                                        ReturnInfoList.add(hm);
                                    }
                                } else { // 基本类型
                                    HashMap hm = new HashMap();
                                    hm.put("setid", "");
                                    hm.put("i9999", "1");
                                    hm.put(childNode.getNodeName(), childNode.getTextContent());
                                    ReturnInfoList.add(hm);
                                }
                            }
                        }
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new Exception("你的帐号或密码错误,请重新输入服务密码!"));
        }

        return ReturnInfoList;
    }
    
    @Override
    public String getMsg() {
        return msg;
    }
}
