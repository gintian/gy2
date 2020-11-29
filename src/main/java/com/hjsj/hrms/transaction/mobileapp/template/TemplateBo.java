package com.hjsj.hrms.transaction.mobileapp.template;

import com.hjsj.hrms.businessobject.general.template.TSubSetDomain;
import com.hjsj.hrms.businessobject.general.template.TTitle;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.TemplateUtilBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.transaction.mobileapp.template.util.SubTable;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @ClassName: TemplateBo
 * @Description: TODO
 * @author xucs
 * @date 2013-12-23 下午03:54:17
 * @version 1.0
 */
public class TemplateBo {

    private Connection conn = null;
    private UserView userView = null;
    private RecordVo tablevo = null;
    private int PixelInInch = 96;
    private String queryvalue;
    private String taskid;
    private int tabid;
    private String infortype;
    private String business_model;
    /** 
     * @return business_model 
     */
    public String getBusiness_model() {
        return business_model;
    }

    /** 
     * @param businessModel 要设置的 business_model 
     */
    public void setBusiness_model(String businessModel) {
        business_model = businessModel;
    }

    private String a0100;
    private String b0110;
    private String e01a1;
    private String basepre;
    private HashMap fiedvalueMap;//存放各个input中的数据
    private int ins_id = 0;
    private TemplateTableBo tablebo;
    public TemplateBo(Connection conn, UserView userview) {
        this.conn = conn;
        this.userView = userview;
    }
    public HashMap filedPrivMap = new HashMap();//这里面存放的是流程节点中节点的指标权限
    /**
     * 
     * @Title: createPageview
     * @Description: 创建HTML页面
     * @param tabid
     * @param taskid
     * @param pagenum
     * @return String
     * @throws
     */
    public String createPageview(int tabid, String taskid, int pagenum,String selfapply)throws Exception {
        StringBuffer strhtml = new StringBuffer();
        String divString = "";
        try {
            this.tabid = tabid;
            this.tablevo = readtableVo(tabid);
            this.taskid = taskid;
            this.tablebo=new TemplateTableBo(this.conn,tabid,this.userView);
            filedPrivMap = this.getFieldPriv(this.taskid, this.conn);//获取流程节点中的指标权限
            Element div = new Element("div");
            // div.setAttribute("class", "pagebgk");
            int direct = this.tablevo.getInt("paperori");
            int width = 0;
            int height = 0;
            if (direct == 1)// 纵向 宽为宽 高为高
            {
                width = this.tablevo.getInt("paperw");
                height = this.tablevo.getInt("paperh");
            } else// 横向 宽为高 高为宽
            {
                width = this.tablevo.getInt("paperh");
                height = this.tablevo.getInt("paperw");
            }
            int wpx = Math.round((float) (width / 25.4 * PixelInInch));
            int hpx = Math.round((float) (height / 25.4 * PixelInInch));
            StringBuffer style = new StringBuffer();// style="left:150px;top:5px;width:688px;height:971px;position:absolute"
            style.append("left:20px;top:5px;width:");
            style.append(wpx);
            style.append("px;");
            style.append("height:");
            style.append(hpx);
            style.append("px");
            style.append(";position:absolute");
            div.setAttribute("style", style.toString());
            div.setAttribute("id", "outerdiv");

            MoblieTemplatePageBo pageBo = new MoblieTemplatePageBo(this.conn, this.tabid, pagenum, taskid, this.userView);
            ArrayList titlelist = pageBo.getAllTitle();
            if (titlelist.size() > 0)
                createTitleElement(titlelist, div);
            ArrayList List = pageBo.getAllCell(tabid, pagenum, taskid);
            ArrayList allCellList = (ArrayList) List.get(0);
            this.queryvalue = (String) List.get(1);
            this.fiedvalueMap = getfieldvalue(allCellList,selfapply);
            if (allCellList.size() > 0) {
                divString = createCellElement(allCellList, div);// 创建完成页面上的表单 下一步生成xml 然后js
                divString = divString.replaceAll("`", "<br>");
            }
            //标题照片
            divString=divString.replaceAll("tp&lt;","<");
            divString=divString.replaceAll("/&gt;tp","/>");
            Element xmldiv = new Element("div");
            String id = "";
            if (this.taskid == null || "0".equals(this.taskid)) {
                if(!"0".equals(selfapply)){// 自助用户
                    id="g_templet_"+tabid;
                }else{
                    id = this.userView.getUserName() + "templet_" + tabid;
                }
                
            } else {
                id = "templet_" + tabid;
            }
            
            style.setLength(0);
            style.append("display:none");
            xmldiv.setAttribute("id", id);
            xmldiv.setAttribute("style", style.toString());
            String xmldivString = createDataSetRecord(xmldiv, allCellList);// xml div创建完成 下一步js
            strhtml.setLength(0);
            strhtml.append(divString);
            strhtml.append(xmldivString);
            String js = createDataSetJavaScript(allCellList,selfapply);
            strhtml.append(js);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
            
        }
        return strhtml.toString();
    }

