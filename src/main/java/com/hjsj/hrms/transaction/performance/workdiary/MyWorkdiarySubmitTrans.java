package com.hjsj.hrms.transaction.performance.workdiary;

import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySelStr;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class MyWorkdiarySubmitTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();

		String state = (String)hm.get("state");
		state=state!=null&&state.trim().length()>0?state:"";
		
		String upsql="";
		if("0".equals(state)){//如果是日报
			ArrayList seldiary=(ArrayList) hm.get("seldiary");
			upsql=this.getUpstr(seldiary);
		}else{
			HashMap reqhm=(HashMap) hm.get("requestPamaHM");
			String p0100 = (String)reqhm.get("p0100");
			reqhm.remove("p0100");
			p0100=p0100.substring(0,p0100.length()-1);
			upsql=getUpstr(p0100);
		}

		
		try {
			ContentDAO dao=new ContentDAO(this.frameconn);
			dao.update(upsql);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	public String getUpstr(ArrayList seldiary){
		StringBuffer sbup=new StringBuffer();
		Date currdate=new Date(System.currentTimeMillis());	
		String Curr_user=(String)this.getFormHM().get("curr_user");
		//如果找不到上级领导人 按原有业务走
		if(Curr_user==null || Curr_user.length()<=0){
			if(Sql_switcher.searchDbServer()== Constant.ORACEL){
				sbup.append("update p01 set p0115='02' , p0114=to_date('"+currdate.toString()+"','yyyy-mm-dd') where " );
			}else{
				sbup.append("update p01 set p0115='02' , p0114='"+currdate.toString()+"' where " );
			}
		}else{
			if(Sql_switcher.searchDbServer()== Constant.ORACEL){
				sbup.append("update p01 set p0115='02' ,Curr_user='"+Curr_user+"' , p0114=to_date('"+currdate.toString()+"','yyyy-mm-dd') where " );
			}else{
				sbup.append("update p01 set p0115='02' ,Curr_user='"+Curr_user+"'  , p0114='"+currdate.toString()+"' where " );
			}
		}
		RecordVo vo = new RecordVo("p01");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		for(int i=0;i<seldiary.size();i++){
			DynaBean dynabean=(DynaBean) seldiary.get(i);
			String p0100=(String) dynabean.get("p0100");
			vo.setString("p0100", p0100);
			try {
				vo = dao.findByPrimaryKey(vo);
				sendPending(vo, dao, "");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(i==0){
				sbup.append("p0100='"+p0100+"'");
			}else{
				sbup.append("or p0100='"+p0100+"'");
			}
			
			//发送邮件
			mailSuperiorUser(p0100);
		}
		return sbup.toString();
	}
	public String getUpstr(String p0100){
		StringBuffer sbup=new StringBuffer();
		Date currdate=new Date(System.currentTimeMillis());	

		String Curr_user=(String)this.getFormHM().get("curr_user");
		//如果找不到上级领导人 按原有业务走
		if(Curr_user==null || Curr_user.length()<=0){
			if(Sql_switcher.searchDbServer()== Constant.ORACEL){
				sbup.append("update p01 set p0115='02' , p0114=to_date('"+currdate.toString()+"','yyyy-mm-dd') where " );
			}else{
				sbup.append("update p01 set p0115='02' , p0114='"+currdate.toString()+"' where " );
			}
		}else{
			if(Sql_switcher.searchDbServer()== Constant.ORACEL){
				sbup.append("update p01 set p0115='02' ,Curr_user='"+Curr_user+"' , p0114=to_date('"+currdate.toString()+"','yyyy-mm-dd') where " );
			}else{
				sbup.append("update p01 set p0115='02' ,Curr_user='"+Curr_user+"'  , p0114='"+currdate.toString()+"' where " );
			}
		}
		//发送邮件
		String[] pid=p0100.split(",");
		RecordVo vo = new RecordVo("p01");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		for (int i = 0; i < pid.length; i++) {///一条日志只有一个p0100.多个p0100说明有多条日志，说明选中记录选中了多条。所以应该对每一条日志都给上级发一次邮件。
			vo.setString("p0100", pid[i]);
			try {
				vo = dao.findByPrimaryKey(vo);
				sendPending(vo, dao, "");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mailSuperiorUser(p0100);
		}
		p0100=p0100.replaceAll(",","','");
		sbup.append(" p0100 in ('"+p0100+"')");
		

		return sbup.toString();
	}

	/**
	 * 报批有考核关系的发送邮件
	 * @param p0100
	 * @throws SQLException
	 * @author LiWeichao
	 */
	private void mailSuperiorUser(String p0100){
		WorkdiarySelStr wss=new WorkdiarySelStr();
		ArrayList list = wss.getSuperiorUser(this.getUserView().getA0100(), new ContentDAO(this.getFrameconn()));
		for (int i = 0; i < list.size(); i++) {
			LazyDynaBean ldb=(LazyDynaBean) list.get(i);
			if(this.formHM.get("curr_user")!=null&&this.formHM.get("curr_user").equals((String)ldb.get("username"))){
				String a0100=(String)ldb.get("a0100");
				try {wss.sendEMail(this.getFrameconn(), a0100, p0100,"1");} catch (GeneralException e) {e.printStackTrace();}
			}
		}
	}
	public void sendPending(RecordVo vo,ContentDAO dao,String times) throws GeneralException, SQLException{
	    String p0100=vo.getString("p0100");	
		String url="/performance/workdiary/workdiaryshow.do?b_search=link&home=5&a0100=&p0100="+p0100+"&timestr="+times;
		String state=vo.getString("state");
		StringBuffer str=new StringBuffer();
		str.append(ResourceFactory.getProperty("work.diary.emial.title")+" "+vo.getString("a0101"));
		java.util.Date P0104_D=vo.getDate("p0104");
		java.util.Date P0106_D=vo.getDate("p0106");
		if(P0104_D!=null)
		{
			str.append(" "+DateUtils.format(P0104_D,"yyyy.MM.dd"));
		}
		if(P0106_D!=null && !"0".equals(state))
		{
			str.append("~"+DateUtils.format(P0106_D,"yyyy.MM.dd"));
		}
		str.append(" 的("+getStateDesc(Integer.parseInt(state))+")");
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
		java.util.Date d=new java.util.Date();
		return  "HRMS-"+nbase+a0100+"-"+d.getTime()+Math.round(Math.ceil(Math.random()*10));
	}
	/**
	 * 返回状态描述
	 * @param state
	 * @return
	 */
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
