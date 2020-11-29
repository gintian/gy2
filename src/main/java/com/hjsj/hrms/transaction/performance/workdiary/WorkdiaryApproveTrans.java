package com.hjsj.hrms.transaction.performance.workdiary;

import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySelStr;
import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class WorkdiaryApproveTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		RecordVo p01Vo=new RecordVo("p01");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		WorkdiarySelStr wss=new WorkdiarySelStr();
		ArrayList seldiary=(ArrayList) hm.get("seldiary2");//因为在日志中，前台展示有的用的是extenditerate标签，有的用的是paginationdb标签。所以获取前台选中的记录时无法区分。所以对于用extenditerate标签时，获取前台记录用seldiary2接收
		String Curr_user=(String) hm.get("curr_user");//报批上级领导username
		if(reqhm.containsKey("fp")){
			p01Vo=(RecordVo) hm.get("p01Vo");
			String p0100=p01Vo.getString("p0100");
			String action=(String) reqhm.get("action");
			PendingTask imip=new PendingTask();
			String pre_pendingID = (String)this.getFormHM().get("pendingCode");
			try{
				if("app".equalsIgnoreCase(action)){
					dao.update("update p01 set Curr_user='"+Curr_user+"' ,p0113='"+p01Vo.getString("p0113")+"' where p0100='"+p0100+"'");
					//dao.update("update p01 set p0113='"+p01Vo.getString("p0113")+"' where p0100='"+p0100+"'");
					String a0100="";
					ArrayList list=wss.getSuperiorUser(this.getUserView().getA0100(), dao);
					for (int i = 0; i < list.size(); i++) {
						LazyDynaBean ldb=(LazyDynaBean) list.get(i);
						if(Curr_user!=null&&Curr_user.equals(ldb.get("username"))){
							a0100=(String) ldb.get("a0100");
							wss.sendEMail(this.getFrameconn(), a0100, p0100,"1");
						}
					}
				}
				if("ok".equalsIgnoreCase(action)){
				    SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String d1 = sdf1.format(new Date());
					dao.update("update p01 set p0115='03' ,p0113='"+p01Vo.getString("p0113")+"',p0116="+Sql_switcher.dateValue(d1)+",p0117='"+this.userView.getUserFullName()+"' where p0100='"+p0100+"'");
					sendMail(dao,p0100,"批准");
					//批准置为已批
					//将旧的代办信息置为已处理状态 
					if(pre_pendingID!=null&&pre_pendingID.trim().length()>0)
					{ 
						imip.updatePending("W",pre_pendingID,1,"",this.userView);
					}
					//同步更新per_diary_actor表
					StringBuffer str = new StringBuffer();
					switch(Sql_switcher.searchDbServer())
				    {
						case Constant.ORACEL:
					    {
					    	d1="to_date('"+d1+"','yyyy-mm-dd hh24:mi:ss')";
					    	break;
					    }
						case Constant.MSSQL:
					    {
					    	d1="'"+d1+"'";
					    	break;
					    }
					}	
					str.append(" insert into per_diary_actor(p0100,a0100,nbase,b0110,e0122,e01a1,a0101,content,commentary_date,state,display) ");
					str.append(" values('"+p0100+"','"+userView.getA0100()+"','"+userView.getDbname()+"','"+userView.getUserOrgId()+"','"+userView.getUserDeptId()+"','"+userView.getUserPosId()+"','"+userView.getUserFullName()+"','"+p01Vo.getString("p0113")+"', ");
					str.append(" "+d1+",0,0) ");
					dao.update(str.toString());
				}
				if("back".equalsIgnoreCase(action)){
				    SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String d1 = sdf1.format(new Date());
					dao.update("update p01 set p0115='07' ,p0113='"+p01Vo.getString("p0113")+"',p0116="+Sql_switcher.dateValue(d1)+",p0117='"+this.userView.getUserFullName()+"'  where p0100='"+p0100+"'");
					sendMail(dao,p0100,"驳回");
					//驳回置为待批
					if(pre_pendingID!=null&&pre_pendingID.trim().length()>0)
					{ 
						//System.out.println(pre_pendingID);
						imip.updatePending("W",pre_pendingID,1,"",this.userView);
					}
					String timestr = (String)reqhm.get("timestr");
					timestr=timestr!=null&&timestr.trim().length()>0?timestr:"";
					sendRejectPending(p0100,dao,timestr);
				}
				if("save".equalsIgnoreCase(action)){
					dao.updateValueObject(p01Vo);
				}
				if("del".equalsIgnoreCase(action)){
					p01Vo.clearValues();
					p01Vo.setString("p0100",p0100);
					dao.updateValueObject(p01Vo);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			reqhm.remove("fp");
	}else{
		try{
		if(reqhm.containsKey("action")){
//			根据action值进行相关操作
			String action=(String) reqhm.get("action");
			if("ok".equalsIgnoreCase(action)){
//				进行批准操作
				for(int i=0;i<seldiary.size();i++){
				    SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String d1 = sdf1.format(new Date());
					DynaBean dynabean=(DynaBean)seldiary.get(i);
					dao.update("update p01 set p0115='03',Curr_user=null ,p0116="+Sql_switcher.dateValue(d1)+",p0117='"+this.userView.getUserFullName()+"' where p0100='"+PubFunc.decrypt((String)dynabean.get("p0100"))+"'");		
					sendMail(dao,PubFunc.decrypt((String)dynabean.get("p0100")),"批准");
					//同步更新per_diary_actor表
					StringBuffer str = new StringBuffer();
					switch(Sql_switcher.searchDbServer())
				    {
						case Constant.ORACEL:
					    {
					    	d1="to_date('"+d1+"','yyyy-mm-dd hh24:mi:ss')";
					    	break;
					    }
						case Constant.MSSQL:
					    {
					    	d1="'"+d1+"'";
					    	break;
					    }
					}	
					str.append(" insert into per_diary_actor(p0100,a0100,nbase,b0110,e0122,e01a1,a0101,content,commentary_date,state,display) ");
					str.append(" values('"+PubFunc.decrypt((String)dynabean.get("p0100"))+"','"+userView.getA0100()+"','"+userView.getDbname()+"','"+userView.getUserOrgId()+"','"+userView.getUserDeptId()+"','"+userView.getUserPosId()+"','"+userView.getUserFullName()+"','"+p01Vo.getString("p0113")+"', ");
					str.append(" "+d1+",0,0) ");
					dao.update(str.toString());
				}
			}
			if("back".equalsIgnoreCase(action)){
//				进行驳回操作
				for(int i=0;i<seldiary.size();i++){
				    SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String d1 = sdf1.format(new Date());
					DynaBean dynabean=(DynaBean)seldiary.get(i);				
					dao.update("update p01 set p0115='07' ,Curr_user=null,p0116="+Sql_switcher.dateValue(d1)+",p0117='"+this.userView.getUserFullName()+"' where p0100='"+PubFunc.decrypt((String)dynabean.get("p0100"))+"'");	
					sendMail(dao,PubFunc.decrypt((String)dynabean.get("p0100")),"驳回");
				}
			}
			if("del".equalsIgnoreCase(action)){
//				进行清空日志操作
				for(int i=0;i<seldiary.size();i++){
					DynaBean dynabean=(DynaBean)seldiary.get(i);
					
						p01Vo.setString("p0100",PubFunc.decrypt((String) dynabean.get("p0100")));
						dao.deleteValueObject(p01Vo);
						p01Vo.clearValues();
						p01Vo.setString("p0100",PubFunc.decrypt((String) dynabean.get("p0100")));
						dao.addValueObject(p01Vo);
//						dao.updateValueObject(p01Vo);					
				}
			}		
			reqhm.remove("action");
		}else{
//			获得p0100,进入审批日志页面
			String p0100=(String) reqhm.get("p0100");
			p01Vo.setString("p0100",p0100);			
			p01Vo=dao.findByPrimaryKey(p01Vo);	
//			if(p01Vo.getString("p0103")!=null){
//				p01Vo.setString("p0103",wss.getenter(p01Vo.getString("p0103")));
//			}
//			if(p01Vo.getString("p0109")!=null){
//				p01Vo.setString("p0109",wss.getenter(p01Vo.getString("p0109")));
//			}
//			if(p01Vo.getString("p0111")!=null){
//				p01Vo.setString("p0111",wss.getenter(p01Vo.getString("p0111")));
//			}
			if(p01Vo.getString("p0113")!=null){
				p01Vo.setString("p0113",p01Vo.getString("p0111"));
			}
			hm.put("p01Vo",p01Vo);
		}	
		}
		catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	private String getFromAddr()
	{
		String str = "";
        RecordVo stmp_vo=ConstantParamter.getConstantVo("SS_STMP_SERVER");
        if(stmp_vo==null)
        	return "";
        String param=stmp_vo.getString("str_value");
        if(param==null|| "".equals(param))
        	return "";
        try
        {
			Document doc = PubFunc.generateDom(param);
	        Element root = doc.getRootElement();
	        Element stmp=root.getChild("stmp");	
	        str=stmp.getAttributeValue("from_addr");
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }  
        return str;
	}
	private void sendMail(ContentDAO dao,String p0100,String ms){
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select A0100,NBASE,P0114 from P01 where p0100='");
		sqlstr.append(p0100);
		sqlstr.append("'");
		String A0100 = "";
		String NBASE="";
		Date P0114= null;
		RowSet rs = null;
		try {
			String emailfield = getEmailField();
			emailfield=emailfield!=null?emailfield:"";
			String tomail = "";
			String frommail = getFromAddr();
			frommail=frommail!=null?frommail:"";
			rs = dao.search(sqlstr.toString());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			if(rs.next()){
				A0100 = rs.getString("A0100");
				A0100=A0100!=null?A0100:"";
				NBASE = rs.getString("NBASE");
				NBASE=NBASE!=null?NBASE:"";
				P0114 = rs.getDate("P0114");
			}
			if(A0100.trim().length()>0&&NBASE.trim().length()>0
					&&emailfield.trim().length()>0){
				String username = "";
				sqlstr.setLength(0);
				sqlstr.append("select a0101,");
				sqlstr.append(emailfield);
				sqlstr.append(" from ");
				sqlstr.append(NBASE+"A01 where A0100='");
				sqlstr.append(A0100);
				sqlstr.append("'");
				rs = dao.search(sqlstr.toString());
				if(rs.next()){
					tomail = rs.getString(emailfield);
					tomail=tomail!=null?tomail:"";
					username = rs.getString("a0101");
					username = username == null ? "" : username;
				}
				if(tomail.trim().length()>0&&frommail.trim().length()>0){
					String title="日志审批信息";
					// 邮件接收人姓名不对，不应是审批人的姓名 2010-09-09 wangzhongjun
					/*String username = this.userView.getUserFullName();
					username=username!=null?username:"";
					username = username.trim().length()>0?username:this.userView.getUserName();*/
					StringBuffer content = new StringBuffer();
					content.append("<table width=\"100%\" border=\"0\">");
					content.append("<tr><td colspan=\"3\">");
					content.append(username+", 您好：");
					content.append("</td></tr>");
					content.append("<tr><td width=\"20\">&nbsp;</td>");
					content.append("<td>");
					content.append("您于"+format.format(P0114)+"提交的日志已被"+ms+"，");
					content.append("请您及时查阅。");
					content.append("</td></tr>");
					content.append("<tr><td colspan=\"3\" align=\"right\">");
					content.append("</td></tr></table>");
					
					EMailBo bo=new EMailBo(this.getFrameconn(),true,"");
	    	    	bo.sendEmail(title,content.toString(),"",frommail,tomail);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (GeneralException e) {
			e.printStackTrace();
		}finally{
			if(rs!=null)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private String getEmailField(){
    	String str="";
    	try{
    	   RecordVo stmp_vo=ConstantParamter.getConstantVo("SS_EMAIL");
           if(stmp_vo==null)
         	  return "";
           String param=stmp_vo.getString("str_value");
           if(param==null|| "#".equals(param))
         	   return "";
           str=param;
    	}catch(Exception ex)
         {
         	ex.printStackTrace();
         }  
    	
    	return str;
    }
	//发送驳回待办
	public void sendRejectPending(String p0100,ContentDAO dao,String timestr) throws GeneralException, SQLException{
		RecordVo vo = new RecordVo("p01");
		vo.setString("p0100", p0100);
		vo = dao.findByPrimaryKey(vo);	
		String state=vo.getString("state");
		String url="/performance/workdiary/myworkdiaryshow.do?b_add=link&query=update&state="+PubFunc.encryption(state)+"&p0100="+p0100+"&timestr="+timestr;
		StringBuffer str=new StringBuffer();
		str.append("您 ");
		Date P0104_D=vo.getDate("p0104");
		Date P0106_D=vo.getDate("p0106");
		if(P0104_D!=null)
		{
			str.append(DateUtils.format(P0104_D,"yyyy.MM.dd"));
		}
		if(P0106_D!=null && !"0".equals(state))
		{
			str.append("~"+DateUtils.format(P0106_D,"yyyy.MM.dd"));
		}
		str.append(" 的 ");
//		String log_type1 = vo.getString("log_type");
//		if(log_type1!=null && log_type1.equals("1")){
//			str.append("的工作计划");
//		}
//		if(log_type1!=null && log_type1.equals("2")){
//			str.append("的工作总结");
//		}
		str.append("("+getStateDesc(Integer.parseInt(state))+")"+" 已驳回");
		PendingTask imip=new PendingTask();
		/**
	     * 向待办库中加入新的待办
	     * @param pendingCode 待办编号
	     * @param appType 申请待办的类型
	     * @param pendingTitle  待办标题
	     * @param senderMessage 待办信息发送人usercode（发送人用户登录名）Usr000001     
	     * @param receiverMessage 待办信息接收人usercode（接收人用户登录名）Usr000001
	     * @param pendingURL  待办链接地址
	     * @param pendingStatus  待办状态（0：待办， 1：已办，2：待阅，3：已阅）
	     * @param pendingLevel  待办级别（0：非重要，1：重要）
	     * @param pendingType  待办所在应用中的类别（不同类型的绩效考核的中文名称，如个人考核，团队考核）
	     * @return
	     */
		String pendingCode = this.getPendingCode(this.userView.getA0100(), this.userView.getDbname());
		url+="&pdCode="+pendingCode;
		imip.insertPending(pendingCode,"W",str.toString(),this.userView.getDbname()+this.userView.getA0100(),this.userView.getDbname()+this.userView.getA0100(),
				url,0,0,"",this.userView);
	}
	private String getPendingCode(String a0100,String nbase)
	{
		Date d=new Date();
		return  "HRMS-"+nbase+a0100+"-"+d.getTime()+Math.round(Math.ceil(Math.random()*10));
	}
	private String getStateDesc(int state)
	{
		StringBuffer buf=new StringBuffer();;
		switch(state)
	    {
	       case 0:
	       {
	    	   buf.append("日报");
	    	   break;
	       }
	       case 1:
	       {
	    	   buf.append("周报");
	    	   break;
	       }
	       case 2:
	       {
	    	   buf.append("月报");
	    	   break;
	       }
	       case 3:
	       {
	    	   buf.append("季报");
	    	   break;
	       }
	       case 4:
	       {
	    	   buf.append("年报");
	    	   break;
	       }		       
	    }
		return buf.toString();
	}
}
