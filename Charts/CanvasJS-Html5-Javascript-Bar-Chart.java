

	
out.print("<script>");
out.print("window.onload = function () {");
	
	out.print("var chart = new CanvasJS.Chart("chartContainer", {");
		out.print("animationEnabled: true,");
	
		out.print("title:{");
			out.print("text:"Fortune 500 Companies by Country"");
			out.print("},");
			out.print("axisX:{");
				out.print("interval: 1");
				out.print("},");
				out.print("axisY2:{");
					out.print("interlacedColor: "rgba(1,77,101,.2)",");
					out.print("gridColor: "rgba(1,77,101,.1)",");
					out.print("title: "Number of Companies"");
					out.print("},");
					out.print("data: [{");
						out.print("type: "bar",");
						out.print("name: "companies",");
						out.print("axisYType: "secondary",");
						out.print("color: "#014D65",");
						out.print("dataPoints: [");
							out.print("{ y: 3, label: "Sweden" },");
							out.print("{ y: 7, label: "Taiwan" },");
							out.print("{ y: 5, label: "Russia" },");
							out.print("{ y: 9, label: "Spain" },");
							out.print("{ y: 7, label: "Brazil" },");
							out.print("{ y: 7, label: "India" },");
							out.print("{ y: 9, label: "Italy" },");
							out.print("{ y: 8, label: "Australia" },");
							out.print("{ y: 11, label: "Canada" },");
							out.print("{ y: 15, label: "South Korea" },");
							out.print("{ y: 12, label: "Netherlands" },");
							out.print("{ y: 15, label: "Switzerland" },");
							out.print("{ y: 25, label: "Britain" },");
							out.print("{ y: 28, label: "Germany" },");
							out.print("{ y: 29, label: "France" },");
							out.print("{ y: 52, label: "Japan" },");
							out.print("{ y: 103, label: "China" },");
							out.print("{ y: 134, label: "US" }");
							out.print("]");
							out.print("}]");
							out.print("});");
							out.print("chart.render();");

							out.print("}");
							out.print("</script>");



out.print("<div id="chartContainer" style="height: 370px; max-width: 920px; margin: 0px auto;"></div>");
out.print("<script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>");
