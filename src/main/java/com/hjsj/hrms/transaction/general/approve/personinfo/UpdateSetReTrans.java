package com.hjsj.hrms.transaction.general.approve.personinfo;

import com.hjsj.hrms.businessobject.hire.AutoSendEMailBo;
import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class UpdateSetReTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		UserView uv=this.getUserView();
		HashMap reqhm = (HashMap) hm.get("requestPamaHM");
		String todo=(String)reqhm.get("todo");
		String scope=(String)reqhm.get("scope");
		reqhm.remove("scope");
		reqhm.remove("todo");
		String setid = (String) reqhm.get("setid");
		String abkflag=(String)hm.get("abkflag");
		String pdbflag=(String)hm.get("pdbflag");
		ArrayList sel_update_info=(ArrayList) hm.get("sel_update_info");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		if(scope==null){
		for(int i=0;i<sel_update_info.size();i++){
			LazyDynaBean dynabean=(LazyDynaBean)sel_update_info.get(i);	
			HashMap mapvalue = (HashMap) dynabean.getMap();
			String tablename=setid;
			if("a".equalsIgnoreCase(abkflag)){
				tablename=pdbflag+setid;
			}
			RecordVo vo=new RecordVo(tablename);
			vo.setValues(mapvalue);			
		try {
				vo =dao.findByPrimaryKey(vo);
				if("0".equals(todo)){
					vo.setString("state","2");
				}else if("1".equals(todo)){
					vo.setString("state","3");
				}else if("5".equals(todo)){
					vo.setString("state","5");
				}else if("6".equals(todo)){
					vo.setString("state","6");
				}
				
				dao.updateValueObject(vo);
			} catch (GeneralException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		}else{
			for(int i=0;i<sel_update_info.size();i++){
			try{
				LazyDynaBean dynabean=(LazyDynaBean)sel_update_info.get(i);	
			String pinfo=dynabean.get("a0100")+"|"+pdbflag;
			if("aok".equals(todo)){
				ArrayList sqls=this.actoinaok(uv,pinfo,"");
				dao.batchUpdate(sqls);
			}
			if("arj".equals(todo)){
				ArrayList sqls=this.actoinarj(uv,pinfo,"",dao);
				dao.batchUpdate(sqls);
			}
			if("upok".equals(todo))
			{
				ArrayList sqls=this.actoinupok(uv,pinfo,"");
				dao.batchUpdate(sqls);
			}else if("upno".equals(todo))
			{
				ArrayList sqls=this.actoinupno(uv,pinfo,"");
				dao.batchUpdate(sqls);
			}
			}catch(Exception e){
				e.printStackTrace();
			}
			}
		}
	}
	private ArrayList actoinaok(UserView uv,String pinfo,String itemstr){
		ArrayList retsql=new ArrayList();
		if(itemstr!=null&&itemstr.length()>1){
			String[] tempitem=itemstr.split("\\|");
		}else{
			String[] tempinfo=pinfo.split("\\|");
			String userid=tempinfo[0];
			String dbname=tempinfo[1];
			ArrayList fieldlist=uv.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
			for(int i=0;i<fieldlist.size();i++){
				FieldSet fs=(FieldSet)fieldlist.get(i);
				String sql="update "+dbname+fs.getFieldsetid()+" set state='3' where  a0100='"+userid+"' and (state='1' or state='2')";
				retsql.add(sql);
			}
		}
		return retsql;
	}	
	private ArrayList actoinarj(UserView uv,String pinfo,String itemstr,ContentDAO dao) throws GeneralException{
		ArrayList retsql=new ArrayList();
		if(itemstr!=null&&itemstr.length()>1){
			String[] tempitem=itemstr.split("\\|");
		}else{
			String[] tempinfo=pinfo.split("\\|");
			String userid=tempinfo[0];
			String dbname=tempinfo[1];
			try{
			this.dispatchMessage(dao,dbname,userid);
			}catch(Exception e){
				System.out.println("邮件服务器设置有误！");
			}
			finally{
			ArrayList fieldlist=uv.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
			for(int i=0;i<fieldlist.size();i++){
				FieldSet fs=(FieldSet)fieldlist.get(i);
				String sql1="update "+dbname+fs.getFieldsetid()+" set state='2' where state='1'  and a0100='"+userid+"'";
				
				String sql2="update "+dbname+fs.getFieldsetid()+" set state='2' where  state='3'  and a0100='"+userid+"'";
				
				
				String sql="update "+dbname+fs.getFieldsetid()+" set state='2' where  state is null and a0100='"+userid+"'";
				retsql.add(sql1);
				retsql.add(sql2);
				retsql.add(sql);
			}
		}
		}
		return retsql;
	}
	/**
	 * 整体可修改
	 * @param uv
	 * @param pinfo
	 * @param itemstr
	 * @return
	 */
	private ArrayList actoinupok(UserView uv,String pinfo,String itemstr){
		ArrayList retsql=new ArrayList();
		if(itemstr!=null&&itemstr.length()>1){
			String[] tempitem=itemstr.split("\\|");
		}else{
			String[] tempinfo=pinfo.split("\\|");
			String userid=tempinfo[0];
			String dbname=tempinfo[1];
			ArrayList fieldlist=uv.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
			for(int i=0;i<fieldlist.size();i++){
				FieldSet fs=(FieldSet)fieldlist.get(i);
				String sql="update "+dbname+fs.getFieldsetid()+" set state='5' where  a0100='"+userid+"' and state='4'";
				retsql.add(sql);
			}
		}
		return retsql;
	}
	/**
	 * 整体不可修改
	 * @param uv
	 * @param pinfo
	 * @param itemstr
	 * @return
	 */
	private ArrayList actoinupno(UserView uv,String pinfo,String itemstr){
		ArrayList retsql=new ArrayList();
		if(itemstr!=null&&itemstr.length()>1){
			String[] tempitem=itemstr.split("\\|");
		}else{
			String[] tempinfo=pinfo.split("\\|");
			String userid=tempinfo[0];
			String dbname=tempinfo[1];
			ArrayList fieldlist=uv.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
			for(int i=0;i<fieldlist.size();i++){
				FieldSet fs=(FieldSet)fieldlist.get(i);
				String sql="update "+dbname+fs.getFieldsetid()+" set state='3' where  a0100='"+userid+"' and state='4'";
				retsql.add(sql);
			}
		}
		return retsql;
	}
	private void dispatchMessage(ContentDAO dao,String dbname,String a0100) throws GeneralException{
		EMailBo bo = new EMailBo(this.getFrameconn(),true,"");
	    /**对这种情况，按超级用户所有地址作为发送地址*/
	   
	    AutoSendEMailBo ase=new AutoSendEMailBo(this.getFrameconn());
	    String fromaddr=ase.getFromAddr();
	    String toaddr=bo.getEmailAddrByA0100(dbname+a0100);
	    String username=this.getname(dao,a0100,dbname);
	    StringBuffer head=new StringBuffer();
    	head.append(username+"您好：<br>");
    	head.append("&nbsp;&nbsp;这是一封来自人力资源自助系统的邮件，您的个人档案信息有不当之处！请核对。<br>");
    	head.append("<br><br>                              谢谢合作<br>");
	    if(toaddr==null){
	    	
	    	bo.sendEmail(username+"整体报批信息提示(人力资源)","邮件地址不存在!","",fromaddr,fromaddr);
	    }else{
	    	
	    	
	    	bo.sendEmail(username+"整体报批信息提示(人力资源)",head.toString(),"",fromaddr,toaddr);
	    
	    }
		bo.close();

	}
	public String getname(ContentDAO dao,String a0100,String dbname) throws GeneralException{
		
		String username="";
		ArrayList a=dao.searchDynaList("select a0101 from "+dbname+"a01 where a0100='"+a0100+"'");
		if(a.size()>0){
			DynaBean dybean=(DynaBean) a.get(0);
			username=(String) dybean.get("a0101");
		}
		return username;
	}
	public String getBr(String value){
		String valueresult ="";
		for(int i=0;i<value.length();i++)
		{
		   if("\n".equals(value.substring(i,i+1))){
			   valueresult+="<br>";
			   }
		   else if("\r".equals(value.substring(i,i+1))){
			   valueresult+="<br>";
		   }else{
			   valueresult+=value.substring(i,i+1);
		   }
		}	
		return valueresult;
	}
}
