/**
 * 
 */
package com.hjsj.hrms.transaction.general.muster;

import com.hjsj.hrms.businessobject.general.inform.search.SearchInformBo;
import com.hjsj.hrms.businessobject.general.muster.ExecuteExcel;
import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import com.hjsj.hrms.businessobject.general.muster.MusterXMLStyleBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Title:OpenMusterTrans</p>
 * <p>Description:打开花名册</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-1-26:14:01:33</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class OpenMusterTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
	  try
	  {	
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String isGetData="0";
		if(hm.get("isGetData")!=null)
		{
			isGetData=(String)hm.get("isGetData");
			hm.remove("isGetData");
		}
		String checkflag=(String)hm.get("checkflag");
		checkflag=checkflag!=null&&checkflag.trim().length()>0?checkflag:"0";
		hm.remove("checkflag");
		
		String hflag=(String)this.getFormHM().get("hflag");
		hflag=hflag!=null&&hflag.trim().length()>0?hflag:"0";
		this.getFormHM().put("hflag", hflag);
		
		String[] tabid=(String[])this.getFormHM().get("tabid");
		if(tabid==null)
			throw new GeneralException(ResourceFactory.getProperty("error.muster.notselect"));
		
        String closeWindow=(String)this.getFormHM().get("closeWindow");
		ArrayList dblist=this.userView.getPrivDbList();
		if("1".equals(hflag)){
			ConstantXml csxml = new ConstantXml(this.frameconn,"HT_PARAM","Params");
			String dbstr=csxml.getTextValue("/Params/nbase");
			dbstr=dbstr!=null?dbstr:"";
			ArrayList list = new ArrayList();
			for(int i=0;i<dblist.size();i++){
				String pre=(String)dblist.get(i);
				if(dbstr.length()>0){
					if(dbstr.toLowerCase().indexOf(pre.toLowerCase())!=-1)
						list.add(pre);
				}
			}
			if(list.size()>0)
				dblist = list;
		}
		String a_code = this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
	    a_code=a_code!=null?a_code:"";
		DbNameBo dbvo=new DbNameBo(this.getFrameconn());
		dblist=dbvo.getDbNameVoList(dblist);
		ArrayList list=new ArrayList();
		String dbpre="";
		for(int i=0;i<dblist.size();i++){
			CommonData vo=new CommonData();
			RecordVo dbname=(RecordVo)dblist.get(i);
			vo.setDataName(dbname.getString("dbname"));
			vo.setDataValue(dbname.getString("pre"));
			list.add(vo);
			if(dbpre!=null&&dbpre.trim().length()<1)
				dbpre=dbname.getString("pre");
		}
		addAllDBItem(list);
		if(hm!=null){
			String dbpre1=(String)hm.get("dbpre");
			dbpre1=dbpre1!=null?dbpre1:"";
			if(dbpre1.length()<1)
				dbpre=dbpre!=null&&dbpre.trim().length()>0?dbpre:"Usr";
			else
				dbpre=dbpre1;
		}
		dbpre=dbpre!=null&&dbpre.trim().length()>0?dbpre:"Usr";
		
		String result=(String)this.getFormHM().get("result");
		result=result!=null&&result.trim().length()>0?result:"0";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		List tablist=Arrays.asList(tabid);
		/**选中多个花名册时,仅以第一个为准*/
		String thetabid=(String)tablist.get(0);
		MusterBo musterbo=new MusterBo(this.getFrameconn(),this.userView);
		String tabname=null;
		if("-1".equals(tabid[0]))
			throw new GeneralException(ResourceFactory.getProperty("error.muster.notselect"));
		/**未定义信息类别,默认为人员信息*/
		String infor_kind=(String)this.getFormHM().get("infor_Flag");
		if(infor_kind==null|| "".equals(infor_kind))
		{
			this.frowset=dao.search("select flag from lname where tabid="+tabid[0]);
			if(this.frowset.next())
				infor_kind=this.frowset.getString(1);
		}
		if(!this.userView.isSuper_admin()&&!this.userView.isHaveResource(IResourceConstant.MUSTER, tabid[0]))
			throw GeneralExceptionHandler.Handle(new Exception("没有操作该花名册的权限！"));
		MusterXMLStyleBo mxbo=new MusterXMLStyleBo(this.getFrameconn(),thetabid);
		String commonQueryId=mxbo.getParamValue2(MusterXMLStyleBo.Param, "usual_query");
		if(commonQueryId!=null&&!"".equals(commonQueryId)&& "1".equals(isGetData))
		{
		    if("1".equals(infor_kind)&&"ALL".equals(dbpre)&&MusterBo.isHkyh())
		        fillAllDBByCommonQuery(a_code, list, commonQueryId, infor_kind, thetabid, result);
		    else
		        fillMusterByCommonQuery(a_code, dbpre, commonQueryId, infor_kind, thetabid, result);
		}
		if(musterbo.openMusterTable(infor_kind,dbpre,thetabid,this.userView.getUserName().trim().replaceAll(" ", "")))
			tabname=musterbo.getTableName(infor_kind,dbpre,thetabid,this.userView.getUserName().trim().replaceAll(" ", ""));
		/**加载表结构,added at 20110315 因启动时不再加载花名册临时表结构*/
		DBMetaModel dbmodel=new DBMetaModel(this.getFrameconn());
		if(!dbmodel.isHaveTheTable(tabname))
			dbmodel.reloadTableModel(tabname);
		/**end.*/
		if(!musterbo.haveDataInMuster(tabname)){
			/** 是否包含历史记录*/
			String history="0";
			
			this.frowset=dao.search("select expr from lbase where tabid="+thetabid);
			if(this.frowset.next())
			{
				String expr=Sql_switcher.readMemo(this.frowset,"expr");
				if(expr!=null&& "1".equals(expr.trim()))
					history="1";
				
			}
			if(!"1".equals(infor_kind))
				history="0";  //目前部门、职位花名册没有提供历史记录功能,所以默认置为 0  dengcan 2008/02/03
			if(musterbo.createTempTable(infor_kind,dbpre,thetabid,this.userView.getUserName().replaceAll(" ", ""),history))
				tabname=musterbo.getTableName(infor_kind,dbpre,thetabid,this.userView.getUserName().trim().replaceAll(" ", "")); 
			 
		}
		ArrayList fieldlist = musterbo.getFieldlist();
		ArrayList itemlist = new ArrayList();
		ArrayList checkitemlist = new ArrayList();
		boolean checkitemid = false;
		for(int i=0;i<fieldlist.size();i++){
			Field field = (Field)fieldlist.get(i);
			if("A0100".equals(field.getName()))
				field.setVisible(false);
//			if(field.getName().equals("B0110")
//						||field.getName().equals("E01A1"))
//					field.setVisible(false);
			if(!"recidx".equalsIgnoreCase(field.getName())&& "0".equals(this.userView.analyseFieldPriv(field.getName())))
				field.setVisible(false);
			if("1".equals(this.userView.analyseFieldPriv(field.getName())))
				field.setReadonly(true);
			if("recidx".equalsIgnoreCase(field.getName()))
				field.setReadonly(true);
			if(!musterbo.haveDataInMuster(tabname,field.getName()))
				checkitemid = true;
			field.setSortable(false);
			if("3".equals(infor_kind)&& "e0122".equalsIgnoreCase(field.getName()))
			{
				field.setLabel("所属部门");
			}
			checkitemlist.add(field.getName());
			FieldItem fielditem = DataDictionary.getFieldItem(field.getName());
			itemlist.add(field);
		}
		if(checkitemid){
			musterbo.addDelTableField(tabname,checkitemlist);
		}
		
	    this.getFormHM().put("a_code",a_code);
		this.getFormHM().put("mustername",tabname);
		/**把花名册指标-->显示的数据格式*/
		this.getFormHM().put("fieldlist",itemlist);
		this.getFormHM().put("condlist",musterbo.getUsuallyCondList(infor_kind,this.userView));
		String countPerson = "";
		if(infor_kind!=null&& "1".equals(infor_kind)){
			int i=0;
	        if("1".equals(infor_kind)&&"ALL".equals(dbpre)&&MusterBo.isHkyh())
	            i=musterbo.calcAllDBPersonCount(infor_kind, list, thetabid);
	        else
			    i=musterbo.calcPersonCount(infor_kind, dbpre, thetabid);
			countPerson="(总人数: "+i+"人)";
		}
		
		String sql="";
		if("1".equals(infor_kind)&&"ALL".equals(dbpre)&&MusterBo.isHkyh())
		    sql=musterbo.getDataSetAllDBSelectSql(infor_kind, list, thetabid);
		else
		    sql=musterbo.getDataSetSelectSql(infor_kind, dbpre, thetabid);
		this.getFormHM().put("sql",sql);
		this.getFormHM().put("countStr",countPerson);
		this.getFormHM().put("currid",thetabid);	
		this.getFormHM().put("infor_Flag",infor_kind);
		this.getFormHM().put("dbpre",dbpre);
		this.getFormHM().remove("save_flag");
		this.getFormHM().put("dblist",list);
		this.getFormHM().put("coumsize",itemlist.size()+"");
		this.getFormHM().put("checkflag",checkflag);
		String title = titleName(dao,thetabid,infor_kind);
		title=title!=null?title:"";
		this.getFormHM().put("mustertitle",title);
		this.getFormHM().put("sortitem",musterbo.getOrderFieldStr(thetabid));
		this.getFormHM().put("closeWindow", closeWindow);
	  }
	  catch(Exception ex) 
	  {
		  ex.printStackTrace();
		  throw GeneralExceptionHandler.Handle(ex);
	  }		
	}
	private String titleName(ContentDAO dao,String tabid,String infor_kind){
		StringBuffer title= new StringBuffer();
		
		ExecuteExcel excel = new ExecuteExcel(this.getFrameconn());
		excel.setPagePrintStyle(tabid);
		String t_fontfamilyname=ResourceFactory.getProperty("gz.gz_acounting.m.font");
        String t_fontEffect="0";
        int    t_fontSize=16;  
        String t_color="#000000";       
        String  t_underLine="#fu[0]";
        String  t_strikethru="#fs[0]";
        if(excel.paramtervo!=null){
        	if(excel.paramtervo.getTitle_fn().length()>0)
        		t_fontfamilyname=excel.paramtervo.getTitle_fn();
	       	 t_fontEffect=excel.getFontEffect(4);
	       	 if(excel.paramtervo.getTitle_fz().length()>0)
	       		 t_fontSize=Integer.parseInt(excel.paramtervo.getTitle_fz());
	       	 if(excel.paramtervo.getTitle_fc().length()>0)
	       		 t_color=excel.paramtervo.getTitle_fc();	       	 
	       	 if(excel.paramtervo.getTitle_fu()!=null&&excel.paramtervo.getTitle_fu().length()>0)
	       		 t_underLine=excel.paramtervo.getTitle_fu();
	       	if(excel.paramtervo.getTitle_fs()!=null&&excel.paramtervo.getTitle_fs().length()>0)
	       		t_strikethru=excel.paramtervo.getTitle_fs();
        }
		try {
			String content = "";
			this.frowset = dao.search("select Title from lname where Tabid="+tabid);
			if(this.frowset.next()){
				content=this.frowset.getString("Title");
			}
			content=content!=null&&content.trim().length()>0?content:"";
			if(content.trim().length()>0){
				int top = 40;
				if(t_fontSize>16)
					top = 30;
				
//				if(infor_kind!=null&&infor_kind.equals("1"))
//					title.append("<div style='position:absolute;top:"+top+";left:580;WIDTH:500;WORD-BREAK:break-all;");
//				else
//					title.append("<div style='position:absolute;top:"+top+";left:520;WIDTH:500;WORD-BREAK:break-all;");
//				title.append("font-size: "+t_fontSize+"px;");
//				title.append("color: "+t_color+";");
//				title.append("FONT-FAMILY: "+t_fontfamilyname+";");
//				if(t_fontEffect.equals("2")){
//					title.append("font-weight:bold;");
//				}else if(t_fontEffect.equals("3")){
//					title.append("font-style:italic;");
//				}else if(t_fontEffect.equals("4")){
//					title.append("font-weight:bold;");
//					title.append("font-style:italic;");
//				}
//				if(t_underLine.equals("#fu[1]")){
//					title.append("text-decoration:underline");
//					if(t_strikethru.equals("#fs[1]"))
//						title.append(" line-through");
//					title.append(";");
//				}else{
//					if(t_strikethru.equals("#fs[1]")){
//						title.append("text-decoration:line-through;");
//					}
//				}
//				title.append("'>");
				title.append(content);
//				title.append("</div>");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return title.toString();
	}
	
	private void addAllDBItem(ArrayList list) {
	    if(list.size()>1&&MusterBo.isHkyh()) {
	        list.add(0, new CommonData("ALL", "全部人员库"));
	    }
	}

    /**
     * 根据自动取数条件重填花名册
     * @param managePriv
     * @param dbpre
     * @param commonQueryId
     * @param infor_kind
     * @param thetabid
     */
    private void fillAllDBByCommonQuery(String managePriv, ArrayList dblist, String commonQueryId, String infor_kind,
            String thetabid, String result) {
        for(int i=0;i<dblist.size();i++){
            CommonData vo=(CommonData)dblist.get(i);
            String dbpre=vo.getDataValue();
            fillMusterByCommonQuery(managePriv, dbpre, commonQueryId, infor_kind, thetabid, result);
        }
    }
	
	/**
	 * 根据自动取数条件重填花名册
	 * @Title: fillMusterByCommonQuery   
	 * @Description:    
	 * @param managePriv
	 * @param dbpre
	 * @param commonQueryId
	 * @param infor_kind
	 * @param thetabid
	 */
	private void fillMusterByCommonQuery(String managePriv, String dbpre, String commonQueryId, String infor_kind,
	        String thetabid, String result) {
	   ContentDAO dao=new ContentDAO(this.getFrameconn());
	   MusterBo musterbo=new MusterBo(this.getFrameconn(),this.userView);
	   managePriv=managePriv!=null&&managePriv.trim().length()>0?managePriv:"all";
       String unite=(String)this.getFormHM().get("unite");
       unite=unite!=null&&unite.trim().length()>0?unite:"2";
       //库前缀
       RecordVo vo = new RecordVo("LExpr");
       vo.setInt("id",Integer.parseInt(commonQueryId));
       boolean flag=true;
       try
       {
           vo = dao.findByPrimaryKey(vo);
       }
       catch(Exception e)
       {
           flag=false;
       }
       if(flag)
       {
           try{
               SearchInformBo searchInformBo = new SearchInformBo(this.frameconn,this.userView,managePriv,dbpre);
               if(this.getUserView().getStatus()==4)
               {
                    searchInformBo.saveSelfServiceQueryResult(vo.getString("lexpr"), vo.getString("factor"), "2", vo.getString("history"),vo.getString("fuzzyflag"), result, infor_kind, unite, 2);
               }
               else
               {
                   String wherestr = searchInformBo.strWhere(vo.getString("lexpr"),vo.getString("factor"),"2",vo.getString("history"),vo.getString("fuzzyflag"),"0",infor_kind,unite);
                   searchInformBo.saveQueryResult(infor_kind,wherestr);
               }
               String returncheck=(String)this.getFormHM().get("returncheck");
               returncheck=returncheck!=null&&returncheck.trim().length()>0?returncheck:"0";
               String history="0";
           
               this.frowset=dao.search("select expr from lbase where tabid="+thetabid);
               if(this.frowset.next())
               {
                   String expr=Sql_switcher.readMemo(this.frowset,"expr");
                   if(expr!=null&& "1".equals(expr.trim()))
                       history="1";
               
               }
               if(!"1".equals(infor_kind))
                   history="0";  
               if("1".equals(returncheck)){
                   if(musterbo.createMusterTempTable(infor_kind,dbpre,thetabid,this.userView.getUserName().trim(),history,managePriv))
                       musterbo.getTableName(infor_kind,dbpre,thetabid,this.userView.getUserName());
               }else{
                   if(musterbo.createMusterTempTable(infor_kind,dbpre,thetabid,this.userView.getUserName().trim(),history))
                       musterbo.getTableName(infor_kind,dbpre,thetabid,this.userView.getUserName());
               }
           }
           catch(Exception e){
               e.printStackTrace();
           }
       }
   	    
	}
	
}
