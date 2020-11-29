package com.hjsj.hrms.transaction.general.sys.options;

import com.hjsj.hrms.businessobject.sys.options.PortalTailorXml;
import com.hjsj.hrms.valueobject.common.LabelValueView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class SavePortalTailorTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
		String portalid = (String)this.getFormHM().get("portalid");
		if(portalid!=null&&!"".equals(portalid)){//新版本（自5）从每户定制来读
			String checkvalues=(String)this.getFormHM().get("checkvalues");
			checkvalues=checkvalues==null?"":checkvalues;
			String[] temps = checkvalues.split("`");
			ArrayList showitem = (ArrayList)this.getFormHM().get("showitem");
			for(int i=0;i<showitem.size();i++){
				if(temps.length<=i){
					break;
				}
				LazyDynaBean bean=(LazyDynaBean)showitem.get(i);
				String temp=temps[i];
				String[] vs=temp.split(",");
				if(vs.length!=3)
					continue;
				else{
					bean.set("twinkle", vs[0]);
					bean.set("scroll", vs[1]);
					bean.set("show", vs[2]);
				}
			}
			new PortalTailorXml().NWriteOutParameterXml(this.getFrameconn(),showitem,"新版门户定制",this.userView.getUserName(),String.valueOf(this.userView.getStatus()),portalid);
		}else{
			ArrayList readlist=new PortalTailorXml().ReadOutParameterXml("SYS_PARAM",this.getFrameconn(),this.userView.getUserName());
		
			ArrayList nodeslist=new ArrayList();
			for(int i=0;readlist!=null && i<readlist.size();i++)
			{
				ArrayList readattributelist=(ArrayList)readlist.get(i);
				for(int j=0;j<readattributelist.size();j++)
				{
					LabelValueView item=(LabelValueView)readattributelist.get(j);
	
					if("id".equals(item.getLabel()))
					{
						if("1".equals(item.getValue()))
							if(this.getFormHM().get("bulletinshow")!=null && !"0".equals(this.getFormHM().get("bulletinshow")))
							{
								ArrayList attributelist=new ArrayList();
								LabelValueView id=new LabelValueView("1","id");
								LabelValueView scroll=new LabelValueView(this.getFormHM().get("bulletinscroll")!=null?this.getFormHM().get("bulletinscroll").toString():"0","scroll");
								LabelValueView twinkle=new LabelValueView(this.getFormHM().get("bulletintwinkle")!=null?this.getFormHM().get("bulletintwinkle").toString():"0","twinkle");
								LabelValueView show=new LabelValueView("1","show");
								attributelist.add(id);
								attributelist.add(scroll);
								attributelist.add(twinkle);
								attributelist.add(show);
								nodeslist.add(attributelist);
							}
							else
							{
								ArrayList attributelist=new ArrayList();
								LabelValueView id=new LabelValueView("1","id");
								LabelValueView scroll=new LabelValueView(this.getFormHM().get("bulletinscroll")!=null?this.getFormHM().get("bulletinscroll").toString():"0","scroll");
								LabelValueView twinkle=new LabelValueView(this.getFormHM().get("bulletintwinkle")!=null?this.getFormHM().get("bulletintwinkle").toString():"0","twinkle");
								LabelValueView show=new LabelValueView("0","show");
								attributelist.add(id);
								attributelist.add(scroll);
								attributelist.add(twinkle);
								attributelist.add(show);
								nodeslist.add(attributelist);
							}
						if("2".equals(item.getValue()))
							if(this.getFormHM().get("warnshow")!=null && !"0".equals(this.getFormHM().get("warnshow")))
							{
								ArrayList attributelist=new ArrayList();
								LabelValueView id=new LabelValueView("2","id");
								LabelValueView scroll=new LabelValueView(this.getFormHM().get("warnscroll")!=null?this.getFormHM().get("warnscroll").toString():"0","scroll");
								LabelValueView twinkle=new LabelValueView(this.getFormHM().get("warntwinkle")!=null?this.getFormHM().get("warntwinkle").toString():"0","twinkle");
								LabelValueView show=new LabelValueView("1","show");
								attributelist.add(id);
								attributelist.add(scroll);
								attributelist.add(twinkle);
								attributelist.add(show);
								nodeslist.add(attributelist);
							}
							else
							{
								ArrayList attributelist=new ArrayList();
								LabelValueView id=new LabelValueView("2","id");
								LabelValueView scroll=new LabelValueView(this.getFormHM().get("warnscroll")!=null?this.getFormHM().get("warnscroll").toString():"0","scroll");
								LabelValueView twinkle=new LabelValueView(this.getFormHM().get("warntwinkle")!=null?this.getFormHM().get("warntwinkle").toString():"0","twinkle");
								LabelValueView show=new LabelValueView("0","show");
								attributelist.add(id);
								attributelist.add(scroll);
								attributelist.add(twinkle);
								attributelist.add(show);
								nodeslist.add(attributelist);
							}
						
						if("3".equals(item.getValue()))
							if(this.getFormHM().get("mustershow")!=null && !"0".equals(this.getFormHM().get("mustershow")))
							{
								ArrayList attributelist=new ArrayList();
								LabelValueView id=new LabelValueView("3","id");
								LabelValueView scroll=new LabelValueView(this.getFormHM().get("musterscroll")!=null?this.getFormHM().get("musterscroll").toString():"0","scroll");
								LabelValueView twinkle=new LabelValueView(this.getFormHM().get("mustertwinkle")!=null?this.getFormHM().get("mustertwinkle").toString():"0","twinkle");
								LabelValueView show=new LabelValueView("1","show");
								attributelist.add(id);
								attributelist.add(scroll);
								attributelist.add(twinkle);
								attributelist.add(show);
								nodeslist.add(attributelist);
							}
							else
							{
								ArrayList attributelist=new ArrayList();
								LabelValueView id=new LabelValueView("3","id");
								LabelValueView scroll=new LabelValueView(this.getFormHM().get("musterscroll")!=null?this.getFormHM().get("musterscroll").toString():"0","scroll");
								LabelValueView twinkle=new LabelValueView(this.getFormHM().get("mustertwinkle")!=null?this.getFormHM().get("mustertwinkle").toString():"0","twinkle");
								LabelValueView show=new LabelValueView("0","show");
								attributelist.add(id);
								attributelist.add(scroll);
								attributelist.add(twinkle);
								attributelist.add(show);
								nodeslist.add(attributelist);
							}
						if("4".equals(item.getValue()))
							if(this.getFormHM().get("queryshow")!=null && !"0".equals(this.getFormHM().get("queryshow")))
							{
								ArrayList attributelist=new ArrayList();
								LabelValueView id=new LabelValueView("4","id");
								LabelValueView scroll=new LabelValueView(this.getFormHM().get("queryscroll")!=null?this.getFormHM().get("queryscroll").toString():"0","scroll");
								LabelValueView twinkle=new LabelValueView(this.getFormHM().get("querytwinkle")!=null?this.getFormHM().get("querytwinkle").toString():"0","twinkle");
								LabelValueView show=new LabelValueView("1","show");
								attributelist.add(id);
								attributelist.add(scroll);
								attributelist.add(twinkle);
								attributelist.add(show);
								nodeslist.add(attributelist);
							}else
							{
								ArrayList attributelist=new ArrayList();
								LabelValueView id=new LabelValueView("4","id");
								LabelValueView scroll=new LabelValueView(this.getFormHM().get("queryscroll")!=null?this.getFormHM().get("queryscroll").toString():"0","scroll");
								LabelValueView twinkle=new LabelValueView(this.getFormHM().get("querytwinkle")!=null?this.getFormHM().get("querytwinkle").toString():"0","twinkle");
								LabelValueView show=new LabelValueView("0","show");
								attributelist.add(id);
								attributelist.add(scroll);
								attributelist.add(twinkle);
								attributelist.add(show);
								nodeslist.add(attributelist);
							}
						if("5".equals(item.getValue()))
							if(this.getFormHM().get("statshow")!=null && !"0".equals(this.getFormHM().get("statshow")))
							{
								ArrayList attributelist=new ArrayList();
								LabelValueView id=new LabelValueView("5","id");
								LabelValueView scroll=new LabelValueView(this.getFormHM().get("statscroll")!=null?this.getFormHM().get("statscroll").toString():"0","scroll");
								LabelValueView twinkle=new LabelValueView(this.getFormHM().get("stattwinkle")!=null?this.getFormHM().get("stattwinkle").toString():"0","twinkle");
								LabelValueView show=new LabelValueView("1","show");
								attributelist.add(id);
								attributelist.add(scroll);
								attributelist.add(twinkle);
								attributelist.add(show);
								nodeslist.add(attributelist);
							}
							else
							{
								ArrayList attributelist=new ArrayList();
								LabelValueView id=new LabelValueView("5","id");
								LabelValueView scroll=new LabelValueView(this.getFormHM().get("statscroll")!=null?this.getFormHM().get("statscroll").toString():"0","scroll");
								LabelValueView twinkle=new LabelValueView(this.getFormHM().get("stattwinkle")!=null?this.getFormHM().get("stattwinkle").toString():"0","twinkle");
								LabelValueView show=new LabelValueView("0","show");
								attributelist.add(id);
								attributelist.add(scroll);
								attributelist.add(twinkle);
								attributelist.add(show);
								nodeslist.add(attributelist);
							}
						if("6".equals(item.getValue()))
							if(this.getFormHM().get("cardshow")!=null && !"0".equals(this.getFormHM().get("cardshow")))
							{
								ArrayList attributelist=new ArrayList();
								LabelValueView id=new LabelValueView("6","id");
								LabelValueView scroll=new LabelValueView(this.getFormHM().get("cardscroll")!=null?this.getFormHM().get("cardscroll").toString():"0","scroll");
								LabelValueView twinkle=new LabelValueView(this.getFormHM().get("cardtwinkle")!=null?this.getFormHM().get("cardtwinkle").toString():"0","twinkle");
								LabelValueView show=new LabelValueView("1","show");
								attributelist.add(id);
								attributelist.add(scroll);
								attributelist.add(twinkle);
								attributelist.add(show);
								nodeslist.add(attributelist);
							}else
							{
								ArrayList attributelist=new ArrayList();
								LabelValueView id=new LabelValueView("6","id");
								LabelValueView scroll=new LabelValueView(this.getFormHM().get("cardscroll")!=null?this.getFormHM().get("cardscroll").toString():"0","scroll");
								LabelValueView twinkle=new LabelValueView(this.getFormHM().get("cardtwinkle")!=null?this.getFormHM().get("cardtwinkle").toString():"0","twinkle");
								LabelValueView show=new LabelValueView("0","show");
								attributelist.add(id);
								attributelist.add(scroll);
								attributelist.add(twinkle);
								attributelist.add(show);
								nodeslist.add(attributelist);
							}
						if("7".equals(item.getValue()))
							if(this.getFormHM().get("reportshow")!=null && !"0".equals(this.getFormHM().get("reportshow")))
							{
								ArrayList attributelist=new ArrayList();
								LabelValueView id=new LabelValueView("7","id");
								LabelValueView scroll=new LabelValueView(this.getFormHM().get("reportscroll")!=null?this.getFormHM().get("reportscroll").toString():"0","scroll");
								LabelValueView twinkle=new LabelValueView(this.getFormHM().get("reporttwinkle")!=null?this.getFormHM().get("reporttwinkle").toString():"0","twinkle");
								LabelValueView show=new LabelValueView("1","show");
								attributelist.add(id);
								attributelist.add(scroll);
								attributelist.add(twinkle);
								attributelist.add(show);
								nodeslist.add(attributelist);
							}
							else
							{
								ArrayList attributelist=new ArrayList();
								LabelValueView id=new LabelValueView("7","id");
								LabelValueView scroll=new LabelValueView(this.getFormHM().get("reportscroll")!=null?this.getFormHM().get("reportscroll").toString():"0","scroll");
								LabelValueView twinkle=new LabelValueView(this.getFormHM().get("reporttwinkle")!=null?this.getFormHM().get("reporttwinkle").toString():"0","twinkle");
								LabelValueView show=new LabelValueView("0","show");
								attributelist.add(id);
								attributelist.add(scroll);
								attributelist.add(twinkle);
								attributelist.add(show);
								nodeslist.add(attributelist);
							}
						 if("8".equals(item.getValue()))
							if(this.getFormHM().get("mattershow")!=null && !"0".equals(this.getFormHM().get("mattershow")))
							{
								ArrayList attributelist=new ArrayList();
								LabelValueView id=new LabelValueView("8","id");
								LabelValueView scroll=new LabelValueView(this.getFormHM().get("matterscroll")!=null?this.getFormHM().get("matterscroll").toString():"0","scroll");
								LabelValueView twinkle=new LabelValueView(this.getFormHM().get("mattertwinkle")!=null?this.getFormHM().get("mattertwinkle").toString():"0","twinkle");
								LabelValueView show=new LabelValueView("1","show");
								attributelist.add(id);
								attributelist.add(scroll);
								attributelist.add(twinkle);
								attributelist.add(show);
								nodeslist.add(attributelist);
							}
							else
							{
								ArrayList attributelist=new ArrayList();
								LabelValueView id=new LabelValueView("8","id");
								LabelValueView scroll=new LabelValueView(this.getFormHM().get("matterscroll")!=null?this.getFormHM().get("matterscroll").toString():"0","scroll");
								LabelValueView twinkle=new LabelValueView(this.getFormHM().get("mattertwinkle")!=null?this.getFormHM().get("mattertwinkle").toString():"0","twinkle");
								LabelValueView show=new LabelValueView("0","show");
								attributelist.add(id);
								attributelist.add(scroll);
								attributelist.add(twinkle);
								attributelist.add(show);
								nodeslist.add(attributelist);
							}
						  if("9".equals(item.getValue()))
								if(this.getFormHM().get("salaryshow")!=null && !"0".equals(this.getFormHM().get("salaryshow")))
								{
									ArrayList attributelist=new ArrayList();
									LabelValueView id=new LabelValueView("9","id");
									LabelValueView scroll=new LabelValueView(this.getFormHM().get("salaryscroll")!=null?this.getFormHM().get("salaryscroll").toString():"0","scroll");
									LabelValueView twinkle=new LabelValueView(this.getFormHM().get("salarytwinkle")!=null?this.getFormHM().get("salarytwinkle").toString():"0","twinkle");
									LabelValueView show=new LabelValueView("1","show");
									attributelist.add(id);
									attributelist.add(scroll);
									attributelist.add(twinkle);
									attributelist.add(show);
									nodeslist.add(attributelist);
								}
								else
								{
									ArrayList attributelist=new ArrayList();
									LabelValueView id=new LabelValueView("9","id");
									LabelValueView scroll=new LabelValueView(this.getFormHM().get("salaryscroll")!=null?this.getFormHM().get("salaryscroll").toString():"0","scroll");
									LabelValueView twinkle=new LabelValueView(this.getFormHM().get("salarytwinkle")!=null?this.getFormHM().get("salarytwinkle").toString():"0","twinkle");
									LabelValueView show=new LabelValueView("0","show");
									attributelist.add(id);
									attributelist.add(scroll);
									attributelist.add(twinkle);
									attributelist.add(show);
									nodeslist.add(attributelist);
								}
					   }
					}
			}
			new PortalTailorXml().WriteOutParameterXml("SYS_PARAM",this.getFrameconn(),nodeslist,"门户定制",this.userView.getUserName(),String.valueOf(this.userView.getStatus()));
		}
	}catch(Exception e)
	{
		 e.printStackTrace();
		 throw GeneralExceptionHandler.Handle(e);
	}
	}
}
