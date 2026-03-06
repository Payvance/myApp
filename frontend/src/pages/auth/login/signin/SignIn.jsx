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
 * Samidha Lahane        1.0.0         01/01/2026   component created
 * Neha Tembhe           1.0.0         05/01/2026   Integrated signin api
 *
 **/
import { useState, useEffect } from "react";
import { useDispatch } from "react-redux";
import { authServices } from "../../../../services/apiService";
import { toast } from "react-toastify";
import Button from "../../../../components/common/button/Button.jsx";
import Checkbox from "../../../../components/common/checkbox/Checkbox.jsx";
import Footer from "../../../../components/common/footer/Footer.jsx";
import InputField from "../../../../components/common/inputfield/InputField.jsx";
import { useTheme } from "../../../../context/ThemeContext.jsx";
import { useNavigate } from 'react-router-dom';
import { formConfig } from "../../../../config/formConfig.js";
import "./SignIn.css";
import { login } from "../../../../redux/slices/authSlice";
import { Link } from "react-router-dom";
import useOtp from "../../../../hooks/useOtp.js";
import OtpModal from "../../../../components/common/otpModal/OtpModal";
import PaswordInputBox from "../../../../components/common/inputfield/PaswordInputBox.jsx"
import PopUp from "../../../../components/common/popups/PopUp.jsx";
import { COMPANY_INFO } from "../../../../config/Config.js";
import { validateField } from "../../../../config/validateField.js";
import { useLocation } from "react-router-dom";
const SignIn = () => {
  const {
    otp,
    otpVerified,
    otpLoading,
    showOtpModal,
    resendTimer,
    canResend,
    sendOtp,
    verifyOtp,
    verifyError,
    resendOtp,
    cancelOtp,
    handleOtpChange,
    handleOtpKeyDown,
  } = useOtp();
  const { setTheme } = useTheme();

  /* Set theme to light on component mount */
  useEffect(() => {
    setTheme("light");
  }, []);


  /* State variables for form fields and UI control */
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [mobileNumber, setMobileNumber] = useState("");
  const [fullName, setFullName] = useState("");
  const [remember, setRemember] = useState(false);
  const [confirmPassword, setConfirmPassword] = useState("");
  const [tenantAdminRole, setTenantAdminRole] = useState(null);
  const dispatch = useDispatch();
  const location = useLocation();
  const navigate = useNavigate();
  // State for controlling the popup
  const [isPopupOpen, setIsPopupOpen] = useState(false);
  const [popupMessage, setPopupMessage] = useState("");
  // Add these at the top of your component state section
  const [showSignInPassword, setShowSignInPassword] = useState(false);
  const [showSignupPassword, setShowSignupPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [isPasswordValid, setIsPasswordValid] = useState(false);
  const [signinEmail, setSigninEmail] = useState("");
  const [signinPassword, setSigninPassword] = useState("");
  const [validationErrors, setValidationErrors] = useState({});
  const [showVerifiedPopup, setShowVerifiedPopup] = useState(false);

// Check for email verification status in URL query parameters on component mount
useEffect(() => {
  const params = new URLSearchParams(location.search);

  const verified = Number(params.get("verified"));
  const already = Number(params.get("already"));

  if (verified === 1) {
    setPopupMessage(
      "Your email has been successfully verified. You can now sign in to your account."
    );
    setShowVerifiedPopup(true);
  }

  if (already === 1) {
    setPopupMessage(
      "Your email is already verified. You can sign in to your account."
    );
    setShowVerifiedPopup(true);
  }

  if (verified === 1 || already === 1) {
    window.history.replaceState({}, document.title, "/signin");
  }

}, [location]);
  /* This useeffect is used for the getting roles from the dropdown
     Set the role tenenet admin
  */
  useEffect(() => {
    const fetchRoles = async () => {
      try {
        const response = await authServices.getRoles();
        const roles = response?.data || [];
        const tenantAdmin = roles.find(
          (role) => role.value === "Tenant Admin"
        );
        setTenantAdminRole(tenantAdmin);
      } catch {
        toast.error("Failed to fetch roles");
      }
    };

    fetchRoles();
  }, []);

  // Set cookie
  const setCookie = (name, value, days) => {
    const expires = new Date();
    expires.setTime(expires.getTime() + days * 24 * 60 * 60 * 1000);
    document.cookie = `${name}=${encodeURIComponent(value)};expires=${expires.toUTCString()};path=/`;
  };

  // Get cookie
  const getCookie = (name) => {
    const cookies = document.cookie.split("; ");
    const cookie = cookies.find((c) => c.startsWith(name + "="));
    return cookie ? decodeURIComponent(cookie.split("=")[1]) : "";
  };

  // Delete cookie
  const deleteCookie = (name) => {
    setCookie(name, "", -1);
  };

  useEffect(() => {
    const savedEmail = getCookie("rememberedEmail");
    const savedPassword = getCookie("rememberedPassword");
    if (savedEmail && savedPassword) {
      setSigninEmail(savedEmail);
      setSigninPassword(savedPassword);
      setRemember(true);
    }
  }, []);


  /* Handle login button click for the sign in form
     if role=2 (tenent admin) then it redirect to the dashboard otherwise profile page
  */
  const handleLogin = async () => {
    if (!signinEmail || !signinPassword) {
      toast.error("Please fill all the fields");
      return;
    }
    try {
      const result = await dispatch(
        login({ username: signinEmail, password: signinPassword })
      ).unwrap();

      // ----- REMEMBER ME -----
      if (remember) {
        setCookie("rememberedEmail", signinEmail, 30);     // store for 30 days
        setCookie("rememberedPassword", signinPassword, 30);
      } else {
        deleteCookie("rememberedEmail");
        deleteCookie("rememberedPassword");
      }

      const roleId = result.roleId;
      if (roleId === 1) {
        localStorage.setItem("user_id", result.userId);
        navigate("/dashboard");
        localStorage.setItem("user_id", result.userId);
      }
      else if (roleId === 3) {
        toast.error("You are only elligible to login from mobile app");
      }
      else if (roleId === 2) {
        // ✅ STORE TENANT ID FOR TENANT ADMIN
        localStorage.setItem("tenant_id", result.tenantId);
        localStorage.setItem("user_id", result.userId);
        navigate("/tenantdashboard"); // Redirect to tenant admin dashboard for role ID 2
      }
      else if (roleId === 4) {
        localStorage.setItem("user_id", result.userId);
        // navigate("/vendordashboard");
        localStorage.setItem("user_id", result.userId);
        if (result.redirectUrl === "/vendor/waiting-approval") {
          setPopupMessage(
            "Your vendor profile is under review. You will be notified once it's approved."
          );
          setIsPopupOpen(true);
        } else if (result.redirectUrl === "/vendor/rejected") {
          setPopupMessage(
            "Your vendor profile has been rejected. Please contact support for further assistance."
          );
          setIsPopupOpen(true);
        }
        else {
          navigate("/vendordashboard");
        }
      }
      else if (roleId === 5) {
        localStorage.setItem("user_id", result.userId);
        if (result.redirectUrl === "/ca/waiting-approval") {
          setPopupMessage(
            "Your CA profile is under review. You will be notified once it's approved."
          );
          setIsPopupOpen(true);
        } else if (result.redirectUrl === "/ca/rejected") {
          setPopupMessage(
            "Your CA profile has been rejected. Please contact support for further assistance."
          );
          setIsPopupOpen(true);
        }
        else {
          navigate("/cadashboard");
        }
      }
      else {
        navigate("/profile");
      }
    } catch (error) {
      toast.error(error);
    }
  };


  /* Handle signup button click for the sign up form  */
  const handleSignup = async () => {
    // Check OTP verification first
    if (!otpVerified) {
      toast.error("Please complete OTP verification");
      return;
    }
    if (
      !fullName ||
      !email ||
      !password ||
      !mobileNumber ||
      !confirmPassword
    ) {
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
      email: email,
      password: password,
      phone: mobileNumber,
      role: tenantAdminRole.code,
    };
    try {
      const response = await authServices.signup(payload);
      setPopupMessage(response.data || "Registration successful! Please verify your email address to activate your account.");
      setIsPopupOpen(true);
      setShowRegister(false);
    } catch (error) {
      toast.error(
        error?.response?.data || "Signup failed. Please try again."
      );
    }
  };

  /* State to control display of sign up panel */
  const [showRegister, setShowRegister] = useState(false);

  /* handle create account button click */
  const handleRegister = () => {
    setShowRegister(true);
  };

  /* handle back to sign in button click */
  const handleBackToSignIn = () => setShowRegister(false);


  return (
    <div className="signin-wrapper">
      <div className={`signin-card ${showRegister ? 'register-active' : ''}`}>
        <div className="signin-left">
          <button className="home-nav-icon" onClick={() => navigate("/")} title="Go to Home">
            <i className="bi bi-house-door"></i>
          </button>
          <div className="welcome-header">
            <h2>{formConfig.signin.welcome.label}</h2>
            <div className="gradient-line"></div>
          </div>
          <p>{formConfig.signin.subtext.label}</p>

          {/* email input field */}
          <InputField
            label={formConfig.signin.email.label}
            type="email"
            value={signinEmail}
            onChange={(e) => setSigninEmail(e.target.value)}
            name="email"
            validationType="EMAIL"
            max={50}
          />

          {/* password input field */}
          <div className="position-relative signin-pass-container">
            <InputField
              label={formConfig.signin.password.label}
              type={showSignInPassword ? "text" : "password"}
              value={signinPassword}
              onChange={(e) => setSigninPassword(e.target.value)}
              name="password"
              validationType="PASSWORD"
              onCopy={(e) => e.preventDefault()}
              onPaste={(e) => e.preventDefault()}
              min={8}
              max={16}
            />
            <i className={`bi ${showSignInPassword ? "bi-eye" : "bi-eye-slash"} eye-icon-signin`}
              onClick={() => setShowSignInPassword(!showSignInPassword)}> </i>
          </div>
          {/* remember me and forgot password */}
          <div className="signin-options">
            <Checkbox
              label="Remember me"
              checked={remember}
              onChange={(e) => setRemember(e.target.checked)}
            />
            <button className="forgot" type="button" onClick={() => navigate("/forgotpassword")}>Forgot password?</button>
          </div>

          {/* login button */}
          <div className="signin-submit">
            <Button text="Sign In" onClick={handleLogin} />
          </div>

          {/* Bottom links */}
          <div className="signin-bottom-links">
            {!showRegister && (
              <div className="mobile-register-link">
                <p>Don't have an account?</p>
                <button type="button" className="forgot" onClick={handleRegister}>
                  Create Account
                </button>
              </div>
            )}
          </div>
          {/* footer */}
        </div>

        {/* right side - welcome (slides left on register) */}
        <div className="signin-right">
          <div className="signin-right-icon" aria-hidden="true"></div>
          {/* on click of register button, show register panel */}
          {!showRegister ? (
            <>
              {/* sign in form */}
              <h2>{formConfig.signup.hey.label}</h2>
              <p>{formConfig.signup.sub.label}</p>
              <div className="signin-right-action">
                <Button text="Create Account" variant="outline" onClick={handleRegister} />
              </div>
            </>
          ) : (
            <>
              {/* sign up form */}
              <h2>{formConfig.signup.welcome.label}</h2>
              <p>{formConfig.signup.subtext.label}</p>
              <div className="signin-right-action">
                <Button text="Sign In" variant="outline" onClick={handleBackToSignIn} />
              </div>
            </>
          )}
        </div> {/* signin-right end */}

        {/* Sign Up panel (appears on the right when register is active) */}
        <div className="signup-panel">
          {/* signup inner container */}
          <div className="signup-inner">
            <button className="home-nav-icon" onClick={() => navigate("/")} title="Go to Home">
              <i className="bi bi-house-door"></i>
            </button>
            {/* welcome header */}
            <div className="welcome-header">
              <h2>{formConfig.signup.get.label}</h2>
              <div className="gradient-line"></div>
              <p>Create account and start your journey</p>
            </div>
            {/* welcome header div end */}
            {/* reuable input field component */}
            <InputField
              label={formConfig.signin.fullname.label}
              type="text"
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              name="fullName"
              validationType="NAME"
              max={100}
            />
            <div className="form-group otp-row">
              <div className="otp-input-col">
                <InputField
                  label={formConfig.signin.mobileno.label}
                  type="tel"
                  value={mobileNumber}
                  onChange={(e) => setMobileNumber(e.target.value)}
                  name="mobileNumber"
                  validationType="MOBILE"
                  max={10}
                  disabled={otpVerified}
                  validationErrors={validationErrors}
                  setValidationErrors={setValidationErrors}
                />
              </div>
              <div className="otp-btn-col">
                {!otpVerified ? (
                  <button
                    type="button"
                    className="otp-btn"
                    onClick={() => sendOtp(mobileNumber)}
                    disabled={otpLoading || !!validationErrors.mobileNumber || mobileNumber.length !== 10}
                  >
                    Send OTP
                  </button>
                ) : (
                  <span className="otp-verified">✔ Verified</span>
                )}
              </div>
            </div>
            <InputField
              label={formConfig.signin.email.label}
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              name="signupEmail"
              validationType="EMAIL"
              max={50}
            />
            <div className="position-relative signup-pass-container">
              <PaswordInputBox
                label={formConfig.signin.password.label}
                type={showSignupPassword ? "text" : "password"}
                value={password}
                onChange={setPassword}
                name="signupPassword"
                validationType="PASSWORD"
                onValidationChange={setIsPasswordValid}
                classN="large"
              />
              <i className={`bi ${showSignupPassword ? "bi-eye" : "bi-eye-slash"} eye-icon-signup`}
                onClick={() => setShowSignupPassword(!showSignupPassword)}></i>
            </div>
            <div className="position-relative confirm-pass-container">
              <InputField
                label={formConfig.signin.confirmPassword.label}
                type={showConfirmPassword ? "text" : "password"}
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                name="confirmPassword"
                max={16}
                onCopy={(e) => e.preventDefault()}
                onPaste={(e) => e.preventDefault()}

              />
              <i className={`bi ${showConfirmPassword ? "bi-eye" : "bi-eye-slash"} eye-icon-confirm`}
                onClick={() => setShowConfirmPassword(!showConfirmPassword)}></i>
            </div>

            <div className="signin-submit">
              <Button text="Create Account" onClick={handleSignup} disabled={!otpVerified} />

            </div>
          </div>
          {/* signup inner container end */}
        </div>
        {/* signup panel end */}
        {
          showOtpModal && (
            <OtpModal
              heading="Verify OTP"
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
          )
        }

      </div > {/* signin card end */}

      < Footer > {COMPANY_INFO.name}. Copyright © 2026. All rights reserved.</Footer >
      <PopUp
        isOpen={isPopupOpen}
        onClose={() => setIsPopupOpen(false)}
        title="Profile Under Review"
        size="medium"
        showCloseButton={true}
      >
        <p>{popupMessage}</p>
      </PopUp>
    {/* Verified email popup */}
    <PopUp
    isOpen={showVerifiedPopup}
    onClose={() => setShowVerifiedPopup(false)}
    size="small"
    >
    <p>{popupMessage}</p>
    <button
    className="btn btn-primary"
    onClick={() => setShowVerifiedPopup(false)}
    >
    OK
   </button>
   </PopUp>

    </div >
  );
};

export default SignIn;


