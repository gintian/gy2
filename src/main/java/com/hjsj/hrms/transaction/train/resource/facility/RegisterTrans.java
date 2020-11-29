package com.hjsj.hrms.transaction.train.resource.facility;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class RegisterTrans extends IBusiness {

	public void execute() throws GeneralException {
		String state = (String)this.getFormHM().get("state");
		if("query".equals(state)){//查询人员列表			
				this.getFormHM().put("empList", queryEmpList());
		}
		if("out".equals(state)){//保存借出记录
			outSave();
		}
		if("in".equals(state)){//保存返还记录
			inSave();
		}
		
		this.getFormHM().put("strdate", DateUtils.FormatDate(new Date(), "yyyy-MM-dd"));
	}
	
	private ArrayList queryEmpList(){
		String value = (String)this.getFormHM().get("value");
		value = SafeCode.decode(value);
		if(value==null||value.trim().length()<1)
			return null;
		ArrayList dblist=userView.getPrivDbList();
        
        //取参培参数设置的交集
	    ConstantXml constantbo = new ConstantXml(this.frameconn,"TR_PARAM");
	    String tmpnbase = constantbo.getTextValue("/param/post_traincourse/nbase");
	    if(tmpnbase!=null&&tmpnbase.length()>0){
	        for (int i = 0; i < dblist.size(); i++) {
				if(tmpnbase.indexOf(dblist.get(i).toString())==-1)
					dblist.remove(i--);
			}
	        if (dblist.isEmpty()) 
				return null;
	        
	        Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
	        String pinyin_field = sysoth.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);//拼音简码
	        
	        StringBuffer sql = new StringBuffer();
			for (int i = 0; i < dblist.size(); i++) {
				String nbase = (String) dblist.get(i);
				sql.append("SELECT a0100,a0101,'" + nbase + "' nbase,(select codeitemdesc from organization where codesetid = 'UM' and codeitemid = e0122) as e0122 FROM " + nbase
						+ "A01 WHERE A0101 LIKE '%" + value.trim() + "%' ");
				if(pinyin_field!=null&&pinyin_field.length()>3)
					sql.append(" OR " + pinyin_field + " LIKE '" + value.trim() + "%' ");
				if(i<dblist.size()-1)
					sql.append("UNION ");
			}
			ArrayList list = new ArrayList();
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			try {
				this.frowset = dao.search(sql.toString());
				//System.out.println(sql);
				while(this.frowset.next()){
					CommonData cd = new CommonData();
					String e0122 = this.frowset.getString("e0122");
					e0122 = e0122==null || e0122.length() < 1 ? " " : e0122;
					cd.setDataName(this.frowset.getString("a0101") +"("+ e0122 +")");
					cd.setDataValue(SafeCode.encode(PubFunc.encrypt(this.frowset.getString("nbase") + this.frowset.getString("a0100"))));
					list.add(cd);
				}
				return list;
			} catch (SQLException e) {
			}
	    }
	    return null;
	}
	
	private void outSave(){
		String flag = "ok";
		String fieldId = (String)this.getFormHM().get("fieldId");
		fieldId = PubFunc.decrypt(SafeCode.decode(fieldId));
		String a0100 = (String)this.getFormHM().get("a0100");
		a0100 = PubFunc.decrypt(SafeCode.decode(a0100));
		String strdate = (String)this.getFormHM().get("strdate");
		strdate = strdate==null||strdate.length()<10?DateUtils.format(new Date(), "yyyy-MM-dd"):strdate;
		String number = (String)this.getFormHM().get("number");
		number = number==null||number.length()<1?"1":number;
		String declare = SafeCode.decode((String)this.getFormHM().get("declare"));
		
		if(Integer.parseInt(number)>getR11Number(fieldId)){
			flag = "该设施数量不足，请重新操作。";
			this.getFormHM().put("flag", flag);
			return;
		}
		HashMap map = getUserInfo(a0100);
		if(map==null){
			flag = "在人员库中未找到该人员，请重新操作。";
			this.getFormHM().put("flag", flag);
			return;
		}
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql = "select r5900,r5901 from r59 where r1101='"+fieldId+"' and a0100='"+map.get("a0100")+"' and nbase='"+map.get("nbase")+"' and r5909="+Sql_switcher.dateValue(strdate);
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				RecordVo vo = new RecordVo("r59");
				vo.setInt("r5900", this.frowset.getInt("r5900"));
				vo.setString("r1101", fieldId);
				vo.setInt("r5901", this.frowset.getInt("r5901")+Integer.parseInt(number));
				vo.setDate("r5903", strdate);
				vo.setString("nbase", (String)map.get("nbase"));
				vo.setString("a0100", (String)map.get("a0100"));
				vo.setString("a0101", (String)map.get("a0101"));
				vo.setString("b0110", (String)map.get("b0110"));
				vo.setString("e0122", (String)map.get("e0122"));
				vo.setString("e01a1", (String)map.get("e01a1"));
				vo.setString("r5911", declare);
				vo.setString("r5907", this.userView.getUserFullName());
				vo.setString("r5909", DateUtils.FormatDate(new Date(),"yyyy-MM-dd HH:mm:ss"));
				dao.updateValueObject(vo);
			}else{
				IDGenerator idg = new IDGenerator(2, this.getFrameconn());
				String str = idg.getId("R59.R5900");
				RecordVo vo = new RecordVo("r59");
				vo.setInt("r5900", Integer.parseInt(str));
				vo.setString("r1101", fieldId);
				vo.setInt("r5901", Integer.parseInt(number));
				vo.setDate("r5903", strdate);
				vo.setString("nbase", (String)map.get("nbase"));
				vo.setString("a0100", (String)map.get("a0100"));
				vo.setString("a0101", (String)map.get("a0101"));
				vo.setString("b0110", (String)map.get("b0110"));
				vo.setString("e0122", (String)map.get("e0122"));
				vo.setString("e01a1", (String)map.get("e01a1"));
				vo.setString("r5911", declare);
				vo.setString("r5907", this.userView.getUserFullName());
				vo.setDate("r5909", DateUtils.FormatDate(new Date(),"yyyy-MM-dd HH:mm:ss"));
				dao.addValueObject(vo);
			}
		} catch (SQLException e) {
			flag = "error";
			e.printStackTrace();
		} catch (Exception e) {
			flag = "error";
			e.printStackTrace();
		}
		this.getFormHM().put("flag", flag);
	}
	
	private void inSave(){
		String flag = "ok";
		String r5900 = (String)this.getFormHM().get("r5900");
		r5900 = PubFunc.decrypt(SafeCode.decode(r5900));
		String a0100 = (String)this.getFormHM().get("a0100");
		a0100 = PubFunc.decrypt(SafeCode.decode(a0100));
		String strdate = (String)this.getFormHM().get("strdate");
		strdate = strdate==null||strdate.length()<10?DateUtils.format(new Date(), "yyyy-MM-dd"):strdate;
		String declare = SafeCode.decode((String)this.getFormHM().get("declare"));
		if(r5900==null||r5900.trim().length()<1)
			return;
		
		HashMap map = getUserInfo(a0100);
		if(map==null){
			flag = "在人员库中未找到该人员，请重新操作。";
			this.getFormHM().put("flag", flag);
			return;
		}
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			RecordVo vo = new RecordVo("r59");
			vo.setInt("r5900", Integer.parseInt(r5900));
			vo.setDate("r5905", strdate);
			vo.setString("nbase_r", (String)map.get("nbase"));
			vo.setString("a0100_r", (String)map.get("a0100"));
			vo.setString("a0101_r", (String)map.get("a0101"));
			vo.setString("b0110_r", (String)map.get("b0110"));
			vo.setString("e0122_r", (String)map.get("e0122"));
			vo.setString("e01a1_r", (String)map.get("e01a1"));
			vo.setString("r5907", this.userView.getUserFullName());
			vo.setDate("r5909", DateUtils.FormatDate(new Date(),"yyyy-MM-dd HH:mm:ss"));
			vo.setString("r5913", declare);
			if(!"".equals(vo.getString("b0110_r")) || !"".equals(vo.getString("e0122_r"))
					|| !"".equals(vo.getString("e01a1_r"))){
				dao.updateValueObject(vo);
			}
		} catch (SQLException e) {
			flag = "error";
			e.printStackTrace();
		} catch (Exception e) {
			flag = "error";
			e.printStackTrace();
		}
		this.getFormHM().put("flag", flag);
	}
	
	private HashMap getUserInfo(String a0100str){
		String nbase = a0100str.substring(0,3);
		String a0100 = a0100str.substring(3);
		HashMap map = null;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			
			String sql = "select a0101,b0110,e0122,e01a1 from " + nbase + "A01 where a0100='" + a0100 + "'";
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				map = new HashMap();
				map.put("nbase", nbase);
				map.put("a0100", a0100);
				map.put("a0101", this.frowset.getString("a0101"));
				map.put("b0110", this.frowset.getString("b0110"));
				map.put("e0122", this.frowset.getString("e0122"));
				map.put("e01a1", this.frowset.getString("e01a1"));
			}
		} catch (SQLException e) {
		}
		return map;
	}
	
	private int getR11Number(String r1101){
		int number = 0;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql = "select r1107 from r11 where r1101='" + r1101 + "'";
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				number = this.frowset.getInt("r1107");
			}
		} catch (SQLException e) {
		}
		return number;
	}
}