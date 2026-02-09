// --------------------
// React & Library Imports
// --------------------
import React, { useEffect, useState } from "react";

import PageHeader from "../../../../components/common/pageheader/PageHeader";
import Toggle from "../../../../components/common/togglebutton/Toggle";
import TenantLayout from "../../../../layouts/TenantLayout";
import PersonalDetails from "../../../profile/common/PersonalDetails";
import Button from "../../../../components/common/button/Button";
import { useParams, useLocation, useNavigate } from "react-router-dom";
import { tenantServices, userServices } from "../../../../services/apiService";
import PopUp from "../../../../components/common/popups/PopUp";
import { toast } from "react-toastify";


/**
 * User update page for tenant administrators.
 * Allows viewing basic user information and updating active status.
 */
const User = () => {
  // --------------------
  // State & Hooks
  // --------------------
  const [personalData, setPersonalData] = useState({});
  const [isActive, setIsActive] = useState(true);
  const [loading, setLoading] = useState(false);
  const [showSuccessPopup, setShowSuccessPopup] = useState(false);
  const [planDetails, setPlanDetails] = useState({
      activeUsers: 0,
      createdUsers: 0,
    });
    const navigate = useNavigate();
    // Only allow toggling if plan limit not reached
    const canToggleUser = planDetails.createdUsers < planDetails.activeUsers;


  

  const { id } = useParams(); 

   // --------------------
  // Data Fetching
  // --------------------

  // Fetch user details whenever the route param changes
  useEffect(() => {
    getUserData();
    }, [id]);
  
  const getUserData = async () => {
    const payload = { userId: Number(id) };
    // Fetch user data from API using the id
    const response = await tenantServices.fetchTenantUserById(payload, localStorage.getItem("tenant_id"));

    const newParsonalData = {
      userName: response.data.name,
      userEmail: response.data.email,
        userPhone: response.data.phone,
    };
    setPersonalData(newParsonalData);
    setIsActive(response.data.tenantUserActive);  
  }

  // --------------------
  // Actions
  // --------------------

  // Persist updated user status and name
  const handleSave = async () => {
  try {
    setLoading(true);

    const payload = {
      userId: Number(id), // from URL
      active: isActive, // from toggle
      name: personalData.userName, // or userName if backend expects that
    };
    
    const tenant_id = localStorage.getItem("tenant_id");
   
    const response = await tenantServices.updateTenantUserStatus(payload, tenant_id);
    setShowSuccessPopup(true);
    // optional: success toast
    toast.success("User updated successfully");
  } catch (error) {
        toast.error("Failed to update user");
  } finally {
    setLoading(false);
  }
};

  const gatPlanDetails = async () => {
  try {
    const tenant_id = localStorage.getItem("tenant_id");
    const response = await tenantServices.getPlanDetails(tenant_id);

    const data = response?.data || {};

    // Count only roles with status: true
    const createdUsers = data.roles?.filter(role => role.status === true).length || 0;

    setPlanDetails({
      activeUsers: data.activeUsers || 0,
      createdUsers: createdUsers,
    });

  } catch (error) {
    toast.error("Failed to fetch plan details");
    console.error("Plan details error:", error);
  }
};

    useEffect(() => {
    gatPlanDetails();
    }, []);


  // --------------------
  // Render
  // --------------------
  return (
    <TenantLayout>
        {/* user-profile-page div start */}
      <div className="user-profile-page">
        {/* PageHeader start */}
        <PageHeader
          title={
            <div style={{ display: "flex", alignItems: "center", gap: "15px" }}>
              <Button isBack variant="back" text="" />
              <span>Update User</span>
              <Toggle
                isOn={isActive}
                onToggle={() => setIsActive(!isActive)}
                labelOn="Tenant User Status Active"
                labelOff="Tenant User Status Inactive"
                size="small"
                disabled={isActive ? false :   !canToggleUser} 
              />
            </div>
            
          }
        />
         {/* PageHeader end */}

        {/* PersonalDetails component start */}
        <div className="profile-sections-container">
          {/* Personal Details Only */}
          <PersonalDetails
            personalData={personalData}
            setPersonalData={setPersonalData}
            disableEmail={true}
            disablePhone={true}
          />
          {/* PersonalDetails component end */}


          {/* profile-footer div start */}
          <div
            className="profile-footer"
            style={{
                position: "sticky",
                bottom: 0,    
                padding: "16px",
                display: "flex",
                justifyContent: "flex-end",
                border: "none",
            }}
          >
            {/* Update button start */}
            <Button
              text="Update User"
              onClick={handleSave}
              disabled={loading}
            />
            {/* Update button end */}
          </div>
          {/* profile-footer div end */}

        </div>
        {/* profile-sections-container div end */}

        {/* Success PopUp start */}
        <PopUp
            isOpen={showSuccessPopup}
            onClose={() => setShowSuccessPopup(false)}
            title="Success"
            subtitle="User Updated Successfully"
            size="small"
    >
            {/* popup content div start */}
            <div style={{ textAlign: "center" }}>
                <p>The user details have been updated successfully.</p>
                 {/* OK button start */}
                <Button
                 text="OK"
                 onClick={() => {
                    setShowSuccessPopup(false);
                    navigate("/usermanagement");
                }}
                />
                {/* OK button end */}
            </div>
            {/* popup content div end */}
        </PopUp>
        {/* Success PopUp end */}
      </div>
      {/* user-profile-page div end */}
    </TenantLayout>
  );
};

export default User;
