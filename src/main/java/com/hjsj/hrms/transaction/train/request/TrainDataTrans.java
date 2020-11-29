package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.TransDataBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:培训班</p>
 * <p>Description:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-12-13 下午06:07:55</p>
 * @author lilinbing
 * @version 4.0
 */
public class TrainDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		
		String a_code = (String)hm.get("a_code");
		a_code = a_code!=null&&a_code.trim().length()>0?a_code:"";
		hm.remove("a_code");
		String a_code1 = a_code;
		String msg = (String)hm.get("vflag");
		hm.remove("vflag");
//		if("".equals(a_code)&&(userView.getStatus()==4||userView.isSuper_admin())){
//			a_code=this.getUserView().getManagePrivCode()+this.getUserView().getManagePrivCodeValue();
//		}
//		/**liwc 业务用户走操作单位，没有操作单位时走管理范围=lmm*/
//		else if("".equals(a_code)&&(userView.getStatus()==0&&!userView.isSuper_admin())){
//			String codeall = userView.getUnit_id();
//			a_code = PubFunc.getTopOrgDept(a_code);
//			if(codeall!=null&&codeall.length()>2)
//				a_code=codeall;
//			if("".equals(a_code))
//				a_code=this.getUserView().getManagePrivCode()+this.getUserView().getManagePrivCodeValue();
//		}
		if("".equals(a_code)){
			TrainCourseBo bo = new TrainCourseBo(this.userView);
			a_code = bo.getUnitIdByBusi();
		}
		
		if("".equals(a_code)&&!userView.isSuper_admin())
			throw new GeneralException(ResourceFactory.getProperty("train.job.authorization1"));
		
		String edit="false";
		
		String spflag = (String)this.getFormHM().get("spflag");
		spflag=spflag!=null&&spflag.trim().length()>0?spflag:"";
		spflag= "00".equalsIgnoreCase(spflag)?"":spflag;
		
		if("03".equals(spflag)){
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
		
		String isAutoHour = (String)this.getFormHM().get("isAutoHour");
		isAutoHour=isAutoHour!=null&&isAutoHour.trim().length()>0?isAutoHour:"0";
		
		if(msg==null||msg.length()<1||(!"0".equalsIgnoreCase(msg))){
			spflag="";
			timeflag="";
			startime="";
			endtime="";
			searchstr="";
		}
		
		TransDataBo transbo = new TransDataBo(this.getFrameconn(),"3"); 
		
		StringBuffer sqlstr = new StringBuffer();
		String times = transbo.timesSql(timeflag,startime,endtime);
		sqlstr.append(transbo.sqlColumTrain());
		sqlstr.append(","+isAutoHour+" as timeouto,'' as questionnaire,'' as khresult ");
		sqlstr.append(transbo.sqlWhere(searchstr,a_code,times,spflag));
		sqlstr.append(" order by "+Sql_switcher.isnull("I9999", "999999999"));
		
		ArrayList itemlist = transbo.itemListTrain();
		Field field= new Field("timeouto");
		field.setLabel("timeouto");
		field.setDatatype(DataType.STRING);
		field.setReadonly(true);  //此字段为只读状态	
		field.setVisible(false);  //此字段隐藏	
		itemlist.add(field);
		
        FieldItem item=new FieldItem();
        item.setFieldsetid("r31");
        item.setItemid("questionnaire");
        item.setItemdesc(ResourceFactory.getProperty("train.questionnaire"));
        item.setItemtype("A");
        item.setCodesetid("0");
        item.setAlign("center");
        item.setReadonly(true);
        itemlist.add(item.cloneField());
        
        item=new FieldItem();
        item.setFieldsetid("r31");
        item.setItemid("khresult");
        item.setItemdesc(ResourceFactory.getProperty("train.khresult"));
        item.setItemtype("A");
        item.setCodesetid("0");
        item.setAlign("center");
        item.setReadonly(true);
        itemlist.add(item.cloneField());
		
        this.userView.getHm().put("train_sql", sqlstr.toString());
		this.getFormHM().put("tablename","r31");
		this.getFormHM().put("a_code",a_code1);
		this.getFormHM().put("strsql",sqlstr.toString());
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
		this.getFormHM().put("hsearchstr",searchstr);

	}

}
