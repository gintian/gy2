package com.hjsj.hrms.businessobject.performance.workdiary;

import com.hjsj.hrms.businessobject.performance.workplanteam.WorkPlanTeamBo;
import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;


public class WorkdiarySelStr{
	 public Connection con = null;
	 public String returnURL;
	    public String target;
	    
		public String getReturnURL() {
			return returnURL;
		}
		public void setReturnURL(String returnURL) {
			this.returnURL = returnURL;
		}
		public String getTarget() {
			return target;
		}
		public void setTarget(String target) {
			this.target = target;
		}
	public String getBr(String value){
		String valueresult ="";
		for(int i=0;i<value.length();i++)
		{
		   if("\n".equals(value.substring(i,i+1))) {
               valueresult+="<br>";
           } else {
               valueresult+=value.substring(i,i+1);
           }
		}	
		return valueresult;
	}
	public String getenter(String value){
//		Pattern pattern=Pattern.compile("<br>");
//		Matcher matcher=pattern.matcher(value);
		value=value.replaceAll("<br>","\n");
		
		return value;
	}
	/**
	 * 得到直接上级姓名
	 * @param a0100
	 * @param dao
	 * @return
	 */
	public ArrayList getSuperiorUser(String a0100,ContentDAO dao)
	{
		String sql="";
		sql="select pmb.*,pmbs.name from per_mainbody_std pmb,per_mainbodyset pmbs where pmb.body_id=pmbs.body_id  and ";		
		if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
            sql+=" level_o";
        } else {
            sql+=" level ";
        }
		//body_id=5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
		String body_id="1";
		sql+="="+body_id+"  and pmb.object_id='"+a0100+"'";
		RowSet rs=null;
		String loguser=ConstantParamter.getLoginUserNameField().toLowerCase();
		ArrayList list=new ArrayList();
		try {
			rs=dao.search(sql);
			while(rs.next())
			{
				LazyDynaBean bean=new LazyDynaBean();
				bean.set("a0101", rs.getString("a0101"));
				bean.set("a0100", rs.getString("mainbody_id"));
				bean.set("username",getUsername(dao,rs.getString("mainbody_id"),loguser));
				list.add(bean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	private String getUsernameField(){
        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
        if(login_vo==null) {
            return "username";
        }
        String login_name = login_vo.getString("str_value");
        int idx=login_name.indexOf(",");
        if(idx==-1)
        {
            return "username";
        }
        String username=login_name.substring(0,idx);
        if("#".equals(username)) {
            return "username";
        }
        if("".equals(username)){
        	return "username";
        }
        return username.toLowerCase();        
	}
	/**
	 * 给上级领导发送邮件
	 * @param conn
	 * @param a0100 上级人员id
	 * @param p0100 日志编号
	 * @param flag 1:审批;0:抄送
	 * @return
	 * @throws GeneralException
	 */
	public boolean sendEMail(Connection conn,String a0100,String p0100,String flag)throws GeneralException
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy.MM.dd");
		String email=ConstantParamter.getEmailField().toLowerCase();
		String loguser=ConstantParamter.getLoginUserNameField().toLowerCase();
		String logpassword=ConstantParamter.getLoginPasswordField().toLowerCase();
		RecordVo user_vo=new RecordVo("UsrA01");
		user_vo.setString("a0100",a0100);
		ContentDAO dao=new ContentDAO(conn);
		String email_address="";//地址
		String a0101="";
		String state="";
		String username="";
		String password="";
		String startime="";
		String endtime="";
		try {
			if(dao.isExistRecordVo(user_vo))
			{
				user_vo=dao.findByPrimaryKey(user_vo);
				if(user_vo!=null && email!=null && email.length()>0)
				{
					email_address=user_vo.getString(email);//发送到哪个邮箱
					//a0101=user_vo.getString("a0101");
					username=user_vo.getString(loguser);
					password=user_vo.getString(logpassword);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(email_address!=null&&email_address.length()>0)
		{
			String sql="select state,a0101,p0104,p0106 from p01 where p0100='"+p0100+"'";
			RowSet rs=null;
			try {
				rs=dao.search(sql);
				if(rs.next())
				{
					state=rs.getString("state");//=0 日报,1 周报,2 月报,3季报,4 年报
					a0101=rs.getString("a0101");
					startime=sdf.format(rs.getDate("p0104"));
					endtime=sdf.format(rs.getDate("p0106"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
			}
			StringBuffer buf=new StringBuffer();
			if(state==null||state.length()<=0) {
                return false;
            }
			StringBuffer title=new StringBuffer();//邮件标题
			if("0".equalsIgnoreCase(state)){
				title.append(a0101+"　"+startime);
			}else{
				title.append(a0101+"　"+startime+"~"+endtime);
			}
			
			title.append("　"+ResourceFactory.getProperty("hire.jp.apply.of")+getStateDesc(Integer.parseInt(state)));
			if(flag!=null&& "0".equals(flag)) {
                title.append(ResourceFactory.getProperty("work.diary.emial.title2"));
            } else {
                title.append(ResourceFactory.getProperty("work.diary.emial.title"));
            }
			buf.append(ResourceFactory.getProperty("work.diary.emial.mess1")+a0101+ResourceFactory.getProperty("hire.jp.apply.of"));
			buf.append(getStateDesc(Integer.parseInt(state)));		
			if(flag!=null&& "0".equals(flag)) {
                buf.append(ResourceFactory.getProperty("work.diary.emial.mess22"));
            } else {
                buf.append(ResourceFactory.getProperty("work.diary.emial.mess2"));
            }
			
			//UserObjectBo user_bo=new UserObjectBo(conn);
			//String src_addr=user_bo.getEmailAddress(a0101,"Usr");
			
			String hrp_logon_url = SystemConfig.getPropertyValue("hrp_logon_url");
			String etoken = PubFunc.convertTo64Base(username+","+password);
			String url = "/performance/workdiary/workdiaryshow.do?b_search=link&a0100=&p0100="+PubFunc.encrypt(p0100);
			url=url+"&home=4&appfwd=1"+"&etoken="+etoken;
			//buf.append("    ");
			//buf.append("<br>"+ResourceFactory.getProperty("work.diary.emial.mess3")+"<a href=\""+hrp_logon_url+url+"\" target=\"_blank\">"+hrp_logon_url+"</a>");
			String strEmail = buf.toString();
			
			EMailBo mailbo = new EMailBo(conn,true, "Usr");				
			mailbo.setTemplateHref(hrp_logon_url+url);
			mailbo.sendEmail(title.toString(), strEmail,"", null, email_address);
		}
		return true;
	}
	/**得到要审批的日志*/
	public ArrayList getLogWaittask(Connection conn,UserView uv,ArrayList list)
	{
		this.con = conn;
		this.createNewTable("per_diary_opinion");
		WorkPlanTeamBo wptb = new WorkPlanTeamBo(uv,conn);
		wptb.analyseParameter();
		HashMap workParametersMap = wptb.workParametersMap;
		//System.out.println(uv.getDbname()+"  "+uv.getA0100()+"  "+uv.getS_userName());
		//业务用户关联自助用户 按自助用户走
		if(uv.getS_userName()!=null&&uv.getS_userName().length()>0&&uv.getStatus()==0){
			/*uv=new UserView(uv.getS_userName(), uv.getS_pwd(), conn);
			try {
				uv.canLogin();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}*/
		}else if(uv.getStatus()!=4) {
            return list;
        }
		String dbase=uv.getDbname();
		String a0100=uv.getA0100();
		String username=uv.getUserName();
		//String username=uv.getS_userName();
		//干警
		if(
		    (workParametersMap.get("valid0")!=null && "1".equals(workParametersMap.get("valid0")))
		   ||(workParametersMap.get("valid11")!=null && "1".equals(workParametersMap.get("valid11")))
		   ||(workParametersMap.get("valid21")!=null && "1".equals(workParametersMap.get("valid21")))
		   ||(workParametersMap.get("valid12")!=null && "1".equals(workParametersMap.get("valid12")))
		   ||(workParametersMap.get("valid22")!=null && "1".equals(workParametersMap.get("valid22")))
		   ||(workParametersMap.get("valid13")!=null && "1".equals(workParametersMap.get("valid13")))
		   ||(workParametersMap.get("valid23")!=null && "1".equals(workParametersMap.get("valid23")))
		   ||(workParametersMap.get("valid14")!=null && "1".equals(workParametersMap.get("valid14")))
		   ||(workParametersMap.get("valid24")!=null && "1".equals(workParametersMap.get("valid24")))
		)
		{
			username = uv.getUserName();
		}
		//System.out.println(uv.getDbname()+"  "+uv.getA0100()+"  "+uv.getUserName());
		DbWizard db = new DbWizard(conn);
		if(!db.isExistTable("p01", false)||!db.isExistTable("per_diary_actor", false)) {
            return list;
        }
		ContentDAO dao=new ContentDAO(conn);
		RowSet rs=null;	
		String sql="";
		sql="select * from p01 where ("+Sql_switcher.isnull("p0115", "'0'")+" <> '0' or p0115 <>'') and (((p0115='02')and Curr_user='"+username+"')"
				+" or p0100 in(select p0100 from per_diary_actor where a0100='"+a0100+"' and state=1 and NBASE='"
				+dbase.toUpperCase()+"' and "+Sql_switcher.isnull("display", "0")+" =0  )) " 
				+" or (p0115='07' and a0100='"+a0100+"' ) " 
				//已批的任务无需显示给填报人 20151226 dengcan
			//	+" or ("+Sql_switcher.datalength("p0113")+">0 and p0115='03' and a0100='"+a0100+"' and p0100 in(select p0100 from per_diary_actor where NBASE='USR' and "+Sql_switcher.isnull("display", "0")+" =0  ) ) " 
				+" order by state,P0104";	
		try {
			RecordVo vo=new RecordVo("per_diary_actor");
			if(!vo.hasAttribute("display")) {
                dao.update("alter table per_diary_actor add display int");//display主页待办任务是否显示 0:显示 1:不显示
            }
			
			rs=dao.search(sql);
			while(rs.next())
			{
				CommonData cData=new CommonData();
				String p0100=rs.getString("p0100");	
				String url="/performance/workdiary/workdiaryshow.do?b_search=link&home=5&a0100=&p0100="+PubFunc.encryption(p0100);
				//请您处理 某某 起始日期~终止日期 的（日报|周报|月报）
				int state=rs.getInt("state");
				StringBuffer str=new StringBuffer();
				String flag=this.reChaoSongFlag(p0100, username, a0100, dbase, conn);
				if("1".equals(flag)) {
                    str.append(ResourceFactory.getProperty("work.diary.emial.title2")+" "+rs.getString("a0101"));
                } else {
                    str.append(ResourceFactory.getProperty("work.diary.emial.title")+" "+rs.getString("a0101"));
                }
				Date P0104_D=rs.getDate("p0104");
				Date P0106_D=rs.getDate("p0106");
				
				String p0115 = rs.getString("p0115");
				//干警
				if(
				    (workParametersMap.get("valid0")!=null && "1".equals(workParametersMap.get("valid0")))
				   ||(workParametersMap.get("valid11")!=null && "1".equals(workParametersMap.get("valid11")))
				   ||(workParametersMap.get("valid21")!=null && "1".equals(workParametersMap.get("valid21")))
				   ||(workParametersMap.get("valid12")!=null && "1".equals(workParametersMap.get("valid12")))
				   ||(workParametersMap.get("valid22")!=null && "1".equals(workParametersMap.get("valid22")))
				   ||(workParametersMap.get("valid13")!=null && "1".equals(workParametersMap.get("valid13")))
				   ||(workParametersMap.get("valid23")!=null && "1".equals(workParametersMap.get("valid23")))
				   ||(workParametersMap.get("valid14")!=null && "1".equals(workParametersMap.get("valid14")))
				   ||(workParametersMap.get("valid24")!=null && "1".equals(workParametersMap.get("valid24")))
				)
				{
					String log_type = rs.getString("log_type");
					String mdopt = "";
					if("1".equals(flag)){
						//审阅
						mdopt = SafeCode.encode(PubFunc.convertTo64Base("0"));
					}else{
						//审批
						mdopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
					}
					
				      String mdnbase= SafeCode.encode(PubFunc.convertTo64Base(rs.getString("nbase")));
				      String mda0100= SafeCode.encode(PubFunc.convertTo64Base(rs.getString("a0100")));
				      String mdp0100= SafeCode.encode(PubFunc.convertTo64Base(rs.getString("p0100")));
	                
					str.append(" ");
					//2015/12/28 dengcan 
					if("07".equals(p0115))
					{
						str.setLength(0);
						mdopt = SafeCode.encode(PubFunc.convertTo64Base("1"));
						if(0==state) {
                            url = "/performance/workdiary/myworkdiaryshow.do?b_add=link&query=update&state="+PubFunc.encryption("0")+"&p0100="+p0100;
                        }
					}
					String[] strArray = this.getTimeDescAndUrl(wptb,P0104_D,log_type,state,mdopt,mdnbase,mda0100,mdp0100,p0115).split("seprator");
					str.append(strArray[0]);
					if(log_type!=null) //20151226 dengcan 旧日志程序无需周计划|总结链接
                    {
                        url = strArray[1];
                    }
					if(log_type!=null && "1".equals(log_type)){
						str.append(" 的工作计划");
					}
					else if(log_type!=null && "2".equals(log_type)){
						str.append(" 的工作总结");
					}
					else 
					{
						str.append(" 的"+getStateDesc(state));
					}
					
					
 					if("07".equals(p0115))
 					{ 
 						 str.append("被领导驳回");
 					}
				} else if("02".equals(p0115)){
					if(P0104_D!=null)
					{
						str.append(" "+DateUtils.format(P0104_D,"yyyy.MM.dd"));
					}
					if(P0106_D!=null && state!=0)
					{
						str.append("~"+DateUtils.format(P0106_D,"yyyy.MM.dd"));
					}
					str.append(" 的("+getStateDesc(state)+")");
				} else if("07".equals(p0115)){
					url = "/performance/workdiary/myworkdiaryshow.do?b_add=link&query=update&state="+PubFunc.encryption("0")+"&p0100="+p0100;
					str.setLength(0);
					if(P0104_D!=null)
					{
						str.append(" "+DateUtils.format(P0104_D,"yyyy.MM.dd"));
					}
					str.append(" 的日志被领导驳回");
				} else{
					url = "/performance/workdiary/myworkdiaryshow.do?b_add=link&query=own&state="+PubFunc.encryption("0")+"&p0100="+p0100;
					str.setLength(0);
					if(P0104_D!=null)
					{
						str.append(" "+DateUtils.format(P0104_D,"yyyy.MM.dd"));
					}
					str.append(" 的日志有领导批示");
				}
				cData.setDataValue(url);
				cData.setDataName(str.toString());
				list.add(cData);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	/**
	 * 取得登录用户名
	 * @param dao
	 * @param a0100
	 * @param usernamefield
	 * @return
	 */
	private String getUsername(ContentDAO dao,String a0100,String usernamefield)
	{
		RecordVo user_vo=new RecordVo("UsrA01");
		user_vo.setString("a0100",a0100);
		String username="";
		try {
			if(dao.isExistRecordVo(user_vo))
			{
				user_vo=dao.findByPrimaryKey(user_vo);
				if(user_vo!=null)
				{
					username=user_vo.getString(usernamefield);					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return username;
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
	/**
	 * 返回是否为抄送人员标志
	 * @param p0100
	 * @param a0100
	 * @param nbase
	 * @param conn
	 * @return 0：不是；1：是
	 */
	public String reChaoSongFlag(String p0100,String username,String a0100,String nbase,Connection conn)
	{
		String sql="select Curr_user from p01 where p0100='"+p0100+"'";
		ContentDAO dao=new ContentDAO(conn);
		RowSet rs=null;
		String curr_user=null;
		String flag="0";
		try {
			rs=dao.search(sql);
			if(rs.next())
			{
				curr_user=rs.getString("Curr_user");
			}
			if(curr_user==null||curr_user.length()<=0||!curr_user.equals(username))
			{
				sql="select a0100 from per_diary_actor where p0100='"+p0100+"' and lower(nbase)='"+nbase.toLowerCase()+"' and a0100='"+a0100+"' and state=1";
				rs=dao.search(sql);
				if(rs.next())
				{
					flag="1";
				}
				return flag;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}
	
	
	
	
	public String reChaoSongFlag2(String p0100,String username,String a0100,String nbase,Connection conn)
	{
		String sql="select Curr_user from p01 where p0100='"+p0100+"'";
		ContentDAO dao=new ContentDAO(conn);
		RowSet rs=null;
		String curr_user=null;
		String flag="0";
		try {
			rs=dao.search(sql);
			if(rs.next())
			{
				curr_user=rs.getString("Curr_user");
			}
			if(curr_user==null||curr_user.length()<=0||!curr_user.equals(username)||curr_user.equals(username))
			{
				sql="select a0100 from per_diary_actor where p0100='"+p0100+"' and nbase='"+nbase+"' and a0100='"+a0100+"' and state=1";
				rs=dao.search(sql);
				if(rs.next())
				{
					flag="1";
				}
				return flag;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}
	public String getTimeDescAndUrl(WorkPlanTeamBo wptb,Date P0104_D,String log_type,int state,String mdopt,String mdnbase,String mda0100,String mdp0100,String p0115){
		StringBuffer buf = new StringBuffer();
		int year_num = DateUtils.getYear(P0104_D);
		int month_num =DateUtils.getMonth(P0104_D);
		int quarter_num = Integer.parseInt(this.getSeason(String.valueOf(month_num)));
		//System.out.println("month_num"+month_num);
		//System.out.println("quarter_num"+quarter_num);
		int week_num =0;
		int day_num =DateUtils.getDay(P0104_D);
		if(state==4){
			buf.append(year_num+"年");
		}
		if(state==3){
			buf.append(year_num+"年第"+quarter_num+"季度");
		}
		if(state==2){
			buf.append(year_num+"年"+month_num+"月");
		}
		if(state==1){
			LinkedHashMap weekMap = wptb.getWeekIndex(String.valueOf(year_num),"all","0");
				Set keySet=weekMap.keySet();
				  java.util.Iterator t=keySet.iterator();
		
					while(t.hasNext())
					{
						String strKey = (String)t.next();  //键值	    
						String strValue = (String)weekMap.get(strKey);   //value值  
						String strDate = strKey.replace("-",".");
						if(strDate.equals(DateUtils.format(P0104_D,"yyyy.MM.dd"))){
							String[] strArray = strValue.split("\\|");
							month_num = Integer.parseInt(strArray[0]);
							week_num = Integer.parseInt(strArray[1]);
							break;
						}
					}
			if(week_num==0) {
                buf.append((year_num+1)+"年1月1周");
            } else {
                buf.append(year_num+"年"+month_num+"月"+week_num+"周");
            }
		}
		if(state==0){
			buf.append(year_num+"年"+month_num+"月"+day_num+"日");
		}
		buf.append("seprator");
		 
		buf.append("/performance/workplan/workplanview/workplan_view_list.do?b_write=write&home=5&mdopt="+mdopt+"&mdnbase="+mdnbase+"&mda0100="+mda0100+"&log_type="+log_type+"&mdp0100="+mdp0100+"&state="+state+"&p0115="+p0115+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target);
		return buf.toString();
	}
	/**
	 * Description:  本月是第几季度
	 * @Version1.0 
	 * Aug 3, 2012 3:20:14 PM Jianghe created
	 * @param month
	 * @return
	 */
	public String getSeason(String month){
		
        int ynum=Integer.parseInt(month);
		String returnValue="";
		if(ynum==1||ynum==2||ynum==3){
			returnValue = "1";
		}
		if(ynum==4||ynum==5||ynum==6){
			returnValue = "2";
		}
		if(ynum==7||ynum==8||ynum==9){
			returnValue = "3";
		}
		if(ynum==10||ynum==11||ynum==12){
			returnValue = "4";
		}
	    return returnValue;
	}
	/**
	 * Description: 如果不存在，创建新表
	 * @Version1.0 
	 * Sep 10, 2012 10:10:34 AM Jianghe created
	 * @param tableName
	 */
	public void createNewTable(String tableName){
		try{
			DbWizard dbWizard = new DbWizard(this.con);	
		    //ContentDAO dao = new ContentDAO(this.con);
		    if(!dbWizard.isExistTable(tableName,false))
			{
		    	Table table = new Table(tableName);				
			    table.addField(getField("id", "I", 10, true));
			    table.addField(getField("p0100", "I", 10, false));				
			    table.addField(getField("B0110", "A", 30, false));				
			    table.addField(getField("E0122", "A", 30, false));				
			    table.addField(getField("E01A1", "A", 30, false));				
			    table.addField(getField("NBASE", "A", 3, false));				
			    table.addField(getField("A0100", "A", 30, false));				
			    table.addField(getField("A0101", "A", 30, false));				
			    table.addField(getField("sp_grade", "I", 4, false));				
			    table.addField(getField("Description", "M", 10, false));				
			    table.addField(getField("pg_code", "A", 30, false));				
			    table.addField(getField("sp_date", "D", 10, false));
			    dbWizard.createTable(table);
			}
		  
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 新建指标计算公式临时表字段
	 */
	public Field getField(String fieldname, String a_type, int length, boolean key)
    {
		Field obj = new Field(fieldname, fieldname);
		if ("A".equals(a_type))
		{
		    obj.setDatatype(DataType.STRING);
		    obj.setLength(length);
		} else if ("M".equals(a_type))
		{
		    obj.setDatatype(DataType.CLOB);
		} else if ("I".equals(a_type))
		{
		    obj.setDatatype(DataType.INT);
		    obj.setLength(length);
		} else if ("F".equals(a_type))
		{
		    obj.setDatatype(DataType.FLOAT);
		    obj.setLength(length);
		    obj.setDecimalDigits(5);
		} else if ("D".equals(a_type))
		{
		    obj.setDatatype(DataType.DATE);
		} else
		{
		    obj.setDatatype(DataType.STRING);
		    obj.setLength(length);
		}
		if(key) {
            obj.setNullable(false);
        }
		obj.setKeyable(key);	
		return obj;
    }
}
