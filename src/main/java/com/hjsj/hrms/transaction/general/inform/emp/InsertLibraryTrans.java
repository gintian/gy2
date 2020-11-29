package com.hjsj.hrms.transaction.general.inform.emp;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class InsertLibraryTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String dbname=(String)this.getFormHM().get("dbname");
		dbname=dbname!=null&&dbname.trim().length()>0?dbname:"";
		
		String pre=(String)this.getFormHM().get("pre");
		pre=pre!=null&&pre.trim().length()>0?pre:"";
		
		String check=(String)this.getFormHM().get("check");
		check=check!=null&&check.trim().length()>0?check:"";
		
		String a0100=(String)this.getFormHM().get("a0100");
		a0100=a0100!=null&&a0100.trim().length()>0?a0100:"";
		
		DbNameBo dbnameBo = new DbNameBo(this.frameconn);
		String checkflag="no";
		
		String checkonly = "true";
		String[] a0100_arr = a0100.split(",");
		if(!checkMoveData(a0100_arr)){
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.review.not.shift")));
		}else{
			if(a0100_arr.length>0){
				ArrayList listvalue = new ArrayList();
				for(int i=0;i<a0100_arr.length;i++){
					if(a0100_arr[i]!=null&&a0100_arr[i].length()>0){
						String chk = dbnameBo.checkOnlyName(a0100_arr[i],dbname,pre);
						if(!"true".equalsIgnoreCase(chk)){
							if("true".equalsIgnoreCase(checkonly)){
								checkonly = "源库中"+chk+",不能移库";
							}else{
								checkonly += "\n"+"源库中"+chk+",不能移库";
							}
							continue;
						}
						String a0100str = dbnameBo.moveDataBetweenBase(a0100_arr[i],dbname,pre,check);
						if("1".equals(check)){
							ArrayList list = new ArrayList();
							list.add(a0100str);
							list.add(pre);
							list.add(a0100_arr[i]);
							list.add(dbname);
							listvalue.add(list);
						}
						checkflag="ok";
					}
				}
				if(listvalue!=null&&listvalue.size()>0)
					dbnameBo.updateSalaryPre(listvalue);
			}
		}
		if(!"true".equalsIgnoreCase(checkonly)){
			throw GeneralExceptionHandler.Handle(new Exception(checkonly));
		}
		this.getFormHM().put("checkflag",checkflag);
	}
	private boolean checkMoveData(String[] arr_a0100){
		boolean checkflag = true;
		ArrayList sp_falglist = sp_flagList(arr_a0100,"salaryHistory");
		for(int j=0;j<sp_falglist.size();j++){
			String sp_flag = (String)sp_falglist.get(j);
			if(sp_flag!=null&&sp_flag.trim().length()>0&& "0".equals(sp_flag)){
				checkflag = false;
				break;
			}
		}
		
		return checkflag;
	}
	public ArrayList sp_flagList(String[] arr_a0100,String tablename){
		ArrayList splist = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append("select sp_flag from ");
		buf.append(tablename);
		buf.append(" where A0100 in(");
		for(int i=0;i<arr_a0100.length;i++){
			String a0100 = arr_a0100[i];
			if(a0100!=null&&a0100.trim().length()>0){
				buf.append("'");
				buf.append(a0100);
				buf.append("',");
			}
		}
		buf.append("'aaaaaa')");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(buf.toString());
			while(this.frowset.next()){
				String sp_flag = this.frowset.getString("sp_flag");
				if(sp_flag!=null&&sp_flag.trim().length()>0){
					if("06".equals(sp_flag)){
						splist.add("1");
					}else{
						splist.add("0");
					}
				}	
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return splist;
	}
	
}