    /**
     * @Title: createTitleElement
     * @Description: 创建HTML的表头
     * @param titlelist
     * @param div
     * @throws
     */
    private void createTitleElement(ArrayList titlelist, Element div) {
        // TODO Auto-generated method stub
        for (int i = 0; i < titlelist.size(); i++) {
            TTitle title = (TTitle) titlelist.get(i);
            title.setCon(this.conn);
            title.setIns_id(this.ins_id);
            title.createTitleView(div, this.userView);
        }
    }
    
    
    /**   
     * @Title: getCanEditRecordList   
     * @Description:控制前台可编辑的recordList ，为了保存指标顺序一致，前台展现数据、保存数据都要调用    
     * @param @param allCellList 当前页所有cell
     * @param @param tablebo  TemplateTableBo类
     * @param @param task_id 当前任务
     * @param @param filedPrivMap 节点权限
     * @param @return 
     * @return ArrayList 
     * @author:wangrd   
     * @throws   
    */
    public ArrayList getCanEditRecordList(ArrayList allCellList,TemplateTableBo tablebo,
            String task_id,HashMap filedPrivMap) {
        ArrayList list = new ArrayList(); 
        String insertDataCtrl =tablebo.getUnrestrictedMenuPriv_Input();/**数据录入不判断子集和指标权限, 0判断(默认值),1不判断  */
        for (int i = 0; i < allCellList.size(); i++) {
            MobileTemplateSetBo setBo = (MobileTemplateSetBo) allCellList.get(i);
            String flag = setBo.getFlag();
            if (flag == null || "".equals(flag) || "H".equals(flag) || "F".equalsIgnoreCase(flag) || "S".equalsIgnoreCase(flag) || "C".equalsIgnoreCase(flag) || "T".equalsIgnoreCase(flag)) {
                continue;
            }
            String field_name = setBo.getField_name();
            boolean specialItem = false;
            if(field_name != null && ("codeitemdesc".equalsIgnoreCase(field_name)||"codesetid".equalsIgnoreCase(field_name)||"corcode".equalsIgnoreCase(field_name)||"parentid".equalsIgnoreCase(field_name)
                    ||"start_date".equalsIgnoreCase(field_name))){
                specialItem = true;
            }
            if (!setBo.isSubflag()) {// 这里用来判断非子集的字段
                String state = this.userView.analyseFieldPriv(setBo.getField_name());

                if (task_id != null && !"".equals(task_id) ) {// 如果不是发起人的话,那么就要判断节点的读写权限
                    String getKey = "";
                    if (setBo.getSub_domain_id() != null && setBo.getSub_domain_id().length() > 0) {
                        getKey = setBo.getField_name().toLowerCase() + "_" + setBo.getSub_domain_id() + "_" + setBo.getChgstate();
                    } else {
                        getKey = setBo.getField_name().toLowerCase() + "_" + setBo.getChgstate();
                    }
                    if ((filedPrivMap.get(getKey) != null && "0".equals(filedPrivMap.get(getKey))) && "0".equals(insertDataCtrl)) {// 如果是无权限,跳出
                        continue;
                    }else
                    	state =(String) filedPrivMap.get(getKey);
                }
                if ("0".equals(insertDataCtrl) && "0".equals(state) && !specialItem) {// 如果是无权限,跳出
                    continue;
                }
                // 处理特殊指标的是否显示
                String Filedtype = setBo.getField_type();
                if ("A".equals(Filedtype)) {// 字符型
                	FieldItem fielditem = null;
                	if(setBo.getField_name()!=null&&!"".equals(setBo.getField_name())){
                		fielditem = DataDictionary.getFieldItem(setBo.getField_name());
                	}
                    if ("codesetid".equals(setBo.getField_name())) {// codesetid
                        ;
                    } else if ("parentid".equals(setBo.getField_name())) {// 上级结点排除
                        ;
                    } else if ("codeitemdesc".equals(setBo.getField_name()) || "corcode".equals(setBo.getField_name())) {// 排除特定结点
                        ;
                    } else {// 不是特殊结点的
                        if ("V".equalsIgnoreCase(setBo.getFlag())) {// 如果是临时变量
                            ;
                        } else {// 不是临时变量不是特殊结点
                            if (fielditem == null) {                          
                                continue;
                            }
                        }
                    }
                } else {// 不是字符型
                    if ("start_date".equalsIgnoreCase(setBo.getField_name())) {// 这个特殊结点排除在外面
                        ;
                    } else {
                        if ("V".equalsIgnoreCase(setBo.getFlag())) {// 如果是临时变量
                            ;
                        } else {
                        	FieldItem item = null;
                        	if(setBo.getField_name()!=null&&!"".equals(setBo.getField_name())){
                        		item = DataDictionary.getFieldItem(setBo.getField_name());
                        	}
                            if (item == null) { // 不是临时变量不是特殊结点                            
                                continue;
                            }
                        }
                    }
                }
            } else {// 子集数据
                String tableCtrl = this.userView.analyseTablePriv(setBo.getSetname());
                
                if (task_id != null && !"".equals(task_id)) {// 如果不是发起人的话,那么就要判断节点的读写权限
                    String getKey = setBo.getSetname().toLowerCase();
                    if (setBo.getSub_domain_id() != null && setBo.getSub_domain_id().length() > 0) {
                        getKey = setBo.getField_name().toLowerCase() + "_" + setBo.getSub_domain_id() + "_" + setBo.getChgstate();
                    } else {
                        getKey = setBo.getField_name().toLowerCase() + "_" + setBo.getChgstate();
                    }
                    if ((filedPrivMap.get(getKey) != null && "0".equals(filedPrivMap.get(getKey)))) {
                        continue;
                    }else
                    	tableCtrl =(String) filedPrivMap.get(getKey);
                }
                if ("0".equals(tableCtrl) && "0".equals(insertDataCtrl)) {
                    continue;
                }

                FieldSet item = DataDictionary.getFieldSetVo(setBo.getSetname());
                if (item == null) {
                    continue;
                }
            }
            list.add(setBo);   
        }
        return list;
    }

    
    /**
     * @param selfapply 
     * @Title: createDataSetJavaScript
     * @Description: 创建前台的dataset的js
     * @param allCellList
     * @throws
     */
    private String createDataSetJavaScript(ArrayList allCellList, String selfapply) {
        String dataset = "";
        StringBuffer strjs = new StringBuffer();
        if (this.taskid == null || "0".equals(this.taskid)) {
            if(!"0".equals(selfapply)){
                dataset = "g_templet_" + this.tabid;  
            }else{
                dataset =this.userView.getUserName() + "templet_" + this.tabid;
            }
            
        } else {
            dataset = "templet_" + this.tabid;
        }
        

        strjs.append("<script language=\"javascript\">");
        strjs.append("function init(){");
        strjs.append(" var ");
        strjs.append(dataset);
        strjs.append("=createDataset(\"");
        strjs.append(dataset);
        strjs.append("\");");
        strjs.append(" var _t=");
        strjs.append(dataset);
        strjs.append(";");
        if(this.tablevo.getInt("static") !=10&&this.tablevo.getInt("static") != 11){
            strjs.append("var _f=_t.addField(\"");
            strjs.append("a0100\",\"String\");");
            strjs.append("_f.label=\"人员编号\";");
            strjs.append(" _f.format=\"\";");
            strjs.append("  _f.visible=true;");
            strjs.append(" _f.codesetid=\"0\";");
            strjs.append("var _f=_t.addField(\"");
            strjs.append("basepre\",\"string\");");
            strjs.append("_f.label=\"人员库\";");
            strjs.append(" _f.format=\"\";");
            strjs.append("  _f.visible=true;");
            strjs.append(" _f.codesetid=\"0\";"); 
        }else if(this.tablevo.getInt("static") ==10){
            strjs.append("var _f=_t.addField(\"");
            strjs.append("b0100\",\"String\");");
            strjs.append("_f.label=\"单位名称\";");
            strjs.append(" _f.format=\"\";");
            strjs.append("  _f.visible=true;");
            strjs.append(" _f.codesetid=\"0\";");  
        }else if(this.tablevo.getInt("static") ==11){
            strjs.append("var _f=_t.addField(\"");
            strjs.append("e01a1\",\"String\");");
            strjs.append("_f.label=\"单位名称\";");
            strjs.append(" _f.format=\"\";");
            strjs.append("  _f.visible=true;");
            strjs.append(" _f.codesetid=\"0\";");  
        }
        
        String insertDataCtrl =this.tablebo.getUnrestrictedMenuPriv_Input();/**数据录入不判断子集和指标权限, 0判断(默认值),1不判断  */
       //是否需要加到recordList全部来源于下面的方法，其他地方不做判断。暂时注释掉，保存时也会使用此方法 wangrd 2015-03-16    
        ArrayList recordList=getCanEditRecordList(allCellList, tablebo, taskid, filedPrivMap);
        for (int i = 0; i < recordList.size(); i++) {
            MobileTemplateSetBo setBo = (MobileTemplateSetBo) recordList.get(i);
            String flag = setBo.getFlag();
            /* wangrd 2015-03-16
            if (flag == null || "".equals(flag) || "H".equals(flag) || flag.equalsIgnoreCase("F") || flag.equalsIgnoreCase("S") || flag.equalsIgnoreCase("C") || flag.equalsIgnoreCase("T")) {
                continue;
            }
            */
            boolean isSubflag = setBo.isSubflag();
            String field_name = setBo.getField_name();
            boolean specialItem = false;
            if(field_name != null && ("codeitemdesc".equalsIgnoreCase(field_name)||"codesetid".equalsIgnoreCase(field_name)||"corcode".equalsIgnoreCase(field_name)||"parentid".equalsIgnoreCase(field_name)
                    ||"start_date".equalsIgnoreCase(field_name))){
                specialItem = true;
            }
            if ("P".equalsIgnoreCase(flag)) {
                /** 添加照片 **/
                strjs.append("var _f=_t.addField(\"");
                strjs.append("photo\",\"blob\");");
                strjs.append("_f.label=\"photo\";");
                strjs.append(" _f.format=\"\";");
                strjs.append("  _f.visible=true;");
                strjs.append(" _f.codesetid=\"0\";");
                /** 添加照片后缀 **/
                strjs.append("var _f=_t.addField(\"");
                strjs.append("ext\",\"string\");");
                strjs.append("_f.label=\"ext\";");
                strjs.append(" _f.format=\"\";");
                strjs.append("  _f.visible=true;");
                strjs.append(" _f.codesetid=\"0\";");
                continue;
            }
            /* wangrd 2015-03-16
            if(!isSubflag){//这里用来判断非子集的字段,子集的在下面生成的时候,会有处理
                String state = this.userView.analyseFieldPriv(setBo.getField_name());
                
                if("0".equals(insertDataCtrl)&&"0".equals(state)&&!specialItem){//如果是无权限,跳出
                    continue;
                }
                
                if (this.taskid != null && !this.taskid.equals("") && !this.taskid.equals("0")) {// 如果不是发起人的话,那么就要判断节点的读写权限
                    
                    String getKey ="";
                    if (setBo.getSub_domain_id() != null && setBo.getSub_domain_id().length() > 0) {
                        getKey = setBo.getSetname().toLowerCase() + "_" + setBo.getSub_domain_id() + "_" + setBo.getChgstate();
                    }else{
                        getKey = setBo.getSetname().toLowerCase() + "_" + setBo.getChgstate();
                    }
                    if((filedPrivMap.get(getKey) != null && filedPrivMap.get(getKey).equals("0"))&&"0".equals(insertDataCtrl)){//如果是无权限,跳出
                        continue;
                    }
                }
            }
            */
            
            
            StringBuffer Filedname = new StringBuffer();
            String Filedtype = "";
            String label = setBo.getField_hz();
            int format = 0;
            boolean visble = true;
            String codesetid = "";
            
            if (setBo.isSubflag()) {//子集数据
                /* wangrd 2015-03-16
                String tableCtrl = this.userView.analyseTablePriv(setBo.getSetname());
                
                if("0".equals(tableCtrl)&&"0".equals(insertDataCtrl)){
                    continue;
                }
                if (this.taskid != null && !this.taskid.equals("") && !this.taskid.equals("0")) {// 如果不是发起人的话,那么就要判断节点的读写权限
                    String getKey = setBo.getSetname().toLowerCase();
                    if (setBo.getSub_domain_id() != null && setBo.getSub_domain_id().length() > 0) {
                        getKey =  setBo.getSetname().toLowerCase() + "_" + setBo.getSub_domain_id() + "_" + setBo.getChgstate();
                    } else {
                        getKey =  setBo.getSetname().toLowerCase() + "_" + setBo.getChgstate();
                    }
                    if ((filedPrivMap.get(getKey) != null && filedPrivMap.get(getKey).equals("0"))) {
                        continue;
                    }
                }
                */
                Filedtype = "clob";
                codesetid = "0";
                Filedname.append("t_");
                Filedname.append(setBo.getSetname().toLowerCase());
                Filedname.append("_");
                FieldSet item = DataDictionary.getFieldSetVo(setBo.getSetname());
                if(item!=null){
                    label = item.getFieldsetdesc();  
                } /* wangrd 2015-03-16 else{
                   continue;
                }*/                
                
            } else {
                Filedtype = setBo.getField_type();
                if ("A".equals(Filedtype)) {//字符型
                	FieldItem fielditem = null;
                    if(setBo.getField_name()!=null&&!"".equals(setBo.getField_name())){
                    	fielditem = DataDictionary.getFieldItem(setBo.getField_name());
                    }
                    if ("codesetid".equals(setBo.getField_name())) {//codesetid 排除
                        codesetid = "UK";
                        visble = true;
                    } else if ("parentid".equals(setBo.getField_name())) {//上级结点排除
                        codesetid = "UM";
                        visble = true;
                    } else if ("codeitemdesc".equals(setBo.getField_name()) || "corcode".equals(setBo.getField_name())) {//排除特定结点
                        codesetid = "";
                        visble = true;
                    } else {//不是特殊结点的
                        if("V".equalsIgnoreCase(setBo.getFlag())){//如果是临时变量
                             codesetid = setBo.getVarVo().getString("codesetid");
                             visble = true;
                        }else{//不是临时变量不是特殊结点
                            if(fielditem!=null){
                                codesetid = fielditem.getFieldsetid();
                                visble = fielditem.isVisible();
                            }/* wangrd 2015-03-16else{
                                continue;
                            }*/
                        }
                        
                        
                    }
                } else {//不是字符型
                    if("start_date".equalsIgnoreCase(setBo.getField_name())){//这个特殊结点排除在外面
                        codesetid = "";
                        visble = true;
                    }else{
                    	if("V".equalsIgnoreCase(setBo.getFlag())){//如果是临时变量
                            codesetid = "";
                            visble = true;
                    	}else{
	                    	   FieldItem item = null;
	                           if(setBo.getField_name()!=null&&!"".equals(setBo.getField_name())){
	                        		item = DataDictionary.getFieldItem(setBo.getField_name());
	                           }
                               if (item != null) {
                                   codesetid = item.getCodesetid();
                                   visble = item.isVisible();
                               }/* wangrd 2015-03-16else{
                                   continue;
                               }*/
                    	}
                     
                    }

                }

                format = setBo.getDisformat();
                Filedname.append(setBo.getField_name().toLowerCase());
                Filedname.append("_");
            }
            
            if (setBo.getSub_domain_id() != null && !"".equals(setBo.getSub_domain_id())) {
                Filedname.append(setBo.getSub_domain_id());
                Filedname.append("_");
            }
            
            Filedname.append(setBo.getChgstate());

            strjs.append("var _f=_t.addField(\"");
            strjs.append(Filedname);
            strjs.append("\",\"");
            
            if ("A".equals(Filedtype)) {
                strjs.append("String");
            } else if ("D".equals(Filedtype)) {
                strjs.append("date");
            } else if ("N".equals(Filedtype) && format == 0) {
                strjs.append("int");
            } else if ("N".equals(Filedtype) && format != 0) {
                strjs.append("float");
            } else if ("M".equals(Filedtype)) {
                strjs.append("String");
            } else {
                strjs.append(Filedtype);
            }
            
            strjs.append("\");");
            strjs.append("_f.label=\"");
            strjs.append(label);
            strjs.append("\";");

            strjs.append(" _f.format=\"");
            
            if ("D".equals(Filedtype)) {
                String disformat = getFormatByDis(format);
                strjs.append(disformat);
                strjs.append("\";");
            } else {
                strjs.append(format);
                strjs.append("\";");
            }
            
            strjs.append("  _f.visible=");
            strjs.append(visble + ";");
            strjs.append(" _f.codesetid=\"");
            strjs.append(codesetid + "\";");

            // /** 主要为了显示从档案库中取的数据,因为有多条记录组合,对日期型和代码型,数值型进行处理 */ 有些数据还不知道要不要
            // 先在这里存放着 @@@@@@@@@@@ xcs 2013-12-4
            // if (f_cell.containsKey(field_name)) {
            // TemplateSetBo setbo = (TemplateSetBo) f_cell.get(field_name);
            // if (((setbo.getHismode() == 2 || setbo.getHismode() == 3 ||
            // setbo.getHismode() == 4)) && item.isChangeBefore()) {
            // if (item.getDatatype() >= 2 && item.getDatatype() <= 12)
            // item.setDatatype("string");
            // item.setCodesetid("0");
            // }
            // }
            //
            // if (item.isChangeBefore() && item.getDatatype() == 10 &&
            // !item.getFormat().equalsIgnoreCase("yyyy.MM.dd")) {
            // int dis = getDisByFormat(item.getFormat());
            // if (dis != 0) {
            // String aa = field_name.substring(0, field_name.length() - 2);
            // field_name = aa + "_" + dis + "_1";
            // }
            //
            // }
            //
            // strjs.append(field_name);
            // strjs.append("\",\"");
            //
            // if (item.getDatatype() == 10 && item.isChangeBefore()) // 如果为日期
            // {
            // if (item.getFormat().equalsIgnoreCase("年限"))
            // strjs.append("string");
            // else
            // strjs.append(DataType.typeToName(item.getDatatype()));
            // } else
            // strjs.append(DataType.typeToName(item.getDatatype()));
            // strjs.append("\");");
            // strjs.append("__f.label=\"");
            // strjs.append(item.getLabel());
            // strjs.append("\";");
            // strjs.append("__f.defaultValue=\"");
            // if (item.getValue() != null)
            // strjs.append(item.getValue());
            // else
            // strjs.append("");
            // strjs.append("\";");
            // strjs.append("__f.format=\"");
            // strjs.append(item.getFormat());
            // strjs.append("\";");
            // if (!item.getAlign().equals("")) {
            // strjs.append("__f.align=\"");
            // strjs.append(item.getAlign());
            // strjs.append("\";");
            // }
            // strjs.append("__f.visible=");
            // if (item.isVisible())
            // strjs.append("true");
            // else
            // strjs.append("false");
            // strjs.append(";");
            // strjs.append("__f.codesetid=\"");
            // if (field_name.equalsIgnoreCase("start_date_2")) {//
            // 如果是生效日期,codesetid要设为0。
            // // 兼容以前的数据
            // strjs.append("0");
            // } else {
            // strjs.append(item.getCodesetid());
            // }
            // strjs.append("\";");
            // strjs.append("__f.dropDown=\"");
            // if (item.getCodesetid().equals("0") ||
            // field_name.equalsIgnoreCase("start_date_2"))//
            // start_date_2的codesetid虽然也是0，但要兼容以前的数据。
            // strjs.append("");
            // else {
            // /** 主要考虑这些代码项太长，前端显示太慢 */
            // /** 代码项大于100 */
            // if (item.getCodesetid().equalsIgnoreCase("UN") ||
            // item.getCodesetid().equalsIgnoreCase("UM") ||
            // item.getCodesetid().equalsIgnoreCase("@K") ||
            // item.getCodesetid().equalsIgnoreCase("1A") ||
            // item.getCodesetid().equalsIgnoreCase("AB") ||
            // item.getCodesetid().equalsIgnoreCase("AG") ||
            // item.getCodesetid().equalsIgnoreCase("AH") ||
            // item.getCodesetid().equalsIgnoreCase("AI") ||
            // item.getCodesetid().equalsIgnoreCase("AJ") ||
            // item.getCodesetid().equalsIgnoreCase("AK") || (itemCodeMap !=
            // null && itemCodeMap.get(item.getCodesetid().toLowerCase()) !=
            // null))
            // strjs.append("dropdownCode");
            // else
            // strjs.append("dropDownList");
            // }
            // strjs.append("\";");
            // strjs.append("__f.editorType=\"");
            // strjs.append("");
            // strjs.append("\";");
            // strjs.append("__f.toolTip=\"");
            // strjs.append("");
            // strjs.append("\";");
            // /** 部门显示层级 */
            // if (item != null && item.getCodesetid().equalsIgnoreCase("UM") &&
            // Integer.parseInt(display_e0122) > 0)
            // strjs.append("__f.level=\"" + display_e0122 + "\";");
            //
            // strjs.append("__f.tag=\"");
            // strjs.append("");
            // strjs.append("\";");
            // if (item.isChangeBefore()) {
            // strjs.append("__f.readOnly=\"");
            // strjs.append("true");
            // strjs.append("\";");
            // }
            // if (this.tablebo.isBEmploy()) {
            //
            // if (item.isChangeAfter() &&
            // !this.userview.analyseFieldPriv(item.getName()).equals("2") &&
            // this.tablebo.getUnrestrictedMenuPriv_Input().equals("0")) {
            // strjs.append("__f.readOnly=\"");
            // strjs.append("true");
            // strjs.append("\";");
            // } else if (item.isChangeAfter() &&
            // this.tablebo.getOpinion_field() != null &&
            // this.tablebo.getOpinion_field().length() > 0 &&
            // this.tablebo.getOpinion_field().equalsIgnoreCase(item.getName()))
            // {
            // strjs.append("__f.readOnly=\"");
            // strjs.append("true");
            // strjs.append("\";");
            // }
            //
            // } else {
            //
            // //
            // if(!(item.getName().equalsIgnoreCase("codeitemdesc")||item.getName().equalsIgnoreCase("codesetid")||item.getName().equalsIgnoreCase("corcode")||item.getName().equalsIgnoreCase("parentid")||item.getName().equalsIgnoreCase("start_date")))
            // {
            // if (item.getName().equalsIgnoreCase("codeitemdesc") ||
            // item.getName().equalsIgnoreCase("codesetid") ||
            // item.getName().equalsIgnoreCase("corcode") ||
            // item.getName().equalsIgnoreCase("parentid") ||
            // item.getName().equalsIgnoreCase("start_date")) {
            // if (fieldPriv_node != null && fieldPriv_node.size() > 0 &&
            // fieldPriv_node.get(field_name.toLowerCase()) != null &&
            // this.tablebo.getUnrestrictedMenuPriv_Input().equals("0")) {
            // String editable = (String)
            // fieldPriv_node.get(field_name.toLowerCase()); // 0|1|2(无|读|写)
            // if (!editable.equals("2")) {
            // strjs.append("__f.readOnly=\"");
            // strjs.append("true");
            // strjs.append("\";");
            // }
            // }
            // } else if (fieldPriv_node != null && fieldPriv_node.size() > 0 &&
            // fieldPriv_node.get(field_name.toLowerCase()) != null &&
            // this.tablebo.getUnrestrictedMenuPriv_Input().equals("0")) {
            //
            // String editable = (String)
            // fieldPriv_node.get(field_name.toLowerCase()); // 0|1|2(无|读|写)
            // if (!editable.equals("2")) {
            // strjs.append("__f.readOnly=\"");
            // strjs.append("true");
            // strjs.append("\";");
            // } else {
            // if (item.isChangeAfter() && this.tablebo.getOpinion_field() !=
            // null && this.tablebo.getOpinion_field().length() > 0 &&
            // this.tablebo.getOpinion_field().equalsIgnoreCase(item.getName()))
            // {
            // strjs.append("__f.readOnly=\"");
            // strjs.append("true");
            // strjs.append("\";");
            // }
            // }
            // } else {
            // if (item.isChangeAfter() && (!(/*
            // * this.userview.analyseFieldPriv
            // * (
            // * item.getName(),0).equals
            // * ("2")||员工角色特殊测试人员要求去掉
            // */this.userview.analyseFieldPriv(item.getName()).equals("2")) &&
            // this.tablebo.getUnrestrictedMenuPriv_Input().equals("0"))) {
            // strjs.append("__f.readOnly=\"");
            // strjs.append("true");
            // strjs.append("\";");
            // } else if (item.isChangeAfter() &&
            // this.tablebo.getOpinion_field() != null &&
            // this.tablebo.getOpinion_field().length() > 0 &&
            // this.tablebo.getOpinion_field().equalsIgnoreCase(item.getName()))
            // {
            // strjs.append("__f.readOnly=\"");
            // strjs.append("true");
            // strjs.append("\";");
            // }
            // }
            // }
            // }
        }
        strjs.append("initDataset(_t);");
        strjs.append("}");
        strjs.append("</script>");
        return strjs.toString();
    }

    /**
     * @Title: getFormatByDis
     * @Description: 对日起类型的数据 进行格式化
     * @param disFormat
     * @return
     * @throws String
     */
    public String getFormatByDis(int disFormat) {
        String format = "yyyy.MM.dd";
        if (disFormat == 6)
            format = "yyyy.MM.dd";
        else if (disFormat == 7)
            format = "yy.MM.dd";
        else if (disFormat == 8 || disFormat == 9)
            format = "yyyy.MM";
        else if (disFormat == 10 || disFormat == 11)
            format = "yy.MM";
        else if (disFormat == 14 || disFormat == 23 || disFormat == 12)
            format = "yyyy年MM月dd日";
        else if (disFormat == 15 || disFormat == 22 || disFormat == 13)
            format = "yyyy年MM月";
        else if (disFormat == 16)
            format = "yy年MM月dd日";
        else if (disFormat == 17)
            format = "yy年MM月";
        else if (disFormat == 18)
            format = "年限";
        else if (disFormat == 19)
            format = "yyyy";
        else if (disFormat == 20)
            format = "MM";
        else if (disFormat == 21)
            format = "dd";
        return format;
    }

