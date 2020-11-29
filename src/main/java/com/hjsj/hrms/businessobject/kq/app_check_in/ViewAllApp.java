package com.hjsj.hrms.businessobject.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>Title:ViewAllApp.java</p>
 * <p>Description>:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Nov 14, 2012 2:23:09 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author:wangmj</p>
 */
public class ViewAllApp {
	private Connection conn;
	public ViewAllApp() {
		
	}
	public ViewAllApp(Connection conn) {
		this.conn = conn;
	}
	
    /**
     * 获取某一天的班次id
     * @param a0100
     * @param nbase
     * @param date
     * @return
     */
	public String getClassid(String a0100,String nbase,Date date){
		ContentDAO cd= new ContentDAO(this.conn);
		StringBuffer sb = new StringBuffer();
		String class_id=null;
		String datestr = OperateDate.dateToStr(date, "yyyy-MM-dd HH:mm");
		sb.append("select class_id from kq_employ_shift where A0100 = '"+a0100+"' and nbase = '"+nbase+"' and Q03Z0 = '"+ 
		        datestr.substring(0, 10).replace('-','.' ) +"'");
		RowSet rs=null;
		try {
		rs = cd.search(sb.toString());
			while(rs.next()){
				int id = rs.getInt(1);
				class_id = Integer.toString(id);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return class_id;
	}
	
	/**
	 * 获取班次时间段
	 * @param class_id
	 * @return
	 */
	public HashMap getArrange(String class_id){
	    StringBuffer sql = new StringBuffer();
        sql.append("select * from kq_class where class_id='" + class_id + "'");
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList onduty = new ArrayList();
        ArrayList offduty = new ArrayList();
        HashMap arrange = new HashMap();
        RowSet re = null;
        try
        {
            re = dao.search(sql.toString());
            if (re.next())
            {
                for (int i = 1; i <= 3; i++)
                {
                    
                    if (null != re.getString("offduty_" + i) && re.getString("offduty_" + i).length() > 1 
                           && null != re.getString("onduty_" + i) && re.getString("onduty_" + i).length() > 1)
                    {
                  	    onduty.add(re.getString("onduty_" + i));
                  	    offduty.add(re.getString("offduty_" + i));
                    }else if(("".equals(re.getString("offduty_" + i)) || null == re.getString("offduty_" + i)) //onduty_1 ~ offduty_2
                           && null != re.getString("onduty_" + i) && re.getString("onduty_" + i).length() > 1)
                    {
                	    for (int j = 3; j >=1 ; j--) {
                		    if (null != re.getString("offduty_" + j) && re.getString("offduty_" + j).length() > 1 ) {
                			    onduty.add(re.getString("onduty_" + i));
                			    offduty.add(re.getString("offduty_" + j));
                		    }
                	    }
                    }
                }
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            if (re != null) {
                try
                {
                    re.close();
                } catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }
        arrange.put("onduty", onduty);
        arrange.put("offduty", offduty);
        return arrange;
	}
	
	/**
	 * 得到某一天的有效班次时间段
	 * @param a0100
	 * @param nbase
	 * @param start
	 * @return
	 */
	public HashMap getEffectiveArrange(String a0100,String nbase,Date start,String classId){
	    ArrayList onduty = new ArrayList();
	    ArrayList offduty = new ArrayList();
	    HashMap arrange = new HashMap();
	    String class_id_before = "";
	    String class_id_today = "";
	    //boolean isValid = true;
	    if (start != null) 
		{
	    	/*class_id_before = getClassid(a0100, nbase, OperateDate.addDay(start, -1));
	    	class_id_today = getClassid(a0100,nbase,start);
	    	try {
	    		ArrayList datelist = RegisterDate.getKqDurationList(conn);
	    		String beforeDay = OperateDate.dateToStr(OperateDate.addDay(start, -1), "yyyy.MM.dd");
	    		if(!datelist.contains(beforeDay))//考勤期间内
	    			isValid = false;
	    	} catch (GeneralException e) {
	    		e.printStackTrace();
	    	}*/
		}else 
		{
			class_id_before = classId;
			class_id_today = classId;
		}
	    
        if(class_id_before != null && !"".equals(class_id_before) ){
            HashMap arrange_before = getArrange(class_id_before);
            ArrayList onduty_before = (ArrayList) arrange_before.get("onduty");
            ArrayList offduty_before = (ArrayList) arrange_before.get("offduty");
            boolean isOver = false;
            for (int j = 0; j < onduty_before.size(); j++)
            {
                if(GetValiateEndDate.isBigToTime((String)onduty_before.get(j), (String)offduty_before.get(j))){//申请前一天班组时间段出现跨天
                    onduty.add("00:00");
                    offduty.add(offduty_before.get(j));
                    isOver = true;
                    continue;
                }
                if (isOver)
                {
                    onduty.add(onduty_before.get(j));
                    offduty.add(offduty_before.get(j));
                }
            }
        }
      
        if(class_id_today != null && !"".equals(class_id_today) ){
          
            HashMap arrange_today = getArrange(class_id_today);
            ArrayList onduty_today = (ArrayList) arrange_today.get("onduty");
            ArrayList offduty_today = (ArrayList) arrange_today.get("offduty");
            boolean isOver = false;
            for(int k=0;k<onduty_today.size();k++)
            {
                if (GetValiateEndDate.isBigToTime((String)onduty_today.get(k), (String)offduty_today.get(k))){//班组时间段段出现跨天
                    onduty.add(onduty_today.get(k));
                    offduty.add("24:00");
                    isOver = true;
                }
                if (!isOver)
                {
                    onduty.add(onduty_today.get(k));
                    offduty.add(offduty_today.get(k));
                }
            }
        }
        arrange.put("onduty", onduty);
        arrange.put("offduty", offduty);
        return arrange;
	}

	
	public String getAppTimeLenDesc(RecordVo appVo, String appTable, UserView userView) {
	    String timeLenDesc = "";
	    
	    try {
    	    String a0100 = appVo.getString("a0100");
    	    String nbase = appVo.getString("nbase");
    	    String b0110 = PubFunc.DotstrNull(appVo.getString("b0110"));
    	    String appType = appVo.getString(appTable + "03");
    	    String start_d = DateUtils.format(appVo.getDate(appTable + "z1"), "yyyy-MM-dd HH:mm");
    	    String end_d = DateUtils.format(appVo.getDate(appTable + "z3"), "yyyy-MM-dd HH:mm");
    	    
    	    if ("q11".equalsIgnoreCase(appTable)) 
            {
    	    	AnnualApply annualApply = new AnnualApply(userView, this.conn);
    	        //参考班次
    	        String q1104 = PubFunc.DotstrNull(appVo.getString("q1104"));
    	        
    	        //有参考班次的加班
    	        if (!"".equals(q1104) && !"0".equals(q1104)) {
                    float classTimelen = annualApply.getOvertimeLen(appType, q1104, nbase, a0100, appVo.getDate(appTable + "z1"), appVo.getDate(appTable + "z3"));
                    timeLenDesc = classTimelen + KqConstant.Unit.HOUR_DESC;
    	        } else {
    	        	SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
                    Date startTime = sdf.parse(start_d);//申请开始日期
                    Date endTime = sdf.parse(end_d);//申请结束日期
                    
                    timeLenDesc = annualApply.getAppTimeLenDesc(nbase, a0100, b0110, startTime, endTime, appType);
    	        }
            } else 
            {
                SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
                Date startTime = sdf.parse(start_d);//申请开始日期
                Date endTime = sdf.parse(end_d);//申请结束日期
                
                AnnualApply annualApply = new AnnualApply(userView, this.conn);
                timeLenDesc = annualApply.getAppTimeLenDesc(nbase, a0100, b0110, startTime, endTime, appType);
            }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    return timeLenDesc;
	}
}
