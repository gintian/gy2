package com.hjsj.hrms.transaction.stat;

import com.hjsj.hrms.businessobject.stat.GeneralQueryStat;
import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
import com.hjsj.hrms.businessobject.sys.AnychartBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
/**
 * 通过数据采集显示统计分析
 * <p>Title:ShowStatMsgChartTrans.java</p>
 * <p>Description>:ShowStatMsgChartTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jun 4, 2010 3:27:53 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class ShowStatMsgChartTrans extends IBusiness{
    
	public void execute() throws GeneralException {
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String statId=(String)this.getFormHM().get("statid");	
		String chart_type=(String)this.getFormHM().get("chart_type");
		String sformula = (String)this.getFormHM().get("sformula");
	    sformula = sformula == null?"":sformula;
	    boolean definedSformula = hm.containsKey("sformula");
	    hm.remove("sformula");
	    if(sformula.length()==0 && !definedSformula)  // 安徽高速，设置了统计方式的，默认定位第一个
	        sformula = getFirstSformula(statId);
		String SNameDisplay;
		String infokind="";//信息群
		String userbase="";//人员库
		ArrayList dblist=new ArrayList();
		String history="";
		boolean isresult=true;	
		if(statId==null||statId.length()<=0)
			return;
		String sql1="select * from sname where id='"+statId+"'";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String condid="";
		try {
			this.frowset=dao.search(sql1);
			if(this.frowset.next())
			{
				infokind=this.frowset.getString("infokind");
				userbase=this.frowset.getString("nbase");
				userbase = userbase == null?"":userbase;
				if(userbase!=null&&userbase.length()>0)
				{
					String [] baseS=userbase.split(",");
					for(int i=0;i<baseS.length;i++)
					{
						if(baseS[i]!=null&&baseS[i].length()>0){
							if(this.userView.hasTheDbName(baseS[i]))
								dblist.add(baseS[i]);
						}
					}
				}
				if(dblist==null||dblist.size()<=0)
					dblist.add(this.userView.getPrivDbList().get(0));//dblist=this.userView.getPrivDbList(); 统计条件没有设置人员库默认只显示在职人员库  wangb 20181229
				String flag = this.frowset.getString("flag");
				if(flag!=null&& "1".equals(flag))
					isresult=false; //false时才查询，查询结果表
				condid = this.frowset.getString("condid");
			}
			this.formHM.put("infokind", infokind);
			this.getFormHM().put("dblist", dblist);
			if(dblist!=null&&dblist.size()>0)
			{
				this.formHM.put("userbases", userbase.replaceAll(",", "`"));
				this.formHM.put("userbase", dblist.get(0));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList condlist=getCondlist(condid,dao);		
		this.formHM.put("condlist", condlist);
		//liuy 2015-1-6 6516：领导桌面：人员结构的分类中，选择什么都显示全部人员，下面的统计图能够按照选择的正确显示  start
		String lexprId=(String)this.getFormHM().get("lexprId");//常用查询条件
		if(StringUtils.isEmpty(lexprId))
			lexprId = (String)hm.get("lexprId");
		if(StringUtils.isNotEmpty(lexprId))
			this.formHM.put("lexprId", lexprId);
		//liuy end
		if(StringUtils.isEmpty(lexprId))
		{
			if(condlist!=null&&condlist.size()>0)
			{
				CommonData da=(CommonData)condlist.get(0);
				lexprId=da.getDataValue();
			}
		}
		String []curr_id=null;
		String commlexr=null;
	    String commfacor=null;
		if(lexprId!=null&&lexprId.length()>0)
		{
			//加上常用查询进行的统计
			curr_id=new String[1];
			curr_id[0]=lexprId;			
		}		
	    if(curr_id!=null&&curr_id.length>0)
	    {
	    	GeneralQueryStat generalstat=new GeneralQueryStat();
			generalstat.getGeneralQueryLexrfacor(curr_id,userbase,"",this.getFrameconn());	    
	    	commlexr=generalstat.getLexpr();
	    	commfacor=generalstat.getLfactor();
	    	history = generalstat.getHistory();
	    }
	    
	    int[] statvalues=null;
	    double[] statvaluess=null;
		String[] fieldDisplay;    
		String preresult="";
		ArrayList list=new ArrayList();	   
	    StatDataEncapsulation simplestat=new StatDataEncapsulation();
	    String querycond=(String)this.getFormHM().get("querycond");//组织机构
	    String orgName="";
	    try {
	    if(querycond==null||querycond.length()==0){
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String backdate = sdf.format(new Date());
	    	String sql="select codesetid,codeitemid,codeitemdesc from organization where codeitemid=parentid  and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date";
	    	if(!this.userView.isSuper_admin()){
	    		String manage=this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
	    		if(manage.length()>2){
	    			sql="select codesetid,codeitemid,codeitemdesc from organization where codeitemid='"+this.userView.getManagePrivCodeValue()+"'  and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date";
	    		}else if(manage.length()==2){
	    			sql="select codesetid,codeitemid,codeitemdesc from organization where codeitemid=parentid  and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date";
	    		}else{
	    			sql="select codesetid,codeitemid,codeitemdesc from organization where 1=2  and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date";
	    		}
	    	}
	    	this.frowset = dao.search(sql);
	    	int index=0;
	    	while(this.frowset.next()){
	    		if(index==1){
	    			querycond="";
	    			orgName="";
	    			break;
	    		}
	    		querycond=this.frowset.getString("codesetid")+this.frowset.getString("codeitemid");
	    		orgName=this.getFrowset().getString("codeitemdesc");
	    		index++;
	    	}
	    	this.getFormHM().put("querycond", querycond);
	    	this.getFormHM().put("orgName", orgName);
	    }else if("root".equalsIgnoreCase(querycond)){
	    	querycond="";
	    }
	    if(sformula.length()>0){
		    SformulaXml xml = new SformulaXml(this.frameconn,statId);
			Element element = xml.getElement(sformula);
			if(element==null){
				sformula="";
				this.getFormHM().put("decimalwidth", "0");
				this.getFormHM().put("isneedsum", "true");
			}else{
				String decimalwidth = element.getAttributeValue("decimalwidth");
				decimalwidth=(decimalwidth==null||decimalwidth.length()==0)?"2":decimalwidth;
				this.getFormHM().put("decimalwidth", decimalwidth);
				String type = element.getAttributeValue("type");
				if("sum".equalsIgnoreCase(type)||"count".equalsIgnoreCase(type))
					this.getFormHM().put("isneedsum", "true");
				else
					this.getFormHM().put("isneedsum", "false");
			}
	    }else{
	    	this.getFormHM().put("decimalwidth", "0");
	    	this.getFormHM().put("isneedsum", "true");
	    }
	    
	    	if(sformula.length()>0)
				statvaluess =simplestat.getLexprDataSformula(dblist, Integer.parseInt(statId), querycond, userView.getUserName(),userView.getManagePrivCode(),userView,infokind,isresult,commlexr,commfacor,preresult,history,sformula,this.frameconn,"","","");
			else
				statvalues =simplestat.getLexprData(dblist, Integer.parseInt(statId), querycond, userView.getUserName(),userView.getManagePrivCode(),userView,infokind,isresult,commlexr,commfacor,preresult,history,"","","");
	    	SNameDisplay = simplestat.getSNameDisplay();
		    if ((sformula.length()==0&&statvalues != null && statvalues.length > 0)||(sformula.length()>0&&statvaluess != null && statvaluess.length > 0)) 
		    {
			   fieldDisplay = simplestat.getDisplay();
			   int statTotal = 0;
			   double statTotals = 0;
			   if(sformula.length()==0)
				   for (int i = 0; i < statvalues.length; i++) 
				   {
					   CommonData vo=new CommonData();
					   String str=fieldDisplay[i];
					   vo.setDataName(str);
					   vo.setDataValue(String.valueOf(statvalues[i]));
					  list.add(vo);
				      statTotal += statvalues[i];
				   }
			   else
				   for (int i = 0; i < statvaluess.length; i++) 
				   {
					   CommonData vo=new CommonData();
					   String str=fieldDisplay[i];
					   vo.setDataName(str);
					   vo.setDataValue(String.valueOf(statvaluess[i]));
					  list.add(vo);
				      statTotal += statvaluess[i];
				   }
		       this.getFormHM().put("snamedisplay",SNameDisplay);
		       this.getFormHM().put("list",list);
		     }else
		     {
		    	    StringBuffer sql =new StringBuffer();
					sql.append("select * from SName where id=");
					sql.append(statId);
					List rs =ExecuteSQL.executeMyQuery(sql.toString());
					if (!rs.isEmpty()) {
						LazyDynaBean rec=(LazyDynaBean)rs.get(0);
						SNameDisplay = rec.get("name")!=null?rec.get("name").toString():"";
					}
					CommonData vo=new CommonData();
					vo.setDataName("");
					vo.setDataValue("0");
					list.add(vo);
					this.getFormHM().put("snamedisplay",SNameDisplay);
		    	    this.getFormHM().put("list",list);
		     }
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	throw new GeneralException("", e.toString(),"", "");
		}
	    /* 领导桌面组织单元是否可选的开关 xiaoyun 2014-5-15 start */
	    String isHideBiPanelOrg = SystemConfig.getPropertyValue("isHideBiPanelOrg");
	    if(StringUtils.isNotEmpty(isHideBiPanelOrg)) {
	    	this.getFormHM().put("isHideBiPanelOrg", isHideBiPanelOrg.trim());
	    }
	    /* 领导桌面组织单元是否可选的开关 xiaoyun 2014-5-15 end */
	    HashMap jfreemap=new HashMap();
		jfreemap.put(SNameDisplay, list);
		this.getFormHM().put("jfreemap" ,jfreemap);
	    this.getFormHM().put("queryconde",querycond);
	    chart_type=chart_type!=null&&chart_type.length()>0?chart_type:"12";
	    this.getFormHM().put("chart_type", chart_type);
	    this.getSformula(statId);
        this.getFormHM().put("sformula", sformula);
	    String xangle = AnychartBo.computeXangle(list);
	    this.getFormHM().put("xangle", xangle);
	    /* 标识：2749 总裁桌面：立体直方图显示连续工龄统计项时，文件显示不全 xiaoyun 2014-7-9 start */
	    this.getFormHM().put("total", getSumLength(list)+"");
	    /* 标识：2749 总裁桌面：立体直方图显示连续工龄统计项时，文件显示不全 xiaoyun 2014-7-9 end */
	}
	
	/**
	 * 获取该集合中字符串长度之和
	 * @param list
	 * @return
	 * @author xiaoyun 2014-7-7
	 */
	private int getSumLength(ArrayList list) {
		int sum = 0;
		CommonData cd = null;
		for (int i = 0; i < list.size(); i++) {
			cd = (CommonData) list.get(i);
			if(StringUtils.isNotEmpty(cd.getDataName())) {
				sum += cd.getDataName().length();
			}
		}
		return sum;
	}
	
	private ArrayList getCondlist(String condid,ContentDAO dao)
	{
		ArrayList list=new ArrayList();
		CommonData da=new CommonData();
		
		if (condid != null && condid.length() > 0){
			String []condids = condid.split(",");
			RowSet rs=null;
			try
			{
				for (int i = 0; i < condids.length; i++) {
					String sql = "select id,name from lexpr where id='" + condids[i] + "'";
					rs = dao.search(sql);
					if (rs.next()) {
						if(this.userView.isHaveResource(2,rs.getInt("id")+"")){
							da=new CommonData();
							da.setDataValue(rs.getInt("id")+"");
							da.setDataName(rs.getString("name"));
							list.add(da);
						}
					}
				}				
			}catch(Exception e)
			{
				e.printStackTrace();
			}finally
			{
				if(rs!=null)
					try {
						rs.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}
		return list;
	}

	/**
	 * 取第一个统计方式
	 * @param statid
	 * @return
	 */
    private String getFirstSformula(String statid){
        SformulaXml xml = new SformulaXml(this.frameconn,statid);
        List list = xml.getAllChildren();
        String sformula = "";
        if(list!=null&&list.size()>0){
            Element element = (Element)list.get(0);
            sformula = element.getAttributeValue("id");
        }
        return sformula;
    }
	
	private void getSformula(String statid){
		SformulaXml xml = new SformulaXml(this.frameconn,statid);
		List list = xml.getAllChildren();
		ArrayList sformulalist = new ArrayList();
		CommonData cdCount = null;  // 安徽高速，个数放最后
		if(list!=null&&list.size()>0){
			for(int i=0;i<list.size();i++){
				Element element = (Element)list.get(i);
				if(element.getAttributeValue("title").equals(ResourceFactory.getProperty("kq.formula.count"))){
					cdCount = new CommonData(element.getAttributeValue("id"),element.getAttributeValue("title"));
				}else{
					CommonData cd= new CommonData(element.getAttributeValue("id"),element.getAttributeValue("title"));
					sformulalist.add(cd);
				}
			}
            if(cdCount!=null)
            	sformulalist.add(cdCount);
			this.getFormHM().put("sformulalist", sformulalist);
			if(list.size()==1)
				this.getFormHM().put("showsformula", "0");
			else
				this.getFormHM().put("showsformula", "1");
		}else{
			this.getFormHM().put("showsformula", "0");
		}
	}
	
}