    /**
     * @throws GeneralException 
     * @return
     * @param allCellList
     *        所有的表格
     * @Title: createDataSetRecord
     * @Description: 创建xml的数据
     * @param xmldiv
     * @throws
     */
    private String createDataSetRecord(Element xmldiv, ArrayList allCellList) throws GeneralException {
        // TODO Auto-generated method stub
        StringBuffer setRecordBuf = new StringBuffer();
        StringBuffer xmlcivString = new StringBuffer();
        Element root = new Element("xml");
        Element records = new Element("records");
        if (this.fiedvalueMap.size() > 0) {

            Element record = new Element("record");
            Element recnew = new Element("new");
            /**
             * <?xml version="1.0" encoding="gbk"?> <xml> <records> <record>
             * <new>
             * **/
            if(this.tablevo.getInt("static") !=10&&this.tablevo.getInt("static") != 11){//是人员
                setRecordBuf.append((String)this.fiedvalueMap.get("a0100"));
                setRecordBuf.append(","+(String)this.fiedvalueMap.get("basepre")+",");
            }
            if(this.tablevo.getInt("static") ==10){//单位
                setRecordBuf.append((String)this.fiedvalueMap.get("b0110")+",");
            }
            if(this.tablevo.getInt("static") ==11){//岗位
                setRecordBuf.append((String)this.fiedvalueMap.get("e01a1")+",");
            }

            String value = "";
          //是否需要加到recordList的指标全部来源于下面的方法，其他地方不做判断，都注释掉，保存时也会使用此方法 wangrd 2015-07-09    
            ArrayList recordList=getCanEditRecordList(allCellList, tablebo, taskid, filedPrivMap);
            for (int i = 0; i < recordList.size(); i++) {
                
                MobileTemplateSetBo setBo = (MobileTemplateSetBo) recordList.get(i);
                String Filedtype = setBo.getField_type() == null ? "" : setBo.getField_type();
                boolean isSubflag = setBo.isSubflag();
                int disFormat = setBo.getDisformat();
                String flag = setBo.getFlag();
                /* wangrd 2015-07-09 上面都已过滤
                if (flag == null || "".equals(flag) || "H".equals(flag) || flag.equalsIgnoreCase("F") || flag.equalsIgnoreCase("S") || flag.equalsIgnoreCase("C") || flag.equalsIgnoreCase("T")) {
                    continue;
                }
                */
                String field_name = setBo.getField_name();
                /*wangrd 2015-07-09 上面都已过滤
                boolean specialItem = false;
                if(field_name != null && (field_name.equalsIgnoreCase("codeitemdesc")||field_name.equalsIgnoreCase("codesetid")||field_name.equalsIgnoreCase("corcode")||field_name.equalsIgnoreCase("parentid")
                        ||field_name.equalsIgnoreCase("start_date"))){
                    specialItem = true;
                }
                */
                if ("P".equalsIgnoreCase(flag)) {//照片
                    String ext = (String) this.fiedvalueMap.get("ext") == null ? "" : (String) this.fiedvalueMap.get("ext");
                    if ("nophoto".equalsIgnoreCase(ext) || "".equalsIgnoreCase(ext)) {
                        setRecordBuf.append(",,");
                        continue;
                    } else {
                        value = "/servlet/DisplayOleContent?mobile=1&filename=" + (String) this.fiedvalueMap.get("photo");
                        setRecordBuf.append(value + ",,");
                        continue;
                    }
                }
                if(!isSubflag){//这里用来判断非子集的字段,子集的在下面生成的时候,会有处理
                    String state = this.userView.analyseFieldPriv(setBo.getField_name());
                    String inputCtrl =this.tablebo.getUnrestrictedMenuPriv_Input(); /**数据录入不判断子集和指标权限, 0判断(默认值),1不判断  */
                    /* wangrd 2015-07-09 上面都已过滤
                    if("0".equals(inputCtrl)&&"0".equals(state)&&!specialItem){//如果是 非特殊节点 无权限,跳出
                        continue;
                    }
                    */
                    if (this.taskid != null && !"".equals(this.taskid)) {// 如果不是发起人的话,那么就要判断节点的读写权限
                        String getKey ="";
                        if (setBo.getSub_domain_id() != null && setBo.getSub_domain_id().length() > 0) {
                            getKey = setBo.getSetname().toLowerCase() + "_" + setBo.getSub_domain_id() + "_" + setBo.getChgstate();
                        }else{
                            getKey = setBo.getSetname().toLowerCase() + "_" + setBo.getChgstate();
                            
                        }
                        /* * wangrd 2015-07-09 上面都已过滤 
                        if((filedPrivMap.get(getKey) != null && filedPrivMap.get(getKey).equals("0"))&&"0".equals(inputCtrl)){//如果是无权限,跳出
                            continue;
                        }
                        */
                    }
                }
                
                
                /*wangrd 2015-07-09 上面都已过滤
                if (flag.equalsIgnoreCase("F") || flag.equalsIgnoreCase("S") || flag.equalsIgnoreCase("C") || flag.equalsIgnoreCase("T")) {//附件
                    continue;
                }
                */
                StringBuffer getName = new StringBuffer();// 需要从fieldvalueMap中得到的数据的对应的名字
                
                if (isSubflag) {
                    FieldSet item = DataDictionary.getFieldSetVo(setBo.getSetname());
                    
                    if(item==null){
                        continue;
                    }
                    
                    getName.append("t_");
                    getName.append(setBo.getSetname().toLowerCase());
                    
                    if (setBo.getChgstate() == 1 && setBo.getSub_domain_id() != null && !"".equals(setBo.getSub_domain_id())) {
                        getName.append("_" + setBo.getSub_domain_id());
                    }
                    
                    getName.append("_" + setBo.getChgstate());
                } else {
                	if("codeitemdesc".equalsIgnoreCase(setBo.getField_name())||"codesetid".equalsIgnoreCase(setBo.getField_name())||"corcode".equalsIgnoreCase(setBo.getField_name())||"parentid".equalsIgnoreCase(setBo.getField_name())
                            ||"start_date".equalsIgnoreCase(setBo.getField_name())){
                    }else{
                        
                    	if("V".equalsIgnoreCase(flag)){//临时变量不需要排除
                    		
                    	}else{
                    		FieldItem item = null;
                            if(setBo.getField_name()!=null&&!"".equals(setBo.getField_name())){
                        		item = DataDictionary.getFieldItem(setBo.getField_name());
                            }
	                        if(item==null){
	                            continue;
	                        }
                    	}
                    }
                    
                    getName.append(setBo.getField_name().toLowerCase());
                    if (setBo.getChgstate() == 1 && setBo.getSub_domain_id() != null && !"".equals(setBo.getSub_domain_id())) {
                        getName.append("_" + setBo.getSub_domain_id());
                    }
                    getName.append("_" + setBo.getChgstate());
                }
                if (isSubflag) {
                     String tableCtrl = this.userView.analyseTablePriv(setBo.getSetname());
                     String insertDataCtrl =this.tablebo.getUnrestrictedMenuPriv_Input();
                     if("0".equals(tableCtrl)&&"0".equals(insertDataCtrl)){
                         continue;
                     }
                     if (this.taskid != null && !"".equals(this.taskid)) {// 如果不是发起人的话,那么就要判断节点的读写权限
                         String getKey = setBo.getSetname().toLowerCase();
                         if (setBo.getSub_domain_id() != null && setBo.getSub_domain_id().length() > 0) {
                             getKey =  setBo.getSetname().toLowerCase() + "_" + setBo.getSub_domain_id() + "_" + setBo.getChgstate();
                         } else {
                             getKey =  setBo.getSetname().toLowerCase() + "_" + setBo.getChgstate();
                         }
                         if ((filedPrivMap.get(getKey) != null && "0".equals(filedPrivMap.get(getKey)))&&"0".equals(insertDataCtrl)){
                             continue;
                         }
                     }
                     String Svalue = (String) fiedvalueMap.get(getName.toString()) == null ? "" : (String) fiedvalueMap.get(getName.toString());
                     //向value中增加子集的rwPriv权限 rwPriv=0无权限 rwPriv==1读权限  rwPriv==2写权限（权限顺序：先看流程权限，再看自己权限）
                     Svalue = appendAttributeToValue(Svalue, getName.toString(), filedPrivMap, conn, setBo);
                     Svalue = SafeCode.encode(Svalue);
                     setRecordBuf.append(Svalue + ",");
                     continue;
                 }
                if ("D".equals(Filedtype)) {
                    String date = (String) fiedvalueMap.get(getName.toString()) == null ? "" : (String) fiedvalueMap.get(getName.toString());
                    setRecordBuf.append(date + ",");
                    continue;
                } else if ("N".equals(Filedtype)) {
                	String Nvalue = (String) fiedvalueMap.get(getName.toString());
                	if (Nvalue == null) {
                		Nvalue = "";
                	} else {
                		// 为了解决数字型代码没有值的时候不存0 0.0 0.00之类的数据
                		if(Float.parseFloat(Nvalue) ==0 ) {
                			Nvalue = "0";
                		}
                	}
                	setRecordBuf.append(Nvalue + ",");
                } else if ("M".equals(Filedtype)) {
                    String Svalue = fiedvalueMap.get(getName.toString()) == null ? "" : (String) fiedvalueMap.get(getName.toString());
                    Svalue = Svalue.replaceAll(",", "````");// 豆号“,”为字符串的分隔符
                    setRecordBuf.append(Svalue + ",");
                } else {
                    String Svalue = (String) fiedvalueMap.get(getName.toString()) == null ? "" : (String) fiedvalueMap.get(getName.toString());
                    Svalue = Svalue.replaceAll(",", "````");// 豆号“,”为字符串的分隔符
                    setRecordBuf.append(Svalue + ",");
                }

            }
            if (setRecordBuf.length() > 0) {
                setRecordBuf.setLength(setRecordBuf.length() - 1);
            }
            recnew.setText(setRecordBuf.toString());
            record.addContent(recnew);
            records.addContent(record);
            root.addContent(records);
        } else {
            root.addContent(records);
        }
        xmldiv.addContent(root);
        /** 输出超文标志 */
        XMLOutputter outputter = new XMLOutputter();
        Format format = Format.getPrettyFormat();
        format.setExpandEmptyElements(true);// must

        format.setEncoding("UTF-8");
        outputter.setFormat(format);
        xmlcivString.append(outputter.outputString(xmldiv));
        return xmlcivString.toString();
    }

