package com.hjsj.hrms.transaction.pos.posparameter;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;


public class SearchPosCodeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		RecordVo constant_vo=ConstantParamter.getRealConstantVo("PS_CODE",this.getFrameconn());
		if(constant_vo!=null)
		{
		  String  ps_code=constant_vo.getString("str_value");
		  this.getFormHM().put("ps_code",ps_code);
		 ///System.out.println(ps_code);
		}
		RecordVo ps_level_code_constant_vo=ConstantParamter.getRealConstantVo("PS_LEVEL_CODE",this.getFrameconn());
		if(ps_level_code_constant_vo!=null)
		{
		  String  ps_level_code=ps_level_code_constant_vo.getString("str_value");
		  this.getFormHM().put("ps_level_code",ps_level_code);
		}
		RecordVo ps_c_code_constant_vo=ConstantParamter.getRealConstantVo("PS_C_CODE",this.getFrameconn());
		if(ps_c_code_constant_vo!=null)
		{ 
		  String  ps_c_code=ps_c_code_constant_vo.getString("str_value");
		  this.getFormHM().put("ps_c_code",ps_c_code);
		  this.getFormHM().put("sqlstrc","select itemid,itemdesc from fielditem where useflag='1' and  fieldsetid='K01' and codesetid='" + ps_c_code + "'");
		}else{
			this.getFormHM().put("sqlstrc","select itemid,itemdesc from fielditem where 1=2");
		}
		RecordVo ps_c_level_code_constant_vo=ConstantParamter.getRealConstantVo("PS_C_LEVEL_CODE",this.getFrameconn());
		if(ps_c_level_code_constant_vo!=null)
		{
		  String  ps_c_level_code=ps_c_level_code_constant_vo.getString("str_value");
		  this.getFormHM().put("ps_c_level_code",ps_c_level_code);
		}
		ArrayList unit_code_fieldlist=DataDictionary.getFieldList("B01",Constant.USED_FIELD_SET);
		unit_code_fieldlist = fieldfilter(unit_code_fieldlist);
		ArrayList pos_code_fieldlist=DataDictionary.getFieldList("K01",Constant.USED_FIELD_SET);
		pos_code_fieldlist = fieldfilter(pos_code_fieldlist);
		this.getFormHM().put("unit_code_fieldlist", unit_code_fieldlist);
		this.getFormHM().put("pos_code_fieldlist", pos_code_fieldlist);
		RecordVo unit_code_field_constant_vo=ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD",this.getFrameconn());
		if(unit_code_field_constant_vo!=null)
		{
		  String  unit_code_field=unit_code_field_constant_vo.getString("str_value");
		  this.getFormHM().put("unit_code_field",unit_code_field);
		}
		RecordVo pos_code_field_constant_vo=ConstantParamter.getRealConstantVo("POS_CODE_FIELD",this.getFrameconn());
		if(pos_code_field_constant_vo!=null)
		{
		  String  pos_code_field=pos_code_field_constant_vo.getString("str_value");
		  this.getFormHM().put("pos_code_field",pos_code_field);
		}
		
		String flag = getflag();
		this.getFormHM().put("ps_c_codeflag", flag);

	}
	private ArrayList fieldfilter(ArrayList list){
		ArrayList l = new ArrayList();
		if(list==null)
			return l;
		for(int i=0;i<list.size();i++){
			FieldItem fielditem = (FieldItem)list.get(i);
			if("0".equals(fielditem.getCodesetid())&& "A".equalsIgnoreCase(fielditem.getItemtype())){
				CommonData cdata = new CommonData(fielditem.getItemid(),fielditem.getItemdesc());
				l.add(cdata);
			}
		}
		return l;
	}
	
	private String getflag(){
		String sql="";
		String flag="N";
		ContentDAO dao = new ContentDAO(this.getFrameconn()); 
		try{
			
			
		if(Sql_switcher.searchDbServer() == Constant.MSSQL){
		   sql = "if(object_id('h01') is not null)  select top 1 'N' as flag from h01 else  select 'Y' as flag";
		   this.frowset = dao.search(sql);
		}
		
		if(Sql_switcher.searchDbServer() == Constant.ORACEL){
		   sql = "select 'Y' flag from USER_TABLES where TABLE_NAME='H01'";
		   this.frowset = dao.search(sql);
		   if(this.frowset.next()){
			   sql = "select  'N' as flag from H01 where ROWNUM=1";
			   this.frowset = dao.search(sql);
		   }
		}
		
		
		if(this.frowset.next())
			flag = this.frowset.getString("flag");
		else
			flag = "Y";
		}catch(Exception e){
			
			e.printStackTrace();
		}
		return flag;
	}
}
