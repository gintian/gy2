package com.hjsj.hrms.transaction.general.inform.search;

import com.hjsj.hrms.businessobject.general.inform.search.SearchInformBo;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceParser;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class EditSearchCondTrans extends IBusiness {

	public void execute() throws GeneralException {
		String check="no";
		String type = (String)this.getFormHM().get("type");
		type=type!=null&&type.trim().length()>0?type:"1";
		
		String flag = (String)this.getFormHM().get("flag");
		flag=flag!=null&&flag.trim().length()>0?flag:"alert";
		
		String id = (String)this.getFormHM().get("id");
		id=id!=null&&id.trim().length()>0?id:"";
		
		String name = (String)this.getFormHM().get("name");
		name=name!=null&&name.trim().length()>0?name:"";
		
		boolean isExistField = true;//判断所有筛选条件指标在结果表里是否都存在
		String wherestr = "";
		String wheresql = "";//花名册常用查询，将wheresql传回前台
		ContentDAO dao = new ContentDAO(this.frameconn);
		if("insert".equalsIgnoreCase(flag)){
			String factor = (String)this.getFormHM().get("factor");
			factor=factor!=null&&factor.trim().length()>0?factor:"";
			factor=SafeCode.decode(factor);
			factor=PubFunc.reBackWord((factor));
			factor=PubFunc.keyWord_reback(factor);
			
			String lexpr = (String)this.getFormHM().get("lexpr");
			lexpr=lexpr!=null&&lexpr.trim().length()>0?lexpr:"";
			lexpr=PubFunc.keyWord_reback(lexpr);
			
			String history = (String)this.getFormHM().get("history");
			history=history!=null&&history.trim().length()>0?history:"";
			
			String like = (String)this.getFormHM().get("like");
			like=like!=null&&like.trim().length()>0?like:"";
			
			RecordVo vo = new RecordVo("LExpr");
			vo.setInt("id",Integer.parseInt(id));
			vo.setString("name",name);
			vo.setString("lexpr",lexpr);
			vo.setString("factor",factor.toUpperCase());
			vo.setInt("type",Integer.parseInt(type));
			vo.setString("moduleflag","10000000000000000000");
			vo.setInt("history",Integer.parseInt(history));
			vo.setInt("fuzzyflag",Integer.parseInt(like));
			dao.addValueObject(vo);
			savePriv(id);
			check="ok";
		}else if("alert".equalsIgnoreCase(flag)){
			String factor = (String)this.getFormHM().get("factor");
			factor=factor!=null&&factor.trim().length()>0?factor.toUpperCase():"";
			factor=SafeCode.decode(factor);
			factor=PubFunc.reBackWord((factor));
			factor=PubFunc.keyWord_reback(factor);
			
			String lexpr = (String)this.getFormHM().get("lexpr");
			lexpr=lexpr!=null&&lexpr.trim().length()>0?lexpr:"";
			lexpr=PubFunc.keyWord_reback(lexpr);
			
			String history = (String)this.getFormHM().get("history");
			history=history!=null&&history.trim().length()>0?history:"";
			
			String like = (String)this.getFormHM().get("like");
			like=like!=null&&like.trim().length()>0?like:"";
			
			RecordVo vo = new RecordVo("LExpr");
			vo.setInt("id",Integer.parseInt(id));
			try {
				vo = dao.findByPrimaryKey(vo);
				vo.setString("name",name);
				vo.setString("factor",factor);
				vo.setString("lexpr",lexpr);
				vo.setString("history",history);
				vo.setString("fuzzyflag",like);
				dao.updateValueObject(vo);
				check="ok";
			} catch (GeneralException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else if("delete".equalsIgnoreCase(flag)){
			RecordVo vo = new RecordVo("LExpr");
			vo.setInt("id",Integer.parseInt(id));
			try {
				vo = dao.findByPrimaryKey(vo);
				dao.deleteValueObject(vo);
				this.getFormHM().put("titlelist",searchTable(type));
				check="ok";
			} catch (GeneralException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else if("search".equalsIgnoreCase(flag)){
			 //查询的范围 [UM UN]
		     String a_code=(String)this.getFormHM().get("a_code");
		     a_code=a_code!=null&&a_code.trim().length()>0?a_code:"all";
		     
		     String unite=(String)this.getFormHM().get("unite");
		     unite=unite!=null&&unite.trim().length()>0?unite:"2";
		     //库前缀
		     String tablename=(String)this.getFormHM().get("tablename");
		     tablename=tablename!=null&&tablename.trim().length()>0?tablename:"";
		     //全部人员库,考勤发卡有全部人员库 选择全部人员库查询会出现错误 wangy
		     ArrayList dblist=new ArrayList();
		     if("".equals(tablename)|| "all".equals(tablename)||tablename==null)
		     {
		    	 String all_code=a_code.substring(2, a_code.length());
			     String qz_table=a_code.substring(0,a_code.length()-2); //前缀
			     String kind="";
			     if("UM".equalsIgnoreCase(qz_table))
					    kind="1";
					else if("UN".equalsIgnoreCase(qz_table))
					    kind="2";
			     dblist=RegisterInitInfoData.getDbList(all_code,kind,this.getFormHM(),this.userView,this.getFrameconn()); 
		     }
		     
			RecordVo vo = new RecordVo("LExpr");
			vo.setInt("id",Integer.parseInt(id));
			try {
				vo = dao.findByPrimaryKey(vo);
				boolean bhis = false;
				if(("1").equals(vo.getString("history")))
		        	bhis=true;
				String userName = this.userView.getUserName().trim().replaceAll(" ", "");
				FactorList factorslist=new FactorList(vo.getString("lexpr"),vo.getString("factor"),"Usr",bhis ,false,false,Integer.parseInt(type),userView.getUserId());
    		    isExistField = this.getIsExistField(userName,factorslist.getFieldList());
				wheresql = factorslist.getSingleTableSqlExpression("T#"+userName+"_mus0");
				wheresql = getTo_Date(userName, wheresql, factorslist.getFieldList());
				if(this.userView.getStatus()==0)
				{
		    		if(!"".equals(tablename)&&!"all".equals(tablename)&&tablename!=null)
		    		{
			    		SearchInformBo searchInformBo = new SearchInformBo(this.frameconn,this.userView,a_code,tablename);
		    		    wherestr = searchInformBo.strWhere(vo.getString("lexpr"),
				    		vo.getString("factor"),"2",vo.getString("history"),vo.getString("fuzzyflag"),"0",type,unite);
			    	    if(searchInformBo.saveQueryResult(type,wherestr)){
			    	    	check="ok";
			    	    }
			    	}else
			    	{
			    		SearchInformBo searchInformBo = new SearchInformBo(this.frameconn,this.userView,a_code,dblist);
			    	    if(searchInformBo.saveQueryResultAll(vo.getString("lexpr"),
				        		vo.getString("factor"),"2",vo.getString("history"),vo.getString("fuzzyflag"),"0",type,unite,type))
				        {
				        	check="ok";
			    	    }
		    		}
				}
		    	else if(this.userView.getStatus()==4)
		    	{
		    		 if(!"".equals(tablename)&&!"all".equals(tablename))
		    	     {
			    		 SearchInformBo searchInformBo = new SearchInformBo(this.frameconn,this.userView,a_code,tablename);
			    		 searchInformBo.saveSelfServiceQueryResult(vo.getString("lexpr"), vo.getString("factor"), "2", vo.getString("history"), vo.getString("fuzzyflag"), "0", type, unite, 2);
		    	     }
			    	 else
			    	 {
			    		 SearchInformBo searchInformBo = new SearchInformBo(this.frameconn,this.userView,a_code,dblist);
			    		 searchInformBo.saveSelfServiceQueryResult(vo.getString("lexpr"), vo.getString("factor"), "2", vo.getString("history"), vo.getString("fuzzyflag"), "0", type, unite, 1);
			    	 }
			    	 check="ok";
		    	}
//				SearchInformBo searchInformBo = new SearchInformBo(this.frameconn,this.userView,a_code,tablename);
//			    String wherestr = searchInformBo.strWhere(vo.getString("lexpr"),
//			    		vo.getString("factor"),"2",vo.getString("history"),vo.getString("fuzzyflag"),"0",type,unite);
//			    if(searchInformBo.saveQueryResult(type,wherestr)){
//			    	check="ok";
//			    }
			} catch (GeneralException e) {
				throw GeneralExceptionHandler.Handle(e);
			} catch (SQLException e) {
				throw GeneralExceptionHandler.Handle(e);
			}
			this.getFormHM().put("titlelist",searchTable(type));
		}else if("alertname".equalsIgnoreCase(flag)){
			this.getFormHM().put("titlelist",searchTable(type));
		}else if("editsearch".equalsIgnoreCase(flag)){
			check="ok";
		}
		this.getFormHM().put("check",check);
		this.getFormHM().put("isExistField",isExistField);
		this.getFormHM().put("wherestr",SafeCode.encode(wherestr));
		this.getFormHM().put("wheresql",SafeCode.encode(wheresql));
		this.getFormHM().put("id",id+":"+name);
		
	}
	/**
	 * 查询条件
	 * @param type
	 * @return
	 */
	private ArrayList searchTable(String type){
		ArrayList titlelist = new ArrayList();
		
		String strsql = "select id,name from lexpr where Type="+type;
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search(strsql);
			int n=1;
			while(this.frowset.next()){
				String id = this.frowset.getString("id");
				String name = this.frowset.getString("name");
				
				if(!(this.userView.isHaveResource(IResourceConstant.LEXPR,id)))
                	continue;
				
				CommonData obj=new CommonData(id+":"+name,id+"."+name);
				titlelist.add(obj);
				n++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return titlelist;
	}
	private void savePriv(String id){
		SysPrivBo privbo=new SysPrivBo(this.userView.getUserName(),this.userView.getStatus()+"",this.getFrameconn(),"warnpriv");
		String res_str=privbo.getWarn_str();
		ResourceParser parser=new ResourceParser(res_str,2);
		String str_value = parser.getContent();
		str_value=str_value!=null&&str_value.trim().length()>0?str_value:"";
		str_value=(str_value.trim().length()>0?str_value+",":"")+id+",";
		UserObjectBo user_bo = new UserObjectBo(this.getFrameconn());
		try {
			if(str_value!=null&&str_value.trim().length()>0)
				user_bo.saveResource(str_value, this.userView,
						IResourceConstant.LEXPR);
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parser.reSetContent(str_value);
		res_str=parser.outResourceContent();
		saveResourceString(this.userView.getUserName(),this.userView.getStatus()+"",res_str);
	}
	private void saveResourceString(String role_id,String flag,String res_str){
        if(res_str==null)
        	res_str="";
        RecordVo vo=new RecordVo("t_sys_function_priv");
        vo.setString("id",role_id);
        vo.setString("status",flag/*GeneralConstant.ROLE*/);
        vo.setString("warnpriv",res_str);
        cat.debug("role_vo="+vo.toString());	
        SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
        sysbo.save();   
        
    }
	
	/**
	 * 判断花名册结果表是否有不存在指标列
	 * @param fieldList
	 * @return
	 */
	private boolean getIsExistField(String userName, ArrayList fieldList){
		boolean isExistField = true;
		for (int i=0;i<fieldList.size();i++){
	    	DbWizard dbw=new DbWizard(this.frameconn);
	    	FieldItem fielditem=(FieldItem)fieldList.get(i);
			String fieldname=fielditem.getItemid();
	    	if(!dbw.isExistField("T#"+userName+"_mus0", fieldname, false))
	    		isExistField = false;
	    }
		return isExistField;
	}
	/**
	 * 将oracle库里花名册结果表里面的varchar类型的日期型数据临时转成日期型
	 * oracle库取日期年月函数不能对字符串使用，sqlserver可以
	 * @param userName
	 * @param wheresql
	 * @param fieldList
	 * @return
	 */
	private String getTo_Date(String userName, String wheresql, ArrayList fieldList){
		for (int i=0;i<fieldList.size();i++){
	    	FieldItem fielditem=(FieldItem)fieldList.get(i);
	    	String fieldname=fielditem.getItemid();
	    	String itemType=fielditem.getItemtype();
			if("D".equals(itemType)&&Sql_switcher.searchDbServer()==Constant.ORACEL){
				wheresql = wheresql.replaceAll("T#"+userName+"_mus0."+fieldname.toUpperCase(), "TO_DATE(T#"+userName+"_mus0."+fieldname.toUpperCase()+",'yyyy-mm-dd')");
			}
	    }
		return wheresql;
	}
	
}
