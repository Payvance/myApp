import React, { useEffect, useState } from 'react';
import { formatDateStandardSpace } from '../../utils/dateUtils';
import './DataViewComponent.css';
import './CardHeader.css';
import PopUp from '../common/popups/PopUp';
import Button from '../common/button/Button';
import { Navigate, useNavigate } from 'react-router-dom';

const DataViewComponent = ({ title, data, loading, isTenant  }) => {
  const [showInactivePopup, setShowInactivePopup] = useState(false);
  const navigate = useNavigate(); 

  useEffect(() => {
  if (isTenant && title === "Plan Details") {
    const statusItem = data?.find(item => item.name === "Status");
    const alreadySeen = sessionStorage.getItem("plan_required_seen"); // ← sessionStorage
    if (statusItem?.value === "Inactive" && !alreadySeen) {
      setShowInactivePopup(true);
    }
  }
}, [data, title, isTenant]);
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

      <PopUp
  isOpen={showInactivePopup}
  onClose={() => setShowInactivePopup(false)}
  title=""
  size="small"
>
  <div className="plan-required-popup">

    <div className="prp__icon-wrap">
      <i className="bi bi-stars" />
    </div>

    <h2 className="prp__title">Welcome aboard!</h2>
    <p className="prp__subtitle">
      You're one step away from unlocking everything. Activate a plan or start a free trial to get going.
    </p>

    <div className="prp__features">
      <div className="prp__feature">
        <i className="bi bi-check-circle-fill" />
        <span>Get access to mobile app</span>
      </div>
      <div className="prp__feature">
        <i className="bi bi-check-circle-fill" />
        <span>Manage users & companies</span>
      </div>
    </div>

    <div className="prp__actions">
      <button
        className="prp__btn prp__btn--primary"
        onClick={() => {
          sessionStorage.setItem("plan_required_seen", "true");
          setShowInactivePopup(false);
          navigate("/tenantplanss");
        }}
      >
        <i className="bi bi-bag-check" /> View Plans
      </button>
      <button
        className="prp__btn prp__btn--ghost"
        onClick={() => setShowInactivePopup(false)}
      >
        Maybe later
      </button>
    </div>

  </div>
</PopUp>
    </div>
  );
};

export default DataViewComponent;
