package com.hjsj.hrms.businessobject.train;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.DataDictionary;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * <p>
 * Title:TrainAddBo.java
 * </p>
 * <p>
 * Description:培训管理通用添加
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-08-13 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class TrainAddBo
{
    private Connection cn;

    private String recTable;

    private String primaryField;

    private String recName;// 新增页面标题

    public TrainAddBo(String recTable, Connection cn)
    {

        this.recTable = recTable;
        this.cn = cn;
        setOther();
    }

    public TrainAddBo(Connection cn)
    {

        this.cn = cn;
    }

    public void setOther()
    {

        if ("r31".equalsIgnoreCase(this.recTable))
        {
            this.primaryField = "r3101";
            this.recName = "培训班";
        }
        else if ("r25".equalsIgnoreCase(this.recTable))
        {
            this.primaryField = "r2501";
            this.recName = "培训计划";
        }
        else if ("r41".equalsIgnoreCase(this.recTable))
        {
            this.primaryField = "r4101";
            this.recName = "培训课程";
        }
        else if ("r37".equalsIgnoreCase(this.recTable))
        {
            this.primaryField = "r3701";
            this.recName = "培训资源评估";
        }
        else if ("r45".equalsIgnoreCase(this.recTable))
        {
            this.primaryField = "r4501";
            this.recName = "培训费用";
        }
        else if ("r40".equalsIgnoreCase(this.recTable))
        {
            this.primaryField = "r4001";
            this.recName = "培训学员";
        }
        else if ("r54".equalsIgnoreCase(this.recTable))
        {
            this.primaryField = "r5400";
            this.recName = "培训考试计划";
        }
    }

    public String getPrimaryField()
    {

        return primaryField;
    }

    public String getRecName()
    {

        return recName;
    }

    // 新增状态特殊字段设置初始值
    public HashMap getInitValueMap(String initValue)
    {

        HashMap map = new HashMap();
        if (initValue == null || (initValue != null && "".equals(initValue))) {
            return map;
        }
        String[] temp1 = initValue.split(",");
        for (int i = 0; i < temp1.length; i++)
        {
            String[] temp2 = temp1[i].split(":");
            if (temp2.length < 2)// 如果初值付为空，则去掉初值的设置
            {
                continue;
            }
            map.put(temp2[0], temp2[1]);
        }
        return map;
    }

    public String getRecTable()
    {

        return recTable;
    }

    /**
     * 是否新建
     */
    public boolean isNew(String priFldValue)
    {

        boolean flag = true;
        if (priFldValue == null) {
            return flag;
        }
        ContentDAO dao = new ContentDAO(this.cn);
        StringBuffer strSql = new StringBuffer();
        strSql.append("select * from ");
        strSql.append(this.recTable);
        strSql.append(" where " + this.primaryField + " ='");
        strSql.append(priFldValue);
        strSql.append("'");

        RowSet rs = null;
        try
        {
            rs = dao.search(strSql.toString());
            if (rs.next()) {
                flag = false;
            }
        }
        catch (SQLException e)
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
                catch (Exception e2)
                {
                    e2.printStackTrace();
                }
            }
        }
        
        return flag;
    }

    /** 新增培训资源评估时候对资源名称做了自定义对话框 */
    public String outPutXmlStr(String classid, String r3702)
    {

        StringBuffer xml = new StringBuffer();
        try
        {
            // 创建xml文件的根元素
            Element root = new Element("TreeNode");
            // 设置根元素属性
            root.setAttribute("id", "");
            root.setAttribute("text", "root");
            root.setAttribute("title", "organization");
            root.setAttribute("target", "mil_body");
            // 创建xml文档自身
            Document myDocument = new Document(root);

            ArrayList list = getChildList(classid, r3702);
            for (Iterator t = list.iterator(); t.hasNext();)
            {
                LazyDynaBean bean = (LazyDynaBean) t.next();
                Element child = new Element("TreeNode");
                String id = (String) bean.get("id");
                String name = (String) bean.get("name");

                child.setAttribute("id", id);
                child.setAttribute("text", name);
                child.setAttribute("title", name);
                child.setAttribute("href", "");
                child.setAttribute("target", "");
                child.setAttribute("icon", "/images/table.gif");
                // 将子元素作为内容添加到根元素
                root.addContent(child);
            }

            XMLOutputter outputter = new XMLOutputter();
            // 格式化输出类
            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            outputter.setFormat(format);

            // 将生成的XML文件作为字符串形式
            xml.append(outputter.outputString(myDocument));

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return xml.toString();
    }

    private ArrayList getChildList(String classid, String r3702)
    {

        ArrayList list = new ArrayList();
        if (null == r3702 || "".equals(r3702)) {
            return list;
        }
        
        String strSql = "";
        if ("01".equals(r3702))// 教师
        {
            strSql = "Select distinct a.R0401 id,a.R0402 name from R04 a,R41 b Where a.R0401=b.R4106 and b.R4103='" + classid + "'";
        } else if ("02".equals(r3702))// 机构
        {
            strSql = "Select distinct a.R0101 id,a.R0102  name from R01 a,R31 b Where a.R0101=b.R3128 and b.R3101='" + classid + "'";
        } else if ("03".equals(r3702))// 资料
        {
            strSql = "Select distinct a.R0701 id,a.R0702 name from R07 a,R41 b Where a.R0701=b.R4114 and b.R4103='" + classid + "'";
        } else if ("04".equals(r3702))// 场所
        {
            strSql = "Select distinct a.R1001 id,a.R1011 name from R10 a,R31 b Where a.R1001=b.R3126 and b.R3101='" + classid + "'";
        } else if ("05".equals(r3702))// 项目
        {
            strSql = "Select distinct a.R1301 id,a.R1302 name from R13 a,R41 b Where a.R1301=b.R4105 and b.R4103='" + classid + "'";
        }
        
        RowSet rs = null;
        try
        {
            ContentDAO dao = new ContentDAO(this.cn);
            rs = dao.search(strSql);
            while (rs.next())
            {
                LazyDynaBean bean = new LazyDynaBean();
                bean.set("id", rs.getString("id"));
                bean.set("name", rs.getString("name"));

                list.add(bean);
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
                catch (Exception e2)
                {
                    e2.printStackTrace();
                }
            }
        }

        return list;
    }

    /**
     * 获取某表某字段的最大值 zxj 2011-11-19
     */
    public String getMaxValue(String table, String field)
    {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT MAX(");
        sql.append(field);
        sql.append(") AS maxOrder FROM ");
        sql.append(table);

        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.cn);
        try
        {
            rs = dao.search(sql.toString());
            if (rs.next())
            {
                String maxOrder = rs.getString("maxOrder");
                if (maxOrder == null || maxOrder.length() == 0)
                {
                    return "";
                }
                else
                {
                    return maxOrder;
                }
            }
            else
            {
                return "";
            }

        }
        catch (Exception e)
        {
            return "";
        }
        finally
        {
            if (null != rs)
            {
                try
                {
                    rs.close();
                }
                catch (Exception e2)
                {
                    e2.printStackTrace();
                }
            }
        }
    }
    /**
     * 更新相关代码类的长度
     * @param codesetid
     * @param codeitemid
     */
	public void upCodeitemLength(String codesetid, String codeitemid) {
		RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.cn);
		int codeitemlength = codeitemid.length();
		String sql = "select itemlength from fielditem where codesetid='" + codesetid
				+ "' union all select itemlength from t_hr_busifield where codesetid='"
				+ codesetid + "'";
		try{
		rs = dao.search(sql);
		if (rs.next()) {
			int maxlength = rs.getInt("itemlength");
			if (maxlength < codeitemlength) {
				sql = "update fielditem set itemlength=" + codeitemlength
						+ " where codesetid='" + codesetid + "'";
				dao.update(sql);
				sql = "update t_hr_busifield set itemlength=" + codeitemlength
						+ " where codesetid='" + codesetid + "'";
				dao.update(sql);
				sql = "select fieldsetid,itemid from fielditem where codesetid='" + codesetid
						+ "' and useflag='1' union select fieldsetid,itemid from t_hr_busifield where codesetid='"
						+ codesetid + "' and useflag='1'";
				rs = dao.search(sql);
				DBMetaModel dbmodel = new DBMetaModel(this.cn);
				DbWizard dbw = new DbWizard(this.cn);
				while (rs.next()) {
					String fieldsetid = rs.getString("fieldsetid");
					if (fieldsetid.startsWith("A")) {
						ArrayList dbprelist = DataDictionary.getDbpreList();
						for (int i = 0; i < dbprelist.size(); i++) {
							String pre = (String) dbprelist.get(i);
							Table table = new Table((pre + fieldsetid).toLowerCase());
							Field field = new Field(rs.getString("itemid").toLowerCase(), rs.getString("itemid").toLowerCase());
							field.setDatatype(DataType.STRING);
							field.setLength(codeitemlength);
							table.addField(field);
							dbw.alterColumns(table);
							dbmodel.reloadTableModel((pre + fieldsetid).toLowerCase());
						}
					} else {
						Table table = new Table(fieldsetid.toLowerCase());
						String itemid = rs.getString("itemid");
						// 没有好的办法获取一个表中的主键列，故暂时只支持Q17中指标Q1709
						// 假期管理表(Q17)，假期类型是主键的一部分，需先去掉主键，修改完长度后，再加上主键
						if ("Q1709".equalsIgnoreCase(itemid)) {
							dbw.dropPrimaryKey(fieldsetid.toLowerCase());
						}
						Field field = new Field(itemid.toLowerCase(), rs.getString("itemid").toLowerCase());
						field.setDatatype(DataType.STRING);
						field.setLength(codeitemlength);
						if ("Q1709".equalsIgnoreCase(itemid)) {
							field.setNullable(false);
						}
						table.addField(field);
						dbw.alterColumns(table);
						dbmodel.reloadTableModel(fieldsetid.toLowerCase());

						// 加回主键
						if ("Q1709".equalsIgnoreCase(itemid)) {
							table.clear();

							field = new Field("nbase");
							field.setKeyable(true);
							table.addField(field);

							field = new Field("A0100");
							field.setKeyable(true);
							table.addField(field);

							// 年度
							field = new Field("Q1701");
							field.setKeyable(true);
							table.addField(field);

							// 假期类型
							field = new Field("Q1709");
							field.setKeyable(true);
							field.setNullable(false);
							table.addField(field);

							dbw.addPrimaryKey(table);
						}
					}
				}
			}
		}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
	}
}
