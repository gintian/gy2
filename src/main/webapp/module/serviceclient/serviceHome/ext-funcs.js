/* window 灞傚彔鏄剧ず鏃秈e涓嬮『搴忛敊涔遍棶棰�  BEGIN */
//閿�姣佹棫鐨� window zindex鍫嗘爤瀵硅薄
Ext.WindowManager.zIndexStack.destroy( );
//鏂板缓 window zindex鍫嗘爤瀵硅薄锛屾帓搴忚鍒欐坊鍔犳寜鏃堕棿鎺掑簭
Ext.WindowManager.zIndexStack = new Ext.util.Collection({
        sorters: {
            sorterFn: function(comp1, comp2) {
                var ret = (comp1.alwaysOnTop || 0) - (comp2.alwaysOnTop || 0);
                if (!ret) {
                   //濡傛灉涓や釜鏄痺indow鏃讹紝浣跨敤window鏄剧ず鏃堕棿鎺掑簭
                	   if((comp1.xtype=='window'||comp1.xtype=='messagebox') && (comp2.xtype=='window' || comp2.xtype=='messagebox')){
                		  return  comp1.showDate>comp2.showDate;
                	   }
                   ret = comp1.getActiveCounter() - comp2.getActiveCounter();
                }
                
                return ret;
            }
        },
        filters: {
            filterFn: function(comp) {
                return comp.isVisible();
            }
        }
});

//涓簑indow鍫嗘爤瀵硅薄娣诲姞 鐩戝惉瀵硅薄
Ext.WindowManager.zIndexStack.addObserver(Ext.WindowManager);

//window 鏄剧ず鏃舵坊鍔犳椂闂�
Ext.override(Ext.window.Window,{
    		show:function(){
    			this.showDate = new Date();
    			return this.callParent();
    		}
});
/* window 灞傚彔鏄剧ず鏃秈e涓嬮『搴忛敊涔遍棶棰�  END */