package com.hjsj.hrms.transaction.welcome;

import com.hjsj.hrms.businessobject.kq.register.KQRestOper;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * <p>
 * Title:
 * create time:2005-6-3:9:37:49
 * </p>
 * 
 * @author luangaojiong
 * @version 1.0
 *  
 */
public class SearchWelcomeBoardListTrans extends IBusiness {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {

		String unitcode = this.getUnit();//得到所在单位或操作单位
	  	String unitcodeWhere = this.getUnitWhere(unitcode);//得到单位的sql语句条件
		String flag = this.getFormHM().get("flag").toString();

		String sql = "select id,noticeunit,topic,content,createuser,createtime,period,approve,approveuser,approvetime,ext,viewcount,"+Sql_switcher.isnull("priority","9999")+" as priority,flag from announce where approve=1";
		StringBuffer strsql = new StringBuffer();
		if("11".equals(flag)){
			sql = sql.substring(0, sql.indexOf("where"));
			sql = sql + "where flag=" + flag;
		}
		strsql.append(sql);
		strsql.append(" "+unitcodeWhere);
		strsql.append(" order by priority ,createtime desc");
		//System.out.println(strsql.toString());
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList list = new ArrayList();
		//公告操作
		try 
		{
			this.frowset = dao.search(strsql.toString());
			int k=0;
			String boardflag = "0";
			Calendar now = Calendar.getInstance();
			Date cur_d=now.getTime();
			Date createtime=null;
			float timelen=0;
			String topic="";
			String id="";
			while (this.frowset.next()) 
			{
					RecordVo vo = new RecordVo("announce");
					vo.setString("viewcount", PubFunc.nullToStr(this.frowset
							.getString("viewcount")));
					vo.setString("id", this.frowset.getString("id"));
					id=this.getFrowset().getString("id");
					//haosl 2017-5-9 update 权限校验逻辑与Search6PortalInfoServlet的公告权限校验保持一致  start
//					if(!(this.userView.isHaveResource(IResourceConstant.ANNOUNCE,id))&&!"11".equals(flag))
//					{
//								continue;
//					}
					if("".equals(flag) || !"11".equals(flag))
					{
						String noticeunit = this.frowset.getString("noticeunit");
					    if(!(userView.isHaveResource(IResourceConstant.ANNOUNCE,id)||this.isNoticeUnit(noticeunit) || this.isBelongUnit(noticeunit, unitcode)))
	    				{
	    					continue;
	    				}	
					}
					//haosl 2017-5-9 update 权限校验逻辑与Search6PortalInfoServlet的公告权限校验保持一致  end
					vo.setString("content",Sql_switcher.readMemo(this.frowset,"content"));
					vo.setString("createuser", PubFunc.nullToStr(this.frowset.getString("createuser")));
					createtime=this.frowset.getDate("createtime");
					if(createtime!=null)
					{
						timelen=KQRestOper.toHourFormMinute(createtime,cur_d);
						timelen=timelen/24;						
					}
					if(timelen>3)
					{
						topic=PubFunc.nullToStr(this.frowset.getString("topic"));
//						if(topic.length()>18)
//							topic=topic.substring(0,18)+"...";
						/* jingq add 2014.4.22 修改公告栏更多页面显示的公告主题字数 */
						topic=topic.replaceAll("\\s*", "");
						if(topic.length()>40)
						    
						    topic=new PubFunc().splitString(topic,100);
					}else
					{
						topic=PubFunc.nullToStr(this.frowset.getString("topic"));
//						if(topic.length()>18)
//							topic=topic.substring(0,18)+"...";
						topic=topic.replaceAll("\\s*", "");//去空格，制表符，换页符等大部分空白字符.
						if(topic.length()>31)
						    topic=new PubFunc().splitString(topic, 58)+"...";
						topic=topic+"<img src='/images/new0.gif' border='0'>";
					}
					vo.setString("topic",topic);					
					vo.setDate("createtime", PubFunc.FormatDate(this.frowset.getDate("createtime")));
			        String period=PubFunc.NullToZero(this.frowset.getString("period"));
					vo.setString("period", period);
					String temp="";
					temp = PubFunc.nullToStr(this.frowset.getString("approve"));
					if ("1".equals(temp.trim())||"11".equals(flag)) {
						vo.setString("approve", "是");
					} else {
						vo.setString("approve", "否");
					}
					vo.setString("approveuser", PubFunc.nullToStr(this.frowset.getString("approveuser")));
					String approvetime=PubFunc.FormatDate(this.frowset.getDate("approvetime"));
					vo.setDate("approvetime",approvetime);
					vo.setString("ext",PubFunc.nullToStr(this.frowset.getString("ext")));
					String msg = this.frowset.getString("flag");
					if(!"11".equals(msg)){
						msg = "";
					}
					vo.setString("flag", msg);
					int dayInt=Integer.parseInt(period);
					String nowDate=DateStyle.getSystemTime();
                    if(nowDate!=null&&nowDate.length()>0)
                    	nowDate=nowDate.substring(0,10);
					if((!PubFunc.compareDate(PubFunc.DoFormatSecDate(approvetime),nowDate,dayInt))||"11".equals(flag))
					{
						k++;
						list.add(vo);
						boardflag = "1";
					}
					/**取前10行*/
					/*if(k==10 && flag.equals("1"))
					{
						break;
					}*/
			}
			if ("0".equals(boardflag)) {
				this.getFormHM().put("boardflag", "1");
			} else {
				this.getFormHM().put("boardflag", "0");
			}
			this.getFormHM().put("boardlist", list);
			this.getFormHM().put("totalNum",String.valueOf(k));
			//结束公告操作
		}
		catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		} 
		String welcome_marquee="0";
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
		welcome_marquee=sysoth.getValue(Sys_Oth_Parameter.WELCOME_MARQUEE);
		if(welcome_marquee==null||welcome_marquee.length()<=0)
		{
			welcome_marquee="0";
		}
        this.getFormHM().put("welcome_marquee",welcome_marquee);
        /*String newssql = "select count(news_id) as number from appoint_news where (senduser = '"+userView.getUserName()+"' or (inceptuser = '"+userView.getUserName()+"' and state <> 0)) and (state = 1 and inceptuser ='"+userView.getUserName()+"')";
        try {
			RowSet rs = dao.search(newssql);
			if(rs.next()){
				String number = rs.getString("number");
				this.getFormHM().put("news",number);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
	}
	
///////////////////////郭峰增加////////////////////////////////////////
	/**
	 * 获取本单位及上级单位的sql语句条件
	 * @param codeid
	 * @return
	 */
	public String getUnitWhere(String codeid){
		String strWhere = "";
		if(!("".equals(codeid)) && !(codeid == null)){//如果不是超级用户
			strWhere = "('";
			int n = codeid.length();
			for(int i=0;i<n;i++){
				strWhere +=codeid.substring(0,codeid.length()-i)+"','";
			}
			strWhere = strWhere.substring(0, strWhere.length()-2);
			strWhere = " and (unitcode in "+strWhere+") or unitcode is null or unitcode like '"+codeid+"%')";
		}
		return strWhere;
	}
	/**
	 * 获取单位。
	 * @return
	 */
	public String getUnit(){
		String unit = "";
		if(!userView.isSuper_admin()){//如果不是超级用户
			int userType = this.userView.getStatus();//判断是业务用户还是自助用户。如果是4则是自助用户,0是业务用户。
			if(userType==4){//如果是自助用户
				unit = this.userView.getUserOrgId();//得到用户所在单位
			}else if(userType==0){//如果是业务用户，先看操作单位。如果没有，则看管理范围
				unit = getOperUnit();
			}
		}
		return unit;
	}
	/*
	 * 查出操作单位（如果有多个，则只取第一个。如果是部门，则取出它所在的单位）。如果没有操作单位，则查出管理范围所在的单位。
	 * **/
	public String getOperUnit() 
	{
			String unit = "";
			String operOrg = this.userView.getUnit_id();
			if (operOrg!=null && operOrg.length() > 3) //如果有操作单位
			{
				String[] temp = operOrg.split("`");
				String unitordepart = temp[0];
				if ("UN".equalsIgnoreCase(unitordepart.substring(0, 2)))//如果是单位
					unit = unitordepart.substring(2);
				else//如果是部门
					unit = getUnit(unitordepart.substring(2));
			}
			else if((!this.userView.isSuper_admin()) && ("".equalsIgnoreCase(operOrg))) // 如果不是超级用户，且没有操作单位
			{
				String codePrefix = this.userView.getManagePrivCode();
				String codeid = this.userView.getManagePrivCodeValue();
				if("UN".equalsIgnoreCase(codePrefix))//如果是单位
					unit = codeid;
				else//如果是部门
					unit = this.getUnit(codeid);
			}
		return unit;		
	}
	

