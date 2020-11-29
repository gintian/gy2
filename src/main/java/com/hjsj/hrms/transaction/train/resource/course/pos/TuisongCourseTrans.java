package com.hjsj.hrms.transaction.train.resource.course.pos;

import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.*;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.*;

public class TuisongCourseTrans extends IBusiness
{

    public void execute() throws GeneralException
    {

        String flag = "ok";
        String state = (String) this.getFormHM().get("state");
        ArrayList selectids = (ArrayList) this.getFormHM().get("selectids");
        String codesetid = (String) this.getFormHM().get("codesetid");
        String basePath = (String)this.getFormHM().get("basePath");

        TrainCourseBo trainCourseBo = new TrainCourseBo(this.userView, this.frameconn);
        try
        {	
            ArrayList dbprivlist = trainCourseBo.getTrainNbases(state);

            if (dbprivlist.size() < 1)
                return;
            // 查看人员基本情况子集中是否有管理codesetid的指标
            String fielditemid = "";
            ArrayList list = DataDictionary.getFieldList("A01", Constant.USED_FIELD_SET);
            for (int i = list.size() - 1; i >= 0; i--)
            {
                FieldItem item = (FieldItem) list.get(i);
                if (codesetid.equals(item.getCodesetid()))
                {
                    fielditemid = item.getItemid();
                    break;
                }
            }
            HashSet job_idset = new HashSet();
            for (int i = selectids.size() - 1; i >= 0; i--)
            {
                String selectid = (String) selectids.get(i);
                String[] tmpstr = selectid.split("`");
                if (tmpstr.length == 2)
                {
                    String job_id = tmpstr[0];
                    job_idset.add(PubFunc.decrypt(SafeCode.decode(job_id)));
                }
            }
            HashMap job_idMap = new HashMap();
            ContentDAO dao = new ContentDAO(this.frameconn);
            if (fielditemid.length() < 1)
            {
                if ("1".equals(state))
                {// 岗位
                    RecordVo ps_c_job_vo = ConstantParamter.getRealConstantVo("PS_C_JOB", this.getFrameconn());
                    if (ps_c_job_vo != null)
                    {
                        String ps_c_job = ps_c_job_vo.getString("str_value");
                        if (ps_c_job.replaceAll("#", "").length() > 0)
                        {
                            for (Iterator i = job_idset.iterator(); i.hasNext();)
                            {
                                ArrayList e01a1list = new ArrayList();
                                String job_id = (String) i.next();
                                String sql = "select e01a1 from k01 where " + ps_c_job + " like '" + job_id + "%'";
                                this.frowset = dao.search(sql);
                                while (this.frowset.next())
                                {
                                    e01a1list.add(this.frowset.getString("e01a1"));
                                }
                                job_idMap.put(job_id, e01a1list);
                            }
                        } else
                        {
                            String temp = ResourceFactory.getProperty("pos.posbusiness.nosetposccode.job");
                            throw GeneralExceptionHandler.Handle(new Exception(temp));
                        }
                    } else
                    {
                        String temp = ResourceFactory.getProperty("pos.posbusiness.nosetposccode.job");
                        throw GeneralExceptionHandler.Handle(new Exception(temp));
                    }
                } else
                {
                    RecordVo ps_job_vo = ConstantParamter.getRealConstantVo("PS_JOB", this.getFrameconn());
                    if (ps_job_vo != null)
                    {
                        String ps_job = ps_job_vo.getString("str_value");
                        if (ps_job.replaceAll("#", "").length() > 0)
                        {
                            for (Iterator i = job_idset.iterator(); i.hasNext();)
                            {
                                ArrayList e01a1list = new ArrayList();
                                String job_id = (String) i.next();
                                String sql = "select e01a1 from k01 where " + ps_job + " like '" + job_id + "%'";
                                this.frowset = dao.search(sql);
                                while (this.frowset.next())
                                {
                                    e01a1list.add(this.frowset.getString("e01a1"));
                                }
                                job_idMap.put(job_id, e01a1list);
                            }
                        } else
                        {
                            String temp = ResourceFactory.getProperty("pos.posbusiness.nosetposcode.job");
                            throw GeneralExceptionHandler.Handle(new Exception(temp));
                        }
                    } else
                    {
                        String temp = ResourceFactory.getProperty("pos.posbusiness.nosetposcode.job");
                        throw GeneralExceptionHandler.Handle(new Exception(temp));
                    }
                }
            }
            StringBuffer dbprivlistsb = new StringBuffer();
            for (int i = dbprivlist.size() - 1; i >= 0; i--)
            {
                dbprivlistsb.append("," + dbprivlist.get(i));
            }

            String sql = "";
            TrainCourseBo bo = new TrainCourseBo(this.getFrameconn());
            String picUrl = "http://www.hjsoft.com.cn:8089/UserFiles/Image/tongzhi.png";
            //用户登陆指标，主要用于取得微信人员id
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
    			}
    		}
    		boolean isencry = ConstantParamter.isEncPwd(this.getFrameconn());
    		Des des=new Des();
    		
