package com.hjsj.hrms.transaction.hire.employActualize.employResume;

import com.hjsj.hrms.businessobject.hire.AutoSendEMailBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class BatchSendMailTrans extends IBusiness {

	public void execute() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String sendtype=(String)map.get("sendtype");  //  0:发送短信  1：发送邮件
			String a0100s=(String)this.getFormHM().get("a0100s");
			/**由于安全平台改造,/被转化为全角,所以转换回来**/
			if(a0100s!=null){
				a0100s= a0100s.replaceAll("／", "/");
			}
			String mailTempID=(String)this.getFormHM().get("mailTempID");
			String title=(String)this.getFormHM().get("title");
			String content=(String)this.getFormHM().get("content");
			PubFunc pub=new PubFunc();
			content=pub.keyWord_reback(content);
			this.getFormHM().put("rovkeName", "");
			String type=(String)this.getFormHM().get("type");	// 0：发送邮件  1：群发邮件
			String status=(String)this.getFormHM().get("status");
			//修改邮件模版
			RecordVo vo=new RecordVo("t_sys_msgtemplate");
			vo.setInt("template_id",Integer.parseInt(mailTempID));
			RecordVo avo=dao.findByPrimaryKey(vo);
			avo.setString("title",title);
			avo.setString("content",content);
			dao.updateValueObject(avo);
			
			//批量发送邮件
			RecordVo zpDbNameVo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname=zpDbNameVo.getString("str_value");
			ArrayList list=new ArrayList();
			AutoSendEMailBo bo=new AutoSendEMailBo(this.getFrameconn());
			if("0".equals(type))
			{
				if(a0100s.indexOf(",")!=-1){//给多个人发送邮件,而不是全部发送
					String[] temp=a0100s.split(",");
					for(int i=0;i<temp.length;i++){
						String newValue="";
						String s=temp[i];
						if(s.indexOf("/")!=-1){
							String a0100=s.substring(0,s.indexOf("/"));
							a0100 = PubFunc.decrypt(a0100);
							String z0301=s.substring(s.indexOf("/")+1);
							z0301 = PubFunc.decrypt(z0301);
							newValue = a0100+"/"+z0301;
						}else{
							String a0100 = PubFunc.decrypt(s);
							newValue = a0100;
						}
						list.add(newValue);
					}
				}
				else{
					String newValue="";
					String s=a0100s;
					if(s.indexOf("/")!=-1){
						String a0100=s.substring(0,s.indexOf("/"));
						a0100 = PubFunc.decrypt(a0100);
						String z0301=s.substring(s.indexOf("/")+1);
						z0301 = PubFunc.decrypt(z0301);
						newValue = a0100+"/"+z0301;
					}else{
						String a0100 = PubFunc.decrypt(s);
						newValue = a0100;
					}
					list.add(newValue);
				}
			}
			else if("1".equals(type))
			{
				StringBuffer sql=new StringBuffer("");
				
				String whl = (String)this.userView.getHm().get("hire_batch_mail_sql");//(String)this.getFormHM().get("str_whl");
				whl = PubFunc.keyWord_reback(whl);
				if("-1".equals(status))
				{
				  //sql.append("select "+dbname+"a01.a0100 from "+dbname+"a01 where "+dbname+"a01.a0100 not in (select distinct a0100 from zp_pos_tache)");
					sql.append(" select "+dbname+"a01.a0100 "+whl);
				}
				else
				{
					sql.append(" select zpt.a0100,zpt.zp_pos_id "+whl);
				  //sql.append("select zpt.a0100,zpt.zp_pos_id from zp_pos_tache zpt ,z03 ");
				  //sql.append(" where zpt.zp_pos_id=z03.z0301 and resume_flag='"+status+"'");
				  //if(!(this.getUserView().isAdmin()&&this.getUserView().getGroupId().equals("1"))&&this.getUserView().getUnit_id().length()>2)
				 //		sql.append(" and z0311 like '"+this.getUserView().getUnit_id().substring(2)+"%'");
					if(!this.userView.isSuper_admin())
					{
					    String operOrg = this.userView.getUnitIdByBusi("7"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板  
						if(operOrg.indexOf("`")==-1){
								sql.append(" and z0311 like '"+operOrg.substring(2)+"%'");
						}
						else
						{
								StringBuffer tempSql=new StringBuffer("");
								String[] temp=operOrg.split("`");
								for(int i=0;i<temp.length;i++)
								{
									tempSql.append(" or z0311 like '"+temp[i].substring(2)+"%'");
								}
								sql.append(" and ( "+tempSql.substring(3)+" ) ");
								
						}
					}
				}
			//	System.out.println("sql="+sql.toString());
				this.frowset=dao.search(sql.toString());
				while(this.frowset.next())
				{
					if(this.frowset.getString(1)==null||"null".equalsIgnoreCase(this.frowset.getString(1))){
						continue;
					}
					if("-1".equals(status))
					{
						list.add(this.frowset.getString(1));
					}
					else
					{
						list.add(this.frowset.getString(1)+"/"+this.frowset.getString(2));
					}
				}
				
				
				
			}
			String name="";
			if("1".equals(sendtype)){
		    	name=bo.sendEqualEMailByUserView1(dbname,list,mailTempID,type,status,this.getUserView());
		    	if(name!=null&&name.trim().length()>0){
		    		this.getFormHM().put("rovkeName", SafeCode.encode(PubFunc.encrypt(name)));
		    	}
			}
			else
			{
				bo.sendMessage(dbname, list, mailTempID, type, status, this.getUserView());
			
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
