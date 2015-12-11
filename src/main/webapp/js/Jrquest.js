$(function() {
	jQuery.ax = function(url, data, type, dataType, successfn, errorfn) {
		type = (type == null || type == "" || typeof (type) == "undefined") ? "post"
				: type;
		dataType = (dataType == null || dataType == "" || typeof (dataType) == "undefined") ? "json"
				: dataType;
		data = (data == null || data == "" || typeof (data) == "undefined") ? {
			"date" : new Date().getTime()
		} : data;
		$.ajax({
			type : type,
			data : data,
			url : url,
			dataType : dataType,
			success : function(d) {
				successfn(d);
			}
		});
	};
	jQuery.axs = function(url, data, successfn) {
		data = (data == null || data == "" || typeof (data) == "undefined") ? {
			"date" : new Date().getTime()
		} : data;
		$.ajax({
			type : "post",
			data : data,
			url : url,
			dataType : "json",
			success : function(d) {
				successfn(d);
			}
		});
	};
	jQuery.axse = function(url, data, successfn, errorfn) {
		data = (data == null || data == "" || typeof (data) == "undefined") ? {
			"date" : new Date().getTime()
		} : data;
		$.ajax({
			type : "post",
			data : data,
			url : url,
			dataType : "json",
			success : function(d) {
				successfn(d);
			}
		});
	};

	jQuery.href = function(url) {
		window.location.href = url;
	};
});
Pager = {
	pageIndex : 1,
	pageSize : 10, 
	GoToFirstPage : function(countPage){
		Pager.pageIndex -= 1;
		Pager.pageIndex = Pager.pageIndex >= 1 ? Pager.pageIndex : 1;
        WindowOpener.ajaxQueryDirs(Pager.pageIndex,Pager.pageSize, $("#curent_dir_"+MyModal.type).text());
	},
	GoToNextPage : function(countPage){
		if (Pager.pageIndex + 1 <= countPage){
			Pager.pageIndex += 1;
     	 	WindowOpener.ajaxQueryDirs(Pager.pageIndex,Pager.pageSize, $("#curent_dir_"+MyModal.type).text());
    	}
		
	},
	GoToFirstFormPage : function(countPage,pageIndex){
		pageIndex -= 1;
		pageIndex = pageIndex >= 1 ? pageIndex : 1;
		var url = HdfsUtil.getRootPath() + '/jump?current_name_dir='+$("#into_dir").text()+"&page="+pageIndex+"&rows="+Pager.pageSize;
		$.href(url);
	},
	GoToNextFormPage : function(countPage,pageIndex){
		if (pageIndex + 1 <= countPage){
			pageIndex += 1;
			var url = HdfsUtil.getRootPath() + '/jump?current_name_dir='+$("#into_dir").text()+"&page="+pageIndex+"&rows="+Pager.pageSize;
			$.href(url);
    	}
	},
	NextPage:function(countPage,pageIndex,pathName){
		if (pageIndex + 1 <= countPage){
			pageIndex += 1;
			var url = HbaseUtil.getRootPath()+pathName+"&page="+pageIndex+"&rows="+Pager.pageSize;
			$.href(url);
		}else{
			return false;
		}
	},
	PrePage:function(countPage,pageIndex,pathName){
		if(pageIndex-1>0){
			pageIndex -= 1;
			var url = HbaseUtil.getRootPath()+pathName+"&page="+pageIndex+"&rows="+Pager.pageSize;
			$.href(url);
		}else{
			return false;
		}
	}
},
WindowOpener = {
		jump4recursion : function(obj){
			WindowOpener.jump(CurrentDir.getCurrentDir(obj));
		},
		jumpRoot : function (){
			WindowOpener.jump("/user/public");
		},
		jumpDetail : function(obj){
			WindowOpener.jump($(obj).text());
		},
		jump : function(parm){
			Pager.pageIndex = 1;
			var url = HdfsUtil.getRootPath() + '/jump?current_name_dir='+parm+"&page="+Pager.pageIndex+"&rows="+Pager.pageSize;
			$.href(url);
		},
		list : function(obj){
			var url = HdfsUtil.getRootPath() + '/list?cur_dir='+ $.trim($("#into_dir").text()) + "/"+ $(obj).html();
			$.href(url);
		},
		index :function(url){
			$.href(url);
		},
	    ajaxJump4recursion:function(obj){
	    	Pager.pageIndex = 1;
	    	var ajax_cc = CurrentDir.getCurrentDir(obj);
			WindowOpener.ajaxQueryDirs(Pager.pageIndex,Pager.pageSize,ajax_cc);
		},
		ajaxJump : function (obj,dir){
			Pager.pageIndex = 1;
			var ajax_cc =  $("#curent_dir_"+MyModal.type).text() + "/" +$(obj).html();
			WindowOpener.ajaxQueryDirs(Pager.pageIndex,Pager.pageSize,ajax_cc);
		},
		ajaxQueryTables : function(obj){
			var tableName = $(obj).html();
			Pager.pageIndex = 1;
			var url = HbaseUtil.getRootPath() + '/hbase/queryList?tableName='+tableName+"&page="+Pager.pageIndex+"&rows="+Pager.pageSize;
			$.href(url);
		},
		ajaxGetHbaseFamily : function(parms){
			$.ax("ajaxGetHbaseFamily",parms,"GET","text",function(msg){
				 var dataObj=eval("("+msg+")");
				 var familyColums = dataObj.tableInfo.familyColums;
				 var html = "";
				 var kk = 0;
				 for (var key in familyColums) {
					 var family =  "<tr><td class='family_td'>"+key+"</td>";
					 var familyCols = familyColums[key];
					 var cols = "";
					 for(var i=0;i<familyCols.length;i++){
						 cols = cols + "," + familyCols[i] ;
					 }
					 cols = cols.substr(1);
					 var rodom = HbaseUtil.RndNum(4);
					 rodom = parseInt(rodom);
					 family = family + "<td id='hcheckbox_"+rodom+"' class='family_cols_td'>"+cols+"</td><td><a href='#' onclick='hbase_xzzd("+rodom+",1)'>选择字段<a></td>";
					 family = family + "</tr>";
					 html = html + family;
			     }
				 $(".hbase_family tbody").html(html);
			});
		},
		ajaxGetHbaseTables : function(parms){
			$.ax("ajaxGetHbaseTable",parms,"GET","text",function(msg){
				 var dataObj=eval("("+msg+")");
				 var tables = dataObj.tableList;
				var html = '<div class="list-group">';
				 for(var i=0;i<tables.length;i++){  
					 html = html + "<a href='#' class='list-group-item tablexz'>"+tables[i]+"</a>";
//					 html = html + "<tr><td><input type='radio' value='0' name='wjj' onchange='radiosz("+i+")' id='wjj_"+i+"'/></td><td>"+tables[i]+"</td></tr>";
				 }
				 html = html + "</div>";
				 $("#show_i").html(html);
			});
		},
		ajaxGetDBField : function(parms){
			$.ax("ajaxGetDBField",parms,"GET","text",function(msg){
	    		 var dataObj=eval("("+msg+")");
	           	 var fields = dataObj.fields;
	           	$('[id=checkedall]:checkbox').attr("checked", false); 
	           	 var htmlFileds = "";
	           	 for(var i=0;i<fields.length;i++){   
	           		htmlFileds = htmlFileds + "<tr><td><input type='checkbox' name='h_t_field'/></td><td>"+fields[i][0]+"</td><td>"+fields[i][1]+"</td></tr>";
	           	 }
	           	 $(".hbase_table_field_list table tbody").html(htmlFileds);
	    	});
		},
		ajaxGetDateBase : function(parms){
			$.ax("ajaxGetDateBase",parms,"GET","text",function(msg){
				$("#selectTFLoader").hide();
	    		 var dataObj=eval("("+msg+")");
	           	 var tables = dataObj.tables;
	           	 var fields = dataObj.fields;
	             var defaulttable = dataObj.defaulttable;
	           	 var htmltable = '<ul class="licss">';
	           	 for(var i=0;i<tables.length;i++){   
	           		 if(defaulttable==tables[i]){
	           			htmltable = htmltable + '<li class="bkgdEb" id="db_xz"><a href="#" >'+tables[i]+'</a></li>';
	           		 }else{
	           			htmltable = htmltable + '<li  id="db_xz"><a href="#">'+tables[i]+'</a></li>';
	           		 }
	           	 }
	           	htmltable = htmltable +"</ul>";
	           	 $(".hbase_table_list table tbody tr td").html(htmltable);
	           	 
	           	 var htmlFileds = "";
	           	 for(var i=0;i<fields.length;i++){   
	           		htmlFileds = htmlFileds + "<tr><td><input type='checkbox' name='h_t_field'/></td><td>"+fields[i][0]+"</td><td>"+fields[i][1]+"</td></tr>";
	           	 }
	           	 $(".hbase_table_field_list table tbody").html(htmlFileds);
	    	});
		},
		ajaxGetBaseCount : function(parms){
			$.ax("ajaxGetBaseCount",parms,"GET","text",function(msg){
				 var dataObj=eval("("+msg+")");
				 var countDb = dataObj.countDb;
				 $("#hbase_colums_count").val(countDb);
			});
			
		},
		ajaxQueryDirs : function(page,rows,ajax_cc){
			var rows=arguments[1]?arguments[1]:5;
			var ajax_cc=arguments[2]?arguments[2]:'';
			var parms = "page="+page+"&rows="+rows+"&current_name_dir="+ajax_cc;
			$.ax("ajaxJump",parms,"GET","text",function(msg){
	    		 var dataObj=eval("("+msg+")");
            	 var dirs = dataObj.dirs;
            	 var list = dataObj.fileinfos;
               if(list.length>0){
            	     var dirshtml = "";
	            	 for(var i=0;i<dirs.length;i++){   
	            		 dirshtml = dirshtml + '/<a href="#" onClick="WindowOpener.ajaxJump4recursion(this)">'+dirs[i]+'</a>';
	            	 }
	            	 $("#curent_dir_"+MyModal.type).html(dirshtml);
            	 
	            	 var dir = "";
	            	 for(var i=0;i<list.length;i++){   
	            		  dir = dir + '<tr><td>&nbsp;<input type="radio" value="0" name="wjj" id="wjj_'+i+'"/>&nbsp;<img src="images/sLogo_07.png" alt="" class="slogo"/></td>';
	           		      dir = dir + '<td id="wjj_'+i+'_value"><a href="#" onclick="WindowOpener.ajaxJump(this)">'+list[i]+'</a></td></tr>';
	                 }
	            	var total = dataObj.total;
	            	var pageIndex = Pager.pageIndex;
	     			var pageSize = Pager.pageSize; 
	            	var countPage = (total % pageSize == 0  ? parseInt(total / pageSize) : parseInt(total / pageSize + 1));
	                var htmlCode = dir + "<tr>";
				    htmlCode += "<td>";
				    htmlCode += "<span>共" + total + "条;<span id='count'>" + countPage+ "</span>页;" + "</span>";
				    htmlCode += "<span>第"+pageIndex+" 页;</span>";
				    htmlCode += "<a href='javascript:void' o ipt:void' onclick='Pager.GoToFirstPage("+countPage+")' id='aPrePage' >前一页</a>&nbsp;&nbsp; ";
				    htmlCode += "<a href='javascript:void' onclick='Pager.GoToNextPage("+countPage+")' id='aNextPage'>下一页</a>&nbsp;&nbsp; ";
				    htmlCode += "</td>";
				    htmlCode += "</tr>";
	            	$("#list_dir_"+MyModal.type).html(htmlCode);
            	 }
	    	});
		}
};