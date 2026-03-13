import React from "react";
import Highcharts from "highcharts";
import HighchartsReact from "highcharts-react-official";
import "./CardHeader.css";
import "./PieChartComponent.css";

const PieChartComponent = ({ title, data, loading }) => {
  if (loading) return <div className="chart-loader">Loading...</div>;

  const options = {
    chart: {
      type: "pie",
      backgroundColor: "transparent",
      height: 200
    },
    title: {
      text: null
    },
    plotOptions: {
      pie: {
        innerSize: "65%",
        size: "100%",
        showInLegend: true,
        borderWidth: 0,
        dataLabels: {
          enabled: true,
          style: { fontSize: "11px" },
          format: "{point.name}",
          color: "var(--text-secondary)"

        }
      }
    },
    tooltip: {
      pointFormat: "<b>{point.percentage:.1f}%</b>"
    },
    legend: {
    align: "center",
    verticalAlign: "bottom",
    itemStyle: {
      fontSize: "11px",
      color: "var(--text-secondary)"
    }
  },
    credits: { enabled: false },
    series: [
      {
        name: "Count",
        data: data?.map(item => ({
          name: item.name,
          y: Number(item.value || item.y)
        }))
      }
    ]
  };

  return (
    <div className="pie-chart-card">
      <div className="card-header">
        <h3 className="card-title">{title}</h3>
      </div>
      <HighchartsReact highcharts={Highcharts} options={options} />
    </div>
  );
};

export default PieChartComponent;