package com.hjsj.hrms.businessobject.performance.options;

import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * <p>Title:PerDegreeBo.java</p>
 * <p>Description:参数设置/等级分类</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-07-16 11:11:11</p> 
 * @author JinChunhai
 * @version 1.0 
 */

public class PerDegreeBo
{

	private String plan_id;
	private String isGainPipGrade = "no"; // 是否获取标准等级的标志
    private String degreeId;
    private String degreename;
    private String degreedesc;
    private String topscore;
    private String used = "0";
    private String toRoundOff = "0";
    private String flag;
    private String domainflag;
    private String B0110;
    private Connection conn = null;
    private String tableName = "degree_highset";

    public PerDegreeBo(Connection conn)
    {
    	this.conn = conn;
    //	getIsGainPipGrade(); // 判断是否获取标准等级
    }

    public PerDegreeBo(Connection conn, String degreeId, String plan_id)
    {

		this.conn = conn;
		this.degreeId = degreeId;
		this.plan_id = plan_id;		
		if(plan_id!=null && plan_id.trim().length()>0) {
            getIsGainPipGrade(); // 判断是否获取标准等级
        }
		setUsed();
		setToRoundOff();
    }

    public String getDegreeId()
    {
    	return degreeId;
    }

    public void setDegreeId(String degreeId)
    {

    	this.degreeId = degreeId;
    }

    public String getDegreename()
    {

    	return degreename;
    }

    public void setDegreename(String degreename)
    {

    	this.degreename = degreename;
    }

    public String getDegreedesc()
    {

    	return degreedesc;
    }

    public void setDegreedesc(String degreedesc)
    {

    	this.degreedesc = degreedesc;
    }

    public String getTopscore()
    {

    	return topscore;
    }

    public void setTopscore(String topscore)
    {

    	this.topscore = topscore;
    }

    public String getFlag()
    {

    	return flag;
    }

    public void setFlag(String flag)
    {

    	this.flag = flag;
    }

    public String getDomainflag()
    {

    	return domainflag;
    }

    public void setDomainflag(String domainflag)
    {

    	this.domainflag = domainflag;
    }

    public String getB0110()
    {

    	return B0110;
    }

    public void setB0110(String b0110)
    {

    	B0110 = b0110;
    }

