import React, { useState, useEffect } from 'react';
import PageHeader from '../../../components/common/pageheader/PageHeader';
import TenantLayout from '../../../layouts/TenantLayout'; // Changed to TenantLayout as this is for the buyer
import SubscriptionCard from '../../../components/common/subscriptioncard/SubscriptionCard';
import Button from '../../../components/common/button/Button';
import InputField from '../../../components/common/inputfield/InputField';
import { addonServices, tenantServices, vendorLicenseServices } from '../../../services/apiService';
import { toast } from 'react-toastify';
import { useLocation, useNavigate } from 'react-router-dom';
import { formConfig } from '../../../config/formConfig';
import './BuyPlan.css';
import '../../../theme/LightTheme.css';
import PopUp from '../../../components/common/popups/PopUp';
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
  const [discountAmount, setDiscountAmount] = useState(0);
  const [discountApplied, setDiscountApplied] = useState(false);
  const [discountType, setDiscountType] = useState(null);
  const [isPaymentPopupOpen, setIsPaymentPopupOpen] = useState(false);
  const [isSuccessPopupOpen, setIsSuccessPopupOpen] = useState(false);
  const [paymentDetails, setPaymentDetails] = useState(null);
  const [discountValue, setDiscountValue] = useState(0);


  const navigate = useNavigate();


  const userId = localStorage.getItem("user_id");
  const tenantId = localStorage.getItem("tenant_id");

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

  // 1️⃣ Subtotal (Plan + Add-ons)
const calculateSubtotal = () => {
  const planPrice = selectedPlan?.price || 0;

  const addonsPrice = selectedAddons.reduce(
    (total, addon) =>
      total + (addon.price || 0) * (addon.quantity || 1),
    0
  );

  return planPrice + addonsPrice;
};


 // 2️⃣ Discounted Subtotal
const calculateDiscountedSubtotal = () => {
  if (!discountApplied) return calculateSubtotal();
  return Math.max(0, calculateSubtotal() - discountAmount);
};

// 3️⃣ GST on Discounted Subtotal
const calculateGSTAmount = () => {
  return (calculateDiscountedSubtotal() * gstValus) / 100;
};


  const calculateTotalPayable = () => {
    return calculateSubtotal() + calculateGSTAmount();
  };

  const calculateTotalWithGST = () => {
    return calculateSubtotal() + calculateGSTAmount();
  };

  // 4️⃣ Final Payable
const calculateFinalPayable = () => {
  return calculateDiscountedSubtotal() + calculateGSTAmount();
};



  const handleCoupon = async () => {
    try {
      if (!couponCode?.trim()) {
        toast.error("Please enter a coupon.");
        return;
      }

      const payload = {
        code: couponCode.trim(),
        userId: userId,
      };

      const result = await vendorLicenseServices.getCouponDiscount(payload);

      const data = result.data;

      setDiscountType(data.discountType);
      setDiscountValue(data.discountValue);

      const subtotal = calculateSubtotal();


      let calculatedDiscount = 0;

      if (data.discountType === "FLAT") {
        
        calculatedDiscount = data.discountValue;
        if(Math.min(data.discountValue, subtotal) < calculatedDiscount){
          toast.error("Coupon cant be applied");
          return;
        }
      
      } else if (data.discountType === "PERCENTAGE") {
        calculatedDiscount =
          (subtotal * data.discountValue) / 100;
      } 

      if(data.available == false){
        toast.error(data.message);
      }else{
        toast.success(data.message);
        setDiscountAmount(calculatedDiscount);
        setDiscountApplied(true);
      }

    } catch (error) {
      setDiscountApplied(false);
      setDiscountAmount(0);
      toast.error("Failed to apply coupon");
    }
  };


  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("en-IN", {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }).format(amount || 0);
  };


  const handleRefral = async () => {
  try {
    if (!referralCode?.trim()) {
      toast.error("Please enter a Referral code.");
      return;
    }

    const payload = {
      referralCode: referralCode.trim(),
    };

    const response = await vendorLicenseServices.getRefralCode(payload);

    const data = response.data; // assuming API returns { status: "...", message: "..." }

    if (data.status === "VALID") {
      toast.success(data.message || "Referral code applied successfully!");
    } else if (data.status === "INVALID") {
      toast.error(`${data.status}: ${data.message || "Invalid referral code"}`);
    } else {
      toast.info(data.message || "Unknown response from server");
    }

  } catch (error) {
    console.error(error);
    toast.error("Failed to validate referral code. Please try again.");
  }
};


