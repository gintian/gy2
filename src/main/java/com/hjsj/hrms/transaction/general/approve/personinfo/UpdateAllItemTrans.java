package com.hjsj.hrms.transaction.general.approve.personinfo;

import com.hjsj.hrms.businessobject.hire.AutoSendEMailBo;
import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class UpdateAllItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm = (HashMap) hm.get("requestPamaHM");
		UserView uv=this.getUserView();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String itemstr=(String) hm.get("itemstr");
		String action=(String)hm.get("action");
		String pinfo=(String)hm.get("pinfo");
		String message=(String)hm.get("message");
		
//		reqhm.remove("message");
		try{
		if(itemstr.length()>0){
//			用户选定子集货主集信息
			if("ok".equals(action)){
				
			}
			if("rj".equals(action)){
				
			}
			if("aok".equals(action)){
				
			}
			if("arj".equals(action)){
				
			}
		}else{
//			用户没有选定任何主集或子集信息
			if("aok".equals(action)){
				ArrayList sqls=this.actoinaok(uv,pinfo,itemstr);
				dao.batchUpdate(sqls);
			}
			if("arj".equals(action)){
				ArrayList sqls=this.actoinarj(uv,pinfo,itemstr,message,dao);
				dao.batchUpdate(sqls);
				
			}
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		hm.remove("message");
//		System.out.println(itemstr);
//		System.out.println(action);
	}
	private ArrayList actoinok(String pinfo,String itemstr){
		return null;
		
	}
	private ArrayList actoinrj(String pinfo,String itemstr){
		return null;
		
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
				String sql="update "+dbname+fs.getFieldsetid()+" set state='3' where state='1' and a0100='"+userid+"'";
				String sql1="update "+dbname+fs.getFieldsetid()+" set state='3' where state='2' and a0100='"+userid+"'";
				retsql.add(sql1);
				retsql.add(sql);
			}
		}
		return retsql;
	}
	private ArrayList actoinarj(UserView uv,String pinfo,String itemstr,String message,ContentDAO dao) throws GeneralException{
		ArrayList retsql=new ArrayList();
		if(itemstr!=null&&itemstr.length()>1){
			String[] tempitem=itemstr.split("\\|");
		}else{
			String[] tempinfo=pinfo.split("\\|");
			String userid=tempinfo[0];
			String dbname=tempinfo[1];
			try{
				this.dispatchMessage(dbname,userid,message,dao);
			}catch(Exception e){
				System.out.println("邮件服务器设置有误！");
			}
			finally{
			ArrayList fieldlist=uv.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
			for(int i=0;i<fieldlist.size();i++){
				FieldSet fs=(FieldSet)fieldlist.get(i);
				String sql="update "+dbname+fs.getFieldsetid()+" set state='2' where state='1' and a0100='"+userid+"'";
				String sql1="update "+dbname+fs.getFieldsetid()+" set state='2' where state='3' and a0100='"+userid+"'";
				retsql.add(sql1);
				retsql.add(sql);
			}
		}
		}
		return retsql;
	}
	private void dispatchMessage(String dbname,String a0100,String content,ContentDAO dao) throws GeneralException{
		EMailBo bo = new EMailBo(this.getFrameconn(),true,"");
	    /**对这种情况，按超级用户所有地址作为发送地址*/
	   String c=content;
	   c=c.replaceAll("\r\n","<br>");
	   c=c.replace("\n","<br>");
	   c=c.replace("\r","<br>");
	    AutoSendEMailBo ase=new AutoSendEMailBo(this.getFrameconn());
	    String fromaddr=ase.getFromAddr();
	    String toaddr=bo.getEmailAddrByA0100(dbname+a0100);
	    StringBuffer head=new StringBuffer();
	    String username=this.getname(dao,a0100,dbname);
    	head.append(username+"您好：<br>");
    	head.append("&nbsp;&nbsp;这是一封来自人力资源自助系统的邮件，您的个人档案信息有不当之处！请核对。<br>");
    	if(c.length()>0){
    		head.append("<br>具体原因如下：");
    	}
    	head.append("<br>"+c);
    	head.append("<br><br>                              谢谢合作!");
	    
	    if(toaddr==null){
	    	bo.sendEmail("整体报批信息提示(人力资源)","邮件地址不存在！"+head.toString(),"",fromaddr,fromaddr);
	    }else{
	    bo.sendEmail("整体报批信息提示(人力资源)",head.toString(),"",fromaddr,toaddr);
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
