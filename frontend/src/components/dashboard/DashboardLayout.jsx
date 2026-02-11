import React from 'react';
import './DashboardLayout.css';

const DashboardLayout = ({ children, loading }) => {
  return (
    <div className="dashboard-layout">
        {loading ? (
          <div className="dashboard__loading">
            <div className="dashboard__loading-spinner"></div>
            <p>Loading dashboard...</p>
          </div>
        ) : (
          <div className="dashboard__content">
            {children}
          </div>
        )}
    </div>
  );
};

export default DashboardLayout;
