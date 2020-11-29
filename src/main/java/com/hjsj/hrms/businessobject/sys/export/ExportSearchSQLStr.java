package com.hjsj.hrms.businessobject.sys.export;

import com.hjsj.hrms.businessobject.sys.ExportXmlBo;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
  */
public class ExportSearchSQLStr {
	  /**
     * 生成主集前台界面
     * @return
     * @throws GeneralException
     */
    public String searchDbNameHtml(Connection conn)throws GeneralException{
        StringBuffer db_str=new StringBuffer();
        StringBuffer strsql=new StringBuffer();
        strsql.append("select dbid,dbname,pre from dbname");
        ContentDAO dao=new ContentDAO(conn);
	    try{
	      RowSet rs = dao.search(strsql.toString());
	      ExportXmlBo export = new ExportXmlBo(conn,"SYS_EXPORT");
	      ArrayList vo = (ArrayList)export.elementName("/root/base","name");
	      while(rs.next()){
	      	  db_str.append("<div id='");
	          db_str.append("div");
	          db_str.append(rs.getString("pre"));
	          db_str.append("'>");
	          db_str.append("<span>");  
	      	  db_str.append("<input type='checkbox' name='input");
	      	  db_str.append(rs.getString("dbid"));
	      	  db_str.append("' value='");
	          db_str.append(rs.getString("pre"));
	          db_str.append("' id='input");
	          db_str.append(rs.getString("dbid"));
	          db_str.append("'");
	          for(int i=0;i<vo.size();i++){
	            if(vo.get(i).equals(rs.getString("pre"))){
	            	db_str.append(" checked");
	            }
	         }
	          db_str.append(">");
	          db_str.append(rs.getString("dbname"));
	          db_str.append("</span>");
	          db_str.append("</div>");
	      }
	      
	      db_str.append("<div id='");
          db_str.append("div");
          db_str.append("B");
          db_str.append("'>");
          db_str.append("<span>");  
      	  db_str.append("<input type='checkbox' name='");
      	  db_str.append("dep");
      	  db_str.append("' value='");
          db_str.append("B");
          db_str.append("' id='dep'");
          for(int i=0;i<vo.size();i++){
            if("B".equals(vo.get(i))){
            	db_str.append(" checked");
            }
         }
          db_str.append(">");
          db_str.append(ResourceFactory.getProperty("sys.export.company"));
          db_str.append("</span>");
          db_str.append("</div>");
          
          db_str.append("<div id='");
          db_str.append("div");
          db_str.append("K");
          db_str.append("'>");
          db_str.append("<span>");  
      	  db_str.append("<input type='checkbox' name='");
      	  db_str.append("job");
      	  db_str.append("' value='");
          db_str.append("K");
          db_str.append("' id='job'");
          for(int i=0;i<vo.size();i++){
            if("K".equals(vo.get(i))){
            	db_str.append(" checked");
            }
         }
          db_str.append(">");
          db_str.append(ResourceFactory.getProperty("sys.export.job"));
          db_str.append("</span>");
          db_str.append("</div>");
	      
	      return db_str.toString();	      
	    }
	    catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
    }
    
