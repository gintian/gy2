/*
 * Created on 2006-4-13
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.report.org_maintenance;

import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * 
 * <p>Title:增加填报单位信息-JSP显示页面</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 12, 2006:8:48:49 AM</p>
 * @author zhangfengjin
 * @version 1.0
 *
 */
public class ReportUnitAddTrans extends IBusiness {

	//增加填报单位信息页面显示
	public void execute() throws GeneralException {
		
		//设置单位编码最大长度
		String maxlen = "30";
		//点击增加填报单位时的列表信息的父接点单位编码
		String codeflag = (String)this.getFormHM().get("codeflag");	
		//System.out.println("codeflag=" + codeflag);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer sql = new StringBuffer();	
		
		//标记为"null"时，表明要增加顶级接点
		if("null".equals(codeflag)|| codeflag==null || "".equals(codeflag)){
			
			if(userView.isSuper_admin()){//系统管理员
				
				//逻辑如果不允许出现两个顶级填报单位即：两套填报编码规范
				/*sql.delete(0,sql.length());
				sql.append("select *  from tt_organization where grade=1");
				try{	
					this.frowset=dao.search(sql.toString());
					if(this.frowset.next()){	
						GeneralException e = new GeneralException("顶级填报单位已经存在！");
						throw GeneralExceptionHandler.Handle(e);						
					}
				}catch(Exception e){
				   e.printStackTrace();
				   throw GeneralExceptionHandler.Handle(e);
				}
				*/
				sql.delete(0,sql.length());
				
				sql.append("select max("+Sql_switcher.length("(unitcode)")+") as n  from tt_organization where grade=1");
				try{	
					this.frowset=dao.search(sql.toString());
					if(this.frowset.next()){	
						int n= this.frowset.getInt("n");
						if(n== 0){
							n=Integer.parseInt(maxlen);
							this.getFormHM().put("mlen","-1");
							this.getFormHM().put("lenInfo",ResourceFactory.getProperty("edit_report.info8"));
						}else{
							this.getFormHM().put("mlen",String.valueOf(n));
							this.getFormHM().put("lenInfo",ResourceFactory.getProperty("label.org.childmessage"));
						}
						this.getFormHM().put("maxlen" , String.valueOf(n));
						this.getFormHM().put("parentCode","");	
				    }else{
				    	this.getFormHM().put("mlen","-1");
				    	this.getFormHM().put("lenInfo",ResourceFactory.getProperty("edit_report.info8"));
						this.getFormHM().put("maxlen" , maxlen);
						this.getFormHM().put("parentCode","");	
				    }
				}catch(Exception e){
				   e.printStackTrace();
				   throw GeneralExceptionHandler.Handle(e);
				}
				
			}else{ //普通用户
				TTorganization ttorganization=new TTorganization(this.getFrameconn());
				RecordVo selfVo=ttorganization.getSelfUnit(userView.getUserName());
				codeflag = selfVo.getString("unitcode");
				
				String len = String.valueOf((Integer.parseInt(maxlen)- codeflag.length()));
				sql.delete(0,sql.length());
				sql.append("select max("+Sql_switcher.length("(unitcode)")+") as n  from tt_organization where parentid = '");
				sql.append(codeflag);
				sql.append("' and parentid <> unitcode");
				
				try{	
					this.frowset=dao.search(sql.toString());
					if(this.frowset.next())
					{		
						int temp = this.frowset.getInt("n");
						if(temp == 0){
							temp = Integer.parseInt(maxlen);
							this.getFormHM().put("mlen","-1");
							this.getFormHM().put("lenInfo",ResourceFactory.getProperty("edit_report.info8"));
						}else{
							this.getFormHM().put("mlen",String.valueOf(temp-codeflag.length()));
							this.getFormHM().put("lenInfo",ResourceFactory.getProperty("label.org.childmessage"));
						}
						this.getFormHM().put("maxlen", String.valueOf(temp-codeflag.length()));
				    }else
				    {
				    	this.getFormHM().put("mlen","-1");
				    	this.getFormHM().put("lenInfo",ResourceFactory.getProperty("edit_report.info8"));
				    	this.getFormHM().put("maxlen", len );
				    }
				}catch(Exception e){
				   e.printStackTrace();
				   throw GeneralExceptionHandler.Handle(e);
				}			
				this.getFormHM().put("parentCode",codeflag);	
			}
			
	
		}else{
			
			//标记不为空时，表明增加以标记为父接点的单位
			//查询以codeflag为父节点的自节点的单位编码长度，
			//即：以用户第一次设置的规定范围内的编码长度作为用户下次新增单位编码长度
			//select unitcode from tt_organization where parentid = '9' and parentid <> unitcode
			
			String len = String.valueOf((Integer.parseInt(maxlen)- codeflag.length()));
			sql.delete(0,sql.length());
			sql.append("select max("+Sql_switcher.length("(unitcode)")+") as n  from tt_organization where parentid = '");
			sql.append(codeflag);
			sql.append("' and parentid <> unitcode");
			
			try{	
				this.frowset=dao.search(sql.toString());
				if(this.frowset.next())
				{		
					int temp = this.frowset.getInt("n");
					if(temp == 0){
						temp = Integer.parseInt(maxlen);
						this.getFormHM().put("mlen","-1");
						this.getFormHM().put("lenInfo",ResourceFactory.getProperty("edit_report.info8"));
					}else{
						this.getFormHM().put("mlen",String.valueOf(temp-codeflag.length()));
						this.getFormHM().put("lenInfo",ResourceFactory.getProperty("label.org.childmessage"));
					}
					this.getFormHM().put("maxlen", String.valueOf(temp-codeflag.length()));
			    }else{
			    	this.getFormHM().put("mlen","-1");
			    	this.getFormHM().put("lenInfo",ResourceFactory.getProperty("edit_report.info8"));
			    	this.getFormHM().put("maxlen", len );
			    }
			}catch(Exception e){
			   e.printStackTrace();
			   throw GeneralExceptionHandler.Handle(e);
			}			
			this.getFormHM().put("parentCode",codeflag);			
		}	
		
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		this.getFormHM().put("start_date", df.format(new Date()));
		this.getFormHM().put("end_date","9999-12-31");
	}
	
	
}
