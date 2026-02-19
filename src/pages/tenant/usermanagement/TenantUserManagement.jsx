import React, { useState, useCallback, useEffect } from "react";
import PageHeader from "../../../components/common/pageheader/PageHeader";
import TenantLayout from "../../../layouts/TenantLayout";
import { LICENSE_BATCH_COLUMNS, TANENT_COLUMNS, USERS_COLUMNS } from "../../../config/columnConfig";
import { DataTable } from "../../../components/common/table";
import InputField from "../../../components/common/inputfield/InputField";
import PopUp from "../../../components/common/popups/PopUp";
import Button from "../../../components/common/button/Button";
import Footer from "../../../components/common/footer/Footer";
import { toast } from "react-toastify";
import formConfig from "../../../config/formConfig";
import useOtp from "../../../hooks/useOtp";
import { authServices, tenantServices, userServices } from "../../../services/apiService";
import { useNavigate } from "react-router-dom";

const TenantUserManagement = () => {
  // ---------------- Popup State ----------------
  const [isCreatePopupOpen, setIsCreatePopupOpen] = useState(false);

  const handleOpenCreatePopup = () => setIsCreatePopupOpen(true);




  // ---------------- Signup Form State ----------------
  const [fullName, setFullName] = useState("");
  const [mobileNumber, setMobileNumber] = useState("");
  const [email, setEmail] = useState("");
  const [tableData, setTableData] = useState({ content: [], totalElements: 0, totalPages: 0, number: 0, size: 10 });
  const [loading, setLoading] = useState(false);
  const [isSuccessPopupOpen, setIsSuccessPopupOpen] = useState(false);
  const [validationErrors, setValidationErrors] = useState({});
  const [planDetails, setPlanDetails] = useState({
    activeUsers: 0,
    createdUsers: 0,
  });
  const navigate = useNavigate();
  const canCreateUser =
  planDetails.createdUsers < planDetails.activeUsers;
  // ---------------- OTP Hook ----------------
  const {
    otp,
    otpVerified,
    otpLoading,
    showOtpModal,
    resendTimer,
    canResend,
    sendOtp,
    verifyOtp,
    resendOtp,
    cancelOtp,
    handleOtpChange,
    handleOtpKeyDown,
    resetOtp, // for resetting OTP state
    resetOtpVerification,
  } = useOtp();

  // ---------------- Reset Form ----------------
  const resetForm = () => {
    setFullName("");
    setMobileNumber("");
    setEmail("");
    resetOtp(); // reset OTP state
    // Reset OTP verified state manually
    setValidationErrors({});
    resetOtpVerification(); // <-- add this
  };

  const handleCloseCreatePopup = () => {
    setIsCreatePopupOpen(false);
    resetForm(); // reset everything when popup closes
  };

  // ---------------- Signup Handler ----------------
  // ---------------- Signup Handler ----------------
const handleSignup = async () => {
  if (!otpVerified) {
    toast.error("Please verify OTP first");
    return;
  }

  if (!fullName || !mobileNumber || !email) {
    toast.error("Please fill all required fields");
    return;
  }

  const payload = {
    name: fullName,
    email: email,
    phone: mobileNumber,
    role: 3, 
    existingTenantId: localStorage.getItem("tenant_id"),
  };

  try {
    const response = await authServices.signup(payload);
    setIsSuccessPopupOpen(true);
    handleCloseCreatePopup();

    fetchData({ page: 0, size: 10 });

  } catch (error) {
    toast.error(error?.response?.data || "Failed to create user.");
  }
};


const fetchData = useCallback(async (params = {}) => {
    try {
      setLoading(true);
      const p = {
        page: params.page || 0,
        size: params.size || 10,
        sortBy: params.sortField,
        sortDir: params.sortOrder,
        search: params.filters?.search,
      };
      const tenant_id = localStorage.getItem("tenant_id");
      
      // API call for inactive/pending users
      const response = await tenantServices.getInactiveUsersPagination(p, tenant_id);
      const data = response?.data || {};
      // Update table data
      // ✅ total users created count
      const totalUsersCreated = data.totalElements || 0;
      
      setTableData({
        content: data.content || [],
        totalElements: data.totalElements || 0,
        totalPages: data.totalPages || 0,
        number: data.number || 0,
        size: data.size || 10,
      });
    } catch (err) {
      toast.error('Failed to fetch pending users', err);
      setTableData({ content: [], totalElements: 0, totalPages: 0, number: 0, size: 10 });
    } finally {
      setLoading(false);
    }
  }, []);
  // Initial data fetch 
  useEffect(() => {
    fetchData({ page: 0, size: 10 });
  }, [fetchData]);


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

  

  return (
    <TenantLayout>
      <div className="license-inventory-content">
        <PageHeader
          title="User Management"
          subtitle="Manage your user accounts"
          button={
  <button
    type="button"
    className="create-plan-btn"
    onClick={handleOpenCreatePopup}
    disabled={!canCreateUser}
    title={
      !canCreateUser
        ? "User limit reached. Upgrade plan to add more users."
        : "Create User"
    }
  >
    Create User
  </button>
}

        />

        <div className="license-inventory-table">
          <DataTable
            fetchData={fetchData}
            data={tableData}
            columns={TANENT_COLUMNS}
            loading={loading}
            basePath="/usermanagement"
            primaryKeys={["userId"]}
            className="license-table"
            showEditButton= {true}
          />

        </div>

        {/* ---------- CREATE USER POPUP ---------- */}
        <PopUp
          isOpen={isCreatePopupOpen}
          onClose={!showOtpModal ? handleCloseCreatePopup : undefined}
          title="Create New User"
          subtitle="Enter user details to create an account"
          size="large"
        >
          <div className="signup-panel">
            <div className="signup-inner">
              {/* Full Name */}
              <InputField
                label={formConfig.signin.fullname.label}
                type="text"
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                name="fullName"
                validationType="NAME"
                max={50}
                required={true}
              />

              {/* Mobile + OTP */}
              <div className="form-group otp-row">
                <div className="otp-input-col">
                  <InputField
                    label={formConfig.signin.mobileno.label}
                    type="tel"
                    value={mobileNumber}
                    onChange={(e) => setMobileNumber(e.target.value)}
                    name="mobileNumber"
                    validationType="MOBILE"
                    disabled={otpVerified}
                    required={true}
                    max={10}
                  />
                </div>
                <div className="otp-btn-col">
                  {!otpVerified ? (
                    <button
                      type="button"
                      className="otp-btn"
                      onClick={() => sendOtp(mobileNumber)}
                      disabled={otpLoading}
                    >
                      {otpLoading ? "Sending..." : "Send OTP"}
                    </button>
                  ) : (
                    <span className="otp-verified">✔ Verified</span>
                  )}
                </div>
              </div>

              {/* Email */}
              <InputField
                label={formConfig.signin.email.label}
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                name="signupEmail"
                validationType="EMAIL"
                required={true}
                max={50}
                validationErrors={validationErrors}
                setValidationErrors={setValidationErrors}
              />

              {/* Submit */}
              <div className="signin-submit">
                <Button
                  text="Create Account"
                  onClick={handleSignup}
                  disabled={!otpVerified || otpLoading || !!validationErrors.signupEmail}
                />
              </div>

              {/* Footer */}
              <div className="signup-back">
                <Footer>PayVance Innovations Private Limited © 2025</Footer>
              </div>
            </div>
          </div>

          {/* OTP Modal */}
          {showOtpModal && (
            <div className="otp-modal-overlay">
              <div className="otp-modal">
                <h3>Verify OTP</h3>
                <p>Enter the 6-digit OTP sent to</p>
                <strong>+91 {mobileNumber}</strong>

                <div className="otp-box-wrapper">
                  {otp.map((digit, index) => (
                    <input
                      key={index}
                      id={`otp-${index}`}
                      type="text"
                      maxLength="1"
                      value={digit}
                      onChange={(e) => handleOtpChange(e.target.value, index)}
                      onKeyDown={(e) => handleOtpKeyDown(e, index)}
                      className="otp-box"
                    />
                  ))}
                </div>

                <div className="otp-actions">
                  <Button
                    text="Verify OTP"
                    onClick={() => verifyOtp(mobileNumber)}
                    disabled={otpLoading}
                  />
                  <button
                    className={`forgot ${!canResend ? "disabled-link" : ""}`}
                    disabled={!canResend || otpLoading}
                    onClick={() => resendOtp(mobileNumber)}
                  >
                    {canResend ? "Resend OTP" : `Resend OTP in ${resendTimer}s`}
                  </button>
                  <button className="forgot" onClick={cancelOtp}>
                    Cancel
                  </button>
                </div>
              </div>
            </div>
          )}
        </PopUp>


        <PopUp
         isOpen={isSuccessPopupOpen}
         onClose={() => setIsSuccessPopupOpen(false)}
         title="Success"
         subtitle="User Created Successfully"
         size="small"
        >
        <div style={{ textAlign: "center" }}>
        <p>The user account has been created successfully.</p>

        <Button
         text="OK"
         onClick={() => {
         setIsSuccessPopupOpen(false);
         navigate("/usermanagement");
         setTimeout(() => {
        window.location.reload();
        }, 0);
      }}
    />
  </div>
</PopUp>

      </div>
    </TenantLayout>
  );
};

export default TenantUserManagement;
