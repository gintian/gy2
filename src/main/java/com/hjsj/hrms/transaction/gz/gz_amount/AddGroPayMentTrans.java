package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.gz.GrossManagBo;
import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class AddGroPayMentTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		StringBuffer info = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String times = (String)hm.get("times");
		times=times!=null&&times.trim().length()>0?times:"";
		String[] tmp = times.split("`");
		times=tmp[0];
		String createType=tmp[1].split("-")[0];//=1创建当前机构
		String createNextType=tmp[1].split("-")[1];//=1创建下级
		String createAllNextType=tmp[1].split("-")[2];//=1创建所有下级（不包含部门） zhaoxg 2014-4-23
		if("0".equals(createType)&& "0".equals(createNextType)&& "0".equals(createAllNextType))
			return;
		String cascadingctrl=(String)hm.get("cascadingctrl");//=0级联下级=1不级联
		String viewUnit=(String)hm.get("viewUnit");
		String aa_code = (String)hm.get("code");
		String acodeitemid = (String)hm.get("codeitemid");
		acodeitemid=acodeitemid!=null&&acodeitemid.trim().length()>0?acodeitemid:"";
		String ctrl_peroid = (String)hm.get("ctrl_peroid");
		String fieldsetid = (String)hm.get("fieldsetid");
		fieldsetid=fieldsetid!=null&&fieldsetid.trim().length()>0?fieldsetid:"";
		GzAmountXMLBo bo = new GzAmountXMLBo(this.getFrameconn(),2);
		HashMap map = bo.getMap();
		HashMap aMap = bo.getValuesMap();
		//GrossPayManagement gross = new GrossPayManagement(this.getFrameconn(),"GZ_PARAM");
	 	ArrayList spflaglist =(ArrayList)map.get("sp") /*gross.elementName("/Params/Gz_amounts","sp_flag")*/;
		
		ArrayList srclist =(ArrayList)map.get("plan") /*gross.elementName("/Params/Gz_amount/relation","src")*/;
		ArrayList destlist =(ArrayList)map.get("formulalist") /*gross.elementName("/Params/Gz_amount/relation","dest")*/;
		HashMap mac = bo.getValuesMap();//dml
		String fc_flag=(String)mac.get("fc_flag");
		GrossManagBo mab = new GrossManagBo(this.getFrameconn());
		if(fc_flag!=null&&fc_flag.length()!=0){
			mab.setFc_flag(fc_flag);
		}
		boolean flag=false;
		ArrayList timearr = timeArr(times);
		if("2".equalsIgnoreCase(ctrl_peroid)){
			timearr = timeArr(times,ctrl_peroid);
		}
		ArrayList sql_list = new ArrayList();
		 String cishu="";
		HashMap exitmap=new HashMap();
		mab.setCascadingctrl(cascadingctrl);
		mab.setViewUnit(viewUnit);
		String ctrlAmountField="";
		if(aMap.get("ctrl_field")!=null)
			ctrlAmountField=(String)aMap.get("ctrl_field");
		ArrayList linkList = mab.getChildLink(acodeitemid, createType, ctrlAmountField, userView,createNextType,createAllNextType);
		/***
		 * 如果定义启动总额控制指标参数，在主集中没有记录的，或者指标值为否的，不能建立，
		 * 如果没有定义这个参数，主集中没有，程序自动加一条进去。
		 */
		for(int j=0;j<linkList.size();j++)
		{
			LazyDynaBean lazyBean=(LazyDynaBean)linkList.get(j);
			String ctrl_value=(String)lazyBean.get("ctrl_value");
			String b0110=(String)lazyBean.get("b0110");
			String codeitemdesc=(String)lazyBean.get("codeitemdesc");
			if("".equals(ctrl_value)|| "2".equals(ctrl_value))//主集中没有记录，或者启用总额控制指标为否，不予建立记录
			{
				if("".equals(ctrl_value))
				{
			    	info.append("["+codeitemdesc+"] 在单位主集中没有记录或者启用总额控制指标值为空，不能新建!\\n");
				}
				continue;
			}
			String codeitemid=(String)lazyBean.get("codeitemid");
			String a_code=(String)lazyBean.get("codesetid");
    		for(int i=0;i<timearr.size();i++){
	    		String selectsql = "";
		    	
		    	String tim=(String)timearr.get(i);
		        String year=tim.substring(0,tim.indexOf("-"));
		        String month = tim.substring(tim.indexOf("-")+1);
		   
	    	    HashMap sucess=new HashMap();
	    	    String tmon="";
	    	    if(fc_flag!=null&&fc_flag.length()!=0){
	    	    	if("0".equalsIgnoreCase(ctrl_peroid)){
	    		    	cishu=this.getCi(month, year, ctrl_peroid, fieldsetid, codeitemid);
	    		    }
	    		    if("1".equalsIgnoreCase(ctrl_peroid)){
		    	    	if("01".equalsIgnoreCase(month)){
		    	    		cishu=this.getCi(month, year, ctrl_peroid, fieldsetid, codeitemid);
		    	    	}
	    		    }
		    	    if("2".equalsIgnoreCase(ctrl_peroid)){
		    	    	if("01".equalsIgnoreCase(month)){
		    	    		cishu=this.getCi(month, year, ctrl_peroid, fieldsetid, codeitemid);
		    	    	}
			        	if("04".equalsIgnoreCase(month)){
			        		cishu=this.getCi(month, year, ctrl_peroid, fieldsetid, codeitemid);
		    	    	}
		    	    	if("07".equalsIgnoreCase(month)){
		     	    		cishu=this.getCi(month, year, ctrl_peroid, fieldsetid, codeitemid);
			        	}
		    	    	if("10".equalsIgnoreCase(month)){
			        		cishu=this.getCi(month, year, ctrl_peroid, fieldsetid, codeitemid);
	      		    	}
	    		    }
	     		}
		    
	    		try {
		    		RowSet rs =null;
		    		rs = dao.search(mab.selectSql(fieldsetid,codeitemid,(String)timearr.get(i)));
		    		if(rs.next()){
		    			flag=true;
				
		    		    if(fc_flag!=null&&fc_flag.length()!=0){
			    	    	String sql="select "+fc_flag+" from "+fieldsetid +" where b0110 = '"+codeitemid+"' and "+Sql_switcher.year(fieldsetid+"z0")+"="+year;
			    	    	if("1".equalsIgnoreCase(ctrl_peroid)){
			    	    		sql+=" and "+fc_flag+"=2";
				        		if(!"01".equalsIgnoreCase(month)){
				        			tmon=this.getsub(month, "1");
				        			sql+=" and "+ Sql_switcher.month(fieldsetid+"z0")+" not in ("+tmon+")";
				         		}
			     	    	}
			    	    	if("2".equalsIgnoreCase(ctrl_peroid)){
				        		tmon=this.getsub(month,"2");
				        		sql+=" and "+ Sql_switcher.month(fieldsetid+"z0")+"in ("+tmon+") and "+fc_flag+"=2";
				        	}
				        	if("0".equalsIgnoreCase(ctrl_peroid)){
				        		sql+=" and "+ Sql_switcher.month(fieldsetid+"z0")+"="+month+" and "+fc_flag+"=2";
			    	    	}
			    	    	rs=dao.search(sql);
			    	    	if(rs.next()){
			    	    		/* 薪资总额 ，设置了封存指标的，新增 提示信息优化 xiaoyun 2014-10-29 start */
			    	    		CodeItem codeItem = null;
			    	    		if(AdminCode.getCode("UN", codeitemid) != null) {
			    	    			codeItem = AdminCode.getCode("UN", codeitemid);
			    	    		}else {
			    	    			codeItem = AdminCode.getCode("UM", codeitemid);
			    	    		}
			    	    		String orgName = codeItem.getCodename();
			    	    		String innfo = "";
			    	    		if("1".equals(ctrl_peroid)) { // 按年控制
			    	    			innfo = info(year,ctrl_peroid,orgName);
			    	    		}else if("0".equals(ctrl_peroid)) { // 按月控制
			    	    			innfo = info(month,ctrl_peroid,orgName);
			    	    		}else { // 按季度控制
			    	    			innfo = info(month,ctrl_peroid,orgName);
			    	    		}
			    	    		//String innfo=info(month,ctrl_peroid);
			    	    		if("0".equals(ctrl_peroid)){
				        			info.append(innfo+"\\n");
				        			continue;
				        		}
				        		if("1".equals(ctrl_peroid)){//先默认 遇到的第一个未封存记录是每年的开头的每个月
				        			//info.append(year+innfo+"\\n");
				        			info.append(innfo+"\\n");
				        			break;
				        		}
				        		/* 薪资总额 ，设置了封存指标的，新增 提示信息优化 xiaoyun 2014-10-29 end */
				        		if("2".equalsIgnoreCase(ctrl_peroid)){
				        			if(exitmap.get(month)!=null){
				        				continue;
				        			}else{
				        				info.append(innfo+"\\n");
				        				for(int m=0;m<3;m++){//暂时还没有太好的办法 过滤掉同季度其他月份 不增加新纪录。先默认 遇到的第一个未封存记录是每个季度的开头的每个月
				    	    				if(Integer.parseInt(month)+m<=9){
				        						String tem="0"+String.valueOf(Integer.parseInt(month)+m);
				    						exitmap.put(tem, "1");
				    	    				}else{
				    	    					exitmap.put(String.valueOf(Integer.parseInt(month)+m), "1");
				        					}
				    					
				        				}
				         				continue;
			    	    			}
			    	    		}	
			    	    	}else{
			    		    	mab.setFc_flag(fc_flag);
					     		sql = mab.selectSql1(this.userView,fieldsetid,codeitemid,"",(String)timearr.get(i),(String)spflaglist.get(0).toString().toLowerCase(),cishu);
				    			if(sql.length()<1){
				    				info.append(ResourceFactory.getProperty("gz.acount.select.company.operating"));
					    			break;
				    			}
				    			rs=dao.search(sql);
				    			if(rs.next()){
				    				if(Sql_switcher.searchDbServer() == Constant.ORACEL)
						    		{
						    			selectsql=mab.getOracleSql1(dao,this.userView, fieldsetid, (String)timearr.get(i), codeitemid, i,(String)spflaglist.get(0).toString().toLowerCase(),cishu);
					    			}else
					    			{
				     	    			selectsql = mab.selectSql1(this.userView,fieldsetid,codeitemid,"",(String)timearr.get(i),(String)spflaglist.get(0).toString().toLowerCase(),cishu);
						    		}
					    		}else{
					    			if(Sql_switcher.searchDbServer() == Constant.ORACEL)
					    			{
				    					selectsql=mab.getOracleSql1(dao,this.userView, fieldsetid, (String)timearr.get(i), codeitemid, i,(String)spflaglist.get(0).toString().toLowerCase(),cishu);
					    			}else
					    			{
			         	    			selectsql = mab.selectSql3(this.userView,fieldsetid,codeitemid,(String)timearr.get(i),(String)spflaglist.get(0).toString().toLowerCase(),a_code.substring(0,2),cishu);
							    	}
			    				}
		     		    	}
		       		    }else{
			    			if("0".equals(ctrl_peroid))//按月
			     			{
			    				info.append("["+AdminCode.getCodeName(a_code.substring(0,2),codeitemid)+"]"+ResourceFactory.getProperty("gz.acount.for.yearsmonth.logo"));
				    			info.append("["+timearr.get(i)+"]");
				    			info.append(ResourceFactory.getProperty("gz.acount.have.bean.add")+"\\n");
				    		}
				    		else if("1".equals(ctrl_peroid))//按年
				    		{
				    			if("01".equals(month)|| "1".equals(month))
				    			{
					    			info.append("["+AdminCode.getCodeName(a_code.substring(0,2),codeitemid)+"]"+ResourceFactory.getProperty("gz.acount.for.years.logo"));
			        				info.append("["+year+"]");
			    	    			info.append(ResourceFactory.getProperty("gz.acount.have.bean.add")+"\\n");
				    			}
				    		}
				    		else//按季度
				     		{
				     			if("01".equals(month)|| "1".equals(month)|| "04".equals(month)|| "4".equals(month)|| "07".equals(month)|| "7".equals(month)|| "10".equals(month))
				    			{
					         		info.append("["+AdminCode.getCodeName(a_code.substring(0,2),codeitemid)+"]"+ResourceFactory.getProperty("gz.acount.for.season.logo"));
					         		info.append("["+mab.getSeasonZH(Integer.parseInt(month))+"]");
					          		info.append(ResourceFactory.getProperty("gz.acount.have.bean.add")+"\\n");
				     			}
				    		}
				     		continue;
			    	    }
			    	}else{
		    			if(fc_flag!=null&&fc_flag.length()!=0){
				     		mab.setFc_flag(fc_flag);
				    		String sql = mab.selectSql1(this.userView,fieldsetid,codeitemid,"",(String)timearr.get(i),(String)spflaglist.get(0).toString().toLowerCase(),cishu);
				    		if(sql.length()<1){
				     			info.append(ResourceFactory.getProperty("gz.acount.select.company.operating"));
				    			break;
				    		}
				    		rs=dao.search(sql);
				     		if(rs.next()){
				    			if(Sql_switcher.searchDbServer() == Constant.ORACEL)
				    			{
				    				selectsql=mab.getOracleSql1(dao,this.userView, fieldsetid, (String)timearr.get(i), codeitemid, i,(String)spflaglist.get(0).toString().toLowerCase(),cishu);
				    			}else
				     			{
			     	    			selectsql = mab.selectSql1(this.userView,fieldsetid,codeitemid,"",(String)timearr.get(i),(String)spflaglist.get(0).toString().toLowerCase(),cishu);
					     		}
					     	}else{
					    		if(Sql_switcher.searchDbServer() == Constant.ORACEL)
					    		{
					     			selectsql=mab.getOracleSql1(dao,this.userView, fieldsetid, (String)timearr.get(i), codeitemid, i,(String)spflaglist.get(0).toString().toLowerCase(),cishu);
					    		}else
					    		{
								
		         	    			selectsql = mab.selectSql3(this.userView,fieldsetid,codeitemid,(String)timearr.get(i),(String)spflaglist.get(0).toString().toLowerCase(),a_code.substring(0,2),cishu);
				    			}
				    		}
				    	}else{
			     			String sql = mab.selectSql(this.userView,fieldsetid,codeitemid,"",(String)timearr.get(i),(String)spflaglist.get(0).toString().toLowerCase());
				     		if(sql.length()<1){
					    		info.append(ResourceFactory.getProperty("gz.acount.select.company.operating"));
					    		break;
					    	}
				    		RowSet rs1 = dao.search(sql);
						
				    		if(rs1.next()){
				    			if(Sql_switcher.searchDbServer() == Constant.ORACEL)
				    			{
					     			selectsql=mab.getOracleSql(dao,this.userView, fieldsetid, (String)timearr.get(i), codeitemid, i,(String)spflaglist.get(0).toString().toLowerCase());
					     		}else
					    		{
			     	    			selectsql = mab.selectSql(this.userView,fieldsetid,codeitemid,"",(String)timearr.get(i),(String)spflaglist.get(0).toString().toLowerCase());
					    		}
					    	}else{
					    			if(Sql_switcher.searchDbServer() == Constant.ORACEL)
					    			{
					     				selectsql=mab.getOracleSql(dao,this.userView, fieldsetid, (String)timearr.get(i), codeitemid, i,(String)spflaglist.get(0).toString().toLowerCase());
						    		}else
						    		{
			         	     			selectsql = mab.selectSql2(this.userView,fieldsetid,codeitemid,(String)timearr.get(i),(String)spflaglist.get(0).toString().toLowerCase(),a_code.substring(0,2));
						
				     				}
				    		}
			    		}
		     		}
				
		    	} catch (SQLException e) {
		    		e.printStackTrace();
		    	}
		    	if(i==0)
		    	{
		    		if(!isMainSet(codeitemid))
		    		{
			    		RecordVo b01vo = new RecordVo("b01");
			    		b01vo.setString("b0110", codeitemid);
			    		b01vo.setString("createusername", this.userView.getUserName());
				    	b01vo.setString("modusername", this.userView.getUserName());
				    	b01vo.setDate("createtime", new Date());
			    		b01vo.setDate("modtime", new Date());
			    		dao.addValueObject(b01vo);	
	    			}
	    		}
	     		String sqlstr = mab.insertStr(selectsql,fieldsetid,(String)spflaglist.get(0).toString().toLowerCase()); 
	    		try {
				//dao.update(sqlstr);
				
		    		if(Sql_switcher.searchDbServer() == Constant.ORACEL)
		    		{
		    			sql_list.add(sqlstr);
		    		}
		    		else
		    		{
	         			dao.update(sqlstr, new ArrayList());
		    		}
				//dao.update(mab.getcontentsql(fieldsetid,srclist,(String)timearr.get(i)));
		    	} catch (SQLException e) {
		    		e.printStackTrace();
		    	}
	    	}// timearr loop end 
		}
		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		{

			try
			{
    			dao.batchUpdate(sql_list);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		if(info.length()<1){
			info.append("ok");
		}
		ArrayList list = new ArrayList();
		HashMap _map = new HashMap();
		_map.put("info", info.toString());
		list.add(_map);
		hm.put("list", list);
		hm.put("info",info.toString());
	}
	public ArrayList timeArr(String time){
		String[] arr = time.split("-");
		int year = Integer.parseInt(arr[0]);
		int startmonth = Integer.parseInt(arr[1]);
		int endmonth = Integer.parseInt(arr[2]);
		ArrayList list = new ArrayList();
		int n=0;
		for(int i=startmonth;i<=endmonth;i++){
			if(i>9){
				list.add(year+"-"+i);
			}else{
				list.add(year+"-0"+i);
			}
			n++;
		}
		
		return list;
	}
	public ArrayList timeArr(String time,String period){
		String[] arr = time.split("-");
		int year = Integer.parseInt(arr[0]);
		int startmonth = Integer.parseInt(arr[1]);
		int endmonth = Integer.parseInt(arr[2]);
		ArrayList list = new ArrayList();
		int n=0;
		int endm=0;
		if("2".equalsIgnoreCase(period)){
			startmonth=(startmonth-1)*3+1;
			endmonth=endmonth*3;
		}
		for(int i=startmonth;i<=endmonth;i++){
			if(i>9){
				list.add(year+"-"+i);
			}else{
				list.add(year+"-0"+i);
			}
			n++;
		}
		
		return list;
	}
	public boolean isMainSet(String codeitemid)
	{
		boolean flag = false;
		try
		{
			String sql = "select * from b01 where b0110='"+codeitemid+"'";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql);
			while(this.frowset.next())
			{
				flag=true;
				break;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	public String info(String infoDesc ,String period, String orgName){ // 薪资总额 ，设置了封存指标的，新增失败提示信息优化 xiaoyun 2014-10-29 增加机构名参数	
		String info="";
		/* 薪资总额 ，设置了封存指标的，新增 提示信息优化 xiaoyun 2014-10-29 start */
		if(orgName != null) {
			info = orgName+" ";
		}
		/* 薪资总额 ，设置了封存指标的，新增 提示信息优化 xiaoyun 2014-10-29 end */
		if("0".equalsIgnoreCase(period)){
			info+=infoDesc+"月份存在未封存的数据,不能新增薪资记录!";
		}
		if("2".equalsIgnoreCase(period)){
			if("01,02,03,".indexOf(infoDesc+",")!=-1){
				info+="一季度存在未封存的数据,不能新增薪资记录!";
			}
			if("04,05,06,".indexOf(infoDesc+",")!=-1){
				info+="二季度存在未封存的数据,不能新增薪资记录!";
			}
			if("07,08,09,".indexOf(infoDesc+",")!=-1){
				info+="三季度存在未封存的数据,不能新增薪资记录!";
			}
			if("10,11,12,".indexOf(infoDesc+",")!=-1){
				info+="四季度存在未封存的数据,不能新增薪资记录!";
			}
		}
		if("1".equalsIgnoreCase(period)){
			info += infoDesc + "年存在未封存的数据,不能新增薪资记录!";
		}
		return info;
		
	}

	public String  getsub(String  month,String period){
		String mon="";
		int t=0;
		if(month.indexOf("0")!=-1&&!"10".equalsIgnoreCase(month)){
			t=Integer.parseInt(month.substring(1));
		}else{
			t=Integer.parseInt(month);
		}
		if("2".equalsIgnoreCase(period)){
			switch (t){
				case 1:mon="01,02,03";break;
				case 2:mon="02,03";break;
				case 3: mon="03";break;
				case 4:mon="04,05,06";break;
				case 5:mon="05,06";break;
				case 6: mon="06";break;
				case 7: mon="07,08,09";break;
				case 8: mon="08,09";break;
				case 9: mon="09";break;
				case 10:mon="10,11,12";break;
				case 11:mon="11,12";break;
				case 12:mon="12";break;
				default :mon="" ;break;
			}
		}
		if("1".equalsIgnoreCase(period)){
			switch (t){
				case 1:mon="";break;
				case 2:mon="01";break;
				case 3: mon="01,02";break;
				case 4:mon="01,02,03";break;
				case 5:mon="01,02,03,04";break;
				case 6: mon="01,02,03,04,05";break;
				case 7: mon="01,02,03,04,05,06";break;
				case 8: mon="01,02,03,04,05,06,07";break;
				case 9: mon="01,02,03,04,05,06,07,08";break;
				case 10:mon="01,02,03,04,05,06,07,08,09";break;
				case 11:mon="01,02,03,04,05,06,07,08,09,10";break;
				case 12:mon="01,02,03,04,05,06,07,08,09,10,11";break;
				default :mon="" ;break;
			}
		}
		return mon;
	}
	public String getCi(String month,String year,String ctrl_peroid,String fieldsetid,String codeitemid){
		String cishu="";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			if("0".equalsIgnoreCase(ctrl_peroid)){
				 String sql="select max("+fieldsetid+"z1) from "+fieldsetid +" where b0110 = '"+codeitemid+"' and "+Sql_switcher.year(fieldsetid+"z0")+"="+year+" and "+Sql_switcher.month(fieldsetid+"z0")+"="+month;
				
						this.frowset=dao.search(sql);
					
					if(this.frowset.next()){
						cishu=String.valueOf(this.frowset.getInt(1)+1);
					}else{
						cishu="1";
					}
			}
			if("1".equalsIgnoreCase(ctrl_peroid)){
				String sql="select max("+fieldsetid+"z1) from "+fieldsetid +" where b0110 = '"+codeitemid+"' and "+Sql_switcher.year(fieldsetid+"z0")+"="+year;
				this.frowset=dao.search(sql);
				if(this.frowset.next()){
					cishu=String.valueOf(this.frowset.getInt(1)+1);
				}else{
					cishu="1";
				}
			}
			if("2".equalsIgnoreCase(ctrl_peroid)){
				String sql="select max("+fieldsetid+"z1) from "+fieldsetid +" where b0110 = '"+codeitemid+"' and "+Sql_switcher.year(fieldsetid+"z0")+"="+year;
				if("01".equalsIgnoreCase(month)){
					sql+=" and "+Sql_switcher.month(fieldsetid+"z0")+"in(01,02,03)";
				}
				if("04".equalsIgnoreCase(month)){
					sql+=" and "+Sql_switcher.month(fieldsetid+"z0")+"in(04,05,06)";
				}
				if("07".equalsIgnoreCase(month)){
					sql+=" and "+Sql_switcher.month(fieldsetid+"z0")+"in(07,08,09)";
				}
				if("10".equalsIgnoreCase(month)){
					sql+=" and "+Sql_switcher.month(fieldsetid+"z0")+"in(10,11,12)";
				}
				this.frowset=dao.search(sql);
				if(this.frowset.next()){
					cishu=String.valueOf(this.frowset.getInt(1)+1);
				}else{
					cishu="1";
				}
			}
		} catch (SQLException e) {		
			e.printStackTrace();
		}
		return cishu;
	}
	
}
