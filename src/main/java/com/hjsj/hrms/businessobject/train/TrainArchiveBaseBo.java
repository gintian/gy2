package com.hjsj.hrms.businessobject.train;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * Title:TrainArchiveBaseBo.java
 * </p>
 * <p>
 * Description:培训模块归档基类
 * </p>
 * <p>
 * Company:hjsoft
 * </p>
 * <p>
 * create time:2012-04-25 09:00:00
 * </p>
 * 
 * @author zhaoxj
 * @version 1.0
 * 
 */
public abstract class TrainArchiveBaseBo
{
    //培训归档方案表
    public static final String TRAIN_ARCHIVE_SCHEMA_TAB = "t_tr_archive_schema";
    
    //培训归档临时表
    protected static final String tableName = "Train_Arch_Temp_Tab"; 

    protected static final String trainArchiveScheType = "100";
    
    //归档分类 null,1: 培训班（学员）归档，2：培训教师归档；3：考试计划（学员成绩）归档
    protected String trainArchiveStatus;  
    
    //培训归档业务id (如：培训班编号、考试计划编号等）
    protected String trainBusiId;

    private Connection cn;
    
    // 临时表字段
    protected ArrayList  tempList; 

    public TrainArchiveBaseBo(Connection cn, String trainBusiId, String trainArchStatus)
    {                    
        this.trainBusiId = delLastCommaSymbol(trainBusiId);
        this.trainArchiveStatus = trainArchStatus;
        setCn(cn);
    }
    
    private String delLastCommaSymbol(String str)
    {
        String newStr = str;
        if(!"".equalsIgnoreCase(newStr))
        {
            newStr = newStr.trim();
            char lastChar = str.charAt(newStr.length() - 1);
            if(",".equalsIgnoreCase(String.valueOf(lastChar))) {
                newStr = newStr.substring(0, newStr.length()-1);
            }
        }
        return newStr;
    }

    protected Connection getCn()
    {
        return this.cn;
    }
    
    private void setCn(Connection cn)
    {
        this.cn = cn;
        modifySchemaTab();
    }
    
