package com.hjsj.hrms.transaction.train.resource.trainroom;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class TrainRoomEditTrans extends IBusiness {

	public void execute() throws GeneralException {
		String state = (String)this.getFormHM().get("state");
		String str = (String)this.getFormHM().get("str");
		String declare = (String)this.getFormHM().get("declare");
		String msg = "";
		if(str==null||str.length()<1)
			return;
		
		if("app".equals(state))
			state = "03";
		else
			state = "07";
		
		StringBuffer sql = new StringBuffer();
		ArrayList list = new ArrayList();
		String[] _str = str.split(",");
		if(IsCheck(_str,state)){
			for (int i = 0; i < _str.length; i++) {
				String[] _v = _str[i].split("`");
				String r1001 = PubFunc.decrypt(SafeCode.decode(_v[0]));
				String nbase = PubFunc.decrypt(SafeCode.decode(_v[1]));
				String a0100 = PubFunc.decrypt(SafeCode.decode(_v[2]));
				sql.setLength(0);
				sql.append("update r61 set r6111='");
				sql.append(state);
				sql.append("',r6107='");
				sql.append(userView.getUserFullName());
				if(declare!=null&&declare.trim().length()>0)
					sql.append("',r6113='"+SafeCode.decode(declare.trim()));
				sql.append("',r6109="+Sql_switcher.dateValue(DateUtils.FormatDate(new Date(),"yyyy-MM-dd")));
				sql.append(" where r1001='");
				sql.append(r1001);
				sql.append("' and nbase='");
				sql.append(nbase);
				sql.append("' and a0100='");
				sql.append(a0100);
				sql.append("' and r6101=");
				sql.append(Sql_switcher.dateValue(_v[3].length()==16?_v[3]+":00":_v[3]));
				sql.append(" and r6103=");
				sql.append(Sql_switcher.dateValue(_v[4].length()==16?_v[4]+":00":_v[4]));
				if(("03").equals(state)&&IsRepeat(r1001,nbase,a0100,_v[3], _v[4])){
					list.add(sql.toString());
				}else if(("07").equals(state)){//判断是否为驳回状态
					list.add(sql.toString());
				}
			}
		
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			if(list != null && list.size() > 0){				
				dao.batchUpdate(list);
			}else{
				msg = "当前教室该时间段已被占用!" ;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			this.getFormHM().put("msg", msg);
		}
		}else{
			msg = "教室占用时间产生冲突,请重新审批!";
		}
		this.getFormHM().put("msg", msg);
	}
	
	//判断当前时间当前教室是否已被占用
	private boolean IsRepeat(String r1001 , String nbase , String a0100 ,String startTime , String endTime ){
		boolean flag = true;
		StringBuffer sql = new StringBuffer();
		String st = Sql_switcher.dateValue(startTime.length()==16?startTime+":00":startTime);
		String et = Sql_switcher.dateValue(endTime.length()==16?endTime+":00":endTime);
		sql.append("select * from r61 where ((r6101 >= "+st+" and r6101 <= "+et+" and r6103 >= "+et+")");
		sql.append(" or (r6101 <= "+st+" and r6103 >= "+st+" and r6101 <= "+et+" and r6103 >= "+et+")");
		sql.append(" or (r6101 <= "+st+" and r6103 >= "+st+" and r6103 <= "+et+"))");
		sql.append(" and r1001='");
		sql.append(r1001);
		sql.append("' and r6111 = '03'");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql.toString());
			if(this.frowset.next()){
				flag = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	private boolean IsCheck(String [] str,String state){
		boolean flag = true;
		if("07".equals(state))
			return flag;
		
		for(int i = 0 ; i < str.length ; i ++){
			String[] strs = str[i].split("`");
			for(int j = i+1 ;  j < str.length; j ++){
				String[] strss  = str[j].split("`");
				int res = strs[3].compareTo(strss[3]); //第一个开始时间和下一个开始时间比
				int res1 = strs[4].compareTo(strss[3]); //第一个结束时间和下一个开始时间比
				int res2 = strs[4].compareTo(strss[4]); //第一个结束时间和下一个结束时间比
				int res3 = strs[3].compareTo(strss[4]);
			
				if( res <= 0 && res1 > 0 && res2 <= 0 && res3 < 0 ){ //第一种情况 开始时间大于开始时间小于结束时间 结束时间小于结束时间大于开始时间
					flag = false;
				}else if(res >= 0 && res3 < 0 && res2 <= 0){		   //第二种情况 开始时间小于开始时间大于结束时间 结束时间小于结束时间
					flag = false;
				}else if(res <= 0 && res3 > 0 && res1 > 0 && res2 <= 0){ //第三种情况 开始时间小于开始时间大于结束时间 结束时间小于开始时间大于结束时间
					flag = false;
				}else if(res <= 0 && res3 > 0 && res1 < 0 && res2 <= 0){ //第四种情况 开始时间大于开始时间 小于结束时间 结束时间小于结束时间
					flag = false;
				}
			}
		}
		return flag;
	}
}