// src/pages/tenant/subscriptionplan/TenantPlans.jsx
import React, { useEffect, useState } from 'react';
import TenantLayout from '../../../layouts/TenantLayout';
import PageHeader from '../../../components/common/pageheader/PageHeader';
import PlanCard from '../../../components/common/plancard/PlanCard';
import { tenantServices } from '../../../services/apiService';
import PopUp from '../../../components/common/popups/PopUp';
import { toast } from 'react-toastify';
import Button from '../../../components/common/button/Button';
import SubscriptionCard from '../../../components/common/subscriptioncard/SubscriptionCard';
import { planServices } from '../../../services/apiService';
import { useNavigate } from 'react-router-dom';
/**
 * TenantPlans
 * ------------
 * Displays available subscription plans for a tenant.
 * Handles trial activation and controls UI state
 * based on tenant status and trial availability.
 */
const TenantPlans = () => {

  /* ================================
     State Management
     ================================ */

  // Stores tenant details fetched from API   
  const [tenantData, setTenantData] = useState({});
  // Controls "Start Free Trial" button disabled state
  const [isButtonDisabled, setIsButtonDisabled] = useState(true);
  // Controls trial success popup visibility
  const [isTrialPopupOpen, setIsTrialPopupOpen] = useState(false);
  // Stores trial configuration details
  const [trialConfig, setTrialConfig] = useState({});
  // Stores trial start and end dates for popup display
  const [trialInfo, setTrialInfo] = useState({
    trialStartAt: null,
    trialEndAt: null,
  });
  const [loadingPlans, setLoadingPlans] = useState(false);
  const [plans, setPlans] = useState([]);
  const navigate = useNavigate();
  /* ================================
     Initial Data Fetch
     ================================ */

  // Fetch tenant data on component mount
  useEffect(() => {
    const tenantId = localStorage.getItem("tenant_id");

    if (tenantId){ 
      getTenantData(tenantId);
      getTenantTrialConfig(tenantId);
    }
    fetchAvailablePlans();
  }, []);

  /* ================================
     API Calls
     ================================ */

  /**
   * Fetch tenant details and determine
   * whether the trial button should be disabled.
   */
  const getTenantData = async (tenantId) => {
    try {
      const response = await tenantServices.getTenantData(tenantId);
      const data = response.data;

      /**
         * Disable button when:
         * - Tenant is NOT inactive
         * - OR trial already started
         * - OR trial already ended
         */
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

  //fetch available plans for tenant
  // Fetch available plans for tenant
  const fetchAvailablePlans = async () => {
    setLoadingPlans(true);
    
    // 1. Get current tenant ID from localStorage and convert to Number for exact match
    const currentTenantId = Number(localStorage.getItem("tenant_id"));

    try {
      const response = await planServices.getAllPlans();
      const apiPlans = response.data;

      const mappedPlans = apiPlans.map((plan) => {
        // 2. Check if the current tenant ID is in the plan's tenantIds array
        const isAlreadyLinked = plan.tenantIds?.includes(currentTenantId);

        return {
          id: plan.id,
          name: plan.name,
          subtitle: plan.code,
          status: plan.is_active === "1" ? "ACTIVE" : "INACTIVE",
          price: plan.plan_price?.amount ?? 0,
          period: plan.plan_price?.billing_period === "yearly" ? "per 12 months" : "per 1 month",
          
          // 3. Add a property to track if it's linked
          isOwned: isAlreadyLinked,

          stats: {
            code: plan.code,
            subscribers: "-",
            revenue: "-"
          },
          features: [
            `Users: ${plan.plan_limitation?.allowed_user_count}`,
            `Companies: ${plan.plan_limitation?.allowed_company_count}`,
            plan.is_separate_db === "1" ? "Separate DB" : "Shared DB"
          ]
        };
      });

      setPlans(mappedPlans);
    } catch (error) {
      console.error("Error fetching plans for tenant:", error);
      toast.error("Failed to load plans.");
    } finally {
      setLoadingPlans(false);
    }
  };

  const handleSelectPlan = (plan) => {
    
    console.log("Tenant selected plan:", plan.id);
    // Logic for starting a trial or moving to checkout
    navigate('/BuyPlan', { state: { plan } });
  };

  /**
   * Handles trial activation for the tenant.
   * Opens success popup on successful response.
   */
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
        setIsTrialPopupOpen(true);   // 🔥 show popup
        getTenantData(localStorage.getItem("tenant_id"));
      })
      .catch(() => {
        toast.error('Failed to start trial. Please try again.');
      });
  };

  // Fetches trial configuration for the tenant
  // Used to display trial details in the plan card
  const getTenantTrialConfig = async (tenantId) => {
    try {
      const response = await tenantServices.getTrialConfig(tenantId);
      
      setTrialConfig(response.data);
    } catch (error) {
      toast.error("Error fetching tenant trial config.");
      return null;
    }
  };
  

  /* ================================
       Render
       ================================ */

  return (
    <TenantLayout>
      {/* Subscription Plan Page Start */}
      <div className="subscription-plan-page">
        {/* Page Header Start */}
        <PageHeader
          title="Plans"
          subtitle="Choose a plan that fits your needs"
        />
        {/* Page Header End */}

        {/* Plan Cards Start */}
        <div className="plans-grid">
        <PlanCard
          plan={{
            id: 0,
            name: 'Trial Plan',
            subtitle: 'Try all features for free',
            price: 0,
            period: `${trialConfig.adsUnlockedDays + trialConfig.extendedTrialDays} days free trial`,
            stats: { code: 'TRIAL' },
            features: [
              `Get ${trialConfig.adsUnlockedDays + trialConfig.extendedTrialDays} days free access`,
              `First ${trialConfig.adsUnlockedDays} days without ads`,
              `Next ${trialConfig.extendedTrialDays} days with ads`,
              `Up to ${trialConfig.activeUsersCount} users and ${trialConfig.companiesCount} company`
            ]
          }}
          onBuy={handleStartTrial}
          buttonText="Start Free Trial"
          buttonDisabled={isButtonDisabled} // ✅ disable based on tenant status / trial dates
        />
        {/* Plan Cards End */}
          {loadingPlans ? (
            <p>Loading available plans...</p>
          ) : plans.length === 0 ? (
            <p>No active plans available at the moment.</p>
          ) : (
            plans.map((plan) => (
              <SubscriptionCard
                key={plan.id}
                plan={plan}
                onBuy={handleSelectPlan}
                buttonText={plan.isOwned ? "Current Plan" : "Select Plan"}
      // 5. Disable button if linked
      buttonDisabled={plan.isOwned}
              />
            ))
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
        {/* Trial Success Popup End */}

      </div>
      {/* Subscription Plan Page End */}
    </TenantLayout>
  );
};

export default TenantPlans;
