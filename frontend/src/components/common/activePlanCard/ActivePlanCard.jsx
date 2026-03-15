// src/components/common/activePlanCard/ActivePlanCard.jsx
import React from 'react';
import './ActivePlanCard.css';

/**
 * ActivePlanCard — one component, two visual themes.
 *
 *  isActive = true   →  dark purple card  (Crown + Expiry + Renew/Add-ons)
 *  isActive = false  →  white light card  (no crown, no expiry + Buy Now)
 *
 * Props
 * ─────────────────────────────────────────────────────────
 * plan           { id, name, subtitle, price, period, expiryDate, features }
 * isActive       boolean   true = tenant's current plan
 * onBuy          (plan) => void
 * onRenew        (plan) => void
 * onAddons       (plan) => void
 * buttonDisabled boolean
 */
const ActivePlanCard = ({
  plan,
  isActive = false,
  onBuy,
  onRenew,
  onAddons,
  buttonDisabled,
}) => {
  const { name, subtitle, price, period, basePrice, expiryDate, features = [] } = plan;

  return (
    <div className={`apc-card${isActive ? '' : ' apc-card--regular'}`}>

      {/* Top band */}
      <div className="apc-band" />

      {/* Crown badge — active plan only */}
      {isActive && (
        <div className="apc-crown">
          <i className="bi bi-star-fill" />
        </div>
      )}

      <div className="apc-body">

        <div>
          <h3 className="apc-name">{name}</h3>
          <p className="apc-sub">{subtitle}</p>
        </div>

        <div className="apc-pricing">
          <div className="apc-price-wrap">
            {plan.basePrice && plan.basePrice > price ? (
            <div className="apc-price-inline">
              <span className="apc-base-price">₹{Math.max(basePrice, price)}</span>
              <span className="apc-price">₹{Math.min(basePrice, price)}</span>
            </div>
          ) : (
            <span className="apc-price">₹{price}</span>
          )}
            <span className="apc-period" style={{ marginLeft: '6px' }}>{period}</span>
          </div>

          {/* Expiry chip — active plan only */}
          {isActive && expiryDate && (
            <div className="apc-expiry">
              <span className="apc-expiry__label">Expires</span>
              <span className="apc-expiry__value">{expiryDate}</span>
            </div>
          )}
        </div>

        <div className="apc-divider" />

        <div className="apc-features">
          <span className="apc-features-label">Features</span>
          <ul className="apc-features-list">
            {features.map((feature, idx) => (
              <li key={idx} className="apc-feature">
                <i className="bi bi-check2" /> {feature}
              </li>
            ))}
          </ul>
        </div>

      </div>

      {/* Footer */}
      {isActive ? (
        <div className="apc-footer apc-footer--two">
          <button
            className="apc-btn apc-btn--renew"
            disabled={buttonDisabled}
            onClick={() => onRenew && onRenew(plan)}
          >
            <i className="bi bi-arrow-clockwise" /> Renew
          </button>
          <button
            className="apc-btn apc-btn--addons"
            disabled={buttonDisabled}
            onClick={() => onAddons && onAddons(plan)}
          >
            <i className="bi bi-plus-circle" /> Add-ons
          </button>
        </div>
      ) : (
        <div className="apc-footer apc-footer--one">
          <button
            className="apc-btn apc-btn--buy"
            disabled={buttonDisabled}
            onClick={() => onBuy && onBuy(plan)}
          >
            <i className="bi bi-bag-check-fill" /> Buy Now
          </button>
        </div>
      )}

    </div>
  );
};

export default ActivePlanCard;