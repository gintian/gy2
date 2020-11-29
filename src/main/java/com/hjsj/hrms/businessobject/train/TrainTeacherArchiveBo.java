package com.hjsj.hrms.businessobject.train;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>
 * Title:TrainTeacherArchiveBo.java
 * </p>
 * <p>
 * Description:培训教师归档
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2012-06-15 13:00:00
 * </p>
 * 
 * @author zhaoxj
 * @version 1.0
 * 
 */
public class TrainTeacherArchiveBo extends TrainArchiveBaseBo
{
    public TrainTeacherArchiveBo(String trainBusiId, Connection cn)
    {
        super(cn, trainBusiId, "2");        
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
        	 FieldItem item=(FieldItem) tempList.get(i);
        	 if("r4116".equalsIgnoreCase(item.getItemid())|| "R4116".equalsIgnoreCase(item.getItemid())) {
                 continue;
             }
            table.addField(item);
        }
        
//        field = new Field("R3101", "培训班编号");
//        field.setDatatype(DataType.STRING);
//        field.setLength(10);
//        table.addField(field);
        Field field=new Field("r4116","培训费用");
        field.setDatatype(DataType.STRING);
        field.setLength(10);
        table.addField(field);
        
        field = new Field("nbase", "人员库");
        field.setDatatype(DataType.STRING);
        field.setLength(10);
        table.addField(field);
        
        field = new Field("a0100", "教师编号");
        field.setDatatype(DataType.STRING);
        field.setLength(10);
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
        for (int i = 0; i < this.tempList.size(); i++)
        {
            FieldItem item = (FieldItem) this.tempList.get(i);
            clms.append(item.getItemid() + ",");
        }
        if (clms.length() > 0) {
            clms.setLength(clms.length() - 1);
        }

        //插入培训教师培训课程信息
        StringBuffer strSql = new StringBuffer();
        strSql.append("INSERT INTO " + tableName);
        strSql.append("(r3101,r3130,r4105,r4106,r4108,r4110,r4112,r4116)");
        strSql.append(" SELECT r31.r3101,r31.r3130,r41.r4105,r41.r4106,r41.r4108,r41.r4110,r41.r4112,r41.r4116");
        strSql.append(" FROM r41,r31 where r41.r4103=r31.r3101 and r31.r3101='");
        strSql.append(this.trainBusiId);
        strSql.append("'");
        strSql.append(" AND EXISTS(SELECT 1 FROM R04 WHERE R04.R0401=R41.R4106");
        strSql.append(" AND " + Sql_switcher.isnull("R04.nbase", "'a'") + "<>'a'");
        strSql.append(" AND " + Sql_switcher.isnull("R04.A0100", "'a'") + "<>'a'");
        if (Constant.MSSQL == Sql_switcher.searchDbServer())
        {
            strSql.append(" AND R04.nbase<>''");
            strSql.append(" AND R04.A0100<>''");
        }
        strSql.append(")");
        
        //更新教师人员库、人员编号
        String srcWhr = Sql_switcher.isnull("R04.nbase", "'a'") + "<>'a'";
        if (Constant.MSSQL == Sql_switcher.searchDbServer())
        {
            srcWhr = srcWhr + " AND R04.nbase<>''";
        }
        
