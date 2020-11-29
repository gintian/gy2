package com.hjsj.hrms.businessobject.train;

import com.hjsj.hrms.businessobject.train.trainexam.exam.TrainExamStudentBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
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

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>
 * Title:TrainClassArchiveBo.java
 * </p>
 * <p>
 * Description:培训考试计划归档
 * </p>
 * <p>
 * Company:hjsoft
 * </p>
 * <p>
 * create time:2012-04-25 13:22:00
 * </p>
 * 
 * @author zhaoxj
 * @version 1.0
 * 
 */
public class TrainExamArchiveBo extends TrainArchiveBaseBo
{
    public TrainExamArchiveBo(String trainBusiId, Connection cn)
    {
        super(cn, trainBusiId, "3");
    }

    // 生成目标表结构和数据
    @Override
    public void generateTempTable()
    {
        DbWizard dbWizard = new DbWizard(this.getCn());
        if (dbWizard.isExistTable(tableName, false))
        {
            dbWizard.dropTable(tableName);
        }

        // 创建表
        Table table = new Table(tableName);
        for (int i = 0; i < this.tempList.size(); i++)
        {
            table.addField((FieldItem) tempList.get(i));
        }
        
        Field field = new Field("a0100", "人员编号");
        field.setDatatype(DataType.STRING);
        field.setLength(8);
        table.addField(field);
        
        field = new Field("a0101", "姓名");
        field.setDatatype(DataType.STRING);
        field.setLength(50);
        table.addField(field);
        
        field = new Field("nbase", "人员库");
        field.setDatatype(DataType.STRING);
        field.setLength(8);
        table.addField(field);
        
        try
        {
            dbWizard.createTable(table);
        }
        catch (GeneralException e1)
        {
            e1.printStackTrace();
        }        

        this.insertData();

    }

    // 往临时表中插入数据
    @Override
    public void insertData()
    {
        StringBuffer clms = new StringBuffer();
        StringBuffer clmsWithSet = new StringBuffer();
        
        for (int i = 0; i < this.tempList.size(); i++)
        {
            FieldItem item = (FieldItem) this.tempList.get(i);
            clms.append(item.getItemid() + ",");
            clmsWithSet.append(item.getFieldsetid() + "." + item.getItemid() + ",");
        }
        
        if (clms.length() > 0)
        {
            clms.setLength(clms.length() - 1);
            clmsWithSet.setLength(clmsWithSet.length() - 1);
        }

        StringBuffer strSql = new StringBuffer();
        strSql.append("INSERT INTO " + tableName);
        strSql.append("(a0100,a0101,nbase," + clms + ")");
        strSql.append(" SELECT a0100,a0101,nbase," + clmsWithSet);
        strSql.append(" FROM R55 LEFT JOIN R54");
        strSql.append(" ON R55.R5400=R54.R5400");
        strSql.append(" WHERE R55.R5400 IN (");
        strSql.append(this.trainBusiId + ")");
        strSql.append(" AND R54.R5400 IN (");
        strSql.append(this.trainBusiId + ")");

        ContentDAO dao = new ContentDAO(this.getCn());
        try
        {
            dao.update(strSql.toString());   
            
            //将试卷状态转成汉字描述
            TrainExamStudentBo stuBo = new TrainExamStudentBo();
            ArrayList r5513List = stuBo.getPaperStatusList();
            for(int i=0; i<r5513List.size(); i++)
            {
            	CommonData data = (CommonData)r5513List.get(i);
            
            	strSql.delete(0, strSql.length());
            	strSql.append("UPDATE ");
            	strSql.append(tableName);
            	strSql.append(" SET r5513='" + data.getDataName() + "'");
            	strSql.append(" WHERE r5513='" + data.getDataValue() + "'");
            	dao.update(strSql.toString());
            }
            
            //将阅卷状态转成汉字描述
            ArrayList r5515List = stuBo.getCheckStatusList();
            for(int i=0; i<r5515List.size(); i++)
            {
            	CommonData data = (CommonData)r5515List.get(i);
            
            	strSql.delete(0, strSql.length());
            	strSql.append("UPDATE ");
            	strSql.append(tableName);
            	strSql.append(" SET r5515='" + data.getDataName() + "'");
            	strSql.append(" WHERE r5515='" + data.getDataValue() + "'");
            	dao.update(strSql.toString());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /*
     * 取得源指标
     */
    @Override
    public ArrayList getPoints(String srcFldSetName)
    {
        ArrayList list = new ArrayList();
        ArrayList tempList = new ArrayList();// 用于创建临时表

        FieldItem fieldItem = null;
        
        String itemid = "r5400";        
        fieldItem = new FieldItem();
        fieldItem.setItemid(itemid);
        fieldItem.setItemlength(8);
        fieldItem.setItemtype("N");
        fieldItem.setItemdesc("考试计划编号");
        fieldItem.setFieldsetid("R54");
        tempList.add(fieldItem);
                
        LazyDynaBean abean = new LazyDynaBean();
        abean.set("subset", "R54");
        abean.set("id", itemid);
        abean.set("name", "考试计划编号");
        abean.set("destFldId", this.getDestFldId(itemid, srcFldSetName));
        abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, fieldItem.getItemtype(), itemid));
        abean.set("destType", this.getDestCodeSet(itemid, srcFldSetName));
        abean.set("mustRela", "1");
        list.add(abean);
        
        itemid = "r5401";        
        fieldItem = new FieldItem();
        fieldItem.setItemid(itemid);
        fieldItem.setItemlength(200);
        fieldItem.setItemtype("A");
        fieldItem.setItemdesc("考试计划名称");
        fieldItem.setFieldsetid("R54");
        tempList.add(fieldItem);
                
        abean = new LazyDynaBean();
        abean.set("subset", "R54");
        abean.set("id", itemid);
        abean.set("name", "考试计划名称");
        abean.set("destFldId", this.getDestFldId(itemid, srcFldSetName));
        abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, fieldItem.getItemtype(), itemid));
        abean.set("destType", this.getDestCodeSet(itemid, srcFldSetName));
        abean.set("mustRela", "0");
        list.add(abean);
        
