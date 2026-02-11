import React from 'react';
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js';
import { Pie } from 'react-chartjs-2';
import './PieChartComponent.css';

ChartJS.register(ArcElement, Tooltip, Legend);

// Color palette for pie charts
const COLOR_PALETTE = [
  '#667eea', // Primary blue
  '#4ecdc4', // Teal
  '#ffa726', // Orange
  '#ff6b6b', // Red
  '#48bb78', // Green
  '#9f7aea', // Purple
  '#f6ad55', // Yellow
  '#fc8181', // Pink
  '#63b3ed', // Light blue
  '#68d391', // Light green
];

const PieChartComponent = ({ title, data, size = 'medium', loading }) => {
  if (loading) {
    return (
      <div className={`pie-chart-card pie-chart-card--${size}`}>
        <div className="pie-chart__skeleton-title"></div>
        <div className="pie-chart__skeleton-chart"></div>
      </div>
    );
  }

  const chartData = {
    labels: data.map(item => item.name),
    datasets: [
      {
        data: data.map(item => item.value),
        backgroundColor: data.map((_, index) => COLOR_PALETTE[index % COLOR_PALETTE.length]),
        borderColor: '#ffffff',
        borderWidth: 2,
        hoverOffset: 4,
        cutout: '40%'  // Creates donut chart with 60% hollow center
      }
    ]
  };

  const options = {
    responsive: true,
    maintainAspectRatio: true,
    plugins: {
      legend: {
        position: 'right',
        labels: {
          padding: 5,
          usePointStyle: true,
          pointStyle: 'circle',
          font: {
            size: 8
          },
          generateLabels: function(chart) {
            const data = chart.data;
            if (data.labels.length && data.datasets.length) {
              const dataset = data.datasets[0];
              return data.labels.map((label, i) => {
                const value = dataset.data[i];
                const total = dataset.data.reduce((a, b) => a + b, 0);
                const percentage = ((value / total) * 100).toFixed(1);
                
                return {
                  text: `${label}`,
                  fillStyle: dataset.backgroundColor[i],
                  hidden: false,
                  index: i,
                  pointStyle: 'circle'
                };
              });
            }
            return [];
          }
        }
      },
      tooltip: {
        callbacks: {
          label: function(context) {
            const label = context.label || '';
            const value = context.parsed;
            const total = context.dataset.data.reduce((a, b) => a + b, 0);
            const percentage = ((value / total) * 100).toFixed(1);
            return `${label}: ${value} (${percentage}%)`;
          }
        }
      }
    }
  };

  return (
    <div className={`pie-chart-card pie-chart-card--${size}`}>
      <h3 className="pie-chart__title">{title}</h3>
      <div className="pie-chart__container">
        <Pie data={chartData} options={options} />
      </div>
    </div>
  );
};

export default PieChartComponent;
