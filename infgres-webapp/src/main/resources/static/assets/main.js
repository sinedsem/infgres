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
    $.get("http://localhost:9011/pusher/generate", function (e) {

    });
}

function push() {

    var influx = $('input[name="dbInflux"]:checked').val();

    var index = influx == 'true' ? 0 : 1;

    $.get("http://localhost:9011/pusher/push", function (time) {
        console.log(time);
        chart.options.data[index].dataPoints.push({label: 'Push', y: time});
        chart.render();
    });

}

function setDb() {
    var influx = $('input[name="dbInflux"]:checked').val();
    $.get("http://localhost:9011/pusher/setDb?influx=" + influx, function (e) {
        console.log(e);
    });
}