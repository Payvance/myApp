import { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import InputField from "../../../../components/common/inputfield/InputField";
import { authServices } from "../../../../services/apiService";
import { toast } from "react-toastify";
import PaswordInputBox from "../../../../components/common/inputfield/PaswordInputBox";
import "./ResetPassword.css";
import formConfig from "../../../../config/formConfig";

/**
 * author                version        date        change description
 * Shreyash Talwekar      1.0.2         24/02/2026  UI redesign — split panel, rp-* CSS
 */

const TIPS = [
  { icon: "bi-shield-check",    text: "Use a mix of letters, numbers & symbols"  },
  { icon: "bi-eye-slash-fill",  text: "Never share your password with anyone"    },
  { icon: "bi-arrow-repeat",    text: "Change your password every 180 days"      },
];

const ResetPassword = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const email    = location.state?.email;

  const [newPassword,     setNewPassword]     = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading,         setLoading]         = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmPass, setShowConfirmPass] = useState(false);
  const [isPasswordValid, setIsPasswordValid] = useState(false);

  useEffect(() => {
    if (!email) {
      toast.error("Unauthorized access");
      navigate("/forgotpassword");
    }
  }, [email, navigate]);

  const handleResetPassword = async () => {
    if (!newPassword || !confirmPassword) {
      toast.error("Please fill all the fields");
      return;
    }
    if (!isPasswordValid) {
      toast.error("Password does not meet the criteria");
      return;
    }
    if (newPassword !== confirmPassword) {
      toast.error("Confirm Password does not match Password");
      return;
    }
    try {
      setLoading(true);
      await authServices.resetPassword({ email, newPassword });
      toast.success("Password reset successfully");
      navigate("/signin");
    } catch (error) {
      toast.error(error?.response?.data || "Failed to reset password");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="rp-wrapper">
      <div className="rp-card">

        {/* ════ LEFT PANEL ════ */}
        <div className="rp-left">
          <div className="rp-blob rp-blob--1" />
          <div className="rp-blob rp-blob--2" />
          <div className="rp-blob rp-blob--3" />

          <div className="rp-icon-ring">
            <i className="bi bi-shield-lock-fill" />
          </div>

          <h2 className="rp-left__title">Reset Password</h2>
          <p className="rp-left__sub">
            Create a strong new password to keep your account safe.
          </p>

          <div className="rp-email-chip">
            <i className="bi bi-envelope-fill" />
            {email}
          </div>

          <div className="rp-tips">
            {TIPS.map((t, i) => (
              <div key={i} className="rp-tip">
                <div className="rp-tip__icon"><i className={`bi ${t.icon}`} /></div>
                <span className="rp-tip__text">{t.text}</span>
              </div>
            ))}
          </div>
        </div>

        {/* ════ RIGHT PANEL ════ */}
        <div className="rp-right">

          <button className="rp-back" onClick={() => navigate("/signin")}>
            <i className="bi bi-arrow-left" /> {formConfig.ResetPassword.backToSignin.label}
          </button>

          <div className="rp-heading">
            <h3>{formConfig.ResetPassword.create.label}</h3>
            <p>Your new password must be different from your previous one.</p>
          </div>

          <div className="rp-divider" />

          <div className="rp-fields">

            <div className="rp-pass-wrap">
              <PaswordInputBox
                label={formConfig.signin.password.label}
                value={newPassword}
                type={showNewPassword ? "text" : "password"}
                onChange={setNewPassword}
                onValidationChange={setIsPasswordValid}
                showCaseInfo={true}
                classN="large"
              />
              <i
                className={`bi ${showNewPassword ? "bi-eye" : "bi-eye-slash"} rp-eye`}
                onClick={() => setShowNewPassword(p => !p)}
              />
            </div>

            <div className="rp-pass-wrap">
              <InputField
                label={formConfig.signin.confirmPassword.label}
                type={showConfirmPass ? "text" : "password"}
                value={confirmPassword}
                onChange={e => setConfirmPassword(e.target.value)}
                max={16}
                onCopy={e  => e.preventDefault()}
                onPaste={e => e.preventDefault()}
                classN="large"
              />
              <i
                className={`bi ${showConfirmPass ? "bi-eye" : "bi-eye-slash"} rp-eye`}
                onClick={() => setShowConfirmPass(p => !p)}
              />
            </div>

          </div>

          <div className="rp-note">
            <i className="bi bi-shield-check" />
            <p>
              After resetting, you will be redirected to sign in again.
              Keep your password <strong>private and secure</strong>.
            </p>
          </div>

          <button
            className="rp-submit-btn"
            onClick={handleResetPassword}
            disabled={loading || !newPassword || !confirmPassword || !isPasswordValid}
          >
            {loading
              ? <><span className="rp-spinner" /> Resetting…</>
              : <><i className="bi bi-check2-circle" /> Reset Password</>
            }
          </button>

          <button className="rp-cancel" onClick={() => navigate("/signin")}>
            {formConfig.ResetPassword.back.label}
          </button>

        </div>
      </div>
    </div>
  );
};

export default ResetPassword;