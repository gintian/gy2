/**
 * 
 */
package com.hjsj.hrms.businessobject.general.template;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.ajax.TransVo;
import com.hrms.struts.taglib.DataTable;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSetMetaData;
import java.util.*;
import java.util.Map.Entry;

/**
 * <p>Title:AutoFormBo</p>
 * <p>Description:创建自动提交表单</p> 
 * <p>Company:hjsj</p> 
 * create time at:Sep 29, 20062:41:11 PM
 * @author chenmengqing
 * @version 4.0
 */
public class AutoFormBo {
	/**
	 * 数据集名称
	 */
	private String name;
	/**
	 * 查询语句
	 */
	private String sql;
	/**业务类型*/
	private int flag;
	private String sp_flag;//任务状态，2为已批从任务监控里进到卡片方式。
	private UserView userview;
	private TemplateTableBo tablebo;
	private StringBuffer signxml=new StringBuffer();
	private HashMap sum_domain_map = new HashMap();
	boolean isFromApply=false; //来自我的申请
	private HashMap templateFieldPriv_node=new HashMap();  //节点下指标权限
	private HashMap subSet_width_title_map=new HashMap();  //模板每个子集的 指标列宽和自定义标题  20160810 dengcan
	
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSp_flag() {
		return sp_flag;
	}
	public void setSp_flag(String sp_flag) {
		this.sp_flag = sp_flag;
	}
	public AutoFormBo()
	{
		
	}
	public AutoFormBo(String name,String sql,int flag) {
		this.name=name;
		this.sql=sql;
		this.flag=flag;
	}
	
