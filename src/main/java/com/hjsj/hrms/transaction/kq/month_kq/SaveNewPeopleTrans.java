package com.hjsj.hrms.transaction.kq.month_kq;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
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

public class SaveNewPeopleTrans extends IBusiness{

	public void execute() throws GeneralException {
			String flag = "1";
			String isok = "新建失败!";
			String year = (String)this.getFormHM().get("year");
			String month = (String)this.getFormHM().get("month");

			int date = Calendar.getInstance().get(Calendar.DATE);
			String dates = year +"-"+ month+"-" + date + "";
			
			//认证应用库
			RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
            String A01="";
            if(login_vo!=null) 
              A01 = login_vo.getString("str_value").toLowerCase();
			
			WeekUtils wk = new WeekUtils();
			MonthKqBo bo = new MonthKqBo(this.frameconn);
			String codes = "";
		   	Date dd = wk.lastMonth(Integer.parseInt(year),Integer.parseInt(month));
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
			
			String codeid = this.userView.getManagePrivCode();
			String codeValue = this.userView.getManagePrivCodeValue();
			ArrayList dbList = this.userView.getPrivDbList();
			//System.out.println(dbList);
			if(dbList.size() == 0){
				isok = "新建失败,您没有人员库权限!";
			}else{
				if(this.isUser(year, month)){
			for(int k = 0 ; k < dbList.size() ; k++){
				if(("," + A01.toLowerCase() + ",").indexOf(("," +dbList.get(k).toString().toLowerCase()+",")) !=-1){
					
			
			/*String [] A01s = A01.split(",");
			for(int k = 0 ; k < A01s.length ; k++){
				if(("," + this.userView.getPrivDbList() + ",").indexOf(("," +A01s[k]+",")) !=-1){
					System.out.println(A01s[k]);
				}
			}*/
			ArrayList list = this.getUserInfoByThisUser(codeid, codeValue,dbList.get(k).toString());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			//MonthKqBo bo = new MonthKqBo(this.frameconn);
			String defValue = bo.getParam();
			MonthKqBean bean = null;
			
			if(!"".equals(defValue.trim()) 
					&& null != defValue){
				
				for(int i = 0 ; i < list.size() ; i++){
					bean = (MonthKqBean)list.get(i);
					RecordVo vo = new RecordVo("Q35");
					vo.setInt("id", this.getMaxIdInQ35());
					vo.setString("b0110", bean.getBo110());
					vo.setString("e0122", bean.getE0122());
					vo.setString("e01a1", bean.getE01a1());
					vo.setString("nbase", dbList.get(k).toString());
					vo.setString("a0100", bean.getA0100());
					vo.setString("a0101", bean.getA0101());
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
					for(int j = 0 ; j < code.length ; j ++){
						vo.setString(code[j], null);
					}
					try {
						ContentDAO dao = new ContentDAO(this.frameconn);
						dao.addValueObject(vo);
						isok = "新建成功!";
						flag = "2";
					} catch (Exception e) {
						e.printStackTrace();
					}
					}
				}else{
					isok = "请先设置考勤默认值!";
				}
			}
				}
			} else {
				isok = "当月已经有数据存在于月度考勤表中,无法新建全部,请手工引入!";
			}
		}
			//如果新建成功 则同时在考勤期间表中建一条数据
			if("2".equals(flag)){
				bo.insertIntoKqDuration(year, month);
			}
			this.getFormHM().put("isok", isok);
	}
	
	//得到当前人员所在的管理范围内的人员信息
	public ArrayList getUserInfoByThisUser(String codeid,String codeValue , String nbase){
		ArrayList list = new ArrayList();
		StringBuffer sb = new StringBuffer();
		sb.append(" select ");
		sb.append(" b0110,e0122,e01a1,a0100,a0101 from "+nbase+"a01");
		sb.append(" where a0100 in ( ");
		sb.append(" select a0100 from "+nbase+"a01 ");
		if("UN".equalsIgnoreCase(codeid)){
			sb.append(" where  b0110 like '%");
			sb.append(codeValue+"%') ");
		}else if("UM".equalsIgnoreCase(codeid)){
			sb.append(" where  e0122 like '" + codeValue + "%')");
		}else if("@K".equalsIgnoreCase(codeid)){
			sb.append("  where e01a1 like '" + codeValue + "%')");
		}else{
			sb.append(" )");
		}
		MonthKqBean bean = null;
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			//System.out.println(sb.toString());
			this.frowset = dao.search(sb.toString());
			while(this.frowset.next()){
				bean = new MonthKqBean();
				bean.setBo110(this.frowset.getString("b0110"));
				bean.setE0122(this.frowset.getString("e0122"));
				bean.setE01a1(this.frowset.getString("e01a1"));
				bean.setA0100(this.frowset.getString("a0100"));
				bean.setA0101(this.frowset.getString("a0101"));
				list.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
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
	
	//引入人员之前判断当月是否已经有人存在于月度考勤表中
	public boolean isUser(String year ,String month){
		String codeid = this.userView.getManagePrivCode();
		String codeValue = this.userView.getManagePrivCodeValue();
		StringBuffer sb = new StringBuffer(" select id from q35 where ");
		sb.append(Sql_switcher.year("q35z0")+" = '" + year );
		sb.append("' and " + Sql_switcher.month("q35z0")+" = '");
		sb.append(month + " ' ");
		sb.append(" and (a0100 in (");
		sb.append("select a0100 from ");
		sb.append("usra01 where");
		if("UN".equalsIgnoreCase(codeid)){
			sb.append(" b0110 like '%");
			sb.append(codeValue+"%')) ");
		}else if("UM".equalsIgnoreCase(codeid)){
			sb.append(" e0122 like '" + codeValue + "%'))");
		}else if("@K".equalsIgnoreCase(codeid)){
			sb.append(" e01a1 like '" + codeValue + "%'))");
		}
		String sql = "select * from q35 where "+Sql_switcher.year("q35z0")+" = '"+year+"' and "+Sql_switcher.month("q35z0")+" = '"+month+"'";
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sb.toString());
			if(this.frowset.next()){
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
