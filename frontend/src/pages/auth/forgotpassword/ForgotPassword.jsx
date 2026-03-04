import { useEffect, useState } from "react";
import InputField from "../../../components/common/inputfield/InputField";
import formConfig from "../../../config/formConfig";
import useOtp from "../../../hooks/useOtp";
import { useNavigate } from "react-router-dom";
import OtpModal from "../../../components/common/otpModal/OtpModal";
import "./ForgotPassword.css";

/**
 * author                version        date        change description
 * Shreyash Talwekar      1.0.2         24/02/2026  UI redesign — split panel, external CSS
 */

const STEPS = [
  { icon: "bi-envelope-at-fill", text: "Enter your registered email address" },
  { icon: "bi-shield-lock-fill", text: "Receive a 6-digit OTP on your email"  },
  { icon: "bi-key-fill",         text: "Verify and reset your password"        },
];

const ForgotPassword = () => {
  const [email, setEmail] = useState("");
  const navigate = useNavigate();
  const [isCheckingEmail, setIsCheckingEmail] = useState(false);

  const {
    otp, otpVerified, otpLoading, showOtpModal,
    resendTimer, canResend, verifyError,
    sendOtp, verifyOtp, resendOtp, cancelOtp,
    handleOtpChange, handleOtpKeyDown,
  } = useOtp();

  useEffect(() => {
    if (otpVerified) navigate("/resetpassword", { state: { email } });
  }, [otpVerified, navigate, email]);

  return (
    <div className="fp-wrapper">
      <div className="fp-card">

        {/* ════ LEFT PANEL ════ */}
        <div className="fp-left">
          <div className="fp-blob fp-blob--1" />
          <div className="fp-blob fp-blob--2" />

          <div className="fp-icon-ring">
            <i className="bi bi-lock-fill" />
          </div>

          <h2 className="fp-left__title">Forgot Password?</h2>
          <p className="fp-left__sub">
            Don't worry — we'll send a one-time code to verify your identity.
          </p>

          <div className="fp-steps">
            {STEPS.map((s, i) => (
              <div key={i} className="fp-step">
                <div className="fp-step__icon">
                  <i className={`bi ${s.icon}`} />
                </div>
                <span className="fp-step__text">{s.text}</span>
              </div>
            ))}
          </div>
        </div>

        {/* ════ RIGHT PANEL ════ */}
        <div className="fp-right">

          {/* Back */}
          <button className="fp-back" onClick={() => navigate("/signin")}>
            <i className="bi bi-arrow-left" /> {formConfig.ResetPassword.backToSignin.label}
          </button>

          {/* Heading */}
          <div className="fp-heading">
            <h3>{formConfig.ResetPassword.recover.label}</h3>
            <p>Enter the email linked to your account and we'll send you an OTP.</p>
          </div>

          <div className="fp-divider" />

          {/* Email + Send OTP */}
          <div className="fp-input-row">
            <div className="fp-input-col">
              <InputField
                label={formConfig.signin.email.label}
                value={email}
                onChange={e => setEmail(e.target.value)}
                validationType="EMAIL"
                disabled={otpVerified}
                max={50}
                classN="large"
              />
            </div>

            {!otpVerified ? (
              <button
                className="fp-send-btn"
                onClick={() => sendOtp(email, "email")}
                disabled={otpLoading || isCheckingEmail}
              >
                <i className="bi bi-send-fill" />
                {otpLoading || isCheckingEmail ? "Sending…" : "Send OTP"}
              </button>
            ) : (
              <span className="fp-verified">
                <i className="bi bi-check-circle-fill" /> Verified
              </span>
            )}
          </div>

          {/* Info note */}
          <div className="fp-note">
            <i className="bi bi-info-circle-fill" />
            <p>
              Check your spam folder if you don't receive the OTP within a minute.
              The OTP is valid for <strong>90 seconds</strong>.
            </p>
          </div>

          {/* Cancel */}
          <button className="fp-cancel" onClick={() => navigate("/signin")}>
            {formConfig.ResetPassword.back.label}
          </button>

        </div>
      </div>

      {/* OTP MODAL */}
      {showOtpModal && (
        <OtpModal
          heading="Please verify yourself"
          description="Enter the 6-digit OTP sent to"
          target={email}
          otp={otp}
          otpLoading={otpLoading}
          totalSeconds={90}
          canResend={canResend}
          verifyError={verifyError}
          onOtpChange={handleOtpChange}
          onOtpKeyDown={handleOtpKeyDown}
          onVerify={() => verifyOtp(email, "email")}
          onResend={() => resendOtp(email, "email")}
          onCancel={cancelOtp}
        />
      )}
    </div>
  );
};

export default ForgotPassword;