        DbWizard dbWizard = new DbWizard(this.getCn());
        try 
        {
        	dbWizard.execute(strSql.toString());
        	
        	if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                dbWizard.updateRecord(tableName,
                    "r04",
                    tableName + ".r4106=r04.r0401",
                    tableName + ".a0100=r04.a0100," + tableName + ".nbase=r04.nbase",
                    "",
                    srcWhr
                );
            }
			
//			Update destTable
//			  Set (destTable.F1, destTable.F2) =
//			    (SELECT srcTable.F1, srcTable.F2
//			     FROM srcTable
//			     WHERE strOn and srcWhere
//			    )
//			  WHERE destWhere
        	else 
        	{
        	    strSql.setLength(0);
                strSql.append("UPDATE " + tableName);
    			strSql.append(" SET (" + tableName + ".a0100," + tableName + ".nbase)");
    			strSql.append(" =(SELECT r04.a0100,r04.nbase FROM r04");
    			strSql.append(" WHERE " + tableName + ".r4106=r04.r0401 AND " + srcWhr);
    			strSql.append(" )");
    			dbWizard.execute(strSql.toString());
            }  
		    
			
		}
        catch (Exception e) 
        {
        	e.printStackTrace();
			return;
		}
       
        //更新培训项目名称
        try 
        {
        	 dbWizard.updateRecord(tableName, 
             		"r13", 
             		tableName + ".r4105=r13.r1301", 
             		tableName + ".r4105=r13.r1302", 
             		"",
             		"");
		}
        catch (Exception e) 
        {
			return;
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
        
        String itemid = "r3101";        
        fieldItem = new FieldItem();
        fieldItem.setItemid(itemid);
        fieldItem.setItemlength(10);
        fieldItem.setItemtype("A");
        fieldItem.setItemdesc("培训班编号");
        fieldItem.setFieldsetid("R31");
        tempList.add(fieldItem);
                
        LazyDynaBean abean = new LazyDynaBean();
        abean.set("subset", "R31");
        abean.set("id", itemid);
        abean.set("name", "培训班编号");
        abean.set("destFldId", this.getDestFldId(itemid, srcFldSetName));
        abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, fieldItem.getItemtype(), itemid));
        abean.set("destType", this.getDestCodeSet(itemid, srcFldSetName));
        abean.set("mustRela", "1");
        list.add(abean);
        
        itemid = "r3130";        
        fieldItem = new FieldItem();
        fieldItem.setItemid(itemid);
        fieldItem.setItemlength(50);
        fieldItem.setItemtype("A");
        fieldItem.setItemdesc("培训班名称");
        fieldItem.setFieldsetid("R31");
        tempList.add(fieldItem);
                
        abean = new LazyDynaBean();
        abean.set("subset", "R31");
        abean.set("id", itemid);
        abean.set("name", "培训班名称");
        abean.set("destFldId", this.getDestFldId(itemid, srcFldSetName));
        abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, fieldItem.getItemtype(), itemid));
        abean.set("destType", this.getDestCodeSet(itemid, srcFldSetName));
        abean.set("mustRela", "0");
        list.add(abean);

        itemid = "r4105";        
        fieldItem = new FieldItem();
        fieldItem.setItemid(itemid);
        fieldItem.setItemlength(30);
        fieldItem.setItemtype("A");
        fieldItem.setItemdesc("项目名称");
        fieldItem.setFieldsetid("R41");
        tempList.add(fieldItem);
                
        abean = new LazyDynaBean();
        abean.set("subset", "R41");
        abean.set("id", itemid);
        abean.set("name", "项目名称");
        abean.set("destFldId", this.getDestFldId(itemid, srcFldSetName));
        abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, fieldItem.getItemtype(), itemid));
        abean.set("destType", this.getDestCodeSet(itemid, srcFldSetName));
        abean.set("mustRela", "0");
        list.add(abean);
        
        itemid = "r4108";        
        fieldItem = new FieldItem();
        fieldItem.setItemid(itemid);
        fieldItem.setItemlength(18);
        fieldItem.setItemtype("D");
        fieldItem.setItemdesc("课程开始时间");
        fieldItem.setFieldsetid("R41");
        tempList.add(fieldItem);
                
        abean = new LazyDynaBean();
        abean.set("subset", "R41");
        abean.set("id", itemid);
        abean.set("name", "课程开始时间");
        abean.set("destFldId", this.getDestFldId(itemid, srcFldSetName));
        abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, fieldItem.getItemtype(), itemid));
        abean.set("destType", this.getDestCodeSet(itemid, srcFldSetName));
        abean.set("mustRela", "0");
        list.add(abean);
        
        itemid = "r4110";        
        fieldItem = new FieldItem();
        fieldItem.setItemid(itemid);
        fieldItem.setItemlength(18);
        fieldItem.setItemtype("D");
        fieldItem.setItemdesc("课程结束时间");
        fieldItem.setFieldsetid("R41");
        tempList.add(fieldItem);
                
        abean = new LazyDynaBean();
        abean.set("subset", "R41");
        abean.set("id", itemid);
        abean.set("name", "课程结束时间");
        abean.set("destFldId", this.getDestFldId(itemid, srcFldSetName));
        abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, fieldItem.getItemtype(), itemid));
        abean.set("destType", this.getDestCodeSet(itemid, srcFldSetName));
        abean.set("mustRela", "0");
        list.add(abean);
        
        itemid = "r4112";        
        fieldItem = new FieldItem();
        fieldItem.setItemid(itemid);
        fieldItem.setItemlength(30);
        fieldItem.setItemtype("N");
        fieldItem.setItemdesc("课时");
        fieldItem.setFieldsetid("R41");
        tempList.add(fieldItem);
                
        abean = new LazyDynaBean();
        abean.set("subset", "R41");
        abean.set("id", itemid);
        abean.set("name", "课时");
        abean.set("destFldId", this.getDestFldId(itemid, srcFldSetName));
        abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, fieldItem.getItemtype(), itemid));
        abean.set("destType", this.getDestCodeSet(itemid, srcFldSetName));
        abean.set("mustRela", "0");
        list.add(abean);
        
        itemid = "r4116";        
        fieldItem = new FieldItem();
        fieldItem.setItemid(itemid);
        fieldItem.setItemlength(30);
        fieldItem.setItemtype("N");
        fieldItem.setItemdesc("项目费用");
        fieldItem.setFieldsetid("R41");
        tempList.add(fieldItem);
                
        abean = new LazyDynaBean();
        abean.set("subset", "R41");
        abean.set("id", itemid);
        abean.set("name", "项目费用");
        abean.set("destFldId", this.getDestFldId(itemid, srcFldSetName));
        abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, fieldItem.getItemtype(), itemid));
        abean.set("destType", this.getDestCodeSet(itemid, srcFldSetName));
        abean.set("mustRela", "0");
        list.add(abean);
        
        itemid = "r4106";        
        fieldItem = new FieldItem();
        fieldItem.setItemid(itemid);
        fieldItem.setItemlength(30);
        fieldItem.setItemtype("A");
        fieldItem.setItemdesc("培训教师");
        fieldItem.setFieldsetid("R41");
        tempList.add(fieldItem);
