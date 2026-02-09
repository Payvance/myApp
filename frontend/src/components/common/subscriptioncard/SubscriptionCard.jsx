// src/components/common/subscriptioncard/SubscriptionCard.jsx
import React from 'react';
import './SubscriptionCard.css';
// imported the css file for the subscription card

// created a subscription card component
const SubscriptionCard = ({
  plan,
  onEdit,
  onBuy,     
  buttonDisabled
}) => {
  // Contains all subscription-related data
  const {
    id,
    name,
    subtitle,
    status,
    price,
    period,
    stats,
    features
  } = plan;

  return (
    <div className="subscription-card">
      {/* ---------- Header Section ---------- */}
      {/* Displays plan name, subtitle, and status badge */}
      <div className="subscription-card-header">
        <div>
          <h3 className="subscription-card-name">{name}</h3>
          <p className="subscription-card-desc">{subtitle}</p>
        </div>
        {/* Status badge (e.g., Active / Inactive) */}
        <span className={`subscription-status-badge ${status.toLowerCase() === 'active' ? 'status-active' : 'status-inactive'}`}>
          {status}
        </span>
      </div>

      {/* ---------- Pricing Section ---------- */}
      {/* Displays price and billing period */}
      <div className="subscription-card-pricing">
        <div className="subscription-pricing-info">
          <h2 className="subscription-card-price">₹{price}</h2>
          <p className="subscription-card-period">{period}</p>
        </div>

        {/* ---------- Stats Section ---------- */}
        {/* Displays key metrics like code */}
        <div className="subscription-card-stats">
          <div className="subscription-stat-item">
            <span className="subscription-stat-label">CODE</span>
            <span className="subscription-stat-value">{stats.code}</span>
          </div>
        </div>
      </div>

      {/* ---------- Features Section ---------- */}
      {/* Displays list of features */}
      <div className="subscription-card-features">
        <span className="subscription-features-label">FEATURES</span>
        <ul className="subscription-features-list">
          {features.map((feature, idx) => (
            <li key={idx} className="subscription-feature-item">
              <i className="bi bi-check2"></i> {feature}
            </li>
          ))}
        </ul>
      </div>

      {/* ---------- Edit Button ---------- */}
      {/* Allows editing of the plan */}
      <button
        className="subscription-edit-btn"
        disabled={buttonDisabled}
        onClick={() => {if (onBuy) onBuy(plan);
          else if (onEdit) onEdit(plan);}}
      >
       {onBuy ? "Buy Now" : "Edit Plan"}
      </button>
    </div>
    // End of SubscriptionCard
  );
};

export default SubscriptionCard;