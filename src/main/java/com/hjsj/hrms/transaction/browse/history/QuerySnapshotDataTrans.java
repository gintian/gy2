package com.hjsj.hrms.transaction.browse.history;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Title:QuerySnapshotDataTrans.java</p>
 * <p>Description>:QuerySnapshotDataTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Nov 19, 2010 5:18:34 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: LiWeichao</p>
 */
public class QuerySnapshotDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String right_fields="";
		String right_value="";
		String left_fields="";
		String left_value="";
		String chk="";
		Map chk_v=new HashMap();
		List chklist=new ArrayList();
		try {
			RowSet rs =dao.search("select str_value from Constant where Upper(Constant)='HISPOINT_PARAMETER'");
			if(rs.next()){
				ConstantXml xml = new ConstantXml(this.frameconn,"HISPOINT_PARAMETER","Emp_HisPoint");
				left_fields =xml.getTextValue("/Emp_HisPoint/Struct");
				right_fields =xml.getTextValue("/Emp_HisPoint/Query");
				chk =xml.getTextValue("/Emp_HisPoint/Base");
			}else{
				//设置的快照指标
				rs = dao.search("select str_value from Constant where Upper(Constant)='EMP_HISDATA_STRUCT'");
				if(rs.next())
					left_fields=rs.getString("str_value");
				//设置的查询指标
				rs=dao.search("select str_value from Constant where Upper(Constant)='EMP_HISDATA_QUERY'");
				if(rs.next())
					right_fields=rs.getString("str_value");
				//设置的人员库
				rs=dao.search("select str_value from Constant where Upper(Constant)='EMP_HISDATA_BASE'");
				if(rs.next())
					chk=rs.getString("str_value");
			}
			String sql = "select pre,dbname from dbname where pre in('###'";
			ArrayList dblist = userView.getPrivDbList();
			for(int i=0;i<dblist.size();i++){
				String pre = ((String)dblist.get(i)).toUpperCase();
				sql+=",'"+(String)dblist.get(i)+"'";
			}
			sql+=") order by dbid";
			rs=dao.search(sql);
			while(rs.next()){
				CommonData obj=new CommonData(rs.getString("pre"),rs.getString("dbname"));
				chklist.add(obj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		/*快照指标的显示*/
		if(left_fields==null||left_fields.length()<=0)
			left_fields="";		
		String[] leftss=left_fields.split(",");
		for(int m=0;m<leftss.length;m++){
			if(leftss[m].length()>0){
				FieldItem fi=DataDictionary.getFieldItem(leftss[m]);
				//zxj 20151221 需要进一步判断是否为人员主集或子集指标
				if(fi!=null  && fi.getFieldsetid().toUpperCase().startsWith("A"))
					left_value+=fi.getItemdesc()+"、";
			}
		}
		/*查询指标的显示*/
		if(right_fields==null||right_fields.length()<=0)
			right_fields="";		
		String[] rightss=right_fields.split(",");
		for(int m=0;m<rightss.length;m++){
			if(rightss[m].length()>0){
				FieldItem fi=DataDictionary.getFieldItem(rightss[m]);
				//zxj 20151221 需要进一步判断是否为人员主集或子集指标
				if(fi!=null && fi.getFieldsetid().toUpperCase().startsWith("A"))
					right_value+=fi.getItemdesc()+"、";
			}
		}
		/*人员库的显示*/
		if(chk==null||chk.length()<=0)
			chk="";
		String[] chks=chk.split(",");
		for(int m=0;m<chks.length;m++){
			if(chks[m].length()>0){
				chk_v.put(chks[m], "checked");
			}
		}
		
		this.getFormHM().put("right_fields",right_fields);
		this.getFormHM().put("right_value",right_value);
		this.getFormHM().put("left_fields",left_fields);
		this.getFormHM().put("left_value",left_value);
		this.getFormHM().put("chk_v", chk_v);
		this.getFormHM().put("chklist", chklist);
	}

}
