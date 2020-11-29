/*
 * Created on 2005-12-20
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * @author Administrator
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class TransferOrgTrans extends IBusiness {

	private String end_date;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String transfercodeitemid = (String) this.getFormHM().get(
				"transfercodeitemid");
		String infor_type =(String)this.getFormHM().get("infor_type");
		String to_id=transfercodeitemid;
			
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
		// 判断新的合并后机构编码是否是新机构编码
		end_date = (String) this.getFormHM().get("end_date");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		String date = sdf.format(calendar.getTime());
		end_date = end_date != null && end_date.length() > 9 ? end_date : date;
		
		String table_name =(String)this.getFormHM().get("table_name");
		
		try {
			//单位不能划转到部门下
			HashMap map = new HashMap();
			if(infor_type!=null&& "2".equals(infor_type)){
				this.frecset = dao.search("select * from organization ");
				while(this.frecset.next()){
					map.put(this.frecset.getString("codeitemid"), this.frecset.getString("codesetid"));
				}
			}
	//		String hmuster_sql = (String)this.getFormHM().get("hmuster_sql");
			ArrayList list  = new ArrayList();
			this.frecset=dao.search(" select * from " +table_name);
			//组织机构划转特殊处理：判断选中的记录和指定划转单位的上下级关系
			//最后判断选择换转到的机构是否在该表中是否存在（直接查询表），不存在直接拉进来
			while(this.frecset.next()){
				
				if(this.frecset.getString("submitflag")!=null&& "1".equals(this.frecset.getString("submitflag").trim())){
					ArrayList transferorglist = new ArrayList();
					if(infor_type!=null&& "2".equals(infor_type)){
						String b0110 = this.frecset.getString("b0110");
						String desc = this.frecset.getString("codeitemdesc_1");
						//判断上下级关系
						if(b0110.startsWith(to_id)){
							if(b0110.length()==to_id.length()){
								//throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("label.org.notransself"),"",""));
								throw GeneralExceptionHandler.Handle(new GeneralException("","选择记录中的\""+desc+"\"不能划转给本身","",""));
							}else if(b0110.length()==to_id.length()+2){
								throw GeneralExceptionHandler.Handle(new GeneralException("","选择记录中的\""+desc+"\"不能划转给直接上级","",""));
							}
						}
						if(to_id.startsWith(b0110)){
							if(to_id.length()==b0110.length()){
								//throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("label.org.notransself"),"",""));
								throw GeneralExceptionHandler.Handle(new GeneralException("","选择记录中的\""+desc+"\"不能划转给本身","",""));
							}else{
								throw GeneralExceptionHandler.Handle(new GeneralException("","选择记录中的\""+desc+"\"不能划转给自己的下级","",""));
							}
						}
						if(map!=null&&map.get(b0110)!=null&& "UN".equals(map.get(b0110))&&map.get(to_id)!=null&& "UM".equals(map.get(to_id))){
							throw GeneralExceptionHandler.Handle(new GeneralException("","选择记录中的\""+desc+"\"是单位不能划转给部门","",""));
						}
					}
					
						transferorglist.add(to_id);
					transferorglist.add(java.sql.Date.valueOf(end_date));
					if(infor_type!=null&& "2".equals(infor_type))
					transferorglist.add(this.frecset.getString("b0110"));
					else
						transferorglist.add(this.frecset.getString("e01a1"));
				list.add(transferorglist);
				}
				
			}
			if("2".equals(infor_type)){
			dao.batchUpdate(" update "+table_name+" set to_id=?,start_date_2=? where b0110=?", list);
			//判断选择换转到的机构是否在该表中是否存在（直接查询表），不存在直接拉进来
			this.frowset =dao.search(" select * from "+table_name+" where b0110='"+to_id+"'");
			if(this.frowset.next()){
			dao.update(" update "+table_name+" set to_id='"+to_id+"' where b0110='"+to_id+"' ");
			}else{
				ArrayList a0100s= new ArrayList();
				a0100s.add(to_id);
				String tabid = table_name.substring(table_name.lastIndexOf("_")+1,table_name.length());
				TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
				tablebo.impDataFromArchive(a0100s,"B");
				dao.update(" update "+table_name+" set to_id='"+to_id+"' where b0110='"+to_id+"' ");
			}
			dao.update(" update "+table_name+" set start_date_2="+Sql_switcher.dateValue(end_date)+" where b0110='"+to_id+"' ");
			}else if("3".equals(infor_type)){
			dao.batchUpdate(" update "+table_name+" set parentid_2=?,start_date_2=? where e01a1=?", list);	
			
			}
			
		} catch (Exception e) {
			this.getFormHM().put("resultinfor", e);
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("resultinfor","");

	}
}
