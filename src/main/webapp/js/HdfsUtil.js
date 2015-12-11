$.fn.coffee = function(obj){
	  for(var eName in obj)
	    for(var selector in obj[eName])
	      $(this).on(eName, selector, obj[eName][selector]);
};
MyModal = {
	type : '',
	myModal : '',
	p : 101,
	stop : 1,
	showModal : function(o,type){
		this.myModal = o;
		this.type = type;
		$("#"+this.myModal).modal();
	},
	setDirChecked : function(nametd,nametddir){
		var dir = nametddir;
		var file = nametd;
		var flag = false;
		$("input[name^='file_c']").each(function(i){  
		    var isCheck = $(this).attr("checked");
		    if('checked' == isCheck || isCheck){
		    	if($("#filetype_"+i).val()=='dir'){
			    	if(dir!==''){
			    		$("#"+dir).val($("#nametddir_"+i).val()+"/"+$("#nametd_"+i).val());
			    	}
			    	if(file!=''){
			    		$("#"+file).html($("#"+dir).val());
			    	}
			    	return false;
		    	}else{
			    	$("#"+this.myModal).modal("hide");
			    	flag = true;
			    	return true;
			    }
		    }
		});
		return flag;
	},
	setOneChecked : function(nametd,nametddir){
		var dir = nametddir;
		var file = nametd;
		$("input[name^='file_c']").each(function(i){  
		    var isCheck = $(this).attr("checked");
		    if('checked' == isCheck || isCheck){
		    	
		    	if(dir!==''){
		    		$("#"+dir).val($("#nametddir_"+i).val()+"/"+$("#nametd_"+i).val());
		    	}
		    	if(file!=''){
		    		$("#"+file).html($("#nametd_"+i).val());
		    	}
		    	return false;
		    }
		});
	},
	setMoreChecked : function(nametd,nametddir){
		var dir = nametddir;
		var file = nametd;
		var files = "";
		var len = $("input[name^='file_c']");
		$("input[name^='file_c']").each(function(i){  
		    var isCheck = $(this).attr("checked");
		   	var flag = false;
		    if('checked' == isCheck || isCheck){
		    	if(files ==  "") {
		    		files +=  $("#nametd_"+i).val();
		    	} else {
		    		files += ("," +  $("#nametd_"+i).val());
		    	}
		    	if(!flag){
		    		$("#"+dir).val($("#nametddir_"+i).val());
		    	}
		    	flag = true;
		    }
		});
		$("#"+file).val(files);
	},
	getChecked : function(){
		return HdfsUtil.checkXZ();
	},
	openModal : function(o,nametd,nametddir,type){
		if(this.getChecked()){
			var flag = false;
			if(type==""){
				this.setOneChecked(nametd,nametddir);
			}else if(type=='c' || type=='m' ||  type=='d'){
				this.setMoreChecked(nametd,nametddir);
			}else if(type=='n' || type=='i'){
			   flag = this.setDirChecked(nametd,nametddir);
			}
			if(flag){
				alert("请选择文件夹");
			}else{
				this.showModal(o,type);
			}
		}else{
			alert("请选择");
		}
	},
	openNewModal : function(o,nametd,nametddir,type){
		if(this.getChecked()){
			if(this.setDirChecked(nametd,nametddir)){
				alert("请选择文件夹");
			}else{
				this.showModal(o,type);
			}
			return false;
		}
		if(nametd!=''){
			$("#"+nametd).text($("#into_dir").text());
		}
		if(nametddir!=''){
			$("#"+nametddir).val($("#into_dir").text());
		}
		this.showModal(o,type);
		return true;
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

$(function() {
	$('.action-box').coffee({
		  click: {
		   '#newfile_b': function(){
			   MyModal.openNewModal("myModal9", "","newFileDir","n");
			},
			'#newdir_b': function(){
			   MyModal.openNewModal("myModal10", "","mkdirname","n");
			},
		   '#rename_b': function(){
			   MyModal.openModal("myModal", "file_dn","oldname","");
			   $("#filename").val($("#file_dn").html());
		    },
		    '#save_down_as': function(){
		    	$("#myModal12").modal();
			 },
			 '#editfilename_save': function(){
		    	$("#current_downdir").val($("#editfilename").val());
		    	$("#file_form_e").submit();
			 },
		    '#importfile_b': function(){
		    	 MyModal.openNewModal("myModal8", "file_import_file","importdirname","i");
		    },
		    '#importzipfile_b': function(){
		    	 MyModal.openNewModal("myModal11", "file_import_zip","importzipdirname","i");
		    },
		    '#download_b': function(){
		    	MyModal.initRunParm();
		    	 MyModal.openModal("myModal4", "","downfilename","");
		    },
		    '#delete_b': function(){
		    	MyModal.openModal("myModal5", "deletename","deletdir","d");
		    	$("#deletename").val($("#deletdir").val() + "/" + $("#deletename").val());
		    },
		    '#copy_b': function(){
		    	MyModal.openModal("myModal3","copyfilename","sourcecopydirname","c");
		    },
		    '#move_b': function(){
		    	MyModal.openModal("myModal2","movefilename","sourcemovedirname","m");
		    },
			'#q_file_name' : function() {
				if($.trim($("#qfilename").val())!=''){
					$("#currentDir_search").val($("#into_dir").text());
					$("#qfileForm").submit();
				}
			},
		    '.showdispalybar': function(){
		    	$("#show_"+MyModal.type).toggle();
		    	WindowOpener.ajaxQueryDirs(1,5);
		    },
		    '.showfilebar' : function(){
				$(".filebar").show();
		    },
			'.hidefilebar' : function(){
				$(".filebar").hide();
			},
			'.hidebar' : function(){
				window.location.reload();
			},
			'.xz_bar' : function(){
				var wjj = $("input[name=wjj]:checked").attr("id");
				var dirpath = $("#"+wjj+"_value").text();
				if(MyModal.type=='n'){
					dirpath = $.trim($("#curent_dir_n").text()) + "/" + dirpath;
				}
				$(".filenameclass").val(dirpath);
			},
			'.file_qbar' : function(){
				var cur_dir = $("#curent_dir_"+MyModal.type).text();
				if($(".filenameclass").val()=="" || $(".filenameclass").val()==" "){
					alert("请选择目标文件夹");
				}else{
					$("#name_dir_"+MyModal.type).val($.trim(cur_dir)+"/"+$(".filenameclass").val());
					$("#file_Form_"+MyModal.type).submit();
				}
			},
			'.dircreate' : function(){
				var dirname = $("#dirname_"+MyModal.type).val();
				$.ax("ajaxCreateDir","dirname="+dirname,"GET","text",function(msg){
					 var dataObj=eval("("+msg+")");    
	            	 var dir = "";
	            	 for(var i=0;i<dataObj.length;i++){   
	            		  dir = dir + '<tr><td>&nbsp;<input type="radio" value="0" name="wjj" id="wjj_'+i+'"/>&nbsp;<img src="images/sLogo_07.png" alt="" class="slogo"/></td>';
	          		      dir = dir + '<td id="wjj_'+i+'_value"><a href="#" onclick="WindowOpener.ajaxJump(this)">'+dataObj[i]+'</a></td></tr>';
	                 }  
	            	$("#list_dir_"+MyModal.type).html(dir);
	            	$(".filebar").hide();
				});
			},
			'#checkedall' : function(){ 
				$('[name=file_c]:checkbox').attr("checked", this.checked); 
			},
			'#fdp_hbase' : function(){ 
				var url = HdfsUtil.getRootPath() + '/hbase/list';
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

HdfsUtil = {
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
	}
};
