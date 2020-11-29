package com.hjsj.hrms.module.recruitment.resumecenter.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * <p>Title:TurnTalentsTrans</p>
 * <p>Description:转人才库</p>
 * <p>Company:hjsj</p>
 * <p>create time:2015-01-30</p>
 * @author wangcq
 * @version 1.0
 */
public class TurnTalentsTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		Boolean result = Boolean.FALSE;
		try{
			ContentDAO dao=new ContentDAO(this.frameconn);
			String username = this.userView.getUserName();
			String fullname = this.userView.getUserFullName();
			String busi = this.userView.getUnitIdByBusi("7");   //获取用户的操作单位
			if(busi.length() > 0)
			    busi = PubFunc.getTopOrgDept(busi);
			if(busi!=null&&busi.length()>0){
				String[] busis = busi.split("`");
				busi = "";
				for(int i=0;i<busis.length;i++)
				{
					busi+=busis[i].substring(2)+"`";
				}
				busi = busi.substring(0, busi.length()-1);
			}
			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			int[] count = {};
			ArrayList list = new ArrayList();   //用于插入操作
			ArrayList array = (ArrayList)this.getFormHM().get("array"); 
			StringBuffer insertStr = new StringBuffer("");
			insertStr.append("insert into zp_talents(a0100,nbase,b0110,create_time,create_user,create_fullname) values(?,?,?,?,?,?)");
			String exitTrans ="";
			int res=0;
			int num = 0;
			deleteRepetition(array);
			for(int i=0; i<array.size(); i++){
				if(array.get(i)==null)
					continue;
				ArrayList param = (ArrayList)array.get(i);
				String a0100 = (String)param.get(0);
				a0100 = PubFunc.decrypt(a0100);
				String nbase = (String)param.get(1);
				String a0101 = (String)param.get(2);
				nbase = PubFunc.decrypt(nbase);
				if(!existTalent(dao,a0100,nbase,username)){
					ArrayList values = new ArrayList();
					values.add(a0100);
					values.add(nbase);
					values.add(busi);
					values.add(java.sql.Date.valueOf(df.format(new Date())));
					values.add(username);
					values.add(fullname);
					list.add(values);
					res++;
				}else{
					if(num<5){
						exitTrans+=a0101+",";
						num++;
					}else{
		    			num++;
		    		}
				}
			}
			if(num>=5)
			{
				exitTrans=exitTrans.substring(0,exitTrans.length()-1)+"等"+num+"人";
			}
			if(list.size()>0)   //选中数据在人才库中都没有才进行操作
				count = dao.batchUpdate(insertStr.toString(), list);
			if(count.length > 0){
				result = Boolean.TRUE;
				this.getFormHM().put("info",SafeCode.encode("已成功转入人才库！"));
			}
			this.getFormHM().put("res",res);
			if(exitTrans.length()>1)
				this.getFormHM().put("exitTrans",exitTrans.substring(0, exitTrans.length()-1));
		}catch(Exception e){
			e.printStackTrace();
			result = Boolean.FALSE;
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			this.getFormHM().put("result", result);
		}
		
	}
	
	//判断人才库中是否有相应人员
	public boolean existTalent(ContentDAO dao, String a0100, String nbase, String username){
		RowSet recset=null;
		boolean exist = false;
		StringBuffer sqlstr = new StringBuffer();
		ArrayList list = new ArrayList();
		list.add(a0100);
		list.add(nbase);
		list.add(username);
		sqlstr.append("select * from zp_talents where a0100=? and nbase=? and create_user=?");
		try {
			recset = dao.search(sqlstr.toString(),list);
			exist = recset.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(recset);
		}
		return exist;
	}
	
	/**
	 * 去除重复人员id
	 * @param array
	 */
	private void deleteRepetition(ArrayList array){
		ArrayList<String> list = new ArrayList<String>();
		for(int i=array.size()-1;i>=0;i--) {
			ArrayList param = (ArrayList)array.get(i);
			if(param == null) {
				continue;
			}
			String a0100 = (String)param.get(0);
			if(list.contains(a0100)) {
				array.remove(i);
			}else {
				list.add(a0100);
			}
		}
	}

}