    /*
     * 为培训归档方案表添加status字段，以区分培训班、考试、培训教师等归档
     */
    private void modifySchemaTab()
    {           
        Field fld = new Field("status", DataType.INT);
        
        Table tab = new Table(TRAIN_ARCHIVE_SCHEMA_TAB);
        tab.addField(fld);
        
        DbWizard db = new DbWizard(this.cn);
        DBMetaModel dbmodel=new DBMetaModel(this.cn);
        try
        {
            if (!db.isExistField(TRAIN_ARCHIVE_SCHEMA_TAB, "status", false))
            {
                db.addColumns(tab);
                dbmodel.reloadTableModel(TRAIN_ARCHIVE_SCHEMA_TAB);
                
                ContentDAO dao = new ContentDAO(this.cn);
                dao.update("UPDATE " + TRAIN_ARCHIVE_SCHEMA_TAB + " SET status=" + trainArchiveStatus);
            }    
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
    // 生成目标表结构和数据(子类实现）
    public abstract void generateTempTable();

    // 往临时表中插入数据（子类实现）
    protected abstract void insertData();
    
    /*
     * 获得人员子集
     */
    public ArrayList getSubSet()
    {

        ArrayList list = new ArrayList();
        StringBuffer strSql = new StringBuffer();
        strSql.append("SELECT fieldsetid,customdesc FROM fieldset");
        strSql.append(" WHERE fieldsetid like 'A%' AND fieldsetid!='A01'");
        strSql.append(" AND useflag='1'");
        strSql.append(" ORDER BY displayorder");

        try
        {
            ContentDAO dao = new ContentDAO(this.cn);
            RowSet rowSet = dao.search(strSql.toString());
            while (rowSet.next())
            {
                CommonData vo = new CommonData(rowSet.getString("fieldsetid"), rowSet.getString("customdesc"));
                list.add(vo);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return list;
    }

    /*
     * 取得归档子集在方案中的当前值
     */
    public String getSetName()
    {

        String str = "";
        String xmlContent = this.getXML();
        if ("".equals(xmlContent)) {
            return str;
        }
        try
        {
            Document doc = PubFunc.generateDom(xmlContent);;
            String path = "//RelaSet[@SrcFldSet=\"" + tableName + "\"]";
            XPath xpath = XPath.newInstance(path);
            Element ele = (Element) xpath.selectSingleNode(doc);
            if(ele == null)
            {
                //兼容BS之前的错误临时表名tr_Arch_Temp_Tab
                path = "//RelaSet[@SrcFldSet='tr_Arch_Temp_Tab']";
                xpath = XPath.newInstance(path);
                ele = (Element)xpath.selectSingleNode(doc);
            }
            
            if (ele != null)
            {
                str = ele.getAttributeValue("DestFldSet");
            }
        }
        catch (JDOMException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return str;
    }

    /*
     * 获得归档方案内容
     */
    private String getXML()
    {

        String content = "";
        
        StringBuffer sqlStr = new StringBuffer("select bytes from t_tr_archive_schema");
        sqlStr.append(" WHERE status=");
        sqlStr.append(trainArchiveStatus);

        try
        {
            ContentDAO dao = new ContentDAO(this.cn);
            RowSet rs = dao.search(sqlStr.toString());
            if (rs.next()) {
                content = Sql_switcher.readMemo(rs, "bytes");
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 取得考核归档方案中的对应关系
     */
    protected String getDestFldId(String srcFldId, String destFldSet)
    {

        String str = "";
        String xmlContent = this.getXML();
        if ("".equals(xmlContent)) {
            return str;
        }
        try
        {
            Document doc = PubFunc.generateDom(xmlContent);;
            String path = "//RelaSet[@DestFldSet=\"" + destFldSet + "\"]";
            XPath xpath = XPath.newInstance(path);
            Element ele = (Element) xpath.selectSingleNode(doc);
            if (ele == null)
            {
                path = "//RelaSet[@DestFldSet=\"" + destFldSet.toUpperCase() + "\"]";
                xpath = XPath.newInstance(path);
                ele = (Element) xpath.selectSingleNode(doc);
            }
            if (ele == null) {
                return str;
            }

            path = "//RelaFld[@SrcFldId=\"" + srcFldId + "\"]";
            xpath = XPath.newInstance(path);
            ele = (Element) xpath.selectSingleNode(doc);
            
            if (ele == null)
            {
                path = "//RelaFld[@SrcFldId=\"" + srcFldId.toUpperCase() + "\"]";
                xpath = XPath.newInstance(path);
                ele = (Element) xpath.selectSingleNode(doc);
            }
            
            if (ele != null)
            {
                str = ele.getAttributeValue("DestFldId");
            }
        }
        catch (JDOMException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return str;
    }

    protected String getDestCodeSet(String srcFldId, String destFldSet)
    {

        String destCodeSet = "0";
        String xmlContent = this.getXML();
        if ("".equals(xmlContent)) {
            return destCodeSet;
        }
        try
        {
            Document doc = PubFunc.generateDom(xmlContent);
            String xpath = "//RelaSet[@DestFldSet=\"" + destFldSet + "\"]";
            XPath xpath_ = XPath.newInstance(xpath);
            Element ele = (Element) xpath_.selectSingleNode(doc);
            if (ele == null) {
                return destCodeSet;
            }

            xpath = "//RelaFld[@SrcFldId=\"" + srcFldId + "\"]";
            xpath_ = XPath.newInstance(xpath);
            ele = (Element) xpath_.selectSingleNode(doc);
            if (ele != null)
            {
                destCodeSet = ele.getAttributeValue("DestCodeSet");
            }
        }
        catch (JDOMException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return destCodeSet;
    }

    /** 取得源字段的代码类 */
    private String getSourceCodeSet(String srcFldId)
    {

        String destCodeSet = "0";
        srcFldId = srcFldId.toUpperCase();
        try
        {
            ContentDAO dao = new ContentDAO(this.cn);
            StringBuffer strSql = new StringBuffer();
            strSql.append("select * from t_hr_busifield where UPPER(fieldsetid)='");
            strSql.append(srcFldId.substring(0, 3));
            strSql.append("' and itemid='");
            strSql.append(srcFldId);
            strSql.append("'");

            RowSet rowSet = dao.search(strSql.toString());
            if (rowSet.next())
            {
                String temp = rowSet.getString("codesetid");
                if (temp != null) {
                    destCodeSet = temp;
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return destCodeSet;
    }

    /**
     * 得到归档子集指标
     */
    protected ArrayList getDestFldsByType(String setName, String type, String fieldName)
    {

        String codesetid = "0";// 源表中字段的代码类值
        ArrayList list = new ArrayList();
        CommonData vo = new CommonData("", "");
        list.add(vo);

        if ("".equals(setName)) {
            setName = this.getSetName();
        }
        
        if ("".equals(setName)) {
            return list;
        }
        
        fieldName = fieldName.toUpperCase();
        
        try
        {
            String itemlength = "0";// 源表字段的长度
            String decimalwidth = "0";//源表数值类型字段的小数位数
            
            ContentDAO dao = new ContentDAO(this.cn);
            StringBuffer strSql = new StringBuffer();
            strSql.append("select itemlength,codesetid,decimalwidth from t_hr_busifield where UPPER(fieldsetid)='");
            strSql.append(fieldName.substring(0, 3));
            strSql.append("' and UPPER(itemid)='");
            strSql.append(fieldName);
            strSql.append("'");

            RowSet rowSet = dao.search(strSql.toString());
            if (rowSet.next())
            {
                String temp = rowSet.getString("itemlength");
                if (temp != null) {
                    itemlength = temp;
                }
                
                temp = rowSet.getString("codesetid");
                if (temp != null) {
                    codesetid = temp;
                }
                
                temp = rowSet.getString("decimalwidth");
                if (temp != null) {
                    decimalwidth = temp;
                }
            }

            strSql = new StringBuffer();
            strSql.append("select * from fielditem where fieldsetid='");
            strSql.append(setName);
            strSql.append("' and useflag='1' and itemtype='");
            strSql.append(type);
            strSql.append("'");
            // 如果源表字段为非代码类型，代码类型不进行限制因为要进行代码对应
            // 日期型不限制
            if (("0".equals(codesetid)&&(!"D".equalsIgnoreCase(type))&&(!"M".equals(type)))) {
                strSql.append(" and itemlength>=" + itemlength);
            }
            if ("N".equalsIgnoreCase(type)) {
                strSql.append(" and decimalwidth>=" + decimalwidth);
            }
            
            rowSet = dao.search(strSql.toString());
            while (rowSet.next())
            {

                vo = new CommonData(rowSet.getString("itemid"), rowSet.getString("itemdesc"));
                list.add(vo);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return list;
    }

    /*
     * 取得源指标(子类实现）
     */
    public abstract ArrayList getPoints(String srcFldSetName);

    /** 取得目标字段的代码类 */
    public String getTargetCodeSet(String targetFldId, String fldSet)
    {

        targetFldId = targetFldId.toUpperCase();
        fldSet = fldSet.toUpperCase();
        
        String destCodeSet = "0";
        try
        {
            ContentDAO dao = new ContentDAO(this.cn);
            StringBuffer strSql = new StringBuffer();
            strSql.append("select *  from fielditem where UPPER(fieldsetid)='");
            strSql.append(fldSet);
            strSql.append("' and UPPER(itemid)='");
            strSql.append(targetFldId);
            strSql.append("'");

            RowSet rowSet = dao.search(strSql.toString());
            if (rowSet.next())
            {
                String temp = rowSet.getString("codesetid");
                if (temp != null) {
                    destCodeSet = temp;
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return destCodeSet;
    }

    /**
     * 取出未对应的源代码
     */
    public ArrayList getNoAccordCodes(String sourceField, String destCode)
    {

        String sourceCodeSet = this.getSourceCodeSet(sourceField);
        ArrayList list = new ArrayList();
        StringBuffer strSql = new StringBuffer();
        strSql.append("select codeitemid,codeitemdesc from codeitem where codesetid='");
        strSql.append(sourceCodeSet);
        strSql.append("' and codeitemid not in (select code2 from trandb_code where codeset1='");
        strSql.append(destCode);
        strSql.append("') ");
        //区分归档对应时对应的指标是课程分类还是课程名称
        if("r4117".equalsIgnoreCase(sourceField)){
        	strSql.append("and not exists (select 1 from r50 where codeitem.codeitemid=r50.codeitemid) ");
        } else if("r4118".equalsIgnoreCase(sourceField)){
        	strSql.append("and exists (select 1 from r50 where codeitem.codeitemid=r50.codeitemid) ");
        }
        strSql.append("order by codeitemid");

        try
        {
            ContentDAO dao = new ContentDAO(this.cn);
            RowSet rowSet = dao.search(strSql.toString());
            while (rowSet.next())
            {
                String codeitemid = rowSet.getString("codeitemid");
                String codeitemdesc = rowSet.getString("codeitemdesc");
                CommonData vo = new CommonData(codeitemid, codeitemid + ":" + codeitemdesc);
                list.add(vo);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获得目标代码
     */
    public ArrayList getTargetCodes(String destCode,String sourceField)
    {

        ArrayList list = new ArrayList();

        try
        {
            ContentDAO dao = new ContentDAO(this.cn);
            String sql="select codeitemid,codeitemdesc from codeitem where codesetid='" + destCode + "'";
          //区分归档对应时对应的指标是课程分类还是课程名称
            if("r4117".equalsIgnoreCase(sourceField)){
            	sql+="and not exists (select 1 from r50 where codeitem.codeitemid=r50.codeitemid)";
            } else if("r4118".equalsIgnoreCase(sourceField)){
            	sql+="and exists (select 1 from r50 where codeitem.codeitemid=r50.codeitemid)";
            }
            sql+="order by codeitemid";
            RowSet rowSet = dao.search(sql);
            while (rowSet.next())
            {
                CommonData vo = new CommonData(rowSet.getString("codeitemid"), rowSet.getString("codeitemid") + ":"
                        + rowSet.getString("codeitemdesc"));
                list.add(vo);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获得已经对应的代码
     */
    public ArrayList getHaveAccordCodes(String destCode,String sourceField)
    {

        ArrayList list = new ArrayList();

        try
        {
            ContentDAO dao = new ContentDAO(this.cn);
            String sql="select * from trandb_code where codeset1='" + destCode + "'";
            if("r4117".equalsIgnoreCase(sourceField)){
            	sql+=" and not exists (select 1 from r50 where trandb_code.code1=r50.codeitemid)";
            } else if("r4118".equalsIgnoreCase(sourceField)){
            	sql+=" and exists (select 1 from r50 where trandb_code.code1=r50.codeitemid)";
            }
            RowSet rowSet = dao.search(sql);
            while (rowSet.next())
            {
                String code1 = rowSet.getString("code1");
                String code2 = rowSet.getString("code2");
                String codename1 = rowSet.getString("codename1");
                String codename2 = rowSet.getString("codename2");
                String dataText = code2 + ":" + codename2 + "=>" + code1 + ":" + codename1;
                CommonData vo = new CommonData(dataText, dataText);
                list.add(vo);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return list;
    }

    /*
     * 获得trandb_code表Id值
     */
    public int getID()
    {

        int id = 0;
        try
        {
            ContentDAO dao = new ContentDAO(this.cn);
            RowSet rowSet = dao.search("select id from trandb_Scheme where dbtype=" + trainArchiveScheType);
            if (rowSet.next())
            {
                id = rowSet.getInt("id");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return id;
    }

    /**
     * 保存代码对应
     */
    public void saveCodeAccord(String destCode, String accordCodes, String sourceField)
    {
        accordCodes = PubFunc.keyWord_reback(accordCodes);
        
        String codeset2 = this.getSourceCodeSet(sourceField);
        // 先删除原先该类别的所有代码对应
        String delStr = "delete from trandb_code where codeset1='" + destCode + "'";
        if("r4117".equalsIgnoreCase(sourceField)){
        	delStr+=" and not exists (select 1 from r50 where trandb_code.code1=r50.codeitemid)";
        } else if("r4118".equalsIgnoreCase(sourceField)){
        	delStr+=" and exists (select 1 from r50 where trandb_code.code1=r50.codeitemid)";
        }
        try
        {
            ContentDAO dao = new ContentDAO(this.cn);
            dao.delete(delStr, new ArrayList());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if ("".equals(accordCodes)) {
            return;
        }

        // 保存代码对应
        String[] codes = accordCodes.split("<@>");
        int id = this.getID();
        String codeset1 = destCode;

        ArrayList list1 = new ArrayList();

        for (int i = 0; i < codes.length; i++)
        {
            ArrayList list2 = new ArrayList();
            list2.add(codeset2);
            list2.add(new Integer(id));
            list2.add(codeset1);

            if ("".equals(codes[i])) {
                continue;
            }

            String[] codeAccord = codes[i].split("=>");
            String[] sourceCode = codeAccord[0].split(":");
            String code2 = sourceCode[0];
            String codename2 = sourceCode[1];

            String[] targetCode = codeAccord[1].split(":");
            String code1 = targetCode[0];
            String codeName1 = targetCode[1];

            list2.add(code2);
            list2.add(codename2);
            list2.add(code1);
            list2.add(codeName1);

            list1.add(list2);
        }

        String insertSql = "insert into trandb_code(codeset2,id,codeset1,code2,codename2,code1,codeName1) values (?,?,?,?,?,?,?)";
        try
        {
            ContentDAO dao = new ContentDAO(this.cn);
            dao.batchInsert(insertSql, list1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 得到子集字段的名称
     */
    public String getDestFldName(String setName, String destFldId)
    {
        String str = "";
        
        StringBuffer sql = new StringBuffer();
        sql.append("select itemdesc from fielditem");
        sql.append(" where UPPER(fieldsetid)='");
        sql.append(setName.toUpperCase());
        sql.append("'");
        sql.append(" and UPPER(itemid)='");
        sql.append(destFldId.toUpperCase());
        sql.append("'");
        
        try
        {
            ContentDAO dao = new ContentDAO(this.cn);
            RowSet rowSet = dao.search(sql.toString());
            if (rowSet.next()) {
                str = rowSet.getString("itemdesc");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return str;
    }

 
    /*
     * 取归档方案表下一个id
     */
    private int getNextSchemaId()
    {
        int id = 1;
        ContentDAO dao = new ContentDAO(this.cn);
        try
        {
            StringBuffer sql = new StringBuffer();
            sql.append("select ");
            sql.append(Sql_switcher.sqlNull("max(id)",0));
            sql.append("+1 as maxid from t_tr_archive_schema");
            
            RowSet rs = dao.search(sql.toString());
            if(rs.next())
            {
                id = rs.getInt("maxid");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return id;
    }
    
    /**
     * 生成新的归档方案
     * 
     * @throws GeneralException
     */
    public String genetateXML(ArrayList sourceCodes, ArrayList sourceNames, ArrayList destCodes, ArrayList destTypes,
            String setName) throws GeneralException
    {
        String isFlag = "yes";
        if (destCodes == null || destCodes.size() == 0) {
            return "no";
        }

        Element root = new Element("ArchScheme");

        Element relaSet = new Element("RelaSet");
        relaSet.setAttribute("SrcFldSet", tableName);
        relaSet.setAttribute("DestFldSet", setName);
        root.addContent(relaSet);

        for (int i = 0; i < destCodes.size(); i++)
        {
            String destFldId = (String) destCodes.get(i);
            String srcFldId = (String) sourceCodes.get(i);
            String srcFldName = (String) sourceNames.get(i);

            if ("noValue".equals(destFldId))
            {
//                此处逻辑有问题，必填项应该是检查目标指标，而不是检查源指标	
//                if (checkRequired(srcFldId))
//                    return SafeCode.encode("\"" + srcFldName + "\"为必填项！");

                continue;
            }
            String srcCodeSet = this.getSourceCodeSet(srcFldId);
            String destFldName = this.getDestFldName(setName, destFldId);
            String destCodeSet = (String) destTypes.get(i);
            Element relaFld = new Element("RelaFld");
            relaFld.setAttribute("SrcFldId", srcFldId);
            relaFld.setAttribute("SrcFldName", srcFldName);
            relaFld.setAttribute("SrcCodeSet", srcCodeSet);
            relaFld.setAttribute("DestFldId", destFldId);
            relaFld.setAttribute("DestFldName", destFldName);
            relaFld.setAttribute("DestCodeSet", destCodeSet);
            relaSet.addContent(relaFld);
        }

        Document myDocument = new Document(root);
        XMLOutputter outputter = new XMLOutputter();
        Format format = Format.getPrettyFormat();
        format.setEncoding("UTF-8");
        outputter.setFormat(format);
        String xmlContent = outputter.outputString(myDocument);

        RecordVo vo = new RecordVo("t_tr_archive_schema");
        ContentDAO dao = new ContentDAO(this.cn);
        String sql = "";
        RowSet rs = null;
        try
        {
            rs = dao.search("select * from t_tr_archive_schema WHERE status=" + this.trainArchiveStatus);
            if (rs.next())
            {
                sql = "update t_tr_archive_schema set bytes='" + xmlContent + "' where status=" + this.trainArchiveStatus;
                dao.update(sql);
            }
            else
            {
                vo.setInt("id", getNextSchemaId());
                vo.setString("bytes", xmlContent);
                vo.setInt("status", Integer.parseInt(this.trainArchiveStatus));
                dao.addValueObject(vo);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (null != rs)
            {
                try
                {
                    rs.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return isFlag;
    }

    /**
     * 获得归档方案中的代码对应
     */
    public HashMap getCodeAccord(String destFldSet)
    {

        HashMap map = new HashMap();
        String xmlContent = this.getXML();
        try
        {
            Document doc = PubFunc.generateDom(xmlContent);
            String xpath = "//RelaSet[@DestFldSet=\"" + destFldSet + "\"]";
            XPath xpath_ = XPath.newInstance(xpath);
            Element ele = (Element) xpath_.selectSingleNode(doc);
            if (ele != null)
            {
                List list = (List) ele.getChildren("RelaFld");
                for (int i = 0; i < list.size(); i++)
                {
                    Element temp = (Element) list.get(i);
                    String srcFldId = temp.getAttributeValue("SrcFldId");
                    String destFldId = temp.getAttributeValue("DestFldId");
                    //校验归档设置保存的指标是否已构库，如果不存在或未构库则直接跳过
                    FieldItem fi = DataDictionary.getFieldItem(destFldId, destFldSet);
                    if(fi == null || "0".equals(fi.getUseflag())) {
                        continue;
                    }
                    
                    String destCodeSet = temp.getAttributeValue("DestCodeSet");
                    LazyDynaBean abean = new LazyDynaBean();
                    abean.set("srcFldId", srcFldId);
                    abean.set("destCodeSet", destCodeSet);
                    map.put(destFldId, abean);
                }
            }
        }
        catch (JDOMException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 保存历史记录到子集表
     */
    public abstract boolean save(ArrayList sourceCodes, ArrayList sourceNames, ArrayList destCodes, ArrayList destTypes,
            String subSet, String userName);

    /**
     * 从学员表中取得培训学员人员库列表
     * @param tab
     * @return
     */
    protected ArrayList getNbasesFromEmpTab(String tab)
    {
        ArrayList nbases = new ArrayList();
        
        String sql = "SELECT distinct nbase FROM " + tab;
        ContentDAO dao = new ContentDAO(this.cn);
        try
        {
            RowSet rs = dao.search(sql);
            while(rs.next()) {
                nbases.add(rs.getString("nbase"));
            }
            rs.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return nbases;
    }
    
    /**
     * 目标字段为代码类型时候要通过代码对应表（trandb_code）来获得目标值
     */
    public String getCode1(int id, String code2, String codeset1)
    {

        String str = "";
        try
        {
            ContentDAO dao = new ContentDAO(this.cn);
            String sql = "select code1 from trandb_code where id=" + new Integer(id).toString() + " and codeset1='"
                    + codeset1 + "' and code2='" + code2 + "'";
            RowSet rowSet = dao.search(sql);
            if (rowSet.next()) {
                str = rowSet.getString("code1");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return str;
    }
    
    /**
     * 获得源指标的数据类型
     */
    protected String getSrcFldType(String srcFld)
    {
        String itemType = "";     
        
        if(this.tempList!=null)
        {
            for(int i = 0; i < this.tempList.size(); i++)
            {
                FieldItem item = (FieldItem)tempList.get(i);
                if(item.getItemid().equals(srcFld))
                {
                    itemType = item.getItemtype();
                    break;
                }            
            }
        }
        else
        {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT itemtype FROM t_hr_busifield");
            sql.append(" WHERE UPPER(itemid)='");
            sql.append(srcFld.toUpperCase());
            sql.append("'");
            
            ContentDAO dao = new ContentDAO(this.cn);
            try
            {
                RowSet rs = dao.search(sql.toString());
                if(rs.next())
                {
                    itemType = rs.getString("itemtype");
                }
                rs.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        
        return itemType;
    }
    
    /** 取得子集字段的长度 */
    public int getSubSetFldLen(String setName, String fldName)
    {
        int str = 1;
        try
        {
            ContentDAO dao = new ContentDAO(this.cn);
            String sql = "select * from fielditem where UPPER(fieldsetid)='" + setName.toUpperCase() + "' and UPPER(itemid)='" + fldName.toUpperCase() + "'";
            RowSet rowSet = dao.search(sql);
            if (rowSet.next()) {
                str = rowSet.getInt("itemlength");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return str;
    }

    /*
     * 取得对象的个数
     */
    public int getI9999(String setName, String object_id)
    {

        int count = 0;
        StringBuffer selStr = new StringBuffer();
        selStr.append("select ");
        selStr.append(Sql_switcher.sqlNull("max(i9999)", "0"));
        selStr.append(" n from " + setName);
        selStr.append(" where a0100='");
        selStr.append(object_id);
        selStr.append("'");
        
        try
        {
            ContentDAO dao = new ContentDAO(this.cn);
            RowSet rs = dao.search(selStr.toString());
            if (rs.next()) {
                count = rs.getInt(1) + 1;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return count;
    }
  

    /**
     * 检查该指标是否为必填项 2011-09-08 11:26:23
     * 
     * @param itemid
     * @return
     */
    private boolean checkRequired(String itemid)
    {
        FieldItem fielditem = DataDictionary.getFieldItem(itemid);
        return fielditem.isFillable();
    }
    
    public abstract void updateState();
}