	/**
	 * 通过部门得到所属单位
	 * */
	public String getUnit(String codeid){
		String unit = "";
		try{
			String style = "";//返回UM或者UN
			StringBuffer sb = new StringBuffer();
			sb.append("select codesetid,codeitemid from organization where codeitemid= (select parentid from organization where codeitemid='"+codeid+"')");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sb.toString());
			if(this.frowset.next()){
				style = this.frowset.getString("codesetid");
				unit = this.frowset.getString("codeitemid");
			}
			if("UM".equalsIgnoreCase(style))
				getUnit(unit);
		}catch(SQLException e){
			e.printStackTrace();
		}
		return unit;
	}
///////////////////////郭峰增加////////////////////////////////////////

/**
 * 业务用户、自助用户的userView.getUnitIdByBusi("4")包含noticeunit中通知的机构即可浏览此公告信息
 * @param noticeunit
 * @return 有为true 没有false
 */
	private boolean isNoticeUnit(String noticeunit){
		boolean flag = false;
		if(noticeunit==null||noticeunit.length()==0)
			return flag;
		if(userView.isSuper_admin())
			return true;
		String unitBusi = userView.getUnitIdByBusi("4");
		if(unitBusi.length()>0 && !"UN".equalsIgnoreCase(unitBusi)){//haosl update  !"UN".equalsIgnoreCase(unitBusi) 没有权限范围则不允许看到公告 20170508 
			unitBusi = PubFunc.getTopOrgDept(unitBusi.replaceAll(",", "`"));
			if("UM`".equalsIgnoreCase(unitBusi)){
				unitBusi = "UN"+userView.getUserOrgId()+"`";
			}
			
			noticeunit = PubFunc.getTopOrgDept(noticeunit.replaceAll(",", "`"));
			String[] strS=StringUtils.split(unitBusi,"`");
	 		String[] strD=StringUtils.split(noticeunit,"`");
			for(int i=0,n=strS.length;i<n;i++){
				String busi = strS[i];
				if(busi.length()>0){
					for(int q=0,m=strD.length;q<m;q++){
						String notice = strD[q];
						if(notice.substring(2).startsWith(busi.substring(2))){
							flag = true;
							break;
						}
					}
					if(flag)
						break;
				}
			}
		}
		return flag;
	}
	/**
	 * 是否归属于通知的机构
	 * @param noticeunit：通知到的机构
	 * @param unitcode：本人归属机构
	 * @return
	 */
	private boolean isBelongUnit(String noticeunit, String unitcode){
		boolean flag = false;
		int userType = userView.getStatus();
		if(userType == 0){//业务用户时，不校验是否归属于通知机构
			return flag;
		}
		if(noticeunit==null||noticeunit.length()==0){
			return flag;
		}
		if(userView.isSuper_admin()){
			return true;
		}
			
		noticeunit = PubFunc.getTopOrgDept(noticeunit.replaceAll(",", "`"));
 		String[] strD=StringUtils.split(noticeunit,"`");
		for(int q=0,m=strD.length;q<m;q++){
			String notice = strD[q];
			if(unitcode.startsWith(notice.substring(2))){//所在机构归小于通知的机构
				flag = true;
				break;
			}
		}
		return flag;
	}	
}