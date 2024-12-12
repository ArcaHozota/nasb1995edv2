<!DOCTYPE html>
<html lang = "ja-JP">
<head>
	<title>NASB1995</title>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="_csrf_header" content="${_csrf.headerName}">
	<meta name="_csrf_token" content="${_csrf.token}">
	<link rel="stylesheet" type="text/css" href="/static/bootstrap/css/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="/static/css/index.css">
	<link rel="stylesheet" type="text/css" href="/static/css/index2.css">
	<link rel="stylesheet" type="text/css" href="/static/css/customize.css">
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
	<link rel="shortcut icon" type="image/x-icon" href="/static/favicon.ico">
	<link rel="preconnect" href="https://fonts.googleapis.com">
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
	<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Niconne&family=Noto+Sans+JP:wght@100..900&family=Noto+Serif+JP:wght@200..900&display=swap">
	<style>
		#indexTablehead,
		#saraniSearchBtn,
		#tableBody {
			font-family: "Noto Serif JP", serif;
			font-weight: 600;
		}
		
		a.effect-shine {
			font-family: "Niconne", serif;
		}
	</style>
	<script type="text/javascript" src="/static/jquery/jquery-3.7.1.min.js"></script>
	<script type="text/javascript" src="/static/bootstrap/js/bootstrap.bundle.min.js"></script>
	<script type="text/javascript" src="/static/layer/layer.js"></script>
	<script type="text/javascript" src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>

<body>
	<nav class="navbar navbar-expand-lg bg-body-tertiary fixed-top" data-bs-theme="dark">
		<div class="container-fluid">
			<div class="d-flex justify-content-start">
				<img src="/category/initial?icons=cross.svg" alt="img01" width="60" height="60">
				<a class="navbar-brand effect-shine" style="font-size: 33px;" href="/home">
					NASB1995
				</a>
			</div>
			<div class="d-flex justify-content-end">
				<a href="/category/login" type="button" class="btn btn-primary me-2" style="font-weight: 600;">
					<i class="fa-solid fa-right-to-bracket"></i> ログイン
				</a>
			</div>
		</div>
	</nav>
	<div class="col-sm-6 offset-sm-3 col-md-8 offset-md-2 main">
		<div class="card border-reflexblue" style="margin-top: 2rem;">
			<div class="card-header text-bg-reflexblue mb-3">
				<h5 class="card-title" style="padding-top: 8px;font-weight: 500;">
					<i class="fa-solid fa-cross"></i> 賛美歌一覧表
				</h5>
			</div>
			<div class="card-body">
				<div class="row">
					<div class="col-md-6 offset-md-2" style="padding-right: 0px;">
						<select id="hymnRecordEdit" class="form-select" style="width: 100%;">
			                <#list hymnDtos as hymnDto>
			                	<#if hymnDto.nameKr?exists>
									<option value="${hymnDto.id}">${hymnDto.nameJp}/${hymnDto.nameKr}</option>
			                	<#else>
			                		<option value="${hymnDto.id}">賛美歌${totalRecords}曲レコード済み</option>
								</#if>
							</#list>
				        </select>
			        </div>
			        <div class="col-md-4" style="padding-left: 0px;">
						<a id="saraniSearchBtn" class="btn btn-kanami" style="--clr: #006B3C;" type="button" href="#">
							<span>&#x1D11E;金海氏検索</span>
						</a>
			        </div>
				</div>
				<div class="background2" id="loadingBackground2" style="display: none;">
					<div class="wrapper">
                        <span class="content">LOADING</span>
                        <span class="effect-1"></span>
                        <span class="effect-2"></span>
                    </div>
				</div>
				<table class="table table-sm table-hover" id="indexTable">
					<thead id="indexTablehead">
						<tr class="table-primary">
							<th scope="col" class="text-center" style="width: 100%;">名称</th>
						</tr>
					</thead>
					<tbody id="tableBody" class="table-group-divider">
						<tr>
							<td>1</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	<script type="text/javascript" src="/static/customizes/index2.js"></script>
</body>

</html>