import React, { useState } from "react";
import Highcharts from "highcharts";
import HighchartsReact from "highcharts-react-official";
import "./BarChartComponent.css";

const BarChartComponent = ({
  title,
  data = [],
  xAxis,
  yAxis,
  size = "medium",
  loading,
  onYearChange
}) => {

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
  const values = data.map(item => Number(item[yAxis]));

  const options = {
    chart: {
      type: "column",
      height: 200
    },

    title: {
      text: null
    },

    xAxis: {
      categories
    },

    yAxis: {
      min: 0,
      title: { text: null }
    },

    plotOptions: {
      column: {
        borderRadius: 8,
        dataLabels: {
          enabled: true
        }
      }
    },

    tooltip: {
      formatter: function () {
        return `<b>${this.x}</b><br/>${title}: ${this.y}`;
      }
    },

    legend: { enabled: false },

    series: [
      {
        name: title,
        data: values
      }
    ]
  };

  return (
    <div className={`bar-chart-card bar-chart-card--${size}`}>
      <div className="bar-chart__header">
        <h3 className="bar-chart__title">{title}</h3>

        {onYearChange && (
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
        )}
      </div>

      <div className="bar-chart__container">
        <HighchartsReact highcharts={Highcharts} options={options} />
      </div>
    </div>
  );
};

export default BarChartComponent;