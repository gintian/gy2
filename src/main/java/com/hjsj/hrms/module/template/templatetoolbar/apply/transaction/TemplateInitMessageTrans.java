package com.hjsj.hrms.module.template.templatetoolbar.apply.transaction;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;
/**
 * <p>Title:TemplateInitMessageTrans.java</p>
 * <p>Description>:不走审批的提交数据提交前查询模板信息</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2016-9-13 下午02:28:45</p>
 * <p>@author:hej</p>
 * <p>@version: 7.0</p>
 */
public class TemplateInitMessageTrans extends IBusiness{
	
	
	@Override
    public void execute() throws GeneralException {
		try{
			String tabid=(String)this.getFormHM().get("tab_id");
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
			
			boolean email_staff=tablebo.isEmail_staff();//邮件通知到本人
			String template_staff=tablebo.getTemplate_staff();  //员工本人的邮件模板
			String template_bos=tablebo.getTemplate_bos();    //业务办理人员的邮件模板
			ArrayList mailTempletList=new ArrayList();
			String    mailTempletID="";
			
			String user_="";
			String user_h="";
			String title=tablebo.getTable_vo().getString("name");  //"人事异动";
			String context="总部有关部门:\r\n     根据工作安排，        调入|调出|调动总部工作,请协助办理相关手续。\r\n   此致";
			context+="\r\n敬礼\r\n                                     人力资源部";
			ContentDAO dao=new ContentDAO(this.getFrameconn());	
			if((template_bos!=null&&template_bos.trim().length()>0)||(template_staff!=null&&template_staff.trim().length()>0)){
				String str="";
				if(template_bos!=null&&template_bos.trim().length()>0)
					str+=","+template_bos;
				if(template_staff!=null&&template_staff.trim().length()>0)
					str+=","+template_staff;
				RowSet rowSet=dao.search("select * from email_name where id in ("+str.substring(1)+")");
				while(rowSet.next()){
					String id=rowSet.getString("id");
					String name=rowSet.getString("name");
					String subject=rowSet.getString("subject");
					String content=Sql_switcher.readMemo(rowSet,"content");
					
					if(template_bos.equals(id)){
						title=subject;
						context=content;
						mailTempletID=id;
					}
					CommonData da=new CommonData(id,name);
					mailTempletList.add(da);
				}
			}
			this.getFormHM().put("mailTempletList", mailTempletList);
			this.getFormHM().put("mailTempletID", mailTempletID);
			if (!this.userView.hasTheFunction("2701515") && !this.userView.hasTheFunction("0C34815")
					&& !this.userView.hasTheFunction("32015") && !this.userView.hasTheFunction("325010115")
					&& !this.userView.hasTheFunction("324010115") && !this.userView.hasTheFunction("010701")
					&& !this.userView.hasTheFunction("32115") && !this.userView.hasTheFunction("400040115")
					&& !this.userView.hasTheFunction("33001015")&& !this.userView.hasTheFunction("33101015"))
				this.getFormHM().put("email_staff", "False");
			else
				this.getFormHM().put("email_staff", String.valueOf(email_staff));
			this.getFormHM().put("template_staff",template_staff);
			this.getFormHM().put("template_bos",template_bos);
			
			/*判断是否 调用中建接口
			 * sso_templetOwner=1:(2;lia;1~4;lsj;0~7;#;1)&51:(54:Usr00000004;1)
			 */
			if(SystemConfig.getPropertyValue("sso_zjz_oa_sendmail")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("sso_zjz_oa_sendmail"))){
				if(SystemConfig.getPropertyValue("sso_templetOwner")!=null){
					
					RowSet rowSet=null;
					String str=	SystemConfig.getPropertyValue("sso_templetOwner");
					String[] temps=str.split("&");
					for(int i=0;i<temps.length;i++){
						String[] temps2=temps[i].split(":");
						if(temps2[0].equals(tabid)){
							String temp=temps2[1].substring(1).substring(0, temps2[1].length()-1);
							String[] temps3=temp.split("~");
							for(int j=0;j<temps3.length;j++){
								String[] temps4=temps3[j].split(";");
								if("#".equals(temps4[1]))
									continue;
									
								rowSet=dao.search("select * from operuser where username='"+temps4[1]+"'");
								if(rowSet.next()){
									user_h+=",4:"+temps4[1];
									user_+=","+temps4[1];
								}
								else{
									user_h+=",1:"+temps4[1];
									rowSet=dao.search("select * from "+temps4[1].substring(0,3)+"A01 where a0100='"+temps4[1].substring(3)+"'");
									if(rowSet.next())
										user_+=","+rowSet.getString("a0101");
								}
							}	
						}
					}
				}
			}
			this.getFormHM().put("user_",user_);
			this.getFormHM().put("user_h",user_h);
			this.getFormHM().put("title",title);
			this.getFormHM().put("context",context);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