    /**
     * @Title: appendAttributeToValue
     * @Description: 
     *               为子集的xml增加rwPriv属性（子集的读写权限），fieldsPriv属性（各个指标的读写权限，格式：1,2,0,1
     *               ），返回新的xml
     * @param svalue
     * @param filedPrivMap
     * @param conn2
     * @param setBo
     * @return
     * @throws GeneralException 
     * @throws String
     */
    private String appendAttributeToValue(String svalue, String fieldname, HashMap filedPrivMap, Connection conn2, MobileTemplateSetBo setBo) throws GeneralException {
        String newvalue = svalue;
        if ("".equals(svalue)) {
            StringBuffer value = new StringBuffer();
            value.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
            value.append("<records columns=\"");
            //处理一下不能显示在前台子集字段的数据
            String temp = setBo.getFields();
            value.append(temp.substring(0, temp.length() - 1));
            value.append("\">");
            value.append("</records>");
            svalue = value.toString();
        }
        String newfieldname = fieldname.substring(2, fieldname.length());// 截取t_
        String priv = (String) filedPrivMap.get(newfieldname);// 子集读写权限
        StringBuffer fieldspriv = new StringBuffer("");// 子集下各个指标的读写权限(即使子集是读，也要有指标权限。此时指标权限全是0.因为我还要用指标权限控制子集中各个指标的只读模式)
        int index = newfieldname.indexOf("_");
        newfieldname = newfieldname.substring(0, index);
        if (priv == null || "".equals(priv)) {
            priv = this.userView.analyseTablePriv(newfieldname.toUpperCase());
        }
        if("1".equals(this.tablebo.getUnrestrictedMenuPriv_Input())) /**数据录入不判断子集和指标权限, 0判断(默认值),1不判断  */
            priv="2"; //0|1|2(无|读|写)
        // 子集为写权限的时候，要继续考虑指标的权限
        String fields = getFieldsByXml(svalue);// 得到子集下的指标，即columns下的指标。（格式：a1905`a1910`a1915`a1920）
        String[] temparray = fields.split("`");
        for (int i = 0; i < temparray.length; i++) {
            if ("".equals(temparray[i])) {
                continue;
            }
            FieldItem item=DataDictionary.getFieldItem(temparray[i]);
            if(item==null){
                String setName = setBo.getField_hz();
                throw new GeneralException("子集"+setName+"中的指标"+temparray[i]+"在指标库中不存在,请重新设计当前模版");
            }
            String tempPriv = this.userView.analyseFieldPriv(temparray[i].toUpperCase());
            if("0".equals(tempPriv)&&"0".equals(this.tablebo.getUnrestrictedMenuPriv_Input())){//无权限,且判断指标权限,将权限置为0
                tempPriv="0";
            }
            if("1".equals(this.tablebo.getUnrestrictedMenuPriv_Input())) /**数据录入不判断子集和指标权限, 0判断(默认值),1不判断  */
                tempPriv = "2";
            fieldspriv.append(tempPriv + ",");
        }
        if (fieldspriv.length() > 0 && fieldspriv.charAt(fieldspriv.length() - 1) == ',') {
            fieldspriv.setLength(fieldspriv.length() - 1);// 去掉最后一个字符
        }
        String fieldswidth = "";// 每个指标的列宽 格式：23,12,9 顺序一定要与columns中指标的排列顺序严格一致
        fieldswidth = getFieldsWidth(fields, newfieldname, conn);

        // 开始为xml追加属性
        try {
            Document doc = PubFunc.generateDom(svalue);
            Element element = null;
            XMLOutputter outputter = new XMLOutputter();
            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            outputter.setFormat(format);
            String xpath = "/records";
            XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
            element = (Element) findPath.selectSingleNode(doc);
            if (element != null) {
                // 追加rwPriv属性
                if (element.getAttribute("rwPriv") == null) {
                    element.setAttribute("rwPriv", priv);
                } else {
                    element.getAttribute("rwPriv").setValue(priv);
                }
                // 追加fieldsPriv属性
                if (fieldspriv.length() > 0) {
                    if (element.getAttribute("fieldsPriv") == null) {
                        element.setAttribute("fieldsPriv", fieldspriv.toString());
                    } else {
                        element.setAttribute("fieldsPriv", fieldspriv.toString());
                    }
                }
                // 追加fieldsWidth属性
                if (fieldswidth.length() > 0) {
                    if (element.getAttribute("fieldsWidth") == null) {
                        element.setAttribute("fieldsWidth", fieldswidth.toString());
                    } else {
                        element.setAttribute("fieldsWidth", fieldswidth.toString());
                    }
                }
            }
            newvalue = outputter.outputString(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newvalue;
    }
    /**
     * @Title: getFieldsByXml
     * @Description: 从xml中取出所需要的数据
     * @param svalue
     * @return
     * @throws String
     */
    private String getFieldsByXml(String svalue) {
        // TODO Auto-generated method stub
        String fields = "";
        try {
            Document doc = PubFunc.generateDom(svalue);
            Element element = null;
            XMLOutputter outputter = new XMLOutputter();
            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            outputter.setFormat(format);
            String xpath = "/records";
            XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
            element = (Element) findPath.selectSingleNode(doc);
            if (element != null) {
                if (element.getAttribute("columns") != null) {
                    fields = element.getAttributeValue("columns");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fields;
    }

    /**
     * 
     * @Title: getFieldsWidth
     * @Description: 得到子集中指标的列宽
     * @param columns
     * @param setname
     * @param conn
     * @return
     * @throws String
     */
    public String getFieldsWidth(String columns, String setname, Connection conn) {
        StringBuffer sb = new StringBuffer("");
        try {
            // 首先得到子集在templet_set表中的xml
            String xml = "";
            StringBuffer sbxml = new StringBuffer("");
            sbxml.append("select sub_domain from template_set t where tabid=" + this.tabid + " and setname='" + setname.toUpperCase() + "' and subflag='1'");
            ContentDAO dao = new ContentDAO(conn);
            RowSet rs = dao.search(sbxml.toString());
            if (rs.next()) {
                xml = Sql_switcher.readMemo(rs, "sub_domain");
            }
            // 首先得到每个指标的列宽，存储在map中
            HashMap nameWidthMap = new HashMap();
            Document doc = PubFunc.generateDom(xml);
            Element element = null;
            XMLOutputter outputter = new XMLOutputter();
            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            outputter.setFormat(format);
            String xpath = "/sub_para";
            XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
            element = (Element) findPath.selectSingleNode(doc);
            if (element != null) {
                List list = element.getChildren("field");
                for (int i = 0; i < list.size(); i++) {
                    Element temp = (Element) list.get(i);
                    String tempname = temp.getAttributeValue("name").toUpperCase();
                    String tempwidth = temp.getAttributeValue("width");
                    nameWidthMap.put(tempname, tempwidth);
                }
            }
            String[] tempArray = columns.split("`");
            for (int i = 0; i < tempArray.length; i++) {
                FieldItem item=DataDictionary.getFieldItem(tempArray[i]);
//                if(item==null){
//                    continue;
//                }
                String width = (String) nameWidthMap.get(tempArray[i].toUpperCase());
                int int_width = Integer.parseInt(width);// 单位为VGA Base。要转换为pt。
                int_width = int_width * 8;// 1 VGA = 8pt
                sb.append(int_width + ",");
            }
            if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ',') {
                sb.setLength(sb.length() - 1);// 去掉最后一个字符
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * @Title: getFieldPriv
     * @Description:得到当前结点的权限（指标和子集的）
     * @param taskid
     * @param conn
     * @return HashMap
     * @throws null
     */
    public HashMap getFieldPriv(String taskid, Connection conn) {
        HashMap _map = new HashMap();
        Document doc = null;
        Element element = null;
        try {
            ContentDAO dao = new ContentDAO(conn);
            String sql = "select * from t_wf_node where node_id=(select node_id from t_wf_task where task_id=" + taskid + " )";
            RowSet rowSet = dao.search(sql);
            if (rowSet.next()) {
                String ext_param = Sql_switcher.readMemo(rowSet, "ext_param");
                if (ext_param != null && ext_param.trim().length() > 0) {
                    doc = PubFunc.generateDom(ext_param);
                    String xpath = "/params/field_priv/field";
                    XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                    List childlist = findPath.selectNodes(doc);
                    if (childlist.size() == 0) {
                        xpath = "/params/field_priv/field";
                        findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                        childlist = findPath.selectNodes(doc);
                    }
                    if (childlist != null && childlist.size() > 0) {
                        for (int i = 0; i < childlist.size(); i++) {
                            element = (Element) childlist.get(i);
                            String editable = "";
                            // 0|1|2(无|读|写)
                            if (element != null && element.getAttributeValue("editable") != null)
                                editable = element.getAttributeValue("editable");
                            if (editable != null && editable.trim().length() > 0) {
                                String columnname = element.getAttributeValue("name").toLowerCase();
                                _map.put(columnname, editable);
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _map;
    }

    /**
     * @return
     * @Title: createCellElement
     * @Description: 创建每个表格 table
     * @param allCellList
     *        void
     * @throws
     */

    private String createCellElement(ArrayList allCellList, Element div)throws GeneralException {
        StringBuffer divString = new StringBuffer();
        for (int i = 0; i < allCellList.size(); i++) {
            MobileTemplateSetBo setBo = (MobileTemplateSetBo) allCellList.get(i);
            Element table = new Element("table");
            // table.setAttribute("border", "1");
            // table.setAttribute("class", getBorderLineCss(setBo,
            // setBo.getRect()));// 没有边框线
            table.setAttribute("style", getTablePos(setBo));
            table.setAttribute("cellspacing", "0");
            table.setAttribute("cellpadding", "0");
            table.setAttribute("border", "0");
            Element tr = new Element("tr");
            Element td = new Element("td");
            td.setAttribute("class", getBorderLineCss(setBo, setBo.getRect()));
            String[] align = getHValign(setBo.getAlign());
            if (setBo.getRtop() != setBo.getRect().y)
                td.setAttribute("height", "100%");
            else
                td.setAttribute("height", "100%");
            if (setBo.getRleft() != setBo.getRect().x)
                td.setAttribute("width", "100%");
            else
                td.setAttribute("width", "100%");
            td.setAttribute("valign", align[1]);
            if (setBo.isSubflag())
                td.setAttribute("align", "left");
            else
                td.setAttribute("align", align[0]);
            createEditor(td, setBo);// 在td里面创建input,textarea,font等标签
            tr.addContent(td);
            table.addContent(tr);
            div.addContent(table);
        }
        XMLOutputter outputter = new XMLOutputter();
        Format format = Format.getPrettyFormat();
        format.setExpandEmptyElements(true);// must
        format.setEncoding("UTF-8");
        outputter.setFormat(format);
        divString.append(outputter.outputString(div));
        return divString.toString();
    }

    /**
     * @Title: createEditor
     * @Description: 创建 每个表格的input font数据
     * @param td
     * @throws
     */
    private void createEditor(Element td, MobileTemplateSetBo setBo)throws GeneralException {
        if (setBo.getFlag() == null || "".equalsIgnoreCase(setBo.getFlag()))
            setBo.setFlag("H");
        if (setBo.getHz() == null)
            setBo.setHz("");
        String flag = setBo.getFlag();
        String field_type = setBo.getField_type();
        String field_name = setBo.getField_name();
        if ("H".equals(flag))// 汉字描述
        {
            int pt = setBo.getFontsize();
            int px = setBo.getRheight();
            if ((pt * 4) > ((px - 2)) * 3) {// 这个换算是为了防止文字过大会使得td撑开
                pt = pt - 2;
                setBo.setFontsize(pt);
            }
            Element font = new Element("font");
            font.setAttribute("face", setBo.getFontname());
            font.setAttribute("style", setBo.getFontStyle());
            font.setText(getOutText(setBo));
            td.addContent(font);
        } else if ("A".equals(flag) || "B".equals(flag) || "K".equals(flag)) // 指标
        {
            
            if (((setBo.getHismode() == 3 || ((setBo.getHismode() == 2 || setBo.getHismode() == 4) && setBo.getMode() != 0 && setBo.getMode() != 2)) && setBo.getChgstate() == 1) || (setBo.isSubflag())) {
                /**
                 * 在变化前的条件下 条件定位 要根据查出来的数据 判断是否生成textArea
                 * 多条记录||条件序号（不是最初第和最近第）根据查询出来的数据决定是否生成textArea 如果是子集的话生成div
                 * */

                createDivPanel(td, setBo);
            } else {
                if ("D".equalsIgnoreCase(field_type)) {
                    createTdEditor(td, 0, setBo);
                } else if ("N".equalsIgnoreCase(field_type)) {
                    createTdEditor(td, 1, setBo);
                } else if ("M".equalsIgnoreCase(field_type)) {
                    createTextAreaEditor(td, setBo);
                } else if ("A".equalsIgnoreCase(field_type)) {
                    createTdEditor(td, 2, setBo);
                }
            }
        } else if ("P".equals(flag)) // picture
        {
            createImageEditor(td, setBo);
        } else if ("F".equals(flag)) // attachment
        {
            // createAttachmentEditor(td, attachmentindex);
        } else if ("V".equals(flag))// 临时变量
        {
            createInputVarEditor(td, setBo);
        } else if ("C".equals(flag))// 计算公式
        {

        } else if ("S".equals(flag))// 签章
        {
            Element font = new Element("font");
            font.setAttribute("face", setBo.getFontname());
            font.setAttribute("style", setBo.getFontStyle());
            font.setText("");
            td.addContent(font);
        }
    }

    /**
     * @param setBo
     * @Title: createInputVarEditor
     * @Description: 创建临时变量Input
     * @param td
     * @throws
     */
    private void createInputVarEditor(Element td, MobileTemplateSetBo setBo) {
        try {
            Element text = new Element("input");
            text.setAttribute("type", "text");
            String field_name = setBo.getField_name().toLowerCase();// +"_"+this.getChgstate();

            text.setAttribute("field", field_name);
            text.setAttribute("disabled", "true");

            StringBuffer style = new StringBuffer();

            style.append("width:");
            if (setBo.isYneed())
                style.append(setBo.getRwidth() - 15);
            else
                style.append(setBo.getRwidth() - 2);
            style.append("px;");
            style.append("font-size:");
            style.append(setBo.getFontsize());
            style.append("pt;text-align:left;");
            style.append("border:1px #000 solid;");
            style.append("background:#EDEDED");
            text.setAttribute("style", style.toString());
            text.setAttribute("extra", "editor");
            if (this.fiedvalueMap.size() > 0) {
                if ("D".equals(setBo.getField_type())) {
                    String date = (String) fiedvalueMap.get(text.getAttributeValue("field"));// rset.getDate(text.getAttributeValue("field"));
                    text.setAttribute("keyValue", getDateFormat(date, setBo.getDisformat()));
                    text.setAttribute("oldValue", getDateFormat(date, setBo.getDisformat()));
                    text.setAttribute("fieldtype", "date");
                    text.setAttribute("format", "yyyy.MM.dd");
                    text.setAttribute("value", getDateFormat(date, 6));
                } else if ("N".equals(setBo.getField_type())) {
					String Nvalue = (String) fiedvalueMap.get(text.getAttributeValue("field"));
					if (Nvalue == null) {
						Nvalue = "";
					} else {
						// 为了解决数字型代码没有值的时候不存0 0.0 0.00之类的数据
						if (Float.parseFloat(Nvalue) == 0) {
							Nvalue = "0";
						}
					}
					text.setAttribute("keyValue", Nvalue);
					text.setAttribute("oldValue", Nvalue);
					text.setAttribute("format", "");
					text.setAttribute("type", "number");
					text.setAttribute("value", Nvalue);
					// 存入类型
					int format = setBo.getDisformat();
					if (format == 0) {
						text.setAttribute("fieldtype", "int");
					} else {
						text.setAttribute("fieldtype", "float");
					}
                } else {
                    String value = (String) fiedvalueMap.get(text.getAttributeValue("field"));
                    text.setAttribute("keyValue", value);
                    text.setAttribute("oldValue", value);
                    text.setAttribute("fieldtype", "string");
                    text.setAttribute("format", "");
                    text.setAttribute("value", value);
                    if (!"0".equals(setBo.getCodeid())) {
                        String codesetid = setBo.getVarVo().getString("codesetid");
                        value = AdminCode.getCodeName(codesetid, setBo.getCodeid());
                        text.setAttribute("value", value);
                        text.setAttribute("codesetid", codesetid);
                    }
                }
            } else {

                if ("D".equals(setBo.getField_type())) {// 如果是日起类型
                    text.setAttribute("keyValue", "");
                    text.setAttribute("oldValue", "");
                    text.setAttribute("fieldtype", "date");
                    text.setAttribute("format", "yyyy.MM.dd");
                    text.setAttribute("value", "");
                } else if ("N".equals(setBo.getField_type())) {
                    int format = setBo.getDisformat();
                    if (format == 0) {
                        text.setAttribute("keyValue", "");
                        text.setAttribute("oldValue", "");
                        text.setAttribute("fieldtype", "int");
                        text.setAttribute("format", "");
                        text.setAttribute("value", "");
                    } else {
                        text.setAttribute("keyValue", "");
                        text.setAttribute("oldValue", "");
                        text.setAttribute("fieldtype", "float");
                        text.setAttribute("format", "");
                        text.setAttribute("value", "");
                    }
                } else {
                    text.setAttribute("keyValue", "");
                    text.setAttribute("oldValue", "");
                    text.setAttribute("fieldtype", "string");
                    text.setAttribute("format", "");
                    text.setAttribute("value", "");
                    if (!"0".equals(setBo.getCodeid())) {
                        String codesetid = setBo.getVarVo().getString("Codesetid");
                        String value = AdminCode.getCodeName(codesetid, setBo.getCodeid());
                        text.setAttribute("value", value);
                        text.setAttribute("codesetid", codesetid);
                    }
                }

            }
            td.addContent(text);
            if (setBo.isYneed()) {
                Element font = new Element("font");
                font.setAttribute("face", setBo.getFontname());
                font.setAttribute("style", setBo.getFontStyle());
                font.setAttribute("color", "red");
                font.setText("*");
                td.addContent(font);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @Title: createImageEditor
     * @Description: 创建照片类型的Input
     * @param td
     * @throws
     */
    private void createImageEditor(Element td, MobileTemplateSetBo setBo) {
        try {
            Element text = new Element("input");
            text.setAttribute("type", "image");
            if (this.fiedvalueMap.size() > 0) {
                String ext = (String) this.fiedvalueMap.get("ext");
                if ("nophoto".equalsIgnoreCase(ext)) {
                    text.setAttribute("src", "/images/photo.jpg");
                } else {
                    String filename = (String) this.fiedvalueMap.get("photo");
                    text.setAttribute("src", "/servlet/DisplayOleContent?mobile=1&filename=" + filename);
                }
            } else {
                text.setAttribute("src", "/images/photo.jpg");
            }

            text.setAttribute("field", "photo");
            StringBuffer style = new StringBuffer();
            style.append("height:");
            style.append(setBo.getRheight() - 7);
            style.append("px;");
            style.append("width:");
            style.append(setBo.getRwidth() - 2);
            style.append("px;");
            text.setAttribute("style", style.toString());
            /** 业务类型为0时，才需要上传照片 */
            String operationcode = this.tablevo.getString("operationcode");
            int operationtype = findOperationType(operationcode);
            if (operationtype == 0) {
                text.setAttribute("ondblclick", "upload_picture('" + setBo.getSetname() + "');");
                text.setAttribute("onclick", "upload_picture('" + setBo.getSetname() + "');");
            }
            td.addContent(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @Title: findOperationType
     * @Description: 业务类型 对人员调入的业务单独处理
     *               =0人员调入,=1调出（须指定目标人员库）,=2离退(须指定目标人员库),=3内部调动, =4系统内部调动
     *               =10其它不作特殊处理的业务 如果目标库未指定的话，则按源库进行处理
     * @param operationcode
     * @return
     * @throws int
     */
    private int findOperationType(String operationcode) {
        RowSet rset = null;
        StringBuffer strsql = new StringBuffer();
        strsql.append("select operationtype from operation where operationcode='");
        strsql.append(operationcode);
        strsql.append("'");
        ContentDAO dao = new ContentDAO(this.conn);
        int flag = -1;
        try {
            rset = dao.search(strsql.toString());
            if (rset.next())
                flag = rset.getInt("operationtype");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rset != null) {
                try {
                    rset.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    /**
     * @param setBo
     * @Title: createTextAreaEditor
     * @Description: 创建textarea格式的表格
     * @param td
     * @throws
     */
    private void createTextAreaEditor(Element td, MobileTemplateSetBo setBo) {
        try {
            Element text = new Element("textarea");
            String field_name = setBo.getField_name().toLowerCase();// +"_"+this.getChgstate();
            if (setBo.getSub_domain_id() != null && setBo.getSub_domain_id().length() > 0) {
                text.setAttribute("field", field_name + "_" + setBo.getSub_domain_id() + "_" + setBo.getChgstate());
                text.setAttribute("id", field_name + "_" + setBo.getSub_domain_id() + "_" + setBo.getChgstate());
            } else {
                text.setAttribute("field", field_name + "_" + setBo.getChgstate());
                text.setAttribute("id", field_name + "_" + "_" + setBo.getChgstate());
            }
            String inputCtrl =this.tablebo.getUnrestrictedMenuPriv_Input(); /**数据录入不判断子集和指标权限, 0判断(默认值),1不判断  */
            String state = this.userView.analyseFieldPriv(field_name);
            //与指标类型A保持同样逻辑 2015-09-12
            if (this.taskid != null && !"".equals(this.taskid) && !"0".equals(this.taskid)) {// 如果不是发起人的话
                String getKey=field_name + "_" + setBo.getChgstate();
                if (setBo.getChgstate() == 2){        
            		if (filedPrivMap.get(getKey) != null) {
            			state =(String) filedPrivMap.get(getKey);                		
            		}              
                }
            }
            if (!"2".equals(state)&&"0".equals(inputCtrl)) {
            	text.setAttribute("disabled", "true");
            }
            if (setBo.getChgstate() == 1||"2".equals(this.business_model)||"3".equals(this.business_model)) {
                text.setAttribute("disabled", "true");
            }

            StringBuffer style = new StringBuffer();
            style.append("height:");
            style.append(setBo.getRheight() - 6);
            style.append("px;");
            style.append("width:");
            if (setBo.isYneed())
                style.append(setBo.getRwidth() - 16);
            else
                style.append(setBo.getRwidth() - 2);
            style.append("px;");
            style.append("font-size:");
            style.append(setBo.getFontsize());
            style.append("pt;text-align:left");
            Attribute background=text.getAttribute("disabled");
            if(background!=null&&"true".equals(background.getValue())){
                style.append("background:#EDEDED");
            }
            text.setAttribute("style", style.toString());
            if (this.fiedvalueMap.size() > 0) {
                String value = (String) this.fiedvalueMap.get(text.getAttributeValue("field"));
                text.setAttribute("keyValue", value);
                text.setAttribute("oldValue", value);
                text.setAttribute("fieldtype", "memory");
                text.setAttribute("format", "");
                text.setText(value);
                //text.setAttribute("value", value);
            } else {
                text.setAttribute("keyValue", "");
                text.setAttribute("oldValue", "");
                text.setAttribute("fieldtype", "memory");
                text.setAttribute("format", "");
                //text.setAttribute("value", "");
            }
            // onclick="processClick(this)" onblur="processBlur(this);
            text.setAttribute("onclick", "processClick(this)");
            text.setAttribute("onblur", "processBlur(this)");
            td.addContent(text);
            if (setBo.isYneed()) {
                Element font = new Element("font");
                font.setAttribute("face", setBo.getFontname());
                font.setAttribute("style", setBo.getFontStyle());
                font.setAttribute("color", "red");
                font.setText("*");
                td.addContent(font);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param setBo
     * @Title: createTdEditor
     * @Description: 创建 各种指标类型的Input
     * @param td
     * @param i
     * @throws
     */
    private void createTdEditor(Element td, int i, MobileTemplateSetBo setBo) {
        try {
            
            String field_name = setBo.getField_name().toLowerCase();
            String state = this.userView.analyseFieldPriv(field_name);
            String inputCtrl =this.tablebo.getUnrestrictedMenuPriv_Input(); /**数据录入不判断子集和指标权限, 0判断(默认值),1不判断  */
            Element text = new Element("input");
            text.setAttribute("type", "text");
            String codesetid = "";
            boolean specialItem = false;
            if("codeitemdesc".equalsIgnoreCase(field_name)||"codesetid".equalsIgnoreCase(field_name)||"corcode".equalsIgnoreCase(field_name)||"parentid".equalsIgnoreCase(field_name)
                    ||"start_date".equalsIgnoreCase(field_name)){
                specialItem = true;
            }
            if("0".equals(state)&&"0".equals(inputCtrl)&&!specialItem){//无权限,判断权限,不显示值,跳出
                text.setAttribute("disabled", "true");
                StringBuffer style = new StringBuffer();
                style.append("width:");
                if (setBo.isYneed())
                    style.append(setBo.getRwidth() - 15);
                else
                    style.append(setBo.getRwidth() - 2);
                style.append("px;");
                style.append("font-size:");
                style.append(setBo.getFontsize());
                style.append("pt;text-align:left;");
                style.append("border:1px #000 solid;");
                style.append("background:#EDEDED");
                text.setAttribute("style", style.toString());
                td.addContent(text);
                return;
            }
            
            if (i == 2) {
                if ("codesetid".equals(field_name)) {
                    codesetid = "@u";
                }else {
                    codesetid = setBo.getCodeid();// 指标相关代码类
                }
            }
            
            if (setBo.getSub_domain_id() != null && setBo.getSub_domain_id().length() > 0) {
                text.setAttribute("field", field_name + "_" + setBo.getSub_domain_id() + "_" + setBo.getChgstate());
                text.setAttribute("id", field_name + "_" + setBo.getSub_domain_id() + "_" + setBo.getChgstate());
                text.setAttribute("name", field_name + "_" + setBo.getSub_domain_id() + "_" + setBo.getChgstate());
                text.setAttribute("desc", setBo.getField_hz());
            }else{
                text.setAttribute("field", field_name + "_" + setBo.getChgstate());
                text.setAttribute("id", field_name + "_" + setBo.getChgstate());
                text.setAttribute("name", field_name + "_" + setBo.getChgstate());
                text.setAttribute("desc", setBo.getField_hz());
            }
            
            if (setBo.getChgstate() == 1||"2".equals(this.business_model)||"3".equals(this.business_model)) {//如果是变化前的指标,或者是来至于已办和我的申请
                text.setAttribute("disabled", "true");
            }
            
            
            if (this.taskid != null && !"".equals(this.taskid) ) {// 如果不是发起人的话,那么就要判断节点的读写权限
                String getKey ="";
                if (setBo.getSub_domain_id() != null && setBo.getSub_domain_id().length() > 0) {
                    getKey = field_name + "_" + setBo.getSub_domain_id() + "_" + setBo.getChgstate();
                }else{
                    getKey = field_name + "_" + setBo.getChgstate();
                    
                }
                if (setBo.getChgstate() == 2){        
            		if (filedPrivMap.get(getKey) != null) {
            			state =(String) filedPrivMap.get(getKey);                		
            		}               
            		if ("0".equals(inputCtrl)&&!"2".equals(state)&&!specialItem) {//如果是变化后的指标,并且判断子集和指标权限,并且不是写权限,就设置成disable
            			text.setAttribute("disabled", "true");
            		}
                }
                if((filedPrivMap.get(getKey) != null && "0".equals(filedPrivMap.get(getKey)))&&"0".equals(inputCtrl)){//如果是无权限,返回
                    text.setAttribute("disabled", "true");
                    StringBuffer style = new StringBuffer();
                    style.append("width:");
                    if (setBo.isYneed())
                        style.append(setBo.getRwidth() - 15);
                    else
                        style.append(setBo.getRwidth() - 2);
                    style.append("px;");
                    style.append("font-size:");
                    style.append(setBo.getFontsize());
                    style.append("pt;text-align:left;");
                    style.append("border:1px #000 solid;");
                    style.append("background:#EDEDED");
                    text.setAttribute("style", style.toString());
                    td.addContent(text);
                    return;
                }
            }
            if (setBo.getChgstate() == 2){       
        		if ("0".equals(inputCtrl)&&!"2".equals(state)&&!specialItem) {//如果是变化后的指标,并且判断子集和指标权限,并且不是写权限,就设置成disable
        			text.setAttribute("disabled", "true");
        		}
            }
            /**设置Input输入框的样式begin**/
            StringBuffer style = new StringBuffer();
            style.append("width:");
            
            if (setBo.isYneed())
                style.append(setBo.getRwidth() - 15);
            else
                style.append(setBo.getRwidth() - 2);
            
            style.append("px;");
            style.append("font-size:");
            style.append(setBo.getFontsize());
            style.append("pt;text-align:left;");
            style.append("border:1px #000 solid;");
            Attribute background=text.getAttribute("disabled");
            if(background!=null&&"true".equals(background.getValue())){
                style.append("background:#EDEDED");
            }
            text.setAttribute("style", style.toString());
            /**设置Input输入框的样式end**/
            /** 向text文本框中添加数据 **/
            if (this.fiedvalueMap.size() > 0) {
                if (i == 0) {// 如果是日起类型
                    String date = (String) fiedvalueMap.get(text.getAttributeValue("field")) == null ? "" : (String) fiedvalueMap.get(text.getAttributeValue("field"));
                    String fieldname=text.getAttributeValue("field");
                    if(this.tablebo.getOperationtype()==8||this.tablebo.getOperationtype()==9){
                        if("start_date_2".equalsIgnoreCase(fieldname)){
                            String to_id="";
                            to_id=(String) this.fiedvalueMap.get("to_id");
                            String b0110="";
                            if(this.tablebo.getInfor_type()==2){//单位部门
                                b0110=(String) this.fiedvalueMap.get("b0110");
                            }else if(this.tablebo.getInfor_type()==3){//岗位
                                b0110=(String) this.fiedvalueMap.get("e01a1");
                            }
                            if(to_id!=null&&to_id.trim().length()>0&&!to_id.equalsIgnoreCase(b0110)){
                                text.setAttribute("disabled", "true");
                            }
                        }
                    }
                    
                    text.setAttribute("keyValue", getDateFormat(date, setBo.getDisformat()));
                    text.setAttribute("oldValue", getDateFormat(date, setBo.getDisformat()));
                    text.setAttribute("fieldtype", "date");
                    text.setAttribute("format", "yyyy.MM.dd");
                    text.setAttribute("readonly", "true");//日期型不允许输入,必须从控件中选择
                    text.setAttribute("value", getDateFormat(date, setBo.getDisformat()));
                } else if (i == 1) {//数值型
					int format = setBo.getDisformat();
					FieldItem fielditem = DataDictionary.getFieldItem(setBo.getField_name());
					int declength = fielditem.getDecimalwidth();
					String desc = fielditem.getItemdesc();
					text.setAttribute("desc", desc);
					String value = (String) fiedvalueMap.get(text.getAttributeValue("field"));
					if (value == null) {
						value = "";
					} else {
						// 为了解决数字型代码没有值的时候不存0 0.0 0.00之类的数据
						if (Float.parseFloat(value) == 0) {
							value = "0";
						}
					}
					text.setAttribute("keyValue", value);
					text.setAttribute("oldValue", value);
					text.setAttribute("format", "" + format);
					text.setAttribute("value", value);
					text.setAttribute("type", "number");
					if (declength == 0 || format == 0) {
						text.setAttribute("fieldtype", "int");
					} else {
						text.setAttribute("fieldtype", "float");
					}
                } else if (i == 2) {
                    String value = fiedvalueMap.get(text.getAttributeValue("field")) == null ? "" : (String) fiedvalueMap.get(text.getAttributeValue("field"));
                    text.setAttribute("keyValue", value);
                    text.setAttribute("oldValue", value);
                    text.setAttribute("fieldtype", "string");
                    text.setAttribute("format", "");
                    text.setAttribute("value", value);
                    FieldItem fielditem = DataDictionary.getFieldItem(setBo.getField_name());
                    String desc ="";
                    if(fielditem!=null){
                        desc=fielditem.getItemdesc();
                    }
                    if (!"0".equals(setBo.getCodeid()) &&!"".equals(setBo.getCodeid())){
                        text.setAttribute("readonly", "true");
                        if("parentid".equalsIgnoreCase(field_name)){
                            String tempvalue = AdminCode.getCodeName("UM", value);
                            if("".equalsIgnoreCase(tempvalue)){
                                tempvalue=AdminCode.getCodeName("UN", value);
                            }
                            text.setAttribute("desc", "上级组织单元名称");
                            text.setAttribute("value", tempvalue);
                            text.setAttribute("codesetid", "parentid");
                        }else{
                        	if ("UM".equalsIgnoreCase(codesetid)){//按层级显示部门 wangrd 2015-05-21
                                Sys_Oth_Parameter sys = new Sys_Oth_Parameter(conn);
                                String uplevel = sys.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
                                uplevel=uplevel!=null&&uplevel.trim().length()>0?uplevel:"0";
                                CodeItem codeItem = AdminCode.getCode(codesetid, value,Integer.parseInt(uplevel));
                                if (codeItem != null) {
                                    value = codeItem.getCodename();
                                } else {
                                    value = "";
                                }
                            }
                            else {
                                value = AdminCode.getCodeName(codesetid, value);
                            }
                            text.setAttribute("value", value);
                            text.setAttribute("codesetid", codesetid);
                            text.setAttribute("desc", desc);
                        }
                        
                        if(codesetid!=null&&codesetid.trim().length()>0)//20170605 dengcan ,变化后机构指标是否按惯例范围控制
                        {
    	                    if(("UN").equals(codesetid)||("UM").equals(codesetid)||("@K").equals(codesetid)){
    	        				text.setAttribute("ctrltype","0");
    	        				if (setBo.isbLimitManagePriv()){
    	        				    text.setAttribute("ctrltype","3");//1：按管理范围 3:业务范围
    	        				} 
    	        			}else{
    	        				text.setAttribute("ctrltype","0");
    	        			}
                        }
                        
                        
                    }else{
                        if("@u".equalsIgnoreCase(codesetid)){
                            text.setAttribute("readonly", "true");
                           if("UN".equalsIgnoreCase(value)){
                               value="单位";
                           }
                           if("UM".equalsIgnoreCase(value)){
                               value="部门";
                           }
                           text.setAttribute("value", value);
                           text.setAttribute("codesetid", codesetid);
                           text.setAttribute("desc", "组织单元类型");
                        }
                    }
                }
            } else {
                if (i == 0) {// 如果是日起类型
                    text.setAttribute("keyValue", "");
                    text.setAttribute("oldValue", "");
                    text.setAttribute("fieldtype", "date");
                    text.setAttribute("format", "yyyy.MM.dd");
                    text.setAttribute("value", "");
                    text.setAttribute("readonly", "true");
                } else if (i == 1) {//数值型
                    int format = setBo.getDisformat();
                    FieldItem fielditem = DataDictionary.getFieldItem(setBo.getField_name());
                    int declength = fielditem.getDecimalwidth();
                    String desc =fielditem.getItemdesc();
                    text.setAttribute("desc", desc);
                    if(declength==0){
                        text.setAttribute("keyValue", "");
                        text.setAttribute("oldValue", "");
                        text.setAttribute("fieldtype", "int");
                        text.setAttribute("format", "0");
                        text.setAttribute("value", "");
                    }else{
                        if (format == 0) {
                                text.setAttribute("keyValue", "");
                                text.setAttribute("oldValue", "");
                                text.setAttribute("fieldtype", "int");
                                text.setAttribute("format", "0");
                                text.setAttribute("value", "");
                        } else {
                                text.setAttribute("keyValue", "");
                                text.setAttribute("oldValue", "");
                                text.setAttribute("fieldtype", "float");
                                text.setAttribute("format", ""+format);
                                text.setAttribute("value", "");
                            }
                        } 
                } else if (i == 2) {//字符和代码
                    text.setAttribute("keyValue", "");
                    text.setAttribute("oldValue", "");
                    text.setAttribute("fieldtype", "string");
                    text.setAttribute("format", "");
                    text.setAttribute("value", "");
                    
                    if (!"0".equals(setBo.getCodeid())&&!"".equals(setBo.getCodeid())) {
                        text.setAttribute("readonly", "true");
                        if("parentid".equalsIgnoreCase(field_name)){
                            text.setAttribute("value", "");
                            text.setAttribute("codesetid", "parentid");
                        }else{
                            text.setAttribute("value", "");
                            text.setAttribute("codesetid", codesetid);
                        }
                    }else{
                        if("@u".equalsIgnoreCase(codesetid)){
                            text.setAttribute("readonly", "true");
                            text.setAttribute("value", "");
                            text.setAttribute("codesetid", codesetid);
                         }
                    }
                    
                    if(codesetid!=null&&codesetid.trim().length()>0)//20170605 dengcan ,变化后机构指标是否按惯例范围控制
                    {
	                    if(("UN").equals(codesetid)||("UM").equals(codesetid)||("@K").equals(codesetid)){
	        				text.setAttribute("ctrltype","0");
	        				if (setBo.isbLimitManagePriv()){
	        				    text.setAttribute("ctrltype","3");//1：按管理范围 3:业务范围
	        				} 
	        			}else{
	        				text.setAttribute("ctrltype","0");
	        			}
                    }
                    
                }
            }
            // onclick="processClick(this)" onblur="processBlur(this);
            text.setAttribute("onclick", "processClick(this)");
            text.setAttribute("onblur", "processBlur(this)");
            td.addContent(text);
            if (setBo.isYneed()) {
                Element font = new Element("font");
                font.setAttribute("face", setBo.getFontname());
                font.setAttribute("style", setBo.getFontStyle());
                font.setAttribute("color", "red");
                font.setText("*");
                td.addContent(font);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 
     * @Title: getDateFormat
     * @Description: TODO
     * @param:  value
     * @param:  disformat
     * @return: String
     * @throws
     */
    private String getDateFormat(String value, int disformat) {
        StringBuffer buf = new StringBuffer();
        if ("".equals(value)) {
            return " ";
        }
        Date date = (Date) DateUtils.getDate(value, "yyyy-MM-dd");
        int year = DateUtils.getYear(date);
        int month = DateUtils.getMonth(date);
        int day = DateUtils.getDay(date);
        String strv[] = exchangNumToCn(year, month, day);
        value = value.replaceAll("-", ".");
        switch (disformat) {
        case 6: // 1991.12.3
            buf.append(year);
            buf.append(".");
            buf=getMonthOrDayValue(buf, month);
            buf.append(".");
            buf=getMonthOrDayValue(buf,day);
            break;
        case 7: // 91.12.3
            if (year >= 2000)
                buf.append(year);
            else {
                String temp = String.valueOf(year);
                buf.append(temp.substring(2));
            }
            buf.append(".");
            buf=getMonthOrDayValue(buf, month);
            buf.append(".");
            buf=getMonthOrDayValue(buf,day);
            break;
        case 8:// 1991.2
            buf.append(year);
            buf.append(".");
            buf.append(month);
            break;
        case 9:// 1992.02
            buf.append(value.substring(0, 7));
            break;
        case 10:// 92.2
            if (year >= 2000)
                buf.append(year);
            else {
                String temp = String.valueOf(year);
                buf.append(temp.substring(2));
            }
            buf.append(".");
            buf.append(month);
            break;
        case 11:// 98.02
            if (year >= 2000)
                buf.append(year);
            else {
                String temp = String.valueOf(year);
                buf.append(temp.substring(2));
            }
            buf.append(".");
            buf=getMonthOrDayValue(buf, month);
            break;
        case 12:// 一九九一年一月二日

            buf.append(strv[0]);
            buf.append("年");
            buf.append(strv[1]);
            buf.append("月");
            buf.append(strv[2]);
            buf.append("日");
            break;
        case 13:// 一九九一年一月
            buf.append(strv[0]);
            buf.append("年");
            buf.append(strv[1]);
            buf.append("月");
            break;
        case 14:// 1991年1月2日
            buf.append(year);
            buf.append("年");
            buf.append(month);
            buf.append("月");
            buf.append(day);
            buf.append("日");
            break;
        case 15:// 1991年1月
            buf.append(year);
            buf.append("年");
            buf.append(month);
            buf.append("月");
            break;
        case 16:// 91年1月2日
            if (year >= 2000)
                buf.append(year);
            else {
                String temp = String.valueOf(year);
                buf.append(temp.substring(2));
            }
            buf.append("年");
            buf.append(month);
            buf.append("月");
            buf.append(day);
            buf.append("日");
            break;
        case 17:// 91年1月
            if (year >= 2000)
                buf.append(year);
            else {
                String temp = String.valueOf(year);
                buf.append(temp.substring(2));
            }
            buf.append("年");
            buf.append(month);
            buf.append("月");
            break;
        case 18:// 年龄
            buf.append(getAge(year, month, day));
            break;
        case 19:// 1991（年）
            buf.append(year);
            break;
        case 20:// 1 （月）
            buf.append(month);
            break;
        case 21:// 23 （日）
            buf.append(day);
            break;
        case 22:// 1999年02月
            buf.append(year);
            buf.append("年");
            buf=getMonthOrDayValue(buf, month);
            buf.append("月");
            break;
        case 23:// 1999年02月03日
            buf.append(year);
            buf.append("年");
            buf=getMonthOrDayValue(buf, month);
            buf.append("月");
            buf=getMonthOrDayValue(buf,day);
            buf.append("日");
            break;
        case 24:// 1992.02.01
            buf.append(year);
            buf.append(".");
            buf=getMonthOrDayValue(buf, month);
            buf.append(".");
            buf=getMonthOrDayValue(buf,day);
            break;
        default:
            buf.append(year);
            buf.append(".");
            buf=getMonthOrDayValue(buf, month);
            buf.append(".");
            buf=getMonthOrDayValue(buf,day);
            break;
        }
        return buf.toString();
    }
    public StringBuffer getMonthOrDayValue(StringBuffer buf,int value){
        if(value>=10){
            buf.append(value);
        }else{
            buf.append("0"+value);
        }
        return buf;
        
    }
    /**
     * @Title: getAge
     * @Description: TODO
     * @param nyear
     * @param nmonth
     * @param nday
     * @return
     * @throws Object
     */
    private String getAge(int nyear, int nmonth, int nday) {
        int ncyear, ncmonth, ncday;
        java.util.Date curdate = new java.util.Date();
        ncyear = DateUtils.getYear(curdate);
        ncmonth = DateUtils.getMonth(curdate);
        ncday = DateUtils.getDay(curdate);
        StringBuffer buf = new StringBuffer();
        int result = ncyear - nyear;
        if (nmonth > ncmonth) {
            result = result - 1;
        } else {
            if (nmonth == ncmonth) {
                if (nday > ncday) {
                    result = result - 1;
                }
            }
        }
        buf.append(result);
        return buf.toString();
    }

    /**
     * @Title: exchangNumToCn
     * @Description: TODO
     * @param year
     * @param month
     * @param day
     * @return
     * @throws String []
     */
    private String[] exchangNumToCn(int year, int month, int day) {
        String[] strarr = new String[3];
        StringBuffer buf = new StringBuffer();
        String value = String.valueOf(year);
        for (int i = 0; i < value.length(); i++) {
            switch (value.charAt(i)) {
            case '1':
                buf.append("一");
                break;
            case '2':
                buf.append("二");
                break;
            case '3':
                buf.append("三");
                break;
            case '4':
                buf.append("四");
                break;
            case '5':
                buf.append("五");
                break;
            case '6':
                buf.append("六");
                break;
            case '7':
                buf.append("七");
                break;
            case '8':
                buf.append("八");
                break;
            case '9':
                buf.append("九");
                break;
            case '0':
                buf.append("零");
                break;
            }
        }
        strarr[0] = buf.toString();
        buf.setLength(0);
        switch (month) {
        case 1:
            buf.append("一");
            break;
        case 2:
            buf.append("二");
            break;
        case 3:
            buf.append("三");
            break;
        case 4:
            buf.append("四");
            break;
        case 5:
            buf.append("五");
            break;
        case 6:
            buf.append("六");
            break;
        case 7:
            buf.append("七");
            break;
        case 8:
            buf.append("八");
            break;
        case 9:
            buf.append("九");
            break;
        case 10:
            buf.append("十");
            break;
        case 11:
            buf.append("十一");
            break;
        case 12:
            buf.append("十二");
            break;
        }
        strarr[1] = buf.toString();
        buf.setLength(0);
        switch (day) {
        case 1:
            buf.append("一");
            break;
        case 2:
            buf.append("二");
            break;
        case 3:
            buf.append("三");
            break;
        case 4:
            buf.append("四");
            break;
        case 5:
            buf.append("五");
            break;
        case 6:
            buf.append("六");
            break;
        case 7:
            buf.append("七");
            break;
        case 8:
            buf.append("八");
            break;
        case 9:
            buf.append("九");
            break;
        case 10:
            buf.append("十");
            break;
        case 11:
            buf.append("十一");
            break;
        case 12:
            buf.append("十二");
            break;
        case 13:
            buf.append("十三");
            break;
        case 14:
            buf.append("十四");
            break;
        case 15:
            buf.append("十五");
            break;
        case 16:
            buf.append("十六");
            break;
        case 17:
            buf.append("十七");
            break;
        case 18:
            buf.append("十八");
            break;
        case 19:
            buf.append("十九");
            break;
        case 20:
            buf.append("二十");
            break;
        case 21:
            buf.append("二十一");
            break;
        case 22:
            buf.append("二十二");
            break;
        case 23:
            buf.append("二十三");
            break;
        case 24:
            buf.append("二十四");
            break;
        case 25:
            buf.append("二十五");
            break;
        case 26:
            buf.append("二十六");
            break;
        case 27:
            buf.append("二十七");
            break;
        case 28:
            buf.append("二十八");
            break;
        case 29:
            buf.append("二十九");
            break;
        case 30:
            buf.append("三十");
            break;
        case 31:
            buf.append("三十一");
            break;
        }
        strarr[2] = buf.toString();
        return strarr;
    }

    /**
     * @throws GeneralException 
     * 
     * @Title: createDivPanel 
     * @Description: TODO
     * @param td
     * @param setBo    
     * @throws
     */
    private void createDivPanel(Element td, MobileTemplateSetBo setBo) throws GeneralException {
        String[] align = getHValign(setBo.getAlign());
        StringBuffer style = new StringBuffer();
        if (!setBo.isSubflag())// 字段
        {
            if (setBo.getField_name() == null)
                return;
            String field_name = setBo.getField_name().toLowerCase();
            if (setBo.getSub_domain_id() != null && setBo.getSub_domain_id().length() > 0) {
                field_name = setBo.getField_name().toLowerCase() + "_" + setBo.getSub_domain_id();
            }
            td.setAttribute("field", field_name + "_" + setBo.getChgstate());
            String temp = field_name + "_" + setBo.getChgstate();
            /** 非子集状态下要创建TextArea用来显示多条记录 **/
            String textValue = (String) this.fiedvalueMap.get(temp) == null ? "" : (String) this.fiedvalueMap.get(temp);
            String textValueArray[] = textValue.split("`");
            if (textValueArray.length > 1) {
                createMoreTextArea(setBo, td, textValueArray);
            } else {
                createMoreFont(setBo, td, textValueArray);
            }
        } else {// 子集
            FieldSet item = DataDictionary.getFieldSetVo(setBo.getSetname());
            if(item==null){
                String setName = setBo.getField_hz();
                throw new GeneralException("子集"+setName+"在指标库中不存在,请重新设计当前模版");
            }
            if (setBo.getSub_domain_id() != null && setBo.getSub_domain_id().length() > 0) {
                td.setAttribute("field", "t_" + setBo.getSetname().toLowerCase() + "_" + setBo.getSub_domain_id() + "_" + setBo.getChgstate());
            } else {
                td.setAttribute("field", "t_" + setBo.getSetname().toLowerCase() + "_" + setBo.getChgstate());
            }

        }
        td.setAttribute("nowrap", "false");

        if (setBo.isSubflag()) {// 向子集中加入div和table
            Element subdiv = new Element("div");// <DIV style="WIDTH: 100%; HEIGHT: 100%; OVERFLOW: auto" id="t_a04_2_div" class="fixedDiv">
            style.setLength(0);
            style.append("WIDTH: 100%; HEIGHT: 100%; OVERFLOW: auto;");
            String id = "";
            if (setBo.getSub_domain_id() != null && setBo.getSub_domain_id().length() > 0) {
                id = "t_" + setBo.getSetname().toLowerCase() + "_" + setBo.getSub_domain_id() + "_" + setBo.getChgstate() + "_div";
            } else {
                id = "t_" + setBo.getSetname().toLowerCase() + "_" + setBo.getChgstate() + "_div";
            }
            subdiv.setAttribute("style", style.toString());
            subdiv.setAttribute("id", id);
            subdiv.setAttribute("class", "fixeddiv");// div 创建完毕
            //对于指标和子集的控制显示与否 目前有三个地方进行控制 1：模板中的控制参数数据录入不判断指标和子集权限 2：流程节点中的指标权限 3：个人的指标权限
            //指标的控制 1>2>3
            String state = this.userView.analyseTablePriv(setBo.getSetname());//分析用户对于当前table的权限
            String insertDataCtrl = this.tablebo.getUnrestrictedMenuPriv_Input();/**数据录入不判断子集和指标权限, 0判断(默认值),1不判断  */
            if("0".equals(insertDataCtrl)){//第一前提是判断子集和指标的权限
                
                if("0".equals(state)){//判断权限,并且当前用户对伊table的权限为无权限,table不输出
                    return;
                }
                
                if (this.taskid != null && !"".equals(this.taskid) ) {// 如果不是发起人的话,那么就要判断节点的读写权限
                    
                    String getKey = setBo.getSetname().toLowerCase();
                    if (setBo.getSub_domain_id() != null && setBo.getSub_domain_id().length() > 0) {
                        getKey =  setBo.getSetname().toLowerCase() + "_" + setBo.getSub_domain_id() + "_" + setBo.getChgstate();
                    } else {
                        getKey =  setBo.getSetname().toLowerCase() + "_" + setBo.getChgstate();
                    }
                    if ((filedPrivMap.get(getKey) != null && "0".equals(filedPrivMap.get(getKey)))) {
                        return;
                    }
                }
            }
            
            Element subtable = new Element("table");
            style.setLength(0);
            style.append("OVERFLOW: auto;");
            subtable.setAttribute("style", style.toString());
            subtable.setAttribute("cellpadding", "1");
            subtable.setAttribute("cellspacing", "0");
            
            if (setBo.getSub_domain_id() != null && setBo.getSub_domain_id().length() > 0) {
                id = "t_" + setBo.getSetname().toLowerCase() + "_" + setBo.getSub_domain_id() + "_" + setBo.getChgstate() + "_table";
            } else {
                id = "t_" + setBo.getSetname().toLowerCase() + "_" + setBo.getChgstate() + "_table";
            }
            
            subtable.setAttribute("id", id);
            subtable.setAttribute("field", id.substring(0, id.length() - 6));
            
            CreatrSubviewtable(setBo, subtable);//去创建子集的详细内容
            
            subdiv.addContent(subtable);
            
            // 开始添加button<BUTTON style="BACKGROUND-IMAGE: url(/ajax/images/button.gif); COLOR: black" class=button onclick="processSubsetButton('0','t_a04_2')">新增</BUTTON>
            if (this.fiedvalueMap.size() > 0 && setBo.getChgstate() == 2&&!"2".equals(this.business_model)&&!"3".equals(this.business_model)) {
                // 只有有数据并且子集是变化后的才向前台输出三个按钮，至于显示与否，还需要根据权限问题在前台判断 ,如果是已办任务就不用向前台输出三个按钮了
                Element newbutton = new Element("button");
                style.setLength(0);
                style.append("BACKGROUND-IMAGE: url(/ajax/images/mobile_button.gif); COLOR: black");
                newbutton.setAttribute("style", style.toString());
                newbutton.setAttribute("class", "button");
                newbutton.setAttribute("onclick", "processSubsetButton('0','" + subtable.getAttributeValue("field") + "')");
                newbutton.setText("新增");

                Element insertbutton = new Element("button");
                insertbutton.setAttribute("style", style.toString());
                insertbutton.setAttribute("class", "button");
                insertbutton.setAttribute("onclick", "processSubsetButton('1','" + subtable.getAttributeValue("field") + "')");
                insertbutton.setText("插入");

                Element deletebutton = new Element("button");
                deletebutton.setAttribute("style", style.toString());
                deletebutton.setAttribute("class", "button");
                deletebutton.setAttribute("onclick", "processSubsetButton('2','" + subtable.getAttributeValue("field") + "')");
                deletebutton.setText("删除");
                
                subdiv.addContent(newbutton);
                subdiv.addContent(insertbutton);
                subdiv.addContent(deletebutton);
            }
            td.addContent(subdiv);
        }
        // td.addContent(text);
    }

    /**
     * @Title: createMoreFont
     * @Description: 可以插入多条数据的表格 只插入一个数据那么就创建一个FONT即可
     * @param  setBo
     * @param  td
     * @param  textValueArray
     * @return void
     * @throws
     */
    private void createMoreFont(MobileTemplateSetBo setBo, Element td, String[] textValueArray) {
        Element font = new Element("font");
        StringBuffer style = new StringBuffer();
        style.append("font-size:");
        style.append(setBo.getFontsize() + "pt;");
        font.setAttribute("style", style.toString());
        String text = textValueArray[0];
        String codeid = setBo.getCodeid();
        if (codeid != null && !"0".equalsIgnoreCase(codeid) && !"".equals(codeid)) {
            text = AdminCode.getCodeName(codeid, text);
        }
        font.setText(text.trim());
        td.addContent(font);
    }

    /**
     * @Title: createMoreTextArea
     * @Description: 可以插入多条数据的表格 插入多条数据数据那么就创建一个textarea
     * @param setBo
     * @param td
     * @param textValueArray
     * @return void
     * @throws
     */
    private void createMoreTextArea(MobileTemplateSetBo setBo, Element td, String[] textValueArray) {
        Element textArea = new Element("textarea");
        int width = setBo.getRwidth();
        StringBuffer style = new StringBuffer();
        style.append("width:");
        style.append((width - 2) + "px;");
        style.append("height:");
        style.append(setBo.getRheight() - 2 + "px;");
        style.append("font-size:");
        style.append(setBo.getFontsize() + "pt;");
        style.append("background:#EDEDED");
        textArea.setAttribute("style", style.toString());
        int chagestate = setBo.getChgstate();
        String codesetid = setBo.getCodeid() == null ? "" : setBo.getCodeid();
        String value = "";
        style.setLength(0);
        if (!("".equals(codesetid) || "0".equals(codesetid))) {
            for (int i = 0; i < textValueArray.length; i++) {
                value = AdminCode.getCodeName(codesetid, textValueArray[i]);
                style.append(value + "\r\n");
            }
            textArea.setText(style.toString());
        } else {
            for (int i = 0; i < textValueArray.length; i++) {
                value = textValueArray[i];
                style.append(value + "\\r\\n");
            }
            textArea.setText(style.toString());
        }

        if (chagestate == 1||"2".equals(this.business_model)||"3".equals(this.business_model)) {
            textArea.setAttribute("disabled", "true");
        }
        if(setBo.getSub_domain_id()!=null&&setBo.getSub_domain_id().length()>0){
            textArea.setAttribute("id", setBo.getField_name()+"_"+setBo.getSub_domain_id()+"_"+setBo.getChgstate());
        }else{
            textArea.setAttribute("id", setBo.getField_name()+"_"+setBo.getSub_domain_id()+"_"+setBo.getChgstate()); 
        }
        td.addContent(textArea);
    }

    /**
     * @throws GeneralException 
     * 
     * @Title: CreatrSubviewtable
     * @Description: 创建子集表格
     * @param  setBo
     * @param  subtable
     * @return void
     * @throws
     */
    private void CreatrSubviewtable(MobileTemplateSetBo setBo, Element subtable) throws GeneralException {
        Element tr = new Element("tr");
        StringBuffer style = new StringBuffer();
        style.append("BACKGROUND-IMAGE: url(/ajax/images/mobile_button.gif); TOP: 0px");
        tr.setAttribute("style", style.toString());
        Element allTd = new Element("td");
        allTd.setAttribute("width", "10px");
        allTd.setAttribute("align", "middle");
        allTd.setAttribute("height", "22px");
        Element image = new Element("img");
        image.setAttribute("title", "全选");
        String eventSub = subtable.getAttributeValue("field");
        image.setAttribute("onclick", "select_chkall(this,'" + eventSub + "')");
        image.setAttribute("src", "./choose.gif");
        tr.addContent(allTd);
        ArrayList subtableFieldList = setBo.getSuTableList();
        String fieldname = "";
        FieldItem item = null;
        String desc = "";
        String codesetid = "";
        String datatype = "";
        String need = "";
        String width = "";
        String lign[] = getHValign(6);
        int tablewidth = 10;
        for (int i = 0; i < subtableFieldList.size(); i++) {//创建子集的表头行
            Element td = new Element("td");
            SubTable subtableField = (SubTable) subtableFieldList.get(i);
            fieldname = subtableField.getFieldname();
            String state = this.userView.analyseFieldPriv(subtableField.getFieldname());//判断当前用户对于该指标的权限
            String insertDataCtrl = this.tablebo.getUnrestrictedMenuPriv_Input();/**数据录入不判断子集和指标权限, 0判断(默认值),1不判断  */
            //节点的指标权限无法直接控制到子集里面的指标
            need = subtableField.getNeed();
            width = subtableField.getWidth();
            item = DataDictionary.getFieldItem(fieldname.toLowerCase());
            if(item==null){
                String setName = setBo.getField_hz();
                throw new GeneralException("子集"+setName+"中的指标"+fieldname+"在指标库中不存在,请重新设计当前模版");
                //desc=fieldname.toLowerCase();
            }else{
                desc = item.getItemdesc();
                codesetid = item.getCodesetid();
                datatype = item.getItemtype();
            }
            td.setAttribute("align", lign[1]);
            td.setAttribute("valign", lign[1]);
            td.setAttribute("desc", desc);
            td.setAttribute("need", need);
            td.setAttribute("codesetid", codesetid);
            td.setAttribute("width", width + "px");
            td.setAttribute("datatype", datatype);
            td.setAttribute("height", "22px");
            style.setLength(0);
            style.append("font-size:");
            style.append(setBo.getFontsize() + "px;");
            if("0".equals(state)&&"0".equals(insertDataCtrl)){
                style.append("display:none;");            
            }
            td.setAttribute("style", style.toString());
            td.setText(desc);
            if("0".equals(state)&&"0".equals(insertDataCtrl)){
            }else{
                tablewidth = tablewidth + Integer.parseInt(width);
            }
            
            tr.addContent(td);
            tr.setAttribute("height", "22px");
        }
        subtable.setAttribute("width", tablewidth + "px");
        subtable.addContent(tr);

        try {
            String xpath = "/records/record";
            Document doc = null;
            Element element = null;
            if (this.fiedvalueMap.size() > 0) {
                String sub_domain = (String) this.fiedvalueMap.get(eventSub);
                TSubSetDomain setdomain=new TSubSetDomain(setBo.getXml_param());
                if(setdomain!=null)
                    sub_domain=compareData(sub_domain,setdomain);
                this.fiedvalueMap.put(eventSub, sub_domain);
                doc = PubFunc.generateDom(sub_domain);
                XPath findPath = XPath.newInstance(xpath);
                List childlist = findPath.selectNodes(doc);
                for (int i = 0; i < childlist.size(); i++) {
                    element = (Element) childlist.get(i);
                    Element recordtr = new Element("tr");
                    recordtr.setAttribute("height", "22px");
                    recordtr.setAttribute("I9999", element.getAttributeValue("I9999"));
                    String deleted = element.getAttributeValue("deleted") == null ? "0" : element.getAttributeValue("deleted");
                    recordtr.setAttribute("deleted", deleted);
                    String value = element.getText();
                    CreateElementTdWithInput(subtableFieldList, subtable, recordtr, i, value, setBo);//创建子集的数据行
                    subtable.addContent(recordtr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @throws GeneralException 
     * @param setBo
     * @Title: CreateElementTdWithInput
     * @Description: 创建子集的数据行
     * @param subtableFieldList
     * @param setBo
     * @param recordtr
     * @throws
     */
    private void CreateElementTdWithInput(ArrayList subtableFieldList, Element subtable, Element recordtr, int i, String value, MobileTemplateSetBo setBo) throws GeneralException {
        Element recordtd = new Element("td");
        recordtd.setAttribute("align", "middle");
        Element recordInput = new Element("input");
        recordInput.setAttribute("type", "checkbox");
        StringBuffer style=new StringBuffer();
        if (setBo.getChgstate() == 1||"2".equals(this.business_model)||"3".equals(this.business_model)) {
            recordInput.setAttribute("disabled", "true");
            style.append("color:red");
            recordInput.setAttribute("style", style.toString());
        }
        recordInput.setAttribute("name", subtable.getAttributeValue("field") + "_chk_" + i);
        recordtd.addContent(recordInput);
        recordtr.addContent(recordtd);
        value = value + " `";// 解决有些字段在没有填写数据的情况下 导致value和子集字段个数不一致的问题
        String Inputvalue[] = value.split("`");
        for (int n = 0; n < subtableFieldList.size(); n++) {
            SubTable subtableField = (SubTable) subtableFieldList.get(n);
            Element td = new Element("td");
            String state = this.userView.analyseFieldPriv(subtableField.getFieldname());//判断当前用户对于该指标的权限
            String insertDataCtrl = this.tablebo.getUnrestrictedMenuPriv_Input();/**数据录入不判断子集和指标权限, 0判断(默认值),1不判断  */
            td.setAttribute("align", "left");
            style.setLength(0);
            if("0".equals(state)&&"0".equals(insertDataCtrl)){//对该指标无权限,并且数据录入判断子集和指标权限,不创建TD也不创建Input
                style.append("display:none");
            }
            td.setAttribute("style", style.toString());
            Element Input = new Element("input");
            if (setBo.getChgstate() == 1) {
                Input.setAttribute("disabled", "true");
            }
            if(setBo.getChgstate()==2&&"1".equals(state)&&"0".equals(insertDataCtrl)){//变化后,有读权限,判断指标权限
                Input.setAttribute("disabled", "false");
            }
            if("2".equals(this.business_model)||"3".equals(this.business_model)){
                Input.setAttribute("disabled", "true");
            }
           
            Input.setAttribute("id", subtable.getAttributeValue("field")+"_"+(i+1)+"_"+n);
            String onclick = "subviewClick('" + subtable.getAttributeValue("field") + "',this);";
            String onblur = "subviewMouseout('" + subtable.getAttributeValue("field") + "',this);";
            style.setLength(0);
            style.append("TEXT-ALIGN: left;FONT-SIZE: 9pt;");
            String width = subtableField.getWidth();
            style.append("WIDTH:");
            style.append(width);
            style.append("px;");
            style.append("border:1px #000 solid;");
            if(setBo.getChgstate() == 1){
                style.append("background:#EDEDED;");
            }else{
                Attribute backgroundctrl = Input.getAttribute("disabled");
                if(backgroundctrl!=null&&"true".equals(backgroundctrl.getValue())){//设置disableInput 背景色为#EDEDED
                    style.append("background:#EDEDED");
                }
            }
            
            if("0".equals(state)&&"0".equals(insertDataCtrl)){//对该指标无权限,并且数据录入判断子集和指标权限,不创建TD也不创建Input
                style.append("display:none");
            }
            String fieldname = subtableField.getFieldname();
            String nowvalue = Inputvalue[n].trim();
            FieldItem item = DataDictionary.getFieldItem(fieldname.toLowerCase());
            if(item==null){//这里是否直接提示出来错误信息
                String setName = setBo.getField_hz();
                throw new GeneralException("子集"+setName+"中的指标"+fieldname+"在指标库中不存在,请从新涉及当前模版");
            }else{
                if("N".equalsIgnoreCase(item.getItemtype())){
                    Input.setAttribute("type", "number");
                }else{
                    Input.setAttribute("type", "text");
                }
                
                String codesetid = item.getCodesetid();
                
                String codevalue = "";
                if (!("".equals(codesetid) || "0".equals(codesetid))) {
                    codevalue = nowvalue;
                    nowvalue = AdminCode.getCodeName(codesetid, codevalue);
                }
                String datatype = item.getItemtype();
                String desc=item.getItemdesc();
                Input.setAttribute("onclick", onclick);
                Input.setAttribute("onblur", onblur);
                Input.setAttribute("style", style.toString());
                Input.setAttribute("value", nowvalue);
                Input.setAttribute("keyvalue", codevalue);
                Input.setAttribute("oldvalue", codevalue);
                Input.setAttribute("codevalue", codevalue);
                Input.setAttribute("datatype", datatype);
                Input.setAttribute("width", width);
                Input.setAttribute("codesetid", codesetid);
                Input.setAttribute("desc", desc);
            }
            
            td.addContent(Input);
            recordtr.addContent(td);
        }

    }

    /**
     * @Title: getOutText
     * @Description:得到输出的文本内容
     * @return String
     * @throws
     */
    private String getOutText(MobileTemplateSetBo setBo) {
        StringBuffer strcontent = new StringBuffer();

        String[] strs = StringUtils.split(setBo.getHz(), "`");
        for (int i = 0; i < strs.length; i++) {
            strcontent.append(strs[i]);
            strcontent.append("`");
        }
        if (strcontent.length() > 0)
            strcontent.setLength(strcontent.length() - 1);
        if (strcontent.length() == 0)
            strcontent.append("　"); // 补空
        if (strcontent.toString().trim().length() == 0) // 解决空单元格边框 显示不全的问题
            strcontent.append("　");
        return strcontent.toString();
    }

    /**
     * @Title: getBorderLineCss
     * @Description: 获得表格线的属性
     * @param rect
     * @return String
     * @throws
     */
    private String getBorderLineCss(MobileTemplateSetBo setBo, Rectangle rect) {
        String css = "";
        int right = setBo.getRleft() + setBo.getRwidth();
        int bottom = setBo.getRtop() + setBo.getRheight();
        int maxb = rect.y + rect.height;
        int maxr = rect.x + rect.width;
        css = getCellCss(setBo);
        return css;
    }

    /**
     * @Title: getCellCss
     * @Description: 获取表格的四周边框有无的属性情况
     * @return String
     * @throws
     */
    private String getCellCss(MobileTemplateSetBo setBo) {

        String css = "";
        if (setBo.getR() == 1 && setBo.getB() == 0 && setBo.getL() == 0 && setBo.getT() == 0)
            css = "r_line";
        else if (setBo.getR() == 0 && setBo.getB() == 1 && setBo.getL() == 0 && setBo.getT() == 0)
            css = "b_line";
        else if (setBo.getR() == 0 && setBo.getB() == 0 && setBo.getL() == 1 && setBo.getT() == 0)
            css = "l_line";
        else if (setBo.getR() == 0 && setBo.getB() == 0 && setBo.getL() == 0 && setBo.getT() == 1)
            css = "t_line";

        else if (setBo.getR() == 1 && setBo.getB() == 1 && setBo.getL() == 0 && setBo.getT() == 0)
            css = "rb_line";
        else if (setBo.getR() == 1 && setBo.getB() == 0 && setBo.getL() == 1 && setBo.getT() == 0)
            css = "lr_line";
        else if (setBo.getR() == 1 && setBo.getB() == 0 && setBo.getL() == 0 && setBo.getT() == 1)
            css = "rt_line";
        else if (setBo.getR() == 0 && setBo.getB() == 1 && setBo.getL() == 1 && setBo.getT() == 0)
            css = "lb_line";
        else if (setBo.getR() == 0 && setBo.getB() == 1 && setBo.getL() == 0 && setBo.getT() == 1)
            css = "tb_line";
        else if (setBo.getR() == 0 && setBo.getB() == 0 && setBo.getL() == 1 && setBo.getT() == 1)
            css = "lt_line";

        else if (setBo.getR() == 1 && setBo.getB() == 1 && setBo.getL() == 1 && setBo.getT() == 0)
            css = "lrb_line";
        else if (setBo.getR() == 0 && setBo.getB() == 1 && setBo.getL() == 1 && setBo.getT() == 1)
            css = "ltb_line";
        else if (setBo.getR() == 1 && setBo.getB() == 0 && setBo.getL() == 1 && setBo.getT() == 1)
            css = "lrt_line";
        else if (setBo.getR() == 1 && setBo.getB() == 1 && setBo.getL() == 0 && setBo.getT() == 1)
            css = "rtb_line";

        else if (setBo.getR() == 1 && setBo.getB() == 1 && setBo.getL() == 1 && setBo.getT() == 1)
            css = "lrtb_line";
        else
            css = "no_line";
        return css;
    }

    /**
     * @Title: getHValign
     * @Description: 表格中内容的上下对齐方式
     * @param ali
     * @return String[]
     * @throws
     */
    private String[] getHValign(int ali) {
        String[] align = new String[2];
        switch (ali) {
        case 0:
            align[0] = "left";
            align[1] = "top";
            break;
        case 1:
            align[0] = "center";
            align[1] = "top";
            break;
        case 2:
            align[0] = "right";
            align[1] = "top";
            break;
        case 3:
            align[0] = "left";
            align[1] = "bottom";
            break;
        case 4:
            align[0] = "center";
            align[1] = "bottom";
            break;
        case 5:
            align[0] = "right";
            align[1] = "bottom";
            break;
        case 6:
            align[0] = "left";
            align[1] = "middle";
            break;
        case 7:
            align[0] = "center";
            align[1] = "middle";
            break;
        case 8:
            align[0] = "right";
            align[1] = "middle";
            break;
        }
        return align;

    }

    /**
     * @Title: getTablePos
     * @Description: 得到某个表格的位置属性
     * @return String 
     * @throws
     */
    private String getTablePos(MobileTemplateSetBo setBo) {
        StringBuffer strpos = new StringBuffer();
        strpos.append("table-layout:fixed;position:absolute;top:");
        // strpos.append("position:absolute;top:");
        if (setBo.getRtop() != setBo.getRect().y)// 如果当前表格的上边坐标不是最小的那个
        {
            strpos.append(setBo.getRtop() - 1);
            strpos.append("px");
            strpos.append(";height:");
            strpos.append(setBo.getRheight() + 1);
            strpos.append("px");
        } else {
            strpos.append(setBo.getRtop());
            strpos.append("px");
            strpos.append(";height:");
            strpos.append(setBo.getRheight());
            strpos.append("px");
        }
        strpos.append(";left:");
        if (setBo.getRleft() != setBo.getRect().x) {
            strpos.append(setBo.getRleft() - 1 - 30);
            strpos.append("px");
            strpos.append(";width:");
            strpos.append(setBo.getRwidth() + 1);
            strpos.append("px");
        } else {
            strpos.append(setBo.getRleft() - 30);
            strpos.append("px");
            strpos.append(";width:");
            strpos.append(setBo.getRwidth());
            strpos.append("px");
        }
        return strpos.toString();
    }

    /**
     * 
     * @Title: readtableVo
     * @Description: 得到业务模板信息
     * @param tabid  模版号
     * @return RecordVo 存放业务模版的vo对象
     * @throws
     */
    private RecordVo readtableVo(int tabid) {
        RecordVo vo = null;
        try {
            vo = TemplateUtilBo.readTemplate(tabid,this.conn);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return vo;
    }

    /**
     * @param selfapply  用于判断是否是个人业务申请
     * 
     * @Title: getfieldvalue
     * @Description: 得到查询的数据
     * @param allCellList  存放的是一个一个单元格的相关属性
     * @return HashMap 存放查询到的数据
     * @throws
     */
    private HashMap getfieldvalue(ArrayList allCellList, String selfapply) {
        ContentDAO dao = new ContentDAO(this.conn);
        HashMap fiedValueMap = new HashMap();
        RowSet rset = null;
        String filename = "";
        try {
            if (this.queryvalue == null || "".equals(this.queryvalue)) {
                return fiedValueMap;
            } else {
                String querysql = getQuerySql(selfapply);
                rset = dao.search(querysql);
                String flag = "";
                String Filetype = "";
                int Disformat = 0;
                StringBuffer queryColumName = new StringBuffer();
                if (rset.next()) {
                    for (int i = 0; i < allCellList.size(); i++) {
                        MobileTemplateSetBo setBo = (MobileTemplateSetBo) allCellList.get(i);
                        flag = setBo.getFlag();
                        if (flag == null || "H".equals(flag) || "".equals(flag)) {// 汉字描述的数据不用从数据库中查询
                            continue;
                        }
                        if ("P".equalsIgnoreCase(flag)) {// 照片的比较特殊
                            String ext = rset.getString("ext");
                            if (ext == null || "".equalsIgnoreCase(ext)) {
                                fiedValueMap.put("ext", "nophoto");
                                fiedValueMap.put("photo", "nophoto");
                            } else {
                                File tempFile = File.createTempFile(ServletUtilities.tempFilePrefix, rset.getString("ext"), new File(System.getProperty("java.io.tmpdir")));
                                try(FileOutputStream fout = new FileOutputStream(tempFile);
                                    InputStream in =rset.getBinaryStream("photo")) {
                                    int len;
                                    byte buf[] = new byte[1024];
                                    while ((len = in.read(buf, 0, 1024)) != -1) {
                                        fout.write(buf, 0, len);
                                    }
                                }
                                filename = tempFile.getName();
                                fiedValueMap.put("ext", ext);
                                fiedValueMap.put("photo", filename);
                            }
                            continue;
                        }
                        if ("F".equalsIgnoreCase(flag) || "S".equalsIgnoreCase(flag) || "C".equalsIgnoreCase(flag) || "T".equalsIgnoreCase(flag)) {
                            continue;
                        }
                        Filetype = setBo.getField_type() == null ? "" : setBo.getField_type();
                        Disformat = setBo.getDisformat();
                        if (setBo.isSubflag()) {// 处理子集的查询列名称
                            queryColumName.append("t_");
                            queryColumName.append(setBo.getSetname().toLowerCase());
                            if (setBo.getChgstate() == 1 && !"".equals(setBo.getSub_domain_id())) {
                                queryColumName.append("_" + setBo.getSub_domain_id());
                            }
                            queryColumName.append("_" + setBo.getChgstate());
                        } else {// 处理指标的查询列名称
                            String tempqueryname=setBo.getField_name();
                            if(tempqueryname==null){
                                continue;
                            }
                            queryColumName.append(setBo.getField_name().toLowerCase());
                            if (setBo.getChgstate() == 1 && !"".equals(setBo.getSub_domain_id())) {
                                queryColumName.append("_" + setBo.getSub_domain_id());
                            }else{
                                if("V".equalsIgnoreCase(flag)){//是临时变量的话不用控制变化前前还是变化后
                                }else{
                                    queryColumName.append("_" + setBo.getChgstate());
                                }
                            }

                        }

                        if ("D".equalsIgnoreCase(Filetype)) {
                            Date date = rset.getDate(queryColumName.toString().toLowerCase());
                            if (date == null) {
                                fiedValueMap.put(queryColumName.toString().toLowerCase(), "");
                            } else {
                                fiedValueMap.put(queryColumName.toString().toLowerCase(), date.toString());
                            }

                        } else if ("N".equalsIgnoreCase(Filetype)) {
                        	String value=rset.getString(queryColumName.toString());
                        	if(value != null)
                        		fiedValueMap.put(queryColumName.toString().toLowerCase(), value);
                        } else if ("M".equalsIgnoreCase(Filetype)) {
                            String value = Sql_switcher.readMemo(rset, queryColumName.toString());
                            fiedValueMap.put(queryColumName.toString().toLowerCase(), value);
                        } else {
                            String value = "";
                            if (setBo.isSubflag()) {
                                value = Sql_switcher.readMemo(rset, queryColumName.toString());
                            } else {
                                value = rset.getString(queryColumName.toString()) == null ? "" : rset.getString(queryColumName.toString());
                                char s = '\000';
                                String ss = String.valueOf(s);
                                value = value.replaceAll(ss, " ");
                            }
                            if (value == null) {
                                value = " ";
                            }
                            fiedValueMap.put(queryColumName.toString().toLowerCase(), value);
                        }
                        queryColumName.setLength(0);
                    }
                    // 再向map中存储一些特殊的字段
                    if (this.tablevo.getInt("static") !=10&&this.tablevo.getInt("static") != 11) {
                        fiedValueMap.put("a0100", rset.getString("a0100"));
                        fiedValueMap.put("basepre", rset.getString("basepre"));
                    } else if (this.tablevo.getInt("static") == 10) {
                        fiedValueMap.put("b0110", rset.getString("b0110"));
                        if(this.tablebo.getOperationtype()==8||this.tablebo.getOperationtype()==9){//如果是合并和划转才会有 to_id这个字段否则会报列名无效
                            fiedValueMap.put("to_id", rset.getString("to_id"));
                        }

                    } else if (this.tablevo.getInt("static") == 11) {
                        fiedValueMap.put("e01a1", rset.getString("e01a1"));
                        if(this.tablebo.getOperationtype()==8||this.tablebo.getOperationtype()==9){//如果是合并和划转才会有 to_id这个字段否则会报列名无效
                            fiedValueMap.put("to_id", rset.getString("to_id"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rset != null) {
                try {
                    rset.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return fiedValueMap;

    }

    /**
     * @param selfapply 
     * 
     * @Title: getQuerySql
     * @Description: 得到查询数据的sql语句
     * @return String 返回该sql语句
     * @throws
     */
    private String getQuerySql(String selfapply) {
        ContentDAO dao = new ContentDAO(this.conn);
        StringBuffer sqlbuffer = new StringBuffer();
        StringBuffer wherebuffer = new StringBuffer();
        String tempsql= "select ins_id from t_wf_task  where task_id='"+this.taskid+"'";
        RowSet rset;
        String sql="";
        try {
            if(!"0".equals(this.taskid)){
                rset = dao.search(tempsql);
                if(rset.next()){
                    this.ins_id=rset.getInt("ins_id");
                }
            }
            sqlbuffer.append("select * ");
            if (!(this.tablevo.getInt("static") == 10||this.tablevo.getInt("static") == 11)) {
                this.infortype = "1";// 代表人员操作
                if (!"".equals(this.a0100) && !"".equals(this.basepre)) {
                    wherebuffer.append(" where a0100='" + this.a0100 + "' and lower(basepre)='" + this.basepre.toLowerCase()+"'");
                }else{
                    wherebuffer.append(" where 1=2");
                }
                if(ins_id!=0){
                    wherebuffer.append(" and ins_id="+this.ins_id);
                }
            } else if (this.tablevo.getInt("static") == 10) {
                this.infortype = "2";// 代表对单位操作
                if (!"".equals(this.b0110)) {
                    wherebuffer.append(" where b0110='" + this.b0110+"'");
                }else{
                    wherebuffer.append(" where 1=2");
                }
                if(ins_id!=0){
                    wherebuffer.append(" and ins_id="+this.ins_id);
                }
            } else if (this.tablevo.getInt("static") == 11) {
                this.infortype = "3";// 代表对岗位操作
                if (!"".equals(this.e01a1)) {
                    wherebuffer.append(" where e01a1='" + this.e01a1+"'");
                }else{
                    wherebuffer.append(" where 1=2");
                }
                if(ins_id!=0){
                    wherebuffer.append(" and ins_id="+this.ins_id);
                }
            }
            sqlbuffer.append(" from ");
            sqlbuffer.append("templet_" + this.tabid);
            if (wherebuffer.length() > 0) {
                sqlbuffer.append(wherebuffer.toString());
            }else{
                sqlbuffer.append(" where 1=2");
            }
            sqlbuffer.append(" order by a0000");
            sql = sqlbuffer.toString();
            if (this.taskid==null||"0".equalsIgnoreCase(this.taskid)) {
                if(!"0".equals(selfapply)){//个人业务申请 查询数据 "g_templet_" + tabid
                    sql = sql.replaceAll("templet_" + tabid,"g_templet_" + tabid);
                }else{
                    sql = sql.replaceAll("templet_" + tabid, this.userView.getUserName() + "templet_" + tabid);
                }
                
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sql;
    }

    public String getA0100() {
        return a0100;
    }

    public void setA0100(String a0100) {
        this.a0100 = a0100;
    }

    public String getB0110() {
        return b0110;
    }

    public void setB0110(String b0110) {
        this.b0110 = b0110;
    }

    public String getE01a1() {
        return e01a1;
    }

    public void setE01a1(String e01a1) {
        this.e01a1 = e01a1;
    }

    public String getBasepre() {
        return basepre;
    }

    public void setBasepre(String basepre) {
        this.basepre = basepre;
    }
    
    /**
     * 比较数据一致性
     * @param value
     * @param setdomain
     * @return
     */
    private String compareData(String value,TSubSetDomain setdomain)
    {
        String data=value;
        Document doc=null;
        Element element=null;
        
        try
        {
            if(value!=null&&value.length()>0)
            {
                doc=PubFunc.generateDom(value);
                String xpath="/records";
                XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                List childlist=findPath.selectNodes(doc);   
                if(childlist!=null&&childlist.size()>0)
                {
                    element=(Element)childlist.get(0);
                    String columns="";
                    if(element!=null&&element.getAttributeValue("columns")!=null)
                        columns=element.getAttributeValue("columns");
                    if(columns.length()>0&&columns.charAt(columns.length()-1)!='`')
                        columns=columns+"`";
                    if(!columns.equalsIgnoreCase(setdomain.getFields()))
                    {
                        columns=columns.substring(0,columns.length()-1);
                        String[] temps=columns.split("`");
                        
                        String fields=setdomain.getFields();
                        if(fields.length()>0)
                            fields=fields.substring(0,fields.length()-1);
                        String[] setTemps=fields.split("`");
                        
                        element.setAttribute("columns", setdomain.getFields().substring(0, setdomain.getFields().length()-1));
                        childlist=element.getChildren();
                        StringBuffer content=new StringBuffer("");
                        for(int i=0;i<childlist.size();i++)
                        {
                            element=(Element)childlist.get(i);
                            String temp_value=element.getText();
                            String[] _temps=temp_value.split("`");
                            HashMap valueMap=new HashMap();
                            for(int j=0;j<temps.length;j++)
                            {
                                if(j<_temps.length)
                                    valueMap.put(temps[j].toLowerCase(), _temps[j]);
                            }
                            
                            content.setLength(0);
                            for(int j=0;j<setTemps.length;j++)
                            {
                                if(valueMap.get(setTemps[j].toLowerCase())!=null)
                                {
                                    content.append((String)valueMap.get(setTemps[j].toLowerCase())+"`");
                                }
                                else
                                    content.append("`");
                            }
                            if(content.length()>0)
                                content.setLength(content.length()-1);
                            element.setText(content.toString());
                        }
                        
                    }
                    else
                    {
                            columns=columns.substring(0,columns.length()-1);
                            element.setAttribute("columns", columns);
                    }
                    
                    
                    XMLOutputter outputter=new XMLOutputter();
                    Format format=Format.getPrettyFormat();
                    format.setEncoding("UTF-8");
                    outputter.setFormat(format);
                    data=outputter.outputString(doc);
                }
            }
            else
            {
                String xml=setdomain.outContentxml();
                data=xml;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return data;
    }
}