    /**
     * 子集权限
     * @param setid
     * @param setdesc
     * @param No
     * @return
     */
    private String addSetRow(Connection conn,String setid,String setdesc,int No)
    {
    	StringBuffer str_row=new StringBuffer();
        str_row.append("<tr>");
        str_row.append("<td align='center' class='RecordRow' nowrap>");
        str_row.append(No);
        str_row.append("</td>");
        str_row.append("<td align='right' class='RecordRow' nowrap>");
        str_row.append(setdesc+"("+setid+")");
        str_row.append("</td>");
    	str_row.append("<td align='center' class='RecordRow' nowrap>");
        str_row.append("<input type='checkbox' name='func");//null_str' value='");
        str_row.append("' value='");
        str_row.append(setid);
        str_row.append("'");
        ExportXmlBo export = new ExportXmlBo(conn,"SYS_EXPORT");
	    ArrayList infoSetList = (ArrayList)export.elementName("/root/base/subset","name");
	    ArrayList infoSet= export.elementSet(infoSetList);
	    
	    for(int i=0;i<infoSet.size();i++) {
	        if(infoSet.get(i).equals(setid)){
	        	str_row.append("checked");
	        }
	    }
        str_row.append(">");
        str_row.append("</td>");
        str_row.append("</tr>");
        return str_row.toString();      
    }
    /**
     * 查询子集权限信息
     * @return
     */
    public String searchTablePriv(Connection conn)throws GeneralException{
        StringBuffer table_str=new StringBuffer();
        ArrayList infoSetList= new ArrayList();
        
        ExportXmlBo export = new ExportXmlBo(conn,"SYS_EXPORT");
	    ArrayList baseList = (ArrayList)export.elementName("/root/base","flag");
	    ArrayList infoSet= export.elementSet(baseList);
	    
	    String[] flag = {"0","0","0"};
	    for(int i=0;i<infoSet.size();i++){
	    	if("A".equals(infoSet.get(i))){
	    		flag[0]="A";
	    	}
	    	if("B".equals(infoSet.get(i))){
	    		flag[1]="B";
	    	}
	    	if("K".equals(infoSet.get(i))){
	    		flag[2]="K";
	    	}
	    }
	    
	    
	    if("A".equals(flag[0])){
	    	ArrayList infroSetListEMPLOY = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
		     infoSetList.addAll(infroSetListEMPLOY);
	    }
	    if("B".equals(flag[1])){
	    	ArrayList infroSetListUNIT = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
	 	        infoSetList.addAll(infroSetListUNIT);	
	    }
	    if("K".equals(flag[2])){
	    	ArrayList infroSetListPOS = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET); 
		        infoSetList.addAll(infroSetListPOS);
	    }

        try{
	    	table_str.append(addTableHeader(GeneralConstant.FIELD_SET));
	    	for(int i=0;i<infoSetList.size();i++){
   	   	      FieldSet fieldset=(FieldSet)infoSetList.get(i); 
	          table_str.append(addSetRow(conn,fieldset.getFieldsetid(),fieldset.getFieldsetdesc(),i));
	      }
	      
	      table_str.append(addTableFoot());
	      return table_str.toString();	      
	    }
	    catch(Exception sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
    }
	/**
     * 增加表头
     * @return
     */
    private String addTableHeader(String label)
    {
        StringBuffer str_header=new StringBuffer();
        str_header.append("<table width='80%'  align='center' border='0' cellpadding='1' cellspacing='1' class='ListTable'>");
        str_header.append("<tr>");
        str_header.append("<td align='center' class='TableRow' width='10%' nowrap>");
        str_header.append(ResourceFactory.getProperty("label.serialnumber"));
        str_header.append("</td>");
        str_header.append("<td align='center' class='TableRow' width='50%' nowrap>");
        str_header.append(ResourceFactory.getProperty(label));
        str_header.append("</td>");
        str_header.append("<td align='center' class='TableRow' nowrap>");
        str_header.append(ResourceFactory.getProperty("column.select"));
        str_header.append("</td>");
        str_header.append("</tr>");        
        return str_header.toString();
    }
    /**
     * 增加表尾
     * @return
     */
    private String addTableFoot()
    {
        StringBuffer str_footer=new StringBuffer();
        str_footer.append("</table>");
        return str_footer.toString();
    }
    
