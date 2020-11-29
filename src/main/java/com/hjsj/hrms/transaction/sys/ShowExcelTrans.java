package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.sys.logonuser.ShowExcel;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;

public class ShowExcelTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try {
			String commitor =(String)this.getFormHM().get("commitor");
			String name =(String)this.getFormHM().get("name");
			String beginexectime =(String)this.getFormHM().get("beginexectime");
			String endexectime =(String)this.getFormHM().get("endexectime");
			commitor=PubFunc.getStr(commitor);
			name=PubFunc.getStr(name);
			beginexectime=PubFunc.getStr(beginexectime);
			endexectime=PubFunc.getStr(endexectime);
			
			String bend = (String)this.getFormHM().get("beginexectimeend");
			String eend = (String) this.getFormHM().get("endexectimeend");
			bend = PubFunc.getStr(bend);
			eend = PubFunc.getStr(eend);
			String columnstr = "id,description,beginexectime,endexectime,execstatus,remoteaddr,commitor,isthree,b0110,eventlog";
	    	/*if((commitor == null || commitor.equals("")) && (name == null || name.equals("")) && (beginexectime ==null || beginexectime.equals("")) && (endexectime == null || endexectime.equals(""))){
	    		strsql.append("select "+columnstr+" from fr_txlog");
	    		haswhere=false;
	    	}else if((commitor == null || commitor.equals("")) && (name == null || name.equals("")) && (beginexectime ==null || beginexectime.equals(""))){
	    	   strsql.append("select "+columnstr+" from fr_txlog where endexectime like '%"+endexectime+"%'");
	    	}
	    	else if((commitor == null || commitor.equals("")) && (name == null || name.equals("")) && (endexectime == null || endexectime.equals(""))){
	     	   strsql.append("select "+columnstr+" from fr_txlog where beginexectime like '%"+beginexectime+"%'");
	    	}
	    	else if((commitor == null || commitor.equals("")) && (beginexectime ==null || beginexectime.equals("")) && (endexectime == null || endexectime.equals(""))){
	     	   strsql.append("select "+columnstr+" from fr_txlog where name like '%"+name+"%'");
	    	}
	    	else if((name == null || name.equals("")) && (beginexectime ==null || beginexectime.equals("")) && (endexectime == null || endexectime.equals(""))){
	     	   strsql.append("select "+columnstr+" from fr_txlog where commitor like '%"+commitor+"%'");
	     	}else if((commitor == null || commitor.equals("")) && (beginexectime ==null || beginexectime.equals(""))){
	      	   strsql.append("select "+columnstr+" from fr_txlog where name like '%"+name+"%' and endexectime like '%"+endexectime+"%'");
	     	}else if((commitor == null || commitor.equals("")) && (endexectime == null || endexectime.equals(""))){
	       	   strsql.append("select "+columnstr+" from fr_txlog where name like '%"+name+"%' and beginexectime like '%"+beginexectime+"%'");
	     	}else if((name == null || name.equals("")) && (beginexectime ==null || beginexectime.equals(""))){
	       	   strsql.append("select "+columnstr+" from fr_txlog where commitor like '%"+commitor+"%' and endexectime like '%"+endexectime+"%'");
	     	}else if((name == null || name.equals("")) && (endexectime == null || endexectime.equals(""))){
	       	   strsql.append("select "+columnstr+" from fr_txlog where commitor like '%"+commitor+"%' and beginexectime like '%"+beginexectime+"%'");
	     	}else if((beginexectime ==null || beginexectime.equals("")) && (endexectime == null || endexectime.equals(""))){
	       	   strsql.append("select "+columnstr+" from fr_txlog where commitor like '%"+commitor+"%' and name like '%"+name+"%'");
	     	}else if((commitor == null || commitor.equals("")) && (name == null || name.equals(""))){
	       	   strsql.append("select "+columnstr+" from fr_txlog where beginexectime like '%"+beginexectime+"%' and endexectime like '%"+endexectime+"%'");
	     	}else if((commitor == null || commitor.equals(""))){
	     		strsql.append("select "+columnstr+" from fr_txlog where name like '%"+name+"%' and beginexectime like '%"+beginexectime+"%' and endexectime like '%"+endexectime+"%'");
	     	}else if((name == null || name.equals(""))){
	     		strsql.append("select "+columnstr+" from fr_txlog where commitor like '%"+commitor+"%' and beginexectime like '%"+beginexectime+"%' and endexectime like '%"+endexectime+"%'");
	     	}else if((beginexectime ==null || beginexectime.equals(""))){
	     		strsql.append("select "+columnstr+" from fr_txlog where commitor like '%"+commitor+"%' and name like '%"+name+"%' and endexectime like '%"+endexectime+"%'");
	     	}else if((endexectime == null || endexectime.equals(""))){
	     		strsql.append("select "+columnstr+" from fr_txlog where commitor like '%"+commitor+"%' and name like '%"+name+"%' and beginexectime like '%"+beginexectime+"%'");
	     	}else{
	    	   strsql.append("select "+columnstr+" from fr_txlog where commitor like '%"+commitor+"%' and name like '%"+name+"%' and beginexectime like '%"+beginexectime+"%' and endexectime like '%"+endexectime+"%'");
	     	}*/
			//操作日志按日期查询  jingq upd 2014.09.15
        	StringBuffer timesql = new StringBuffer();
        	//开始时间
        	if(!"".equals(beginexectime)&&!"".equals(bend)&&!beginexectime.equals(bend)){
        		timesql.append(" and beginexectime between '"+beginexectime+" 00:00:00' and '"+bend+" 23:59:59'");
        	} else if("".equals(beginexectime)&&!"".equals(bend)){
        		timesql.append(" and beginexectime <= '"+bend+"'");
        	} else if(!"".equals(beginexectime)&&"".equals(bend)){
        		timesql.append(" and beginexectime >= '"+beginexectime+"'");
        	} else if(!"".equals(beginexectime)&&!"".equals(bend)&&beginexectime.equals(bend)){
        		timesql.append(" and beginexectime like '%"+beginexectime+"%'");
        	} else {
        		timesql.append("");
        	}
        	//结束时间
        	if(!"".equals(endexectime)&&!"".equals(eend)&&!endexectime.equals(eend)){
        		timesql.append(" and endexectime between '"+endexectime+" 00:00:00' and '"+eend+" 23:59:59'");
        	} else if("".equals(endexectime)&&!"".equals(eend)){
        		timesql.append(" and endexectime <= '"+eend+"'");
        	} else if(!"".equals(endexectime)&&"".equals(bend)){
        		timesql.append(" and endexectime >= '"+endexectime+"'");
        	} else if(!"".equals(endexectime)&&!"".equals(eend)&&endexectime.equals(eend)){
        		timesql.append(" and endexectime like '%"+endexectime+"%'");
        	} else {
        		timesql.append("");
        	}
        	
