<!DOCTYPE html>

<html class="gr__chartjs_org"><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Bar Chart</title>
    
    
    out.print("<script src="http://www.chartjs.org/dist/2.7.1/Chart.bundle.js">
    out.print("</script><style type="text/css">
    
    out.print("@-webkit-keyframes chartjs-render-animation{from{opacity:0.99}to{opacity:1}}@keyframes chartjs-render-animation{from{opacity:0.99}to{opacity:1}}.chartjs-render-monitor{-webkit-animation:chartjs-render-animation 0.001s;animation:chartjs-render-animation 0.001s;}</style>
    out.print("<script src="http://www.chartjs.org/samples/latest/utils.js"></script>
    out.print("<style>
    out.print("canvas {
        out.print("    -moz-user-select: none;
        out.print("    -webkit-user-select: none;
        out.print("    -ms-user-select: none;
        out.print("}
        out.print("</style>
        out.print("</head>

        out.print("<body data-gr-c-s-loaded="true">
        out.print(" <div id="container" style="width: 75%;"><div class="chartjs-size-monitor" style="position: absolute; left: 0px; top: 0px; right: 0px; bottom: 0px; overflow: hidden; pointer-events: none; visibility: hidden; z-index: -1;"><div class="chartjs-size-monitor-expand" style="position:absolute;left:0;top:0;right:0;bottom:0;overflow:hidden;pointer-events:none;visibility:hidden;z-index:-1;"><div style="position:absolute;width:1000000px;height:1000000px;left:0;top:0"></div></div><div class="chartjs-size-monitor-shrink" style="position:absolute;left:0;top:0;right:0;bottom:0;overflow:hidden;pointer-events:none;visibility:hidden;z-index:-1;"><div style="position:absolute;width:200%;height:200%;left:0; top:0"></div></div></div>
        out.print("<canvas id="canvas" width="1062" height="531" class="chartjs-render-monitor" style="display: block; width: 1062px; height: 531px;"></canvas>
        out.print("</div>
        out.print("<script>
        out.print("var color = Chart.helpers.color;
        out.print("var barChartData = {
            out.print("labels: [
                out.print(""January",
                out.print(""February",
                out.print(""March",
                out.print(""April",
                out.print(""May",
                out.print(""June",
                out.print(""July",
                out.print(" ],
                out.print("datasets: [{
                    out.print(" label: 'Book Name',
                    out.print("backgroundColor: color(window.chartColors.blue).alpha(0.5).rgbString(),
                    out.print(" borderColor: window.chartColors.red,
                    out.print("borderWidth: 1,
                    out.print("data: [
                        out.print("40,
                        out.print("10,
                        out.print("90,
                        out.print("2,
                        out.print("100,
                        out.print("30,
                        out.print("99,
                        out.print("]
                        out.print("}]

                        out.print("};

                        out.print("window.onload = function() {
                            out.print("var ctx = document.getElementById("canvas").getContext("2d");
                            out.print("window.myBar = new Chart(ctx, {
                                out.print("type: 'bar',
                                out.print("data: barChartData,
                                out.print("options: {
                                    out.print("responsive: true,
                                    out.print("legend: {
                                        out.print("position: 'top',
                                        out.print("},
                                        out.print("title: {
                                            out.print("display: true,
                                            out.print("text: 'Compare'
                                            out.print("}
                                            out.print("}
                                            out.print("});

                                            out.print("};
                                            out.print("</script>



</body></html>