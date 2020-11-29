/*
 * Created on 2005-6-4
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.info;

import com.hjsj.hrms.businessobject.infor.multimedia.MultiMediaBo;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.ScanFormationBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DeleteSelfDeatilInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList selfinfolist=(ArrayList)this.getFormHM().get("selectedlist");
		String setnamee=(String)this.getFormHM().get("setname");
		String userbase=(String)this.getFormHM().get("userbase");
		String tablename=userbase + setnamee;
		/*HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String A0100=(String)hm.get("a0100");                       //获得人员ID
		if("A0100".equals(A0100))
			A0100=userView.getUserId();
		StringBuffer strsql=new StringBuffer();
		strsql.append("delete from ");
		strsql.append(tablename);
		strsql.append(" where A0100='");
		strsql.append(A0100);
		strsql.append("'");		
		if(!"A01".substring(1,3).equals(setname.substring(1,3)))
		{
		   strsql.append(" and (");
		   for(int i=0;i<selfinfolist.size();i++)
		   {
		   	RecordVo vo=(RecordVo)selfinfolist.get(i);
		   	 strsql.append("I9999=");
		   	 strsql.append(vo.getInt("i9999"));
		   	 strsql.append(" or ");
		   }
		   strsql.append("1=2)");
		}
		try
		{
			new ExecuteSQL().execUpdate(strsql.toString());				
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	  */
        if(selfinfolist==null||selfinfolist.size()==0||setnamee==null)
            return;
        RecordVo voo = (RecordVo)selfinfolist.get(0);
        if(!tablename.equalsIgnoreCase(voo.getModelName())){
        	return;
        }
       
