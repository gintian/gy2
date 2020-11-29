package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * <p>Title:业务字典指标删除</p>
 * <p>Description:根据fieldsetid,与itemid来删除;itemid会有重复</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 31, 2008:9:45:56 AM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class DelBusiFieldTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
//		HashMap hm=this.getFormHM();
//		ContentDAO dao=new ContentDAO(this.getFrameconn());
//		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
//		String fieldsetid=(String) reqhm.get("fieldsetid");
//		String itemid=(String) reqhm.get("itemid");
//		reqhm.remove("fieldsetid");
//		reqhm.remove("itemid");
//		RecordVo busiFieldVo=new RecordVo("t_hr_busifield");
//		busiFieldVo.setString("fieldsetid",fieldsetid);
//		busiFieldVo.setString("itemid",itemid);
//		
//		try {
//			busiFieldVo=dao.findByPrimaryKey(busiFieldVo);
//			Compositor.compositfield(dao,busiFieldVo,"0");
//			dao.deleteValueObject(busiFieldVo);
//
//			
//			
//		} catch (GeneralException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		String itemid = (String)this.getFormHM().get("str");
//		String fieldsetid = (String)this.getFormHM().get("fieldsetid");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String itemid = (String)hm.get("str");
		//业务字典，删除指标时，选择两个以上就删除不了了  jingq add 2014.11.7
		itemid = PubFunc.keyWord_reback(itemid);
		String fieldsetid = (String)hm.get("fieldsetid");
		String fieldset = itemid.substring(0,itemid.length()-1);
		String [] temp = fieldset.split("/");
		if(!"".equals(fieldset)){
			this.deleteFactor(temp,fieldsetid);
			//this.getFormHM().put("info","true");
		}
	}
	public void deleteFactor(String[] temp,String fieldsetid) throws GeneralException{
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer fieldSetId=new StringBuffer();
        StringBuffer strsql=new StringBuffer();
        StringBuffer str=new StringBuffer();
        try{
        	for(int i=0;i<temp.length;i++){
				 fieldSetId.append("'");
				 fieldSetId.append(temp[i]);
				 fieldSetId.append("',");
	         }    
        	fieldSetId.setLength(fieldSetId.length()-1);
        	strsql.append("delete from t_hr_BusiField where ");
        	strsql.append("itemid in("+fieldSetId.toString()+") and fieldsetid='"+fieldsetid+"'");
        	dao.delete(strsql.toString(), new ArrayList());
        }
        catch(SQLException e){
        	e.printStackTrace();
        }
	}
}


