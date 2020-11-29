package com.hjsj.hrms.transaction.sys;


import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;



/**
 * @author chenmengqing
 */
public class SearchResourceTrans extends IBusiness {
    private String oSpan="var oSpan = document.createElement('span');";
    private String oInput="var oInput = document.createElement('input');";
    /**
     * 权限对象
     */
    private SysPrivBo sysPrivBo;
    
    private EncryptLockClient lock;
    
    /**
     * 递归查询子菜单项
     * @param pDiv
     * @param up_menu_id 
     * @return
     * @throws GeneralException
     */
    private String searchSubFunctionDom(String pDiv,String up_menu_id)throws GeneralException
    {
        StringBuffer strFunc=new StringBuffer();  
    	StringBuffer strsql=new StringBuffer();
    	strsql.append("select function_id,menu_name,menu_id from t_sys_menu ");
    	strsql.append(" where is_available=1 and up_menu_id=");
    	strsql.append("'");
    	strsql.append(up_menu_id);
    	strsql.append("' and menu_id<>'");
    	strsql.append(up_menu_id);
    	strsql.append("' order by menu_id,order_id");
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	ResultSet rset=null;
	    try
	    {
	      rset=dao.search(strsql.toString());
	      //System.out.println("---->SQL="+strsql.toString());
	      while(rset.next())
	      {
	          String oDiv="oDiv"+rset.getString("menu_id");
	          strFunc.append("var ");
	          strFunc.append(oDiv);
	          strFunc.append("=document.createElement('div');");
	          strFunc.append(oSpan);
	          strFunc.append(oInput);
	          strFunc.append("oInput.setAttribute('type','CHECKBOX');");	          
	          strFunc.append("oInput.setAttribute('name','func');");
	          strFunc.append("oInput.setAttribute('value','");	          
	          strFunc.append(rset.getString("function_id"));
	          strFunc.append("');");
	          strFunc.append("var oText=document.createTextNode('");
	          strFunc.append(rset.getString("menu_name"));
	          strFunc.append("');");
	          strFunc.append("oSpan.appendChild(oInput);");	          
	          strFunc.append("oSpan.appendChild(oText);");
	          strFunc.append(oDiv);
	          strFunc.append(".appendChild(oSpan);");
	          
	          strFunc.append(pDiv);
	          strFunc.append(".appendChild(");
	          strFunc.append(oDiv);
	          strFunc.append(");");
	          
	          strFunc.append(searchSubFunctionDom(oDiv,rset.getString("menu_id")));

	      }
	      return strFunc.toString();   	      
	    }
	    catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
        finally
        {	
            try
            {
                if(rset!=null)
                    rset.close();
            }
            catch(SQLException sqle0)
            {
                sqle0.printStackTrace();
            }
         }
	}

