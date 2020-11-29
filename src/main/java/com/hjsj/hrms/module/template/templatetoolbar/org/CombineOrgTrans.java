package com.hjsj.hrms.module.template.templatetoolbar.org;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.CreateSequence;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * 项目名称 ：ehr7x
 * 类名称：CombineOrgTrans
 * 类描述：合并机构
 * 创建人： lis
 * 创建时间：Aug 10, 2016
 */
public class CombineOrgTrans extends IBusiness {

	private static final long serialVersionUID = 1L;
	
	private String end_date;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Override
    public void execute() throws GeneralException {
		String combinecodeitemid = (String) this.getFormHM().get("combinecodeitemid");
		String to_id=combinecodeitemid;
		
		//判断combinecodeitemid是否在组织机构中存在。
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		// 判断新的合并后机构编码是否是新机构编码
		boolean flag = true;
		if(combinecodeitemid!=null&&combinecodeitemid.length()>0){
			flag=false;
		}else{
			flag=true;
		}
		end_date = (String) this.getFormHM().get("end_date");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		String date = sdf.format(calendar.getTime());
		end_date = end_date != null && end_date.length() > 9 ? end_date : date;
		
		String tarCodeitemdesc = (String) this.getFormHM().get("tarcodeitemdesc");
		tarCodeitemdesc = PubFunc.splitString(tarCodeitemdesc, 50);
		String table_name =(String)this.getFormHM().get("table_name");
		table_name = PubFunc.decrypt(SafeCode.decode(table_name));
		String infor_type =(String)this.getFormHM().get("infor_type");
		RecordVo vo = new RecordVo(table_name);
		if (flag) {
			
			IDGenerator idg=new IDGenerator(2,this.getFrameconn());
			String a0100=  idg.getId("rsbd.a0100");
			if("2".equals(infor_type)){
				vo.setString("b0110", "B"+a0100);
				vo.setString("to_id", "B"+a0100);
				to_id="B"+a0100;
			}
			if("3".equals(infor_type)){
				vo.setString("e01a1", "B"+a0100);
				vo.setString("to_id", "B"+a0100);
				to_id="B"+a0100;
			}
			vo.setDate("start_date_2",end_date);
			vo.setString("codeitemdesc_2", tarCodeitemdesc);
			vo.setString("codeitemdesc_1", tarCodeitemdesc);
			vo.setInt("submitflag",0);
			vo.setInt("state", 0);
			String seqnum=CreateSequence.getUUID();
			vo.setString("seqnum", seqnum);
			
			try {
				//获得新机构的类型
				ArrayList list  = new ArrayList();
				String b0101="";
				this.frecset=dao.search(" select * from "+table_name+" where submitflag=1");
				if(this.frecset.next()){
					if("2".equals(infor_type)){
						b0101= this.frecset.getString("b0110");
					}
					if("3".equals(infor_type)){
						b0101= this.frecset.getString("e01a1");
					}
					
				}
				if(b0101!=null&&b0101.length()>0){
				this.frecset = dao.search("select codesetid,parentid from organization where codeitemid='"+b0101+"' ");
				if(this.frecset.next()){
					if("2".equals(infor_type))
						vo.setString("codesetid_2", this.frecset.getString("codesetid"));
					if(!b0101.equals(this.frecset.getString("parentid"))){
						vo.setString("parentid_2", this.frecset.getString("parentid"));
					}else{
						vo.setString("parentid_2", "");
					}
				
				}
				}
				dao.addValueObject(vo);

			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			} finally {
				try {
					 this.getFrameconn().commit();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		
		}else{
			try{
			if(infor_type!=null&& "2".equals(infor_type)){
				dao.update(" update "+table_name+" set codeitemdesc_2='"+tarCodeitemdesc+"' where b0110='"+combinecodeitemid+"'");
			}else{
				dao.update(" update "+table_name+" set codeitemdesc_2='"+tarCodeitemdesc+"' where e01a1='"+combinecodeitemid+"'");
			}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		try {
			ArrayList list  = new ArrayList();
			this.frecset=dao.search(" select * from " +table_name);
			while(this.frecset.next()){
				
				if(this.frecset.getString("submitflag")!=null&& "1".equals(this.frecset.getString("submitflag").trim())){
					ArrayList transferorglist = new ArrayList();
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
			}else if("3".equals(infor_type)){
				dao.batchUpdate(" update "+table_name+" set to_id=?,start_date_2=? where e01a1=?", list);	
			}
		} catch (Exception e) {
			this.getFormHM().put("isrefresh", e);
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
}
