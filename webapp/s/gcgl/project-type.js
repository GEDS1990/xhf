/**
 * Created by jianbingfang on 2015/4/12.
 */
function drawChartProjectType(year) {

    $('#loading-project-type').show();

    $.post('gcgl-project-type-data.do', {year: year}, function (data) {

        $('#loading-project-type').hide();

        $('#chart-project-type').highcharts({
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            lang: {
                noData: '无数据'
            },
            title: {
                text: null
            },
            credits: {
                enabled: false
            },
            tooltip: {
                headerFormat: '<span style="font-size:15px"><b>{point.key}</b></span><br/>',
                pointFormat: '共{point.y}个，占比{point.percentage:.1f}%'
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        color: '#000000',
                        connectorColor: '#000000',
                        style: {"color": "#606060", "fontSize": "14px"},
                        formatter: function () {
                            return this.y ?
                                ('<span style="font-size:14px"><b>' + this.point.name + '</span></b>: ' + this.y)
                                : null
                        }
                    },
                    startAngle: 0
                }
            },
            series: [{
                type: 'pie',
                name: '数量',
                data: data
            }]
        });

    }).error(function () {
        alert('投标数据获取失败');
        $('#loading-project-type').hide();
    });
}