$(function(){
		window.$write = document.getElementsByName("userName")[0];
		var shift = false;
		var capslock = false;
	// Download by http://www.codefans.net
	$('#keyboard li').mousedown(function(){
		var $this = $(this);
		if($this.hasClass('capslock'))
			return;
		$this.toggleClass('hold');
	});
	$('#keyboard li').mouseup(function(){
		var $this = $(this);
		if($this.hasClass('capslock'))
			return;
		$this.toggleClass('hold');
	});
	
	$('#keyboard li').click(function(){
		var $this = $(this),
			character = $this.html(); // If it's a lowercase letter, nothing happens to this variable
		
		// Shift keys
		if ($this.hasClass('left-shift') || $this.hasClass('right-shift')) {
			$('.letter').toggleClass('uppercase');
			$('.symbol span').toggle();
			
			shift = (shift === true) ? false : true;
			capslock = false;
			return false;
		}
		
		// Caps lock
		if ($this.hasClass('capslock')) {
			$('.letter').toggleClass('uppercase');
			$('.capslock').toggleClass('hold');
			capslock = true;
			return false;
		}
		
		// Delete
		if ($this.hasClass('delete')) {
			var currentValue = $write.value;
			//var html = $write.html();
			var updateValue = currentValue.substr(0,currentValue.length-1);
			//$write.html(html.substr(0, html.length - 1));
			$write.value = updateValue;
			return false;
		}
		
		// Special characters
		if ($this.hasClass('symbol')) character = $('span:visible', $this).html();
		if ($this.hasClass('space')) character = ' ';
		if ($this.hasClass('tab')) character = "\t";
		if ($this.hasClass('return')) character = "\n";
		
		// Uppercase letter
		if ($this.hasClass('uppercase')) character = character.toUpperCase();
		
		// Remove shift once a key is clicked.
		if (shift === true) {
			$('.symbol span').toggle();
			if (capslock === false) $('.letter').toggleClass('uppercase');
			
			shift = false;
		}
		
		// Add the character
		//$write.html($write.html() + character);
		$write.value = $write.value + character;

	});
});