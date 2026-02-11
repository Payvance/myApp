import React from 'react';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from 'chart.js';
import { Bar } from 'react-chartjs-2';
import './BarChartComponent.css';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const BarChartComponent = ({ title, data, xAxis, yAxis, size = 'medium', loading }) => {
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
      <h3 className="bar-chart__title">{title}</h3>
      <div className="bar-chart__container">
        <Bar data={chartData} options={options} />
      </div>
    </div>
  );
};

export default BarChartComponent;
