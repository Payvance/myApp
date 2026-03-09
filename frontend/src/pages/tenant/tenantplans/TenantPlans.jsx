// src/pages/tenant/subscriptionplan/TenantPlans.jsx
import React, { useEffect, useState } from 'react';
import TenantLayout from '../../../layouts/TenantLayout';
import PageHeader from '../../../components/common/pageheader/PageHeader';
import PlanCard from '../../../components/common/plancard/PlanCard';
import { tenantServices } from '../../../services/apiService';
import PopUp from '../../../components/common/popups/PopUp';
import { toast } from 'react-toastify';
import Button from '../../../components/common/button/Button';
import ActivePlanCard from '../../../components/common/activePlanCard/ActivePlanCard'; // ✅ only this needed
import { planServices } from '../../../services/apiService';
import { useNavigate } from 'react-router-dom';

const TenantPlans = () => {

  const [tenantData, setTenantData] = useState({});
  const [isButtonDisabled, setIsButtonDisabled] = useState(true);
  const [isTrialPopupOpen, setIsTrialPopupOpen] = useState(false);
  const [trialConfig, setTrialConfig] = useState({});
  const [trialInfo, setTrialInfo] = useState({ trialStartAt: null, trialEndAt: null });
  const [loadingPlans, setLoadingPlans] = useState(false);
  const [plans, setPlans] = useState([]);
  const [activePlan, setActivePlan] = useState(null);
  const [loadingActivePlan, setLoadingActivePlan] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const tenantId = localStorage.getItem("tenant_id");
    if (tenantId) {
      getTenantData(tenantId);
      getTenantTrialConfig(tenantId);
      fetchActivePlan(tenantId);
    }
    fetchAvailablePlans();
  }, []);

  const getTenantData = async (tenantId) => {
    try {
      const response = await tenantServices.getTenantData(tenantId);
      const data = response.data;
      setIsButtonDisabled(
        data.status !== "inactive" ||
        data.trialStartAt !== null ||
        data.trialEndAt !== null
      );
      setTenantData(response.data);
    } catch (error) {
      toast.error('Unable to load tenant details. Please refresh the page.');
    }
  };

  const fetchAvailablePlans = async () => {
    setLoadingPlans(true);
    const currentTenantId = Number(localStorage.getItem("tenant_id"));
    try {
      const response = await planServices.getAllPlans();
      const apiPlans = response.data;

      // Filter: active flag, must have a name and a price (removes blank/broken cards)
      const mappedPlans = apiPlans
        .filter((plan) =>
          plan.is_active === "1" &&
          plan.name?.trim() !== "" &&
          plan.plan_price !== null
        )
        .map((plan) => ({
          id:       plan.id,
          name:     plan.name.trim(),
          subtitle: plan.code,
          price:    plan.plan_price.amount,
          period:   plan.plan_price.billing_period === "yearly" ? "per 12 months" : "per 1 month",
          isOwned:  plan.tenantIds?.includes(currentTenantId) ?? false,
          features: [
            `Users: ${plan.plan_limitation?.allowed_user_count ?? 0}`,
            `Companies: ${plan.plan_limitation?.allowed_company_count ?? 0}`,
            plan.is_separate_db === "1" ? "Separate DB" : "Shared DB",
          ],
        }));
      setPlans(mappedPlans);
    } catch (error) {
      console.error("Error fetching plans for tenant:", error);
      toast.error("Failed to load plans.");
    } finally {
      setLoadingPlans(false);
    }
  };

  const handleSelectPlan = (plan) => {
    navigate('/BuyPlan', { state: { plan } });
  };

  const fetchActivePlan = async (tenantId) => {
    try {
      setLoadingActivePlan(true);
      const response = await tenantServices.getActivePlan(tenantId);
      if (response.data && response.data.plan) {
        setActivePlan(response.data);
      }
    } catch (error) {
      console.log("No active plan found or error fetching active plan");
      setActivePlan(null);
    } finally {
      setLoadingActivePlan(false);
    }
  };

  const handleRenewPlan = (plan) => {
    navigate('/Renew/BuyPlan', { state: { plan, action: 'renew' } });
  };

  const handleAddOns = (plan) => {
    navigate('/Addon/BuyPlan', { state: { plan } });
  };

  const handleStartTrial = () => {
    const userId = localStorage.getItem("user_id");
    if (!userId) {
      toast.error('User session expired. Please log in again.');
      return;
    }
    tenantServices.startTrial({ userId })
      .then((response) => {
        const { trialStartAt, trialEndAt } = response.data;
        setTrialInfo({ trialStartAt, trialEndAt });
        setIsTrialPopupOpen(true);
        getTenantData(localStorage.getItem("tenant_id"));
      })
      .catch(() => {
        toast.error('Failed to start trial. Please try again.');
      });
  };

  const getTenantTrialConfig = async (tenantId) => {
    try {
      const response = await tenantServices.getTrialConfig(tenantId);
      setTrialConfig(response.data);
    } catch (error) {
      toast.error("Error fetching tenant trial config.");
      return null;
    }
  };

    // Price of the currently active plan (0 if no active plan)
  const activePlanPrice = activePlan?.plan?.price?.amount ?? 0;


  return (
    <TenantLayout>
      <div className="subscription-plan-page">
        <PageHeader title="Plans" />

        <div className="plans-grid">

          {/* Trial Plan Card */}
          <PlanCard
            plan={{
              id: 0,
              name: 'Trial Plan',
              subtitle: 'Try all features for free',
              price: 0,
              period: `${trialConfig.adsUnlockedDays + trialConfig.extendedTrialDays} days free trial`,
              stats: { code: 'TRIAL' },
              features: [
                `First ${trialConfig.adsUnlockedDays} days without ads`,
                `Next ${trialConfig.extendedTrialDays} days with ads`,
                `Up to ${trialConfig.activeUsersCount} users and ${trialConfig.companiesCount} company`
              ]
            }}
            onBuy={handleStartTrial}
            buttonText="Start Free Trial"
            buttonDisabled={isButtonDisabled}
          />

          {/* ── All subscription plans ── */}
          {loadingPlans || loadingActivePlan ? (
            <p>Loading available plans...</p>
          ) : plans.length === 0 ? (
            <p>No active plans available at the moment.</p>
          ) : (
            plans.map((plan) => {

              // Check if this is the tenant's currently active plan
              const isActive = activePlan?.plan?.planId === plan.id;

              // Format expiry date only for the active plan
              const endDate = isActive && activePlan?.endDate
                ? new Date(activePlan.endDate).toLocaleDateString('en-IN')
                : null;

                const isCheaper = !isActive && activePlanPrice > 0 && plan.price < activePlanPrice;

              return (
                <ActivePlanCard
                  key={plan.id}
                  plan={{
                    id:         plan.id,
                    name:       plan.name,
                    subtitle:   plan.subtitle,
                    price:      plan.price,
                    period:     plan.period,
                    expiryDate: endDate,
                    features:   plan.features,
                  }}
                  isActive={isActive}
                  onBuy={handleSelectPlan}
                  onRenew={handleRenewPlan}
                  onAddons={handleAddOns}
                  
                  isDisabled={isCheaper}
                  buttonDisabled={isCheaper}
                />
              );
            })
          )}

        </div>

        {/* Trial Started Popup */}
        <PopUp
          isOpen={isTrialPopupOpen}
          onClose={() => setIsTrialPopupOpen(false)}
          title="🎉 Trial Started Successfully"
          subtitle="Your free trial is now active"
          size="small"
        >
          <p>Your trial has started successfully.</p>
          <p>An email has been sent with the required details. Please check your inbox.</p>
          <p>You can now log in through the connector.</p>
          <Button text="OK" onClick={() => setIsTrialPopupOpen(false)} />
        </PopUp>

      </div>
    </TenantLayout>
  );
};

export default TenantPlans;