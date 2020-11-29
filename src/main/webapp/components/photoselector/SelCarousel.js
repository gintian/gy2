Ext.define('EHR.photoselector.SelCarousel',{
	extend:'Ext.carousel.Carousel',
	alternateClassName: 'PhotoSelector.Carousel',
	onDrag:function(e){
		if (!this.isDragging) {
			return;
		}
			        
	    var startOffset = this.dragStartOffset,
	    		direction = this.getDirection(),
	        delta = direction === 'horizontal' ? e.deltaX : e.deltaY,
	        maxIndex = this.getMaxItemIndex(),
	        currentActiveIndex = this.getActiveIndex(),
	    		offset;
	    		
	    	if ((currentActiveIndex === 0 && delta > 0) || (currentActiveIndex === maxIndex && delta < 0)) {
	        delta *= 0.5;
	    }	
	    		
	    	offset = startOffset + delta;
	    
	    if(currentActiveIndex==0 && offset>0){
	        return;
	    }
	    if(currentActiveIndex==this.getInnerItems().length-1 && offset<0){
	        return;
	    }
	
	    this.callParent(arguments);
	}	
});