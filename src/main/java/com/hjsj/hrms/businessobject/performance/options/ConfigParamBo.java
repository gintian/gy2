package com.hjsj.hrms.businessobject.performance.options;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
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

/**
 * <p>Title:ConfigParamBo.java</p>
 * <p>Description:绩效管理参数设置</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-06-01 11:11:11</p>
 * @author JinChunhai
 * @version 1.0 
 */

public class ConfigParamBo
{
	
    private Connection cn = null;

    public ConfigParamBo(Connection conn)
    {
    	this.cn = conn;
    }

    /** 取得markingMode */
    public String getMarkingMode()
    {

		String markingMode = "1";
		try
		{
		    ContentDAO dao = new ContentDAO(this.cn);
		    RowSet rowSet = dao.search("select constant,str_value from constant where constant='PER_PARAMETERS'");
		    if (rowSet.next())
		    {
				String str_value = rowSet.getString("str_value");
				if (str_value == null || (str_value != null && "".equals(str_value))) {
                    return markingMode;
                } else
				{
				    try
				    {
						Document doc = PubFunc.generateDom(str_value);
						String xpath = "//Per_Parameters";
						XPath xpath_ = XPath.newInstance(xpath);
						Element ele = (Element) xpath_.selectSingleNode(doc);
						if (ele != null)
						{
						    ele = ele.getChild("Plan");
						    if (ele != null) {
                                markingMode = ele.getAttributeValue("MarkingMode");
                            }
						}
				    } catch (Exception e)
				    {
				    	e.printStackTrace();
				    }
				}
		    } else
		    {
				Element root = new Element("Per_Parameters");
				Element child = new Element("Plan");
				child.setAttribute("MarkingMode", "1");
				root.setContent(child);
		
				Document myDocument = new Document(root);
				XMLOutputter outputter = new XMLOutputter();
				Format format = Format.getPrettyFormat();
				format.setEncoding("UTF-8");
				outputter.setFormat(format);
				String xmlContent = outputter.outputString(myDocument);
		
				RecordVo vo = new RecordVo("constant");
				vo.setString("constant", "PER_PARAMETERS");
				vo.setString("str_value", xmlContent);
				dao.addValueObject(vo);
		    }
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
	
		return markingMode;
    }

    /** 保存markingMode */
    public void saveMarkingMode(String theVal, String itemStr)
    {

		Element root = new Element("Per_Parameters");
		Element child = new Element("Plan");
		child.setAttribute("MarkingMode", theVal);
		root.addContent(child);
	
		child = new Element("TargetTraceItem");
		child.setText(itemStr);
		root.addContent(child);
	
		Document myDocument = new Document(root);
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		String xmlContent = outputter.outputString(myDocument);
	
		ContentDAO dao = new ContentDAO(this.cn);
		RecordVo vo = new RecordVo("constant");
		vo.setString("constant", "PER_PARAMETERS");
		vo.setString("str_value", xmlContent);
	
		try
		{
		    dao.updateValueObject(vo);
		    
		} catch (GeneralException e)
		{
		    e.printStackTrace();
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
    }

    /** 取得目标跟踪显示指标 */
    public ArrayList getItemList()
    {

		ArrayList list = new ArrayList();
		try
		{
		    ContentDAO dao = new ContentDAO(this.cn);
		    String sqlStr = "SELECT itemid,itemdesc FROM t_hr_busifield WHERE fieldsetid='P04' and state='1' AND useflag='1'  AND (itemid='P0419' OR ownflag<>'1') ORDER BY displayid";
		    RowSet rowSet = dao.search(sqlStr);
		    String targetTraceItem = this.getTargetTraceItem();
		    String[] items = targetTraceItem.split(",");
		    HashMap map = new HashMap();
		    for (int i = 0; i < items.length; i++) {
                map.put(items[i], "");
            }
	
		    while (rowSet.next())
		    {
				String itemdesc = rowSet.getString("itemdesc") == null ? "" : rowSet.getString("itemdesc");
				String itemid = rowSet.getString("itemid") == null ? "" : rowSet.getString("itemid");
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("itemid", itemid);
				abean.set("itemdesc", itemdesc);
				abean.set("selected", map.get(itemid) == null ? "0" : "1");
				list.add(abean);
		    }
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return list;
    }

    /** 取得目标卡采集指标 */
    public ArrayList getTargetCollectItemList()
    {

		ArrayList list = new ArrayList();
		try
		{
		    ContentDAO dao = new ContentDAO(this.cn);
		    String sqlStr = "SELECT itemid,itemdesc FROM t_hr_busifield WHERE fieldsetid='P04' AND useflag='1' and state='1' AND (itemid='P0419' OR ownflag<>'1') ORDER BY displayid";
		    RowSet rowSet = dao.search(sqlStr);
		    String targetTraceItem = this.getTargetCollectItem();
		    String[] items = targetTraceItem.split(",");
		    HashMap map = new HashMap();
		    for (int i = 0; i < items.length; i++) {
                map.put(items[i], "");
            }
	
		    while (rowSet.next())
		    {
				String itemdesc = rowSet.getString("itemdesc") == null ? "" : rowSet.getString("itemdesc");
				String itemid = rowSet.getString("itemid") == null ? "" : rowSet.getString("itemid");
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("itemid", itemid);
				abean.set("itemdesc", itemdesc);
				abean.set("selected", map.get(itemid) == null ? "0" : "1");
				list.add(abean);
		    }
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return list;
    }

    /** 取得待选择的邮件模板 */
    public ArrayList getEmailTempList()
    {

		ArrayList list = new ArrayList();
		list.add(new CommonData("-1", ""));
		try
		{
		    ContentDAO dao = new ContentDAO(this.cn);
		    String sqlStr = "SELECT id,name FROM EMAIL_NAME WHERE nModule=9 AND nInfoclass=1 ORDER BY id";//修改，现在绩效通过新增的通知模添加，nModule为9
		    RowSet rowSet = dao.search(sqlStr);
		    while (rowSet.next())
		    {
				String itemdesc = rowSet.getString("name") == null ? "" : rowSet.getString("name");
				String itemid = rowSet.getString("id") == null ? "" : rowSet.getString("id");
				CommonData data = new CommonData();
				data.setDataName(itemdesc);
				data.setDataValue(itemid);
				list.add(data);
		    }
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return list;
    }
    /**
     * 获取人员登记列表 zhaoxg 2014-4-23
     * @return
     */
    public ArrayList getRnameList(){
		ArrayList list = new ArrayList();
		list.add(new CommonData("#", "(无)"));
		list.add(new CommonData("", "(默认，取人员登记表)"));
		try
		{
		    ContentDAO dao = new ContentDAO(this.cn);
		    String sqlStr = "select Tabid,name from Rname where upper(FlagA)='A' ORDER BY Tabid";
		    RowSet rowSet = dao.search(sqlStr);
		    while (rowSet.next())
		    {
				String itemdesc = rowSet.getString("name") == null ? "" : rowSet.getString("name");
				String itemid = rowSet.getString("Tabid") == null ? "" : rowSet.getString("Tabid");
				CommonData data = new CommonData();
				data.setDataName(itemdesc);
				data.setDataValue(itemid);
				list.add(data);
		    }
		    if(rowSet!=null) {
                rowSet.close();
            }
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return list;
    }

    /** 取得待选择的业务模板 */
    public ArrayList getBusiTempList()
    {

		ArrayList list = new ArrayList();
		list.add(new CommonData("-1", ""));
		try
		{
		    ContentDAO dao = new ContentDAO(this.cn);
		    String _static="static";
		    if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
		    	_static="static_o";
		    }
		    String sqlStr = "SELECT TabId,Name FROM template_table WHERE "+_static+"=1";
		    RowSet rowSet = dao.search(sqlStr);
		    while (rowSet.next())
		    {
				String itemdesc = rowSet.getString("name") == null ? "" : rowSet.getString("name");
				String itemid = rowSet.getString("TabId") == null ? "" : rowSet.getString("TabId");
				CommonData data = new CommonData();
				data.setDataName(itemdesc);
				data.setDataValue(itemid);
				list.add(data);
		    }
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return list;
    }

    /** 目标卡跟踪显示指标 */
    public String getTargetTraceItem()
    {

		String items = "";
		try
		{
		    ContentDAO dao = new ContentDAO(this.cn);
		    RowSet rowSet = dao.search("select str_value from constant where constant='PER_PARAMETERS'");
		    if (rowSet.next())
		    {
				String str_value = rowSet.getString("str_value");
				if (str_value == null || (str_value != null && "".equals(str_value))) {
                    return items;
                } else
				{
				    try
				    {
						Document doc = PubFunc.generateDom(str_value);
						String xpath = "//Per_Parameters";
						XPath xpath_ = XPath.newInstance(xpath);
						Element ele = (Element) xpath_.selectSingleNode(doc);
						if (ele != null)
						{
						    ele = ele.getChild("TargetTraceItem");
						    if (ele != null) {
                                items = ele.getTextTrim();
                            }
						}
				    } catch (Exception e)
				    {
				    	e.printStackTrace();
				    }
				}
		    }
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return items;
    }
    /** 目标卡跟踪显示指标 */
    public String getTargetCalcItem()
    {

		String items = "";
		try
		{
		    ContentDAO dao = new ContentDAO(this.cn);
		    RowSet rowSet = dao.search("select str_value from constant where constant='PER_PARAMETERS'");
		    if (rowSet.next())
		    {
				String str_value = rowSet.getString("str_value");
				if (str_value == null || (str_value != null && "".equals(str_value))) {
                    return items;
                } else
				{
				    try
				    {
						Document doc = PubFunc.generateDom(str_value);
						String xpath = "//Per_Parameters";
						XPath xpath_ = XPath.newInstance(xpath);
						Element ele = (Element) xpath_.selectSingleNode(doc);
						if (ele != null)
						{
						    ele = ele.getChild("TargetCalcItem");
						    if (ele != null) {
                                items = ele.getTextTrim();
                            }
						}
				    } catch (Exception e)
				    {
				    	e.printStackTrace();
				    }
				}
		    }
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return items;
    }
    /** 目标卡采集指标 */
    public String getTargetCollectItem()
    {

		String items = "";
		try
		{
		    ContentDAO dao = new ContentDAO(this.cn);
		    RowSet rowSet = dao.search("select str_value from constant where constant='PER_PARAMETERS'");
		    if (rowSet.next())
		    {
				String str_value = rowSet.getString("str_value");
				if (str_value == null || (str_value != null && "".equals(str_value))) {
                    return items;
                } else
				{
				    try
				    {
						Document doc = PubFunc.generateDom(str_value);
						String xpath = "//Per_Parameters";
						XPath xpath_ = XPath.newInstance(xpath);
						Element ele = (Element) xpath_.selectSingleNode(doc);
						if (ele != null)
						{
						    ele = ele.getChild("TargetCollectItem");
						    if (ele != null) {
                                items = ele.getTextTrim();
                            }
						}
				    } catch (Exception e)
				    {
				    	e.printStackTrace();
				    }
				}
		    }
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return items;
    }

    /** 取得职位子集 */
    public ArrayList getPostSubset()
    {

		ArrayList list = new ArrayList();
		CommonData dataobj = new CommonData("", "");
		list.add(dataobj);
		ArrayList fieldsetlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.POS_FIELD_SET);
		for (int i = 0; i < fieldsetlist.size(); i++)
		{
		    FieldSet fieldset = (FieldSet) fieldsetlist.get(i);
		    if ("K00".equalsIgnoreCase(fieldset.getFieldsetid()) || "K01".equalsIgnoreCase(fieldset.getFieldsetid())) {
                continue;
            }
		    dataobj = new CommonData(fieldset.getFieldsetid(), fieldset.getFieldsetid() + ":" + fieldset.getCustomdesc());
		    list.add(dataobj);
		}
		return list;
    }

    /** 取得职位子集的指标项目 */
    public ArrayList getPostSubsetItems(String subSet, String itemType,int flag)
    {

		ArrayList list = new ArrayList();
	
		CommonData dataobj = new CommonData("", "");
		list.add(dataobj);
	
		if(subSet.trim().length()==0) {
            return list;
        }
		StringBuffer buf = new StringBuffer();
		buf.append("select * from fielditem where useflag=1 and fieldsetid='");
		buf.append(subSet);
		buf.append("'");
		if (itemType.length() > 0) {
            buf.append(" and itemtype ='" + itemType + "'");
        }
		if(flag==2)//取非代码型
        {
            buf.append(" and codesetid ='0'");
        }
		try
		{
		    ContentDAO dao = new ContentDAO(this.cn);
		    RowSet rs = dao.search(buf.toString());
		    while (rs.next())
		    {
		    	String itemid = rs.getString("itemid");
		    	
				if(flag==0 || flag==2) {
                    list.add(new CommonData(rs.getString("itemid"), rs.getString("itemid") + ":" + rs.getString("itemdesc")));
                } else if(flag==1) {
                    list.add(new CommonData(rs.getString("itemid"), rs.getString("itemdesc")));
                }
		    }
		    if(rs!=null) {
                rs.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return list;
    }

    /** 取得子集中对应的代码型指标集 */
    public ArrayList getDestFldIds(String targetPostSet,String codesetid)
    {
		ArrayList list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append("select * from fielditem where fieldsetid='");
		buf.append(targetPostSet);
		buf.append("' and codesetid='"+codesetid+"'");
		try
		{
		    ContentDAO dao = new ContentDAO(this.cn);
		    RowSet rs = dao.search(buf.toString());
		    while (rs.next()) {
                list.add(new CommonData(rs.getString("itemid"), rs.getString("itemdesc")));
            }
		    
		    if(rs!=null) {
                rs.close();
            }
	
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return list;
    }
    
    
    /** 取得目标卡岗位职责参数对应 */
    public ArrayList getTargetAccordList(String accordString,String targetPostSet)
    {

		ArrayList list = new ArrayList();
		accordString = accordString.replaceAll("＝", "=");
		String[] accords = accordString.split(","); //P0407=K1502,P0419=K1503
		HashMap accordmap = new HashMap();
		for(int j=0;j<accords.length;j++)
		{
		    if(accords[j].trim().length()>0)
		    {
				String[] temp = accords[j].trim().split("=");
				accordmap.put(temp[0].toLowerCase(), temp[1].toLowerCase());
		    }
		}	
		
		ArrayList type_A_list = this.getPostSubsetItems(targetPostSet,"A",1);
		ArrayList type_D_list = this.getPostSubsetItems(targetPostSet,"D",1);
		ArrayList type_N_list = this.getPostSubsetItems(targetPostSet,"N",1);
		ArrayList type_M_list = this.getPostSubsetItems(targetPostSet,"M",1);
		
		ArrayList targetList = DataDictionary.getFieldList("P04", Constant.USED_FIELD_SET);
		String filterFlds = "p0400,nbase,a0100,a0101,b0110,e0122,e01a1,p0401,plan_id,p_p0400,item_id,F_P0400,state,fromflag";
		for (int i = 0; i < targetList.size(); i++)
		{
		    FieldItem item = (FieldItem) targetList.get(i);
		    String itemid = item.getItemid();
		    if (filterFlds.toLowerCase().indexOf(itemid.toLowerCase()) != -1) {
                continue;
            }
		    String itemtype = item.getItemtype();
		    String itemdesc = item.getItemdesc();
		    String codesetid = item.getCodesetid();
		    int itemLength = item.getItemlength();
		    int decimalwidth = item.getDecimalwidth();
		    String destFldId =  accordmap.get(itemid)==null?"":(String)accordmap.get(itemid);
		    
		    String dataType = "";
		    ArrayList destFldIds = new ArrayList();
		    if("A".equals(itemtype))
		    {			
				destFldIds=type_A_list;
				if(!"0".equals(codesetid))//如果目标卡指标是代码类型的，子集指标待选的也列出所有字符型的包括非代码的字符指标
		//		    destFldIds = getDestFldIds(targetPostSet,codesetid);
                {
                    dataType="代码型("+itemLength+","+codesetid+")";
                } else {
                    dataType="字符型("+itemLength+")";
                }
		    }
		    else if("D".equals(itemtype))
		    {
				destFldIds=type_D_list;
				dataType="日期型";
		    }		
		    else if("M".equals(itemtype))
		    {
				destFldIds=type_M_list;
				dataType="备注型";
		    }		
		    else if("N".equals(itemtype))
		    {
				destFldIds=type_N_list;
				if(decimalwidth>0) {
                    dataType="数值型("+itemLength+","+decimalwidth+")";
                } else if(decimalwidth==0) {
                    dataType="数值型("+itemLength+")";
                }
		    }		
		    
		    LazyDynaBean abean = new LazyDynaBean();
		    abean.set("itemid", itemid);
		    abean.set("name", itemdesc);		    
		    abean.set("destFldIds", destFldIds);
		    
		    boolean haveFeld = false;
		    for (int k = 0; k < destFldIds.size(); k++)
			{
			    CommonData d = (CommonData)destFldIds.get(k);
			    if(destFldId.toUpperCase().equalsIgnoreCase(d.getDataValue().toUpperCase()))
			    {
			    	haveFeld = true;
			    	break;
			    }	
			}
		    if(haveFeld) {
                abean.set("destFldId", destFldId.toUpperCase());
            } else {
                abean.set("destFldId", "");
            }
		    abean.set("dataType", dataType);
		    list.add(abean);
		}
		return list;
    }
    /** 取得目标跟踪显示指标 */
    public ArrayList getItemList2()
    {

		ArrayList list = new ArrayList();
		list.add(new CommonData("", ""));
		RowSet rowSet = null;
		try
		{
		    ContentDAO dao = new ContentDAO(this.cn);
		    String sqlStr = "SELECT itemid,itemdesc FROM t_hr_busifield WHERE fieldsetid='P04' and itemtype='M' ";
		    rowSet = dao.search(sqlStr);
	
		    while (rowSet.next())	
		    {
		    	String itemid = rowSet.getString("itemid");
		    	if(("p0407".equalsIgnoreCase(itemid)) || ("p0425".equalsIgnoreCase(itemid))) {
                    continue;
                }
		    	
		    	list.add(new CommonData(rowSet.getString("itemid"), rowSet.getString("itemdesc")));
		    }
		    
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return list;
    }
    public static ArrayList getConfigDrawList(int type,String config)
    {
    	ArrayList list = new ArrayList();
    	if(type==1)//360
    	{
    		LazyDynaBean bean = null;
    		bean = new LazyDynaBean();
    		bean.set("id", "1");
    		bean.set("name",ResourceFactory.getProperty("general.inform.org.graph"));
    		if(config.toUpperCase().indexOf(",1,")!=-1|| "".equals(config)) {
                bean.set("select", "1");
            } else {
                bean.set("select","0");
            }
    		list.add(bean);
    		
    		bean = new LazyDynaBean();
    		bean.set("id","0");
    		bean.set("name", ResourceFactory.getProperty("org.performance.table"));
    		if(config.toUpperCase().indexOf(",0,")!=-1|| "".equals(config)) {
                bean.set("select", "1");
            } else {
                bean.set("select","0");
            }
    		list.add(bean);
    		
    		bean = new LazyDynaBean();
    		bean.set("id","DJ");
    		bean.set("name", ResourceFactory.getProperty("sys.res.card"));
    		if(config.toUpperCase().indexOf(",DJ,")!=-1|| "".equals(config)) {
                bean.set("select", "1");
            } else {
                bean.set("select","0");
            }
    		list.add(bean);  
    		
    		bean = new LazyDynaBean();
    		bean.set("id","2");
    		bean.set("name",ResourceFactory.getProperty("org.performance.kp"));
    		if(config.toUpperCase().indexOf(",2,")!=-1|| "".equals(config)) {
                bean.set("select", "1");
            } else {
                bean.set("select","0");
            }
    		list.add(bean);
    		
    		bean = new LazyDynaBean();
    		bean.set("id","3");
    		bean.set("name",ResourceFactory.getProperty("org.performance.zt"));
    		if(config.toUpperCase().indexOf(",3,")!=-1|| "".equals(config)) {
                bean.set("select", "1");
            } else {
                bean.set("select","0");
            }
    		list.add(bean);
    		
    		bean = new LazyDynaBean();
    		bean.set("id","4");
    		bean.set("name",ResourceFactory.getProperty("org.performance.qs"));
    		if(config.toUpperCase().indexOf(",4,")!=-1|| "".equals(config)) {
                bean.set("select", "1");
            } else {
                bean.set("select","0");
            }
    		list.add(bean);
    		//评价盲点 郭峰增加
    		bean = new LazyDynaBean();
    		bean.set("id","7");
    		bean.set("name",ResourceFactory.getProperty("org.performance.blind"));
    		if(config.toUpperCase().indexOf(",7,")!=-1|| "".equals(config)) {
                bean.set("select", "1");
            } else {
                bean.set("select","0");
            }
    		list.add(bean);
    	}
    	else
    	{
    		LazyDynaBean bean = null;
    		bean = new LazyDynaBean();
    		bean.set("id", "1");
    		bean.set("name",ResourceFactory.getProperty("general.inform.org.graph"));
    		if(config.toUpperCase().indexOf(",1,")!=-1|| "".equals(config)) {
                bean.set("select", "1");
            } else {
                bean.set("select","0");
            }
    		list.add(bean);
    		
    		bean = new LazyDynaBean();
    		bean.set("id", "5");
    		bean.set("name",ResourceFactory.getProperty("jx.khplan.khresulttable"));
    		if(config.toUpperCase().indexOf(",5,")!=-1|| "".equals(config)) {
                bean.set("select", "1");
            } else {
                bean.set("select","0");
            }
    		list.add(bean);
    		
    		bean = new LazyDynaBean();
    		bean.set("id", "2");
    		bean.set("name",ResourceFactory.getProperty("org.performance.kp"));
    		if(config.toUpperCase().indexOf(",2,")!=-1|| "".equals(config)) {
                bean.set("select", "1");
            } else {
                bean.set("select","0");
            }
    		list.add(bean);
    		
    		bean = new LazyDynaBean();
    		bean.set("id", "6");
    		bean.set("name",ResourceFactory.getProperty("jx.khplan.interview"));
    		if(config.toUpperCase().indexOf(",6,")!=-1|| "".equals(config)) {
                bean.set("select", "1");
            } else {
                bean.set("select","0");
            }
    		list.add(bean);
    		
    		bean = new LazyDynaBean();
    		bean.set("id","DJ");
    		bean.set("name", ResourceFactory.getProperty("sys.res.card"));
    		if(config.toUpperCase().indexOf(",DJ,")!=-1|| "".equals(config)) {
                bean.set("select", "1");
            } else {
                bean.set("select","0");
            }
    		list.add(bean);  
    		//评价盲点 郭峰增加
    		bean = new LazyDynaBean();
    		bean.set("id","7");
    		bean.set("name",ResourceFactory.getProperty("org.performance.blind"));
    		if(config.toUpperCase().indexOf(",7,")!=-1|| "".equals(config)) {
                bean.set("select", "1");
            } else {
                bean.set("select","0");
            }
    		list.add(bean);
    	}
    	return list;
    }
    
    
    ////////////////////////////////目标卡部门职责参数////////////////////////////////////
    /**
     * 获得部门职责子集列表
     * (按年变化的单位的非B01的子集)
     * **/
    public ArrayList getDepartSetList(){
    	ArrayList list = new ArrayList();
    	list.add(new CommonData("",""));
    	RowSet rs = null;
    	try{
    		ContentDAO dao = new ContentDAO(this.cn);
    		StringBuffer sb = new StringBuffer();
    		sb.append("select fieldsetid,fieldsetdesc from fieldset where  fieldsetid like 'B%' and fieldsetid <>'B01' and useflag=1 and changeflag='2' order by displayorder");
    		rs = dao.search(sb.toString());
    		while(rs.next()){
    			String fieldsetid = rs.getString("fieldsetid");
    			String fieldsetdesc = rs.getString("fieldsetdesc");
    			list.add(new CommonData(fieldsetid,fieldsetdesc));
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
    	return list;
    }

    /**
     *获得全体数据的list列表
     * */
    public ArrayList getFieldList(String accordString,String fieldsetname){
    	HashMap filtermap = new HashMap();//把需要过滤的字段装进去
    	filtermap.put("P0400", "1");
    	filtermap.put("PLAN_ID", "1");
    	filtermap.put("B0110", "1");
    	filtermap.put("E0122", "1");
    	filtermap.put("E01A1", "1");
    	filtermap.put("NBASE", "1");
    	filtermap.put("A0100", "1");
    	filtermap.put("A0101", "1");
    	filtermap.put("P0401", "1");
    	filtermap.put("P_P0400", "1");
    	filtermap.put("ITEM_ID", "1");
    	filtermap.put("F_P0400", "1");
    	filtermap.put("STATE", "1");
    	filtermap.put("FROMFLAG", "1");
    	
    	HashMap columnmap =  new HashMap();
    	accordString=accordString.replace("＝","=");//20150506数据源中有大写等号，导致下面分割时报错 
    	String[] strarray = accordString.split(",");
    	int arraycount = strarray.length;
    	for(int i=0;i<arraycount;i++){
    		if("".equals(strarray[i])){
    			continue;
    		}
    		String[] innerarray = strarray[i].split("=");
    		if(!"".equals(innerarray[0]) && !"".equals(innerarray[1])){
    			columnmap.put(innerarray[0], innerarray[1]);
    		}
    	}
    	
    	ArrayList[] arrayList = getSetFieldList(fieldsetname);//list[0]为字符型指标，list[1]为代码型，list[2]为数值型，list[3]为日期型，list[4]为备注型
    	
    	ArrayList list = new ArrayList();
    	ArrayList fieldList=(ArrayList)DataDictionary.getFieldList("P04",Constant.USED_FIELD_SET);
    	int count = fieldList.size();
    	for(int i=0;i<count;i++)
		{
			FieldItem fielditem = (FieldItem)fieldList.get(i);
			if("0".equals(fielditem.getState())){
				continue;
			}

			String itemid = fielditem.getItemid().toUpperCase();
			if(filtermap.get(itemid)!=null){
				continue;
			}
			LazyDynaBean bean = new LazyDynaBean();
			String fieldSet = columnmap.get(itemid)==null?"":(String)columnmap.get(itemid);//子集的显示
			ArrayList fieldSetList = new ArrayList();
			String itemdesc = fielditem.getItemdesc();
			String showType = "";
			String itemtype = fielditem.getItemtype();
			if("A".equalsIgnoreCase(itemtype)){
				int length = fielditem.getItemlength();
				String codesetid = fielditem.getCodesetid();
				if("0".equals(codesetid)){//字符型
					showType +="字符型("+length+")";
					fieldSetList = arrayList[0];
				}else{
					showType +="代码型("+length+","+codesetid+")";
					fieldSetList = arrayList[1];
				}
			}else if("N".equalsIgnoreCase(itemtype)){//数值型
				int integerlength = fielditem.getItemlength();
				int decimallength = fielditem.getDecimalwidth();
				showType +="数值型("+integerlength+","+decimallength+")";
				fieldSetList = arrayList[2];
			}else if("D".equalsIgnoreCase(itemtype)){
				showType +="日期型";
				fieldSetList = arrayList[3];
			}else if("M".equalsIgnoreCase(itemtype)){
				showType +="备注型";
				fieldSetList = arrayList[4];
			}
			bean.set("departfieldid", itemid);//字段id
			bean.set("departfieldname", itemdesc);//字段名字
			bean.set("departfieldtype", showType);//字段类型
			bean.set("departfieldset", fieldSet);//子集的显示
			bean.set("departfieldsetlist", fieldSetList);//子集列表
			list.add(bean);
		}
    	return list;
    }
    /**得到子集的项目列表
     *list[0]为字符型指标，list[1]为代码型，list[2]为数值型，list[3]为日期型，list[4]为备注型
     * **/
    public ArrayList[] getSetFieldList(String fieldsetname){
    	
    	ArrayList[] list = new ArrayList[5];
    	for(int i=0;i<list.length;i++){
    		list[i] = new ArrayList();
    		list[i].add(new CommonData("",""));
    	}
    	if(fieldsetname==null || "".equals(fieldsetname)){
    		return list;
    	}
    	ArrayList fieldList=(ArrayList)DataDictionary.getFieldList(fieldsetname,Constant.USED_FIELD_SET);
    	int count = fieldList.size();
    	for(int i=0;i<count;i++)
		{
			FieldItem fielditem = (FieldItem)fieldList.get(i);
			String itemid = fielditem.getItemid().toUpperCase();
			if(itemid==null || "".equals(itemid)){
				continue;
			}
			String itemdesc = fielditem.getItemdesc();
			String itemtype = fielditem.getItemtype();
			String codesetid = fielditem.getCodesetid();
			if("A".equalsIgnoreCase(itemtype)){
				if("0".equals(codesetid)){//字符型
					list[0].add(new CommonData(itemid,itemdesc));
					list[4].add(new CommonData(itemid,itemdesc));
				}else{
					list[1].add(new CommonData(itemid,itemdesc));
				}
			}else if("N".equalsIgnoreCase(itemtype)){
				list[2].add(new CommonData(itemid,itemdesc));
			}else if("D".equalsIgnoreCase(itemtype)){
				list[3].add(new CommonData(itemid,itemdesc));
			}else if("M".equalsIgnoreCase(itemtype)){
				list[4].add(new CommonData(itemid,itemdesc));
			}
		}
    	return list;
    }
}
