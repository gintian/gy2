package com.hjsj.hrms.transaction.train;

import com.hjsj.hrms.businessobject.gz.GzSpFlowBo;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.transaction.train.b_plan.MessBean;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchEvaluationDetails extends IBusiness{

    public void execute() throws GeneralException {
        MessBean msb = null;
        
        String hotLessonName = "";
        String courseName = "";
        String examName = "";
        int k = 0;
        String target = "il_body";
        int j = 0;
        
      HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
      String flag = (String)hm.get("flag");
      flag = flag == null ? "" : flag;        
      hm.remove("flag");
      this.getFormHM().put("flag", flag);
      if("hot".equals(flag))
          target = "_blank";
        
        RowSet hotRowSet = null;
        RowSet examRowSet = null;
        RowSet courseRowSet = null;
        
        ArrayList list = new ArrayList();
        ArrayList lists = null;
        
        hotRowSet = TrainCourseBo.getHotLesson(this.getFrameconn());
        
      if("".equals(flag))
        {
            examRowSet = getTrainExams(this.getFrameconn());  
            courseRowSet = getTrainCourses(this.getFrameconn()); 
            
            GzSpFlowBo gsf = new GzSpFlowBo(this.getFrameconn(),this.userView);
            lists = gsf.getHotInvestigateList("train");
        }
        
        try {
            //评估
            if(lists!=null && lists.size() > 0)
            {            
                for(int i=0; i<lists.size(); i++)
                {
                    LazyDynaBean bean = (LazyDynaBean)lists.get(i);   
                    String hzname = (String)bean.get("name");               
                    String url = (String)bean.get("url");    
                    url = url.replace("enteryType=1", "enteryType=0&enteryFlag=1");//enteryflag=0 热点调查； =1：学习评估
    
                    j = hzname.indexOf(".");
                    hzname = hzname.substring(j+1);
                    hotLessonName = "<img src=\"/images/forumme1.gif\">&nbsp;<a href=\""+url+"&home=5&ver=5\" target=\""+target+"\">"+/*(i+1)+". "+*/subText(hzname,100)+"</a>";
                    msb = new MessBean();
                    msb.setContent(hotLessonName);
					if (i == 0) {
						msb.setKeyid("tra");
					}
                    list.add(msb);
                    ++k;
                }
            }
            
            //考试
            if(examRowSet != null){
                if(examRowSet.next()){      
                    String url = "/train/resource/myexam.do?b_query=link&type=0";  
                    
                    examName ="<img src=\"/images/forumme1.gif\">&nbsp;<a href=\"" + url + "&home=5&ver=5\" target=\""+target+"\">"+ "(考试) " + (String)examRowSet.getString("R5401") + "</a>";   
                    msb = new MessBean();
                    msb.setContent(examName);
                    msb.setKeyid("exa");
                    list.add(msb);
                    
                    while(examRowSet.next()){
                        examName ="<img src=\"/images/forumme1.gif\">&nbsp;<a href=\"" + url + "&home=5&ver=5\" target=\""+target+"\">"+ "(考试) " + (String)examRowSet.getString("R5401") + "</a>";   
                        msb = new MessBean();
                        msb.setContent(examName);
                        list.add(msb);
                    }
                }
            }
            
            //课程
            if(courseRowSet != null){
                if(courseRowSet.next()){    
                    String r5004 = "";
                    r5004 = courseRowSet.getString("R5004");
                    r5004 = r5004 == null ? "" : r5004;
                    String lesson_from = courseRowSet.getString("lesson_from").toString();
                    String lprogress = courseRowSet.getString("lprogress").toString();

                    if("0".equalsIgnoreCase(lesson_from)){
                       	courseName = "<img src=\"/images/forumme1.gif\">&nbsp;<a href=\"javascript:;\" onclick=\"learn('" + SafeCode.encode(PubFunc.encrypt(courseRowSet.getString("R5000").toString())) +"','" + SafeCode.encode(PubFunc.encrypt(r5004)) + "')\">" + "(自选) " + (String)courseRowSet.getString("R5003") + "(" + lprogress + "%)" +"</a>"; 
                    } else {
                       	courseName = "<img src=\"/images/forumme1.gif\">&nbsp;<a href=\"javascript:;\" onclick=\"learn('" + SafeCode.encode(PubFunc.encrypt(courseRowSet.getString("R5000").toString())) +"','" + SafeCode.encode(PubFunc.encrypt(r5004)) + "')\">" + "(推送) " + (String)courseRowSet.getString("R5003") + "( " + lprogress + "%)" +"</a>"; 
                    }
                            
                    msb = new MessBean();
                    msb.setContent(courseName);
                    msb.setKeyid("cla");
                    list.add(msb);
                    
                    while(courseRowSet.next()){
                        String r5000 = courseRowSet.getString("R5000").toString();
                        lesson_from = courseRowSet.getString("lesson_from").toString();
                        lprogress = courseRowSet.getString("lprogress").toString();

                        r5004 = courseRowSet.getString("R5004");
                        r5004 = r5004 == null ? "" : r5004;
                        if("0".equalsIgnoreCase(lesson_from)){
                           	courseName = "<img src=\"/images/forumme1.gif\">&nbsp;<a href=\"javascript:;\" onclick=\"learn('" + SafeCode.encode(PubFunc.encrypt(r5000)) +"','" + SafeCode.encode(PubFunc.encrypt(r5004)) + "')\">" + "(自选) " + (String)courseRowSet.getString("R5003") + "(" + lprogress + "%)" +"</a>";
                        } else {
                           	courseName = "<img src=\"/images/forumme1.gif\">&nbsp;<a href=\"javascript:;\" onclick=\"learn('" + SafeCode.encode(PubFunc.encrypt(r5000)) +"','" + SafeCode.encode(PubFunc.encrypt(r5004)) + "')\">" + "(推送) " + (String)courseRowSet.getString("R5003") + "(" + lprogress + "%)" +"</a>";
                        }
                        msb = new MessBean();
                        msb.setContent(courseName);
                        list.add(msb);
                    }
                }
            }
            
            //热门课程
            if(hotRowSet != null){  
                if(hotRowSet.next()){                
                    if(!"".equals(getDescById(this.getFrameconn(), hotRowSet.getString("r5000").toString())) && getDescById(this.getFrameconn(), hotRowSet.getString("r5000").toString()) != null){
                        String[] s = getDescById(this.getFrameconn(), hotRowSet.getString("r5000").toString()).split(",");
                        String text = "";
                        String r5004 = "";
                        String msg = "";
                        if(s.length == 1){
                            text = s[0];
                        }else if(s.length == 3){
                            text = s[0];
                            r5004 = s[1];
							msg = s[2];
                        }
                        hotLessonName = "<img src=\"/images/forumme1.gif\">&nbsp;<a href=\"javascript:;\" onclick=\"learn('" + SafeCode.encode(PubFunc.encrypt(hotRowSet.getString("r5000").toString())) +"','" + SafeCode.encode(PubFunc.encrypt(r5004)) + "','"+msg+"')\">" + "(热门课程)" + text + "</a>";
                        msb = new MessBean();
                        msb.setContent(hotLessonName);
                        msb.setKeyid("hot");
                        list.add(msb);
                    }
                    
                    while(hotRowSet.next()){
                        String r5000 = hotRowSet.getString("r5000").toString();
                        String desc = getDescById(this.getFrameconn(), r5000);
                        if(!"".equals(desc) && desc != null){
                            String[] s = desc.split(",");
                            String text = "";
                            String r5004 = "";
                            String msg = "";
                            if(s.length == 1){
                                text = s[0];
                            }else if(s.length == 3){
                                text = s[0];
                                r5004 = s[1];
                                msg = s[2];
                            }
                            hotLessonName = "<img src=\"/images/forumme1.gif\">&nbsp;<a href=\"javascript:;\" onclick=\"learn('" +  SafeCode.encode(PubFunc.encrypt(r5000)) +"','" +  SafeCode.encode(PubFunc.encrypt(r5004)) + "','"+msg+"')\">" + "(热门课程)" + text + "</a>";
                            msb = new MessBean();
                            msb.setContent(hotLessonName);
                            list.add(msb);
                        }
                    }
                }
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(list.size() > 0){
                this.getFormHM().put("list", list);
            }
        }
    }
    
     //在线考试
     private RowSet getTrainExams(Connection conn)
        {
            RowSet rs = null;
            
            //查询学员在线考试(已启动的提前10天开始提醒）
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT R5401");
            sql.append(" FROM R54");
            sql.append(" WHERE r5411='05'");
            sql.append(" AND (R5405-10)<=" + Sql_switcher.sqlNow());
            sql.append(" AND r5400 in (SELECT r5400 FROM R55 B");
            sql.append(" WHERE B.nbase='" + this.userView.getDbname() + "'");
            sql.append(" AND B.A0100='" + this.userView.getA0100() + "'");
            sql.append(" AND B.R5513=-1)");
            
            ContentDAO dao=new ContentDAO(conn);
            try
            {
                rs = dao.search(sql.toString());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            
            return rs;
        }
     //培训课程
     private RowSet getTrainCourses(Connection conn)
        {
            RowSet rs = null;
            
            //查询未学完的课程
            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT L.R5000,R.R5003,R.R5004,L.lprogress,CASE L.LESSON_FROM WHEN 1 THEN 0 WHEN 0 THEN 1 ELSE LESSON_FROM END LESSON_FROM");
            sql.append(" FROM tr_selected_lesson L LEFT JOIN R50 R");
            sql.append(" ON L.R5000=R.R5000");
            sql.append(" WHERE L.nbase='" + this.userView.getDbname() + "'");
            sql.append(" AND L.A0100='" + this.userView.getA0100() + "'");
            sql.append(" AND " + Sql_switcher.isnull("L.lprogress", "0") + "<100");
            sql.append(" AND R.R5022='04'");
            sql.append(" ORDER BY LESSON_FROM DESC,ID DESC");
            
            ContentDAO dao=new ContentDAO(conn);
            try
            {
                rs = dao.search(sql.toString());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            
            return rs;
        }
     
    private String getDescById(Connection conn,String id){
        String desc = "";
        RowSet rs = null;
        
        StringBuffer sql = new StringBuffer();
        sql.append("select r5003,r5004");
        sql.append(" from r50");
        sql.append(" where r5000 = "+id);
        
        StringBuffer sqls = new StringBuffer();
        sqls.append("select r5000");
    	sqls.append(" from tr_selected_lesson");
    	sqls.append(" where r5000 = "+id+" and nbase = '");
    	sqls.append(this.userView.getDbname());
    	sqls.append("' and a0100 = '");
    	sqls.append(this.userView.getA0100());
    	sqls.append("'");
        
        ContentDAO dao=new ContentDAO(conn);
        try {
            rs = dao.search(sql.toString());
            if(rs.next()){
            	RowSet rss = dao.search(sqls.toString());
            	if(rss.next())
					desc = rs.getString("r5003") + "," + rs.getString("r5004")+",1" ;
				else
					desc = rs.getString("r5003") + "," + rs.getString("r5004")+",0" ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return desc;
    }
    
    private String subText(String text,int sublen)
    {
        if(text==null||text.length()<=0)
            return "";
        try {
            text = new String(text.getBytes(),"GB2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(text.length()<sublen)
            return text;
        text=text.substring(0,sublen)+"...";
        return text;
    }
}
