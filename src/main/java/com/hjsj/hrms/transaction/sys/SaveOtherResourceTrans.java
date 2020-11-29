/**
 * 
 */
package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hjsj.hrms.transaction.lawbase.SaveLawResourceTrans;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceParser;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 * <p>Title:</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Jul 24, 20062:36:33 PM
 * @author chenmengqing
 * @version 4.0
 */
public class SaveOtherResourceTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String flag=(String)this.getFormHM().get("flag");
			String roleid=(String)this.getFormHM().get("roleid");
			String res_flag=(String)this.getFormHM().get("res_flag");
			if(flag==null|| "".equals(flag))
	            flag=GeneralConstant.ROLE;
			if(res_flag==null|| "".equals(res_flag))
				res_flag="0";
			/**资源类型*/
			int res_type=Integer.parseInt(res_flag);
			/**采用预警字段作为其资源控制字段*/
			/**当前被授权用户拥有的资源*/
			SysPrivBo privbo=new SysPrivBo(roleid,flag,this.getFrameconn(),"warnpriv");
			String res_str=privbo.getWarn_str();
			ResourceParser parser=new ResourceParser(res_str,res_type);
	        Document doc = null;
			StringBuffer strxml = new StringBuffer();
			String gz_set = "";
			String ins_set = "";
			if(res_type==12||res_type==18){//薪资类别与保险类别 添加查询后勾选权限保存逻辑不适用 此修改只针对薪资和保险 add hej 2015.7.17
			try
			{
				strxml.append("<?xml version='1.0' encoding='GB2312'?>");
	            strxml.append("<resource>");
	            strxml.append(res_str);
	            strxml.append("</resource>");
	            doc = PubFunc.generateDom(strxml.toString());
	            if(res_type==12){
	            	 XPath xPath = XPath.newInstance("/resource/gz_set");
	            	 List list=xPath.selectNodes(doc);	
	            	 Element element = null;
	            	  if(list!=null&&list.size()>0)
					  {
	            		  element = (Element)list.get(0); 
	            		  if(element!=null)
	            			  gz_set = ","+element.getText()+",";	
					  }
	            }else{
	            	XPath xPath = XPath.newInstance("/resource/ins_set");
	            	 List list=xPath.selectNodes(doc);	
	            	 Element element = null;
	            	  if(list!=null&&list.size()>0)
					  {
	            		  element = (Element)list.get(0); 
	            		  if(element!=null)
	            			  ins_set = ","+element.getText()+",";	
					  }
	            }
			}catch(Exception e)
			{
			  e.printStackTrace();	
			}     
			//String aaa = parser.getContent();
			ArrayList list=(ArrayList)this.getFormHM().get("alllist");
			if(list==null||list.size()==0)
				return;
			StringBuffer str_value=new StringBuffer();
			for(int i=0;i<list.size();i++)
			{
                DynaBean dbean=(LazyDynaBean)list.get(i);
                String flag0=(String)dbean.get("c0");
                String tabid = (String)dbean.get("tabid");
                if("0".equals(flag0)){
                	if(res_type==12){
                		if((gz_set).contains(","+tabid+",")){
                				 int index = gz_set.indexOf(tabid);
                        		 String begz_set = gz_set.substring(0, index);
                        		 String engz_set = gz_set.substring(index, gz_set.length()); 
                        		 int index1=engz_set.indexOf(",");
                        		 if(index1==engz_set.length()-1){
                        			 gz_set=begz_set;
                        		 }else{
                        			 String ends=engz_set.substring(index1+1, engz_set.length());
                        			 gz_set=begz_set+ends;
                        		 }
                		} 
                	}else{
                		if((ins_set).contains(","+tabid+",")){
               				 int index = ins_set.indexOf(tabid);
                    		 String beins_set = ins_set.substring(0, index);
                    		 String enins_set = ins_set.substring(index, ins_set.length()); 
                    		 int index1=beins_set.indexOf(",");
                    		 if(index1==enins_set.length()-1){
                    			 ins_set=beins_set;
                    		 }else{
                    			 String ends=enins_set.substring(index1+1, enins_set.length());
                    			 ins_set=beins_set+ends;
                    		 } 
                		}
                  }	
                }
                else if("1".equals(flag0))
                {
                	if(res_type==12){
                		if((gz_set).contains(","+tabid+",")){
                    		continue;
                    	}else{
                    		gz_set = gz_set +tabid +",";
                    	}
                	}else{
                		if((ins_set).contains(","+tabid+",")){
                    		continue;
                    	}else{
                    		ins_set = ins_set +tabid +",";
                    	}
                	}
                	
                }
			}
			if(res_type==12){
				if(gz_set.length()!=0)
					gz_set = gz_set.substring(0, gz_set.length());
				parser.reSetContent(gz_set);
			}
			else{
				if(ins_set.length()!=0)
					ins_set = ins_set.substring(0, ins_set.length());
				parser.reSetContent(ins_set);
			}
			}else{
			//String aaa = parser.getContent();
			ArrayList list=(ArrayList)this.getFormHM().get("alllist");
			if(list==null||list.size()==0)
				return;
			StringBuffer str_value=new StringBuffer();
			for(int i=0;i<list.size();i++)
			{
                DynaBean dbean=(LazyDynaBean)list.get(i);
                String flag0=(String)dbean.get("c0");
                if("1".equals(flag0))
                {
                	str_value.append((String)dbean.get("tabid"));
                	str_value.append(",");
                }
			}
			if(str_value.length()!=0)
				str_value.setLength(str_value.length()-1);
			//parser.addContent(str_value.toString());
			parser.reSetContent(str_value.toString());
			}
			res_str=parser.outResourceContent();
			saveResourceString(roleid,flag,res_str);
			
			SaveLawResourceTrans slrt = new SaveLawResourceTrans();
			this.getFormHM().put("@eventlog", slrt.getEventLog(roleid, flag, res_type));
			//保存操作成功，修改opt    jingq  add    2014.5.7
			String opt = "1";
	    	this.getFormHM().put("opt", opt);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

    private void saveResourceString(String role_id,String flag,String res_str)
    {
        if(res_str==null)
        	res_str="";
        /*
        RecordVo vo=new RecordVo("t_sys_function_priv",1);
        vo.setString("id",role_id);
        vo.setString("status",flag);
        vo.setString("warnpriv",res_str);
        cat.debug("role_vo="+vo.toString());	
        SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
        sysbo.save(); 
        */
	      StringBuffer strsql=new StringBuffer();
	      strsql.append("select id from t_sys_function_priv where id='");
	      strsql.append(role_id);
	      strsql.append("' and status=");
	      strsql.append(flag);
	      try
	      {
	    	ArrayList paralist=new ArrayList();
	    	ContentDAO dao=new ContentDAO(this.getFrameconn());
	    	this.frowset=dao.search(strsql.toString());
	    	cat.debug("select sql="+strsql.toString());	

	    	if(this.frowset.next())
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
	    	cat.debug("updat warnpriv sql="+strsql.toString());
	    	dao.update(strsql.toString(),paralist);
	      }
	      catch(SQLException sqle)
	      {
	    	  sqle.printStackTrace();
	      }
    }	
}
