package com.hjsj.hrms.transaction.train.traincourse;

import com.hjsj.hrms.businessobject.train.TransDataBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:培训班</p>
 * <p>Description:显示培训班</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-12-13 下午06:07:55</p>
 * @author lilinbing
 * @version 4.0
 */
public class TrainSearchDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		
		String a_code = (String)hm.get("a_code");
		a_code = a_code!=null&&a_code.trim().length()>0?a_code:"";
		hm.remove("a_code");

		String model = (String)hm.get("model");
		model = model!=null&&model.trim().length()>0?model:"1";
		hm.remove("model");
		String edit="false";
		
		String spflag = (String)this.getFormHM().get("spflag");
		spflag=spflag!=null&&spflag.trim().length()>0?spflag:"";
		spflag= "00".equalsIgnoreCase(spflag)?"":spflag;
	      //20180720  zxj 严格过滤，防止注入
        spflag = PubFunc.hireKeyWord_filter(spflag);        
        if(null == AdminCode.getCode("23", spflag)) 
            spflag = "";
        
		if("2".equals(model)){
			spflag= "01".equals(spflag)|| "07".equals(spflag)?"":spflag;
			if("02".equals(spflag)|| "02".equals(spflag))
				edit="true";
		}else{
			if("01".equals(spflag)|| "07".equals(spflag))
				edit="true";
		}
		
		String timeflag = (String)this.getFormHM().get("timeflag");
		timeflag=timeflag!=null&&timeflag.trim().length()>0?timeflag:"";
		timeflag= "00".equalsIgnoreCase(timeflag)?"":timeflag;
		
		String startime = (String)this.getFormHM().get("startime");
		startime=startime!=null&&startime.trim().length()>0?startime:"";
		
		String endtime = (String)this.getFormHM().get("endtime");
		endtime=endtime!=null&&endtime.trim().length()>0?endtime:"";
		
		String searchstr = (String)this.getFormHM().get("searchstr");
		searchstr=searchstr!=null&&searchstr.trim().length()>0?searchstr:"";
		
		TransDataBo transbo = new TransDataBo(this.getFrameconn(),model); 
		
		StringBuffer sqlstr = new StringBuffer();
		String times = transbo.timesSql(timeflag,startime,endtime);
		sqlstr.append(transbo.sqlColum());
		sqlstr.append(transbo.sqlWhere(this.userView,searchstr,a_code,times,spflag));
		if("1".equals(model)){
			sqlstr.append(" and R3127 in('01','02','03','04','05','06','07')");
		}if("2".equals(model)){
			sqlstr.append(" and R3127 in('02','03','04','05','06')");
		}
		
		sqlstr.append(" order by "+Sql_switcher.isnull("I9999", "999999999"));
		
		ArrayList itemlist = transbo.itemList();
		
		this.userView.getHm().put("train_sql", sqlstr.toString());
		this.getFormHM().put("encryptParam",PubFunc.encrypt("a_code="+a_code));
		this.getFormHM().put("tablename","r31");
		this.getFormHM().put("a_code",a_code);
		this.getFormHM().put("strsql",sqlstr.toString());
		this.getFormHM().put("model",model);
		this.getFormHM().put("spflag",spflag);
		this.getFormHM().put("timeflag",timeflag);
		this.getFormHM().put("startime",startime);
		this.getFormHM().put("endtime",endtime);
		this.getFormHM().put("searchstr","");
		this.getFormHM().put("itemlist",itemlist);
		this.getFormHM().put("flaglist",transbo.spFlagList());
		this.getFormHM().put("timelist",transbo.timeFlagList());
		this.getFormHM().put("username",this.userView.getUserName());
		this.getFormHM().put("fieldSize",itemlist.size()+"");
		this.getFormHM().put("edit",edit);
	}

}