    		ArrayList personlist = new ArrayList();
    		ArrayList emaillist = new ArrayList();
    		HashMap pushmap = new HashMap();
    		String personNames = "";
            for (int i = dbprivlist.size() - 1; i >= 0; i--)
            {
                String dbpre = (String) dbprivlist.get(i);
                if (fielditemid.length() < 1)
                {
                    for (int n = selectids.size() - 1; n >= 0; n--)
                    {
                        String selectid = (String) selectids.get(n);
                        String[] tmpstr = selectid.split("`");
                        if (tmpstr.length == 2)
                        {
                        	Set idset = new HashSet();//存放微信id的set，防止存入重复数据，导致发送重复的微信消息
                        	HashMap urlmap = new HashMap();
                            String job_id = tmpstr[0];
                            job_id = PubFunc.decrypt(SafeCode.decode(job_id));
                            String r5000 = tmpstr[1];
                            r5000 = PubFunc.decrypt(SafeCode.decode(r5000));
                            ArrayList e01a1list = (ArrayList) job_idMap.get(job_id);
                            for (int m = e01a1list.size() - 1; m >= 0; m--)
                            {
                                String e01a1 = (String) e01a1list.get(m);
                                sql = "select a0100,b0110,e0122,e01a1,a0101,"+username+","+password+" from " + dbpre + "a01 where e01a1='" + e01a1 + "'";
                                this.frecset = dao.search(sql);
                                while (this.frecset.next())
                                {
                                	String u = this.frecset.getString(username)==null?"":this.frecset.getString(username);
            						String p = this.frecset.getString(password)==null?"":this.frecset.getString(password);
            						if(isencry){
            							p = des.DecryPwdStr(p);
            						}
            						idset.add(u);
            						String etoken = PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(u+","+p));
            						urlmap.put(u, basePath+"elearning/mylession/mobile/list.jsp?etoken="+etoken);
            						
                                    String lesson_from = "0";
                                    if (!"1".equals(state))
                                        lesson_from = "4";
                                    
                                    String person = this.frecset.getString("a0100");
                                    if(pushmap.containsKey(person)){
                                    	String r5000s = (String) pushmap.get(person);
                                    	r5000s += "," + SafeCode.encode(PubFunc.encrypt(r5000));
                                    	pushmap.put(person, r5000s);
                                    } else{
                                    	pushmap.put(person, SafeCode.encode(PubFunc.encrypt(r5000)));
                                    	personlist.add(person);
                                    }
                                    
                                    bo.pushCourse(r5000, dbpre, this.frecset.getString("a0100"), this.frecset.getString("b0110"), this.frecset.getString("e0122"), this.frecset.getString("e01a1"), this.frecset.getString("a0101"), lesson_from,"");
                                }
                            }
                            
                            
                            if(idset.size()>0){
                            	String lesson_from = "0";
                                if (!"1".equals(state))
                                    lesson_from = "4";
                                
                                HashMap url = new HashMap();
            					for(Iterator it = idset.iterator();it.hasNext();){
            						String u = (String)it.next();
            						String ul = urlmap.get(u).toString();
            							ul +="&encryptParam="+PubFunc.encrypt("r5000="+r5000);
            						
            						url.put(u, ul);
            					}
            					
                                bo.sendCourseToWX(r5000, lesson_from, picUrl, url, new ArrayList(idset));
                            }
                        }
                    }
                } else {
                    for (int n = selectids.size() - 1; n >= 0; n--) {
                        String selectid = (String) selectids.get(n);
                        String[] tmpstr = selectid.split("`");
                        
                        if (tmpstr.length == 2) {
                        	Set idset = new HashSet();//存放微信id的set，防止存入重复数据，导致发送重复的微信消息
                        	HashMap urlmap = new HashMap();
                            String job_id = tmpstr[0];
                            job_id = PubFunc.decrypt(SafeCode.decode(job_id));
                            String r5000 = tmpstr[1];
                            r5000 = PubFunc.decrypt(SafeCode.decode(r5000));
                            String persons = "";
                            sql = "select a0100,b0110,e0122,e01a1,a0101,"+username+" from " + dbpre + "a01 where " + fielditemid + "='" + job_id + "'";
                            this.frecset = dao.search(sql);
                            while (this.frecset.next())
                            {
                            	String u = this.frecset.getString(username)==null?"":this.frecset.getString(username);
        						String p = this.frecset.getString(password)==null?"":this.frecset.getString(password);
        						if(isencry){
        							p = des.DecryPwdStr(p);
        						}
                            	idset.add(u);
                            	String etoken = PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(u+","+p));
        						urlmap.put(u, basePath+"elearning/mylession/mobile/list.jsp?etoken="+etoken);
        						
                                String lesson_from = "0";
                                if (!"1".equals(state))
                                    lesson_from = "4";
                                
                                String person = this.frecset.getString("a0100");
                                if(pushmap.containsKey(person)){
                                	String r5000s = (String) pushmap.get(person);
                                	r5000s += "," + SafeCode.encode(PubFunc.encrypt(r5000));
                                	pushmap.put(person, r5000s);
                                } else{
                                	pushmap.put(person, SafeCode.encode(PubFunc.encrypt(r5000)));
                                	personlist.add(person);
                                }
                                
                                bo.pushCourse(r5000, dbpre, this.frecset.getString("a0100"), this.frecset.getString("b0110"), this.frecset.getString("e0122"), this.frecset.getString("e01a1"), this.frecset.getString("a0101"), lesson_from,"");
                            }
                            
                            if(persons != null && persons.length() > 0)
                                persons = persons.substring(0, persons.length()-1);
                            
                            pushmap.put(r5000, persons);
                            
                            if(idset.size()>0){
                            	String lesson_from = "0";
                                if (!"1".equals(state))
                                    lesson_from = "4";
                                
                                HashMap url = new HashMap();
            					for(Iterator it = idset.iterator();it.hasNext();){
            						String u = (String)it.next();
            						String ul = urlmap.get(u).toString();
            						int index = ul.indexOf("&");
            						if(index>-1){
            							String allurl = ul.substring(0,index);
            							String allparam = ul.substring(index);
            							ul += "&encryptParam="+PubFunc.encrypt("r5000="+r5000);
            						}
            						url.put(u, ul);
            					}
            					
                                bo.sendCourseToWX(r5000, lesson_from, picUrl, url, new ArrayList(idset));
                            }
                        }
                    }
                }
                
                
                emaillist = bo.pushLesson(personlist, dbpre, pushmap, basePath);
            }
            
          //发送邮件
	        AsyncEmailBo emailbo = new AsyncEmailBo(this.frameconn, this.userView);
	        emailbo.send(emaillist);
            
        } catch (Exception e)
        {
            flag = "error";
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally
        {
            this.getFormHM().put("flag", flag);
        }
    }

}
