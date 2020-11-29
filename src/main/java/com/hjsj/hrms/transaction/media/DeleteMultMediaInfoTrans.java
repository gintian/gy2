/*
 * Created on 2005-8-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.media;

import com.hjsj.hrms.businessobject.infor.multimedia.MultiMediaBo;
import com.hrms.frame.dao.ContentDAO;
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
public class DeleteMultMediaInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String userbase=(String)this.getFormHM().get("userbase");
	    ArrayList mediainfolist=(ArrayList)this.getFormHM().get("selectedlist");
        if(mediainfolist==null||mediainfolist.size()==0)
            return;
        
//        Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
//	 	String inputchinfor=sysbo.getValue(Sys_Oth_Parameter.INPUTCHINFOR);
//	 	inputchinfor=inputchinfor!=null&&inputchinfor.trim().length()>0?inputchinfor:"1";
//	 	String approveflag=sysbo.getValue(Sys_Oth_Parameter.APPROVE_FLAG);
//	 	approveflag=approveflag!=null&&approveflag.trim().length()>0?approveflag:"1";
//	 	
//	 	if(inputchinfor.equals("1")&&approveflag.equals("1")){
//	 		for(int i=0;i<mediainfolist.size();i++){
//	 			LazyDynaBean rec=(LazyDynaBean)mediainfolist.get(i); 
//	 			String A0100=(String)rec.get("a0100");
//		 		String i9999=(String)rec.get("i9999");
//	 		}
//	 	}else{
	 		ContentDAO dao=new ContentDAO(this.getFrameconn());
	 		String sql="delete from " + userbase + "a00 ";
	 		try{
	 		 //删除临时文件 2014-05-04 wangrd   
                MultiMediaBo mediabo= new MultiMediaBo(this.frameconn,this.userView);
                ArrayList delMultilist =new ArrayList();
                for(int i=0;i<mediainfolist.size();i++){
                    LazyDynaBean rec1=(LazyDynaBean)mediainfolist.get(i); 
                    String A0100=rec1.get("a0100").toString();
                    String i9999= rec1.get("i9999").toString();
                    LazyDynaBean rec = new LazyDynaBean();
                    rec.set("dbflag", "A");
                    rec.set("setid", "A00");
                    rec.set("nbase", userbase);
                    rec.set("a0100", A0100);
                    rec.set("i9999", i9999);
                    rec.set("fileid", rec1.get("fileid").toString());
                    delMultilist.add(rec);                
                }                   
                mediabo.deleteAllMultimediaFile(delMultilist);
                
	 			if(mediainfolist.size()>0){
	 				LazyDynaBean rec=(LazyDynaBean)mediainfolist.get(0); 
	 				sql +=" where  a0100='" + rec.get("a0100").toString() + "' and i9999 in(";
	 			}
	 			for(int i=0;i<mediainfolist.size();i++){
	 				if(i!=0)
	 					sql+= ",";
	 				LazyDynaBean rec=(LazyDynaBean)mediainfolist.get(i); 
	 				sql+= rec.get("i9999").toString(); 
	 			}        	
	 			sql+=")";
	 			if(mediainfolist.size()>0)
	 				dao.update(sql);
	 		}catch(SQLException sqle){
	 			throw GeneralExceptionHandler.Handle(sqle);
	 		}
//	 	}
	}
	public FieldSet fieldSet(){
		FieldSet fieldset = new FieldSet("a00");
		fieldset.setFieldsetid("a00");
		fieldset.setFieldsetdesc("多媒体子集");
		return fieldset;
	}
	public ArrayList fieldItem(){
		ArrayList list = new ArrayList();
		FieldItem fielditem = new FieldItem();
		fielditem.setFieldsetid("a00");
		fielditem.setItemid("title");
		fielditem.setItemdesc("文件");
		fielditem.setItemtype("F");
		list.add(fielditem);
		
		fielditem.setItemid("ole");
		fielditem.setItemdesc("图片");
		fielditem.setItemtype("F");
		list.add(fielditem);
		
		fielditem.setItemid("flag");
		fielditem.setItemdesc("类型");
		fielditem.setItemtype("F");
		list.add(fielditem);
		
		return list;
		
	}
}
