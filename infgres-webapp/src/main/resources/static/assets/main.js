$(document).ready(function () {
    $('select').material_select();
});

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
};

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

function clearDbs(full) {
    $.get("http://localhost:9011/api/clearDbs?full=" + full, function (e) {
        console.log(e);
    });
}

function loadNodesAndGroups() {
    $.get("http://localhost:9011/api/nodes", function (nodes) {
        var select = document.getElementById("nodes_select");
        select.innerHTML = "";

        var options = "<option disabled>Select Node(s)</option>";
        for (var i = 0; i < nodes.length; i++) {
            options += "<option value='" + nodes[i].id + "' title='" + nodes[i].id + "'>" + nodes[i].name + "</option>";
        }
        select.innerHTML = options;
        $(select).material_select();
    });

    $.get("http://localhost:9011/api/groups", function (groups) {
        var select = document.getElementById("groups_select");
        select.innerHTML = "";

        var options = "<option disabled>Select Group(s)</option>";
        for (var i = 0; i < groups.length; i++) {
            options += "<option value='" + groups[i].id + "' title='" + groups[i].id + "'>" + groups[i].name + "</option>";
        }
        select.innerHTML = options;
        $(select).material_select();
    });
}


function report() {
    var options;
    options = document.getElementById("nodes_select").options;
    var reportRequest = {};
    reportRequest.startTime = document.getElementById("startTime").value;
    reportRequest.endTime = document.getElementById("endTime").value;
    reportRequest.nodeIds = [];
    reportRequest.groupIds = [];

    for (var i = 0, iLen = options.length; i < iLen; i++) {
        var opt = options[i];

        if (opt.selected) {
            reportRequest.nodeIds.push(opt.value || opt.text);
        }
    }

    options = document.getElementById("groups_select").options;

    for (var i = 0, iLen = options.length; i < iLen; i++) {
        var opt = options[i];

        if (opt.selected) {
            reportRequest.groupIds.push(opt.value || opt.text);
        }
    }

    var influx = $('input[name="dbInflux"]:checked').val();

    $.ajax({
        type: 'POST',
        url: 'http://localhost:9011/api/report',
        data: JSON.stringify(reportRequest), // or JSON.stringify ({name: 'jonas'}),
        success: function (rr) {
            console.log(rr);
            $('#json-renderer').jsonViewer(rr, {collapsed: true});
            reportDuration(influx);
        },
        contentType: "application/json",
        dataType: 'json'
    });

    /*$.post("http://localhost:9011/api/report", JSON.stringify(reportRequest), function (rr) {
     console.log(rr);
     reportDuration(influx);
     }, "json");*/

}

function reportDuration(influx) {

    var index = influx == 'true' ? 0 : 1;

    $.get("http://localhost:9011/api/reportDuration", function (time) {
        console.log(time);
        chart.options.data[index].dataPoints.push({label: 'Report', y: time});
        chart.render();
    });

}