var chart;

window.onload = function () {
    chart = new CanvasJS.Chart("chartContainer",
        {
            theme: "theme2",
            animationEnabled: true,
            title: {
                text: "Time spent to process request",
                fontSize: 30
            },
            toolTip: {
                shared: true
            },
            axisY: {
                title: "Milliseconds"
            }
        });


    chart.render();

    chart.options.data = [
        {
            name: "Influx",
            legendText: "Influx",
            showInLegend: true,
            dataPoints: []
        },
        {
            name: "Postgres",
            legendText: "Postgres",
            showInLegend: true,
            dataPoints: []
        }

    ];


    chart.render();
}

function generate() {
    $.get("http://localhost:9011/api/generate", function (e) {

    });
}

function push() {

    var influx = $('input[name="dbInflux"]:checked').val();

    var index = influx == 'true' ? 0 : 1;

    $.get("http://localhost:9011/api/push", function (time) {
        console.log(time);
        chart.options.data[index].dataPoints.push({label: 'Push', y: time});
        chart.render();
    });

}

function setDb() {
    var influx = $('input[name="dbInflux"]:checked').val();
    $.get("http://localhost:9011/api/setDb?influx=" + influx, function (e) {
        console.log(e);
    });
}


function report() {

    var influx = $('input[name="dbInflux"]:checked').val();

    $.get("http://localhost:9011/api/report", function (rr) {
        console.log(rr);
        reportDuration(influx);
    });

}

function reportDuration(influx) {

    var index = influx == 'true' ? 0 : 1;

    $.get("http://localhost:9011/api/reportDuration", function (time) {
        console.log(time);
        chart.options.data[index].dataPoints.push({label: 'Report', y: time});
        chart.render();
    });

}