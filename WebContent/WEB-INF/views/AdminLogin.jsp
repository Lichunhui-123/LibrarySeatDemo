<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="style/alogin.css" rel="stylesheet" type="text/css" />
<title>用户登录</title>
<style type="text/css">
body{
    margin: 0;
	padding: 0;
	font-size: 12px;
	background: #214D90 url(../images/bg1.jpg) repeat-x;
	color: #999999;
	font-family: Tahoma,Verdana
}
</style>
<script type="text/javascript">

    //加载验证码
	function loadimage(){
		document.getElementById("randImage").src="images/image.jsp?"+Math.random();
	}
</script>
</head>
<body>
<form id="form2" name="form2" action="aLogin.htm?roleId=1" method="post">
<header>
		<h1 align="center" style="color: #AEEEEE">欢迎管理员来到登录中心</h1>
		<h2></h2>
</header>
	<div class="MAIN">
		<ul>
			<li class="top"></li>
			<li class="top2"></li>
			<li class="middle_A"></li>
			<li class="topD">
			<ul class="login">
				<br/>
				<br/>
				<li><span class="left">用户名：</span> <span style=""> <input id="userName" name="userName" type="text" class="txt" value="${userName }" /> </span></li>
				<li><span class="left">密&nbsp;&nbsp;&nbsp;码：</span> <span style=""> <input id="password" name="password" type="password" class="txt" value="${password }" onkeydown= "if(event.keyCode==13)form1.submit()"/> </span></li>
				<li><span class="left">验证码：</span> <span style=""> <input type="text" value="${imageCode }" name="imageCode"  class="txtCode" id="imageCode" size="10" onkeydown= "if(event.keyCode==13)form1.submit()"/>&nbsp;<img onclick="javascript:loadimage();"  title="换一张试试" name="randImage" id="randImage" src="images/image.jsp" width="60" height="20" border="1" align="absmiddle"> </span></li>
			    <li class="middle_C"><span class="btn"> <img alt="" src="images/login/btnlogin.gif" onclick="javascript:document.getElementById('form2').submit()"/> </span>&nbsp;&nbsp;<span ><font color="red">${error }</font></span></li>
			</ul>
			</li>
			
			<li class="topE"></li>
			<li class="bottom_A"></li>
			<li class="bottom_B">图书馆预约占座管理系统</li>
		</ul>
	</div>
</form>
</body>
</html>