    /**
     * 指标权限
     * @param setid
     * @param setdesc
     * @param flag
     * @return
     */
    private String addTableRow(String setid,String setdesc,String fieldValue,int No)
    {
    	StringBuffer str_row=new StringBuffer();
        str_row.append("<tr>");
        str_row.append("<td align='center' width='10%' class='RecordRow' nowrap>");
        str_row.append(No);
        str_row.append("</td>");
        str_row.append("<td align='right' width='50%' class='RecordRow' nowrap>");
        str_row.append(setdesc);
        str_row.append("</td>");
    	str_row.append("<td align='center' class='RecordRow' nowrap>");
        str_row.append("<input type='checkbox' name='func");//null_str' value='");
        str_row.append("' value='");
        str_row.append(setid);
        str_row.append("'");
        String[] temp=setid.split("\\.");
        if(fieldValue!=null&&fieldValue.length()>0){
        	String[] filed = fieldValue.split(",");
        	for(int i=0;i<filed.length;i++){
        		String filedset[] = filed[i].split("\\."); 
        		if(filedset[1].equals(temp[1])){
	        		str_row.append(" checked");
        		}
        	}
        }
        str_row.append(">");
        str_row.append("</td>");

        str_row.append("</tr>");
        return str_row.toString();      
    }
    /**
     * 生成指标授权界面
     * @param fieldsetid
     * @return
     * @throws GeneralException
     */
    private String addFieldDomain(ExportXmlBo export,String fieldsetid,String fieldValue)throws GeneralException
    {
        StringBuffer domain_str=new StringBuffer();
        ArrayList infoSetList= DataDictionary.getFieldList(fieldsetid,Constant.USED_FIELD_SET);

	    try
	    {
	      domain_str.append("<input type='hidden' name='fieldtiemid'>");
	      domain_str.append("<div style='display:none' id='");
	      domain_str.append("div");
	      domain_str.append(fieldsetid+"_1");
	      domain_str.append("'>");
	      domain_str.append(addTableHeader(GeneralConstant.Field_LABLE)); 
	      
		    for(int i=0;i<infoSetList.size();i++) {
	      	  FieldItem fielditem=(FieldItem)infoSetList.get(i);
	          domain_str.append(addTableRow(fieldsetid+"."+fielditem.getItemid().toUpperCase(),fielditem.getItemdesc(),fieldValue,i));
		    }
	      
	      domain_str.append(addTableFoot());
	      domain_str.append("</div>");
	      
	      return domain_str.toString();	      
	    }
	    catch(Exception sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
    }
    /**
     * 查询指标权限
     * @return
     */
    public String searchFieldPriv(Connection conn)throws GeneralException { 
        StringBuffer field_str=new StringBuffer();
        ExportXmlBo export = new ExportXmlBo(conn,"SYS_EXPORT");
	    ArrayList infoSetList = (ArrayList)export.elementName("/root/base/subset","name");
	    ArrayList infoSet= export.elementSet(infoSetList);
	    
	    String fileditemid = export.elementValueStr("/root/base/subset");

	    for(int i=0;i<infoSet.size();i++){
           StringBuffer strsql=new StringBuffer();
           strsql.append("select fieldsetid,customdesc from fieldset where fieldsetid = '"+(String)infoSet.get(i)+"'");
           ContentDAO dao=new ContentDAO(conn);
           RowSet rs = null;
	       try
	       {
	    	 rs = dao.search(strsql.toString());
	         while(rs.next()){
	             field_str.append("<div id='");
	             field_str.append("div");
	             field_str.append(rs.getString("fieldsetid"));
	             field_str.append("'>");
	          
	             field_str.append("<span style='cursor:hand' title='");
	             field_str.append(ResourceFactory.getProperty("sys.export.opensubset"));
	             field_str.append("' onclick=show('");
	             field_str.append("div");
	             field_str.append(rs.getString("fieldsetid"));
	             field_str.append("')>");
	             field_str.append("<img src='/images/table.gif' border=0>");
	             field_str.append(rs.getString("customdesc"));
	             field_str.append("</span>");
	             
	             field_str.append(addFieldDomain(export,rs.getString("fieldsetid"),fileditemid));
	             field_str.append("</div>");
	         }
	       }catch(SQLException sqle){
		      sqle.printStackTrace();
		      throw GeneralExceptionHandler.Handle(sqle);
		    }
        }
	    return field_str.toString();	         
    }
    public String[] sqlStr(){
		String[] sqlstr=new String[4];
		
		String where=" from t_sys_jobs ";
		String column="job_id,description,jobclass,job_param,job_time,trigger_flag,status";
		String sql="select "+column;
		String orderby="order by job_id ";
		sqlstr[0]=sql;
		sqlstr[1]=where;
		sqlstr[2]=column;
		sqlstr[3]=orderby;
		return  sqlstr;
	}

}
