// src/components/common/plancard/PlanCard.jsx
import React from 'react';
import '../subscriptioncard/SubscriptionCard.css';

const PlanCard = ({ plan, onBuy, buttonText = "Try Now", buttonDisabled = false }) => {
  const { name, subtitle, price, period, stats, features } = plan;

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
        </div>

        {/* ── Pricing ── */}
        <div className="sc-pricing">
          <div className="sc-price-wrap">
            <span className="sc-price">₹{price}</span>
            <span className="sc-period">{period}</span>
          </div>
          <div className="sc-code">
            <span className="sc-code__label">Code</span>
            <span className="sc-code__value">{stats?.code}</span>
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
        className="sc-btn sc-btn--buy"
        onClick={() => onBuy && onBuy(plan)}
        disabled={buttonDisabled}
      >
        <i className="bi bi-bag-check" />
        {buttonText}
      </button>

    </div>
  );
};

export default PlanCard;