        	boolean haswhere = true;
        	StringBuffer strsql=new StringBuffer();
        	if("".equals(commitor)&&"".equals(name)&&"".equals(timesql)){
        		strsql.append("select * from fr_txlog");
        		haswhere =false;
        	} else if(!"".equals(commitor)&&!"".equals(name)){
        		strsql.append("select * from fr_txlog where commitor like '%"+commitor+"%' and name like '%"+name+"%'");
        	} else if(!"".equals(commitor)&&"".equals(name)){
        		strsql.append("select * from fr_txlog where commitor like '%"+commitor+"%'");
        	} else if("".equals(commitor)&&!"".equals(name)){
        		strsql.append("select * from fr_txlog where name like '%"+name+"%'");
        	} else {
        		strsql.append("select * from fr_txlog where 1=1");
        	}
	    	SearchLogListTrans slt = new SearchLogListTrans();
	    	strsql.append(haswhere?(" and "+slt.getPrivSQL(userView, this.frameconn,"query").substring(6)):slt.getPrivSQL(userView, this.frameconn,"query"));
	    	strsql.append(timesql);
        	this.getFormHM().clear();
        	//xus 20/5/8 【60147】VFS+UTF-8+达梦：系统管理/安全策略/操作日志，导出excel和页面显示不一样，见附件
	    	strsql.append(" order by beginexectime desc");
	    	ArrayList column = new ArrayList();//中文字段名
	    	ArrayList infolist = new ArrayList();//各列数值
	    	ArrayList columnlist = new ArrayList();//列名
	    	/*column.add(ResourceFactory.getProperty("menu.function"));
	    	column.add("功能名称");
	    	column.add("开始日期");
	    	column.add("结束日期");
	    	column.add("完成标识");
	    	column.add("主机IP");
	    	column.add("用户名");
	    	column.add("用户类型");
	    	column.add("单位");
	    	column.add("详细");*/
	    	column.add(ResourceFactory.getProperty("menu.function"));
	    	column.add(ResourceFactory.getProperty("column.submit.function"));
	    	column.add(ResourceFactory.getProperty("column.submit.begindate"));
	    	column.add(ResourceFactory.getProperty("column.submit.enddate"));
	    	column.add(ResourceFactory.getProperty("column.submit.execstate"));
	    	column.add(ResourceFactory.getProperty("column.submit.romoteaddr"));
	    	column.add(ResourceFactory.getProperty("column.submit.username"));
	    	column.add(ResourceFactory.getProperty("column.submit.usertype"));
	    	column.add(ResourceFactory.getProperty("tree.unroot.undesc"));
	    	column.add(ResourceFactory.getProperty("kq.strut.more"));
	    	ContentDAO dao = new ContentDAO(this.frameconn);
	    	
