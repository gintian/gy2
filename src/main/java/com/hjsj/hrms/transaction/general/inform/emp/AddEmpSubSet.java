package com.hjsj.hrms.transaction.general.inform.emp;

import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:AddEmpSubSet.java
 * </p>
 * <p>
 * Description:人员相关子集的新增/编辑操作
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-04-11 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class AddEmpSubSet extends IBusiness
{
    public void execute() throws GeneralException
    {
        ArrayList fieldInfoList = new ArrayList();
        ArrayList readOnlyFlds = new ArrayList();
        String i9999 = "";
        try
        {
        	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        	String oper = (String) hm.get("oper");	
        	hm.remove("oper");
        	
        	//curri9999 用于插入方式新增记录 标志在curri9999标志的记录前面插入记录
        	String curri9999 = (String) hm.get("curri9999");	
        	hm.remove("curri9999");
        	this.getFormHM().put("curri9999",curri9999);
        	
        	String dateFld = (String) hm.get("dateFld");	
        	hm.remove("dateFld");
        	String userFld = (String) hm.get("userFld");	
        	hm.remove("userFld");
        		
        	String a0100 = (String) hm.get("a0100");
        	a0100 = a0100 != null ? a0100 : "";
        	hm.remove("a0100");
        	this.getFormHM().put("a0100", a0100);
        	
        
        	i9999 = (String) hm.get("i9999");
        	i9999 = i9999 != null ? i9999 : "";
        	hm.remove("i9999");	
        	
        	String dbname = (String) hm.get("dbname");
        	dbname = dbname != null ? dbname : "";
        	hm.remove("dbname");
        	this.getFormHM().put("dbname", dbname);
        	
        
        	String itemtable = (String) hm.get("subset");
        	this.getFormHM().put("itemtable", itemtable != null ? itemtable : "");
        	hm.remove("subset");
        	
        	String flag = (String) hm.get("flag");
            this.getFormHM().put("flag", flag != null ? flag : "");
            hm.remove("flag");
            
            if(!"train".equalsIgnoreCase(flag)){
                itemtable = PubFunc.decrypt(itemtable);
                dbname = PubFunc.decrypt(dbname);
                a0100 = PubFunc.decrypt(a0100);
            }
        	
        	if (null != itemtable && 3 == itemtable.length())
        	    itemtable = dbname + itemtable;
        	
        	ArrayList fieldList = new ArrayList();
        	
        	if("t_vorg_staff".equalsIgnoreCase(itemtable)){
        		GzDataMaintBo gzbo = new GzDataMaintBo(this.frameconn);
        		fieldList = gzbo.torgFieldItemList();
        	}else{
        		fieldList = DataDictionary.getFieldList(itemtable.substring(3), Constant.USED_FIELD_SET);
        	}
        	ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());/** duml 使在面试过程子集 必填的五项指标 保持必填*/
        	HashMap map=parameterXMLBo.getAttributeValues();
        	HashMap configMap=null;
        	if(map!=null)
        		configMap=(HashMap)map.get("infoMap");
        	/**招聘中专用*/
        	HashMap memo=null;
        	 if(oper!=null && "zp".equalsIgnoreCase(oper))
        			 memo=this.getItemMemo(itemtable.substring(3));
        	
        	ContentDAO dao = new ContentDAO(this.getFrameconn());
    
    //	String itemidarr = (String) hm.get("readonlyFilds");// 只读指标 r3127,r3128,	
    //	hm.remove("readonlyFilds");
    //	String[] readonlyFilds = null;
    //	ArrayList readonlyFilds1 = new ArrayList();
    //	if (itemidarr != null && !itemidarr.equals(""))
    //	    readonlyFilds = itemidarr.split(",");
    //	
    //	if (readonlyFilds != null)
    //	    for (int j = 0; j < readonlyFilds.length; j++)
    //	    {
    //		String temp = readonlyFilds[j];
    //		if (temp.equals(""))
    //		    continue;
    //		LazyDynaBean abean = new LazyDynaBean();
    //		abean.set("itemid", temp);
    //		readonlyFilds1.add(abean);
    //	    }
    	
    
    	
    	
    //	if(curri9999!=null)//插入方式新增记录
    //	    i9999 = DbNameBo.insertSubSetA0100(itemtable, a0100, curri9999,getFrameconn());
    
    	    for (int i = 0; i < fieldList.size(); i++)// 循环字段
    	    {
    		FieldItem fieldItem = (FieldItem) fieldList.get(i);
    		String itemid = fieldItem.getItemid();
    		String itemName = fieldItem.getItemdesc();
    		String itemType = fieldItem.getItemtype();
    		String codesetId = fieldItem.getCodesetid();
    		if ("i9999".equalsIgnoreCase(itemid))
    		    continue;
    		 if(oper==null || oper!=null && !"zp".equalsIgnoreCase(oper))
    		 {
    		     if("0".equals(this.userView.analyseFieldPriv(itemid)))
    			    continue;
    		
    		
    		 String pri = this.userView.analyseFieldPriv(fieldItem.getItemid());
    		    if("1".equals(pri))//只读
    		    {
    			LazyDynaBean abean = new LazyDynaBean();
    			abean.set("itemid", fieldItem.getItemid());
    			abean.set("codesetid", fieldItem.getCodesetid());
    			readOnlyFlds.add(abean);
    		    }
    		    if("0".equals(pri))//没有权限
    			continue;
    		 }
    		 
    		 
    		if(fieldItem.getDisplaywidth()==0)
    		    continue;
    		FieldItemView fieldItemView = new FieldItemView();
    		fieldItemView.setAuditingFormula(fieldItem.getAuditingFormula());
    		fieldItemView.setAuditingInformation(fieldItem.getAuditingInformation());
    		fieldItemView.setCodesetid(codesetId);
    		fieldItemView.setDecimalwidth(fieldItem.getDecimalwidth());
    		fieldItemView.setDisplayid(fieldItem.getDisplayid());
    		fieldItemView.setDisplaywidth(fieldItem.getDisplaywidth());
    		fieldItemView.setExplain(fieldItem.getExplain());
    		fieldItemView.setFieldsetid(fieldItem.getFieldsetid());
    		
    		if(oper!=null && "zp".equalsIgnoreCase(oper)&&memo!=null)
    		{
    			String itemmemo =(String)(memo.get(fieldItem.getItemid().toUpperCase())==null?"":memo.get(fieldItem.getItemid().toUpperCase()));
    			if(itemmemo==null || itemmemo.length()==0)
    				itemmemo="-1";
    			fieldItemView.setExplain(itemmemo);
    		}
    		else
    		{
    			fieldItemView.setExplain("-1");
    		}
    		 
    	    fieldItemView.setItemdesc(itemName);
    		fieldItemView.setItemid(itemid);
    		fieldItemView.setItemlength(fieldItem.getItemlength());
    		fieldItemView.setItemtype(itemType);
    		fieldItemView.setModuleflag(fieldItem.getModuleflag());
    		fieldItemView.setState(fieldItem.getState());
    		fieldItemView.setUseflag(fieldItem.getUseflag());
    		fieldItemView.setPriv_status(fieldItem.getPriv_status());
    		fieldItemView.setRowflag(String.valueOf(fieldList.size() - 1)); // 在struts用来表示换行的变量
    	
    		//if(configMap!=null&&(((String)configMap.get("title")).equalsIgnoreCase(fieldItem.getItemid())||((String)configMap.get("comment_user")).equalsIgnoreCase(fieldItem.getItemid())||((String)configMap.get("content")).equalsIgnoreCase(fieldItem.getItemid())||((String)configMap.get("level")).equalsIgnoreCase(fieldItem.getItemid())||((String)configMap.get("comment_date")).equalsIgnoreCase(fieldItem.getItemid()))){
    		//fieldItemView.setFillable(true);
    		//}else dml
    		fieldItemView.setFillable(fieldItem.isFillable());
    		if(configMap!=null&&((String)configMap.get("comment_user")).equalsIgnoreCase(fieldItem.getItemid())){//dml
    			fieldItemView.setReadonly(true);
    		}else
    			fieldItemView.setReadonly(fieldItem.isReadonly());
    		if ("0".equals(i9999))// 新建
    		{
    		    String temp = "";
    		    if(oper!=null && "zp".equalsIgnoreCase(oper) && (itemid.equalsIgnoreCase(userFld) || itemid.equalsIgnoreCase(dateFld)))
    		    {			
    			    if(itemid.equalsIgnoreCase(userFld))
    				temp = this.userView.getUserFullName();
    			    if(itemid.equalsIgnoreCase(dateFld))
    				temp = PubFunc.getStringDate("yyyy-MM-dd");				
    		    }		    
    			fieldItemView.setViewvalue(temp);
    			fieldItemView.setValue(temp);
    		    
    		} else
    		// 修改
    		{
    		    StringBuffer strsql = new StringBuffer();
    		    
    		    if("t_vorg_staff".equalsIgnoreCase(itemtable)){
    		    	strsql.append("select ");
    		    	if("state".equalsIgnoreCase(itemid)){
    					if(Sql_switcher.searchDbServer()==Constant.ORACEL
    							||Sql_switcher.searchDbServer()==Constant.DB2){
    						strsql.append("TRUNC("+itemid+",0) as "+itemid);
    					}else
    						strsql.append("CAST("+itemid+" AS INT) as "+itemid);
    				}else if("orgname".equalsIgnoreCase(itemid)){
    					strsql.append(itemid);
    					strsql.append(",b0110");
    				}else
    					strsql.append(itemid);
    		    	strsql.append(" from ");
    		    	strsql.append(itemtable + " where a0100='");
    		    	strsql.append(a0100 + "' and i9999=");
    		    	strsql.append(i9999);
    		    	strsql.append(" and dbase='");
    		    	strsql.append(dbname+"'");
    		    }else{
    		    	strsql.append("select " + itemid + " from ");
    		    	strsql.append(itemtable + " where a0100='");
    		    	strsql.append(a0100 + "' and i9999=");
    		    	strsql.append(i9999);
    		    }
    
    		    this.frowset = dao.search(strsql.toString());
    		    if (this.frowset.next())
    		    {
    			Object val = null;
    			if ("D".equalsIgnoreCase(itemType) && Sql_switcher.searchDbServer() == Constant.ORACEL)// 日期型
                                                                                                                    // oracle数据库必须这样取数据
    			    val = this.getFrowset().getDate(itemid);
    			else
    			    val = this.frowset.getString(itemid);
    
    			if (val == null)
    			{
    			    fieldItemView.setViewvalue("");
    			    fieldItemView.setValue("");
    			} else
    			{
    			    if ("A".equals(itemType) || "M".equals(itemType))
    			    {
    				String value = (String) val;
    				if (!"0".equals(codesetId))
    				{
    					 if("orgname".equalsIgnoreCase(itemid)){
    						 String b0110 =  this.frowset.getString("b0110");
    						 fieldItemView.setValue(b0110);
    						 fieldItemView.setViewvalue(value);
    					 }else{
    						 String codevalue = value;
    						 if (codevalue.trim().length() > 0 && codesetId != null && codesetId.trim().length() > 0)
    							 fieldItemView.setViewvalue(AdminCode.getCode(codesetId, codevalue) != null ? AdminCode.getCode(codesetId, codevalue).getCodename() : "");
    						 else
    							 fieldItemView.setViewvalue("");
    						 fieldItemView.setValue(value != null ? value.toString() : "");
    					 }
    				} else
    				{
    				    fieldItemView.setViewvalue(value);
    				    fieldItemView.setValue(value);
    				}
    			    } else if ("D".equals(itemType)) // 日期型有待格式化处理
    			    {
    				if (Sql_switcher.searchDbServer() == Constant.MSSQL)
    				{
    				    String value = (String) val;
    
    				    if (value != null && value.length() >= 10 && fieldItem.getItemlength() == 10)
    				    {
    					value = new FormatValue().format(fieldItem, value.substring(0, 10));
    					value = PubFunc.replace(value, ".", "-");
    					fieldItemView.setViewvalue(value);
    					fieldItemView.setValue(value);
    				    } else if (value != null && value.toString().length() >= 10 && fieldItem.getItemlength() == 4)
    				    {
    					value = new FormatValue().format(fieldItem, value.substring(0, 4));
    					value = PubFunc.replace(value, ".", "-");
    					fieldItemView.setViewvalue(value);
    					fieldItemView.setValue(value);
    				    } else if (value != null && value.toString().length() >= 10 && fieldItem.getItemlength() == 7)
    				    {
    					value = new FormatValue().format(fieldItem, value.substring(0, 7));
    					value = PubFunc.replace(value, ".", "-");
    					fieldItemView.setViewvalue(value);
    					fieldItemView.setValue(value);
    				    } else
    				    {
    					fieldItemView.setViewvalue("");
    					fieldItemView.setValue("");
    				    }
    				} else if (Sql_switcher.searchDbServer() == Constant.ORACEL)
    				{
    				    Date dateVal = (Date) val;
    				    fieldItemView.setViewvalue(dateVal.toString());
    				    fieldItemView.setValue(dateVal.toString());
    				}
    
    			    } else
    			    // 数值类型的有待格式化处理
    			    {
    				String value = (String) val;
    				fieldItemView.setValue(PubFunc.DoFormatDecimal(value != null ? value.toString() : "", fieldItem.getDecimalwidth()));
    			    }
    			}
    		    }
    
    		}
    		fieldInfoList.add(fieldItemView);
    
    	    }
            String empInfo = "";
    	    if(!"".equals(dbname)){//zzk 2013/12/31 兼容面试评价
    		    String sql = " select (select codeitemdesc from organization where codeitemid=A.b0110) orgName," +
    			" (select codeitemdesc from organization where codeitemid=A.e0122) deptName,A0101 empName " +
    			" from "+dbname+"A01 A where A.a0100='"+a0100+"'";
    	
    	        frowset = dao.search(sql);
    	        if(frowset.next()){//由于该人员可能还没进入人员库,没有岗位和部门，所以处理一下为null的情况
    	        	String orgName = frowset.getString("orgName")==null?"":frowset.getString("orgName");
    	        	String deptName = frowset.getString("deptName")==null?"":frowset.getString("deptName");
    	        	empInfo = "（单位名称："+orgName+"&nbsp;&nbsp;&nbsp;&nbsp;部门："+deptName+"&nbsp;&nbsp;&nbsp;&nbsp;姓名："+frowset.getString("empName")+"） ";
    	        }
    	        	
    	    }
            this.getFormHM().put("empInfo", empInfo);
    	} catch (Exception e)
    	{
    	    e.printStackTrace();
    	    throw GeneralExceptionHandler.Handle(e);
    	} finally
    	{
    	    this.getFormHM().put("SubFlds", fieldInfoList);
    	    this.getFormHM().put("i9999", i9999);
    	    this.getFormHM().put("readOnlyFlds",readOnlyFlds);
    	}

    }
    public HashMap getItemMemo(String fieldsetid)
    {
    	HashMap map = new HashMap();
    	try
    	{
    		ContentDAO dao  = new ContentDAO(this.getFrameconn());
    		this.frowset=dao.search("select itemid,itemmemo from fielditem where UPPER(fieldsetid)='"+fieldsetid.toUpperCase()+"'"); 
    		while(this.frowset.next())
    		{
    			String dd=this.frowset.getString(2);
    			if(dd==null||dd.length()==0|| "NULL".equalsIgnoreCase(dd)){
    				map.put(this.frowset.getString(1).toUpperCase(),"");
    			}else{
    				map.put(this.frowset.getString(1).toUpperCase(), this.frowset.getString(2));
    			}
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return map;
    }

}