//        Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
//	 	String inputchinfor=sysbo.getValue(Sys_Oth_Parameter.INPUTCHINFOR);
//	 	inputchinfor=inputchinfor!=null&&inputchinfor.trim().length()>0?inputchinfor:"1";
//	 	String approveflag=sysbo.getValue(Sys_Oth_Parameter.APPROVE_FLAG);
//	 	approveflag=approveflag!=null&&approveflag.trim().length()>0?approveflag:"1";
//	 	if(inputchinfor.equals("1")&&approveflag.equals("1")){
//	 		String setname=(String)this.getFormHM().get("setname");
//	 		String A0100=(String)this.getFormHM().get("a0100");
//	 		String userbase=(String)this.getFormHM().get("userbase");
//	 		userbase=userbase!=null&&userbase.trim().length()>0?userbase:"usr";
//	 		MyselfDataApprove mysel = new MyselfDataApprove(this.frameconn,this.userView,userbase,A0100);
//	 		FieldSet fieldset = DataDictionary.getFieldSetVo(setname);
//	 		ArrayList newlist = new ArrayList();
//	 		ArrayList listold = getOldList(userbase,setname,A0100,selfinfolist);
//	 		for(int i=0;i<listold.size();i++){
//	 			RecordVo itemvo = (RecordVo)selfinfolist.get(i);
//	 			ArrayList oldlist = (ArrayList)listold.get(i);
//	 			mysel.getOtherParamList(userbase,A0100,fieldset.getFieldsetid(),"01,02,07",itemvo.getString("i9999"));
//				ArrayList sequenceList = mysel.getSequenceList();
//				if(sequenceList.size()<1){
//					mysel.getOtherParamList(userbase,A0100,fieldset.getFieldsetid(),"03",itemvo.getString("i9999"));
//					ArrayList sequenceList1 = mysel.getSequenceList();
//					String sequence ="1";
//					if(sequenceList1.size()>0){
//						sequence =(Integer.parseInt((String)sequenceList1.get(sequenceList1.size()-1))+1)+"";
//					}
//					mysel.saveMyselfData(userbase,A0100,fieldset,newlist,oldlist,"delete","01",itemvo.getString("i9999"),sequence);
//				}
//	 		}
////	 		ArrayList fieldlist = DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET );
////	 		for(int i=0;i<selfinfolist.size();i++){
////	 			RecordVo itemvo = (RecordVo)selfinfolist.get(i);
////	 			ArrayList oldlist=new ArrayList();
////	 			for(int j=0;j<fieldlist.size();j++){
////	 				FieldItem fielditem = (FieldItem)fieldlist.get(j);
////	 				fielditem.setValue(itemvo.getString(fielditem.getItemid()));
////	 				oldlist.add(fielditem);
////	 			}
////	 			mysel.saveMyselfData(userbase,A0100,fieldset,newlist,oldlist,"delete","01",itemvo.getString("i9999"));
////	 		}
//	 		
//	 	}else{
	 		ContentDAO dao=new ContentDAO(this.getFrameconn());
	 		try{
	 			ArrayList list = getRecordVoList(selfinfolist);
	 			//判断编制
				ArrayList msglist = new ArrayList();				
				ScanFormationBo scanFormationBo = new ScanFormationBo(this.getFrameconn(),this.userView);
				if (scanFormationBo.doScan()){
					if ("true".equals(scanFormationBo.getPart_flag()) 
							&&    setnamee.equalsIgnoreCase(scanFormationBo.getPart_setid())){//兼职子集
						;
					}
					else {
			 			String A0100 = "";
			 			for(int i=0;i<selfinfolist.size();i++){
			 				RecordVo itemvo = (RecordVo)selfinfolist.get(i);
			 				A0100=itemvo.getString("a0100");
			 				break;
			 			}	
			 			String maxI999AfterDel = this.getMaxI9999AfterDelete(selfinfolist, userbase,setnamee, A0100, dao);
			 			if (!"0".equals(maxI999AfterDel)) {
							StringBuffer itemids = new StringBuffer();
							LazyDynaBean scanBean = new LazyDynaBean();
							setScanBeanList(itemids,scanBean, userbase,setnamee, A0100,maxI999AfterDel);
							scanFormationBo.setInfoChangeFlag("delete");
							if (scanFormationBo.needDoScan(userbase+',', itemids.toString())){								
								scanBean.set("objecttype", "1");	
								scanBean.set("nbase", userbase);
								scanBean.set("a0100", A0100);	
								scanBean.set("ispart", "0");									
								scanBean.set("addflag", "0");									
						
								ArrayList beanList = new ArrayList();
								beanList.add(scanBean);
								scanFormationBo.execDate2TmpTable(beanList);
								String mess=  scanFormationBo.isOverstaffs();
								if (!"ok".equals(mess)){
									if("warn".equals(scanFormationBo.getMode())){
										msglist.add(mess);
									}else{
										throw GeneralExceptionHandler.Handle(new GeneralException("",mess,"",""));
									}
								}			
							}
			 			}
 			
					}
				}
	 			
				FieldSet fieldSet = DataDictionary.getFieldSetVo(setnamee);
				//删除人员信息时判断子集是否支持附件，不支持就不删除附件
				if("1".equalsIgnoreCase(fieldSet.getMultimedia_file_flag()) || "A01".equalsIgnoreCase(fieldSet.getFieldsetid())) {
				    //删除附件 2014-05-04 wangrd               
				    MultiMediaBo mediabo= new MultiMediaBo(this.frameconn,this.userView);
				    ArrayList delMultilist =new ArrayList();
				    for(int i=0;i<selfinfolist.size();i++){
				        RecordVo itemvo = (RecordVo)selfinfolist.get(i);
				        String A0100=itemvo.getString("a0100");
				        String i9999= itemvo.getString("i9999");
				        LazyDynaBean rec = new LazyDynaBean();
				        rec.set("dbflag", "A");
				        rec.set("setid", setnamee);
				        rec.set("nbase", userbase);
				        rec.set("a0100", A0100);
				        rec.set("i9999", i9999);
				        delMultilist.add(rec);                
				    }   	 			
				    mediabo.deleteAllMultimediaFile(delMultilist);
				}
	 			
	 			dao.deleteValueObject(selfinfolist);
	 			//联动兼职数
	 			//兼职控制编制
				/**兼职参数*/
	 			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
				String partflag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");//是否启用，true启用
				//兼职岗位占编 1：占编	
				String takeup_quota=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"takeup_quota");
				String ps_parttime="0";
				if("true".equals(partflag)&&"1".equals(takeup_quota)){
					ps_parttime="1";
				}
				String pos_ctrl=sysbo.getValueS(Sys_Oth_Parameter.WORKOUT, "pos");
				
				if("true".equals(pos_ctrl)&&"1".equals(ps_parttime)){
					String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");//兼职子集
					RecordVo vo = (RecordVo)selfinfolist.get(0);
					String vosetid = vo.getModelName();
					String setname = vosetid.substring(3);
					String dbpre = vosetid.substring(0,3);
					if(setid.equalsIgnoreCase(setname)){
						String pos_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"pos").toLowerCase();//兼任兼职
						/**任免标识字段*/
						String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint").toLowerCase();
						PosparameXML pos = new PosparameXML(this.frameconn); 
						String dbs = pos.getValue(PosparameXML.AMOUNTS,"dbs"); 
						dbs=dbs!=null&&dbs.trim().length()>0?dbs:"";
						String pos_value="";
						String appoint_value="";
						FieldItem pos_field_item = DataDictionary.getFieldItem(pos_field);
						FieldItem appoint_field_item = DataDictionary.getFieldItem(appoint_field);
						if(dbs.toUpperCase().indexOf(dbpre.toUpperCase())!=-1&&pos_field.length()>0&&appoint_field.length()>0&&pos_field_item!=null&& "1".equals(pos_field_item.getUseflag())&&appoint_field_item!=null&& "1".equals(appoint_field_item.getUseflag())){
							DbNameBo db=new DbNameBo(this.getFrameconn());
							
							for(int i=list.size()-1;i>=0;i--){
								vo = (RecordVo)list.get(i);
								appoint_value=vo.getString(appoint_field);
								if("0".equals(appoint_value)){
									pos_value=vo.getString(pos_field);
									db.dateLinkage(pos_value, 1, "-");
								}
							}
						}
					}
				}
				if(msglist.size()>0){
					StringBuffer msg = new StringBuffer();
					for(int i=0;i<msglist.size();i++){
						if(msglist.size()>1){
							msg.append((i+1)+":"+msglist.get(i)+"\\n");
						}else{
							msg.append(msglist.get(i));
						}
					}
					this.getFormHM().put("msg", msg.toString());
				}else
					this.getFormHM().put("msg", "");
				
	 		}
	 		catch(Exception sqle)
	 		{
	 			sqle.printStackTrace();
	 			throw GeneralExceptionHandler.Handle(sqle);
	 		}
