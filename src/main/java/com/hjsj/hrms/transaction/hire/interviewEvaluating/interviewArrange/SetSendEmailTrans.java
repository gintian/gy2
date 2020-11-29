package com.hjsj.hrms.transaction.hire.interviewEvaluating.interviewArrange;


import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hjsj.hrms.businessobject.hire.AutoSendEMailBo;
import com.hjsj.hrms.businessobject.hire.InterviewEvaluatingBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * <p>title:SetSendEmail.java</p>
 * <p>Description:发送email</p>
 * <p>Company:hjsj</p>
 * <p>create time:2007.04.05  10:13:05 am</P>
 * @author lizhenwei
 * @version 1.0
 *
 */
public class SetSendEmailTrans extends IBusiness {
        public void execute() throws GeneralException {
        	FileOutputStream fileOut = null;
        	try{
        		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
        	    //String http=(String)hm.get("address")+"hire/employNetPortal/search_zp_position.do?b_search=search&entery=1&aid="+;
        		String flag = "";// 邮件发送标识 0：成功，1：未配置邮件服务器，2：邮件地址错误
        		ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
    			HashMap parameterMap=parameterXMLBo.getAttributeValues();
    			boolean isHref=false;
     			if(parameterMap!=null&&parameterMap.get("interviewing_itemid")!=null)
     			{
     				isHref=true;
     			}
        		String isMailField =(String)this.getFormHM().get("isMailField");
        		String dbName = (String)this.getFormHM().get("dbname");
        		String zploop=(String)this.getFormHM().get("zploop");
        		String id = (String)this.getFormHM().get("id");
        		
        		String a0100=(String)this.getFormHM().get("a0100");
        		//2014.11.3 xxd 当从员工录用界面进入消息发送时，传进来的值为数组，不进行直接解密，在进行sql拼装的时候再进行解密
        		if(zploop != null && !"4".equals(zploop)){
        			id = PubFunc.decrypt(id);
        			a0100 = PubFunc.decrypt(a0100);
        		}
        		
        		String title=(String)this.getFormHM().get("title");
        		String zp_pos_id=(String)this.getFormHM().get("zp_pos_id");
        		zp_pos_id = PubFunc.decrypt(zp_pos_id);
        		String zpbatch=(String)this.getFormHM().get("zpbatch");
        		String codeid=(String)this.getFormHM().get("codeid");
        		
        		RowSet rowSet = null;
        		FieldItem item=DataDictionary.getFieldItem("z0503");
        		String address = (String) hm.get("address");//登录的地址,在发送邮件时//被替换成全角的了,替换回来
        		address = address.replaceAll("／", "/");
        		String http=address+"hire/employNetPortal/search_zp_position.do?b_search=search&entery=1&aid="+SafeCode.encode(PubFunc.encrypt(a0100));
        		if(this.getUserView().getVersion()>=50)
        		{
        			 http=address+"hire/hireNetPortal/search_zp_position.do?b_search=search&entery=1&aid="+SafeCode.encode(PubFunc.encrypt(a0100));
        		}
        		
        		ContentDAO dao = new ContentDAO(this.frameconn);
        		AutoSendEMailBo autoSendEMailBo = new AutoSendEMailBo(this.getFrameconn());
        		String from_addr=autoSendEMailBo.getFromAddr();
					//SendEmail sendEmail=new SendEmail();
        		EMailBo bo=null;
        		boolean aflag=true;
        		String username="";
        		String password="";
        		String from = from_addr;
        		 boolean flagS=false;
        		EmailTemplateBo  etb = new EmailTemplateBo(this.getFrameconn());
        		try
        		{
        		   LazyDynaBean bean = etb.getFromAddrByUser(userView);//查找业务用户的邮箱
        		   //如果业务用户设置了邮箱
     			   if(bean.get("email")!=null&&!"".equals((String)bean.get("email"))&&bean.get("pw")!=null&&!"".equals((String)bean.get("pw")))
     			   {
     				   username=(String)bean.get("email");
     				   password=(String)bean.get("pw");
     				   from=username;
     				   bo = new EMailBo(this.getFrameconn(),true);
     				   bo.setUsername(username);
	 		    	   bo.setPassword(password);
	 		    	  if(bo.configTransfor())
	 		          {
	 		        	  flagS=true;
	 		          }
     			   }
     			   if(!flagS)
	 		    	  {
	 		    		   LazyDynaBean bean2 = etb.getOperuserBean(userView);
	 		    		   if(bean2.get("email")!=null&&!"".equals((String)bean2.get("email"))&&bean2.get("pw")!=null&&!"".equals((String)bean2.get("pw")))
	 					   {
	 						   username=(String)bean2.get("email");
	 						   password=(String)bean2.get("pw");
	 						   from=username;
	 						   bo.setUsername(username);
	 		 		    	   bo.setPassword(password);
	 		 		    	   if(bo.configTransfor())
	 		 		    	   {
	 		 		    		   flagS=true;
	 		 		    	   }
	 					   }
	 		    	  }
     			//如果业务用户没有设置邮箱，就按系统设置的邮箱
	 		      if(!flagS)
	 		       {
	 		    	  try
	 		    	   {
	 		    		   bo= new EMailBo(this.getFrameconn(),true,"");
	 		    		   from=from_addr;
	 		    	   }
	 		    	   catch(Exception ex)
	 		    	   {
	 		    		  aflag=false;
	 		    	   }
	 		       }
        		}
        		catch(Exception e)
        		{
        			aflag=false;
        		}
        		
				if(!aflag)
					flag = "1";
				else//如果配置了邮件服务器了
				{			/************************zploop 2发给应聘者 3发送给考官      zpbatch群发   4:多选人员发送,应该来自于员工录用*********/                         
						if("2".equalsIgnoreCase(zploop))
						{
							if(zpbatch==null||zpbatch.trim().length()==0){
				    			StringBuffer sql=new StringBuffer();
				    			ArrayList rlist = new ArrayList();
				    			String upsql="update z05 set z0511='";//发送邮件之后，显示为"已通知"
				        		if(this.userView.getUserFullName()!=null&&this.userView.getUserFullName().length()!=0){
				        			 upsql= upsql+this.userView.getUserFullName()+"',STATE='22' where z0501=?";
				        		}else{
				        			upsql= upsql+this.userView.getUserName()+"',sate='22' where z0501=?";
				        		}

					    		sql.append("select z0501,"+dbName+"a01.a0101,z05.a0100,organization.codeitemdesc,");
					    		sql.append(Sql_switcher.dateToChar("z0509", "yyyy-mm-dd hh:mm")+" as z0509,");
			    	    		sql.append("z05.z0503,"+dbName+"A01."+isMailField+" from z05 left join "+dbName+"a01 on z05.a0100="+dbName+"a01.a0100 left join zp_pos_tache on z05.a0100=zp_pos_tache.a0100 and zp_pos_tache.zp_pos_id = '"+zp_pos_id+"'"        
				    				+" left join z03 on zp_pos_tache.zp_pos_id=z03.z0301 left join organization  on z03.z0311=organization.codeitemid where z05.z0501 = '"+id+"' and z05.a0100='"+a0100+"'");//in ("+whl.substring(1)+") ";
				        		rowSet=dao.search(sql.toString());
				        		SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				        		SimpleDateFormat bartDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
				        		while(rowSet.next())
				        		{
				        			ArrayList list1 = new ArrayList();
				        			if(InterviewEvaluatingBo.isMail(rowSet.getString(isMailField))){
				        			list1.add(rowSet.getString("Z0501"));
				    	    		String context="";
					        		if(this.getFormHM().get("content")!=null){
					        			context=(String)this.getFormHM().get("content");
					        			context=PubFunc.keyWord_reback(context);
					        		}
					        		context=context.replaceAll("\\[","");
					        		context=context.replaceAll("\\]","");
					        		context=context.replaceAll("\\(~姓名~\\)",rowSet.getString("a0101"));
					        		//context=context.replaceAll(":", ":<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");  //zzk修改 
					        		if(rowSet.getString("codeitemdesc")!=null)
				    	    			context=context.replaceAll("\\(~应聘职位~\\)",rowSet.getString("codeitemdesc"));
				    	    		String date =rowSet.getString("z0509");
				    	    		if(date!=null&&date.trim().length()>0)
				    	    		{
					    	    		context=context.replaceAll("\\(~面试时间~\\)",date);
				    	    		}
				    	    		else{
										this.getFormHM().put("falg","11");
										return;
				    	    		}
				    	    			
				    	     		if(rowSet.getString("z0503")!=null&&rowSet.getString("z0503").trim().length()>0)
				    	     		{
				    	     			if(item!=null&&item.isCode())
				    	     				context=context.replaceAll("\\(~面试地点~\\)",AdminCode.getCodeName(item.getCodesetid(),rowSet.getString("z0503")));
				    	     			else
					    		        	context=context.replaceAll("\\(~面试地点~\\)",rowSet.getString("z0503"));
				    	     		}else{
										this.getFormHM().put("falg","12");
										return;
				    	     		}
					        		context=context.replaceAll("\\(~系统时间~\\)","<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+bartDateFormat2.format(new Date()));
					        		if(isHref)
					            		context+="<br><br>请点击 <a href=\""+http+"\" target=\"_blank\">面试回复</a> 以方便我们进行面试安排！";
				     	     		if(rowSet.getString(isMailField)!=null&&rowSet.getString(isMailField).trim().length()>1)
					        		{
					         			//sendEmail.send(rowSet.getString(isMailField).trim(), title,context);
				     	     			context=context.replaceAll("\r\n","<br>");
					         		    bo.sendEmail(title,context,"",from,rowSet.getString(isMailField).trim());
					         			flag = "0";
				        			}
				     	    		}else{
				        				flag = "2";
				        			}
				        			if(list1!=null && list1.size()>0)
				        				rlist.add(list1);
			         			}
				        		if(rlist!=null && rlist.size()>0)
				        			dao.batchUpdate(upsql, rlist);
							}else{
								ArrayList list=DataDictionary.getFieldList("Z05",Constant.USED_FIELD_SET);
								InterviewEvaluatingBo interviewEvaluatingBo=new InterviewEvaluatingBo(this.getFrameconn());
								String email_phone=interviewEvaluatingBo.getEmail_PhoneField();
								String isPhoneField=email_phone.split("/")[1];
								String isMailField1=email_phone.split("/")[0];
								String extendWhereSql1=(String)this.getFormHM().get("extendWhereSql1");
								
								String context="";
				        		if(this.getFormHM().get("content")!=null){
				        			context=(String)this.getFormHM().get("content");
				        			context=PubFunc.keyWord_reback(context);
				        		}
				        		/*ids="'"+ids.replaceAll("\\^", "','")+"'";
				        		StringBuffer sql=new StringBuffer();
					    		sql.append("select z05.z0501,"+dbName+"a01.a0101,z05.a0100,organization.codeitemdesc,");
					    		sql.append(Sql_switcher.dateToChar("z0509", "yyyy-mm-dd hh:mm")+" as z0509,");
			    	    		sql.append("z05.z0503,"+dbName+"A01."+isMailField+" from z05 left join "+dbName+"a01 on z05.a0100="+dbName+"a01.a0100 " +
			    	    				"left join zp_pos_tache on z05.a0100=zp_pos_tache.a0100 and zp_pos_tache.resume_flag='12'"        
				    				+" left join z03 on zp_pos_tache.zp_pos_id=z03.z0301 left join organization  " +
				    						"on z03.z0311=organization.codeitemid where z05.z0501 in("+ids+")");*/
				        		extendWhereSql1=(extendWhereSql1==null|| "".equals(extendWhereSql1))?" Z05.state='21' ":(extendWhereSql1+" and z05.state='21'");//只给待通知状态的发送邮件
				        		String sql=interviewEvaluatingBo.getInterviewArrangeInfoSQL(codeid,dbName,isMailField1,isPhoneField,list,extendWhereSql1," order by Z05.state asc,Z05.z0509 asc",1,this.userView);
				        		rowSet=dao.search(sql.toString());
				        		StringBuffer buf=new StringBuffer();
				        		String upsql="update z05 set z0511='";
				        		if(this.userView.getUserFullName()!=null&&this.userView.getUserFullName().length()!=0){
				        			 upsql= upsql+this.userView.getUserFullName()+"',STATE='22' where z0501=?";
				        		}else{
				        			upsql= upsql+this.userView.getUserName()+"',sate='22' where z0501=?";
				        		}
				        		ArrayList rlist=new ArrayList();
				        		
				        		//有无“待通知”人员
				        		boolean isEmpty = true;				        		
				        		while(rowSet.next()){
				        		    isEmpty = false;
				        		    
				        			if(InterviewEvaluatingBo.isMail(rowSet.getString(isMailField1))){
						        		String date =rowSet.getString("z0509");
					    	    		if(date==null||date.trim().length()==0)
					    	    		{
											this.getFormHM().put("falg","11");
											return;
					    	    		}
					    	    			
					    	     		if(rowSet.getString("z0503")==null||rowSet.getString("z0503").trim().length()==0)
					    	     		{
											this.getFormHM().put("falg","12");
											return;
					    	     		}

				        			}
				        		
				        		}
				        		
				        		//如果没有要发送的人员,默认认为是所有人员是处于已通知状态,提示信息变为“所选人员均处于已通知状态,未发送邮件”
				        		if(isEmpty){
				        			flag="allInSended";
				        			this.getFormHM().put("falg",flag);
				        			return;
				        		}
				        		
				        		rowSet=dao.search(sql.toString());
				        		while(rowSet.next()){
				        			
				        			ArrayList list1=new ArrayList();
				        			if(rowSet.getString(isMailField1)==null||rowSet.getString(isMailField1).length()==0){
				        				buf.append(rowSet.getString("a0101")+",email:"+rowSet.getString(isMailField1)+"   发送失败\r\n"); 
				        				continue;
				        			}
				        			context=(String)this.getFormHM().get("content");
				        			context=PubFunc.keyWord_reback(context);
				        			if(InterviewEvaluatingBo.isMail(rowSet.getString(isMailField1))){
				        				
				        				context=context.replaceAll("\\[","");
						        		context=context.replaceAll("\\]","");
						        		context=context.replaceAll("\\(~姓名~\\)",rowSet.getString("a0101"));
						        		context=context.replaceAll(":", ":<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
						        		if(rowSet.getString("codeitemdesc")!=null)
					    	    			context=context.replaceAll("\\(~应聘职位~\\)",rowSet.getString("codeitemdesc"));
						        		String date =rowSet.getString("z0509");
						        		SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
						        		SimpleDateFormat bartDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
					    	    		if(date!=null&&date.trim().length()>0)
					    	    		{
						    	    		context=context.replaceAll("\\(~面试时间~\\)",date);
					    	    		}
					    	    		else{
											this.getFormHM().put("falg","11");
											return;
					    	    		}
					    	    			
					    	     		if(rowSet.getString("z0503")!=null&&rowSet.getString("z0503").trim().length()>0)
					    	     		{
					    	     			if(item!=null&&item.isCode())
					    	     				context=context.replaceAll("\\(~面试地点~\\)",AdminCode.getCodeName(item.getCodesetid(),rowSet.getString("z0503")));
					    	     			else
						    		        	context=context.replaceAll("\\(~面试地点~\\)",rowSet.getString("z0503"));
					    	     		}else{
											this.getFormHM().put("falg","12");
											return;
					    	     		}
					    	    		context=context.replaceAll("\\(~系统时间~\\)","<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+bartDateFormat2.format(new Date()));
					    	    		
					    	    		if(rowSet.getString(isMailField1)!=null&&rowSet.getString(isMailField1).trim().length()>1)
						        		{
					     	     			context=context.replaceAll("\r\n","<br>");
					     	     			try
					       				    {		
					     	     				String txt=""+context;
					     	     				if(isHref){	
					     	     				    String batchAddress = (String) hm.get("address");
					     	     				    batchAddress = batchAddress.replaceAll("／", "/");
								            		http=batchAddress+"hire/hireNetPortal/search_zp_position.do?b_search=search&entery=1&aid="+SafeCode.encode(PubFunc.encrypt(rowSet.getString("a0100")));
								            		txt=txt+"<br><br>请点击 <a href=\""+http+"\" target=\"_blank\">面试回复</a> 以方便我们进行面试安排！";
					     	     				}
					     	     				bo.sendEmail(title,txt,"",from,rowSet.getString(isMailField1).trim());
					     	     				flag = "0";
					     	     				list1.add(rowSet.getString("z0501"));
					       				    }catch(Exception e){
					       					    buf.append(rowSet.getString("a0101")+",email:"+rowSet.getString(isMailField1)+"    发送失败\r\n"); 
					       				    }
					       				   
					        			}
				        			}else{
				        				buf.append(rowSet.getString("a0101")+",email:"+rowSet.getString(isMailField1)+"   发送失败\r\n"); 
				        			}
				        			if(list1!=null&&list1.size()!=0){
				        				rlist.add(list1);
				        			}
				        		}
				        		if(rlist!=null&&rlist.size()>0){
				        			dao.batchUpdate(upsql, rlist);
				        		}
				        		 if(buf.toString().length()>0){
			       	                	String outName="RevokeFile_"+PubFunc.getStrg()+".txt";
			       	                	fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outName);
			       	                	fileOut.write(buf.toString().getBytes());
			       				    	//outName=outName.replace(".txt","#");
			       				    	flag="file";
			       				    	this.getFormHM().put("rovkeName", SafeCode.encode(PubFunc.encrypt(outName)));
			       	                }
							}
							
						}
						else if("4".equalsIgnoreCase(zploop))
						{
							String[] id_arr=id.split("`");
							String[] a0100_arr=a0100.split("`");
							for(int i=0;i<id_arr.length;i++)
							{
								if(id_arr[i]==null|| "".equals(id_arr[i])||a0100_arr[i]==null|| "".equals(a0100_arr[i]))
									continue;
								StringBuffer sql=new StringBuffer();
					    		sql.append("select "+dbName+"a01.a0101,z05.a0100,organization.codeitemdesc,");
					    		sql.append(Sql_switcher.dateToChar("z0513", "yyyy-mm-dd hh:mm")+" as z0513,");
			    	    		sql.append("z05.z0503,"+dbName+"A01."+isMailField+" from z05 left join "+dbName+"a01 on z05.a0100="+dbName+"a01.a0100 left join zp_pos_tache on z05.a0100=zp_pos_tache.a0100 and zp_pos_tache.zp_pos_id = '"+PubFunc.decrypt(id_arr[i].split("~")[1])+"'"        
				    				+" left join z03 on zp_pos_tache.zp_pos_id=z03.z0301 left join organization  on z03.z0311=organization.codeitemid where z05.z0501 = '"+PubFunc.decrypt(id_arr[i].split("~")[0])+"' and z05.a0100='"+PubFunc.decrypt(a0100_arr[i])+"'");//in ("+whl.substring(1)+") ";
				        		rowSet=dao.search(sql.toString());
				        		SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				        		SimpleDateFormat bartDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
				        		while(rowSet.next())
				        		{
				        			if(rowSet.getString(isMailField)==null|| "".equals(rowSet.getString(isMailField))){
				        				flag="tt";
				        				continue;
				        			}
				        			if(InterviewEvaluatingBo.isMail(rowSet.getString(isMailField))){
				    	    		String context="";
					        		if(this.getFormHM().get("content")!=null){
					        			context=(String)this.getFormHM().get("content");
					        			context=PubFunc.keyWord_reback(context);
					        		}
					        		context=context.replaceAll("\\[","");
					        		context=context.replaceAll("\\]","");
					        		context=context.replaceAll("\\(~姓名~\\)",rowSet.getString("a0101"));
					        		if(rowSet.getString("codeitemdesc")!=null)
				    	    			context=context.replaceAll("\\(~应聘职位~\\)",rowSet.getString("codeitemdesc"));
							//java.util.Date date=rowSet.getDate("z0509");	
				    	    		String date =rowSet.getString("z0513");
				    	    		if(date!=null)
				    	    		{
					 			// String dd_str=DateUtils.format(date,"yyyy-MM-dd HH:mm");
					    			context=context.replaceAll("\\(~报道时间~\\)",date);
				    	    		}
				    	    		else
				    	    			context=context.replaceAll("\\(~面试时间~\\)",bartDateFormat.format(new Date()));
					        		context=context.replaceAll("\\(~系统时间~\\)",bartDateFormat2.format(new Date()));								
				     	     		if(rowSet.getString(isMailField)!=null&&rowSet.getString(isMailField).trim().length()>1)
					        		{
					         			//sendEmail.send(rowSet.getString(isMailField).trim(), title,context);
				     	     			bo.sendEmail(title,context.replaceAll("\r\n","<br>"),"",from,rowSet.getString(isMailField).trim());
					         			context=(String)this.getFormHM().get("content");
					         			flag = "0";
				        			}
				     	    		}else{
				        				flag = "2";
				        			}
			         			}
							}
						}
						else if("3".equals(zploop))
						{
							StringBuffer sql =new  StringBuffer();
							sql.append("select z0505,z0507,Z0503,z0509 from z05 where z0501='"+id+"'");//面试安排表   z0505 专业考官   z0507 外语考官   z0503 面试地点  Z0509 面试时间
							RowSet rs = dao.search(sql.toString());
							StringBuffer a100= new StringBuffer("");
							String interviewtime =  "";
							String interviewplace = "";
							while(rs.next())
							{
								String z0505=rs.getString("z0505")==null?"":rs.getString("z0505");
								String z0507=rs.getString("z0507")==null?"":rs.getString("z0507");
								interviewplace=rs.getString("z0503")==null?"":rs.getString("z0503");
								interviewtime=PubFunc.FormatDate(rs.getDate("z0509"));
								boolean bool=true;
								if(!"".equals(z0505))
								{
									if(z0505.endsWith(","))
						    			z0505=z0505.substring(0,z0505.length()-1);
									a100.append(z0505);
									bool=false;
								}
								if(!"".equals(z0507))
								{
									if(z0507.endsWith(","))
						    			z0507=z0507.substring(0,z0507.length()-1);
									if(!false)
										a100.append(",");
									a100.append(z0507);
								}
							}
							
							if("".equals(a100.toString()))//如果没有考官，则弹出这个错误。
							{
								this.getFormHM().put("falg","3");
								return;
							}
							if("".equals(interviewtime)){
								this.getFormHM().put("falg","11");
								return;
							}
							if("".equals(interviewplace)){
								this.getFormHM().put("falg","12");
								return;
							}
							String context="";
			        		if(this.getFormHM().get("content")!=null)
			        			context=(String)this.getFormHM().get("content");
			        		context=PubFunc.keyWord_reback(context);
			        		sql.setLength(0);
			        		sql.append("select "+dbName+"a01.a0101,z05.a0100,organization.codeitemdesc,");
				    		sql.append(Sql_switcher.dateToChar("z0509", "yyyy-mm-dd hh:mm")+" as z0509,");
		    	    		sql.append("z05.z0503,"+dbName+"A01."+isMailField+" from z05 left join "+dbName+"a01 on z05.a0100="+dbName+"a01.a0100 left join zp_pos_tache on z05.a0100=zp_pos_tache.a0100 and zp_pos_tache.zp_pos_id = '"+zp_pos_id+"'"        
			    				+" left join z03 on zp_pos_tache.zp_pos_id=z03.z0301 left join organization  on z03.z0311=organization.codeitemid where z05.z0501 = '"+id+"' and z05.a0100='"+a0100+"'");
			        		rowSet=dao.search(sql.toString());
			        		SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			        		SimpleDateFormat bartDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
			        		
			        		RowSet rSet = dao.search(sql.toString());
			        		/**面试时间*/
			        		String z0509="";
			        		/**面试地点*/
			        		String z0503="";
			        		String codeitemdesc="";
			        		String a0101="";
			        		String people="";
			        		while(rSet.next())
			        		{
			        			z0509 =rSet.getString("z0509");
			        			z0503=rSet.getString("z0503");
			        			codeitemdesc=rSet.getString("codeitemdesc");
			        			a0101=rSet.getString("a0101");
			        		}
			        		if(z0509==null)
			        			z0509=bartDateFormat.format(new Date());
			        		String sql2 = "";
			        		if(Sql_switcher.searchDbServer()==Constant.MSSQL)
			        			sql2 = "select count(*) people from z05 where convert(varchar(20),z0509,120) like '"+z0509.substring(0,10)+"%'"; 
			        		else if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			        			sql2 = "select count(*) people from z05 where to_char(z0509,'yyyy-MM-dd')  like '"+z0509.substring(0,10)+"%'"; 
			        		RowSet rSet2 = dao.search(sql2.toString());
			        		while(rSet2.next())
			        		{
			        			people =rSet2.getString("people");
			        		}
			        		if(item.isCode())
			        		{
			        			if(z0503!=null&&!"".equals(z0503))
			        				z0503=AdminCode.getCodeName(item.getCodesetid(), z0503);
			        		}
			        		
			        		HashMap amap=this.getName(id, isMailField, dao);
			        		String[] arr=a100.toString().split(",");
			        		context=context.replaceAll("\\[","");
			        		context=context.replaceAll("\\]","");
			        		String alertMessage ="";
			        		for(int i=0;i<arr.length;i++)
			        		{
			        			//xiexd 2014.12.02 当专业考官未进行设置时，数组第一个元素为空，获取考官信息时错误
			        			if(!"".equals(arr[i]) && arr[i]!=null){
			        				String econt=context;
			        				String aa=arr[i].toUpperCase();
			        				LazyDynaBean bean = (LazyDynaBean)amap.get(aa);
			        				String aname=(String)bean.get("a0101");
			        				String email=(String)bean.get("email");
			        				if(InterviewEvaluatingBo.isMail(email))
			        				{
			        					econt=econt.replaceAll("\\(~应聘人员姓名~\\)",a0101);
			        					econt=econt.replaceAll("\\(~面试考官姓名~\\)",aname);
			        					econt=econt.replaceAll("\\(~系统时间~\\)",bartDateFormat2.format(new Date()));	
			        					econt=econt.replaceAll("\\(~应聘职位~\\)",codeitemdesc);
			        					econt=econt.replaceAll("\\(~面试地点~\\)",z0503);
			        					econt=econt.replaceAll("\\(~面试时间~\\)",z0509);
			        					econt=econt.replaceAll("\\(~面试人数~\\)",people);
			        					//sendEmail.send(email, title,econt);
			        					bo.sendEmail(title,econt.replaceAll("\r\n","<br>"),"",from,email);
			        					flag = "0";
			        				}else{
			        					alertMessage = alertMessage+","+aname;
			        				}
			        			}
			        		}
			        		if(alertMessage.trim().length()>0){
				        		StringBuffer buf = new StringBuffer();
				        		buf.append("以下考官邮箱有误,请检查\r\n"+alertMessage.substring(1));
				        		String outName="RevokeFile_"+PubFunc.getStrg()+".txt";
		       	                fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outName);
		       	                fileOut.write(buf.toString().getBytes());
		       	                fileOut.close();
		       				    //outName=outName.replace(".txt","#");
		       				    flag="file";
		       				    this.getFormHM().put("rovkeName", SafeCode.encode(PubFunc.encrypt(outName)));
			        		}
						}
					}
					//sendEmail=null;
				    this.getFormHM().put("falg",flag);
        		
        	}catch(Exception e){
        		e.printStackTrace();
        		 throw GeneralExceptionHandler.Handle(e);
        	} finally {
        		PubFunc.closeResource(fileOut);//资源释放 
        	}
        	
        }
        public HashMap getName(String id,String isMailField,ContentDAO dao)
    	{
    		/*HashMap map = new HashMap();
    		try
    		{
    			String sql = "select a0100,a0101,"+isMailField+" from usra01 where a0100 in("+a0100+")";
    			this.frowset=dao.search(sql);
    			while(this.frowset.next())
    			{
    				String a01=this.frowset.getString("a0100");
    				String a0101 = this.frowset.getString("a0101")==null?"":this.frowset.getString("a0101");
    				String email=this.frowset.getString(isMailField)==null?"":this.frowset.getString(isMailField);
    				LazyDynaBean bean = new LazyDynaBean();
    				bean.set("a0101", a0101);
    				bean.set("email",email);
    				map.put(a01, bean);
    			}
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    		return map;*/
        	HashMap map=new HashMap();
    		RowSet rowSet = null;
    		try
    		{
    			StringBuffer ids=new StringBuffer("");
    		
    			String a_z0505="";
    			String a_z0507="";
    			HashSet idSet=new HashSet();
    			HashMap amap = new HashMap();
    			rowSet=dao.search("select pre from dbname");
            	HashMap tmap = new HashMap();
            	while(rowSet.next())
            	{
            		tmap.put(rowSet.getString(1).toUpperCase(), "1");
            	}
            	rowSet=dao.search("select z0505,z0507 from z05 where z0501='"+id+"'");
    			while(rowSet.next())
    			{
    				a_z0505=rowSet.getString("z0505");
    				a_z0507=rowSet.getString("z0507");
    				int i=0;
    				if(a_z0505!=null&&a_z0505.indexOf(",")!=-1)
    				{
    					String[] aa_z0505=a_z0505.split(",");
    					for(int index = 0;index < aa_z0505.length;index++)
    					{
    						if(aa_z0505[index]==null|| "".equals(aa_z0505[index]))
    							continue;
    						if(tmap.get(aa_z0505[index].substring(0,3).toUpperCase())!=null)
    						{
    							if(amap.get(aa_z0505[index].substring(0,3).toUpperCase())!=null)
    							{
    								String t= (String)amap.get(aa_z0505[index].substring(0,3).toUpperCase());
    								t+=","+aa_z0505[index].substring(3);
    								amap.put(aa_z0505[index].substring(0,3).toUpperCase(), t);
    							}
    							else
    							{
    								amap.put(aa_z0505[index].substring(0,3).toUpperCase(), ","+aa_z0505[index].substring(3));
    							}
    						}
    						else
    						{
    							String t= ((String)amap.get("USR".toUpperCase()))==null?"":(String)amap.get("USR".toUpperCase());
    							t+=","+aa_z0505[index];
    							amap.put("USR", t);
    						}
    					}
    				}
    				if(a_z0507!=null&&a_z0507.indexOf(",")!=-1)
    				{
    					String[] aa_z0507=a_z0507.split(",");
    					for(int index = 0;index < aa_z0507.length;index++)
    					{
    						if(aa_z0507[index]==null|| "".equals(aa_z0507[index]))
    							continue;
    						if(tmap.get(aa_z0507[index].substring(0,3).toUpperCase())!=null)
    						{
    							if(amap.get(aa_z0507[index].substring(0,3).toUpperCase())!=null)
    							{
    								String t= (String)amap.get(aa_z0507[index].substring(0,3).toUpperCase());
    								t+=","+aa_z0507[index].substring(3);
    								amap.put(aa_z0507[index].substring(0,3).toUpperCase(), t);
    							}
    							else
    							{
    								amap.put(aa_z0507[index].substring(0,3).toUpperCase(), ","+aa_z0507[index].substring(3));
    							}
    						}
    						else
    						{
    							String t= ((String)amap.get("USR".toUpperCase()))==null?"":(String)amap.get("USR".toUpperCase());
    							t+=","+aa_z0507[index];
    							amap.put("USR", t);
    						}
    					}
    				}
    			}
    			Set keySet = amap.keySet();
    			for(Iterator t=keySet.iterator();t.hasNext();)
    			{
    				String key = (String)t.next();
    				String a0100=(String)amap.get(key);
    				if(a0100!=null&&a0100.length()>0)
    				{
    					a0100="'"+a0100.substring(1).replaceAll(",", "','")+"'";
    					rowSet=dao.search("select a0100,a0101,"+isMailField+" from "+key+"A01 where a0100 in ("+a0100+")");
    					while(rowSet.next())
    					{
    						String a01=rowSet.getString("a0100");
    	    				String a0101 = rowSet.getString("a0101")==null?"":rowSet.getString("a0101");
    	    				String email=rowSet.getString(isMailField)==null?"":rowSet.getString(isMailField);
    	    				LazyDynaBean bean = new LazyDynaBean();
    	    				bean.set("a0101", a0101);
    	    				bean.set("email",email);
    	    				map.put(key.toUpperCase()+a01, bean);
    					}
    				}
    			}
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    		
    		return map;
    	}
}
