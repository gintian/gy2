package com.hjsj.hrms.transaction.propose;


import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.log4j.Category;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author chenmengqing
 */
public class SearchHtmlFileListTrans extends IBusiness {
    Category gc = Category.getInstance("SearchHtmlFileListTrans");
    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
	    	String unitcode = this.getOperUnit();
	    	String fileflag=(String)this.getFormHM().get("fileflag");
	    	//表格上传
	    	if("1".equals(fileflag) && (this.userView.hasTheFunction("070401") || this.userView.hasTheFunction("30021"))){
	    		
	    	}else if("2".equals(fileflag) && (this.userView.hasTheFunction("070301") || this.userView.hasTheFunction("30020"))){//流程上传
	    		
	    	}else{
	    		throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("msg.system.InsufficientPermissions")));
	    	}
    	
    	
        StringBuffer strsql=new StringBuffer();
	    strsql.append("select contentid,name,description,createdate,status,ext,unitcode from resource_list where (unitcode like '"+unitcode+"%' or unitcode is null) and id='"+fileflag+"'");
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    ArrayList list=new ArrayList();
	    try
	    {
	      this.frowset = dao.search(strsql.toString());
	      while(this.frowset.next())
	      {
	          RecordVo vo=new RecordVo("resource_list");
	          vo.setString("contentid",this.getFrowset().getString("contentid"));
	          /**匿名*/
	          String temp=PubFunc.nullToStr(this.getFrowset().getString("name"));
	          //不对上传文件的标题长度超过20截断  wangb 12936  20170519    
	          vo.setString("name", temp);
	          //temp=Sql_switcher.readMemo(this.frowset,"description");
	          vo.setString("description",this.getFrowset().getString("description")/*temp*/);
	          temp=PubFunc.DoFormatDate(this.getFrowset().getString("createdate"));
	          if(temp==null|| "".equals(temp))
	          {
		          vo.setString("createdate","");	              
	          }
	          else
	          {
	          	vo.setString("createdate",temp);
	          }
	          int tempInt=this.getFrowset().getInt("status");
	          
	          if((new Integer(tempInt))==null || tempInt==0)
	          {
		          vo.setInt("status",0);	              
	          }
	          else
	          {
	          	  vo.setInt("status",tempInt);
	          }
	          vo.setString("unitcode", this.frowset.getString("unitcode"));

	          list.add(vo);
	      }
	    }
	    catch(OutOfMemoryError error)
		{
	    	error.printStackTrace();
		}
	    catch(Exception ex)
	    {
	      ex.printStackTrace();
	      throw GeneralExceptionHandler.Handle(ex);
	    }
	    finally
	    {
	    	
	        this.getFormHM().put("proposelist",list);
	        this.getFormHM().put("unitcode", unitcode);
	    }
        RecordVo vo_f=new RecordVo("resource_list");
        //上传界面单选按钮默认为 是  jingq add 2014.6.3
        vo_f.setString("status", "1");
        this.getFormHM().put("proposeTb", vo_f);
       
    }
    
	/*
	 * 查出操作单位（如果有多个，则只取第一个。如果是部门，则取出它所在的单位）。如果没有操作单位，则查出管理范围所在的单位。
	 * **/
	public String getOperUnit() throws GeneralException
	{
			String unit = "";
			String operOrg = this.userView.getUnit_id();
			if (operOrg!=null && operOrg.length() > 3) //如果有操作单位
			{
				String[] temp = operOrg.split("`");
				String unitordepart = temp[0];
				if ("UN".equalsIgnoreCase(unitordepart.substring(0, 2)))//如果是单位
					unit = unitordepart.substring(2);
				else
					unit = getUnit(unitordepart.substring(2));
			}
			else if((!this.userView.isSuper_admin()) && ("".equalsIgnoreCase(operOrg))) // 如果不是超级用户，且没有操作单位
			{
				String codePrefix = this.userView.getManagePrivCode();
				String codeid = this.userView.getManagePrivCodeValue();
				if("UN".equalsIgnoreCase(codePrefix))
					unit = codeid;
				else
					unit = this.getUnit(codeid);
			}
		return unit;		
	}
	

	/**
	 * 通过部门得到所属单位
	 * */
	public String getUnit(String codeid) throws GeneralException{
		String unit = "";
		try{
			String style = "";//返回UM或者UN
			StringBuffer sb = new StringBuffer();
			sb.append("select codesetid,codeitemid from organization where codeitemid= (select parentid from organization where codeitemid='"+codeid+"')");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sb.toString());
			if(this.frowset.next()){
				style = this.frowset.getString("codesetid");
				unit = this.frowset.getString("codeitemid");
			}
			if("UM".equalsIgnoreCase(style))
				getUnit(unit);
		}catch(SQLException e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return unit;
	}

}
