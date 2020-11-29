package com.hjsj.hrms.transaction.sys.options.template;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:SearchZbBySetName.java</p>
 * <p>Description:通知摸板</p>
 * <p>Company:hjsj</p>
 * <p>create time:2007.03.29 15:04 pm</p>
 * @author Lizhenwei
 * @version 1.0
 */


public class SearchZbBySetName extends IBusiness{
	public void execute() throws GeneralException{
		try
		{
			//通知模板内容
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String content ="[人员姓名(A0100)] :\r\n    您好！\r\n   很高兴您的简历符合我们  [应聘职位(z0311)]  职位的要求，感谢您申请本公司的职位\r\n\r\n                                                                        [(~系统时间~)]";
			String sql_str = "select codeitemid,codeitemdesc from codeitem where codesetid ='36' and( codeitemid ='1' or codeitemid ='2' or codeitemid='3' or codeitemid='4') order by codeitemid";
			ArrayList list = new ArrayList();//招聘环节列表
			String id = (String)this.getFormHM().get("id");//子系统号
			ArrayList zb_list = new ArrayList();//指标列表
			ArrayList zbj_list = new ArrayList();//指标集列表
		
			String fieldSetId = (String)this.getFormHM().get("setid");//指标集的id
			//String zploop = (String)this.getFormHM().get("zploop_id");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			String zploop=(String)hm.get("zploop");
			if("1".equals(zploop) && fieldSetId == null){//为适应初次筛选的模板
				zbj_list.add(new CommonData("","初次筛选信息表"));
				zb_list.add(new CommonData(""," "));
				zb_list.add(new CommonData("人员姓名(A0100)","人员姓名(A0100)"));
				zb_list.add(new CommonData("应聘职位(z0311)","应聘职位(z0311)"));
				zb_list.add(new CommonData("(~系统时间~)","(~系统时间~)"));
				 this.frowset = dao.search(sql_str);
				    while(this.frowset.next()){
				        CommonData vo=new CommonData(this.frowset.getString("codeitemid"),this.frowset.getString("codeitemdesc"));
						list.add(vo);
			        }
				
				this.getFormHM().put("content",content);
				this.getFormHM().put("zpLoop_list",list);
				this.getFormHM().put("name","");
				this.getFormHM().put("title","");
				this.getFormHM().put("address","");
				this.getFormHM().put("zpLoopNew","");
				this.getFormHM().put("type",0+"");
				this.getFormHM().put("template_id","");
				this.getFormHM().put("zb_id","");
				
			}
				
				
			else if(fieldSetId == null || "".equals(fieldSetId)){//首次进入
				String setsql ="";
				if("32".equals(id))
				{
					setsql="select fieldsetid , fieldsetdesc from t_hr_busitable where useflag <> 0 and fieldsetid = 'Z05'";
				}
				else
					setsql="select fieldsetid , fieldsetdesc from fieldset where useflag <> 0";
				String firstSetId = "";
				boolean b = false;
				try {
					this.frowset = dao.search(setsql);
					while(this.frowset.next()){
						if(!b){
							firstSetId = this.frowset.getString("fieldsetid");
							b=true;
						}
						CommonData dataobj = new CommonData();
						String setid = this.getFrowset().getString("fieldsetid");
						String setdesc = this.getFrowset().getString("fieldsetdesc");
						dataobj = new CommonData(setid,"("+setid+")"+setdesc);
						zbj_list.add(dataobj);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				String itemsql ="";
				if("32".equals(id)){
					itemsql = "select itemid,itemdesc  from t_hr_busiField  where useflag <> 0 and fieldsetid ='"+firstSetId+"'";
				}
				else
				     itemsql= "select itemid,itemdesc  from fielditem where useflag <> 0 and fieldsetid ='"+firstSetId+"'";
				CommonData top = new CommonData();
				top = new CommonData("","");
				zb_list.add(top);
				zb_list.add(new CommonData("(~系统时间~)","(~系统时间~)"));
				try {
					this.frowset = dao.search(itemsql);
					while(this.frowset.next()){
						String itemid = this.frowset.getString("itemid");
						String itemdesc = this.frowset.getString("itemdesc");
						if("32".equals(id)){
							if(!"A0100".equalsIgnoreCase(itemid) && !"Z0503".equalsIgnoreCase(itemid) && !"Z0505".equalsIgnoreCase(itemid) && !"Z0507".equalsIgnoreCase(itemid) && !"Z0509".equalsIgnoreCase(itemid))
		                             continue;	
							if("a0100".equalsIgnoreCase(itemid))
								itemdesc="人员姓名";
						}
						CommonData dataobj =null;
						dataobj = new CommonData(itemdesc+"("+itemid+")","("+itemid+")"+itemdesc);
						zb_list.add(dataobj);
					}
					if("32".equals(id)){
						CommonData dataobj=new CommonData("应聘职位(z0311)","(z0311)应聘职位");
						zb_list.add(dataobj);
					}
					
					 this.frowset = dao.search(sql_str);
					    while(this.frowset.next()){
					    	
							CommonData vo=new CommonData(this.frowset.getString("codeitemid"),this.frowset.getString("codeitemdesc"));
							list.add(vo);
				        }
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					this.getFormHM().put("zpLoop_list",list);
					this.getFormHM().put("content",content);
					this.getFormHM().put("name","");
					this.getFormHM().put("title","");
					this.getFormHM().put("address","");
					this.getFormHM().put("zpLoop","");
					this.getFormHM().put("type",0+"");
					this.getFormHM().put("template_id","");
					this.getFormHM().put("zb_id","");
				}
				
				
			}else{//改变指标集
				String itemsql = "select itemid,itemdesc  from fielditem where useflag <> 0 and fieldsetid ='"+fieldSetId+"'";
				CommonData top = new CommonData();
				top = new CommonData("","");
				zb_list.add(top);
				try {
					this.frowset = dao.search(itemsql);
					while(this.frowset.next()){
						String itemdesc = this.frowset.getString("itemdesc");
						CommonData dataobj = new CommonData();
						dataobj = new CommonData(itemdesc,itemdesc);
						zb_list.add(dataobj);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
				
			this.getFormHM().put("zbj_list",zbj_list);
				
			this.getFormHM().put("zb_list",zb_list);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		        throw GeneralExceptionHandler.Handle(ex);   
		}
		
			
	}
}