        //参加考试人员指标
        ArrayList fieldlist = DataDictionary.getFieldList("R55", 1);
        for (int i = 0; i < fieldlist.size(); i++)
        {
            fieldItem = (FieldItem) fieldlist.get(i);

            itemid = fieldItem.getItemid();
            if ("b0110".equalsIgnoreCase(itemid) 
                    || "e01A1".equalsIgnoreCase(itemid) 
                    || "e0122".equalsIgnoreCase(itemid)
                    || "a0100".equalsIgnoreCase(itemid)
                    || "a0101".equalsIgnoreCase(itemid)
                    || "nbase".equalsIgnoreCase(itemid)
                    || "r5400".equalsIgnoreCase(itemid)
                ) {
                continue;
            }

            if (("r5513").equalsIgnoreCase(itemid)||("r5515").equalsIgnoreCase(itemid))
            {
                fieldItem = (FieldItem)fieldItem.clone();//复制指标，否则其它用到的地方，指标类型也会被改变
            	fieldItem.setItemtype("A");
            	fieldItem.setItemlength(30);
            }
            
            fieldItem.setFieldsetid("R55");
            
            abean = new LazyDynaBean();
            abean.set("subset", "R55");
            abean.set("id", itemid);
            abean.set("name", fieldItem.getItemdesc());      
            abean.set("destFldId", this.getDestFldId(itemid, srcFldSetName));
            abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, fieldItem.getItemtype(), itemid));
            abean.set("destType", this.getDestCodeSet(itemid, srcFldSetName));
            abean.set("mustRela", "0");
            
            list.add(abean);
            
            tempList.add(fieldItem);
        }
        
        this.tempList = tempList;
        return list;
    }

    /**
     * 保存历史记录到子集表
     */
    @Override
    public boolean save(ArrayList sourceCodes, ArrayList sourceNames, ArrayList destCodes, ArrayList destTypes, String subSet, String userName)
    {
        boolean flag = true;
        String createUserName = userName;
        String modUserName = userName;
        String creatTime = Sql_switcher.dateValue(PubFunc.getStringDate("yyyy-MM-dd hh:mm:ss"));
        String modTime = creatTime;
        String destTab = "";
        String keyDestFld = "";

        boolean autoCommit = true;
        try
        {
            autoCommit = this.getCn().getAutoCommit();
            this.getCn().setAutoCommit(false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        ContentDAO dao = new ContentDAO(this.getCn());
        try
        {
            ArrayList nbases = getNbasesFromEmpTab(tableName);

            for (int iNbase = 0; iNbase < nbases.size(); iNbase++)
            {
                String nbase = (String) nbases.get(iNbase);
                destTab = nbase + subSet;

                StringBuffer inserSql1 = new StringBuffer();
                StringBuffer inserSql2 = new StringBuffer();
                StringBuffer delSql = new StringBuffer();
                
                ArrayList list1 = new ArrayList();
                
                HashMap map = this.getCodeAccord(subSet);
                if (map.size() == 0) {
                    return flag;
                }

                inserSql1.append("insert into ");
                inserSql1.append(destTab);
                inserSql1.append("(createtime,modtime,createusername,modusername,");
                inserSql2.append(" values (" + creatTime + "," + modTime + ",'" + createUserName + "','" + modUserName + "',");

                StringBuffer selStr = new StringBuffer();
                selStr.append("select ");
                
                Set keySet = map.keySet();
                ArrayList destFldIdList = new ArrayList();
                for (Iterator iter = keySet.iterator(); iter.hasNext();)
                {
                    String destFldId = (String) iter.next();
                    inserSql1.append(destFldId + ",");
                    inserSql2.append("?,");
                    
                    LazyDynaBean abean = (LazyDynaBean) map.get(destFldId);
                    String srcFldId = (String) abean.get("srcFldId");
                    selStr.append(srcFldId + ",");           
                    
                    destFldIdList.add(destFldId);
                    
                    if ("r5400".equalsIgnoreCase(srcFldId))
                    {
                        keyDestFld = destFldId;
                    }
                }

                
                selStr.append("a0100,a0101,nbase from " + tableName);
                selStr.append(" WHERE nbase='");
                selStr.append(nbase);
                selStr.append("'");
                selStr.append(" ORDER BY A0100,r5400");

                inserSql1.append("a0100,i9999)");
                inserSql2.append("?,?)");
                
                //归档前，先删除已归过的记录
                if (!"".equals(keyDestFld))
                {
                    delSql.append(" DELETE FROM " + destTab);
                    delSql.append(" WHERE EXISTS(SELECT 1 FROM ");
                    delSql.append(tableName + " A ");
                    delSql.append(" WHERE A.nbase='" + nbase + "'");
                    delSql.append(" AND A.r5400=" + destTab + "." + keyDestFld);
                    delSql.append(")");
                    
                    dao.update(delSql.toString());
                }

                String curA0100 = "";
                int i9999 = 0;
                                
                RowSet rs = dao.search(selStr.toString());
                while (rs.next())
                {
                    ArrayList list2 = new ArrayList();
                    // 归档方案中指定了的目标字段
                    for (int i = 0; i < destFldIdList.size(); i++)
                    {
                        String destFldId = (String) destFldIdList.get(i);
                        LazyDynaBean abean = (LazyDynaBean) map.get(destFldId);
                        String srcFldId = (String) abean.get("srcFldId");
                        String destCodeSet = (String) abean.get("destCodeSet");
                        String destType = getSrcFldType(srcFldId);

                        if ("0".equals(destCodeSet))
                        {
                            if (!"D".equals(destType)) {
                                list2.add(rs.getString(srcFldId));
                            } else {
                                list2.add(rs.getDate(srcFldId));
                            }
                        }
                        else
                        {
                            // 目标字段为代码类型时候要通过代码对应表（trandb_code）来获得目标值
                            int id = this.getID();
                            String code2 = rs.getString(srcFldId);
                            String codeset1 = destCodeSet;
                            String temp = getCode1(id, code2, codeset1);
                            if ("".equals(temp))// 如果没有设置代码对应，则在目标字段的长度范围内存入源字段的值
                            {
                                int len = this.getSubSetFldLen(subSet, destFldId);
                                String temp1 = rs.getString(srcFldId);
                                if (temp1 != null)
                                {
                                    if (temp1.length() > len) {
                                        temp1 = temp1.substring(0, len);
                                    }
                                    list2.add(temp1);
                                }
                                else {
                                    list2.add("");
                                }
                            }
                            else {
                                list2.add(temp);
                            }
                        }
                    }
                    
                    String a0100 = rs.getString("a0100");                    
                    list2.add(a0100);

                    //对每个人取最新的I9999，如果为同一人则直接+1
                    if (!curA0100.equalsIgnoreCase(a0100))
                    {
                        i9999 = this.getI9999(destTab, a0100);
                        curA0100 = a0100;
                    }
                    else 
                    {
                        i9999++;
                    }                    
                    
                    list2.add(new Integer(i9999));
                    list1.add(list2);
                }
                String insertSql = inserSql1.toString() + inserSql2.toString();

                dao.batchInsert(insertSql, list1);
            }

            // 更新培训班状态为结束
            //dao.update("update r31 set r3127='06' where r3101=" + this.trainBusiId);

            this.getCn().commit();
        }
        catch (Exception e)
        {
            flag = false;
            e.printStackTrace();
            try
            {
                this.getCn().rollback();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        finally
        {
            try
            {
                this.getCn().setAutoCommit(autoCommit);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return flag;
    }

    /* 更新培训班和培训计划的状态 */
    @Override
    public void updateState()
    {
        
    }

}
