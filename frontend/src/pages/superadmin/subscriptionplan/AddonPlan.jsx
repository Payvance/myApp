import React, { useState, useEffect } from "react";
import "./AddonPlans.css";

// Layout & UI
import SuperAdminLayout from "../../../layouts/SuperAdminLayout";
import PageHeader from "../../../components/common/pageheader/PageHeader";
import PopUp from "../../../components/common/popups/PopUp";
import InputField from "../../../components/common/inputfield/InputField";
import OptionInputBox from "../../../components/common/optioninputbox/OptionInputBox";
import SubscriptionCard from "../../../components/common/subscriptioncard/SubscriptionCard";


// Add-on services
import { addonServices, planServices } from "../../../services/apiService";
import formConfig from "../../../config/formConfig";
import { toast } from "react-toastify";
import Button from "../../../components/common/button/Button";

const AddonPlan = () => {
  const [isCreatePopupOpen, setIsCreatePopupOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [loadingAddons, setLoadingAddons] = useState(false);
  const [addons, setAddons] = useState([]);
  const [plans, setPlans] = useState([]);
  const [showErrorPopup, setShowErrorPopup] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");


  const initialFormData = {
    id: null,
  plan_id: "",
  code: "",
  name: "",
  unit: "",
  unit_price: "",
  currency: "INR",
  status: "active",
};


  // Add-on form data (matches backend)
  const [formData, setFormData] = useState(initialFormData);

  // Fetch plans on component mount
    useEffect(() => {
      fetchPlans();
    }, []);

    useEffect(() => {
      fetchAddons();
    }, []);

  // ===============================
  // Handlers
  // ===============================

  // Handle input changes
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  // Handle create add-on
  const handleCreateAddon = () => {
    setIsCreatePopupOpen(true);

  };

  // Handle close create popup
  const handleCloseCreatePopup = () => {
    setIsCreatePopupOpen(false);
    setFormData(initialFormData);
  };

 // Handle edit add-on
const handleEditAddon = async (addon) => {
  try {
    // Fetch add-on details from API
    const response = await addonServices.getAddonById(addon.id);
    const data = response.data;

    // Map API response to formData format
    setFormData({
      plan_id: data.plan_id,      // map plan_id to planId
      code: data.code || "",
      name: data.name || "",
      unit: data.unit || "",
      unit_price: data.unit_price || "",
      currency: data.currency || "INR",
      status: data.status || "active",
      id: data.id,               // store ID to know it's edit mode
    });

    // Open the same create/edit popup
    setIsCreatePopupOpen(true);

  } catch (error) {
    console.error("Failed to fetch add-on details:", error);
    toast.error("Failed to fetch add-on details");
  }
};



  // ===============================
  // Fetch Add-ons
  // ===============================
  const fetchAddons = async () => {
    setLoadingAddons(true);
    try {
      // Fetch add-ons from API
      const response = await addonServices.getAllAddons();

      // Map API response to UI format
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
          // change name to period
          `Period: ${addon.unit}`,
          `Currency: ${addon.currency}`,
        ],
      }));

      setAddons(mappedAddons);

    } finally {
      setLoadingAddons(false);
    }
  };

  // ===============================
  // Create Add-on
  // ===============================
  // Handle create add-on submit
  const handleSubmitCreateAddon = async () => {
    setIsSubmitting(true);

    // Payload for create add-on
    const payload = {
      plan_id: formData.plan_id,     
      code: formData.code,
      name: formData.name,
      currency: formData.currency,
      unit: formData.unit,
      unit_price: Number(formData.unit_price),
      status: formData.status,
    };

    try {
      // Create add-on in API
      const response = await addonServices.createAddon(payload);

      const data = response.data;
      //  If backend says success = false
      if (data.success === false) {
        setErrorMessage(data.message);
        setShowErrorPopup(true);
        return;
      }
      
    if (data.success === true) {
      toast.success('Add On created successfully', {
      onClose: () => {
        // create add-on popup
        setIsCreatePopupOpen(false);
        setFormData(initialFormData);
        setFormData({     
          plan_id: "",
          code: "",
          name: "",
          unit: "",
          unit_price: "",
          currency: "INR",
          status: "active",
        });
      },
        autoClose: 1000 // 2 seconds before onClose triggers
      })};
      await fetchAddons();
    } finally {
      setIsSubmitting(false);
    }
  };


  // ======================================
    // FETCH PLANS API CALL
    // ======================================
    const fetchPlans = async () => {
      
      try {
        const response = await planServices.getAllPlans();
        // backend usually sends array directly or inside data
        const apiPlans = response.data;
  
        const mappedPlans = apiPlans.map((plan) => ({
          id: plan.id,
          code: plan.code,
          name: plan.name,
        }));
        setPlans(mappedPlans);
      } finally {
        
      }
    };


  // ===============================
  // Update Add-on
  // ===============================
  // Handle update add-on submit
  const handleSubmitUpdateAddon = async () => {
    setIsSubmitting(true);
    // Payload for update add-on
    const payload = {
      
      plan_id: formData.plan_id,
      code: formData.code,
      name: formData.name,
      currency: formData.currency,
      unit: formData.unit,
      unit_price: Number(formData.unit_price),
      status: formData.status,
    };
    try {
      // Update add-on in API
      const response = await addonServices.updateAddon(formData.id, payload);

      toast.success('Add On updated successfully', {
            onClose: () => {
              // close popup
              setIsCreatePopupOpen(false);
              setFormData(initialFormData);
            },
              autoClose: 1000 // 2 seconds before onClose triggers
            });
      await fetchAddons();
      
    } finally {
      setIsSubmitting(false);
    }
  };


  const isFormValid = () => {
  return (
    formData.plan_id !== "" &&
    formData.code.trim() !== "" &&
    formData.name.trim() !== "" &&
    formData.unit !== "" &&
    Number(formData.unit_price) > 0 &&
    formData.status !== ""
  );
};


  return (
    // super admin layout component
    <SuperAdminLayout>
      {/* // addon plan page div start */}
      <div className="addon-plan-page">
        {/* Page Header Component */}
        <PageHeader
          title="Add-ons"
          subtitle="Manage usage-based add-ons"
          button={<button className="create-plan-btn" onClick={handleCreateAddon}>Create Add-on</button>}
        />

        {/* subscription plans grid div start */}
        <div className="plans-grid">
          {loadingAddons ? (
            <p>Loading add-ons...</p>
          ) : addons.length === 0 ? (
            <p>No add-ons found</p>
          ) : (
            addons.map((addon) => (
              <SubscriptionCard
                key={addon.id}
                plan={addon}
                onEdit={(addon) => handleEditAddon(addon)}
              />
            ))
          )}

        </div>
        {/* subscription plans grid div end */}
      </div>

      {/* popup div start */}
      <PopUp
        isOpen={isCreatePopupOpen}
        onClose={handleCloseCreatePopup}
        title={formData.id ? "Update Add-on" : "Create New Add-on"}
        // added subtitle to popup
        subtitle="Configure add-on settings and offers"
        size="large"
      >
        {/* create plan form div start */}
        <div className="create-plan-form">
          <div className="form-row">
            {/* add-on code input field */}
            <OptionInputBox
             label={formConfig.addon.selectplan.label}
             name="plan_id"
             value={formData.plan_id}
             onChange={handleInputChange}
             options={plans.map((plan) => ({
               code: plan.id,     // value sent to backend
               value: plan.name, // text shown in dropdown
             }))}
             required
             disabled={!!formData.id}
             classN="large"
            />
            
            <InputField
              label={formConfig.addon.addOnCode.label}
              name="code"
              value={formData.code}
              onChange={handleInputChange}
              // validation type
              validationType="ALPHA_NUMERIC_ONLY"
              required
              classN="large"
              placeholder="WHATSAPP_MSG"
              disabled={formData.id ? true : false}
              max={15}
            />
            
          </div>

          <div className="form-row">
            {/* add-on name input field */}
            <InputField
              label={formConfig.addon.addOnName.label}
              name="name"
              value={formData.name}
              onChange={handleInputChange}
              validationType="EVERYTHING"
              required
              classN="large"
              placeholder="Whatsapp Messaging"
              disabled={formData.id ? true : false}
              max={50}
            />
            {/* add-on unit input field */}
            <OptionInputBox
              label={formConfig.addon.periodType.label}
              name="unit"
              value={formData.unit}
              onChange={handleInputChange}
              options={[
                { code: 'yearly', value: 'Yearly' },
                { code: 'monthly', value: 'Monthly' }
              ]}
              required
              classN="large"
              disabled={formData.id ? true : false}
            />
            
          </div>

          <div className="form-row">
            {/* add-on unit price input field */}
            <InputField
              label={formConfig.addon.UnitPrice.label}
              name="unit_price"
              value={formData.unit_price}
              onChange={handleInputChange}
              validationType="AMOUNT"
              required
              classN="large"
              placeholder="0.50"
              disabled={formData.id ? true : false}
            />
            {/* add-on status input field */}
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
          </div>

          {/* form actions div start */}
          <div className="form-actions">
            {/* button to create add-on */}
            <button
              className="btn-full btn-primary"
              onClick={formData.id ? handleSubmitUpdateAddon : handleSubmitCreateAddon}
              disabled={isSubmitting || !isFormValid()}
            >
              {isSubmitting ? 'Creating...' : formData.id ? 'Update Add-on' : 'Create Add-on'}
            </button>
          </div>
        </div>
      </PopUp>
      {/* popup div end */}
      {/* popup to show error start*/}
      <PopUp
      isOpen={showErrorPopup}
      onClose={() => setShowErrorPopup(false)}
      showCloseButton={true}
      size="small"
    >
      <div
        style={{
          display: "flex",
          flexDirection: "column",
          gap: "20px",
          alignItems: "center",
          textAlign: "center",
        }}
      >
        <div style={{ maxWidth: "350px" }}>
          <p style={{ fontSize: "16px" }}>
            {errorMessage}
          </p>
        </div>

        <div style={{ width: "100px" }}>
        <Button
          text="OK"
          onClick={() => setShowErrorPopup(false)}
          variant="primary"
        />
        </div>
      </div>
    </PopUp>
    {/* popup to show error end */}
    </SuperAdminLayout>
  );
};

export default AddonPlan;