const handlePayment = async () => {
  try {
    if (!selectedPlan) {
      toast.error("Please select a plan before proceeding.");
      return;
    }

    const payload = {
      tenant_id: tenantId,
      plan_id: selectedPlan.id,
      addons: selectedAddons.map(a => ({
        addon_id: a.id,
        quantity: a.quantity || 1,
      })),
      discount: discountApplied ? Number(discountAmount.toFixed(2)) : 0.00
    };

    // Make API call
    const response = await vendorLicenseServices.subscribePlan(payload);

    const invoiceId = response.data.invoice_id; // make sure correct key name
   
    
    // Save response data if needed (transaction id etc.)
    setPaymentDetails(response.data);
    setIsPaymentPopupOpen(false);
    
    setIsSuccessPopupOpen(true);


    simulatePayment(response.data.invoice_id); // Simulate payment for testing

  } catch (error) {
    console.error(error);
    toast.error("Payment failed. Please try again.");
  }
};

const handleRemoveDiscount = () => {
  setDiscountApplied(false);
  setDiscountAmount(0);
  setDiscountType(null);
  setCouponCode('');
  toast.info("Discount removed", {
    autoClose: 1000,   // closes after 1 second
  });
};

const simulatePayment = async (invoiceId) => {
  try {
   
    const payload = {
      "invoice_id": invoiceId,
    };
    const response = await vendorLicenseServices.simulatePayment(payload);
  } catch (error) {
    console.error("Payment simulation failed:", error);
  } 
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
            <div className='payment-header-title'>
            <PageHeader
              title="Payment Summary"
              subtitle="Review your selection"
            />
            </div>
            <div className="payment-window">
              {selectedPlan && (
                <div className="plan-details">
                  <div className="plan-info">

                    <span>Users: {calculateTotalUsers()}</span>
                    <span>Companies: {selectedPlan?.features?.[1]?.split(': ')[1] || '0'}</span>

                  </div>
                </div>
              )}
              <div className="summary-divider" />
              {selectedAddons.length > 0 && (
                <div className="selected-addons">
                  <h4>Selected Add-ons:</h4>
                  {selectedAddons.map((addon) => (
                    <div key={addon.id} className="addon-item">

                      {/* Addon Name */}
                      <div className="addon-name">
                        {addon.name}
                      </div>
                      <div className="addon-wrapper">
                        {/* Quantity Controls */}
                        <div className="addon-quantity-wrapper">
                          <button
                            className="qty-btn minus"
                            onClick={() => handleRemoveAddon(addon.id)}
                          >
                            −
                          </button>

                          <span className="qty-count">
                            {addon.quantity || 1}
                          </span>

                          <button
                            className="qty-btn plus"
                            onClick={() => handleBuyAddon(addon)}
                          >
                            +
                          </button>
                        </div>

                        {/* Price Below Buttons */}
                        <div className="addon-price">
                          ₹{formatCurrency(addon.price * (addon.quantity || 1))}
                        </div>
                      </div>
                    </div>

                  ))}
                  <div className="summary-divider" />
                </div>
              )}

              <div className="payment-amount">
                <div className="payment-summary-card">

      {/* Plan Price */}
      <div className="summary-row">
        <span>Plan Price</span>
        <span>₹{formatCurrency(selectedPlan?.price || 0)}</span>
      </div>
                
      {/* Add-ons */}
      <div className="summary-row">
        <span>Add-ons</span>
        <span>
          ₹{formatCurrency(
            selectedAddons.reduce(
              (total, addon) =>
                total + (addon.price || 0) * (addon.quantity || 1),
              0
            )
          )}
        </span>
      </div>
        
      <div className="summary-divider" />
        
      {/* Subtotal */}
      <div className="summary-row">
        <span>Subtotal</span>
        <span>₹{formatCurrency(calculateSubtotal())}</span>
      </div>
        
      {/* Discount (Only if applied) */}
      {discountApplied && (
      <div className="summary-row discount-row">
        <span style={{ display: "flex", alignItems: "center", gap: "6px" }}>
           {discountType === "PERCENTAGE"
            ? `Discount (${discountValue}%)`
            : "Discount"}
          <i
            className="bi bi-x-circle-fill"
            style={{
              color: "#dc3545",
              cursor: "pointer",
              fontSize: "14px"
            }}
            title="Remove Discount"
            onClick={handleRemoveDiscount}
          ></i>
        </span>
          
        <span>- ₹{formatCurrency(discountAmount)}</span>
      </div>
    )}


  {/* GST calculated on discounted subtotal */}
  <div className="summary-row">
    <span>GST ({gstValus}%)</span>
    <span>₹{formatCurrency(calculateGSTAmount())}</span>
  </div>

  <div className="summary-divider" />

  {/* Final Payable */}
  <div className="summary-row total-row">
    <span>To Pay</span>
    <span className="total-amount">
      ₹{formatCurrency(calculateFinalPayable())}
    </span>
  </div>

</div>



              </div>



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
                    onClick={handleCoupon}
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
                    onClick={handleRefral}
                  />
                </div>
              </div>
              <div className="payment-action">
                <Button
                  text="Proceed"
                  variant="primary"
                  classN="medium"
                  onClick={() => setIsPaymentPopupOpen(true)}
                />
              </div>
            </div>
          </div>

          <PopUp
  isOpen={isPaymentPopupOpen}
  onClose={() => setIsPaymentPopupOpen(false)}
  title="Payment Summary"
  subtitle="Review your order before proceeding"
  size="large"
>
  <div className="popup-payment-summary">

    <div className="popup-section">
      <h4 className="popup-section-title">Plan Details</h4>

      {/* Plan Price */}
      <div className="popup-row">
        <span>Plan Price</span>
        <span>₹{formatCurrency(selectedPlan?.price || 0)}</span>
      </div>

      {/* Add-ons */}
      <div className="popup-row">
        <span>Add-ons</span>
        <span>
          ₹{formatCurrency(
            selectedAddons.reduce(
              (total, addon) =>
                total + (addon.price || 0) * (addon.quantity || 1),
              0
            )
          )}
        </span>
      </div>

      <div className="popup-divider" />

      {/* Subtotal */}
      <div className="popup-row">
        <span>Subtotal</span>
        <span>₹{formatCurrency(calculateSubtotal())}</span>
      </div>

      {/* Discount */}
      {discountApplied && (
        <div className="popup-row discount">
          <span>Discount</span>
          <span>- ₹{formatCurrency(discountAmount)}</span>
        </div>
      )}

      {/* GST on discounted subtotal */}
      <div className="popup-row">
        <span>GST ({gstValus}%)</span>
        <span>₹{formatCurrency(calculateGSTAmount())}</span>
      </div>

      <div className="popup-divider" />

      {/* Final Amount */}
      <div className="popup-row final">
        <span>Amount Payable</span>
        <span>₹{formatCurrency(calculateFinalPayable())}</span>
      </div>
    </div>

    {/* Action Button */}
    <div className="popup-action">
      <Button
        text={`Pay ₹${formatCurrency(calculateFinalPayable())} Now`}
        variant="primary"
        classN="medium"
        onClick={handlePayment}
      />
    </div>

  </div>
</PopUp>


          <PopUp
  isOpen={isSuccessPopupOpen}
  onClose={() => setIsSuccessPopupOpen(false)}
  title="Payment Successful"
  subtitle="Your subscription has been activated successfully"
  size="medium"
>
  <div className="success-popup-content">

    <h3>Thank You for Your Purchase!</h3>

    <p>
      Your payment has been processed successfully.
      Your subscription is now active and ready to use.
    </p>

    <div className="success-summary">
      <div className="summary-row">
        <span>Amount Paid</span>
        <span>₹{formatCurrency(calculateFinalPayable())}</span>
      </div>

      <div className="summary-row">
        <span>Plan</span>
        <span>{selectedPlan?.name}</span>
      </div>

      {paymentDetails?.transactionId && (
        <div className="summary-row">
          <span>Transaction ID</span>
          <span>{paymentDetails.transactionId}</span>
        </div>
      )}
    </div>

    <div className="success-action">
      <Button
        text="Done"
        variant="primary"
        onClick={() => {
          setIsSuccessPopupOpen(false);
          navigate('/plansmanagement');
        }}
      />
    </div>

  </div>
</PopUp>



        </div>
      </div>
    </TenantLayout>
  );
};

export default BuyPlan;