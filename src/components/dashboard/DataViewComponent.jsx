import React from 'react';
import './DataViewComponent.css';

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

  return (
    <div className="data-view-card">
      <h3 className="data-view__title">{title}</h3>
      <div className="data-view__content">
        {data.map((item, index) => (
          <div key={index} className="data-view__row">
            <span className="data-view__name">{item.name}</span>
            <span className="data-view__value">{item.value}</span>
          </div>
        ))}
      </div>
    </div>
  );
};

export default DataViewComponent;
