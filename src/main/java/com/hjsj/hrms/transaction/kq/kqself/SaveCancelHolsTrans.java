package com.hjsj.hrms.transaction.kq.kqself;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.app_check_in.GetValiateEndDate;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.kqself.CancelHols;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class SaveCancelHolsTrans extends IBusiness {

    public void execute() throws GeneralException {
    	
    	String err = "0";
        try {
        	// 考勤日历标识
        	String flag = this.getFormHM().get("flag")==null?"":(String)this.getFormHM().get("flag");
        	boolean kqemp = false;
        	if("kqemp".equalsIgnoreCase(flag))
        		kqemp = true;
        	
        	HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");     
        	// 假单标识
        	String tableflag = this.getFormHM().get("tableflag")==null?"":(String)this.getFormHM().get("tableflag");
        	// 若为空或没有传假单标识，暂时认为是请假自助单过来的销假单
        	if(StringUtils.isEmpty(tableflag))
        		tableflag = ((String)hm.get("table")).toLowerCase();
        	// 若为空或没有传假单标识，暂时认为是请假自助单过来的销假单
        	if(StringUtils.isEmpty(tableflag))
        		tableflag = "q15";
        	
            RecordVo vo = this.getFormHM().get("cancelvo")==null?null:(RecordVo) this.getFormHM().get("cancelvo");
            // 考勤日历的销假
            if(kqemp) {
            	// 销假事由
            	String xreason = (String)this.getFormHM().get("xreason");
            	
            	vo=new RecordVo(tableflag); 
            	// 原始单号
            	String id = this.getFormHM().get("id")==null?"":(String)this.getFormHM().get("id");
            	StringBuffer sql=new StringBuffer();
            	Calendar now = Calendar.getInstance();
            	Date dd=now.getTime();//系统时间
            	Map map = KqUtilsClass.getCurrKqInfo();
    			Date kq_start = OperateDate.strToDate((String)map.get("kq_start"), "yyyy.MM.dd");
    			GetValiateEndDate va = new GetValiateEndDate(this.userView, this.frameconn);
            	ContentDAO dao=new ContentDAO(this.getFrameconn());
            	
            	vo.setString(tableflag+"01",id);
				vo=dao.findByPrimaryKey(vo);
				sql.append("select * from  ").append(tableflag);
				sql.append(" where ").append(tableflag).append("19=?");
				sql.append(" and ").append(tableflag).append("17=1");
				ArrayList list19 = new ArrayList();
				list19.add(id);
				this.frowset=dao.search(sql.toString(), list19);
				if(this.frowset.next())
				{
					String q1501=this.frowset.getString(tableflag+"01");
					vo.setString(tableflag+"01",q1501);
					vo=dao.findByPrimaryKey(vo);
					Date z1=vo.getDate(tableflag+"z1");
					Date z3=vo.getDate(tableflag+"z3");
					dd=vo.getDate(tableflag+"05");
					if(kq_start.after(z1)){
						z1= kq_start;
						Map timeMap = va.getTimeByDate(vo.getString("nbase"), vo.getString("a0100"), z1);
						if (!(timeMap == null) && !"".equals(timeMap)
								&& !timeMap.isEmpty()) {
							z1 = OperateDate.strToDate(OperateDate
									.dateToStr(z1, "yyyy-MM-dd")
									+ " " + (String) timeMap.get("startTime"),
									"yyyy-MM-dd HH:mm");
						}
					}
					vo.setString(tableflag+"z1",DateUtils.format(z1,"yyyy-MM-dd HH:mm"));
					vo.setString(tableflag+"z3",DateUtils.format(z3,"yyyy-MM-dd HH:mm"));
					vo.setString(tableflag+"05",DateUtils.format(dd,"yyyy-MM-dd HH:mm"));
					vo.setString(tableflag+"17","1");
					// 39377 再次报批更改驳回原因
					vo.setString(tableflag+"07", xreason);
					
		        }else
				{
		        	vo.setString(tableflag+"01",id);
					vo=dao.findByPrimaryKey(vo);
					Date z1=vo.getDate(tableflag+"z1");
					Date z3=vo.getDate(tableflag+"z3");
					if(kq_start.after(z1)){
						z1= kq_start;
						Map timeMap = va.getTimeByDate(vo.getString("nbase"), vo.getString("a0100"), z1);
						if (!(timeMap == null) && !"".equals(timeMap)
								&& !timeMap.isEmpty()) {
							z1 = OperateDate.strToDate(OperateDate
									.dateToStr(z1, "yyyy-MM-dd")
									+ " " + (String) timeMap.get("startTime"),
									"yyyy-MM-dd HH:mm");
						}
					}
					vo.setString(tableflag+"z1",DateUtils.format(z1,"yyyy-MM-dd HH:mm"));
					vo.setString(tableflag+"05",DateUtils.format(dd,"yyyy-MM-dd HH:mm"));
					vo.setString(tableflag+"z3",DateUtils.format(z3,"yyyy-MM-dd HH:mm"));
					vo.setString(tableflag+"z0","03");
					vo.setString(tableflag+"z5","01");
					vo.setString(tableflag+"19",id);
					vo.setString(tableflag+"17","1");
//					String d =vo.getString("q1507");
					vo.setString(tableflag+"07", xreason);  //销假事由
				}
            }
            
            String app_way = (String) this.getFormHM().get("app_way");
            /** vo {q15z3=2017-11-08 17:30, nbase=Usr, e0122=0104, q15z1=2017-11-08 08:30, q15z0=03, a0101=张军, a0100=00000009, 
                  	q1519=0000000112, e01a1=010402, q1507=大幅度发, q1517=1, q1505=2017-12-08 16:33, 
                	q1503=01, q1513=张军, q1501=0000000112, b0110=01, q15z7=2017-11-13 16:17:57.0, q15z5=01}
             **/
            if (vo == null)
                return;
            
            String smflag = kqemp ? (String) this.getFormHM().get("smflag") : (String) hm.get("smflag");
            if (smflag == null || smflag.length() <= 0)
                smflag = "01";
            
            if ("2".equals(app_way)) {
                vo.setString(tableflag+"z1", (String) this.getFormHM().get("scope_start_time"));
                vo.setString(tableflag+"z3", (String) this.getFormHM().get("scope_end_time"));
            }

            String start = vo.getString(tableflag+"z1");
            String end = vo.getString(tableflag+"z3");
            if (start == null || start.length() <= 0 || end == null || end.length() <= 0)
                throw new GeneralException("开始或结束时间不能为空！");
            
            Date kq_start = DateUtils.getDate(start, "yyyy-MM-dd HH:mm");
            Date kq_end = DateUtils.getDate(end, "yyyy-MM-dd HH:mm");

            /** 判断开始日期是否在结束日期之前 */
            if (kq_start.after(kq_end))
                throw new GeneralException(ResourceFactory.getProperty("error.kq.wrongrequence"));

            AnnualApply annualApply = new AnnualApply(this.userView, this.getFrameconn());
            annualApply.checkAppInSealDuration(kq_start);

            // 原申请假单号
            String leaveAppId = vo.getString(tableflag+"19");
            leaveAppId = leaveAppId == null ? "" : leaveAppId;
            //销假单号
            String cancelAppId = vo.getString(tableflag+"01");
            cancelAppId = cancelAppId == null ? "" : cancelAppId;
            //判断申请记录是否重复
            boolean isCorrect = !annualApply.isRepeatedAllAppType(tableflag, userView.getDbname(), userView.getA0100(),
                    userView.getUserFullName(), start, end, 
                    this.getFrameconn(), leaveAppId, cancelAppId);

            if ("02".equals(smflag)) {
                vo.setString(tableflag+"z5", "02");
            } else if ("01".equals(smflag)) {
                vo.setString(tableflag+"z5", "01");
            }
            
            String sels = vo.getString(tableflag+"03");
            CancelHols cancelHols = new CancelHols(this.userView, this.getFrameconn());
            cancelHols.cancelTimeApp(vo, sels, kq_start, kq_end, isCorrect, "", "qicao", tableflag);
        } catch (Exception e) {
            e.printStackTrace();
            err = e.toString();
            throw GeneralExceptionHandler.Handle(e);
        }finally {
        	this.getFormHM().put("err", err);
        }
    }
}
