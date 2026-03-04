import React from 'react';
import { formatDateStandardSpace } from '../../utils/dateUtils';
import './DataViewComponent.css';
import './CardHeader.css';

const DataViewComponent = ({ title, data, loading }) => {
  if (loading) {
    return (
      <div className="data-view-card">
        <div className="data-view__skeleton-title"></div>
        <div className="data-view__skeleton-content">
          {[...Array(5)].map((_, index) => (
            <div key={index} className="data-view__skeleton-row">
              <div className="data-view__skeleton-name"></div>
              <div className="data-view__skeleton-value"></div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  // Conditional rendering for Top 5 Tenants by Revenue (vendor dashboard)
  const isTopTenants = title === "Top 5 Tenants by Revenue" && Array.isArray(data) && data.length > 0 && data[0].tenantName && data[0].revenue;

  // Check if this is plan details section
  const isPlanDetails = title === "Plan Details";

  // Format date values for plan details
  const formatValue = (name, value) => {
    if (isPlanDetails && (name === "Start Date" || name === "End Date")) {
      return value && value !== "--" ? formatDateStandardSpace(value) : value;
    }
    return value;
  };

  return (
    <div className="data-view-card">
      <div className="card-header">
        <h3 className="card-title">{title}</h3>
      </div>
      <div className="data-view__content">
        {isTopTenants ? (
          data.map((item, index) => (
            <div key={index} className="data-view__row">
              <span className="data-view__name">{item.tenantName}</span>
              <span className="data-view__value">₹{item.revenue.toLocaleString()}</span>
            </div>
          ))
        ) : (
          data.map((item, index) => (
            <div key={index} className="data-view__row">
              <span className="data-view__name">{item.name}</span>
              <span className="data-view__value">{formatValue(item.name, item.value)}</span>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default DataViewComponent;
