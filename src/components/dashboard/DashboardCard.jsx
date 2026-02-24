import React from 'react';
import { useNavigate } from 'react-router-dom';
import './DashboardCard.css';

const DashboardCard = ({
  title,
  value,
  icon,
  color = 'primary',
  size = 'medium',
  width = 'auto',
  onCardClick,
  ...rest
}) => {
  const navigate = useNavigate();

  const handleCardClick = (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (onCardClick) {
      onCardClick({ title, value, icon, color, ...rest });
      return;
    }

    // Navigate based on card title - using actual routes from App.jsx
    if (title) {
      const titleLower = title.toLowerCase();
      
      if (titleLower === 'users created') {
        navigate('/usermanagement');
      } else if (titleLower === 'companies') {
        navigate('/plansmanagement');
      } else if (titleLower === 'active plan') {
        navigate('/plansmanagement');
      } else if (titleLower === 'active vs inactive users') {
        navigate('/usermanagement'); // User management with active/inactive filter
      } else if (titleLower === 'active users') {
        navigate('/usermanagement'); // User management with active filter
      } else if (titleLower === 'pending requests') {
        navigate('/tenantrequests'); // Using existing route
      }
    }
  };

  return (
    <div 
      className={`dashboard-card dashboard-card--${color} dashboard-card--${size} dashboard-card--clickable`}
      style={{ width }}
      onClick={handleCardClick}
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
