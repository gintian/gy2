package com.hjsj.hrms.transaction.org.orgpre;

import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class SearchNewRecrodValueTrans extends IBusiness {
	
	public void execute() throws GeneralException {
		/**子集名*/
		String setname=(String)this.getFormHM().get("setname");	
		String setpriv = this.userView.analyseTablePriv(setname.toUpperCase());
		if(!"2".equals(setpriv))
			throw GeneralExceptionHandler.Handle(new Throwable(ResourceFactory.getProperty("workdiary.message.add.record.competence")+"！"));
		String type=(String)this.getFormHM().get("type");
		PosparameXML pos = new PosparameXML(this.frameconn); 
		String sp_flag = pos.getValue(PosparameXML.AMOUNTS,"sp_flag");
		try{
		  FieldSet fieldset = DataDictionary.getFieldSetVo(setname);
		  if("insert".equalsIgnoreCase(type)){
			if(!fieldset.isMainset()){
				String i9999=(String)this.getFormHM().get("I9999");
				initSubSetValue(setname,i9999,sp_flag);
			}
		  }else{
			  if(!fieldset.isMainset()){
					initSubSetValue(setname,sp_flag);
			  }	else{
				  initMaintSubSetValue(setname,sp_flag); 
			  }		  
		  }
		}catch(Exception ex){
			throw GeneralExceptionHandler.Handle(ex);
		}
		String infor=(String)this.getFormHM().get("infor");	
		String unit_type=(String)this.getFormHM().get("unit_type");	
		String a_code=(String)this.getFormHM().get("a_code");	
		
		this.getFormHM().put("setname",setname);
		this.getFormHM().put("infor",infor);
		this.getFormHM().put("unit_type",unit_type);
		this.getFormHM().put("a_code",a_code);

		String item ="";
		IDGenerator idg=new IDGenerator(2,this.getFrameconn());
		ArrayList fieldlist = DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET);
		for(int i=0;i<fieldlist.size();i++){
			FieldItem fielditem = (FieldItem)fieldlist.get(i);
			item+=fielditem.getItemid()+",";
			if(fielditem.isSequenceable()){
				String seq_no=idg.getId(setname+"."+fielditem.getItemid());
				this.getFormHM().put(fielditem.getItemid(),seq_no);
			}
		}
		this.getFormHM().put("fielditem",item);
	}
	/**
	 * 初始化子集参数
	 * @param setname
	 * @param curri9999 //当前插入记录的值
	 * @throws GeneralException
	 */
	private void initSubSetValue(String setname,String curri9999,String sp_flag)throws GeneralException {
		GzDataMaintBo gzbo = new GzDataMaintBo(this.getFrameconn());
		String setvalue=(String)this.getFormHM().get("itemid");
		String itemid = "";
		if(!"K".equalsIgnoreCase(setname.substring(0,1))){
			itemid="B0110";
		}else{
			itemid="E01A1";
		}
		String i9999=gzbo.insertSubSet(setname,itemid,setvalue,curri9999,sp_flag);
		this.getFormHM().put("I9999", i9999);
		this.getFormHM().put(itemid, setvalue);
	}
	private void initMaintSubSetValue(String setname,String sp_flag)throws GeneralException {
		GzDataMaintBo gzbo = new GzDataMaintBo(this.getFrameconn());
		String setvalue=(String)this.getFormHM().get("itemid");
		String itemid = "";
		if(!"K".equalsIgnoreCase(setname.substring(0,1))){
			itemid="B0110";
		}else{
			itemid="E01A1";
		}
		this.getFormHM().put(itemid, setvalue);
		gzbo.insertMaintSubSet(setname,itemid,setvalue,sp_flag);
	}
	private void initSubSetValue(String setname,String sp_flag)throws GeneralException {
		GzDataMaintBo gzbo = new GzDataMaintBo(this.getFrameconn());
		String setvalue=(String)this.getFormHM().get("itemid");
		String itemid = "";
		if(!"K".equalsIgnoreCase(setname.substring(0,1))){
			itemid="B0110";
		}else{
			itemid="E01A1";
		}

		String i9999=gzbo.insertSubSet1(setname,itemid,setvalue,sp_flag);
		this.getFormHM().put("I9999", i9999);
		this.getFormHM().put(itemid, setvalue);
		getNewData(setname, setvalue, i9999);
	}	

    /**
     * 获取新增数据的时间和次数
     * 
     * @param setname
     *            编制子集
     * @param codeValue
     *            机构编码
     * @param i9999
     *            最新的记录id
     */
	private void getNewData(String setname,String codeValue, String i9999) {
	    try {
            StringBuffer sql =  new StringBuffer();
            sql.append("select " + Sql_switcher.dateToChar(setname + "z0", "yyyy-MM-dd"));
            sql.append(" " + setname + "z0," + setname + "z1");
            sql.append(" from " + setname);
            sql.append(" where b0110=? and i9999=?");
            ArrayList<String> paramList = new ArrayList<String>();
            paramList.add(codeValue);
            paramList.add(i9999);
            ContentDAO dao = new ContentDAO(this.frameconn);
            this.frowset = dao.search(sql.toString(), paramList);
            if(this.frowset.next()) {
                this.getFormHM().put(setname + "Z0", this.frowset.getString(setname + "z0"));
                this.getFormHM().put(setname + "Z1", this.frowset.getString(setname + "z1"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
