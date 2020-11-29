package com.hjsj.hrms.transaction.kq.machine;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.kqself.NetSignIn;
import com.hjsj.hrms.businessobject.kq.machine.RepairKqCard;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 保存补刷卡
 *<p>
 * Title:SaveRepairKqCardTrans.java
 * </p>
 *<p>
 * Description:
 * </p>
 *<p>
 * Company:HJHJ
 * </p>
 *<p>
 * Create time:Oct 23, 2007
 * </p>
 * 
 * @author sunxin
 *@version 4.0
 */
public class SaveRepairKqCardTrans extends IBusiness {

    /*
     * (non-Javadoc)
     * 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    private String ip_adr;
    private String into_flag;
    private String causation;
    private Date   oper_time;

    public void execute() throws GeneralException {
    	String reflag = "ok";
    	String repeat_flag = "0";//判断是否去重，=0没有，=1有重复
    	try {
	    	//ajax传过来的加密的人员nbase+a0100
	        String ids = (String)this.getFormHM().get("ids");
	        
	        //表单提交方式形成的表格数据
	        ArrayList selectedinfolist = (ArrayList) this.getFormHM().get("selectedinfolist");
	        
	        //如果没有表单形式的数据，则将ajax数据模拟成表单里的表格数据
	        if (selectedinfolist == null || selectedinfolist.size() <= 0) {
	            if(ids == null || "".equals(ids))
	                return;
	            
	            if(selectedinfolist == null)
	                selectedinfolist = new ArrayList();
	         
	            String[] emps = ids.split(",");
	            for(int i=0; i<emps.length; i++) {
	                String emp = PubFunc.decryption(SafeCode.decode(emps[i]));
	                if(emp.length() != 11)
	                    continue;
	                
	                LazyDynaBean bean = new LazyDynaBean();
	                bean.set("nbase", emp.substring(0, 3));
	                bean.set("a0100", emp.substring(3));
	                	               
	                selectedinfolist.add(bean);
	            }
	        }
	        
	        String temp_emp_table = (String) this.getFormHM().get("temp_emp_table");
			 //xiexd 2014.09.12解密参数
			 temp_emp_table = PubFunc.keyWord_reback(temp_emp_table);
			 
	        /** 刷卡规则 **/
	        String repair_flag = (String) this.getFormHM().get("repair_flag");
	        String ip_adr = (String) this.getFormHM().get("ip_adr");
	        String repair_fashion = (String) this.getFormHM().get("repair_fashion");
	        if (repair_fashion == null || repair_fashion.length() <= 0)
	            return;
	
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	        String strDate = sdf.format(new java.util.Date());
	        Date oper_time = DateUtils.getDate(strDate, "yyyy-MM-dd HH:mm");
	        String into_flag = (String) this.getFormHM().get("into_flag");
	        into_flag = into_flag != null && into_flag.length() > 0 ? into_flag : "0";
	        String causation = (String) this.getFormHM().get("causation");
	        if(ids != null && !"".equals(ids))
	            causation = SafeCode.decode(causation);
	        this.ip_adr = ip_adr;
	        this.into_flag = into_flag;
	        this.causation = causation;
	        this.oper_time = oper_time;
			AnnualApply annualApply = new AnnualApply(this.userView,this.frameconn);
	        if ("0".equals(repair_fashion))// 简单加班
	        {
	            NetSignIn netSignIn = new NetSignIn(this.userView, this.getFrameconn());
	            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	            String easy_date = (String) this.getFormHM().get("jddate");//linbz
	//            String easy_date = (String) hm.get("end_date12"); // 改变时间标签
	            String work_date_server = netSignIn.getWork_date(); // 当前日期
	            String work_tiem_server = netSignIn.getWork_time(); // 当前时间
	            String easy_hh = (String) this.getFormHM().get("easy_hh");
	            if (easy_hh == null || easy_hh.length() <= 0)
	                easy_hh = "00";
	            
	            String easy_mm = (String) this.getFormHM().get("easy_mm");
	            if (easy_mm == null || easy_mm.length() <= 0)
	                easy_mm = "00";
	            
	            String easy_time = easy_hh + ":" + easy_mm;
	            work_date_server = work_date_server.replaceAll("\\.", "-");
	            String easy_date1 = easy_date.replaceAll("\\.", "-");
	            easy_date = easy_date.replaceAll("-", "\\.");
	            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	            java.util.Calendar c1 = java.util.Calendar.getInstance();
	            java.util.Calendar c2_server = java.util.Calendar.getInstance();
	            try {
	                c1.setTime(formatter.parse(easy_date1));
	                c2_server.setTime(formatter.parse(work_date_server));
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	            
	            int result = c1.compareTo(c2_server);
	            if (result > 0) {
	                throw new GeneralException("补签日期不能大于当前日期！");
	            }
	            
	            if (result == 0) {
	                // 日期相等就要对比时间
	                SimpleDateFormat form = new SimpleDateFormat("HH:mm");
	                java.util.Calendar c1_1 = java.util.Calendar.getInstance();
	                java.util.Calendar c2_2_server = java.util.Calendar.getInstance();
	                try {
	                    c1_1.setTime(form.parse(easy_time));
	                    c2_2_server.setTime(form.parse(work_tiem_server));
	                } catch (Exception e) {
	                    throw new GeneralException("时间类型不对！");
	                }
	
	                int result_1 = c1_1.compareTo(c2_2_server);
	                if (result_1 > 0){
	                    throw new GeneralException("补签时间不能大于当前时间！");
	                }
	            }
	            Date date = OperateDate.strToDate(easy_date1 + " " + easy_time, "yyyy-MM-dd HH:mm");
	    		if(!annualApply.isSessionSearl(date,date))
	        	    throw new GeneralException(ResourceFactory.getProperty("kq_card.repair.warn"));
	    		//linbz 29360 之前去重方法错误，从新优化
	    		String repeatA0101s = removalRepeat(selectedinfolist, easy_date, easy_time);
	    		if(!StringUtils.isEmpty(repeatA0101s)){
	    			//有重复的标识
	    			repeat_flag = "1";
	    		}
	    		//去重后若selectedinfolist为空则直接返回
	    		if(selectedinfolist == null || selectedinfolist.size() <= 0)
	    			return;
	    		
	            cycleArray(temp_emp_table, selectedinfolist, easy_date, easy_time, "1");
	        } else if ("1".equals(repair_fashion)) {
	            if (repair_flag == null || repair_flag.length() <= 0)
	                return;
	
	            if ("0".equals(repair_flag)) {
	                /** 按排班情况 **/
	                String statr_date = (String) this.getFormHM().get("statr_date");
	                String end_date = (String) this.getFormHM().get("end_date");
	                String class_flag = (String) this.getFormHM().get("class_flag");
	                // 得到服务器时间，补签时间不能大于服务器的当前时间；wangy
	                NetSignIn netSignIn = new NetSignIn(this.userView, this.getFrameconn());
	                String work_date_server = netSignIn.getWork_date();
	                /** 服务器时间与补签时间对比;补签不等大于服务的当前时间; wangy 开始 **/
	                work_date_server = work_date_server.replaceAll("\\.", "-");
	                String work_date2 = statr_date.replaceAll("\\.", "-");
	                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	                java.util.Calendar c1 = java.util.Calendar.getInstance();
	                java.util.Calendar c2_server = java.util.Calendar.getInstance();
	                try {
	                    c1.setTime(formatter.parse(work_date2));
	                    c2_server.setTime(formatter.parse(work_date_server));
	
	                } catch (Exception e) {
	                    throw new GeneralException("日期时间类型不对！HH:mm");
	                }
	
	                int result = c1.compareTo(c2_server);
	                if (result > 0){
	                    throw new GeneralException("补签开始日期不能大于当前日期！");
	                }
	                /** 结束 **/
	                String work_date3 = end_date.replaceAll("\\.", "-");
	                try {
	                    c1.setTime(formatter.parse(work_date3));
	                    c2_server.setTime(formatter.parse(work_date_server));
	
	                } catch (Exception e) {
	                    throw new GeneralException("日期时间类型不对！HH:mm");
	                }
	                
	                int result1 = c1.compareTo(c2_server);
	                if (result1 > 0){
	                    throw new GeneralException("补签结束日期不能大于当前日期！");
	                }
	                Date startDate = OperateDate.strToDate(work_date2 + " 00:00", "yyyy-MM-dd HH:mm");
	                Date endDate = OperateDate.strToDate(work_date3 + " 23:59", "yyyy-MM-dd HH:mm");
	        		if(!annualApply.isSessionSearl(startDate,endDate)){
	            	    throw new GeneralException(ResourceFactory.getProperty("kq_card.repair.warn"));
	        		}
	                classArray(temp_emp_table, selectedinfolist, statr_date, end_date, class_flag);
	            } else if ("1".equals(repair_flag)) {
	                /** 循环 **/
	                String cycle_date = (String) this.getFormHM().get("cycle_date");
	                String cycle_hh = (String) this.getFormHM().get("cycle_hh");
	                if (cycle_hh == null || cycle_hh.length() <= 0)
	                    cycle_hh = "00";
	                
	                String cycle_mm = (String) this.getFormHM().get("cycle_mm");
	                if (cycle_mm == null || cycle_mm.length() <= 0)
	                    cycle_mm = "00";
	                
	                String cycle_num = (String) this.getFormHM().get("cycle_num");
	                String cycle_time = cycle_hh + ":" + cycle_mm;
	                // 得到服务器时间，补签时间不能大于服务器的当前时间；wangy
	                NetSignIn netSignIn = new NetSignIn(this.userView, this.getFrameconn());
	                String work_date_server = netSignIn.getWork_date();
	                String work_tiem_server = netSignIn.getWork_time();
	                /** 服务器时间与补签时间对比;补签不等大于服务的当前时间; wangy 开始 **/
	                work_date_server = work_date_server.replaceAll("\\.", "-");
	                String work_date2 = cycle_date.replaceAll("\\.", "-");
	                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	                java.util.Calendar c1 = java.util.Calendar.getInstance();
	                java.util.Calendar c2_server = java.util.Calendar.getInstance();
	                try {
	                    c1.setTime(formatter.parse(work_date2));
	                    c2_server.setTime(formatter.parse(work_date_server));
	
	                } catch (Exception e) {
	                    throw new GeneralException("日期时间类型不对！HH:mm");
	                }
	                
	                int result = c1.compareTo(c2_server);
	                if (result > 0) {
	                    throw new GeneralException("补签开始日期不能大于当前日期！");
	                } else if (result == 0) {
	                    // 日期相等就要对比时间
	                    SimpleDateFormat form = new SimpleDateFormat("HH:mm");
	                    java.util.Calendar c1_1 = java.util.Calendar.getInstance();
	                    java.util.Calendar c2_2_server = java.util.Calendar.getInstance();
	                    try {
	                        c1_1.setTime(form.parse(cycle_time));
	                        c2_2_server.setTime(form.parse(work_tiem_server));
	                    } catch (Exception e) {
	                        throw new GeneralException("时间类型不对！");
	                    }
	                    int result_1 = c1_1.compareTo(c2_2_server);
	                    if (result_1 > 0){
	                        throw new GeneralException("补签时间不能大于当前时间！");
	                    }
	                }
	                /** 结束 **/
	                /** 判断循环时间 **/
	                String bDate = work_date2;
	                String work_date3 = "";
	                int result1;
	                java.util.Calendar c12 = java.util.Calendar.getInstance();
	                java.util.Calendar c2_server2 = java.util.Calendar.getInstance();
	                try {
	                    Date bdate = (Date) formatter.parse(bDate);
	                    int cs = Integer.parseInt(cycle_num);
	                    if (cs != 0)
	                        cs = cs - 1;
	                    
	                    Date dd = DateUtils.addDays(bdate, cs);
	                    String mDateTime = formatter.format(dd);
	                    work_date3 = mDateTime.replaceAll("\\.", "-"); // 循环后的时间
	                    try {
	                        c12.setTime(formatter.parse(work_date3));
	                        c2_server2.setTime(formatter.parse(work_date_server));
	                    } catch (Exception e) {
	                        throw new GeneralException("日期时间类型不对！HH:mm");
	                    }
	                    result1 = c12.compareTo(c2_server2);
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	                
	                result1 = c12.compareTo(c2_server2);
	                if (result1 > 0) {
	                    throw new GeneralException("补签循环时间不能大于当前日期！");
	                }
	
	                Date startDate = OperateDate.strToDate(work_date2 + " 00:00", "yyyy-MM-dd HH:mm");
	                Date endDate = OperateDate.strToDate(work_date3 + " 23:59", "yyyy-MM-dd HH:mm");
	        		if(!annualApply.isSessionSearl(startDate,endDate)){
	            	    throw new GeneralException(ResourceFactory.getProperty("kq_card.repair.warn"));
	        		}
	        		
	                cycleArray(temp_emp_table, selectedinfolist, cycle_date, cycle_time, cycle_num);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        if (e instanceof GeneralException)
	            reflag = ((GeneralException) e).getErrorDescription();
	        else
	            reflag = e.getMessage();
	    } finally {
	        this.getFormHM().put("repair_reflag", reflag);
	        this.getFormHM().put("repeat_flag", repeat_flag);
	    }
    }

    /**
     * 循环方式补刷卡
     * 
     * @param selectedinfolist
     * @param cycle_date
     * @param cycle_time
     * @param cycle_num
     */
    public void cycleArray(String temp_emp_table, ArrayList selectedinfolist, String cycle_date, String cycle_time, String cycle_num) throws GeneralException {
        RepairKqCard repairKqCard = new RepairKqCard(this.getFrameconn(), this.userView, this.ip_adr, this.into_flag, this.causation, this.oper_time);
        repairKqCard.updateTempEmpFlag(selectedinfolist, temp_emp_table);
        String time_table = repairKqCard.createTimeTemp("");
        if (cycle_date == null || cycle_date.length() <= 0) {
            throw new GeneralException("循环日期不能为空！");
        }

        repairKqCard.insertCycleTimeTemp(time_table, cycle_date, cycle_time, cycle_num);
        String temp_dataTable = repairKqCard.createCycleTemp(temp_emp_table, time_table);
        /* 限制补刷卡次数 */
        int numLimit = repairKqCard.getRepairCardNumLimit();
        if (numLimit > 0) {
            ArrayList list = repairKqCard.isOverTopRepairday(numLimit, temp_dataTable);
            if (list != null && list.size() > 0) {
                StringBuffer mess = new StringBuffer();
                mess.append("");
                for (int i = 0; i < list.size(); i++) {
                    String a0101 = list.get(i).toString();
                    if (mess.indexOf(a0101 + "，") != -1)
                        continue;
                    
                    mess.append(a0101 + "，");
                    if (i > 0 && (i % 7) == 0)
                        mess.append("\\n");
                }
                throwRepairCardException(mess.toString(), numLimit + "");
            } else {
                // 更改：0016724 bug 用来控制第一次不补刷卡次数
                int daysd = Integer.parseInt(numLimit + ""); // 刷卡次数
                int numts = Integer.parseInt(cycle_num); // 循环天数
                if (numts > daysd) {
                    throwRepairCardException("", numLimit + "");
                }

                repairKqCard.insertCycleTemp(temp_dataTable); // 补刷卡写入
            }
        } else {
            repairKqCard.insertCycleTemp(temp_dataTable);
        }
        KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn());
        kqUtilsClass.dropTable(temp_dataTable);
        kqUtilsClass.dropTable(time_table);

    }

    /**
     * 按排班情况补刷卡
     * 
     * @param selectedinfolist
     * @param statr_date
     * @param end_date
     * @param class_flag
     */
    public void classArray(String temp_emp_table, ArrayList selectedinfolist, String statr_date, String end_date, String class_flag) throws GeneralException {
        if (statr_date == null || statr_date.length() <= 0) {
            throw new GeneralException("，开始日期不能为空！");
        }

        if (end_date == null || end_date.length() <= 0) {
            throw new GeneralException("，结束日期不能为空！");
        }
        RepairKqCard repairKqCard = new RepairKqCard(this.getFrameconn(), this.userView, this.ip_adr, this.into_flag, this.causation, this.oper_time);
        repairKqCard.updateTempEmpFlag(selectedinfolist, temp_emp_table);
        String time_table = repairKqCard.createTimeTemp("");
        repairKqCard.insertInstanceTimeTemp(time_table, statr_date, end_date);
        String temp_dateTable = repairKqCard.createInstanceTemp(temp_emp_table, time_table);
        /* 限制补刷卡次数 */
        int numLimit = repairKqCard.getRepairCardNumLimit();
        if (numLimit > 0) {
            ArrayList list = repairKqCard.isOverTopRepairday(numLimit, temp_dateTable);
            if (list != null && list.size() > 0) {
                StringBuffer mess = new StringBuffer();
                mess.append("");
                for (int i = 0; i < list.size(); i++) {
                    String a0101 = list.get(i).toString();
                    if (mess.indexOf(a0101 + "，") != -1)
                        continue;
                    
                    mess.append(list.get(i).toString() + "，");
                    if (i > 0 && (i % 7) == 0)
                        mess.append("\\n");
                }

                throwRepairCardException(mess.toString(), numLimit + "");
            } else {
                repairKqCard.classRepairFromTemp(temp_dateTable, class_flag);
            }
        } else {
            repairKqCard.classRepairFromTemp(temp_dateTable, class_flag);
        }
        KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn());
        kqUtilsClass.dropTable(temp_dateTable);
        kqUtilsClass.dropTable(time_table);
    }

