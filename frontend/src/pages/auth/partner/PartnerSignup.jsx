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
import "../login/signin/SignIn.css";
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
      toast.success(response?.data || "Partner registered successfully");
      navigate("/signin");
    } catch (error) {
      toast.error(error?.response?.data || "Signup failed");
    }
  };

  return (
    <div className="signin-wrapper">
      {/* SINGLE COLUMN CARD */}
      <div className="signin-card" style={{ width: "420px", gridTemplateColumns: "1fr", }}>
        <div className="signup-panel" style={{ position: "static", width: "100%" }}>
          <div className="signup-inner" style={{ marginLeft: "-20%"}}>

            {/* HEADER */}
            <div className="welcome-header" >
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
            <button className="forgot" onClick={() => navigate("/signin")} style={{ marginLeft: "30%"}}>
              Back to Sign In
            </button>

            <Footer>{COMPANY_INFO.name} © 2025</Footer>
          </div>
        </div>

        {/* OTP MODAL */}
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
          {canResend
            ? "Resend OTP"
            : `Resend OTP in ${resendTimer}s`}
        </button>

        <button className="forgot" onClick={cancelOtp}>
          Cancel
        </button>
      </div>
    </div>
  </div>
)}

      </div>
    </div>
  );
};

export default PartnerWithUs;
