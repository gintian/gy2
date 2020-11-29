package com.hjsj.hrms.transaction.pos.posparameter;

import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

public class SaveUnitParameterTrans extends IBusiness {

    HashSet itemSet = new HashSet();
	public void execute() throws GeneralException {
		
		String setid = (String)this.getFormHM().get("ps_set");
		String sp_flag = (String)this.getFormHM().get("sp_flag");
		String ctrl_type = (String)this.getFormHM().get("psvalid");
		String controlitemid = (String)this.getFormHM().get("controlitemid");
		//换成b01表中的代码后,需要兼容以前的. 当controlitemid为空时,查找对应的字段,得出该字段是在编制子集还是b01主集,如果不再b01中,替换成数据库中保存的值
		if(StringUtils.isBlank(controlitemid)){
			PosparameXML pos = new PosparameXML(this.frameconn);
			String controlitemidField = pos.getValue(PosparameXML.AMOUNTS,"ctrlitemid"); //编制控制指标
			controlitemidField = controlitemidField == null ?"":controlitemidField;
			if(!controlitemidField.startsWith("b01") || !controlitemidField.startsWith("B01")){
				String sql = "select itemid,itemdesc from fielditem where fieldsetid = '"+setid+"' and codesetid='45' and useflag='1'";
				ContentDAO dao =  new ContentDAO(this.frameconn);
				try {
					this.frowset = dao.search(sql);
					while(this.frowset.next()){
						controlitemid = this.frowset.getString("itemid");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		String planitem = (String)this.getFormHM().get("planitem");
		String realitem = (String)this.getFormHM().get("realitem");
		String staticitem = (String)this.getFormHM().get("staticitem");
		String flagitem = (String)this.getFormHM().get("flagitem");
		String methoditem = (String)this.getFormHM().get("methoditem");
		String conditem = (String)this.getFormHM().get("conditem");
		String messitem = (String)this.getFormHM().get("messitem");
		String ctrlorgitem = (String)this.getFormHM().get("ctrlorgitem");
		String nextorgitem = (String)this.getFormHM().get("nextorgitem");
		String dbpre = (String)this.getFormHM().get("dbpre");
		String nextlevel = (String)this.getFormHM().get("nextlevel");
		nextlevel=nextlevel!=null&&nextlevel.trim().length()>0?nextlevel:"0";
		
		PosparameXML pos = new PosparameXML(this.frameconn);
		CommonData data = new CommonData(setid,"setid");
		ArrayList list = new ArrayList();
		list.add(data);
		data = new CommonData(sp_flag,"sp_flag");
		list.add(data);
		data = new CommonData(ctrl_type,"ctrl_type");
		list.add(data);
		data = new CommonData(dbpre,"dbs");
		list.add(data);
		data = new CommonData(nextlevel,"nextlevel");
		list.add(data);
		data = new CommonData(controlitemid,"ctrlitemid");
		list.add(data);
		pos.setValue(PosparameXML.AMOUNTS,list);
		
		list.clear();
		//add by wangchaoqun on 2014-10-20 begin
		planitem = PubFunc.keyWord_reback(planitem);
		realitem = PubFunc.keyWord_reback(realitem);
		staticitem = PubFunc.keyWord_reback(staticitem);
		flagitem = PubFunc.keyWord_reback(flagitem);
		methoditem = PubFunc.keyWord_reback(methoditem);
		conditem = PubFunc.keyWord_reback(conditem);
		messitem = PubFunc.keyWord_reback(messitem);
		ctrlorgitem = PubFunc.keyWord_reback(ctrlorgitem);
		nextorgitem = PubFunc.keyWord_reback(nextorgitem);
		//add by wangchaoqun on 2014-10-20 end
		String[] planitems = planitem.split("/");
		String[] realitems = realitem.split("/");
		String[] staticitems = staticitem.split("/");
		String[] flagitems = flagitem.split("/");
		String[] methoditems = methoditem.split("/");
		String[] conditems = conditem.split("/");
		String[] messitems = messitem.split("/");
	    String[] ctrlorgitems = ctrlorgitem.split("/");
	    String[] nextorgitems = nextorgitem.split("/");
		ArrayList ctrllist = new ArrayList();
		if(!"".equalsIgnoreCase(planitem)){
			int n=0;
			for(int i=0;i<planitems.length;i++){
				ArrayList clist = new ArrayList();
				data = new CommonData(planitems[i],"planitem");
				clist.add(data);
				data = new CommonData(realitems[i],"realitem");
				clist.add(data);
				data = new CommonData(checkNull(staticitems,i),"static");
				clist.add(data);
				data = new CommonData(flagitems[i],"flag");
				clist.add(data);
				//if(flagitems[i].equals("1"))
				//	searchItem(checkNull(staticitems,i));
				data = new CommonData(methoditems[i],"method");
				clist.add(data);
				if(methoditems[i]!=null&& "1".equals(methoditems[i])){
					data = new CommonData(checkNull(conditems,n),"cond");
					n++;
					clist.add(data);
				}
				data = new CommonData(checkNull(messitems,i),"message");
				clist.add(data);
				data = new CommonData(checkNull(ctrlorgitems,i),"ctrlorg");
				clist.add(data);
				data = new CommonData(nextorgitems[i],"nextorg");
				clist.add(data);
				ctrllist.add(clist);
			}
		}
		pos.setChildValue(PosparameXML.CTRL_ITEM,setid,ctrllist);
		pos.saveParameter();
		
		
		String ps_workfixed=(String)this.getFormHM().get("ps_workfixed");
		String ps_workexist=(String)this.getFormHM().get("ps_workexist");
		String ps_workparttime=(String)this.getFormHM().get("ps_workparttime");
		ps_workparttime=ps_workparttime==null?"":ps_workparttime;
		String ps_set=(String)this.getFormHM().get("zw_set");
		String psvalid=(String) this.getFormHM().get("zwvalid");
		String mode=(String) this.getFormHM().get("mode");
		mode=mode==null||mode.length()==0?"force":mode;
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
		sysoth.setValue(Sys_Oth_Parameter.WORKOUT,"pos",psvalid);
		sysoth.setValue(Sys_Oth_Parameter.WORKOUT,"mode",mode);
		sysoth.saveParameter();
		
		
		
		
		ContentDAO dao=new ContentDAO(this.getFrameconn()); 
		try{
			dao.delete("delete from constant where constant='PS_WORKOUT'",new ArrayList());
		}catch(Exception e){
           e.printStackTrace();
           //throw GeneralExceptionHandler.Handle(e);
        }
		StringBuffer sql =new StringBuffer();	
		sql.append("insert into constant(constant,type,str_value,describe)values('PS_WORKOUT','0','"+ ps_set + "|" + ps_workfixed + "," + ps_workexist  + "','')");
		try{
			//System.out.println(sql.toString());
			dao.insert(sql.toString(),new ArrayList());
		}catch(Exception e){
           e.printStackTrace();
           throw GeneralExceptionHandler.Handle(e);
        }
		
		if(ps_workparttime.length()>0){
			try{
				dao.delete("delete from constant where constant='PS_WORKPARTTIME'",new ArrayList());
			}catch(Exception e){
	           e.printStackTrace();
	           //throw GeneralExceptionHandler.Handle(e);
	        }
			sql.setLength(0);
			sql.append("insert into constant(constant,type,str_value,describe)values('PS_WORKPARTTIME','0','"+ ps_workparttime+"','')");
			try{
				//System.out.println(sql.toString());
				dao.insert(sql.toString(),new ArrayList());
			}catch(Exception e){
	           e.printStackTrace();
	           throw GeneralExceptionHandler.Handle(e);
	        }
		}
	}
	private String checkNull(String[] arr,int i){
		if(arr==null)
			return "";
		if(arr.length<(i+1))
			return "";
		return arr[i];
	}
	
}
