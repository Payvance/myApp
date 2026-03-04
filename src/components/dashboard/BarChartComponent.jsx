import React, { useState } from "react";
import Highcharts from "highcharts";
import HighchartsReact from "highcharts-react-official";
import "./BarChartComponent.css";
import "./CardHeader.css";

const BarChartComponent = ({
  title,
  data = [],
  xAxis,
  yAxis,
  stacked = false,
  size = "medium",
  loading,
  onYearChange
}) => {

  // Check if dark mode is active
  const isDarkMode = document.documentElement.classList.contains('dark') || 
                    window.matchMedia('(prefers-color-scheme: dark)').matches;

  // Base year (start of financial tracking)
  const baseYear = 2025;

  // Look for the currently viewed year in the Title string (e.g. "FY 2025-26")
  let currentStartYear = baseYear;
  if (title) {
    const match = title.match(/(\d{4})/);
    if (match) currentStartYear = parseInt(match[1]);
  }

  const [selectedPair, setSelectedPair] = useState(`${currentStartYear}-${currentStartYear + 1}`);

  // Sync state if title explicitly changes from outside loading
  React.useEffect(() => {
    if (title) {
      const match = title.match(/(\d{4})/);
      if (match) {
        setSelectedPair(`${parseInt(match[1])}-${parseInt(match[1]) + 1}`);
      }
    }
  }, [title]);

  // Generate FIXED year pairs so they don't disappear on change
  const yearPairs = [];
  for (let i = 0; i <= 10; i++) {
    const startYear = baseYear + i;
    const endYear = startYear + 1;
    yearPairs.push(`${startYear}-${endYear}`);
  }

  const handleYearChange = (e) => {
    const pair = e.target.value;
    setSelectedPair(pair);

    const [startYear, endYear] = pair.split("-").map(Number);

    if (onYearChange) {
      onYearChange({ startYear, endYear });
    }
  };

  if (loading) {
    return (
      <div className={`bar-chart-card bar-chart-card--${size}`}>
        <div className="bar-chart__skeleton-title"></div>
        <div className="bar-chart__skeleton-chart"></div>
      </div>
    );
  }

  const categories = data.map(item => item[xAxis]);

  // Detect series automatically if yAxis is not provided
  let series = [];
  if (yAxis) {
    series = [{
      name: title || "Data",
      data: data.map(item => Number(item[yAxis]))
    }];
  } else if (data.length > 0) {
    const keys = Object.keys(data[0]).filter(k => k !== xAxis && k !== 'stacked');
    series = keys.map(key => ({
      name: key || "Data",
      data: data.map(item => Number(item[key]))
    }));
  }

  const options = {
    chart: {
      type: "column",
      height: 200,
      style: {
        fontFamily: "Inter, sans-serif",
        color: isDarkMode ? "#ffffff" : "#2c3e50"
      },
      backgroundColor: "transparent"
    },

    title: {
      text: null,
      style: {
        color: isDarkMode ? "#ffffff" : "#2c3e50"
      }
    },

    xAxis: {
      categories,
      labels: {
        style: {
          color: isDarkMode ? "#9ca3af" : "#6c757d"
        }
      },
      lineColor: isDarkMode ? "#374151" : "transparent",
      tickColor: isDarkMode ? "#374151" : "transparent"
    },

    yAxis: {
      min: 0,
      title: { text: null },
      labels: {
        style: {
          color: isDarkMode ? "#9ca3af" : "#6c757d"
        }
      },
      gridLineColor: isDarkMode ? "#374151" : "#e5e7eb",
      lineColor: isDarkMode ? "#374151" : "#e5e7eb"
    },

    plotOptions: {
      column: {
        stacking: stacked ? 'normal' : undefined,
        borderRadius: 4,
        dataLabels: {
          enabled: false,
          style: {
            color: isDarkMode ? "#ffffff" : "#2c3e50"
          }
        }
      }
    },

    tooltip: {
      shared: true,
      headerFormat: '<b>{point.x}</b><br/>',
      pointFormat: '{series.name}: ₹{point.y}<br/>',
      footerFormat: stacked ? 'Total: ₹{point.total}' : undefined,
      backgroundColor: isDarkMode ? "rgba(31, 41, 55, 0.95)" : "rgba(255, 255, 255, 0.95)",
      borderColor: isDarkMode ? "#374151" : "#e5e7eb",
      borderRadius: 8,
      style: {
        color: isDarkMode ? "#ffffff" : "#2c3e50",
        fontSize: "12px"
      }
    },

    legend: {
      enabled: false // Hide legend
    },

    series: series
  };

  return (
    <div className={`bar-chart-card bar-chart-card--${size}`}>
      <div className="card-header">
        <h3 className="card-title">{title}</h3>

        {onYearChange && (
          <div className="card-header__trail">
            <select
              className="bar-chart__year-select"
              value={selectedPair}
              onChange={handleYearChange}
            >
              {yearPairs.map(pair => (
                <option key={pair} value={pair}>
                  FY {pair}
                </option>
              ))}
            </select>
          </div>
        )}
      </div>

      <div className="bar-chart__container">
        <HighchartsReact highcharts={Highcharts} options={options} />
      </div>
    </div>
  );
};

export default BarChartComponent;