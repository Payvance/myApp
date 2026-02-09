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
import { useDispatch} from "react-redux";
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
import PaswordInputBox from "../../../../components/common/inputfield/PaswordInputBox.jsx"
import PopUp from "../../../../components/common/popups/PopUp.jsx";
import { COMPANY_INFO } from "../../../../config/Config.js";

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
    if ( roleId === 1) {
      localStorage.setItem("user_id", result.userId);
      navigate("/dashboard");
      localStorage.setItem("user_id", result.userId);
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
    toast.success(response.data || "Registered successfully. Verify email.");
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
      {/* main container for sign in form */}
      <div className={`signin-card ${showRegister ? 'register-active' : ''}`}>
        {/* left side - form fields */}
        <div className="signin-left">
          <div className="welcome-header">
            <h2>Welcome Back!</h2>
            <div className="gradient-line"></div>
          </div>
          <p>Sign in to continue to your account</p>

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
            type={showSignInPassword? "text" : "password"}
            value={signinPassword}
            onChange={(e) => setSigninPassword(e.target.value)}
            name="password"
            validationType="PASSWORD"
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

          {/* footer */}
          <Footer>{COMPANY_INFO.name} © 2025</Footer>
        </div>

        {/* right side - welcome (slides left on register) */}
        <div className="signin-right">
          <div className="signin-right-icon" aria-hidden="true"></div>
          {/* on click of register button, show register panel */}
          {!showRegister ? (
            <>
              {/* sign in form */}
              <h2>Hey There!</h2>
              <p>Begin Your Journey With Creating An Account With Us.</p>
              <div className="signin-right-action">
                <Button text="Create Account" variant="outline" onClick={handleRegister} />
              </div>
            </>
          ) : (
            <>
              {/* sign up form */}
              <h2>Welcome Back!</h2>
              <p>Already Have An Account? Sign In To Continue.</p>
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
            {/* welcome header */}
            <div className="welcome-header">
              <h2>Create Account</h2>
              <div className="gradient-line"></div>
              <p>Sign up to continue to your account</p>
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
                disabled={otpVerified}
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
            
            />   
            <i className={`bi ${showConfirmPassword ? "bi-eye" : "bi-eye-slash"} eye-icon-confirm`} 
            onClick={() => setShowConfirmPassword(!showConfirmPassword)}></i>
            </div>
           <div className="signin-divider">
            <span className="line"></span>
            <span className="or-text">Create Account and Start Your Journey</span>
            <span className="line"></span>
          </div>
            
            <div className="signin-submit">
              <Button text="Create Account" onClick={handleSignup} disabled={!otpVerified} />

            </div>
            <div className="signup-back">
              <button type="button" className="forgot" onClick={handleBackToSignIn}>Back to Sign In</button>
              <Footer>{COMPANY_INFO.name} © 2025</Footer> 
              <div className="partner-link">
              <Link to="/partnerwithus" className="forgot">
                Partner with us
              </Link>
            </div>           
            </div>
          </div>
          {/* signup inner container end */}
        </div>
        {/* signup panel end */}
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

      </div> {/* signin card end */}


        <PopUp
        isOpen={isPopupOpen}
        onClose={() => setIsPopupOpen(false)}
        title="Profile Under Review"
        size="medium"
        showCloseButton={true}
      >
        <p>{popupMessage}</p>
      </PopUp>

    </div>
  );
};

export default SignIn;
