import React, { useState, useEffect, useRef } from 'react';
import PageHeader from '../../../components/common/pageheader/PageHeader';
import TenantLayout from '../../../layouts/TenantLayout';
import Button from '../../../components/common/button/Button';
import { addonServices, companyDetailsServices, tenantServices, vendorLicenseServices } from '../../../services/apiService';
import { toast } from 'react-toastify';
import { useLocation, useNavigate } from 'react-router-dom';
import './BuyPlan.css';
import '../../../theme/LightTheme.css';
import PopUp from '../../../components/common/popups/PopUp';
import AddOnCard from '../../../components/common/addOnCard/AddOnCard';
import InputField from '../../../components/common/inputfield/InputField';
import formConfig from '../../../config/formConfig';

const BuyPlan = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const isRenew  = location.pathname === '/Renew/BuyPlan';
  const isAddon  = location.pathname === '/Addon/BuyPlan';
  const isNormal = !isRenew && !isAddon;

  const [loadingAddons, setLoadingAddons]     = useState(false);
  const [addons, setAddons]                   = useState([]);
  const [selectedPlan, setSelectedPlan]       = useState(null);
  const [selectedAddons, setSelectedAddons]   = useState([]);
  const [additionalUsers, setAdditionalUsers] = useState(0);
  const [gstRate, setGstRate]                 = useState(0);

  const [couponCode, setCouponCode]         = useState('');
  const [referralCode, setReferralCode]     = useState('');
  const [couponOpen, setCouponOpen]         = useState(false);
  const [referralOpen, setReferralOpen]     = useState(false);
  const [detailsOpen, setDetailsOpen]       = useState(false);
  const [discountApplied, setDiscountApplied] = useState(false);
  const [discountAmount, setDiscountAmount]   = useState(0);
  const [discountType, setDiscountType]       = useState(null);
  const [discountValue, setDiscountValue]     = useState(0);

  const [walletBalance, setWalletBalance]       = useState(0);
  const [walletInputAmount, setWalletInputAmount] = useState('');
  const [walletRedeemAmount, setWalletRedeemAmount] = useState(0);
  const [walletApplied, setWalletApplied]       = useState(false);
  const [walletInputError, setWalletInputError] = useState('');
  const [loadingWallet, setLoadingWallet]       = useState(true);

  const [cashfreeInvoiceId, setCashfreeInvoiceId] = useState(null);
  const [processingPayment, setProcessingPayment] = useState(false);
  const [isPaymentPopupOpen, setIsPaymentPopupOpen] = useState(false);
  const [isSuccessPopupOpen, setIsSuccessPopupOpen] = useState(false);
  const [paymentDetails, setPaymentDetails]     = useState(null);

  const [activePlan, setActivePlan]             = useState(null);
  const [creditAmount, setCreditAmount]         = useState(0);
  const [creditLabel, setCreditLabel]           = useState('');
  const [hasExistingPlan, setHasExistingPlan]   = useState(false);

  // ── Company details ───────────────────────────────────────
  const [companyDetails, setCompanyDetails] = useState({
    gstNumber: '', companyName: '', address: '',
  });
  const [companyFormData, setCompanyFormData] = useState({
    gstNumber: '', companyName: '', address: '',
  });
  const [showCompanyPopup, setShowCompanyPopup] = useState(false);
  const [isUpdate, setIsUpdate]                 = useState(false);
  const [loading, setLoading]                   = useState(false);

  const hidePromo = isRenew || isAddon || (isNormal && hasExistingPlan);

  const userId   = localStorage.getItem("user_id");
  const tenantId = localStorage.getItem("tenant_id");

  const addonFetchedForPlanId = useRef(null);

  useEffect(() => {
    fetchGST();
    fetchWallet();
    fetchActivePlan();
    fetchCompanyDetails();
    if (location.state?.plan) setSelectedPlan(location.state.plan);
  }, [location.state]);

  useEffect(() => {
    if (!selectedPlan) return;
    if (addonFetchedForPlanId.current === selectedPlan.id) return;
    addonFetchedForPlanId.current = selectedPlan.id;
    fetchAddons(selectedPlan);
  }, [selectedPlan]);

  const fetchGST = async () => {
    try {
      const res = await tenantServices.getLatestGst();
      setGstRate(res.data?.rate || 0);
    } catch {
      toast.error("Failed to fetch GST");
    }
  };

  const fetchWallet = async () => {
    setLoadingWallet(true);
    try {
      const res = await tenantServices.getWalletDetails(tenantId);
      setWalletBalance(res.data?.wallet?.balance || res.data?.wallet?.availableBalance || 0);
    } catch {
      setWalletBalance(0);
    } finally {
      setLoadingWallet(false);
    }
  };

  const fetchActivePlan = async () => {
    try {
      const res = await tenantServices.getActivePlan(tenantId);
      if (!res.data?.plan) return;

      setActivePlan(res.data);
      setHasExistingPlan(true);

      const p = res.data.plan;
      const mappedPlan = {
        id:       p.planId,
        name:     p.planName,
        price:    p.price?.amount || 0,
        code:     p.planCode,
        features: [],
      };

      if ((isRenew || isAddon) && !location.state?.plan) {
        setSelectedPlan(mappedPlan);
      }

      const endDateCheck = res.data.endDate ? new Date(res.data.endDate) : null;
      const todayCheck   = new Date();
      todayCheck.setHours(0, 0, 0, 0);
      const planIsActive = endDateCheck ? endDateCheck >= todayCheck : true;

      if ((isRenew || isAddon || planIsActive) && res.data.addons?.length > 0) {
        const ownedAddons = res.data.addons.map(a => ({
          id:          a.addonId,
          name:        a.name,
          subtitle:    a.code,
          status:      a.status?.toUpperCase(),
          price:       a.unitPrice,
          period:      `per ${a.unit}`,
          stats:       { code: a.code, subscribers: "-", revenue: "-" },
          features:    [`Currency: ${a.currency}`],
          quantity:    a.quantity || 1,
          minQuantity: a.quantity || 1,
        }));
        setSelectedAddons(ownedAddons);
      }

      if (isNormal && planIsActive) {
        const planCredit  = p.price?.amount || 0;
        const addonCredit = (res.data.addons || []).reduce(
          (t, a) => t + (a.unitPrice || 0) * (a.quantity || 1), 0
        );
        setCreditAmount(planCredit + addonCredit);
        setCreditLabel(p.planName || 'Previous plan');
      }

    } catch {
      setActivePlan(null);
    }
  };

  const fetchAddons = async (plan) => {
    setLoadingAddons(true);
    try {
      const res = await addonServices.getaddonbyplan({ planId: plan.id });
      const availableAddons = res.data.map(a => ({
        id:       a.id,
        name:     a.name,
        subtitle: a.code,
        status:   a.status?.toUpperCase(),
        price:    a.unit_price,
        period:   `per ${a.unit}`,
        stats:    { code: a.code, subscribers: "-", revenue: "-" },
        features: [`Currency: ${a.currency}`],
      }));

      const availableIds = new Set(availableAddons.map(a => a.id));
      const ownedNotInList = selectedAddons
        .filter(a => !availableIds.has(a.id))
        .map(a => ({
          id:       a.id,
          name:     a.name,
          subtitle: a.subtitle,
          status:   a.status,
          price:    a.price,
          period:   a.period,
          stats:    a.stats,
          features: a.features,
        }));

      setAddons([...availableAddons, ...ownedNotInList]);
    } catch {
      toast.error("Failed to load add-ons");
    } finally {
      setLoadingAddons(false);
    }
  };


  const fetchCompanyDetails = async () => {
    try {
      const res = await tenantServices.getCompanyDetails(tenantId);
      if (res.data) {
        setCompanyDetails({
          gstNumber:   res.data.gstNumber   || '',
          companyName: res.data.companyName || '',
          address:     res.data.address     || '',
        });
        if (res.data.companyName || res.data.gstNumber || res.data.address) {
          setIsUpdate(true);
        }
      }
    } catch {
      setCompanyDetails({ gstNumber: '', companyName: '', address: '' });
    }
  };

  // ── Company details handlers ──────────────────────────────
  const handleOpenCompanyPopup = () => {
    // Initialize form data with current company details
    setCompanyFormData({ ...companyDetails });
    setShowCompanyPopup(true);
  };

  const handleCloseCompanyPopup = () => {
    // Reset form data and close popup
    setCompanyFormData({ gstNumber: '', companyName: '', address: '' });
    setShowCompanyPopup(false);
  };

  const handleCompanyChange = (e) => {
    const { name, value } = e.target;
    setCompanyFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleCompanySubmit = async () => {
    try {
      setLoading(true);
      const payload = {
        tenantId:    localStorage.getItem("tenant_id"),
        gstNumber:   companyFormData.gstNumber,
        companyName: companyFormData.companyName,
        address:     companyFormData.address,
      };
      await companyDetailsServices.upsertCompanyDetails(payload);
      
      // Update the main company details after successful submission
      setCompanyDetails({ ...companyFormData });
      
      if (isUpdate) {
        toast.success("Company details updated successfully");
      } else {
        toast.success("Company details submitted successfully");
        setIsUpdate(true);
      }
      // Refresh company details after update
      await fetchCompanyDetails();
      handleCloseCompanyPopup();
    } catch {
      toast.error("Failed to Submit company details");
    } finally {
      setLoading(false);
    }
  };

  const handleBuyAddon = (addon) => {
    setSelectedAddons(prev => {
      const existing = prev.find(a => a.id === addon.id);
      if (existing) {
        return prev.map(a => a.id === addon.id ? { ...a, quantity: (a.quantity || 0) + 1 } : a);
      }
      return [...prev, { ...addon, quantity: 1, minQuantity: 0 }];
    });
    if (addon.stats?.code === 'USER') {
      setAdditionalUsers(p => p + parseInt(addon.unit || 0));
    }
  };

  const handleRemoveAddon = (addonId) => {
    setSelectedAddons(prev => {
      const a = prev.find(x => x.id === addonId);
      if (!a) return prev;
      if (a.stats?.code === 'USER') {
        setAdditionalUsers(p => Math.max(0, p - parseInt(a.unit || 0)));
      }
      const floor = a.minQuantity || 0;
      if (a.quantity > floor + 1) {
        return prev.map(x => x.id === addonId ? { ...x, quantity: x.quantity - 1 } : x);
      }
      if (a.quantity === floor + 1) {
        return floor > 0
          ? prev.map(x => x.id === addonId ? { ...x, quantity: floor } : x)
          : prev.filter(x => x.id !== addonId);
      }
      return prev;
    });
  };

  const extraQty = (a) => Math.max(0, (a.quantity || 0) - (a.minQuantity || 0));

  const calculateSubtotal = () => {
    const planPrice = isAddon ? 0 : (selectedPlan?.price || 0);

    let addonsCharge = 0;
    if (isAddon) {
      addonsCharge = selectedAddons.reduce((t, a) => t + (a.price || 0) * extraQty(a), 0);
    } else if (isRenew) {
      addonsCharge = selectedAddons.reduce((t, a) => t + (a.price || 0) * (a.quantity || 1), 0);
    } else {
      addonsCharge = selectedAddons.reduce((t, a) => t + (a.price || 0) * extraQty(a), 0);
    }

    const raw = planPrice + addonsCharge;
    return isNormal ? Math.max(0, raw - creditAmount) : raw;
  };

  const calculateAfterDiscount = () =>
    discountApplied ? Math.max(0, calculateSubtotal() - discountAmount) : calculateSubtotal();

  const calculateGSTAmount = () => (calculateAfterDiscount() * gstRate) / 100;

  const calculatePreWalletTotal = () => calculateAfterDiscount() + calculateGSTAmount();

  const calculateFinalPayable = () =>
    walletApplied
      ? Math.max(0, calculatePreWalletTotal() - walletRedeemAmount)
      : calculatePreWalletTotal();

  const calculateTotalUsers = () => {
    const base = parseInt(selectedPlan?.features?.[0]?.split(': ')[1] || '0');
    return base + additionalUsers;
  };

  const fmt = (amount) =>
    new Intl.NumberFormat("en-IN", { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(amount || 0);

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
        if (calc > subtotal) { toast.error("Coupon can't be applied"); return; }
      } else if (data.discountType === "PERCENTAGE") {
        calc = (subtotal * data.discountValue) / 100;
      }
      if (data.available === false) { toast.error(data.message); return; }
      toast.success(data.message);
      setDiscountAmount(calc);
      setDiscountApplied(true);
    } catch {
      setDiscountApplied(false); setDiscountAmount(0);
      toast.error("Failed to apply coupon");
    }
  };

  const handleRemoveDiscount = () => {
    setDiscountApplied(false); setDiscountAmount(0); setDiscountType(null); setCouponCode('');
    toast.info("Discount removed", { autoClose: 1000 });
  };

  const handleRefral = async () => {
    try {
      if (!referralCode?.trim()) { toast.error("Please enter a referral code."); return; }
      const res = await vendorLicenseServices.getRefralCode({ referralCode: referralCode.trim() });
      const data = res.data;
      if (data.status === "VALID")        toast.success(data.message || "Referral code applied!");
      else if (data.status === "INVALID") toast.error(`${data.status}: ${data.message}`);
      else                                toast.info(data.message);
    } catch {
      toast.error("Failed to validate referral code.");
    }
  };

  const handleWalletInputChange = (e) => {
    const val = e.target.value;
    if (val !== '' && !/^\d*\.?\d{0,2}$/.test(val)) return;
    setWalletInputAmount(val);
    setWalletInputError('');
  };

  const handleWalletMax = () => {
    const usable = Math.min(walletBalance, calculatePreWalletTotal(), WALLET_MAX_LIMIT);
    setWalletInputAmount(usable.toFixed(2));
    setWalletInputError('');
  };

  const handleApplyWallet = () => {
    const typed = parseFloat(walletInputAmount);
    if (!walletInputAmount || isNaN(typed) || typed <= 0) {
      setWalletInputError('Please enter an amount to use.'); return;
    }
    if (typed > walletBalance) {
      setWalletInputError(`Exceeds your balance of ₹${fmt(walletBalance)}`); return;
    }
    if (typed > WALLET_MAX_LIMIT) {
      setWalletInputError(`Maximum wallet usage is ₹${fmt(WALLET_MAX_LIMIT)} per transaction`); return;
    }
    if (typed > calculatePreWalletTotal()) {
      setWalletInputError(`Can't exceed total of ₹${fmt(calculatePreWalletTotal())}`); return;
    }
    setWalletInputError('');
    setWalletRedeemAmount(typed);
    setWalletApplied(true);
    toast.success(`₹${fmt(typed)} wallet credits applied`);
  };

  const handleRemoveWallet = () => {
    setWalletApplied(false); setWalletRedeemAmount(0);
    setWalletInputAmount(''); setWalletInputError('');
    toast.info("Wallet credits removed", { autoClose: 1000 });
  };

  // Open payment popup and refresh company details + GST
  const handleOpenPaymentPopup = async () => {
    await fetchCompanyDetails();
    await fetchGST();
    setIsPaymentPopupOpen(true);
  };

  // Check if company details are filled
  const isCompanyDetailsFilled = () => {
    return companyDetails.companyName?.trim() && companyDetails.address?.trim();
  };

  // Handle payment with company details validation
  const handlePaymentWithValidation = async () => {
    if (!isCompanyDetailsFilled()) {
      toast.error("Please fill the company details before proceeding.");
      return;
    }
    await handlePayment();
  };

  const CASHFREE_ENV = "sandbox";
  const WALLET_MAX_LIMIT = 300;

  const handlePayment = async () => {
    try {
      if (!selectedPlan && !isAddon) { toast.error("Please select a plan."); return; }
      setProcessingPayment(true);

      const payload = {
        tenant_id:     parseInt(tenantId),
        plan_id:       selectedPlan?.id,
        coupon_code:   discountApplied && couponCode ? couponCode.trim() : null,
        referral_code: referralCode ? referralCode.trim() : null,
        wallet_amount: walletApplied ? Number(walletRedeemAmount.toFixed(2)) : 0.00,
        addons: selectedAddons
          .map(a => ({
            addon_id: a.id,
            quantity: isAddon
              ? Math.max(0, (a.quantity || 0) - (a.minQuantity || 0))
              : (a.quantity || 1),
          }))
          .filter(a => a.quantity > 0),
      };

      const res = await tenantServices.processBilling(payload, tenantId);
      const { payment_session_id, invoice_id } = res.data;
      if (!payment_session_id) throw new Error("No payment_session_id received.");

      setCashfreeInvoiceId(invoice_id);
      setIsPaymentPopupOpen(false);
      openCashfreeCheckout(payment_session_id, invoice_id);

    } catch (error) {
      toast.error(error?.response?.data?.message || "Failed to initiate payment. Please try again.");
    } finally {
      setProcessingPayment(false);
    }
  };

  // ── Opens Cashfree checkout in the SAME tab ───────────────
  const openCashfreeCheckout = (paymentSessionId, invoiceId) => {
    localStorage.setItem('cf_invoice_id', String(invoiceId));
    localStorage.setItem('cf_plan_id',    String(selectedPlan?.id || ''));
    localStorage.removeItem('cf_status');

    const initCheckout = () => {
      const cf = window.Cashfree({ mode: CASHFREE_ENV });
      cf.checkout({ paymentSessionId, redirectTarget: "_self" })
        .catch((e) => {
          toast.error("Payment failed: " + (e.message || "Unknown error"));
        });
    };

    // If SDK already loaded, use it directly
    if (window.Cashfree) {
      initCheckout();
      return;
    }

    // Otherwise inject the script tag and wait for it to load
    const script = document.createElement("script");
    script.src = "https://sdk.cashfree.com/js/v3/cashfree.js";
    script.async = true;
    script.onload = initCheckout;
    script.onerror = () => toast.error("Failed to load payment SDK. Please check your connection.");
    document.head.appendChild(script);
  };

  const checkPaymentStatus = async (invoiceId) => {
    try {
      toast.info("Verifying payment...");
      const res    = await tenantServices.getInvoiceStatus(invoiceId);
      const status = res.data?.status || res.data?.payment_status;
      if (status === 'PAID' || status === 'paid' || status === 'SUCCESS') {
        handleCashfreeSuccess(invoiceId);
      } else if (status === 'PENDING') {
        toast.info("Payment is pending. You will be notified once confirmed.");
      } else {
        toast.error("Payment was not completed. Please try again.");
      }
    } catch {
      toast.warn("Could not verify payment automatically. Please check your subscription status.");
    }
  };

  const handleCashfreeSuccess = async (invoiceId) => {
    try {
      toast.info("Activating your subscription...");
      const res = await vendorLicenseServices.subscribePlan({
        tenant_id:  parseInt(tenantId),
        plan_id:    selectedPlan?.id,
        invoice_id: parseInt(invoiceId || cashfreeInvoiceId),
        addons: selectedAddons
          .map(a => ({
            addon_id: a.id,
            quantity: isAddon
              ? Math.max(0, (a.quantity || 0) - (a.minQuantity || 0))
              : (a.quantity || 1),
          }))
          .filter(a => a.quantity > 0),
        discount: discountApplied ? Number(discountAmount.toFixed(2)) : 0.00,
      });
      setPaymentDetails(res.data);
      setIsSuccessPopupOpen(true);
      localStorage.removeItem('cf_invoice_id');
      localStorage.removeItem('cf_plan_id');
    } catch {
      toast.error("Payment received but subscription activation failed. Please contact support.");
    }
  };

  const renderAddonCards = () => {
    if (loadingAddons) return <p>Loading add-ons...</p>;
    if (addons.length === 0) return <p>No add-ons available at this time.</p>;

    return addons.map((addon) => {
      const sel      = selectedAddons.find(a => a.id === addon.id);
      const ownedQty = sel?.minQuantity || 0;
      const totalQty = sel?.quantity    || 0;
      const extra    = Math.max(0, totalQty - ownedQty);

      return (
        <AddOnCard
          key={addon.id}
          plan={addon}
          onBuy={handleBuyAddon}
          onRemove={handleRemoveAddon}
          quantity={isAddon ? extra : totalQty}
          ownedQty={ownedQty}
        />
      );
    });
  };

  const renderAddonSummaryRows = () => {
    if (isAddon) {
      return (
        <>
          {selectedAddons.filter(a => (a.minQuantity || 0) > 0).map(a => (
            <div key={`owned-${a.id}`} className="pp-line pp-line--muted">
              <span className="pp-line__icon pp-line__icon--addon"><i className="bi bi-puzzle" /></span>
              <span className="pp-line__label">
                {a.name} ×{a.minQuantity}
                <span className="pp-line__meta">Currently active</span>
              </span>
              <span className="pp-line__value pp-line__value--muted">₹0.00</span>
            </div>
          ))}
          {selectedAddons.filter(a => extraQty(a) > 0).map(a => (
            <div key={`new-${a.id}`} className="pp-line">
              <span className="pp-line__icon pp-line__icon--addon"><i className="bi bi-puzzle" /></span>
              <span className="pp-line__label">
                {a.name} ×{extraQty(a)}
                <span className="pp-line__meta pp-line__meta--new">New addition</span>
              </span>
              <span className="pp-line__value">₹{fmt((a.price || 0) * extraQty(a))}</span>
            </div>
          ))}
        </>
      );
    }

    if (isRenew) {
      return (
        <>
          {selectedAddons.map(a => {
            const owned = a.minQuantity || 0;
            const extra = Math.max(0, (a.quantity || 0) - owned);
            return (
              <React.Fragment key={a.id}>
                {owned > 0 && (
                  <div className="pp-line">
                    <span className="pp-line__icon pp-line__icon--addon"><i className="bi bi-puzzle" /></span>
                    <span className="pp-line__label">
                      {a.name} ×{owned}
                      <span className="pp-line__meta">Renewing</span>
                    </span>
                    <span className="pp-line__value">₹{fmt((a.price || 0) * owned)}</span>
                  </div>
                )}
                {extra > 0 && (
                  <div className="pp-line">
                    <span className="pp-line__icon pp-line__icon--addon"><i className="bi bi-puzzle" /></span>
                    <span className="pp-line__label">
                      {a.name} ×{extra}
                      <span className="pp-line__meta pp-line__meta--new">New addition</span>
                    </span>
                    <span className="pp-line__value">₹{fmt((a.price || 0) * extra)}</span>
                  </div>
                )}
              </React.Fragment>
            );
          })}
        </>
      );
    }

    return (
      <>
        {selectedAddons.filter(a => (a.minQuantity || 0) > 0).map(a => (
          <div key={`owned-${a.id}`} className="pp-line pp-line--muted">
            <span className="pp-line__icon pp-line__icon--addon"><i className="bi bi-puzzle" /></span>
            <span className="pp-line__label">
              {a.name} ×{a.minQuantity}
              <span className="pp-line__meta">Currently active</span>
            </span>
            <span className="pp-line__value pp-line__value--muted">₹0.00</span>
          </div>
        ))}
        {selectedAddons.filter(a => extraQty(a) > 0).map(a => (
          <div key={`new-${a.id}`} className="pp-line">
            <span className="pp-line__icon pp-line__icon--addon"><i className="bi bi-puzzle" /></span>
            <span className="pp-line__label">
              {a.name} ×{extraQty(a)}
              <span className="pp-line__meta pp-line__meta--new">New addition</span>
            </span>
            <span className="pp-line__value">₹{fmt((a.price || 0) * extraQty(a))}</span>
          </div>
        ))}
      </>
    );
  };

  return (
    <TenantLayout>
      <div className="buy-plan-content">
        <div className="buy-plan-layout">

          <div className="addons-section">
            <div style={{ display: "flex", gap: "5px", alignItems: "center" }}>
              <Button isBack variant="back" text="" />
              <PageHeader title={isRenew ? "Your Add-ons" : "Available Add-ons"} />
            </div>
            <div className="plans-grid">
              {renderAddonCards()}
            </div>
          </div>

          <div className="payment-section">
            <div className="pp-root">

              <div className="pp-header">
                <div className="pp-header__left">
                  <div className="pp-header__icon-wrap">
                    <i className="bi bi-receipt-cutoff" />
                  </div>
                  <div>
                    <h3 className="pp-header__title">Order Summary</h3>
                    <p className="pp-header__sub">
                      {isRenew ? 'Renewing subscription' : isAddon ? 'Adding to your plan' : 'New subscription'}
                    </p>
                  </div>
                </div>
                {selectedPlan && (
                  <div className="pp-header__plan-chip">
                    <span className="pp-header__plan-dot" />
                    {selectedPlan.name}
                  </div>
                )}
              </div>

              <div className="pp-hero">
                <div className="pp-hero__label">Amount Payable</div>
                <div className="pp-hero__amount">₹{fmt(calculateFinalPayable())}</div>
                {gstRate > 0 && (
                  <div className="pp-hero__note">Inclusive of {gstRate}% GST</div>
                )}
              </div>

              <div className="pp-details">
                <button
                  className={`pp-details__toggle ${detailsOpen ? 'pp-details__toggle--open' : ''}`}
                  onClick={() => setDetailsOpen(o => !o)}
                >
                  <span className="pp-details__toggle-left">
                    <i className="bi bi-list-ul" />
                    View charge breakdown
                  </span>
                  <span className="pp-details__toggle-right">
                    <span className="pp-details__toggle-count">
                      {[
                        !isAddon && selectedPlan,
                        selectedAddons.length > 0,
                        isNormal && creditAmount > 0,
                        discountApplied,
                        walletApplied,
                      ].filter(Boolean).length} items
                    </span>
                    <i className={`bi bi-chevron-${detailsOpen ? 'up' : 'down'} pp-details__chevron`} />
                  </span>
                </button>

                {detailsOpen && (
                  <div className="pp-details__body">

                    {!isAddon && selectedPlan && (
                      <div className="pp-line">
                        <span className="pp-line__icon pp-line__icon--plan"><i className="bi bi-box" /></span>
                        <span className="pp-line__label">
                          {selectedPlan.name}
                          <span className="pp-line__meta">Base plan</span>
                        </span>
                        <span className="pp-line__value">₹{fmt(selectedPlan?.price || 0)}</span>
                      </div>
                    )}

                    {renderAddonSummaryRows()}

                    {isNormal && creditAmount > 0 && (
                      <div className="pp-line pp-line--credit">
                        <span className="pp-line__icon pp-line__icon--credit"><i className="bi bi-arrow-counterclockwise" /></span>
                        <span className="pp-line__label">
                          Plan credit
                          <span className="pp-line__meta">{creditLabel}</span>
                        </span>
                        <span className="pp-line__value pp-line__value--green">−₹{fmt(creditAmount)}</span>
                      </div>
                    )}

                    <div className="pp-subtotal-row">
                      <span>Subtotal</span>
                      <span>₹{fmt(calculateSubtotal())}</span>
                    </div>

                    {discountApplied && (
                      <div className="pp-line pp-line--saving">
                        <span className="pp-line__icon pp-line__icon--coupon"><i className="bi bi-tag-fill" /></span>
                        <span className="pp-line__label">
                          {discountType === 'PERCENTAGE' ? `Coupon — ${discountValue}% off` : 'Coupon discount'}
                          <span className="pp-line__meta">Promo applied</span>
                        </span>
                        <span className="pp-line__value pp-line__value--green">−₹{fmt(discountAmount)}</span>
                      </div>
                    )}

                    <div className="pp-line">
                      <span className="pp-line__icon pp-line__icon--tax"><i className="bi bi-percent" /></span>
                      <span className="pp-line__label">
                        GST
                        <span className="pp-line__meta">{gstRate}% on taxable amount</span>
                      </span>
                      <span className="pp-line__value">₹{fmt(calculateGSTAmount())}</span>
                    </div>

                    {walletApplied && walletRedeemAmount > 0 && (
                      <div className="pp-line pp-line--saving">
                        <span className="pp-line__icon pp-line__icon--wallet"><i className="bi bi-wallet2" /></span>
                        <span className="pp-line__label">
                          Wallet credits
                          <span className="pp-line__meta">Redeemed from balance</span>
                        </span>
                        <span className="pp-line__value pp-line__value--green">
                          −₹{fmt(walletRedeemAmount)}
                        </span>
                      </div>
                    )}

                    <div className="pp-total-row">
                      <span>Total payable</span>
                      <span>₹{fmt(calculateFinalPayable())}</span>
                    </div>

                  </div>
                )}
              </div>

              <div className="pp-wallet">
                <div className="pp-wallet__header">
                  <i className="bi bi-wallet2 pp-wallet__icon" />
                  <div className="pp-wallet__info">
                    <p className="pp-wallet__title">Wallet Balance</p>
                    <span className={`pp-wallet__avail${loadingWallet ? ' pp-wallet__avail--loading' : ''}`}>
                      {loadingWallet ? 'Loading...' : `₹${fmt(walletBalance)} available`}
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
                      <span className="pp-wallet__input-label">How much would you like to use? <span style={{fontSize:'11px',color:'var(--text-tertiary)'}}>(max ₹{fmt(Math.min(walletBalance, WALLET_MAX_LIMIT))})</span></span>
                      <div className="pp-wallet__input-row">
                        <div className="pp-wallet__input-wrap">
                          <span className="pp-wallet__input-prefix">₹</span>
                          <input
                            className="pp-wallet__input"
                            type="text" inputMode="decimal" placeholder="0.00"
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
                    ₹{fmt(walletRedeemAmount)} will be redeemed from wallet
                  </p>
                )}
                {!loadingWallet && walletBalance <= 0 && (
                  <p className="pp-wallet__empty-note">No wallet credits available</p>
                )}
              </div>

              {!hidePromo && (
                <>
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
                            type="text" name="couponCode" value={couponCode}
                            onChange={(e) => !discountApplied && setCouponCode(e.target.value)}
                            placeholder="Enter coupon code" readOnly={discountApplied}
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
                            {discountType === 'PERCENTAGE'
                              ? `${discountValue}% off applied`
                              : `₹${fmt(discountAmount)} off applied`
                            }
                          </p>
                        )}
                      </div>
                    )}
                  </div>

                  <div className="pp-promo">
                    <button className="pp-promo__toggle" onClick={() => setReferralOpen(o => !o)}>
                      <span><i className="bi bi-person-plus" /> Have a referral code?</span>
                      <i className={`bi bi-chevron-${referralOpen ? 'up' : 'down'} pp-chevron`} />
                    </button>
                    {referralOpen && (
                      <div className="pp-promo__body">
                        <div className="pp-promo__input-row">
                          <input
                            className="pp-input" type="text" name="referralCode" value={referralCode}
                            onChange={(e) => setReferralCode(e.target.value)}
                            placeholder="Enter referral code"
                            onKeyDown={(e) => e.key === 'Enter' && handleRefral()}
                          />
                          <button className="pp-apply-btn" onClick={handleRefral}>Apply</button>
                        </div>
                      </div>
                    )}
                  </div>
                </>
              )}

              <div className="pp-action">
                <button className="pp-proceed-btn" onClick={handleOpenPaymentPopup}>
                  <div className="pp-proceed-btn__inner">
                    <span className="pp-proceed-btn__label">Proceed to Pay</span>
                    <span className="pp-proceed-btn__amount">₹{fmt(calculateFinalPayable())}</span>
                  </div>
                  <i className="bi bi-arrow-right pp-proceed-btn__arrow" />
                </button>
              </div>

            </div>
          </div>
        </div>
      </div>

      <PopUp
        isOpen={isPaymentPopupOpen}
        onClose={() => setIsPaymentPopupOpen(false)}
        title=""
        subtitle=""
        size="large"
      >
        <div className="inv">

          <div className="inv__scroll-body">

            <div className="inv__head">
              <div className="inv__head-left">
                <div className="inv__logo-wrap">
                  <i className="bi bi-receipt-cutoff inv__logo-icon" />
                </div>
                <div>
                  <div className="inv__brand">Tax Invoice</div>
                  <div className={`inv__status-chip inv__status-chip--${isRenew ? 'renew' : isAddon ? 'addon' : 'new'}`}>
                    {isRenew ? 'Renewal' : isAddon ? 'Add-on' : 'New Subscription'}
                  </div>
                </div>
              </div>
              <div className="inv__head-right">
                <div className="inv__date-label">Date</div>
                <div className="inv__date-val">
                  {new Date().toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' })}
                </div>
              </div>
            </div>

            <div className="inv__parties">
              <div className="inv__party">
                <div className="inv__party-label">
                  Billed To
                  <button
                    className="inv__edit-btn"
                    type="button"
                    title="Edit billing details"
                    onClick={handleOpenCompanyPopup}
                  >
                    <i className="bi bi-pencil-square" />
                  </button>
                </div>
                {companyDetails ? (
                  <>
                    <div className="inv__party-name">{companyDetails.companyName || 'Your Company'}</div>
                    {companyDetails.gstNumber && <div className="inv__party-meta">GST: {companyDetails.gstNumber}</div>}
                    {companyDetails.address   && (
                      <div className="inv__party-meta inv__party-meta--addr">{companyDetails.address}</div>
                    )}
                  </>
                ) : (
                  <div className="inv__party-name inv__party-name--loading">—</div>
                )}
              </div>
              <div className="inv__party inv__party--right">
                <div className="inv__party-label">Plan</div>
                <div className="inv__party-name">{selectedPlan?.name || '—'}</div>
                <div className="inv__party-meta">
                  {isRenew ? 'Renewal' : isAddon ? 'Add-on purchase' : 'New subscription'}
                </div>
              </div>
            </div>

            <div className="inv__table">
              <div className="inv__table-head">
                <span className="inv__th inv__th--desc">Description</span>
                <span className="inv__th inv__th--qty">Qty</span>
                <span className="inv__th inv__th--rate">Rate</span>
                <span className="inv__th inv__th--amount">Amount</span>
              </div>

              {!isAddon && selectedPlan && (
                <div className="inv__row">
                  <span className="inv__td inv__td--desc">
                    <span className="inv__item-name">{selectedPlan.name}</span>
                    <span className="inv__item-sub">Base subscription plan</span>
                  </span>
                  <span className="inv__td inv__td--qty">1</span>
                  <span className="inv__td inv__td--rate">₹{fmt(selectedPlan?.price || 0)}</span>
                  <span className="inv__td inv__td--amount">₹{fmt(selectedPlan?.price || 0)}</span>
                </div>
              )}

              {isAddon ? (
                <>
                  {selectedAddons.filter(a => (a.minQuantity || 0) > 0).map(a => (
                    <div key={`owned-${a.id}`} className="inv__row inv__row--muted">
                      <span className="inv__td inv__td--desc">
                        <span className="inv__item-name">{a.name}</span>
                        <span className="inv__item-sub">Currently active — not charged</span>
                      </span>
                      <span className="inv__td inv__td--qty">{a.minQuantity}</span>
                      <span className="inv__td inv__td--rate">₹{fmt(a.price || 0)}</span>
                      <span className="inv__td inv__td--amount inv__td--zero">₹0.00</span>
                    </div>
                  ))}
                  {selectedAddons.filter(a => extraQty(a) > 0).map(a => (
                    <div key={`new-${a.id}`} className="inv__row">
                      <span className="inv__td inv__td--desc">
                        <span className="inv__item-name">{a.name}</span>
                        <span className="inv__item-sub inv__item-sub--new">New addition</span>
                      </span>
                      <span className="inv__td inv__td--qty">{extraQty(a)}</span>
                      <span className="inv__td inv__td--rate">₹{fmt(a.price || 0)}</span>
                      <span className="inv__td inv__td--amount">₹{fmt((a.price || 0) * extraQty(a))}</span>
                    </div>
                  ))}
                </>
              ) : isRenew ? (
                selectedAddons.map(a => {
                  const owned = a.minQuantity || 0;
                  const extra = Math.max(0, (a.quantity || 0) - owned);
                  return (
                    <React.Fragment key={a.id}>
                      {owned > 0 && (
                        <div className="inv__row">
                          <span className="inv__td inv__td--desc">
                            <span className="inv__item-name">{a.name}</span>
                            <span className="inv__item-sub">Renewing</span>
                          </span>
                          <span className="inv__td inv__td--qty">{owned}</span>
                          <span className="inv__td inv__td--rate">₹{fmt(a.price || 0)}</span>
                          <span className="inv__td inv__td--amount">₹{fmt((a.price || 0) * owned)}</span>
                        </div>
                      )}
                      {extra > 0 && (
                        <div className="inv__row">
                          <span className="inv__td inv__td--desc">
                            <span className="inv__item-name">{a.name}</span>
                            <span className="inv__item-sub inv__item-sub--new">New addition</span>
                          </span>
                          <span className="inv__td inv__td--qty">{extra}</span>
                          <span className="inv__td inv__td--rate">₹{fmt(a.price || 0)}</span>
                          <span className="inv__td inv__td--amount">₹{fmt((a.price || 0) * extra)}</span>
                        </div>
                      )}
                    </React.Fragment>
                  );
                })
              ) : (
                <>
                  {selectedAddons.filter(a => (a.minQuantity || 0) > 0).map(a => (
                    <div key={`owned-${a.id}`} className="inv__row inv__row--muted">
                      <span className="inv__td inv__td--desc">
                        <span className="inv__item-name">{a.name}</span>
                        <span className="inv__item-sub">Currently active — not charged</span>
                      </span>
                      <span className="inv__td inv__td--qty">{a.minQuantity}</span>
                      <span className="inv__td inv__td--rate">₹{fmt(a.price || 0)}</span>
                      <span className="inv__td inv__td--amount inv__td--zero">₹0.00</span>
                    </div>
                  ))}
                  {selectedAddons.filter(a => extraQty(a) > 0).map(a => (
                    <div key={`new-${a.id}`} className="inv__row">
                      <span className="inv__td inv__td--desc">
                        <span className="inv__item-name">{a.name}</span>
                        <span className="inv__item-sub inv__item-sub--new">New addition</span>
                      </span>
                      <span className="inv__td inv__td--qty">{extraQty(a)}</span>
                      <span className="inv__td inv__td--rate">₹{fmt(a.price || 0)}</span>
                      <span className="inv__td inv__td--amount">₹{fmt((a.price || 0) * extraQty(a))}</span>
                    </div>
                  ))}
                </>
              )}
            </div>

            <div className="inv__totals">
              {isNormal && creditAmount > 0 && (
                <div className="inv__total-row inv__total-row--saving">
                  <span>Plan Credit <span className="inv__total-meta">({creditLabel})</span></span>
                  <span>−₹{fmt(creditAmount)}</span>
                </div>
              )}
              <div className="inv__total-row">
                <span>Subtotal</span>
                <span>₹{fmt(calculateSubtotal())}</span>
              </div>
              {discountApplied && (
                <div className="inv__total-row inv__total-row--saving">
                  <span>Discount{discountType === 'PERCENTAGE' ? ` (${discountValue}% off)` : ''}</span>
                  <span>−₹{fmt(discountAmount)}</span>
                </div>
              )}
              <div className="inv__total-row">
                <span>GST <span className="inv__total-meta">({gstRate}%)</span></span>
                <span>₹{fmt(calculateGSTAmount())}</span>
              </div>
              {walletApplied && walletRedeemAmount > 0 && (
                <div className="inv__total-row inv__total-row--saving">
                  <span>Wallet Credits</span>
                  <span>−₹{fmt(walletRedeemAmount)}</span>
                </div>
              )}
              <div className="inv__total-row inv__total-row--final">
                <span>Total Payable</span>
                <span>₹{fmt(calculateFinalPayable())}</span>
              </div>
            </div>

            <div className="inv__footer-note">
              <i className="bi bi-shield-check" />
              This is a proforma invoice. Official invoice will be sent post payment.
            </div>

          </div>

          <div className="inv__action-footer">
            <button
              className="inv__cancel-btn"
              onClick={() => setIsPaymentPopupOpen(false)}
              disabled={processingPayment}
            >
              Cancel
            </button>
            <button
              className="inv__pay-btn"
              onClick={handlePaymentWithValidation}
              disabled={processingPayment || !isCompanyDetailsFilled()}
            >
              {processingPayment ? (
                <><span className="inv__pay-spinner" /> Processing...</>
              ) : (
                <>
                  <i className="bi bi-lock-fill" />
                  Pay ₹{fmt(calculateFinalPayable())} Securely
                  <i className="bi bi-arrow-right" />
                </>
              )}
            </button>
          </div>

        </div>
      </PopUp>


      {/* ── Company Details popup ─────────────────────────── */}
      <PopUp
        isOpen={showCompanyPopup}
        onClose={handleCloseCompanyPopup}
        title="Company Details"
        size="medium"
      >
        <div className="company-details-container">
          <div className="company-details-row">
            <InputField
              label={formConfig.vendorprofile.gstno.label}
              name="gstNumber"
              value={companyFormData.gstNumber}
              onChange={handleCompanyChange}
              validationType="GST"
              max={15}
              classN="large"
            />
            <InputField
              label={formConfig.CompanyDetails.companyName.label}
              name="companyName"
              value={companyFormData.companyName}
              onChange={handleCompanyChange}
              required
              max={150}
              classN="large"
            />
          </div>
          <div className="company-details-row">
            <div className={`company-details-field floating-textarea ${companyFormData.address ? 'has-value' : ''}`}>
              <textarea
                name={formConfig.CompanyDetails.address.label}
                required
                value={companyFormData.address}
                onChange={handleCompanyChange}
                maxLength={255}
                placeholder=" "
              />
              <label>
                {formConfig.CompanyDetails.address.label}
                <span className="required">*</span>
              </label>
            </div>
          </div>
        </div>
        <Button
          text={loading ? "Saving..." : isUpdate ? "Update" : "Submit"}
          onClick={handleCompanySubmit}
          disabled={loading || !companyFormData.companyName?.trim() || !companyFormData.address?.trim()}
        />
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
          <p>Your payment has been processed successfully. Your subscription is now active and ready to use.</p>
          <div className="success-summary">
            <div className="summary-row"><span>Amount Paid</span><span>₹{fmt(calculateFinalPayable())}</span></div>
            <div className="summary-row"><span>Plan</span><span>{selectedPlan?.name}</span></div>
            {paymentDetails?.transactionId && (
              <div className="summary-row"><span>Transaction ID</span><span>{paymentDetails.transactionId}</span></div>
            )}
          </div>
          <div className="success-action">
            <Button
              text="Done"
              variant="primary"
              onClick={() => { setIsSuccessPopupOpen(false); navigate('/tenantplanss'); }}
            />
          </div>
        </div>
      </PopUp>

    </TenantLayout>
  );
};

export default BuyPlan;