	public AutoFormBo(String name,int flag) {
		this.name=name;
		this.flag=flag;
	}	
	/**
	 * 取得对应的刷新数据
	 * @param transvo
	 * @param rset
	 * @param conn
	 */
	public void getDataSetRecord(TransVo transvo,RowSet rset,Connection conn,HashMap f_cellhm)
	{
		try
		{
			ArrayList fieldlist=transvo.getFieldlist();
            String basepre="";
            String a0100="";
            String value="";
            HashMap codemp=new HashMap();
			while(rset.next())
			{
				transvo.addRow();
                basepre=rset.getString("basepre");
                a0100=rset.getString("a0100");				
				for(int i=0;i<fieldlist.size();i++)
				{
					Field item=(Field)fieldlist.get(i);
                    if(item.getDataType()==DataType.BLOB&& "photo".equalsIgnoreCase(item.getName()))
                    {
                    	String filephoto="";
                    	if(flag==0) {
                            filephoto=ServletUtilities.createOleFile("photo","ext",rset);
                        } else {
                            filephoto=ServletUtilities.createOleFile(basepre+"A00",a0100,conn);
                        }
                    	if(!(filephoto==null|| "".equals(filephoto))) {
                            value="/servlet/DisplayOleContent?filename="+filephoto;
                        } else {
                            value="";
                        }
                    }else   if("attachment".equalsIgnoreCase(item.getName()))
                    {
                    	
                    	
                    }
                    else
                    {
                    	if(f_cellhm.containsKey(item.getName()))
                    	{
                    		TemplateSetBo setbo=(TemplateSetBo)f_cellhm.get(item.getName());
                    		if((setbo.getHismode()==2||setbo.getHismode()==3||setbo.getHismode()==4)&&item.isChangeBefore()&&(!setbo.isSubflag()))
                    		{
                    			value=setbo.getCellContent(basepre,a0100,rset,this.userview,this.tablebo);
                    		}
                    		else {
                                value = DataTable.getValueByFieldType(rset, item, codemp);
                            }
                        	if(item.getName().indexOf("t_")!=-1)
                        	{
                        		value=SafeCode.encode(value);
                        	}                      		
                    		/*
                        	if(f_cellhm.containsKey(fieldname))
                        	{
                        		TemplateSetBo setbo=(TemplateSetBo)f_cellhm.get(fieldname);
                        		if((setbo.getHismode()==2||setbo.getHismode()==3)&&item.isChangeBefore()&&(!setbo.isSubflag()))
                        		{
                        			value=setbo.getCellContent(basepre,a0100,rset,this.userview,this.tablebo);
                        		}
                        		else
                            		value = DataTable.getValueByFieldType(rset, item, map);                        			
                        	}
                        	else
                        		value = DataTable.getValueByFieldType(rset, item, map);
                        	if(fieldname.indexOf("t_")!=-1)
                        	{
                        		value=SafeCode.encode(value);
                        	}                    		
                    		*/
                    	}
                    	else {
                            value = DataTable.getValueByFieldType(rset, item, codemp);
                        }
                    }
                    //value = DataTable.getValueByFieldType(rset, item, codemp);					
                    transvo.setValue(item.getName(),value);
				}//for i loop end.
			}//while loop end.
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	
	/**
	 * 创建记录集
	 * @param list
	 * @return
	 */
	public String createDataSetRecord(ArrayList list,RowSet rset,HashMap map,Connection conn)
	{
        StringBuffer strcontent = new StringBuffer();
        try
        {
            StringBuffer strrec = new StringBuffer();
            Element root = new Element("xml");
            root.setAttribute("id", "__" + name);
            Element datachild = new Element("records");
            root.addContent(datachild);
            String basepre="";
            String a0100="";
            int i = 0;
            int j = 0;
            try
            {
                String fieldname = null;
            	while(rset.next())
            	{
                	Field item = null;
                    Element rec = new Element("record");
                    if(i == 0) {
                        rec.setAttribute("isCurrent", "true");
                    }
                    rec.setAttribute("state", "none");
                    datachild.addContent(rec);
                    Element recnew = new Element("new");
                    rec.addContent(recnew);
                    basepre=rset.getString("basepre");
                    a0100=rset.getString("a0100");
                    for(j = 0; j < list.size(); j++)
                    {
                        Object obj = list.get(j);
                        if(obj instanceof FieldItem)
                        {
                            FieldItem fielditem = (FieldItem)obj;
                            item = fielditem.cloneField();
                        } else
                        {
                            item = ((Field)list.get(j));
                        }
                        if(item.isChangeAfter()) {
                            fieldname=item.getName()+"_2";
                        } else if(item.isChangeBefore()){
                        	fieldname=item.getName()+"_1";
                        }
                        else {
                            fieldname=item.getName();
                        }
                        item.setName(fieldname);
                        String value="";
                        if(item.getDataType()==DataType.BLOB&& "photo".equalsIgnoreCase(item.getName()))
                        {
                        	String filephoto="";
                        	if(flag==0) {
                                filephoto=ServletUtilities.createOleFile("photo","ext",rset);
                            } else {
                                filephoto=ServletUtilities.createOleFile(basepre+"A00",a0100,conn);
                            }
                        	if(!(filephoto==null|| "".equals(filephoto))) {
                                value="/servlet/DisplayOleContent?filename="+filephoto;
                            }
                        }else   if("attachment".equalsIgnoreCase(item.getName()))
                        {
                        	
                        	
                        }
                        else {
                            value = DataTable.getValueByFieldType(rset, item, map);
                        }
                        if(item.getDatatype() == 13 || item.getDatatype() == 1) {
                            strrec.append(SafeCode.encode(value));
                        } else {
                            strrec.append(value);
                        }
                        strrec.append("");
                        strrec.append(",");
                    }//for 
                    recnew.setText(strrec.toString());
                    ++i;
                    strrec.setLength(0);
            	}//while 
                XMLOutputter outputter = new XMLOutputter();
                Format format = Format.getPrettyFormat();
                format.setEncoding("UTF-8");
                outputter.setFormat(format);
                strcontent.append(outputter.outputString(root));
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
        finally { }
        return strcontent.toString();

	}
	
	/**
	 * 取得当前模板子集信息
	 * @param list
	 * @param f_cellhm
	 * @return
	 */
	private HashMap getTempletSetInfo(ArrayList list,HashMap f_cellhm)
	{
		 HashMap setMap=new HashMap();
		 Field item = null;
		 String srccolumn=null;
		 String fieldname="";
         for(int j = 0; j < list.size(); j++)
         {
             Object obj = list.get(j);
             if(obj instanceof FieldItem)
             {
                 FieldItem fielditem = (FieldItem)obj;
                 item = fielditem.cloneField();
             } else
             {
                 item = ((Field)list.get(j));
             }
             srccolumn=item.getName();
             if(item.isChangeAfter()) {
                 fieldname=item.getName()+"_2";
             } else if(item.isChangeBefore()){
             	fieldname=item.getName()+"_1";
             	if(this.sum_domain_map!=null&&this.sum_domain_map.get(""+j)!=null&&this.sum_domain_map.get(""+j).toString().trim().length()>0){
            		fieldname=item.getName()+"_"+this.sum_domain_map.get(""+j).toString().trim()+"_1";
            	}
             }
             else {
                 fieldname=item.getName();
             }
             item.setName(fieldname);
             if(f_cellhm.containsKey(fieldname))
         	{
             	TemplateSetBo setbo=(TemplateSetBo)f_cellhm.get(fieldname);
             	if(setbo.isSubflag()&&setbo.getXml_param().length()>0)//比对当前模板子集数据和模板定义是否一致 2010-02-02 dengcan
         		{
         			TSubSetDomain setdomain=new TSubSetDomain(setbo.getXml_param());
         			setMap.put(fieldname, setdomain);
         		}
             	
         	}
         }
         return setMap;
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
				doc=PubFunc.generateDom(value);;
				String xpath="/records";
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
				List childlist=findPath.selectNodes(doc);	
				if(childlist!=null&&childlist.size()>0)
				{
					element=(Element)childlist.get(0);
					String columns="";
					if(element!=null&&element.getAttributeValue("columns")!=null) {
                        columns=element.getAttributeValue("columns");
                    }
					if(columns.length()>0&&columns.charAt(columns.length()-1)!='`') {
                        columns=columns+"`";
                    }
					if(!columns.equalsIgnoreCase(setdomain.getFields()))
					{
						columns=columns.substring(0,columns.length()-1);
						String[] temps=columns.split("`");
						
						String fields=setdomain.getFields();
						if(fields.length()>0) {
                            fields=fields.substring(0,fields.length()-1);
                        }
						String[] setTemps=fields.split("`");
						
						element.setAttribute("columns", setdomain.getFields().substring(0, setdomain.getFields().length()-1));
						//bug 41139 60锁子集没有按照设置的日期格式显示。
						String slops="";
						String coloumType="";
						String coloumCodeset="";
						String coloumLength="";
						String coloumDecimalLength="";
						String[] columnsStr=columns.split("`");
						ArrayList fieldfmtlist = setdomain.getFieldfmtlist();
						for(int num=0;num<columnsStr.length;num++){
							String valueStr=columnsStr[num];
							for(int j=0;j<fieldfmtlist.size();j++){
								TFieldFormat fomat=(TFieldFormat)fieldfmtlist.get(j);
								FieldItem fielditem = fomat.getFielditem();
								if("attach".equalsIgnoreCase(fomat.getName())){//bug 50095 V76人事异动（60锁）400表单打开时报空指针
									continue;
								}
								if(fielditem!=null) {
									if(fomat.getFielditem().getItemid().equalsIgnoreCase(columnsStr[num])){
										String width=fomat.getSlop()==null?"":fomat.getSlop();
										coloumType+=fomat.getFielditem().getItemtype().toUpperCase()+",";
										coloumCodeset+=fomat.getFielditem().getCodesetid()+",";
										coloumDecimalLength+=fomat.getFielditem().getDecimalwidth()+",";
										coloumLength+=fomat.getWidth()+",";
										if("D".equalsIgnoreCase(fomat.getFielditem().getItemtype())){
											slops+=width+",";
										}else{
											slops+=",";
										}
										break;
									}
								}
							}
						}
						element.setAttribute("slops", slops);
						element.setAttribute("coloumType", coloumType);
						element.setAttribute("coloumDecimalLength", coloumDecimalLength);
						element.setAttribute("coloumLength", coloumLength);
						element.setAttribute("coloumCodeset", coloumCodeset);
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
								if(j<_temps.length) {
                                    valueMap.put(temps[j].toLowerCase(), _temps[j]);
                                }
							}
							
							content.setLength(0);
							for(int j=0;j<setTemps.length;j++)
							{
								if(valueMap.get(setTemps[j].toLowerCase())!=null)
								{
									content.append((String)valueMap.get(setTemps[j].toLowerCase())+"`");
								}
								else {
                                    content.append("`");
                                }
							}
							if(content.length()>0) {
                                content.setLength(content.length()-1);
                            }
							element.setText(content.toString());
						}
						
					}
					else
					{
							columns=columns.substring(0,columns.length()-1);
							element.setAttribute("columns", columns);
							//bug 41139 60锁子集没有按照设置的日期格式显示。
							String slops="";
							String coloumType="";
							String coloumCodeset="";
							String coloumLength="";
							String coloumDecimalLength="";
							String[] columnsStr=columns.split("`");
							ArrayList fieldfmtlist = setdomain.getFieldfmtlist();
							for(int num=0;num<columnsStr.length;num++){
								String valueStr=columnsStr[num];
								for(int j=0;j<fieldfmtlist.size();j++){
									TFieldFormat fomat=(TFieldFormat)fieldfmtlist.get(j);
									FieldItem fielditem = fomat.getFielditem();
									if(fielditem!=null) {
										if(fomat.getFielditem().getItemid().equalsIgnoreCase(columnsStr[num])){
											String width=fomat.getSlop();
											String pre = fomat.getPre();
											coloumType+=fomat.getFielditem().getItemtype().toUpperCase()+",";
											coloumCodeset+=fomat.getFielditem().getCodesetid()+",";
											coloumDecimalLength+=fomat.getFielditem().getDecimalwidth()+",";
											coloumLength+=fomat.getWidth()+",";
											if("D".equalsIgnoreCase(fomat.getFielditem().getItemtype())){
												slops+=width+",";
											}else{
												slops+=",";
											}
											break;
										}
									}
								}
							}
							element.setAttribute("slops", slops);
							element.setAttribute("coloumType", coloumType);
							element.setAttribute("coloumDecimalLength", coloumDecimalLength);
							element.setAttribute("coloumLength", coloumLength);
							element.setAttribute("coloumCodeset", coloumCodeset);
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
	/**
	 * 通过setdomain中设置的default默认值，将其获取，以"`"分割，方便在添加和插入时显示默认值 liuzy 20160127
	 * @param setdomain
	 * @return
	 */
	private String getDefaultValue(TSubSetDomain setdomain){
		String defaultValue="";
		ArrayList fieldfmtlist=setdomain.getFieldfmtlist();
		for(int i=0;i<fieldfmtlist.size();i++)
		{
			TFieldFormat fieldformat=(TFieldFormat)fieldfmtlist.get(i);
			String value = fieldformat.getValue();
			defaultValue+=value+",";
		}
		defaultValue=defaultValue.substring(0, defaultValue.length()-1);
		return defaultValue;
	}
	
	/**
	 * 获得模板下每条记录对应指标的读写权限
	 * @param list
	 * @param rset
	 * @param map
	 * @param conn
	 * @param f_cellhm
	 * @return
	 */
	public String createDataSetRecordPriv(ArrayList list,RowSet rset,HashMap map,Connection conn) 
	{
		StringBuffer strrec = new StringBuffer();
        try
        {
        	if(!(this.tablebo.getOperationtype()==9||this.tablebo.getOperationtype()==8)) {
                return strrec.toString();
            }
            strrec.append("<script language='javascript'> var priv_obj=new Array();");
            String basepre="";
            String a0100="";
            String srccolumn=null;
            int i = 0;
            try
            {
            	StringBuffer extend_str=new StringBuffer(" var group_arr=new Array();");
                String fieldname = null;
                rset.beforeFirst();
                int group_no=0;
                String _to_id="";
                int contorlI=0;
                DatabaseMetaData dbMeta = conn.getMetaData();
                int version=dbMeta.getDatabaseMajorVersion();  //  sql2000=8    sql2005=9    sql2008=10    sql2012=11
            	while(rset.next())
            	{
            	    if(version==8){
            	        contorlI++;
                        if(contorlI>40){
                            break;
                        } 
            	    }
            		boolean isvalue=false;
            		Field item = null;
                    if(this.tablebo.getInfor_type()==1)
                    {
                    	basepre=rset.getString("basepre");
                    	a0100=rset.getString("a0100");
                    }
                    else if(this.tablebo.getInfor_type()==2)
                    {
                    	a0100=rset.getString("b0110");
                    }
                    else if(this.tablebo.getInfor_type()==3)
                    {
                    	a0100=rset.getString("e01a1");
                    }
                    String key_value=a0100;
                    if(rset.getString("to_id")==null) {
                        continue;
                    }
                    
                    if(_to_id.length()==0)
                    {
                    	_to_id=rset.getString("to_id");
                    	group_no=1;
                    }
                    if(!_to_id.equalsIgnoreCase(rset.getString("to_id")))
                    {
                    	group_no++;
                    	_to_id=rset.getString("to_id");
                    }
                    
                    extend_str.append("group_arr['"+key_value+"']='"+group_no+"`"+_to_id+"';"); 
                    if(this.tablebo.getInfor_type()==2)
	           		{
	           			 if(rset.getString("to_id").equalsIgnoreCase(rset.getString("b0110")))
	           			 {	
	           				 isvalue=true;
	           			 }
	           		
	           		 }
	           		 if(this.tablebo.getInfor_type()==3)
	           		 {
	           			 if(rset.getString("to_id").equalsIgnoreCase(rset.getString("e01a1")))
	           			 {
	           				 isvalue=true;
	           			 }
	           		 }
	           		 if(isvalue)
	           		 {
	           			strrec.append("priv_obj["+(i++)+"]='"+key_value+"';");
	           		 }
                  
            	}//while 
            	
            	strrec.append(extend_str.toString());
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
            strrec.append("</script>");
        }
        finally { }
        return strrec.toString();

	}
	
    /**
     * 获得节点定义的指标权限
     * @param task_id
     * @return
     */
    private HashMap getFieldPrivFromXml(String ext_param)
    {
        HashMap _map=new HashMap();
        Document doc=null;
        Element element=null;
        try
        {	
            if(ext_param!=null&&ext_param.trim().length()>0)
            {
                doc=PubFunc.generateDom(ext_param);; 
                String xpath="/params/field_priv/field";
                XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                List childlist=findPath.selectNodes(doc);   
                if(childlist.size()==0){
                    xpath="/params/field_priv/field";
                     findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                     childlist=findPath.selectNodes(doc);
                }
                if(childlist!=null&&childlist.size()>0)
                {
                    for(int i=0;i<childlist.size();i++)
                    {
                        element=(Element)childlist.get(i);
                        String editable="";
                        //0|1|2(无|读|写)
                        if(element!=null&&element.getAttributeValue("editable")!=null) {
                            editable=element.getAttributeValue("editable");
                        }
                        if(editable!=null&&editable.trim().length()>0)
                        {
                            String columnname=element.getAttributeValue("name").toLowerCase();
                            _map.put(columnname, editable);
                        }
                        
                    }
                }
            } 
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return _map;
    }
	
	/**
	 * 获得节点定义的指标权限
	 * @param task_id
	 * @return
	 */
	public HashMap getFieldPriv(String task_id,Connection conn)
	{
		HashMap _map=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			if (task_id.indexOf(",")>-1){//批量审批模式
			    String[] lists=StringUtils.split(task_id,",");
			    String taskids="";
                for(int i=0;i<lists.length;i++)
                {
                    if(lists[i]==null||lists[i].trim().length()==0) {
                        continue;
                    }
                    if (taskids.length()>0) {
                        taskids=taskids+",";
                    }
                    taskids=taskids+lists[i];
                }
                
                String sql="select * from t_wf_node where node_id in "
                    +"(select node_id from t_wf_task where task_id in ("+taskids+") )";
                            
                RowSet rowSet=dao.search(sql);
			    while (rowSet.next())
			    {
			        String ext_param= Sql_switcher.readMemo(rowSet,"ext_param"); 
			        if (_map.size()<1){
			            _map= getFieldPrivFromXml(ext_param); 
			        }
			        else {
			            HashMap tmpMap = getFieldPrivFromXml(ext_param);
			            Iterator iter = tmpMap.entrySet().iterator(); 
			            while (iter.hasNext()) { //与上一节点的指标权限比较，取权限大的。
			                Entry entry = (Entry) iter.next(); 
			                String columnname = (String)entry.getKey(); 
			                String editable = (String)entry.getValue(); 
			                if (editable==null ||"".equals(editable)){
			                    editable="0";
                            }
			                if (_map.containsKey(columnname)) {
			                    String oldEditable=(String)_map.get(columnname);
			                    if (oldEditable==null ||"".equals(oldEditable)){
			                        oldEditable="0";
			                    }
			                    if (editable!=null){
			                        if (Integer.parseInt(editable)>Integer.parseInt(oldEditable)){//比原来的权限大
			                            _map.put(columnname, editable);  
			                        }
			                    }
			                }
			                else {
			                    _map.put(columnname, editable);  
			                }
			            } 
			        }
			    }
			}
			else {
				if(task_id!=null&&!"0".equals(task_id.trim()))
				{
			        String sql="select * from t_wf_node where node_id=(select node_id from t_wf_task where task_id="+task_id+" )";
	                RowSet rowSet=dao.search(sql);
	                if(rowSet.next())
	                {
	                    String ext_param= Sql_switcher.readMemo(rowSet,"ext_param"); 
	                    _map = getFieldPrivFromXml(ext_param);
	                }  
				}
			    
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return _map;
	}
	


	
	
	/**
	 * 
	
	* @Title: createDataSetRecord
	
	* @Description: TODO
	
	* @param list 模版中涉及到的指标（包括子集）
	* @param rset 不同任务接口进入，查询的结果集
	* @param map   传入时为空的一个map
	* @param conn  数据库的连接
	* @param f_cellhm 存放 子集 人员库 机构库 等相关指标的setBO
	* @param submitFlagMap 存放选中的记录相关信息
	* @return String    返回 xml的数据
	
	* @throws
	 */
	public String createDataSetRecord(ArrayList list,RowSet rset,HashMap map,Connection conn,HashMap f_cellhm,HashMap submitFlagMap)
	{
        StringBuffer strcontent = new StringBuffer();
        try
        {
            StringBuffer strrec = new StringBuffer();
            Element root = new Element("xml");
            root.setAttribute("id", "__" + name);
            Element datachild = new Element("records");
            root.addContent(datachild);
            String basepre="";
            String basepre2="";//虚拟库前缀 兼容机构，岗位的电子签章 xieguiquan 2011-06-23
            String a0100="";
            String srccolumn=null;
            int i = 0;
            int j = 0;
            try
            {
                String fieldname = null;
                HashMap templetSetMap=getTempletSetInfo(list,f_cellhm);
                TSubSetDomain setdomain=null;
                HashMap recordMap = new HashMap();
                boolean isFinished_record=false;//判断当前显示的记录 是否为已结束任务的记录  用于浏览结束后记录的模板时，变化前指标不动态取库中的值
                if(this.tablebo!=null&&this.tablebo.getTasklist()!=null&&this.tablebo.getTasklist().size()==1&&this.tablebo.getInslist()!=null&&this.tablebo.getInslist().size()==1&&this.tablebo.getIns_id()!=0) 
                {
                	String _task_id=(String)this.tablebo.getTasklist().get(0);
                	ContentDAO dao=new ContentDAO(conn);
                	RowSet rowSet=dao.search("select task_state from t_wf_task where task_id="+_task_id);
                	if(rowSet.next())
                	{
                		if("5".equals(rowSet.getString(1))) {
                            isFinished_record=true;
                        }
                	}
                	 //记录对应任务id
                	StringBuffer strins = new StringBuffer();
                	for(int a=0;a<this.tablebo.getTasklist().size();a++)//按任务号查询需要审批的对象20080418
    				{
    					if(a!=0) {
                            strins.append(",");
                        }
    					strins.append((String)this.tablebo.getTasklist().get(a));
    				}
                	if(strins!=null&&!"0".equals(strins.toString().trim()))
	             	{
                	    rowSet=dao.search("select * from t_wf_task_objlink where task_id in ("+strins+")");
	                	while(rowSet.next())
	                	{
	                		recordMap.put(rowSet.getString("seqnum"), rowSet.getString("task_id"));
	                	}
	                	if(rowSet!=null) {
                            rowSet.close();
                        }
	             	}
                }
               
                
                
                HashMap columnMap=new HashMap();
                ResultSetMetaData md=rset.getMetaData();
                boolean isTaskId=false;  //是否有task_id列，
                int taskidflag =0;
                for(int e=0;e<md.getColumnCount();e++)
                {
                	 
                	columnMap.put(md.getColumnName(e+1).toLowerCase(), "1");
                	if("task_id".equals(md.getColumnName(e+1).toLowerCase())) {
                        taskidflag++;
                    }
                	if("seqnum".equals(md.getColumnName(e+1).toLowerCase())) {
                        taskidflag++;
                    }
                }
               if(taskidflag==2) {
                   isTaskId = true;
               }
               
                String flag="0";
                VersionControl ver_ctrl=new VersionControl();
                int contorlI=0;
                DatabaseMetaData dbMeta = conn.getMetaData();
                int version=dbMeta.getDatabaseMajorVersion();  //  sql2000=8    sql2005=9    sql2008=10    sql2012=11
            	while(rset.next())//遍历每条记录（每个人员）
            	{
            	    if(version==8){
            	        contorlI++;
                        if(contorlI>40){
                            break;
                        } 
            	    }
            	    
                	Field item = null;
                    Element rec = new Element("record");
                    if(i == 0) {
                        rec.setAttribute("isCurrent", "true");
                    }
                    rec.setAttribute("state", "none");
                    datachild.addContent(rec);
                    Element recnew = new Element("new");
                    rec.addContent(recnew);
                    
                    HashMap filedPrivMap=new HashMap(); //节点下指标权限 
                    if(isTaskId&&!isFromApply)
                    {
                    	String task_id= "";//rset.getString("task_id");
                    	if(recordMap!=null&&recordMap.get(rset.getString("seqnum"))!=null){
                    		task_id = ""+recordMap.get(rset.getString("seqnum"));
                    	}else{
                    		task_id = (String)this.tablebo.getTasklist().get(0);
                    	}
                    	if(templateFieldPriv_node.get(task_id)==null)
                    	{
                    		HashMap filedPriv=getFieldPriv(task_id,conn);
                    		if(filedPriv.size()!=0) {
                                templateFieldPriv_node.put(task_id,filedPriv);
                            }
                    	}
                    	if(templateFieldPriv_node.get(task_id)!=null) {
                            filedPrivMap=(HashMap)templateFieldPriv_node.get(task_id);
                        }
                    }
                    
                    
                    if(this.tablebo.getInfor_type()==1)
                    {
                    	basepre=rset.getString("basepre");
                    	a0100=rset.getString("a0100");
                    	basepre2=basepre;
                    }
                    else if(this.tablebo.getInfor_type()==2)
                    {
                    	a0100=rset.getString("b0110");
                    	basepre2=a0100;
                    }
                    else if(this.tablebo.getInfor_type()==3)
                    {
                    	a0100=rset.getString("e01a1");
                    	basepre2=a0100;
                    }
                    //去除重复的signature
                    String copysignature="";
                    for(j = 0; j < list.size(); j++)
                    {
                    	//首先找到item
                        Object obj = list.get(j);
                        if(obj instanceof FieldItem)
                        {
                            FieldItem fielditem = (FieldItem)obj;
                            item = fielditem.cloneField();
                        } else
                        {
                            item = ((Field)list.get(j));
                        }
                        //找到指标名字
                        srccolumn=item.getName();
                        if(srccolumn.endsWith("_1")){
                        	fieldname = srccolumn;
                        	srccolumn = srccolumn.replace("_1", "");
                        }else{
	                        if(item.isChangeAfter()) {
                                fieldname=item.getName()+"_2";
                            } else if(item.isChangeBefore()){
	                        	fieldname=item.getName()+"_1";
	                        	if(this.sum_domain_map!=null&&this.sum_domain_map.get(""+j)!=null&&this.sum_domain_map.get(""+j).toString().trim().length()>0){//忘记了 sum_domain_map的意思
	                        		fieldname=item.getName()+"_"+this.sum_domain_map.get(""+j).toString().trim()+"_1";
	                        	}
	                        }
	                        else {
                                fieldname=item.getName();
                            }
                        }
                        item.setName(fieldname);
                        //找指标的值
                        String value="";
                        String subDefaultValue="";  //子集中设置的默认值，以"`"分割进行拼接
                        if(item.getDataType()==DataType.BLOB&& "photo".equalsIgnoreCase(item.getName()))
                        {//如果指标是备注型
                        	String filephoto="";
                    		filephoto=ServletUtilities.createOleFile("photo","ext",rset);
                    		/*
                        	if(flag==0)
                        		filephoto=ServletUtilities.createOleFile("photo","ext",rset);
                        	else
                        		filephoto=ServletUtilities.createOleFile(basepre+"A00",a0100,conn);
                        		*/
                        	if(!(filephoto==null|| "".equals(filephoto)))//安全平台改造，将filename加密
                            {
                                value="/servlet/DisplayOleContent?filename="+SafeCode.encode(PubFunc.encrypt(filephoto));
                            } else {
                                value="blank";//没有照片时
                            }
                        }else   if("attachment".equalsIgnoreCase(item.getName()))//这个应该是郭峰当时做的个人附件
                        {
                        	
                        	
                        }
                        else //如果指标不是备注型也不是照片
                        {
                            if("signature".equalsIgnoreCase(item.getName())&&ver_ctrl.searchFunctionId("3206")){//如果是电子签章
                        		 value = Sql_switcher.readMemo(rset,"signature");
                        		 if("0".equals(flag)){
                        			 this.signxml.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
                                     this.signxml.append("<params>");
                        		 } 
                        		 flag="1";
                        		 if(value!=null&&value.length()>0){
                        			// this.signxml.append("<params>");
                        			 //value.substring(value.indexOf("<params>")+8, value.indexOf("</params"));
                        			 if(copysignature.indexOf(basepre2+"|"+a0100)==-1){
                        				 copysignature =basepre2+"|"+a0100;
                        			 this.signxml.append(value.substring(value.indexOf("<params>")+8, value.indexOf("</params")).trim());
                        			 }
                        		 }else{
                        			 if(copysignature.indexOf(basepre2+"|"+a0100)==-1){
                        				 copysignature =basepre2+"|"+a0100;
                        			 this.signxml.append("<record id =\""+basepre2+"|"+a0100+"\"  DocuemntID=\"\">");
                        			 this.signxml.append("</record>");
                        			 }
                        		 }
                        	 }
                            else{//如果不是电子签章
	                        	if(f_cellhm.containsKey(fieldname))//f_cellhm为模板页中所有的指标项(变量、子集区域)
	                        	{
	                        		TemplateSetBo setbo=(TemplateSetBo)f_cellhm.get(fieldname);
	                        		setbo.setFinished_record(isFinished_record);
	                        		if((setbo.getHismode()==2||setbo.getHismode()==3||setbo.getHismode()==4)&&item.isChangeBefore()&&(!setbo.isSubflag()))
	                        		{//如果是多条记录或者是条件记录或者是条件序号&&指标是变化前的&&不是子集
	                        			value=setbo.getCellContent(basepre,a0100,rset,this.userview,this.tablebo);
	                        			
	                        		}
	                        		else
	                        		{
	                            		value = DataTable.getValueByFieldType(rset, item, map);//从数据库中取出数据 郭峰
	                            		 //js按毫秒算出的日期 有时与java算出的不一致，如 1987.08.01 ,js算成 1987.07.31 所以统一加4000秒
	                            		if(item.getDatatype()==DataType.DATE&&value.trim().length()>0) {
                                            value=PubFunc.add(value, "4000000",0);
                                        }
	                            		
	                            		if(setbo.isSubflag())//比对当前模板子集数据和模板定义是否一致 2010-02-02 dengcan
	                            		{
	                            			 value=value.replace("&", "＆");
	                            			 setdomain=(TSubSetDomain)templetSetMap.get(fieldname);
	                            			 if(setdomain!=null){
	                            				 value=compareData(value,setdomain);
	                            				 subDefaultValue=getDefaultValue(setdomain);
	                            			 }
	                            		}
	                            		
	                            		if(item.getDatatype()==10&& "年限".equalsIgnoreCase(item.getFormat())&&value!=null&&value.length()>0)
	                            		{
	                            			String appdate=ConstantParamter.getAppdate(this.userview.getUserName());
	                            			Calendar d=Calendar.getInstance(); 
	                            			d.setTimeInMillis(Long.parseLong(value));
	                            			String[] temps=appdate.split("\\.");
	                            			int year=d.get(Calendar.YEAR);
	                            			int month=d.get(Calendar.MONTH)+1;
	                            			int day=d.get(Calendar.DATE);
	                            			
	                            			int result =Integer.parseInt(temps[0])-year;   
	                                         if   (month> Integer.parseInt(temps[1]))   {   
	                                             result = result-1;   
	                                         }   
	                                         else 
	                                         {
		                                         if   (month== Integer.parseInt(temps[1]))   {   
		                                             if   (day >Integer.parseInt(temps[2]))   {   
		                                                 result   =   result   -   1;   
		                                             }   
		                                         }   
	                                         }
	                            			 value=String.valueOf(result);
	                            		}
	                            		
	                            		
	//                            		if(item.getName().equalsIgnoreCase("c558r_2"))
	//                            		{
	//                            			java.util.Date tmp=rset.getDate(item.getName());
	//                            			Calendar c=Calendar.getInstance();
	//                            			c.setTime(tmp);
	//                            			System.out.println("--->senconds="+c.getTimeInMillis());
	//                            		}
	//                            		嘉华 templet_XXX 中，入职表a0101_1某些记录没数据，所以加此方法 2009－3－16
	                            		if((value==null||value.trim().length()==0)&&fieldname.toLowerCase().indexOf("a0101")!=-1)
	                            		{
	                            			if(columnMap.get("a0101_2")!=null&& "a0101_1".equalsIgnoreCase(fieldname))
	                            			{
	                            				Field item2=(Field)item.clone();
	                            				item2.setName("a0101_2");
	                            				value = DataTable.getValueByFieldType(rset, item2, map);
	                            			}
	                            			
	                            			if(columnMap.get("codeitemdesc_2")!=null&& "codeitemdesc_1".equalsIgnoreCase(fieldname))
	                            			{
	                            				Field item2=(Field)item.clone();
	                            				item2.setName("codeitemdesc_2");
	                            				value = DataTable.getValueByFieldType(rset, item2, map);
	                            			}
	                            			
	                            		}
	                            		
	                        		}
	                        	}
	                        	else
	                        	{
	                        		value = DataTable.getValueByFieldType(rset, item, map);
	 
	                        		if("submitflag".equalsIgnoreCase(fieldname)&&submitFlagMap!=null&&submitFlagMap.size()>0)
	                        		{
	                        			if(submitFlagMap.get(rset.getString("seqnum"))!=null){
	                        				value=((String)submitFlagMap.get(rset.getString("seqnum")));
	                        				if(value.indexOf(",")!=-1) {
                                                value =value.substring(0,value.indexOf(","));
                                            }
	                        			}
	                        		}
	                        		if("task_id".equalsIgnoreCase(fieldname)&&submitFlagMap!=null&&submitFlagMap.size()>0)
	                        		{
	                        			if(submitFlagMap.get(rset.getString("seqnum"))!=null){
	                        				value=((String)submitFlagMap.get(rset.getString("seqnum")));
	                        				if(value.indexOf(",")!=-1) {
                                                value =value.substring(value.indexOf(",")+1);
                                            }
	                        			}
	                        		}
	                        		//嘉华 templet_XXX 中，入职表a0101_1某些记录没数据，所以加此方法 2009－3－16
	                        		if((value==null||value.trim().length()==0)&&fieldname.toLowerCase().indexOf("a0101")!=-1)
	                        		{
	                        			if("a0101_1".equalsIgnoreCase(fieldname)&&columnMap.get("a0101_2")!=null)
	                        			{
	                        				Field item2=(Field)item.clone();
	                        				item2.setName("a0101_2");
	                        				value = DataTable.getValueByFieldType(rset, item2, map);
	                        			}
	                        			if("codeitemdesc_1".equalsIgnoreCase(fieldname))
	                        			{
	                        				Field item2=(Field)item.clone();
	                        				item2.setName("codeitemdesc_2");
	                        				value = DataTable.getValueByFieldType(rset, item2, map);
	                        			}
	                        			
	                        		}
	                        	}
                            } //如果不是电子签章 结束
                        	/**对插入子集区域编码处理,变化前和变化后*/
                        	if(fieldname.indexOf("t_")==0)
                        	{
                        		value=value.replace("&", "＆");
                        		value = appendAttributeToValue(value,fieldname,filedPrivMap,conn,subDefaultValue);//向value中增加子集的rwPriv权限   rwPriv=0无权限  rwPriv==1读权限    rwPriv==2写权限（权限顺序：先看流程权限，再看自己权限）
                        		value=SafeCode.encode(value);
                        	}
                        	else
                        	{
                        		value=value.replaceAll(",", "````");//豆号“,”为字符串的分隔符
                        	}
                        } //如果不是备注型也不是照片 结束
                        
    					/**分析权限标志,对变化前和变化后都加上权限。程序员来没有考虑一种情况，就是两个变化前的指标，如a0405_2_1,而filedPrivMap没有关于他的map对应关系  郭峰
    					 * =0无任何权限
    					 * =1读权限
    					 * =2写权限
    					 * */
                        if(item.isChangeBefore()||item.isChangeAfter()|| "a0101".equalsIgnoreCase(srccolumn))
                        {
	    					String state=this.userview.analyseFieldPriv(srccolumn);
	    					boolean isSpecialItem=false;//是否是特殊字段
	    					if("codeitemdesc".equalsIgnoreCase(srccolumn)|| "codesetid".equalsIgnoreCase(srccolumn)|| "corcode".equalsIgnoreCase(srccolumn)|| "parentid".equalsIgnoreCase(srccolumn)|| "start_date".equalsIgnoreCase(srccolumn))
	    					{	state="2";
	    						isSpecialItem=true;
	    					}
	    				//	if(state.equals("0"))
                    	//		state=this.userview.analyseFieldPriv(srccolumn.toUpperCase(),0);	//员工自助权限    					
                        	/**插入子集区域*/
                        	int idx=srccolumn.indexOf("_");//对于普通指标，srccolumn没有下划线
                        	if(idx!=-1&&!isSpecialItem)
                        	{
                        		String setname=srccolumn.substring(idx+1);
                        		state=this.userview.analyseTablePriv(setname.toUpperCase());//得到用户对当前子集的权限
                        //		if(state.equals("0"))
                        //			state=this.userview.analyseTablePriv(setname.toUpperCase(),0);//员工自助权限
                        	}
                        	
                        	String _fieldname=fieldname;
//                        	//郭峰修改 如a0405_2_1,向filedPrivMap中增加关于它的map对应关系
                        	if(idx!=-1&&!isSpecialItem){//如果是子集，有三个下划线   /**如果插入两个一样的子集,那么就会有三个下划线，否则就是两个下划线，xcs 2013-11-26**/
                        		if(fieldname.length()>2&& "t_".equalsIgnoreCase(fieldname.substring(0,2))){//判断是不是子集
                        			if(_fieldname.charAt(_fieldname.length()-1)=='1'){//如果是变化前
                            			String temp_str = _fieldname.substring(0,_fieldname.lastIndexOf("_"));//去掉最后一个下划线（截取到最后一个下划线前）
                                		if(temp_str.indexOf("_")!=temp_str.lastIndexOf("_")){//说明有三个下划线
                                			String temp_field_name = temp_str.substring(0,temp_str.lastIndexOf("_"));
                                			temp_field_name=temp_field_name.substring(2)+"_1";//到此处得到子集的代码
                                			if(filedPrivMap.size()>0&&filedPrivMap.get(temp_field_name.toLowerCase())!=null){
                                    			filedPrivMap.put(_fieldname.substring(2), filedPrivMap.get(temp_field_name.toLowerCase()));
                                    		}
                                		}
                            		}
                        		}
                        	}else{//如果是指标，只有两个下划线
                        		if((_fieldname.indexOf("_")!=_fieldname.lastIndexOf("_")) && _fieldname.charAt(_fieldname.length()-1)=='1'){//说明有两个下划线，说明有重复的变化前指标
                            		String temp_field_name = _fieldname.substring(0,_fieldname.indexOf("_"))+"_1";
                            		if(filedPrivMap.size()>0&&filedPrivMap.get(temp_field_name.toLowerCase())!=null){
                            			filedPrivMap.put(_fieldname, filedPrivMap.get(temp_field_name.toLowerCase()));
                            		}
                            	}
                        	}
                           	if("1".equals(this.tablebo.getUnrestrictedMenuPriv_Input())&&item.isChangeAfter()) {
                                state="2";
                            }
                        	
                        	if(fieldname.length()>2&& "t_".equalsIgnoreCase(fieldname.substring(0,2)))  //如果是子集
                            {
                                _fieldname=_fieldname.substring(2);//截取前两个字段（截取t_）
                            }
                        	if(filedPrivMap.size()>0&&filedPrivMap.get(_fieldname.toLowerCase())!=null)
                        	{
                        		String editable=(String)filedPrivMap.get(_fieldname.toLowerCase()); //	//0|1|2(无|读|写)
                        		if(editable!=null)//权限高于用户管理那设置的指标权限
                                {
                                    state=editable;
                                }
                        		
                        	}
                        	if("2".equalsIgnoreCase(this.sp_flag)&&"2".equalsIgnoreCase(state)){
                        		state="1";
                        	}
                        	String opinion_field = tablebo.getOpinion_field();
                        	
                        	
                        	//子集区域end.
                       // 	if(!(srccolumn.equalsIgnoreCase("codeitemdesc")||srccolumn.equalsIgnoreCase("codesetid")||srccolumn.equalsIgnoreCase("corcode")||srccolumn.equalsIgnoreCase("parentid")||srccolumn.equalsIgnoreCase("start_date")))
                        	{
                        		if (TemplateTableBo.isJobtitleVoteModule(this.userview)){
                        			state="1";
                        		}
                        		if("0".equals(state))
                        		{
                        			//if(this.tablebo.getUnrestrictedMenuPriv_Input().equals("1")&&item.isChangeBefore())//0判断(默认值),1不判断。数据录入针对的是变化后的指标
                        			//	value="";
                        			//else if(this.tablebo.getUnrestrictedMenuPriv_Input().equals("0"))
                        				value="";
                        		}
                        	}
                        }
                        //权限控制结束 chenmengqing addat 20070620
                        if(item.getDatatype() == 13 || item.getDatatype() == 1)//=13:CLOB  =1 STRING
                        {
                            strrec.append(/*SafeCode.encode(value)*/value);
                        } else {
                            strrec.append(value);
                        }

                        strrec.append("");
                        strrec.append(",");
                    } //for end
                    recnew.setText(strrec.toString());
                    ++i;
                    strrec.setLength(0);
            	} //while end
	            if(this.signxml.length()>0){
	            	this.signxml.append("</params>");
	            	String xml = this.signxml.toString();
	            	StringBuffer copyxml =new StringBuffer();
	            	while(xml.indexOf(">")!=-1){
	            		copyxml.append(xml.substring(0, xml.indexOf(">")+1));
	            		xml = xml.substring(xml.indexOf(">")+1,xml.length()).trim();
	            	}
	            	this.signxml.setLength(0);
	            	this.signxml.append(copyxml);
	            }
                XMLOutputter outputter = new XMLOutputter();
                Format format = Format.getPrettyFormat();
                format.setEncoding("UTF-8");
                outputter.setFormat(format);
//                System.out.println(SafeCode.decode(outputter.outputString(root)));
                strcontent.append(outputter.outputString(root));
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
        finally { }
        return strcontent.toString();

	}
	/**为子集的xml增加rwPriv属性（子集的读写权限），fieldsPriv属性（各个指标的读写权限，格式：1,2,0,1），返回新的xml
	 * value:xml值   fieldname 子集名字(格式：t_setname_chgstate或t_setname_domain_chgstate)   fieldPrivMap  节点权限(格式：a19_2=2)
	 * subDefaultValue:子集设置的默认值
	 * */
	public String appendAttributeToValue(String value,String fieldname,HashMap fieldPrivMap,Connection conn,String subDefaultValue){
		String newvalue = value;
		String newfieldname = fieldname.substring(2,fieldname.length());//截取t_
		String priv = (String)fieldPrivMap.get(newfieldname);//子集读写权限
		StringBuffer fieldspriv = new StringBuffer("");//子集下各个指标的读写权限(即使子集是读，也要有指标权限。此时指标权限全是0.因为我还要用指标权限控制子集中各个指标的只读模式)
		
		/*newfieldname = newfieldname.substring(0,newfieldname.length()-2);//截掉_2 这种方式不能起到 截掉_2作用*/
		int index = newfieldname.indexOf("_");
		newfieldname = newfieldname.substring(0,index);
		if(priv==null || "".equals(priv)){
			priv = this.userview.analyseTablePriv(newfieldname.toUpperCase());
			if("1".equals(this.tablebo.getUnrestrictedMenuPriv_Input())) /**数据录入不判断子集和指标权限, 0判断(默认值),1不判断  */ {
                priv="2"; ////0|1|2(无|读|写)
            }
		}
		//判断是否是职称评审投票系统，此系统不使用ehr的用户，默认有全部子集指标权限
		if (TemplateTableBo.isJobtitleVoteModule(this.userview)){
			priv="1";
		}
		//子集为写权限的时候，要继续考虑指标的权限
		String fields = getFieldsByXml(value);//得到子集下的指标，即columns下的指标。（格式：a1905`a1910`a1915`a1920）
		String[] temparray = fields.split("`");
		for(int i=0;i<temparray.length;i++){
			if("".equals(temparray[i])){
				continue;
			}
			String tempPriv = this.userview.analyseFieldPriv(temparray[i].toUpperCase());
			if("attach".equals(temparray[i].toLowerCase())){ //默认给子集中的附件赋予写权限 liuzy 20160119
				tempPriv="2";
			}
			if("1".equals(this.tablebo.getUnrestrictedMenuPriv_Input())) /**数据录入不判断子集和指标权限, 0判断(默认值),1不判断  */ {
                tempPriv="2"; ////0|1|2(无|读|写)
            }
			//判断是否是职称评审投票系统，此系统不使用ehr的用户，默认有全部子集指标权限
			if (TemplateTableBo.isJobtitleVoteModule(this.userview)){
				tempPriv="1";
			}
			fieldspriv.append(tempPriv+",");
		}
		if(fieldspriv.length()>0 && fieldspriv.charAt(fieldspriv.length()-1)==','){
			fieldspriv.setLength(fieldspriv.length()-1);//去掉最后一个字符
		}
		String fieldswidth ="";//每个指标的列宽 格式：23,12,9   顺序一定要与columns中指标的排列顺序严格一致
		String fieldsTitle="";
		if(subSet_width_title_map!=null&&subSet_width_title_map.get(fields.toUpperCase()+"_"+newfieldname.toUpperCase()+"_w")!=null) //20160810 dengcan
		{
			fieldswidth=(String)subSet_width_title_map.get(fields.toUpperCase()+"_"+newfieldname.toUpperCase()+"_w");
		}
		else
		{
			fieldswidth = getFieldsWidth(fields,newfieldname,conn);
			subSet_width_title_map.put(fields.toUpperCase()+"_"+newfieldname.toUpperCase()+"_w", fieldswidth); 
		}
		
		if(subSet_width_title_map!=null&&subSet_width_title_map.get(fields.toUpperCase()+"_"+newfieldname.toUpperCase()+"_t")!=null) //20160810 dengcan
		{
			fieldsTitle =(String)subSet_width_title_map.get(fields.toUpperCase()+"_"+newfieldname.toUpperCase()+"_t");
		}
		else
		{
			fieldsTitle=getFieldsTitle(fields,newfieldname,conn); //自定义列头
			subSet_width_title_map.put(fields.toUpperCase()+"_"+newfieldname.toUpperCase()+"_t", fieldsTitle); 
		}
		
		//开始为xml追加属性
		try{
			Document doc=PubFunc.generateDom(value);;
			Element element=null;
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			String xpath="/records";
			XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点		
			element =(Element) findPath.selectSingleNode(doc);
			if(element!=null){
				//追加rwPriv属性
				if(element.getAttribute("rwPriv")==null){
					element.setAttribute("rwPriv",priv);
				}else{
					element.getAttribute("rwPriv").setValue(priv);
				}
				//追加fieldsPriv属性
				if(fieldspriv.length()>0){
					if(element.getAttribute("fieldsPriv")==null){
						element.setAttribute("fieldsPriv",fieldspriv.toString());
					}else{
						element.setAttribute("fieldsPriv",fieldspriv.toString());
					}
				}
				//追加fieldsWidth属性
				if(fieldswidth.length()>0){
					if(element.getAttribute("fieldsWidth")==null){
						element.setAttribute("fieldsWidth",fieldswidth.toString());
					}else{
						element.setAttribute("fieldsWidth",fieldswidth.toString());
					}
				}
				//追加fieldstitle属性
				if(fieldsTitle.length()>0){
				    element.setAttribute("fieldsTitle",fieldsTitle.toString());
				}
				//追加fieldsdefault属性
				if(subDefaultValue.length()>0){
				}
				element.setAttribute("fieldsDefault",subDefaultValue.toString());
			}
			newvalue = outputter.outputString(doc);
		}catch(Exception e){
			e.printStackTrace();
		}
		return newvalue;
	}
	/**得到子集下的指标*/
	public String getFieldsByXml(String value){
		String fields = "";
		try{
			Document doc=PubFunc.generateDom(value);;
			Element element=null;
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			String xpath="/records";
			XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点		
			element =(Element) findPath.selectSingleNode(doc);
			if(element!=null){
				if(element.getAttribute("columns")!=null){
					fields = element.getAttributeValue("columns");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return fields;
	}
	/**得到指标的列宽*/
	public String getFieldsWidth(String columns,String setname,Connection conn){
		StringBuffer sb = new StringBuffer("");
		try{
			//首先得到子集在templet_set表中的xml
			String xml = "";
			StringBuffer sbxml = new StringBuffer("");
			sbxml.append("select sub_domain from template_set t where tabid="+this.tablebo.getTabid()+" and setname='"+setname.toUpperCase()+"' and subflag='1'");
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = dao.search(sbxml.toString());
			while(rs.next()){
				xml = Sql_switcher.readMemo(rs, "sub_domain");
			
			//首先得到每个指标的列宽，存储在map中
			HashMap nameWidthMap = new HashMap();
			Document doc=PubFunc.generateDom(xml);;
			Element element=null;
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			String xpath="/sub_para";
			XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点		
			element =(Element) findPath.selectSingleNode(doc);
			if(element!=null){
				List list = element.getChildren("field");
				for (int i = 0; i < list.size(); i++){
					Element temp = (Element) list.get(i);
					String tempname = temp.getAttributeValue("name").toUpperCase();
					String tempwidth = temp.getAttributeValue("width");
					nameWidthMap.put(tempname, tempwidth);
				}
			}
			String[] tempArray = columns.split("`");
			String width=null;
			if(sb.length()==0){
			for(int i=0;i<tempArray.length;i++){
				width = (String)nameWidthMap.get(tempArray[i].toUpperCase());
				if (width==null){
					sb.delete(0,sb.length());
					break;//wangrd 2015-06-09
				}else{
					int int_width = Integer.parseInt(width);//单位为VGA Base。要转换为pt。
					int_width = int_width*8;//1 VGA = 8pt
					sb.append(int_width+",");
				}
			}
			}else{
				break;
			}
			if (width==null) {
                continue;
            }
			
			if(sb.length()>0 && sb.charAt(sb.length()-1)==','){
				sb.setLength(sb.length()-1);//去掉最后一个字符
			}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	   /**得到指标的列头 wangrd 2015-02-12*/
    public String getFieldsTitle(String columns,String setname,Connection conn){
        StringBuffer sb = new StringBuffer("");
        try{
            //首先得到子集在templet_set表中的xml
            String xml = "";
            StringBuffer sbxml = new StringBuffer("");
            sbxml.append("select sub_domain from template_set t where tabid="+this.tablebo.getTabid()+" and setname='"+setname.toUpperCase()+"' and subflag='1'");
            ContentDAO dao = new ContentDAO(conn);
            RowSet rs = dao.search(sbxml.toString());
            while(rs.next()){
                xml = Sql_switcher.readMemo(rs, "sub_domain");
            
            //首先得到每个指标的列宽，存储在map中
            HashMap nameWidthMap = new HashMap();
            Document doc=PubFunc.generateDom(xml);;
            Element element=null;
            XMLOutputter outputter = new XMLOutputter();
            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            outputter.setFormat(format);
            String xpath="/sub_para";
            XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点      
            element =(Element) findPath.selectSingleNode(doc);
            if(element!=null){
                List list = element.getChildren("field");
                for (int i = 0; i < list.size(); i++){
                    Element temp = (Element) list.get(i);
                    String tempname = temp.getAttributeValue("name").toUpperCase();
                    String title = temp.getAttributeValue("title");
                    nameWidthMap.put(tempname, title);
                }
            }
            String[] tempArray = columns.split("`");
            String title=null;
            if(sb.length()==0){
            for(int i=0;i<tempArray.length;i++){
                title = (String)nameWidthMap.get(tempArray[i].toUpperCase());
                if(title==null){
                	sb.delete(0,sb.length());
                	break;
                }else{
                	sb.append(title+",");
                }
            }
            }else{
            	break;
            }
            if(title==null) {
                continue;
            }
            
            if(sb.length()>0 && sb.charAt(sb.length()-1)==','){
                sb.setLength(sb.length()-1);//去掉最后一个字符
            }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }
    
	/**
	 * 创建自动提交的表单（前台，借用AJAX数据集控件）
	 * @param list
	 * @param pagesize
	 * @param pageindex
	 * @param maxrows
	 * @return
	 */
	public  String createDataSetJavaScript(ArrayList list,int pagesize,int pageindex,int maxrows,HashMap f_cell,HashMap itemCodeMap)
	{
		StringBuffer strjs=new StringBuffer();
        String dataset = name;
        strjs.append("<script language=\"javascript\">");
        strjs.append(" var ");
        strjs.append(dataset);
        strjs.append("=createDataset(\"");
        strjs.append(dataset);
        strjs.append("\");");
        strjs.append(" var __t=");
        strjs.append(dataset);
        strjs.append(";");
        strjs.append("__t.type=\"reference\";");
        strjs.append("__t.pageSize=");
        strjs.append(pagesize);
        strjs.append(";");
        /**保存刷新加载数据的方式，=bytrans通过交易
         * 未定义，则表示通过框架本身直接加载数据
         * 后台刷新数据的功能号
         * */
        strjs.append("__t.flushByTrans=true;");
        strjs.append("__t.funcId=\"0570010120\";");
        
        strjs.append("__t.pageIndex=");
        strjs.append(pageindex);
        strjs.append(";");
        strjs.append("__t.pageCount=");
        if(maxrows % pagesize == 0) {
            strjs.append(maxrows / pagesize);
        } else {
            strjs.append(maxrows / pagesize + 1);
        }
        strjs.append(";");
        strjs.append("__t.sql=\"");
        /**安全平台改造,将传到前台的sql加密**/
        sql=PubFunc.encrypt(sql);
        strjs.append(sql);
        strjs.append("\";");
        /**设置为true时，数据集走到后面自动触发后台取数的交易*/
        strjs.append("__t.autoLoadPage=false"); 
        strjs.append(";");
        strjs.append("__t.loadDataAction=\"/ajax/ajaxService\";");
        Field item = null;
        String field_name=null;
        
//        HashMap fieldmap=new HashMap();
        Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.tablebo.getConn());
		String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
		if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122)) {
            display_e0122="0";
        }
        
		HashMap fieldPriv_node=new HashMap();
		if(templateFieldPriv_node!=null&&templateFieldPriv_node.size()>0)
		{ 
			 Iterator seq=templateFieldPriv_node.keySet().iterator();
			 while(seq.hasNext())
			 {
					String key=(String)seq.next();
					fieldPriv_node=(HashMap)templateFieldPriv_node.get(key); 
			} 
		}
		
        for(int i = 0; i < list.size(); i++)
        {
            Object obj = list.get(i);
            if(obj instanceof FieldItem)
            {
                FieldItem fielditem = (FieldItem)obj;
                item = fielditem.cloneField();
            } else
            {
                item = (Field)obj;
            }
            if(item.isChangeBefore()){
            	field_name=item.getName()+"_1";
            	if(this.sum_domain_map!=null&&this.sum_domain_map.get(""+i)!=null&&this.sum_domain_map.get(""+i).toString().trim().length()>0){
            		field_name=item.getName()+"_"+this.sum_domain_map.get(""+i).toString().trim()+"_1";
            	}
            }
            else if(item.isChangeAfter()) {
                field_name=item.getName()+"_2";
            } else {
                field_name=item.getName();
            }
//            if(fieldmap.get(field_name)!=null)
//            	continue;
//            fieldmap.put(field_name, field_name);
            strjs.append("var __f=__t.addField(\"");            
            /**主要为了显示从档案库中取的数据,因为有多条记录组合,对日期型和代码型,数值型进行处理*/
            if(f_cell.containsKey(field_name))
            {
            	TemplateSetBo setbo=(TemplateSetBo)f_cell.get(field_name);
            	if(((setbo.getHismode()==2||setbo.getHismode()==3||setbo.getHismode()==4))&&item.isChangeBefore())
            	{
        			if(item.getDatatype()>=2&&item.getDatatype()<=12) {
                        item.setDatatype("string");
                    }
        			item.setCodesetid("0");
            	}
            }
            
            if(item.isChangeBefore()&&item.getDatatype()==10&&!"yyyy.MM.dd".equalsIgnoreCase(item.getFormat()))
            {
            	int dis=getDisByFormat(item.getFormat());
            	if(dis!=0)
            	{
            		String aa=field_name.substring(0,field_name.length()-2);
            		field_name=aa+"_"+dis+"_1";
            	}
            	
            }
            
           	strjs.append(field_name);            
            strjs.append("\",\"");
            
            if(item.getDatatype()==10&&item.isChangeBefore()) //如果为日期
            {
            	if("年限".equalsIgnoreCase(item.getFormat())) {
                    strjs.append("string");
                } else {
                    strjs.append(DataType.typeToName(item.getDatatype()));
                }
            }
            else {
                strjs.append(DataType.typeToName(item.getDatatype()));
            }
            strjs.append("\");");
            strjs.append("__f.label=\"");
            strjs.append(item.getLabel());
            strjs.append("\";");
            strjs.append("__f.defaultValue=\"");
            if(item.getValue()!=null) {
                strjs.append(item.getValue());
            } else {
                strjs.append("");
            }
            strjs.append("\";");
            strjs.append("__f.format=\"");
            strjs.append(item.getFormat());
            strjs.append("\";");
            if(!"".equals(item.getAlign()))
            {
                strjs.append("__f.align=\"");
                strjs.append(item.getAlign());
                strjs.append("\";");
            }
            strjs.append("__f.visible=");
            if(item.isVisible()) {
                strjs.append("true");
            } else {
                strjs.append("false");
            }
            strjs.append(";");
            strjs.append("__f.codesetid=\"");
            if("start_date_2".equalsIgnoreCase(field_name)){//如果是生效日期,codesetid要设为0。 兼容以前的数据
            	strjs.append("0");
            }else{
            	strjs.append(item.getCodesetid());
            }
            strjs.append("\";");
            strjs.append("__f.dropDown=\"");
            if("0".equals(item.getCodesetid())|| "start_date_2".equalsIgnoreCase(field_name))//start_date_2的codesetid虽然也是0，但要兼容以前的数据。
            {
                strjs.append("");
            } else
            {
            	/**主要考虑这些代码项太长，前端显示太慢*/
            	/**代码项大于100*/
            	if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())||
            			"1A".equalsIgnoreCase(item.getCodesetid())|| "AB".equalsIgnoreCase(item.getCodesetid())|| "AG".equalsIgnoreCase(item.getCodesetid())|| "AH".equalsIgnoreCase(item.getCodesetid())||
            	  "AI".equalsIgnoreCase(item.getCodesetid())|| "AJ".equalsIgnoreCase(item.getCodesetid())|| "AK".equalsIgnoreCase(item.getCodesetid())
            	  ||(itemCodeMap!=null&&itemCodeMap.get(item.getCodesetid().toLowerCase())!=null)
            		) {
                    strjs.append("dropdownCode");
                } else {
                    strjs.append("dropDownList");
                }
            }
            strjs.append("\";");
            strjs.append("__f.editorType=\"");
            strjs.append("");
            strjs.append("\";");
            strjs.append("__f.toolTip=\"");
            strjs.append("");
            strjs.append("\";");
            /** 部门显示层级 */
            if(item!=null&& "UM".equalsIgnoreCase(item.getCodesetid())&&Integer.parseInt(display_e0122)>0) {
                strjs.append("__f.level=\""+display_e0122+"\";");
            }
            
            strjs.append("__f.tag=\"");
            strjs.append("");
            strjs.append("\";");
            if(item.isChangeBefore())
            {
            	strjs.append("__f.readOnly=\"");
            	strjs.append("true");
            	strjs.append("\";");
            }
//            else if(item.getVarible()==1){//解决临时变量可以编辑的问题
//                strjs.append("__f.readOnly=\"");
//                strjs.append("true");
//                strjs.append("\";");
//            }
            else if(this.tablebo.isBEmploy()){
            	
	            	if(item.isChangeAfter()&&!"2".equals(this.userview.analyseFieldPriv(item.getName()))&& "0".equals(this.tablebo.getUnrestrictedMenuPriv_Input()))
		            {
		            	strjs.append("__f.readOnly=\"");
		            	strjs.append("true");
		            	strjs.append("\";");            	
		            }else if(item.isChangeAfter()&&this.tablebo.getOpinion_field()!=null&&this.tablebo.getOpinion_field().length()>0&&this.tablebo.getOpinion_field().equalsIgnoreCase(item.getName())){
		            	strjs.append("__f.readOnly=\"");
		            	strjs.append("true");
		            	strjs.append("\";");  
		            }
            	 
            }
            else
            {
            	
            //	if(!(item.getName().equalsIgnoreCase("codeitemdesc")||item.getName().equalsIgnoreCase("codesetid")||item.getName().equalsIgnoreCase("corcode")||item.getName().equalsIgnoreCase("parentid")||item.getName().equalsIgnoreCase("start_date")))
            	{ 
            		if("codeitemdesc".equalsIgnoreCase(item.getName())|| "codesetid".equalsIgnoreCase(item.getName())|| "corcode".equalsIgnoreCase(item.getName())|| "parentid".equalsIgnoreCase(item.getName())|| "start_date".equalsIgnoreCase(item.getName()))
            		{
            			if(fieldPriv_node!=null&&fieldPriv_node.size()>0&&fieldPriv_node.get(field_name.toLowerCase())!=null&& "0".equals(this.tablebo.getUnrestrictedMenuPriv_Input()))
            			{
            				String editable=(String)fieldPriv_node.get(field_name.toLowerCase()); //0|1|2(无|读|写)
            				if("2".equalsIgnoreCase(this.sp_flag)&& "2".equals(editable)){//bug 42345 7x包60锁任务监控进入表单能够编辑变化后指标
            					editable="1";
            				}
                    		if(!"2".equals(editable))
                    		{
                    			strjs.append("__f.readOnly=\"");
    			            	strjs.append("true");
    			            	strjs.append("\";"); 
                    		}
            			}
            		}
            		else if(fieldPriv_node!=null&&fieldPriv_node.size()>0&&fieldPriv_node.get(field_name.toLowerCase())!=null/*&&this.tablebo.getUnrestrictedMenuPriv_Input().equals("0")*/)
                	{
            			
            			
                		String editable=(String)fieldPriv_node.get(field_name.toLowerCase()); //0|1|2(无|读|写)
                		if("2".equalsIgnoreCase(this.sp_flag)&& "2".equals(editable)){//bug 42345 7x包60锁任务监控进入表单能够编辑变化后指标
        					editable="1";
        				}
                		if(!"2".equals(editable))
                		{
                			strjs.append("__f.readOnly=\"");
			            	strjs.append("true");
			            	strjs.append("\";"); 
                		}else{
                			if(item.isChangeAfter()&&this.tablebo.getOpinion_field()!=null&&this.tablebo.getOpinion_field().length()>0&&this.tablebo.getOpinion_field().equalsIgnoreCase(item.getName())){
    			            	strjs.append("__f.readOnly=\"");
    			            	strjs.append("true");
    			            	strjs.append("\";");  
    			            }
                		}
                	}
                	else
                	{
			            if(item.isChangeAfter()&&(!(/*this.userview.analyseFieldPriv(item.getName(),0).equals("2")||员工角色特殊测试人员要求去掉*/"2".equals(this.userview.analyseFieldPriv(item.getName())))&& "0".equals(this.tablebo.getUnrestrictedMenuPriv_Input())))
			            {
			            	strjs.append("__f.readOnly=\"");
			            	strjs.append("true");
			            	strjs.append("\";");            	
			            }else if(item.isChangeAfter()&&this.tablebo.getOpinion_field()!=null&&this.tablebo.getOpinion_field().length()>0&&this.tablebo.getOpinion_field().equalsIgnoreCase(item.getName())){
			            	strjs.append("__f.readOnly=\"");
			            	strjs.append("true");
			            	strjs.append("\";");  
			            }else if("2".equalsIgnoreCase(this.sp_flag)){//bug 42345 7x包60锁任务监控进入表单能够编辑变化后指标
			            	strjs.append("__f.readOnly=\"");
			            	strjs.append("true");
			            	strjs.append("\";");  
			            }
                	}
            	}
            }
        }
        strjs.append("initDataset(__t);");
        strjs.append("</script>");
        return strjs.toString();
	}
	
	public int getDisByFormat(String disFormat)
	{
		int dis=0;
		if("yy.MM.dd".equalsIgnoreCase(disFormat)) {
            dis=7;
        } else if("yyyy.MM".equalsIgnoreCase(disFormat)) {
            dis=8;
        } else if("yy.MM".equalsIgnoreCase(disFormat)) {
            dis=10;
        } else if("yyyy年MM月dd日".equalsIgnoreCase(disFormat)) {
            dis=14;
        } else if("yyyy年MM月".equalsIgnoreCase(disFormat)) {
            dis=15;
        } else if("yy年MM月dd日".equalsIgnoreCase(disFormat)) {
            dis=16;
        } else if("yy年MM月".equalsIgnoreCase(disFormat)) {
            dis=17;
        } else if("年限".equalsIgnoreCase(disFormat)) {
            dis=18;
        } else if("yyyy".equalsIgnoreCase(disFormat)) {
            dis=19;
        } else if("MM".equalsIgnoreCase(disFormat)) {
            dis=20;
        } else if("dd".equalsIgnoreCase(disFormat)) {
            dis=21;
        }
		return dis;
	}
	
	
	
	public void setTablebo(TemplateTableBo tablebo) {
		this.tablebo = tablebo;
	}
	public void setUserview(UserView userview) {
		this.userview = userview;
	}
	public StringBuffer getSignxml() {
		return signxml;
	}
	public void setSignxml(StringBuffer signxml) {
		this.signxml = signxml;
	}
	public HashMap getSum_domain_map() {
		return sum_domain_map;
	}
	public void setSum_domain_map(HashMap sum_domain_map) {
		this.sum_domain_map = sum_domain_map;
	}
	public boolean isFromApply() {
		return isFromApply;
	}
	public void setFromApply(boolean isFromApply) {
		this.isFromApply = isFromApply;
	}
	
}
