package com.hjsj.hrms.businessobject.gz.voucher;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
/**
 * 
* 
* 类名称：VoucherBo   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Aug 21, 2013 11:58:37 AM   
* 修改人：zhaoxg   
* 修改时间：Aug 21, 2013 11:58:37 AM   
* 修改备注：   财务凭证业务类
* @version    
*
 */
public class VoucherBo {
    private Connection conn = null;
    private ArrayList fieldlist = new ArrayList();
    private UserView view;
    private HSSFWorkbook wb = null;
    private HSSFSheet sheet = null;
    private HSSFCellStyle style = null;
    private HSSFCellStyle style_l = null;
    private HSSFCellStyle style_r = null;
    private HSSFCellStyle style_title = null;
    private HSSFCellStyle style_thead = null;
    HSSFDataFormat dataformat = null;
    private HSSFCellStyle style_r_1 = null;
    private HSSFCellStyle style_r_2 = null;
    private HSSFCellStyle style_r_3 = null;
    private HSSFCellStyle style_r_4 = null;
    private String param;
    private Document doc;
    private String xml;
    short rowNum = 1;

    public VoucherBo(Connection conn, UserView view) {
        this.conn = conn;
        this.view = view;
    }

    /**
     * 初始化不同的根接点xml的内容
     * 
     * @param conn
     * @param constant
     *            //constant字段内容
     * @param param
     *            //接点路径 例如：voucher
     */
    public VoucherBo(Connection conn, String constant, String param,
            String pn_id) {
        this.conn = conn;
        this.param = constant;
        initXML(constant, param, pn_id);
        try {
            doc = PubFunc.generateDom(xml.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 取得科目表的固定字段列表
     * 
     * @return
     */
    public ArrayList getFieldlistList() {
        ArrayList templist = new ArrayList();

        StringBuffer format = new StringBuffer();
        format.append("############");

        Field field = new Field("ccode", "科目编号");
        field.setDatatype(DataType.STRING);
        field.setLength(20);
        field.setVisible(true);
        templist.add(field);

        field = new Field("ccode_name", "科目名称");
        field.setDatatype(DataType.STRING);
        field.setLength(50);
        field.setVisible(true);
        templist.add(field);

        field = new Field("igrade", "科目级别");
        field.setDatatype(DataType.INT);
        field.setLength(12);
        field.setFormat("####");
        field.setVisible(true);
        templist.add(field);

        field = new Field("i_id", "科目id");
        field.setDatatype(DataType.STRING);
        field.setLength(20);
        field.setVisible(false);
        templist.add(field);

        return templist;
    }

    /**
     * 获取科目表的列
     * 
     * @return
     */
    public ArrayList getAccountList() {
        ArrayList list = new ArrayList();
        LazyDynaBean abean = null;
        abean = new LazyDynaBean();
        abean.set("id", "1");
        abean.set("code", "ccode");
        abean.set("name", "科目编号");
        abean.set("type", "A");
        list.add(abean);

        abean = new LazyDynaBean();
        abean.set("id", "2");
        abean.set("code", "ccode_name");
        abean.set("name", "科目名称");
        abean.set("type", "A");
        list.add(abean);

        abean = new LazyDynaBean();
        abean.set("id", "3");
        abean.set("code", "igrade");
        abean.set("name", "科目级别");
        abean.set("type", "N");
        list.add(abean);

        return list;
    }

    public ArrayList getAccountDataList(ArrayList ItemList, String sql,
            ContentDAO dao) {
        ArrayList list = new ArrayList();
        try {
            RowSet rs = dao.search(sql);
            LazyDynaBean bean = new LazyDynaBean();
            Hashtable abean = null;
            String id = "";
            String code = "";
            String name = "";
            String type = "";
            String value = "";
            while (rs.next()) {
                abean = new Hashtable();
                for (int i = 0; i < ItemList.size(); i++) {
                    bean = (LazyDynaBean) ItemList.get(i);
                    id = (String) bean.get("id");
                    code = (String) bean.get("code");
                    name = (String) bean.get("name");
                    type = (String) bean.get("type");
                    if ("A".equals(type)) {
                        value = rs.getString(code);
                    } else if ("N".equals(type)) {
                        value = rs.getInt(code) + "";
                    }
                    if (value != null) {
                        abean.put(code, value);
                        abean.put("type", type);
                    } else {
                        abean.put(code, "");
                        abean.put("type", type);
                    }

                }
                list.add(abean);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;

    }
    /**
     * 
     * @Title: exportGzData 
     * @Description: TODO
     * @param fileName:excel名字
     * @param dataList各行数据
     * @param List//表头数据List
     * @param url   
     * @throws
     */
    public void exportGzData(String fileName, ArrayList dataList,
            ArrayList List, String url) {
        this.wb = new HSSFWorkbook();
        this.style = getStyle("c", wb);
        this.style_l = getStyle("l", wb);
        this.style_r = getStyle("r", wb);
        this.style_title = getStyle("title", wb);
        this.style_r = getStyle("r", wb);
        int page = 1;
        int nrows = 20000;
        Hashtable dataBean = null;
        LazyDynaBean bean = null;
        try {

            this.sheet = wb.createSheet(page + "");
            page++;
                this.rowNum = 1;
            for (int index = 0; index < List.size(); index++) {
                LazyDynaBean headbean = (LazyDynaBean) List.get(index);
                String itemdesc = (String) headbean.get("name");
                executeCell2(Short.parseShort(String.valueOf(index)),
                        itemdesc, "title");
            }
            this.rowNum++;

            for (int i = 0; i < dataList.size(); i++) {

                dataBean = (Hashtable) dataList.get(i);
                for (int j = 0; j < List.size(); j++) {
                    bean = (LazyDynaBean) List.get(j);
                    String code = (String) bean.get("code");
                    String type = (String) bean.get("type");
                    if ("N".equals(type)) {
                        String deciwidth = (String) bean.get("decwidth");
                        if (deciwidth == null || "".equals(deciwidth.trim()))
                            deciwidth = "0";
                        HSSFCellStyle style = null;
                        style = getStyle("r", wb);
                        executeCellN(Short.parseShort(String.valueOf(j)),
                                (String) dataBean.get(code), style,
                                Integer.parseInt(deciwidth));
                    } else {
                        executeCell2(Short.parseShort(String.valueOf(j)),
                                (String) dataBean.get(code), "L", "A");
                    }
                }
                this.rowNum++;
            }
            FileOutputStream fileOut;
            fileOut = new FileOutputStream(url);
            this.wb.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * excel表中每个格数据的布局
     * 
     * @param align
     * @param wb
     * @return
     */
    public HSSFCellStyle getStyle(String align, HSSFWorkbook wb) {
        HSSFCellStyle a_style = wb.createCellStyle();
        a_style.setBorderBottom(BorderStyle.THIN);
        a_style.setBottomBorderColor(HSSFColor.BLACK.index);
        a_style.setBorderLeft(BorderStyle.THIN);
        a_style.setLeftBorderColor(HSSFColor.BLACK.index);
        a_style.setBorderRight(BorderStyle.THIN);
        a_style.setRightBorderColor(HSSFColor.BLACK.index);
        a_style.setBorderTop(BorderStyle.THIN);
        a_style.setTopBorderColor(HSSFColor.BLACK.index);
        a_style.setVerticalAlignment(VerticalAlignment.CENTER);
        if ("c".equals(align))
            a_style.setAlignment(HorizontalAlignment.CENTER);
        else if ("l".equals(align))
            a_style.setAlignment(HorizontalAlignment.LEFT);
        else if ("r".equals(align))
            a_style.setAlignment(HorizontalAlignment.RIGHT);
        else if ("title".equals(align)) {
            a_style.setAlignment(HorizontalAlignment.CENTER);
            a_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            a_style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
        }
        return a_style;
    }

    public void executeCell2(short columnIndex, String value, String style) {
        HSSFRow row = this.sheet.getRow(rowNum);
        if (row == null)
            row = this.sheet.createRow(rowNum);
        HSSFCell cell = row.getCell(columnIndex);
        if (cell == null)
            cell = row.createCell(columnIndex);
        if ("c".equalsIgnoreCase(style))
            cell.setCellStyle(this.style);
        else if ("l".equalsIgnoreCase(style))
            cell.setCellStyle(this.style_l);
        else if ("R".equalsIgnoreCase(style)) {
            cell.setCellStyle(this.style_r);
        } else if ("title".equalsIgnoreCase(style)) {
            cell.setCellStyle(this.style_title);
        }
        if(columnIndex==0){
            this.sheet.setColumnWidth(columnIndex, 20*160);
        }else if(columnIndex==1){
            this.sheet.setColumnWidth(columnIndex, 55*160);
        }else{
            this.sheet.setColumnWidth(columnIndex, 20*160);
        }
        cell.setCellValue(value);
    }

    HSSFRichTextString richTextString = null;
    HSSFRow row = null;
    HSSFCell cell = null;

    public void executeCellN(short columnIndex, String value,
            HSSFCellStyle style, int scale) {
        row = this.sheet.getRow(rowNum);
        if (row == null)
            row = this.sheet.createRow(rowNum);
        cell = row.createCell(columnIndex);
        cell.setCellStyle(style);
        if (value == null || "".equals(value.trim()))
            value = "0";
        cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
        BigDecimal bd = new BigDecimal(value);
        BigDecimal bd2 = bd.setScale(scale, bd.ROUND_HALF_UP);
        cell.setCellValue(bd2.doubleValue());

    }

    public void executeCell2(short columnIndex, String value, String style,
            String type) {
        row = this.sheet.getRow(rowNum);
        if (row == null)
            row = this.sheet.createRow(rowNum);
        cell = row.createCell(columnIndex);
        if ("c".equalsIgnoreCase(style))
            cell.setCellStyle(this.style);
        else if ("l".equalsIgnoreCase(style))
            cell.setCellStyle(this.style_l);
        else if ("R".equalsIgnoreCase(style))
            cell.setCellStyle(this.style_r);
        if (value != null && value.trim().length() > 0
                && "N".equalsIgnoreCase(type)) {
            cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
            cell.setCellValue(Double.parseDouble(value));
        } else {
            if (value == null)
                value = "";
            richTextString = new HSSFRichTextString(value);
            cell.setCellValue(richTextString);
        }
    }

    /**
     * 取字段str_value的内容
     * 
     * @param constant
     *            //constant字段内容
     * @param param
     *            //接点路径 例如：Params
     */
    private void initXML(String constant, String param, String pn_id) {
        StringBuffer temp_xml = new StringBuffer();
        param = param != null && param.trim().length() > 0 ? param : "voucher";
        temp_xml.append("<?xml version='1.0' encoding='GB2312' ?>");
        temp_xml.append("<" + param + ">");
        temp_xml.append("</" + param + ">");
        try {
            ContentDAO dao = new ContentDAO(conn);
            RowSet rs = dao
                    .search("select content from GZ_Warrant where pn_id = '"
                            + pn_id + "'");
            if (rs.next())
                xml = rs.getString("content");
            if (xml == null || "".equals(xml)) {
                xml = temp_xml.toString();
            }
            doc = PubFunc.generateDom(xml.toString());
            rs.close();
        } catch (Exception ex) {
            xml = temp_xml.toString();
        }
    }
    
    
	//是否是双币凭证
	public boolean  isDualMoney() 
	{
		boolean b = false;
		try {
			if (doc != null) {
				XPath moneyPath = XPath.newInstance("/voucher/is_dual_money");
				Element elemoney = (Element) moneyPath.selectSingleNode(doc);
				if (elemoney != null
						&& "true".equalsIgnoreCase(elemoney.getText())) {
					b = true;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}
	

    /**
     * 设置对应节点属性的值
     * 
     * @param str_path
     *            保存路径 例如：/voucher/items
     * @param value
     *            //值
     * @return
     */
    public void setAttributeValue(String str_path, String attributeName,
            String attributeValue) {
        try {
            XPath xpath = XPath.newInstance(str_path);
            Element spElement = (Element) xpath.selectSingleNode(doc);
            if (spElement != null) {
                spElement.setAttribute(attributeName, attributeValue);
            } else {
                String arr[] = str_path.split("/");
                if (arr != null && arr.length > 0) {
                    for (int i = 1; i < arr.length; i++) {
                        String path = "";
                        for (int j = 1; j <= i; j++) {
                            path += "/" + arr[j];
                        }
                        xpath = XPath.newInstance(path);
                        Element bbElement = (Element) xpath
                                .selectSingleNode(doc);
                        if (bbElement == null) {
                            int index = arr[i].indexOf("[");
                            Element element = null;
                            if (index != -1) {
                                element = new Element(
                                        arr[i].substring(0, index));
                            } else {
                                element = new Element(arr[i]);
                            }
                            if (i == arr.length - 1)
                                element.setAttribute(attributeName,
                                        attributeValue);
                            spElement.addContent(element);
                        } else {
                            spElement = bbElement;
                        }
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 设置对应节点参数的值
     * 
     * @param str_path
     *            保存路径 例如：/voucher/items
     * @param value
     *            //值
     * @return
     */
    public void setTextValue(String str_path, String value) {
        try {
            XPath xpath = XPath.newInstance(str_path);
            Element spElement = (Element) xpath.selectSingleNode(doc);
            if (spElement != null) {
                spElement.setText(value);
            } else {
                String arr[] = str_path.split("/");
                if (arr != null && arr.length > 0) {
                    for (int i = 1; i < arr.length; i++) {
                        String path = "";
                        for (int j = 1; j <= i; j++) {
                            path += "/" + arr[j];
                        }
                        xpath = XPath.newInstance(path);
                        Element bbElement = (Element) xpath
                                .selectSingleNode(doc);
                        if (bbElement == null) {
                            int index = arr[i].indexOf("[");
                            Element element = null;
                            if (index != -1) {
                                element = new Element(
                                        arr[i].substring(0, index));
                            } else {
                                element = new Element(arr[i]);
                            }

                            if (i == arr.length - 1)
                                element.setText(value);
                            spElement.addContent(element);
                        } else {
                            spElement = bbElement;
                        }
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 返回要保存的xml内容
     * 
     */
    public String saveStrValue() {
        StringBuffer buf = new StringBuffer();
        XMLOutputter outputter = new XMLOutputter();
        Format format = Format.getPrettyFormat();
        format.setEncoding("UTF-8");
        outputter.setFormat(format);
        buf.append(outputter.outputString(doc));
        return buf.toString();
    }

    /**
     * 获取xml中的属性值
     * 
     * @return
     */
    public String getXmlValue() {
        String xpath = "//voucher";
        String value = "";
        XPath xpath_;
        try {
            xpath_ = XPath.newInstance(xpath);
            Element ele = (Element) xpath_.selectSingleNode(doc);
            Element child;
            if (ele != null) {
                child = ele.getChild("items");
                if (child != null) {
                    value = child.getAttributeValue("fields");
                }
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        }
        return value;
    }
    /**
     * 获取xml中的属性值
     * 
     * @return
     */
    public String getXmlValue1(String _value) {
        String xpath = "//voucher";
        String value = "";
        XPath xpath_;
        try {
            xpath_ = XPath.newInstance(xpath);
            Element ele = (Element) xpath_.selectSingleNode(doc);
            Element child;
            if (ele != null) {
                child = ele.getChild("webservice");
                if (child != null) {
                    value = child.getAttributeValue(_value);
                }
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        }
        return value;
    }
    public void NewAccount(String ccode,String ccode_name,int igrade){
        try {
            ContentDAO dao = new ContentDAO(conn);
            String sql="insert into GZ_code(i_id,ccode,ccode_name,igrade) values('"+getMaxid(dao)+"','"+ccode+"','"+ccode_name+"',"+igrade+")";
            dao.insert(sql, new ArrayList());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void UpdateAccount(String ccode,String ccode_name,int igrade,String i_id){
        try {
            ContentDAO dao = new ContentDAO(conn);
            String sql="update GZ_code set ccode='"+ccode+"',ccode_name='"+ccode_name+"',igrade='"+igrade+"' where i_id="+i_id+"";
            dao.update(sql);
        }catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    /**
     * 获取最大的i_id+1(自增1)
     * @param dao
     * @return
     */
    public String getMaxid(ContentDAO dao){
        String maxid = "1";
        try
        {
            String sql = "select max("+Sql_switcher.sqlToInt("i_id")+") as max from GZ_code";
            RowSet rs=dao.search(sql);
            if(rs.next()){
                maxid=Integer.parseInt(rs.getString("max")==null?"0":rs.getString("max"))+1+"";
            }
            rs.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return maxid;
    }
    /**
     * 取所有薪资列别
     * 
     * @return
     */
    public ArrayList getSalarySetList() {
        ArrayList list = new ArrayList();
        try {
            String sql = "select salaryid,cname from salarytemplate order by salaryid ";
            ContentDAO dao = new ContentDAO(this.conn);
            RowSet rs = null;
            rs = dao.search(sql);
            while (rs.next()) {
                LazyDynaBean bean = new LazyDynaBean();
                bean.set("salaryid", rs.getString("salaryid"));
                bean.set("cname", rs.getString("salaryid")+":"+rs.getString("cname"));
                list.add(bean);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * 取得人员库列表
     * @return
     * @throws GeneralException
     */
    public ArrayList getDbList() throws GeneralException
    {
        ArrayList list=new ArrayList();
        try
        {
            DbNameBo dd=new DbNameBo(this.conn);
            ArrayList dblist=dd.getAllDbNameVoList(this.view);
            
            ContentDAO dao=new ContentDAO(this.conn);
            String cbase=","+getDb().toLowerCase();
            
            
            LazyDynaBean abean=null;
            for(int i=0;i<dblist.size();i++)
            {
                RecordVo vo=(RecordVo)dblist.get(i);
                
                String dbpre=vo.getString("pre");
                String dbname=vo.getString("dbname");
                String isSelected="0";
                if(cbase.indexOf(","+dbpre.toLowerCase()+",")!=-1){
                    isSelected="1";
                abean=new LazyDynaBean();
                abean.set("pre",dbpre);
                abean.set("dbname",dbname);
                abean.set("isSelected",isSelected);
                list.add(abean);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return list;
    }
    /**
     * 得到标准类别当前纪录信息
     * @return
     * @throws GeneralException
     */
    public String getDb()throws GeneralException
    {
        StringBuffer cbase = new StringBuffer();
        try
        {
            ContentDAO dao=new ContentDAO(this.conn);
            
            String sql = "select CBASE from salarytemplate";
            RowSet rs=dao.search(sql);
            while(rs.next()){
                String[] temp = rs.getString("CBASE").split(",");
                for(int i=0;i<temp.length;i++){
                    if(cbase.indexOf(temp[i])==-1){
                        cbase.append(rs.getString("CBASE"));
                        cbase.append(",");
                    }
                }

            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return cbase.toString();
    }
    /**
     * 得到全部的显示列名的列表
     * @param String salaryid
     * @return ArrayList
     */
    public ArrayList getAllItemList(String salaryid,String setchange)
    {
        ArrayList list = new ArrayList();
        try
        {
            if("warrant".equals(salaryid)){
                String sql = "select itemid,itemdesc from t_hr_BusiField where fieldsetid='GZ_WARRANTLIST'";
                ContentDAO dao = new ContentDAO(this.conn);
                RowSet rs= null;
                rs=dao.search(sql);
                while(rs.next())
                {
                    list.add(new CommonData(rs.getString(1),rs.getString(2)));
                }
            }else{
                StringBuffer sql = new StringBuffer();
                sql.append("select itemid,itemdesc from salaryset where salaryid='");
                if("EntrySet".equals(setchange)){//如果是凭证项目设置,不需要排除数据类型但itemd不能是人员编号不能是人员序号
                    sql.append(salaryid+"' and UPPER(itemid) not in('A0100','A0000')");
                }else{//如果是分录汇总设置，只能取字符型的数据，切itemd不能是人员编号不能是人员序号
                    sql.append(salaryid+"' and itemtype='A' and UPPER(itemid) not in('A0100','A0000')");
                }
                ContentDAO dao = new ContentDAO(this.conn);
                RowSet rs= null;
                rs=dao.search(sql.toString());
                while(rs.next())
                {
                    FieldItem item = DataDictionary.getFieldItem(rs.getString(1).toLowerCase());
                    if(item==null|| "0".equals(item.getUseflag()))
                        continue;
                    if("nbase".equalsIgnoreCase(rs.getString(1).toLowerCase())){
                        list.add(new CommonData(rs.getString(1),"人员库标识"));
                    }else{
                        list.add(new CommonData(rs.getString(1),item.getItemdesc()));
                    }
                    
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return list;
            
    }
    /**
     * 获取所选薪资的帐套的下拉列表
     * @param salaryid
     * @return
     */
    public ArrayList getSalaryList(String salaryid){
        ArrayList list = new ArrayList();
        try
        {   
            String[] _salaryid=salaryid.split(",");
            String sid="";
            for(int i=0;i<_salaryid.length;i++){
                sid+="'"+_salaryid[i]+"'"+",";
            }
            StringBuffer sql = new StringBuffer();
            sql.append("select salaryid,cname from salarytemplate where salaryid in (");
            sql.append(sid.substring(0, sid.length()-1)+")  order by seq");
            ContentDAO dao = new ContentDAO(this.conn);
            RowSet rs= null;
            rs=dao.search(sql.toString());
            while(rs.next())
            {
                list.add(new CommonData(rs.getString(1),rs.getString(2)));
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return list;
        
    }
    /**
     * 取得右侧已选指标（分录汇总指标）
     * @param salaryid
     * @return
     */
    public ArrayList getRightList(String salaryid){
        ArrayList list = new ArrayList();
        try
        {   
            String[] _salaryid=salaryid.split(",");
            for(int i=0;i<_salaryid.length;i++){
                FieldItem item = DataDictionary.getFieldItem(_salaryid[i].toLowerCase());
                list.add(new CommonData(_salaryid[i],item.getItemdesc()));
            }

        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return list;
        
    }
    /**
     * 取得右侧已选指标（分录汇总指标,修改页面获得前台汉字显示内容）
     * @param salaryid
     * @return
     */
    public String getRightList1(String salaryid){
        StringBuffer itemdesc = new StringBuffer();
        try
        {   
            String[] _salaryid=salaryid.split(",");
            for(int i=0;i<_salaryid.length;i++){
                FieldItem item = DataDictionary.getFieldItem(_salaryid[i].toLowerCase());
                itemdesc.append(item.getItemdesc());
                itemdesc.append(",");
            }

        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return itemdesc.toString();
        
    }
    /**
     * 取得右侧已选指标（凭证项目设置页面）
     * @param salaryid
     * @return
     */
    public ArrayList getRightItemList(String items){
        ArrayList list = new ArrayList();
        try
        {   
            String[] _salaryid=items.split(",");
            ArrayList containList = new ArrayList();
            for(int i=0;i<_salaryid.length;i++){
                if(_salaryid[i]!=null&&!"".equals(_salaryid[i])&&!"null".equals(_salaryid[i])){
                    if(containList.contains(_salaryid[i].toLowerCase())){
                        continue;
                    }else{
                       containList.add(_salaryid[i].toLowerCase());
                    }
                    FieldItem item = DataDictionary.getFieldItem(_salaryid[i].toLowerCase());
                    if(item.getItemdesc()==null|| "".equals(item.getItemdesc())){
                        list.add(new CommonData(_salaryid[i],getWarranItem(_salaryid[i])));
                    }else{
                        list.add(new CommonData(_salaryid[i],item.getItemdesc()));
                    }
                }           
            }

        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return list;
        
    }
    public ArrayList getVoucherItem(){
        ArrayList voucheritem = new ArrayList();
        try
        {
            String sql = "select c_type from GZ_Warrant";
            ContentDAO dao = new ContentDAO(this.conn);
            HashSet set = new HashSet();
            RowSet rs= null;
            rs=dao.search(sql);
            while(rs.next()){
                set.add(rs.getString("c_type")==null?"":rs.getString("c_type"));
            }
            Iterator iterator=set.iterator();
            while(iterator.hasNext()){
                voucheritem.add(iterator.next());
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return voucheritem;
    }
    public String getWarranItem(String itemid){
        String item="";
        try
        {   
            String sql = "select itemid,itemdesc from t_hr_BusiField where fieldsetid='GZ_WARRANTLIST' and itemid="+itemid+"";
            ContentDAO dao = new ContentDAO(this.conn);
            RowSet rs= null;
            rs=dao.search(sql);
            if(rs.next()){
                item=rs.getString("itemdesc");
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return item;
        
    }
    /**
     * 获得凭证项目明细表指标
     * @return
     */
    public ArrayList getWarrantList(){
        ArrayList list = new ArrayList();
        try
        {       
            String sql = "select itemid,itemdesc from t_hr_BusiField where fieldsetid='GZ_WARRANTLIST'";
            ContentDAO dao = new ContentDAO(this.conn);
            RowSet rs= null;
            rs=dao.search(sql);
            while(rs.next())
            {
                list.add(new CommonData(rs.getString(1),rs.getString(2)));
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return list;    
    }
    /**
     * 
    
    * @Title: getTitleList
    
    * @Description: 获取分录界面上显示的表头信息
    
    * @param xmlArray 从数据库中取得相应的字段
    * @param conn 数据库的连接
    * @param groupArray //分录分组指标中已选中的指标项
    * @param interface_type//财务凭证的类别
    * @param salaryid//凭证中选择的薪资类别
    * @return ArrayList    
    
    * @throws
     */
    public ArrayList getTitleList(String[] xmlArray, Connection conn,
            String[] groupArray, String interface_type,String[]salaryid) {
        if ("2".equals(interface_type)) {
            ArrayList titleList = new ArrayList();
            ContentDAO dao = new ContentDAO(conn);
            for (int i = 0; i < xmlArray.length; i++) {
                if("seq".equalsIgnoreCase(xmlArray[i].toLowerCase())){
                    continue;
                }
                String sql = "select itemdesc from t_hr_busifield where lower (fieldsetid)= 'gz_warrantlist' and lower (itemid)='"
                        + xmlArray[i].toLowerCase() + "'";
                try {
                    RowSet rs = dao.search(sql);
                    while (rs.next()) {
                        titleList.add(rs.getString("itemdesc"));
                    }
                    titleList.remove("分录ID");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            titleList.add("顺序号");
            titleList = sortTitleList(titleList);
            return titleList;
        } else if ("1".equals(interface_type)) {
            ArrayList titleList = new ArrayList();
            ArrayList removeList= new ArrayList();
            String temp="";     
            ArrayList realList =getRealList(conn);
            for (int i = 0; i < xmlArray.length; i++) {
                if("c_group".equalsIgnoreCase(xmlArray[i])){
                    titleList.add("分录分组指标");
                    continue;
                }
                if(!(realList.contains(xmlArray[i].toUpperCase()))){
                    temp=temp+xmlArray[i]+",";
                    continue;
                }
                if ("EXT_MONEY".equals(xmlArray[i].toUpperCase().toUpperCase())){
                	continue;
                }
                FieldItem item = DataDictionary.getFieldItem(xmlArray[i].toLowerCase());
                if(!("".equals(item)||item==null)){
                    if(!item.isVisible()){
                        continue;
                    }
                }
                titleList.add(item.getItemdesc());
            }
            
            if(!("".equals(groupArray)||groupArray==null)){
                for(int i=0;i<groupArray.length;i++){
                    temp=temp+groupArray[i]+",";
                }
                temp=temp.substring(0, temp.length()-1);
                groupArray=temp.split(",");
                for(int i=0;i<groupArray.length;i++){
                    FieldItem item = DataDictionary.getFieldItem(groupArray[i].toLowerCase());
                    if(item==null){
                        continue;
                    }
                    if(titleList.contains(item.getItemdesc())){
                        continue;
                    }
                    titleList.add(item.getItemdesc());
                    
                }
            }else if(!("".equals(temp)||temp==null)){
                groupArray=temp.split(",");
                for(int i=0;i<groupArray.length;i++){
                    FieldItem item = DataDictionary.getFieldItem(groupArray[i].toLowerCase());
                    if(item==null){
                        continue;
                    }
                    if(titleList.contains(item.getItemdesc())){
                        continue;
                    }
                    titleList.add(item.getItemdesc());
                }
            }
            removeList.add("凭证号");
            removeList.add("凭证日期");
            removeList.add("计提月份");
            removeList.add("发薪次数");
            removeList.add("凭证类别");
            removeList.add("部门编号");
            removeList.add("金额");
            removeList.add("分录ID");
            removeList.add("凭证ID");
            titleList.removeAll(removeList);
            titleList.add("顺序号");
            titleList = sortTitleList(titleList);
            return titleList;
        }
        return null;
    }
    public ArrayList getRealList(Connection conn){
        ContentDAO dao = new ContentDAO(conn);
        String tsql="select itemid from t_hr_busifield where lower(fieldsetid)='gz_warrantlist'";
        ArrayList realList=new ArrayList();
        RowSet rs;
        try {
            rs = dao.search(tsql);
            while(rs.next()){
                realList.add(rs.getString(1));
            }
            if(rs!=null){
                rs.close();
            }
        } catch (SQLException e1) {

            e1.printStackTrace();
        }
        return realList;
    }
    public String getTemp(String[]xmlArray,Connection conn){
        String temp="";     
        ArrayList realList =getRealList(conn);
        if(xmlArray==null||0==xmlArray.length){
            return "";
        }
        for (int i = 0; i < xmlArray.length; i++) {
            if("c_group".equalsIgnoreCase(xmlArray[i].toLowerCase())){
                continue;
            }
            if(!(realList.contains(xmlArray[i].toUpperCase()))){
                temp=temp+xmlArray[i]+",";
            }
        }
        return temp;
    }
    
    public ArrayList sortTitleList(ArrayList XMLList){
        ArrayList tempList = new ArrayList();
        tempList.add("摘要");
        tempList.add("科目");
        tempList.add("分录名称");
        tempList.add("计算公式");
        tempList.add("本币计算公式");
        tempList.add("分录分组指标");
        tempList.add("限制条件");
        tempList.add("借贷方向");
        tempList.add("辅助核算项目");
        tempList.add("辅助核算值");
        ArrayList xmlList = XMLList;
        ArrayList tList = new ArrayList();
        for(int i=0;i<tempList.size();i++){
            String ss = (String) tempList.get(i);
            if(xmlList.contains(ss.toLowerCase())||xmlList.contains(ss.toUpperCase())){
                continue;
            }
            tList.add(tempList.get(i));
        }
        tempList.removeAll(tList);
        xmlList.removeAll(tempList);
        tempList.addAll(xmlList);
        return tempList;
    }
    public ArrayList getXiangmuList(){
        String xiangmuValue="PZ_ID,VOUCHER_DATE,DBILL_DATE,DBILL_TIMES,C_TYPE,DEPTCODE,C_MARK,C_SUBJECT,FL_NAME,N_LOAN,MONEY,CHECK_ITEM,CHECK_ITEM_VALUE";
        CommonData temp=new CommonData();
        ArrayList xiangmuList=new ArrayList();
        String[] xiangmuArray=xiangmuValue.split(",");
        for(int i=0;i<xiangmuArray.length;i++){
            FieldItem item = DataDictionary.getFieldItem(xiangmuArray[i].toLowerCase());
            temp=new CommonData(xiangmuArray[i].toLowerCase(),item.getItemdesc());
            xiangmuList.add(temp);
        }
        return xiangmuList;
    }

	public ArrayList getRateList(String[] salarySetArray, String whereSql) {
		ArrayList array = new ArrayList();
		RowSet rowSet=null;
		try {
			StringBuffer str= new StringBuffer();
			ArrayList valuelist = new ArrayList();
			ContentDAO dao=new ContentDAO(this.conn);
			str.append(" select distinct(itemid),itemdesc from salaryset where salaryid in( ");
			for(int i =0;i<salarySetArray.length;i++){
				valuelist.add(salarySetArray[i]);
				if(i==salarySetArray.length-1)
					str.append("?");
				else
					str.append("?,");
			}
			str.append(") and ( ");
			str.append(whereSql);
			str.append(")");
			rowSet=dao.search(str.toString(),valuelist);
			while(rowSet.next()){
				String itemid = rowSet.getString("itemid");
				if("A0000".equalsIgnoreCase(itemid)||"A00Z3".equalsIgnoreCase(itemid)||"A00Z1".equalsIgnoreCase(itemid))//人员序号,发放次数,归属次数 不需要
					continue;
				array.add(new CommonData(itemid,rowSet.getString("itemdesc")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rowSet);	
		}
		return array;
	}


}
