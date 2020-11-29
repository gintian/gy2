package com.hjsj.hrms.transaction.workplan.summary;

import com.hjsj.hrms.businessobject.workplan.WorkPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant;
import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant.Cycle;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.summary.WorkPlanSummaryBo;
import com.hjsj.hrms.businessobject.workplan.summary.WorkSummaryMethodBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PersonListTrans extends IBusiness {
	
	public void execute() throws GeneralException
	{
	    try {
    		String para = (String) this.getFormHM().get("para");
    		String type = (String) this.getFormHM().get("type");
    		String p0100 = (String) this.getFormHM().get("p0100");
    		p0100 = WorkPlanUtil.decryption(p0100);
    		String month = (String) this.getFormHM().get("month");
    		month = SafeCode.decode(month);
    		String week = (String) this.getFormHM().get("week");
    		String nbase = (String) this.getFormHM().get("nbase");
    		nbase = WorkPlanUtil.decryption(nbase);
    		String a0100 = (String) this.getFormHM().get("a0100");
    		a0100 = WorkPlanUtil.decryption(a0100);
    		String summaryCycle = (String) this.getFormHM().get("cycle");
            String summaryYear = (String)this.getFormHM().get("year");
            String querytype = (String)this.getFormHM().get("querytype");
            
    		nbase = "".equalsIgnoreCase(nbase) ? this.getUserView().getDbname() : nbase;
    		a0100 = "".equalsIgnoreCase(a0100) ? this.getUserView().getA0100() : a0100;
    		// 转码
    		para = SafeCode.decode(para).trim();
    		WorkPlanSummaryBo bo = new WorkPlanSummaryBo(userView,this.frameconn);
    		if (para != null && para.trim().length() > 0)
    		{
    			//搜索结果列表
    			if ("searchperson".equalsIgnoreCase(type))
    			{
    				int num = ((Integer) this.getFormHM().get("num")).intValue();
    				String e0122 = (String) this.getFormHM().get("e0122");
    				e0122=WorkPlanUtil.decryption(e0122); 
    				ArrayList list = bo.searchPersonList(para,p0100,WorkPlanConstant.TaskInfo.MAX_CANDIDATE_NUMBER,num,nbase,a0100,e0122,querytype);
    				this.getFormHM().put("list", list);
    			}
    			
    		}//显示关注人
    		else if ("addperson".equalsIgnoreCase(type))
			{
				//添加关注人,先找出该总结所有关注人并放入existedPeopleList集合中,如果新添加的人员不在existedPeopleList中,则可以添加
				String objectIds = (String) this.getFormHM().get("objectId");
				if(StringUtils.isNotBlank(objectIds))
					objectIds = objectIds.substring(0, objectIds.length()-1);
				String[] arrIds = objectIds.split("\\^");
				String objectId = "";
				StringBuffer sbf = new StringBuffer();
				sbf.append("select nbase, a0100 from p09 where p0901 = 3 and p0903 = ?");
				RowSet rs = null;
				ContentDAO dao = new ContentDAO(frameconn);
				rs = dao.search(sbf.toString(), Arrays.asList(new Object[]{p0100}));
				List existedPeopleList = new ArrayList();
				existedPeopleList.add(this.userView.getDbname() + this.userView.getA0100());
				while(rs.next()){
					existedPeopleList.add(rs.getString("nbase") + rs.getString("a0100"));
				}
				ArrayList list = new ArrayList();
				for(int i=0;i<arrIds.length;i++){
					HashMap map = new HashMap();
					objectId = PubFunc.decryption(arrIds[i]);
					if(existedPeopleList.contains(objectId) || StringUtils.isBlank(objectId)){
						continue;
					}
					map = bo.addperson(p0100,objectId);
					list.add(map);
				}
				this.getFormHM().put("list", list);
			}
    		else if ("photolist".equalsIgnoreCase(type)) {
    			ArrayList list = bo.searchPhotoList(p0100);
    			this.getFormHM().put("list", list);
    		}else if ("photodelete".equalsIgnoreCase(type)) {
    			String p0900 = WorkPlanUtil.decryption((String)this.getFormHM().get("p0900"));
    			bo.deletePhoto(p0900);
    		}
    		//显示关注人地图
    		else if ("personmap".equalsIgnoreCase(type))
    		{
    			
    			//int rownum =  ((Integer) this.getFormHM().get("rownum")).intValue();
    			int num = Integer.parseInt((String) this.getFormHM().get("num"));
    			
    			ArrayList list = bo.searchPersonMap(this.userView.getA0100(), 
    			        Integer.parseInt(summaryCycle),
    			        Integer.parseInt(summaryYear),Integer.parseInt(month), 
    			        week, WorkPlanConstant.PAGESIZE, num);
    			String renum=(String) ((HashMap) list.get(list.size()-1)).get("num");
    			String toIndex=(String) ((HashMap) list.get(list.size()-1)).get("toIndex");
    			list.remove(list.size()-1);
    			this.getFormHM().put("list",  list.subList(( Integer.parseInt(renum) - 1) * WorkPlanConstant.PAGESIZE, Integer.parseInt(toIndex)));
    			this.getFormHM().put("num", renum);
    			this.getFormHM().put("type", type);
    		}
    		else if ("personorgmap".equalsIgnoreCase(type)){
    			int num = Integer.parseInt((String) this.getFormHM().get("num")); //第几页
    			int rownum =  WorkPlanConstant.PAGESIZE;
    			ArrayList orglist = new ArrayList();
    			WorkPlanUtil wputil = new WorkPlanUtil(getFrameconn(), getUserView());
				ArrayList deptlist = wputil.getDeptList(nbase, a0100);
				int pagecount = deptlist.size(); //总数
				if ((num-1)*rownum-pagecount ==0 || (num-1)*rownum > pagecount ){
                    if (num>1)
                    	num=num-1;  
                }
			        int begin_index=(num-1)*rownum;
			        int end_index=num*rownum;
			        if (end_index>pagecount) end_index=pagecount;
				  
		            for (int i=begin_index;i<end_index;i++){
					   LazyDynaBean bean = (LazyDynaBean) deptlist.get(i);
		                HashMap mp = new HashMap();
	                    WorkPlanBo pb = new WorkPlanBo(this.frameconn, userView);
	                    String photo = pb.getPhotoPath(userView.getDbname(), userView.getA0100());
	                    String a0101 = userView.getUserFullName();
	                    String deptdesc = (String) bean.get("deptdesc");
	                    String b0110 = (String) bean.get("b0110");
	                    mp.put("url", photo);
	                    mp.put("a0101", a0101);
	                    mp.put("pos", deptdesc);
	                    mp.put("belong_type", "2");
	                    mp.put("E0122",  WorkPlanUtil.encryption(b0110));
	                    mp.put("a0100", WorkPlanUtil.encryption(userView.getA0100()));
	                    mp.put("nbase", WorkPlanUtil.encryption(userView.getDbname()));
	                    orglist.add(mp);
		            }
				   
				    this.getFormHM().put("list", orglist);
	    			this.getFormHM().put("num", num+"");
	    			this.getFormHM().put("type", type);
    		}
    		// 团队地图
    		else if ("teammap".equalsIgnoreCase(type))
    		{
    			if (null == week || "".equals(week))
    			{
    				String[] planCycles = { Cycle.Day, Cycle.WEEK, Cycle.MONTH, Cycle.QUARTER, Cycle.YEAR, Cycle.HALFYEAR };
    				int cycle = Integer.parseInt(planCycles[Integer.parseInt(summaryCycle)]); 
    				WorkPlanUtil workPlanUtil = new WorkPlanUtil(getFrameconn(), getUserView());
    				int[] weeks = workPlanUtil.getLocationPeriod(cycle + "", Integer.parseInt(summaryYear), Integer.parseInt(month));
    				WorkPlanSummaryBo wpsBo = new WorkPlanSummaryBo();
    				week = wpsBo.getCurCycleIndex(summaryCycle, summaryYear,month, weeks[2]+"");
    			}
    			String[] summaryDates = bo.getSummaryDates(summaryCycle,summaryYear, month, Integer.parseInt(week));
    			String startTime = summaryDates[0];
    			String endTime = summaryDates[1];
    			int num = Integer.parseInt((String) this.getFormHM().get("num"));
    			WorkSummaryMethodBo methodBo = new WorkSummaryMethodBo(this.getUserView(),this.getFrameconn());
    		
        		LazyDynaBean bean = new LazyDynaBean();
        		String flag= (String) this.getFormHM().get("flag");
        		if("false".equals(flag)){//当flag=false时，是说明从人力地图的缺编岗位进来的
        			 String e01a1 = (String) this.getFormHM().get("e01a1");
        			 e01a1 = WorkPlanUtil.decryption(e01a1);
        			 ContentDAO dao=new ContentDAO(getFrameconn());
        			 //取直接上级字段
        			 String superoirField = new WorkPlanUtil(getFrameconn(), userView).getSuperiorFld();
        			 if (superoirField == null || "".equals(superoirField)) {
        					return;
        			}
        			//由当前岗位查找下级岗位
                 	 ArrayList e01a1List=new ArrayList();
                 	 String sql="select K.e01a1,O.codeitemdesc from k01 K,organization O where K.e01a1=O.codeitemid and "+superoirField+"='"+e01a1+"'";
 	       			 RowSet rset=dao.search(sql);
 	       			 while(rset.next()){
 	       				 LazyDynaBean e01a1bean = new LazyDynaBean();
 	       				 e01a1bean.set("e01a1", rset.getString("e01a1"));
 	       				 e01a1bean.set("codeitemdesc", rset.getString("codeitemdesc"));
 	       				 e01a1List.add(e01a1bean);
 	       			 }
 	       		     bean = methodBo.getTeamList(e01a1List,WorkPlanConstant.PAGESIZE,num,startTime,endTime,summaryCycle);
        		}else{
        			 bean = methodBo.getTeamList(nbase,a0100,WorkPlanConstant.PAGESIZE,num,startTime,endTime,summaryCycle);
        		}
        		String num1 =(String)bean.get("num");
        		if (num1!=null){
        			num= Integer.parseInt(num1);
        		}
    			 //分页
    	        ArrayList list= new ArrayList();
    	        int pagecount=((ArrayList) bean.get("list")).size();
    	        int begin_index=(num-1)*WorkPlanConstant.PAGESIZE;
    	        int end_index=num*WorkPlanConstant.PAGESIZE;
    	        if (end_index>pagecount) end_index=pagecount;
    	        for (int i=begin_index;i<end_index;i++){
    	        	list.add(((ArrayList) bean.get("list")).get(i));
    	        }
    	        
    			this.getFormHM().put("list",list);
    			this.getFormHM().put("num", bean.get("num"));
    			this.getFormHM().put("count", bean.get("count"));
    			this.getFormHM().put("type", type);
    		}
    		else if ("orgmap".equalsIgnoreCase(type)) {
    			 WorkPlanBo pb = new WorkPlanBo(getFrameconn(), getUserView());
    			 WorkPlanUtil wputil = new WorkPlanUtil(getFrameconn(), getUserView());
    			 int num = Integer.parseInt((String) this.getFormHM().get("num"));
    			 String e01a1s = (String) this.getFormHM().get("e01a1");
    			// e01a1s=(e01a1s==null)?"false":e01a1s;
    			 e01a1s=WorkPlanUtil.decryption(e01a1s); 
    			 ArrayList e01a1list = new ArrayList();
    			 if (e01a1s!=null && !"".equals(e01a1s)){
                     String [] arre01a1 =e01a1s.split(",");                        
                     for (int i=0;i<arre01a1.length;i++){
                         String e01a1= arre01a1[i];
                         if ("".equals(e01a1)) continue;
                         LazyDynaBean bean = new LazyDynaBean();                   
                         bean.set("e01a1", e01a1);
                         e01a1list.add(bean);                           
                     }
                 } else {                
                     e01a1list=  wputil.getMyE01a1List(nbase, a0100);
                 }
                 String info = pb.getMySubDeptList(e01a1list, num);  
                 info="{\"orgmap\":["+info+"]}"; 
                 this.getFormHM().put("info", info);
     			this.getFormHM().put("type", type);
    			
    		}
    		//map列表
    		else if ("initselectmaplist".equalsIgnoreCase(type)) {
    				String isteam="false";
    				String isperson = "false";
    				String isorg ="false";
    				String issuborg = "false";
    				if("person".equals(querytype)){//haosl 20161129
	    				//是否有关注人
	    				isperson=bo.isHavePerson(Integer.parseInt(summaryCycle),
	        			        Integer.parseInt(summaryYear),Integer.parseInt(month), week);
	    				//是否有团队成员
	    				isteam=bo.isHaveTeam();
    				}else{
    					//是否有下属部门
    					issuborg=bo.isHaveSubOrg();
    					//是否是部门负责人
    					isorg=bo.isHaveOrg();
    				}
    				
    				this.getFormHM().put("isteam", isteam);
    				this.getFormHM().put("isperson", isperson);
    				this.getFormHM().put("isorg", isorg);
                  	this.getFormHM().put("issuborg", issuborg);
    			this.getFormHM().put("querytype", querytype);
    		}
			else if ("orgsumshow".equalsIgnoreCase(type)) {
				String personsum = "none";
				String orgsum = "none";
				if ("org".equals(querytype)) {
					if ("true".equals(bo.isHaveSubOrg())) {
						orgsum = "block";
					}
					personsum = "block";
				}
				else if ("person".equals(querytype)) {
					if ("false".equals(bo.isHaveOrg())) {
						if ("true".equals(bo.isHaveSubOrg())) {
							orgsum = "block";
						}
						else if ("true".equals(bo.isHaveTeam())) {
							personsum = "block";
						}
					}
				}

				this.getFormHM().put("personsum", personsum);
				this.getFormHM().put("orgsum", orgsum);
			}
        } catch (Exception e) {
        	e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
	}
}
