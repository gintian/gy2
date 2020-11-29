package com.hjsj.hrms.transaction.sys.codemaintence;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class AddorEidtCodesetTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm = (HashMap) this.getFormHM().get("requestPamaHM");
		ContentDAO dao = new ContentDAO(this.getFrameconn());

		if (reqhm != null) {
			StringBuffer sql=new StringBuffer();
			  sql.append("select categories from codeset group by categories ");
			  StringBuffer hidcategories = new StringBuffer();
			  ArrayList catelist = new ArrayList();
			  try {
				this.frowset=dao.search(sql.toString());
				while(this.frowset.next())
				{
					String temp = this.frowset.getString("categories");
					if(temp==null||temp.length()==0)
						continue;
					CommonData cd = new CommonData(temp,temp);
					catelist.add(cd);
					hidcategories.append(","+temp);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			this.getFormHM().put("catelist", catelist);
			this.getFormHM().put("hidcategories", hidcategories.length()>0?hidcategories.substring(1):"");
			String categories = "";
			RecordVo codesetvo = new RecordVo("codeset");
			if (reqhm.containsKey("codesetid")) {
				String codesetid = (String) reqhm.get("codesetid");
				codesetvo.setString("codesetid", codesetid);
				try {
					codesetvo = dao.findByPrimaryKey(codesetvo);
					reqhm.remove("codesetid");
					this.getFormHM().put("codesetvo", codesetvo);
					this.getFormHM().put("flag", "0");
					this.getFormHM().put("vflag", "1");
					categories = codesetvo.getString("categories");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("codemaintence.codefail"),"",""));
				} 
			}
			if (reqhm.containsKey("query")) {
				reqhm.remove("query");
				codesetvo.clearValues();
				codesetvo.setString("codesetid",madeCodesetid(dao));
				this.getFormHM().put("flag", "0");
				this.getFormHM().put("vflag", "0");
				this.getFormHM().put("codesetvo", codesetvo);
				//categories=(String)this.getFormHM().get("categories");
			}
			this.getFormHM().put("categories", categories);
		} else {
			String excp="";
			DynaBean codesetbean = (LazyDynaBean) this.getFormHM().get(
					"codesetvo");
			this.userView.getHm().put("codesetid", (String)codesetbean.get("codesetid"));//记录下codesetid用于分页定位
			/*
			 * flag =0增加codeset =1修改codeset
			 */
			String flag = (String) this.getFormHM().get("flag");
			if (flag != null && "0".equals(flag)) {
				try {
					if(this.judgeCodeset(dao,(String) codesetbean.get("codesetdesc"))){
						excp="<"+(String) codesetbean.get("codesetdesc")+">代码类名称已经存在，增加代码类失败！";
						Exception ex=new Exception(excp);
						throw GeneralExceptionHandler.Handle(ex);
					}
					dao.addValueObject(this.getRecordVo(codesetbean));
					
				} catch (Exception e) {
					if(excp.length()<1){
						excp="<"+(String) codesetbean.get("codesetid")+">"+ResourceFactory.getProperty("codemaintence.codeset.exist");
					}
					throw GeneralExceptionHandler.Handle(new GeneralException("",excp,"",""));
				}
			}
			if (flag != null && "1".equals(flag)) {
				try {
					if(this.judgeCodeset(dao,(String) codesetbean.get("codesetdesc"))){
						if(!this.jugeCodeset(dao,(String) codesetbean.get("codesetdesc"), (String) codesetbean.get("codesetid"))){
							excp="<"+(String) codesetbean.get("codesetdesc")+">代码类名称已经存在，修改代码类失败！";
							Exception ex=new Exception(excp);
							throw GeneralExceptionHandler.Handle(ex);
						}
					}
					RecordVo vo = this.getRecordVo(codesetbean);
					RecordVo dbvo = dao.findByPrimaryKey(vo);
					String s1 = dbvo.getString("status");
					s1=(s1==null||(s1!=null&& "".equals(s1)))?"0":s1;
					String s2 = vo.getString("status");
					s2=(s2==null||(s2!=null&& "".equals(s2)))?"0":s2;
					if(s1.equalsIgnoreCase(s2)||("1".equals(s1)&& "2".equals(s2))||("2".equals(s1)&& "1".equals(s2)))//标示是否属于系统代码或用户代码类改变
						this.getFormHM().put("flag","0");
					else
						this.getFormHM().put("flag","1");
					//this.formHM.put("states",s2);
					
					
					int i=dao.updateValueObject(this.getRecordVo(codesetbean));
					
					if("1".equalsIgnoreCase(vo.getString("validateflag"))){
						String codesetid=(String) vo.getString("codesetid");
						String sql= "update codeitem set start_date="+Sql_switcher.dateValue("1949-10-1")+",end_date="+Sql_switcher.dateValue("9999-12-31")+" where codesetid='"+codesetid+"' and start_date is null and end_date is null";
						dao.update(sql);
					}else{
						String codesetid=(String) vo.getString("codesetid");
						String sql= "update codeitem set invalid=1 where codesetid='"+codesetid+"'";
						dao.update(sql);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					if(excp.length()<1){
						excp=ResourceFactory.getProperty("codemaintence.codeset.update.fail");
					}
					throw GeneralExceptionHandler.Handle(new GeneralException("",excp,"",""));
				}
			}
			this.getFormHM().put("codesetid",(String)codesetbean.get("codesetid"));
			this.getFormHM().put("codesetdesc",(String)codesetbean.get("codesetdesc"));
			this.getFormHM().put("states",(String)codesetbean.get("status")!=null?(String)codesetbean.get("status"):"");
			this.getFormHM().put("categories", (String)codesetbean.get("categories")!=null?(String)codesetbean.get("categories"):"");
		}
	}
	public boolean judgeCodeset(ContentDAO dao,String codesetdesc) throws GeneralException{
		String sql="select * from codeset where codesetdesc='"+codesetdesc+"'";
		ArrayList a=dao.searchDynaList(sql);
		if(a.size()>0){
			return true;
		}
		return false;
	}
	private boolean jugeCodeset(ContentDAO dao,String codesetdesc,String codesetid) throws GeneralException{
		String sql="select * from codeset where codesetdesc='"+codesetdesc+"' and codesetid='"+codesetid+"'";
		ArrayList a=dao.searchDynaList(sql);
		if(a.size()>0){
			return true;
		}
		return false;
	}
	public RecordVo getRecordVo(DynaBean codesetbean) {
		RecordVo codesetvo = new RecordVo("codeset");
		codesetvo.setString("codesetid", (String) codesetbean.get("codesetid"));
		codesetvo.setString("codesetdesc", (String) (codesetbean
				.get("codesetdesc") != null ? codesetbean.get("codesetdesc")
				: ""));
		codesetvo.setString("maxlength",
				(String) (codesetbean.get("maxlength") != null ? codesetbean
						.get("maxlength") : ""));
		codesetvo.setString("status",
				(String) (codesetbean.get("status") != null ? codesetbean
						.get("status") : ""));
		codesetvo.setString("validateflag",
				(String) (codesetbean.get("validateflag") != null ? codesetbean
						.get("validateflag") : "1"));
		codesetvo.setString("categories",(String)codesetbean
						.get("categories"));
		codesetvo.setString("leaf_node",(String)codesetbean
				.get("leaf_node"));
		return codesetvo;
	}
	public String madeCodesetid(ContentDAO dao) throws GeneralException{
		ArrayList codesetidList=new ArrayList();
		ArrayList idList=new ArrayList();
		String codesetid=null;
		String sqlid="select codesetid from codeset order by codesetid desc";
		try{
		RowSet rs =dao.search(sqlid);
		while(rs.next()){
			codesetidList.add(rs.getString("codesetid"));
		}
		String codeflag=SystemConfig.getPropertyValue("dev_flag");
		if(codeflag==null|| "0".equals(codeflag)|| "".equals(codeflag)){
			codeflag="0";
		}else{
			codeflag="1";
		}
		if("0".equalsIgnoreCase(codeflag)){
			for(char i=76;i<=87;i++){// //guodd 2015-08-03 用户模式，代码编码格式为：第一位为L — W，第二位为随意字母
				for(char j=65;j<=90;j++){
					StringBuffer sbuf=new StringBuffer();
					sbuf.append(i);
					sbuf.append(j);
					idList.add(sbuf.toString());
				}
			}
			for(int m=0;m<idList.size();m++){
				if(!codesetidList.contains(idList.get(m))){
					codesetid=(String) idList.get(m);
					return codesetid;
				}
			}
		}
		if("1".equalsIgnoreCase(codeflag)){
			String status = (String)this.getFormHM().get("status");
			if(!"2".equals(status)){
				//代码生成规则改变，注掉此处 guodd 2015-08-05
//				for(char i=90;i>=67;i--){
//					for(char j=90;j>=67;j--){
//						StringBuffer sbuf=new StringBuffer();
//						sbuf.append(i);
//						sbuf.append(j);
//						idList.add(sbuf.toString());
//					}
//				}
//				for(int m=0;m<idList.size();m++){
//					if(!codesetidList.contains(idList.get(m))){
//						codesetid=(String) idList.get(m);
//						return codesetid;
//					}
//				}
			
				//新代码生成规则
				//先走业务代码开发商规则
				for(char i=88;i<=90;i++){//首字母为X、Y、Z
					for(char j=65;j<=90;j++){//第二为为A-Z
						StringBuffer sbuf=new StringBuffer();
						sbuf.append(i);
						sbuf.append(j);
						idList.add(sbuf.toString());
					}
				}
				
				for(int m=0;m<idList.size();m++){//查找空缺，找到了就返回
					if(!codesetidList.contains(idList.get(m))){
						codesetid=(String) idList.get(m);
						break;
						//return codesetid;
					}
				}
				//如果业务代码生成成功，返回新代码
				if(codesetid!=null)
					return codesetid;
				
				//如果开发商规则不满足，走行业版规则
				for(char i=73;i<=75;i++){//首字母为I、J、K
					for(char j=65;j<=75;j++){//第二位为A-K
						StringBuffer sbuf=new StringBuffer();
						sbuf.append(i);
						sbuf.append(j);
						idList.add(sbuf.toString());
					}
					for(int k=0;k<=9;k++){//或者第二位为0-9
						StringBuffer sbuf=new StringBuffer();
						sbuf.append(i);
						sbuf.append(k);
						idList.add(sbuf.toString());
					}
				}
				for(int m=0;m<idList.size();m++){//查找空缺，找到了就返回
					if(!codesetidList.contains(idList.get(m))){
						codesetid=(String) idList.get(m);
						return codesetid;
					}
				}
				
			}else{
				for(int i=1;i<9;i++){
					idList.add("0"+String.valueOf(i));
				}
				for(int i=10;i<100;i++){
					idList.add(String.valueOf(i));
				}
				for(int m=0;m<idList.size();m++){
					if(!codesetidList.contains(idList.get(m))){
						codesetid=(String) idList.get(m);
						return codesetid;
					}
				}
			}
		}

		}catch(Exception e){
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("codemaintence.madecodesetid.fail"),"",""));

		}
		return codesetid;
	}

}
