package com.hjsj.hrms.transaction.pos.posparameter;

import com.hjsj.hrms.businessobject.gz.GrossPayManagement;
import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
/**
 * 
 *<p>Title:SearchUnitParameterTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 22, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class SearchUnitParameterTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String org_flag = (String)hm.get("org_flag");
		hm.remove("org_flag");
		this.getFormHM().put("org_flag",org_flag==null?"":org_flag);
		PosparameXML pos = new PosparameXML(this.frameconn);
		String ps_set1 = pos.getValue(PosparameXML.AMOUNTS,"setid");
		String ps_set = (String)this.getFormHM().get("ps_set");
		
		this.getFormHM().put("ps_set",ps_set1); 
		if(ps_set==null)
			ps_set = ps_set1;
		else if(ps_set!=null&&ps_set.length()>1&& "K".equalsIgnoreCase(ps_set.substring(0,1)))
			ps_set = ps_set1;
        //岗位参数设置点确定后 ps_set就变为#了 不对 bug16776
        if (ps_set1!=null && ps_set1.length()>0 && ("#".equals(ps_set)|| "＃".equals(ps_set))){
            ps_set=ps_set1;
        }
		ArrayList fieldsetlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
		ArrayList fieldset = new ArrayList();
		ArrayList spflaglist  = new ArrayList();
		CommonData data1 = new CommonData("0","请选择...");
		fieldset.add(data1);
		spflaglist.add(data1);
		GzAmountXMLBo xmlbo = new GzAmountXMLBo(this.getFrameconn(),0);
		String viewname = xmlbo.getValue("base_set");
		String[] viewnames = null;
		if(viewname.length()>0)
			viewnames = viewname.split(",");
		String viewname2 = xmlbo.getValue("ins_base_set");
		String[] viewnames2 = null;
		if(viewname2.length()>0)
			viewnames2 = viewname2.split(",");
		for(int i=0;i<fieldsetlist.size();i++){
			boolean b = true;
			FieldSet set = (FieldSet)fieldsetlist.get(i);
			if("B00".equalsIgnoreCase(set.getFieldsetid())|| "B01".equalsIgnoreCase(set.getFieldsetid()))
				continue;
			if(viewnames!=null&&viewnames.length>0)
			for(int j=0;j<viewnames.length;j++){
				if("0".equals(this.userView.analyseTablePriv(viewnames[j].toUpperCase()))){
					//b=false;
		  	    	//continue;
				}
				if(set.getFieldsetid().equalsIgnoreCase(viewnames[j])){
					b=false;
				}
			}
			if(viewnames2!=null&&viewnames2.length>0)
				for(int j=0;j<viewnames2.length;j++){
					if("0".equals(this.userView.analyseTablePriv(viewnames2[j].toUpperCase()))){
						//b=false;
			  	    	//continue;
					}
					if(set.getFieldsetid().equalsIgnoreCase(viewnames2[j])){
						b=false;
					}
				}
			if(b){
				CommonData data = new CommonData(set.getFieldsetid(),set.getCustomdesc());
				fieldset.add(data);
			}
		}
		this.getFormHM().put("fieldsetlist",fieldset);
		String sql = "select * from fielditem where fieldsetid = '"+ps_set+"' and codesetid = '23' and useflag='1'";
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				CommonData data = new CommonData(this.frowset.getString("itemid"),this.frowset.getString("itemdesc"));
				spflaglist.add(data);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.getFormHM().put("spflaglist",spflaglist);
		GrossPayManagement gross = new GrossPayManagement(this.getFrameconn());
		ArrayList tableList = gross.fielditemList(ps_set);
		String table = gross.getSelectString(tableList);
		this.getFormHM().put("table", table);
		
		String numitemid = "";
		if(tableList.size()>0){
			CommonData obj= (CommonData)tableList.get(0);
			if(obj!=null)
				numitemid = obj.getDataValue();
		}
		
		this.getFormHM().put("numitemid", numitemid);
		
		ArrayList controlitemids = new ArrayList(); //关联45代码的指标
		controlitemids.add(new CommonData("","请选择"));
		String controlitemid = pos.getValue(PosparameXML.AMOUNTS,"ctrlitemid"); //编制控制指标
		controlitemid = controlitemid == null ?"":controlitemid;
		this.getFormHM().put("controlitemid", controlitemid);
//		sql = "select itemid,itemdesc from fielditem where fieldsetid = '"+ps_set+"' and codesetid='45' and useflag='1'";
		//指定取b01表中的代码   
		sql = "select itemid,itemdesc from fielditem where fieldsetid = 'B01' and codesetid='45' and useflag='1'";
		try{
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				CommonData data = new CommonData(this.frowset.getString("itemid"),this.frowset.getString("itemdesc"));
				controlitemids.add(data);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		this.getFormHM().put("controlitemids", controlitemids);
		
		sql="select id,name from LExpr where Type='1'";
		StringBuffer expr = new StringBuffer();
		ArrayList statlist = new ArrayList();
		expr.append("<select id=\"selectid\" style=\"width:150;\" name=\"selectid\" onclick=\"onLeave1('#');\" onchange=\"addexpr()\">");
		expr.append("<option value=\"\"></option>");
		try {
			this.frowset = dao.search(sql);
			while(frowset.next()){
				expr.append("<option value=\""+frowset.getString("id")+"\">");
				expr.append(frowset.getString("name"));
				expr.append("</option>");
				CommonData data = new CommonData(frowset.getString("id"),frowset.getString("name"));
				statlist.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		expr.append("<option value=\"0\">新增...</option>");
		this.getFormHM().put("expr",expr.toString());
		
		ArrayList list = new ArrayList();
		
		String sp_flag = pos.getValue(PosparameXML.AMOUNTS,"sp_flag");
		String psvalid = pos.getValue(PosparameXML.AMOUNTS,"ctrl_type");
		if("0".equals(ps_set)){//“选择编制子集”参数选择成“请选择…”时，同时把“控制部门 编制”参数设置成“不需要”。chent 20170724
			psvalid = "0";
		}
		String dbpre = pos.getValue(PosparameXML.AMOUNTS,"dbs");
		String nextlevel = pos.getValue(PosparameXML.AMOUNTS,"nextlevel");
		nextlevel=nextlevel!=null&&nextlevel.trim().length()>0?nextlevel:"0";
		this.getFormHM().put("sp_flag",sp_flag);
		this.getFormHM().put("psvalid",psvalid);
		this.getFormHM().put("dbpre",dbpre==null?"":dbpre);
		this.getFormHM().put("nextlevel",nextlevel);
		if(!ps_set.equalsIgnoreCase(ps_set1))
			this.getFormHM().put("ps_set",ps_set);
		ArrayList clist = pos.getChildList(PosparameXML.AMOUNTS,ps_set);
		if(clist.size()>0){
			for(int i=0;i<clist.size();i++){
				LazyDynaBean bean = new LazyDynaBean();
				String planitem = pos.getChildValue(PosparameXML.AMOUNTS,ps_set,clist.get(i).toString(),"planitem");
				String realitem = pos.getChildValue(PosparameXML.AMOUNTS,ps_set,clist.get(i).toString(),"realitem");
				String staticitem = pos.getChildValue(PosparameXML.AMOUNTS,ps_set,clist.get(i).toString(),"static");
				String flag = pos.getChildValue(PosparameXML.AMOUNTS,ps_set,clist.get(i).toString(),"flag");
				String method = pos.getChildValue(PosparameXML.AMOUNTS,ps_set,clist.get(i).toString(),"method");
				method=method!=null&&method.trim().length()>0?method:"0";
				String cond = pos.getTextValue(PosparameXML.AMOUNTS,ps_set,clist.get(i).toString());
				String message = pos.getChildValue(PosparameXML.AMOUNTS, ps_set, clist.get(i).toString(), "message");
				message = message == null ?"":message;
				String ctrlorg = pos.getChildValue(PosparameXML.AMOUNTS, ps_set, clist.get(i).toString(), "ctrlorg");
				ctrlorg = ctrlorg == null ?"":ctrlorg;
				String nextorg = pos.getChildValue(PosparameXML.AMOUNTS, ps_set, clist.get(i).toString(), "nextorg");
				nextorg = nextorg==null || nextorg=="" ?"0":nextorg;
				bean.set("planitem",planitem);
				bean.set("realitem",realitem);
				bean.set("staticitem",staticitem);
				bean.set("flag",flag);
				bean.set("method",method);
				bean.set("message", message);
				bean.set("ctrlorg", ctrlorg);
				bean.set("nextorg",nextorg);
				if("1".equals(method)){
					bean.set("cond",cond);
				}
				for(int j=0;j<tableList.size();j++){
					CommonData data = (CommonData)tableList.get(j);
					if(data.getDataValue().equalsIgnoreCase(planitem))
						bean.set("planitemdesc",data.getDataName());
					if(data.getDataValue().equalsIgnoreCase(realitem))
						bean.set("realitemdesc",data.getDataName());
				}
				for(int j=0;j<statlist.size();j++){
					CommonData data = (CommonData)statlist.get(j);
					if(data.getDataValue().equalsIgnoreCase(staticitem)){
						bean.set("staticitemdesc",data.getDataName());
						break;
					}
				}
				if(bean.get("planitemdesc")==null||bean.get("realitemdesc")==null)
					continue;
				list.add(bean);
			}
		}
		this.getFormHM().put("list",list);
		
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
		/**兼职参数*/
		String flag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");//是否启用，true启用
		//兼职岗位占编 1：占编	
		String takeup_quota=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"takeup_quota");
		String ps_parttime="0";
		if("true".equals(flag)&&"1".equals(takeup_quota)){
			ps_parttime="1";
		}
		this.getFormHM().put("ps_parttime", ps_parttime);
		RecordVo ps_workout_vo=ConstantParamter.getRealConstantVo("PS_WORKOUT",this.getFrameconn());
		String fzw_set = (String)this.formHM.get("zw_set");
		if(ps_workout_vo!=null)
		{
		  String  ps_workout=ps_workout_vo.getString("str_value");
		  StringTokenizer str=new StringTokenizer(ps_workout,"|");//K01|K0114,K0111
		  if(str.hasMoreTokens())
		  {
			  String zw_set = str.nextToken().toUpperCase();
			  this.getFormHM().put("zw_set",zw_set);
			  if(fzw_set==null)
				  fzw_set=zw_set;
			  if(!fzw_set.equalsIgnoreCase(zw_set))
				  this.getFormHM().put("zw_set",fzw_set);
		      if(str.hasMoreTokens())
		      {
		    	 StringTokenizer strfield=new StringTokenizer(str.nextToken(),",");
		    	 if(strfield.hasMoreTokens())
		    	 {
		    		 String ps_workfixed = (String)this.formHM.get("ps_workfixed");
		    		 ps_workfixed=ps_workfixed!=null?ps_workfixed.toUpperCase():"";
		    		 
		    		 String zw_ps_workfixed= strfield.nextToken().toUpperCase();
		    		 this.getFormHM().put("ps_workfixed",zw_ps_workfixed);
		    		 if(ps_workfixed.trim().length()<2)
		    			 ps_workfixed=zw_ps_workfixed;
		    		 if(!ps_workfixed.equalsIgnoreCase(zw_ps_workfixed))
						  this.getFormHM().put("ps_workfixed",ps_workfixed);
		    		 
		    		 String ps_workexist = (String)this.formHM.get("ps_workexist");
		    		 ps_workexist=ps_workexist!=null?ps_workexist.toUpperCase():"";
		    		 
		    		 String zw_ps_workexist= strfield.nextToken().toUpperCase();
		    		 this.getFormHM().put("ps_workexist",zw_ps_workexist);
		    		 if(ps_workexist.trim().length()<2)
		    			 ps_workexist=zw_ps_workexist;
		    		 if(!ps_workexist.equalsIgnoreCase(zw_ps_workexist))
						  this.getFormHM().put("ps_workexist",ps_workexist); 
		    		 
		    		 if("1".equals(ps_parttime)){
		    			 RecordVo ps_workparttime_vo=ConstantParamter.getRealConstantVo("PS_WORKPARTTIME",this.getFrameconn());
		    				if(ps_workparttime_vo!=null){
					    		 String ps_workparttime = (String)this.formHM.get("ps_workparttime");
					    		 ps_workparttime=ps_workparttime!=null?ps_workparttime.toUpperCase():"";
					    		 
					    		 String zw_ps_workparttime= ps_workparttime_vo.getString("str_value").toUpperCase();
					    		 this.getFormHM().put("ps_workparttime",zw_ps_workparttime);
					    		 if(ps_workparttime.trim().length()<2&&zw_ps_workparttime.length()>2)
					    			 ps_workparttime=zw_ps_workparttime;
					    		 if(!ps_workparttime.equalsIgnoreCase(zw_ps_workparttime))
									  this.getFormHM().put("ps_workparttime",ps_workparttime);
		    				}else{
		    					this.getFormHM().put("ps_workparttime","");
		    				}
		    		 }else{
	    					this.getFormHM().put("ps_workparttime","");
	    			}
		    	 }else{
					 String ps_workfixed = (String)this.formHM.get("ps_workfixed");
		    		 ps_workfixed=ps_workfixed!=null?ps_workfixed.toUpperCase():"";
		    		 
		    		 String ps_workexist = (String)this.formHM.get("ps_workexist");
		    		 ps_workexist=ps_workexist!=null?ps_workexist.toUpperCase():"";
		    		 
		    		 String ps_workparttime = (String)this.formHM.get("ps_workparttime");
		    		 ps_workparttime=ps_workparttime!=null?ps_workparttime.toUpperCase():"";
		    		 
		    		 this.getFormHM().put("ps_workfixed",ps_workfixed);
		    		 this.getFormHM().put("ps_workexist",ps_workexist); 
		    		 this.getFormHM().put("ps_workparttime",ps_workparttime);
				}
		      }else{
					 String ps_workfixed = (String)this.formHM.get("ps_workfixed");
		    		 ps_workfixed=ps_workfixed!=null?ps_workfixed.toUpperCase():"";
		    		 
		    		 String ps_workexist = (String)this.formHM.get("ps_workexist");
		    		 ps_workexist=ps_workexist!=null?ps_workexist.toUpperCase():"";
		    		 
		    		 String ps_workparttime = (String)this.formHM.get("ps_workparttime");
		    		 ps_workparttime=ps_workparttime!=null?ps_workparttime.toUpperCase():"";
		    		 
		    		 this.getFormHM().put("ps_workfixed",ps_workfixed);
		    		 this.getFormHM().put("ps_workexist",ps_workexist); 
		    		 this.getFormHM().put("ps_workparttime",ps_workparttime); 
				}
		  }else{
				 String ps_workfixed = (String)this.formHM.get("ps_workfixed");
	    		 ps_workfixed=ps_workfixed!=null?ps_workfixed.toUpperCase():"";
	    		 
	    		 String ps_workexist = (String)this.formHM.get("ps_workexist");
	    		 ps_workexist=ps_workexist!=null?ps_workexist.toUpperCase():"";
	    		 
	    		 String ps_workparttime = (String)this.formHM.get("ps_workparttime");
	    		 ps_workparttime=ps_workparttime!=null?ps_workparttime.toUpperCase():"";
	    		 
	    		 this.getFormHM().put("ps_workfixed",ps_workfixed);
	    		 this.getFormHM().put("ps_workexist",ps_workexist); 
	    		 this.getFormHM().put("ps_workparttime",ps_workparttime); 
			}
		}else{
			 String ps_workfixed = (String)this.formHM.get("ps_workfixed");
    		 ps_workfixed=ps_workfixed!=null?ps_workfixed.toUpperCase():"";
    		 
    		 String ps_workexist = (String)this.formHM.get("ps_workexist");
    		 ps_workexist=ps_workexist!=null?ps_workexist.toUpperCase():"";
    		 
    		 String ps_workparttime = (String)this.formHM.get("ps_workparttime");
    		 ps_workparttime=ps_workparttime!=null?ps_workparttime.toUpperCase():"";
    		 
    		 this.getFormHM().put("ps_workfixed",ps_workfixed);
    		 this.getFormHM().put("ps_workexist",ps_workexist); 
    		 this.getFormHM().put("ps_workparttime",ps_workparttime); 
		}
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
		String zwvalid=sysoth.getValue(Sys_Oth_Parameter.WORKOUT,"pos");
		
		this.getFormHM().put("zwvalid",zwvalid);
		
		String mode=sysoth.getValue(Sys_Oth_Parameter.WORKOUT,"mode");
		mode=mode==null||mode.length()==0?"force":mode;
		this.getFormHM().put("mode",mode);
		
	}
}
