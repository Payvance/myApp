// src/components/common/addOnCard/AddOnCard.jsx
import React from 'react';
import './AddOnCard.css';

/**
 * AddOnCard Component
 *
 * Props (matching BuyPlan.jsx usage):
 *  - plan: { id, name, subtitle, price, period, stats, features }
 *  - onBuy(plan)       — called on increment (+ button)
 *  - onRemove(planId)  — called on decrement (- button), BuyPlan handles qty logic
 *  - quantity          — controlled from parent (BuyPlan's selectedAddons)
 */
const AddOnCard = ({
  plan,
  onBuy,
  onRemove,
  quantity = 0,
}) => {
  const {
    id,
    name,
    subtitle,
    price,
    period,
    stats,
    features = [],
  } = plan;

  const isSelected = quantity > 0;

  const handleDecrement = () => {
    if (quantity === 0) return;
    if (onRemove) onRemove(id);
  };

  const handleIncrement = () => {
    if (onBuy) onBuy(plan);
  };

  return (
    <div className={`addon-card ${isSelected ? 'addon-card--selected' : ''}`}>

      {/* -------- Header -------- */}
      <div className="addon-card__header">
        

        <div className="addon-card__title-group">
          <div className="addon-card__name-row">
            <h3 className="addon-card__name">{name}</h3>
            {isSelected && (
              <span className="addon-card__badge">SELECTED</span>
            )}
          </div>
          <p className="addon-card__subtitle">{subtitle}</p>
        </div>
      </div>

      {/* -------- Pricing + Code -------- */}
      <div className="addon-card__pricing-row">
        <div className="addon-card__price-block">
          <span className="addon-card__price">₹{price}</span>
        </div>
        {stats?.code && (
          <span className="addon-card__code-badge">{stats.code}</span>
        )}
      </div>

      {/* -------- Features -------- */}
      {features.length > 0 && (
        <ul className="addon-card__features">
          {features.map((f, i) => (
            <li key={i} className="addon-card__feature-item">
              <i className="bi bi-check2" /> {f}
            </li>
          ))}
        </ul>
      )}

      {/* -------- Quantity Stepper -------- */}
      <div className="addon-card__stepper-wrap">
        <div className={`addon-card__stepper ${isSelected ? 'addon-card__stepper--active' : ''}`}>
          <button
            className="addon-card__stepper-btn addon-card__stepper-btn--minus"
            onClick={handleDecrement}
            disabled={quantity === 0}
            aria-label="Decrease quantity"
          >
            −
          </button>

          <span className="addon-card__stepper-count">{quantity}</span>

          <button
            className="addon-card__stepper-btn addon-card__stepper-btn--plus"
            onClick={handleIncrement}
            aria-label="Increase quantity"
          >
            +
          </button>
        </div>

        {isSelected && (
          <span className="addon-card__qty-label">{quantity} added</span>
        )}
      </div>

    </div>
  );
};

export default AddOnCard;