			this.frowset = dao.search(strsql.toString());
			while(this.frowset.next()){
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("id",PubFunc.nullToStr(this.frowset.getString("id")));
					bean.set("description",PubFunc.nullToStr(this.frowset.getString("description")));
					bean.set("beginexectime",PubFunc.getStr(PubFunc.nullToStr(this.frowset.getString("beginexectime"))));
					bean.set("endexectime",PubFunc.getStr(PubFunc.nullToStr(this.frowset.getString("endexectime"))));
					bean.set("execstatus",PubFunc.getStr(PubFunc.nullToStr(this.frowset.getString("execstatus"))));
					bean.set("remoteaddr",PubFunc.nullToStr(this.frowset.getString("remoteaddr")));
					bean.set("commitor",PubFunc.nullToStr(this.frowset.getString("commitor")));
					String isthree=PubFunc.nullToStr(this.frowset.getString("isthree"));
					/**
					 * label.role.sys=系统管理员
						label.role.sycrecy=安全保密员
						label.role.auditor=安全审计员
					 */
					String usertype = " ";
					if("0".equals(isthree))
						usertype = ResourceFactory.getProperty("column.submit.usertype.putong");
					if("1".equals(isthree))
						usertype=ResourceFactory.getProperty("label.role.sys");
					else if("2".equals(isthree))
						usertype=ResourceFactory.getProperty("label.role.sycrecy");
					else if("3".equals(isthree))
						usertype=ResourceFactory.getProperty("label.role.auditor");
					bean.set("isthree",usertype);
					String unit=PubFunc.nullToStr(this.frowset.getString("b0110"));
					String fullPriv=PubFunc.getTopOrgDept(unit);
    	    		String priv = fullPriv.split("`")[0];
    	    		String desc = com.hrms.frame.utility.AdminCode.getCodeName("UN", priv);
    	    		if(desc==null)
    	    			desc= com.hrms.frame.utility.AdminCode.getCodeName("UM", priv);
					bean.set("b0110",desc);
					//--------------由于薪资变动日志加入了表格用于布局，导出的时候过滤掉html代码  zhaoxg add 2015-5-11-----
					String eventlog = PubFunc.nullToStr(this.frowset.getString("eventlog"));
					eventlog = eventlog.replaceAll("<table>", "\n");
					eventlog = eventlog.replaceAll("</table>", "");
					eventlog = eventlog.replaceAll("<td>", "");
					eventlog = eventlog.replaceAll("</td>", "    ");
					eventlog = eventlog.replaceAll("<tr>", "");
					eventlog = eventlog.replaceAll("</tr>", "\n");
					eventlog = eventlog.replaceAll("<br>", "\n");
					//-------------------------------------------end--------------------------------------------
					bean.set("eventlog",eventlog);
					infolist.add(bean);
			}
			
			columnlist.add("id");
			columnlist.add("description");
			columnlist.add("beginexectime");
			columnlist.add("endexectime");
			columnlist.add("execstatus");
			columnlist.add("remoteaddr");
			columnlist.add("commitor");
			columnlist.add("isthree");
			columnlist.add("b0110");
			columnlist.add("eventlog");
			ShowExcel show = new ShowExcel();
			String filename = show.creatExcel(column,infolist,columnlist);
			//xus 20/4/18 vfs 改造
//			filename = SafeCode.encode(PubFunc.encrypt(filename));
			filename = PubFunc.encrypt(filename);
			this.getFormHM().put("excelfile",filename);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
}
