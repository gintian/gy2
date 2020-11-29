package com.hjsj.hrms.transaction.performance.workdiary;

import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySQLStr;
import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySelStr;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.struts.upload.FormFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MyWorkdiaryAddorEditTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String fileFlag1 = (String)reqhm.get("fileFlag1");
		if(fileFlag1!=null && !"".equals(fileFlag1))
			hm.put("fileFlag", fileFlag1);
		
		String state = (String)hm.get("state");
		state=state!=null&&state.trim().length()>0?state:"";
		if(state.length()>1)
			state = PubFunc.decryption(state);
		hm.put("state",state);
		String yearnum = (String)hm.get("yearnum");
		yearnum=yearnum!=null&&yearnum.trim().length()>0?yearnum:"";
		hm.put("yearnum",yearnum);
		
		String monthnum = (String)hm.get("monthnum");
		monthnum=monthnum!=null&&monthnum.trim().length()>0?monthnum:"0";
		hm.put("monthnum",monthnum);
		
		RecordVo p01Vo_myself=new RecordVo("p01");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		UserView uv=this.getUserView();
		if(reqhm.containsKey("query")){//点击“填写”、“编辑”
			p01Vo_myself = new RecordVo("p01"); // 我的日志进入编辑页面和报批保存时使用 lium
			String query=(String) reqhm.get("query");
			if("add".equals(query)){//进入增加我的日志页面
				IDGenerator  idg=new IDGenerator(2,this.getFrameconn());
				String pid = idg.getId("P01.P0100");
				p01Vo_myself.setString("p0100",pid);
				p01Vo_myself.setString("a0100",uv.getA0100());
				p01Vo_myself.setString("nbase",uv.getDbname());
				p01Vo_myself.setString("b0110",uv.getUserOrgId());
				p01Vo_myself.setString("e0122",uv.getUserDeptId());
				p01Vo_myself.setString("a0101",uv.getUserFullName());
				p01Vo_myself.setString("e01a1",uv.getUserPosId());
				p01Vo_myself.setString("p0115","01");
				p01Vo_myself.setString("state",state);
				if("1".equals(state)|| "2".equals(state)){//周报、月报
					String startime = (String)reqhm.get("startime");
					reqhm.remove("startime");
					startime=startime!=null&&startime.trim().length()>0?startime:"";
					
					String endtime = (String)reqhm.get("endtime");
					reqhm.remove("endtime");
					endtime=endtime!=null&&endtime.trim().length()>0?endtime:"";
					String itemid[] = itemid();//0 下期   1 本期
					if("1".equals(state)){//周报
						if(itemid!=null&&itemid[0]!=null&&itemid[0].trim().length()>0&&itemid[1]!=null&&itemid[1].trim().length()>0){
							String plancontent=workPlan(dao,p01Vo_myself.getString("a0100"),itemid[0],startime,state);
							p01Vo_myself.setString(itemid[1],plancontent);//把上一周下期的内容放到本期中
						}
					}else if("2".equals(state)){//月报
						if(itemid!=null&&itemid[1]!=null&&itemid[1].trim().length()>0&&itemid[0]!=null&&itemid[0].trim().length()>0){
							String plancontent=workPlan(dao,p01Vo_myself.getString("a0100"),itemid[0],startime,state);
							p01Vo_myself.setString(itemid[1],plancontent);
						}
					}
					String tablestr = perPlanTable(dao,startime,endtime);
					hm.put("perPlanTable",tablestr);
					hm.put("startime",startime);
					hm.put("endtime",endtime);
				}else{//日报
					String timestr = (String)reqhm.get("timestr");
					hm.put("times", timestr);
					timestr = PubFunc.decryption(timestr);
					timestr=timestr!=null&&timestr.trim().length()>0?timestr:"";
					reqhm.remove("timestr");
					if(timestr.trim().length()>5){
						hm.put("startime",timestr);
						hm.put("endtime",timestr);
					}else{
						hm.put("startime","");
						hm.put("endtime","");
					}
					String tablestr = perPlanTable(dao,timestr,timestr);
					hm.put("perPlanTable",tablestr);
				}
				this.getFormHM().put("personid", "");
				this.getFormHM().put("personname","");
				hm.put("returnch","0");
				//hm.put("p01Vo_myself",p01Vo_myself);
				hm.put("p01Vo_myself",revampVo(p01Vo_myself,state));
				hm.put("flag","0");
				hm.put("fileFlag", "0");
				hm.put("appflag","0");
				hm.put("dis","");
			}else{//进入编辑页面
				if(reqhm.get("pdCode")!=null && !"".equals(reqhm.get("pdCode")))
					hm.put("pendingCode", reqhm.get("pdCode"));
//				通过p0100查询p01vo
				String p0100=(String) reqhm.get("p0100");
				//我的任务查看日志，后台报错 jingq add 2015.02.06
				if(!p0100.matches("[0-9]+"))
					p0100 = PubFunc.decryption(p0100);
				if("".equals(p0100))
					p0100=(String) reqhm.get("p0100");
				reqhm.remove("p0100");
				p01Vo_myself.setString("p0100",p0100);
				try {
					p01Vo_myself=dao.findByPrimaryKey(p01Vo_myself);
					//更新per_diary_actor表display=1 字段 标记为已阅
					dao.update(" update per_diary_actor set display=1 where p0100="+p0100);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Date teststartime=p01Vo_myself.getDate("p0104");
				Date testendtime=p01Vo_myself.getDate("p0106");
				if("1".equals(state)|| "2".equals(state)){
					hm.put("startime",teststartime.toString().substring(0,10));
					hm.put("endtime",testendtime.toString().substring(0,10));
				}else{
					FieldItem fielditem04= DataDictionary.getFieldItem("p0104");
					FieldItem fielditem06= DataDictionary.getFieldItem("p0106");
					if(fielditem04.getItemlength()>10)
						hm.put("startime",teststartime.toString().substring(0,19));
					else
						hm.put("startime",teststartime.toString().substring(0,10));
					if(fielditem06.getItemlength()>10)
						hm.put("endtime",testendtime.toString().substring(0,19));
					else
						hm.put("endtime",testendtime.toString().substring(0,10));
				}
				if(p01Vo_myself.getString("p0113")!=null){
					p01Vo_myself.setString("p0113",this.getenter(p01Vo_myself.getString("p0113")));
				}
				String tablestr = perPlanTable(dao,teststartime.toString().substring(0,10),
						testendtime.toString().substring(0,10));
				hm.put("perPlanTable",tablestr);
				//hm.put("p01Vo_myself",p01Vo_myself);
				hm.put("p01Vo_myself",revampVo(p01Vo_myself,state));
				String fileFlag = (String)hm.get("fileFlag");
				hm.put("flag","1");
				hm.put("fileFlag",fileFlag);
				if(reqhm.containsKey("query")&& "own".equals((String)reqhm.get("query"))){
					hm.put("appflag","1");
					reqhm.remove("query");
					hm.put("dis","true");
				}else{
					hm.put("appflag","0");
					hm.put("dis","");
				}
				String timestr = (String)reqhm.get("timestr");
				hm.put("times", timestr);
				timestr = PubFunc.decryption(timestr);
				timestr=timestr!=null&&timestr.trim().length()>0?timestr:"";
				reqhm.remove("timestr");
				String returnch = (String)reqhm.get("returnch");
				returnch=returnch!=null&&returnch.trim().length()>0?returnch:"";
				reqhm.remove("returnch");
				
				if(returnch.trim().length()>0){
					hm.put("returnch",returnch);
				}else{
					if(timestr.trim().length()>5){
						hm.put("returnch","0");
					}else{
						hm.put("returnch","1");
					}
				}
				String personArr[] = perdairy(dao,p0100);
				this.getFormHM().put("personid", personArr[0]);
				this.getFormHM().put("personname", personArr[1]);
			}
			reqhm.remove("query");
		}else{//点击“保存”、“报批”

			String save=(String) reqhm.get("save");
			if("0".equals(state) && ( "app".equals(save) || "updateapp".equals(save)) ){//日报提交日期限制 系统参数设置-其他参数
				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
				
				String limit_HH=sysbo.getValue(Sys_Oth_Parameter.DAIRYINFOLIMIT,"limit_HH");
				String limit_MM=sysbo.getValue(Sys_Oth_Parameter.DAIRYINFOLIMIT,"limit_MM");
				String dairyinfolimit=sysbo.getValue(Sys_Oth_Parameter.DAIRYINFOLIMIT);
				if(dairyinfolimit==null || "".equals(dairyinfolimit)){
					dairyinfolimit = "9999";//如果系统参数中没有设置延长几天，那么就规定为不延长，按当天算。郭峰
				}
				Date date=new Date();
				String dd=(String) hm.get("startime");
				Date pd=DateUtils.getDate(dd+" 23:59","yyyy-MM-dd HH:mm");
				if(limit_HH!=null&&limit_HH.length()>0&&limit_MM!=null&&limit_MM.length()>0)
				{
					pd=DateUtils.getDate(dd+" "+limit_HH+":"+limit_MM,"yyyy-MM-dd HH:mm");
				}
				if("0".equalsIgnoreCase(dairyinfolimit))//当天				{
				{
					pd=DateUtils.getDate(dd,"yyyy-MM-dd");
					date=DateUtils.getDate(new SimpleDateFormat("yyyy-MM-dd").format(date),"yyyy-MM-dd");
					if(!pd.equals(date))
					{
						throw new GeneralException(ResourceFactory.getProperty("workdiary.message.out.time"));
					}
				}else //延后日
				{
					pd=DateUtils.addDays(pd, Integer.parseInt(dairyinfolimit));
					if(pd.before(date))
					{
						throw new GeneralException(ResourceFactory.getProperty("workdiary.message.out.time"));
					}
				}
			}
			p01Vo_myself=(RecordVo) hm.get("p01Vo_myself");
			if("add".equals(save)){//保存
				boolean isExist = dao.isExistRecordVo(p01Vo_myself);//判断数据库中是否已经存在该条记录 lis
				if(!isExist){//如果不存在则新增 lis
					String startime=(String) hm.get("startime");
					String endtime=(String)hm.get("endtime");
					//if(state.equals("0")){
						startime +=" 00:00:00";
						endtime +=" 23:59:59";
						
						Date sd=DateUtils.getDate(startime,"yyyy-MM-dd HH:mm:ss");
						Date ed=DateUtils.getDate(endtime,"yyyy-MM-dd HH:mm:ss");
					
						float sjsj=DateUtils.hourDiff(sd,ed);
						if(sjsj<0){
							throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("workdiary.time.error"),"",""));
						}
						
						FormFile file=(FormFile)getFormHM().get("picturefile");
	
	
						if(file!=null){
							String lfilename = file.getFileName();
							if(lfilename!=null&&lfilename.indexOf(".")!=-1){
								boolean flag = FileTypeUtil.isFileTypeEqual(file);
						    	if(!flag){
						    		throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
						    	}
								String filetxt = lfilename.substring(lfilename.lastIndexOf("."));//扩展名
								
								String filename = (String)this.getFormHM().get("filename");
								filename = PubFunc.hireKeyWord_filter(filename); // 刘蒙
								filename=filename!=null&&filename.trim().length()>0?filename:lfilename.substring(0,lfilename.lastIndexOf("."));
								
								RecordVo vo = new RecordVo("per_diary_file");
								IDGenerator idg=new IDGenerator(2,this.getFrameconn());
								String feast_id=idg.getId("per_diary_file.file_id");
								vo.setInt("file_id",Integer.parseInt(feast_id));
								vo.setString("name", filename);
								vo.setString("ext", filetxt);
								vo.setString("p0100", p01Vo_myself.getString("p0100"));
								//vo.setInt("p0100", Integer.parseInt(p01Vo_myself.getString("p0100")));
								dao.addValueObject(vo);
								updateFile(file,vo,dao);
							}
						}
					//}
					
					p01Vo_myself.setDate("p0104",startime);
					if(p01Vo_myself.getString("p0113")!=null){
						p01Vo_myself.setString("p0113",this.getBr(p01Vo_myself.getString("p0113")));
					}
					p01Vo_myself.setDate("p0106",endtime);
					p01Vo_myself.setDate("p0114",strDate());
					dao.addValueObject(p01Vo_myself);
					
					String personid = (String)this.getFormHM().get("personid");
					personid = PubFunc.hireKeyWord_filter(personid); // 刘蒙
					personid=personid!=null?personid:"";
					//if(personid.trim().length()>1)
						insertPerDairy(dao,p01Vo_myself.getString("p0100"),personid);
				}
			}else if("addload".equals(save)){
//				进行增加操作
				String src_startime=(String) hm.get("startime");
				String src_endtime=(String)hm.get("endtime");
				//if(state.equals("0")){
				String startime = src_startime + " 00:00:00";
				String endtime = src_endtime + " 23:59:59";
					
					Date sd=DateUtils.getDate(startime,"yyyy-MM-dd HH:mm:ss");
					Date ed=DateUtils.getDate(endtime,"yyyy-MM-dd HH:mm:ss");
				
					float sjsj=DateUtils.hourDiff(sd,ed);
					if(sjsj<0){
						throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("workdiary.time.error"),"",""));
					}
					
					FormFile file=(FormFile)getFormHM().get("picturefile");
					if(file!=null){
						String lfilename = file.getFileName();
						if(lfilename!=null&&lfilename.indexOf(".")!=-1){
							boolean flag = FileTypeUtil.isFileTypeEqual(file);
					    	if(!flag){
					    		throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
					    	}
							String filetxt = lfilename.substring(lfilename.lastIndexOf("."));//扩展名
							
							String filename = (String)this.getFormHM().get("filename");
							filename = PubFunc.hireKeyWord_filter(filename); // 刘蒙
							filename=filename!=null&&filename.trim().length()>0?filename:lfilename.substring(0,lfilename.lastIndexOf("."));
							
							RecordVo vo = new RecordVo("per_diary_file");
							IDGenerator idg=new IDGenerator(2,this.getFrameconn());
							String feast_id=idg.getId("per_diary_file.file_id");
							vo.setInt("file_id",Integer.parseInt(feast_id));
							vo.setString("name", filename);
							vo.setString("ext", filetxt);
							vo.setString("p0100", p01Vo_myself.getString("p0100"));
							dao.addValueObject(vo);
							updateFile(file,vo,dao);
						}
					}
				//}
				
				p01Vo_myself.setDate("p0104",startime);
				if(p01Vo_myself.getString("p0113")!=null){
					p01Vo_myself.setString("p0113",this.getBr(p01Vo_myself.getString("p0113")));
				}
				p01Vo_myself.setDate("p0106",endtime);
				p01Vo_myself.setDate("p0114",strDate());
				dao.addValueObject(p01Vo_myself);
				
				if("1".equals(state)|| "2".equals(state)){
					hm.put("startime",src_startime);
					hm.put("endtime",src_endtime);
					String tablestr = perPlanTable(dao,startime.toString().substring(0,10),
							endtime.toString().substring(0,10));
					hm.put("perPlanTable",tablestr);
				}else{
					hm.put("startime",src_startime);
					hm.put("endtime",src_endtime);
					String tablestr = perPlanTable(dao,startime.toString().substring(0,10),
							endtime.toString().substring(0,10));
					hm.put("perPlanTable",tablestr);
				}
				String fileFlag = (String)hm.get("fileFlag");
				hm.put("flag","1");
				hm.put("fileFlag", fileFlag);
				hm.put("appflag","0");
				hm.put("dis","");
				
				String personid = (String)this.getFormHM().get("personid");
				personid = PubFunc.hireKeyWord_filter(personid); // 刘蒙
				personid=personid!=null?personid:"";
				//if(personid.trim().length()>1)
					insertPerDairy(dao,p01Vo_myself.getString("p0100"),personid);
				
				String returnch = (String)reqhm.get("returnch");
				returnch=returnch!=null&&returnch.trim().length()>0?returnch:"";
				reqhm.remove("returnch");
				hm.put("returnch", returnch);
				String personArr[] = perdairy(dao,p01Vo_myself.getString("p0100"));
				this.getFormHM().put("personid", personArr[0]);
				this.getFormHM().put("personname", personArr[1]);
				
			}else if("updateload".equals(save)){
//				进行增加操作
				String src_startime=(String) hm.get("startime");
				String src_endtime=(String)hm.get("endtime");
				//if(state.equals("0")){
				String startime = src_startime + " 00:00:00";
				String endtime = src_endtime + " 23:59:59";
					
					Date sd=DateUtils.getDate(startime,"yyyy-MM-dd HH:mm:ss");
					Date ed=DateUtils.getDate(endtime,"yyyy-MM-dd HH:mm:ss");
				
					float sjsj=DateUtils.hourDiff(sd,ed);
					if(sjsj<0){
						throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("workdiary.time.error"),"",""));
					}
					
					FormFile file=(FormFile)getFormHM().get("picturefile");
					if(file!=null){
						String lfilename = file.getFileName();
						if(lfilename!=null&&lfilename.indexOf(".")!=-1){
							boolean flag = FileTypeUtil.isFileTypeEqual(file);
					    	if(!flag){
					    		throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
					    	}
							String filetxt = lfilename.substring(lfilename.lastIndexOf("."));//扩展名
							
							String filename = (String)this.getFormHM().get("filename");
							filename = PubFunc.hireKeyWord_filter(filename); // 刘蒙
							filename=filename!=null&&filename.trim().length()>0?filename:lfilename.substring(0,lfilename.lastIndexOf("."));
							
							RecordVo vo = new RecordVo("per_diary_file");
							IDGenerator idg=new IDGenerator(2,this.getFrameconn());
							String feast_id=idg.getId("per_diary_file.file_id");
							vo.setInt("file_id",Integer.parseInt(feast_id));
							vo.setString("name", filename);
							vo.setString("ext", filetxt);
							vo.setString("p0100", p01Vo_myself.getString("p0100"));
							dao.addValueObject(vo);
							updateFile(file,vo,dao);
						}
					}
				//}
				
				p01Vo_myself.setDate("p0104",startime);
				if(p01Vo_myself.getString("p0113")!=null){
					p01Vo_myself.setString("p0113",this.getBr(p01Vo_myself.getString("p0113")));
				}
				p01Vo_myself.setDate("p0106",endtime);
				p01Vo_myself.setDate("p0114",strDate());
				try {
					dao.updateValueObject(p01Vo_myself);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if("1".equals(state)|| "2".equals(state)){
					hm.put("startime",src_startime);
					hm.put("endtime",src_endtime);
					String tablestr = perPlanTable(dao,startime.toString().substring(0,10),
							endtime.toString().substring(0,10));
					hm.put("perPlanTable",tablestr);
				}else{
					hm.put("startime",src_startime);
					hm.put("endtime",src_endtime);
					String tablestr = perPlanTable(dao,startime.toString().substring(0,10),
							endtime.toString().substring(0,10));
					hm.put("perPlanTable",tablestr);
				}
				String fileFlag = (String)hm.get("fileFlag");
				hm.put("flag","1");
				hm.put("fileFlag", fileFlag);
				hm.put("appflag","0");
				hm.put("dis","");
				String personid = (String)this.getFormHM().get("personid");
				personid = PubFunc.hireKeyWord_filter(personid); // 刘蒙
				personid=personid!=null?personid:"";
				//if(personid.trim().length()>1)
					insertPerDairy(dao,p01Vo_myself.getString("p0100"),personid);
				String returnch = (String)reqhm.get("returnch");
				returnch=returnch!=null&&returnch.trim().length()>0?returnch:"";
				reqhm.remove("returnch");
				hm.put("returnch", returnch);
				String personArr[] = perdairy(dao,p01Vo_myself.getString("p0100"));
				this.getFormHM().put("personid", personArr[0]);
				this.getFormHM().put("personname", personArr[1]);
			}else if("app".equals(save)){
//				进行增加操作
				String startime=(String) hm.get("startime");
				String endtime=(String)hm.get("endtime");
				String timestr = (String)hm.get("times");
				timestr=timestr!=null&&timestr.trim().length()>0?timestr:"";
				//if(state.equals("0")){
					startime +=" 00:00:00";
					endtime +=" 23:59:59";
					
					Date sd=DateUtils.getDate(startime,"yyyy-MM-dd HH:mm:ss");
					Date ed=DateUtils.getDate(endtime,"yyyy-MM-dd HH:mm:ss");
				
					float sjsj=DateUtils.hourDiff(sd,ed);
					if(sjsj<0){
						throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("workdiary.time.error"),"",""));
					}

					FormFile file=(FormFile)getFormHM().get("picturefile");
					if(file!=null){
						String lfilename = file.getFileName();
						if(lfilename!=null&&lfilename.indexOf(".")!=-1){
							boolean flag = FileTypeUtil.isFileTypeEqual(file);
					    	if(!flag){
					    		throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
					    	}
							String filetxt = lfilename.substring(lfilename.lastIndexOf("."));//扩展名
							
							String filename = (String)this.getFormHM().get("filename");
							filename = PubFunc.hireKeyWord_filter(filename); // 刘蒙
							filename=filename!=null&&filename.trim().length()>0?filename:lfilename.substring(0,lfilename.lastIndexOf("."));
							
							RecordVo vo = new RecordVo("per_diary_file");
							IDGenerator idg=new IDGenerator(2,this.getFrameconn());
							String feast_id=idg.getId("per_diary_file.file_id");
							vo.setInt("file_id",Integer.parseInt(feast_id));
							vo.setString("name", filename);
							vo.setString("ext", filetxt);
							vo.setString("p0100", p01Vo_myself.getString("p0100"));
							dao.addValueObject(vo);
							updateFile(file,vo,dao);
						}
					}
				//}
				
				p01Vo_myself.setDate("p0104",startime);
				if(p01Vo_myself.getString("p0113")!=null){
					p01Vo_myself.setString("p0113",this.getBr(p01Vo_myself.getString("p0113")));
				}
				p01Vo_myself.setDate("p0106",endtime);
				p01Vo_myself.setDate("p0114",strDate());
				p01Vo_myself.setString("p0115","02");
				dao.addValueObject(p01Vo_myself);
				//添加审批人
				addSuperiorUser(p01Vo_myself.getString("p0100"),dao);
				mailSuperiorUser(p01Vo_myself.getString("p0100"));
				//添加待办
				String Curr_user=(String) this.getFormHM().get("curr_user");
				Curr_user = PubFunc.hireKeyWord_filter(Curr_user); // 刘蒙
				if(Curr_user!=null && Curr_user.length()>0){
					PendingTask imip=new PendingTask();
				    try {
						sendPending(p01Vo_myself,dao,timestr);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
				String personid = (String)this.getFormHM().get("personid");
				personid = PubFunc.hireKeyWord_filter(personid); // 刘蒙
				personid=personid!=null?personid:"";
				//if(personid.trim().length()>1)
					insertPerDairy(dao,p01Vo_myself.getString("p0100"),personid);
			}else if("updateapp".equals(save)){//报批
				try {
					String timestr = (String)hm.get("times");
					timestr=timestr!=null&&timestr.trim().length()>0?timestr:"";
					if(p01Vo_myself.getString("p0113")!=null){
						p01Vo_myself.setString("p0113",this.getBr(p01Vo_myself.getString("p0113")));
					}
					//if(state.equals("0")){
						FormFile file=(FormFile)getFormHM().get("picturefile");
						
						if(file!=null){
							String lfilename = file.getFileName();
							if(lfilename!=null&&lfilename.indexOf(".")!=-1){
								boolean flag = FileTypeUtil.isFileTypeEqual(file);
						    	if(!flag){
						    		throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
						    	}
								String filetxt = lfilename.substring(lfilename.lastIndexOf("."));//扩展名
								String filename = (String)this.getFormHM().get("filename");
								filename = PubFunc.hireKeyWord_filter(filename); // 刘蒙
								filename=filename!=null&&filename.trim().length()>0?filename:lfilename.substring(0,lfilename.lastIndexOf("."));
								RecordVo vo = new RecordVo("per_diary_file");
								IDGenerator idg=new IDGenerator(2,this.getFrameconn());
								String feast_id=idg.getId("per_diary_file.file_id");
								vo.setInt("file_id",Integer.parseInt(feast_id));
								vo.setString("name", filename);
								vo.setString("ext", filetxt);
								vo.setString("p0100", p01Vo_myself.getString("p0100"));
								dao.addValueObject(vo);
								updateFile(file,vo,dao);
							}
						}
					//}
					p01Vo_myself.setString("p0115","02");
					dao.updateValueObject(p01Vo_myself);
					//添加审批人
					addSuperiorUser(p01Vo_myself.getString("p0100"),dao);
					mailSuperiorUser(p01Vo_myself.getString("p0100"));
					//添加待办
					String Curr_user=(String) this.getFormHM().get("curr_user");
					Curr_user = PubFunc.hireKeyWord_filter(Curr_user); // 刘蒙
					if(Curr_user!=null && Curr_user.length()>0){
						PendingTask imip=new PendingTask();
					    String pre_pendingID = (String)this.getFormHM().get("pendingCode");
					    if(pre_pendingID!=null&&pre_pendingID.trim().length()>0)
						{ 
					    	//System.out.println(pre_pendingID);
							imip.updatePending("W",pre_pendingID,1,"",this.userView);
						}
					    try {
							sendPending(p01Vo_myself,dao,timestr);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					String personid = (String)this.getFormHM().get("personid");
					personid = PubFunc.hireKeyWord_filter(personid); // 刘蒙
					personid=personid!=null?personid:"";
					//if(personid.trim().length()>1)
						insertPerDairy(dao,p01Vo_myself.getString("p0100"),personid);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}else{
//				进行修改操作
				try {
					if(p01Vo_myself.getString("p0113")!=null){
						p01Vo_myself.setString("p0113",this.getBr(p01Vo_myself.getString("p0113")));
					}
					//if(state.equals("0")){
						FormFile file=(FormFile)getFormHM().get("picturefile");

						if(file!=null){
							String lfilename = file.getFileName();
							if(lfilename!=null&&lfilename.indexOf(".")!=-1){
								boolean flag = FileTypeUtil.isFileTypeEqual(file);
						    	if(!flag){
						    		throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
						    	}
								String filetxt = lfilename.substring(lfilename.lastIndexOf("."));//扩展名
								
								String filename = (String)this.getFormHM().get("filename");
								filename = PubFunc.hireKeyWord_filter(filename); // 刘蒙
								filename=filename!=null&&filename.trim().length()>0?filename:lfilename.substring(0,lfilename.lastIndexOf("."));
								
								RecordVo vo = new RecordVo("per_diary_file");
								IDGenerator idg=new IDGenerator(2,this.getFrameconn());
								String feast_id=idg.getId("per_diary_file.file_id");
								vo.setInt("file_id",Integer.parseInt(feast_id));
								vo.setString("name", filename);
								vo.setString("ext", filetxt);
								vo.setString("p0100", p01Vo_myself.getString("p0100"));
								dao.addValueObject(vo);
								updateFile(file,vo,dao);
							}
						}
					//}
					dao.updateValueObject(p01Vo_myself);
					String personid = (String)this.getFormHM().get("personid");
					personid = PubFunc.hireKeyWord_filter(personid); // 刘蒙
					personid=personid!=null?personid:"";
					//if(personid.trim().length()>1)
						insertPerDairy(dao,p01Vo_myself.getString("p0100"),personid);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
			reqhm.remove("save");
		} //点击保存、报批 结束
		ArrayList fieldlist=DataDictionary.getFieldList("P01",Constant.USED_FIELD_SET);
		ArrayList filterlist=this.filteritem(fieldlist);//过滤掉页面不显示内容，得到页面要显示的字段
		this.getFormHM().put("filename", "");
		this.getFormHM().put("fieldlist",filterlist);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	public void sendPending(RecordVo vo,ContentDAO dao,String times) throws GeneralException, SQLException{
	    String p0100=vo.getString("p0100");	
	    p0100 = PubFunc.encryption(p0100);
		String url="/performance/workdiary/workdiaryshow.do?b_search=link&home=5&a0100=&p0100="+p0100+"&timestr="+times;
		String state=vo.getString("state");
		StringBuffer str=new StringBuffer();
		str.append(ResourceFactory.getProperty("work.diary.emial.title")+" "+vo.getString("a0101"));
		Date P0104_D=vo.getDate("p0104");
		Date P0106_D=vo.getDate("p0106");
		if(P0104_D!=null)
		{
			str.append(" "+DateUtils.format(P0104_D,"yyyy.MM.dd"));
		}
		if(P0106_D!=null && !"0".equals(state))
		{
			str.append("~"+DateUtils.format(P0106_D,"yyyy.MM.dd"));
		}
		str.append(" 的("+getStateDesc(Integer.parseInt(state))+")");
		PendingTask imip=new PendingTask();
		/**
	     * 向待办库中加入新的待办
	     * @param pendingCode 待办编号
	     * @param appType 申请待办的类型
	     * @param pendingTitle  待办标题
	     * @param senderMessage 待办信息发送人usercode（发送人用户登录名）Usr000001     
	     * @param receiverMessage 待办信息接收人usercode（接收人用户登录名）Usr000001
	     * @param pendingURL  待办链接地址
	     * @param pendingStatus  待办状态（0：待办， 1：已办，2：待阅，3：已阅）
	     * @param pendingLevel  待办级别（0：非重要，1：重要）
	     * @param pendingType  待办所在应用中的类别（不同类型的绩效考核的中文名称，如个人考核，团队考核）
	     * @return
	     */
		String pendingCode = this.getPendingCode(this.userView.getA0100(), this.userView.getDbname());
		url+="&pdCode="+pendingCode;
		imip.insertPending(pendingCode,"W",str.toString(),this.userView.getDbname()+this.userView.getA0100(),this.userView.getDbname()+this.userView.getA0100(),
				url,0,0,"",this.userView);
	}
	private String getPendingCode(String a0100,String nbase)
	{
		Date d=new Date();
		return  "HRMS-"+nbase+a0100+"-"+d.getTime()+Math.round(Math.ceil(Math.random()*10));
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
	public String getBr(String value){
		String valueresult ="";
		for(int i=0;i<value.length();i++)
		{
		   if("\n".equals(value.substring(i,i+1)))
			   valueresult+="<br>";
		   else
			   valueresult+=value.substring(i,i+1);
		}	
		return valueresult;
	}
	/**
	 * 根据后台设置的显示＆隐藏进行控制
	 * @param fieldlist
	 * @return
	 */
	public ArrayList filteritem(ArrayList fieldlist ){
		ArrayList fieldlist1=new ArrayList();
		StringBuffer buf=new StringBuffer();
		buf.append("p0100");
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem field=(FieldItem) fieldlist.get(i);
			if(buf.indexOf(field.getItemid().toLowerCase())!=-1)
			{
				fieldlist1.add(field);
				continue;
			}
			if("A0101".equalsIgnoreCase(field.getItemid())|| "E0122".equalsIgnoreCase(field.getItemid())|| "E01A1".equalsIgnoreCase(field.getItemid())|| "P0116".equalsIgnoreCase(field.getItemid())|| "P0117".equalsIgnoreCase(field.getItemid()))
				continue;
			if(field.isVisible())
				fieldlist1.add(field);
		}
		return fieldlist1;
	}
	public String getenter(String value){
		value=value.replaceAll("<br>","\n");
		
		return value;
	}
	/**
	 * 获取当前日期字符串 格式xxxx-xx-xx
	 * @return String date 字符串
	 */
	public String strDate(){
			int day = Calendar.getInstance().get(Calendar.DATE);
			int month = Calendar.getInstance().get(Calendar.MONTH)+1;
			int year = Calendar.getInstance().get(Calendar.YEAR);
			
			String strday = day+"";
			String strmonth = month+"";
			
			if(day<10){
				strday = "0"+strday;
			}
			if(month<10){
				strmonth = "0"+strmonth;
			}	
			return year+"-"+strmonth+"-"+strday;
	}
	/**
	 * 获取上一次记录的日期
	 * @return String date 字符串
	 */
	public String upSunday(String str){
		Date date = DateUtils.addDays(DateUtils.getDate(str,"yyyy-MM-dd"),-1);
		String da = DateUtils.format(date,"yyyy-MM-dd");
		return da;
	}
	public String[] itemid(){
		ArrayList fieldlist=DataDictionary.getFieldList("P01",Constant.USED_FIELD_SET);
		String itemid[]=new String[2];
		for(int i=0;i<fieldlist.size();i++){
			FieldItem fielditem = (FieldItem)fieldlist.get(i);
			if("下期工作计划".equalsIgnoreCase(fielditem.getItemdesc())){
				itemid[0]=fielditem.getItemid();
			}else if("本期工作计划".equalsIgnoreCase(fielditem.getItemdesc())){
				itemid[1]=fielditem.getItemid();
			}
		}
		return itemid;
		
	}
	/**
	 * 获取上一起的下期工作计划
	 * @return String date 字符串
	 */
	public String workPlan(ContentDAO dao,String a0100,String itemid,String startime,String state){
			String upcontent="";
			WorkdiarySQLStr wss=new WorkdiarySQLStr();
			String tart_time =wss.getDataValue("p0106","=",upSunday(startime));
			StringBuffer buf = new StringBuffer();
			buf.append("select ");
			buf.append(itemid);
			buf.append(" from p01 where a0100='");
			buf.append(a0100);
			buf.append("' and ");
			buf.append(tart_time);
			buf.append(" and state=");
			buf.append(state);
			try {
				this.frowset=dao.search(buf.toString());
				while(this.frowset.next()){
					upcontent=this.frowset.getString(itemid);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return upcontent;
	}
	/**
	 * 文件存到数据库
	 * @param file
	 * @param attach_id
	 */
	private void updateFile(FormFile file,RecordVo vo,ContentDAO dao){
		try{
			RecordVo a_vo =dao.findByPrimaryKey(vo);
			switch(Sql_switcher.searchDbServer()){
				case Constant.ORACEL:
					break;
				default:
					byte[] data = file.getFileData();
					a_vo.setObject("content",data);
				break;	
			}
			if(Sql_switcher.searchDbServer()==Constant.ORACEL){
				Blob blob = getOracleBlob(file,a_vo.getString("file_id"));
				a_vo.setObject("content",blob);
				dao.updateValueObject(a_vo);
			}else{
				dao.updateValueObject(a_vo);
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	private String[] perdairy(ContentDAO dao,String p0100){
		String[] perArr = new String[2];
		try {
			String personid = "";
			String personname = "";
			this.frowset = dao.search("select NBASE,A0100,A0101 from per_diary_actor where state=1 and P0100='"+p0100+"'");
			while(this.frowset.next()){
				personid+=this.frowset.getString("NBASE")+this.frowset.getString("A0100")+"`";
				personname+=this.frowset.getString("A0101")+",";
			}
			perArr[0]= personid;
			perArr[1]= personname;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return perArr;
	}
	private void insertPerDairy(ContentDAO dao,String p0100,String personstr){
		try {
			String sql = "delete from per_diary_actor where P0100='"+p0100+"'";
			dao.update(sql);
			
			String[] personarr = personstr.split("`");
			List personlist = new ArrayList();
			for (int i = 0; i < personarr.length; i++) {
				String person = personarr[i];
				try {
					// 抄送已集成新选人控件，需解密 chent 20180212 add
					person = PubFunc.decrypt(person);
				} catch(Exception e) {}
				Map voPerson = new HashMap();
				if (person != null && person.length() > 3) {
					StringBuffer sqlstr = new StringBuffer();
					sqlstr.append("select B0110,E0122,E01A1,A0101 from ");
					sqlstr.append(person.substring(0,3));
					sqlstr.append("A01 where A0100='");
					sqlstr.append(person.substring(3).replaceAll("\\,", ""));
					sqlstr.append("'");
					this.frowset = dao.search(sqlstr.toString());
					while (this.frowset.next()) {
						voPerson.put("DBNAME", person.substring(0,3));
						voPerson.put("A0100", person.substring(3));
						voPerson.put("B0110", this.frowset
								.getString("B0110"));
						voPerson.put("E0122", this.frowset
								.getString("E0122"));
						voPerson.put("E01A1", this.frowset
								.getString("E01A1"));
						voPerson.put("A0101", this.frowset
								.getString("A0101"));
						personlist.add(voPerson);
						
					}
				}
			}
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("insert into per_diary_actor (NBASE,A0100,P0100,B0110,E0122,E01A1,A0101,state) values(");
			sqlstr.append("?,?,?,?,?,?,?,1)");
			ArrayList perlist = new ArrayList();
			for (int i = 0; i < personlist.size(); i++) {
				Map voPerson = (Map) personlist.get(i);
				ArrayList listvalue = new ArrayList();
				listvalue.add((String)voPerson.get("DBNAME"));
				listvalue.add(((String)voPerson.get("A0100")).replaceAll("\\,", ""));
				listvalue.add(p0100);
				listvalue.add((String)voPerson.get("B0110"));
				listvalue.add((String)voPerson.get("E0122"));
				listvalue.add((String)voPerson.get("E01A1"));
				listvalue.add((String)voPerson.get("A0101"));

				if(((String)voPerson.get("A0100")).replaceAll("\\,", "")!=null && ((String)voPerson.get("A0100")).replaceAll("\\,", "").length()>=0){
					WorkdiarySelStr wss=new WorkdiarySelStr();
					try {wss.sendEMail(this.getFrameconn(), ((String)voPerson.get("A0100")).replaceAll("\\,", ""), p0100,"0");} catch (GeneralException e) {e.printStackTrace();}
				}
				perlist.add(listvalue);
			}
			dao.batchInsert(sqlstr.toString(), perlist);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * oracle得到blob字段
	 * @param file
	 * @param attach_id
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private Blob getOracleBlob(FormFile file,String fileid) throws FileNotFoundException, IOException {
		StringBuffer strSearch=new StringBuffer();
		strSearch.append("select content from ");
		strSearch.append("per_diary_file ");
		strSearch.append(" where file_id='");
		strSearch.append(fileid);	
		strSearch.append("' FOR UPDATE");
		
		StringBuffer strInsert=new StringBuffer();
		strInsert.append("update  per_diary_file");
		strInsert.append(" set content=EMPTY_BLOB() where file_id='");
		strInsert.append(fileid);
		strInsert.append("'");

	    OracleBlobUtils blobutils=new OracleBlobUtils(this.getFrameconn());
	    Blob blob =null;
	    InputStream inputStream=file.getInputStream();
	    try{
		   blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),inputStream); 
	    }
        finally{
            PubFunc.closeResource(inputStream);   
        }
		return blob;
	}

	private String perPlanTable(ContentDAO dao,String starttime,String endtime){
		StringBuffer tablestr=new StringBuffer();
		tablestr.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\"");
		tablestr.append(" cellpadding=\"0\">");
		WeekUtils wb = new WeekUtils();
		Date date1 = thedate(starttime,endtime);//wb.strTodate(endtime);
		
		GregorianCalendar cal1 = new GregorianCalendar();
		cal1.setTime(date1);
		int month = cal1.get(GregorianCalendar.MONTH)+1;
		int quarter = wb.getQuarter(date1);
		
		String myear = "01";
		if(month>6){
			myear="02";
		}
		String monthstr = "";
		if(month>9)
			monthstr = month+"";
		else
			monthstr = "0"+month;
		WorkdiarySQLStr wss=new WorkdiarySQLStr();
		StringBuffer strSearch=new StringBuffer();
		strSearch.append("select distinct pp.plan_id,pp.name from ");
		strSearch.append("per_plan pp,per_object po,p04");
		strSearch.append(" where ((pp.theyear='");
		strSearch.append(cal1.get(GregorianCalendar.YEAR));
		strSearch.append("' and pp.cycle='0') or (");
		strSearch.append(" pp.theyear='");
		strSearch.append(cal1.get(GregorianCalendar.YEAR));
		strSearch.append("' and pp.themonth='");
		strSearch.append(monthstr);	
		strSearch.append("' and pp.cycle='3') or (");
		strSearch.append(" pp.theyear='");
		strSearch.append(cal1.get(GregorianCalendar.YEAR));
		strSearch.append("' and pp.thequarter='0");
		strSearch.append(quarter);	
		strSearch.append("' and pp.cycle='2') or (");
		strSearch.append(" pp.theyear='");
		strSearch.append(cal1.get(GregorianCalendar.YEAR));
		strSearch.append("' and pp.thequarter='");
		strSearch.append(myear);	
		strSearch.append("' and pp.cycle='1')");
		strSearch.append(" or (");
		strSearch.append(wss.getDataValue("pp.start_date",">=",starttime));	
		strSearch.append(" and ");	
		strSearch.append(wss.getDataValue("pp.start_date","<=",endtime));
		strSearch.append(" and pp.cycle='7')");
		//张婕 结束时间所在月报看不到该目标卡，不对 应该都能看到
		strSearch.append(" or (");
		strSearch.append(wss.getDataValue("pp.end_date",">=",starttime));	
		strSearch.append(" and ");	
		strSearch.append(wss.getDataValue("pp.end_date","<=",endtime));
		strSearch.append(" and pp.cycle='7')");
		
		strSearch.append(") and pp.plan_id=po.plan_id and pp.plan_id=p04.plan_id and  pp.method='2'");
		strSearch.append(" and p04.a0100='");
		strSearch.append(this.userView.getA0100()+"'");
//		strSearch.append(" and p04.nbase='");
//		strSearch.append(this.userView.getDbname()+"'");
		strSearch.append(" and po.object_id='");
		strSearch.append(this.userView.getA0100());
		strSearch.append("' and po.sp_flag in ('03','06') and status <> 0");
		try {
			this.frowset = dao.search(strSearch.toString());
			int i=0;
			while(this.frowset.next()){
				String plan_id = this.frowset.getString("plan_id");
				String name = this.frowset.getString("name");
				tablestr.append("<tr>");
				tablestr.append("<td>&nbsp;");
				tablestr.append("<span style=\"cursor:hand;color:#0000FF\" onclick=\"perPlan('");
				tablestr.append(PubFunc.encryption(plan_id));
				tablestr.append("','"+PubFunc.encryption(this.userView.getA0100())+"');\">");
				tablestr.append(name);
				tablestr.append("</span></td>");
				tablestr.append("</tr>");
				i++;
			}
			if(i>0){
				tablestr.append("<tr><td>&nbsp;</td></tr>");
				tablestr.append("</table>");
			}else{
				tablestr.setLength(0);
				tablestr.append("no");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return tablestr.toString();
	}
	
	/**
	 * 添加当前审批人
	 * @param p0100
	 * @param dao
	 * @throws SQLException
	 * @author LiWeichao
	 */
	public void addSuperiorUser(String id,ContentDAO dao){
		String Curr_user=(String) this.getFormHM().get("curr_user");
		try {
			if(Curr_user!=null && Curr_user.length()>0)
				dao.update("update p01 set Curr_user='"+Curr_user+"' where p0100="+id);
			else
				dao.update("update p01 set Curr_user=null where p0100="+id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 给审批人发送邮件。
	 * @param p0100
	 * @throws SQLException
	 * @author LiWeichao
	 */
	public void mailSuperiorUser(String p0100){
		WorkdiarySelStr wss=new WorkdiarySelStr();
		ArrayList list = wss.getSuperiorUser(this.getUserView().getA0100(), new ContentDAO(this.getFrameconn()));
		for (int i = 0; i < list.size(); i++) {
			LazyDynaBean ldb=(LazyDynaBean) list.get(i);
			if(this.formHM.get("curr_user")!=null&&this.formHM.get("curr_user").equals((String)ldb.get("username"))){
				String a0100=(String)ldb.get("a0100");
				try {wss.sendEMail(this.getFrameconn(), a0100, p0100,"1");} catch (GeneralException e) {e.printStackTrace();}
			}
		}
	}

	/**
	 * 判断日期段属于那个月的日志
	 * @param starttime
	 * @param endtime
	 * @return
	 */
	private Date thedate(String starttime,String endtime){
		WeekUtils wb = new WeekUtils();
		Date date1 = wb.strTodate(starttime);
		Date date2 = wb.strTodate(endtime);
		if(DateUtils.getDay(date2)>1)
			return date2;
		else
			return date1;
	}
	
	/**
	 * 添加引入规则 2011-09-05 17:05:06
	 * @param p01vo
	 * @return
	 */
	private RecordVo revampVo(RecordVo p01vo,String state){
		boolean flag = true;
		if("0".equals(state))
			flag = false;
		
		if("01".equals(p01vo.getString("p0115"))||"07".equals(p01vo.getString("p0115"))){//起草和驳回
			String p0101=p01vo.getString("p0101");//工作项目
			if(p0101!=null&&p0101.trim().length()>0)
				flag = false;
			String p0109=p01vo.getString("p0109");//完成情况
			if(p0109!=null&&p0109.trim().length()>0)
				flag = false;
			String p0111=p01vo.getString("p0111");//问题和建议
			if(p0111!=null&&p0111.trim().length()>0)
				flag = false;
			
			if(flag){
				p01vo.setString("p0101", getRevamp("p0101",state));
				p01vo.setString("p0109", getRevamp("p0109",state));
				p01vo.setString("p0111", getRevamp("p0111",state));
			}
		}
		return p01vo;
	}
	
	private String getRevamp(String itemid, String state){
		StringBuffer tmpstr = new StringBuffer();
		String startime = (String)this.getFormHM().get("startime");
		startime = PubFunc.hireKeyWord_filter(startime); // 刘蒙
		String endtime = (String)this.getFormHM().get("endtime");
		endtime = PubFunc.hireKeyWord_filter(endtime); // 刘蒙
		if("2".equals(state)){//定义属于本月周报的日期范围
			Date startdate = DateUtils.getDate(startime, "yyyy-MM-dd");
			Date enddate = DateUtils.getDate(endtime, "yyyy-MM-dd");
			startdate = DateUtils.addDays(startdate, -5);
			enddate = DateUtils.addDays(enddate, 1);
			startime = DateUtils.format(startdate, "yyyy-MM-dd");
			endtime = DateUtils.format(enddate, "yyyy-MM-dd");
		}
		startime = startime + " 00:00:00";
		endtime = endtime + " 23:59:59";
		StringBuffer sql = new StringBuffer();
		sql.append("select p0104,p0106,"+itemid);
		sql.append(" from p01");
		sql.append(" where a0100='"+this.userView.getA0100()+"'");
		sql.append(" and nbase='"+this.userView.getDbname()+"'");
		sql.append(" and state="+(Integer.parseInt(state)-1));
		sql.append(" and p0104>="+Sql_switcher.dateValue(startime)+" and p0106<="+Sql_switcher.dateValue(endtime));
		sql.append(" order by p0104,p0106");
		//System.out.println(sql.toString());
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql.toString());
			while(this.frowset.next()){
				Date date1 = this.frowset.getDate("p0104");
				Date date2 = this.frowset.getDate("p0106");
				String tmpItem = this.frowset.getString(itemid);
				if(tmpItem!=null&&tmpItem.length()>0){
					if("1".equals(state)){
						tmpstr.append(DateUtils.FormatDate(date1, "EEE"));
					}else if("2".equals(state)){
						tmpstr.append(DateUtils.FormatDate(date1, "yyyy-MM-dd"));
						tmpstr.append("至");
						tmpstr.append(DateUtils.FormatDate(date2, "yyyy-MM-dd"));
					}
					tmpstr.append("\r\n");
					tmpstr.append(tmpItem);
					tmpstr.append("\r\n\r\n");
				}
			}
			
			if(tmpstr!=null&&tmpstr.length()<1&&"2".equals(state)){//如果没有引用周报内容 则引用当前月的日报内容
				startime = (String)this.getFormHM().get("startime");
				startime = PubFunc.hireKeyWord_filter(startime); // 刘蒙
				endtime = (String)this.getFormHM().get("endtime");
				endtime = PubFunc.hireKeyWord_filter(endtime); // 刘蒙
				startime = startime + " 00:00:00";
				endtime = endtime + " 23:59:59";
				
				sql.setLength(0);
				sql.append("select p0104,"+itemid);
				sql.append(" from p01");
				sql.append(" where a0100='"+this.userView.getA0100()+"'");
				sql.append(" and nbase='"+this.userView.getDbname()+"'");
				sql.append(" and state=0");
				sql.append(" and p0104>="+Sql_switcher.dateValue(startime)+" and p0106<="+Sql_switcher.dateValue(endtime));
				sql.append(" order by p0104");
				this.frowset = dao.search(sql.toString());
				while(this.frowset.next()){
					Date date1 = this.frowset.getDate("p0104");
					String tmpItem = this.frowset.getString(itemid);
					if(tmpItem!=null&&tmpItem.length()>0){
						tmpstr.append(DateUtils.FormatDate(date1, "yyyy-MM-dd EEE"));
						tmpstr.append("\r\n");
						tmpstr.append(tmpItem);
						tmpstr.append("\r\n\r\n");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tmpstr.toString();
	}
}
