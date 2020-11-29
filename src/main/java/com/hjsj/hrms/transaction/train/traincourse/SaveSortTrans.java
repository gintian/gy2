package com.hjsj.hrms.transaction.train.traincourse;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * <p>Title:培训班</p>
 * <p>Description:保存培训班排序</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-12-13 下午06:07:55</p>
 * @author lilinbing
 * @version 4.0
 */
public class SaveSortTrans extends IBusiness {
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList subclass_value=(ArrayList)this.getFormHM().get("subclass_value");
		
		String tablename = (String)this.getFormHM().get("tablename");
		tablename=tablename!=null?tablename:"";
		
		ArrayList itemlist = new ArrayList();
		int vec[] = new int[subclass_value.size()];
		int n=0;
		for(int i=0;i<subclass_value.size();i++){
			String item = (String)subclass_value.get(i);
			item=item!=null?item:"";
			String[] item_arr = item.split("::");
			if(item_arr.length==2){
				itemlist.add(item_arr[0]);
				vec[n]=Integer.parseInt(item_arr[1]);
				n++;
			}
		}
		for (int i = 1; i < vec.length; i++) { 
		    int j = i; 
		    while (vec[j - 1] < vec[i]) {
		    	int a = vec[j];
		        vec[j] = vec[j - 1];
		        vec[j - 1]=a;
		        j--; 
		        if (j <= 0) { 
		        	break; 
		        } 
		    } 
		    vec[j] = vec[i]; 
		}
		for(int i=0;i<vec.length;i++){
			if(i+2>vec.length)
				break;
			if(vec[i]==0)
				vec[i]+=1;
			if(vec[i+1]<=vec[i]){
				vec[i+1]=vec[i]+1;
			}
		}
		ArrayList listvalue = new ArrayList();
		for(int i=0;i<itemlist.size();i++){
			ArrayList list = new ArrayList();
			list.add(vec[i]+"");
			list.add((String)itemlist.get(i));
			listvalue.add(list);
		}
		StringBuffer buf = new StringBuffer();
		buf.append("update "+tablename+" set I9999=? where "+tablename+"01=?");
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			dao.batchUpdate(buf.toString(),listvalue);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
