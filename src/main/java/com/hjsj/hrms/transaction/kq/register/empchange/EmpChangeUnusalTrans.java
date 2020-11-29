package com.hjsj.hrms.transaction.kq.register.empchange;

import com.hjsj.hrms.businessobject.kq.register.Employ_Change;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Date;

/**
 * <p>
 * Title:EmpChangeUnusalTrans
 * </p>
 * <p>
 * Description:查询比对结果的特殊情况
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-05-12
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 *  
 */
public class EmpChangeUnusalTrans extends IBusiness{
   public void execute() throws GeneralException {    
	    
		String	code=RegisterInitInfoData.getKqPrivCodeValue(userView);
		   
        ArrayList addlist= new ArrayList();
        addlist = getDataList(addlist,3);   
        addlist = getDataList(addlist,2);
        addlist = getDataList(addlist,4);
        this.getFormHM().put("changelist",addlist);       
        this.getFormHM().put("code",code);        
        if(addlist!=null&&addlist.size()>0){
           this.getFormHM().put("changestatus","3");  
           this.getFormHM().put("unusal_count",addlist.size()+"");
        }else{
           this.getFormHM().put("changestatus","3");
           this.getFormHM().put("unusal_count","0");
        }
        
   }  
   
   /**
    * 根据状态查询异常数据
    * @param list ArrayList
    * @param status String 状态
    * @return ArrayList<RecordVo>
    */
   private ArrayList getDataList(ArrayList list,int status) {
	   ArrayList kq_dbase_list = userView.getPrivDbList();	
	   DBMetaModel model = new DBMetaModel(this.frameconn);
		model.reloadTableModel("kq_employ_change");
	   for(int i = 0; i < kq_dbase_list.size(); i++) {
		   
		   String userbase = kq_dbase_list.get(i).toString();
		   String whereIN = RegisterInitInfoData.getWhereINSql(userView,userbase);
		   String sql = Employ_Change.getDateSqlAdd(userbase, whereIN, status);
		   
		   ContentDAO dao = new ContentDAO(this.getFrameconn());
		   
		   try {
			   this.frowset=dao.search(sql); 
			   
			   while(this.frowset.next()) {
	    	       RecordVo vo = new RecordVo("kq_employ_change");
	    	       vo.setString("nbase", this.frowset.getString("nbase"));
	    	       vo.setString("a0100", this.frowset.getString("A0100"));
	    	       vo.setString("b0110", this.frowset.getString("B0110"));
	    	       vo.setString("e0122", this.frowset.getString("E0122"));
	    	       vo.setString("a0101", this.frowset.getString("A0101"));
	    	       vo.setString("e01a1", this.frowset.getString("E01A1"));
	    	       vo.setString("flag", this.frowset.getString("flag"));
	    	       vo.setInt("status", this.frowset.getInt("status"));
	    	       Date change_D=this.frowset.getDate("change_date");     	          	           	       
	    	       vo.setDate("change_date",change_D);
	    	       Date change_end = this.frowset.getDate("change_end_date");     	          	           	       
	    	       vo.setDate("change_end_date",change_end);
	    	       list.add(vo);
			   }
		   }catch(Exception ee) {
			   ee.printStackTrace();
		   } 
       }
	   
	   return list;
	   
   }
   
}
