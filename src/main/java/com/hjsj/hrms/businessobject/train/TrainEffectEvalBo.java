package com.hjsj.hrms.businessobject.train;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * <p>
 * Title:TrainEffectEvalBo.java
 * </p>
 * <p>
 * Description:培训效果评估
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-08-27 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class TrainEffectEvalBo {
    private Connection conn = null;

    private String classid;

    private String endDate;

    private String type;

    private Document doc;

    public TrainEffectEvalBo() {

    }

    public TrainEffectEvalBo(Connection con, String classid, String type) {

        this.conn = con;
        this.classid = classid;
        this.type = type;
    }
    
    public TrainEffectEvalBo(Connection con, String classid) {

        this.conn = con;
        this.classid = classid;
    }

    /* 获得培训效果评估XML */
    public String getXML() {
        String content = "";
        String sqlStr = "SELECT ctrl_param," + Sql_switcher.isnull("R3116", Sql_switcher.dateValue(PubFunc.getStringDate("yyyy-MM-dd"))) + " R3116 FROM R31 WHERE R3101='" + this.classid + "'";

        try {
            ContentDAO dao = new ContentDAO(this.conn);
            RowSet rs = dao.search(sqlStr);
            if (rs.next()) {
                content = Sql_switcher.readMemo(rs, "ctrl_param");
                endDate = rs.getDate("R3116").toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }
    
    public ArrayList getEffectEvalXML() {
        
        ArrayList list = new ArrayList();

        try {
            String content = getXML();
            if (content != null && content.length() > 0) {
                this.doc = PubFunc.generateDom(content);
            }

            LazyDynaBean templateJ = new LazyDynaBean();
            templateJ.set("title", getTitle("template", "job"));
            if (!"1".equalsIgnoreCase(this.type)) {
                templateJ.set("textSet", getSet("template"));
            }

            String xpath = "//eval[@job_id=\"" + this.classid + "\"]//template[@type=\"job\"]";
            XPath xpath_ = XPath.newInstance(xpath);
            Element ele = null;
            if (doc != null) {
                ele = (Element) xpath_.selectSingleNode(doc);
            }

            if (ele == null) {
                templateJ.set("text", "");
                templateJ.set("run", "0");
                templateJ.set("end_date", endDate);
                list.add(templateJ);
            } else {
                String text = ele.getText();
                text = text == null || text.length() < 1 ? "" : text;
                templateJ.set("text", text);
                templateJ.set("run", "true".equals(ele.getAttributeValue("run")) ? "1" : "0");
                templateJ.set("end_date", ele.getAttributeValue("end_date"));
                list.add(templateJ);
            }

            LazyDynaBean templateT = new LazyDynaBean();
            templateT.set("title", getTitle("template", "teacher"));
            if (!"1".equalsIgnoreCase(this.type)) {
                templateT.set("textSet", getSet("template"));
            }

            xpath = "//eval[@job_id=\"" + this.classid + "\"]//template[@type=\"teacher\"]";
            xpath_ = XPath.newInstance(xpath);
            ele = null;
            if (doc != null) {
                ele = (Element) xpath_.selectSingleNode(doc);
            }

            if (ele == null) {
                templateT.set("text", "");
                templateT.set("run", "0");
                templateT.set("end_date", endDate);
                list.add(templateT);
            } else {
                String text = ele.getText();
                text = text == null || text.length() < 1 ? "" : text;
                templateT.set("text", text);
                templateT.set("run", "true".equals(ele.getAttributeValue("run")) ? "1" : "0");
                templateT.set("end_date", ele.getAttributeValue("end_date"));
                list.add(templateT);
            }

            LazyDynaBean questionnaireJ = new LazyDynaBean();
            questionnaireJ.set("title", getTitle("questionnaire", "job"));
            if (!"1".equalsIgnoreCase(this.type)) {
                questionnaireJ.set("textSet", getSet("questionnaire"));
            }

            xpath = "//eval[@job_id=\"" + this.classid + "\"]//questionnaire[@type=\"job\"]";
            xpath_ = XPath.newInstance(xpath);
            ele = null;
            if (doc != null) {
                ele = (Element) xpath_.selectSingleNode(doc);
            }

            if (ele == null) {
                questionnaireJ.set("text", "");
                questionnaireJ.set("run", "0");
                questionnaireJ.set("end_date", endDate);
                list.add(questionnaireJ);
            } else {
                String text = ele.getText();
                text = text == null || text.length() < 1 ? "" : text;
                questionnaireJ.set("text", text);
                questionnaireJ.set("run", "true".equals(ele.getAttributeValue("run")) ? "1" : "0");
                questionnaireJ.set("end_date", ele.getAttributeValue("end_date"));
                list.add(questionnaireJ);
            }

            LazyDynaBean questionnaireT = new LazyDynaBean();
            questionnaireT.set("title", getTitle("questionnaire", "teacher"));
            if (!"1".equalsIgnoreCase(this.type)) {
                questionnaireT.set("textSet", getSet("questionnaire"));
            }

            xpath = "//eval[@job_id=\"" + this.classid + "\"]//questionnaire[@type=\"teacher\"]";
            xpath_ = XPath.newInstance(xpath);
            ele = null;
            if (doc != null) {
                ele = (Element) xpath_.selectSingleNode(doc);
            }

            if (ele == null) {
                questionnaireT.set("text", "");
                questionnaireT.set("run", "0");
                questionnaireT.set("end_date", endDate);
                list.add(questionnaireT);
            } else {
                String text = ele.getText();
                text = text == null || text.length() < 1 ? "" : text;
                questionnaireT.set("text", text);
                questionnaireT.set("run", "true".equals(ele.getAttributeValue("run")) ? "1" : "0");
                questionnaireT.set("end_date", ele.getAttributeValue("end_date"));
                list.add(questionnaireT);
            }

            LazyDynaBean ctrl_apply = new LazyDynaBean();
            ctrl_apply.set("title", getTitle("ctrl_apply", ""));
            if (!"1".equalsIgnoreCase(this.type)) {
                ctrl_apply.set("textSet", getSet("ctrl_apply"));
            }

            xpath = "//doctrl[@job_id=\"" + this.classid + "\"]//ctrl_apply";
            xpath_ = XPath.newInstance(xpath);
            ele = null;
            if (doc != null) {
                ele = (Element) xpath_.selectSingleNode(doc);
            }

            if (ele == null) {
                ctrl_apply.set("text", "1");
                list.add(ctrl_apply);
            } else {
                String text = ele.getText();
                text = text == null || text.length() < 1 ? "1" : text;
                ctrl_apply.set("text", text);
                list.add(ctrl_apply);
            }

            LazyDynaBean ctrl_count = new LazyDynaBean();
            ctrl_count.set("title", getTitle("ctrl_count", ""));
            if (!"1".equalsIgnoreCase(this.type)) {
                ctrl_count.set("textSet", getSet("ctrl_count"));
            }

            xpath = "//doctrl[@job_id=\"" + this.classid + "\"]//ctrl_count";
            xpath_ = XPath.newInstance(xpath);
            ele = null;
            if (doc != null) {
                ele = (Element) xpath_.selectSingleNode(doc);
            }

            if (ele == null) {
                ctrl_count.set("text", "0");
                list.add(ctrl_count);
            } else {
                String text = ele.getText();
                text = text == null || text.length() < 1 ? "0" : text;
                ctrl_count.set("text", text);
                list.add(ctrl_count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    /*
     * 获得下拉选择的集合
     */
    public ArrayList getSet(String tag) {

        ArrayList list = new ArrayList();
        ContentDAO dao = new ContentDAO(this.conn);
        String sql = "";
        if ((!"ctrl_count".equalsIgnoreCase(tag)) && (!"ctrl_apply".equalsIgnoreCase(tag))) {
            CommonData none = new CommonData("", "");
            list.add(none);
            if ("template".equals(tag)) {
                sql = "select b.template_id id,b.name content from per_template_set a,per_template b where a.template_setid=b.template_setid and  a.subsys_id='20'";
            } else if ("questionnaire".equals(tag)) {
                sql = "select * from (select planid,planname,status,(case when recoverycount is null then 0 else recoverycount end) recoverycount,createuser,createtime qn_createtime,pubtime,b0110,qnid,options,(select count(1) from qn_template where  qnid = qn_plan.qnid) connnumber from qn_plan ) myGridData where status = 1 or  status = 3";
            }

            try {
                RowSet rs = dao.search(sql);
                while (rs.next()) {
                	String id;
                	String content;
                	if ("questionnaire".equals(tag)){
                		id =rs.getString("qnid");
                		id = rs.getString("planid") == null ? "" : (rs.getString("planid") + ":" + rs.getString("qnid"));
                		content = rs.getString("planname") == null ? "" : rs.getString("planname");
                	}else{
                		id = rs.getString("id") == null ? "" : rs.getString("id");
                        content = rs.getString("content") == null ? "" : rs.getString("content");
                	}
                	CommonData temp = new CommonData(id, content);
                    list.add(temp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            CommonData temp = new CommonData("0", ResourceFactory.getProperty("train.info.chang.no"));
            list.add(temp);
            CommonData temp1 = new CommonData("1", ResourceFactory.getProperty("train.info.chang.yes"));
            list.add(temp1);
        }
        return list;
    }

    public String getClassName() {

        String name = "";
        ContentDAO dao = new ContentDAO(this.conn);
        String sql = "select r3130 from r31 where r3101='" + this.classid + "'";
        try {
            RowSet rs = dao.search(sql);
            if (rs.next()) {
                name = rs.getString("r3130") == null ? "" : rs.getString("r3130");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    public String getTitle(String tag, String type) {

        String title = "";
        if ("template".equals(tag) && "job".equals(type)) {
            title = "培训班评估模板";
        } else if ("template".equals(tag) && "teacher".equals(type)) {
            title = "教师评估模板";
        } else if ("questionnaire".equals(tag) && "teacher".equals(type)) {
            title = "教师教学效果调查问卷";
        } else if ("questionnaire".equals(tag) && "job".equals(type)) {
            title = "培训班效果调查问卷";
        } else if ("ctrl_apply".equals(tag) && "".equals(type)) {
            title = "培训班报名是否需要审批";
        } else if ("ctrl_count".equals(tag) && "".equals(type)) {
            title = "培训班报名是否满额控制";
        }
        // else if (tag.equals("checkClass") && type.equals(""))
        // title = "培训班审核是否需要控制";
        return title;
    }

    public void save(LazyDynaBean temJob, LazyDynaBean temTeacher, LazyDynaBean quesJob, LazyDynaBean quesTeacher, LazyDynaBean ctrl_apply, LazyDynaBean ctrl_count) {
        Element root = new Element("param");

        Element eval = new Element("eval");
        eval.setAttribute("job_id", this.classid);
        Element doctrl = new Element("doctrl");
        doctrl.setAttribute("job_id", this.classid);

        Element temJob1 = new Element("template");
        temJob1.setAttribute("type", "job");
        temJob1.setAttribute("run", "1".equals((String) temJob.get("run")) ? "true" : "false");
        temJob1.setAttribute("end_date", (String) temJob.get("end_date"));
        temJob1.setText((String) temJob.get("text"));

        Element temTeacher1 = new Element("template");
        temTeacher1.setAttribute("type", "teacher");
        temTeacher1.setAttribute("run", "1".equals((String) temTeacher.get("run")) ? "true" : "false");
        temTeacher1.setAttribute("end_date", (String) temTeacher.get("end_date"));
        temTeacher1.setText((String) temTeacher.get("text"));

        Element quesJob1 = new Element("questionnaire");
        quesJob1.setAttribute("type", "job");
        quesJob1.setAttribute("run", "1".equals((String) quesJob.get("run")) ? "true" : "false");
        quesJob1.setAttribute("end_date", (String) quesJob.get("end_date"));
        quesJob1.setText((String) quesJob.get("text"));

        Element quesTeacher1 = new Element("questionnaire");
        quesTeacher1.setAttribute("type", "teacher");
        quesTeacher1.setAttribute("run", "1".equals((String) quesTeacher.get("run")) ? "true" : "false");
        quesTeacher1.setAttribute("end_date", (String) quesTeacher.get("end_date"));
        quesTeacher1.setText((String) quesTeacher.get("text"));

        Element ctrl_apply1 = new Element("ctrl_apply");
        ctrl_apply1.setText((String) ctrl_apply.get("text"));

        Element ctrl_count1 = new Element("ctrl_count");
        ctrl_count1.setText((String) ctrl_count.get("text"));

        // Element checkClass1 = new Element("checkClass");
        // checkClass1.setText((String)ctrl_count.get("text"));

        eval.addContent(temJob1);
        eval.addContent(quesJob1);
        eval.addContent(temTeacher1);
        eval.addContent(quesTeacher1);
        doctrl.addContent(ctrl_apply1);
        doctrl.addContent(ctrl_count1);
        // doctrl.addContent(checkClass1);
        root.addContent(eval);
        root.addContent(doctrl);

        Document myDocument = new Document(root);
        XMLOutputter outputter = new XMLOutputter();
        Format format = Format.getPrettyFormat();
        format.setEncoding("UTF-8");
        outputter.setFormat(format);
        String xmlContent = outputter.outputString(myDocument);

        ContentDAO dao = new ContentDAO(this.conn);
        String sql = "";
        try {
            sql = "update r31 set ctrl_param='" + xmlContent + "' where r3101='" + this.classid + "'";
            dao.update(sql);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public LazyDynaBean getBean(String tag, String type)
    {
        String xml = this.getXML();
    LazyDynaBean bean = new LazyDynaBean();
//  初始化操作
    bean.set("title", getTitle(tag, type));
    
    if ("ctrl_count".equalsIgnoreCase(tag)) {
        bean.set("text","0");
    } else if ("ctrl_apply".equalsIgnoreCase(tag)) {
        bean.set("text","1");
    } else {
        bean.set("text","");
    }
    bean.set("textSet", getSet(tag));
     if((!"ctrl_count".equalsIgnoreCase(tag))&&(!"ctrl_apply".equalsIgnoreCase(tag))){
        bean.set("run", "0");
        //bean.set("end_date", PubFunc.getStringDate("yyyy-MM-dd"));
        bean.set("end_date", endDate);
     }
    
    if ("".equals(xml)) {
        return bean;
    }
    
    
    try
    {
        Document doc = PubFunc.generateDom(xml);
        String xpath="";
        if((!"ctrl_count".equalsIgnoreCase(tag))&&(!"ctrl_apply".equalsIgnoreCase(tag))){
            xpath = "//eval[@job_id=\"" + this.classid + "\"]";
        }else {
            xpath = "//doctrl[@job_id=\"" + this.classid + "\"]";
        }
        XPath xpath_ = XPath.newInstance(xpath);
        Element ele = (Element) xpath_.selectSingleNode(doc);
        if (ele == null) {
            return bean;
        }
        
        xpath = "//" + tag; 
        if((!"ctrl_count".equalsIgnoreCase(tag))&&(!"ctrl_apply".equalsIgnoreCase(tag))) {
            xpath += "[@type=\"" + type + "\"]";
        }
        xpath_ = XPath.newInstance(xpath);
        ele = (Element) xpath_.selectSingleNode(doc);
        if (ele == null) {
            return bean;
        }

        String text = ele.getText();
        
        if ("ctrl_count".equalsIgnoreCase(tag)) {
            text = text == null || text.length() < 1 ? "0" : text;
        } else if ("ctrl_apply".equalsIgnoreCase(tag)) {
            text = text == null || text.length() < 1 ? "1" : text;
        }
        
        bean.set("text", text);
        if((!"ctrl_count".equalsIgnoreCase(tag))&&(!"ctrl_apply".equalsIgnoreCase(tag))){
            bean.set("run", "true".equals(ele.getAttributeValue("run")) ? "1" : "0");
            bean.set("end_date", ele.getAttributeValue("end_date"));
        }
    } catch (Exception e)
    {
        e.printStackTrace();
    }
    return bean;
    }
}
