package com.hjsj.hrms.transaction.general.muster;

import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import com.hjsj.hrms.businessobject.general.muster.MusterXMLStyleBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class OpenMusterTableTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		try {

			
			String isImportData="0";
			if(hm.get("isImportData")!=null)
			{
				isImportData=(String)hm.get("isImportData");
				hm.remove("isImportData");
			}
			String currid =""; 
            if(hm.get("tabid")!=null)
            {
            	currid=(String)hm.get("tabid");
            	hm.remove("tabid");
            }
            else
            {
            	currid=(String)this.getFormHM().get("currid");
            }
				
            /** 未定义信息类别,默认为人员信息 */
            String infor_kind = "1";
            if(hm.get("infor_Flag")!=null) {
                infor_kind = (String)hm.get("infor_Flag");
                hm.remove("infor_Flag");
            }
            
			if (currid.trim().length()<1)
				throw new GeneralException(ResourceFactory.getProperty("error.muster.notselect"));
			if(!this.userView.isSuper_admin()&&!this.userView.isHaveResource(IResourceConstant.MUSTER, currid))
				throw GeneralExceptionHandler.Handle(new Exception("没有操作该花名册的权限！"));
			ArrayList dblist = this.userView.getPrivDbList();
			DbNameBo dbvo = new DbNameBo(this.getFrameconn());
			dblist = dbvo.getDbNameVoList(dblist);
			ArrayList list = new ArrayList();
			String dbpre = (String)this.getFormHM().get("dbpre");
			dbpre = dbpre != null ? dbpre : "";

			if (dbpre.trim().length() < 1 && dblist.size() > 0) {
				RecordVo vo = (RecordVo) dblist.get(0);
				dbpre = vo.getString("pre");
			}
			
			if("1".equals(infor_kind)) {
    			for (int i = 0; i < dblist.size(); i++) {
    				CommonData vo = new CommonData();
    				RecordVo dbname = (RecordVo) dblist.get(i);
    	
    				vo.setDataName(dbname.getString("dbname")!=null?dbname.getString("dbname"):"");
    				vo.setDataValue(dbname.getString("pre")!=null?dbname.getString("pre"):"");
    				list.add(vo);
    			}
			}
			
			dbpre = dbpre != null && dbpre.trim().length() > 0 ? dbpre : "Usr";

			String result = (String) this.getFormHM().get("result");
			result = result != null && result.trim().length() > 0 ? result: "0";
			ContentDAO dao = new ContentDAO(this.getFrameconn());

			MusterBo musterbo = new MusterBo(this.getFrameconn(), this.userView);
			
			String tabname = null;
			if (currid.trim().length()<1)
				throw new GeneralException(ResourceFactory.getProperty("error.muster.notdata"));
			
			String conid=(String)this.getFormHM().get("condid");
			conid=conid!=null?conid:"";
			
			ArrayList condlist = musterbo.getCondList(infor_kind, this.userView);
			/*if(conid.trim().length()<1&&condlist.size()>0){
				CommonData vo = (CommonData)condlist.get(0);
				conid = vo.getDataValue();
			}*/
			condlist.add(0,new CommonData("","全部"));
			if(!musterbo.condExists(conid, condlist))
			    conid = "";
			String res = (String)hm.get("res");
			res=res!=null?res:"0";
			
			if (musterbo.openMusterTable(infor_kind, dbpre, currid,this.userView.getUserName()))
				tabname = musterbo.getTableName(infor_kind, dbpre, currid,this.userView.getUserName());
			MusterXMLStyleBo mxbo=new MusterXMLStyleBo(this.getFrameconn(),currid);
			String commonQueryId=mxbo.getParamValue2(MusterXMLStyleBo.Param, "usual_query");
			if(commonQueryId!=null&&!"".equals(commonQueryId))
				condlist = new ArrayList();
			if(commonQueryId!=null&&!"".equals(commonQueryId)&& "1".equals(isImportData))
			{
				/*RecordVo vo = new RecordVo("LExpr");
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
					SearchInformBo searchInformBo = new SearchInformBo(this.frameconn,this.userView,"all",dbpre);
			    	if(this.getUserView().getStatus()==4)
			    	{
			    		 searchInformBo.saveSelfServiceQueryResult(vo.getString("lexpr"), vo.getString("factor"), "2", vo.getString("history"),vo.getString("fuzzyflag"), result, infor_kind, "", 2);
			    	}
			    	else
			    	{
			    		String wherestr = searchInformBo.strWhere(vo.getString("lexpr"),vo.getString("factor"),"2",vo.getString("history"),vo.getString("fuzzyflag"),"0",infor_kind,"");
		         	    searchInformBo.saveQueryResult(infor_kind,wherestr);
			    	}
				}*/
				
				conid=commonQueryId;
			}
			if ("1".equals(res)) {
				/** 是否包含历史记录 */
				String history = "0";
			    musterbo.runCondTable(conid,"", dbpre, infor_kind);
				this.frowset = dao.search("select expr from lbase where tabid="+ currid);
				if (this.frowset.next()) {
					String expr = Sql_switcher.readMemo(this.frowset, "expr");
					if (expr != null && "1".equals(expr.trim()))
						history = "1";

				}
				if (musterbo.createMusterTempTable(infor_kind, dbpre, currid,this.userView.getUserName().trim(), history))
					tabname = musterbo.getTableName(infor_kind, dbpre,currid, this.userView.getUserName().trim());

			}
			ArrayList fieldlist = musterbo.getFieldlist();
			StringBuffer column=new StringBuffer();
			ArrayList itemlist = new ArrayList();
			for (int i = 0; i < fieldlist.size(); i++) {
				Field field = (Field) fieldlist.get(i);
				if (!"recidx".equalsIgnoreCase(field.getName())&& "0".equals(this.userView.analyseFieldPriv(field.getName())))
					field.setVisible(false);
				if ("1".equals(this.userView.analyseFieldPriv(field.getName())))
					field.setReadonly(true);
				if ("recidx".equalsIgnoreCase(field.getName()))
					field.setReadonly(true);
				field.setSortable(false);
				FieldItem fielditem = DataDictionary.getFieldItem(field.getName());
				if(fielditem!=null&& "D".equalsIgnoreCase(fielditem.getItemtype()))
				{
		    		if(fielditem.getItemlength()==10)
		    			column.append(","+field.getName());
			    	else if(fielditem.getItemlength()==7)
			    	{
			    		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			    		{
			    			column.append(","+Sql_switcher.substr(field.getName(), "0", "7")+" as "+field.getName());
			    		}else
			        		column.append(","+Sql_switcher.substr(field.getName(), "0", "8")+" as "+field.getName());
			    	}else if(fielditem.getItemlength()==4){
			    		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			    		{
			    			column.append(","+Sql_switcher.substr(field.getName(), "0", "4")+" as "+field.getName());
			    		}else
			    	    	column.append(","+Sql_switcher.substr(field.getName(), "0", "5")+" as "+field.getName());
			    	}else
			    		column.append(","+field.getName());
				}
				else
				{
					column.append(","+field.getName());
				}
				itemlist.add(field);
			}

			String a_code = this.userView.getManagePrivCode()
					+ this.userView.getManagePrivCodeValue();
			a_code = a_code != null ? a_code : "";

			this.getFormHM().put("a_code", a_code);
			this.getFormHM().put("mustername", tabname);
			/** 把花名册指标-->显示的数据格式 */
			this.getFormHM().put("fieldlist", itemlist);
			this.getFormHM().put("condlist",condlist);
			this.getFormHM().put("condid",conid);
			String countPerson = "";
			if(infor_kind!=null&& "1".equals(infor_kind)){
				RowSet rs = dao.search("select count(A0100) from "+tabname+" group by A0100");
				int i=0;
				while(rs.next()){
					i++;
				}
				countPerson="(总人数:"+i+"人)";
			}
			//String sql = "select * from " + tabname + " order by recidx";
			String sql="select "+column.toString().substring(1)+" from "+tabname+" order by recidx";
			this.getFormHM().put("sql", sql);
			this.getFormHM().put("countStr",countPerson);
			this.getFormHM().put("currid", currid);
			this.getFormHM().put("infor_Flag", infor_kind);
			this.getFormHM().put("dbpre", dbpre);
			this.getFormHM().remove("save_flag");
			this.getFormHM().put("dblist", list);
			this.getFormHM().put("coumsize", itemlist.size() + "");
			String title = titleName(dao, currid, infor_kind);
			title = title != null ? title : "";
			this.getFormHM().put("mustertitle", title);
			this.getFormHM().put("sortitem",musterbo.getOrderFieldStr(currid));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	private String titleName(ContentDAO dao,String tabid,String infor_kind){
		StringBuffer title= new StringBuffer();
		try {
			String content = "";
			this.frowset = dao.search("select Title from lname where Tabid="+tabid);
			if(this.frowset.next()){
				content=this.frowset.getString("Title");
			}
			content=content!=null&&content.trim().length()>0?content:"";
			if(content.trim().length()>0){
				title.append(content);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return title.toString();
	}
}
