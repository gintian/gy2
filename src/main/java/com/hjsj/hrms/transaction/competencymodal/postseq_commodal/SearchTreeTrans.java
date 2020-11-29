package com.hjsj.hrms.transaction.competencymodal.postseq_commodal;

import com.hjsj.hrms.businessobject.competencymodal.PostModalBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SearchTreeTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			/**=1职务体系，=2岗位体系，=3岗位*/
			String object_type=(String)map.get("object_type");
			String historyType=(String)map.get("historyType");
			if(historyType!=null)
			{
				map.remove("historyType");
			}else{
				historyType="0";
			}
			String historyDate=(String)this.getFormHM().get("historyDate");
			if(historyDate==null|| "".equals(historyDate)){
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				Calendar calendar = Calendar.getInstance();				
				historyDate = sdf.format(calendar.getTime());
			}
			PostModalBo bo = new PostModalBo(this.getFrameconn(),this.userView);
			String isoper=bo.compareDate(historyDate);
			String codesetid="";
			String ishistory="1";
			if("3".equals(object_type))
			{
				codesetid="@K";
				TreeItemView treeItem=new TreeItemView();
				treeItem.setName("root");
				treeItem.setIcon("/images/unit.gif");	
				treeItem.setTarget("mil_body");
				String rootdesc="";
				Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
				rootdesc=sysoth.getValue(Sys_Oth_Parameter.ORG_ROOT_DESC);
				if(rootdesc==null||rootdesc.length()<=0)
				{
					rootdesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
				}
				this.getFormHM().put("rootDesc", rootdesc);
			    treeItem.setRootdesc(rootdesc.replaceAll("&", "&amp;"));
			    treeItem.setText(rootdesc);
			    treeItem.setTitle(rootdesc);
			    if(userView.isSuper_admin())
				    treeItem.setLoadChieldAction("/common/vorg/loadtree?busiPriv=&params=root&parentid=00&fromtype=pm&issuperuser=1&loadtype=0&treetype=duty&action=&target=mil_body&backdate="+historyDate+"&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue());
				else
				{
					treeItem.setLoadChieldAction("/common/vorg/loadtree?busiPriv=&params=root&parentid=00&fromtype=pm&issuperuser=0&loadtype=0&treetype=duty&action=&target=mil_body&backdate="+historyDate+"&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue());
				}
			    treeItem.setAction("/competencymodal/postseq_commodal/post_modal_list.do?b_query=link&a_code=@K");
			    this.getFormHM().put("treeItem",treeItem.toJS());
			}else
			{
				String param="";
				if("1".equals(object_type))
				{
					param="PS_CODE";//职务体系参数
				}else if("2".equals(object_type))
				{
					param="PS_C_CODE";//岗位体系参数
				}
				RecordVo constantuser_vo = ConstantParamter.getRealConstantVo(param);
	         	if (constantuser_vo == null) {
			    String temp="";
			    if("PS_CODE".equals(param)){
				   temp=ResourceFactory.getProperty("pos.posbusiness.nosetposcode");
			    }else if("PS_C_CODE".equals(param)){
				   temp=ResourceFactory.getProperty("pos.posbusiness.nosetposccode");
			    }
			     throw GeneralExceptionHandler.Handle(new GeneralException("",temp,"", ""));
		        }
		        codesetid = constantuser_vo.getString("str_value");
		        if("".equals(codesetid)|| "#".equals(codesetid)){
			        String temp="";
			        if("PS_CODE".equals(param)){
				        temp=ResourceFactory.getProperty("pos.posbusiness.nosetposcode");
			        }else if("PS_C_CODE".equals(param)){
				        temp=ResourceFactory.getProperty("pos.posbusiness.nosetposccode");
			        }
			        throw GeneralExceptionHandler.Handle(new GeneralException("",temp,"", ""));
		          }
				String sql = "select codesetdesc from codeset where UPPER(codesetid)='"+ codesetid.toUpperCase() + "'";
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				this.frowset = dao.search(sql);
				if (this.frowset.next())
					this.getFormHM().put("rootDesc",this.frowset.getString("codesetdesc"));
				this.getFormHM().put("codeitemid", "");
				PostModalBo pmb = new PostModalBo(this.getFrameconn(),this.getUserView());
				ishistory=pmb.isHistory(codesetid);
			}
			this.getFormHM().put("ishistory", ishistory);
			this.getFormHM().put("codesetid", codesetid);
			this.getFormHM().put("object_type",object_type);
			this.getFormHM().put("historyDate",historyDate);
			this.getFormHM().put("isoper", isoper);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
