package com.hjsj.hrms.businessobject.train;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
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
import java.sql.SQLException;
import java.util.*;

/**
 * <p>
 * Title:TrainClassArchiveBo.java
 * </p>
 * <p>
 * Description:培训班归档
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-06-28 13:00:00
 * </p>
 * 
 * <p>
 * modify time:2012-07-30
 * </p>
 * @author FanZhiGuo,zhaoxj
 *
 * @version 2.0
 * 
 */
public class TrainClassArchiveBo extends TrainArchiveBaseBo
{
    public TrainClassArchiveBo(String trainBusiId, Connection cn)
    {
        super(cn, trainBusiId, "1");        
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

        // 创建表 LiWeiChao
        Table table = new Table(tableName);
        for (int i = 0; i < this.tempList.size(); i++)
        {   
            table.addField((FieldItem) tempList.get(i));
        }
//        Field field = new Field("R3101", "培训班编号");
//        field.setDatatype(DataType.STRING);
//        field.setLength(10);
//        table.addField(field);
        
        Field field = new Field("nbase", "人员库");
        field.setDatatype(DataType.STRING);
        field.setLength(10);
        table.addField(field);
        
        field = new Field("R4001", "学员编号");
        field.setDatatype(DataType.STRING);
        field.setLength(10);
        table.addField(field);
        
        field = new Field("R4002", "学员姓名");
        field.setDatatype(DataType.STRING);
        field.setLength(30);
        table.addField(field);
        
        field = new Field("R4005", "参加培训班编号");
        field.setDatatype(DataType.STRING);
        field.setLength(10);
        table.addField(field);
        
        field = new Field("B0110", "单位");
        field.setDatatype(DataType.STRING);
        field.setLength(30);
        table.addField(field);
        
        field = new Field("E0122", "部门");
        field.setDatatype(DataType.STRING);
        field.setLength(30);
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
    	ContentDAO dao = new ContentDAO(this.getCn());
    	boolean flag = false;
    	RowSet rs = null;
    	String sql="select 1 from r41 where r4103='" + this.trainBusiId + "'";
    	try{
	    	rs = dao.search(sql);
	    	if(rs.next()) {
                flag = true;
            }
	    	ArrayList fitemlist = new ArrayList();
			boolean f = false;
	        StringBuffer clms = new StringBuffer();
	        StringBuffer clms1 = new StringBuffer();
	        String clms2 = "";
	        for (int i = 0; i < this.tempList.size(); i++)
	        {
	            FieldItem item = (FieldItem) this.tempList.get(i);
				if (!flag && ("r41".equalsIgnoreCase(item.getFieldsetid())
								|| "lprogress".equals(item.getItemid()) || "state".equals(item.getItemid()))) {
                    continue;
                }
	            if("r3101".equals(item.getItemid())||"e01a1".equals(item.getItemid())) {
                    clms1.append("b."+item.getItemid() + ",");
                } else if(flag && ("lprogress".equals(item.getItemid())||"state".equals(item.getItemid()))){
	            	f=true;
	            	clms1.append("a."+item.getItemid() + ",");
	            }
	            else {
                    clms1.append(item.getItemid() + ",");
                }
	            clms.append(item.getItemid() + ",");
	            fitemlist.add(item);
	        }
	        if (clms.length() > 0) {
                clms.setLength(clms.length() - 1);
            }
			if (clms1.length() > 0) {
                clms1.setLength(clms1.length() - 1);
            }
			if (clms.indexOf(",lprogress")>-1) {
                clms2=clms.substring(0, clms.indexOf(",lprogress"));
            } else {
                clms2=clms.toString();
            }
	
	        StringBuffer strSql = new StringBuffer();
	        if(f) {
                strSql.append("select b.nbase,b.r4001,b.r4002,b.b0110,b.e0122,b.r4005," + clms1.toString()+" from (");
            }
	        strSql.append("select r40.nbase,r40.r4001,r40.r4002,r40.b0110,r40.e0122,r40.r4005," + clms2);
	        
	        strSql.append(" from r40,r31");
	        if(flag) {
                strSql.append(",r41");
            }
	        strSql.append(" where r40.r4005=r31.r3101");
	        if(flag) {
                strSql.append(" and r41.r4103=r31.r3101");
            }
	        strSql.append(" and r31.r3101='" + this.trainBusiId);
	        strSql.append("' and r40.r4013 = '03'");
	        if(f && flag) {
                strSql.append(") b left join tr_selected_lesson a on b.r4001=a.a0100 and b.r4118=(select codeitemid from r50 where r5000 = a.r5000)");
            }
	
	        StringBuffer insertsql1 = new StringBuffer();
	        insertsql1.append("insert into ");
	        insertsql1.append(tableName);
	        insertsql1.append("(nbase,r4001,r4002,b0110,e0122,r4005,");
	        insertsql1.append(clms.toString() + ")");
	
	        StringBuffer insertsql2 = new StringBuffer();
	        insertsql2.append("values (?,?,?,?,?,");
	        for (int i = 0; i < fitemlist.size()/* count - 1 */; i++){
	            insertsql2.append("?,");
	        }
	        insertsql2.append("?)");
	
	        rs = null;
        
            ArrayList l1 = new ArrayList();
            rs = dao.search(strSql.toString());
            while (rs.next())
            {
                ArrayList l2 = new ArrayList();                

                //l2.add(rs.getString("r3101"));
                l2.add(rs.getString("nbase"));
                l2.add(rs.getString("r4001"));
                l2.add(rs.getString("r4002"));
                l2.add(rs.getString("b0110"));
                l2.add(rs.getString("e0122"));
                l2.add(rs.getString("r4005"));
                for (int i = 0; i < fitemlist.size(); i++)
                {
                    FieldItem item = (FieldItem) fitemlist.get(i);
                   
                    String itemType = item.getItemtype();
                    String itemId = item.getItemid();
                    
                    if ("M".equalsIgnoreCase(itemType)) {
                        l2.add(Sql_switcher.readMemo(rs, itemId));
                    } else if ("D".equalsIgnoreCase(itemType)) {
                        l2.add(rs.getDate(itemId));
                    } else {
                        l2.add(rs.getString(itemId));
                    }
                }

                l1.add(l2);
            }

            String insertSql = insertsql1.toString() + insertsql2.toString();

            dao.batchInsert(insertSql, l1);
			if (f) {
				ArrayList stateList = getLessonStatusList();
				for (int i = 0; i < stateList.size(); i++) {
					CommonData data = (CommonData) stateList.get(i);

					strSql.delete(0, strSql.length());
					strSql.append("UPDATE ");
					strSql.append(tableName);
					strSql.append(" SET state='" + data.getDataName() + "'");
					strSql.append(" WHERE state='" + data.getDataValue() + "'");
					dao.update(strSql.toString());
				}
			}
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeRowSet(rs);
        }
    }

 
    /*
     * 取得源指标 LiWeichao 改
     */
    @Override
    public ArrayList getPoints(String srcFldSetName)
    {
        ArrayList list = new ArrayList();
        ArrayList tempList = new ArrayList();// 用于创建临时表 LiWeichao
        ArrayList fieldlist = DataDictionary.getFieldList("R31", 1);// 培训班指标
        for (int i = 0; i < fieldlist.size(); i++)
        {
            FieldItem fieldItem = (FieldItem) fieldlist.get(i);
            if ("b0110".equalsIgnoreCase(fieldItem.getItemid()) 
                    || "e0122".equalsIgnoreCase(fieldItem.getItemid())
                    || "r3125".equalsIgnoreCase(fieldItem.getItemid())
                    || "0".equalsIgnoreCase(fieldItem.getState())) {
                continue;
            }
            // 过滤关联型源指标
			String itemid = fieldItem.getItemid().toUpperCase();
			String sql = "select itemtype, codeflag from t_hr_busifield where fieldsetid='R31' and itemid='" + itemid + "'";
			ContentDAO dao = new ContentDAO(this.getCn());
			RowSet rs = null;
			try {
				rs = dao.search(sql);
				if(rs.next()){
					String itemtype = rs.getString("itemtype");
					String codeflag = rs.getString("codeflag");
					if ("A".equals(itemtype) && "1".equals(codeflag)){
						continue;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				try{
					if(rs != null) {
                        rs.close();
                    }
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
            LazyDynaBean abean = new LazyDynaBean();
            abean.set("id", fieldItem.getItemid());
            abean.set("name", fieldItem.getItemdesc());
            abean.set("destFldId", this.getDestFldId(fieldItem.getItemid(), srcFldSetName));
            abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, fieldItem.getItemtype(), fieldItem.getItemid()));
            abean.set("destType", this.getDestCodeSet(fieldItem.getItemid(), srcFldSetName));
            
            if ("r3101".equalsIgnoreCase(fieldItem.getItemid())) {
                abean.set("mustRela", "1");
            } else {
                abean.set("mustRela", "0");
            }
            
            list.add(abean);
            tempList.add(fieldItem);
        }

        fieldlist = DataDictionary.getFieldList("R40", 1);// 培训学员指标
        for (int i = 0; i < fieldlist.size(); i++)
        {
            FieldItem fieldItem = (FieldItem) fieldlist.get(i);
            if ("b0110".equalsIgnoreCase(fieldItem.getItemid()) 
                    || "r4001".equalsIgnoreCase(fieldItem.getItemid())
                    || "r4002".equalsIgnoreCase(fieldItem.getItemid())
                    || "r4005".equalsIgnoreCase(fieldItem.getItemid())
                    || "e0122".equalsIgnoreCase(fieldItem.getItemid())
                    || "0".equalsIgnoreCase(fieldItem.getState())) {
                continue;
            }
            
            LazyDynaBean abean = new LazyDynaBean();
            abean.set("id", fieldItem.getItemid());
            abean.set("name", fieldItem.getItemdesc());
            abean.set("destFldId", this.getDestFldId(fieldItem.getItemid(), srcFldSetName));
            abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, fieldItem.getItemtype(), fieldItem.getItemid()));
            abean.set("destType", this.getDestCodeSet(fieldItem.getItemid(), srcFldSetName));
            abean.set("mustRela", "0");
            
            list.add(abean);
            tempList.add(fieldItem);
        }
        RowSet rs = null;
        RowSet rss = null;
        RowSet r = null;
		try {
			ContentDAO dao = new ContentDAO(this.getCn());
			fieldlist = DataDictionary.getFieldList("R41", 1);// 培训班培训课程信息表指标
			for (int i = 0; i < fieldlist.size(); i++) {
				FieldItem fieldItem = (FieldItem) fieldlist.get(i);
				if("R4103".equalsIgnoreCase(fieldItem.getItemid())|| "0".equalsIgnoreCase(fieldItem.getState())) {
                    continue;
                }
				// 过滤关联型源指标
				String itemid = fieldItem.getItemid().toUpperCase();
				String sqls = "select itemtype, codeflag from t_hr_busifield where fieldsetid='R41' and itemid='" + itemid + "'";
				rss = dao.search(sqls);
				if (rss.next()) {
					String itemtype = rss.getString("itemtype");
					String codeflag = rss.getString("codeflag");
					if ("A".equals(itemtype) && "1".equals(codeflag)) {
						continue;
					}
				}

				LazyDynaBean abean = new LazyDynaBean();
				abean.set("id", fieldItem.getItemid());
				abean.set("name", fieldItem.getItemdesc());
				abean.set("destFldId", this.getDestFldId(fieldItem.getItemid(),
						srcFldSetName));
				abean.set("destFldIds", this.getDestFldsByType(srcFldSetName,
						fieldItem.getItemtype(), fieldItem.getItemid()));
				abean.set("destType", this.getDestCodeSet(
						fieldItem.getItemid(), srcFldSetName));
				abean.set("mustRela", "0");

				list.add(abean);
				tempList.add(fieldItem);
			}
			String sql = "select FieldSetId,itemid,codesetid from t_hr_busifield where FieldSetId='R41' and itemid='R4118' and state =1 and useflag=1";
			rs = dao.search(sql);
			if(rs.next()){
				FieldItem fieldItem = new FieldItem();
				fieldItem.setFieldsetid("tr_selected_lesson");
				fieldItem.setItemid("lprogress");
				fieldItem.setItemdesc("学习进度");
				fieldItem.setItemtype("N");
				fieldItem.setItemlength(3);
				
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("id", fieldItem.getItemid());
				abean.set("name", fieldItem.getItemdesc());
				abean.set("destFldId", this.getDestFldId(fieldItem.getItemid(), srcFldSetName));
				abean.set("destFldIds", this.getDestFldsByType(srcFldSetName, fieldItem.getItemtype(), fieldItem.getItemid()));
				abean.set("destType", this.getDestCodeSet(fieldItem.getItemid(), srcFldSetName));
				abean.set("mustRela", "0");

				list.add(abean);
				tempList.add(fieldItem);
				
				FieldItem fieldItem1 = new FieldItem();
				fieldItem1.setFieldsetid("tr_selected_lesson");
				fieldItem1.setItemid("state");
				fieldItem1.setItemdesc("学习状态");
				fieldItem1.setItemtype("A");
				fieldItem1.setItemlength(30);
				
				LazyDynaBean abean1 = new LazyDynaBean();
				abean1.set("id", fieldItem1.getItemid());
				abean1.set("name", fieldItem1.getItemdesc());
				abean1.set("destFldId", this.getDestFldId(fieldItem1.getItemid(), srcFldSetName));
				abean1.set("destFldIds", this.getDestFldsByType(srcFldSetName, fieldItem1.getItemtype(), fieldItem1.getItemid()));
				abean1.set("destType", this.getDestCodeSet(fieldItem1.getItemid(), srcFldSetName));
				abean1.set("mustRela", "0");

				list.add(abean1);
				tempList.add(fieldItem1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch (Exception e) {
				e.printStackTrace();	
			}
			try{
				if(rss!=null) {
                    rss.close();
                }
			}catch (Exception e) {
				e.printStackTrace();	
			}
			try{
				if(r!=null) {
                    r.close();
                }
			}catch (Exception e) {
				e.printStackTrace();	
			}
		}
		
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
        String keyDestFld = ""; //目标关键指标（子集中的培训班编号指标）

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
        
        RowSet rs = null;
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
    
                //未归过档的 新增sql
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
    
                selStr.append("r4001 from " + tableName);
                selStr.append(" WHERE nbase='");
                selStr.append(nbase);
                selStr.append("'");
                
                inserSql1.append("a0100,i9999)");
                inserSql2.append("?,?)");    
                
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
                HashMap valuemap = new HashMap();
                ArrayList r4001list = new ArrayList();
                rs = dao.search(selStr.toString());
                while (rs.next())
                {
                    ArrayList list2 = new ArrayList();
                    String value = "";
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
                            if(!"D".equals(destType)){
                                if ("r3101".equalsIgnoreCase(srcFldId) || "r4101".equalsIgnoreCase(srcFldId) || "r4118".equalsIgnoreCase(srcFldId)) {
                                    value += rs.getString(srcFldId);
                                }
                                
                                list2.add(rs.getString(srcFldId));
                            }else{
                                if("r4108".equalsIgnoreCase(srcFldId) || "r4110".equalsIgnoreCase(srcFldId)){
                                   Date date = rs.getDate(srcFldId);
                                   FieldItem fi = DataDictionary.getFieldItem(srcFldId, "r41");
                                   String datetyle = getDatesTyle(fi); 
                                   value += DateUtils.format(date, datetyle);
                                }
                                
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
                    String r4001 = rs.getString("r4001");
                    value +=r4001;
                    list2.add(r4001);
                    
                    if(valuemap.containsKey(value)) {
                        continue;
                    } else {
                        valuemap.put(value, "1");
                    }
                    
                    int i9999 = this.getI9999(destTab, r4001);
                    
					if (r4001list.size() > 0) {
						for (int i = 0; i < r4001list.size(); i++) {
							if (r4001.equals(r4001list.get(i))) {
                                i9999 += 1;
                            }
						}
					}
					r4001list.add(r4001);
                    
                    list2.add(new Integer(i9999));
                    list1.add(list2);
                }
                String insertSql = inserSql1.toString() + inserSql2.toString();
                
                dao.batchInsert(insertSql, list1);
            }           
        
            // 更新培训班状态为结束
            dao.update("update r31 set r3127='06' where r3101='" + this.trainBusiId + "'");
            
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
            
            closeRowSet(rs);
        }

        return flag;
    }

    /* 更新培训班和培训计划的状态 */
    @Override
    public void updateState()
    {
        // 把此培训班设置为结束状态
        String sql = "update r31 set r3127='06' where r3101='" + this.trainBusiId + "'";
        String sql2 = "select r3125 from r31 where r3101='" + this.trainBusiId + "'";
        String relaPLan = "";
        ContentDAO dao = new ContentDAO(this.getCn());
        RowSet rs = null;
        try
        {
            dao.update(sql);
            rs = dao.search(sql2);
            if (rs.next()) {
                relaPLan = rs.getString("r3125");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeRowSet(rs);
        }
        
        if (relaPLan == null || (relaPLan != null && "".equals(relaPLan))) {
            return;
        }
        
        // 另外如果此培训班关联了计划，如果此计划下的所有培训班状态都为结束状态时，则把培训计划也设置为结束状态
        String sql3 = "select count(*) n from r31 where r3125='" + relaPLan + "'";
        try
        {
            rs = dao.search(sql3);
            int n = 0;
            int m = 0;
            if (rs.next()) {
                n = rs.getInt("n");
            }

            rs = dao.search(sql3 + " and r3127='06'");
            if (rs.next()) {
                m = rs.getInt("n");
            }

            if (n == m)// 此计划下的所有培训班状态都为结束状态
            {
                sql3 = "update r25 set r2509='06' where r2501='" + relaPLan + "'";
                dao.update(sql3);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeRowSet(rs);
        }
    }

    private void closeRowSet(RowSet rs)
    {
        if (null != rs)
        {
            try
            {
                rs.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public ArrayList getLessonStatusList() {
		
		ArrayList list = new ArrayList();
		
		CommonData data  = new CommonData("0", "未学");
		CommonData data0 = new CommonData("1", "正学");
		CommonData data1 = new CommonData("2", "已学");

		list.add(data);
		list.add(data0);
		list.add(data1);

		return list;
	}
    
    public String getDatesTyle(FieldItem fi) {
        String datesTyle = "yyyy-MM-dd";
        int length = fi.getItemlength();
        if(length == 4) {
            datesTyle = "yyyy";
        }
        if(length == 7) {
            datesTyle = "yyyy-MM";
        }
        if(length == 15) {
            datesTyle = "yyyy-MM-dd HH:mm";
        }
        if(length == 18) {
            datesTyle = "yyyy-MM-dd HH:mm:ss";
        }

        return datesTyle;
    }
}
