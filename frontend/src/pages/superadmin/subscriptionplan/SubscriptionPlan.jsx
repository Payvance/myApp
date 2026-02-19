// src/pages/superadmin/subscriptionplan/SubscriptionPlan.jsx
import React, { useState, useEffect } from 'react';
import './SubscriptionPlan.css';
import SuperAdminLayout from '../../../layouts/SuperAdminLayout';
import PageContainer from '../../../components/common/pagecontainer/PageContainer';
import Button from '../../../components/common/button/Button';
import PopUp from '../../../components/common/popups/PopUp';
import InputField from '../../../components/common/inputfield/InputField';
import OptionInputBox from '../../../components/common/optioninputbox/OptionInputBox';
// import Checkbox from '../../../components/common/checkbox/Checkbox';
import Checkbox from '../../../components/common/checkbox/Checkbox';
import Toggle from '../../../components/common/togglebutton/Toggle';
import SubscriptionCard from '../../../components/common/subscriptioncard/SubscriptionCard';
import PageHeader from '../../../components/common/pageheader/PageHeader';

// import plan services
import { planServices } from '../../../services/apiService';
import formConfig from '../../../config/formConfig';
import { toast } from 'react-toastify';

const SubscriptionPlan = () => {
  const [isCreatePopupOpen, setIsCreatePopupOpen] = useState(false);
  const [isEditPopupOpen, setIsEditPopupOpen] = useState(false);

  // state for create plan 
  const [isSubmitting, setIsSubmitting] = useState(false);

  // state for plans for subscription card
  const [plans, setPlans] = useState([]);
  // state for loading 
  const [loadingPlans, setLoadingPlans] = useState(false);

  // Fetch plans on component mount
  useEffect(() => {
    fetchPlans();
  }, []);

  const initialFormData = {
  planCode: '',
  planName: '',
  allowedUsers: '',
  allowedCompany: '',
  periodType: 'monthly',
  duration: 1,
  planPrice: '',
  status: 'active',
  databaseShared: true
};

  // form data for create plan
  const [formData, setFormData] = useState(initialFormData);
  const [isEditMode, setIsEditMode] = useState(false);
  const [editingPlanId, setEditingPlanId] = useState(null);
  const [showErrorPopup, setShowErrorPopup] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  // handle edit plan
  const handleEditPlan = async (plan) => {
    try{
      setEditingPlanId(plan.id);
      // Fetch plan details from API
      const response = await planServices.getPlanById(plan.id);
      const data = response.data;

      // Map API response to form data format
      setFormData({
        planCode: data.code,
        planName: data.name,
        allowedUsers: data.plan_limitation?.allowed_user_count ?? 0,
        allowedCompany: data.plan_limitation?.allowed_company_count ?? 0,
        periodType: data.plan_price?.billing_period ?? "monthly",
        duration: data.plan_price?.duration ?? 1, 
        planPrice: data.plan_price?.amount ?? 0,
        status: data.is_active === "1" ? "active" : "inactive",
        databaseShared: data.is_separate_db === "0" ? true : false,
      });
      setIsEditMode(true);
      setIsCreatePopupOpen(true);
    } catch (error) {
      console.error("Failed to fetch plan details:", error);
    }
  };
  // handle create plan
  const handleCreatePlan = () => {
    setIsCreatePopupOpen(true);
  };

  // handle close create popup
  const handleCloseCreatePopup = () => {
    setIsCreatePopupOpen(false);
    setIsEditMode(false);
    setFormData(initialFormData);
  };

  // handle close edit popup
  const handleCloseEditPopup = () => {
    setIsEditPopupOpen(false);
    setIsEditMode(false);
    setFormData(initialFormData);
  };

  // handle input change
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };
  // ======================================
  // CREATE PLAN API CALL
  // ======================================
  const handleSubmitCreatePlan = async () => {
    
    setIsSubmitting(true);
    // payload for create plan
    const payload = {
      code: formData.planCode,
      name: formData.planName,
      // description: formData.planDescription,active/inactive
      is_active: formData.status === 'active' ? '1' : '0',
      is_separate_db: formData.databaseShared ? '0' : '1',
      plan_limitation: {
        allowed_user_count: Number(formData.allowedUsers),
        allowed_company_count: Number(formData.allowedCompany),
      },
      plan_price: {
        billing_period: formData.periodType,
        currency: 'INR',
        amount: Number(formData.planPrice),
        duration: Number(formData.duration),
      },
    };

    // create plan api call response
    try {
      const response = await planServices.createPlan(payload);
        // 🔹 IMPORTANT: Check success flag from backend
        if (!response.data.success) {
          setErrorMessage(response.data.message);
          setShowErrorPopup(true);
          return;   // stop execution
        }
      toast.success('Subscription created successfully', {
        onClose: () => {
          // close popup
          setIsCreatePopupOpen(false);
          setFormData(initialFormData);

      // reset form (optional but recommended)
          setFormData({
            planCode: '',
            planName: '',
            allowedUsers: '',
            allowedCompany: '',
            periodType: 'monthly',
            planPrice: '',
            status: 'active',
            databaseShared: true,
          });
        },
        autoClose: 1000 // 2 seconds before onClose triggers
      });
      // fetch plans again to show the newly created plan
      await fetchPlans();
    }catch (error) {

    // Extract backend message safely
    const backendMessage =
      error?.response?.data?.message ||
      error?.response?.data ||
      "Something went wrong.";

      setErrorMessage("Something went wrong.");
      setShowErrorPopup(true);

  } finally {
      setIsSubmitting(false);
    }
  };


  // ======================================
  // FETCH PLANS API CALL
  // ======================================
  const fetchPlans = async () => {
    setLoadingPlans(true);
    try {
      const response = await planServices.getAllPlans();
      // backend usually sends array directly or inside data
      const apiPlans = response.data;
      // Map backend response → UI card format
  const mappedPlans = apiPlans.map((plan) => {
        const duration = plan.plan_price?.duration ?? 1;
        const billingPeriod = plan.plan_price?.billing_period;

        return {
          id: plan.id,
          name: plan.name,
          subtitle: plan.code,
          status: plan.is_active === "1" ? "ACTIVE" : "INACTIVE",
          price: plan.plan_price?.amount ?? 0,

          //  USE BACKEND DURATION HERE
          period:
            billingPeriod === "yearly"
              ? `per ${duration} year${duration > 1 ? "s" : ""}`
              : `per ${duration} month${duration > 1 ? "s" : ""}`,

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
    } finally {
      setLoadingPlans(false);
    }
  };


  const handleSubmitUpdatePlan = async () => {
  
  setIsSubmitting(true);
  const payload = {
    code: formData.planCode,
    name: formData.planName,
    is_active: formData.status === 'active' ? '1' : '0',
    is_separate_db: formData.databaseShared ? '0' : '1',
    plan_limitation: {
      allowed_user_count: Number(formData.allowedUsers),
      allowed_company_count: Number(formData.allowedCompany),
    },
    plan_price: {
      billing_period: formData.periodType,
      currency: 'INR',
      amount: Number(formData.planPrice),
      duration: Number(formData.duration),
    },
  };

  try {
    await planServices.updatePlan(editingPlanId, payload);
    toast.success('Plan updated succesfully.', {
    onClose: () => {
      // close popup & reset
    setIsCreatePopupOpen(false);
    setIsEditMode(false);
    setEditingPlanId(null);
    setFormData(initialFormData);
    },
    autoClose: 1000 // 2 seconds before onClose triggers
  });
    

    // refresh plans
    await fetchPlans();
  } catch (error) {
    toast.error("Failed to update plan");
  } finally {
    setIsSubmitting(false);
  }
};


const isFormValid = () => {
  return (
    formData.planCode.trim() !== '' &&
    formData.planName.trim() !== '' &&
    Number(formData.allowedUsers) > 0 &&
    Number(formData.allowedCompany) > 0 &&
    formData.periodType !== '' &&
    (Number(formData.duration) >= 1 && Number(formData.duration) <= 12) &&
    Number(formData.planPrice) > 0 &&
    formData.status !== ''
  );
};




  return (
    <SuperAdminLayout>
      <div className="subscription-plan-page">
        {/* Page Header Component */}
        <PageHeader
          title="Subscription Plans"
          subtitle="Manage subscription plans and pricing"
          button={<button className="create-plan-btn" onClick={handleCreatePlan}>Create Plan</button>}
        />

        {/* subscription plans grid div start */}
        <div className="plans-grid">
          {loadingPlans ? (
            <p>Loading subscription plans...</p>
          ) : plans.length === 0 ? (
            <p>No subscription plans found</p>
          ) : (
            plans.map((plan) => (
              <SubscriptionCard
                key={plan.id}
                plan={plan}
                onEdit={handleEditPlan}
              />
            ))
          )}

        </div>
        {/* subscription create popup div end */}
      </div>

      {/* // subscription create popup div start */}
      {/* popup div start */}
      <PopUp
        isOpen={isCreatePopupOpen}
        onClose={handleCloseCreatePopup}
        title={isEditMode ? "Update Subscription Plan" : "Create New Subscription Plan"}
        // added subtitle to popup
        subtitle="Configure subscription plan settings and pricing"
        size="large"
      >
        {/* added input fields */}
        <div className="create-plan-form">
          <div className="form-row">
            {/* plan code */}
            <InputField
              label={formConfig.subscriptionPlan.planCode.label}
              name="planCode"
              max={15}
              value={formData.planCode}
              onChange={handleInputChange}
              validationType="ALPHA_NUMERIC_ONLY"
              required
              classN="large"
              disabled={isEditMode? true : false}
            />
            {/* plan name */}
            <InputField
              label={formConfig.subscriptionPlan.planName.label}
              name="planName"
              max={50}
              value={formData.planName}
              onChange={handleInputChange}
              validationType="TEXT_ONLY"
              required
              classN="large"
               disabled={isEditMode? true : false}
            />
          </div>

          <div className="form-row">
            {/* allowed users */}
            <InputField
              label={formConfig.subscriptionPlan.allowedUsers.label}
              name="allowedUsers"
              value={formData.allowedUsers}
              onChange={handleInputChange}
              max={3}
              validationType="NUMBER_ONLY"
              required
              classN="large"
               disabled={isEditMode? true : false}
            />
            {/* allowed company */}
            <InputField
              label={formConfig.subscriptionPlan.allowedCompany.label}
              name="allowedCompany"
              value={formData.allowedCompany}
              onChange={handleInputChange}
              type="number"
              max={3}
              required
              classN="large"
               disabled={isEditMode? true : false}
            />
          </div>

          <div className="form-row">
            {/* period type and duration group */}
            <div className="period-duration-group">
              <OptionInputBox
                label={formConfig.subscriptionPlan.periodType.label}
                name="periodType"
                value={formData.periodType}
                onChange={handleInputChange}
                options={[
                  { code: 'monthly', value: 'Monthly' },
                  { code: 'yearly', value: 'Yearly' }
                ]}
                required
                classN="large"
                disabled={isEditMode ? true : false}
              />
              {/* period duration */}
              <InputField
                label={formConfig.subscriptionPlan.periodDuration.label}
                name="duration"
                value={formData.duration}
                onChange={handleInputChange}
                validationType="NUMBER_ONLY"
                type="number"
                min={1}
                max={12}
                required
                classN="large"
                disabled={isEditMode ? true : false}
              />
            </div>
            {/* plan price */}
            <InputField
              label={formConfig.subscriptionPlan.planPrice.label}
              name="planPrice"
              value={formData.planPrice}
              onChange={handleInputChange}
              validationType="AMOUNT"
              required
              classN="large"
               disabled={isEditMode? true : false}
            />
          </div>

          <div className="form-row">
            {/* status */}
            <OptionInputBox
              label={formConfig.subscriptionPlan.status.label}
              name="status"
              value={formData.status}
              onChange={handleInputChange}
              options={[
                { code: 'active', value: 'Active' },
                { code: 'inactive', value: 'Inactive' }
              ]}
              required
              classN="large"
            />
            {/* database shared or  not */}
            <div className="database-checkboxes">
              <Checkbox
                label="Separate Database"
                checked={!formData.databaseShared}
                onChange={(e) => {
                  if (e.target.checked) {
                    setFormData(prev => ({ ...prev, databaseShared: false }));
                  }
                }}
                size="medium"
                 disabled={isEditMode? true : false}
              />
              <Checkbox
                label="Shared Database"
                checked={formData.databaseShared}
                onChange={(e) => {
                  if (e.target.checked) {
                    setFormData(prev => ({ ...prev, databaseShared: true }));
                  }
                }}
                size="medium"
                 disabled={isEditMode? true : false}
              />
            </div>
          </div>

          <div className="form-actions">
            <button
              className="btn-full btn-primary"
              onClick={isEditMode ? handleSubmitUpdatePlan : handleSubmitCreatePlan}
              disabled={isSubmitting || !isFormValid()}
            >
              {isSubmitting ? 'Creating...' : isEditMode ? 'Update Plan' : 'Create Plan'}
            </button>

          </div>
        </div>
        {/* create plan form div end */}
      </PopUp>
      {/*  popup div end */}

      <PopUp
      isOpen={showErrorPopup}
      onClose={() => setShowErrorPopup(false)}
      showCloseButton={true}
      size="small"
    >
      <div
        style={{
          display: 'flex',
          flexDirection: 'column',
          gap: '20px',
          alignItems: 'center',
          textAlign: 'center'
        }}
      >
        <div style={{ maxWidth: '350px' }}>
          <p style={{ fontSize: '16px' }}>
            {errorMessage}
          </p>
        </div>

        <div style={{ width: '100px' }}>
          <Button
            text="OK"
            onClick={() => setShowErrorPopup(false)}
            variant="primary"
          />
        </div>
      </div>
    </PopUp>


      
    </SuperAdminLayout>
  );
};

export default SubscriptionPlan;
