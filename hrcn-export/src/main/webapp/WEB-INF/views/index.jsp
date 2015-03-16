<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="false" %>
<html>
  <head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>巨商汇日志平台</title>
      <link rel="shortcut icon" type="image/ico" href="/images/favicon.ico" />
      <!-- 新 Bootstrap 核心 CSS 文件 -->
      <link rel="stylesheet" href="/js/bootstrap-3.3.2/css/bootstrap.min.css">
      <link rel="stylesheet" href="/js/bootstrap-3.3.2/css/bootstrap-theme.min.css">

      <link rel="stylesheet" href="/js/jquery/jquery-ui/css/jquery-ui.min.css">
      <link rel="stylesheet" href="/js/jquery/DataTables/css/jquery.dataTables.min.css">
      <link rel="stylesheet" href="/js/jquery/DataTables/css/jquery.dataTables_themeroller.css">
  <style>
      body {
          padding-top: 50px;
      }
      .table_div{
          overflow: auto;text-justify: auto;border: groove;margin-top: 20px;
      }
  </style>
  <!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
  <script type="text/javascript" src="/js/jquery.min.js"></script>
  <script type="text/javascript" src="/js/jquery/jquery-form/jquery.form.min.js"></script>

  <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
  <script type="text/javascript" src="/js/bootstrap-3.3.2/js/bootstrap.min.js"></script>
  <script type="text/javascript" src="/js/jquery/jquery-ui/js/jquery-ui.min.js"></script>
  <script type="text/javascript">
      $(document).ready( function () {
          $("#logSearch").ajaxForm({
              type: 'post',
              url: '/log/toSearch.do',
              data: {"key":$("input[name='key']").val(),"tablename":$("input[name='tablename']").val()},
              dataType: 'html',
              success: function(html) {
                  $('#logListDIV').html(html);
                  $('#logListDIV').slideDown("slow");
              }
          });
          $("#logStatDIV").load("/stat/currentStatis.do");
          $("#clientMonitor").load("/log/clientMonitor.do");

      });

  </script>
  </head>
  <body>
  <nav class="navbar navbar-inverse navbar-fixed-top">
      <div class="container-fluid">
          <div class="navbar-header">
              <a class="navbar-brand" href="/log/index.do">巨商汇日志平台</a>
          </div>
          <div class="navbar-collapse collapse" id="navbar">
              <ul class="nav navbar-nav navbar-right" style="margin-right: 30%;">
                  <li><a href="/stat/showStat.do">图表统计</a></li>
              </ul>
          </div>
      </div>
  </nav>

  <div class="panel panel-default">
      <div class="panel-body" >
          <form id="logSearch" class="navbar-form navbar-collapse" action="/log/search.do" method="post">
              <div style="text-align: center;">
                  <input  name="key" type="text" class="form-control" style="width:400px;"  placeholder="请输入检索的内容...">
                  <select name="tablename" title="选择要查询的服务" class="form-control" >
                    <c:forEach var="name" items="${names}" varStatus="status">
                      <option value="${name.key}">${name.value}</option>
                    </c:forEach>
                  </select>
                  <button type="search"  class="btn" >查询</button>
              </div>
          </form>
          <div class="table_div" id="logListDIV" style="display: none;overflow: hidden;"></div>
          <div class="table_div" id="logStatDIV"></div>
          <div class="table_div" id="clientMonitor"  style="overflow: hidden;"></div>
      </div>
  </div>
  </body>
</html>
