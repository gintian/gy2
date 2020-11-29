package com.hjsj.hrms.transaction.kq.kqself.net_signin;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.kqself.NetSignIn;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.machine.RepairKqCard;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SaveMakeupNetSignInTrans extends IBusiness{
	
	   
	   public void execute()throws GeneralException
	   {
		   String singin_flag=(String)this.getFormHM().get("singin_flag");
		   String ip_addr=(String)this.getFormHM().get("ip_addr");
		   
//		   this.userView.setRemote_ip(ip_addr);
		   if(singin_flag==null||singin_flag.length()<=0)
			   return;
		   
		   //签到是否限制IP 0：不绑定 1：绑定（默认）2：有IP绑定，无IP不绑定
		   KqParam kqParam = KqParam.getInstance();
		   String net_sign_check_ip = kqParam.getNetSignCheckIP();
		   
		   if(!"0".equals(net_sign_check_ip))
		   {
				 this.userView.setRemote_ip(ip_addr);
		   }
		   String nbase=this.userView.getDbname();
		   String a0100=this.userView.getA0100();
		   String work_date=(String)this.getFormHM().get("makeup_date");		   
		   String work_tiem=(String)this.getFormHM().get("makeup_time");
		   String oper_cause= (String)this.getFormHM().get("oper_cause");
		   
		   //得到服务器时间，补签时间不能大于服务器的当前时间；wangy
		   NetSignIn netSignIn=new NetSignIn(this.userView,this.getFrameconn());
		   String work_date_server=netSignIn.getWork_date();
		   String work_tiem_server=netSignIn.getWork_time();
		   
		   if(work_date==null||work_date.length()<=0||work_tiem==null||work_tiem.length()<=0)
		   {
			   this.getFormHM().put("mess","时间日期不能为空，补签申请失败!");
			   return;
		   } else {
			   String z1str=(String)this.getFormHM().get("z1str"); 
			   if (! KqUtilsClass.comparentWithKqDuration(work_date)) {
				   this.getFormHM().put("mess", z1str + "所在考勤期间已封存！");
				   return ;
				}
		   }
		   work_date=work_date.replaceAll("-","\\.");
		   try
	  	   {
	  		   Date dd=DateUtils.getDate(work_date, "yyyy.MM.dd");
	  	   }catch(Exception e)
	  	   {
	  		 throw GeneralExceptionHandler.Handle(new GeneralException("","日期格式不正确！yyyy-MM-dd","",""));
	  	   }
	  	   try
	  	   {
	  		   Date dd=DateUtils.getDate(work_tiem, "HH:mm");
	  	   }catch(Exception e)
	  	   {
	  		 throw GeneralExceptionHandler.Handle(new GeneralException("","时间格式不正确！HH:mm","",""));
	  	   }
	  	   
	  	   /**服务器时间与补签时间对比;补签不等大于服务的当前时间; wangy 开始**/
	  	   work_date_server=work_date_server.replaceAll("\\.","-");
	  	   String work_date2=work_date.replaceAll("\\.","-");
	  	   SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	  	   java.util.Calendar c1=java.util.Calendar.getInstance();
	  	   java.util.Calendar c2_server=java.util.Calendar.getInstance();
	  	   try
	  	   {
	  		 c1.setTime(formatter.parse(work_date2));
	  		 c2_server.setTime(formatter.parse(work_date_server));
	  		 
	  	   }catch(Exception e)
	  	   {
	  		 throw GeneralExceptionHandler.Handle(new GeneralException("","日期时间类型不对！HH:mm","",""));
	  	   }
	  	   int result=c1.compareTo(c2_server);
	  	   if(result>0)
	  	   {
	  		 throw GeneralExceptionHandler.Handle(new GeneralException("","补签日期不能大于当前日期！","","")); 
	  	   }else if(result==0)
	  	   {
	  		   //日期相等就要对比时间
	  		   SimpleDateFormat form = new SimpleDateFormat("HH:mm");
	  		   java.util.Calendar c1_1=java.util.Calendar.getInstance();
		  	   java.util.Calendar c2_2_server=java.util.Calendar.getInstance();
		  	   try
		  	   {
		  		 c1_1.setTime(form.parse(work_tiem));
		  		 c2_2_server.setTime(form.parse(work_tiem_server));
		  	   }catch(Exception e)
		  	   {
		  		 throw GeneralExceptionHandler.Handle(new GeneralException("","时间类型不对！","","")); 
		  	   }
		  	  int result_1=c1_1.compareTo(c2_2_server);
		  	  if(result_1>0)
		  		throw GeneralExceptionHandler.Handle(new GeneralException("","补签时间不能大于当前时间！","",""));
	  	   }
	  	   /**结束**/
	  	   
		   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	  	   String strDate = sdf.format(new java.util.Date());
	  	   Date oper_time=DateUtils.getDate(strDate,"yyyy-MM-dd HH:mm");
		   boolean isCorrect=false;
		   String mess="";
		   if(netSignIn.validateIP(nbase,a0100,net_sign_check_ip,singin_flag))
		   {
			   String cardno=netSignIn.getKqCard(nbase,a0100);
			   if(cardno==null||cardno.length()<=0)
	    	   {
	    			throw GeneralExceptionHandler.Handle(new GeneralException("","没有分配考勤卡号，不能网上考勤！","",""));
	    	   }
			   
			   RepairKqCard repairKqCard=new RepairKqCard(this.getFrameconn(),this.userView);
			   int numLimit = repairKqCard.getRepairCardNumLimit();
		       if (numLimit > 0) {
				   ArrayList datelist =RegisterDate.getKq_duration(work_date,this.getFrameconn()); 
				   if(datelist!=null&&datelist.size()>0)
					{
						String start_date=datelist.get(0).toString();
						String end_date=datelist.get(datelist.size()-1).toString();
						if(repairKqCard.isOverTopRepairdaynum(numLimit,nbase,a0100,start_date,end_date))
						{
							throw GeneralExceptionHandler.Handle(new GeneralException("",
							        ResourceFactory.getProperty("kq.repair.over.num_hint_1") 
		                            + numLimit 
		                            + ResourceFactory.getProperty("kq.repair.over.num_hint_2"),
		                            "",""));
						}
					}
			   }
			   if("0".equals(singin_flag))//签到
			   {
				   if(!netSignIn.IsExists(nbase,a0100,work_date,work_tiem))
					   throw GeneralExceptionHandler.Handle(new GeneralException("","规定时间间隔内不能签多次！","","")); 
				   if(!netSignIn.ifNetSign(nbase,a0100,work_date,work_tiem))
				   {
				 		 throw GeneralExceptionHandler.Handle(new GeneralException("","不可以在请假时间范围内，签到签退！","","")); 
				   }
				   /*if(!netSignIn.signInScope(nbase,a0100,this.userView.getUserOrgId(),this.userView.getUserDeptId(),this.userView.getUserPosId(),work_date,work_tiem,singin_flag))
				   {
					   throw GeneralExceptionHandler.Handle(new GeneralException("","补签到申请无效!","","")); 
				   }else
				   {
					   String class_id=netSignIn.getClass_id();
					   mess=netSignIn.signInCount(class_id,work_date,work_tiem,singin_flag);
					   isCorrect=netSignIn.onNetSign(nbase,a0100,cardno,oper_cause,oper_time,work_date,work_tiem,"补签到","02",ip_addr);
					   if(mess!=null&&mess.length()>0)
						   mess=","+mess;
					   if(isCorrect)
						   mess="补签到申请成功"+mess+"!";
					   else
						   mess="补签到申请失败!";
				   }*/
				   netSignIn.signInScope(nbase,a0100,this.userView.getUserOrgId(),this.userView.getUserDeptId(),this.userView.getUserPosId(),work_date,work_tiem,singin_flag);
				   String class_id=netSignIn.getClass_id();
				   mess=netSignIn.signInCount(class_id,work_date,work_tiem,singin_flag);
				 //不捆绑IP wangyao
				 if("0".equals(net_sign_check_ip))
				   {
					   String loact_ip=this.userView.getRemote_ip();//得到ip,不绑定
					   isCorrect=netSignIn.onNetSign(nbase,a0100,cardno,oper_cause,oper_time,work_date,work_tiem,"补签到","02",loact_ip);
				   }else
				   {
					   isCorrect=netSignIn.onNetSign(nbase,a0100,cardno,oper_cause,oper_time,work_date,work_tiem,"补签到","02",ip_addr);
				   }
				   if(mess!=null&&mess.length()>0)
					   mess=","+mess;
				   if(isCorrect)
				   {
					   mess="补签到申请成功"+mess+"!";
				   }else
				   {
					   mess="补签到申请失败!";
				   }	
			 }else if("1".equals(singin_flag))//签退
			 {
				   if(!netSignIn.IsExists(nbase,a0100,work_date,work_tiem))
					   throw GeneralExceptionHandler.Handle(new GeneralException("","规定时间间隔内不能签多次！","","")); 
				   if(!netSignIn.ifNetSign(nbase,a0100,work_date,work_tiem))
				   {
				 		 throw GeneralExceptionHandler.Handle(new GeneralException("","不可以在请假时间范围内，签到签退！","","")); 
				   }
				   /*if(!netSignIn.signInScope(nbase,a0100,this.userView.getUserOrgId(),this.userView.getUserDeptId(),this.userView.getUserPosId(),work_date,work_tiem,singin_flag))
				   {
					   throw GeneralExceptionHandler.Handle(new GeneralException("","补签退申请无效!","","")); 
				   }else
				   {
					   String class_id=netSignIn.getClass_id();
					   mess=netSignIn.signInCount(class_id,work_date,work_tiem,singin_flag);
					   isCorrect=netSignIn.onNetSign(nbase,a0100,cardno,oper_cause,oper_time,work_date,work_tiem,"补签退","02",ip_addr);
					   if(mess!=null&&mess.length()>0)
						   mess=","+mess;
					   if(isCorrect)
						   mess="补签退申请成功"+mess+"!";
					   else
						   mess="补签退申请失败!";
				   }*/	
				   netSignIn.signInScope(nbase,a0100,this.userView.getUserOrgId(),this.userView.getUserDeptId(),this.userView.getUserPosId(),work_date,work_tiem,singin_flag);
				   String class_id=netSignIn.getClass_id();
				   mess=netSignIn.signInCount(class_id,work_date,work_tiem,singin_flag);
				   //不捆绑IP wangyao
				   if("0".equals(net_sign_check_ip))
				   {
					   String loact_ip=this.userView.getRemote_ip();//得到ip,不绑定
					   isCorrect=netSignIn.onNetSign(nbase,a0100,cardno,oper_cause,oper_time,work_date,work_tiem,"补签退","02",loact_ip);
				   }else
				   {
					   isCorrect=netSignIn.onNetSign(nbase,a0100,cardno,oper_cause,oper_time,work_date,work_tiem,"补签退","02",ip_addr);
				   }
//				   isCorrect=netSignIn.onNetSign(nbase,a0100,cardno,oper_cause,oper_time,work_date,work_tiem,"补签退","02",ip_addr);
				   if(mess!=null&&mess.length()>0)
					   mess=","+mess;
				   if(isCorrect)
				   {
					   mess="补签退申请成功"+mess+"!";					   
				   }						  
				   else
					   mess="补签退申请失败!";
			   }			   
		   }else
		   {
			   throw GeneralExceptionHandler.Handle(new GeneralException("","本机IP与您的指定的IP不对应！","",""));
		   }
		   this.getFormHM().clear();
		   mess=com.hrms.frame.codec.SafeCode.decode(mess);
		   this.getFormHM().put("mess",mess);
		   
	   }	  
	   
}

