
	//全选
	function checkAll(name){
		var label=$("input[name='"+name+"']");
		var flag=label[0].checked;
		$("input[name='"+name+"']").each(function(){
			$(this).attr({
				"checked":!flag
			});
		});
	}
	//反选
	function inverChecked(name){
		$("input[name='"+name+"']").each(function(){
			var flag=this.checked;
			$(this).attr({
				"checked":!flag
			});
		});
	}
	