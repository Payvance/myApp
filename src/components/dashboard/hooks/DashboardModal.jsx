import React from 'react';
import './DashboardModal.css';

const DashboardModal = ({ show, onClose, data }) => {

  if (!show || !data) return null;

  return (
    <div className="dashboard-modal-overlay">
      <div className="dashboard-modal">
        
        <div className="dashboard-modal-header">
          <h3>{data.title} Details</h3>
          <button className="close-btn" onClick={onClose}>×</button>
        </div>

        <div className="dashboard-modal-body">
          {/* Show main value */}
          <div className="modal-row main-value-row">
            <span className="modal-key">Value</span>
            <span className="modal-value">{data.value}</span>
          </div>
          {/* Show trend if present */}
          {data.trend && (
            <div className="modal-row">
              <span className="modal-key">Trend</span>
              <span className="modal-value">{data.trend}</span>
            </div>
          )}
          {/* Show percentage if present */}
          {data.percentage && (
            <div className="modal-row">
              <span className="modal-key">Percentage</span>
              <span className="modal-value">{data.percentage}</span>
            </div>
          )}
          {/* Show lastUpdated if present */}
          {data.lastUpdated && (
            <div className="modal-row">
              <span className="modal-key">Last Updated</span>
              <span className="modal-value">{data.lastUpdated}</span>
            </div>
          )}
          {/* Show other details if needed */}
        </div>

      </div>
    </div>
  );
};

export default DashboardModal;