package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.app_check_in.GetValiateEndDate;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.register.pigeonhole.UpdateQ33;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * 批量审批审核考勤申请
 * <p>Title:BatchSubscribebTrans.java</p>
 * <p>Description>:BatchSubscribebTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 20, 2010 7:34:13 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class BatchSubscribebTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		ArrayList selectedinfolist=(ArrayList)this.getFormHM().get("selectedinfolist");
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String table=(String)hm.get("table");
		String ta=table.toLowerCase();
		String result=(String)this.getFormHM().get("sp_result");
		if ("Q19".equalsIgnoreCase(ta) || "Q25".equalsIgnoreCase(ta)) {
			result = (String)hm.get("sp_result");
			result = SafeCode.decode(result);
		}
		String radio=(String)hm.get("radio");
		String flag=(String)hm.get("flag");
		
        try
	     {
        	 ContentDAO dao=new ContentDAO(this.getFrameconn()); 
        	 StringBuffer buf=new StringBuffer();
        	 buf.append("update ");
        	 buf.append(table+" set state='1'");
        	 buf.append(" where ");
        	 buf.append( ta+"01=?");
        	 ArrayList paralist=new ArrayList();
        	 for(int i=0;i<selectedinfolist.size();i++)
             {
        		    LazyDynaBean rec=(LazyDynaBean)selectedinfolist.get(i); 
        		    ArrayList list=new ArrayList();
        		    list.add(rec.get(ta+"01").toString());
        		    paralist.add(list);         		   
             }
           dao.batchUpdate(buf.toString(),paralist);
           
           String para = KqParam.getInstance().getDURATION_OVERTIME_MAX_LIMIT();
           if (para != null && para.length() > 0) 
           {
        	   String isPermit = ExmineKqOverTimeLimit(flag, dao);
        	   if (isPermit.length() > 0) 
			   {
        		   throw new GeneralException(isPermit);
			   }
           }
           
           StringBuffer up=new StringBuffer();          
           ArrayList dblist=this.userView.getPrivDbList();
           String result_where="";          
           if(flag!=null&& "02".equals(flag) )
           {
        	   result_where=","+table+"09='"+this.userView.getUserFullName()+"',"+table+"11='"+result+"'";
           }else if(flag!=null&& "03".equals(flag))
           {
        	   result_where=","+table+"13='"+this.userView.getUserFullName()+"',"+table+"15='"+result+"'";
           }           
           SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
  		   String strDate = sdf.format(new java.util.Date());
           String w_z7=table+"z7="+Sql_switcher.dateValue(strDate);
           String sql="";
           if(flag!=null&& "02".equalsIgnoreCase(flag))
           {
        	   up.append("update "+table+" set "+table+"z5='02',"+table+"z0='"+radio+"'"+result_where);
        	   if (!("q19".equalsIgnoreCase(ta) || "q25".equalsIgnoreCase(ta))) {
        		   up.append(","+w_z7);
        	   }
        	   up.append(" where state='1' and "+table+"z5='08'");        	   
        	   String where="";
        	   for(int i=0;i<dblist.size();i++)
               {
            		String nbase=dblist.get(i).toString();            		
            		String whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase);
            		where=" and nbase='"+nbase+"' and a0100 in(select a0100 "+whereIN+")";   
            		dao.update(up.toString()+where);
               }
        	   
           }else if(flag!=null&& "03".equalsIgnoreCase(flag))
           {
        	   String up_select =("update "+table+" set "+table+"z5='03',"+table+"z0='"+radio+"'"+result_where);
        	   if (!("q19".equalsIgnoreCase(ta) || "q25".equalsIgnoreCase(ta))) {
        		   up_select += "," + w_z7;
        	   }
        	   String up_where =(" where state='1' and "+table+"z5='02'");
        	   up.append(up_select+up_where);
        	   String where=""; 	  
        	   for(int i=0;i<dblist.size();i++)
               {
        		    
            		String nbase=dblist.get(i).toString();            		
            		String whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase);
            		//condition.append(" and  EXISTS(select a0100 "+whereIN+" and q05.a0100="+userbase+".a0100)");
            		if(whereIN!=null&&(whereIN.indexOf("WHERE")!=-1||whereIN.indexOf("where")!=-1))
    					where=" and nbase='"+nbase+"' and EXISTS(select a0100 "+whereIN+" and "+table+".a0100="+nbase+"A01.a0100)";
    				else
    					where=" and nbase='"+nbase+"' and EXISTS(select a0100 "+whereIN+" where "+table+".a0100="+nbase+"A01.a0100)";
            		sql="select * from "+table+" where state='1' and "+table+"z5='02'"+where;
            		
            		if("q15".equalsIgnoreCase(table)&&radio!=null&& "01".equals(radio))
            			holidayOperation(sql,up_select,up_where);
            		else if(("Q19".equalsIgnoreCase(table) || "Q25".equalsIgnoreCase(table))
            				&& radio != null && "01".equals(radio)){
            			empClassOperation(sql,up_select,up_where,ta,flag);//调班调休批量审批
            			dao.update(up.toString()+where);
            		}
            		else
            			dao.update(up.toString()+where);
               }
           }	
        }
		 catch(Exception ex)
		 {
		    ex.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ex);
		 }
		 this.getFormHM().put("state_flag", "0");

	}
	/**
	 * 请假表中对假期管理表的处理
	 * @param sql
	 * @param updateSQL
	 * @return
	 * @throws GeneralException 
	 */
    private boolean holidayOperation(String sql,String select,String where) throws GeneralException
    {
    	boolean isCorrect=true;
    	RowSet rs=null;
    	try
    	{
    		String sels="";
    		Date kq_start;
    		Date kq_end;
    		String start="";
    		String end="";
    		ContentDAO dao=new ContentDAO(this.getFrameconn());
    		AnnualApply annualApply=new AnnualApply(this.userView,this.getFrameconn()); 
    		float[] holiday_rules=annualApply.getHoliday_minus_rule();//年假假期规则
    		rs=dao.search(sql);
    		String id="";    
    		String updateSQL="";
    		String history="";
    		String sqlstr="";
    		while(rs.next())
    		{
    			sqlstr=select;
    			sels=rs.getString("q1503");
    			id=rs.getString("q1501");
    			if(KqParam.getInstance().isHoliday(this.frameconn, rs.getString("b0110"), sels))
			    {
    				HashMap kqItem_hash=annualApply.count_Leave(sels);
				    kq_start=rs.getTimestamp("q15z1");
				    kq_end=rs.getTimestamp("q15z3");
				    start=DateUtils.format(kq_start,"yyyy.MM.dd HH:mm:ss");
			    	end=DateUtils.format(kq_end,"yyyy.MM.dd HH:mm:ss");
				    float leave_tiem=annualApply.getHistoryLeaveTime(kq_start,kq_end,rs.getString("a0100"),rs.getString("nbase"),rs.getString("b0110"),kqItem_hash,holiday_rules);
				    history=annualApply.upLeaveManage(rs.getString("a0100"),rs.getString("nbase"),sels,start,end,leave_tiem,"1",rs.getString("b0110"),kqItem_hash,holiday_rules);
				    sqlstr=sqlstr+",history='"+history+"'";
			    }
    			 /** 如果请调休假 检查调休假可用时长是否够用*/
                int hr_count = 0;
                GetValiateEndDate ve = new GetValiateEndDate(this.userView, this.getFrameconn());
                String leavetime_type_used_overtime = KqParam.getInstance().getLEAVETIME_TYPE_USED_OVERTIME();
                Map infoMap = ve.getInfoMap(rs.getString("nbase"), rs.getString("a0100"));
                String error ="";
                //考勤规则应取改假类自己的规则
                HashMap kqItemHash = annualApply.count_Leave(sels);
                kqItemHash.put("item_unit", KqConstant.Unit.HOUR);
                //假期时长扣减规则参数
                float[] holidayRules = null; //annualApply.getHoliday_minus_rule();
                if (KqParam.getInstance().isHoliday(frameconn, rs.getString("b0110"), sels))
                    holidayRules = annualApply.getHoliday_minus_rule();
                
                Date startTime = rs.getTimestamp("q15z1");
                Date endTime = rs.getTimestamp("q15z3");
                float timeLen = annualApply.calcLeaveAppTimeLen(rs.getString("nbase"), rs.getString("a0100"), "", startTime , endTime, kqItemHash, holidayRules, Integer.MAX_VALUE);
                 
                hr_count = (hr_count + (int)(timeLen * 60));

                if(sels.equalsIgnoreCase(leavetime_type_used_overtime))
                    error = ve.checkUsableTime(rs.getTimestamp("q15z1"),infoMap,rs.getString("q1503"),rs.getString("nbase"),"",String.valueOf(hr_count));
                if(error.length()>0)
                    throw GeneralExceptionHandler.Handle(new GeneralException(error));
                /** 审批请假单时 如果是调休假 更新调休明细表Q33*/
                if(sels.equalsIgnoreCase(leavetime_type_used_overtime)){
                    int timeCount = hr_count;
                    if(timeCount > 0) {
                    	UpdateQ33 updateq33 = new UpdateQ33(this.userView,this.getFrameconn());
                    	// 48612
                    	updateq33.setStartDate(startTime);
                    	updateq33.upQ33(rs.getString("nbase"),rs.getString("a0100"),timeCount);
                    }
                }
    			updateSQL=sqlstr+" "+where+" and q1501='"+id+"'";
    			dao.update(updateSQL);
    		}
    	}catch(Exception e)
    	{
    		isCorrect=false;
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}finally{
        	if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
        }
    	return isCorrect;
    }
    
    /**
     * 调班调休申请申请状态的处理
     * @param sql
     * @param select
     * @param where
     */
    private void empClassOperation(String sql,String select,String where,String ta,String flag){
    	ContentDAO dao = new ContentDAO(frameconn);
    	try {
			this.frowset = dao.search(sql.toString());
			
			while(this.frowset.next()){
				
				RecordVo recordVo = new RecordVo(ta.toUpperCase());
				recordVo.setString(ta+"01", this.frowset.getString(ta + "01"));
				recordVo = dao.findByPrimaryKey(recordVo);
				
				recordVo.setString(ta + "z5", "03");
				recordVo.setString(ta + "z0", "01");
				
				if ("03".equals(flag)) {
					up_kq_employ_shift(ta,recordVo);//批准申请则处理人员班次
				}
				
				//dao.updateValueObject(recordVo);
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (GeneralException e) {
			e.printStackTrace();
		}
    	
    }
    
    /**
     * 调班调休申请人员班次的处理
     * @param table
     * @param ex_co
     */
    private void up_kq_employ_shift(String table, RecordVo ex_co){
		ArrayList up_list=new ArrayList();		
		StringBuffer sql=new StringBuffer();
		sql.append("update kq_employ_shift set");
		sql.append(" class_id=? ");
		sql.append(" where a0100=? and nbase=? and q03z0=?");
		ArrayList cur_list=new ArrayList();
		ArrayList ex_list=new ArrayList();
    	if ("q19".equalsIgnoreCase(table)) {
    		String q19z7=ex_co.getString(table+"z7");
    		String q19z9=ex_co.getString(table+"z9");
    		
    		cur_list.add(q19z9);
    		cur_list.add(ex_co.getString("a0100").toString());
    		cur_list.add(ex_co.getString("nbase").toString());
    		cur_list.add(ex_co.getString(table+"z1").toString());
    		up_list.add(cur_list);
    		
    		ex_list.add(q19z7);
    		ex_list.add(ex_co.getString("q19a0").toString());
    		ex_list.add(ex_co.getString("nbase").toString());
    		ex_list.add(ex_co.getString(table+"z3").toString());
    		up_list.add(ex_list);    	     
		} else {
	        String q25z7=ex_co.getString(table+"z7");
	        
	        cur_list.add("0");    	      
	        cur_list.add(ex_co.getString("a0100").toString());
	        cur_list.add(ex_co.getString("nbase").toString());
	        cur_list.add(ex_co.getString(table+"z1").toString());
	        up_list.add(cur_list);    	
	        
	        ex_list.add(q25z7);
	        ex_list.add(ex_co.getString("a0100").toString());
	        ex_list.add(ex_co.getString("nbase").toString());
	        ex_list.add(ex_co.getString(table+"z3").toString());
	        up_list.add(ex_list);
		}
    	 
		ContentDAO dao=new ContentDAO(this.getFrameconn());
	    try
		{
	    	dao.batchUpdate(sql.toString(),up_list);
		}catch(Exception e)
		{
			e.printStackTrace();
			try {
				throw GeneralExceptionHandler.Handle(e);
			} catch (GeneralException e1) {
				e1.printStackTrace();
			}
		}
    }
    
    private String ExmineKqOverTimeLimit(String flag, ContentDAO dao){
    	String returnStr = "";
    	StringBuffer sql = new StringBuffer();
    	sql.append("select * from q11 where state = '1'" );
    	if ("02".equals(flag)) 
		{
			sql.append(" and q11z5 = '08'");//查找报审的
		}else if ("03".equals(flag)) 
		{
			sql.append(" and q11z5 = '02'");//查找报批的
		}
    	sql.append(" order by a0101,q11z1");
    	
    	ArrayList infoList = new ArrayList();
    	RecordVo recordVo;
    	
    	try {
			frowset = dao.search(sql.toString());
			while(frowset.next()){
				String q1101 = frowset.getString("q1101");
				recordVo = new RecordVo("Q11");
				recordVo.setString("q1101", q1101);
				recordVo = dao.findByPrimaryKey(recordVo);
				
				infoList.add(recordVo);
			}
			
			//开始检查
			AnnualApply annualApply = new AnnualApply(userView, frameconn);
			float overtimeLen = 0;
			float apptimeLen = 0;
			int num = 0;
			String para = KqParam.getInstance().getDURATION_OVERTIME_MAX_LIMIT();
	        if (para == null || para.length() <= 0)
	            para = "-1";
	        int overtimeLimit = Float.valueOf(para).intValue();//加班时长限额
	        String iftoRestField = KqUtilsClass.getFieldByDesc("Q11", ResourceFactory.getProperty("kq.self.app.workingdaysoff.yesorno"));
			String currA0100 = "";
			String nextA0100 = "";
			
			for (int j = 0; j < infoList.size(); j++) 
			{
				recordVo = (RecordVo) infoList.get(j);
				currA0100 = recordVo.getString("a0100");// 人员编号
				if(j != infoList.size() -1)
					nextA0100 = ((RecordVo)infoList.get(j + 1)).getString("a0100");
				
				if (overtimeLimit > 0) //审核或者批准，检查考勤期间内的加班时长是否超过加班时间时长限额的限制
				{
					String IftoRest="";
					if (iftoRestField != null && iftoRestField.length() > 0) 
					{
						IftoRest = recordVo.getString(iftoRestField);
					}
					if(!"1".equals(IftoRest))//调休的加班不计算
							apptimeLen = apptimeLen + annualApply.getOneOverTimelen(recordVo);
					if (overtimeLen == 0 && (!nextA0100.equals(currA0100) || j == infoList.size() -1)) 
					{
						overtimeLen = annualApply.getKqdurationOverTimelen(recordVo.getString("nbase"), recordVo.getString("a0100"), "3");
					}
					
					if (overtimeLen + apptimeLen > overtimeLimit && (!nextA0100.equals(currA0100) || j == infoList.size() -1)) {
						if(returnStr.length() == 0)
							returnStr += "无法审批所选申请单，请重新选择！<br>下列人员超出本期间允许加班" + PubFunc.round(""+overtimeLimit,2) + "小时的限制：<br>";
						if (num == 9 && returnStr.length() > 0) {
							returnStr += "。。。。。。";
							break;
						} else
							returnStr += "已选" + recordVo.getString("a0101") + "的申请时长为" + PubFunc.round(""+apptimeLen,2) + "小时，" + "累计已有加班" + PubFunc.round(""+overtimeLen,2) + "小时；<br>";
						num ++;
					}
					
					if (!nextA0100.equals(currA0100)) 
					{
						overtimeLen = 0;
						apptimeLen = 0;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		
		return returnStr;
    }
}
