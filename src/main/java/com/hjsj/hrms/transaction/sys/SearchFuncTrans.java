/**
 * 
 */
package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:查询功能列表</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 29, 2008:3:15:01 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SearchFuncTrans extends IBusiness {
    private EncryptLockClient lock;
    /**
     * 权限对象
     */
    private SysPrivBo sysPrivBo;
    /**
     * 增加表头
     * @return
     */
    private String addTableHeader(String label,String tab_name)
    {
        StringBuffer str_header=new StringBuffer();
        str_header.append("<table width='60%'  align='center' border='0' cellpadding='0' cellspacing='0' class='ListTable'>");
        if("tablepriv".equalsIgnoreCase(tab_name))
    	{
        	ContentDAO dao=new ContentDAO(this.getFrameconn());
            ArrayList list=sysPrivBo.getFenleiCodeitemList(dao);
            if(list!=null&&list.size()>0)
            {
            	String privcode=(String)this.getFormHM().get("privcode");
            	if(privcode==null||privcode.length()<=0)
            		privcode="###";
            	str_header.append("<tr>");
            	str_header.append("<td colspan='4' class='TableRow' nowrap>");//子集分类控制
            	str_header.append("人员分类：");
            	str_header.append("<select name='privcode' onchange='onItemChange(this);'>");
            	for(int i=0;i<list.size();i++)
            	{
            		CodeItem codeitem=(CodeItem)list.get(i);
            		if(privcode.equalsIgnoreCase(codeitem.getCcodeitem()))
            		  str_header.append("<option value='"+codeitem.getCcodeitem()+"' selected>"+codeitem.getCodename()+"</option>");	
            		else
            		  str_header.append("<option value='"+codeitem.getCcodeitem()+"'>"+codeitem.getCodename()+"</option>");        		
            	}
            	if("###".equalsIgnoreCase(privcode))
            		str_header.append("<option value='###' selected>默认权限</option>");
            	else
            		str_header.append("<option value='###'>默认权限</option>");
            	str_header.append("</select>");
                str_header.append("</td>");
                str_header.append("</tr>");
                this.getFormHM().put("ishaveprivcode", "1");
                this.getFormHM().put("privcode", privcode);
            }else
            {
            	this.getFormHM().put("privcode", "");
            	this.getFormHM().put("ishaveprivcode", "0");
            }
    	}        
        str_header.append("<tr>");
        str_header.append("<td align='center' class='TableRow' nowrap>");
        str_header.append(ResourceFactory.getProperty(label));
        str_header.append("</td>");
        str_header.append("<td align='center' class='TableRow' nowrap>");
        str_header.append(ResourceFactory.getProperty(GeneralConstant.NULL_LABEL));
        str_header.append("</td>");
        str_header.append("<td align='center' class='TableRow' nowrap>");
        str_header.append(ResourceFactory.getProperty(GeneralConstant.READ_LABEL));
        str_header.append("</td>");
        str_header.append("<td align='center' class='TableRow' nowrap>");
        str_header.append(ResourceFactory.getProperty(GeneralConstant.WRITE_LABEL));
        str_header.append("</td>");  
        str_header.append("</tr>");        
        return str_header.toString();
    }    
    /**
     * 读，写权限
     * @param setid
     * @param setdesc
     * @param flag
     * @return
     */
    private String addTableRow(String setid,String setdesc,String flag,String havePriv)
    {
    	setdesc=setdesc.replaceAll("\n\r", "").replaceAll("\n", "").replaceAll("\r", "");
    	//this.writeDate(setid+":"+setdesc);
        StringBuffer str_row=new StringBuffer();
        str_row.append("<tr>");
        /*多浏览器兼容，非IE不支持直接this.属性名 获取自定义属性值，需要使用getAttribute方法 guodd 2019-03-23*/
        str_row.append("<td align='right' class='RecordRow' nowrap><a href='###' setdesc='"+setdesc+"' onclick=pegging(this.getAttribute('setdesc'),'"+setid+"') >");
        str_row.append(setdesc);
        str_row.append("</a></td>");
        /**读权限,子集有读权限时，指标提供读写两种权限*/
        
        if("1".equals(flag))
        {
            str_row.append("<td align='center' class='RecordRow' nowrap>");
            str_row.append("<input type='radio' name='");//null_str' value='");
	        str_row.append(setid);
	        str_row.append("' value='0'");
            if("0".equals(havePriv))
    	        str_row.append("' checked>");
            else
                str_row.append("'>");
	        str_row.append("</td>");
	        
	        str_row.append("<td align='center' class='RecordRow' nowrap>");
            str_row.append("<input type='radio' name='");//_str' value='");
	        str_row.append(setid);
	        str_row.append("' value='1'");
            if("1".equals(havePriv))
    	        str_row.append("' checked>");
            else
                str_row.append("'>");
	        str_row.append("</td>");
	        
	        str_row.append("<td align='center' class='RecordRow' nowrap>");
	        str_row.append("");
	        str_row.append("</td>");
        }
        
        /**
         * 写权限
         */
        if("2".equals(flag)/*||flag.equals("1")*/)
        {
            str_row.append("<td align='center' class='RecordRow' nowrap>");
            str_row.append("<input type='radio' name='");//null_str' value='");
	        str_row.append(setid);
	        str_row.append("' value='0'");	        
            if("0".equals(havePriv))
    	        str_row.append(" checked>");
            else
                str_row.append(">");
	        str_row.append("</td>");
	        
	        str_row.append("<td align='center' class='RecordRow' nowrap>");
            str_row.append("<input type='radio' name='");//read_str' value='");
	        str_row.append(setid);	 
	        str_row.append("' value='1'");	 	        
            if("1".equals(havePriv))
    	        str_row.append(" checked>");
            else
                str_row.append(">");
	        str_row.append("</td>");
	        
	        str_row.append("<td align='center' class='RecordRow' nowrap>");
            str_row.append("<input type='radio' name='");//write_str' value='");
	        str_row.append(setid);
	        str_row.append("' value='2'");	 	        
            if("2".equals(havePriv))
    	        str_row.append(" checked>");
            else
                str_row.append(">");
	        str_row.append("</td>");
        }        
        str_row.append("</tr>");
        return str_row.toString();
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
     * 查询子集权限信息
     * @return
     */
    private String searchTablePriv()throws GeneralException
    {
        StringBuffer table_str=new StringBuffer();
        StringBuffer strsql=new StringBuffer();
        strsql.append("select fieldsetid,customdesc,");
        strsql.append(Sql_switcher.substr("fieldsetid", "1", "1"));
        strsql.append(" as pre from fieldset where useflag<>'0' order by pre,displayorder");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        /**
         * flag 0,1,2,3,4,5,6
         */
        String flag="0";
        String havePriv="0";
	    try
	    {
	    	VersionControl ver_ctrl=new VersionControl();
	      this.frowset = dao.search(strsql.toString());
	      table_str.append(addTableHeader(GeneralConstant.FIELD_SET,"tablepriv"));
	      String privcode=(String)this.getFormHM().get("privcode");
	      String ishaveprivcode=(String)this.getFormHM().get("ishaveprivcode");
	      while(this.frowset.next())
	      {
	    	  String fieldsetid = this.frowset.getString("fieldsetid");
	    	  if(fieldsetid.startsWith("Y")||fieldsetid.startsWith("V")||fieldsetid.startsWith("W")||fieldsetid.startsWith("v")||fieldsetid.startsWith("y")||fieldsetid.startsWith("w")){
	    		  if(!this.lock.isHaveBM(31))
	    			  continue;
	    		  if(!ver_ctrl.searchFunctionId("350", userView.hasTheFunction("350")))
						continue;
	    	  }
	    	  if(fieldsetid.startsWith("H")&&!ver_ctrl.searchFunctionId("25012", userView.hasTheFunction("25012")))
					continue;
	    		  /**
	           * 支持分布式授权机制
	           */	    	  
	    	  flag=userView.analyseTablePriv(this.frowset.getString("fieldsetid"));
	          if("0".equals(flag))
	              continue;
	          /**
	           * 现拥有的权限
	           */
	          if("1".equals(ishaveprivcode)&&privcode!=null&&privcode.length()>0&&!"###".equals(privcode))
	    	  {
	        	  havePriv=sysPrivBo.analyseSubprivTablePriv(this.frowset.getString("fieldsetid"));	   
	    	  }else
	    	  {
	             havePriv=sysPrivBo.analyseTablePriv(this.frowset.getString("fieldsetid"));	   
	    	  }
	          table_str.append(addTableRow(this.frowset.getString("fieldsetid"),this.frowset.getString("customdesc"),flag,havePriv));
	      }
	      /**
	       * 多媒体子集
	       */
	      if("1".equals(ishaveprivcode)&&privcode!=null&&privcode.length()>0&&!"###".equals(privcode))
    	  {
	    	  flag=userView.analyseTablePriv("A00")+"";
	          if(!"0".equals(flag))
	          {
		          havePriv=sysPrivBo.analyseSubprivTablePriv("A00");	             
		          table_str.append(addTableRow("A00","人员多媒体子集",flag,havePriv));
	          }
	          flag=userView.analyseTablePriv("B00");
	          if(!"0".equals(flag))
	          {
		           
		          havePriv=sysPrivBo.analyseSubprivTablePriv("B00");
		          table_str.append(addTableRow("B00","单位多媒体子集",flag,havePriv));
	          }	      
	          flag=userView.analyseTablePriv("K00");
	          if(!"0".equals(flag))
	          {
	        	  havePriv=sysPrivBo.analyseSubprivTablePriv("K00");             
		          table_str.append(addTableRow("K00","职位多媒体子集",flag,havePriv));
	          }
	          flag=userView.analyseTablePriv("H00");
	          if(!"0".equals(flag))
	          {
	        	  havePriv=sysPrivBo.analyseSubprivTablePriv("H00");             
		          table_str.append(addTableRow("H00","基准岗位多媒体子集",flag,havePriv));
	          }
    	  }else
    	  {
    		  flag=userView.analyseTablePriv("A00");
              if(!"0".equals(flag))
              {
    	          havePriv=sysPrivBo.analyseTablePriv("A00");              
    	          table_str.append(addTableRow("A00","人员多媒体子集",flag,havePriv));
              }
              flag=userView.analyseTablePriv("B00");
              if(!"0".equals(flag))
              {
    	          havePriv=sysPrivBo.analyseTablePriv("B00");              
    	          table_str.append(addTableRow("B00","单位多媒体子集",flag,havePriv));
              }	      
              flag=userView.analyseTablePriv("K00");
              if(!"0".equals(flag))
              {
    	          havePriv=sysPrivBo.analyseTablePriv("K00");              
    	          table_str.append(addTableRow("K00","岗位多媒体子集",flag,havePriv));
              }	
              flag=userView.analyseTablePriv("H00");
              if(!"0".equals(flag))
              {
    	          havePriv=sysPrivBo.analyseTablePriv("H00");              
    	          table_str.append(addTableRow("H00","基准岗位多媒体子集",flag,havePriv));
              }	
    	  }
                
          table_str.append(addTableFoot());
	      return table_str.toString();	      
	    }
	    catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
    }

    /**
     * 生成指标授权界面
     * @param fieldsetid
     * @return
     * @throws GeneralException
     */
    private String addFieldDomain(String fieldsetid,String privcode)throws GeneralException
    {
        StringBuffer strsql=new StringBuffer();
        StringBuffer domain_str=new StringBuffer();
        strsql.append("select itemid,itemdesc from fielditem where fieldsetid='");
        strsql.append(fieldsetid);
        strsql.append("' and useflag<>'0' order by displayid");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
    	ResultSet rset=null;    
    	String flag="0";
    	String havePriv="0";
	    try
	    {
	      rset = dao.search(strsql.toString());
	      
	      domain_str.append("<div style='display:none' id='");
	      domain_str.append("div");
	      domain_str.append(fieldsetid+"_1");
	      domain_str.append("'>");
	      domain_str.append(addTableHeader(GeneralConstant.Field_LABLE,"fieldpriv"));
	      if(privcode!=null&&privcode.length()>0&&!"###".equals(privcode))
    	  {
	    	  flag=sysPrivBo.analyseSubprivTablePriv(this.frowset.getString("fieldsetid"));
    	  }else
    	  {
    		  flag=sysPrivBo.analyseTablePriv(fieldsetid);
    	  }
	      	      
	      /**对主集的另外加上特殊指标*/
	      if("A01".equals(fieldsetid))
	      {
	           /*userView.analyseFieldPriv("B0110")*/;
	          if(!"0".equals(flag))
	          {    
	        	  flag=userView.analyseFieldPriv("B0110");	
		          havePriv=sysPrivBo.analyseFieldPriv("B0110");
		          domain_str.append(addTableRow("B0110",ResourceFactory.getProperty(GeneralConstant.B0110),flag,havePriv));
	          }
	          //flag=userView.analyseFieldPriv("E01A1");
	          if(!"0".equals(flag))
	          {    
	        	  flag=userView.analyseFieldPriv("E01A1");		        	  
		          havePriv=sysPrivBo.analyseFieldPriv("E01A1");
		          domain_str.append(addTableRow("E01A1",ResourceFactory.getProperty(GeneralConstant.E01A1),flag,havePriv));
	          }
	      }	    
	      if("H01".equals(fieldsetid)|| "Y01".equals(fieldsetid)|| "V01".equals(fieldsetid)|| "W01".equals(fieldsetid)){
	    	  if(!"0".equals(flag))
	          {    
	    		  String keyitemid = fieldsetid+"00";
	        	  flag=userView.analyseFieldPriv(keyitemid);	
		          havePriv=sysPrivBo.analyseFieldPriv(keyitemid);
		          domain_str.append(addTableRow(keyitemid,ResourceFactory.getProperty(keyitemid.toLowerCase()+".label"),flag,havePriv));
	          }
	      }
	      while(rset.next())
	      {
	          /**
	           * 支持分布式授权机制
	           */
        	  flag=userView.analyseFieldPriv(rset.getString("itemid")); 
        	  if("0".equals(flag))
        		  continue;
	          //cat.debug("flag_priv="+flag);        	  
	          havePriv=sysPrivBo.analyseFieldPriv(rset.getString("itemid"));
	          //cat.debug(rset.getString("itemid")+"====>"+havePriv);
	          domain_str.append(addTableRow(rset.getString("itemid"),rset.getString("itemdesc"),flag,havePriv));
	      }
	      
	      domain_str.append(addTableFoot());
	      domain_str.append("</div>");
	      
	      return domain_str.toString();	      
	    }
	    catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
    }
    
    /**
     * 查询指标权限
     * @return
     */
    private String searchFieldPriv()throws GeneralException
    {
        StringBuffer field_str=new StringBuffer();
        StringBuffer strsql=new StringBuffer();
        //strsql.append("select fieldsetid,customdesc from fieldset where useflag<>'0' order by displayorder");
        strsql.append("select fieldsetid,customdesc,");
        strsql.append(Sql_switcher.substr("fieldsetid", "1", "1"));
        strsql.append(" as pre from fieldset where useflag<>'0' order by pre,displayorder");
        
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        /**
         * flag 0,1,2,3,4,5,6
         */
        String flag="0";
	    try
	    {
	    	ArrayList list=sysPrivBo.getFenleiCodeitemList(dao);
	    	String privcode=(String)this.getFormHM().get("privcode");
            if(list!=null&&list.size()>0)
            {
            	if(privcode==null||privcode.length()<=0)
            		privcode="###";
            	field_str.append("<div class='TableRow'>");            	
            	field_str.append("人员分类：");
            	field_str.append("<select name='privcode' onchange='onItemChange(this);' style=\\\"margin-top:3px;\\\">");
            	for(int i=0;i<list.size();i++)
            	{
            		CodeItem codeitem=(CodeItem)list.get(i);
            		if(privcode.equalsIgnoreCase(codeitem.getCcodeitem()))
            			field_str.append("<option value='"+codeitem.getCcodeitem()+"' selected>"+codeitem.getCodename()+"</option>");	
            		else
            			field_str.append("<option value='"+codeitem.getCcodeitem()+"'>"+codeitem.getCodename()+"</option>");        		
            	}
            	if("###".equalsIgnoreCase(privcode))
            		field_str.append("<option value='###' selected>默认权限</option>");
            	else
            		field_str.append("<option value='###'>默认权限</option>");
            	field_str.append("</select>");
            	field_str.append("</div>");                
                this.getFormHM().put("ishaveprivcode", "1");
                this.getFormHM().put("privcode", privcode);
            }else
            {
            	this.getFormHM().put("privcode", "");
            	this.getFormHM().put("ishaveprivcode", "0");
            }
	      this.frowset = dao.search(strsql.toString());
	      VersionControl ver_ctrl=new VersionControl();
	      while(this.frowset.next())
	      {
	    	  String fieldsetid = this.frowset.getString("fieldsetid");
	    	  if(fieldsetid.startsWith("Y")||fieldsetid.startsWith("V")||fieldsetid.startsWith("W")||fieldsetid.startsWith("v")||fieldsetid.startsWith("y")||fieldsetid.startsWith("w"))
	    		  if(!this.lock.isHaveBM(31))
	    			  continue;
	    	  if(fieldsetid.startsWith("H")&&!ver_ctrl.searchFunctionId("25012", userView.hasTheFunction("25012")))
					continue;
	          /**支持分布式授权机制*/
	    	  if(privcode!=null&&privcode.length()>0&&!"###".equals(privcode))
	    	  {
	    		  flag=userView.analyseSubTablePriv(privcode,this.frowset.getString("fieldsetid"))+"";
		          if("0".equals(flag))
		              continue;
		          if("0".equals(sysPrivBo.analyseSubprivTablePriv(this.frowset.getString("fieldsetid"))))
		              continue;
	    	  }else
	    	  {
	    		  flag=userView.analyseTablePriv(this.frowset.getString("fieldsetid"));
		          if("0".equals(flag))
		              continue;
		          if("0".equals(sysPrivBo.analyseTablePriv(this.frowset.getString("fieldsetid"))))
		              continue;
	    	  }
	         
	          field_str.append("<div id='");
	          field_str.append("div");
	          field_str.append(this.frowset.getString("fieldsetid"));
	          field_str.append("'>");
	          
	          field_str.append("<span style='cursor:hand;height:15px;padding:5px 0 0 5px;' title='单击展开子集' onclick=show('");
	          field_str.append("div");
	          field_str.append(this.frowset.getString("fieldsetid"));
	          field_str.append("')>");
	          field_str.append("<img src='/images/table.gif' border=0 align=absmiddle style=margin-right:3px;>");
	          field_str.append(this.frowset.getString("customdesc"));
	          field_str.append("</span>");
	          
	          field_str.append(addFieldDomain(this.frowset.getString("fieldsetid"),privcode));
	          
	          field_str.append("</div>");
	      }
	      //this.writeDate(field_str.toString());
	      return field_str.toString();	      
	    }
	    catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
    }    
    
	public void execute() throws GeneralException {
        HashMap hm=(HashMap)this.getFormHM();        
        String	tab_name=(String)hm.get("tab_name");
        String role_id=(String)hm.get("role_id");
        String flag=(String)hm.get("user_flag");
        this.lock=(EncryptLockClient)this.getFormHM().get("lock");
        if(tab_name==null|| "".equals(tab_name))
            return;
        if(role_id==null|| "".equals(role_id))
            return;
        if(flag==null|| "".equals(flag))
            flag=GeneralConstant.ROLE;
        String title="&nbsp;";
        if("funcpriv".equalsIgnoreCase(tab_name))
        	title=ResourceFactory.getProperty("menu.function");
        if("dbpriv".equalsIgnoreCase(tab_name))
        	title=ResourceFactory.getProperty("menu.base");
        if("mediapriv".equalsIgnoreCase(tab_name))
        	title=ResourceFactory.getProperty("menu.media");
        if("managepriv".equalsIgnoreCase(tab_name))
        	title=ResourceFactory.getProperty("menu.manage");
        if("tablepriv".equalsIgnoreCase(tab_name))
        	title=ResourceFactory.getProperty("menu.table");
        if("fieldpriv".equalsIgnoreCase(tab_name))
        	title=ResourceFactory.getProperty("menu.field");        
        if("partymanagepriv".equalsIgnoreCase(tab_name))
        	title=ResourceFactory.getProperty("menu.manage.party");
        if("membermanagepriv".equalsIgnoreCase(tab_name))
        	title=ResourceFactory.getProperty("menu.manage.member");
        if("busipriv".equalsIgnoreCase(tab_name))
        	title=ResourceFactory.getProperty("menu.manage.busi");
        
	    try
	    {
	    	String str="";
	    	if("tablepriv".equalsIgnoreCase(tab_name))
	    	{
	    		String privcode=(String)this.getFormHM().get("privcode");
	        	if(privcode==null||privcode.length()<=0)
	        		privcode="###";
	        	if("###".equals(privcode))
		          sysPrivBo=new SysPrivBo(role_id,flag,this.getFrameconn(),"tablepriv");	
	        	else
	        	   sysPrivBo=new SysPrivBo(role_id,flag,this.getFrameconn(),"subpriv",privcode);
	            str=searchTablePriv();
	            cat.debug("table_priv="+str);	
	            this.getFormHM().put("script_str", str);
	    	}
	        /**
	         * 指标授权
	         */
	        if("fieldpriv".equals(tab_name))
	        {
	        	String privcode=(String)this.getFormHM().get("privcode");
	        	if(privcode==null||privcode.length()<=0)
	        		privcode="###";
	        	if("###".equals(privcode))
		          sysPrivBo=new SysPrivBo(role_id,flag,this.getFrameconn(),"fieldpriv");
	        	else
	        	  sysPrivBo=new SysPrivBo(role_id,flag,this.getFrameconn(),"subpriv",privcode);	
	           str=searchFieldPriv();
	           cat.debug("field_priv="+str);
	           this.getFormHM().put("script_str", str);	           
	        }
	        if("busipriv".equals(tab_name)){
	        	this.getFormHM().put("script_str", this.initBusiprivHtml(role_id,flag));	      
	        }
	    	this.getFormHM().put("tabtitle", title);
	    }
	    catch(Exception ex)
	    {
	    	ex.printStackTrace();
	    	throw GeneralExceptionHandler.Handle(ex);
	    }			

	}

	private String initBusiprivHtml(String id,String status) throws SQLException{
		StringBuffer html = new StringBuffer();
		html.append("<table width=\\\"80%\\\" height=\\\"100%\\\" border=\\\"0\\\" cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" align=\\\"center\\\">");
		html.append("<tr><td align=\\\"center\\\">");
		html.append("<table width=\\\"100%\\\" height=\\\"100%\\\"  border=\\\"1\\\" cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" align=\\\"center\\\" class=ListTableF1>");
		html.append("<tr align=\\\"center\\\"  class=\\\"\\\" style=\\\"BORDER-BOTTOM: #C4D8EE 1pt solid;\\\" >");
		html.append("<td valign=\\\"center\\\" class=\\\"TableRow\\\" width=\\\"20%\\\" >");
		html.append(ResourceFactory.getProperty("busi.name"));
		html.append("</td>");
		html.append("<td valign=\\\"center\\\" class=\\\"TableRow\\\" width=\\\"70%\\\">");
		html.append(ResourceFactory.getProperty("system.unit_id"));
		html.append("</td>");
		html.append("<td valign=\\\"center\\\" class=\\\"TableRow\\\" width=\\\"10%\\\">");
		html.append(ResourceFactory.getProperty("menu.gz.options"));
		html.append("</td>");
		html.append("</tr> ");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String busi_org_dept="";
		String sql = "select busi_org_dept from t_sys_function_priv where id='"+id+"' and status="+status;
		if("0".equalsIgnoreCase(status))
			sql = "select busi_org_dept from operuser where username='"+id+"'";
		this.frecset = dao.search(sql);
		while(this.frecset.next()){
			busi_org_dept = Sql_switcher.readMemo(this.frecset, "busi_org_dept");
		}
		String[] businame={ResourceFactory.getProperty("busi.name.1"),ResourceFactory.getProperty("busi.name.2"),
				ResourceFactory.getProperty("busi.name.3"),ResourceFactory.getProperty("busi.name.4"),
				ResourceFactory.getProperty("busi.name.5"),ResourceFactory.getProperty("busi.name.6"),
				ResourceFactory.getProperty("busi.name.7"),ResourceFactory.getProperty("busi.name.8"),
				ResourceFactory.getProperty("busi.name.9"),ResourceFactory.getProperty("busi.name.10")};
		HashMap mapValue=new HashMap();
		HashMap mapView=new HashMap();
		CheckPrivSafeBo cpsBo = new CheckPrivSafeBo(this.frameconn, this.userView);
		if(busi_org_dept.length()>0){
			String str[] = busi_org_dept.split("\\|");
			for(int i=0;i<str.length;i++){//1,UNxxx`UM9191`
				String tmp = str[i];
				String ts[] = tmp.split(",");
				if(ts.length==2){
					mapValue.put(ts[0], ts[1]);
					StringBuffer sb = new StringBuffer();
					if(ts[1].length()>0){//UNxxx`UM9191`
						ts[1] = cpsBo.orgValidCheck(ts[1]).replaceAll(",", "`");//校验不是有效期内机构不显示  wangb 20180504
						String tt[] = ts[1].split("`");
						for(int n=0;n<tt.length;n++){
							String ttt = tt[n];//UNxxx
							if(ttt.length()>2){
								String codesetid = ttt.substring(0, 2);
								String codeitemid = ttt.substring(2);
								sb.append(com.hrms.frame.utility.AdminCode.getCodeName(codesetid, codeitemid)+",");
							}else{
								continue;
							}
						}
					}
					mapView.put(ts[0], sb.toString());
				}else{
					continue;
				}
			}
		}
		
		html.append("<tr class=\\\"trShallow\\\" height=\\\"30\\\">");
		html.append("<td align=\\\"center\\\" valign=\\\"middle\\\" class=\\\"RecordRowHx\\\" style=\\\"height:60px\\\" nowrap>"+businame[1]+"</td>");
		html.append("<td style=\\\"padding: 5 5 5 5\\\" align=\\\"left\\\" valign=\\\"middle\\\" class=\\\"RecordRowHx\\\" id=busi_org_dept2v>");
		String tmpvalue=(String)mapView.get("2");
		tmpvalue=tmpvalue==null?"":tmpvalue;
		html.append(tmpvalue);
		html.append("</td>");
		String tmpview=(String)mapValue.get("2");
		tmpview=tmpview==null?"":tmpview;
		html.append("<input type=hidden id=busi_org_dept2 value=\\\""+tmpview+"\\\" />");
		html.append("<td  valign=middle class=RecordRowHx align=center>"); 
		html.append(" <img src=/images/code.gif onclick=getorg(2) />"); 
		html.append("</td>");
		html.append("</tr>"); 
		html.append("<tr class=\\\"trShallow\\\" height=\\\"30\\\">"); 
		html.append("<td align=\\\"center\\\" valign=\\\"middle\\\" class=\\\"RecordRowHx\\\" style=\\\"height:60px\\\" nowrap>"+businame[2]+"</td>");
		html.append("<td  style=\\\"padding: 5 5 5 5\\\"  align=left valign=middle class=\\\"RecordRowHx\\\" id=busi_org_dept3v >");
		tmpvalue=(String)mapView.get("3");
		tmpvalue=tmpvalue==null?"":tmpvalue;
		html.append(tmpvalue);
		html.append("</td>");
		tmpview=(String)mapValue.get("3");
		tmpview=tmpview==null?"":tmpview;
		html.append("<input type=hidden id=busi_org_dept3 value=\\\""+tmpview+"\\\" />");
		html.append("<td  valign=middle class=\\\"RecordRowHx\\\" align=\\\"center\\\">");  
		html.append("<img src=\\\"/images/code.gif\\\" onclick=\\\"getorg(3);\\\"/>");   	
		html.append("</td>");
		html.append("</tr>"); 
		html.append("<tr class=\\\"trShallow\\\" height=\\\"30\\\">"); 
		html.append("<td align=\\\"center\\\" valign=\\\"middle\\\" class=\\\"RecordRowHx\\\" style=\\\"height:60px\\\" nowrap>"+businame[0]+"</td>");
		html.append("<td style=\\\"padding: 5 5 5 5\\\"   align=left valign=middle class=\\\"RecordRowHx\\\" id=busi_org_dept1v >");
		tmpvalue=(String)mapView.get("1");
		tmpvalue=tmpvalue==null?"":tmpvalue;
		html.append(tmpvalue);
		html.append("</td>");
		tmpview=(String)mapValue.get("1");
		tmpview=tmpview==null?"":tmpview;
		html.append("<input type=hidden id=busi_org_dept1 value=\\\""+tmpview+"\\\" />");
		html.append("<td  valign=middle class=\\\"RecordRowHx\\\" align=\\\"center\\\"> "); 
		html.append("<img src=\\\"/images/code.gif\\\" onclick=\\\"getorg(1);\\\"/>");   	
		html.append("</td>");
		html.append("</tr>"); 
		html.append("<tr class=\\\"trShallow\\\" height=\\\"30\\\"> ");
		html.append("<td align=\\\"center\\\" valign=\\\"middle\\\" class=\\\"RecordRowHx\\\" style=\\\"height:60px\\\" nowrap>"+businame[3]+"</td>");
		html.append("<td  style=\\\"padding: 5 5 5 5\\\"  align=left valign=middle class=\\\"RecordRowHx\\\" id=busi_org_dept4v >");
		tmpvalue=(String)mapView.get("4");
		tmpvalue=tmpvalue==null?"":tmpvalue;
		html.append(tmpvalue);
		html.append("</td>");
		tmpview=(String)mapValue.get("4");
		tmpview=tmpview==null?"":tmpview;
		html.append("<input type=hidden id=busi_org_dept4 value=\\\""+tmpview+"\\\" />");
		html.append("<td  valign=middle class=\\\"RecordRowHx\\\" align=\\\"center\\\"> "); 
		html.append("<img src=\\\"/images/code.gif\\\" onclick=\\\"getorg(4);\\\"/>");   	
		html.append("</td>");
		html.append("</tr>");
		html.append("<tr class=\\\"trShallow\\\" height=\\\"30\\\"> ");
		html.append("<td align=\\\"center\\\" valign=\\\"middle\\\" class=\\\"RecordRowHx\\\" style=\\\"height:60px\\\" nowrap>"+businame[4]+"</td>");
		html.append("<td style=\\\"padding: 5 5 5 5\\\" align=left valign=middle class=\\\"RecordRowHx\\\" id=busi_org_dept5v >");
		tmpvalue=(String)mapView.get("5");
		tmpvalue=tmpvalue==null?"":tmpvalue;
		html.append(tmpvalue);
		html.append("</td>");
		tmpview=(String)mapValue.get("5");
		tmpview=tmpview==null?"":tmpview;
		html.append("<input type=hidden id=busi_org_dept5 value=\\\""+tmpview+"\\\" />");
		html.append("<td  valign=middle class=\\\"RecordRowHx\\\" align=\\\"center\\\"> "); 
		html.append("<img src=\\\"/images/code.gif\\\" onclick=\\\"getorg(5);\\\"/>");   	
		html.append("</td>");
		html.append("</tr>");
		html.append("<tr class=\\\"trShallow\\\" height=\\\"30\\\"> ");
		html.append("<td align=\\\"center\\\" valign=\\\"middle\\\" class=\\\"RecordRowHx\\\" style=\\\"height:60px\\\" nowrap>"+businame[5]+"</td>");
		html.append("<td  style=\\\"padding: 5 5 5 5\\\"  align=left valign=middle class=\\\"RecordRowHx\\\" id=busi_org_dept6v >");
		tmpvalue=(String)mapView.get("6");
		tmpvalue=tmpvalue==null?"":tmpvalue;
		html.append(tmpvalue);
		html.append("</td>");
		tmpview=(String)mapValue.get("6");
		tmpview=tmpview==null?"":tmpview;
		html.append("<input type=hidden id=busi_org_dept6 value=\\\""+tmpview+"\\\" />");
		html.append("<td  valign=middle class=\\\"RecordRowHx\\\" align=\\\"center\\\">");  
		html.append("<img src=\\\"/images/code.gif\\\" onclick=\\\"getorg(6);\\\"/>");   	
		html.append(" </td>");
		html.append("</tr>");
		html.append("<tr class=\\\"trShallow\\\" height=\\\"30\\\"> ");
		html.append("<td align=\\\"center\\\" valign=\\\"middle\\\" class=\\\"RecordRowHx\\\" style=\\\"height:60px\\\" nowrap>"+businame[6]+"</td>");
		html.append("<td  style=\\\"padding: 5 5 5 5\\\"  align=left valign=middle class=\\\"RecordRowHx\\\" id=busi_org_dept7v >");
		tmpvalue=(String)mapView.get("7");
		tmpvalue=tmpvalue==null?"":tmpvalue;
		html.append(tmpvalue);
		html.append("</td>");
		tmpview=(String)mapValue.get("7");
		tmpview=tmpview==null?"":tmpview;
		html.append("<input type=hidden id=busi_org_dept7 value=\\\""+tmpview+"\\\" />");
		html.append("<td  valign=middle class=\\\"RecordRowHx\\\" align=\\\"center\\\">");  
		html.append("<img src=\\\"/images/code.gif\\\" onclick=\\\"getorg(7);\\\"/>");   	
		html.append("</td>");
		html.append("</tr>");
		html.append("<tr class=\\\"trShallow\\\" height=\\\"30\\\">"); 
	    html.append("<td align=\\\"center\\\" valign=\\\"middle\\\" class=\\\"RecordRowHx\\\" style=\\\"height:60px\\\" nowrap>"+businame[7]+"</td>");
		html.append("<td  style=\\\"padding: 5 5 5 5\\\"  align=left valign=middle class=\\\"RecordRowHx\\\" id=busi_org_dept8v >");
		tmpvalue=(String)mapView.get("8");
		tmpvalue=tmpvalue==null?"":tmpvalue;
		html.append(tmpvalue);
		html.append("</td>");
		tmpview=(String)mapValue.get("8");
		tmpview=tmpview==null?"":tmpview;
		html.append("<input type=hidden id=busi_org_dept8 value=\\\""+tmpview+"\\\" />");
		html.append("<td  valign=middle class=\\\"RecordRowHx\\\" align=\\\"center\\\">");  
		html.append("<img src=\\\"/images/code.gif\\\" onclick=\\\"getorg(8);\\\"/> ");  	
		html.append("</td>");
		html.append("</tr>");
		//职称评审业务权限设置 jingq add 2015.10.26
		html.append("<tr class=\\\"trShallow\\\" height=\\\"30\\\">"); 
	    html.append("<td align=\\\"center\\\" valign=\\\"middle\\\" class=\\\"RecordRowHx\\\" style=\\\"height:60px\\\" nowrap>"+businame[8]+"</td>");
		html.append("<td  style=\\\"padding: 5 5 5 5\\\"  align=left valign=middle class=\\\"RecordRowHx\\\" id=busi_org_dept9v >");
		tmpvalue=(String)mapView.get("9");
		tmpvalue=tmpvalue==null?"":tmpvalue;
		html.append(tmpvalue);
		html.append("</td>");
		tmpview=(String)mapValue.get("9");
		tmpview=tmpview==null?"":tmpview;
		html.append("<input type=hidden id=busi_org_dept9 value=\\\""+tmpview+"\\\" />");
		html.append("<td  valign=middle class=\\\"RecordRowHx\\\" align=\\\"center\\\">");  
		html.append("<img src=\\\"/images/code.gif\\\" onclick=\\\"getorg(9);\\\"/> ");  	
		html.append("</td>");
		html.append("</tr>");
		
		//考勤管理业务权限设置 haosl add 2018.10.11
		html.append("<tr class=\\\"trShallow\\\" height=\\\"30\\\">"); 
	    html.append("<td align=\\\"center\\\" valign=\\\"middle\\\" class=\\\"RecordRowHx\\\" style=\\\"height:60px\\\" nowrap>"+ResourceFactory.getProperty("busi.name.11")+"</td>");
		html.append("<td  style=\\\"padding: 5 5 5 5\\\"  align=left valign=middle class=\\\"RecordRowHx\\\" id=busi_org_dept11v >");
		tmpvalue=(String)mapView.get("11");
		tmpvalue=tmpvalue==null?"":tmpvalue;
		html.append(tmpvalue);
		html.append("</td>");
		tmpview=(String)mapValue.get("11");
		tmpview=tmpview==null?"":tmpview;
		html.append("<input type=hidden id=busi_org_dept11 value=\\\""+tmpview+"\\\" />");
		html.append("<td  valign=middle class=\\\"RecordRowHx\\\" align=\\\"center\\\">");  
		html.append("<img src=\\\"/images/code.gif\\\" onclick=\\\"getorg(11);\\\"/> ");
		
		//证照管理业务权限设置 zhaoxj add 2018.06.25
		html.append("<tr class=\\\"trShallow\\\" height=\\\"30\\\">"); 
	    html.append("<td align=\\\"center\\\" valign=\\\"middle\\\" class=\\\"RecordRowHx\\\" style=\\\"height:60px\\\" nowrap>"+businame[9]+"</td>");
		html.append("<td  style=\\\"padding: 5 5 5 5\\\"  align=left valign=middle class=\\\"RecordRowHx\\\" id=busi_org_dept10v >");
		tmpvalue=(String)mapView.get("10");
		tmpvalue=tmpvalue==null?"":tmpvalue;
		html.append(tmpvalue);
		html.append("</td>");
		tmpview=(String)mapValue.get("10");
		tmpview=tmpview==null?"":tmpview;
		html.append("<input type=hidden id=busi_org_dept10 value=\\\""+tmpview+"\\\" />");
		html.append("<td  valign=middle class=\\\"RecordRowHx\\\" align=\\\"center\\\">");  
		html.append("<img src=\\\"/images/code.gif\\\" onclick=\\\"getorg(10);\\\"/> ");  	
		html.append("</td>");
		html.append("</tr>");
		
		html.append("</table>");
		html.append("</td>");
		html.append("</tr>");
		
		
		html.append("</table>");   
		return html.toString();
	}
	/** private static void writeDate(String value) {  
		BufferedWriter output  = null;
        try{  
            //File file = new File("c:/c.txt");  
            //if (!file.exists())   
            	//file.createNewFile();   
            output = new BufferedWriter(new FileWriter("c:/c.txt",true));  
            output.newLine();
            output.write(value);  
        } catch (Exception ex) {  
            ex.printStackTrace(); 
        } finally{
        	try {
        		output.flush();
				output.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
        }
       
 }  **/
}
