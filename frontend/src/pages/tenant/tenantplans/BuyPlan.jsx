import React, { useState, useEffect } from 'react';
import PageHeader from '../../../components/common/pageheader/PageHeader';
import TenantLayout from '../../../layouts/TenantLayout';
import Button from '../../../components/common/button/Button';
import InputField from '../../../components/common/inputfield/InputField';
import { addonServices, tenantServices, vendorLicenseServices, companyDetailsServices } from '../../../services/apiService';
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
  const [walletInputAmount, setWalletInputAmount] = useState(''); // what the user types
  const [walletRedeemAmount, setWalletRedeemAmount] = useState(0); // confirmed applied amount
  const [walletApplied, setWalletApplied] = useState(false);
  const [walletInputError, setWalletInputError] = useState('');
  const [loadingWallet, setLoadingWallet] = useState(true);

  // Cashfree state
  const [cashfreeSessionId, setCashfreeSessionId] = useState(null);
  const [cashfreeInvoiceId, setCashfreeInvoiceId] = useState(null);
  const [processingPayment, setProcessingPayment] = useState(false);

  // Tab state
  const [activePaymentTab, setActivePaymentTab] = useState('summary'); // 'summary' or 'cashfree'

  // Company details state
  const [companyDetails, setCompanyDetails] = useState(null);
  const [loadingCompanyDetails, setLoadingCompanyDetails] = useState(false);

  const navigate = useNavigate();
  const userId = localStorage.getItem("user_id");
  const tenantId = localStorage.getItem("tenant_id");

  useEffect(() => {
    fetchGST();
    fetchWallet();
    fetchCompanyDetails();
    handleCashfreeReturn();   // check if we just returned from Cashfree payment page
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
    setLoadingWallet(true);
    try {
      const response = await tenantServices.getWalletDetails(tenantId);
      setWalletBalance(response.data?.wallet.balance || response.data?.wallet.availableBalance || 0);
    } catch {
      setWalletBalance(0);
    } finally {
      setLoadingWallet(false);
    }
  };

  const fetchCompanyDetails = async () => {
    setLoadingCompanyDetails(true);
    try {
      const response = await companyDetailsServices.getByTenant(tenantId);
      setCompanyDetails(response.data);
    } catch (error) {
      console.error("Failed to fetch company details:", error);
      setCompanyDetails(null);
    } finally {
      setLoadingCompanyDetails(false);
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

  // ── Coupon / Referral handlers ────────────────────────────
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

  // ── Wallet handlers ───────────────────────────────────────
  const handleWalletInputChange = (e) => {
    const val = e.target.value;
    if (val !== '' && !/^\d*\.?\d{0,2}$/.test(val)) return;
    setWalletInputAmount(val);
    setWalletInputError('');
  };

  const handleWalletMax = () => {
    const maxUsable = Math.min(walletBalance, calculateAfterDiscount());
    setWalletInputAmount(maxUsable.toFixed(2));
    setWalletInputError('');
  };

  const handleApplyWallet = () => {
    const typed = parseFloat(walletInputAmount);
    if (!walletInputAmount || isNaN(typed) || typed <= 0) {
      setWalletInputError('Please enter an amount to use.');
      return;
    }
    if (typed > walletBalance) {
      setWalletInputError(`Exceeds your balance of ₹${formatCurrency(walletBalance)}`);
      return;
    }
    const maxUsable = calculateAfterDiscount();
    if (typed > maxUsable) {
      setWalletInputError(`Can't exceed bill amount of ₹${formatCurrency(maxUsable)}`);
      return;
    }
    setWalletInputError('');
    setWalletRedeemAmount(typed);
    setWalletApplied(true);
    toast.success(`₹${formatCurrency(typed)} wallet credits applied`);
  };

  const handleRemoveWallet = () => {
    setWalletApplied(false);
    setWalletRedeemAmount(0);
    setWalletInputAmount('');
    setWalletInputError('');
    toast.info("Wallet credits removed", { autoClose: 1000 });
  };

  // ── Payment flow ──────────────────────────────────────────

  /**
   * STEP 1 — "Pay Now" clicked in the summary popup.
   * Calls processBilling → backend returns payment_session_id
   * → load Cashfree SDK → open Cashfree checkout
   */
  const handlePayment = async () => {
    try {
      if (!selectedPlan) { toast.error("Please select a plan."); return; }
      setProcessingPayment(true);

      const payload = {
        tenant_id: parseInt(tenantId),
        plan_id: selectedPlan.id,
        coupon_code: discountApplied && couponCode ? couponCode.trim() : null,
        referral_code: referralCode ? referralCode.trim() : null,
        wallet_amount: walletApplied ? Number(walletRedeemAmount.toFixed(2)) : 0.00,
        addons: selectedAddons.map(a => ({ addon_id: a.id, quantity: a.quantity || 1 })),
      };

      // Returns: { amount, cf_order_id, invoice_id, payment_session_id, message, invoice_number }
      const response = await tenantServices.processBilling(payload, tenantId);
      const { payment_session_id, invoice_id } = response.data;

      if (!payment_session_id) throw new Error("No payment_session_id received from server.");

      // Save for use in the success callback
      setCashfreeSessionId(payment_session_id);
      setCashfreeInvoiceId(invoice_id);

      // Close summary popup then launch Cashfree
      setIsPaymentPopupOpen(false);
      await openCashfreeCheckout(payment_session_id, invoice_id);

    } catch (error) {
      console.error("Billing error:", error);
      toast.error(error?.response?.data?.message || "Failed to initiate payment. Please try again.");
    } finally {
      setProcessingPayment(false);
    }
  };

  /**
   * STEP 2 — Load Cashfree JS SDK and open the checkout.
   * Cashfree drop-in handles card / UPI / netbanking etc.
   * On success it calls handleCashfreeSuccess.
   */
  // ⚙️ "sandbox" while testing → "production" for live
  const CASHFREE_ENV = "sandbox";

  /**
   * STEP 2 — Open Cashfree in a NEW TAB by injecting a self-contained
   * HTML page via Blob URL. This page loads the Cashfree SDK and calls
   * checkout({ redirectTarget: "_self" }) inside the new tab itself,
   * so the redirect happens there — not in our app tab.
   *
   * After payment, Cashfree redirects the new tab to the return_url
   * set on the backend. We poll with setInterval: when the tab closes
   * we verify payment status and call subscribePlan.
   */
  const openCashfreeCheckout = (paymentSessionId, invoiceId) => {
    // Persist context — survives tab switch
    localStorage.setItem('cf_invoice_id', String(invoiceId));
    localStorage.setItem('cf_plan_id',    String(selectedPlan.id));
    localStorage.removeItem('cf_status');

    // Build a self-contained HTML page that runs Cashfree SDK in the new tab
    const cashfreeHtml = `<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8"/>
  <title>Processing Payment...</title>
  <script src="https://sdk.cashfree.com/js/v3/cashfree.js"><\/script>
</head>
<body>
  <div style="display:flex;align-items:center;justify-content:center;height:100vh;font-family:sans-serif;flex-direction:column;gap:16px">
    <div style="width:40px;height:40px;border:4px solid #e5e7eb;border-top-color:#7E56E4;border-radius:50%;animation:spin 0.8s linear infinite"></div>
    <p style="color:#6b7280;font-size:14px">Redirecting to payment page...</p>
  </div>
  <style>@keyframes spin{to{transform:rotate(360deg)}}</style>
  <script>
    window.onload = function() {
      const cashfree = window.Cashfree({ mode: "${CASHFREE_ENV}" });
      cashfree.checkout({
        paymentSessionId: "${paymentSessionId}",
        redirectTarget: "_self",
      }).catch(function(err) {
        document.body.innerHTML = '<p style="color:red;text-align:center;margin-top:40vh">Payment failed: ' + (err.message || 'Unknown error') + '</p>';
      });
    };
  <\/script>
</body>
</html>`;

    const blob    = new Blob([cashfreeHtml], { type: 'text/html' });
    const blobUrl = URL.createObjectURL(blob);
    const paymentTab = window.open(blobUrl, '_blank');

    if (!paymentTab) {
      URL.revokeObjectURL(blobUrl);
      toast.error("Popup blocked! Please allow popups for this site and try again.");
      return;
    }

    toast.info("Payment page opened in a new tab.", { autoClose: 3000 });

    // Poll every 1.5s — when tab closes, verify payment and activate subscription
    const pollInterval = setInterval(() => {
      if (paymentTab.closed) {
        clearInterval(pollInterval);
        URL.revokeObjectURL(blobUrl);
        checkPaymentStatus(invoiceId);
      }
    }, 1500);
  };

  /**
   * Tab closed — ask backend whether payment went through before activating.
   */
  const checkPaymentStatus = async (invoiceId) => {
    try {
      toast.info("Verifying payment...");
      const response = await tenantServices.getInvoiceStatus(invoiceId);
      const status   = response.data?.status || response.data?.payment_status;
      console.log("[Cashfree] invoice status after tab close:", status);

      if (status === 'PAID' || status === 'paid' || status === 'SUCCESS') {
        handleCashfreeSuccess(invoiceId);
      } else if (status === 'PENDING') {
        toast.info("Payment is pending. You will be notified once confirmed.");
      } else {
        toast.error("Payment was not completed. Please try again.");
      }
    } catch (err) {
      console.warn("[Cashfree] Could not verify payment status:", err);
      toast.warn("Could not verify payment automatically. Please check your subscription status.");
    }
  };

  /**
   * STEP 3 — Payment confirmed → activate subscription via subscribePlan.
   */
  const handleCashfreeSuccess = async (invoiceId) => {
    try {
      toast.info("Activating your subscription...");

      const subscribePayload = {
        tenant_id:  parseInt(tenantId),
        plan_id:    selectedPlan.id,
        invoice_id: parseInt(invoiceId || cashfreeInvoiceId),
        addons:     selectedAddons.map(a => ({ addon_id: a.id, quantity: a.quantity || 1 })),
        discount:   discountApplied ? Number(discountAmount.toFixed(2)) : 0.00,
      };

      const response = await vendorLicenseServices.subscribePlan(subscribePayload);
      setPaymentDetails(response.data);
      setIsSuccessPopupOpen(true);
      localStorage.removeItem('cf_invoice_id');
      localStorage.removeItem('cf_plan_id');

    } catch (error) {
      console.error("Subscription activation error:", error);
      toast.error("Payment received but subscription activation failed. Please contact support.");
    }
  };

  // No-op — kept so useEffect call doesn't break
  const handleCashfreeReturn = () => {};

  const addonsTotal = selectedAddons.reduce((t, a) => t + (a.price || 0) * (a.quantity || 1), 0);

  return (
    <TenantLayout>
      <div className="buy-plan-content">
        <div className="buy-plan-layout">

          {/* ── Left: Add-on cards ─────────────────────────── */}
          <div className="addons-section">
            <div style={{ display: "flex", gap: "5px", alignItems: "center" }}>
              <Button isBack variant="back" text="" />
              <PageHeader title="Available Add-ons" />
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

          {/* ── Right: Payment panel with tabs ───────────────────────── */}
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

              {/* Tab Navigation */}
              <div style={{
                display: 'flex',
                gap: '8px',
                borderBottom: '1px solid var(--border-primary, #e6eaf0)',
                marginBottom: '16px',
                paddingBottom: '0'
              }}>
                <button
                  onClick={() => setActivePaymentTab('summary')}
                  style={{
                    padding: '12px 16px',
                    border: 'none',
                    background: 'transparent',
                    cursor: 'pointer',
                    fontSize: '13px',
                    fontWeight: '600',
                    color: activePaymentTab === 'summary' ? 'var(--purple-500, #7E56E4)' : 'var(--text-secondary, #6B7280)',
                    borderBottom: activePaymentTab === 'summary' ? '2px solid var(--purple-500, #7E56E4)' : 'transparent',
                    transition: 'all 0.2s ease'
                  }}
                >
                  <i className="bi bi-receipt" style={{ marginRight: '6px' }} />
                  Summary
                </button>
                <button
                  onClick={() => setActivePaymentTab('cashfree')}
                  style={{
                    padding: '12px 16px',
                    border: 'none',
                    background: 'transparent',
                    cursor: 'pointer',
                    fontSize: '13px',
                    fontWeight: '600',
                    color: activePaymentTab === 'cashfree' ? 'var(--purple-500, #7E56E4)' : 'var(--text-secondary, #6B7280)',
                    borderBottom: activePaymentTab === 'cashfree' ? '2px solid var(--purple-500, #7E56E4)' : 'transparent',
                    transition: 'all 0.2s ease'
                  }}
                >
                  <i className="bi bi-credit-card" style={{ marginRight: '6px' }} />
                  Cashfree
                </button>
              </div>

              {/* Summary Tab Content */}
              {activePaymentTab === 'summary' && (
              <>

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

              {/* ── Wallet section ── */}
              <div className="pp-wallet">
                <div className="pp-wallet__header">
                  <i className="bi bi-wallet2 pp-wallet__icon" />
                  <div className="pp-wallet__info">
                    <p className="pp-wallet__title">Wallet Balance</p>
                    <span className={`pp-wallet__avail${loadingWallet ? ' pp-wallet__avail--loading' : ''}`}>
                      {loadingWallet ? 'Loading...' : `₹${formatCurrency(walletBalance)} available`}
                    </span>
                  </div>
                  {walletApplied && (
                    <button className="pp-wallet__remove-btn" onClick={handleRemoveWallet}>Remove</button>
                  )}
                </div>
                {!loadingWallet && walletBalance > 0 && !walletApplied && (
                  <>
                    <div className="pp-wallet__divider" />
                    <div className="pp-wallet__body">
                      <span className="pp-wallet__input-label">How much would you like to use?</span>
                      <div className="pp-wallet__input-row">
                        <div className="pp-wallet__input-wrap">
                          <span className="pp-wallet__input-prefix">₹</span>
                          <input
                            className="pp-wallet__input"
                            type="text"
                            inputMode="decimal"
                            placeholder="0.00"
                            value={walletInputAmount}
                            onChange={handleWalletInputChange}
                            disabled={walletApplied}
                          />
                          <button className="pp-wallet__max-btn" onClick={handleWalletMax} type="button">MAX</button>
                        </div>
                        <button
                          className="pp-wallet__apply-btn"
                          onClick={handleApplyWallet}
                          disabled={!walletInputAmount || walletApplied}
                        >
                          Apply
                        </button>
                      </div>
                      {walletInputError && (
                        <p className="pp-wallet__error-note">
                          <i className="bi bi-exclamation-circle-fill" /> {walletInputError}
                        </p>
                      )}
                    </div>
                  </>
                )}
                {walletApplied && walletRedeemAmount > 0 && (
                  <p className="pp-wallet__redeem-note">
                    <i className="bi bi-check-circle-fill" />
                    ₹{formatCurrency(walletRedeemAmount)} will be redeemed from wallet
                  </p>
                )}
                {!loadingWallet && walletBalance <= 0 && (
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

              </>
              )}

              {/* Cashfree Tab Content */}
              {activePaymentTab === 'cashfree' && (
              <div style={{ padding: '16px 0' }}>
                <div style={{
                  padding: '16px',
                  background: 'rgba(126, 86, 228, 0.05)',
                  borderRadius: '8px',
                  border: '1px solid rgba(126, 86, 228, 0.1)',
                  marginBottom: '16px'
                }}>
                  <h4 style={{ margin: '0 0 8px 0', fontSize: '13px', fontWeight: '600', color: 'var(--text-primary, #1f2937)' }}>
                    <i className="bi bi-info-circle" style={{ marginRight: '6px', color: 'var(--purple-500, #7E56E4)' }} />
                    Secure Payment Gateway
                  </h4>
                  <p style={{ margin: '0', fontSize: '12px', color: 'var(--text-secondary, #6B7280)' }}>
                    Your payment will be processed securely through Cashfree. You'll be redirected to complete the payment.
                  </p>
                </div>

                {/* Order Summary for Cashfree */}
                <div style={{ marginBottom: '12px' }}>
                  <h4 style={{ fontSize: '12px', fontWeight: '600', color: 'var(--text-secondary, #6B7280)', margin: '0 0 8px 0' }}>Order Total</h4>
                  <div style={{
                    padding: '12px',
                    background: 'var(--bg-secondary, #f8f9fa)',
                    borderRadius: '6px',
                    border: '1px solid var(--border-primary, #e6eaf0)'
                  }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '6px', fontSize: '12px' }}>
                      <span>Subtotal</span>
                      <span>₹{formatCurrency(calculateSubtotal())}</span>
                    </div>
                    {discountApplied && (
                      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '6px', fontSize: '12px', color: 'var(--success, #10b981)' }}>
                        <span>Discount</span>
                        <span>− ₹{formatCurrency(discountAmount)}</span>
                      </div>
                    )}
                    {walletApplied && walletRedeemAmount > 0 && (
                      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '6px', fontSize: '12px', color: 'var(--success, #10b981)' }}>
                        <span>Wallet</span>
                        <span>− ₹{formatCurrency(walletRedeemAmount)}</span>
                      </div>
                    )}
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '6px', fontSize: '12px' }}>
                      <span>GST ({gstValus}%)</span>
                      <span>₹{formatCurrency(calculateGSTAmount())}</span>
                    </div>
                    <div style={{
                      borderTop: '1px solid var(--border-primary, #e6eaf0)',
                      paddingTop: '8px',
                      display: 'flex',
                      justifyContent: 'space-between',
                      fontSize: '13px',
                      fontWeight: '600',
                      color: 'var(--text-primary, #1f2937)'
                    }}>
                      <span>Total Amount</span>
                      <span>₹{formatCurrency(calculateFinalPayable())}</span>
                    </div>
                  </div>
                </div>

                {/* Cashfree Checkout Button */}
                <button
                  onClick={() => {
                    if (!cashfreeSessionId) {
                      toast.error('Payment session not initialized. Please try again.');
                      return;
                    }
                    openCashfreeCheckout(cashfreeSessionId, cashfreeInvoiceId);
                  }}
                  disabled={processingPayment || !cashfreeSessionId}
                  style={{
                    width: '100%',
                    padding: '12px 16px',
                    background: processingPayment ? 'var(--text-tertiary, #8a93a2)' : 'var(--purple-500, #7E56E4)',
                    color: '#fff',
                    border: 'none',
                    borderRadius: '6px',
                    fontSize: '13px',
                    fontWeight: '600',
                    cursor: processingPayment ? 'not-allowed' : 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    gap: '8px',
                    transition: 'all 0.2s ease',
                    opacity: processingPayment || !cashfreeSessionId ? 0.6 : 1
                  }}
                >
                  {processingPayment ? (
                    <>
                      <span style={{ width: '14px', height: '14px', border: '2px solid #fff', borderTop: 'transparent', borderRadius: '50%', animation: 'spin 0.6s linear infinite' }} />
                      Processing...
                    </>
                  ) : (
                    <>
                      <i className="bi bi-lock-fill" />
                      Pay ₹{formatCurrency(calculateFinalPayable())} with Cashfree
                    </>
                  )}
                </button>

                <p style={{ fontSize: '11px', color: 'var(--text-tertiary, #8a93a2)', marginTop: '12px', textAlign: 'center' }}>
                  <i className="bi bi-shield-check" style={{ marginRight: '4px' }} />
                  Your payment is secure and encrypted
                </p>
              </div>
              )}

            </div>
          </div>

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
          {/* Company Details Section */}
          {companyDetails && (
            <div className="popup-section" style={{ backgroundColor: 'rgba(126, 86, 228, 0.05)', borderRadius: '8px', padding: '12px', marginBottom: '16px', border: '1px solid rgba(126, 86, 228, 0.1)' }}>
              <h4 className="popup-section-title" style={{ marginTop: 0 }}>Billing Information</h4>
              <div className="popup-row"><span style={{ fontWeight: '500' }}>Company Name</span><span>{companyDetails.companyName}</span></div>
              {companyDetails.gstNumber && (
                <div className="popup-row"><span style={{ fontWeight: '500' }}>GST Number</span><span>{companyDetails.gstNumber}</span></div>
              )}
              {companyDetails.address && (
                <div className="popup-row"><span style={{ fontWeight: '500' }}>Address</span><span>{companyDetails.address}</span></div>
              )}
              <div className="popup-divider" style={{ margin: '8px 0' }} />
            </div>
          )}

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
            <Button
              text={processingPayment ? 'Opening payment...' : `Pay ₹${formatCurrency(calculateFinalPayable())} Now`}
              variant="primary"
              classN="medium"
              onClick={handlePayment}
              disabled={processingPayment}
            />
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
            <Button text="Done" variant="primary" onClick={() => { setIsSuccessPopupOpen(false); navigate('/tenantplanss'); }} />
          </div>
        </div>
      </PopUp>

    </TenantLayout>
  );
};

export default BuyPlan;