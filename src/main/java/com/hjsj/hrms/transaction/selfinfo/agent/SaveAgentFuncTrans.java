package com.hjsj.hrms.transaction.selfinfo.agent;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SaveAgentFuncTrans extends IBusiness {

	public void execute() throws GeneralException {
        String id=(String)this.getFormHM().get("id");
        String func_str=(String)this.getFormHM().get("selstr");
    	String imme=(String)this.getFormHM().get("imme");    	
    	if("1".equalsIgnoreCase(imme))
    	{
    		String checked=(String)this.getFormHM().get("checked");    		
    		func_str=getAllChildFuncId(func_str);
    		ImmeSaveFunctionPriv(id,func_str,checked);
    		
    	}
    	else
    		saveFunctionPriv(id,func_str);
	 }
	 private void saveFunctionPriv(String id,String func_str) throws GeneralException
	 {

            RecordVo vo=new RecordVo("t_sys_function_priv",1);
	        vo.setString("id",id); 
	       	vo.setString("functionpriv",func_str);	        
	        try
		    {
		    	ArrayList paralist=new ArrayList();
		    	ContentDAO dao=new ContentDAO(this.getFrameconn());
		    	dao.updateValueObject(vo);
		    	           
		    }
		    catch(Exception  ex)
		    {
		    	  throw GeneralExceptionHandler.Handle(new GeneralException("没有找到对应的代理用户记录！"));
		    } 
	 }
	 private void ImmeSaveFunctionPriv(String id,String func_str,String checked)throws GeneralException  {
	    	/*GeneralConstant.ROLE*/
	          RecordVo vo=new RecordVo("agent_set",1);
	          vo.setString("id",id);	          
	       	  StringBuffer strsql=new StringBuffer();
		      strsql.append("select id,functionpriv from agent_set where id=");
		      strsql.append(id);		     
		      try
		      {
		    	ArrayList paralist=new ArrayList();
		    	ContentDAO dao=new ContentDAO(this.getFrameconn());
		    	this.frowset=dao.search(strsql.toString());		    	
		    	if(this.frowset.next())
		    	{
		    		String usedstr=Sql_switcher.readMemo(this.frowset, "functionpriv");
		    		String tmp=commfunstr(func_str,usedstr,checked);
		         	vo.setString("functionpriv",tmp);	  
		         	dao.updateValueObject(vo);
		    	}else{
		    		throw GeneralExceptionHandler.Handle(new GeneralException("没有找到对应的代理用户记录！"));
		    	}	           
		      }
		      catch(Exception  ex)
		      {
		    	  ex.printStackTrace();
		      }        
	    }    
	/**
     * 功能树异步加载时，下级树节点取不到
     * @param func_str
     * @return
     */
    private String getAllChildFuncId(String func_str)
    {
    	StringBuffer buf=new StringBuffer();
    	String[] funcarr=StringUtils.split(func_str, ',');
    	for(int i=0;i<funcarr.length;i++)
    	{
    		String func=funcarr[i];
    		buf.append(",");      		
    		buf.append(func);
    		buf.append(",");    		
    		buf.append(getChildFuncId(func));
    	}//for i loop end.
    	return buf.toString();
    }
    /**
     * 取得当前节点下有权限功能节点
     * @param func_id
     * @return
     */
    private String getChildFuncId(String curr_id)
    {
    	StringBuffer buf=new StringBuffer();
        InputStream in=this.getClass().getResourceAsStream("/com/hjsj/hrms/constant/function.xml");
        VersionControl ver_ctrl=new VersionControl();
        try
        {
	        Document doc = PubFunc.generateDom(in);
	        List list=null;
        	String xpath = "//function[@id=\"" + curr_id + "\"]";
        	XPath xpath_ = XPath.newInstance(xpath);
        	Element ele = (Element) xpath_.selectSingleNode(doc);
        	list = ele.getChildren("function");
	        for (int i = 0; i < list.size(); i++)
	        {
	          Element node = (Element) list.get(i);
	          String func_id=node.getAttributeValue("id");
	          /**
	           * 支持分布式授权机制
	           */	          
	          if(!userView.hasTheFunction(node.getAttributeValue("id")))
	              continue;
	          if(!ver_ctrl.searchFunctionId(node.getAttributeValue("id")))
	        	  continue;
	          if(curr_id!=null&& "0".equals(curr_id))
	           {
	        	  if(func_id!=null&&("06".equals(func_id)|| "0C".equals(func_id)))//绩效考评，部门考勤
		           {
	        		  if(!haveTheFunc(buf.toString(),node.getAttributeValue("id")))
	    	          {
	    		          buf.append(func_id);	 
	    		          buf.append(",");		          
	    	          }
	    	          buf.append(getChildFuncId(func_id));
		           }
	           }else if(curr_id!=null&&("06".equals(curr_id)|| "0C".equals(curr_id)))
	           {
	        	    
	        		  if(!haveTheFunc(buf.toString(),node.getAttributeValue("id")))
	    	          {
	    		          buf.append(func_id);	 
	    		          buf.append(",");		          
	    	          }
	    	          buf.append(getChildFuncId(func_id));
	           }else
	           {
	        	      if(!haveTheFunc(buf.toString(),node.getAttributeValue("id")))
	    	          {
	    		          buf.append(func_id);	 
	    		          buf.append(",");		          
	    	          }
	    	          buf.append(getChildFuncId(func_id));
	           }
	          
	        } //for i loop end.
	        
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }finally {
        	PubFunc.closeIoResource(in);
        }
    	return buf.toString();
    	
    }
    private boolean haveTheFunc(String func_str,String func_id)
    {
    	if(func_str.indexOf(","+func_id+",")==-1)
    		return false;
    	else
    		return true;
    }   
    /**
     * 组装功能串
     * @param func_str  新加或新减功能号
     * @param srcstr    原串
     * @param checked   =1 新增功能号标识 =0取消的功能号标识
     * @return
     */
    private String commfunstr(String func_str,String srcstr,String checked)
    {
    	String[] funcarr=StringUtils.split(func_str, ',');
    	StringBuffer bufa=new StringBuffer();
    	StringBuffer srcbuff=new StringBuffer();
    	srcbuff.append(srcstr);
    	int idx=0;
    	for(int i=0;i<funcarr.length;i++)
    	{
    		bufa.setLength(0);
    		bufa.append(",");
    		bufa.append(funcarr[i]);
    		bufa.append(",");
    		String tmp=bufa.toString();
    		idx=srcbuff.indexOf(tmp);
    		if("1".equalsIgnoreCase(checked))//新增功能号
    		{
    			if(idx==-1)//原授权的功能串找不到，则追加进去
    			{
    				srcbuff.append(bufa.toString());
    			}
    			
    		}
    		else
    		{
    			if(idx!=-1)//原授权的功能串能找到，则删除掉
    			{
    				srcbuff.replace(idx, idx+tmp.length(), ",");
    			}
    		}
    	}
    	return srcbuff.toString();
    }
}
