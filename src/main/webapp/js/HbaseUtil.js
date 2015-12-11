$.fn.coffee = function(obj){
	  for(var eName in obj)
	    for(var selector in obj[eName])
	      $(this).on(eName, selector, obj[eName][selector]);
};
MyModal = {
	p : 101,
	stop : 1,
	type : '',
	myModal : '',
	showModal : function(modalname,type){
		this.myModal = modalname;
		this.type = type;
		$("#"+this.myModal).css('z-index', "99999");
		$("#"+this.myModal).modal({
			keyboard: false,
			backdrop: 'static'
		});
	},
	openModal : function(modalname,type){
		this.showModal(modalname,type);
	},
	initRunParm : function(){
		this.p = 0;
		this.stop = 0;
	},
	run : function(){
       this.p += 4;
       $("div[class=bar]").css("width",  this.p + "%");
       setTimeout("run()", 500);
       if ( this.p >100 && this.stop<1) {         
           this.p = 0;
       }
	}
};
Cudq = {
		addLine:function(tablename,width){
			var parms = "tablename="+tablename;
			$.ax("ajaxGetFamilyColums",parms,"GET","text",function(msg){
				var dataObj=eval("("+msg+")");
				var familyColums = dataObj.familyColums;
				var html = '<tr>';
				html+='<td width="5%"><input type="checkbox" name="ch"/></td>';
				html+='<td width="10%"><input type="text" name="" style="width:95%"/></td>';
				for(key in familyColums){
					var familyColum = familyColums[key];
					var length = familyColum.length;
					html+='<td style="padding:0">';
					html+='<table class="table" style="padding:0;margin:0"><tr>';
					for(var i=0;i<length;i++){
						html+='<td width="'+width+'%"><input type="text" name="" style="width:90%" /></td>';
					}
					html+='</tr></table></td>';
					$("#hbase_index_table").append(html);
				}
				
			});
		},
		delLines:function(){
            $('input:checkbox[name="ch"]').each(function () {
                if ($(this).attr("checked")) {
                    $(this).parent().parent().remove();
                }
            });
		}
};
$(function() {
	$('.action-box').coffee({
		  click: {
			'#new_b':function(){
			  MyModal.openModal("myModal3","n");  
			},
		   '#import_b': function(){
			   MyModal.openModal("myModal2","i");
			},
			'.showdispalybar': function(){
		    	$("#show_"+MyModal.type).toggle();
		    	WindowOpener.ajaxGetHbaseTables();
		    },
		    '#hbase_add_family':function(){
		    	var rodom = HbaseUtil.RndNum(4);
		    	 rodom = parseInt(rodom);
		    	var html = "<tr><td width='30%' class='family_td'><input type='text' name='' /></td>" +
		    			"<td class='family_cols_td' width='60%' style='text-align:left !important'>" +
		    			"<input style='width:350px' readonly='readonly' type='text' id='hcheckbox_"+rodom+"' />" +
		    					"<a style='float:right;margin-right:10px;height:30px;line-height:30px;vertical-align:middle' href='#' onclick='hbase_xzzd("+rodom+",2)'>选择字段<a>" +
		    							"</td>";
		    	$(".hbase_family tbody").append(html);
		    }, 
		    '#hbase_del_family':function(){
		    	$(".hbase_family tr:last").remove();
		    },
		    '.hidebar' : function(){
				window.location.reload();
			},
			'#db_xz' : function(){
		    	var db_name = $(this).find("a").html();
		    	var parms = "dbtablename="+db_name;
		    	$(".licss li").each(function(){
		    		$(this).removeClass("bkgdEb");
		    	});
		    	$(this).addClass("bkgdEb");
		    	var dbtype = $("#dbtype").val();
				var dbname = $("#dbname").val();
				var url = $("#url").val();
				var username = $("#username").val();
				var passwd = $("#passwd").val();
				var params = "dbtype="+dbtype+"&dbname="+dbname+"&url="+url+"&username="+username+"&passwd="+passwd+"&"+parms;
		    	WindowOpener.ajaxGetDBField(params);
		    },
		    '#fdp_wjj' : function(){
		    	var url = HdfsUtil.getRootPath() + "/list";
		    	WindowOpener.index(url);
		    },
		    '#fdp_hbase' : function(){
		    	var url = HdfsUtil.getRootPath() + "/hbase/list";
		    	WindowOpener.index(url);
		    },
		    '#fdp_solr' : function(){
		    	var url = HdfsUtil.getRootPath() + "/solr/list";
		    	WindowOpener.index(url);
		    },
		    '#checkedall' : function(){ 
				$('[name=h_t_field]:checkbox').attr("checked", this.checked); 
			},
			'#checkexzxd' : function(){ 
				$('[name=dbzdxz]:checkbox').attr("checked", this.checked); 
			},
			'.tablexz' : function(){ 
				$("#hbase_table_name").val($(this).text());
				$("#show_i").hide();
				var parms = "hbasetablename="+$("#hbase_table_name").val();
				WindowOpener.ajaxGetHbaseFamily(parms); 
			},
			'#new_tj' : function(){ 
				$("#progressBar").show();
				$("#newForm").submit();
				$("#progressBar").hide();
			}
		  },
		  change:{
			  '#hbase_sel_1':function(){
				  $(".hbase_sel_1").val(0);
				  var p1=$(this).children('option:selected').val();
				  if(p1==5){
					  hbase_xzzd('hbase_sel_1',3);
				  }
			   },
			  '#hbase_sel_2':function(){
				  $(".hbase_sel_2").val(0);
				  var p1=$(this).children('option:selected').val();
				  if(p1==5){
					  hbase_xzzd('hbase_sel_2',3);
				  }
			   },
			   '#hbase_sel_3':function(){
				   $(".hbase_sel_3").val(0);
				  var p1=$(this).children('option:selected').val();
				  if(p1==5){
					  hbase_xzzd('hbase_sel_3',3);
				  }
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

HbaseUtil = {
	checkXZ : function(id) {
		var flag = false;
		$("input[name^='file_c']").each(function(i){  
		    var isCheck = $(this).attr("checked");
		   if('checked' == isCheck || isCheck){
			   flag = true;
		    }
		});
		return flag;
	},
	
	getRootPath : function(){
	    var curWwwPath=window.document.location.href;
	    var pathName=window.document.location.pathname;
	    var pos=curWwwPath.indexOf(pathName);
	    var localhostPaht=curWwwPath.substring(0,pos);
	    var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);
	    return(localhostPaht+projectName);
	},
	RndNum : function(n){
		var rnd="";
		for(var i=0;i<n;i++)
		rnd+=Math.floor(Math.random()*10);
		return rnd;
	}
};
