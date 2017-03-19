window.onload = function () {
    var chart = new CanvasJS.Chart("chartContainer",
        {
            theme: "theme2",
            animationEnabled: true,
            title: {
                text: "Crude Oil Reserves Vs Production, 2011",
                fontSize: 30
            },
            toolTip: {
                shared: true
            },
            axisY: {
                title: "billion of barrels"
            },
            axisY2: {
                title: "million barrels/day"
            },
            data: [
                {
                    type: "column",
                    name: "Proven Oil Reserves (bn)",
                    legendText: "Proven Oil Reserves",
                    showInLegend: true,
                    dataPoints: [
                        {label: "Saudi", y: 262},
                        {label: "Venezuela", y: 211},
                        {label: "Canada", y: 175},
                        {label: "Iran", y: 137},
                        {label: "Iraq", y: 115},
                        {label: "Kuwait", y: 104},
                        {label: "UAE", y: 97.8},
                        {label: "Russia", y: 60},
                        {label: "US", y: 23.3},
                        {label: "China", y: 20.4}


                    ]
                },
                {
                    type: "column",
                    name: "Oil Production (million/day)",
                    legendText: "Oil Production",
                    axisYType: "secondary",
                    showInLegend: true,
                    dataPoints: [
                        {label: "Saudi", y: 11.15},
                        {label: "Venezuela", y: 2.5},
                        {label: "Canada", y: 3.6},
                        {label: "Iran", y: 4.2},
                        {label: "Iraq", y: 2.6},
                        {label: "Kuwait", y: 2.7},
                        {label: "UAE", y: 3.1},
                        {label: "Russia", y: 10.23},
                        {label: "US", y: 10.3},
                        {label: "China", y: 4.3}


                    ]
                }

            ],
            legend: {
                cursor: "pointer",
                itemclick: function (e) {
                    if (typeof(e.dataSeries.visible) === "undefined" || e.dataSeries.visible) {
                        e.dataSeries.visible = false;
                    }
                    else {
                        e.dataSeries.visible = true;
                    }
                    chart.render();
                }
            },
        });


    chart.render();

    chart.options.data = [
        {
            name: "Influx",
            legendText: "Influx",
            showInLegend: true,
            dataPoints: [
                {label: "hello", y: 262},
            ]
        },
        {
            name: "Postgres",
            legendText: "Postgres",
            showInLegend: true,
            dataPoints: [
                {y: 11.15},
            ]
        }

    ];


    chart.render();
}

function test(){
    $.get("http://localhost:9011/pusher/generate",function (e) {
        console.log(e);
    })
}