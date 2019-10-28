var Index = function () {

    var lineMapCache = {};

    // echarts theme
    var theme = "macarons";

    var option = {
        title: [{
            top: '5%',
            left: '10%',
            text: ''
        }, {
            top: '55%',
            left: '10%',
            text: ''
        }],
        tooltip: {
            trigger: 'axis',
            formatter: function (params) {

                console.log(params);
                var time = params[0]["name"];
                var map = {};
                for (var i = 0; i < params.length; i++) {
                    map[params[i]["seriesName"]] = params[i]["data"].toFixed(2);
                }
                var rs = [];
                rs.push("时间：" + time + "<br><br>");
                rs.push("内存占用-DRAM" + "：" + map["DRAM-HMC"] + " MB<br>");
                rs.push("内存占用-NVM" + "：" + map["DRAM-UNI"] + " MB<br>");
                rs.push("DRAM占用百分比" + "：" + (parseFloat(map["DRAM-HMC"]) * 100 / (parseFloat(map["DRAM-HMC"]) + parseFloat(map["DRAM-UNI"]))).toFixed(2) + " % <br><br>");
                rs.push("吞吐量-HMC" + "：" + map["HMCached"] + " MB/s<br>");
                rs.push("吞吐量-UNI" + "：" + map["HM-UNI(Memcached)"] + " MB/s<br>");
                console.log(lineMapCache);
                var TDValue = 0.0, TNValue = 0.0;
                for (var i = 0; i < parseInt(time); i++) {
                    TDValue += lineMapCache["HMCached"][i]["y"];
                    TNValue += lineMapCache["HM-UNI(Memcached)"][i]["y"];
                }
                rs.push("DRAM write traffic 的百分比：" + (TDValue * 100.0 / (TDValue + TNValue)).toFixed(2) + " % <br>");

                return rs.join("");
            }
        },
        axisPointer: {
            link: {xAxisIndex: 'all'}
        },
        legend: [{
            top: '5%',
            left: '30%',
            textStyle: {
                fontSize: 20
            },
            data: []
        }, {
            top: '55%',
            left: '30%',
            textStyle: {
                fontSize: 20
            },
            data: []
        }],
        grid: [{
            top: '15%',
            left: '5%',
            right: '15%',
            bottom: '55%',
            containLabel: true
        }, {
            top: '65%',
            left: '5%',
            right: '15%',
            containLabel: true
        }],
        toolbox: {
            feature: {
                dataZoom: {
                    yAxisIndex: 'none'
                },
                restore: {},
                saveAsImage: {}
            }
        },
        dataZoom: [
            {
                type: 'slider',
                show: true,
                bottom: '0%',
                xAxisIndex: [0, 1]
            },
            {
                type: 'inside',
                xAxisIndex: [0, 1]
            }
        ],
        xAxis: [{
            type: 'category',
            boundaryGap: false,
            name: '请求数',
            nameTextStyle: {
                fontSize: 20
            },
            gridIndex: 0,
            data: []
        }, {
            type: 'category',
            boundaryGap: false,
            name: '请求数',
            nameTextStyle: {
                fontSize: 20
            },
            gridIndex: 1,
            data: []
        }],
        yAxis: [{
            type: 'value',
            name: 'DRAM访问比',
            nameTextStyle: {
                fontSize: 20
            }
            ,
            gridIndex: 0
        }, {
            type: 'value',
            min: 100000;
            interval: 10000;
            name: '吞吐量（MB/s）',
            nameTextStyle: {
                fontSize: 20
            },
            gridIndex: 1
        }],
        series: []
    };

    var lineChart;

    /**
     * 初始化 EChart
     */
    var lineInit = function () {
        lineChart = echarts.init(document.getElementById('line'), theme);
    };

    /**
     * 初始化 WebSocket
     */
    var webSocketInit = function () {
        var host = window.location.host;
        var ws;
        if ('WebSocket' in window) {
            ws = new WebSocket("ws://" + host + "/ws")
        } else {
            ws = new SockJS("http://" + host + "/sockjs/ws");
        }

        //连接打开事件
        ws.onopen = function () {
            console.log("onopen");
        };
        //收到消息事件
        ws.onmessage = function (msg) {
            // 将数据转成json对象
            var json = eval("(" + event.data + ")");
            // 如果信息出错，则直接返回，不作处理
            if (json["code"] !== 100) return;

            var lineMap = json["extend"]["lineMap"];
            lineMapCache = lineMap;

            var xAxisData = [];
            for (var index = 1; index <= lineMap["DRAM-HMC"].length; index++) {
                xAxisData.push(index);
            }
            option.xAxis[0].data = xAxisData;
            option.xAxis[1].data = xAxisData;

            var series = [];
            series.push(getLineData("DRAM-HMC", lineMap["DRAM-HMC"], 0));
            series.push(getLineData("DRAM-UNI", lineMap["DRAM-UNI"], 0));
            series.push(getLineData("HMCached", lineMap["HMCached"], 1));
            series.push(getLineData("HM-UNI(Memcached)", lineMap["HM-UNI(Memcached)"], 1));
            option.series = series;

            option.legend[0].data = ["DRAM-HMC", "DRAM-UNI"];
            option.legend[1].data = ["HMCached", "HM-UNI(Memcached)"];
            // console.log(option);
            lineChart.setOption(option);
        };
        //连接关闭事件
        ws.onclose = function () {
            console.log("onclose");
        };
        //发生了错误事件
        ws.onerror = function () {
            console.log("onerroe");
        };

        //窗口关闭时，关闭连接
        window.unload = function () {
            ws.close();
        };
    };

    var getLineData = function (name, list, axisIndex) {
        var line = {};
        line["name"] = name;
        line["type"] = "line";
        line["data"] = [];
        line["showSymbol"] = false;
        line["hoverAnimation"] = false;
        //if (axisIndex === 1) {
        //    line["stack"] = "吞吐量";
        //    line["areaStyle"] = {};
        //}
        line["xAxisIndex"] = axisIndex;
        line["yAxisIndex"] = axisIndex;
        for (var i = 0; i < list.length; i++) {
            var point = list[i];
            // var pArr = [point["x"], point["y"]];
            line["data"].push(point["y"]);
        }
        return line;
    };

    return {
        init: function () {
            lineInit();
            webSocketInit();
        }
    }
}();

jQuery(document).ready(function () {
    Index.init();
});