//                
//        abean = new LazyDynaBean();
//        abean.set("subset", "R41");
//        abean.set("id", itemid);
//        abean.set("name", "培训教师");
//        abean.set("destFldId", this.getDestFldId(itemid, srcFldSetName));
//        abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, fieldItem.getItemtype(), itemid));
//        abean.set("destType", this.getDestCodeSet(itemid, srcFldSetName));
//        list.add(abean);
        
        this.tempList = tempList;
        return list;
    }
  
    
    /**
     * 保存历史记录到子集表
     */
    @Override
    public boolean save(ArrayList sourceCodes, ArrayList sourceNames, ArrayList destCodes, ArrayList destTypes,
                        String subSet, String userName)
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
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        ContentDAO dao = new ContentDAO(this.getCn());        
        try
        {
            ArrayList nbases = getNbasesFromEmpTab(tableName);
            
            for(int iNbase=0; iNbase<nbases.size(); iNbase++)
            {
                String nbase = (String)nbases.get(iNbase);
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
                    
                    if ("r3101".equalsIgnoreCase(srcFldId))
                    {
                        keyDestFld = destFldId;
                    }
                }
    
                selStr.append("a0100 from " + tableName);
                selStr.append(" WHERE nbase='");
                selStr.append(nbase);
                selStr.append("'");
                selStr.append(" ORDER BY A0100");
                
                inserSql1.append("a0100,i9999)");
                inserSql2.append("?,?)");    
                
                String a0100 = "",preA0100 = "";
                int i9999 = 0;
                
                //归档前，先删除已归过的记录
                if (!"".equals(keyDestFld))
                {
                    delSql.append(" DELETE FROM " + destTab);
                    delSql.append(" WHERE EXISTS(SELECT 1 FROM ");
                    delSql.append(tableName + " A ");
                    delSql.append(" WHERE A.nbase='" + nbase + "'");
                    delSql.append(" AND A.r3101=" + destTab + "." + keyDestFld);
                    delSql.append(")");
                    
                    dao.update(delSql.toString());                    
                }
                
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
                            if(!"D".equals(destType)) {
                                list2.add(rs.getString(srcFldId));
                            } else {
                                list2.add(rs.getDate(srcFldId));
                            }
                        }
                        else                        
                        {
                            //目标字段为代码类型时候要通过代码对应表（trandb_code）来获得目标值
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
                    
                    a0100 = rs.getString("a0100");
                    list2.add(a0100);

                    if(!a0100.equals(preA0100)) {
                        i9999 = this.getI9999(destTab, a0100);
                    } else {
                        i9999++;
                    }
					
                    list2.add(new Integer(i9999));
                    list1.add(list2);
                    
                    preA0100 = a0100;
                }
                String insertSql = inserSql1.toString() + inserSql2.toString();
                
                dao.batchInsert(insertSql, list1);
            }           
        
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
            catch(Exception ex)
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
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        return flag;
    }

    @Override
    public void updateState()
    {
    	
    }
}
