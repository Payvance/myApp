import React from "react";
import Highcharts from "highcharts";
import HighchartsReact from "highcharts-react-official";

const PieChartComponent = ({ title, data, loading }) => {
  if (loading) return <div className="chart-loader">Loading...</div>;

  const options = {
    chart: {
      type: "pie",
      backgroundColor: "transparent",
      height: 240  
    },
    title: {
      text: title,
      align: "left",
      verticalAlign: "top",
      x: 0,
      y: 10,
      style: { fontSize: "14px", fontWeight: 550 }
    },
    plotOptions: {
      pie: {
        innerSize: "65%",
        size: "100%",   // 🔥 reduce pie size
        showInLegend: true, 
        borderWidth: 0,
        dataLabels: {
          enabled: true,
          style: { fontSize: "11px" },
          format: "{point.name}"
        }
      }
    },
    tooltip: {
      pointFormat: "<b>{point.percentage:.1f}%</b>"
    },
    legend: {
      align: "center",
      verticalAlign: "bottom",
      itemStyle: { fontSize: "11px" }
    },
    credits: { enabled: false },
    series: [
      {
        name: "Users",
        data: data?.map(item => ({
          name: item.name,
          y: Number(item.value || item.y)
        }))
      }
    ]
  };

  return <HighchartsReact highcharts={Highcharts} options={options} />;
};

export default PieChartComponent;