//	 	}

    }
	private ArrayList getOldList(String userbase,String setname,String A0100,ArrayList selfinfolist){
		StringBuffer buf = new StringBuffer();
		buf.append("select * from "+userbase+setname);
		buf.append(" where a0100='"+A0100+"' and i9999 in(");
		ArrayList list = new ArrayList();
		ArrayList fieldlist = DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET );
		for(int i=0;i<selfinfolist.size();i++){
			RecordVo itemvo = (RecordVo)selfinfolist.get(i);
			buf.append(itemvo.getString("i9999"));
			if(i<selfinfolist.size()-1){
				buf.append(",");
			}
		}
		buf.append(")");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(buf.toString());
			while(this.frowset.next()){
				ArrayList oldlist = new ArrayList();
				for(int j=0;j<fieldlist.size();j++){
	 				FieldItem fielditem = (FieldItem)fieldlist.get(j);
	 				String itemvalue = this.frowset.getString(fielditem.getItemid());
	 				itemvalue=itemvalue!=null&&itemvalue.trim().length()>0?itemvalue:"";
	 				fielditem.setValue(itemvalue);
	 				oldlist.add(fielditem);
	 			}
				list.add(oldlist);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}

	private ArrayList getRecordVoList(ArrayList selfinfolist) throws GeneralException, SQLException{
		ArrayList list = new ArrayList();
		for(int i=selfinfolist.size()-1;i>=0;i--){
			RecordVo vo = (RecordVo)selfinfolist.get(i);
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RecordVo tmpvo = dao.findByPrimaryKey(vo);
			list.add(tmpvo);
		}
		return list;
	}

	private String getI9999s(ArrayList selfinfolist){
		StringBuffer buf = new StringBuffer();
		buf.append(",");
		for(int i=0;i<selfinfolist.size();i++){
			RecordVo itemvo = (RecordVo)selfinfolist.get(i);
			buf.append(itemvo.getString("i9999"));
		}
		buf.append(",");;
		return buf.toString();
	}
	private String getMaxI9999AfterDelete(ArrayList selfinfolist,String usrbase ,String fieldsetid,String a0100,ContentDAO dao) throws SQLException{
		String maxi9999 = "0";
		try
		{
			String I9999s = getI9999s(selfinfolist);	
			String sql="";
			sql = "select max(i9999) i9999 from "+usrbase+fieldsetid+" where a0100='"+a0100+"'";
			this.frecset = dao.search(sql);
			if(this.frecset.next())
				maxi9999 =String.valueOf( this.frecset.getInt("i9999"));
			if (I9999s.indexOf(","+maxi9999+",")>=0) {
				I9999s= I9999s.substring(1);
				I9999s =I9999s.substring(0, I9999s.length()-1);
				sql = "select max(i9999) i9999 from "+usrbase+fieldsetid+" where a0100='"+a0100+"'"
	             +" and I9999 not in ("+I9999s+")";
				this.frecset = dao.search(sql);
				
				if(this.frecset.next()){
					maxi9999 =String.valueOf( this.frecset.getInt("i9999"));
				}
				
			}
			else {
				maxi9999 = "0";
			}

        }
	    catch(SQLException e) {
	    	
	    }
		
		return maxi9999;
	}
	
	private void setScanBeanList(StringBuffer scanItemIds, LazyDynaBean scanBean,
			                 String userbase,String setname,String A0100,String i9999) {

		scanItemIds.setLength(0);
		StringBuffer buf = new StringBuffer();
		buf.append("select * from "+userbase+setname);
		buf.append(" where a0100='"+A0100+"' and i9999 ="+i9999);
		ArrayList fieldlist = DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET );

		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(buf.toString());
			if (this.frowset.next()){
					for(int j=0;j<fieldlist.size();j++){
	 				FieldItem fielditem = (FieldItem)fieldlist.get(j);
	 				String itemid =fielditem.getItemid().toLowerCase();
	 				Object obj =this.frowset.getObject(fielditem.getItemid());
	 				String itemvalue = "";
	 				if (obj!=null )
	 				    itemvalue =obj.toString(); 		

	 				itemvalue=itemvalue!=null&&itemvalue.trim().length()>0?itemvalue:"";
	 				if (!"".equals(scanItemIds.toString())){
	 					scanItemIds.append(",") ;
	 				}
	 				scanItemIds.append(itemid);
	 	 			scanBean.set(itemid, itemvalue)	;
	 			}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
}
