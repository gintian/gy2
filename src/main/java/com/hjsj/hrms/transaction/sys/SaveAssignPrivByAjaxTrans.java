/**
 * 
 */
package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title:保存授的功能权限、人员库、多媒体分类</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 31, 2008:8:41:18 AM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SaveAssignPrivByAjaxTrans extends IBusiness {

    /**
     * 用户标识
     */
    private String userflag=GeneralConstant.ROLE;
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
    
    private void ImmeSaveFunctionPriv(String role_id,String func_str,String checked) {
    	/*GeneralConstant.ROLE*/
          RecordVo vo=new RecordVo("t_sys_function_priv",1);
          vo.setString("id",role_id);
          vo.setString("status",this.userflag);
       	  vo.setString("functionpriv",func_str);
        
          cat.debug("role_vo="+vo.toString());	
	      StringBuffer strsql=new StringBuffer();
	      strsql.append("select id,functionpriv from t_sys_function_priv where id='");
	      strsql.append(role_id);
	      strsql.append("' and status=");
	      strsql.append(this.userflag);
	      try
	      {
	    	ArrayList paralist=new ArrayList();
	    	ContentDAO dao=new ContentDAO(this.getFrameconn());
	    	this.frowset=dao.search(strsql.toString());
	    	cat.debug("select sql="+strsql.toString());	
	        SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
	    	if(this.frowset.next())
	    	{
	    		String usedstr=Sql_switcher.readMemo(this.frowset, "functionpriv");
	    		String tmp=commfunstr(func_str,usedstr,checked);
	         	vo.setString("functionpriv",tmp);	    		
	    	}
            sysbo.save();
	      }
	      catch(Exception  ex)
	      {
	    	  ex.printStackTrace();
	      }        
    }    
    /**
     * @param role_id
     */
    private void saveFunctionPriv(String role_id,String func_str) {
    	/*GeneralConstant.ROLE*/
    	
        RecordVo vo=new RecordVo("t_sys_function_priv",1);
        vo.setString("id",role_id);
        vo.setString("status",this.userflag);
//        if(Sql_switcher.searchDbServer() == Constant.ORACEL)
//        {
//    		OracleBlobUtils blobutils = new OracleBlobUtils(this.getFrameconn());
//    		blobutils.modifyClob("t_sys_function_priv","functionpriv",func_str," id='"+role_id+"' and status="+this.userflag);
//    		return;
//        }
//        else
       	vo.setString("functionpriv",func_str);
        
        cat.debug("role_vo="+vo.toString());	
        
        SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
        sysbo.save();
        //savePrivString(role_id,this.userflag,func_str,"functionpriv");
    }
    
    private void saveDbPriv(String role_id,String dbstr) {
        RecordVo vo=new RecordVo("t_sys_function_priv");
        vo.setString("id",role_id);
        vo.setString("status",this.userflag/*GeneralConstant.ROLE*/);        
        vo.setString("dbpriv",dbstr);

        cat.debug("role_vo="+vo.toString());	
        
        SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
        sysbo.save();
    }
    private void saveBusiPriv(String role_id,String dbstr) throws GeneralException, SQLException {
    	RecordVo vo=null;
    	if("0".equalsIgnoreCase(this.userflag)){
    		vo=new RecordVo("operuser");
    		vo.setString("username", role_id);
    		vo.setString("busi_org_dept",dbstr);
    		ContentDAO dao = new ContentDAO(this.frameconn);
    		dao.updateValueObject(vo);
    	}else{
    		vo=new RecordVo("t_sys_function_priv");
    		vo.setString("id",role_id);
            vo.setString("status",this.userflag/*GeneralConstant.ROLE*/);
            vo.setString("busi_org_dept",dbstr);
            cat.debug("role_vo="+vo.toString());	
            SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
            sysbo.save();
        }
    }
    
    private void saveMediaPriv(String role_id,String media_str)
    {
        RecordVo vo=new RecordVo("t_sys_function_priv");
        vo.setString("id",role_id);
        vo.setString("status",this.userflag);
        vo.setString("mediapriv",media_str);
        cat.debug("role_vo="+vo.toString());	
        
        SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
        sysbo.save();  

    }
    
	  private void saveFieldPriv(String role_id,String field_str)
	  {
	      if(field_str==null)
	          field_str="";
//	      StringBuffer strsql=new StringBuffer();
//	      strsql.append("select id from t_sys_function_priv where id='");
//	      strsql.append(role_id);
//	      strsql.append("' and status=");
//	      strsql.append(this.userflag);
//	      try
//	      {
//	    	ArrayList paralist=new ArrayList();
//	    	ContentDAO dao=new ContentDAO(this.getFrameconn());
//	    	this.frowset=dao.search(strsql.toString());
//	    	cat.debug("select sql="+strsql.toString());	
//
//	    	if(this.frowset.next())
//	    	{
//	    		strsql.setLength(0);
//	    		strsql.append("update t_sys_function_priv set fieldpriv='");
//	    		strsql.append(field_str);
//	    		strsql.append("' where id='");
//	    		strsql.append(role_id);
//	    		strsql.append("' and status=");
//	    		strsql.append(this.userflag);
//	    	}
//	    	else
//	    	{
//		    	paralist.add(role_id);	    		
//		    	paralist.add(field_str);	    		
//	    		strsql.setLength(0);
//	    		strsql.append("insert into t_sys_function_priv (id,fieldpriv,status) values(?,'");
//	    		strsql.append(field_str);	
//	    		strsql.append("',");	
//	    		strsql.append(this.userflag);
//	    		strsql.append(")");
//	    	}
//	    	cat.debug("updat field_priv sql="+strsql.toString());
//	    	dao.update(strsql.toString(),paralist);
//	      }
//	      catch(SQLException sqle)
//	      {
//	    	  sqle.printStackTrace();
//	      }
	      RecordVo vo=new RecordVo("t_sys_function_priv");
	      vo.setString("id",role_id);
	      vo.setString("fieldpriv",field_str);
	      vo.setString("status",this.userflag/*GeneralConstant.ROLE*/);
	      cat.debug("role_vo="+vo.toString());	

	      SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
	      sysbo.save();    
	  } 
	  
	    private void savePrivString(String id,String flag,String pri_str,String fielename)
	    {
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
		      strsql.append(id);
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
			    	paralist.add(pri_str);	    		
		    		strsql.setLength(0);
		    		strsql.append("update t_sys_function_priv set ");// warnpriv=?");
		    		strsql.append(fielename);
		    		//strsql.append("='");
		    		//strsql.append(pri_str);//java.sql.SQLException: ORA-01704: 文字字符串过长	
		    		//strsql.append("'");
		    		strsql.append("=");
		    		strsql.append("?");	 		    		
		    		strsql.append(" where id='");
		    		strsql.append(id);
		    		strsql.append("' and status=");
		    		strsql.append(flag);
		    	}
		    	else
		    	{
			    	paralist.add(id);	    		
			    	paralist.add(pri_str);	    		
		    		strsql.setLength(0);
		    		strsql.append("insert into t_sys_function_priv (id,");
		    		strsql.append(fielename); 

		    		strsql.append(",status) values(?,");
		    		strsql.append("'");
		    		strsql.append("?");	    		
		    		//strsql.append(pri_str);//java.sql.SQLException: ORA-01704: 文字字符串过长	
		    		strsql.append("',");		    		
		    		strsql.append(flag);
		    		strsql.append(")");
		    	}
		    	cat.debug("updat priv sql="+strsql.toString());
		    	dao.update(strsql.toString(),paralist);
		      }
		      catch(SQLException sqle)
		      {
		    	  sqle.printStackTrace();
		      }
	    }		  
    /**
     * 保存表权限
     * @param role_id
     */
    private void saveTablePriv(String role_id,String table_str)
    {
        if(table_str==null)
        	table_str="";
        RecordVo vo=new RecordVo("t_sys_function_priv");
        vo.setString("id",role_id);
        vo.setString("status",this.userflag/*GeneralConstant.ROLE*/);
        vo.setString("tablepriv",table_str);
        cat.debug("role_vo="+vo.toString());	        
        SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
        sysbo.save();   
        this.invalidField(role_id, table_str);
    }
    
    /**
     * 清除无效的授权指标
     * @param role_id
     * @param table_str
     */
    private void invalidField(String role_id,String table_str){
    	RecordVo vo=new RecordVo("t_sys_function_priv");
	    vo.setString("id",role_id);
	    vo.setString("status",this.userflag/*GeneralConstant.ROLE*/);
    	if(table_str==null||table_str.length()==0){
	  	      vo.setString("fieldpriv","");
	  	      cat.debug("role_vo="+vo.toString());	
	  	      SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
	  	      sysbo.save(); 
    	}else{
    		ContentDAO dao = new ContentDAO(this.getFrameconn());
    		try {
				vo = dao.findByPrimaryKey(vo);
				if(vo!=null){
					String fieldpriv = vo.getString("fieldpriv");
					String[] fields = fieldpriv.split(",");
					StringBuffer sbfield = new StringBuffer(",");
					for(int i=0;i<fields.length;i++){
						String field = fields[i];
						if(field.length()==6){
							FieldItem item = DataDictionary.getFieldItem(field.substring(0,5).toLowerCase());
							if(item!=null&&table_str.indexOf(item.getFieldsetid())!=-1)
								sbfield.append(field+",");
						}
					}
					vo.setString("fieldpriv", sbfield.toString());
					dao.updateValueObject(vo); 
				}
			} catch (GeneralException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
    	}
    }
    
    private void saveManagePriv(String role_id,String manage_str)
    {
        if(manage_str==null)
        	manage_str= "";
        RecordVo vo=new RecordVo("t_sys_function_priv");
        vo.setString("id",role_id);
        vo.setString("status",this.userflag/*GeneralConstant.ROLE*/);
        vo.setString("managepriv",manage_str);
        cat.debug("role_vo="+vo.toString());	
        
        SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
        sysbo.save();         
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
    
    private boolean haveTheFunc(String func_str,String func_id)
    {
    	if(func_str.indexOf(","+func_id+",")==-1)
    		return false;
    	else
    		return true;
    }    
    /**
     * 取得当前功能节点下的父节点
     * @param curr_id
     * @return
     */
    private String getAllparentFuncId(String curr_id,String parentid,String moduleMenuId)
    {
    	StringBuffer buf=new StringBuffer();
    	String[] funcarr=StringUtils.split(curr_id, ',');
    	curr_id=funcarr[0];
        InputStream in=this.getClass().getResourceAsStream("/com/hjsj/hrms/constant/function.xml");
        VersionControl ver_ctrl=new VersionControl();
        try
        {
	        Document doc = PubFunc.generateDom(in);
	        List list=null;
        	/*String xpath = "//function[@id=\"" + curr_id + "\"]";
        	XPath xpath_ = XPath.newInstance(xpath);
        	Element ele = (Element) xpath_.selectSingleNode(doc);
        	Element ele_parent=ele.getParentElement();
	        String func_id=ele_parent.getAttributeValue("id"); 
        	while(!(func_id==null||func_id.length()==0))
        	{
  	            func_id=ele_parent.getAttributeValue("id");        		
        		ele_parent=ele_parent.getParentElement();
        		if(func_id==null)
        			continue;
		        buf.append(func_id);	 
		        buf.append(",");	
        	}*/
	        /**xuj 2011-1-6 解决function中id号有相同时不同版本寻找正确的父节点*/
	        Element root = doc.getRootElement();
	        String xpath = "//function[@id=\"" + curr_id + "\"]";
	        //存在模块id时，按照模块id+当前节点id定位 guodd 2019-07-08
	        if(moduleMenuId!=null &&  !moduleMenuId.equalsIgnoreCase(curr_id)) {//bug 50230  子节点才走此路径    wangb 2019-07-11
	        	xpath = "//function[@id=\"" + moduleMenuId + "\"]//function[@id=\"" + curr_id + "\"]";
	        }
	        
	        list = XPath.selectNodes(root, xpath);
	        Element ele = null;
	        int version = userView.getVersion();
	        int size = list.size();
	        if(size>1){
		        ver_ctrl.setVer(version);
		        for(int i=0;i<size;i++){
		        	/*if(buf.length()>0)
			        	break;*/
		        	StringBuffer sbf=new StringBuffer();
		        	ele = (Element)list.get(i);
		        	Element ele_parent=ele.getParentElement();
			        String func_id=ele_parent.getAttributeValue("id"); 
			        //针对此节点，有两级不同模块下的id都一样，所以需要往上追两级才能判断是那条权限线条 guodd 2016-11-28
			        if("260112".equals(func_id))
			        	    func_id = ele_parent.getParentElement().getAttributeValue("id");
			        if(parentid!=null && parentid.length()>0 && !func_id.equalsIgnoreCase(parentid))
			        	    continue;
		        	while(!(func_id==null||func_id.length()==0))
		        	{
		  	            func_id=ele_parent.getAttributeValue("id");
		  	            if(func_id==null)
		        			continue;
		  	            /**版本控制*/
				        if(!ver_ctrl.searchFunctionId(func_id)){
				        	sbf.setLength(0);
				        	break;
				        }
		        		ele_parent=ele_parent.getParentElement();
		        		sbf.append(func_id);	 
		        		sbf.append(",");
		        	}
		        	if(sbf.length()>0) {
		        		buf.append(sbf.toString());
		        	}
		        }
	        }else{
	        	ele=(Element)list.get(0);
	        	Element ele_parent=ele.getParentElement();
		        String func_id=ele_parent.getAttributeValue("id"); 
	        	while(!(func_id==null||func_id.length()==0))
	        	{
	  	            func_id=ele_parent.getAttributeValue("id");        		
	        		ele_parent=ele_parent.getParentElement();
	        		if(func_id==null)
	        			continue;
			        buf.append(func_id);	 
			        buf.append(",");
	        	}
	        }
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }
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
        	List eleList=xpath_.selectNodes(doc);
        	Element ele=null;
        	//根据锁版本判断需要展示的权限 展示出具有符合对应版本的节点，如果没有符合条件的则取出第一个没有设置ctrl_ver属性的节点
        	//zhanghua 2017-6-2
        	for(int i=0;i<eleList.size();i++){
        		Element eleTemp = (Element) eleList.get(i);
        		String Ver=eleTemp.getAttributeValue("ctrl_ver");
        		if(ele==null&&StringUtils.isBlank(Ver))
        			ele=eleTemp;
        		if(StringUtils.isNotBlank(Ver)&&Ver.indexOf(String.valueOf(userView.getVersion()))>=0){
        			ele=eleTemp;
        			break;
        		}
        	}
//        	if(ele==null){
//        		ele = (Element)  XPath.newInstance("//function[@id=\"" + curr_id + "\"]").selectSingleNode(doc);
//        	}
        	list = ele.getChildren("function");
        	
        	
        	
	        for (int i = 0; i < list.size(); i++)
	        {
	          Element node = (Element) list.get(i);
	          String func_id=node.getAttributeValue("id");
	          
	          /**HJ-eHR5.0平台界面,不显示参数设置、系统管理以及小工具条 与功能授权数据加载条件保持一致，否则会出现脏数据 guodd 2017-10-10*/
	          if(("hl".equalsIgnoreCase(userView.getBosflag())|| "hcm".equalsIgnoreCase(userView.getBosflag()))&&("07".equalsIgnoreCase(func_id)|| "08".equalsIgnoreCase(func_id)|| "00".equalsIgnoreCase(func_id)))
	          {
	        	  	continue;	        	  
	          }
	          /**
	           * 支持分布式授权机制
	           */
	          if(!userView.hasTheFunction(node.getAttributeValue("id")))
	              continue;
	          if(!ver_ctrl.searchFunctionId(node.getAttributeValue("id")))
	        	  continue;
	          if(!haveTheFunc(buf.toString(),node.getAttributeValue("id")))
	          {
		          buf.append(func_id);	 
		          buf.append(",");		          
	          }
	          buf.append(getChildFuncId(func_id));
	        } //for i loop end.
	        
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }
    	return buf.toString();
    	
    }
    
    private void saveResourceString(String role_id,String flag,String res_str)
    {
        if(res_str==null)
        	res_str="";
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
    
	public void execute() throws GeneralException {
        String role_id=(String)this.getFormHM().get("role_id");
   		
        String tab_name=(String)this.getFormHM().get("tab_name");
        userflag=(String)this.getFormHM().get("user_flag");
        
        cat.debug("role_id="+role_id);
        cat.debug("tab_name="+tab_name); 
        cat.debug("user_flag="+userflag);
        try
        {
	        if(role_id==null|| "".equals(role_id))
	            return;
	        //role_id=role_id.toUpperCase(); chenmengqing added 20080605 for oracle大小写问题
	        if(tab_name==null|| "".equals(tab_name))
	            return;
	        //如果为空，则default为角色
	        if(userflag==null|| "".equals(userflag))
	            userflag=GeneralConstant.ROLE;
	        //role_id=PubFunc.ToGbCode(role_id);
	        /**
	         * 功能授权
	         */
	        if("funcpriv".equals(tab_name))
	        {
	        	String func_str=(String)this.getFormHM().get("selstr");
	        	String imme=(String)this.getFormHM().get("imme");
	        	String checked=(String)this.getFormHM().get("checked");
	        	if("1".equalsIgnoreCase(imme))
	        	{
	        		/**取父节点*/
	        		String tmp="";
	        		/**只有下级选中时自动带上级功能节点,去掉不带,有点小问题,当去掉最后一个叶子节点,
	        		 * 上级功能节点,还得再次操作  已解决xuj 2011-1-6*/
	        		String parentid = (String)this.getFormHM().get("parentid");
	        		//选中节点的模块id，后面定位节点时使用 guodd 2019-07-08
	        		String moduleMenuId = (String)this.getFormHM().get("moduleMenuId");
	        		if("1".equalsIgnoreCase(checked))
	        			tmp=getAllparentFuncId(func_str,parentid,moduleMenuId);
	        		/**取当前节点下所有子节点*/
	        		func_str=getAllChildFuncId(func_str);
	        		func_str=func_str+tmp;
	        		ImmeSaveFunctionPriv(role_id,func_str,checked);
	        	}
	        	else
	        		saveFunctionPriv(role_id,func_str);
	        }
	        /**
	         * 人员库授权
	         */
	        if("dbpriv".equals(tab_name))
	        {
	        	String db_str=(String)this.getFormHM().get("selstr");	        	
	            saveDbPriv(role_id,db_str);
	        }
	        /**多媒体子集*/        
	        if("mediapriv".equals(tab_name))
	        {
	        	String media_str=(String)this.getFormHM().get("selstr");	   	        	
	        	saveMediaPriv(role_id,media_str);
	        }	        
	        /**子集*/        
	        if("tablepriv".equals(tab_name))
	        {
	        	String privcode=(String)this.getFormHM().get("privcode");
	        	String media_str=(String)this.getFormHM().get("selstr");	
	        	if(privcode==null||privcode.length()<=0|| "###".equals(privcode))
	        	{
	        		saveTablePriv(role_id,media_str);
	        		
	        	}else
	        	{
	        		SysPrivBo sysPrivBo=new SysPrivBo(role_id,userflag,this.getFrameconn(),"subpriv",privcode);
	        		sysPrivBo.setSubprivPrivStr(privcode,"table",media_str);
	        	}  
	        	
	        }	
	        /**指标*/
	        if("fieldpriv".equals(tab_name))
	        {
	        	String privcode=(String)this.getFormHM().get("privcode");
	        	String media_str=(String)this.getFormHM().get("selstr");
	        	if(privcode==null||privcode.length()<=0|| "###".equals(privcode))
	        	{
	        	   saveFieldPriv(role_id,media_str);
	        	}else
	        	{
	        		SysPrivBo sysPrivBo=new SysPrivBo(role_id,userflag,this.getFrameconn(),"subpriv",privcode);
	        		sysPrivBo.setSubprivPrivStr(privcode,"field",media_str);
	        	}
	        }		
	        /**管理范围*/
	        if("managepriv".equals(tab_name))
	        {
	        	String selstr=(String)this.getFormHM().get("selstr");	  
	        	String[] arr=StringUtils.split(selstr,",");
	        	selstr=StringUtils.join(arr, ",");
	        	saveManagePriv(role_id,selstr);
	        }
	        if("partymanagepriv".equals(tab_name)|| "membermanagepriv".equals(tab_name))
	        {
	        	String selstr=(String)this.getFormHM().get("selstr");	  
	        	String[] arr=StringUtils.split(selstr,",");
	        	selstr=StringUtils.join(arr, ",");
	        	if("partymanagepriv".equals(tab_name)){
	        		selstr=selstr.replace("ALL", "64");
	        	}else if("membermanagepriv".equals(tab_name)){
	        		selstr=selstr.replace("ALL", "65");
	        	}
	        	SysPrivBo privbo=new SysPrivBo(role_id,userflag,this.getFrameconn(),"warnpriv");
				String res_str=privbo.getWarn_str();
				int res_type = IResourceConstant.PARTY;
		        if("membermanagepriv".equalsIgnoreCase(tab_name))
		        	res_type = IResourceConstant.MEMBER;
				ResourceParser parser=new ResourceParser(res_str,res_type);
				parser.reSetContent(selstr);
				res_str=parser.outResourceContent();
				saveResourceString(role_id,userflag,res_str);
	        }	
	        if("busipriv".equals(tab_name))
	        {
	        	String bsui_str=(String)this.getFormHM().get("selstr");	        	
	        	saveBusiPriv(role_id,bsui_str);
	        }
	        
	        try{
		        StringBuffer mess = new StringBuffer(ResourceFactory.getProperty("log.asign.dui"));
	        	ContentDAO dao = new ContentDAO(this.frameconn);
		        if("4".equals(userflag)){//自助
	        		mess.append(ResourceFactory.getProperty("label.role.detail.name.1"));
	        		this.frowset = dao.search("select a0101 from "+role_id.substring(0, 3)+"A01 where a0100='"+role_id.substring(3)+"'");
	        		if(this.frowset.next())
	        			mess.append("["+this.frowset.getString("a0101")+"]");
		        }else if("0".equals(userflag)){//业务
	        		mess.append(ResourceFactory.getProperty("label.role.detail.name.0"));
	        		mess.append("["+role_id+"]");
		        }else{//角色
	        		mess.append(ResourceFactory.getProperty("label.sys.warn.domain.role"));
	        		this.frowset = dao.search("select role_name from t_sys_role where role_id='"+role_id+"'");
	        		if(this.frowset.next())
	        			mess.append("["+this.frowset.getString("role_name")+"]");
	        	}
		        mess.append(ResourceFactory.getProperty("log.asign."+tab_name));
	        	this.getFormHM().put("@eventlog", mess.toString());
	        }catch(Exception e){
	        	e.printStackTrace();
	        }
	        
        }
        catch(Exception ee)
        {
        	ee.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ee);
        }
	}
    
}
