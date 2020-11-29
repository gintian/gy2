package com.hjsj.hrms.transaction.kq.machine.net_signin;

import com.hjsj.hrms.businessobject.kq.kqself.NetSignIn;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CheckSinginTrans extends IBusiness{
	public void execute() throws GeneralException {
		RowSet rs=null;
		try{
		String nbase=(String)this.getFormHM().get("nbase");
		nbase = PubFunc.decrypt(nbase);
		String workdate=(String)this.getFormHM().get("workdate");	
		if(workdate!=null&&workdate.length()>0)
			workdate=workdate.replaceAll("-", ".");
		String a0100=(String)this.getFormHM().get("a0100");		
		a0100 = PubFunc.decrypt(a0100);
		StringBuffer sql=new StringBuffer();
		String classid="";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		
		sql.append("select class_id from kq_employ_shift where ");
		sql.append(" a0100='"+a0100+"' and nbase='"+nbase+"' and q03z0='"+workdate+"'");
		try {
			rs=dao.search(sql.toString());
			if(rs.next())
			{
				classid=rs.getString("class_id");
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String sdao_count_field=(String)this.getFormHM().get("sdao_count_field");
		if(sdao_count_field!=null&&sdao_count_field.length()>0)
		{
			sql.delete(0,sql.length());		
			sql.append("select "+sdao_count_field+" as sdao from q03 where a0100='"+a0100+"'");
			sql.append(" and nbase='"+nbase+"' and q03z0='"+workdate+"'");
			String sdao="xx";		
			try {
				rs=dao.search(sql.toString());
				if(rs.next())
				{
					//根据首钢更改 只有员工休息的才能签上岛签到 classid=0 是休息
					if("0".equalsIgnoreCase(classid)|| "".equals(classid))
					{
						sdao=rs.getString("sdao");
						if(sdao==null||sdao.length()<=0)
							sdao="0";
					}
				}
					
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.getFormHM().put("sdao", sdao);//0:取消上岛；1上岛；x:没有日明细数据
		}else
		{
			this.getFormHM().put("sdao", "nn");
			
		}
		String onsingin="false";
		String offsingin="false";
		if(classid==null||classid.length()<=0)
		{
			this.getFormHM().put("onsingin", onsingin);
			this.getFormHM().put("offsingin", offsingin);
			this.getFormHM().put("class_mess", "");
			return;
		}
	    NetSignIn netSignIn=new NetSignIn();
		String columns=netSignIn.kqClassShiftColumns();
		sql.delete(0,sql.length());
		sql.append("select "+columns+" from kq_class where class_id='"+classid+"'");
		StringBuffer buf=new StringBuffer();
		buf.append("");
		try {
			rs = dao.search(sql.toString());			
		
			String on_start_time="";
			String on_end_time="";
			String off_start_time="";
			String off_end_time="";
			
			if(rs.next())
			{
				
				
				buf.append(rs.getString("name"));
				buf.append("");
//				buf.append(rs.getString("onduty_1")!=null&&rs.getString("onduty_1").length()>0?"&nbsp;&nbsp;"+rs.getString("onduty_1"):"");
				String off = netSignIn.getOffduty(rs);				
//				buf.append(off!=null&&off.length()>0?"~"+off:"");
				on_start_time=rs.getString("onduty_start_1");					
				on_end_time=rs.getString("onduty_end_1");
				if(rs.getString("offduty_start_3")!=null&&rs.getString("offduty_start_3").length()>0&&rs.getString("offduty_end_3")!=null&&rs.getString("offduty_end_3").length()>0)
				{
					off_start_time=rs.getString("offduty_start_3");
					off_end_time=rs.getString("offduty_end_3");
				}else if(rs.getString("offduty_start_2")!=null&&rs.getString("offduty_start_2").length()>0&&rs.getString("offduty_end_2")!=null&&rs.getString("offduty_end_2").length()>0)
				{
					off_start_time=rs.getString("offduty_start_2");
					off_end_time=rs.getString("offduty_end_2");
				}else if(rs.getString("offduty_start_1")!=null&&rs.getString("offduty_start_1").length()>0&&rs.getString("offduty_end_1")!=null&&rs.getString("offduty_end_1").length()>0)
				{
					off_start_time=rs.getString("offduty_start_1");
					off_end_time=rs.getString("offduty_end_1");
				}
			}				
				
						
			if(on_start_time!=null&&on_start_time.length()>0&&on_end_time!=null&&on_end_time.length()>0)
			{
				sql.delete(0,sql.length());
				//这里是一个错误，如果签到了在kq_originality_data就有一条数据；这样如果判断签退也是会灰的
//				sql.append("select 1 ");
				sql.append("select location ");
				sql.append(" from kq_originality_data");
				sql.append(" where a0100='"+a0100+"'");
				if(on_start_time.compareTo(on_end_time)>0)
				{
			    	String cur_date=PubFunc.getStringDate("HH-mm");
			    	cur_date=cur_date.replaceAll("-","\\:");
			    	//下班时间大于现在时间，今天+前一天
					if(on_start_time.compareTo(cur_date)>0)
					{
						Calendar    c    =    Calendar.getInstance();    
						c.add(Calendar.DAY_OF_MONTH, -1);    
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");    
						String mDateTime=formatter.format(c.getTime());    
						String strStart=mDateTime.substring(0, 10);//得到前一天的时间
						strStart=strStart.replaceAll("-","\\.");
//						System.out.println("前一天时间 = "+strStart);
						sql.append(" and ((work_date='"+strStart+"' and work_time>='"+on_start_time+"' and work_time<='23:59') ");
						sql.append(" or (work_date='"+workdate+"' and work_time>='00:00' and work_time<='"+on_end_time+"'))");
					}else
					{
						//下班时间小于现在时间，又分两个
						//(1)现在时间大于刷卡开始时间,今天+后一天
						if(cur_date.compareTo(on_start_time)>0)
						{
							Calendar    c    =    Calendar.getInstance();    
							c.add(Calendar.DAY_OF_MONTH, +1);    
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");    
							String mDateTime=formatter.format(c.getTime());    
							String strStart=mDateTime.substring(0, 10);//得到后一天的时间
							strStart=strStart.replaceAll("-","\\.");
//							System.out.println("后一天时间 = "+strStart);
							sql.append(" and ((work_date='"+workdate+"' and work_time>='"+on_start_time+"' and work_time<='23:59') ");
							sql.append(" or (work_date='"+strStart+"' and work_time>='00:00' and work_time<='"+on_end_time+"'))");
						}else
						{
							//(2)今天23
							Calendar    c    =    Calendar.getInstance();    
							c.add(Calendar.DAY_OF_MONTH, +1);    
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");    
							String mDateTime=formatter.format(c.getTime());    
							String strStart=mDateTime.substring(0, 10);//得到后一天的时间
							strStart=strStart.replaceAll("-","\\.");
//							System.out.println("后一天时间2 = "+strStart);
							sql.append(" and ((work_date='"+workdate+"' and work_time>='"+on_start_time+"' and work_time<='23:59') ");
							sql.append(" or (work_date='"+strStart+"' and work_time>='00:00' and work_time<='"+on_end_time+"'))");
						}
					}
				}else
				{
					sql.append(" and work_date='"+workdate+"'");
					sql.append(" and work_time>='"+on_start_time+"'");			
				    sql.append(" and work_time<='"+on_end_time+"'");	
				}
			    sql.append(" and UPPER(nbase)='"+nbase.toUpperCase()+"'");			    
			    rs = dao.search(sql.toString());
//			    if(rs.next())
			    for(int i=0;rs.next();i++)
			    {
			    	String location = rs.getString("location");
			    	if("签到".equalsIgnoreCase(location))
				    {
				    	onsingin="true";
				    }
			    }
			}
			if(off_start_time!=null&&off_start_time.length()>0&&off_end_time!=null&&off_end_time.length()>0)
			{
				sql.delete(0,sql.length());
//				sql.append("select 1 ");
				sql.append("select location ");
				sql.append(" from kq_originality_data");
				sql.append(" where a0100='"+a0100+"'");	
				
				sql.append(" and work_date='"+workdate+"'");
				sql.append(" and work_time>='"+off_start_time+"'");			
			    sql.append(" and work_time<='"+off_end_time+"'");		   
			    sql.append(" and UPPER(nbase)='"+nbase.toUpperCase()+"'");
			    rs = dao.search(sql.toString());			    
//			    if(rs.next())
			    for(int i=0;rs.next();i++)
			    {
			    	String location = rs.getString("location");
			    	if("签退".equalsIgnoreCase(location))
			    	{
			    		offsingin="true";
			    	}
//			    	offsingin="true";
			    }
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//xiexd 2014.09.17加密
		a0100 = PubFunc.encrypt(a0100);
		nbase = PubFunc.encrypt(nbase);
		this.getFormHM().put("a0100", a0100);
		this.getFormHM().put("class_mess", buf.toString());
		this.getFormHM().put("onsingin", onsingin);
		this.getFormHM().put("offsingin", offsingin);
		this.getFormHM().put("nbase", nbase);
		}catch(Exception e){
			
		}finally{
        	if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }   
	  	
	}

}
