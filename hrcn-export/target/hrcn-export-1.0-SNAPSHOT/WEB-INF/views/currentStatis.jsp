<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page session="false" %>
<html>
  <head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>巨商汇日志平台</title>
      <!-- ECharts单文件引入 -->
      <script type="text/javascript" src="/js/echart/echarts-all.js"></script>
      <script type="text/javascript">
          // 基于准备好的dom，初始化echarts图表
          var myChart = echarts.init(document.getElementById('main'));
          option = {
              title : {
                  text: '今日各模块统计',
                  subtext: '【${now}】'

              },
              tooltip : {
                  trigger: 'axis'
              },
              legend: {
                  data:['模块统计']
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
                      data : ${actionCodeList}
                  }
              ],
              yAxis : [
                  {
                      type : 'value'
                  }
              ],
              series : [
                  {
                      name:'模块统计',
                      type:'bar',
                      //barWidth:'30',
                      data:${countArray}
                    /*  markPoint : {
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
  </head>
  <body>
  <!-- 为ECharts准备一个具备大小（宽高）的Dom -->
  <div id="main" style="height:400px;"></div>
  </body>
</html>
