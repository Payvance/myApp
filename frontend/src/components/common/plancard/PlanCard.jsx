// src/components/common/plancard/PlanCard.jsx
import React from 'react';
import '../subscriptioncard/SubscriptionCard.css';

const PlanCard = ({ plan, onBuy, buttonText = "Try Now", buttonDisabled = false }) => {
  const { name, subtitle, price, period, stats, features } = plan;

  return (
    <div className="subscription-card">
      {/* ---------- Header ---------- */}
      <div className="subscription-card-header">
        <div>
          <h3 className="subscription-card-name">{name}</h3>
          <p className="subscription-card-desc">{subtitle}</p>
        </div>
        {/* ❌ Status removed */}
      </div>

      {/* ---------- Pricing ---------- */}
      <div className="subscription-card-pricing">
        <div className="subscription-pricing-info">
          <h2 className="subscription-card-price">₹{price}</h2>
          <p className="subscription-card-period">{period}</p>
        </div>

        {/* ---------- Stats ---------- */}
        <div className="subscription-card-stats">
          <div className="subscription-stat-item">
            <span className="subscription-stat-label">CODE</span>
            <span className="subscription-stat-value">{stats?.code}</span>
          </div>
        </div>
      </div>

      {/* ---------- Features ---------- */}
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

      {/* ---------- Buy Button ---------- */}
      <button
        className="subscription-edit-btn"
        onClick={() => onBuy && onBuy(plan)}
        disabled={buttonDisabled}
      >
        {buttonText}
      </button>
    </div>
  );
};

export default PlanCard;
