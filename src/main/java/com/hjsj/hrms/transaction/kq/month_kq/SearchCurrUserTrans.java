package com.hjsj.hrms.transaction.kq.month_kq;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SearchCurrUserTrans extends IBusiness{

	public void execute() throws GeneralException {
		String codes = (String)this.getFormHM().get("codes");
		ArrayList usersList = new ArrayList();
		if(null != codes && !"".equals(codes.trim())){
			MonthKqBean beans = null;
			String [] code = codes.split(",");
			for(int i = 0 ; i < code.length ; i ++){
				beans = new MonthKqBean();
				//判断审批关系中传过来的是USR00000001类型还是SU用户类型
				if(code[i].length() > 3){					
					String nbase = code[i].substring(0,3);//应用库 此处需要通过该应用库查找是否存在当前应用库
					if(this.isNbase(nbase)){						
						String userCode = code[i].substring(3,code[i].length());//人员编码
						String userNames = this.getUserNameByCode(nbase, userCode);
						if(!"".equals(userNames.trim())){						
							beans.setItemdesc(userNames);
							beans.setItemid(code[i]);
							usersList.add(beans);
						}else{//如果通过截取字符串没有找到人员信息
							beans.setItemdesc(code[i]);
							beans.setItemid(code[i]);
							usersList.add(beans);
						}
					}else{
						beans.setItemdesc(code[i]);
						beans.setItemid(code[i]);
						usersList.add(beans);
					}
				}else{//如果审批关系中存的是用户 则通过用户查找当前审批人信息
					beans.setItemdesc(code[i]);
					beans.setItemid(code[i]);
					usersList.add(beans);
				}
			}
		}
		this.getFormHM().put("currUserList", usersList);
	}
	
	//通过人员编码以及应用库得到人员姓名
	public String getUserNameByCode(String nbase , String userCode){
		String userName = "";
		String sql = " select a0101 from usra01 where a0100 = '"+userCode+"'";
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				userName = this.frowset.getString("a0101");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userName;
	}
	//通过截取的前三位判断是否存在当前应用库
	public boolean isNbase(String nbase){
		String sql = " select * from dbname where pre = '"+nbase+"' ";
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
