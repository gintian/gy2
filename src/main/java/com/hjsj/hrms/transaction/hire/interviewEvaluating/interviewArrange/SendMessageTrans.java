package com.hjsj.hrms.transaction.hire.interviewEvaluating.interviewArrange;

import com.hjsj.hrms.businessobject.hire.AutoSendEMailBo;
import com.hjsj.hrms.businessobject.hire.InterviewEvaluatingBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.businessobject.sys.SmsYWInterfaceBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 0521010022
 * <p>Title:SendMessageTrans.java</p>
 * <p>Description>:SendMessageTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 13, 2010 3:49:27 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class SendMessageTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			String context=(String)this.getFormHM().get("content");
			context=PubFunc.keyWord_reback(context);
			String dbName = (String)this.getFormHM().get("dbname");
    		String id = PubFunc.decrypt((String)this.getFormHM().get("id"));
    		String a0100 =  PubFunc.decrypt((String)this.getFormHM().get("a0100"));
    		String zploop = (String)this.getFormHM().get("zploop");
    		String zp_pos_id =  PubFunc.decrypt((String)this.getFormHM().get("zp_pos_id"));
    		RowSet rowSet = null;
    		String flag="";
    		ContentDAO dao = new ContentDAO(this.frameconn);
    		AutoSendEMailBo autoSendEMailBo = new AutoSendEMailBo(this.getFrameconn());
    		String mobile_field =autoSendEMailBo.getMobileField();
    		String zpbatch=(String)this.getFormHM().get("zpbatch");
    		String codeid=(String)this.getFormHM().get("codeid");
    		/**未设置电话指标*/
    		if(mobile_field==null || "".equals(mobile_field)){
    			flag="6";
    			this.getFormHM().put("falg", flag);
    			return;
    		}
    		 RecordVo sms_vo=ConstantParamter.getConstantVo("SS_SMS_OPTIONS");
    		 /**未设置短信接口参数*/
	         if(sms_vo==null)
	         {
	        	flag="7";
	    		this.getFormHM().put("falg", flag);
	    		return;
	         }
	         String param=sms_vo.getString("str_value");
	         if(param==null|| "".equals(param))
	         {
	        	flag="7";
	    		this.getFormHM().put("falg", flag);
	    		return;
	         }
	         /**内容为空，不能发送*/
	         if(context==null|| "".equals(context))
	         {
	        	flag="8";
		    	this.getFormHM().put("falg", flag);
		    	return;
	         }
	         if("2".equalsIgnoreCase(zploop))
				{	
	        	 	
	        	 	ArrayList codelist=new ArrayList();
	        	 	ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
	        	 	String interviewingRevertItemid="";
	        	 	HashMap map=parameterXMLBo.getAttributeValues();
	        	 	if(map!=null&&map.get("interviewing_itemid")!=null)
	        		{
	        			interviewingRevertItemid=(String)map.get("interviewing_itemid");
	        		}
	        	 	ArrayList destlist = new ArrayList();
	        	 	
	    			StringBuffer sql=new StringBuffer();
	    			if(interviewingRevertItemid!=null&&interviewingRevertItemid.length()>1){
        	    		sql.setLength(0);
        	    		sql.append("select codesetid from fielditem  where fieldsetid='A01' and itemid='");
        	    		sql.append(interviewingRevertItemid);
        	    		sql.append("'");
        	    		rowSet=dao.search(sql.toString());
        	    		String codesetid="";
        	    		if(rowSet.next()){
        	    			codesetid=rowSet.getString("codesetid");
        	    		}
        	    		if(codesetid!=null&&codesetid.length()!=0){
        	    			sql.setLength(0);
        	    			sql.append("select * from codeitem where codesetid='");
        	    			sql.append(codesetid);
        	    			sql.append("'");
        	    			rowSet=dao.search(sql.toString());
        	    			while(rowSet.next()){
        	    				LazyDynaBean bena=new LazyDynaBean();
        	    				bena.set("codeid", rowSet.getString("codeitemid"));
        	    				bena.set("name", rowSet.getString("codeitemdesc"));
        	    				codelist.add(bena);
        	    			}
        	    		}
	        	 	}
	    			SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	        		SimpleDateFormat bartDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
	    			if(zpbatch==null||zpbatch.trim().length()==0){
		    			sql.setLength(0);
			    		sql.append("select "+dbName+"a01.a0101,z05.a0100,organization.codeitemdesc,");
			    		sql.append(Sql_switcher.dateToChar("z0509", "yyyy-mm-dd hh:mm")+" as z0509,");
	 	    		    sql.append("z05.z0503,"+dbName+"A01."+mobile_field+" from z05 left join "+dbName+"a01 on z05.a0100="+dbName+"a01.a0100 left join zp_pos_tache on z05.a0100=zp_pos_tache.a0100 and zp_pos_tache.zp_pos_id = '"+zp_pos_id+"'"        
		    				+" left join z03 on zp_pos_tache.zp_pos_id=z03.z0301 left join organization  on z03.z0311=organization.codeitemid where z05.z0501 = '"+id+"' and z05.a0100='"+a0100+"'");//in ("+whl.substring(1)+") ";
		        		rowSet=dao.search(sql.toString());
		        		
		        		while(rowSet.next())
		        		{
		        			if(autoSendEMailBo.isMobileNumber(rowSet.getString(mobile_field))){
			            		context=context.replaceAll("\\[","");
			        	    	context=context.replaceAll("\\]","");
			        	    	context=context.replaceAll("\\(~姓名~\\)",rowSet.getString("a0101"));
			        	    	if(rowSet.getString("codeitemdesc")!=null)
		    	    		    	context=context.replaceAll("\\(~应聘职位~\\)",rowSet.getString("codeitemdesc"));
		    	    	    	String date =rowSet.getString("z0509");
		    	    	     	if(date!=null)
		    	    	    	{
			    	        		//context=context.replaceAll("\\(~面试时间~\\)",date);
		    	    	     		
		    	    	    	}
		    	    	    	else{
		    	    	    		date=bartDateFormat.format(new Date());
		    	    	    		//date=date.replaceAll(" ", "/");
		    	    	     		//context=context.replaceAll("\\(~面试时间~\\)",date);
		    	    	    	}
		    	     	    	if(rowSet.getString("z0503")!=null)
			    		         	context=context.replaceAll("\\(~面试地点~\\)",rowSet.getString("z0503"));
			        	    	context=context.replaceAll("\\(~系统时间~\\)",bartDateFormat2.format(new Date()));
			        	    	if(interviewingRevertItemid!=null&&interviewingRevertItemid.length()>1){
			        	    		String ti="";
			        	    		if(context.indexOf("(~短信回复提示~)")!=-1){
			        	    			if(codelist!=null&&codelist.size()!=0){
			        	    				ti="请回复短信,zp#开头加相应代码,";
			        	    				for(int i=0;i<codelist.size();i++){
			        	    					LazyDynaBean bena=(LazyDynaBean)codelist.get(i);
			        	    					ti=ti+(String)bena.get("codeid")+":"+(String)bena.get("name")+";";
			        	    				}
			        	    			}
			        	    			context=context.replaceAll("\\(~短信回复提示~\\)", ti);
			        	    		}
			        	    	}else{
			        	    		if(context.indexOf("(~短信回复提示~)")!=-1){
			        	    			context=context.replaceAll("\\(~短信回复提示~\\)", "");
			        	    		}
			        	    			
			        	    	}
		     	     	    	if(rowSet.getString(mobile_field)!=null&&rowSet.getString(mobile_field).trim().length()>1)
			        	    	{
		     	     	    		LazyDynaBean dyvo=new LazyDynaBean();
			        				dyvo.set("sender",this.userView.getUserFullName());
			    					dyvo.set("receiver",rowSet.getString("a0101"));
			    					dyvo.set("phone_num",rowSet.getString(mobile_field));
			    					context=context.replaceAll(" ", "");
			    					context=context.replaceAll("\\(~面试时间~\\)",date);//为了保留日期与时间中间的空格，放到下面来替换
			    					context=context.replaceAll("\\r","");
			    					context=context.replaceAll("\\n","");
			    					context=context.replaceAll("\\r\\n","");
			    					dyvo.set("msg",context);
			    					destlist.add(dyvo);
			         		    	flag = "9";
			         		    	context=(String)this.getFormHM().get("content");
		        		    	}
		     	    	    }else{
		        			    	flag = "10";
		        		    }
	      			   }
	        	 	}else{//群发信息
	        	 		ArrayList list=DataDictionary.getFieldList("Z05",Constant.USED_FIELD_SET);
						InterviewEvaluatingBo interviewEvaluatingBo=new InterviewEvaluatingBo(this.getFrameconn());
						String email_phone=interviewEvaluatingBo.getEmail_PhoneField();
						String isPhoneField=email_phone.split("/")[1];
						String isMailField1=email_phone.split("/")[0];
						String extendWhereSql1=(String)this.getFormHM().get("extendWhereSql1");
						/*ids="'"+ids.replaceAll("\\^", "','")+"'";
						StringBuffer sql1=new StringBuffer();
						sql1.append("select z05.z0501,"+dbName+"a01.a0101,z05.a0100,organization.codeitemdesc,");
						sql1.append(Sql_switcher.dateToChar("z0509", "yyyy-mm-dd hh:mm")+" as z0509,");
						sql1.append("z05.z0503,"+dbName+"A01."+isPhoneField+" from z05 left join "+dbName+"a01 on z05.a0100="+dbName+"a01.a0100 " +
	    	    				"left join zp_pos_tache on z05.a0100=zp_pos_tache.a0100 and zp_pos_tache.resume_flag='12'"        
		    				+" left join z03 on zp_pos_tache.zp_pos_id=z03.z0301 left join organization  " +
		    						"on z03.z0311=organization.codeitemid where z05.z0501 in("+ids+")");*/
						extendWhereSql1=(extendWhereSql1==null|| "".equals(extendWhereSql1))?" Z05.state='21' ":(extendWhereSql1+" and z05.state='21'");
		        		String sql1=interviewEvaluatingBo.getInterviewArrangeInfoSQL(codeid,dbName,isMailField1,isPhoneField,list,extendWhereSql1," order by Z05.state asc,Z05.z0509 asc",1,this.userView);
						rowSet=dao.search(sql1.toString());
						ArrayList rlist=new ArrayList();
		        		while(rowSet.next()){
		        			ArrayList list1=new ArrayList();
		        			if(autoSendEMailBo.isMobileNumber(rowSet.getString(isPhoneField))){
		        				context=context.replaceAll("\\[","");
			        	    	context=context.replaceAll("\\]","");
			        	    	context=context.replaceAll("\\(~姓名~\\)",rowSet.getString("a0101"));
			        	    	if(rowSet.getString("codeitemdesc")!=null)
		    	    		    	context=context.replaceAll("\\(~应聘职位~\\)",rowSet.getString("codeitemdesc"));
		    	    	    	String date =rowSet.getString("z0509");
		    	    	    	if(date!=null)
		    	    	    	{
			    	        		//context=context.replaceAll("\\(~面试时间~\\)",date);
		    	    	     		
		    	    	    	}
		    	    	    	else{
		    	    	    		date=bartDateFormat.format(new Date());
		    	    	    		//date=date.replaceAll(" ", "/");
		    	    	     		//context=context.replaceAll("\\(~面试时间~\\)",date);
		    	    	    	}
		    	    	    	if(rowSet.getString("z0503")!=null)
			    		         	context=context.replaceAll("\\(~面试地点~\\)",rowSet.getString("z0503"));
			        	    	context=context.replaceAll("\\(~系统时间~\\)",bartDateFormat2.format(new Date()));
			        	    	if(interviewingRevertItemid!=null&&interviewingRevertItemid.length()>1){
			        	    		String ti="";
			        	    		if(context.indexOf("(~短信回复提示~)")!=-1){
			        	    			if(codelist!=null&&codelist.size()!=0){
			        	    				ti="请回复短信,zp#开头加相应代码,";
			        	    				for(int i=0;i<codelist.size();i++){
			        	    					LazyDynaBean bena=(LazyDynaBean)codelist.get(i);
			        	    					ti=ti+(String)bena.get("codeid")+":"+(String)bena.get("name")+";";
			        	    				}
			        	    			}
			        	    			context=context.replaceAll("\\(~短信回复提示~\\)", ti);
			        	    		}
			        	    	}else{
			        	    		if(context.indexOf("(~短信回复提示~)")!=-1){
			        	    			context=context.replaceAll("\\(~短信回复提示~\\)", "");
			        	    		}	
			        	    	}
			        	    	if(rowSet.getString(isPhoneField)!=null&&rowSet.getString(isPhoneField).trim().length()>1)
			        	    	{
		     	     	    		LazyDynaBean dyvo=new LazyDynaBean();
			        				dyvo.set("sender",this.userView.getUserFullName());
			    					dyvo.set("receiver",rowSet.getString("a0101"));
			    					dyvo.set("phone_num",rowSet.getString(isPhoneField));
			    					context=context.replaceAll(" ", "");
			    					context=context.replaceAll("\\(~面试时间~\\)",date);//为了保留日期与时间中间的空格，放到下面来替换
			    					context=context.replaceAll("\\r","");
			    					context=context.replaceAll("\\n","");
			    					context=context.replaceAll("\\r\\n","");
			    					dyvo.set("msg",context);
			    					destlist.add(dyvo);
			         		    	flag = "9";
			         		    	context=(String)this.getFormHM().get("content");
			         		    	list1.add(rowSet.getString("z0501"));
		        		    	}
		        			}else{
		        				flag = "10";
		        			}
		        			rlist.add(list1);
		        		}
		        		String upsql="update z05 set z0511='";
		        		if(this.userView.getUserFullName()!=null&&this.userView.getUserFullName().length()!=0){
		        			upsql=upsql+this.userView.getUserFullName()+"',STATE='22' where z0501=?";
		        		}else{
		        			upsql=upsql+this.userView.getUserName()+"',STATE='22' where z0501=?";
		        		}
		        		dao.batchUpdate(upsql,rlist);
	        	 	}
	        		if(destlist!=null&&destlist.size()>0)
	     			{
	        			if(interviewingRevertItemid!=null&&interviewingRevertItemid.length()>1){
		        			SmsYWInterfaceBo ywBo = new SmsYWInterfaceBo(this.frameconn);
		        			ArrayList ywList = ywBo.getList();
		        			LazyDynaBean bean = new LazyDynaBean();
		        			boolean flag1=false;
		        			if(ywList!=null&&ywList.size()!=0){
			        			for (int i = 0; i< ywList.size(); i++) {
			        				LazyDynaBean ywBean = (LazyDynaBean) ywList.get(i);
			        				String code = (String) ywBean.get("code");
			        				String classes = (String) ywBean.get("classes");
			        				String desc = (String) ywBean.get("desc");
			        				String status = (String) ywBean.get("status");
			        				if("zp".equalsIgnoreCase(code)){
			        					if("com.hjsj.hrms.businessobject.hire.ReceiveMessageBo".equalsIgnoreCase(classes)){
			        						if("1".equalsIgnoreCase(status)){
			        							flag1=true;
			        						}else{
			        							flag="nexit"; 
			        						}
			        					}else{
			        						flag="nexit"; 
			        					}
			        				}else{
			        					flag="nexit"; 
			        				}
			        			}
		        			}else{
		        				flag="nexit"; 
		        			}
		        			if(flag1==true){
			            		SmsBo smsbo=new SmsBo(this.getFrameconn());
				        		smsbo.batchSendMessage(destlist);
		        			}
	        			}else{
	        				SmsBo smsbo=new SmsBo(this.getFrameconn());
			        		smsbo.batchSendMessage(destlist);
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
	    	    		sql.append("z05.z0503,"+dbName+"A01."+mobile_field+" from z05 left join "+dbName+"a01 on z05.a0100="+dbName+"a01.a0100 left join zp_pos_tache on z05.a0100=zp_pos_tache.a0100 and zp_pos_tache.zp_pos_id = '"+id_arr[i].split("~")[1]+"'"        
		    				+" left join z03 on zp_pos_tache.zp_pos_id=z03.z0301 left join organization  on z03.z0311=organization.codeitemid where z05.z0501 = '"+id_arr[i].split("~")[0]+"' and z05.a0100='"+a0100_arr[i]+"'");//in ("+whl.substring(1)+") ";
		        		rowSet=dao.search(sql.toString());
		        		SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		        		SimpleDateFormat bartDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
		        		ArrayList destlist = new ArrayList();
		        		while(rowSet.next())
		        		{
		        			if(autoSendEMailBo.isMobileNumber(rowSet.getString(mobile_field))){
			        	    	context=context.replaceAll("\\[","");
			        	    	context=context.replaceAll("\\]","");
			        	    	context=context.replaceAll("\\(~姓名~\\)",rowSet.getString("a0101"));
			        	    	if(rowSet.getString("codeitemdesc")!=null)
		    	    		    	context=context.replaceAll("\\(~应聘职位~\\)",rowSet.getString("codeitemdesc"));
					
		    	    		    String date =rowSet.getString("z0513");
		    	    	    	if(date!=null)
		    	    	    	{
			    		        	context=context.replaceAll("\\(~报道时间~\\)",date);
		    	    	    	}
		    	    	    	else{
		    	    			//context=context.replaceAll("\\(~面试时间~\\)",bartDateFormat.format(new Date()));
		    	    	    	}
			        	    	context=context.replaceAll("\\(~系统时间~\\)",bartDateFormat2.format(new Date()));								
		     	     	    	if(rowSet.getString(mobile_field)!=null&&rowSet.getString(mobile_field).trim().length()>1)
			        	    	{
		     	     		
		     	     	    		LazyDynaBean dyvo=new LazyDynaBean();
			        				dyvo.set("sender",this.userView.getUserFullName());
			    					dyvo.set("receiver",rowSet.getString("a0101"));
			    					dyvo.set("phone_num",rowSet.getString(mobile_field));
			    					context=context.replaceAll(" ", "");
			    					context=context.replaceAll("\\(~面试时间~\\)",date);//为了保留日期与时间中间的空格，放到下面来替换
			    					context=context.replaceAll("\\r","");
			    					context=context.replaceAll("\\n","");
			    					context=context.replaceAll("\\r\\n","");
			    					dyvo.set("msg",context);
			    					destlist.add(dyvo);
			         	    		context=(String)this.getFormHM().get("content");
			         	    		flag = "9";
		        		    	}
		     	    		}else{
		        				flag = "10";
		        			}
	         			}
		        		if(destlist!=null&&destlist.size()>0)
		     			{
		            		SmsBo smsbo=new SmsBo(this.getFrameconn());
			        		smsbo.batchSendMessage(destlist);
		     			}
					}
				}
				else if("3".equals(zploop))
				{
					StringBuffer sql =new  StringBuffer();
					sql.append("select z0505,z0507 from z05 where z0501='"+id+"'");
					RowSet rs = dao.search(sql.toString());
					StringBuffer a100= new StringBuffer("");
					while(rs.next())
					{
						String z0505=rs.getString("z0505")==null?"":rs.getString("z0505");
						String z0507=rs.getString("z0507")==null?"":rs.getString("z0507");
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
					if("".equals(a100.toString()))
					{
						this.getFormHM().put("falg","3");
						return;
					}
	        		sql.setLength(0);
	        		sql.append("select "+dbName+"a01.a0101,z05.a0100,organization.codeitemdesc,");
		    		sql.append(Sql_switcher.dateToChar("z0509", "yyyy-mm-dd hh:mm")+" as z0509,");
 	        		sql.append("z05.z0503,"+dbName+"A01."+mobile_field+" from z05 left join "+dbName+"a01 on z05.a0100="+dbName+"a01.a0100 left join zp_pos_tache on z05.a0100=zp_pos_tache.a0100 and zp_pos_tache.zp_pos_id = '"+zp_pos_id+"'"        
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
	        		if(z0509==null)
	        			z0509=bartDateFormat.format(new Date());
	        		HashMap amap=this.getName("'"+a100.toString().replaceAll(",", "','")+"'", mobile_field, dao);
	        		String[] arr=a100.toString().split(",");
	        		context=context.replaceAll("\\[","");
	        		context=context.replaceAll("\\]","");
	        		ArrayList destlist = new ArrayList();
	        		String sql1="select * from dbname";
	     			this.frowset=dao.search(sql1);
	     			HashMap nameMap=new HashMap();
	     			while(this.frowset.next()){
	     				nameMap.put(this.frowset.getString("pre"), "1");
	     			}
	        		for(int i=0;i<arr.length;i++)
	        		{
	        			String econt=context;
	        			
	        			String aa=arr[i];
	        			String tem=aa.substring(0, 3);
	        			LazyDynaBean bean = null;
	        			if(nameMap.get(tem)!=null){
	        				 bean = (LazyDynaBean)amap.get(aa.substring(3));
	        			}else{
	        				 bean = (LazyDynaBean)amap.get(aa);
	        			}	        		
	        			String aname=(String)bean.get("a0101");
	        			String email=(String)bean.get("email");
	        			if(autoSendEMailBo.isMobileNumber(email))
	        			{
	        				econt=econt.replaceAll("\\(~应聘人员姓名~\\)",a0101);
	        				econt=econt.replaceAll("\\(~面试考官姓名~\\)",aname);
	        				econt=econt.replaceAll("\\(~系统时间~\\)",bartDateFormat2.format(new Date()));	
	        				econt=econt.replaceAll("\\(~应聘职位~\\)",codeitemdesc);
	        				econt=econt.replaceAll("\\(~面试地点~\\)",z0503);
	        				econt=econt.replaceAll("\\(~面试人数~\\)",people);
	        				//econt=econt.replaceAll("\\(~面试时间~\\)",z0509);
	        				flag = "9";
	        				LazyDynaBean dyvo=new LazyDynaBean();
	        				dyvo.set("sender",this.userView.getUserFullName());
	    					dyvo.set("receiver",aname);
	    					dyvo.set("phone_num",email);
	    					econt=econt.replaceAll(" ", "");
	    					econt=econt.replaceAll("\\(~面试时间~\\)",z0509);
	    					econt=econt.replaceAll("\\r","");
	    					econt=econt.replaceAll("\\n","");
	    					econt=econt.replaceAll("\\r\\n","");
	    					dyvo.set("msg",econt);
	    					destlist.add(dyvo);
	        			}
	        		}
	        		if(destlist!=null&&destlist.size()>0)
	     			{
	            		SmsBo smsbo=new SmsBo(this.getFrameconn());
		        		smsbo.batchSendMessage(destlist);
	     			}
				}
	         this.getFormHM().put("falg", flag);
	         
	  
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	 public HashMap getName(String a0100,String isMailField,ContentDAO dao)
 	{
 		HashMap map = new HashMap();
 		try
 		{
 			String ta[]=a0100.split(",");
 			String sql="";
 			String sql1="select * from dbname";
 			this.frowset=dao.search(sql1);
 			HashMap nameMap=new HashMap();
 			while(this.frowset.next()){
 				nameMap.put(this.frowset.getString("pre"), "1");
 			}
 			for(int i=0;i<ta.length;i++){
 				String tem=ta[i].substring(1, 4);
 				if(nameMap.get(tem)!=null){
 	 				sql = "select a0100,a0101,"+isMailField+" from "+tem+"a01 where a0100 in("+ta[i].substring(4,ta[i].length()-1)+")";
 	 			}else{
 	 				sql = "select a0100,a0101,"+isMailField+" from usra01 where a0100 in("+a0100+")";
 	 			}
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
 		}
 		catch(Exception e)
 		{
 			e.printStackTrace();
 		}
 		return map;
 	}

}
