import React, { useState, useEffect } from 'react';
import PageHeader from '../../../components/common/pageheader/PageHeader';
import TenantLayout from '../../../layouts/TenantLayout';
import Button from '../../../components/common/button/Button';
import InputField from '../../../components/common/inputfield/InputField';
import { addonServices, tenantServices, vendorLicenseServices } from '../../../services/apiService';
import { toast } from 'react-toastify';
import { useLocation, useNavigate } from 'react-router-dom';
import { formConfig } from '../../../config/formConfig';
import './BuyPlan.css';
import '../../../theme/LightTheme.css';
import PopUp from '../../../components/common/popups/PopUp';
import AddOnCard from '../../../components/common/addOnCard/AddOnCard';

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
  const [discountValue, setDiscountValue] = useState(0);
  const [isPaymentPopupOpen, setIsPaymentPopupOpen] = useState(false);
  const [isSuccessPopupOpen, setIsSuccessPopupOpen] = useState(false);
  const [paymentDetails, setPaymentDetails] = useState(null);
  const [gstValus, setGstValue] = useState(0);
  const [couponOpen, setCouponOpen] = useState(false);
  const [referralOpen, setReferralOpen] = useState(false);

  // Wallet state
  const [walletBalance, setWalletBalance] = useState(0);
  const [walletRedeemAmount, setWalletRedeemAmount] = useState(0);
  const [walletApplied, setWalletApplied] = useState(false);

  const navigate = useNavigate();
  const userId = localStorage.getItem("user_id");
  const tenantId = localStorage.getItem("tenant_id");

  useEffect(() => {
    fetchGST();
    fetchWallet();
    if (location.state?.plan) {
      setSelectedPlan(location.state.plan);
    }
  }, [location.state]);

  useEffect(() => {
    if (selectedPlan) fetchAddons(selectedPlan);
  }, [selectedPlan]);

  const fetchGST = async () => {
    try {
      const response = await tenantServices.getLatestGst();
      setGstValue(response.data?.rate || 0);
    } catch {
      toast.error("Failed to fetch GST");
    }
  };

  const fetchWallet = async () => {
    try {
      const response = await tenantServices.getWalletBalance({ tenantId });
      setWalletBalance(response.data?.balance || 0);
    } catch {
      // silently ignore — shows ₹0.00
    }
  };

  const fetchAddons = async (plan) => {
    setLoadingAddons(true);
    try {
      const response = await addonServices.getaddonbyplan({ planId: plan.id });
      const mappedAddons = response.data.map((addon) => ({
        id: addon.id,
        name: addon.name,
        subtitle: addon.code,
        status: addon.status?.toUpperCase(),
        price: addon.unit_price,
        period: `per ${addon.unit}`,
        stats: { code: addon.code, subscribers: "-", revenue: "-" },
        features: [`Billing: ${addon.unit}`, `Currency: ${addon.currency}`],
      }));
      setAddons(mappedAddons);
    } catch {
      toast.error("Failed to load add-ons");
    } finally {
      setLoadingAddons(false);
    }
  };

  const handleBuyAddon = (addon) => {
    setSelectedAddons(prev => {
      const existing = prev.find(a => a.id === addon.id);
      if (existing) {
        return prev.map(a => a.id === addon.id ? { ...a, quantity: (a.quantity || 1) + 1 } : a);
      }
      return [...prev, { ...addon, quantity: 1 }];
    });
    if (addon.stats?.code === 'USER') {
      setAdditionalUsers(prev => prev + parseInt(addon.unit));
    }
  };

  const handleRemoveAddon = (addonId) => {
    setSelectedAddons(prev => {
      const addonToRemove = prev.find(a => a.id === addonId);
      if (!addonToRemove) return prev;
      if (addonToRemove.stats?.code === 'USER') {
        setAdditionalUsers(p => Math.max(0, p - parseInt(addonToRemove.unit)));
      }
      if (addonToRemove.quantity > 1) {
        return prev.map(a => a.id === addonId ? { ...a, quantity: a.quantity - 1 } : a);
      }
      return prev.filter(a => a.id !== addonId);
    });
  };

  const calculateTotalUsers = () => {
    const base = parseInt(selectedPlan?.features?.[0]?.split(': ')[1] || '0');
    return base + additionalUsers;
  };

  // ── Calculations ──────────────────────────────────────────
  const calculateSubtotal = () => {
    const planPrice = selectedPlan?.price || 0;
    const addonsPrice = selectedAddons.reduce((t, a) => t + (a.price || 0) * (a.quantity || 1), 0);
    return planPrice + addonsPrice;
  };

  const calculateAfterDiscount = () =>
    discountApplied ? Math.max(0, calculateSubtotal() - discountAmount) : calculateSubtotal();

  const calculateAfterWallet = () =>
    walletApplied ? Math.max(0, calculateAfterDiscount() - walletRedeemAmount) : calculateAfterDiscount();

  const calculateGSTAmount = () => (calculateAfterWallet() * gstValus) / 100;

  const calculateFinalPayable = () => calculateAfterWallet() + calculateGSTAmount();

  const formatCurrency = (amount) =>
    new Intl.NumberFormat("en-IN", { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(amount || 0);

  // ── Handlers ──────────────────────────────────────────────
  const handleCoupon = async () => {
    try {
      if (!couponCode?.trim()) { toast.error("Please enter a coupon."); return; }
      const result = await vendorLicenseServices.getCouponDiscount({ code: couponCode.trim(), userId });
      const data = result.data;
      setDiscountType(data.discountType);
      setDiscountValue(data.discountValue);
      const subtotal = calculateSubtotal();
      let calc = 0;
      if (data.discountType === "FLAT") {
        calc = data.discountValue;
        if (Math.min(data.discountValue, subtotal) < calc) { toast.error("Coupon can't be applied"); return; }
      } else if (data.discountType === "PERCENTAGE") {
        calc = (subtotal * data.discountValue) / 100;
      }
      if (data.available === false) {
        toast.error(data.message);
      } else {
        toast.success(data.message);
        setDiscountAmount(calc);
        setDiscountApplied(true);
      }
    } catch {
      setDiscountApplied(false);
      setDiscountAmount(0);
      toast.error("Failed to apply coupon");
    }
  };

  const handleRemoveDiscount = () => {
    setDiscountApplied(false);
    setDiscountAmount(0);
    setDiscountType(null);
    setCouponCode('');
    // recalculate wallet if applied
    if (walletApplied) {
      const newAfterDiscount = calculateSubtotal();
      setWalletRedeemAmount(Math.min(walletBalance, newAfterDiscount));
    }
    toast.info("Discount removed", { autoClose: 1000 });
  };

  const handleRefral = async () => {
    try {
      if (!referralCode?.trim()) { toast.error("Please enter a referral code."); return; }
      const response = await vendorLicenseServices.getRefralCode({ referralCode: referralCode.trim() });
      const data = response.data;
      if (data.status === "VALID") toast.success(data.message || "Referral code applied!");
      else if (data.status === "INVALID") toast.error(`${data.status}: ${data.message}`);
      else toast.info(data.message);
    } catch {
      toast.error("Failed to validate referral code.");
    }
  };

  const handleApplyWallet = () => {
    if (walletBalance <= 0) return;
    const redeemable = Math.min(walletBalance, calculateAfterDiscount());
    setWalletRedeemAmount(redeemable);
    setWalletApplied(true);
    toast.success(`₹${formatCurrency(redeemable)} wallet credits applied`);
  };

  const handleRemoveWallet = () => {
    setWalletApplied(false);
    setWalletRedeemAmount(0);
    toast.info("Wallet credits removed", { autoClose: 1000 });
  };

  const handlePayment = async () => {
    try {
      if (!selectedPlan) { toast.error("Please select a plan."); return; }
      const payload = {
        tenant_id: tenantId,
        plan_id: selectedPlan.id,
        addons: selectedAddons.map(a => ({ addon_id: a.id, quantity: a.quantity || 1 })),
        discount: discountApplied ? Number(discountAmount.toFixed(2)) : 0.00,
      };
      const response = await vendorLicenseServices.subscribePlan(payload);
      setPaymentDetails(response.data);
      setIsPaymentPopupOpen(false);
      setIsSuccessPopupOpen(true);
      simulatePayment(response.data.invoice_id);
    } catch {
      toast.error("Payment failed. Please try again.");
    }
  };

  const simulatePayment = async (invoiceId) => {
    try {
      await vendorLicenseServices.simulatePayment({ invoice_id: invoiceId });
    } catch (e) {
      console.error("Payment simulation failed:", e);
    }
  };

  const addonsTotal = selectedAddons.reduce((t, a) => t + (a.price || 0) * (a.quantity || 1), 0);

  return (
    <TenantLayout>
      <div className="buy-plan-content">
        <div className="buy-plan-layout">

          {/* ── Left: Add-on cards ─────────────────────────── */}
          <div className="addons-section">
            <div style={{display: "flex", gap: "5px", alignItems: "center"}}>
            <Button isBack variant="back" text="" />
            <PageHeader
              title="Available Add-ons"
            />
            </div>
            <div className="plans-grid">
              {loadingAddons ? (
                <p>Loading add-ons...</p>
              ) : addons.length === 0 ? (
                <p>No add-ons available at this time.</p>
              ) : (
                addons.map((addon) => (
                  <AddOnCard
                    key={addon.id}
                    plan={addon}
                    onBuy={handleBuyAddon}
                    onRemove={handleRemoveAddon}
                    quantity={selectedAddons.find(a => a.id === addon.id)?.quantity || 0}
                  />
                ))
              )}
            </div>
          </div>

          {/* ── Right: Payment panel ───────────────────────── */}
          <div className="payment-section">
            <div className="pp-root">

              {/* Header */}
              <div className="pp-header">
                <div className="pp-header__icon"><i className="bi bi-receipt" /></div>
                <div>
                  <h3 className="pp-header__title">Payment Summary</h3>
                  <p className="pp-header__sub">Review your order</p>
                </div>
              </div>

              {/* Plan badge */}
              {selectedPlan && (
                <div className="pp-plan-badge">
                  <div className="pp-plan-badge__left">
                    <span className="pp-plan-badge__dot" />
                    <span className="pp-plan-badge__name">{selectedPlan.name}</span>
                  </div>
                  <div className="pp-plan-badge__pills">
                    <span className="pp-pill"><i className="bi bi-people" /> {calculateTotalUsers()} Users</span>
                    <span className="pp-pill"><i className="bi bi-building" /> {selectedPlan?.features?.[1]?.split(': ')[1] || '0'} Co.</span>
                  </div>
                </div>
              )}



              {/* Price breakdown */}
              <div className="pp-breakdown">

                <div className="pp-row">
                  <span className="pp-row__label">Plan price</span>
                  <span className="pp-row__value">₹{formatCurrency(selectedPlan?.price || 0)}</span>
                </div>

                {addonsTotal > 0 && (
                  <div className="pp-row">
                    <span className="pp-row__label">Add-ons total</span>
                    <span className="pp-row__value">₹{formatCurrency(addonsTotal)}</span>
                  </div>
                )}

                <div className="pp-row pp-row--subtotal">
                  <span className="pp-row__label">Subtotal</span>
                  <span className="pp-row__value">₹{formatCurrency(calculateSubtotal())}</span>
                </div>

                {/* Coupon discount */}
                {discountApplied && (
                  <>
                    <div className="pp-row pp-row--discount">
                      <span className="pp-row__label">
                        <i className="bi bi-tag-fill pp-tag-icon" />
                        {discountType === 'PERCENTAGE' ? `Coupon (${discountValue}% off)` : 'Coupon discount'}
                      </span>
                      <span className="pp-row__value pp-row__value--green">− ₹{formatCurrency(discountAmount)}</span>
                    </div>
                    <div className="pp-row pp-row--after-discount">
                      <span className="pp-row__label">After discount</span>
                      <span className="pp-row__value">₹{formatCurrency(calculateAfterDiscount())}</span>
                    </div>
                  </>
                )}

                {/* Wallet redemption */}
                {walletApplied && walletRedeemAmount > 0 && (
                  <>
                    <div className="pp-row pp-row--discount">
                      <span className="pp-row__label">
                        <i className="bi bi-wallet2 pp-tag-icon" />
                        Wallet credits
                        <button className="pp-remove-btn" onClick={handleRemoveWallet} title="Remove wallet">
                          <i className="bi bi-x-circle-fill" />
                        </button>
                      </span>
                      <span className="pp-row__value pp-row__value--green">− ₹{formatCurrency(walletRedeemAmount)}</span>
                    </div>
                    <div className="pp-row pp-row--after-discount">
                      <span className="pp-row__label">After wallet</span>
                      <span className="pp-row__value">₹{formatCurrency(calculateAfterWallet())}</span>
                    </div>
                  </>
                )}

                <div className="pp-row">
                  <span className="pp-row__label">GST ({gstValus}%)</span>
                  <span className="pp-row__value">₹{formatCurrency(calculateGSTAmount())}</span>
                </div>

                <div className="pp-divider pp-divider--thick" />

                <div className="pp-row pp-row--total">
                  <span className="pp-row__label">Total payable</span>
                  <span className="pp-row__value pp-row__value--total">₹{formatCurrency(calculateFinalPayable())}</span>
                </div>
              </div>

              {/* Wallet section */}
              <div className="pp-wallet">
                <div className="pp-wallet__header">
                  <div className="pp-wallet__left">
                    <i className="bi bi-wallet2 pp-wallet__icon" />
                    <div>
                      <p className="pp-wallet__title">Wallet Balance</p>
                      <p className="pp-wallet__balance">₹{formatCurrency(walletBalance)}</p>
                    </div>
                  </div>
                  {!walletApplied ? (
                    <button className="pp-wallet__apply-btn" onClick={handleApplyWallet} disabled={walletBalance <= 0}>
                      Apply
                    </button>
                  ) : (
                    <button className="pp-wallet__remove-btn" onClick={handleRemoveWallet}>Remove</button>
                  )}
                </div>
                {walletApplied && walletRedeemAmount > 0 && (
                  <p className="pp-wallet__redeem-note">
                    <i className="bi bi-check-circle-fill" /> ₹{formatCurrency(walletRedeemAmount)} will be redeemed
                  </p>
                )}
                {walletBalance <= 0 && (
                  <p className="pp-wallet__empty-note">No wallet credits available</p>
                )}
              </div>

              {/* Coupon accordion */}
              <div className="pp-promo">
                <button className="pp-promo__toggle" onClick={() => setCouponOpen(o => !o)}>
                  <span>
                    <i className="bi bi-ticket-perforated" />
                    {discountApplied
                      ? <><i className="bi bi-check-circle-fill pp-check-icon" /> Coupon applied</>
                      : 'Have a coupon?'
                    }
                  </span>
                  <i className={`bi bi-chevron-${couponOpen ? 'up' : 'down'} pp-chevron`} />
                </button>
                {couponOpen && (
                  <div className="pp-promo__body">
                    <div className="pp-promo__input-row">
                      <input
                        className={`pp-input${discountApplied ? ' pp-input--applied' : ''}`}
                        type="text"
                        name="couponCode"
                        value={couponCode}
                        onChange={(e) => !discountApplied && setCouponCode(e.target.value)}
                        placeholder="Enter coupon code"
                        readOnly={discountApplied}
                        onKeyDown={(e) => e.key === 'Enter' && !discountApplied && handleCoupon()}
                      />
                      {discountApplied ? (
                        <button className="pp-apply-btn pp-apply-btn--remove" onClick={handleRemoveDiscount}>
                          <i className="bi bi-x-lg" /> Remove
                        </button>
                      ) : (
                        <button className="pp-apply-btn" onClick={handleCoupon}>Apply</button>
                      )}
                    </div>
                    {discountApplied && (
                      <p className="pp-promo__success-note">
                        <i className="bi bi-check-circle-fill" />
                        {discountType === 'PERCENTAGE' ? `${discountValue}% off applied` : `₹${formatCurrency(discountAmount)} off applied`}
                      </p>
                    )}
                  </div>
                )}
              </div>

              {/* Referral accordion */}
              <div className="pp-promo">
                <button className="pp-promo__toggle" onClick={() => setReferralOpen(o => !o)}>
                  <span>
                    <i className="bi bi-person-plus" />
                    Have a referral code?
                  </span>
                  <i className={`bi bi-chevron-${referralOpen ? 'up' : 'down'} pp-chevron`} />
                </button>
                {referralOpen && (
                  <div className="pp-promo__body">
                    <div className="pp-promo__input-row">
                      <input
                        className="pp-input"
                        type="text"
                        name="referralCode"
                        value={referralCode}
                        onChange={(e) => setReferralCode(e.target.value)}
                        placeholder="Enter referral code"
                        onKeyDown={(e) => e.key === 'Enter' && handleRefral()}
                      />
                      <button className="pp-apply-btn" onClick={handleRefral}>Apply</button>
                    </div>
                  </div>
                )}
              </div>

              {/* Proceed */}
              <div className="pp-action">
                <button className="pp-proceed-btn" onClick={() => setIsPaymentPopupOpen(true)}>
                  <div className="pp-proceed-btn__inner">
                    <span className="pp-proceed-btn__label">Proceed to Pay</span>
                    <span className="pp-proceed-btn__amount">₹{formatCurrency(calculateFinalPayable())}</span>
                  </div>
                  <i className="bi bi-arrow-right pp-proceed-btn__arrow" />
                </button>
              </div>

            </div>
          </div>

          <PopUp
  isOpen={isPaymentPopupOpen}
  onClose={() => setIsPaymentPopupOpen(false)}
  title="Payment Summary"
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

      {/* Payment confirmation popup */}
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
            <div className="popup-row"><span>Plan Price</span><span>₹{formatCurrency(selectedPlan?.price || 0)}</span></div>
            <div className="popup-row"><span>Add-ons</span><span>₹{formatCurrency(addonsTotal)}</span></div>
            <div className="popup-divider" />
            <div className="popup-row"><span>Subtotal</span><span>₹{formatCurrency(calculateSubtotal())}</span></div>
            {discountApplied && (
              <div className="popup-row discount"><span>Discount</span><span>− ₹{formatCurrency(discountAmount)}</span></div>
            )}
            {walletApplied && walletRedeemAmount > 0 && (
              <div className="popup-row discount"><span>Wallet credits</span><span>− ₹{formatCurrency(walletRedeemAmount)}</span></div>
            )}
            <div className="popup-row"><span>GST ({gstValus}%)</span><span>₹{formatCurrency(calculateGSTAmount())}</span></div>
            <div className="popup-divider" />
            <div className="popup-row final"><span>Amount Payable</span><span>₹{formatCurrency(calculateFinalPayable())}</span></div>
          </div>
          <div className="popup-action">
            <Button text={`Pay ₹${formatCurrency(calculateFinalPayable())} Now`} variant="primary" classN="medium" onClick={handlePayment} />
          </div>
        </div>
      </PopUp>

      {/* Success popup */}
      <PopUp
        isOpen={isSuccessPopupOpen}
        onClose={() => setIsSuccessPopupOpen(false)}
        title="Payment Successful"
        subtitle="Your subscription has been activated successfully"
        size="medium"
      >
        <div className="success-popup-content">
          <h3>Thank You for Your Purchase!</h3>
          <p>Your payment has been processed successfully. Your subscription is now active and ready to use.</p>
          <div className="success-summary">
            <div className="summary-row"><span>Amount Paid</span><span>₹{formatCurrency(calculateFinalPayable())}</span></div>
            <div className="summary-row"><span>Plan</span><span>{selectedPlan?.name}</span></div>
            {paymentDetails?.transactionId && (
              <div className="summary-row"><span>Transaction ID</span><span>{paymentDetails.transactionId}</span></div>
            )}
          </div>
          <div className="success-action">
            <Button text="Done" variant="primary" onClick={() => { setIsSuccessPopupOpen(false); navigate('/plansmanagement'); }} />
          </div>
        </div>
      </PopUp>

    </TenantLayout>
  );
};

export default BuyPlan;