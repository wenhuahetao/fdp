<%@ page contentType="text/html; charset=UTF-8" %> 
<script type="text/javascript">
/* $(document).ready(function() { 
	$(".head_fx").find(".fx img").click(function(){
		var src = $(this).attr("src");
		var filename = src.substr(src.lastIndexOf("/")+1);
		if(filename.indexOf("_1.png")<0){
			var newFilename = filename.replace(".png","_1.png");
			var newSrc = src.substr(0,src.lastIndexOf("/")+1)+newFilename;
			$(this).attr("src",newSrc);
		}
	});
}); */
</script>
<div class="head_fx" style="width:100%;height:60px;background-image: -webkit-gradient(linear, left top, left bottom, from(#339900), to(#47ca00))">
	<div style="margin:15px" class="fl">
		<img src="<%=request.getContextPath()%>/images/LOGO.png" alt="" />
	</div>
	
	<div style="padding:0 10px" class="fr fx">
		<img src="<%=request.getContextPath()%>/images/jiankong.png" width=50 height=50
		onclick="javascript:onClick=window.open('http://112.33.1.202:10001/#/login', '_blank ');"  style="cursor:pointer;" alt="监控" class="logo fr"/>
	</div>
	
	
	<div class="fr" style="margin-left:0px">
		<img src="<%=request.getContextPath()%>/images/fgx.png" alt="" class="logo fr"/>
	</div>
	
	<div style="padding:0 10px" class="fr fx">
		<img src="<%=request.getContextPath()%>/images/solr.png" 
		onclick="WindowOpener.index('<%=request.getContextPath()%>/solr/list')"  style="cursor:pointer;"
		width=50 height=50  alt="大数据搜索" class="logo fl"/>
	</div>
	
	<div class="fr" style="margin-left:0px">
		<img src="<%=request.getContextPath()%>/images/fgx.png" alt="" class="logo fr"/>
	</div>
	<div style="padding:0 10px" class="fr fx">
		<img src="<%=request.getContextPath()%>/images/hbase.png" width=50 height=50
		onclick="WindowOpener.index('<%=request.getContextPath()%>/hbase/list')"  style="cursor:pointer;" alt="数据库" class="logo fr"/>
	</div>
	
	<div class="fr">
		<img src="<%=request.getContextPath()%>/images/fgx.png" alt="" class="logo fr"/>
	</div>
	
	<div style="padding:0 10px" class="fr fx">
		<img src="<%=request.getContextPath()%>/images/wenjianjia.png" 
				onclick="WindowOpener.index('<%=request.getContextPath()%>/list')"  style="cursor:pointer;" width=50 height=50 alt="知识库" class="logo fr"/>
	</div>
	
	<div class="fr">
		<img src="<%=request.getContextPath()%>/images/fgx.png" alt="" class="logo fr"/>
	</div>
	<div style="padding:0 10px" class="fr fx">
			<img src="<%=request.getContextPath()%>/images/caiji.png"  style="cursor:pointer;" width=50 height=50
				onclick="WindowOpener.index('<%=request.getContextPath()%>/collection/index')" alt="数据采集" class="logo fl"/>
	</div>

</div>
