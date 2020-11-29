package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;

public class SaveSelectPreTrans extends IBusiness {

	public void execute() throws GeneralException {
		String r3101 = (String)this.getFormHM().get("r3101");
		r3101=r3101!=null&&r3101.trim().length()>0?r3101:"";
		
		r3101 = PubFunc.decrypt(SafeCode.decode(r3101));
		
		String personstr = (String)this.getFormHM().get("personstr");
		personstr=personstr!=null&&personstr.trim().length()>0?personstr:"";
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String[] personarr = personstr.split("`");
		
		RecordVo r31vo = getTrainRecord(dao,r3101);
		String r3112 = r31vo.getString("r3112");
		r3112=r3112!=null&&r3112.trim().length()>0?r3112:"0";
		if(r3112.indexOf(".")!=-1){
			FieldItem item = DataDictionary.getFieldItem("r3112");
			int d = item.getDecimalwidth();
			r3112=String.valueOf(new BigDecimal(r3112).setScale(d, BigDecimal.ROUND_HALF_DOWN));
		}
		ArrayList listvalue = new ArrayList();
		ArrayList itemList = getItemId();
		for(int i=0;i<personarr.length;i++){
			String person = personarr[i];
			if(person==null || person.length()==0)
			    continue;
			
			String[] arr = person.split("::");
			if(arr.length != 5)
			    continue;
			
			RecordVo vo = new RecordVo("r40");
			String cade = arr[2];
			if(!"root".equalsIgnoreCase(cade))
			    cade = PubFunc.decrypt(SafeCode.decode(cade));
			String[] org = getOrgPre(dao,cade);
			vo.setString("r4001",PubFunc.decrypt(SafeCode.decode(arr[0])));
			vo.setString("r4005",r3101);
			vo.setString("r4002",arr[1]);
			vo.setString("b0110",org[0]);
			vo.setString("e0122",org[1]);
			vo.setDate("r4006",r31vo.getDate("r3115"));
			vo.setDate("r4007",r31vo.getDate("r3116"));
			vo.setDouble("r4008",Double.parseDouble(r3112));
			vo.setString("nbase",PubFunc.decrypt(SafeCode.decode(arr[4])));
			for(int j = 0; j < itemList.size(); j++){
			    String itemid = itemList.get(j).toString();
			    Object itemValue = getValueById(dao, PubFunc.decrypt(SafeCode.decode(arr[4])), PubFunc.decrypt(SafeCode.decode(arr[0])), itemid);
				vo.setObject(itemid, itemValue);
			}
			vo.setString("r4013","03");
			listvalue.add(vo);
		}
		
		try {
			dao.addValueObject(listvalue);
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	private String[] getOrgPre(ContentDAO dao,String a_code){
		String[] org = new String[2];
		String b0110="";
		String e0122="";
		String codesetid="";
		String codevalue="";
		if(a_code!=null&&a_code.trim().length()>1)
			codesetid=a_code.substring(0,2);
		if(a_code!=null&&a_code.trim().length()>2)
			codevalue=a_code.substring(2);
		if("UN".equalsIgnoreCase(codesetid)&&(!"".equalsIgnoreCase(codevalue))){
			b0110=codevalue;
		}else if("UM".equalsIgnoreCase(codesetid)&&(!"".equalsIgnoreCase(codevalue))){
			e0122=codevalue;
			CodeItem codeitem=AdminCode.getCode("UM", codevalue);
			String parentid=codeitem.getPcodeitem();
			b0110=getParentCodeValue(dao,parentid);
		}
		org[0]=b0110;
		org[1]=e0122;
		return org;
	}
	/**
	 * 根据部门编码，查找对应的上级单位编码值,通过递归找到上级单位
	 * 节点。
	 * @param codevalue
	 * @return
	 */
	private String getParentCodeValue(ContentDAO dao,String codevalue){
		String value="";
		StringBuffer buf=new StringBuffer();
		buf.append("select codeitemid,codesetid,parentid from organization where codeitemid=?");
		ArrayList paralist=new ArrayList();
		paralist.add(codevalue);
		try{
			RowSet rset=dao.search(buf.toString(),paralist);
			if(rset.next()){
				String codeid=rset.getString("codesetid");
				String parentid=rset.getString("parentid");
				if(!"UN".equalsIgnoreCase(codeid))
					value=getParentCodeValue(dao,parentid);
				else
					value=rset.getString("codeitemid");
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return value;
	}
	private RecordVo getTrainRecord(ContentDAO dao,String r3101){
		RecordVo vo = new RecordVo("r31");
		vo.setString("r3101",r3101);
		try {
			vo = dao.findByPrimaryKey(vo);
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return vo;
	}
	
	/**
	 * 通过ID查找当前需要的信息
	 * */
	private Object getValueById(ContentDAO dao, String nbase, String a0100, String itemid){
	    Object itemValue = null;
	    
	    FieldItem item = DataDictionary.getFieldItem(itemid);
	    if(item == null)
	    	return itemValue;
	    if((!"e01a1".equalsIgnoreCase(itemid)) && (!"1".equals(item.getUseflag())))
	    	return itemValue;
	    
	    String setId = item.getFieldsetid();
	    if("r40".equalsIgnoreCase(setId))
	        return itemValue;
	    
	    String setTab = nbase + setId;
	    
	    StringBuffer sql = new StringBuffer();
	    sql.append("select ");
	    sql.append(itemid);
	    sql.append(" from ");
	    sql.append(setTab);
	    sql.append(" A where A.a0100='"+a0100+"'");
	    if(!"a01".equalsIgnoreCase(setId)){
	        sql.append(" AND A.i9999=(select max(i9999) from ");
	        sql.append(setTab);
	        sql.append(" B ");
	        sql.append(" where B.a0100=A.a0100)");
	    }
	    
		try {
			this.frowset = dao.search(sql.toString());
			if(this.frowset.next()){
				itemValue = this.frowset.getObject(itemid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemValue;
	}
	
	/**
	 * 得到除基本信息以外的指标 存入集合中
	 * */
	private ArrayList getItemId(){
		ArrayList fieldlist = DataDictionary.getFieldList("r40",Constant.USED_FIELD_SET);
		ArrayList itemList = new ArrayList();
		for(int i = 0 ; i < fieldlist.size() ; i ++){
		FieldItem fieldItem = (FieldItem)fieldlist.get(i);
		if(!"r4001".equalsIgnoreCase(fieldItem.getItemid()) && !"r4005".equalsIgnoreCase(fieldItem.getItemid()) &&
				!"r4002".equalsIgnoreCase(fieldItem.getItemid()) && !"b0110".equalsIgnoreCase(fieldItem.getItemid()) &&
				!"e0122".equalsIgnoreCase(fieldItem.getItemid()) && !"r4006".equalsIgnoreCase(fieldItem.getItemid()) &&
				!"r4007".equalsIgnoreCase(fieldItem.getItemid()) && !"r4008".equalsIgnoreCase(fieldItem.getItemid()) &&
				!"r4009".equalsIgnoreCase(fieldItem.getItemid()) && !"r4010".equalsIgnoreCase(fieldItem.getItemid()) &&
				!"r4013".equalsIgnoreCase(fieldItem.getItemid()) && !"r4016".equalsIgnoreCase(fieldItem.getItemid()) &&
				!"nbase".equalsIgnoreCase(fieldItem.getItemid()) ){
				itemList.add(fieldItem.getItemid());
			}
		}
		return itemList;
	}

}
