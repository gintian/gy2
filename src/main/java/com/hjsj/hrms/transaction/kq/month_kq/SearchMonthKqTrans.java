package com.hjsj.hrms.transaction.kq.month_kq;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
/**
 * 进入月度考勤
 * <p>Title:SearchMonthKqTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:</p>
 * @author jinjiawei
 * @version 1.0
 * */
public class SearchMonthKqTrans extends IBusiness{
	
	
	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		
		String a_code = (String)hm.get("a_code"); //机构树 
		a_code = a_code!=null&&a_code.trim().length()>0?a_code:"";
		hm.remove("a_code");
		
		MonthKqBo bo = new MonthKqBo(this.frameconn);
		//this.userView.hasTheFunction(strfuncid)    //登录用户是否有某个功能号的权限 限制起草状态时用
		//String s = this.userView.getManagePrivCode();//得到登录人的管理范围
		//String s1 = this.userView.getManagePrivCodeValue();
		WeekUtils wk = new WeekUtils();
		String codeid = this.userView.getManagePrivCode();
		String year = (String)this.getFormHM().get("years");
		if("".equals(year) || null == year){
			year = Calendar.getInstance().get(Calendar.YEAR) + "";
		}
		String month =(String)this.getFormHM().get("months");
		if("".equals(month) || month == null){
			month = Calendar.getInstance().get(Calendar.MONTH)+1 + "";
		}
		String codeValue = this.userView.getManagePrivCodeValue();
		Date date = null;
		//获取当前日期(年、月) 所在的月份的最后一日
		if(null != this.getFormHM().get("type")){
			if("change".equals(this.getFormHM().get("type").toString())){				
				
				//this.getFormHM().remove("years");
				
				//this.getFormHM().remove("months");
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				try {
					date = wk.lastMonth(Integer.parseInt(year), Integer.parseInt(month));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
					date = wk.lastMonth(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH)+1);
			}
		}
		String codes = "";
		//System.out.println(date.getDate());
		for(int i = 1 ; i <=date.getDate() ; i ++){
			ManagePrivCode mc = new ManagePrivCode(this.userView,this.getFrameconn());
			
			//判断这一年的这一个月的这一天 是否是周六周日
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
		if(codes.length() >= 1){
			codes = codes.substring(0,codes.length()-1);
		}
		//String [] code = codes.split(",");
		ArrayList list = DataDictionary.getFieldList("Q35", Constant.USED_FIELD_SET);
		
		int clos = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("Select ");
		String columns = "id,a0100,a0101,status,";
		StringBuffer where = new StringBuffer();
		MonthKqBean beans = null;
		ArrayList list2 = new ArrayList();
		boolean flag = true;
		for(int i = 0 ; i < list.size(); i++){
			FieldItem item = (FieldItem)list.get(i);
			String temptempid = item.getItemid();
			if("appuser".equalsIgnoreCase(temptempid)){
				continue;
			}
			beans = new MonthKqBean();				//这里用一个对象代替item  想使用item中的属性 可在对象中动态添加即可
			beans.setState(item.getState());
			beans.setItemid(item.getItemid());
			beans.setItemdesc(item.getItemdesc());
			beans.setItemtype(item.getItemtype());
			beans.setCodesetid(item.getCodesetid());
			beans.setIsshow("no");					//no代表不是周六周末 yes代表是周六周末 为在前台进行控制
			if("1".equals(beans.getState().trim())){//如果设置的是显示的指标
				if(31 == date.getDate()){	
					if("a0101".equalsIgnoreCase(beans.getItemid())){
						flag = false;
					}
					if(flag){
						beans.setIsSuoDing("yes");
					}//如果当前月的最后一天等于三十一号
					columns += beans.getItemid() + ",";
					clos ++;
					//for(int j = 0 ; j < code.length ; j++){
					//	if(beans.getItemid().equalsIgnoreCase(code[j])){
					if((","+codes.toUpperCase()+",").indexOf((","+beans.getItemid().toUpperCase()+",")) != -1){						
						beans.setIsshow("yes");
					}
					//	}
					//}
					list2.add(beans);
				}else if(30 == date.getDate()){
					if("a0101".equalsIgnoreCase(beans.getItemid())){
						flag = false;
					}
					if(flag){
						beans.setIsSuoDing("yes");
					}
					if(!"Q3531".equalsIgnoreCase(beans.getItemid())){
						columns += beans.getItemid() + ",";
						clos ++;
						//for(int j = 0 ; j < code.length ; j++){
						//	if(beans.getItemid().equalsIgnoreCase(code[j])){
						if((","+codes.toUpperCase()+",").indexOf((","+beans.getItemid().toUpperCase()+",")) != -1){		
								beans.setIsshow("yes");
							}
					//	}
						list2.add(beans);
					}
				}else if(29 == date.getDate()){
					if("a0101".equalsIgnoreCase(beans.getItemid())){
						flag = false;
					}
					if(flag){
						beans.setIsSuoDing("yes");
					}
					if(!"Q3531".equalsIgnoreCase(beans.getItemid()) &&
							!"Q3530".equalsIgnoreCase(beans.getItemid())){
						columns += beans.getItemid() + ",";
						clos ++;
						//for(int j = 0 ; j < code.length ; j++){
						//	if(beans.getItemid().equalsIgnoreCase(code[j])){
						if((","+codes.toUpperCase()+",").indexOf((","+beans.getItemid().toUpperCase()+",")) != -1){		
								beans.setIsshow("yes");
							}
						//}
						list2.add(beans);
					}
				}else if(28 == date.getDate()){
					if("a0101".equalsIgnoreCase(beans.getItemid())){
						flag = false;
					}
					if(flag){
						beans.setIsSuoDing("yes");
					}
					if(!"Q3531".equalsIgnoreCase(beans.getItemid()) &&
							!"Q3530".equalsIgnoreCase(beans.getItemid()) &&
							!"q3529".equalsIgnoreCase(beans.getItemid())){
						columns += beans.getItemid() + ",";
						clos ++;
						//for(int j = 0 ; j < code.length ; j++){
						//	if(beans.getItemid().equalsIgnoreCase(code[j])){
						if((","+codes.toUpperCase()+",").indexOf((","+beans.getItemid().toUpperCase()+",")) != -1){		
								beans.setIsshow("yes");
							}
					//	}
						list2.add(beans);
					}
				}
			}
		}
		columns = columns.substring(0,columns.length()-1); //去掉最后一个逗号
		sql.append(columns);
		where.append(" from q35 ");
		if(null != this.getFormHM().get("type")){
		String type = this.getFormHM().get("type").toString();
			if("change".equals(type)){
				//String year = this.getFormHM().get("years").toString();
				this.getFormHM().remove("years");
				//String month =this.getFormHM().get("months").toString();
				//this.getFormHM().remove("months");
				this.getFormHM().put("years", year);
				
				this.getFormHM().put("months", month);
				
				//where.append("where year(q35z0) = ");
				where.append(" where " + Sql_switcher.year("q35z0") + "=");
				where.append(year + " and ");
				where.append(Sql_switcher.month("q35z0") + "=");
				//where.append("month(q35z0) = ");
				where.append(month);
			}else{
				this.getFormHM().remove("years");
				this.getFormHM().remove("months");
				this.getFormHM().put("years", Calendar.getInstance().get(Calendar.YEAR)+"");
				this.getFormHM().put("months", Calendar.getInstance().get(Calendar.MONTH)+1+"");
				
				where.append(" where " + Sql_switcher.year("q35z0") + "=");
				where.append(Calendar.getInstance().get(Calendar.YEAR) + " and ");//Calendar.getInstance().get(Calendar.YEAR) int
				where.append(Sql_switcher.month("q35z0") + "=");
				where.append(Calendar.getInstance().get(Calendar.MONTH)+1);
			}
		}else{ //首次进入月度考勤表 取当前年月
			//where.append("where year(q35z0) = ");
			where.append(" where "+Sql_switcher.year("q35z0") + "=");
			where.append(Calendar.getInstance().get(Calendar.YEAR) + " and ");//Calendar.getInstance().get(Calendar.YEAR) int
			//where.append("month(q35z0) = ");
			where.append(Sql_switcher.month("q35z0") + "= ");
			where.append(Calendar.getInstance().get(Calendar.MONTH)+1);
		}
		if(!"".equals(a_code) || 
				null != a_code){
			if(a_code.length() > 2){
				codeid = a_code.substring(0,2);
				codeValue = a_code.substring(2,a_code.length());
				
				where.append(" and (a0100 in (");
				where.append("select a0100 from ");
				where.append("usra01 where");
				if("UN".equalsIgnoreCase(codeid)){
					where.append(" b0110 like '%");
					where.append(codeValue+"%')) ");
				}else if("UM".equalsIgnoreCase(codeid)){
					where.append(" e0122 like '" + codeValue + "%'))");
				}else if("@K".equalsIgnoreCase(codeid)){
					where.append(" e01a1 like '" + codeValue + "%'))");
				}
			}
		}
		
		//where.append(" and (a0100 in (");
		//where.append("select a0100 from ");
		//where.append("usra01 where");
		//if(codeid.equalsIgnoreCase("UN")){				
		//	where.append(" b0110 like '%");
		//	where.append(codeValue+"%') ");
		//}else if(codeid.equalsIgnoreCase("UM")){
		//	where.append(" e0122 like '" + codeValue + "%')");
		//}else if(codeid.equalsIgnoreCase("@K")){
		//	where.append(" e01a1 like '" + codeValue + "%')");
		//}else{
			//where.append(" 1=1");
		//	where.append(" )");
		//}
		//if(this.userView.hasTheFunction("0AC020106")//3238110106  3238110107
		//		|| this.userView.hasTheFunction("0AC020107")){ //如果有封存和解封的按钮权限 则应该看到当月所有的记录
		//	where.append(" and 1=1 ");
		//}else{
			if(!this.userView.hasTheFunction("0AC020108")){ //如果没有人员引入按钮的权限 则不显示起草的记录3238110108
				where.append(" and status not in ('01') ");
			}
			where.append(" and (curr_user = '");
			where.append(this.userView.getDbname() + this.userView.getA0100() +"'");
			where.append(" or appuser like '%;");
			where.append(this.userView.getDbname() + this.userView.getA0100() + ";%')");
		//}
		//设置的认证应用库
		RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
        String nbase = "";
		String A01="";
        if(login_vo!=null) 
          A01 = login_vo.getString("str_value").toLowerCase();
        ArrayList dbList = this.userView.getPrivDbList(); //权限范围内的库
        if(dbList.size() > 0 ){
        	for(int k = 0 ; k < dbList.size() ; k++){
        		//如果既是设置的认证应用库 又是当前登录用户权限范围内的库
				if(("," + A01.toLowerCase() + ",").indexOf(("," +dbList.get(k).toString().toLowerCase()+",")) !=-1){
						nbase += dbList.get(k).toString() + ",";
				}
        	}
        }
        nbase = nbase.substring(0,nbase.length()-1); //截取最后一个逗号
		this.getFormHM().put("nbase", nbase); //设置的认证应用库 前台显示用的
		//System.out.println(sql + where.toString());
		this.getFormHM().put("riqi", date.getDate() + "");
		this.getFormHM().put("list", list2);
		this.getFormHM().put("sql", sql.toString());
		this.getFormHM().put("where", where.toString());
		this.getFormHM().put("cols", columns);
		this.getFormHM().put("clos", clos+""); //动态传递 所需要合并的列数
		this.getFormHM().put("list2", getCorCode()); 
		this.getFormHM().put("isShowButton", this.isShowButton()); //是否显示报批批准按钮 如果有直接上级 显示报批按钮 没有直接上级 显示批准按钮 再加上功能权限控制 双层控制
		this.getFormHM().put("yearList", this.getMonthKqYears(Calendar.getInstance().get(Calendar.YEAR), codeid, codeValue));
		ArrayList monthList = new ArrayList();
		
		for(int i = 1 ; i <= 12 ; i ++){
			beans = new MonthKqBean();
			beans.setMonths(i);
			monthList.add(beans);
		}
		this.getFormHM().put("monthList", monthList);
	}
	
