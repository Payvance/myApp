// src/components/common/subscriptioncard/SubscriptionCard.jsx
import React from 'react';
import './SubscriptionCard.css';

const SubscriptionCard = ({ plan, onEdit, onBuy, buttonDisabled }) => {
  const { id, name, subtitle, status, price, period, stats, features } = plan;
  const isActive = status.toLowerCase() === 'active' || status.toLowerCase() === 'available';

  return (
    <div className="subscription-card">

      {/* Gradient top band */}
      <div className="sc-band" />

      <div className="sc-body">

        {/* ── Header ── */}
        <div className="sc-header">
          <div>
            <h3 className="sc-name">{name}</h3>
            <p className="sc-sub">{subtitle}</p>
          </div>
          <span className={`sc-badge ${isActive ? 'sc-badge--active' : 'sc-badge--inactive'}`}>
            {status}
          </span>
        </div>

        {/* ── Pricing ── */}
        <div className="sc-pricing">
          <div className="sc-price-wrap">
            <span className="sc-price">₹{price}</span>
            <span className="sc-period">{period}</span>
          </div>
        </div>

        <div className="sc-divider" />

        {/* ── Features ── */}
        <div className="sc-features">
          <span className="sc-features-label">Features</span>
          <ul className="sc-features-list">
            {features.map((feature, idx) => (
              <li key={idx} className="sc-feature">
                <i className="bi bi-check2" /> {feature}
              </li>
            ))}
          </ul>
        </div>

      </div>

      {/* ── Button ── */}
      <button
        className={`sc-btn ${onBuy ? 'sc-btn--buy' : 'sc-btn--edit'}`}
        disabled={buttonDisabled}
        onClick={() => { if (onBuy) onBuy(plan); else if (onEdit) onEdit(plan); }}
      >
        <i className={`bi ${onBuy ? 'bi-bag-check' : 'bi-pencil'}`} />
        {onBuy ? 'Buy Now' : 'Edit Plan'}
      </button>

    </div>
  );
};

export default SubscriptionCard;