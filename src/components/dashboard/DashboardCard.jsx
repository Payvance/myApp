import React from 'react';
import './DashboardCard.css';

const DashboardCard = ({
  title,
  value,
  icon,
  color = 'primary',
  size = 'medium',
  width = 'auto'
}) => {
  return (
    <div 
      className={`dashboard-card dashboard-card--${color} dashboard-card--${size}`}
      style={{ width }}
    >
      <div className="dashboard-card__icon">
        <i className={`bi ${icon}`}></i>
      </div>
      <div className="dashboard-card__content">
        <h3 className="dashboard-card__title">{title}</h3>
        <p className="dashboard-card__value">{value}</p>
      </div>
    </div>
  );
};

export default DashboardCard;
