package com.hjsj.hrms.transaction.general.deci.leader;

import com.hjsj.hrms.businessobject.general.deci.leader.LeadarParamXML;
import com.hjsj.hrms.businessobject.general.deci.leader.LeaderParam;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 班子分析
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 7, 2007:2:58:23 PM</p> 
 *@author dengcan
 *@version 4.0
 */
public class LeaderParamTrans  extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList field_list=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
		ArrayList unit_list=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
		ArrayList user_field_list=new ArrayList();
		ArrayList unit_field_list = new ArrayList();
		CommonData dataobj=new CommonData();
		dataobj.setDataName("请选择");
		dataobj.setDataValue("");
		user_field_list.add(dataobj);		
		for(int i=0;i<field_list.size();i++)
		{
			FieldSet fielditem=(FieldSet)field_list.get(i);
			dataobj = new CommonData(fielditem.getFieldsetid(), fielditem.getFieldsetdesc());
			user_field_list.add(dataobj);
		}
		for(int i=0;i<unit_list.size();i++)
		{
			FieldSet fielditem=(FieldSet)unit_list.get(i);
			dataobj = new CommonData(fielditem.getFieldsetid(), fielditem.getFieldsetdesc());
			unit_field_list.add(dataobj);
		}
		LeadarParamXML leadarParamXML=new LeadarParamXML(this.getFrameconn());
		this.getFormHM().put("user_field_list",user_field_list);
		this.getFormHM().put("unit_field_list",unit_field_list);
		bzParamSet(leadarParamXML,user_field_list);//班子
		hbParamSet(leadarParamXML,user_field_list);
		unitParamSet(leadarParamXML,unit_field_list);
		LeaderParam leaderParam=new LeaderParam(this.getFrameconn(),this.userView);	
        //基本指标
		String output_field=leadarParamXML.getTextValue(LeadarParamXML.OUTPUT);			
		ArrayList list=leaderParam.getFields(output_field);
		String output_mess=getMess(list);		
		this.getFormHM().put("output_mess",output_mess);
		//显示指标列表
		String display_field=leadarParamXML.getTextValue(LeadarParamXML.DISPLAY);			
		list=leaderParam.getFields(display_field);
		String display_mess=getMess(list);
		this.getFormHM().put("display_mess",display_mess);
		
		String condi_display_field=leadarParamXML.getTextValue(LeadarParamXML.CONDI_DISPLAY);			
		list=leaderParam.getFields(condi_display_field);
		String condi_display_mess=getMess(list);
		this.getFormHM().put("condi_display_mess",condi_display_mess);
		
		//常用统计
		String gcond=leadarParamXML.getTextValue(LeadarParamXML.GCOND);	
		if(gcond==null||gcond.length()<=0)
			gcond="";
		list=leaderParam.getSelectSname(gcond);
		String gcond_mess=getMess(list);
		this.getFormHM().put("gcond_mess",gcond_mess);
		//单位登记表
		String unit_card=leadarParamXML.getTextValue(LeadarParamXML.UNIT_CARD);	
		if(unit_card==null||unit_card.length()<=0)
			unit_card="";
		list=leaderParam.getSelectRname(unit_card);
		String unit_mess=getMess(list);
		this.getFormHM().put("unit_mess",unit_mess);
		//班子库
		String bz_pre=leadarParamXML.getTextValue(LeadarParamXML.BZDBPRE);			
		//ArrayList bz_list=leaderParam.getFields(bz_pre);
		String bz_mess=getDBMess(bz_pre);		
		this.getFormHM().put("bz_mess",bz_mess);
		//后备库
		String hb_pre=leadarParamXML.getTextValue(LeadarParamXML.HBDBPRE);			
		//ArrayList hb_list=leaderParam.getFields(hb_pre);
		String hb_mess=getDBMess(hb_pre);		
		this.getFormHM().put("hb_mess",hb_mess);
		//单位子集信息
		String unitfile_field=leadarParamXML.getTextValue(LeadarParamXML.UNIT_ZJ);			
		//ArrayList unitfilelist=leaderParam.getFields(unitfile_field);
		String unitfile_mess=leaderParam.getUnitMess(unitfile_field);		
		this.getFormHM().put("unitfile_mess",unitfile_mess);
		//组织机构树设置
		String loadtype_field=leadarParamXML.getTextValue(LeadarParamXML.LOADTYPE);			
		//ArrayList unitfilelist=leaderParam.getFields(unitfile_field);
		//String loadtype_mess=leaderParam.getLoadMess(loadtype_field);		
		this.getFormHM().put("loadtype_mess",loadtype_field);
		
	}
	/**
	 * 班子
	 * @param user_field_list
	 */
	private void bzParamSet(LeadarParamXML leadarParamXML,ArrayList user_field_list)
	{
		 
		String bz_fieldsetid=leadarParamXML.getValue(LeadarParamXML.TEAM_LEADER,"setid");
		if(bz_fieldsetid==null||bz_fieldsetid.length()<=0)
		{
			CommonData fielditem=(CommonData)user_field_list.get(0);
			bz_fieldsetid=fielditem.getDataValue();
		}
		this.getFormHM().put("bz_fieldsetid",bz_fieldsetid);//人员集指标
		LeaderParam leaderParam=new LeaderParam(this.getFrameconn(),this.userView);		
		ArrayList bz_codesetlist=leaderParam.getFieldBySetNameTrans(bz_fieldsetid,this.userView);	    
 	    String bz_codesetid=leadarParamXML.getValue(LeadarParamXML.TEAM_LEADER,"fielditem");
 	    if(bz_codesetid==null||bz_codesetid.length()<=0)
 	    {
 	    	if(bz_codesetlist!=null&&bz_codesetlist.size()>0)
 	    	{
 	    		CommonData dataobj =(CommonData)bz_codesetlist.get(0);
 	 	    	bz_codesetid=dataobj.getDataValue();
 	    	}
 	    	
 	    }
 	    ArrayList bz_codeitemlist=leaderParam.codeItemList(bz_fieldsetid,bz_codesetid);
 	    String bz_codeitemid=leadarParamXML.getValue(LeadarParamXML.TEAM_LEADER,"value");;
 	    this.getFormHM().put("bz_codesetid",bz_codesetid);//代码指标
 	    this.getFormHM().put("bz_codesetlist",bz_codesetlist);	//代码指标
 	    this.getFormHM().put("bz_codeitemlist",bz_codeitemlist);//代码值
 	    this.getFormHM().put("bz_codeitemid",bz_codeitemid);//代码值
	}
	/**
	 * 班子
	 * @param user_field_list
	 */
	private void hbParamSet(LeadarParamXML leadarParamXML,ArrayList user_field_list)
	{
		String hb_fieldsetid=leadarParamXML.getValue(LeadarParamXML.CANDID_LEADER,"setid");		
		if(hb_fieldsetid==null||hb_fieldsetid.length()<=0)
		{
			CommonData fielditem=(CommonData)user_field_list.get(0);
			hb_fieldsetid=fielditem.getDataValue();
		}
		this.getFormHM().put("hb_fieldsetid",hb_fieldsetid);//人员集指标
		LeaderParam leaderParam=new LeaderParam(this.getFrameconn(),this.userView);		
		ArrayList hb_codesetlist=leaderParam.getFieldBySetNameTrans(hb_fieldsetid,this.userView);	    
 	    String hb_codesetid=leadarParamXML.getValue(LeadarParamXML.CANDID_LEADER,"fielditem");
 	    if(hb_codesetid==null||hb_codesetid.length()<=0)
 	    {
 	    	if(hb_codesetlist!=null&&hb_codesetlist.size()>0)
 	    	{
 	    		CommonData dataobj =(CommonData)hb_codesetlist.get(0);
 	 	    	hb_codesetid=dataobj.getDataValue();
 	    	}
 	    }
 	    ArrayList hb_codeitemlist=leaderParam.codeItemList(hb_fieldsetid,hb_codesetid);
 	    String hb_codeitemid=leadarParamXML.getValue(LeadarParamXML.CANDID_LEADER,"value");		
 	    this.getFormHM().put("hb_codesetid",hb_codesetid);//代码指标
 	    this.getFormHM().put("hb_codesetlist",hb_codesetlist);	//代码指标
 	    this.getFormHM().put("hb_codeitemlist",hb_codeitemlist);//代码值
 	    this.getFormHM().put("hb_codeitemid",hb_codeitemid);//代码值
	}
	private void unitParamSet(LeadarParamXML leadarParamXML,ArrayList unit_field_list)
	{
		String unit_fieldsetid=leadarParamXML.getValue(LeadarParamXML.UNIT_ZJ,"setid");		
		if(unit_fieldsetid==null||unit_fieldsetid.length()<=0)
		{
			CommonData fielditem=(CommonData)unit_field_list.get(0);
			unit_fieldsetid=fielditem.getDataValue();
		}
		this.getFormHM().put("unit_fieldsetid",unit_fieldsetid);//人员集指标
		LeaderParam leaderParam=new LeaderParam(this.getFrameconn(),this.userView);		
		ArrayList unit_codesetlist=leaderParam.getFieldBySetNameTrans(unit_fieldsetid,this.userView);	    
 	    String unit_codesetid=leadarParamXML.getValue(LeadarParamXML.UNIT_ZJ,"fielditem");
 	    if(unit_codesetid==null||unit_codesetid.length()<=0)
 	    {
 	    	if(unit_codesetlist!=null&&unit_codesetlist.size()>0)
 	    	{
 	    		CommonData dataobj =(CommonData)unit_codesetlist.get(0);
 	    		unit_codesetid=dataobj.getDataValue();
 	    	}
 	    }
 	    ArrayList unit_codeitemlist=leaderParam.codeItemList(unit_fieldsetid,unit_codesetid);
 	    String unit_codeitemid=leadarParamXML.getValue(LeadarParamXML.UNIT_ZJ,"value");
 	    this.getFormHM().put("unit_codesetid",unit_codesetid);//代码指标
 	    this.getFormHM().put("unit_codesetlist",unit_codesetlist);	//代码指标
 	    this.getFormHM().put("unit_codeitemlist",unit_codeitemlist);//代码值
 	    this.getFormHM().put("unit_codeitemid",unit_codeitemid);//代码值
	}
	
	private String getMess(ArrayList list)
	{
		StringBuffer mess=new StringBuffer();
		if(list==null||list.size()<=0)
			return "";
		int r=1;
		for(int i=0;i<list.size();i++)
		{
			 CommonData dataobj =(CommonData)list.get(i);
			 mess.append(dataobj.getDataName());
			 if(r%5==0)
    			   mess.append("<br>");
    		   else
    			 mess.append(",");  
    	    r++;
		}
		return mess.toString();
	}
	private String getDBMess(String dbpre){
		String[] pres = dbpre.trim().split(",");
		StringBuffer dbpres = new StringBuffer();
		ArrayList dblist = new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql = "select dbname,pre from dbname";
		try {
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				RecordVo vo = new RecordVo("dbname");
				vo.setString("pre",this.frowset.getString("pre"));
				vo.setString("dbname",this.frowset.getString("dbname"));
				dblist.add(vo);
			}
		} catch (SQLException e) {e.printStackTrace();}
		for(int i=0;i<pres.length;i++){
			for(int j=0;j<dblist.size();j++){
				RecordVo db = (RecordVo)dblist.get(j);
				if(pres[i].equalsIgnoreCase(db.getString("pre").toString()))
					dbpres.append(db.getString("dbname").toString());
			}
			if((i+1)%2==0)
				dbpres.append("<br>");
			else
				dbpres.append(",");
		}
		dbpres.setLength(dbpres.length()-1);
		return dbpres.toString();
	}
}
