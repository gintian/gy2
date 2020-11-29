/*
 * Created on 2005-12-20
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hjsj.hrms.utils.PubFunc;
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
import java.util.HashMap;

/**
 * @author Administrator
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class CombineOrgTrans extends IBusiness {

	private boolean version = false;
	private String end_date;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		if (this.userView.getVersion() >= 50) {
			version = true;
		}
		// TODO Auto-generated method stub
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
//		String selectcodeitems = (String) hm.get("selectcodeitemids");
//		selectcodeitems = /*PubFunc.ToGbCode*/com.hrms.frame.codec.SafeCode.decode(selectcodeitems);
//		String selects[] = selectcodeitems.split("`");
		
		String combinecodeitemid = (String) this.getFormHM().get(
				"combinecodeitemid");
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
		
		String tarCodeitemdesc = (String) this.getFormHM().get(
				"tarcodeitemdesc");
		tarCodeitemdesc = PubFunc.splitString(tarCodeitemdesc, 50);
		String table_name =(String)this.getFormHM().get("table_name");
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
			
//			ArrayList combinefieldlist= (ArrayList)this.getFormHM().get("combinefieldlist");
//			for(int i=0;i<combinefieldlist.size();i++)
//			{
//				FieldItem fieldItem=(FieldItem)combinefieldlist.get(i);
//				
//				if(fieldItem.getItemid()!=null && fieldItem.getItemid().length()>0)
//				{  
//							    
//					if("D".equals(fieldItem.getItemtype()) && (fieldItem.getValue() != null&&fieldItem.getValue().length()>0))
//					{
//						 vo.setDate(fieldItem.getItemid().toLowerCase(), PubFunc.DateStringChangeValue(fieldItem.getValue()));
//					}else if("M".equals(fieldItem.getItemtype()))
//					{
//						if (fieldItem.getValue() == null || fieldItem.getValue().equals("null") || fieldItem.getValue().equals(""))	
//						{	
//							vo.setString(fieldItem.getItemid().toLowerCase(), null);
//						}
//						else
//						{
//							String content=fieldItem.getValue();						
//							content=PubFunc.getStr(content);						
//							vo.setString(fieldItem.getItemid().toLowerCase(), content);
//						}
//					}else if("N".equals(fieldItem.getItemtype()))
//					{
//						if (fieldItem.getValue() == null || fieldItem.getValue().equals("null") || fieldItem.getValue().equals(""))	
//						{	
//						}
//						else
//						{
//							vo.setString(fieldItem.getItemid().toLowerCase(), fieldItem.getValue());
//						}
//					}
//					else
//					{
//					    if(fieldItem.getValue() == null || fieldItem.getValue().equals("null") || fieldItem.getValue().equals(""))	
//					    {
//						    	
//						     vo.setString(fieldItem.getItemid().toLowerCase(), null);
//						}else
//						{
//							String content=PubFunc.getStr(fieldItem.getValue());
//						    vo.setString(fieldItem.getItemid().toLowerCase(), PubFunc.splitString(content,fieldItem.getItemlength()));
//						}
//					}
//					
//				}
//			}
			
			try {
				//获得新机构的类型
				//String hmuster_sql = (String)this.getFormHM().get("hmuster_sql");
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
					// this.getFrameconn().commit();
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
	//		String hmuster_sql = (String)this.getFormHM().get("hmuster_sql");
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
		this.getFormHM().put("isrefresh", "");
	}
	
}
