package com.hjsj.hrms.transaction.kq.month_kq;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SaveMonthKqPeopleTrans extends IBusiness{

	public void execute() throws GeneralException {
		String tmps_str = this.getFormHM().get("temp_str").toString();
		
		String [] temps = tmps_str.split(",");
		
		MonthKqBo bo = new MonthKqBo(this.frameconn);
		String year = this.getFormHM().get("year").toString();
		String month = this.getFormHM().get("month").toString();
		
		String isok = "引入人员失败!";
		
		WeekUtils wk = new WeekUtils();
		String codes = "";
	   	Date dd = wk.lastMonth(Integer.parseInt(year), Integer.parseInt(month));
		for(int i = 1 ; i <=dd.getDate() ; i ++){
			//判断这一年的这一个月的这一天 是否是周六周日
			ManagePrivCode mc = new ManagePrivCode(this.userView , this.getFrameconn());
			if(1 == bo.isGongXiu(Integer.parseInt(year), Integer.parseInt(month), i,mc.getUNB0110(),this.userView)){
				if(i < 10){
					codes += "q350" + i + ",";
				}else{
					codes += "q35" + i + ",";
				}
			}
			if(1 == bo.isJieJia(Integer.parseInt(year), Integer.parseInt(month), i)){
				if(i < 10){
					codes += "q350" + i + ",";
				}else{
					codes += "q35" + i + ",";
				}
			}
		}
		String flag = "1";
		//ConstantXml constant = new ConstantXml(this.frameconn, "kq_monthly");
		//String defValue = constant.getNodeAttributeValue("/param/Kq_Parameters",
		//		"def_value"); 
		String defValue = bo.getParam(); //得到考勤默认值
		if(bo.IsFc(year, month)){
		    String userName = "";
			for(int i = 0 ; i < temps.length ; i++){
				//String a0100 = this.getFormHM().get("a0100").toString();
				String a0100 = temps[i];
				String [] userInfos = a0100.split("[/]");
				int date = Calendar.getInstance().get(Calendar.DATE);
				String dates = year + "-" + month + "-" + date + "";
				if(!"".equals(defValue.trim()) 
						&& null != defValue){
					if(this.isUser(userInfos[0],year,month,userInfos[1])){
						//得加设置考勤默认值的判断
						if(this.InsertPeopleToMonthKqTable(userInfos[0], userInfos[1], dates , codes)){
							isok = "引入人员成功!";
							flag = "2";
						}
					}else{
						//isok = userInfos[2] + "已经存在于考勤表中,不能继续引入!";
						userName += userInfos[2];
					}
				}else{
					isok = "请先设置考勤项目默认值!";
				}
			}
			if(!"".equals(userName)){
				isok  = userName + "已经存在于当月考勤表中,不能再次引入!";
			}
		}else{
			isok = "当月数据已经封存，无法引入人员!";
		}
		if("2".equals(flag)){
			bo.insertIntoKqDuration(year, month);
		}
		this.getFormHM().put("isok", isok);
	}
	
	//向月度考勤表中插入得到的当前引入人员信息
	public boolean InsertPeopleToMonthKqTable(String a0100 ,String nbase , String dates,String codes) {
		ArrayList list = new ArrayList();
		//得到设置的默认考勤选项
		//String currUser = this.getUserView().getDbname() + this.getUserView().getA0100();
		//ConstantXml constant = new ConstantXml(this.frameconn, "kq_monthly");
		//String defValue = constant.getNodeAttributeValue("/param/Kq_Parameters",
		//		"def_value");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		MonthKqBo bo = new MonthKqBo(this.frameconn);
		String defValue = bo.getParam();
		String [] userInfo = this.getInfoByUserCode(a0100, nbase);
		//System.out.println(userInfo.length);
		RecordVo vo = new RecordVo("Q35");
		vo.setInt("id", this.getMaxIdInQ35());
		vo.setString("b0110", userInfo[0]);
		vo.setString("e0122", userInfo[1]);
		vo.setString("e01a1", userInfo[2]);
		RecordVo param_vo=ConstantParamter.getConstantVo("SS_LOGIN");
		String str_dbpre=param_vo.getString("str_value").toUpperCase();
		DbNameBo b = new DbNameBo(this.getFrameconn());
		vo.setString("nbase", str_dbpre.substring(0,str_dbpre.length() -1 ));
		vo.setString("a0100", a0100);
		vo.setString("a0101", userInfo[3]);
		try {
			vo.setDate("q35z0", format.parse(dates));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		vo.setString("curr_user", this.userView.getDbname() + this.userView.getA0100());
		vo.setString("userflag", this.userView.getDbname() + this.userView.getA0100());
		vo.setString("status", "01");
		vo.setString("q3501", defValue);
		vo.setString("q3502", defValue);
		vo.setString("q3503", defValue);
		vo.setString("q3504", defValue);
		vo.setString("q3505", defValue);
		vo.setString("q3506", defValue);
		vo.setString("q3507", defValue);
		vo.setString("q3508", defValue);
		vo.setString("q3509", defValue);
		vo.setString("q3510", defValue);
		vo.setString("q3511", defValue);
		vo.setString("q3512", defValue);
		vo.setString("q3513", defValue);
		vo.setString("q3514", defValue);
		vo.setString("q3515", defValue);
		vo.setString("q3516", defValue);
		vo.setString("q3517", defValue);
		vo.setString("q3518", defValue);
		vo.setString("q3519", defValue);
		vo.setString("q3520", defValue);
		vo.setString("q3521", defValue);
		vo.setString("q3522", defValue);
		vo.setString("q3523", defValue);
		vo.setString("q3524", defValue);
		vo.setString("q3525", defValue);
		vo.setString("q3526", defValue);
		vo.setString("q3527", defValue);
		vo.setString("q3528", defValue);
		vo.setString("q3529", defValue);
		vo.setString("q3530", defValue);
		vo.setString("q3531", defValue);
		vo.setString("appuser", ";"+this.userView.getDbname()+this.userView.getA0100()+";");
		String [] code = codes.split(",");
		for(int i = 0 ; i < code.length ; i ++){
			vo.setString(code[i], null);
		}
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			//dao.insert(sb.toString(), list);
			dao.addValueObject(vo);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//根据人员编码得到相应信息
	public String[] getInfoByUserCode(String a0100,String nbase){
		String sql = "select b0110,e0122,e01a1,a0101 from "+nbase+"a01 where a0100 = '"+a0100+"'";
		String [] userInfo = new String[4];
		try {
			ContentDAO dao =  new ContentDAO(this.frameconn);
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				userInfo[0] = this.frowset.getString("b0110");
				userInfo[1] = this.frowset.getString("e0122");
				userInfo[2] = this.frowset.getString("e01a1");
				userInfo[3] = this.frowset.getString("a0101");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userInfo;
	}
	
	//引入人员之前判断人员是否已经存在于月度考勤表中
	public boolean isUser(String a0100, String year ,String month,String nbase){
		String sql = "select * from q35 where nbase = '"+nbase+"' and  a0100 = ('"+a0100+"') and "+Sql_switcher.year("q35z0")+" = '"+year+"' and "+Sql_switcher.month("q35z0")+" = '"+month+"'";
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			//System.out.println(sql);
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	
	public int getMaxIdInQ35(){
		int id = 0;
		String sql = "select max(id) as id from q35";
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				id = this.frowset.getInt("id") + 1;
			}else{
				id = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}
}
