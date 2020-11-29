package com.hjsj.hrms.transaction.selfinfo;

import com.hjsj.hrms.businessobject.structuresql.StructureExecSqlString;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.ScanFormationBo;
import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SaveSelfReturnInfoTrans extends IBusiness  {
	public void execute() throws GeneralException{
		DbNameBo db=new DbNameBo(this.getFrameconn());
		HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
		String actiontype=(String)this.getFormHM().get("actiontype");
		String A0100=(String)this.getFormHM().get("a0100");
		String I9999=(String)this.getFormHM().get("i9999");
		String n9999="";
		if(reqhm!=null){
			n9999=(String)reqhm.get("i9999");
		}
		List fieldlist=(List)this.getFormHM().get("infofieldlist");	         //获得fieldList
		String setname=(String)this.getFormHM().get("setname");
		String userbase=(String)this.getFormHM().get("userbase");
		String tablename=userbase + setname;
		try{
		//Sql_switcher sqlswitcher=new Sql_switcher();
		if("A0100".equals(A0100) || "su".equalsIgnoreCase(A0100))
			A0100=getUserId(tablename);	
		StringBuffer fields=new StringBuffer();
		StringBuffer fieldvalues=new StringBuffer();
		String[] fieldsname=new String[fieldlist.size()];
		String[] fieldcode=new String[fieldlist.size()];
		String org_id="";
		String pos_id="";
		String UN_code = "";
		String UM_code = "";
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem fieldItem=(FieldItem)fieldlist.get(i);
			fields.append(fieldItem.getItemid());
			fieldsname[i]=fieldItem.getItemid();			
			//fieldcode[i]=fieldItem.getValue();
			if(fieldItem.isSequenceable()&&"new".equals(actiontype))
			{
				IDGenerator idg=new IDGenerator(2,this.frameconn);
				String idd=idg.getId(fieldItem.getSequencename());				
				fieldvalues.append("'"+idd+"'");
			}
			else if("D".equals(fieldItem.getItemtype()))
			{
			  fieldvalues.append(PubFunc.DateStringChange(fieldItem.getValue()));
		      fieldcode[i]=PubFunc.DateStringChange(fieldItem.getValue());				 		  
			}else if("M".equals(fieldItem.getItemtype()))
			{
				if (fieldItem.getValue() == null || "null".equals(fieldItem.getValue()) || "".equals(fieldItem.getValue()))
				{	
					fieldcode[i]="null";
					fieldvalues.append("null");
				}
				else
				{
					int itemLength = fieldItem.getItemlength();
					int pyLenght = 0;
					pyLenght = fieldItem.getValue().trim().length();

					if (itemLength != 0 && itemLength != 10 & pyLenght > itemLength) {
						throw new Exception(fieldItem.getItemdesc() + "内容的长度超过限制！");
					}
					fieldcode[i] = "'" + fieldItem.getValue() + "'";
					fieldvalues.append("'" + fieldItem.getValue() + "'");
				}
			}else if("N".equals(fieldItem.getItemtype()))
			{
				if (fieldItem.getValue() == null || "null".equals(fieldItem.getValue()) || "".equals(fieldItem.getValue()))
				{	
					fieldcode[i]="null";
					fieldvalues.append("null");
				}
				else
				{
					fieldcode[i]=fieldItem.getValue();
					fieldvalues.append(fieldItem.getValue());
				}
			}
		    else
			{
			    if (fieldItem.getValue() == null || "null".equals(fieldItem.getValue()) || "".equals(fieldItem.getValue()))
			   {
			    	fieldcode[i]="null";
			    	fieldvalues.append("null");
			   }
			   else
			   {
			        fieldcode[i]="'" + PubFunc.splitString(fieldItem.getValue(),fieldItem.getItemlength()) + "'";
			        if("b0110".equalsIgnoreCase(fieldItem.getItemid())|| "e0122".equalsIgnoreCase(fieldItem.getItemid())){
			        	org_id=PubFunc.splitString(fieldItem.getValue(),fieldItem.getItemlength());
			        	if("b0110".equalsIgnoreCase(fieldItem.getItemid())){
			        		UN_code=PubFunc.splitString(fieldItem.getValue(),fieldItem.getItemlength());
			        	}else{
			        		UM_code=PubFunc.splitString(fieldItem.getValue(),fieldItem.getItemlength());
			        	}
			        }
			        if("e01a1".equalsIgnoreCase(fieldItem.getItemid())){
			        	pos_id=PubFunc.splitString(fieldItem.getValue(),fieldItem.getItemlength());
			        }
					fieldvalues.append("'" + PubFunc.splitString(fieldItem.getValue(),fieldItem.getItemlength()) + "'");
				}
			}
			fields.append(",");
			fieldvalues.append(",");
		}
		
        ArrayList msglist = new ArrayList();     
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        ScanFormationBo scanFormationBo = new ScanFormationBo(this.getFrameconn(),this.userView);
        if (scanFormationBo.doScan()){
            boolean bScan=true;
            StringBuffer itemids = new StringBuffer();
            LazyDynaBean scanBean = new LazyDynaBean();
            setScanBeanList(actiontype,userbase,setname,A0100,I9999,(ArrayList)fieldlist,itemids,scanBean);

            if ("true".equals(scanFormationBo.getPart_flag()) 
                    &&    setname.equals(scanFormationBo.getPart_setid())){//兼职子集
                String part_fld="";
                part_fld=scanFormationBo.getPart_unit();
                if ((part_fld!=null) && (!"".equals(part_fld))) 
                  itemids.append(",b0110");
                part_fld=scanFormationBo.getPart_dept();
                if ((part_fld!=null) && (!"".equals(part_fld))) 
                    itemids.append(",e0122");
                part_fld=scanFormationBo.getPart_pos();
                if ((part_fld!=null) && (!"".equals(part_fld))) 
                    itemids.append(",e01a1");
            }
            if ((","+itemids+",").indexOf(",e01a1,")<0){
                scanFormationBo.setPosChange(false);
            }
            if (scanFormationBo.needDoScan(userbase+',', itemids.toString())){
                
                scanBean.set("objecttype", "1");    
                scanBean.set("nbase", userbase);
                scanBean.set("a0100", A0100);   
                scanBean.set("ispart", "0");    
                
                if("new".equals(actiontype)){
                    scanBean.set("addflag", "1");   
                }
                else {
                    scanBean.set("addflag", "0");   
                }

                if ("01".equals(setname.substring(1,3))){//主集
                    ;   
                }
                else {
                    if ("true".equals(scanFormationBo.getPart_flag()) 
                            &&    setname.equals(scanFormationBo.getPart_setid())){//兼职子集
                        scanBean.set("ispart", "1");    
                        scanBean.set("i9999", I9999);
                        String part_fld="";
                        part_fld=scanFormationBo.getPart_unit();
                        if ((part_fld!=null) && (!"".equals(part_fld))) 
                          if ((String)scanBean.get(part_fld)!=null) 
                              scanBean.set("b0110", (String)scanBean.get(part_fld));
                        part_fld=scanFormationBo.getPart_dept();
                        if ((part_fld!=null) && (!"".equals(part_fld))) 
                            if ((String)scanBean.get(part_fld)!=null) 
                                scanBean.set("e0122", (String)scanBean.get(part_fld));
                        part_fld=scanFormationBo.getPart_pos();
                        if ((part_fld!=null) && (!"".equals(part_fld))) 
                            if ((String)scanBean.get(part_fld)!=null) 
                                scanBean.set("e01a1", (String)scanBean.get(part_fld));
                    }
                    else {//普通子集 最近一条才检查
                         scanBean.set("addflag", "0");  //普通子集都为修改 
                         if("new".equals(actiontype)){                               
                             if(reqhm.containsKey("insert1")){//新增
                                 bScan = false;
                             }
                         }
                         else {
                             if (!(getMaxI9999(userbase,setname,A0100,dao).equals(I9999))){//不是最近一条
                                 bScan =false;
                             }
                         }
                    }
                }

                if (bScan) {                    
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

		// zxj 20200615 原拼接字符串进行新增修改由于安全问题或特殊字符拼接导致的错误，都已改了传值的方式
		StructureExecSqlString structureExecSqlString = new StructureExecSqlString();
		structureExecSqlString.setFieldcode(fieldcode);
		
		boolean flag=false;
		if("new".equals(actiontype)){
			if(tablename.length()==3 && "01".equals(tablename.substring(1,3)) || tablename.length()==6 && "01".equals(tablename.substring(4,6))){
			   A0100 = structureExecSqlString.InfoInsert("1",tablename,fields.toString(),fieldvalues.toString(),A0100,userView.getUserName(),this.getFrameconn());
			   db.dateLinkage(org_id,pos_id,1,"+");
			   /*******新增人员超出编制******/
				db.appendMainSetA0100(tablename,UN_code,UM_code,A0100);
/*				if(pos_id!=null&&pos_id.trim().length()>0){
					boolean inflag=db.overWorkOut(tablename,pos_id,1);
					String unitdesc=AdminCode.getCodeName("@K",pos_id);
					if(inflag){
						throw GeneralExceptionHandler.Handle(new GeneralException("",unitdesc+"人数超编！","",""));
					}
				}*/
			}
		    else{
		    	String tempii=n9999;

		    	I9999 = structureExecSqlString.InfoInsert("1",tablename,fields.toString(),fieldvalues.toString(),A0100,userView.getUserName(),this.getFrameconn());
		    	db.appendMainSetA0100(tablename,UN_code,UM_code,A0100,I9999);
/*				if(pos_id!=null&&pos_id.trim().length()>0){
					boolean inflag=db.overWorkOut(tablename,pos_id,1);
					String unitdesc=AdminCode.getCodeName("@K",pos_id);
					if(inflag){
						throw GeneralExceptionHandler.Handle(new GeneralException("",unitdesc+"人数超编！","",""));
					}
				}*/
		    	if(reqhm.containsKey("insert1")){
		    		reqhm.remove("insert1");
		    		int f0=new Integer(tempii).intValue();
		    		int f1=new Integer(I9999).intValue();
//		    		if(f1-f0>1){
		    			this.updateRecord(tempii,tablename,A0100,this.getFrameconn());
//		    		}
		    	}
		    }
			
		}
		else{
			RecordVo vo_old = null;
			if(tablename.length()==3 && "01".equals(tablename.substring(1,3))
					|| tablename.length()==6 && "01".equals(tablename.substring(4,6))){
				vo_old = db.getRecordVoA01(tablename,A0100);
			}else{
				vo_old = db.getRecordVoA01(tablename,A0100,Integer.parseInt(I9999));
			}
			flag = structureExecSqlString.InfoUpdate("1",tablename,fieldsname,fieldcode,A0100,I9999,userView.getUserName(),this.getFrameconn());
			/*******修改人员超出编制******/
			if(db.checkUpdate(getNewRecord(vo_old,fieldsname,fieldcode),vo_old,setname))
				db.updateMainSetA0100(tablename,UN_code,UM_code,vo_old,A0100);
/*			if(pos_id!=null&&pos_id.trim().length()>0){
				boolean inflag=db.overWorkOut(tablename,pos_id,1);
				String unitdesc=AdminCode.getCodeName("@K",pos_id);
				if(inflag){
					throw GeneralExceptionHandler.Handle(new GeneralException("",unitdesc+"人数超编！","",""));
				}
			}*/
		}
		
		this.getFormHM().put("infofieldlist",fieldlist);
		this.getFormHM().put("a0100",A0100);
		//this.getFormHM().put("actiontype","update");
		this.getFormHM().put("i9999",I9999);
		}catch(Exception ex)
		{
			ex.printStackTrace();
				ContentDAO dao = new ContentDAO(this.frameconn);
				try {
					dao.update("delete from "+tablename+" where CreateTime is null and ModTime is null and CreateUserName is null and ModUserName is null and a0100='"+A0100+"'");
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	private RecordVo getNewRecord(RecordVo vo_old,String fields[],String fieldvalue[]){
		for (int i = 0; i < fields.length; i++) {
			String itemid = fields[i];
			if(itemid!=null&&itemid.trim().length()>0){
				vo_old.setString(itemid,fieldvalue[i]);
			}
		}
		return vo_old;
	}
	public String getUserId(String tableName) throws GeneralException{
		/*String strsql = "select max(A0100) as a0100 from " + tableName + " order by A0100";
		List rs=ExecuteSQL.executeMyQuery(strsql,this.getFrameconn());
		int userPlace;
		String userNumber;
		if (!rs.isEmpty()) {
			LazyDynaBean rec=(LazyDynaBean)rs.get(0);
			if(rec.get("a0100")!=null && rec.get("a0100").toString().trim().length()>0)
			   userPlace =Integer.parseInt(rec.get("a0100").toString()) + 1;
			else
			   userPlace=1;
		} else
			userPlace = 1;
	//	System.out.println("userNumber" + userPlace);
		userNumber = Integer.toString(userPlace);
		for (int i = 0; i < 8 - (Integer.toString(userPlace)).length(); i++)
			userNumber = "0" + userNumber;*/
		
		
		return DbNameBo.insertMainSetA0100(tableName,this.getFrameconn());
	}
	
   private String getMaxI9999(String usrbase,String fieldsetid,String a0100,ContentDAO dao) throws SQLException{
        String maxi9999 = "0";
        String sql = "select max(i9999) i9999 from "+usrbase+fieldsetid+" where a0100='"+a0100+"'";
        this.frecset = dao.search(sql);
        if(this.frecset.next())
            maxi9999 = String.valueOf(this.frecset.getInt("i9999"));
        
        return maxi9999;
    }
	    
	private void updateRecord(String I9999,String tablename,String A0100,Connection conn){
		
		String upsql1="update "+tablename+" set I9999=I9999+1 where I9999>="+I9999+"   and a0100='"+A0100+"' ";
		String upsql="update "+tablename+" set I9999="+I9999+" where I9999=(select max(I9999) from "+tablename+" where a0100='"+A0100+"')  and a0100='"+A0100+"'";
		try{
		    ContentDAO dao = new ContentDAO(conn);
			dao.update(upsql1);
			dao.update(upsql);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
    private void setScanBeanList(String actiontype, String userbase, String setname, String A0100,
            String I9999, ArrayList fieldlist, StringBuffer scanItemIds, LazyDynaBean scanBean) {
        scanItemIds.setLength(0);
        boolean compareFlag = "update".equals(actiontype);
        LazyDynaBean rec = null;
        if (compareFlag) {
            List rs = null;
            StringBuffer strsql = new StringBuffer();
            strsql.append("select * from ");
            strsql.append(userbase + setname);
            strsql.append(" where A0100='");
            strsql.append(A0100);
            strsql.append("'");
            if (!"A01".substring(1, 3).equals(setname.substring(1, 3))) // 如果子集的修改则条件有I9999
            {
                strsql.append(" and I9999=");
                strsql.append(I9999);
            }
            rs = ExecuteSQL.executeMyQuery(strsql.toString(), this.getFrameconn());
            boolean isExistData = !rs.isEmpty();
            compareFlag = false;
            if (isExistData) {
                rec = (LazyDynaBean) rs.get(0);
                compareFlag = true;
            }

        }

        for (int i = 0; i < fieldlist.size(); i++) {
            FieldItemView fieldItem = (FieldItemView) fieldlist.get(i);
            if (fieldItem.isSequenceable())
                continue;

            String itemid = fieldItem.getItemid().toLowerCase();
            String value = PubFunc.getStr(fieldItem.getValue());

            if ((value == null) || ("null".equalsIgnoreCase(value))) {
                value = "";
            }
            scanBean.set(itemid, value);

            if (compareFlag) {
                value = "";
                if ("A".equals(fieldItem.getItemtype()) || "M".equals(fieldItem.getItemtype())) {
                    value = rec.get(fieldItem.getItemid()) != null ? rec.get(fieldItem.getItemid()).toString() : "";
                } else if ("D".equals(fieldItem.getItemtype())) {
                    if (rec.get(fieldItem.getItemid()) != null && rec.get(fieldItem.getItemid()).toString().length() >= 10 && fieldItem.getItemlength() == 10) {
                        value = (new FormatValue().format(fieldItem, rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0, 10)));
                    } else if (rec.get(fieldItem.getItemid()) != null && rec.get(fieldItem.getItemid()).toString().length() >= 10 && fieldItem.getItemlength() == 4) {
                        value = (new FormatValue().format(fieldItem, rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0, 4)));
                    } else if (rec.get(fieldItem.getItemid()) != null && rec.get(fieldItem.getItemid()).toString().length() >= 10 && fieldItem.getItemlength() == 7) {
                        value = (new FormatValue().format(fieldItem, rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0, 7)));
                    } else {
                        value = "";
                    }
                } else {
                    value = (PubFunc.DoFormatDecimal(rec.get(fieldItem.getItemid()) != null ? rec.get(fieldItem.getItemid()).toString() : "", fieldItem.getDecimalwidth()));
                }
                if (fieldItem.getValue() != null) {
                    if (fieldItem.getValue().equals(value))
                        continue;
                } else {
                    if ("".equals(value))
                        continue;
                }
            }

            if (!"".equals(scanItemIds.toString())) {
                scanItemIds.append(",");
            }
            scanItemIds.append(itemid);

        }
    }	
}
