package com.hjsj.hrms.businessobject.kq.kqself;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.pigeonhole.UpdateQ33;
import com.hjsj.hrms.module.kq.application.KqOverTimeForLeaveBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class CancelHols {

	private UserView userView;
	private Connection conn;
	public CancelHols()
	{
		
	}
	public CancelHols(UserView userView,Connection conn)
	{
		this.userView=userView;
		this.conn=conn;
	}
	/** 
	 * 销假校验标识
	 * 0  	只校验
	 * 1 	只操作数据
	 * 空 	校验 + 操作数据
	 * **/
	private String checkFlag;
	/**
	 * 查看注销记录是否存在
	 * @param id
	 * @return
	 */
	public boolean ifSaveCancelHols(String id,String q1519)
	{
		boolean isCorrect=false;
		
		if(id==null||id.length()<=0) {
            return false;
        }
		if(q1519==null||q1519.length()<=0) {
            q1519="";
        }
		if(id.equals(q1519)) {
            return false;
        }
		RowSet rs=null;
		try
		{
			StringBuffer sql=new StringBuffer();
			sql.append("select * from q15 where q1501='"+id+"'");
			
			ContentDAO dao=new ContentDAO (this.conn);
			rs=dao.search(sql.toString());
			if(rs.next()) {
                isCorrect=true;
            }
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			KqUtilsClass.closeDBResource(rs);
		}
		return isCorrect;
	}
	
	
	public void cancelTimeApp(RecordVo vo,String sels,Date kq_start,Date kq_end,boolean isCorrect,String sp,String approve)throws GeneralException
	{
		AnnualApply annualApply=new AnnualApply(this.userView,this.conn); 
		try
		{
			if("2".equalsIgnoreCase(sp))
			{
				saveCancel(vo,isCorrect,approve);
				return;
			}
			
			//sels=sels.substring(0,2); zxj 判断是否假期管理假类，只看前两位是错的
		    UpdateQ33 upq33 = new UpdateQ33(this.userView,this.conn);
		    String leavetime_type_used_overtime = KqParam.getInstance().getLEAVETIME_TYPE_USED_OVERTIME();
		    if(KqParam.getInstance().isHoliday(this.conn, vo.getString("b0110"), sels))
		    {
		    	HashMap kqItem_hash=annualApply.count_Leave(sels);
		    	float[] holiday_rules=annualApply.getHoliday_minus_rule();//年假假期规则
		    	float cancel_leave_tiem=annualApply.getHistoryLeaveTime(kq_start,kq_end,vo.getString("a0100"),vo.getString("nbase"),vo.getString("b0110"),kqItem_hash,holiday_rules);
		    	//System.out.println(leave_tiem);
		    	if(cancel_leave_tiem<=0) {
                    throw GeneralExceptionHandler.Handle(new GeneralException("",vo.getString("a0101")+"可休有效时间为0天","",""));
                }
		    	
		    	//float app_leave_tiem=getAppLeaveTime(vo,kqItem_hash);
		    	String start=DateUtils.format(kq_start,"yyyy.MM.dd HH:mm:ss");
		    	String end=DateUtils.format(kq_end,"yyyy.MM.dd HH:mm:ss");			    	
		    	RecordVo vo_19=new RecordVo("q15");
				if("5".equals(sp)&&vo.getString("q15z0")!=null&& "01".equals(vo.getString("q15z0"))&&vo.getString("q15z5")!=null&& "03".equals(vo.getString("q15z5")))
				{
				    String q1519=vo.getString("q1519");					    
				    vo_19.setString("q1501", q1519);
				    ContentDAO dao=new ContentDAO(this.conn);
				    vo_19=dao.findByPrimaryKey(vo_19);
				    if(vo_19!=null)
				    {
				    	 
				    	String history=vo_19.getString("history");
				        Date src_z1=vo_19.getDate("q15z1");
				    	Date src_z3=vo_19.getDate("q15z3");
				    	String stD=DateUtils.format(src_z1, "yyyy.MM.dd");
						String edD=DateUtils.format(src_z3, "yyyy.MM.dd");
						String stT=DateUtils.format(src_z1, "yyyy.MM.dd HH:mm:ss");
						String edT=DateUtils.format(src_z3, "yyyy.MM.dd HH:mm:ss");
						float history_CancelF[]=new float[4];
						if(holiday_rules!=null&&holiday_rules.length==7)
			           	{
							if(edD.equalsIgnoreCase(DateUtils.format(kq_end,"yyyy.MM.dd")))//先看销假的结束天和请假的结束天是否一致
							{
								if(stT.equalsIgnoreCase(start)&&!edT.equalsIgnoreCase(end))
								{
									float leave_tiem=annualApply.getHistoryLeaveTime(kq_end,src_z3,vo.getString("a0100"),vo.getString("nbase"),vo.getString("b0110"),kqItem_hash,holiday_rules);
									String history_hols_ed=annualApply.getLeaveManage(vo.getString("a0100"),vo.getString("nbase"),sels,end,DateUtils.format(src_z3, "yyyy.MM.dd HH:mm"),leave_tiem,"1",vo.getString("b0110"),kqItem_hash,holiday_rules);
									if(history!=null&&history_hols_ed!=null&&history_hols_ed.equalsIgnoreCase(history))//判断销假后的扣除的实际天和销假前扣除的实际天数一致
									{
										//不操作
									}else
									{
										history_CancelF=annualApply.getCancelHolsTimeManage(vo.getString("a0100"),vo.getString("nbase"),sels,start,end,cancel_leave_tiem,vo.getString("b0110"),kqItem_hash,stD,edD,holiday_rules);
									}
								}else
								{
									float leave_tiem=annualApply.getHistoryLeaveTime(src_z1,kq_start,vo.getString("a0100"),vo.getString("nbase"),vo.getString("b0110"),kqItem_hash,holiday_rules);
									String history_hols_ed=annualApply.getLeaveManage(vo.getString("a0100"),vo.getString("nbase"),sels,DateUtils.format(src_z1, "yyyy.MM.dd HH:mm"),start,leave_tiem,"1",vo.getString("b0110"),kqItem_hash,holiday_rules);
									if(history!=null&&history_hols_ed!=null&&history_hols_ed.equalsIgnoreCase(history))//判断销假后的扣除的实际天和销假前扣除的实际天数一致
									{
										//不操作
									}else
									{
										history_CancelF=annualApply.getCancelHolsTimeManage(vo.getString("a0100"),vo.getString("nbase"),sels,start,end,cancel_leave_tiem,vo.getString("b0110"),kqItem_hash,stD,edD,holiday_rules);
									}
								}
								
							}else if(stT.equalsIgnoreCase(start)&&!edT.equalsIgnoreCase(end))
							{
								float leave_tiem=annualApply.getHistoryLeaveTime(kq_end,src_z3,vo.getString("a0100"),vo.getString("nbase"),vo.getString("b0110"),kqItem_hash,holiday_rules);
								String history_hols_ed=annualApply.getLeaveManage(vo.getString("a0100"),vo.getString("nbase"),sels,end,DateUtils.format(src_z3, "yyyy.MM.dd HH:mm"),leave_tiem,"1",vo.getString("b0110"),kqItem_hash,holiday_rules);
								if(history!=null&&history_hols_ed!=null&&history_hols_ed.equalsIgnoreCase(history))//判断销假后的扣除的实际天和销假前扣除的实际天数一致
								{
									//不操作
								}else
								{
									history_CancelF=annualApply.getCancelHolsTimeManage(vo.getString("a0100"),vo.getString("nbase"),sels,start,end,cancel_leave_tiem,vo.getString("b0110"),kqItem_hash,stD,edD,holiday_rules);
								}
							}else
							{
								history_CancelF=annualApply.getCancelHolsTimeManage(vo.getString("a0100"),vo.getString("nbase"),sels,start,end,cancel_leave_tiem,vo.getString("b0110"),kqItem_hash,stD,edD,holiday_rules);
							}
			           	}else {
                            history_CancelF=annualApply.getCancelHolsTimeManage(vo.getString("a0100"),vo.getString("nbase"),sels,start,end,cancel_leave_tiem,vo.getString("b0110"),kqItem_hash,stD,edD,holiday_rules);
                        }
				    	
				    	String history_CancelS=history_CancelF[0]+","+history_CancelF[1]+";"+history_CancelF[2]+","+history_CancelF[3];
				    	Date start_d=vo_19.getDate("q15z1");
				    	Date end_d=vo_19.getDate("q15z3");
				    	String array_TOP[]=null;
					    String array_LAST[]=null;
					    String historyS[]=history.split(";");
					    if(historyS!=null&&historyS.length>0)
					    {
					    	 array_TOP=historyS[0].split(",");
					    	 if(historyS.length==2) {
                                 array_LAST=historyS[1].split(",");
                             }
					    }	
					    float vf_top=0;
					    float bvf_top=0;
					    if(array_TOP!=null&&array_TOP.length==2)
					    {
					    	  String value=array_TOP[0];//扣除的可休假
					    	  String balance_value=array_TOP[1];//扣除的上年结余假
					    	  vf_top=Float.parseFloat(value);
					    	  bvf_top=Float.parseFloat(balance_value);
					    	  if(vf_top>0&&history_CancelF[0]>0) {
                                  vf_top=vf_top-history_CancelF[0];
                              }
					    	  if(bvf_top>0&&history_CancelF[1]>0) {
                                  bvf_top=bvf_top-history_CancelF[1];
                              }
					    }
					    float vf_last=0;
					    float bvf_last=0;
					    if(array_LAST!=null&&array_LAST.length==2)
					    {
					    	  String value=array_LAST[0];//扣除的可休假
					    	  String balance_value=array_LAST[1];//扣除的上年结余假
					    	  vf_last=Float.parseFloat(value);
					    	  bvf_top=Float.parseFloat(balance_value);
					    	  if(vf_last>0&&history_CancelF[0]>0) {
                                  vf_last=vf_last-history_CancelF[0];
                              }
					    	  if(bvf_last>0&&history_CancelF[1]>0) {
                                  bvf_last=bvf_last-history_CancelF[1];
                              }
					    }						    
					    //annualApply.holsBackfill(DateUtils.format(start_d,"yyyy.MM.dd HH:mm"),DateUtils.format(end_d,"yyyy.MM.dd HH:mm"),vo.getString("a0100"),vo.getString("nbase"),sels,history_CancelS,0);
//					    history=vf_top+","+bvf_top+";"+vf_last+","+bvf_last;
//					    vo_19.setString("history", history);
					    dao.updateValueObject(vo_19);
				    }
				}
				saveCancel(vo,isCorrect,approve);
				if(leavetime_type_used_overtime != null){
				    if(leavetime_type_used_overtime.equalsIgnoreCase(sels))//销调休假 返还销掉的时长
                    {
                        upq33.returnLeaveTime(vo);
                    }
				}
				if("5".equals(sp))//审批后
				{
					if(vo.getString("q15z0")!=null&& "01".equals(vo.getString("q15z0"))&&vo.getString("q15z5")!=null&& "03".equals(vo.getString("q15z5")))
					{
					    // 请假单审批时间
                        Date spDate = vo_19.getDate("q15z7");
                        if (spDate == null) {
                            spDate = new Date();
                        }
                        
                        //审批时间往前提一秒, 使回退操作可以包含本条记录
                        Calendar calendar = Calendar.getInstance(); 
                        calendar.setTime(spDate); 
                        calendar.add(Calendar.SECOND, -1); 
                        spDate = calendar.getTime(); 
                        
                        String spTime = DateUtils.format(spDate,"yyyy-MM-dd HH:mm:ss");
                        annualApply.bachReStatHols(spTime,vo.getString("a0100"),vo.getString("b0110"),vo.getString("nbase"),sels,kqItem_hash);
                    }
				}					
		    }else
		    {
		    	saveCancel(vo,isCorrect,approve);
		    	if(leavetime_type_used_overtime != null){
		    	    if(leavetime_type_used_overtime.equalsIgnoreCase(sels) && "app".equalsIgnoreCase(approve))//销调休假 返还销掉的时长
                    {
                        upq33.returnLeaveTime(vo);
                    }
		    	}
		    }
		}catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 返回请假申请的时间
	 * @param start_date
	 * @param end_date
	 * @param a0100
	 * @param nbase
	 * @param b0110
	 * @param kqItem_hash
	 * @return
	 */
	public float getAppLeaveTime(RecordVo vo,HashMap kqItem_hash)throws GeneralException
	{
		String q1519=vo.getString("q1519");
		if(q1519==null||q1519.length()<=0) {
            return 0;
        }
		RecordVo app_vo=new RecordVo("q15");
		app_vo.setString("q1501",q1519);
		float timeValue=0;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			app_vo=dao.findByPrimaryKey(app_vo);
			if(app_vo==null) {
                return 0;
            }
			
			Date start_date=app_vo.getDate("q15z1");;
			Date end_date=app_vo.getDate("q15z3");
			String a0100=app_vo.getString("a0100");
			String nbase=app_vo.getString("nbase");
			String b0110=app_vo.getString("b0110");
			AnnualApply annualApply=new AnnualApply(this.userView,this.conn); 
			timeValue=annualApply.getLeaveTime(start_date,end_date,a0100,nbase,b0110,kqItem_hash);
		}catch(Exception e)
		{
		e.printStackTrace();	
		}
		return timeValue;
	}
	
	/**
	 * 保存申请
	 * @param table
	 * @param ta
	 * @param vo
	 */
	public void saveCancel(RecordVo vo,boolean isCorrect,String approve)throws GeneralException
	{
		
		boolean oper=ifSaveCancelHols(vo.getString("q1501"),vo.getString("q1519"));		
		ContentDAO dao=new ContentDAO(this.conn);
		if(!getMacthingCancelTime(vo,dao))
		{
			String uname = vo.getString("q1509");
			if(uname == null || uname.length() < 1){
				uname = "";
			}else{
				uname += uname + "，";
			}
			throw GeneralExceptionHandler.Handle(new GeneralException("",uname+ "销假申请超出请假时间范围，请核实！","",""));
		}
		if(isCorrect)
		{
			RowSet rs=null;
			try
			{
				 if(!oper)
				 {
				    String q1519new=vo.getString("q1519");					    
				    StringBuffer sql=new StringBuffer();
					sql.append("select * from q15 where q1519='"+q1519new+"'");
					rs=dao.search(sql.toString());
					// linbz 校验销假单是否已存在
					if(rs.next()) {
						String q1501 = rs.getString("q1501");
						
						sql.setLength(0);
						sql.append("update q15 set q15z7=?,q1509=?,q15z5=?,q15z0=? where q1501=? ");
						ArrayList list = new ArrayList();
						list.add(vo.getDate("q15z7"));
						list.add(vo.getString("q1509"));
						list.add(vo.getString("q15z5"));
						list.add(vo.getString("q15z0"));
						list.add(q1501);
						dao.update(sql.toString(), list);
					}
					else {
	//					 RecordVo vos = new RecordVo("q15");
						 IDGenerator idg=new IDGenerator(2,this.conn);
						 String insertid="";
						 boolean iscorrect = false;
						 //szk 7723 验证id是否存在
						 while (!iscorrect) {
							 insertid = idg.getId(("q15.q1501").toUpperCase());
							 iscorrect = checkAppkeyid2("q15", insertid, dao);
						 }
						 
						 java.util.Date q1505d = null;
						 java.util.Date q15z1d = null;
						 java.util.Date q15z3d = null;
						 Object q1505 =vo.getObject("q1505");
						 if(q1505 instanceof String) {
                             q1505d =DateUtils.getDate(String.valueOf(q1505),"yyyy-MM-dd HH:mm");
                         } else {
                             q1505d = (Date)q1505;
                         }
						 
						 Object q15z1 = vo.getObject("q15z1");
						 if(q15z1 instanceof String) {
                             q15z1d =DateUtils.getDate(String.valueOf(q15z1),"yyyy-MM-dd HH:mm");
                         } else {
                             q15z1d = (Date)q15z1;
                         }
						 
						 Object q15z3= vo.getObject("q15z3");
						 if(q15z3 instanceof String) {
                             q15z3d =DateUtils.getDate(String.valueOf(q15z3),"yyyy-MM-dd HH:mm");
                         } else {
                             q15z3d = (Date)q15z3;
                         }
						 
						 vo.setDate("q1505", q1505d);
						 vo.setDate("q15z1", q15z1d);
						 vo.setDate("q15z3", q15z3d);
						 vo.setString("q1501",insertid);
						 vo.setString("q1517","1");
						 dao.addValueObject(vo); 
					 }
				 }else if(oper)
				 {
					 String q15z5=vo.getString("q15z5");
					 String q1505 =vo.getString("q1505");
					 String q15z1 = vo.getString("q15z1");
					 String q15z3= vo.getString("q15z3");
					 java.util.Date q1505d =DateUtils.getDate(q1505,"yyyy-MM-dd HH:mm");
					 java.util.Date q15z1d =DateUtils.getDate(q15z1,"yyyy-MM-dd HH:mm");
					 java.util.Date q15z3d =DateUtils.getDate(q15z3,"yyyy-MM-dd HH:mm");
					 vo.setDate("q1505", q1505d==null?vo.getDate("q1505"):q1505d);
					 vo.setDate("q15z1", q15z1d==null?vo.getDate("q15z1"):q15z1d);
					 vo.setDate("q15z3", q15z3d==null?vo.getDate("q15z3"):q15z3d);
					 vo.setString("q1517","1");
					 if("qicao".equals(approve)) {
                         if(!"03".equals(q15z5)) {
                             dao.updateValueObject(vo);
                         }
                     }
					 if("app".equals(approve)) {
                         dao.updateValueObject(vo);
                     }
				 }
				 
			}catch(Exception e)
			{
				e.printStackTrace();
		        throw GeneralExceptionHandler.Handle(e); 
			}finally {
	            KqUtilsClass.closeDBResource(rs);
	        }
		}
	}
	/**
	 * 检查id是否已被用了
	 * @param table
	 * @param id
	 * @param dao
	 * @return
	 * @author szk
	 * 2015-2-27下午05:50:45
	 */
	private boolean checkAppkeyid2(String table, String id, ContentDAO dao) {
		boolean iscorrect = true;
		RowSet rs = null;
		try {
			String sql = "select 1 from " + table + " where " + table + "01='"
					+ id + "'";
			rs = dao.search(sql);
			if (rs.next()) {
                iscorrect = false;
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			KqUtilsClass.closeDBResource(rs);
		}
		return iscorrect;
	}
	private boolean getMacthingCancelTime(RecordVo vo,ContentDAO dao)
	{
		HashMap map = getSpDates(vo, "q15");
		Date kq_start = (Date)map.get("kq_start");	
		Date kq_end = (Date)map.get("kq_end");	
        String q1519=vo.getString("q1519");
        RecordVo o_vo=new RecordVo("q15");
        o_vo.setString("q1501", q1519);
        boolean isCorrect=true;
        try
        {
        	o_vo=dao.findByPrimaryKey(o_vo);
        	if(o_vo!=null)
        	{
        		Date d_start=o_vo.getDate("q15z1");
        		Date d_end=o_vo.getDate("q15z3");        		
                if(d_start.after(kq_start))
                {
                	isCorrect=false;
                }else if(kq_end.after(d_end))
                {
                	isCorrect=false;
                }
                
        	}
        }catch(Exception e)
        {
        	e.printStackTrace();
        }
        return isCorrect;
	}
	/**
	 * 校验销假申请时间范围
	 * @param vo
	 * @param dao
	 * @param tableflag
	 * @return
	 */
	private boolean getMacthingCancelTime(RecordVo vo,ContentDAO dao, String tableflag)
	{
		HashMap map = getSpDates(vo, tableflag);
		Date kq_start = (Date)map.get("kq_start");	
		Date kq_end = (Date)map.get("kq_end");	
        
        String q1519=vo.getString(tableflag+"19");
        RecordVo o_vo=new RecordVo(tableflag);
        o_vo.setString(tableflag+"01", q1519);
        boolean isCorrect=true;
        try
        {
        	o_vo=dao.findByPrimaryKey(o_vo);
        	if(o_vo!=null)
        	{
        		Date d_start=o_vo.getDate(tableflag+"z1");
        		Date d_end=o_vo.getDate(tableflag+"z3");        		
                if(d_start.after(kq_start))
                {
                	isCorrect=false;
                }else if(kq_end.after(d_end))
                {
                	isCorrect=false;
                }
                
        	}
        }catch(Exception e)
        {
        	e.printStackTrace();
        }
        return isCorrect;
	}
	/**
	 * 查看注销记录是否存在
	 * @param id
	 * @param q1519
	 * @param tableflag
	 * @return
	 */
	public boolean ifSaveCancelHols(String id, String q1519, String tableflag)
	{
		boolean isCorrect=false;
		
		if(id==null||id.length()<=0) {
            return false;
        }
		if(q1519==null||q1519.length()<=0) {
            q1519="";
        }
		if(id.equals(q1519)) {
            return false;
        }
		RowSet rs=null;
		try
		{
			StringBuffer sql=new StringBuffer();
			sql.append("select * from ").append(tableflag);
			sql.append(" where ").append(tableflag).append("01='"+id+"'");
			
			ContentDAO dao=new ContentDAO (this.conn);
			rs=dao.search(sql.toString());
			if(rs.next()) {
                isCorrect=true;
            }
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			KqUtilsClass.closeDBResource(rs);
		}
		return isCorrect;
	}
	
	/**
	 * 保存销假申请 扩展 
	 * @param vo
	 * @param isCorrect
	 * @param approve
	 * @param tableflag
	 * @throws GeneralException
	 */
	public void saveCancel(RecordVo vo,boolean isCorrect,String approve, String tableflag)throws GeneralException
	{
		
		boolean oper=ifSaveCancelHols(vo.getString(tableflag+"01"), vo.getString(tableflag+"19"), tableflag);		
		ContentDAO dao=new ContentDAO(this.conn);
		if(!getMacthingCancelTime(vo, dao, tableflag))
		{
			String uname = vo.getString(tableflag+"09");
			if(uname == null || uname.length() < 1){
				uname = "";
			}else{
				uname += uname + "，";
			}
			throw GeneralExceptionHandler.Handle(new GeneralException("",uname+ "销假申请超出请假时间范围，请核实！","",""));
		}
		// 人事异动模板处理改为  先校验后编辑数据库	37801改为在update数据库时校验
//		if("0".equals(checkFlag))
//			return;
		if(isCorrect)
		{
			RowSet rs=null;
			try
			{
				 if(!oper)
				 {
					String table19new=vo.getString(tableflag+"19");					    
				    StringBuffer sql=new StringBuffer();
					sql.append("select ").append(tableflag).append("01 from ").append(tableflag).append(" where ").append(tableflag).append("19='").append(table19new).append("'");
					rs=dao.search(sql.toString());
					// linbz 校验销假单是否已存在
					if(rs.next()) {
						String table01 = rs.getString(tableflag+"01");
						
						sql.setLength(0);
						sql.append("update ").append(tableflag);
						sql.append(" set ").append(tableflag).append("z7=?,").append(tableflag).append("09=?,");
						sql.append(tableflag).append("z5=?,").append(tableflag).append("z0=? ");
						sql.append(" where ").append(tableflag).append("01=? ");
						ArrayList list = new ArrayList();
						list.add(vo.getDate(tableflag+"z7"));
						list.add(vo.getString(tableflag+"09"));
						list.add(vo.getString(tableflag+"z5"));
						list.add(vo.getString(tableflag+"z0"));
						list.add(table01);
						//  37801 人事异动模板处理改为  先校验后编辑数据库
						if(!"0".equals(checkFlag)) {
                            dao.update(sql.toString(), list);
                        }
					}
					else {
	//					 RecordVo vos = new RecordVo("q15");
						 IDGenerator idg = new IDGenerator(2,this.conn);
						 String insertid = vo.getString(tableflag+"01");
						 // 35135  增加校验，防止考勤模块代销假传的单号和所销单号一样
						 String table19 = vo.getString(tableflag+"19");
						 boolean iscorrect = false;
						 //szk 7723 验证id是否存在
						 while (!iscorrect && (StringUtils.isEmpty(insertid) || insertid.equalsIgnoreCase(table19))) {
							insertid = idg.getId((tableflag+"."+tableflag+"01").toUpperCase());
							iscorrect = checkAppkeyid2(tableflag, insertid, dao);
						 }
						 HashMap map = getSpDates(vo, tableflag);
						 Date kq_start = (Date)map.get("kq_start");	
						 Date kq_end = (Date)map.get("kq_end");	
						 Date table05d = (Date)map.get("table05d");	
							
						 vo.setDate(tableflag+"05", table05d);
						 vo.setDate(tableflag+"z1", kq_start);
						 vo.setDate(tableflag+"z3", kq_end);
						 vo.setString(tableflag+"01",insertid);
						 vo.setString(tableflag+"17","1");
						 dao.addValueObject(vo); 
					}
				 }else if(oper)
				 {
					 String q15z5=vo.getString(tableflag+"z5");
					 HashMap map = getSpDates(vo, tableflag);
					 Date kq_start = (Date)map.get("kq_start");	
					 Date kq_end = (Date)map.get("kq_end");	
					 Date table05d = (Date)map.get("table05d");	
					 
					 vo.setDate(tableflag+"05", table05d);
					 vo.setDate(tableflag+"z1", kq_start);
					 vo.setDate(tableflag+"z3", kq_end);
					 vo.setString(tableflag+ "17","1");
					 //  37801 人事异动模板处理改为  先校验后编辑数据库 // 39430防止驳回后的单子再次报批，需更改审批状态故增加校验
					 if(!"0".equals(checkFlag) || "02".equals(vo.getString(tableflag+"z5"))) {
						 
						 if("qicao".equals(approve)) {
                             if(!"03".equals(q15z5)) {
                                 dao.updateValueObject(vo);
                             }
                         }
						 if("app".equals(approve)) {
                             dao.updateValueObject(vo);
                         }
					 }
				 }
				 	       	 
			}catch(Exception e)
			{
				e.printStackTrace();
		        throw GeneralExceptionHandler.Handle(e); 
			}finally {
	            KqUtilsClass.closeDBResource(rs);
	        }
		}
	}
	
	/**
	 * 销假方法扩展支持(请假加班公出)
	 * @param vo
	 * @param sels
	 * @param kq_start
	 * @param kq_end
	 * @param isCorrect
	 * @param sp
	 * @param approve
	 * @param tableflag
	 * @throws GeneralException
	 */
	public void cancelTimeApp(RecordVo vo,String sels,Date kq_start,Date kq_end,
			boolean isCorrect,String sp,String approve, String tableflag)throws GeneralException
	{
		AnnualApply annualApply=new AnnualApply(this.userView,this.conn); 
		try
		{
			// 33930 销加班时增加校验，如果是调休加班已用则不允许撤销
			if("q11".equalsIgnoreCase(tableflag)) {
				String msg = checkCancelOverTimeApp(vo);
				if(StringUtils.isNotEmpty(msg)) {
                    throw GeneralExceptionHandler.Handle(new GeneralException("",msg,"",""));
                }
			}
			
			// 加班 公出 申请单的销假 先做保存操作 linbz
			if("q11".equalsIgnoreCase(tableflag) || "q13".equalsIgnoreCase(tableflag)) {
				saveCancel(vo, isCorrect, approve, tableflag);
				return;
			}
			if("2".equalsIgnoreCase(sp))
			{
				saveCancel(vo, isCorrect, approve, tableflag);
				return;
			}
			
			//sels=sels.substring(0,2); zxj 判断是否假期管理假类，只看前两位是错的
		    UpdateQ33 upq33 = new UpdateQ33(this.userView,this.conn);
		    String leavetime_type_used_overtime = KqParam.getInstance().getLEAVETIME_TYPE_USED_OVERTIME();
		    if(KqParam.getInstance().isHoliday(this.conn, vo.getString("b0110"), sels))
		    {
		    	HashMap kqItem_hash=annualApply.count_Leave(sels);
		    	float[] holiday_rules=annualApply.getHoliday_minus_rule();//年假假期规则
		    	float cancel_leave_tiem=annualApply.getHistoryLeaveTime(kq_start,kq_end,vo.getString("a0100"),vo.getString("nbase"),vo.getString("b0110"),kqItem_hash,holiday_rules);
		    	if(cancel_leave_tiem<=0) {
                    throw GeneralExceptionHandler.Handle(new GeneralException("",vo.getString("a0101")+"可休有效时间为0天","",""));
                }
		    	
		    	String start=DateUtils.format(kq_start,"yyyy.MM.dd HH:mm:ss");
		    	String end=DateUtils.format(kq_end,"yyyy.MM.dd HH:mm:ss");			    	
		    	RecordVo vo_19=new RecordVo("q15");
				if("5".equals(sp)&&vo.getString("q15z0")!=null&& "01".equals(vo.getString("q15z0"))&&vo.getString("q15z5")!=null&& "03".equals(vo.getString("q15z5")))
				{
					
				    String q1519=vo.getString("q1519");					    
				    vo_19.setString("q1501", q1519);
				    ContentDAO dao=new ContentDAO(this.conn);
				    vo_19=dao.findByPrimaryKey(vo_19);
				    if(vo_19!=null)
				    {
				    	 
				    	String history=vo_19.getString("history");
				        Date src_z1=vo_19.getDate("q15z1");
				    	Date src_z3=vo_19.getDate("q15z3");
				    	String stD=DateUtils.format(src_z1, "yyyy.MM.dd");
						String edD=DateUtils.format(src_z3, "yyyy.MM.dd");
						String stT=DateUtils.format(src_z1, "yyyy.MM.dd HH:mm:ss");
						String edT=DateUtils.format(src_z3, "yyyy.MM.dd HH:mm:ss");
						float history_CancelF[]=new float[4];
						if(holiday_rules!=null&&holiday_rules.length==7)
			           	{
							if(edD.equalsIgnoreCase(DateUtils.format(kq_end,"yyyy.MM.dd")))//先看销假的结束天和请假的结束天是否一致
							{
								if(stT.equalsIgnoreCase(start)&&!edT.equalsIgnoreCase(end))
								{
									float leave_tiem=annualApply.getHistoryLeaveTime(kq_end,src_z3,vo.getString("a0100"),vo.getString("nbase"),vo.getString("b0110"),kqItem_hash,holiday_rules);
									String history_hols_ed=annualApply.getLeaveManage(vo.getString("a0100"),vo.getString("nbase"),sels,end,DateUtils.format(src_z3, "yyyy.MM.dd HH:mm"),leave_tiem,"1",vo.getString("b0110"),kqItem_hash,holiday_rules);
									if(history!=null&&history_hols_ed!=null&&history_hols_ed.equalsIgnoreCase(history))//判断销假后的扣除的实际天和销假前扣除的实际天数一致
									{
										//不操作
									}else
									{
										history_CancelF=annualApply.getCancelHolsTimeManage(vo.getString("a0100"),vo.getString("nbase"),sels,start,end,cancel_leave_tiem,vo.getString("b0110"),kqItem_hash,stD,edD,holiday_rules);
									}
								}else
								{
									float leave_tiem=annualApply.getHistoryLeaveTime(src_z1,kq_start,vo.getString("a0100"),vo.getString("nbase"),vo.getString("b0110"),kqItem_hash,holiday_rules);
									String history_hols_ed=annualApply.getLeaveManage(vo.getString("a0100"),vo.getString("nbase"),sels,DateUtils.format(src_z1, "yyyy.MM.dd HH:mm"),start,leave_tiem,"1",vo.getString("b0110"),kqItem_hash,holiday_rules);
									if(history!=null&&history_hols_ed!=null&&history_hols_ed.equalsIgnoreCase(history))//判断销假后的扣除的实际天和销假前扣除的实际天数一致
									{
										//不操作
									}else
									{
										history_CancelF=annualApply.getCancelHolsTimeManage(vo.getString("a0100"),vo.getString("nbase"),sels,start,end,cancel_leave_tiem,vo.getString("b0110"),kqItem_hash,stD,edD,holiday_rules);
									}
								}
								
							}else if(stT.equalsIgnoreCase(start)&&!edT.equalsIgnoreCase(end))
							{
								float leave_tiem=annualApply.getHistoryLeaveTime(kq_end,src_z3,vo.getString("a0100"),vo.getString("nbase"),vo.getString("b0110"),kqItem_hash,holiday_rules);
								String history_hols_ed=annualApply.getLeaveManage(vo.getString("a0100"),vo.getString("nbase"),sels,end,DateUtils.format(src_z3, "yyyy.MM.dd HH:mm"),leave_tiem,"1",vo.getString("b0110"),kqItem_hash,holiday_rules);
								if(history!=null&&history_hols_ed!=null&&history_hols_ed.equalsIgnoreCase(history))//判断销假后的扣除的实际天和销假前扣除的实际天数一致
								{
									//不操作
								}else
								{
									history_CancelF=annualApply.getCancelHolsTimeManage(vo.getString("a0100"),vo.getString("nbase"),sels,start,end,cancel_leave_tiem,vo.getString("b0110"),kqItem_hash,stD,edD,holiday_rules);
								}
							}else
							{
								history_CancelF=annualApply.getCancelHolsTimeManage(vo.getString("a0100"),vo.getString("nbase"),sels,start,end,cancel_leave_tiem,vo.getString("b0110"),kqItem_hash,stD,edD,holiday_rules);
							}
			           	}else {
                            history_CancelF=annualApply.getCancelHolsTimeManage(vo.getString("a0100"),vo.getString("nbase"),sels,start,end,cancel_leave_tiem,vo.getString("b0110"),kqItem_hash,stD,edD,holiday_rules);
                        }
				    	
				    	String history_CancelS=history_CancelF[0]+","+history_CancelF[1]+";"+history_CancelF[2]+","+history_CancelF[3];
				    	Date start_d=vo_19.getDate("q15z1");
				    	Date end_d=vo_19.getDate("q15z3");
				    	String array_TOP[]=null;
					    String array_LAST[]=null;
					    String historyS[]=history.split(";");
					    if(historyS!=null&&historyS.length>0)
					    {
					    	 array_TOP=historyS[0].split(",");
					    	 if(historyS.length==2) {
                                 array_LAST=historyS[1].split(",");
                             }
					    }	
					    float vf_top=0;
					    float bvf_top=0;
					    if(array_TOP!=null&&array_TOP.length==2)
					    {
					    	  String value=array_TOP[0];//扣除的可休假
					    	  String balance_value=array_TOP[1];//扣除的上年结余假
					    	  vf_top=Float.parseFloat(value);
					    	  bvf_top=Float.parseFloat(balance_value);
					    	  if(vf_top>0&&history_CancelF[0]>0) {
                                  vf_top=vf_top-history_CancelF[0];
                              }
					    	  if(bvf_top>0&&history_CancelF[1]>0) {
                                  bvf_top=bvf_top-history_CancelF[1];
                              }
					    }
					    float vf_last=0;
					    float bvf_last=0;
					    if(array_LAST!=null&&array_LAST.length==2)
					    {
					    	  String value=array_LAST[0];//扣除的可休假
					    	  String balance_value=array_LAST[1];//扣除的上年结余假
					    	  vf_last=Float.parseFloat(value);
					    	  bvf_top=Float.parseFloat(balance_value);
					    	  if(vf_last>0&&history_CancelF[0]>0) {
                                  vf_last=vf_last-history_CancelF[0];
                              }
					    	  if(bvf_last>0&&history_CancelF[1]>0) {
                                  bvf_last=bvf_last-history_CancelF[1];
                              }
					    }
					    // 人事异动模板处理改为  先校验后编辑数据库
					    if(!"0".equals(checkFlag)) {
                            dao.updateValueObject(vo_19);
                        }
				    }
				}
				// 若是销调休假则先校验是否符合未调休限额，否则不进行销假操作  && // 人事异动模板处理改为  先校验后编辑数据库
				if(leavetime_type_used_overtime != null && !"0".equals(checkFlag)){
					if(leavetime_type_used_overtime.equalsIgnoreCase(sels))//销调休假 返还销掉的时长
                    {
                        upq33.returnLeaveTime(vo);
                    }
				}
				saveCancel(vo, isCorrect, approve, tableflag);
				//审批后 	&& // 人事异动模板处理改为  先校验后编辑数据库
				if("5".equals(sp) && !"0".equals(checkFlag))
				{
					if(vo.getString("q15z0")!=null&& "01".equals(vo.getString("q15z0"))&&vo.getString("q15z5")!=null&& "03".equals(vo.getString("q15z5")))
					{
						Date sp_D=vo_19.getDate("q15z7");
						
						//审批时间往前提一秒, 使回退操作可以包含本条记录
	                    Calendar calendar = Calendar.getInstance(); 
	                    calendar.setTime(sp_D); 
	                    calendar.add(Calendar.SECOND, -1); 
	                    sp_D = calendar.getTime(); 
	                    
						String sp_time=DateUtils.format(sp_D,"yyyy-MM-dd HH:mm:ss");
						annualApply.bachReStatHols(sp_time,vo.getString("a0100"),vo.getString("b0110"),vo.getString("nbase"),sels,kqItem_hash);
					}
				}					
		    }else
		    {
		    	// 若是销调休假则先校验是否符合未调休限额，否则不进行销假操作
		    	if(leavetime_type_used_overtime != null){
		    		if(leavetime_type_used_overtime.equalsIgnoreCase(sels)) {
		    			KqOverTimeForLeaveBo overTimeForLeaveBo = new KqOverTimeForLeaveBo(conn, userView);
		    			// 36959 销假如果是调休假则需要校验所销的调休时长 是否符合规则（即所销时长+调休加班时长<=参数设置时长）
		    	        String msgs = overTimeForLeaveBo.checkQXJOvertimeForLeaveAllHour(vo);
		    	        if(StringUtils.isNotEmpty(msgs)) {
                            throw GeneralExceptionHandler.Handle(new GeneralException("",msgs,"",""));
                        }
		    			// 人事异动模板处理改为  先校验后编辑数据库
		    			if(!"0".equals(checkFlag) && "app".equalsIgnoreCase(approve)) {
		    				// 销调休假 返还销掉的时长
		    				upq33.returnLeaveTime(vo);
		    			}
		    		}
		    	}
		    	saveCancel(vo, isCorrect, approve, tableflag);
		    }
		}catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 获取申请单的开始日期、结束日期、审批日期
	 * @param vo
	 * @param tableflag
	 * @return
	 */
	public HashMap getSpDates(RecordVo vo,String tableflag) {
		HashMap map = new HashMap();
		try {
			Date kq_start = null;	
			Date kq_end = null;
			Date table05d = null;	
			Object table05=vo.getObject(tableflag+"05");
			if(table05 instanceof String) {
                table05d =DateUtils.getDate(String.valueOf(table05),"yyyy-MM-dd");
            } else {
                table05d = (Date)table05;
            }
			 
			Object start=vo.getObject(tableflag+"z1");
			if(start instanceof String) {
                kq_start =DateUtils.getDate(String.valueOf(start),"yyyy-MM-dd HH:mm");
            } else {
                kq_start = (Date)start;
            }
			
			Object end=vo.getObject(tableflag+"z3");
			if(end instanceof String) {
                kq_end =DateUtils.getDate(String.valueOf(end),"yyyy-MM-dd HH:mm");
            } else {
                kq_end = (Date)end;
            }
			 
			map.put("kq_start", kq_start);
			map.put("kq_end", kq_end);
			map.put("table05d", table05d);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return map;
	}
	
	/**
	 * 校验所销的调休加班单据是否已用
	 * @param vo
	 * @return
	 * @throws GeneralException
	 */
	public String checkCancelOverTimeApp(RecordVo vo)throws GeneralException
	{
		if(null == vo) {
            return "";
        }
		String msg = "";
        RowSet rs = null;
		try{
			// 是否调休
	        String overForOff = KqUtilsClass.getFieldByDesc("Q11", "是否调休");
	        // 没有设置调休指标直接返回
	        if(StringUtils.isEmpty(overForOff)) {
                return "";
            }
	        // 获取所销原来单据数据
	        RecordVo vo19 = new RecordVo("q11");
		    String q1119 = vo.getString("q1119");
		    vo19.setString("q1101", q1119);
		    ContentDAO dao = new ContentDAO(this.conn);
		    vo19 = dao.findByPrimaryKey(vo19);
		    if(vo19 == null) {
                return "";
            }
		    
	        // 该单据没有调休字段直接返回
	        if(vo19.getString(overForOff) == null) {
                return "";
            }
	        
	        // 检查是否为调休加班 
	        LazyDynaBean overtimeBean = new LazyDynaBean();
	        overtimeBean.set("q1103", vo19.getString("q1103"));
	        overtimeBean.set(overForOff, vo19.getString(overForOff));
	        KqOverTimeForLeaveBo overTimeForLeaveBo = new KqOverTimeForLeaveBo(conn, userView);
//	        String msgs = overTimeForLeaveBo.checkOvertimeForLeaveMaxHour(vo19);
	        boolean isOvertimeForLeave = overTimeForLeaveBo.isOvertimeForLeave(overtimeBean);
	        // 不是调休加班
	        if(!isOvertimeForLeave) {
                return "";
            }
	        // 要按销假单的日期来判断，不是按原始请假单
	        HashMap map = getSpDates(vo, "q11");
	        Date kq_start = (Date)map.get("kq_start");	
	        Date kq_end = (Date)map.get("kq_end");
	        vo.setDate("q11z1", kq_start);
	        vo.setDate("q11z3", kq_end);
	        // 44239 是调休加班后，需再次校验该时间段内调休假的待批申请单是否够扣减
	        msg = overTimeForLeaveBo.checkQXJOvertimeForPendingLeaveHour(vo);
	        if(StringUtils.isNotEmpty(msg)) {
                return msg;
            }
	        
			String q11z1 = DateUtils.FormatDate(kq_start, "yyyy.MM.dd");
			String q11z3 = DateUtils.FormatDate(kq_end, "yyyy.MM.dd");
	        
			StringBuffer sql = new StringBuffer("");
			sql.append("select sum(Q3307) q3307  from Q33 ");
			sql.append(" where A0100=? and nbase=? and Q3303>=? and Q3303<=? ");
			
			ArrayList sqlParams = new ArrayList();
			sqlParams.add(vo.getString("a0100"));
			sqlParams.add(vo.getString("nbase"));
			sqlParams.add(q11z1);
			sqlParams.add(q11z3);
			
			rs = dao.search(sql.toString(), sqlParams);
	        while(rs.next()){
	        	int q3307 = rs.getInt("q3307");
	        	if(q3307 > 0) {
                    msg = "该申请单加班时长已被调休假使用，不允许撤销！";
                }
	        }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
		return msg;
	}
	public String getCheckFlag() {
		return checkFlag;
	}
	public void setCheckFlag(String checkFlag) {
		this.checkFlag = checkFlag;
	}
}