    /**
     * 查询一级菜单表,前台输出结果能出来，但报错
     * @return
     * @throws GeneralException
     */
    private String searchFunctionDom()throws GeneralException
    {
        StringBuffer strFunc=new StringBuffer();
        StringBuffer strsql=new StringBuffer();
        strsql.append("select function_id,menu_name,menu_id from t_sys_menu ");
        strsql.append(" where is_available=1 and menu_id=up_menu_id order by menu_id,order_id");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
	    try
	    {
	      this.frowset = dao.search(strsql.toString());
	      String oDiv=null;
	      while(this.frowset.next())
	      {
	          oDiv="oDiv"+this.frowset.getString("menu_id");
	          strFunc.append("var ");
	          strFunc.append(oDiv);
	          strFunc.append("= document.createElement('div');");
	          strFunc.append(oSpan);
	          strFunc.append(oInput);
	          strFunc.append("oInput.setAttribute('type','CHECKBOX');");	          
	          strFunc.append("oInput.setAttribute('name','func');");
	          strFunc.append("oInput.setAttribute('value','");	          
	          strFunc.append(this.frowset.getString("function_id"));
	          strFunc.append("');");
	          strFunc.append("var oText=document.createTextNode('");
	          strFunc.append(this.frowset.getString("menu_name"));
	          strFunc.append("');");
	          strFunc.append("oSpan.appendChild(oInput);");	          
	          strFunc.append("oSpan.appendChild(oText);");
	          strFunc.append(oDiv);
	          strFunc.append(".appendChild(oSpan);");
	          strFunc.append("document.body.appendChild(");
	          strFunc.append(oDiv);
	          strFunc.append(");");	  	          
	          strFunc.append(searchSubFunctionDom(oDiv,this.frowset.getString("menu_id")));
	      }
	      return strFunc.toString();	      
	    }
	    catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }

    }

    /**
     * 递归查询子菜单项
     * @param level
     * @param up_menu_id 
     * @return
     * @throws GeneralException
     */
    private String searchSubFunctionHmtl(int level,String up_menu_id)throws GeneralException
    {
        StringBuffer strPre=new StringBuffer();
        for(int i=0;i<level;i++)
            strPre.append("&nbsp;&nbsp;");
        StringBuffer strFunc=new StringBuffer();  
    	StringBuffer strsql=new StringBuffer();
    	strsql.append("select function_id,menu_name,menu_id from t_sys_menu ");
    	strsql.append(" where is_available=1 and up_menu_id=");
    	strsql.append("'");
    	strsql.append(up_menu_id);
    	strsql.append("' and menu_id<>'");
    	strsql.append(up_menu_id);
    	strsql.append("' order by menu_id,order_id");
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	ResultSet rset=null;

	    try
	    {
	      rset=dao.search(strsql.toString());
	      //System.out.println("---->SQL="+strsql.toString());
	      while(rset.next())
	      {
	          /**
	           * 支持分布式授权机制
	           */
	          if(!userView.hasTheFunction(rset.getString("function_id")))
	              continue;	          
	          strFunc.append("<div style='display:none' id='");
	          strFunc.append("div");
	          strFunc.append(rset.getString("menu_id"));
	          strFunc.append("'>");

	          strFunc.append("<span style='cursor:hand' onDblClick=show('");
	          strFunc.append("div");
	          strFunc.append(rset.getString("menu_id"));
	          strFunc.append("')>");	          
	          strFunc.append(strPre.toString());
	          strFunc.append("<input type='checkbox' name='func' value='");
	          strFunc.append(rset.getString("function_id"));
	          strFunc.append("' id='input");
	          strFunc.append(rset.getString("menu_id"));
	          strFunc.append("' onclick=setvalue('");
	          strFunc.append("div");
	          strFunc.append(rset.getString("menu_id"));
	          strFunc.append("','input");
	          strFunc.append(rset.getString("menu_id"));
	          strFunc.append("') ");
	          /**
	           * 再现其功能权限
	           */
	          if(sysPrivBo.isHaveTheFunction(rset.getString("function_id")))
	          {
	              strFunc.append(" checked");
	          }
	          strFunc.append(">");	          
	          
	          strFunc.append(rset.getString("menu_name"));
	          strFunc.append("</span>");
	          ++level;
	          strFunc.append(searchSubFunctionHmtl(level,rset.getString("menu_id")));
	          strFunc.append("</div>");	          

	      }
	      return strFunc.toString();   	      
	    }
	    catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
        finally
        {	
            try
            {
                if(rset!=null)
                    rset.close();
            }
            catch(SQLException sqle0)
            {
                sqle0.printStackTrace();
            }
         }
	}
    
    /**
     * 查询一级菜单表,
     * @return
     * @throws GeneralException
     */
    private String searchFunctionHmtl()throws GeneralException
    {
        StringBuffer strFunc=new StringBuffer();
        StringBuffer strsql=new StringBuffer();
        strsql.append("select function_id,menu_name,menu_id from t_sys_menu ");
        strsql.append(" where is_available=1 and menu_id=up_menu_id order by menu_id,order_id");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
	    try
	    {
	      this.frowset = dao.search(strsql.toString());
//	      String oDiv=null;
	      while(this.frowset.next())
	      {
	          /**
	           * 支持分布式授权机制
	           */
	          if(!userView.hasTheFunction(this.frowset.getString("function_id")))
	              continue;
	          strFunc.append("<div id='");
	          strFunc.append("div");
	          strFunc.append(this.frowset.getString("menu_id"));
	          strFunc.append("'>");
	          strFunc.append("<span style='cursor:hand' onDblClick=show('");
	          strFunc.append("div");
	          strFunc.append(this.frowset.getString("menu_id"));
	          strFunc.append("')>");
	          strFunc.append("<input type='checkbox' name='func' value='");
	          strFunc.append(this.frowset.getString("function_id"));
	          strFunc.append("' id='input");
	          strFunc.append(this.frowset.getString("menu_id"));
	          strFunc.append("' onclick=setvalue('");
	          strFunc.append("div");
	          strFunc.append(this.frowset.getString("menu_id"));
	          strFunc.append("','input");
	          strFunc.append(this.frowset.getString("menu_id"));
	          strFunc.append("') ");
	          if(sysPrivBo.isHaveTheFunction(this.frowset.getString("function_id")))
	          {
	              strFunc.append(" checked");
	          }
	          strFunc.append(">");
	          
	          strFunc.append(this.frowset.getString("menu_name"));
	          strFunc.append("</span>");
         
	          strFunc.append(searchSubFunctionHmtl(1,this.frowset.getString("menu_id")));
	          
	          strFunc.append("</div>");
	      }
	      return strFunc.toString();	      
	    }
	    catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }

    }
    
    /**子菜单功能*/
    private String searchSubFunctionXmlHtml(int level,Element element)
    {
        StringBuffer strPre=new StringBuffer();
        Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());//判断
        String inputchinfor=sysbo.getValue(Sys_Oth_Parameter.INPUTCHINFOR);
		inputchinfor=inputchinfor!=null&&inputchinfor.trim().length()>0?inputchinfor:"1";//是不是直接入库
		String approveflag=sysbo.getValue(Sys_Oth_Parameter.APPROVE_FLAG);
		approveflag=approveflag!=null&&approveflag.trim().length()>0?approveflag:"1";//是不是审批
		HashMap filtrateMap=filtrateId(inputchinfor,approveflag);
        ++level;
        for(int i=0;i<level;i++)
            strPre.append("&nbsp;&nbsp;");
        StringBuffer strFunc=new StringBuffer(); 
        List list = element.getChildren("function"); 
        VersionControl ver_ctrl=new VersionControl();        
        for(int i=0;i<list.size();i++)
        {
              Element node = (Element) list.get(i);
	          String func_id=node.getAttributeValue("id");
	          if(!isMayOut(func_id))
	        	  continue; 
	          /**版本控制*/
	          if(!ver_ctrl.searchFunctionId(node.getAttributeValue("id")))
	        	  continue;	          
	          /**
	           * 支持分布式授权机制
	           */
	          /**过滤**/
	          if(isFiltrate(func_id,filtrateMap))
	        	  continue;	
	          if(!userView.hasTheFunction(node.getAttributeValue("id")))
	              continue;	          
	          strFunc.append("<div style='display:none' id='");
	          strFunc.append("div");
	          strFunc.append(node.getAttributeValue("id"));
	          strFunc.append("'>");

	          strFunc.append("<span style='cursor:hand' title='单击展开菜单' onclick=show('");
	          strFunc.append("div");
	          strFunc.append(node.getAttributeValue("id"));
	          strFunc.append("')>");	          
	          strFunc.append(strPre.toString());
	          strFunc.append("<input type='checkbox' name='func' value='");
	          strFunc.append(node.getAttributeValue("id"));
	          strFunc.append("' id='input");
	          strFunc.append(node.getAttributeValue("id"));
	          strFunc.append("' onclick=setvalue('");
	          strFunc.append("div");
	          strFunc.append(node.getAttributeValue("id"));
	          strFunc.append("','input");
	          strFunc.append(node.getAttributeValue("id"));
	          strFunc.append("') ");
	          /**
	           * 再现其功能权限
	           */
	          if(sysPrivBo.isHaveTheFunction(node.getAttributeValue("id")))
	          {
	              strFunc.append(" checked");
	          }
	          strFunc.append(">");	          
	          
	          strFunc.append(node.getAttributeValue("name"));
	          strFunc.append("</span>");
	          //++level;
	          strFunc.append(searchSubFunctionXmlHtml(level,node));
	          strFunc.append("</div>");	          
        }
        return strFunc.toString();
    }
    
    /**
     * 是否有业务平台
     * @return
     */
    private boolean isHaveBusiDesk(int module_id)
    {
    	return this.lock.isBmodule(module_id,this.userView.getUserName());    	
    }
    /**
     * 过滤功能号 
     * @param id
     * @param map
     * @return
     */
    private boolean isFiltrate(String id,HashMap map)
    {
    	boolean isCorrect=false;
    	if(map!=null)
    	{
    		String filtrateid=(String)map.get(id);
    		if(filtrateid!=null&&filtrateid.length()>0)
    		{
    			isCorrect=true;
    		}	
    	}
    	return isCorrect;
    }
    /**
     * 返回需要过滤的idMap
     * @param inputchinfor
     * @param approveflag
     * @return
     */
    private HashMap filtrateId(String inputchinfor,String approveflag)
    {
    	HashMap hashMap=new HashMap();
    	if("1".equals(inputchinfor)&& "1".equals(approveflag))
    	{
    		hashMap.put("01030115", "01030115");//整体报批
    		hashMap.put("03084", "03084");//整体批准
    		hashMap.put("03083", "03083");//整体驳回
    		hashMap.put("260633", "260633");//批准
    		hashMap.put("260634", "260634");//整体驳回
    	}else
    	{
    		hashMap.put("01030106", "01030106");//我的变动信息明细
    		hashMap.put("03085", "03085");//删除    		
    		hashMap.put("260635", "260635");//删除 
    	}
    	return hashMap;
    }
    /**
     * 客户定制的个性化需求,有的用户就不输出啦
     * @param funcid
     * @return
     */
    private boolean haveCustomFunc(String funcid)
    {
    	String cfunc=",080804,30044,";
    	boolean isldap=SystemConfig.isLdap();
    	if(cfunc.indexOf(","+funcid+",")==-1)
    		return true;
    	if(cfunc.indexOf(","+funcid+",")!=-1&&isldap)
    		return true;
    	return false;
    }    
    /**
     * 是否输出功能列表
     * @param func_id
     * @return
     */
    private boolean isMayOut(String func_id)
    {
    	boolean bflag=true;
        if(("2".equals(func_id)&&(!isHaveBusiDesk(11))))
        	bflag=false;
        if(("03".equals(func_id)&&(!(isHaveBusiDesk(0)||isHaveBusiDesk(1)))))
        	bflag=false;
        if(("05".equals(func_id)&&(!(isHaveBusiDesk(0)||isHaveBusiDesk(1)))))
        	bflag=false;
        if(("04".equals(func_id)&&(!(isHaveBusiDesk(0)||isHaveBusiDesk(1)))))
        	bflag=false;
        if(("06".equals(func_id)&&(!isHaveBusiDesk(3))))
        	bflag=false;
        if(("09".equals(func_id)&&(!isHaveBusiDesk(2))))
        	bflag=false;
        if(("0A".equals(func_id)&&(!isHaveBusiDesk(4))))
        	bflag=false;
        if(("0B".equals(func_id)&&(!isHaveBusiDesk(5))))
        {
        	bflag=false;
        }
        if(("07".equals(func_id)&&(!isHaveBusiDesk(0))))
        	bflag=false;
        if(("08".equals(func_id)&&(!(isHaveBusiDesk(0)||isHaveBusiDesk(1)))))
        	bflag=false;
        if(("11".equals(func_id)&&(!isHaveBusiDesk(0))))
        	bflag=false;   
        if(("230".equals(func_id)&&(!isHaveBusiDesk(11))))
        	bflag=false;   
        if(("250".equals(func_id)&&(!isHaveBusiDesk(11))))
        	bflag=false;  
        if(("260".equals(func_id)&&(!isHaveBusiDesk(11))))
        	bflag=false; 
        if(("290".equals(func_id)&&(!isHaveBusiDesk(11))))
        	bflag=false;         
        if(("270".equals(func_id)&&(!isHaveBusiDesk(6))))
        	bflag=false;   
        if(("240".equals(func_id)&&(!isHaveBusiDesk(7))))
        	bflag=false;  
        if(("280".equals(func_id)&&(!isHaveBusiDesk(12))))
        	bflag=false;   
        bflag=haveCustomFunc(func_id);
    	return bflag;
    }
    /**从功能授权配置的文件取得所有的功能编码*/
    private String searchFunctionXmlHtml()throws GeneralException
    {
        StringBuffer strFunc=new StringBuffer();
        InputStream in=this.getClass().getResourceAsStream("/com/hjsj/hrms/constant/function.xml");
        try
        {
	        Document doc = PubFunc.generateDom(in);
	        cat.debug("Function's Document successfully readed");
	        Element root = doc.getRootElement();
	        List list = root.getChildren("function");
	        /**版本之间的差异控制，市场考滤*/
	        VersionControl ver_ctrl=new VersionControl();
	        ver_ctrl.setVer(this.lock.getVersion());
	        
	        for (int i = 0; i < list.size(); i++)
	        {
	          Element node = (Element) list.get(i);
	          String func_id=node.getAttributeValue("id");
	          if(!isMayOut(func_id))
	        	  continue;
	          /**版本控制*/
	          if(!ver_ctrl.searchFunctionId(node.getAttributeValue("id")))
	        	  continue;
	          /**
	           * 支持分布式授权机制
	           */
	          if(!userView.hasTheFunction(node.getAttributeValue("id")))
	              continue;
	          strFunc.append("<div id='");
	          strFunc.append("div");
	          strFunc.append(node.getAttributeValue("id"));
	          strFunc.append("'>");
	          strFunc.append("<span style='cursor:hand' title='单击展开菜单' onclick=show('");
	          strFunc.append("div");
	          strFunc.append(node.getAttributeValue("id"));
	          strFunc.append("')>");
	          strFunc.append("<input type='checkbox' name='func' value='");
	          strFunc.append(node.getAttributeValue("id"));
	          strFunc.append("' id='input");
	          strFunc.append(node.getAttributeValue("id"));
	          strFunc.append("' onclick=setvalue('");
	          strFunc.append("div");
	          strFunc.append(node.getAttributeValue("id"));
	          strFunc.append("','input");
	          strFunc.append(node.getAttributeValue("id"));
	          strFunc.append("') ");
	          if(sysPrivBo.isHaveTheFunction(node.getAttributeValue("id")))
	          {
	              strFunc.append(" checked");
	          }
	          strFunc.append(">");
	          
	          strFunc.append(node.getAttributeValue("name"));
	          strFunc.append("</span>");
	          strFunc.append(searchSubFunctionXmlHtml(1,node));
	          strFunc.append("</div>");
	        } //for i loop end.
        }
        catch(Exception ee)
        {
            throw GeneralExceptionHandler.Handle(ee);
        }
        finally
        {
            
        }
        return strFunc.toString();
    }
    /**
     * 生成报表授权前台界面
     * @return
     * @throws GeneralException
     */
    private String searchReportHtml()throws GeneralException
    {
        StringBuffer db_str=new StringBuffer();
        StringBuffer strsql=new StringBuffer();;
        strsql.append("select tsortid,name from tsort");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
	    try
	    {
	      this.frowset = dao.search(strsql.toString());
	      while(this.frowset.next())
	      {
	          /**
	           * 支持分布式授权机制
	           */
	          if(!userView.hasTheReport(this.frowset.getString("tsortid")))
	              continue;
	          db_str.append("<div id='");
	          db_str.append("div");
	          db_str.append(this.frowset.getString("tsortid"));
	          db_str.append("'>");
	          db_str.append("<span>");
	          db_str.append("<input type='checkbox' name='func' value='");
	          db_str.append(this.frowset.getString("tsortid"));
	          db_str.append("' id='input");
	          db_str.append(this.frowset.getString("tsortid"));
	          db_str.append("','input");
	          db_str.append(this.frowset.getString("tsortid"));
	          db_str.append("'");
	          if(sysPrivBo.isHaveTheReportSort(this.frowset.getString("tsortid")))
	          {
	              db_str.append(" checked");
	          }
	          db_str.append(">");
	          db_str.append(this.frowset.getString("name"));
	          db_str.append("</span>");
	          db_str.append("</div>");
	      }
	      return db_str.toString();	      
	    }
	    catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
    }    
    /**
     * 生成前台选择多媒体界面
     * @return
     * @throws GeneralException
     */
    private String searchMediaHtml()throws GeneralException
    {
        StringBuffer db_str=new StringBuffer();
        StringBuffer strsql=new StringBuffer();;
        strsql.append("select id,flag,sortname from mediasort");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
	    try
	    {
	      this.frowset = dao.search(strsql.toString());
	      while(this.frowset.next())
	      {
	          /**
	           * 支持分布式授权机制
	           */
	          if(!userView.hasTheMediaSet(this.frowset.getString("flag")))
	              continue;
	          db_str.append("<div id='");
	          db_str.append("div");
	          db_str.append(this.frowset.getString("flag"));
	          db_str.append("'>");
	          db_str.append("<span>");
	          db_str.append("<input type='checkbox' name='func' value='");
	          db_str.append(this.frowset.getString("flag"));
	          db_str.append("' id='input");
	          db_str.append(this.frowset.getString("flag"));
	          db_str.append("','input");
	          db_str.append(this.frowset.getString("flag"));
	          db_str.append("'");
	          if(sysPrivBo.isHaveTheMedia(this.frowset.getString("flag")))
	          {
	              db_str.append(" checked");
	          }
	          db_str.append(">");
	          db_str.append(this.frowset.getString("sortname"));
	          db_str.append("</span>");
	          db_str.append("</div>");
	      }
	      return db_str.toString();	      
	    }
	    catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
    }
    /**
     * 生成选库前台界面
     * @return
     * @throws GeneralException
     */
    private String searchDbNameHtml()throws GeneralException
    {
        StringBuffer db_str=new StringBuffer();
        StringBuffer strsql=new StringBuffer();;
        strsql.append("select dbid,dbname,pre from dbname");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
	    try
	    {
	      this.frowset = dao.search(strsql.toString());

	      while(this.frowset.next())
	      {
	          /**
	           * 支持分布式授权机制
	           */
	          if(!userView.hasTheDbName(this.frowset.getString("pre")))
	              continue;
	          db_str.append("<div id='");
	          db_str.append("div");
	          db_str.append(this.frowset.getString("dbid"));
	          db_str.append("'>");
	          db_str.append("<span>");
	          db_str.append("<input type='checkbox' name='func' value='");
	          db_str.append(this.frowset.getString("pre"));
	          db_str.append("' id='input");
	          db_str.append(this.frowset.getString("dbid"));
	          db_str.append("','input");
	          db_str.append(this.frowset.getString("dbid"));
	          db_str.append("'");
	          if(sysPrivBo.isHaveTheDb(this.frowset.getString("pre")))
	          {
	              db_str.append(" checked");
	          }
	          db_str.append(">");
	          db_str.append(this.frowset.getString("dbname"));
	          db_str.append("</span>");
	          db_str.append("</div>");
	      }
	      return db_str.toString();	      
	    }
	    catch(SQLException sqle)
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
        str_header.append("<table width='80%'  align='center' border='0' cellpadding='0' cellspacing='0' class='ListTable'>");
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
     * 读，写权限
     * @param setid
     * @param setdesc
     * @param flag
     * @return
     */
    private String addTableRow(String setid,String setdesc,String flag,String havePriv)
    {

        StringBuffer str_row=new StringBuffer();
        str_row.append("<tr>");
        str_row.append("<td align='right' class='RecordRow' nowrap>");
        str_row.append(setdesc);
        str_row.append("</td>");
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
	      this.frowset = dao.search(strsql.toString());
	      table_str.append(addTableHeader(GeneralConstant.FIELD_SET));
	      while(this.frowset.next())
	      {
	          /**
	           * 支持分布式授权机制
	           */
	          flag=userView.analyseTablePriv(this.frowset.getString("fieldsetid"));
	          if("0".equals(flag))
	              continue;
	          /**
	           * 现拥有的权限
	           */
	          havePriv=sysPrivBo.analyseTablePriv(this.frowset.getString("fieldsetid"));
	          
	          table_str.append(addTableRow(this.frowset.getString("fieldsetid"),this.frowset.getString("customdesc"),flag,havePriv));
	      }
	      /**
	       * 多媒体子集
	       */
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
	          table_str.append(addTableRow("K00","职位多媒体子集",flag,havePriv));
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
    private String addFieldDomain(String fieldsetid)throws GeneralException
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
	      domain_str.append(addTableHeader(GeneralConstant.Field_LABLE));
	      flag=sysPrivBo.analyseTablePriv(fieldsetid);	      
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
	      /*单位主集B0110、职位主集中E01A1权限参考人员主集中的B110及E01A1的权限
	      if(fieldsetid.equals("B01"))
	      {
	          if(!flag.equals("0"))
	          {    
	        	  flag=userView.analyseFieldPriv("B0110");		        	  
		          havePriv=sysPrivBo.analyseFieldPriv("B0110");
		          domain_str.append(addTableRow("B0110",ResourceFactory.getProperty(GeneralConstant.B0110),flag,havePriv));
	          }	          
	      }
	      if(fieldsetid.equals("K01"))
	      {
	          if(!flag.equals("0"))
	          {    
	        	  flag=userView.analyseFieldPriv("E01A1");		  
		          havePriv=sysPrivBo.analyseFieldPriv("E01A1");
		          domain_str.append(addTableRow("E01A1",ResourceFactory.getProperty(GeneralConstant.E01A1),flag,havePriv));
	          }        
	      }	 
	      */     
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
	      this.frowset = dao.search(strsql.toString());
	      while(this.frowset.next())
	      {
	          /**支持分布式授权机制*/
	          flag=userView.analyseTablePriv(this.frowset.getString("fieldsetid"));
	          if("0".equals(flag))
	              continue;
	          if("0".equals(sysPrivBo.analyseTablePriv(this.frowset.getString("fieldsetid"))))
	              continue;
	          field_str.append("<div id='");
	          field_str.append("div");
	          field_str.append(this.frowset.getString("fieldsetid"));
	          field_str.append("'>");
	          
	          field_str.append("<span style='cursor:hand' title='单击展开子集' onclick=show('");
	          field_str.append("div");
	          field_str.append(this.frowset.getString("fieldsetid"));
	          field_str.append("')>");
	          field_str.append("<img src='/images/table.gif' border=0>");
	          field_str.append(this.frowset.getString("customdesc"));
	          field_str.append("</span>");
	          
	          field_str.append(addFieldDomain(this.frowset.getString("fieldsetid")));
	          
	          field_str.append("</div>");
	      }

	      return field_str.toString();	      
	    }
	    catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
    }
    
   private String searchChildOrganization(int level,String parentid)throws GeneralException
   {
       StringBuffer strPre=new StringBuffer();
       for(int i=0;i<level;i++)
           strPre.append("&nbsp;&nbsp;");       
       StringBuffer manage_str=new StringBuffer();
       StringBuffer strsql=new StringBuffer();
       strsql.append("select codesetid,codeitemid,codeitemdesc from organization where codeitemid<>parentid and parentid='");
       strsql.append(parentid);
       strsql.append("'");
       ResultSet rset=null;
       ContentDAO dao=new ContentDAO(this.getFrameconn());
	    try
	    {
	      rset = dao.search(strsql.toString());
	      while(rset.next())
	      {
	          manage_str.append("<div style='display:none' id='");
	          manage_str.append("div");
	          manage_str.append(rset.getString("codeitemid"));
	          manage_str.append("'>");
	          manage_str.append("<span style='cursor:hand' onDblClick=show('");
	          manage_str.append("div");
	          manage_str.append(rset.getString("codeitemid"));
	          manage_str.append("')>");
	          manage_str.append("<input type='radio' name='org' value='");
	          manage_str.append(rset.getString("codesetid")+rset.getString("codeitemid"));
	          manage_str.append("' id='input");
	          manage_str.append(rset.getString("codeitemid"));
	          manage_str.append("' ");

	          if(sysPrivBo.isEqualManagePriv(rset.getString("codesetid")+rset.getString("codeitemid")))
	          {
	              manage_str.append(" checked");
	          }
	          manage_str.append(">");
	          manage_str.append(strPre.toString());
	          if("UN".equals(rset.getString("codesetid")))
	              manage_str.append("<img src='/images/unit.gif' border=0>");
	          if("UM".equals(rset.getString("codesetid")))
	              manage_str.append("<img src='/images/dept.gif' border=0>");
	          if("@K".equals(rset.getString("codesetid")))
	              manage_str.append("<img src='/images/pos_l.gif' border=0>");
	          
	          manage_str.append(rset.getString("codeitemdesc"));
	          manage_str.append("</span>");
	          ++level;
	          manage_str.append(searchChildOrganization(level,rset.getString("codeitemid")));
	          
	          manage_str.append("</div>");
	          
	      }

	      return manage_str.toString();	      
	    }
	    catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
       
   }
    /**
     * 生成组织机构树
     * @return
     * @throws GeneralException
     */
    private String searchManagePriv()throws GeneralException
    {
        StringBuffer manage_str=new StringBuffer();
        StringBuffer strsql=new StringBuffer();
        strsql.append("select codesetid,codeitemid,codeitemdesc from organization where codeitemid=parentid");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
	    try
	    {
	      this.frowset = dao.search(strsql.toString());
	      while(this.frowset.next())
	      {
	          manage_str.append("<div id='");
	          manage_str.append("div");
	          manage_str.append(this.frowset.getString("codeitemid"));
	          manage_str.append("'>");
	          manage_str.append("<span style='cursor:hand' onDblClick=show('");
	          manage_str.append("div");
	          manage_str.append(this.frowset.getString("codeitemid"));
	          manage_str.append("')>");
	          manage_str.append("<input type='radio' name='org' value='");
	          manage_str.append(this.frowset.getString("codesetid")+this.frowset.getString("codeitemid"));
	          manage_str.append("' id='input");
	          manage_str.append(this.frowset.getString("codeitemid"));
	          manage_str.append("' ");

	          if(sysPrivBo.isEqualManagePriv(this.frowset.getString("codesetid")+this.frowset.getString("codeitemid")))
	          {
	              manage_str.append(" checked");
	          }
	          manage_str.append(">");
	          if("UN".equals(this.frowset.getString("codesetid")))
	              manage_str.append("<img src='/images/unit.gif' border=0>");
	          if("UM".equals(this.frowset.getString("codesetid")))
	              manage_str.append("<img src='/images/dept.gif' border=0>");
	          if("@K".equals(this.frowset.getString("codesetid")))
	              manage_str.append("<img src='/images/pos.gif' border=0>");
	          
	          manage_str.append(this.frowset.getString("codeitemdesc"));
	          manage_str.append("</span>");

	          manage_str.append(searchChildOrganization(1,this.frowset.getString("codeitemid")));
	          
	          manage_str.append("</div>");
	          
	      }

	      return manage_str.toString();	      
	    }
	    catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
        
    }
    /**员工00000002，部门领导00000003，单位领导00000004，集团领导00000005*/
    private String getViewflag(String flag,String role_id)
    {
    	String roleproperty="0";
    	if(flag.equals(GeneralConstant.ROLE)/*&&(role_id.equals("00000002")||role_id.equals("00000003")||role_id.equals("00000004")||role_id.equals("00000005"))*/)
    	{
    		try
    		{
	    		ContentDAO dao=new ContentDAO(this.getFrameconn());
	    		RecordVo  vo=new RecordVo("t_sys_role");
	    		vo.setString("role_id",role_id);
	    		vo=dao.findByPrimaryKey(vo);
	    		roleproperty=vo.getString("role_property");
    		}
    		catch(Exception ee)
    		{
    			ee.printStackTrace();
    		}
    		if("1".equals(roleproperty)|| "5".equals(roleproperty)|| "6".equals(roleproperty)|| "7".equals(roleproperty))
    			return "1";
    		else
    			return "0";    		
    	}
    	else
    		return "0";
    }
    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");         
        String	tab_name=(String)hm.get("a_tab");
        //String  role_id=(String)hm.get("a_roleid");
        String role_id=(String)this.getFormHM().get("role_id");
        String flag=(String)hm.get("a_flag");
        this.lock=(EncryptLockClient)this.getFormHM().get("lock");
        if(tab_name==null|| "".equals(tab_name))
            return;
        if(role_id==null|| "".equals(role_id))
            return;
        String str="";
        if(flag==null|| "".equals(flag))
            flag=GeneralConstant.ROLE;

        try
        {
            //role_id=PubFunc.ToGbCode(role_id);
	        /**
	         * 功能授权
	         */
	        if("funcpriv".equals(tab_name))
	        {
		        sysPrivBo=new SysPrivBo(role_id,flag,this.getFrameconn(),"functionpriv");	        	
	            str=searchFunctionXmlHtml();//searchFunctionHmtl();
	            cat.debug("script_str="+str);
	            //this.getFormHM().put("script_str",str);
	        }
	        /**
	         * 人员库授权
	         */
	
	        if("dbpriv".equals(tab_name))
	        {
		        sysPrivBo=new SysPrivBo(role_id,flag,this.getFrameconn(),"dbpriv");	        	
	            str=searchDbNameHtml();
	            cat.debug("db_script_str="+str);
	        }
	       
	        /**
	         * 子集授权
	         */
	        if("tablepriv".equals(tab_name))
	        {
		        sysPrivBo=new SysPrivBo(role_id,flag,this.getFrameconn(),"tablepriv");	        	
	            str=searchTablePriv();
	            cat.debug("table_priv="+str);
	        }
	        /**
	         * 指标授权
	         */
	        if("fieldpriv".equals(tab_name))
	        {
		       sysPrivBo=new SysPrivBo(role_id,flag,this.getFrameconn(),"fieldpriv");	        	
	           str=searchFieldPriv();
	           cat.debug("field_priv="+str);
	        }
	        /**
	         * 管理范围
	         */
	        if("managepriv".equals(tab_name))
	        {
		       sysPrivBo=new SysPrivBo(role_id,flag,this.getFrameconn(),"managepriv");	        	
	           //str=searchManagePriv();
	           str=sysPrivBo.getManage_str();
	           cat.debug("manage_priv="+str);           
	        }
	        /**多媒体授权信息*/
	        if("mediapriv".equals(tab_name))
	        {
		       sysPrivBo=new SysPrivBo(role_id,flag,this.getFrameconn(),"mediapriv");	        	
	           str=searchMediaHtml();
	           cat.debug("media_priv="+str);               
	        }
	        if("reportpriv".equals(tab_name))
	        {
		       sysPrivBo=new SysPrivBo(role_id,flag,this.getFrameconn(),"reportsortpriv");	        	
	           str=searchReportHtml();
	           cat.debug("report_priv="+str);              
	        }
        }
        catch(Exception ee)
        {
        	ee.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ee);
        }
        /**
         * save the role_id.
         */
        this.getFormHM().put("script_str",str);         
        this.getFormHM().put("role_id",role_id);
        this.getFormHM().put("tab_name",tab_name);
        this.getFormHM().put("user_flag",flag);
        this.getFormHM().put("manage",str);
        this.getFormHM().put("viewflag",getViewflag(flag,role_id));
    }

}
