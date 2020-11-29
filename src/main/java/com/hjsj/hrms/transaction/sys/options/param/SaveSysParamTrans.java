package com.hjsj.hrms.transaction.sys.options.param;

import com.hjsj.hrms.businessobject.sys.options.otherparam.OtherParam;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.template.utils.TemplateStaticDataBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class SaveSysParamTrans  extends IBusiness{
	public void execute()throws GeneralException
	{
		HashMap hm=this.getFormHM();
		ContentDAO dao =new ContentDAO(this.getFrameconn());
		//String typeid=(String)this.getFormHM().get("typeid");
		//if(typeid==null||typeid.length()<=0)
		//	typeid="1";
		String operationcode=(String)this.getFormHM().get("operationcode");
		if(operationcode==null||operationcode.length()==0)
			operationcode="0";
		String goboardset=(String)this.getFormHM().get("goboardset");
		if(goboardset==null||goboardset.length()==0)
			goboardset="";
		String[] typestr = (String[])this.getFormHM().get("typestr");
		ArrayList typelist = (ArrayList)this.getFormHM().get("type_list");
		String chk = (String)this.getFormHM().get("chk");
		String idType = (String)this.getFormHM().get("idType");
		String oname = (String)this.getFormHM().get("onlyname");
		String[] validstr = (String[])this.getFormHM().get("validstr");
		String chkvalid="0",uniquenessvalid="0";
		if(validstr!=null)
		for(int i=0;i<validstr.length;i++){
			if("chkvalid".equalsIgnoreCase(validstr[i]))
				chkvalid="1";
			if("uniquenessvalid".equalsIgnoreCase(validstr[i]))
				uniquenessvalid="1";
		}
		
		String transfer = (String)this.getFormHM().get("transfer");
		transfer=transfer!=null?transfer:"";
		String combine=(String)this.getFormHM().get("combine");
		combine=combine!=null?combine:"";
		String bolish=(String)this.getFormHM().get("bolish");
		bolish=bolish!=null?bolish:"";
		String delete=(String)this.getFormHM().get("delete");
		delete=delete!=null?delete:"";
//		Sys_Oth_Parameter sys_param=new Sys_Oth_Parameter();
//		String xmlContent=sys_param.search_SYS_PARAMETER(this.getFrameconn());
//		sys_param.setUnittype(xmlContent,typeid,this.getFrameconn());
		try
		{
			String typeid = "",descript="";
			HashMap map = new HashMap();
			Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
			for(int i=0;i<typelist.size();i++){
				LazyDynaBean bean = (LazyDynaBean)typelist.get(i);
				map.put(bean.get("value"),bean.get("name"));
			}
			if(typestr!=null)
			for(int i=0;i<typestr.length;i++){
				typeid += typestr[i]+",";
				descript += map.get(typestr[i])+",";
			}
			if(typeid.length()>0)
				typeid.substring(0,typeid.length()-1);
			if(descript.length()>0)
			descript.substring(0,descript.length()-1);
			sysoth.setValue(Sys_Oth_Parameter.UNITTYPE,"type",typeid);
			sysoth.setValue(Sys_Oth_Parameter.UNITTYPE,"descript",descript);
			sysoth.setValue(Sys_Oth_Parameter.GOBROAD,"operationcode",operationcode);
			sysoth.setValue(Sys_Oth_Parameter.GOBROADSUBSET,"setname",goboardset);
			sysoth.setValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name",chk);
			sysoth.setValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name",oname);
			sysoth.setValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","valid",chkvalid);
			sysoth.setValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid",uniquenessvalid);
			sysoth.setValue(Sys_Oth_Parameter.CHK_IdTYPE,idType);
			sysoth.setValue(Sys_Oth_Parameter.ORGANIZATION,"transfer",transfer);
			sysoth.setValue(Sys_Oth_Parameter.ORGANIZATION,"combine",combine);
			sysoth.setValue(Sys_Oth_Parameter.ORGANIZATION,"bolish",bolish);
			sysoth.setValue(Sys_Oth_Parameter.ORGANIZATION,"delete",delete);
//			sysoth.saveParameter();
			
			OtherParam op=new OtherParam(sysoth.docToString());
			
			String bycardnosrc=(String) hm.get("bycardnosrc");
		    String bycardnobirth=(String) hm.get("bycardnobirth");
		    String bycardnoage=(String) hm.get("bycardnoage");
		    String bycardnovalid=(String) hm.get("bycardnovalid");
		    String bycardnoax=(String) hm.get("bycardnoax");
		    if("on".equals(bycardnovalid)){
		    	bycardnovalid="true";
		    }else{
		    	bycardnovalid="false";
		    }
		    op.editFormual("bycardno",chk==null?"":chk.toUpperCase(),bycardnobirth,bycardnoage,bycardnovalid,bycardnoax);
		    String byworkdest=(String) hm.get("byworkdest");
		    String byworksrc=(String) hm.get("byworksrc");
		    String byworkvalid=(String) hm.get("byworkvalid");
		    if("on".equals(byworkvalid)){
		    	byworkvalid="true";
		    }else{
		    	byworkvalid="false";
		    }
		    op.editFormual("bywork",byworksrc,byworkdest,byworkvalid);
		    String byorgdest=(String) hm.get("byorgdest");
		    String byorgsrc=(String) hm.get("byorgsrc");
		    String byorgvalid=(String) hm.get("byorgvalid");
		    if("on".equals(byorgvalid)){
		    	byorgvalid="true";
		    }else{
		    	byorgvalid="false";
		    }
		    op.editFormual("byorg",byorgsrc,byorgdest,byorgvalid);
		    
//		    op.syncField(dao);
		    String xml = op.saveXml(dao);
		    //配置参数保存到数据库后,更新缓存  wangb 20180309  bug 35217
		    RecordVo vo=new RecordVo("CONSTANT");
			vo.setString("constant","SYS_OTH_PARAM");
			vo.setString("type",null );
			vo.setString("describe",null);
			vo.setString("str_value",xml);
			ConstantParamter.putConstantVo(vo,"SYS_OTH_PARAM" );
			TemplateStaticDataBo.refreshConstantStr("SYS_OTH_PARAM");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
	}

}
