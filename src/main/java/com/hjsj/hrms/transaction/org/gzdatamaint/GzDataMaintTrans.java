package com.hjsj.hrms.transaction.org.gzdatamaint;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class GzDataMaintTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm = (HashMap) this.getFormHM().get("requestPamaHM");
		
		String a_code = (String)reqhm.get("a_code");
		a_code=a_code!=null&&a_code.trim().length()>0?a_code:"";
		reqhm.remove("a_code");
		String returnflag=(String)reqhm.get("returnflag"); 
		this.getFormHM().put("returnflag",returnflag);
		String checkadd = (String)reqhm.get("checkadd");
		checkadd=checkadd!=null&&checkadd.trim().length()>0?checkadd:"";
		reqhm.remove("checkadd");
		//判断是不是又有UN`的权限，如果有则使a_code为UN
		String nviewunit=this.userView.getUnitIdByBusi("1");
		if(StringUtils.isNotBlank(nviewunit) && "UN`".equalsIgnoreCase(nviewunit)&&StringUtils.isBlank(a_code)) {
			a_code = "UN";
		}
		this.getFormHM().put("a_code",a_code);
		
		String checkflag = (String)reqhm.get("checkflag");
		checkflag=checkflag!=null&&checkflag.trim().length()>0?checkflag:"";
		reqhm.remove("checkflag");
		
		String infor = (String)reqhm.get("infor");
		infor=infor!=null&&infor.trim().length()>0?infor:"";
		reqhm.remove("infor");
		if(infor.length()<1){
			infor = (String)this.getFormHM().get("infor");
			infor=infor!=null&&infor.trim().length()>0?infor:"";
		}
		
		String gzflag = (String)reqhm.get("gzflag");
		gzflag=gzflag!=null&&gzflag.trim().length()>0?gzflag:"";
		reqhm.remove("gzflag");
		if(gzflag.length()<1){
			gzflag = (String)this.getFormHM().get("gzflag");
			gzflag=gzflag!=null&&gzflag.trim().length()>0?gzflag:"";
		}

		String fieldsetid = (String)this.getFormHM().get("fieldsetid");
		fieldsetid = fieldsetid!=null&&fieldsetid.trim().length()>0?fieldsetid:"";
		
		GzAmountXMLBo xmlbo = new GzAmountXMLBo(this.getFrameconn(),0);
		String hasParam="0";
		String viewname = "";
		if("2".equals(gzflag))
			viewname = xmlbo.getValue("base_set");
		else if("3".equals(gzflag))
			viewname = xmlbo.getValue("ins_base_set");
		if(viewname.length()<2)
		{
			//throw new GeneralException(ResourceFactory.getProperty("workdiary.message.related.subset.notset")+"!");
			hasParam="1";
			this.getFormHM().put("hasParam", hasParam);
			return;
		}
		
		ArrayList fieldlist = baseSet(viewname,fieldsetid);
		if(fieldlist.size()>0){
			CommonData dataobj = (CommonData)fieldlist.get(0);
			fieldsetid = dataobj.getDataValue();
		}
		if(fieldlist.size()<1)
		{
			hasParam="1";
			this.getFormHM().put("hasParam", hasParam);
			return;
			//throw new GeneralException(ResourceFactory.getProperty("workdiary.message.related.subset.notset")+"!");
		}
		if(!this.havetable(fieldsetid)){
			throw new GeneralException("指标子集\""+fieldsetid.toUpperCase()+"\"未构库！");
		}
		String viewdata = (String)this.getFormHM().get("viewdata");
		viewdata=viewdata!=null&&viewdata.trim().length()>0?viewdata:"0";
		
		String sort_fields = (String)this.getFormHM().get("sort_str");
		sort_fields=sort_fields!=null&&sort_fields.trim().length()>0?sort_fields:"";
		
		GzDataMaintBo gzbo = new GzDataMaintBo(this.frameconn,this.userView);
		ArrayList itemlist = gzbo.fieldList(fieldsetid);
		
		StringBuffer sqlstr = new StringBuffer();
		if(fieldsetid!=null&&fieldsetid.trim().length()>0){
			sqlstr.append("select "+gzbo.vilStr(fieldsetid,itemlist));
			sqlstr.append(gzbo.whereStrLevelSql(fieldsetid,a_code,viewdata,checkadd));
			sqlstr.append(" order by ");
			if(sort_fields.trim().length()>0){
				sqlstr.append(gzbo.getoMianOrderbyStr(sort_fields));
			}else{
				if(infor!=null&& "3".equals(infor)){
					sqlstr.append("K01.E01A1");
				}else{
					sqlstr.append("B01.B0110");
				}	
				
			}	
		}
		String unit_type="";
		if("2".equals(infor)){
			unit_type="3";
		}else if("3".equals(infor)){
			unit_type="4";
		}
		this.getFormHM().put("hasParam", hasParam);
		this.getFormHM().put("sort_str",sort_fields);
		this.getFormHM().put("checkflag",checkflag);
		this.getFormHM().put("viewdata",viewdata);
		this.getFormHM().put("selectsql",sqlstr.toString());
		this.getFormHM().put("tablename",fieldsetid);
		this.getFormHM().put("itemlist",itemlist);
		this.getFormHM().put("fieldlist",fieldlist);
		this.getFormHM().put("fieldsetid",fieldsetid);
		this.getFormHM().put("infor",infor);
		this.getFormHM().put("unit_type",unit_type);
		this.getFormHM().put("gzflag",gzflag);
		
		
	}
	private ArrayList baseSet(String viewname,String fieldsetid){
		ArrayList selectlist = new ArrayList();
		if(viewname.length()>0){
			String[] viewnames = viewname.split(",");
			for(int i=0;i<viewnames.length;i++){
				CommonData dataobj;
				if("0".equals(this.userView.analyseTablePriv(viewnames[i])))
					continue;
				FieldSet fieldset =DataDictionary.getFieldSetVo(viewnames[i].toUpperCase());
				if(fieldset!=null){
					if(fieldsetid.equalsIgnoreCase(viewnames[i])){
						dataobj = new CommonData(viewnames[i],fieldset.getCustomdesc());
						selectlist.add(0,dataobj);
					}else{
						dataobj = new CommonData(viewnames[i],fieldset.getCustomdesc());
						selectlist.add(dataobj);
					}
				}
			}
		}
		return selectlist;

	}
	private boolean havetable(String fieldsetid){
		try{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String strSql="select 1 from "+fieldsetid+ " where 1=2";
			dao.search(strSql);
			return true;
		}catch(Exception e){
			return false;
		}
	}

}
