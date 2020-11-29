/*
 * Created on 2005-5-30
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.updownfile;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchDownFileListTrans extends IBusiness {

	  public void execute() throws GeneralException {
		  
	  	String unitcode = this.getUnit();//得到所在单位或操作单位
	  	String unitcodeWhere = this.getUnitWhere(unitcode);//得到单位的sql语句条件
		HashMap reqMap=(HashMap)this.getFormHM().get("requestPamaHM");
	  	String fileflag=this.getFormHM().get("flag").toString();
	  	
	  	String fromPage="";
	  	if(reqMap.get("fromPage")!=null)
	  	{
	  		fromPage=(String)reqMap.get("fromPage");
	  		reqMap.remove("fromPage");
	  	}
	  	this.getFormHM().put("fromPage",fromPage);
	  	
	    StringBuffer strsql=new StringBuffer();
	    strsql.append("select contentid,id,name,description,createdate,status,ext,fileid from resource_list where id="+fileflag+unitcodeWhere/*+" and status=1"*/);
	    strsql.append(" order by createdate desc");
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    ArrayList list=new ArrayList();
	    try
	    {
	      this.frowset = dao.search(strsql.toString());
	      while(this.frowset.next())
	      {
	          RecordVo vo=new RecordVo("resource_list");
	          
	          vo.setString("contentid",this.getFrowset().getString("contentid"));
	         
	          String temp=this.getFrowset().getString("id");
	          if(temp==null || "".equals(temp))
	          {
	          	vo.setString("id","");
	          }
	          else
	          {
	            vo.setString("id", this.getFrowset().getString("id"));
	          }
	          
	          temp=this.getFrowset().getString("name");
	          if(temp==null || "".equals(temp))
	          {
	          	vo.setString("name","...");
	          }
	          else
	          {
	          vo.setString("name",this.getFrowset().getString("name"));
	          }
	          
	          
	          temp=this.getFrowset().getString("description");
	          if(temp==null || "".equals(temp))
	          {
	          	vo.setString("description","");
	          }
	          else
	          {
	          vo.setString("description",this.getFrowset().getString("description"));
	          }
	          
	          
	          temp=PubFunc.DoFormatDate(this.getFrowset().getString("createdate"));
	          if(temp==null || "".equals(temp))
	          {
	          	vo.setString("createdate","");
	          }
	          else
	          {
	          vo.setString("createdate",PubFunc.DoFormatDate(this.getFrowset().getString("createdate")));
	          }
	          
	          
	          temp=this.getFrowset().getString("status");
	          if(temp==null || "".equals(temp))
	          {
	          	vo.setString("status","");
	          }
	          else
	          {
	            vo.setString("status",this.getFrowset().getString("status"));
	          }
	          vo.setString("ext",this.frowset.getString("ext"));
	          //20/3/5 xus vfs 改造
	          String fileid = "";
	          if(this.getFrowset().getObject("fileid") != null && StringUtils.isNotBlank(this.getFrowset().getString("fileid"))) {
        		  fileid = this.getFrowset().getString("fileid");
	          }
	          vo.setString("fileid",fileid);
	          cat.debug("resource_list_vo="+vo.toString());	 
	          list.add(vo);
	      }
	      
	      this.getFormHM().put("downFilelist",list);
	    }
	    catch(Exception ex)
	    {
	      ex.printStackTrace();
	      throw GeneralExceptionHandler.Handle(ex);
	    }

     }
	  
///////////////////////郭峰增加////////////////////////////////////////
		/**
		 * 获取本单位及上级单位的sql语句条件
		 * @param codeid
		 * @return
		 */
		public String getUnitWhere(String codeid){
			String strWhere = "";
			if(!("".equals(codeid)) && !(codeid == null)){//如果不是超级用户
				strWhere = "('";
				int n = codeid.length();
				for(int i=0;i<n;i++){
					strWhere +=codeid.substring(0,codeid.length()-i)+"','";
				}
				strWhere = strWhere.substring(0, strWhere.length()-2);
				strWhere = " and (unitcode in "+strWhere+") or unitcode is null or unitcode like '"+codeid+"%')";
			}
			return strWhere;
		}
		/**
		 * 获取单位。
		 * @return
		 */
		public String getUnit(){
			String unit = "";
			if(!userView.isSuper_admin()){//如果不是超级用户
				int userType = this.userView.getStatus();//判断是业务用户还是自助用户。如果是4则是自助用户,0是业务用户。
				if(userType==4){//如果是自助用户
					unit = this.userView.getUserOrgId();//得到用户所在单位
				}else if(userType==0){//如果是业务用户，先看操作单位。如果没有，则看管理范围
					unit = getOperUnit();
				}
			}
			return unit;
		}
		/*
		 * 查出操作单位（如果有多个，则只取第一个。如果是部门，则取出它所在的单位）。如果没有操作单位，则查出管理范围所在的单位。
		 * **/
		public String getOperUnit() 
		{
				String unit = "";
				String operOrg = this.userView.getUnit_id();
				if (operOrg!=null && operOrg.length() > 3) //如果有操作单位
				{
					String[] temp = operOrg.split("`");
					String unitordepart = temp[0];
					if ("UN".equalsIgnoreCase(unitordepart.substring(0, 2)))//如果是单位
						unit = unitordepart.substring(2);
					else//如果是部门
						unit = getUnit(unitordepart.substring(2));
				}
				else if((!this.userView.isSuper_admin()) && ("".equalsIgnoreCase(operOrg))) // 如果不是超级用户，且没有操作单位
				{
					String codePrefix = this.userView.getManagePrivCode();
					String codeid = this.userView.getManagePrivCodeValue();
					if("UN".equalsIgnoreCase(codePrefix))//如果是单位
						unit = codeid;
					else//如果是部门
						unit = this.getUnit(codeid);
				}
			return unit;		
		}
		

		/**
		 * 通过部门得到所属单位
		 * */
		public String getUnit(String codeid){
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
			}
			return unit;
		}
	///////////////////////郭峰增加////////////////////////////////////////

}
