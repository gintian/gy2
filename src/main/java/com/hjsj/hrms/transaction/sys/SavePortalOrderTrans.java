package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.sys.options.PortalTailorXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
/**
 * 平台门户面板顺序保存
 * @author Luckstar
 *
 */
public class SavePortalOrderTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
			ArrayList result = (ArrayList)this.getFormHM().get("result");
			int cols = Integer.parseInt((String)this.getFormHM().get("cols"));
			String portalid = (String)this.getFormHM().get("portalid");
			if(cols==0)
				return;
			ArrayList allpanels = new ArrayList();
			ArrayList newpanels = new ArrayList();
			for(int i=0;i<cols;i++){
				allpanels.add(new ArrayList());
			}
			for(int i=0;i<result.size();i++){
				MorphDynaBean bean = (MorphDynaBean)result.get(i);
				String id=(String)bean.get("id");
				id=id.split("-")[1];
				int col=Integer.parseInt((String)bean.get("col"));
				((ArrayList)allpanels.get(col)).add(id);
			}
			int firstColSize = ((ArrayList) allpanels.get(0)).size();
			for (int i = 1; i < allpanels.size(); i++) {
				ArrayList temps = (ArrayList) allpanels.get(i);
				while (temps.size() > firstColSize) {
					newpanels.add(temps.remove(temps.size() - 1));
				}
			}
			for (int i = 0; i < newpanels.size(); i++) {
				int c = i % cols;
				((ArrayList) allpanels.get(c)).add((String) newpanels
						.get(i));
			}
			
			ArrayList beans=new PortalTailorXml().NReadOutParameterXml(this.getFrameconn(),this.userView.getUserName(),portalid);
			ArrayList showitem = new ArrayList();
			TreeMap treemap= new TreeMap();
			for(int i=0;i<allpanels.size();i++){
				ArrayList ids=(ArrayList)allpanels.get(i);
				for(int m=0;m<ids.size();m++){
					String id=(String)ids.get(m);
					boolean flag = true;
					for(int n=0;n<beans.size();n++){
						LazyDynaBean bean= (LazyDynaBean)beans.get(n);
						String beanid=(String)bean.get("id");
						if(beanid.equals(id)){
							flag = false;
							treemap.put(new Integer(cols*m+i),beans.remove(n));
							break;
						}
					}
					if(flag){
						LazyDynaBean bean= new LazyDynaBean();
						bean.set("id", id);
						bean.set("twinkle", "0");
						bean.set("scroll", "0");
						bean.set("show", "1");
						treemap.put(new Integer(cols*m+i),bean);
					}
				}
			}
			for(Iterator i = treemap.values().iterator();i.hasNext();){
				showitem.add(i.next());
			}
			new PortalTailorXml().NWriteOutParameterXml(this.getFrameconn(),showitem,"新版门户定制",this.userView.getUserName(),String.valueOf(this.userView.getStatus()),portalid);
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
