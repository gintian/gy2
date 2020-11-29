package com.hjsj.hrms.transaction.train.postAnalyse;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

	public class SaveIntoClass extends IBusiness {

		public void execute() throws GeneralException {
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String r3101 = (String)hm.get("r3101");
			if(r3101!=null && r3101.length() >0)
			    r3101 = PubFunc.decrypt(SafeCode.decode(r3101));
			r3101=r3101!=null&&r3101.trim().length()>0?r3101:"";
			String userbase=(String)this.getFormHM().get("dbpre");
			
			String personstr = (String)hm.get("a0100");
			personstr=SafeCode.decode(personstr);
			personstr=personstr!=null&&personstr.trim().length()>0?personstr:"";
			if(r3101==null||r3101.length()<1)
				return;
			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String[] personarr = personstr.split(",");
			
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
			String A0101="";
			String b0110="";
			String e0122="";
			
			try{
			for(int i=0;i<personarr.length;i++){
				String a0100 = personarr[i].replaceAll("'", "");
				if(a0100==null || a0100.length()==0)
				    continue;
				
				a0100 = PubFunc.decrypt(SafeCode.decode(a0100));
				String sql="select a0100,A0101,b0110,e0122 from "+userbase+"a01 where a0100='"+a0100+"'";
				this.frowset=dao.search(sql);
				if(this.frowset.next()){
					A0101=this.frowset.getString("A0101");
					b0110=this.frowset.getString("b0110");
					e0122=this.frowset.getString("e0122");
				}
				
				RecordVo vo = new RecordVo("r40");
				vo.setString("r4001",a0100);
				vo.setString("r4005",r3101);
				vo.setString("r4002",A0101);
				vo.setString("b0110",b0110);
				vo.setString("e0122",e0122);
				vo.setDate("r4006",r31vo.getDate("r3115"));
				vo.setDate("r4007",r31vo.getDate("r3116"));
				vo.setDouble("r4008",Double.parseDouble(r3112));
				vo.setString("nbase",userbase);
				for(int j = 0; j < itemList.size(); j++){
				    String itemid = itemList.get(j).toString();
				    Object itemValue = getValueById(dao, userbase, a0100, itemid);
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
			}catch (Exception e) {
				e.printStackTrace();
			}
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
		    if(item == null || !"1".equals(item.getUseflag()))
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
