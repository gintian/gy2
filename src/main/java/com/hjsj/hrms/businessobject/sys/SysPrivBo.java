package com.hjsj.hrms.businessobject.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Title:用于管理角色或用户权限</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:May 10, 2005:7:32:21 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SysPrivBo {
    /**
     * 对象编号
     */
    private String object_id;
    /**
     * 对象标识
     * 0:对应用户权限(t_sys_staff)
     * 1:对应角色权限
     * 2:对应项目组权限
     * 3:对应职位权限
     * 4:对员工权限（usra01\reta01）
     */
    private String status;
    /**
     * 功能串
     */
    private String func_str;
    /**
     * 　人员库串
     */
    private String db_str;
    /**
     * 子集串
     */
    private String table_str;
    /**
     * 指标串
     */
    private String field_str;
    /**
     * 管理范围串
     */
    private String manage_str;
    /**多媒体*/
    private String media_str;
    /**报表授权*/
    private String report_str;
    /**登记表*/
    private String card_str;
    /**模板授权*/
    private String template_str;
    /**花名册*/
    private String namelist_str;
    /**规章制度*/
    private String rule_str;
    /**薪酬体系*/
    private String salaryset_str;
    /**报警*/
    private String warn_str;
    /**人员分类授权*/
    private String subpriv_str;
    private String privcode="";
    /**
     * 数据库连接
     */
    private Connection conn;
    /**
     * 对象vo
     */
    private RecordVo vo;
    
    
    /**
     * 构造函数，进行了查询
     * @param object_id
     * @param status
     * @param conn
     */
    public SysPrivBo(String object_id,String status,Connection conn,String priv) {
        this.object_id=object_id;
        this.status=status;
        this.conn=conn;
        searchSysPriv(priv);
    }
    public SysPrivBo(String object_id,String status,Connection conn,String priv,String privcode) {
        this.object_id=object_id;
        this.status=status;
        this.conn=conn;
        this.privcode=privcode;        
        searchSysPriv(priv);
    }

	/**
     * 构造函数，为了对其进行操作，增、删、改及查
     * @param vo
     * @param conn
     */
    public SysPrivBo(RecordVo vo,Connection conn) {
        this.object_id=vo.getString("id");
        this.conn=conn;
        this.vo=vo;
    }
    /**
     * 重新输入用户对象及类型
     * @param object_id
     * @param status
     */
    public void reSetObject(String object_id,String status,String priv)    
    {
        this.object_id=object_id;
        this.status=status; 
        searchSysPriv(priv);        
    }
   /**
    * 把组下用户权限更新
    */
    private void updateprivByGroupId()
    {
    	String id=this.vo.getString("id");
    	StringBuffer buf=new StringBuffer("select * from usergroup where groupname=");
    	buf.append("'");
    	buf.append(id);
    	buf.append("'");
    	ContentDAO dao=new ContentDAO(conn);
    	try
    	{
    		RowSet rset=dao.search(buf.toString());
    		if(rset.next())
    		{
    			
    		}
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	
    }
    

    
    /**
     *保存内容
     */
    public void save()
    {
        ContentDAO dao=new ContentDAO(conn);
        boolean bnew=false;
        try
        {
            /**
             * 未定义权限则增加记录
             */
            /*try
            {  */
            	//dao.findByPrimaryKey(vo);
            	bnew=this.isExistRecordVo(vo,dao);
                //dao.findRowSetDynaClassByPrimaryKey(vo);
             /*   bnew=true;
                
            }
            catch(GeneralException dd)
            {
                ;//未找到记录
            }*/
            if(bnew)
            {
            	/**分析是否是用户组，如果是用户组的话，则需要把组下的用户权限也进行相应的更新*/
            	if("0".equals(this.vo.getString("status")))
            	{
            		
            	}
           		dao.updateValueObject(vo);
            }
            else {
                dao.addValueObject(vo);
            }
        }
        catch(SQLException sqle)
        {
            sqle.printStackTrace();
        }
        catch(GeneralException ge)
        {
            ge.printStackTrace();
        }
    }
    
    private void searchSysPriv(String priv)
    {
        StringBuffer strsql=new StringBuffer();
        strsql.append("select ");
        strsql.append(priv);
        strsql.append(" from t_sys_function_priv where id='");
        strsql.append(object_id);
        strsql.append("' and status=");
        strsql.append(status);
        ResultSet rset=null;

        try
        {	ContentDAO dao = new ContentDAO(conn);
        	rset=dao.search(strsql.toString());
            if(rset.next())
            {
            	if("functionpriv".equalsIgnoreCase(priv)) {
                    this.func_str=Sql_switcher.readMemo(rset,"functionpriv");// rset.getString("functionpriv");
                }
            	//rset.getc
            	if("dbpriv".equalsIgnoreCase(priv)) {
                    this.db_str=Sql_switcher.readMemo(rset,"dbpriv");//rset.getString("dbpriv");
                }
            	if("tablepriv".equalsIgnoreCase(priv)) {
                    this.table_str=Sql_switcher.readMemo(rset,"tablepriv");//rset.getString("tablepriv");
                }
            	if("fieldpriv".equalsIgnoreCase(priv)) {
                    this.field_str=Sql_switcher.readMemo(rset,"fieldpriv");//rset.getString("fieldpriv");
                }
            	if("managepriv".equalsIgnoreCase(priv)) {
                    this.manage_str=Sql_switcher.readMemo(rset,"managepriv");//rset.getString("managepriv");
                }
            	if("cardpriv".equalsIgnoreCase(priv)) {
                    this.card_str=Sql_switcher.readMemo(rset,"cardpriv");//rset.getString("cardpriv");
                }
            	if("namelistpriv".equalsIgnoreCase(priv)) {
                    this.namelist_str=Sql_switcher.readMemo(rset,"namelistpriv");//rset.getString("namelistpriv");
                }
            	if("reportsortpriv".equalsIgnoreCase(priv)) {
                    this.report_str=Sql_switcher.readMemo(rset,"reportsortpriv");//rset.getString("reportsortpriv");
                }
            	if("warnpriv".equalsIgnoreCase(priv)) {
                    this.warn_str=Sql_switcher.readMemo(rset,"warnpriv");//rset.getString("warnpriv");
                }
            	if("salarysetpriv".equalsIgnoreCase(priv)) {
                    this.salaryset_str=Sql_switcher.readMemo(rset,"salarysetpriv");//rset.getString("salarysetpriv");
                }
            	if("templatepriv".equalsIgnoreCase(priv)) {
                    this.template_str=Sql_switcher.readMemo(rset,"templatepriv");//rset.getString("templatepriv");
                }
            	if("mediapriv".equalsIgnoreCase(priv)) {
                    this.media_str=Sql_switcher.readMemo(rset,"mediapriv");//rset.getString("mediapriv");
                }
            	if("rulepriv".equalsIgnoreCase(priv)) {
                    this.rule_str=Sql_switcher.readMemo(rset,"rulepriv");//rset.getString("rulepriv");
                }
            	if("subpriv".equalsIgnoreCase(priv))
            	{
            		this.subpriv_str=Sql_switcher.readMemo(rset,"subpriv");
            		if(subpriv_str!=null&&subpriv_str.length()>0&&this.privcode!=null&&this.privcode.length()>0)
            		{
            			getSubprivPrivStr(this.subpriv_str,this.privcode);
            		}
            	}   
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if(rset!=null) {
                    rset.close();
                }
                
            }
            catch(SQLException sqle)
            {
                sqle.printStackTrace();
            }
        }    	
    }
    /**
     * 查询权限表
     */
    private void searchSysPriv()
    {
        StringBuffer strsql=new StringBuffer();
        strsql.append("select functionpriv,condpriv,dbpriv,tablepriv,fieldpriv,managepriv,");
        strsql.append("cardpriv,namelistpriv,reportsortpriv,warnpriv,salarysetpriv,");
        strsql.append("templatepriv,mediapriv,rulepriv from t_sys_function_priv where id='");
        strsql.append(object_id);
        strsql.append("' and status=");
        strsql.append(status);
        ResultSet rset=null;
        try
        {
            ContentDAO dao=new ContentDAO(conn);
            rset=dao.search(strsql.toString());
            if(rset.next())
            {
                this.func_str=rset.getString("functionpriv");
            	this.db_str=rset.getString("dbpriv");
                this.table_str=rset.getString("tablepriv");
            	this.field_str=rset.getString("fieldpriv");
                this.manage_str=rset.getString("managepriv");
                this.card_str=rset.getString("cardpriv");
                this.namelist_str=rset.getString("namelistpriv");
                this.report_str=rset.getString("reportsortpriv");
                this.warn_str=rset.getString("warnpriv");
                this.salaryset_str=rset.getString("salarysetpriv");
                this.template_str=rset.getString("templatepriv");
                this.media_str=rset.getString("mediapriv");                      
                this.rule_str=rset.getString("rulepriv");   
            }
        }
        catch(SQLException sqle)
        {
            sqle.printStackTrace();
        }
        finally
        {
            try
            {
                if(rset!=null) {
                    rset.close();
                }
            }
            catch(SQLException sqle)
            {
                sqle.printStackTrace();
            }
        }
    }
   /**
    * 分析范围权限范围是否相等
    * @param managepriv
    * @return
    */
   public boolean isEqualManagePriv(String managepriv)
   {
       if(managepriv==null|| "".equals(managepriv)) {
           return false;
       }
       if(manage_str==null) {
           return false;
       }
       return manage_str.equals(managepriv);
   }
    /**
     * 分析对象是否有此功能
     * @param func_id  for example func_id=1010
     * @return
     */
    public boolean isHaveTheFunction(String func_id)
    {
        if(func_str==null|| "".equals(func_str)) {
            return false;
        }
        
        if(func_str.indexOf(","+func_id+",")==-1) {
            return false;
        }
        return true;
    }
    /**
     * 报表分类
     * @param sort_id
     * @return
     */
    public boolean isHaveTheReportSort(String sort_id)
    {
        if(report_str==null|| "".equals(report_str)) {
            return false;
        }
        if(report_str.indexOf(","+sort_id+",")==-1) {
            return false;
        }
        return true;        
    }   
    /**
     * 多媒体子集文件分类
     * @param media_id
     * @return
     */
    public boolean isHaveTheMedia(String media_id)
    {
        if(media_str==null|| "".equals(media_str)) {
            return false;
        }
        if(media_str.indexOf(","+media_id+",")==-1) {
            return false;
        }
        return true;        
    }    
    /**
     * 分析对象是否有此人员库
     * @param db_id for examples db_id=,ddd,
     * @return
     */
    public boolean isHaveTheDb(String db_id)
    {
        if(db_str==null|| "".equals(db_str)) {
            return false;
        }
        if(db_str.indexOf(db_id)==-1) {
            return false;
        }
        return true;        
    }
    /**
     * 分析表的权限
     * ,A01X{,AxxX},（X=1,2,3,4,5,6）
     * 1,2:表示对历史记录和当前记录有读权和写权
     * 3,4:表示只对历史记录有读权和写权
     * 5,6:表示只对当前记录有读权和写权
     * @param table_id for examplse table_id=A01
     * @return
     */
    public String analyseTablePriv(String table_id)
    {
    	searchSysPriv("tablepriv");
        if(table_str==null|| "".equals(table_str)) {
            return "0";
        }
        /**
         * cmq changed at 20100507 
         * 增加","
         */
        table_id=","+table_id;  
        /**end.*/
        int len=table_id.length();        
        int idx=table_str.indexOf(table_id);
        
        if(idx==-1) {
            return "0";
        }
        return table_str.substring(idx+len,idx+len+1);
    }
    public String analyseSubprivTablePriv(String table_id)
    {
    	if(table_str==null|| "".equals(table_str)) {
            return "0";
        }
        /**
         * cmq changed at 20100507 
         * 增加","
         */
        table_id=","+table_id;  
        /**end.*/
        int len=table_id.length();        
        int idx=table_str.indexOf(table_id);
        
        if(idx==-1) {
            return "0";
        }
        return table_str.substring(idx+len,idx+len+1);
    }
    /**
     * 分析指标的权限
     * ,xxxxxX{,xxxxX},（X=0,1,2,3,4,5,6）
     * @param field_id　for examples field_id=A0101;
     * @return
     */
    public String analyseFieldPriv(String field_id)
    {
        if(field_str==null|| "".equals(field_str)) {
            return "0";
        }
        int len=field_id.length()+1;
        //校验指标权限必须带, 符号 有可能导致 指标 与 其他指标+flag权限标识后，指标名称重复导致权限显示不对  wangb bug 55676
        int idx=field_str.indexOf(","+field_id);
        if(idx==-1) {
            return "0";
        }
        return field_str.substring(idx+len,idx+len+1);        
    }
    
    /**
     * 
     * @param role_id usr00000001
     * @param flag	GeneralConstant.EMPLOYEE
     * @param res_str	parser.outResourceContent()
     */
    public void saveResourceString(String role_id,String flag,String res_str)
	 {
	        if(res_str==null) {
                res_str="";
            }
	        RecordVo vo=new RecordVo("t_sys_function_priv");
	        vo.setString("id",role_id);
	        vo.setString("status",flag/*GeneralConstant.ROLE*/);
	        vo.setString("warnpriv",res_str);
	        this.vo = vo;
	        save();        
	 }
    
    public void saveResourceStringSql(String role_id,String flag,String res_str)
    {
        if(res_str==null) {
            res_str="";
        }
        StringBuffer strsql=new StringBuffer();
	      strsql.append("select id from t_sys_function_priv where id='");
	      strsql.append(role_id);
	      strsql.append("' and status=");
	      strsql.append(flag);
	      RowSet rs=null;
	      try
	      {
	    	ArrayList paralist=new ArrayList();
	    	ContentDAO dao=new ContentDAO(this.conn);
	    	rs=dao.search(strsql.toString());
	    	
	    	if(rs.next())
	    	{
		    	paralist.add(res_str);	    		
	    		strsql.setLength(0);
	    		strsql.append("update t_sys_function_priv set warnpriv=?");
	    		//strsql.append(field_str);
	    		strsql.append(" where id='");
	    		strsql.append(role_id);
	    		strsql.append("' and status=");
	    		strsql.append(flag);
	    	}
	    	else
	    	{
		    	paralist.add(role_id);	    		
		    	paralist.add(res_str);	    		
	    		strsql.setLength(0);
	    		strsql.append("insert into t_sys_function_priv (id,warnpriv,status) values(?,?,");
	    		strsql.append(flag);
	    		strsql.append(")");
	    	}	    	
	    	dao.update(strsql.toString(),paralist);
	      }
	      catch(SQLException sqle)
	      {
	    	  sqle.printStackTrace();
	      }finally
	      {
	    	  if(rs!=null) {
                  try {
                      rs.close();
                  } catch (SQLException e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
                  }
              }
	      }
	      
    }
    /**
     * @return Returns the func_str.
     */
    public String getFunc_str() {
    	if(func_str==null) {
            return "";
        }
        return func_str;
    }
    /**
     * @return Returns the db_str.
     */
    public String getDb_str() {
    	if(db_str==null) {
            return "";
        }
        return db_str;
    }
    /**
     * @return Returns the field_str.
     */
    public String getField_str() {
        return field_str;
    }
    /**
     * @return Returns the manage_str.
     */
    public String getManage_str() {
    	if(manage_str==null) {
            return "";
        }
        return manage_str;
    }
    /**
     * @return Returns the table_str.
     */
    public String getTable_str() {
        return table_str;
    }
    
    public String getCard_str() {
		return card_str;
	}
	public String getMedia_str() {
		if(media_str==null) {
            return "";
        }
		return media_str;
	}
	public String getReport_str() {
		return report_str;
	}
	public String getTemplate_str() {
		return template_str;
	}

	public String getNamelist_str() {
		return namelist_str;
	}

	public String getRule_str() {
		return rule_str;
	}

	public String getSalaryset_str() {
		return salaryset_str;
	}

	public String getWarn_str() {
		return warn_str;
	}    
	public ArrayList getFenleiCodeitemList(ContentDAO dao)
	{
		String sql="select str_value from constant where upper(constant)='SYS_INFO_PRIV' and type='1'";
		RowSet rs=null;
		String str_value="";
		try {
			rs=dao.search(sql);
			if(rs.next()) {
                str_value=rs.getString("str_value");
            }
		
			String codesetid="";
			if(str_value!=null&&str_value.length()>0)
			{
				String values[]=str_value.split(",");
				if(values.length==2)
				{
					codesetid=values[1];
				}
			}
			ArrayList list=new ArrayList();
			if(codesetid==null||codesetid.length()<=0) {
                return list;
            }
			/*list=AdminCode.getCodeItemList(codesetid.toLowerCase());*/
			sql = "select codeitemid,codeitemdesc,parentid,childid from codeitem where codesetid='"+codesetid+"' order by CodeItemId";
			if("UN".equals(codesetid) || "UM".equals(codesetid)){
				sql = "select codeitemid,codeitemdesc,parentid,codeitemid childid from organization where codesetid='"+codesetid+"' order by CodeItemId";
			}
			rs = dao.search(sql);
			while(rs.next()){
				CodeItem item = new CodeItem();
				item.setCodeid(codesetid);
				item.setCodeitem(rs.getString("CodeItemId"));
				String text = rs.getString("CodeItemDesc");
				if (text == null) {
                    text = "";
                }
				text = text.replaceAll("\r", "");
				text = text.replaceAll("\n", "");
				item.setCodename(text);
				item.setPcodeitem(rs.getString("parentid"));
				item.setCcodeitem(rs.getString("childid"));
				list.add(item);
			}
			return list;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}

	}
	public String getSubprivPrivStr(String subpriv_str,String privcode)
	{
		Document doc=null;;
		try
		{
			  
			  doc= PubFunc.generateDom(subpriv_str);
			  XPath xPath = XPath.newInstance("/params/codeitem[@id='"+privcode+"']");
			  List list=xPath.selectNodes(doc);			 
			  if(list!=null&&list.size()>0)
			  {
				  Element elementD=(Element)list.get(0);
				  Element elementB=elementD.getChild("table");
				  if(elementB!=null) {
                      this.table_str=elementB.getText();
                  }
				  Element elementF=elementD.getChild("field");
				  if(elementF!=null) {
                      this.field_str=elementF.getText();
                  }
			  }
		}catch(Exception e)
		{
		  e.printStackTrace();	
		}
		return "1";
	}
	public boolean setSubprivPrivStr(String privcode,String cell,String textvalue)
	{
		if(this.subpriv_str==null||this.subpriv_str.length()<=0)
		{
			StringBuffer temp_xml=new StringBuffer();
			temp_xml.append("<?xml version='1.0' encoding='GB2312' ?>");
			temp_xml.append("<params>");
			temp_xml.append("</params>");			
			this.subpriv_str=temp_xml.toString();
		}
		boolean iscorrect=false;
		Document doc=null;;
		try
		{
			  doc=PubFunc.generateDom(this.subpriv_str);
			  XPath xPath = XPath.newInstance("/params/codeitem[@id='"+privcode+"']");
			  List list=xPath.selectNodes(doc);			 
			  if(list!=null&&list.size()>0)
			  {
				  Element elementD=(Element)list.get(0);
				  if("table".equalsIgnoreCase(cell))
				  {
					  Element elementB=elementD.getChild("table");
					  if(elementB!=null) {
                          elementB.setText(textvalue);
                      } else
					  {
						  elementB=new Element("table");
						  elementB.setText(textvalue);
						  elementD.addContent(elementB);
					  }
					  //清除无效授权指标
					  Element elementF=elementD.getChild("field");
					  if(elementF!=null){
						  if(textvalue==null||textvalue.length()==0){
							  elementF.setText("");
						  }else{
							  	String [] fields = this.field_str.split(",");
								StringBuffer sbfield = new StringBuffer(",");
								for(int i=0;i<fields.length;i++){
									String field = fields[i];
									if(field.length()==6){
										FieldItem item = DataDictionary.getFieldItem(field.substring(0,5).toLowerCase());
										if(item!=null&&textvalue.indexOf(item.getFieldsetid())!=-1) {
                                            sbfield.append(field+",");
                                        }
									}
								}
								elementF.setText(sbfield.toString());
						  }
					  }
				  }else if("field".equalsIgnoreCase(cell))
				  {
					  Element elementF=elementD.getChild("field");
					  if(elementF!=null) {
                          elementF.setText(textvalue);
                      } else
					  {
						  elementF=new Element("field");
						  elementF.setText(textvalue);
						  elementD.addContent(elementF);
					  }
				  }		 
			  }else
			  {
				  xPath = XPath.newInstance("/params");
				  Element element=(Element)xPath.selectSingleNode(doc);	
				  if(element!=null)
				  {					  
					  Element elementC=new Element("codeitem");
					  elementC.setAttribute("id",privcode);		
					  element.addContent(elementC);
					  if("table".equalsIgnoreCase(cell))
					  {
						  Element elementB=new Element("table");
						  elementB.setText(textvalue);
						  elementC.addContent(elementB);
					  }else if("field".equalsIgnoreCase(cell))
					  {
						  Element elementF=new Element("field");
						  elementF.setText(textvalue);
						  elementC.addContent(elementF);
					  }		 
				  }
			  }
			  StringBuffer buf=new StringBuffer();
			  XMLOutputter outputter=new XMLOutputter();
			  Format format=Format.getPrettyFormat();
			  format.setEncoding("UTF-8");
			  outputter.setFormat(format);
			  buf.append(outputter.outputString(doc));
			  this.vo=new RecordVo("t_sys_function_priv");
		      vo.setString("id",this.object_id);
		      vo.setString("status",this.status/*GeneralConstant.ROLE*/);
		      vo.setString("subpriv",buf.toString());		      
		      save();   
		      iscorrect=true;
		}catch(Exception e)
		{
		  e.printStackTrace();	
		}
		return iscorrect;
		
	}
	public String getSubpriv_str() {
		return subpriv_str;
	}
	public void setSubpriv_str(String subpriv_str) {
		this.subpriv_str = subpriv_str;
	}
	
	private boolean isExistRecordVo(RecordVo vo,ContentDAO dao){
		RecordVo tmpvo =null;
		try{
			tmpvo = new RecordVo(vo.getModelName());
			ArrayList keylist=vo.getKeylist();
			for(int i=0;i<keylist.size();i++){
				String name= (String)keylist.get(i);
				tmpvo.setString(name, vo.getString(name));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		}
		return dao.isExistRecordVo(tmpvo);
		
	}
}
