package com.hjsj.hrms.transaction.general.inform.search;

import com.hjsj.hrms.businessobject.general.inform.search.SearchInformBo;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

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
public class SaveSearchCondTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		 //区别查询类型,[1.简单查询  2.通用查询]
	     String query_type=(String)this.getFormHM().get("query_type");
	     query_type=query_type!=null&&query_type.trim().length()>0?query_type:"1";
	     
	     //因子表达式
	     String sexpr=SafeCode.decode((String)this.getFormHM().get("sexpr"));
	     sexpr=sexpr!=null&&sexpr.trim().length()>0?PubFunc.keyWord_reback(sexpr):"";	
	     sexpr=PubFunc.reBackWord(sexpr);
	     //条件
	     String sfactor=(String)this.getFormHM().get("sfactor");
	     sfactor=sfactor!=null&&sfactor.trim().length()>0?sfactor:"";
	     sfactor=SafeCode.decode(sfactor);
	     sfactor=PubFunc.keyWord_reback(sfactor);
	     sfactor=PubFunc.reBackWord(sfactor);
	     String asfactor=sfactor;
	     //查询的范围 
	     String a_code=(String)this.getFormHM().get("a_code");
	     a_code=a_code!=null&&a_code.trim().length()>0?a_code:"all";
	     
	     //库前缀
	     String tablename=(String)this.getFormHM().get("tablename");
	     tablename=tablename!=null&&tablename.trim().length()>0?tablename:"";
	     tablename =tablename.replaceAll(",","`");
	     //二次查询
	     String second = (String)this.getFormHM().get("second");
	     second = second != null && second.trim().length() > 0 ? second : "0";
	     //全部人员库,考勤发卡有全部人员库 选择全部人员库查询会出现错误 wangy
	     ArrayList dblist=new ArrayList();
	     if("".equals(tablename)|| "all".equals(tablename))
	     {
	    	 String all_code=a_code.substring(2, a_code.length());
		     String qz_table=a_code.substring(0,a_code.length()-2); //前缀
		     String kind="";
		     if("UM".equalsIgnoreCase(qz_table))
				    kind="1";
				else if("UN".equalsIgnoreCase(qz_table))
				    kind="2";
		     if("hmuster".equals((String)this.getFormHM().get("moduleFlag"))) {
		    	 //高级花名册默认走人员库权限不走认证人员库导致查询结果集只有认证人员库对应的临时表更 新 其他人员库未更新
		    	 dblist=this.userView.getPrivDbList();
		     }else
		    	 dblist=RegisterInitInfoData.getDbList(all_code,kind,this.getFormHM(),this.userView,this.getFrameconn()); 
	     }
	     if(tablename.indexOf("`")!=-1){
	    	 String[] tmpdbpres=tablename.split("`");
	    	 for(int i=tmpdbpres.length-1;i>=0;i--){
	    		 if(tmpdbpres[i].length()==3)
	    		 dblist.add(tmpdbpres[i]);
	    	 }
	     }
	     //区别查询类型 [1.人员查询  2.单位查询  3.职位查询]
	     String type=(String)this.getFormHM().get("type");
	     type=type!=null&&type.trim().length()>0?type:"1";
	     
	     //是否为历史记录查询 [1.是  0.否]
	     String history=(String)this.getFormHM().get("history");
	     history=history!=null&&history.trim().length()>0?history:"0";
	     
	     //是否为模糊查询 [1.是  0.否]
	     String like=(String)this.getFormHM().get("like");
	     like=like!=null&&like.trim().length()>0?like:"0";
	     
	     //单位查询范围[0.查单位 1.查部门 2.全部]
	     String unite=(String)this.getFormHM().get("unite");
	     unite=unite!=null&&unite.trim().length()>0?unite:"2";
	     String fieldSetId=(String)this.getFormHM().get("fieldSetId");
	     //是否为二次结果查询 [1.是  0.否]
	     String result="0";
	     String wherestr = "";
	     String check="no";
	     if(type!=null&& "P".equalsIgnoreCase(type)){
	    	 try{
					FactorList factorslist=new FactorList(sexpr, PubFunc.getStr(sfactor), "", false, false, false, 0, "su");
					factorslist.getSingleTableSqlExpression(fieldSetId);
		    }catch(Exception e){
		    		 throw GeneralExceptionHandler.Handle(e);
		    }
		    this.getFormHM().put("check","ok"); 
	     //选择人员还走以前的程序，否则走新增加的查询程序
	     }else if(type!=null&& "5".equalsIgnoreCase(type)){
	    	 wherestr=sexpr+"|"+sfactor;
	    	 this.getFormHM().put("wheresql",wherestr);
	    	 this.getFormHM().put("type",type);
	    	  check="ok";
	    	  this.getFormHM().put("check",check);
	     }else if(type!=null&& "6".equalsIgnoreCase(type)){
	    	 wherestr=sexpr+"|"+sfactor+"|"+like;
	    	  this.getFormHM().put("wheresql",wherestr);
	    	  check="ok";
	    	  this.getFormHM().put("type",type);
	    	  this.getFormHM().put("check",check);
	     }else{
	    	 try{
		    	 FactorList factorslist=new FactorList(sexpr,PubFunc.getStr(sfactor),"Usr",true,true,true,Integer.parseInt(type),userView.getUserId());
		         factorslist.setSuper_admin(userView.isSuper_admin());
		         factorslist.getSqlExpression();
	    	 }catch(Exception e){
	    		 throw GeneralExceptionHandler.Handle(e);
	    	 }
	    	 // 基准岗位, 业务用户/自助用户都使用t_sys_result表
	    	 boolean usrResult=(this.getUserView().getStatus()==0/*业务用户*/)&&(!"9".equals(type));
	    	 boolean tsysResult=(this.getUserView().getStatus()==4/*自助用户*/)||("9".equals(type)/*基准岗位*/);
	    	 boolean no_manager_priv="true".equalsIgnoreCase((String)this.getFormHM().get("no_manager_priv"));
	         if(usrResult)
		     {
		         if(!"".equals(tablename)&&!"all".equals(tablename)&&tablename.indexOf("`")==-1)
	    	     {
		    	    	 SearchInformBo searchInformBo = new SearchInformBo(this.frameconn,this.userView,a_code,tablename);
			        	 if(fieldSetId!=null && "0".equals(fieldSetId)){
			        		 wherestr = searchInformBo.strWhere(sexpr,sfactor,query_type,history,like,result,type,unite,no_manager_priv);
			    	    	 if(searchInformBo.saveQueryResult(type,wherestr)){
				        		 check="ok";
				        	 }else{
				        		 check="ok"; 
				        	 }
				        }else {
			    	    	 check="ok";
			 	        }
		       }else
	    	     {
		    	 //人员库为全部走新增加程序
		        	 SearchInformBo searchInformBo = new SearchInformBo(this.frameconn,this.userView,a_code,dblist);
		        	 if(fieldSetId!=null && "0".equals(fieldSetId)){
		    	    	 if(searchInformBo.saveQueryResultAll(sexpr,sfactor,query_type,history,like,result,type,unite,type)){
		     	    		 check="ok";
		    	    	 }else{
		    	    		 check="ok"; 
		    	    	 }
		    	     }else {
		    	    	 check="ok";
		    	     }
	    	     }
		     }
		     else if(tsysResult)
		     {
		    	 if(!"".equals(tablename)&&!"all".equals(tablename)&&tablename.indexOf("`")==-1 && !"1".equals(second))
	    	     {
		    		 SearchInformBo searchInformBo = new SearchInformBo(this.frameconn,this.userView,a_code,tablename);
		    		 searchInformBo.saveSelfServiceQueryResult(sexpr, sfactor, query_type, history, like, result, type, unite, 2, no_manager_priv);
	    	     }
		    	 else if(!"1".equals(second))
		    	 {
		    		 SearchInformBo searchInformBo = new SearchInformBo(this.frameconn,this.userView,a_code,dblist);
		    		 searchInformBo.saveSelfServiceQueryResult(sexpr, sfactor, query_type, history, like, result, type, unite, 1, no_manager_priv);
		    	 }
		    	 check="ok";
		     }
//		     SearchInformBo searchInformBo = new SearchInformBo(this.frameconn,this.userView,a_code,tablename);
////		     String wherestr = "";
////		     String check="no";
//		     if(fieldSetId!=null && fieldSetId.equals("0")){
//		    	 wherestr = searchInformBo.strWhere(sexpr,sfactor,query_type,history,like,result,type,unite);
////		    	 if(this.userView.getStatus()!=4){
//		    	 if(searchInformBo.saveQueryResult(type,wherestr)){
//		    		 check="ok";
////		    		 }
//		    	 }else{
//		    		 check="ok"; 
//		    	 }
//		     }else {
//		    	 check="ok";
//		     }
		     //模糊查询 [1.是  0.否] @author:liwc
		     String likeflag=(String)this.getFormHM().get("like");
		     likeflag=likeflag!=null&&likeflag.trim().length()>0?likeflag:"0";
		     if("1".equals(likeflag)){
		    	 sfactor=getVagueInquiry(sfactor);
		    	 check="ok";
		     }
		     this.getFormHM().put("likeflag", like);
	         this.getFormHM().put("history", history);
		     this.getFormHM().put("check",check);
		     this.getFormHM().put("wheresql",SafeCode.encode(wherestr));
		     this.getFormHM().put("sexpr", SafeCode.encode(sexpr));
		     this.getFormHM().put("sfactor", SafeCode.encode(asfactor));
		     this.getFormHM().put("fieldSetId",fieldSetId);
		     this.getFormHM().put("second",second);
	     }
	
	    
	     
	}
	
	/**
	 * 添加 模糊查询
	 * @param sfactor
	 * @return liwc
	 */
	public String getVagueInquiry(String sfactor){
		StringBuffer str=new StringBuffer();
		String fieldid=(String)this.getFormHM().get("fieldid");
		if(fieldid!=null&&fieldid.trim().length()>0)
		{
	    	String[] sfactors=sfactor.split("`");
	    	ArrayList fielditemlist=this.getUserView().getPrivFieldList(fieldid);
	    	for(int i=0;i<fielditemlist.size();i++)
	        {
	          FieldItem fielditem=(FieldItem)fielditemlist.get(i);
    	      for (int j = 0; j < sfactors.length; j++) {
    			if("A".equalsIgnoreCase(fielditem.getItemtype()) && fielditem.getItemid().equalsIgnoreCase(sfactors[j].split("=")[0])){
	    			String value=sfactors[j].substring(sfactors[j].indexOf("=")+1);
	    			if ("".equals(value.trim())) {
	    	          continue;
		            }else if ("0".equals(fielditem.getCodesetid()))
		            	sfactors[j]=sfactors[j].replaceAll("=", "=*")+"*";
		            else {
		            	sfactors[j]=sfactors[j] + "*";
		          }
	    		}
	         }
	    	}
     		for (int i = 0; i < sfactors.length; i++) {
    			str.append(sfactors[i]+"`");
	    	}
		}
		
		return str.toString();
	}
}
