package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchPersonTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
		String like = (String)reqhm.get("like");
		like=like!=null&&like.trim().length()>0?like:"";
		reqhm.remove("like");

		String checkflag = (String)this.getFormHM().get("checkflag");
		checkflag=checkflag!=null&&checkflag.trim().length()>0?checkflag:"1";
		
		String a_code = (String)this.getFormHM().get("a_code");
		a_code=a_code!=null&&a_code.trim().length()>0?a_code:"";

		String nbase = (String)this.getFormHM().get("nbase");
		nbase=nbase!=null&&nbase.trim().length()>0?nbase:"";
		
		ArrayList dblist = new ArrayList();
        nbase=nbase!=null&&nbase.trim().length()>0?nbase:"";
        nbase= "all".equalsIgnoreCase(nbase)?"":nbase;
        String nbasearr[] = nbase.split(","); 
        if(nbase.trim().length()>0&&nbasearr.length>0){
      	  for(int i=0;i<nbasearr.length;i++){
      		  if(nbasearr[i]!=null&&nbasearr[i].length()>0)
      			  dblist.add(nbasearr[i]); 
      	  }
        }else
      	  dblist=userView.getPrivDbList();
        
        //培训考试计划手工选人特殊处理  取参培参数设置的交集
        ArrayList arrayList = new ArrayList();
        ArrayList sel_nbase = new ArrayList();
		 ConstantXml constantbo = new ConstantXml(this.getFrameconn(),"TR_PARAM");
		 String tmpnbase = constantbo.getTextValue("/param/post_traincourse/nbase");
		 if(tmpnbase!=null&&tmpnbase.length()>0){
			 String nbs[]=tmpnbase.split(",");
			 for(int i=0;i<nbs.length;i++){
				 if(nbs[i]!=null&&nbs[i].length()>0){
					 sel_nbase.add(nbs[i]);
				 }
			 }
		 }
		 for (int i = 0; i < dblist.size(); i++) {
				if(sel_nbase.contains(dblist.get(i)))
					arrayList.add(dblist.get(i));
		  }
		
		String itemkey = (String)this.getFormHM().get("itemkey");
		itemkey=itemkey!=null&&itemkey.trim().length()>0?itemkey:"";
		
		String sexpr=(String)this.getFormHM().get("sexpr");
		sexpr=sexpr!=null?sexpr:"";
		sexpr = PubFunc.keyWord_reback(sexpr);
		String sfactor=(String)this.getFormHM().get("sfactor");
		sfactor=sfactor!=null?sfactor:"";
		ContentDAO dao = new ContentDAO(this.frameconn);
		sfactor = PubFunc.keyWord_reback(sfactor);
		
		StringBuffer tablestr = new StringBuffer();

		if(sfactor.length()>0&&sexpr.length()>0&&arrayList!=null){
			for(int i=0;i<arrayList.size();i++){
				String dbpre = (String)arrayList.get(i);
				if(dbpre==null||dbpre.length()<1)
					continue;
				
				String dbname="";
				String dbid="";
				try {
					this.frowset = dao.search("select DbId,DBName from dbname where Pre='"+dbpre+"'");
					if(this.frowset.next()){
						dbid=this.frowset.getString("dbid");
						dbname=this.frowset.getString("dbname");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(sexpr.startsWith("*"))
					sexpr=sexpr.substring(1);
				
				//SearchInformBo searchInformBo = new SearchInformBo(this.frameconn,this.userView,a_code,dbpre);
				//String wherestr = searchInformBo.strWhere(sexpr,sfactor,"1","0",like,"0","1","2");
				TrainCourseBo bo = new TrainCourseBo(this.userView);
				String wherestr = bo.strWhere(sexpr,sfactor,dbpre,"1","0",like,"0","1","2");
				StringBuffer buf = new StringBuffer();
				String tablename=dbpre+"A01";
				
				buf.append("select "+tablename+".A0100,"+tablename+".A0101,"+tablename+".B0110,"+tablename+".E0122,");
				buf.append("(select codeitemdesc from organization where codeitemid=");
				buf.append(tablename+".B0110) as B0110_desc,");
				buf.append("(select codeitemdesc from organization where codeitemid=");
				buf.append(tablename+".E0122) as E0122_desc,'"+dbpre+"' as dbpre,'");
				buf.append( dbname+"' as dbname,'"+dbid+"' as dbid");
				buf.append(wherestr);
				if("1".equals(checkflag)){
					buf.append(" and "+tablename+".A0100 not in(select R4001 from R40 where R4005='");
					buf.append(itemkey+"' and nbase='"+dbpre+"')");  
				}
				if(tablestr!=null&&tablestr.length()>1)
					tablestr.append(" union ");
				tablestr.append(buf.toString());
			}
		}
        this.getFormHM().put("sqlstr",tablestr.toString());
	}
}
