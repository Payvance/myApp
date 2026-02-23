import React, { useState } from 'react';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from 'chart.js';
import { Bar } from 'react-chartjs-2';
import './BarChartComponent.css';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const BarChartComponent = ({ title, data, xAxis, yAxis, size = 'medium', loading, onYearChange }) => {
  const currentYear = new Date().getFullYear();
  const defaultPair = `${currentYear}-${currentYear + 1}`;
  const [selectedPair, setSelectedPair] = useState(defaultPair); // Default: 2026-2027
  
  // Generate year pairs (current year + next 10 years)
  const yearPairs = [];
  for (let i = 0; i <= 10; i++) {
    const startYear = currentYear + i;
    const endYear = startYear + 1;
    yearPairs.push(`${startYear}-${endYear}`);
  }

  const handleYearChange = (e) => {
    const pair = e.target.value;
    setSelectedPair(pair);
    
    // Parse the pair to get start and end years
    const [startYear, endYear] = pair.split('-').map(year => parseInt(year));
    
    if (onYearChange) {
      onYearChange({
        startYear,
        endYear
      });
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

  const chartData = {
    labels: data.map(item => item[xAxis]),
    datasets: [
      {
        label: title,
        data: data.map(item => item[yAxis]),
        backgroundColor: 'rgba(0, 145, 255, 0.8)',
        borderColor: 'rgba(232, 251, 255, 1)',
        borderWidth: 2,
        borderRadius: 8,
        hoverBackgroundColor: 'rgba(251, 255, 220, 1)',
      }
    ]
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: false
      },
      tooltip: {
        backgroundColor: 'rgba(0, 0, 0, 0.8)',
        padding: 12,
        cornerRadius: 8,
        titleFont: {
          size: 14,
          weight: 'bold'
        },
        bodyFont: {
          size: 13
        },
        callbacks: {
          label: function(context) {
            const label = context.dataset.label || '';
            const value = context.parsed.y;
            return `${label}: ${value}`;
          }
        }
      }
    },
    scales: {
      x: {
        grid: {
          display: false
        },
        ticks: {
          font: {
            size: 12
          },
          color: '#6c757d'
        }
      },
      y: {
        beginAtZero: true,
        grid: {
          borderDash: [5, 5],
          color: 'rgba(0, 0, 0, 0.05)'
        },
        ticks: {
          font: {
            size: 12
          },
          color: '#6c757d'
        }
      }
    }
  };

  return (
    <div className={`bar-chart-card bar-chart-card--${size}`}>
      <div className="bar-chart__header">
        <h3 className="bar-chart__title">{title}</h3>
        <div className="bar-chart__year-selector">
          <label htmlFor={`year-pair-${title}`}>Year Range:</label>
          <select
            id={`year-pair-${title}`}
            value={selectedPair}
            onChange={handleYearChange}
            className="bar-chart__year-select"
          >
            {yearPairs.map(pair => (
              <option key={pair} value={pair}>
                {pair}
              </option>
            ))}
          </select>
        </div>
      </div>
      <div className="bar-chart__container">
        <Bar data={chartData} options={options} />
      </div>
    </div>
  );
};

export default BarChartComponent;
