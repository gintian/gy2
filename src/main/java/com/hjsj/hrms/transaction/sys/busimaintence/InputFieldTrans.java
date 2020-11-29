package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hjsj.hrms.businessobject.sys.busimaintence.BusiSQLStr;
import com.hjsj.hrms.businessobject.sys.busimaintence.BusiSelStr;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class InputFieldTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String fieldsetid=(String) reqhm.get("fieldsetid");
		this.getFormHM().put("fieldsetid", fieldsetid);
//		String itemid=(String) reqhm.get("itemid");
//		reqhm.remove("fieldsetid");
		StringBuffer str = new StringBuffer();//添加岗位和单位 sql 语句 wangb 20170711
		if(reqhm.containsKey("query")){
//			进入选择指标叶面
			String groupinfo="";
			String itemsel="";
			String[] sql=new String[4];
			if(reqhm.containsKey("inputfield")){
				String abkflag=(String)reqhm.get("group");
				String itemid=(String)reqhm.get("inputfield");
			groupinfo=BusiSelStr.getGroup(abkflag);
			itemsel=BusiSelStr.getFieldset(abkflag,itemid);
			sql=BusiSQLStr.getFieldString(itemid);
			if("A01".equalsIgnoreCase(itemid)){//当查询的是A01对应的指标时，补上单位和岗位记录  wangb 20170710 和 oracle数据库 每个字段数据类型必须一致，String 套在了int类型显示报错 wangb  31465 20170911 
//				str.append("select 'A01' fieldsetid,'E01A1' itemid,'1' displayid,'岗位名称' itemdesc,'A' itemtype,'30' itemlength,'0' decimalwidth,'@K' codesetid,'27' displaywidth,'' state,'1' useflag from fielditem where   fieldsetid='A01' ");
				str.append("select 'A01' fieldsetid,'E01A1' itemid,1 displayid,'岗位名称' itemdesc,'A' itemtype,30 itemlength,0 decimalwidth,'@K' codesetid,27 displaywidth,'' state,'1' useflag from fielditem where fieldsetid='A01' ");
				str.append("union select 'A01' fieldsetid,'B0110' itemid,1 displayid,'单位名称' itemdesc,'A' itemtype,30 itemlength,0 decimalwidth,'UN' codesetid,27 displaywidth,'' state,'1' useflag from fielditem where fieldsetid='A01' ");
				str.append("union "+ sql[0]);
			}else{
				str.append(sql[0]);
			}
			reqhm.remove("inputfield");
			}else{
				String abkflag=(String)reqhm.get("group");
				if(reqhm.containsKey("group")){
					groupinfo=BusiSelStr.getGroup(abkflag);
					itemsel=BusiSelStr.getFieldset(abkflag,null);
					sql=BusiSQLStr.getFieldString(abkflag+"01");
					reqhm.remove("group");
					if("A".equalsIgnoreCase(abkflag)){//当查询的是A01对应的指标时，补上单位和岗位记录  wangb 20170710 和 oracle数据库 每个字段数据类型必须一致，String 套在了int类型显示报错 wangb  31465 20170911
//						str.append("select 'A01' fieldsetid,'E01A1' itemid,'1' displayid,'岗位名称' itemdesc,'A' itemtype,'30' itemlength,'0' decimalwidth,'@K' codesetid,'27' displaywidth,'' state,'1' useflag from fielditem where   fieldsetid='A01' ");
						str.append("select 'A01' fieldsetid,'E01A1' itemid,1 displayid,'岗位名称' itemdesc,'A' itemtype,30 itemlength,0 decimalwidth,'@K' codesetid,27 displaywidth,'' state,'1' useflag from fielditem where fieldsetid='A01' ");
						str.append("union select 'A01' fieldsetid,'B0110' itemid,1 displayid,'单位名称' itemdesc,'A' itemtype,30 itemlength,0 decimalwidth,'UN' codesetid,27 displaywidth,'' state,'1' useflag from fielditem where fieldsetid='A01' ");
						str.append("union "+ sql[0]);
					}else{
						str.append(sql[0]);
					}
				}else{
					groupinfo=BusiSelStr.getGroup("A");   //A 改为大写;ora库不支持小写;
					itemsel=BusiSelStr.getFieldset("A",null);
					sql=BusiSQLStr.getFieldString("A01");
					/*当查询的是A01对应的指标时，补上单位和岗位记录  wangb 20170710 和 oracle数据库 每个字段数据类型必须一致，String 套在了int类型显示报错 wangb  31465 20170911 */
//					str.append("select 'A01' fieldsetid,'E01A1' itemid,'1' displayid,'岗位名称' itemdesc,'A' itemtype,'30' itemlength,'0' decimalwidth,'@K' codesetid,'27' displaywidth,'' state,'1' useflag from fielditem where   fieldsetid='A01' ");
					str.append("select 'A01' fieldsetid,'E01A1' itemid,1 displayid,'岗位名称' itemdesc,'A' itemtype,30 itemlength,0 decimalwidth,'@K' codesetid,27 displaywidth,'' state,'1' useflag from fielditem where fieldsetid='A01' ");
					str.append("union select 'A01' fieldsetid,'B0110' itemid,1 displayid,'单位名称' itemdesc,'A' itemtype,30 itemlength,0 decimalwidth,'UN' codesetid,27 displaywidth,'' state,'1' useflag from fielditem where fieldsetid='A01' ");
					str.append("union "+ sql[0]);
				}
				reqhm.remove("inputfield");
				reqhm.remove("group");
			}
			hm.put("bsql",str.toString());
			hm.put("bwhere",sql[1]);
			hm.put("bcolumn",sql[2]);
			hm.put("borderby",sql[3]);
			hm.put("itemsel",itemsel);
			hm.put("fieldsel",groupinfo);
			reqhm.remove("query");
			//统计指标总数  wangb 20180511 
			StringBuffer countStr = new StringBuffer();
			countStr.append("select count(1) from (");
			countStr.append(str.toString() + sql[1]);
			countStr.append(") b");
			try {
				this.frowset = dao.search(countStr.toString());
				if(this.frowset.next())
					hm.put("fielditemcount",String.valueOf(this.frowset.getInt(1)+20));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else{
//			导入指标
			String fieldid=(String) reqhm.get("fieldid");
//			reqhm.remove("fieldid");
//			BusiSQLStr b;
			ArrayList selitem=(ArrayList) hm.get("selitem");
			int displayid = this.getDisplayid(fieldid, dao);
			for(int i=0;i<selitem.size();i++){
				RecordVo busiFiledVo=this.getBusiFieldVo((DynaBean)selitem.get(i),fieldid,dao,displayid);
				busiFiledVo.setString("ownflag",(String) hm.get("ownflag"));
				busiFiledVo.setString("keyflag","0");
				busiFiledVo.setString("state","1");
				busiFiledVo.setString("useflag","0");
				busiFiledVo.setString("codeflag","0");
				displayid++;
//				try{
					if(!dao.isExistRecordVo(busiFiledVo))
						dao.addValueObject(busiFiledVo);	
//				}catch(Exception e){
//					selitem=new ArrayList();
//					hm.put("selitem",selitem);
//					throw GeneralExceptionHandler.Handle(new GeneralException("","<"+busiFiledVo.getString("itemdesc")+">指标已经存在，请选择其他指标！","",""));
//				}
			}
			
			
			
		}
	}
	private RecordVo getBusiFieldVo(DynaBean dynabean,String fieldid,ContentDAO dao,int displayid){
		RecordVo busiFieldVo=new RecordVo("t_hr_busiField");
		String itemidisplay;
		try {
			//未使用itemidisplay参数，此处注销，jingq upd 2015.01.22
			//itemidisplay = Serialnumber.getSerialnum(dao,fieldid);
			//String[] itemdip=itemidisplay.split("/");
			busiFieldVo.setString("fieldsetid",fieldid);
			//busiFieldVo.setString("itemid",itemdip[0]);
			busiFieldVo.setString("displayid",displayid+"");
			busiFieldVo.setString("codesetid",(String) dynabean.get("codesetid"));
			busiFieldVo.setString("itemid",(String) dynabean.get("itemid"));
			busiFieldVo.setString("itemtype",(String) dynabean.get("itemtype"));
			busiFieldVo.setString("itemdesc",(String) dynabean.get("itemdesc"));
			busiFieldVo.setString("itemlength",(String) dynabean.get("itemlength"));
			busiFieldVo.setString("decimalwidth",(String) dynabean.get("decimalwidth"));
			busiFieldVo.setString("itemmemo",(String) dynabean.get("itemmemo"));
			busiFieldVo.setString("displaywidth",(String) dynabean.get("displaywidth"));
			busiFieldVo.setString("state","0");
			busiFieldVo.setString("auditinginformation",(String) dynabean.get("auditinginformation"));
			busiFieldVo.setString("auditingformula",(String) dynabean.get("auditingformula"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return busiFieldVo;
	}
	private int getDisplayid(String fieldsetid,ContentDAO dao)
	{
		int n=0;
		try
		{
			String sql = "select MAX(displayid) as displayid from t_hr_busiField where UPPER(fieldsetid)='"+fieldsetid.toUpperCase()+"'";
			this.frowset=dao.search(sql);
			while(this.frowset.next())
			{
				n=Integer.parseInt(PubFunc.NullToZero(this.frowset.getString("displayid")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return (int)(n+1);
	}

}
