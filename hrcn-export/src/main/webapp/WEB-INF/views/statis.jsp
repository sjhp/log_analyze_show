<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="false" %>
<html>
  <head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>巨商汇日志平台</title>
  </head>
  <body>
  <!-- 为ECharts准备一个具备大小（宽高）的Dom -->
  <div id="main" style="height:400px"></div>
  <script type="text/javascript">
          // 基于准备好的dom，初始化echarts图表
      var myChart = echarts.init(document.getElementById('main'));

      option = {
          title : {
              text: '各模块日志统计表',
              subtext: ''
          },
          tooltip : {
              trigger: 'axis'
          },
          legend: {
              data:['异常次数统计','短信发送次数统计','下单次数统计','注册次数统计','登陆次数统计']
          },
          toolbox: {
              show : true,
              feature : {
                  mark : {show: false},
                  dataView : {show: true, readOnly: false},
                  magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
                  restore : {show: false},
                  saveAsImage : {show: true}
              }
          },
          calculable : true,
          xAxis : [
              {
                  type : 'category',
                  boundaryGap : false,
                  data : ${dateList}
              }
          ],
          yAxis : [
              {
                  type : 'value',
                  axisLabel : {
                      formatter: '{value}/次 '
                  }
              }
          ],
          series : [
              {
                  name:'异常次数统计',
                  type:'line',
                  data:${exception}
              },
              {
                  name:'短信发送次数统计',
                  type:'line',
                  data:${message}
              },
              {
                  name:'下单次数统计',
                  type:'line',
                  data:${order}
              },
              {
                  name:'注册次数统计',
                  type:'line',
                  data:${register}
              },
              {
                  name:'登陆次数统计',
                  type:'line',
                  data:${login}
                 /* markPoint : {
                      data : [
                          {type : 'max', name: '最大值'},
                          {type : 'min', name: '最小值'}
                      ]
                  },
                  markLine : {
                      data : [
                          {type : 'average', name: '平均值'}
                      ]
                  }*/
              }
          ]
      };
      // 为echarts对象加载数据
      myChart.setOption(option);

  </script>
  </body>
</html>
