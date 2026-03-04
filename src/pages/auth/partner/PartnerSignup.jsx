/**
 * Copyright: © 2025 Payvance Innovation Pvt. Ltd.
 *
 * Organization: Payvance Innovation Pvt. Ltd.
 *
 * This is unpublished, proprietary, confidential source code of Payvance Innovation Pvt. Ltd.
 * Payvance Innovation Pvt. Ltd. retains all title to and intellectual property rights in these materials.
 *
 **/

/**
 *
 * author                version        date        change description
 * Neha Tembhe           1.0.0         8/01/2026   Partner signup integration
 *
 **/
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import InputField from "../../../components/common/inputfield/InputField.jsx";
import Button from "../../../components/common/button/Button.jsx";
import Footer from "../../../components/common/footer/Footer.jsx";
import PaswordInputBox from "../../../components/common/inputfield/PaswordInputBox.jsx";
import { useTheme } from "../../../context/ThemeContext.jsx";
import { authServices } from "../../../services/apiService";
import { formConfig } from "../../../config/formConfig.js";
import useOtp from "../../../hooks/useOtp";
import OtpModal from "../../../components/common/otpModal/OtpModal.jsx";
import "../login/signin/SignIn.css";
import "./PartnerSignup.css";
import { COMPANY_INFO } from "../../../config/Config.js";

const PartnerWithUs = () => {
  const { setTheme } = useTheme();
  const navigate = useNavigate();

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
    verifyError,
  } = useOtp();

  useEffect(() => {
    setTheme("light");
  }, []);

  const [fullName, setFullName] = useState("");
  const [mobileNumber, setMobileNumber] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [isPasswordValid, setIsPasswordValid] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  /* This function is used to send the correct payload data with validation check for the 
     confirmed password, api calling with toast messages.
  */
  const handleSignup = async () => {
    if (!otpVerified) {
      toast.error("Please complete OTP verification");
      return;
    }

    if (!fullName || !mobileNumber || !email || !password || !confirmPassword) {
      toast.error("Please fill all the fields");
      return;
    }
    if (!isPasswordValid) {
      toast.error("Password does not meet the criteria");
      return;
    }
    if (password !== confirmPassword) {
      toast.error("Confirm Password does not match Password");
      return;
    }

    const payload = {
      name: fullName,
      phone: mobileNumber,
      email,
      password,
      orgName: "payvance",
    };

    try {
      const response = await authServices.signup(payload);
      navigate("/signin");
      toast.success(response?.data || "Partner registered successfully");
    } catch (error) {
      toast.error(error?.response?.data || "Signup failed");
    }
  };

  return (
    <div className="signin-wrapper partner-page">
      {/* SINGLE COLUMN CARD */}
      <div className="signin-card partner-card-layout">
        <div className="signup-panel partner-panel">
          <div className="signup-inner partner-inner">

            {/* HEADER */}
            <div className="welcome-header">
              <h2>Partner With Us</h2>
              <div className="gradient-line"></div>
              <p>Create your partner account</p>
            </div>

            {/* FULL NAME */}
            <InputField
              label={formConfig.signin.fullname.label}
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              validationType="NAME"
              max={100}
            />

            {/* MOBILE + OTP */}
            <div className="otp-row">
              <div className="otp-input-col">
                <InputField
                  label={formConfig.signin.mobileno.label}
                  value={mobileNumber}
                  onChange={(e) => setMobileNumber(e.target.value)}
                  validationType="MOBILE"
                  disabled={otpVerified}
                  max={10}
                />
              </div>

              <div className="otp-btn-col">
                {!otpVerified ? (
                  <button
                    className="otp-btn"
                    onClick={() => sendOtp(mobileNumber)}
                    disabled={otpLoading}
                  >
                    Send OTP
                  </button>
                ) : (
                  <span className="otp-verified">✔ Verified</span>
                )}
              </div>
            </div>

            {/* EMAIL */}
            <InputField
              label={formConfig.signin.email.label}
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              validationType="EMAIL"
              max={254}
            />

            {/* PASSWORD */}
            <div className="position-relative signup-pass-container">
              <PaswordInputBox
                label={formConfig.signin.password.label}
                type={showPassword ? "text" : "password"}
                value={password}
                onChange={setPassword}
                onValidationChange={setIsPasswordValid}
                classN="large"
              />
              <i
                className={`bi ${showPassword ? "bi-eye" : "bi-eye-slash"} eye-icon-signup`}
                onClick={() => setShowPassword(!showPassword)}
              > </i>
            </div>
            {/* CONFIRM PASSWORD */}
            <div className="position-relative signup-pass-container">
              <InputField
                label={formConfig.signin.confirmPassword.label}
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                type={showConfirmPassword ? "text" : "password"}
                onPaste={(e) => e.preventDefault()}
              />
              <i
                className={`bi ${showConfirmPassword ? "bi-eye" : "bi-eye-slash"} eye-icon-signup`}
                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
              > </i>
            </div>
            {/* SUBMIT */}
            <div className="signin-submit">
              <Button
                text="Create Partner Account"
                onClick={handleSignup}
                disabled={!otpVerified}
              />
            </div>

            {/* BACK TO LOGIN */}
            <div className="partner-back-links">
              <button className="forgot" onClick={() => navigate("/signin")}>
                Back to Sign In
              </button>

              <button className="forgot" onClick={() => navigate("/")}>
                Back to Homepage
              </button>
            </div>
          </div>

        </div>

        {/* OTP MODAL */}
        {showOtpModal && (
          <OtpModal
            heading={formConfig.Logout.verifyItp.label}
            description="Enter the 6-digit OTP sent to"
            target={`+91 ${mobileNumber}`}
            otp={otp}
            otpLoading={otpLoading}
            canResend={canResend}
            onOtpChange={handleOtpChange}
            onOtpKeyDown={handleOtpKeyDown}
            onVerify={() => verifyOtp(mobileNumber)}
            onResend={() => resendOtp(mobileNumber)}
            onCancel={cancelOtp}
            verifyError={verifyError}
            totalSeconds={60}
          />
        )}

      </div>
      <Footer>{COMPANY_INFO.name} . All rights reserved</Footer>
    </div>
  );
};

export default PartnerWithUs;
