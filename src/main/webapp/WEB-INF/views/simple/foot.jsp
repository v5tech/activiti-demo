<%@ page language="java"  pageEncoding="UTF-8" contentType="text/html; charset=utf-8"%>
<div id="footer">
	<div class="container">
		<p class="text-muted text-center">&copy; 雪山飞鹄 2014</p>
	</div>
</div>
	<script src="${pageContext.request.contextPath}/bootstrap/js/jquery-1.10.2.min.js"></script>
	<!-- <script src="${pageContext.request.contextPath}/bootstrap/js/bootstrap.min.js"></script> -->
	<script src="${pageContext.request.contextPath}/bootstrap-file/js/bootstrap.min.js"></script>
	<script src="${pageContext.request.contextPath}/bootstrap-select/bootstrap-select.min.js"></script>
	<script src="${pageContext.request.contextPath}/prettify/prettify.js"></script>
	<script type="text/javascript">
        /*$(window).on('load', function () {
            $('.selectpicker').selectpicker();
        });*/
        function showModle(title,url){
        	if(title=="请假流程定义"){
        		$.get(url,function(data){
        			$('.modal-body').text(data);
        		});
        	}else if(title=="请假流程图"){
        		$('.modal-body').html('<img alt="'+title+'" src="'+url+'">');
        	}
        	$('.modal-title').html(title);
        	$('#pdfModal').modal({keyboard: false});
        }
    </script>
	</body>
</html>