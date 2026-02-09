import React, { useState, useEffect } from 'react';
import PageHeader from '../../../components/common/pageheader/PageHeader';
import TenantLayout from '../../../layouts/TenantLayout'; // Changed to TenantLayout as this is for the buyer
import SubscriptionCard from '../../../components/common/subscriptioncard/SubscriptionCard';
import Button from '../../../components/common/button/Button';
import InputField from '../../../components/common/inputfield/InputField';
import { addonServices, tenantServices } from '../../../services/apiService';
import { toast } from 'react-toastify';
import { useLocation } from 'react-router-dom';
import { formConfig } from '../../../config/formConfig';
import './BuyPlan.css';
import '../../../theme/LightTheme.css';
// Component to display add-on plans for tenants to purchase
const BuyPlan = () => {
  const location = useLocation();
  const [loadingAddons, setLoadingAddons] = useState(false);
  const [addons, setAddons] = useState([]);
  const [couponCode, setCouponCode] = useState('');
  const [referralCode, setReferralCode] = useState('');
  const [selectedPlan, setSelectedPlan] = useState(null);
  const [selectedAddons, setSelectedAddons] = useState([]);
  const [additionalUsers, setAdditionalUsers] = useState(0);

  useEffect(() => {
    fetchGST();
  if (location.state?.plan) {
    setSelectedPlan(location.state.plan);
  }
}, [location.state]);

useEffect(() => {
  if (selectedPlan) {
    fetchAddons(selectedPlan);
  }
}, [selectedPlan]);

const [gstValus, setGstValue] = useState(0);

const fetchGST = async () => {
  try {
    const response = await tenantServices.getLatestGst(); // e.g., { rate: 21.0, effectiveDate: "2026-01-13" }
  
    setGstValue(response.data?.rate || 0); // store only the number
  } catch (error) {
    toast.error("Failed to fetch GST");
  }
};

  // Fetch API 
  const fetchAddons = async (plan) => {
    setLoadingAddons(true);
    try {
      
      const payload = {
        planId: plan.id
      }
      
      const response = await addonServices.getaddonbyplan(payload);

      const mappedAddons = response.data.map((addon) => ({
        id: addon.id,
        name: addon.name,
        subtitle: addon.code,
        status: addon.status?.toUpperCase(),
        price: addon.unit_price,
        period: `per ${addon.unit}`,
        stats: {
          code: addon.code,
          subscribers: "-",
          revenue: "-",
        },
        features: [
          `Billing: ${addon.unit}`,
          `Currency: ${addon.currency}`,
        ],
      }));

      setAddons(mappedAddons);
    } catch (error) {
      toast.error("Failed to load add-ons");
    } finally {
      setLoadingAddons(false);
    }
  };

  const handleBuyAddon = (addon) => {
    setSelectedAddons(prev => {
      const existingAddon = prev.find(a => a.id === addon.id);
      if (existingAddon) {
        // Increment quantity if already exists
        return prev.map(a => 
          a.id === addon.id 
            ? { ...a, quantity: (a.quantity || 1) + 1 }
            : a
        );
      } else {
        // Add new addon with quantity 1
        return [...prev, { ...addon, quantity: 1 }];
      }
    });
    
    // Only track users for USER addons
    if (addon.code === 'USER') {
      setAdditionalUsers(prev => prev + parseInt(addon.unit));
    }
  };

  const handleRemoveAddon = (addonId) => {
    setSelectedAddons(prev => {
      const addonToRemove = prev.find(a => a.id === addonId);
      if (addonToRemove) {
        // Only track users for USER addons
        if (addonToRemove.code === 'USER') {
          setAdditionalUsers(prev => Math.max(0, prev - parseInt(addonToRemove.unit)));
        }
        
        // If quantity > 1, decrement, otherwise remove completely
        if (addonToRemove.quantity > 1) {
          return prev.map(a => 
            a.id === addonId 
              ? { ...a, quantity: a.quantity - 1 }
              : a
          );
        } else {
          return prev.filter(a => a.id !== addonId);
        }
      }
      return prev;
    });
  };

  const calculateTotalUsers = () => {
    const baseUsers = parseInt(selectedPlan?.features?.[0]?.split(': ')[1] || '0');
    return baseUsers + additionalUsers;
  };

  const [gstValue, setnewGstValue] = useState(0);

const calculateSubtotal = () => {
  const planPrice = selectedPlan?.price || 0;
  const addonsPrice = selectedAddons.reduce(
    (total, addon) => total + (addon.price || 0) * (addon.quantity || 1),
    0
  );
  return planPrice + addonsPrice;
};

const calculateGSTAmount = () => {
  return (calculateSubtotal() * gstValus) / 100;
};

const calculateTotalPayable = () => {
  return calculateSubtotal() + calculateGSTAmount();
};


 
  
  return (
    <TenantLayout>
      <div className="buy-plan-content">
        
        <div className="buy-plan-layout">
          {/* Left Side - Addon Cards */}
          <div className="addons-section">
            <PageHeader
              title="Available Add-ons"
              subtitle="Enhance your plan with additional features"
            />
            <div className="plans-grid">
              {loadingAddons ? (
                <p>Loading add-ons...</p>
              ) : addons.length === 0 ? (
                <p>No add-ons available at this time.</p>
              ) : (
                addons.map((addon) => (
                  <SubscriptionCard
                    key={addon.id}
                    plan={addon}
                    onBuy={handleBuyAddon}
                  />
                ))
              )}
            </div>
          </div>

          {/* Right Side - Payment Window */}
          <div className="payment-section">
            <PageHeader
              title="Payment Summary"
              subtitle="Review your selection"
            />
            <div className="payment-window">
              <div className="payment-amount">
  <h3>
    Subtotal: ₹{calculateSubtotal().toFixed(2)}
  </h3>

  <h4>
    GST ({gstValus}%): ₹{calculateGSTAmount().toFixed(2)}
  </h4>

  <hr />

  <h3 style={{ fontWeight: 600 }}>
    Total Payable: ₹{calculateTotalPayable().toFixed(2)}
  </h3>
</div>

              {selectedPlan && (
                <div className="plan-details">
                  <div className="plan-info">
                    <div className="plan-limits">
                      <span>Users: {calculateTotalUsers()}</span>
                      <span>Companies: {selectedPlan?.features?.[1]?.split(': ')[1] || '0'}</span>
                    </div>
                  </div>
                </div>
              )}
              {selectedAddons.length > 0 && (
                <div className="selected-addons">
                  <h4>Selected Add-ons:</h4>
                  {selectedAddons.map((addon) => (
                    <div key={addon.id} className="addon-item">
                      <span className="addon-name">{addon.name} x{addon.quantity || 1}</span>
                      <div className="addon-right">
                        <span className="addon-price">₹{(addon.price * (addon.quantity || 1))}.00</span>
                        <Button
                          text="Remove"
                          variant="red"
                          onClick={() => handleRemoveAddon(addon.id)}
                          classN="small"
                        />
                      </div>
                    </div>
                  ))}
                </div>
              )}
              <div className="payment-inputs">
                <div className="input-row">
                  <InputField
                    label={formConfig.payment.couponCode.label}
                    name="couponCode"
                    value={couponCode}
                    onChange={(e) => setCouponCode(e.target.value)}
                    placeholder="Enter coupon code"
                    classN="medium"
                  />
                  <Button
                    text="Apply"
                    variant="primary"
                  />
                </div>
                <div className="input-row">
                  <InputField
                    label={formConfig.payment.referralCode.label}
                    name="referralCode"
                    value={referralCode}
                    onChange={(e) => setReferralCode(e.target.value)}
                    placeholder="Enter referral code"
                    classN="medium"
                  />
                  <Button
                    text="Apply"
                    variant="primary"
                  />
                </div>
              </div>
              <div className="payment-action">
                <Button
                  text="Do Payment"
                  variant="primary"
                  classN="medium"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </TenantLayout>
  );
};

export default BuyPlan;