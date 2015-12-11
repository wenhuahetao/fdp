$.fn.coffee = function(obj){
	  for(var eName in obj)
	    for(var selector in obj[eName])
	      $(this).on(eName, selector, obj[eName][selector]);
};
$(function() {
	$('.action-box').coffee({
		  click: {
			'.structured_begin' : function(){ 
				var url = CollectionUtil.getRootPath() + '/collection/structureView';
				$.href(url);
		    },
		    '.unstructured_begin' : function(){ 
				var url = CollectionUtil.getRootPath() + '/collection/unstructureView';
				$.href(url);
		    }
		  },
		  mouseenter: {
		    '#btn-sort': function(){
		    }
		  }
	});
	
});
CurrentDir = {
	dir : "",
	getDir : function(obj) {
		if($(obj).prev().html()!=null){
			this.dir = $(obj).prev().html() + "/" + this.dir;
			this.getDir($(obj).prev());
		}
	},
	getCurrentDir : function(obj){
		this.dir = "";
		CurrentDir.getDir(obj);
		return "/" + this.dir + $(obj).html();
	}	
}; 
CollectionUtil = {
	getRootPath : function(){
	    var curWwwPath=window.document.location.href;
	    var pathName=window.document.location.pathname;
	    var pos=curWwwPath.indexOf(pathName);
	    var localhostPaht=curWwwPath.substring(0,pos);
	    var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);
	    return(localhostPaht+projectName);
	}
};