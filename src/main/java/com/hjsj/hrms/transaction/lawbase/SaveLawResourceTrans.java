/**
 * 
 */
package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ResourceParser;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:SaveLawResourceTrans</p>
 * <p>Description:保存制度分类号</p> 
 * <p>Company:hjsj</p> 
 * create time at:Sep 23, 200611:56:57 AM
 * @author chenmengqing
 * @version 4.0
 */
public class SaveLawResourceTrans extends IBusiness {

	public void execute() throws GeneralException {
		String isCorrect="true";
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
			String law_dir=(String)this.getFormHM().get("law_dir");
			//parser.addContent(law_dir);
			if("6".equals(res_flag)||"19".equals(res_flag)){
				String[] ids = law_dir.split(",");
				StringBuffer sb = new StringBuffer();
				for(int i=0;i<ids.length;i++){
					law_dir = PubFunc.decrypt(ids[i]);
					sb.append(law_dir+",");
				}
				law_dir = sb.toString();
			}
			parser.reSetContent(law_dir);
			res_str=parser.outResourceContent();
			saveResourceString(roleid,flag,res_str);
			this.getFormHM().put("@eventlog", this.getEventLog(roleid, flag, res_type));
		}
		catch(Exception ex)
		{
			isCorrect="false";
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		this.getFormHM().put("isCorrect", isCorrect);
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
	 public String getEventLog(String role_id,String userflag,int res_type ){
		 StringBuffer mess = new StringBuffer(ResourceFactory.getProperty("log.asign.dui"));
		 Connection conn = null;
		 try{
			 conn = AdminDb.getConnection();
	        	ContentDAO dao = new ContentDAO(conn);
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
		        mess.append(this.getElementName(res_type));
		        mess.append(ResourceFactory.getProperty("button.resource.assign"));
	        }catch(Exception e){
	        	e.printStackTrace();
	        }finally{
        	if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
	        }
	        return mess.toString();
	 }
	 
	 private String getElementName(int res_type)
	    {
	        String name = null;
	        switch(res_type)
	        {
	        case 0: // '\0'
	            name = ResourceFactory.getProperty("sys.res.card");
	            break;

	        case 1: // '\001'
	            name = ResourceFactory.getProperty("sys.res.tjb");
	            break;

	        case 5: // '\005'
	            name = ResourceFactory.getProperty("sys.res.hmuster");
	            break;

	        case 4: // '\004'
	            name = ResourceFactory.getProperty("sys.res.muster");
	            break;

	        case 2: // '\002'
	            name = ResourceFactory.getProperty("sys.res.query");
	            break;

	        case 3: // '\003'
	            name = ResourceFactory.getProperty("sys.res.static");
	            break;

	        case 6: // '\006'
	            name = ResourceFactory.getProperty("sys.res.rule");
	            break;

	        case 7: // '\007'
	            name = ResourceFactory.getProperty("sys.res.rsbd");
	            break;

	        case 8: // '\b'
	            name = ResourceFactory.getProperty("sys.res.gzbd");
	            break;

	        case 9: // '\t'
	            name = ResourceFactory.getProperty("sys.res.inv");
	            break;

	        case 10: // '\n'
	            name = ResourceFactory.getProperty("sys.res.trainjob");
	            break;

	        case 11: // '\013'
	            name = ResourceFactory.getProperty("sys.res.announce");
	            break;

	        case 12: // '\f'
	            name = ResourceFactory.getProperty("sys.res.gzset");
	            break;

	        case 13: // '\r'
	            name = /*ResourceFactory.getProperty("button.resource.assign")*/"";
	            break;

	        case 14: // '\016'
	            name = ResourceFactory.getProperty("sys.res.archtype");
	            break;

	        case 15: // '\017'
	            name = ResourceFactory.getProperty("sys.res.kq_mach");
	            break;

	        case 16: // '\020'
	            name = /*ResourceFactory.getProperty("button.resource.assign")*/"";
	            break;

	        case 17: // '\021'
	            name = ResourceFactory.getProperty("sys.res.ins_bd");
	            break;

	        case 18: // '\022'
	            name = ResourceFactory.getProperty("sys.res.ins_set");
	            break;

	        case 19: // '\023'
	            name = ResourceFactory.getProperty("sys.res.filetype");
	            break;

	        case 20: // '\024'
	            name = ResourceFactory.getProperty("sys.res.gzchart");
	            break;

	        case 21: // '\025'
	            name = /*ResourceFactory.getProperty("button.resource.assign")*/"";
	            break;

	        case 22: // '\026'
	            name = ResourceFactory.getProperty("sys.res.khmodule");
	            break;

	        case 23: // '\027'
	            name = ResourceFactory.getProperty("sys.res.khfield");
	            break;

	        case 24: // '\030'
	            name = ResourceFactory.getProperty("kq.class.title");
	            break;

	        case 25: // '\031'
	            name = /*ResourceFactory.getProperty("button.resource.assign")*/"";
	            break;

	        case 26: // '\032'
	            name =ResourceFactory.getProperty("kq.group");
	            break;

	        case 27: // '\033'
	            name = /*ResourceFactory.getProperty("button.resource.assign")*/"";
	            break;

	        case 28: // '\034'
	            name = ResourceFactory.getProperty("sys.res.gzreportstyle");
	            break;

	        case 29: // '\035'
	            name = /*ResourceFactory.getProperty("button.resource.assign")*/"";
	            break;

	        case 30: // '\036'
	            name = /*ResourceFactory.getProperty("button.resource.assign")*/"";
	            break;

	        case 31: // '\037'
	            name = ResourceFactory.getProperty("sys.res.org");
	            break;

	        case 32: // ' '
	            name = ResourceFactory.getProperty("sys.res.pos");
	            break;

	        case 33: // '!'
	            name = ResourceFactory.getProperty("sys.res.keyeventset");
	            break;

	        case 34: // '"'
	            name = ResourceFactory.getProperty("sys.res.psorgans");
	            break;

	        case 35: // '#'
	            name = ResourceFactory.getProperty("sys.res.psorgans_jcg");
	            break;

	        case 36: // '$'
	            name = ResourceFactory.getProperty("sys.res.psorgans_fg");
	            break;

	        case 37: // '%'
	            name = ResourceFactory.getProperty("sys.res.psorgans_gx");
	            break;
	        }
	        return name;
	    }
	 
//    private void saveResourceString(String role_id,String flag,String res_str)
//    {
//        if(res_str==null)
//        	res_str="";
//        RecordVo vo=new RecordVo("t_sys_function_priv");
//        vo.setString("id",role_id);
//        vo.setString("status",flag/*GeneralConstant.ROLE*/);
//        vo.setString("warnpriv",res_str);
//        cat.debug("role_vo="+vo.toString());	
//        SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
//        sysbo.save();        
//    }	
}
