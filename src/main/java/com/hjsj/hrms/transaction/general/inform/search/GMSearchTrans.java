package com.hjsj.hrms.transaction.general.inform.search;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
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
public class GMSearchTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
		String type = (String)reqhm.get("type");
		type=type!=null&&type.trim().length()>0?type:"1";
		reqhm.remove("type");

		String a_code = (String)reqhm.get("a_code");
		a_code=a_code!=null&&a_code.trim().length()>0?a_code:"";
		reqhm.remove("a_code");
		
		String id = (String)reqhm.get("id");
		id=id!=null&&id.trim().length()>0?id:"";
		reqhm.remove("id");
		
		String tablename = (String)reqhm.get("tablename");
		tablename=tablename!=null&&tablename.trim().length()>0?tablename:"";
		reqhm.remove("tablename");
		
		String checkflag = (String)reqhm.get("checkflag");
		checkflag=checkflag!=null&&checkflag.trim().length()>0?checkflag:"";
		reqhm.remove("checkflag");
		
		String ps_flag = (String)reqhm.get("ps_flag");
		ps_flag=ps_flag!=null&&ps_flag.trim().length()>0?ps_flag:"";
		reqhm.remove("ps_flag");
		String priv = (String)reqhm.get("priv");
		reqhm.remove("priv");
		this.getFormHM().put("no_manager_priv", priv);
		String fieldsetid="0";
		String fieldsetdesc="";
		ArrayList fieldsetlist = new ArrayList();
		
		ArrayList setlist = new ArrayList();
		if(reqhm.get("fieldsetid")!=null&&!"".equals((String)reqhm.get("fieldsetid")))
		{
			fieldsetid=(String)reqhm.get("fieldsetid");
			FieldSet ff=DataDictionary.getFieldSetVo(fieldsetid);
			CommonData obj=new CommonData(ff.getFieldsetid(),ff.getCustomdesc());
			fieldsetlist.add(obj);
			fieldsetdesc=ff.getCustomdesc();
			reqhm.remove("fieldsetid");
		}
		else
		{   
			
			
			if(reqhm.containsKey("bzsearch")){   //从编制参数界面进入返回全部子集 13-08-03 gdd
				setlist=this.userView.getPrivFieldSetList(Constant.ALL_FIELD_SET);
		    }else if("1".equals(type)){
	    		setlist=this.userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
	    	}else if("2".equals(type)){
	    		setlist=this.userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
    		}else if("3".equals(type)){
    			setlist=this.userView.getPrivFieldSetList(Constant.POS_FIELD_SET);
    		}else if("9".equals(type)){// 基准岗位liuy 2015-3-12 7975：组织机构-岗位管理-基准岗位-高级花名册-取数-查询结果（指标没有列出来）
    			setlist=this.userView.getPrivFieldSetList(Constant.JOB_FIELD_SET);
    		}

	    	for(int i=0;i<setlist.size();i++){
	    		FieldSet fieldset = (FieldSet)setlist.get(i);
	    		if("0".equalsIgnoreCase(fieldset.getUseflag()))
	    			continue;
	    		if("A00".equalsIgnoreCase(fieldset.getFieldsetid()))
	    			continue;	
	    		if("B00".equalsIgnoreCase(fieldset.getFieldsetid()))
		    		continue;
		    	if("K00".equalsIgnoreCase(fieldset.getFieldsetid()))
		    		continue;
	    		CommonData obj=new CommonData(fieldset.getFieldsetid(),fieldset.getCustomdesc());
	    		fieldsetlist.add(obj);
	    	}
		}
		String privflag="0";
		if(reqhm.get("privflag")!=null&&!"".equals((String)reqhm.get("privflag")))
		{
			privflag=(String)reqhm.get("privflag");
			reqhm.remove("privflag");
		}
		this.getFormHM().put("privflag", privflag);
		this.getFormHM().put("setlist",fieldsetlist);
		this.getFormHM().put("type",type);
		this.getFormHM().put("a_code",a_code);
		this.getFormHM().put("tablename",tablename);
		this.getFormHM().put("ps_flag",ps_flag);
		this.getFormHM().put("fieldSetId", fieldsetid);
		this.getFormHM().put("fieldSetDesc", fieldsetdesc);
		this.getFormHM().put("checkflag", checkflag);
		tableStr(id);
	}
	private void tableStr(String id){
		StringBuffer buf = new StringBuffer();
		String check="0";
		
		if(id!=null&&id.length()>0){
			ContentDAO dao = new ContentDAO(this.frameconn);
			RecordVo vo = new RecordVo("LExpr");
			vo.setInt("id",Integer.parseInt(id));
			try {
				vo = dao.findByPrimaryKey(vo);
				String factor = vo.getString("factor");
				String lexpr = vo.getString("lexpr");
				if(isMaint(factor)){
					check="1";
				}
				buf.append(factorStr(factor));
				buf.append("||"+lexpr);
				buf.append("||"+vo.getString("history"));
				buf.append("||"+vo.getString("fuzzyflag"));
			} catch (GeneralException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.getFormHM().put("tablestr",buf.toString());
		}else{
			this.getFormHM().put("tablestr","");
		}
		this.getFormHM().put("check",check);
		
	}
	private String factorStr(String factor){
		StringBuffer buf = new StringBuffer();
		String arr[] = factor.split("`");
		if(arr.length>0){
			for(int i=0;i<arr.length;i++){
				String eq = "";
				if(arr[i].indexOf("<>")!=-1){
					eq = "<>";
				}else if(arr[i].indexOf("<=")!=-1){
					eq = "<=";
				}else if(arr[i].indexOf(">=")!=-1){
					eq = ">=";
				}else if(arr[i].indexOf("=")!=-1){
					eq = "=";
				}else if(arr[i].indexOf("<")!=-1){
					eq = "<";
				}else if(arr[i].indexOf(">")!=-1){
					eq = ">";
				}
				String fa = arr[i].replaceAll("<>",":").replaceAll(">=",":").replaceAll("<=",":").replaceAll("=",":");
				fa=fa.replaceAll("<",":").replaceAll(">",":");
				String itemarr[] = fa.split(":");
				if(itemarr.length>0){
					String itemid = itemarr[0];
					if(itemid.length()>1){
						FieldItem fielditem = DataDictionary.getFieldItem(itemid);
						if(fielditem!=null){
							String desc = itemarr.length==2?itemarr[1]:"";
							if(fielditem.isCode()){
								String code = "";
								if(desc.indexOf("*")==-1)
									code=desc+","+AdminCode.getCodeName(fielditem.getCodesetid(),desc);
								else
									code=desc+","+desc;
								buf.append(itemid+":"+fielditem.getItemdesc()+":");
								buf.append(fielditem.getCodesetid()+":"+fielditem.getItemtype()+":"+eq+":"+code);
								buf.append(":"+fielditem.getFieldsetid()+"`");
							}else{
								buf.append(itemid+":"+fielditem.getItemdesc()+":");
								buf.append(fielditem.getCodesetid()+":"+fielditem.getItemtype()+":"+eq+":"+desc);
								buf.append(":"+fielditem.getFieldsetid()+"`");
							}
						}
					}
				}
			}
		}
		return buf.toString();
	}
	private boolean isMaint(String factor){
		boolean check=false;
		String arr[] = factor.split("`");
		if(arr.length>0){
			for(int i=0;i<arr.length;i++){
				String fa = arr[i].replaceAll("<>",":").replaceAll(">=",":").replaceAll("<=",":").replaceAll("=",":");
				fa=fa.replaceAll("<",":").replaceAll(">",":");
				String itemarr[] = fa.split(":");
				if(itemarr.length>0){
					String itemid = itemarr[0];
					if(itemid.length()>1){
						FieldItem fielditem = DataDictionary.getFieldItem(itemid);
						if(fielditem!=null&&!fielditem.isMainSet()){
							check=true;
							break;
						}
					}
				}
			}
		}
		return check;
	}
}