	//得到前台div中表所显示的信息
	public ArrayList getCorCode(){
		ArrayList list = new ArrayList();
		MonthKqBean bean = null;
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		//String sql = "select codeitemid,corcode,codeitemdesc from codeitem where codesetid = '27' and invalid='1'";
		String sql = "select item_id , item_name,item_symbol from kq_item order by displayorder";
		try {
			rs = dao.search(sql);
			while(rs.next()){
				bean = new MonthKqBean();
				//if(null != rs.getString("codeitemid") ){					
				if(null != rs.getString("item_id") ){	
					bean.setItemid(rs.getString("item_id"));
					//if(null != rs.getString("corcode")){						
					//	bean.setCorcode(rs.getString("corcode") + rs.getString("codeitemdesc")); //id + corcode
					//}else{
					
					if(rs.getString("item_symbol") == null
							|| "".equals(rs.getString("item_symbol").trim())){
						bean.setCorcode(rs.getString("item_name") + " " + "");
					}else{
						bean.setCorcode(rs.getString("item_name") + " " + rs.getString("item_symbol"));
					}
					list.add(bean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	//得到考勤表中存在的年份
	public ArrayList getMonthKqYears(int years,String codeid,String codeValue){
		StringBuffer sb = new StringBuffer();
		//sb.append("select distinct year(q35z0) as year ");
		sb.append("select distinct ");
		sb.append(Sql_switcher.year("q35z0") + " as year ");
		sb.append(" from q35 where a0100 in (");
		sb.append("select a0100 from ");
		sb.append("usra01 ");
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
		sb.append(" order by year desc");
		ArrayList list = new ArrayList();
		MonthKqBean beans = null;
		try {
			if(this.isInMonthKqTable()){
				beans = new MonthKqBean();
				beans.setYears(years);
				list.add(beans);
			}
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sb.toString());
				while(this.frowset.next()){
					beans = new MonthKqBean();
					int year = this.frowset.getInt("year");
					if( 0 != year){ //如果在q35表中年月标识是null 的话 取出来转换则是0 
						beans.setYears(year);
						list.add(beans);
					}
				}
				if(list.size() == 0){
					beans = new MonthKqBean();
					beans.setYears(Calendar.getInstance().get(Calendar.YEAR));
					list.add(beans);
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	//判断当前年份有没有在月度考勤表中有数据
	public boolean isInMonthKqTable(){
		//String sql = "select * from q35 where year(q35z0) = '"+Calendar.getInstance().get(Calendar.YEAR)+"'";
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from q35 ");
		sb.append(" where " + Sql_switcher.year("q35z0") + "= ");
		sb.append(Calendar.getInstance().get(Calendar.YEAR));
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
	
	//判断是否显示报批 批准按钮的权限
	public String isShowButton(){
		//ConstantXml constant = new ConstantXml(this.frameconn, "kq_monthly");
		//String relation = constant.getNodeAttributeValue("/param/Kq_Parameters",
		//		"sp_relation");
		MonthKqBo bo = new MonthKqBo(this.frameconn);
		String relation = bo.getParam1();
		if(null == relation){
			relation = "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append(" select mainbody_id from t_wf_mainbody ");
		sb.append(" where sp_grade = '9'");
		sb.append(" and (object_id = '");
		sb.append(this.userView.getDbname() + this.userView.getA0100() + "' or ");
		sb.append(" object_id = '");
		sb.append(this.userView.getUserName() + "')");
		sb.append(" and relation_id = '");
		sb.append(relation + "'");
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sb.toString());
			if(this.frowset.next()){
				return "false";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "true";
	}
}
