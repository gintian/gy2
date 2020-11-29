package com.hjsj.hrms.transaction.train.station;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * <p>SetTrainStationTrans.java</p>
 * <p>Description:岗位培训指标xml解析</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2011-05-23 10:03:20</p>
 * @author LiWeichao
 * @version 5.0
 */
public class SetTrainStationTrans extends IBusiness {

	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList sel_nbase=new ArrayList();
		ConstantXml constantbo = new ConstantXml(this.getFrameconn(),"TR_PARAM");
		String nbase = constantbo.getTextValue("/param/post_traincourse/nbase");
		String emp_setid = constantbo.getTextValue("/param/post_traincourse/emp_setid");
		String emp_coursecloumn = constantbo.getTextValue("/param/post_traincourse/emp_coursecloumn");
		String post_setid = constantbo.getTextValue("/param/post_traincourse/post_setid");
		String post_coursecloumn = constantbo.getTextValue("/param/post_traincourse/post_coursecloumn");
		String emp_passcloumn = constantbo.getTextValue("/param/post_traincourse/emp_passcloumn");
		String emp_passvalues = constantbo.getTextValue("/param/post_traincourse/emp_passvalues");
		if(nbase!=null&&nbase.length()>0){
			String nbs[]=nbase.split(",");
			for(int i=0;i<nbs.length;i++){
				if(nbs[i]!=null&&nbs[i].length()>0){
					sel_nbase.add(nbs[i]);
				}
			}
		}
		this.getFormHM().put("emp_passvalues", emp_passvalues);
		this.getFormHM().put("emp_pssscloumn", emp_passcloumn);
		this.getFormHM().put("sel_nbase", sel_nbase);
		this.getFormHM().put("nbase_list", getDbpreList(dao));
		this.getFormHM().put("emp_list", getList(dao,1));
		this.getFormHM().put("post_list", getList(dao,3));
		this.getFormHM().put("emp_setid", emp_setid);
		this.getFormHM().put("emp_coursecloumn", emp_coursecloumn);
		this.getFormHM().put("post_setid", post_setid);
		this.getFormHM().put("post_coursecloumn", post_coursecloumn);
		
	}
	/**获取人员库列表*/
	private ArrayList getDbpreList(ContentDAO dao) {
		ArrayList dbList=new ArrayList();
		try{
			String sql="select pre,dbname from dbname order by dbid";
			this.frecset=dao.search(sql);
			while(this.frecset.next()){
				CommonData obj=new CommonData();
				String pre=this.frecset.getString("pre");
				pre=pre==null?"":pre;
				obj.setDataName(pre);
				obj.setDataValue(this.frecset.getString("dbname"));
				dbList.add(obj);
			}
		}catch (SQLException e) {
			e.fillInStackTrace();
		}
		return dbList;
	}
	
	/**获取子集*/
	private ArrayList getList(ContentDAO dao,int key){
		ArrayList list=new ArrayList();
		CommonData obj=new CommonData();
		obj.setDataName("#");
		obj.setDataValue("请选择...");
		list.add(obj);
		ArrayList fieldsetlist = DataDictionary.getFieldSetList(1, key);
		for (int i = 0; i < fieldsetlist.size(); i++) {
			FieldSet fieldset = (FieldSet)fieldsetlist.get(i);
			if("K00".equalsIgnoreCase(fieldset.getFieldsetid())||"K01".equalsIgnoreCase(fieldset.getFieldsetid()))
				continue;
			if("A00".equalsIgnoreCase(fieldset.getFieldsetid())||"A01".equalsIgnoreCase(fieldset.getFieldsetid()))
				continue;
			obj=new CommonData();
			obj.setDataName(fieldset.getFieldsetid());
			obj.setDataValue(fieldset.getCustomdesc());
			list.add(obj);
		}
		return list;
	}
}
