<!DOCTYPE html>
<html lang="ja-JP">
<head>
	<title>NASB1995</title>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="_csrf_header" content="${_csrf.headerName}">
	<meta name="_csrf_token" content="${_csrf.token}">
	<link rel="stylesheet" type="text/css" href="/static/bootstrap/css/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="/static/css/customize.css">
	<link rel="shortcut icon" type="image/x-icon" href="/static/favicon.ico">
	<link rel="preconnect" href="https://fonts.googleapis.com">
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
	<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Corinthia:wght@700&family=Noto+Sans+JP:wght@100..900&display=swap">
	<script type="text/javascript" src="/static/jquery/jquery-3.7.1.min.js"></script>
</head>

<body>
	<style>
		.bg-dark {
			background-color: #000000 !important;
		}
	</style>
	<nav class="navbar navbar-dark bg-dark fixed-top" role="navigation">
		<div class="container-fluid">
			<div class="d-flex justify-content-start">
				<img src="/static/image/cross.svg" alt="img01" width="60" height="60">
				<a class="navbar-brand effect-shine" style="font-size: 33px;" href="#">
					NASB1995
				</a>
			</div>
		</div>
		<form id="logoutForm" method="post" action="/category/logout" style="display: none;"></form>
	</nav>
	<div class="container" style="margin-top: 60px;">
		<div class="text-center">
			<h2>システム情報</h2>
			<#if exception?exists>
				<h7>${exception}</h7>
			<#else>
				<h7>想定外のエラーが発生しました。管理員へご連絡ください。</h7>
			</#if>
			<button id="backBtn" style="width: 300px; margin: 0px auto 0px auto;"
				class="btn btn-lg btn-warning btn-block">戻る</button>
		</div>
	</div>
	<script type="text/javascript">
		$("#backBtn").on('click', function () {
			$("#logoutForm").submit();
		});
	</script>
</body>

</html>