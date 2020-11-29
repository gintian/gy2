package com.hjsj.hrms.module.questionnaire.recovery.transaction;

import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SaveOrgorroleTreeTrans extends IBusiness{
	@Override
    public void execute() throws GeneralException {
		String flagarray  = (String)this.getFormHM().get("flagarray");//人员机构角色idflagarray= "["PEUsr00000009","PEUsr00000004","UM010103","PEUsr00000066"]"
		String content  = (String)this.getFormHM().get("content");//答题链接
		String qnid = (String)this.getFormHM().get("qnid");
		String planid = (String)this.getFormHM().get("planid");
		if(flagarray==null||flagarray.length()<0)
			return;
		try {
			planid = Integer.parseInt(planid)+"";
		}catch(Exception e) {
			return;
		}
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rst = null;
		RowSet rst1 = null;
		RowSet rst2 = null;
		RowSet rst3 = null;

		Set set=new HashSet();
		try {
			flagarray = flagarray.substring(1,flagarray.length()-1);
			flagarray = flagarray.replaceAll("\"", "");
			String [] idarray = flagarray.split(",");
			for(int i=0;i<idarray.length;i++){
				String flagid = idarray[i];
				if(flagid.startsWith("UN")||flagid.startsWith("UM")){
					String flagidf = flagid.substring(2,flagid.length());
					StringBuilder sql = new StringBuilder();
//					ArrayList dbnameList = DataDictionary.getDbpreList();//获得人员库前缀
    				sql.append(" select 'Usr' as nbase,u.a0100 from UsrA01 u where ");
					if(flagid.startsWith("UN")){//单位
						sql.append(" u.b0110 like '"+flagidf+"%' ");
					}else{//部门
						sql.append(" u.e0122 like '"+flagidf+"%' ");
					}
					sql.append(" order by a0100");
					this.frowset = dao.search(sql.toString());
					while(this.frowset.next()){
						String a0100 = this.frowset.getString("a0100");
						String nbase = this.frowset.getString("nbase");
						set.add(nbase+a0100);
					}
				}
				if(flagid.startsWith("PE")){//人员id
					String flagids = flagid.substring(2,flagid.length());
					set.add(flagids);
				}
				if(flagid.startsWith("ROLE")){//角色id
					String flagidt = flagid.substring(5,flagid.length());
					String sql = "select staff_id,status from t_sys_staff_in_role where role_id='"+flagidt+"'";
					rst = dao.search(sql);
					while(rst.next()){
						int status = rst.getInt("status");
						String staffid = rst.getString("staff_id");
						if(status==0){//用户查询关联的人员
							String linksql= "select a0100,nbase from OperUser where UserName='"+staffid+"'"; 
							rst1 = dao.search(linksql);
							while(rst1.next()){
								String A0100 = rst1.getString("a0100");
								String nbase = rst1.getString("nbase");
								//增加非空串判断 xiegh 2017/3/22
								if(A0100!=null && !"".equals(A0100)){
									set.add(nbase+A0100);
								}
							}
						}
						if(status==1){//A0100
							//String staffids = staffid.substring(3,staffid.length());
							//if(!staffid.startsWith("Usr")){
							//	set.add("Usr"+staffid);
							//}
							//else{
								set.add(staffid);
							//}
						}
						if(status==2){//组织机构  查询机构关联的人员  wangb 20170730 bug 37246
							ArrayList dbList = this.userView.getPrivDbList();
							for(int j =0 ; j <dbList.size() ; j++){
								String dbname = (String) dbList.get(j);
								String sqlstr = "select a0100 from "+dbname+"A01 where B0110=? or E0122=? or E01A1=?";
								ArrayList list = new ArrayList();
								list.add(staffid);
								list.add(staffid);
								list.add(staffid);
								rst3 = dao.search(sqlstr, list);
								while(rst3.next()){
									String a0100 = rst3.getString("A0100");
									set.add(dbname+a0100);
								}
							}
						}
					}
				}	
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally {
	            PubFunc.closeResource(rst);
	            PubFunc.closeResource(rst1);
	            PubFunc.closeResource(rst2);
	            PubFunc.closeResource(rst3);
	        }
		/*清除重复数据 wangb 20180730*/
		Set hashSet = new HashSet();
        ArrayList newList = new ArrayList();
        for (Iterator iter = set.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (hashSet.add(element))
                newList.add(element);
        }
        set.clear();
        set.addAll(newList);
		String username = "";
		String password = "";
		RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
		if (login_vo == null) {
			username = "username";
			password = "userpassword";
		} else {
			String login_name = login_vo.getString("str_value").toLowerCase();
			int idx = login_name.indexOf(",");
			if (idx == -1) {
				username = "username";
				password = "userpassword";
			} else {
				username = login_name.substring(0, idx);
				password = login_name.substring(idx+1);
				if ("#".equals(username) || "".equals(username)) {
					username = "username";
					password = "userpassword";
				}
				
				if ("#".equals(password) || "".equals(password)) {
                    password = "userpassword";
                }
			}
		}
		
		//boolean isencry = ConstantParamter.isEncPwd(this.getFrameconn());
		//Des des=new Des();
		ArrayList emaillist = new ArrayList();
		for (Object str : set) { 
			String nbase = str.toString().substring(0,3);
			String a0100 = str.toString().substring(3);
			String sql = "select b0110,e0122,e01a1,"+username+","+password+" from "+nbase+"A01 where a0100='"+a0100+"'";
			try {
				this.frowset = dao.search(sql.toString());
				if(this.frowset.next()){
//					String u = this.frowset.getString(username);
//					u = u==null?"":u;
//					String p = this.frowset.getString(password);
//					p = p==null?"":p;
//					if(isencry){
//						p = des.DecryPwdStr(p);
//					}
//					idset.add(u);
					
					LazyDynaBean emailbean = sendEMail(nbase, a0100, content,qnid);
					addListdata(nbase,a0100,planid,qnid);
					if(emailbean != null)
					    emaillist.add(emailbean);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.fillInStackTrace();
			}
		} 
		AsyncEmailBo emailbo = new AsyncEmailBo(this.frameconn, this.userView);
		emailbo.send(emaillist);
		
		this.getFormHM().put("flag", "ok");
	}
	private void addListdata(String nbase,String a0100, String planid,String qnid) {
		ContentDAO dao = new ContentDAO(this.frameconn);
		ResultSet  rs = null;
		String planname = "";
		String receiver = nbase+a0100;
		String sender = nbase+this.userView.getA0100();
		String ext_flag = planid;
		try {
			String sql = "select * from t_hr_pendingtask where Receiver='"+receiver+"' and Ext_flag='"+ext_flag+"'";
			this.frowset = dao.search(sql);
			if(!this.frowset.next()){
				IDGenerator idg = new IDGenerator(2,this.getFrameconn());
				String pendingid = idg.getId("pengdingTask.pengding_id");
				ArrayList<RecordVo> list = new ArrayList<RecordVo>();
				RecordVo resultVo = new RecordVo("t_hr_pendingtask");
				resultVo.setInt("pending_id", Integer.parseInt(pendingid));
	            String sqlname = "select planname from qn_plan where planid='"+planid+"'";
	            rs = dao.search(sqlname);
	            while (rs.next()) {
	            	planname = rs.getString("planname");
	            }
				resultVo.setString("pending_title", planname);
				resultVo.setString("pending_url", "/module/system/questionnaire/template/AnswerQn.jsp?suerveyid="+PubFunc.encryption(planid));
				resultVo.setString("pending_status", "0");
				resultVo.setString("pending_type", "80");
				resultVo.setString("receiver", receiver);
				resultVo.setString("sender", sender);
				resultVo.setDate("create_time", new Date());
				resultVo.setString("ext_flag", ext_flag);
				list.add(resultVo);
				dao.addValueObject(list);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            PubFunc.closeResource(rs);
        }
	}
	/**
	 * 发送邮件
	 * @param dbname
	 * @param a0100
	 * @param urlpath
	 * @param qnid
	 * @return
	 * @throws Exception
	 */
    private LazyDynaBean sendEMail(String dbname, String a0100, String urlpath,String qnid) throws Exception {
        LazyDynaBean emailbean = new LazyDynaBean();
        ResultSet rs = null;
        try {
            String email = ConstantParamter.getEmailField().toLowerCase();
            //String loguser = ConstantParamter.getLoginUserNameField().toLowerCase();
           // String logpassword = ConstantParamter.getLoginPasswordField();
            RecordVo vo = null;
            ContentDAO dao = new ContentDAO(this.frameconn);

            StringBuffer buf = new StringBuffer();// 邮件内容
            StringBuffer title = new StringBuffer();// 邮件标题

            vo = new RecordVo(dbname + "A01");
            vo.setString("a0100", a0100);
            if (dao.isExistRecordVo(vo)) {
                if (vo != null) {
                    buf.setLength(0);
                    title.setLength(0);
                    String email_address = vo.getString(email);
                    String a0101 = vo.getString("a0101");
                    //String username = vo.getString(loguser);
                    //String password = vo.getString(logpassword.toLowerCase());
                    //String etoken = PubFunc.convertTo64Base(username + "," + password);

                    title.append("问卷调查通知");
                    buf.append(a0101 + ":<br><br>&nbsp;&nbsp;&nbsp;&nbsp;您好，系统为您推送了一份问卷调查。<br><br>");
                    buf.append("&nbsp;&nbsp;&nbsp;&nbsp;点击问卷名称可以进行答题：<br><br>");
                    String sql = "  select qnname from qn_template where qnid='"+qnid+"'";
                    rs = dao.search(sql);
                    while (rs.next()) {
                    	String qnname = rs.getString("qnname");
                        buf.append("<a href=\""+urlpath+"\">&nbsp;&nbsp;《" + qnname + "》</a>、");

                    String content = "";
                    if (buf.toString().endsWith("、"))
                        content = (String) buf.subSequence(0, buf.length() - 1) + "。";

                    emailbean.set("toAddr", email_address);
                    emailbean.set("subject", title.toString());
                    emailbean.set("bodyText", content);
                    emailbean.set("href", "");
                    emailbean.set("hrefDesc", "");                   
                }
            }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return emailbean;
    }
}
