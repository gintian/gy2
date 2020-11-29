package com.hjsj.hrms.transaction.gz.tempvar;

import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

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
public class ViewTempvarTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String cState = (String)reqhm.get("state");//薪资类别号
		cState=cState!=null&&cState.trim().length()>0?cState:"";
		reqhm.remove("state");
		
		String type = (String)reqhm.get("type");
		type=type!=null&&type.trim().length()>0?type:"";
		reqhm.remove("type");
		
		String nflag = (String)reqhm.get("nflag");
		nflag=nflag!=null&&nflag.trim().length()>0?nflag:"";
		reqhm.remove("nflag");
		
		String showflag = (String) reqhm.get("showflag");
		showflag=showflag!=null&&showflag.trim().length()>0?showflag:"0";
		reqhm.remove("showflag");
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select nid,cname,chz,ntype,fldlen,flddec,cstate,templetid,");
		sqlstr.append("(select codesetdesc from codeset where codesetid=midvariable.codesetid) as codesetdesc,codesetid ");

		hm.put("sql",sqlstr.toString());
		
		// WJH 
		if ("4".equals(nflag)) {
			// 薪资总额
			hm.put("where"," from midvariable where templetid=0 and cstate=-1 and nflag=4");
		}else if ("5".equals(nflag)) {
			// 数据采集
			hm.put("where"," from midvariable where templetid=0 and (cstate='"+cState+"' or cstate is null or cstate = 'null')and nflag='"+nflag+"'");
		}else if (!"3".equals(type) && "0".equals(nflag)) {
			// 薪资发放，此时nflag应为0
			hm.put("where"," from midvariable where templetid=0 and(cstate='"+cState+"' or cstate is null) and nflag="+nflag);
        } else {
        	// 其他: 人事异动， 登记表复杂查询，或报表（猜测）等
        	if ("0".equals(nflag)){
        		// 人事异动
        		hm.put("where"," from midvariable where nflag=0 and templetId <> 0 and (templetId = "+cState+" or cstate = '1')");
        	}else if ("2".equals(nflag)){
        		// 报表
        		hm.put("where"," from midvariable where nflag="+nflag+" and (templetId = "+cState+" or cstate = '1')");
			}else{
				// 复杂查询登记表
				hm.put("where"," from midvariable where nflag="+nflag+" and templetId=0");
			}
        	
        }
		
		/* 
		 if(!nflag.equals("4")){
			//xcs modify @2013-8-1
			if(nflag.equals("5")){
					hm.put("where"," from midvariable where templetid=0 and (cstate='"+cState+"' or cstate is null or cstate = 'null')and nflag='"+nflag+"'");
			}else{
				if(type.equals("3")){
				    hm.put("where"," from midvariable where templetid=0 and(cstate='"+cState+"' or cstate is null) and nflag="+nflag);
					
				}else{
				    hm.put("where"," from midvariable where nflag=0 and templetId <> 0 and (templetId = "+cState+" or cstate = '1')");
				}
			}
		}else{
			hm.put("where"," from midvariable where templetid=0 and cstate=-1 and nflag=4");
		}
		*/
		
		hm.put("column","nid,cname,chz,ntype,fldlen,flddec,cstate,templetid,codesetdesc,codesetid");
		hm.put("orderby"," order by sorting");
		
		TempvarBo tempvarbo = new TempvarBo();
		ArrayList fieldsetlist = tempvarbo.fieldListTemp(this.userView,nflag);
		String itemid = "";
		if(fieldsetlist.size()>0){
			CommonData common = (CommonData)fieldsetlist.get(0);
			itemid = common.getDataValue();
		}
		
		this.getFormHM().put("showflag", showflag);
		
		hm.put("fieldsetid","");
		hm.put("fieldsetlist",fieldsetlist);
		
		
		hm.put("itemid",itemid);
		hm.put("itemlist",tempvarbo.itemList(itemid));
		
		hm.put("codesetid","");
		hm.put("codesetlist",tempvarbo.codeList(this.frameconn,""));
		
		hm.put("formula","");
		hm.put("cstate",cState);
		hm.put("type",type);
		hm.put("nflag",nflag);
	}
	
}
