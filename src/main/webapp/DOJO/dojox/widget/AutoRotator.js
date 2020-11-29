//>>built
define("dojox/widget/AutoRotator","dojo/_base/declare,dojo/_base/array,dojo/_base/lang,dojo/on,dojo/mouse,dojox/widget/Rotator".split(","),function(f,g,h,d,e,i){return f("dojox.widget.AutoRotator",i,{suspendOnHover:!1,duration:4E3,autoStart:!0,pauseOnManualChange:!1,cycles:-1,random:!1,reverse:!1,constructor:function(){var a=this;a.cycles-0==a.cycles&&0<a.cycles?a.cycles++:a.cycles=a.cycles?-1:0;a._signals=[d(a._domNode,e.enter,function(){if(a.suspendOnHover&&!a.anim&&!a.wfe){var c=a._endTime,b=a._now();
a._suspended=!0;a._resetTimer();a._resumeDuration=c>b?c-b:0.01}}),d(a._domNode,e.leave,function(){if(a.suspendOnHover&&!a.anim)a._suspended=!1,a.playing&&!a.wfe&&a.play(!0)})];a.autoStart&&1<a.panes.length?a.play():a.pause()},destroy:function(){g.forEach(this._signals,function(a){a.remove()});delete this._signals;dojo.forEach(this._connects,dojo.disconnect);this.inherited(arguments)},play:function(a,c){this.playing=!0;this._resetTimer();!0!==a&&0<this.cycles&&this.cycles--;if(0==this.cycles)this.pause();
else if(!this._suspended)if(this.onUpdate("play"),c)this._cycle();else{var b=(this._resumeDuration||0)-0,b=(0<b?b:this.panes[this.idx].duration||this.duration)-0;this._resumeDuration=0;this._endTime=this._now()+b;this._timer=setTimeout(h.hitch(this,"_cycle",!1),b)}},pause:function(){this.playing=this._suspended=!1;this.cycles=-1;this._resetTimer();this.onUpdate("pause")},_now:function(){return(new Date).getTime()},_resetTimer:function(){clearTimeout(this._timer)},_cycle:function(){var a=this,c=a.idx,
b;if(a.random){do b=Math.floor(Math.random()*a.panes.length+1);while(b==c)}else b=c+(a.reverse?-1:1);(c=a.go(b))&&c.addCallback(function(b){a.onUpdate("cycle");a.playing&&a.play(!1,b)})},onManualChange:function(a){this.cycles=-1;"play"!=a&&(this._resetTimer(),this.pauseOnManualChange&&this.pause());this.playing&&this.play()}})});