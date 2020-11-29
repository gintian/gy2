package com.hjsj.hrms.actionform.kq.kqself.apply;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class AnnualApplyForm extends FrameForm {

	    private ArrayList flist=new ArrayList();
	    private ArrayList plist = new ArrayList();
	    private String plan_id;
	    private String plan_name;
	    
	    
		public String getPlan_name() {
			return plan_name;
		}
		public void setPlan_name(String planName) {
			plan_name = planName;
		}
		public String getPlan_id() {
			return plan_id;
		}
		public void setPlan_id(String plan_id) {
			this.plan_id = plan_id;
		}
		public ArrayList getPlist() {
			return plist;
		}
		public void setPlist(ArrayList plist) {
			this.plist = plist;
		}
		@Override
        public void outPutFormHM() {

		  this.setFlist((ArrayList)this.getFormHM().get("flist"));
		  this.setPlist((ArrayList)this.getFormHM().get("plist"));
		  this.setPlan_id((String)this.getFormHM().get("plan_id"));
		  this.setPlan_name((String)this.getFormHM().get("plan_name"));
		}
		@Override
        public void inPutTransHM() {
		   this.getFormHM().put("flist",this.getFlist()); 
		   this.getFormHM().put("plan_id",this.getPlan_id());
		   this.getFormHM().put("plan_name", this.getPlan_name());
		}
		public ArrayList getFlist() {
			return flist;
		}
		public void setFlist(ArrayList flist) {
			this.flist = flist;
		}

}
