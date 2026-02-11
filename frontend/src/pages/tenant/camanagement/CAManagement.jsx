import React, { useState } from "react";
import PageHeader from "../../../components/common/pageheader/PageHeader";
import TenantLayout from "../../../layouts/TenantLayout";
import InputField from "../../../components/common/inputfield/InputField";
import Button from "../../../components/common/button/Button";
import PopUp from "../../../components/common/popups/PopUp";
import { toast } from "react-toastify";
import formConfig from "../../../config/formConfig";
import { tenantCaManagementServices } from "../../../services/apiService";

const CAManagement = () => {
  const [isCreatePopupOpen, setIsCreatePopupOpen] = useState(false);
  const [caNo, setCaNo] = useState("");
  const [referenceCode, setReferenceCode] = useState("");

  const handleOpenCreatePopup = () => setIsCreatePopupOpen(true);
  const handleCloseCreatePopup = () => {
    setIsCreatePopupOpen(false);
    setCaNo("");
    setReferenceCode("");
  };

  const handleSubmit = async () => {
    if (!caNo || !referenceCode) {
      toast.error("Please fill all required fields");
      return;
    }

    const payload = {
      userId: localStorage.getItem("user_id"),
      caNo: caNo,
      referenceCode: referenceCode,
    };

    try {
      const response = await tenantCaManagementServices.createCA(payload);
      
      if (response.data.success) {
        toast.success(response.data.message || "CA created successfully");
      } else {
        toast.error(response.data.message || "Failed to create CA");
      }
    } catch (error) {
      const errorMessage = error.response?.data?.message || error.message || "Failed to create CA";
      toast.error(errorMessage);
    }
  };

  return (
    <TenantLayout>
      <div className="license-inventory-content">
        <PageHeader
          title="CA Management"
          subtitle="Manage your Chartered Accountant records"
          button={
            <button
              type="button"
              className="create-plan-btn"
              onClick={handleOpenCreatePopup}
            >
              Create CA
            </button>
          }
        />
        
        {/* ---------- CREATE CA POPUP ---------- */}
        <PopUp
          isOpen={isCreatePopupOpen}
          onClose={handleCloseCreatePopup}
          title="Create New CA"
          subtitle="Enter CA details to create a new record"
          size="medium"
        >
          <div className="signup-panel">
            <div className="signup-inner">
              {/* CA No. */}
              <InputField
                label={formConfig.caprofile.caNo.label}
                type="text"
                value={caNo}
                onChange={(e) => setCaNo(e.target.value)}
                name="caNo"
                validationType="ALPHANUMERIC"
                required={true}
                max={50}
              />

              {/* Reference Code */}
              <InputField
                label={formConfig.caprofile.referenceCode.label}
                type="text"
                value={referenceCode}
                onChange={(e) => setReferenceCode(e.target.value)}
                name="referenceCode"
                validationType="ALPHANUMERIC"
                required={true}
                max={50}
              />

              {/* Submit Button */}
              <div className="signin-submit">
                <Button
                  text="Create CA"
                  onClick={handleSubmit}
                />
              </div>
            </div>
          </div>
        </PopUp>
      </div>
    </TenantLayout>
  );
};

export default CAManagement;