    /**
     * 去除重复补刷卡记录，并返回A0100
     * @param selectedinfolist
     * @param work_date
     * @param work_time
     * @return
     */
    public String removalRepeat(ArrayList selectedinfolist, String work_date, String work_time){
    	StringBuffer repeatA0100s = new StringBuffer("");
    	RowSet kqrs = null;
    	try{
	    	ContentDAO dao = new ContentDAO(this.getFrameconn());
	    	/*查询补刷记录与原刷卡数据是否重复sql*/
	        StringBuffer kqsql = new StringBuffer();
	        kqsql.append(" select A0100 from kq_originality_data ");
	        kqsql.append(" where nbase=? ");
	        kqsql.append(" and A0100=? ");
	        kqsql.append(" and work_date=? ");
	        kqsql.append(" and work_time=? ");
	    	for(int i=selectedinfolist.size()-1;i>=0;i--){
				LazyDynaBean rec=(LazyDynaBean)selectedinfolist.get(i); 
				String nbase = (String) rec.get("nbase");
				String A0100 = (String) rec.get("a0100");
				
				if(StringUtils.isEmpty(nbase) || StringUtils.isEmpty(A0100)){
					selectedinfolist.remove(i);
					continue;
				}
				
				ArrayList onew_list=new ArrayList();
				onew_list.add(nbase);
				onew_list.add(A0100);
				onew_list.add(work_date);
				onew_list.add(work_time);
				
				kqrs = dao.search(kqsql.toString(), onew_list);
				if(kqrs.next()){
					String A0100a = kqrs.getString("A0100");
					if(!StringUtils.isEmpty(A0100a)){
						repeatA0100s.append(",").append(A0100a);
						selectedinfolist.remove(i);
						continue;
					}
				}
	    	}
    	} catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(kqrs);
        }
    	return repeatA0100s.toString();
    }
    
    
    private void throwRepairCardException(String msg, String cardNumLimit) throws GeneralException {
        throw new GeneralException(msg 
                + "\\n" 
                + ResourceFactory.getProperty("kq.repair.over.num_hint_1") 
                + cardNumLimit 
                + ResourceFactory.getProperty("kq.repair.over.num_hint_2"));
    }
}
