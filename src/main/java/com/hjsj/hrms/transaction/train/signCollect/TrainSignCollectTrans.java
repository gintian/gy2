package com.hjsj.hrms.transaction.train.signCollect;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.TransDataBo;
import com.hjsj.hrms.businessobject.train.attendance.TrainAtteBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 培训汇总
 * <p>Title:TrainSignCollectTrans.java</p>
 * <p>Description>:TrainSignCollectTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 8, 2011 5:05:35 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class TrainSignCollectTrans extends IBusiness {

	public void execute() throws GeneralException {
		String sort=(String)this.getFormHM().get("sort");
		String a_code=(String)this.getFormHM().get("a_code");
		HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
		String a_code_new=(String)reqhm.get("a_code");
		String classplan=(String)this.getFormHM().get("classplan");
		
        if (classplan != null && classplan.length() > 0)
            classplan = PubFunc.decrypt(SafeCode.decode(classplan));

		DbWizard dbw = new DbWizard(this.getFrameconn());
		if(!dbw.isExistTable("R47",false)){
			throw new GeneralException(ResourceFactory.getProperty("出勤汇总表不存在！"));
		}
		if(a_code_new!=null&&a_code_new.equals(a_code))
		{
			a_code=a_code_new;
			this.getFormHM().put("a_code", a_code);
			reqhm.remove("a_code");
			classplan="";
		}
		if("".equals(a_code)&&!userView.isSuper_admin()){
			TrainCourseBo bo = new TrainCourseBo(this.userView);
			a_code = bo.getUnitIdByBusi();
		}
		String timeflag = (String)this.getFormHM().get("timeflag");
		timeflag=timeflag!=null&&timeflag.trim().length()>0?timeflag:"";
		timeflag= "00".equalsIgnoreCase(timeflag)?"":timeflag;
		
		String startime = (String)this.getFormHM().get("startime");
		startime=startime!=null&&startime.trim().length()>0?startime:"";
		
		String endtime = (String)this.getFormHM().get("endtime");
		endtime=endtime!=null&&endtime.trim().length()>0?endtime:"";
		TrainAtteBo trainAtteBo=new TrainAtteBo();
		TransDataBo transbo = new TransDataBo(this.getFrameconn(),"3"); 
		String times = transbo.timesSql(timeflag,startime,endtime);		
		ArrayList classlist=trainAtteBo.getTrainClassForSpflag(this.getFrameconn(),a_code,"04,06",times);//仅包含已发布和结束的
		
//		if(classlist==null||classlist.size()<=0)
//		{
//			classlist=new ArrayList();
//			CommonData cd=new CommonData();
//			cd.setDataName("无培养班");
//			cd.setDataValue("#");
//			classlist.add(cd);
//			classplan="";
//			this.getFormHM().put("view_record", "false");
//		}else
			this.getFormHM().put("view_record", "true");
		
		if((classplan==null||classplan.length()<=0)&&classlist!=null&&classlist.size()>0)
		{
			CommonData cd=(CommonData)classlist.get(0);
			classplan=cd.getDataValue();
			if (classplan != null && classplan.length() > 0)
	            classplan = PubFunc.decrypt(SafeCode.decode(classplan));
		}				
		this.getFormHM().put("timelist",transbo.timeFlagList());
		this.getFormHM().put("classplanlist", classlist);
		this.getFormHM().put("classplan", SafeCode.encode(PubFunc.encrypt(classplan)));
		
		//班级审批状态
		LazyDynaBean classbean=trainAtteBo.getTrainClassSpflag(this.getFrameconn(),classplan);
		String classpanSpflag=(String)classbean.get("r3127");
		this.getFormHM().put("classpanSpflag", classpanSpflag);
		if("1".equals(sort)){
		  getClassSignCollectSQLParam();
		  this.getFormHM().put("loadclass", "true");
		  return;
		}
		else
		  this.getFormHM().put("loadclass", "false");
		
		ArrayList fielditemlist=(ArrayList)this.getFormHM().get("fielditemlist");
		if("2".equals(sort))
		{
			String search=(String)this.getFormHM().get("search");
			search=trainAtteBo.getSearchWhere(search);//条件查询
			LazyDynaBean bean=trainAtteBo.getCourseSignCollectSQLParam(classplan,fielditemlist,search);
			this.userView.getHm().put("train_columns", bean.get("columns")); 
			this.userView.getHm().put("train_sql", bean.get("sql_str").toString() + " " + bean.get("where_str").toString() + " " + bean.get("order_str").toString()); 
			this.getFormHM().put("sql_str", bean.get("sql_str"));
			this.getFormHM().put("where_str",  bean.get("where_str"));
			this.getFormHM().put("order_str", bean.get("order_str"));
			this.getFormHM().put("columns",bean.get("columns"));			
			this.getFormHM().put("fielditemlist", bean.get("fielditemlist")); 
		}else if("3".equals(sort))
		{
			String search=(String)this.getFormHM().get("search");
			search=trainAtteBo.getSearchWhere(search);//条件查询
			LazyDynaBean bean=trainAtteBo.getClassSignCollectSQLParam(a_code,times,search,"04,06",fielditemlist);
			this.userView.getHm().put("train_columns", bean.get("columns")); 
            this.userView.getHm().put("train_sql", bean.get("sql_str").toString() + " " + bean.get("where_str").toString() + " " + bean.get("order_str").toString());
			this.getFormHM().put("sql_str", bean.get("sql_str"));
			this.getFormHM().put("where_str",  bean.get("where_str"));
			this.getFormHM().put("order_str", bean.get("order_str"));
			this.getFormHM().put("columns",bean.get("columns"));			
			this.getFormHM().put("fielditemlist", bean.get("fielditemlist")); 
		}
		this.getFormHM().put("search", "");
		
	}
	
	private void getClassSignCollectSQLParam(){
		ArrayList fielditemlist=DataDictionary.getFieldList("R47",Constant.USED_FIELD_SET);	
        ArrayList list=new ArrayList();
        for(int i=0;i<fielditemlist.size();i++){
			FieldItem fielditem=(FieldItem)fielditemlist.get(i);			
			if("1".equals(fielditem.getState()))
			{						
				fielditem.setVisible(true);					
			}else
			{
				fielditem.setVisible(false);
			}
			list.add(fielditem.clone());			
		}
		StringBuffer cloums=new StringBuffer();//得到属性列
		for(int i=0;i<fielditemlist.size();i++){
			FieldItem fielditem=(FieldItem)fielditemlist.get(i);
			cloums.append(fielditem.getItemid()+",");					
		}
		cloums.setLength(cloums.length()-1);
		String sql="select "+cloums.toString();
		String where ="from r47 where 1=2";
		String order="order by b0110,e0122";
		this.getFormHM().put("sql_str", sql);
		this.getFormHM().put("where_str", where);
		this.getFormHM().put("order_str", order);
		this.getFormHM().put("columns", cloums.toString());
	}

}
