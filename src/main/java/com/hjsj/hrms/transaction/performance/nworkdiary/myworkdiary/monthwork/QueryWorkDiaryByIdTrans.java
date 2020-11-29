package com.hjsj.hrms.transaction.performance.nworkdiary.myworkdiary.monthwork;

import com.hjsj.hrms.businessobject.performance.nworkdiary.myworkdiary.WorkDiaryBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * SearchAfterDateTrans.java
 * Description: 查找变更后的日期国网月报
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Jan 9, 2013 11:06:34 AM Jianghe created
 */
public class QueryWorkDiaryByIdTrans extends IBusiness{
	public void execute() throws GeneralException 
	{
		try{
			String isOwner = (String)this.getFormHM().get("isOwner");
			String p0100 = (String)this.getFormHM().get("p0100");
			String record_num = (String)this.getFormHM().get("record_num");
			WorkDiaryBo bo = new WorkDiaryBo(this.getFrameconn(),this.userView,this.userView.getDbname(),this.userView.getA0100());
			LazyDynaBean bean = bo.queryById(p0100, record_num);
			this.getFormHM().put("p0100",(String) bean.get("p0100"));
			this.getFormHM().put("record_num", (String)bean.get("record_num"));
			this.getFormHM().put("content", SafeCode.encode((String)bean.get("content")));
			this.getFormHM().put("title", (String)bean.get("title"));
			this.getFormHM().put("type", (String)bean.get("type"));
			Date start_time = (Date)bean.get("start_time");
			Date end_time = (Date)bean.get("end_time");
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
			SimpleDateFormat sdf1=new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat sdf3=new SimpleDateFormat("yyyy年MM月dd日");
			String timeDetail="";
            if("0".equals((String)bean.get("type"))){
            	timeDetail = sdf3.format(start_time)+" 至 "+sdf3.format(end_time);
            }else{
				timeDetail = sdf.format(start_time)+" 至 "+sdf.format(end_time);
            }
			//timeDetail = "2013年11月30日 15时30分 至 2013年11月30日 15时30分";
            this.getFormHM().put("isOwner", isOwner);
			this.getFormHM().put("timeDetail", timeDetail);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}

