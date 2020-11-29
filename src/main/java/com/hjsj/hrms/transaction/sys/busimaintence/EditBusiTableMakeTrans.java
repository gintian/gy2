package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hjsj.hrms.businessobject.performance.singleGrade.TableOperateBo;
import com.hjsj.hrms.businessobject.sys.busimaintence.BusiSelStr;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * @author t
 *
 */
public class EditBusiTableMakeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
//		查询tableList子集List
//		contractedFieldList某个确定的子集的所有构库指标
//		uncontractedFiledList某个确定子集的所有未构库指标
//		获得下拉选择框
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		UserView uv=this.getUserView();
		String returnvalue="";
        BusiSelStr bss=new BusiSelStr();
        String id=null;
        if(reqhm!=null&&reqhm.containsKey("id")){
    		id=(String) reqhm.get("id");
    		reqhm.remove("id");
    		uv.getHm().put("subsysid",id);
    		returnvalue=(String) reqhm.get("returnvalue");
    		reqhm.remove("returnvalue");
    	}
        if(reqhm!=null&&reqhm.containsKey("update")){
//        	进行修改相关操作
        	reqhm.remove("update");
        	String zero=(String)reqhm.get("zero");
        	String[] cfield=(String[]) hm.get("cfield");
        	String[] ucfield=(String[]) hm.get("ucfield");
        	String fieldsetid="";
        	if(cfield!=null&&cfield.length>0){
        		String tempfi=cfield[0];
        		reqhm.put("fieldsetid",((String[])tempfi.split("/"))[0]);
        		fieldsetid=((String[])tempfi.split("/"))[0];
        	}else{
        		String tempfi=ucfield[0];
        		reqhm.put("fieldsetid",((String[])tempfi.split("/"))[0]);
        		fieldsetid=((String[])tempfi.split("/"))[0];
        	}
        	try {
				updateziji(dao,cfield,ucfield,zero);
				this.updateBusiTable(fieldsetid, dao,zero);
				
			} catch (GeneralException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }else{
        	
        	//初始化叶面
        	String operation="";
        	if(reqhm!=null)
        		operation=(String)reqhm.get("operation");//=1修改=0新构库
        	else
        		operation=(String)hm.get("operation");
        	ArrayList syselist=new ArrayList();
        	if(id!=null&&id.length()>0){
        		 syselist=bss.getSubsys(dao,id);
    		}else{
    			if(uv.getHm().get("subsysid")!=null){
    				syselist=bss.getSubsys(dao,(String) uv.getHm().get("subsysid"));
    			}else{
    				syselist=bss.getSubsys(dao,null);
    			}
    			
    		}
        	
        	String sysvalue=(String) hm.get("sysvalue");
        	if(sysvalue!=null){
        		ArrayList zijilist=bss.getzijiStr(dao,sysvalue,operation);
        		hm.put("zijilist",zijilist);
        	}else{
        		ArrayList zijilist=bss.getzijiStr(dao,bss.getSysvalue(),operation);
        		
        		hm.put("zijilist",zijilist);
        	}
        	hm.put("operation",operation);
        	hm.put("syselist",syselist);
        	hm.put("returnvalue",returnvalue);
        }
		
	}
/**
 * 
 * @param dao
 * @param cfield 构库指标
 * @param ucfield未构库指标
 * @throws GeneralException
 * @throws SQLException
 */
	private void updateziji(ContentDAO dao,String[] cfield,String[] ucfield,String zero) throws GeneralException, SQLException{
		ArrayList recordlist=new ArrayList();
		if(cfield!=null&&!"r".equalsIgnoreCase(zero)){
		for(int i=0;i<cfield.length;i++){
			String value=cfield[i];
			String[] tempfi=value.split("/");
			String field=tempfi[0];
			String item=tempfi[1];
			RecordVo busiFieldVo=new RecordVo("t_hr_busiField");
			busiFieldVo.setString("fieldsetid",field);
			busiFieldVo.setString("itemid",item);
			busiFieldVo=dao.findByPrimaryKey(busiFieldVo);
			busiFieldVo.setString("displayid",i+1+"");
			busiFieldVo.setString("useflag","1");
			dao.updateValueObject(busiFieldVo);
			recordlist.add(busiFieldVo);
		}
		}
		if(ucfield!=null&&!"l".equalsIgnoreCase(zero)){
		for(int i=0;i<ucfield.length;i++){
			String value=ucfield[i];
			String[] tempfi=value.split("/");
			String field=tempfi[0];
			String item=tempfi[1];
			RecordVo busiFieldVo=new RecordVo("t_hr_busiField");
			busiFieldVo.setString("fieldsetid",field);
			busiFieldVo.setString("itemid",item);
			busiFieldVo=dao.findByPrimaryKey(busiFieldVo);
			busiFieldVo.setString("displayid",i+1+cfield.length+"");
			busiFieldVo.setString("useflag","0");
			dao.updateValueObject(busiFieldVo);
			recordlist.add(busiFieldVo);
		}
		}
		this.updateDictionary(dao,recordlist);
	}
	private void updateDictionary(ContentDAO dao,ArrayList recordlist) throws GeneralException{
		TableOperateBo tob=new TableOperateBo(this.getFrameconn());
		ArrayList fieldlist=new ArrayList();
//		DbWizard dbw=new DbWizard(this.getFrameconn());
		String tablename="";
//		UpdateTableOper uto=new UpdateTableOper(this.frameconn);
		for(int i=0;i<recordlist.size();i++){
			RecordVo busiVo=(RecordVo) recordlist.get(i);
//			Table t=new Table(busiVo.getString("fieldsetid"));
			tablename=busiVo.getString("fieldsetid");
//			if(busiVo.getString("useflag").equals("1")){
				boolean flag=false;
				if("1".equals(busiVo.getString("keyflag"))){
					flag=true;
				}
				Field temf=tob.getField(flag,busiVo.getString("itemid"),busiVo.getString("itemdesc"),busiVo.getString("itemtype"),/*changdu,xiaoshudianchandu*/busiVo.getInt("itemlength"),busiVo.getInt("decimalwidth"));
//				t.addField(temf);
				fieldlist.add(temf);
//			}else{
//				t.remove(busiVo.getString("itemid"));
//			}
			
//			dbw.addColumns(fieldlist);
		}
		tob.create_update_Table(tablename,fieldlist,true);
		DataDictionary.refresh();
//		DBMetaModel dm=new DBMetaModel();
//		dm.reloadTableModel(tablename);
	}
	private void updateBusiTable(String fieldsetid,ContentDAO dao,String zero)
	{
		try
		{
			String useflag="1";
			if("r".equalsIgnoreCase(zero))
			{
				useflag="0";
			}
			String sql = "update t_hr_BusiTable set useflag='"+useflag+"' where UPPER(fieldsetid)='"+fieldsetid+"'";
			dao.update(sql);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
