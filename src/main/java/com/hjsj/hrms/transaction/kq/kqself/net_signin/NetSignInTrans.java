package com.hjsj.hrms.transaction.kq.kqself.net_signin;

import com.hjsj.hrms.businessobject.kq.kqself.NetSignIn;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 网上签到签退
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 1, 2007:10:05:26 AM</p> 
 *@author dengcan
 *@version 4.0
 */
public class NetSignInTrans extends IBusiness{	
    private static final long serialVersionUID = 1L;

    public void execute()throws GeneralException
	   {
		   String mess = "";
		   String signflag = "";
		   
		   try
		   {
			   String singin_flag = (String)this.getFormHM().get("singin_flag");
			   String ip_addr = (String)this.getFormHM().get("ip_addr");
			   String isMobile = (String)this.getFormHM().get("isMobile");
			   this.getFormHM().remove("isMobile");
			   if(singin_flag==null||singin_flag.length()<=0)
				   return;
				   
			   //签到是否限制IP 0：不绑定 1：绑定（默认）2：有IP绑定，无IP不绑定
			   KqParam kqParam = KqParam.getInstance();
			   String net_sign_check_ip = kqParam.getNetSignCheckIP();
			   
			   if(!"0".equals(net_sign_check_ip))
			   {
			       this.userView.setRemote_ip(ip_addr);
				   if("1".equals(isMobile))
	                   ip_addr = this.userView.getRemote_ip();
			   }
				 
			   NetSignIn netSignIn = new NetSignIn(this.userView,this.getFrameconn());
			   
			   String nbase = this.userView.getDbname();
			   String a0100 = this.userView.getA0100();
			   String work_date = netSignIn.getWork_date();
			   String work_tiem = netSignIn.getWork_time();
			   
			   boolean isCorrect = false;
			   
			   String net_sign_approve = kqParam.getNetSignApprove();
			   String sp_flag = "03";
			   if(net_sign_approve!=null && "1".equals(net_sign_approve))
					sp_flag = "02";
			   
			   boolean validateIP = false;
			   try
			   {
			       validateIP = netSignIn.validateIP(nbase, a0100, net_sign_check_ip,singin_flag);
			   }
			   catch (GeneralException e) {
			       mess = e.getErrorDescription();
			       e.printStackTrace();
			   }
			   catch (Exception e) {
			       e.printStackTrace();
			   }
	
			   if("1".equals(isMobile) || validateIP)
			   {
				   String cardno = netSignIn.getKqCard(nbase,a0100);
				   if(cardno==null || cardno.length()<=0)
		    	   {
					   mess = ResourceFactory.getProperty("kq.netsign.error.notsetcard");
		    	   }
				   
				   if("0".equals(singin_flag))//签到
				   {
					   if(!netSignIn.IsExists(nbase,a0100,work_date,work_tiem))
					   {
						   this.getFormHM().put("mess",ResourceFactory.getProperty("kq.netsign.error.notrepeatsign"));
						   return;					   
					   }	
					   
					   if(!netSignIn.ifNetSign(nbase,a0100,work_date,work_tiem))
					   {
					       isCorrect=false;
					 	   this.getFormHM().put("mess",ResourceFactory.getProperty("kq.netsign.error.notsigninleavetime"));
					 	   return;
					   }
					   ArrayList classList = netSignIn.getClassID(nbase, a0100, this.userView.getUserOrgId(), this.userView.getUserDeptId(), this.userView.getUserPosId(), work_date);
					   if (classList == null || classList.size() <= 0) 
					   {
						   this.getFormHM().put("mess", ResourceFactory.getProperty("kq.netsign.error.notarrange.in"));
						   return;
					   }
					   if(!netSignIn.signInScope(nbase,a0100,this.userView.getUserOrgId(),this.userView.getUserDeptId(),this.userView.getUserPosId(),work_date,work_tiem,singin_flag))
					   {
						   this.getFormHM().put("mess", ResourceFactory.getProperty("kq.netsign.error.notinvalidtime.in"));
						   return;					 
					   }else
					   {
						   String class_id = netSignIn.getClass_id();
						   mess = netSignIn.signInCount(class_id,work_date,work_tiem,singin_flag);
						   //不捆绑IP wangyao
						   if("0".equals(net_sign_check_ip))
						   {
							   String loact_ip = this.userView.getRemote_ip();//得到ip,不绑定
							   isCorrect = netSignIn.onNetSign(nbase,a0100,cardno,"",null,work_date,work_tiem,
									   ResourceFactory.getProperty("train.b_plan.reg.on"),sp_flag,loact_ip);
						   }else
						   {
							   isCorrect = netSignIn.onNetSign(nbase,a0100,cardno,"",null,work_date,work_tiem,
									   ResourceFactory.getProperty("train.b_plan.reg.on"),sp_flag,ip_addr);
						   }
						   if(mess!=null&&mess.length()>0)
							   mess = "," + mess;
						   if(isCorrect)
						   {
							   if(netSignIn.isIs_sign())
							   {
								   signflag="0";
							   }else
							   {
								   signflag="3";
							   }
							   mess = ResourceFactory.getProperty("kq.netsign.in.success") + mess + "！";
						   }else
						   {
							   mess = ResourceFactory.getProperty("kq.netsign.in.fail");
						   }						   
					   }
		
				   }else if("1".equals(singin_flag))//签退
				   {
					   if(!netSignIn.IsExists(nbase,a0100,work_date,work_tiem))
					   {
						   this.getFormHM().put("mess", ResourceFactory.getProperty("kq.netsign.error.notrepeatsign"));
						   return;
					   }	
					   if(!netSignIn.ifNetSign(nbase,a0100,work_date,work_tiem))
					   {
					 		 isCorrect=false;
					 		 this.getFormHM().put("mess", ResourceFactory.getProperty("kq.netsign.error.notsigninleavetime"));
					 		 return;
					   }
					   ArrayList classList = netSignIn.getClassID(nbase, a0100, this.userView.getUserOrgId(), this.userView.getUserDeptId(), this.userView.getUserPosId(), work_date);
					   if (classList == null || classList.size() <= 0) 
					   {
						   this.getFormHM().put("mess", ResourceFactory.getProperty("kq.netsign.error.notarrange.out"));
						   return;
					   }
					   if(!netSignIn.signInScope(nbase,a0100,this.userView.getUserOrgId(),this.userView.getUserDeptId(),this.userView.getUserPosId(),work_date,work_tiem,singin_flag))
					   {
						   this.getFormHM().put("mess", ResourceFactory.getProperty("kq.netsign.error.notinvalidtime.out"));
						   return;
					   }else
					   {
						   String class_id=netSignIn.getClass_id();
						   mess=netSignIn.signInCount(class_id,work_date,work_tiem,singin_flag);
						   if("0".equals(net_sign_check_ip))
						   {
							   String loact_ip=this.userView.getRemote_ip();//得到ip,不绑定
							   isCorrect=netSignIn.onNetSign(nbase,a0100,cardno,"",null,work_date,work_tiem,
									   ResourceFactory.getProperty("train.b_plan.reg.off"),sp_flag,loact_ip);
						   }else
						   {
							   isCorrect=netSignIn.onNetSign(nbase,a0100,cardno,"",null,work_date,work_tiem,
									   ResourceFactory.getProperty("train.b_plan.reg.off"),sp_flag,ip_addr);
						   }
						   if(mess!=null&&mess.length()>0)
							   mess=","+mess;
						   if(isCorrect)
						   {
							   mess = ResourceFactory.getProperty("kq.netsign.out.success") + mess + "！";
							   if(netSignIn.isIs_sign())
							   {
								   signflag="1";
							   }else
							   {
								   signflag="3";
							   }
						   }						  
						   else
							   mess = ResourceFactory.getProperty("kq.netsign.out.fail");
					   }			
					   
				   }			   
			   }
//			   else
//			   {
//				   mess = ResourceFactory.getProperty("kq.netsign.error.ipnotequal");
//			   }
		   
			   this.getFormHM().put("mess",mess);
			   this.getFormHM().put("signflag",signflag);
		   }
		   catch(Exception e)
		   {
			   e.printStackTrace();
			   throw GeneralExceptionHandler.Handle(e);
		   }
	   }	  
	   
}
