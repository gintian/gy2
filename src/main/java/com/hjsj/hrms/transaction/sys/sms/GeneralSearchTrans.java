package com.hjsj.hrms.transaction.sys.sms;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class GeneralSearchTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
		String flag = (String)reqhm.get("flag");//考试计划应用=exam
		reqhm.remove("flag");
		String tablename = (String)reqhm.get("tablename");
		tablename=tablename!=null&&tablename.trim().length()>0?tablename:"";
		reqhm.remove("tablename");

		ArrayList setlist = new ArrayList();
		ArrayList fieldsetlist = new ArrayList();
		String fieldsetid="0";
		String fieldsetdesc="";
    	setlist=this.userView.getPrivFieldSetList(Constant.USED_FIELD_SET);
	    for(int i=0;i<setlist.size();i++){
		    FieldSet fieldset = (FieldSet)setlist.get(i);
		    if("0".equalsIgnoreCase(fieldset.getUseflag()))
		     	continue;
		    if("A00".equalsIgnoreCase(fieldset.getFieldsetid()))
		    	continue;	
		    if("B00".equalsIgnoreCase(fieldset.getFieldsetid()))
		    	continue;
			if("K00".equalsIgnoreCase(fieldset.getFieldsetid()))
			    continue;
		    CommonData obj=new CommonData(fieldset.getFieldsetid(),fieldset.getCustomdesc());
		    fieldsetlist.add(obj);
	    }
	    if(reqhm.get("fieldsetid")!=null&&!"".equals((String)reqhm.get("fieldsetid")))
	    {
			fieldsetid=(String)reqhm.get("fieldsetid");
			FieldSet ff=DataDictionary.getFieldSetVo(fieldsetid);
			fieldsetdesc=ff.getCustomdesc();
			reqhm.remove("fieldsetid");
		}
		this.getFormHM().put("setList",fieldsetlist);
		this.getFormHM().put("tablename",tablename);
		this.getFormHM().put("fieldSetId", fieldsetid);
		this.getFormHM().put("fieldSetDesc", fieldsetdesc);
		this.getFormHM().put("preList", this.getPrivDbname(flag));
		this.getFormHM().put("t_flag", flag);//培训计划
	}
	
	/**
	 * 获得权限内的人员库
	 * @return
	 */
	private ArrayList getPrivDbname(String flag) throws GeneralException{
		ArrayList list = new ArrayList();
		List pdList = this.userView.getPrivDbList();
		StringBuffer buff = new StringBuffer();
		
		ArrayList sel_nbase=new ArrayList();
		if("exam".equals(flag)){
			//培训参数中人员库
			 ConstantXml constantbo = new ConstantXml(this.getFrameconn(),"TR_PARAM");
			 String tmpnbase = constantbo.getTextValue("/param/post_traincourse/nbase");
			 if(tmpnbase!=null&&tmpnbase.length()>0){
				 String nbs[]=tmpnbase.split(",");
				 for(int i=0;i<nbs.length;i++){
					 if(nbs[i]!=null&&nbs[i].length()>0){
						 sel_nbase.add(nbs[i]);
					 }
				 }
			 }else{
				 //如果没有设置人员库，则提示错误信息
				 throw GeneralExceptionHandler.Handle(new Exception("未设置人员库！<br><br>请到   培训管理>参数设置>其它参数>岗位培训指标设置   中设置人员库。"));
			 }
		}
		
		for (int i = 0; i < pdList.size(); i++) {
			buff.append(",");
			buff.append(pdList.get(i));
		}
		if (buff.length() > 0) {
			StringBuffer sql = new StringBuffer();
			sql.append("select pre,dbname from dbname");
			ContentDAO dao = new ContentDAO(this.frameconn);
			try {
				this.frowset = dao.search(sql.toString());
				String priv = buff.substring(1);
				while (frowset.next()) {
					String pre = frowset.getString("pre");
					String dbn = frowset.getString("dbname");
					if (this.userView.isSuper_admin()) {
						if("exam".equals(flag)&&!sel_nbase.contains(pre))//培训参数没有的不要
							continue;
						CommonData obj=new CommonData(pre,dbn);
						list.add(obj);
					} else {
						if (priv.toLowerCase().indexOf(pre.toLowerCase()) != -1) {
							if("exam".equals(flag)&&!sel_nbase.contains(pre))//培训参数没有的不要
								continue;
							CommonData obj=new CommonData(pre,dbn);
							list.add(obj);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} 
		if (list.size() == 0) {
			throw GeneralExceptionHandler.Handle(new Exception("没有人员库权限！"));
		} else {
			CommonData obj=new CommonData("all","全部人员库");
			list.add(obj);
		}
		
		return list;
	}

}