    /** 自助平台用户，则取其单位编码 */
    public String getB0110(String pre, String a0100)
    {
    	RowSet rs = null;
		String unit = "";
		try
		{
		    String sql = "select b0110 from " + pre + "a01 where a0100='" + a0100 + "'";
		    ContentDAO dao = new ContentDAO(this.conn);
		    
		    rs = dao.search(sql);
		    while (rs.next())
		    {
		    	unit = rs.getString("b0110");
		    }
		    if(rs!=null) {
                rs.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return unit;
    }

    /**
      * 获得某考核分类下的考核等级
      * 
      * @return
      */
    public ArrayList getDegrees()
    {
    	RowSet rs = null;
		ArrayList list = new ArrayList();
		StringBuffer strsql = new StringBuffer();
		strsql.append("select id,itemname from per_degreedesc where degree_id = ");
		strsql.append(this.degreeId);
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
		    rs = dao.search(strsql.toString());
		    int i = 1;
		    while (rs.next())
		    {
				String id = rs.getString("id");
				String itemname = rs.getString("itemname")==null?"":rs.getString("itemname");
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("bh", new Integer(i++));
				abean.set("id", id);
				abean.set("itemname", itemname);
				abean.set("value", "0");
				list.add(abean);
		    }
		    if(rs!=null) {
                rs.close();
            }
	
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return list;
    }

    /**
      * 创建等级分类高级设置中临时表
      */
    public void createTable() throws GeneralException
    {

		DbWizard dbWizard = new DbWizard(this.conn);
		if (dbWizard.isExistTable(tableName, false))
		{
		    dbWizard.dropTable(tableName);
		}
	
		Table table = new Table(tableName);
		Field obj = new Field("mode1");
		obj.setDatatype(DataType.STRING);
		obj.setLength(2);
		obj.setKeyable(false);
		table.addField(obj);
	
		obj = new Field("oper");
		obj.setDatatype(DataType.STRING);
		obj.setLength(2);
		obj.setKeyable(false);
		table.addField(obj);
	
		obj = new Field("value");
		obj.setDatatype(DataType.STRING);
		obj.setLength(30);
		obj.setKeyable(false);
		table.addField(obj);
	
		obj = new Field("grouped");
		obj.setDatatype(DataType.STRING);
		obj.setLength(50);
		obj.setKeyable(false);
		table.addField(obj);
	
		ArrayList list = this.getDegrees();
		for (int i = 0; i < list.size(); i++)
		{
		    LazyDynaBean abean = (LazyDynaBean) list.get(i);
		    String fieldName = "degree" + (String) abean.get("id");
		    obj = new Field(fieldName);
		    obj.setDatatype(DataType.STRING);
		    obj.setLength(2);
		    obj.setKeyable(false);
		    table.addField(obj);
		}
		//所属部门等级
		obj = new Field("UMGrade");
		obj.setDatatype(DataType.STRING);
		obj.setLength(30);
		obj.setKeyable(false);
		table.addField(obj);
		
		obj = new Field("seq");
		obj.setDatatype(DataType.INT);
		obj.setKeyable(false);
		table.addField(obj);
	
		obj = new Field("num1");
		obj.setDatatype(DataType.INT);
		obj.setKeyable(false);
		table.addField(obj);
	
		dbWizard.createTable(table);
	
		// 创建表
		// ContentDAO dao = new ContentDAO(this.conn);
		// StringBuffer strSql = new StringBuffer("create table ");
		// strSql.append(tableName + "(");
		//
		// strSql.append("mode varchar(2),");
		// strSql.append("oper varchar(2),");
		// strSql.append("value varchar(30),");
		// strSql.append("grouped varchar(4),");
		// ArrayList list = this.getDegrees();
		// for (int i = 0; i < list.size(); i++)
		// {
		// LazyDynaBean abean = (LazyDynaBean) list.get(i);
		// String fieldName = "degree" + (String) abean.get("id");
		// strSql.append(fieldName);
		// strSql.append(" varchar(2),");
		// }
		// strSql.append("seq int,");
		// strSql.append("num int)");
		// try
		// {
		// dao.update(strSql.toString());
		// } catch (SQLException e)
		// {
		// e.printStackTrace();
		// }
	
		this.insertData();
    }

    /**
      * 取得高级设置的xml内容
      */
    public String getExtpro()
    {
    	RowSet rs = null;
		String extpro = "";
		StringBuffer strsql = new StringBuffer();
		if((this.plan_id!=null && this.plan_id.trim().length()>0) && (this.isGainPipGrade!=null && this.isGainPipGrade.trim().length()>0 && "yes".equalsIgnoreCase(this.isGainPipGrade)))
		{
			strsql.append("select parameter_content from per_plan where plan_id=");
			strsql.append(this.plan_id);
		}else
		{
			strsql.append("select extpro from per_degree where degree_id=");
			strsql.append(this.degreeId);
		}				
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
		    rs = dao.search(strsql.toString());
		    if (rs.next())
		    {
				String temp = rs.getString(1);
				if (extpro != null) {
                    extpro = temp;
                }
		    }
		    if(rs!=null) {
                rs.close();
            }
	
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return extpro;
    }
    
    /**
     * 判断是否获取标准等级
     */
    public void getIsGainPipGrade()
    {
    	RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);		
		try
		{		
			String extpro = "";
			if(this.plan_id!=null && this.plan_id.trim().length()>0)
			{
				StringBuffer strsql = new StringBuffer();
				strsql.append("select parameter_content from per_plan where plan_id=");
				strsql.append(this.plan_id);		
			    rs = dao.search(strsql.toString());
			    if(rs.next())
			    {
					String temp = rs.getString(1);
					if (extpro != null) {
                        extpro = temp;
                    }
			    }
			    
			    if(extpro != null && extpro.trim().length()>0)
			    {
			    	Document doc = PubFunc.generateDom(extpro);
		    	    String xpath = "//AdvancedDegrees";
		    	    XPath xpath_ = XPath.newInstance(xpath);
		    	    Element ele = (Element) xpath_.selectSingleNode(doc);		    	    
		    	    if(ele != null)
	    	    	{
		    	    	List list1 = (List) ele.getChildren("AdvancedDegree");
						for (int i = 0; i < list1.size(); i++)
						{
						    Element temp = (Element) list1.get(i);
						    if(temp != null)
			    	    	{
							    String degree_id = temp.getAttributeValue("degree_id");
							    if(degree_id!=null && degree_id.trim().length()>0 && degree_id.equalsIgnoreCase(this.degreeId))
							    {
							    	this.isGainPipGrade = "yes";  
							    	break;
							    }
			    	    	}
						}
	    	    	}
			    }		    
			}						
		    if(rs!=null) {
                rs.close();
            }
	
		} catch (Exception e)
    	{
    	    e.printStackTrace();
    	}
    }
        
    /**获得启用的等级分类高级设置
     * @param ignoreUsed 是否忽略启用设置
     */    
    public ArrayList getDegreeHighSetList(boolean ignoreUsed)
    {
    	ArrayList list = new ArrayList();
    	String extpro = this.getExtpro();
    	if (extpro == null || "".equals(extpro)) {
            return list;
        }
    	String mode = "";
    	String oper = "";
    	String value = "";
    	String grouped = "";
    	String actIds = "";
    	String uMGrade = "";
    	try
    	{
    	    Document doc = PubFunc.generateDom(extpro);
    	    String xpath = "//AdvancedDegrees";
    	    XPath xpath_ = XPath.newInstance(xpath);
    	 //   Element ele = (Element) xpath_.selectSingleNode(doc);
    	    
    	    Element ele = null;
    	    if((this.plan_id!=null && this.plan_id.trim().length()>0) && (this.isGainPipGrade!=null && this.isGainPipGrade.trim().length()>0 && "yes".equalsIgnoreCase(this.isGainPipGrade)))
    		{
    	    	Element dele = (Element) xpath_.selectSingleNode(doc);  
    	    	if(dele != null)
    	    	{
	    	    	List list1 = (List) dele.getChildren("AdvancedDegree");
					for (int i = 0; i < list1.size(); i++)
					{
					    Element temp = (Element) list1.get(i);
					    if(temp != null)
		    	    	{
						    String degree_id = temp.getAttributeValue("degree_id");
						    if(degree_id!=null && degree_id.trim().length()>0 && degree_id.equalsIgnoreCase(this.degreeId))
						    {
						    	ele = (Element) list1.get(i);  
						    	break;
						    }
		    	    	}
					}
    	    	}				
    		}else
    		{
    			ele = (Element) xpath_.selectSingleNode(doc);
    		}
    	    
    	    
    	    if (ele != null)
    	    {
    	    	String qy = ele.getAttributeValue("used");
    	    	String toRoundOff = ele.getAttributeValue("toRoundOff");
    	    	if (ignoreUsed || (!ignoreUsed && qy != null && "true".equalsIgnoreCase(qy)))
    	    	{
    	    		HashMap idMap = this.firstLastId();
    	    		String first_id=(String)idMap.get("first_id");
    	    		String last_id=(String)idMap.get("last_id");
    	    		
    	    		List list1 = (List) ele.getChildren("Degree");
    	    		for (int i = 0; i < list1.size(); i++)
    	    		{    	    		    
    	    		    Element temp = (Element) list1.get(i);
    	    		    mode = temp.getAttributeValue("Mode");
    	    		    oper = temp.getAttributeValue("Oper");
    	    		    value = temp.getAttributeValue("Value");
    	    		    grouped = temp.getAttributeValue("Grouped");
    	    		    actIds = temp.getAttributeValue("ActIds");    		   
    	    		    uMGrade = temp.getAttributeValue("UMGrade");  

    	    		    if(grouped != null && "true".equalsIgnoreCase(grouped)) {
                            grouped = "DEPART";
                        } else if(grouped == null || grouped.trim().length()<=0 || "false".equalsIgnoreCase(grouped)) {
                            grouped = "-1";
                        }
    	    		    
    	    		    if(actIds.length()==0) {
                            continue;
                        } else
    	    		    {
    	    		    	// 如果当前等级分类下的等级项目个数不等于 1 个
    	    		    	if(!last_id.equalsIgnoreCase(first_id))
    	    		    	{
	    	    		    	//最后一级(或者是包含最后一级的多个)设了不多于无效
	    	    		    	if((","+last_id+",").indexOf(actIds)>-1 && "2".equals(oper)) {
                                    continue;
                                }
	   	    		    	    //第一级(或者是包含第一级的多个)设了不少于无效 20160622
	    	    		  //  	if((","+first_id+",").indexOf(actIds)>-1 && oper.equals("1"))
	    	    	//	    		continue;  
	    	    		    	//同时设置了第一个级和最后一级无效
	    	    		    	if((","+first_id+",").indexOf(actIds)>-1 && (","+last_id+",").indexOf(actIds)>-1) {
                                    continue;
                                }
    	    		    	}
    	    		    }
    	    		    LazyDynaBean abean = new LazyDynaBean();
    	    		    abean.set("mode", mode);
    	    		    abean.set("oper", oper);
    	    		    abean.set("value", value);
    	    		    abean.set("grouped", grouped.toUpperCase());
    	    		    abean.set("actIds", actIds);
    	    		    abean.set("UMGrade", uMGrade==null?"":uMGrade);
    	    		    abean.set("toRoundOff", toRoundOff==null?"":toRoundOff);
    	    		    list.add(abean);
    	    		}
    	    	}
    	    }
    	    
    	} catch (Exception e)
    	{
    	    e.printStackTrace();
    	}  	
    	
    	return list;
    }
   
    /**
      * 在临时表中插入数据
      */
    public void insertData()
    {

		String extpro = this.getExtpro();
		if (extpro == null || "".equals(extpro)) {
            return;
        }
		String mode = "";
		String oper = "";
		String value = "";
		String grouped = "";
		String actIds = "";
		String UMGrade="";
		StringBuffer strInsert1 = new StringBuffer();
		strInsert1.append("insert into " + this.tableName + " (mode1,oper,value,grouped,UMGrade,");
		StringBuffer strInsert2 = new StringBuffer();
		strInsert2.append(" values (?,?,?,?,?,");
		ArrayList list = new ArrayList();
		try
		{
		    Document doc = PubFunc.generateDom(extpro);
		    String xpath = "//AdvancedDegrees";
		    XPath xpath_ = XPath.newInstance(xpath);
		    //   Element ele = (Element) xpath_.selectSingleNode(doc);
    	    
    	    Element ele = null;
    	    if((this.plan_id!=null && this.plan_id.trim().length()>0) && (this.isGainPipGrade!=null && this.isGainPipGrade.trim().length()>0 && "yes".equalsIgnoreCase(this.isGainPipGrade)))
    		{
    	    	Element dele = (Element) xpath_.selectSingleNode(doc); 
    	    	if(dele != null)
    	    	{
	    	    	List list1 = (List) dele.getChildren("AdvancedDegree");
					for (int i = 0; i < list1.size(); i++)
					{
					    Element temp = (Element) list1.get(i);
					    if(temp != null)
		    	    	{
						    String degree_id = temp.getAttributeValue("degree_id");
						    if(degree_id!=null && degree_id.trim().length()>0 && degree_id.equalsIgnoreCase(this.degreeId))
						    {
						    	ele = (Element) list1.get(i);  
						    	break;
						    }
		    	    	}
					}
    	    	}
    		}else
    		{
    			ele = (Element) xpath_.selectSingleNode(doc);
    		}
		    
		    if (ele != null)
		    {
				used = ele.getAttributeValue("used");
				if (used != null && "true".equalsIgnoreCase(used)) {
                    used = "1";
                } else {
                    used = "0";
                }
				List list1 = (List) ele.getChildren("Degree");
				for (int i = 0; i < list1.size(); i++)
				{
				    ArrayList list2 = new ArrayList();
				    Element temp = (Element) list1.get(i);
				    mode = temp.getAttributeValue("Mode");
				    oper = temp.getAttributeValue("Oper");
				    value = temp.getAttributeValue("Value");
				    grouped = temp.getAttributeValue("Grouped");
				    actIds = temp.getAttributeValue("ActIds");
				    UMGrade=temp.getAttributeValue("UMGrade");
				    if (mode != null && "1".equals(mode)) {
                        value = PubFunc.round(new Float(Float.parseFloat(value) * 100).toString(), 0);
                    }
				    list2.add(mode);
				    list2.add(oper);
				    list2.add(value);
				    if(grouped != null && "true".equalsIgnoreCase(grouped)) {
                        grouped = "DEPART";
                    } else if(grouped == null || grouped.trim().length()<=0 || "false".equalsIgnoreCase(grouped)) {
                        grouped = "-1";
                    }
				    
				    list2.add(grouped.toUpperCase());
				    list2.add(UMGrade);
				    
				    ArrayList list3 = this.getDegrees();
				    String[] degrees = actIds.split(",");
				    int j = 0;
				    for (; j < list3.size(); j++)
				    {
						LazyDynaBean abean = (LazyDynaBean) list3.get(j);
						String fieldName = "degree" + (String) abean.get("id");
						if (i == 0)
						{
						    strInsert1.append(fieldName + ",");
						    strInsert2.append("?,");
						}
			
						fieldName = fieldName.substring(6, fieldName.length());
						boolean flag = false;
						for (int k = 0; k < degrees.length; k++)
						{
						    if (fieldName.equals(degrees[k]))
						    {
								flag = true;
								break;
						    }
						}
						if (flag) {
                            list2.add("1");
                        } else {
                            list2.add("0");
                        }
				    }
				    if (i == 0)
				    {
						strInsert1.append("seq,");
						strInsert2.append("?,");
						strInsert1.append("num1)");
						strInsert2.append("?)");
				    }
		
				    list2.add(new Integer(i + 1));
				    list2.add(new Integer(i + 1));
				    list.add(list2);
				}
				ContentDAO dao = new ContentDAO(this.conn);
				String strSql = strInsert1.toString() + strInsert2.toString();
				dao.batchInsert(strSql, list);
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
    }

    /**
      * 从临时表中取出数据
      */
    public ArrayList getData()
    {

		ArrayList list = new ArrayList();
		StringBuffer selSql = new StringBuffer();
		selSql.append("select mode1,oper,value,grouped,UMGrade,");
		ArrayList list1 = this.getDegrees();
		for (int i = 0; i < list1.size(); i++)
		{
		    LazyDynaBean abean = (LazyDynaBean) list1.get(i);
		    String fieldName = "degree" + (String) abean.get("id");
		    selSql.append(fieldName + ",");
		}
		selSql.append("seq,num1 from " + this.tableName);
		selSql.append(" order by seq");
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
		    RowSet rs = dao.search(selSql.toString());
		    int count = 1;
		    while (rs.next())
		    {
				LazyDynaBean abean = new LazyDynaBean();
				String mode = rs.getString("mode1");
				String oper = rs.getString("oper");
				String value = rs.getString("value")==null?"0":rs.getString("value");
				String grouped = rs.getString("grouped");
				String seq = rs.getString("seq");
				String num = rs.getString("num1");
				String UMGrade = rs.getString("UMGrade")==null?"": rs.getString("UMGrade");
				
				abean.set("count", String.valueOf(count));
				abean.set("mode", mode);
				abean.set("oper", oper);
				abean.set("value", value);
				abean.set("grouped", grouped);
				abean.set("seq", seq);
				abean.set("num", num);
				abean.set("UMGrade", UMGrade);
				
				for (int i = 0; i < list1.size(); i++)
				{
				    LazyDynaBean abean1 = (LazyDynaBean) list1.get(i);
				    String fieldName = "degree" + (String) abean1.get("id");
				    String fieldValue = rs.getString(fieldName);
				    abean.set(fieldName, fieldValue);
				}
				count++;
				
				list.add(abean);
		    }
	
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return list;
    }

    /**
      * 从临时表中删除数据
      */
    public void delData(String delStr)
    {

		String[] ids = delStr.split("@");
		StringBuffer delSql = new StringBuffer();
		for (int i = 0; i < ids.length; i++)
		{
		    if ("".equals(ids[i])) {
                continue;
            }
		    delSql.append(ids[i]);
		    delSql.append(",");
		}
		delSql.setLength(delSql.length() - 1);
	
		StringBuffer strSql = new StringBuffer();
		strSql.append("delete from ");
		strSql.append(this.tableName);
		strSql.append(" where num1 in (");
		strSql.append(delSql.toString());
		strSql.append(")");
	
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
		    dao.delete(strSql.toString(), new ArrayList());
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}

    }

    public String setUsed()
    {

		String str = "0";
		String extpro = this.getExtpro();
		if (extpro == null || "".equals(extpro)) {
            return str;
        }
		try
		{
		    Document doc = PubFunc.generateDom(extpro);
		    String xpath = "//AdvancedDegrees";
		    XPath xpath_ = XPath.newInstance(xpath);
		    //   Element ele = (Element) xpath_.selectSingleNode(doc);
    	    
    	    Element ele = null;
    	    if((this.plan_id!=null && this.plan_id.trim().length()>0) && (this.isGainPipGrade!=null && this.isGainPipGrade.trim().length()>0 && "yes".equalsIgnoreCase(this.isGainPipGrade)))
    		{
    	    	Element dele = (Element) xpath_.selectSingleNode(doc);  
    	    	if(dele != null)
    	    	{
	    	    	List list1 = (List) dele.getChildren("AdvancedDegree");
					for (int i = 0; i < list1.size(); i++)
					{
					    Element temp = (Element) list1.get(i);
					    if(temp != null)
		    	    	{
						    String degree_id = temp.getAttributeValue("degree_id");
						    if(degree_id!=null && degree_id.trim().length()>0 && degree_id.equalsIgnoreCase(this.degreeId))
						    {
						    	ele = (Element) list1.get(i);  
						    	break;
						    }
		    	    	}
					}
    	    	}
    		}else
    		{
    			ele = (Element) xpath_.selectSingleNode(doc);
    		}
		    
		    if (ele != null)
		    {
				used = ele.getAttributeValue("used");
				if (used != null && "true".equalsIgnoreCase(used)) {
                    used = "1";
                } else {
                    used = "0";
                }
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return str;
    }
    public String setToRoundOff()
    {
    	
    	String str = "0";
    	String extpro = this.getExtpro();
    	if (extpro == null || "".equals(extpro)) {
            return str;
        }
    	try
    	{
    		Document doc = PubFunc.generateDom(extpro);
    		String xpath = "//AdvancedDegrees";
    		XPath xpath_ = XPath.newInstance(xpath);
    		//   Element ele = (Element) xpath_.selectSingleNode(doc);
    		
    		Element ele = null;
    		if((this.plan_id!=null && this.plan_id.trim().length()>0) && (this.isGainPipGrade!=null && this.isGainPipGrade.trim().length()>0 && "yes".equalsIgnoreCase(this.isGainPipGrade)))
    		{
    			Element dele = (Element) xpath_.selectSingleNode(doc);  
    			if(dele != null)
    			{
    				List list1 = (List) dele.getChildren("AdvancedDegree");
    				for (int i = 0; i < list1.size(); i++)
    				{
    					Element temp = (Element) list1.get(i);
    					if(temp != null)
    					{
    						String degree_id = temp.getAttributeValue("degree_id");
    						if(degree_id!=null && degree_id.trim().length()>0 && degree_id.equalsIgnoreCase(this.degreeId))
    						{
    							ele = (Element) list1.get(i);  
    							break;
    						}
    					}
    				}
    			}
    		}else
    		{
    			ele = (Element) xpath_.selectSingleNode(doc);
    		}
    		
    		if (ele != null)
    		{
    			toRoundOff = ele.getAttributeValue("toRoundOff");
    			
    		}
    	} catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	return str;
    }

    public String getUsed()
    {

    	return used;
    }
    public String getToRoundOff()
    {
    	
    	return toRoundOff;
    }

    /**
      * 临时表记录排序
      * 
      * @return
     */
    public ArrayList sortList()
    {

		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		String sqlstr = "";
		sqlstr = "select mode1,num1 from " + this.tableName + "  order by seq";
		ArrayList dylist = null;
		try
		{
		    dylist = dao.searchDynaList(sqlstr);
		    for (Iterator it = dylist.iterator(); it.hasNext();)
		    {
				DynaBean dynabean = (DynaBean) it.next();
				String num = dynabean.get("num1").toString();
				String mode = dynabean.get("mode1").toString();
				if ("1".equals(mode)) {
                    mode = "百分比";
                } else if ("2".equals(mode)) {
                    mode = "人数";
                }
				CommonData dataobj = new CommonData(num, num + ":" + mode);
				list.add(dataobj);
		    }
		} catch (GeneralException e)
		{
		    e.printStackTrace();
		}
		return list;
    }

    /**
      * 保存排序
     */
    public void saveSort(String sort)
    {

		ContentDAO dao = new ContentDAO(this.conn);
		String[] fitem = sort.split(",");
		String updateStr = "update " + this.tableName + " set seq=? where num1=?";
		ArrayList list = new ArrayList();
	
		for (int i = 0; i < fitem.length; i++)
		{
		    ArrayList list2 = new ArrayList();
		    list2.add(new Integer(i + 1));
		    list2.add(new Integer(fitem[i]));
		    list.add(list2);
		}
		try
		{
		    dao.batchUpdate(updateStr, list);
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
    }

    /**
      * 从临时表查询数据
     */
    public ArrayList query()
    {
    	RowSet rs = null;
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		String strSql = "select * from " + this.tableName + " order by seq";
		try
		{
		    rs = dao.search(strSql);
		    while (rs.next())
		    {
				String mode = rs.getString("mode1");
				String oper = rs.getString("oper");
				String value = rs.getString("value");
				String grouped = rs.getString("grouped");
				String UMGrade = rs.getString("UMGrade")==null?"":rs.getString("UMGrade");
				// Mode方式：1=比例控制
				if ("1".equals(mode)) {
                    value = PubFunc.round(new Float(Float.parseFloat(value) / 100).toString(), 2);
                }
		
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("mode", mode);
				abean.set("oper", oper);
				abean.set("value", value);
				abean.set("grouped", grouped);
				abean.set("UMGrade", UMGrade);
				
				String actIds = ",";
				ArrayList degrees = this.getDegrees();
				for (int i = 0; i < degrees.size(); i++)
				{
				    LazyDynaBean bean = (LazyDynaBean) degrees.get(i);
				    String id1 = (String) bean.get("id");
				    String temp = rs.getString("degree" + id1);
				    if ("1".equals(temp)) {
                        actIds += id1 + ",";
                    }
				}
		
				abean.set("actIds", actIds);
		
				list.add(abean);
		    }
		    if(rs!=null) {
                rs.close();
            }
		    
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return list;

    }

    /**
      * 保存高级设置
     */
    public void saveHighSet(String used,String toRoundOff)
    {

		if ("1".equals(used)) {
            used = "True";
        } else {
            used = "False";
        }
	
		Element root = new Element("AdvancedDegrees");
		root.setAttribute("used", used);
		root.setAttribute("toRoundOff", toRoundOff);	
		root.setAttribute("degree_id", this.degreeId);	
		ArrayList list = this.query();
		
		if(this.plan_id!=null && this.plan_id.trim().length()>0)
		{
			LoadXml loadxml = new LoadXml(this.conn, this.plan_id);						
			loadxml.saveGradeHighValue(used,toRoundOff,this.degreeId,list);
			
		}else
		{		
			for (int i = 0; i < list.size(); i++)
			{
			    Element degree = new Element("Degree");
			    LazyDynaBean bean = (LazyDynaBean) list.get(i);
			    String mode = (String) bean.get("mode");
			    String oper = (String) bean.get("oper");
			    String value = (String) bean.get("value");
			    String grouped = (String) bean.get("grouped");
			    if("-1".equalsIgnoreCase(grouped)) {
                    grouped = "";
                }
			    String actIds = (String) bean.get("actIds");
			    String UMGrade = (String) bean.get("UMGrade");
			    
			    degree.setAttribute("Mode", mode);
			    degree.setAttribute("Oper", oper);
			    degree.setAttribute("Value", value);
			    degree.setAttribute("Grouped", grouped);
			    degree.setAttribute("ActIds", actIds);
			    degree.setAttribute("UMGrade", UMGrade);
			    root.addContent(degree);
			}
		
			Document myDocument = new Document(root);
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			String xmlContent = outputter.outputString(myDocument);
		
			String strSql = "update per_degree set extPro='" + xmlContent + "' where degree_id = " + this.degreeId;
			ContentDAO dao = new ContentDAO(this.conn);
			try
			{
			    dao.update(strSql);
			} catch (SQLException e)
			{
			    e.printStackTrace();
			}
		}
		
		
		DbWizard dbWizard = new DbWizard(this.conn);
		if (dbWizard.isExistTable(tableName, false))
		{
		    dbWizard.dropTable(tableName);
		}
		
    }

    /**
      * getNum()
     */
    public String getNum()
    {
    	RowSet rs = null;
		String num = "0";
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
		    rs = dao.search("Select * from " + this.tableName);
		    if (!rs.next()) {
                return "1";
            }
	
		    rs = dao.search("Select max(num1)+1 n from " + this.tableName);
		    if (rs.next()) {
                num = rs.getString("n");
            }
	
		    if(rs!=null) {
                rs.close();
            }
		    
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return num;
    }

    public float getSumPercent()
    {
    	RowSet rs = null;
		float sumVal = 0;
		StringBuffer strsql = new StringBuffer();
		strsql.append("select sum(percentvalue) theVal from per_degreedesc where degree_id=");
		strsql.append(this.degreeId);
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
		    rs = dao.search(strsql.toString());
		    if (rs.next()) {
                sumVal = rs.getFloat("theVal");
            }
		    
		    if(rs!=null) {
                rs.close();
            }
		    
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return sumVal;
    }

    public float getStrict()
    {
    	RowSet rs = null;
		float strict = 0;
		StringBuffer strsql = new StringBuffer();
		strsql.append("select strict from per_degreedesc where degree_id=");
		strsql.append(this.degreeId);
		strsql.append(" order by id");
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
		    rs = dao.search(strsql.toString());
		    int x = 0;
		    while (rs.next())
		    {
		    	x++;
		    	if(x==1) {
                    strict = rs.getFloat("strict");
                }
		    }		
		    
		    if(rs!=null) {
                rs.close();
            }
	
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return strict;
    }
    
    
    public HashMap firstLastId()
    {
    	RowSet rs = null;
    	HashMap map = new HashMap();
    	StringBuffer strsql = new StringBuffer();
    	strsql.append("select id from per_degreedesc where degree_id=");
    	strsql.append(this.degreeId);
    	strsql.append(" order by id");
    	ContentDAO dao = new ContentDAO(this.conn);
    	try
    	{
    	    rs = dao.search(strsql.toString());
    	    int first_id=0;
    	    int last_id=0;
    	    int n=0;
    	    while (rs.next())
    	    {
    	    	if(n==0) {
                    first_id=rs.getInt(1);
                }
    	    	last_id=rs.getInt(1);
    	    	n++;
    	    }		
    	    map.put("first_id", first_id+"");
    	    map.put("last_id", last_id+"");
    	    
    	    if(rs!=null) {
                rs.close();
            }

    	} catch (SQLException e)
    	{
    	    e.printStackTrace();
    	}
    	return map;
    }
    
    /** 限制的分值是否比下一等级的最低分还要小 */
    public boolean isSmallThan()
    {
    	RowSet rs = null;
		boolean flag = false;
		StringBuffer strsql = new StringBuffer();
		strsql.append("select strict,bottomscore from per_degreedesc where degree_id=");
		strsql.append(this.degreeId);
		strsql.append(" order by id");
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
		    rs = dao.search(strsql.toString());
		    int i = 0;
		    float strict = 0;
		    while (rs.next() && i<2)
		    {
				i++;
				if (i == 1) {
                    strict = rs.getFloat("strict");
                }
		
				if (i == 2)
				{
				    float bottomscore = rs.getFloat("bottomscore");
				    if (bottomscore >= strict) {
                        flag = true;
                    }
				}
			
		    }
		    if(rs!=null) {
                rs.close();
            }
		    
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return flag;
    }

    /** 测试等级是否为空 */
    public String testItemName()
    {

		StringBuffer theStr = new StringBuffer();
		StringBuffer strsql = new StringBuffer();
		strsql.append("select itemname from per_degreedesc where degree_id=");
		strsql.append(this.degreeId);
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
		    RowSet rs = dao.search(strsql.toString());
		    int i = 0;
		    while (rs.next())
		    {
				i++;
				String temp = rs.getString("itemname");
				if (temp == null || temp != null && "".equals(temp)) {
                    theStr.append("," + Integer.toString(i));
                }
		    }
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return theStr.length() > 0 ? theStr.substring(1) : "";
    }

    public float getSumPercent(String flag)
    {

		float sumVal = 0;
		ArrayList list = new ArrayList();
		StringBuffer strsql = new StringBuffer();
		strsql.append("select percentvalue from per_degreedesc where degree_id=");
		strsql.append(this.degreeId);
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
		    RowSet rs = dao.search(strsql.toString());
		    while (rs.next()) {
                list.add(Float.valueOf(rs.getFloat("percentvalue")));
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
	
		if ("2".equals(flag))// 2-混合先算分值===去掉首尾的比例求和
		{
		    for (int j = 1; j < list.size() - 1; j++)
		    {
				Float temp = (Float) list.get(j);
				sumVal += temp.floatValue();
		    }
		} else if ("3".equals(flag))// 3-混合先算比例==去首尾的比例求和
		{
			if(list.size()>0)
			{
				 Float first = (Float) list.get(0);
				 Float last = (Float) list.get(list.size() - 1);
				 sumVal = first.floatValue() + last.floatValue();
			}	   
		}
	
		return sumVal;
    }
    
    /**
	 * 高级分组指标
	 */
	public ArrayList getGroupList(String plan_id)
	{
		ArrayList list=new ArrayList();
		
		list.add(new CommonData("-1",""));
		list.add(new CommonData("UNIT","单位"));
		list.add(new CommonData("DEPART","部门"));
		list.add(new CommonData("CLASSIFY","对象类别"));
		
		LoadXml loadxml = null;
		if(plan_id!=null && plan_id.trim().length()>0) {
            loadxml = new LoadXml(this.conn,plan_id);
        } else {
            loadxml = new LoadXml();
        }
		
		String subsetMenus = loadxml.getRelatePlanSubSetMenuValue(); // 引入的子集
		if(subsetMenus!=null && subsetMenus.trim().length()>0)
		{
			String[] temps=subsetMenus.split(",");
			for(int j=0;j<temps.length;j++)
			{
				String temp=temps[j].trim();
				if(temp.length()==0) {
                    continue;
                }
			    FieldItem fielditem = DataDictionary.getFieldItem(temp);	
			    
			    if(!"0".equalsIgnoreCase(fielditem.getCodesetid())) // 代码型子集指标
                {
                    list.add(new CommonData(fielditem.getItemid().toUpperCase(),fielditem.getItemdesc()));
                }
			}
		}
		
		return list;
	}

}
