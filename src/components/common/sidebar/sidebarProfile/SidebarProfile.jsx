import { useState, useRef, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import Button from "../../button/Button";
import PopUp from "../../popups/PopUp";
import { authServices, resetPasswordServices, companyDetailsServices, userServices } from "../../../../services/apiService";
import { toast } from "react-toastify";
import "./SidebarProfile.css";
import { useTheme } from "../../../../context/ThemeContext";
import { SUPPORT_INFO } from "../../../../config/Config";
import InputField from "../../inputfield/InputField";
import formConfig from "../../../../config/formConfig";
import ThemeDropdown from "../../themedropdown/ThemeDropdown";
import PaswordInputBox from "../../inputfield/PaswordInputBox";

/* Utils */
const getInitials = (name = "") => {
  if (!name) return "?";
  const parts = name.trim().split(/\s+/);
  if (parts.length > 1) {
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  }
  return parts[0][0].toUpperCase();
};

const UserProfileMenu = ({
  upgradeRoute = "/upgrade-plan",
  showUpgrade = false,
  showLicense = false,
  showPersonalization = true,
  showForgotPassword = true,
  showcompanyDetails = true,
}) => {
  /* -------------------- STATE -------------------- */
  const [openMenu, setOpenMenu] = useState(false);
  const [openSupport, setOpenSupport] = useState(false);
  const [showLogoutPopup, setShowLogoutPopup] = useState(false);
  const [supportPosition, setSupportPosition] = useState("right");
  const [user, setUser] = useState({ name: "", email: "" });
  const [forgotStep, setForgotStep] = useState(0);
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [showResetPopup, setShowResetPopup] = useState(false);
  const [showOldPass, setShowOldPass] = useState(false);
  const [showNewPass, setShowNewPass] = useState(false);
  const [showConfirmPass, setShowConfirmPass] = useState(false);
  const [isPasswordValid, setIsPasswordValid] = useState(false);
  const [copiedField, setCopiedField] = useState(null);
  const [isThemeDropdownOpen, setIsThemeDropdownOpen] = useState(false);
  const [openTheme, setOpenTheme] = useState(false);
  const THEMES = [
    { name: "Light", value: "light", icon: "bi-sun-fill" },
    { name: "Dark", value: "dark", icon: "bi-moon-fill" },
    { name: "PayVance", value: "custom", icon: "bi-palette-fill" },
  ];
  const roleId = Number(localStorage.getItem("roleId"));
  const [showCompanyPopup, setShowCompanyPopup] = useState(false);
  const [isUpdate, setIsUpdate] = useState(false);
  const [companyDetails, setCompanyDetails] = useState({
  gstNumber: "",
  companyName: "",
  address: "",
  });
  const handleCompanyChange = (e) => {
  const { name, value } = e.target;
  setCompanyDetails((prev) => ({
    ...prev,
    [name]: value,
  }));
};
// Fetch company details when the popup opens
const fetchCompanyDetails = async () => {
  try {
    const tenantId = localStorage.getItem("tenant_id");
    const res = await companyDetailsServices.getCompanyDetails(tenantId);
    if (res?.data) {
      setCompanyDetails({
        gstNumber: res.data.gstNumber || "",
        companyName: res.data.companyName || "",
        address: res.data.address || "",
      });
      setIsUpdate(true);
    }
  } catch (error) {
    setCompanyDetails({
      gstNumber: "",
      companyName: "",
      address: "",
    });
  }
};
// Handle company details submission 
const handleCompanySubmit = async () => {
  try {
    setLoading(true);
      const payload = {
      tenantId: localStorage.getItem("tenant_id"),
      gstNumber: companyDetails.gstNumber,
      companyName: companyDetails.companyName,
      address: companyDetails.address,
    };

    await companyDetailsServices.upsertCompanyDetails(payload);
     if (isUpdate) {
      toast.success("Company details updated successfully");
    } else {
      toast.success("Company details submitted successfully");
      setIsUpdate(true); 
    }
    setTimeout(() => {
      setShowCompanyPopup(false);
    }, 1500);

  } catch (error) {
    toast.error("Failed to Submit company details");
  } finally {
    setLoading(false);
  }
};


  // -------------------- THEME CONTEXT --------------------
  const { theme, setTheme, isDark, isCustom } = useTheme();

  const menuRef = useRef(null);
  const supportRef = useRef(null);
  const navigate = useNavigate();

  /* -------------------- EFFECTS -------------------- */
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        setOpenMenu(false);
        setOpenSupport(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  useEffect(() => {
    const getUserData = async () => {
      try {
        const userId = localStorage.getItem("user_id");
        const response = await userServices.getUserData(userId);
        if (response?.data) {
          setUser({
            name: response.data.name || "",
            email: response.data.email || "",
          });
        }
      } catch (error) {
        console.error("Failed to fetch user data:", error);
      }
    };
    getUserData();
  }, []);

  /* -------------------- HANDLERS -------------------- */
  const handleLogout = async () => {
    setShowLogoutPopup(false);
    setOpenMenu(false);
    try {
      await authServices.logout();
      localStorage.removeItem("token");
      sessionStorage.removeItem("token");
      navigate("/signin");
    } catch (error) {
      console.error("Logout failed:", error);
    }
  };

  const toggleSupport = () => {
    setOpenSupport((prev) => !prev);
    if (!openSupport && supportRef.current) {
      const rect = supportRef.current.getBoundingClientRect();
      const viewportWidth = window.innerWidth;
      setSupportPosition(rect.right + 220 > viewportWidth ? "left" : "right");
    }
  };

  const handleThemeChange = (selectedTheme) => {
    // Set the theme using ThemeContext
    setTheme(selectedTheme);
  };

  /* Copy with double-tick feedback */
  const copyToClipboard = (text, field) => {
    navigator.clipboard.writeText(text).then(() => {
      setCopiedField(field);
      setTimeout(() => setCopiedField(null), 2000);
    });
  };

  const handleUnifiedReset = async (e) => {
    e?.preventDefault();
    if (!oldPassword || !newPassword || !confirmPassword) {
      toast.error("All fields are required");
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
    if (oldPassword === newPassword) {
      toast.error("New password must be different from current password");
      return;
    }
    setLoading(true);
    try {
      const verifyRes = await resetPasswordServices.verifyAndResetPassword({
        currentPassword: oldPassword,
      });
      if (verifyRes.data === 1) {
        const resetRes = await resetPasswordServices.setNewPassword({
          newPassword: newPassword,
        });
        if (resetRes.data.message === "Password changed successfully") {
          toast.success("Password changed successfully");
          setShowResetPopup(false);
          handleLogout();
        } else {
          toast.error("Failed to update password");
        }
      } else {
        toast.error("Incorrect current password. Try again.");
      }
    } catch (err) {
      toast.error("An error occurred during password reset");
    } finally {
      setLoading(false);
    }
  };

  /* -------------------- JSX -------------------- */
  return (
    <>
      <div className="sidebar-footer" ref={menuRef}>

        {/* ── User profile row ── */}
        <div
          className="user-profile"
          onClick={() => {
            setOpenMenu((prev) => !prev);
            setOpenSupport(false);
          }}
        >
          <span className="avatar">{getInitials(user.name)}</span>
          <div className="user-info">
            <span className="username" title={user.name}>{user.name}</span>
          </div>
        </div>

        {/* ── Dropdown ── */}
        {openMenu && (
          <div className="profile-dropdown">
            {/* ── Theme (Help-style) ── */}
            <div className="help-wrapper">
              <div
                className="dropdown-item"
                onClick={() => setOpenTheme((prev) => !prev)}
              >
                <i className={`bi ${THEMES.find(t => t.value === theme)?.icon || 'bi-circle-half'}`} />
                <span>Appearance</span>
                <i className={`bi bi-chevron-${openTheme ? "left" : "right"} ms-auto`} style={{ fontSize: 11 }} />
              </div>

              {openTheme && (
                <div className="support-box sp-theme-panel support-right">
                  <p className="support-title">Appearance</p>
                  {THEMES.map((t) => {
                    const isActive = theme === t.value;
                    return (
                      <button
                        key={t.value}
                        className={`sp-theme-row${isActive ? " sp-theme-row--active" : ""}`}
                        onClick={() => { setTheme(t.value); setOpenTheme(false); }}
                      >
                        <i className={`bi ${t.icon} sp-theme-row__icon`} />
                        <span className="sp-theme-row__name">{t.name}</span>
                        {isActive && <i className="bi bi-check2 sp-theme-row__check" />}
                      </button>
                    );
                  })}
                </div>
              )}
            </div>


            <div className="dropdown-divider" />

            {showUpgrade && (
              <button
                className="dropdown-item upgrade-glow"
                onClick={() => { navigate(upgradeRoute); setOpenMenu(false); }}
              >
                <i className="bi bi-star-fill" />
                <span>Upgrade plan</span>
              </button>
            )}

            {showLicense && (
              <Link to="/license" className="dropdown-item" onClick={() => setOpenMenu(false)}>
                <i className="bi bi-patch-check" />
                <span>License Information</span>
              </Link>
            )}

            {showcompanyDetails && roleId === 2 && (
             <div
             className="dropdown-item"
             onClick={async () => {
             setShowCompanyPopup(true);
             setOpenMenu(false);
             await fetchCompanyDetails();
             }}
             >
             <i className="bi bi-gear" />
             <span>Company Details</span>
             </div>
             )}
            <div className="dropdown-divider" />

            {/* ── Help / Support ── */}
            <div className="help-wrapper" ref={supportRef}>
              <div className="dropdown-item" onClick={toggleSupport}>
                <i className="bi bi-life-preserver" />
                <span>Help</span>
                <i className={`bi bi-chevron-${openSupport ? "left" : "right"} ms-auto`} />
              </div>

              {openSupport && (
                <div className={`support-box slide-fade support-${supportPosition}`}>
                  <p className="support-title">Support</p>

                  {/* Email row */}
                  <div className="support-item">
                    <i className="bi bi-envelope-fill" />
                    <span title={SUPPORT_INFO.email}>{SUPPORT_INFO.email}</span>
                    <i
                      className={`bi ${copiedField === 'email' ? 'bi-check2-all copy-icon copy-icon--done' : 'bi-clipboard copy-icon'}`}
                      onClick={() => copyToClipboard(SUPPORT_INFO.email, 'email')}
                    />
                  </div>

                  {/* Phone row */}
                  <div className="support-item">
                    <i className="bi bi-telephone-fill" />
                    <span title={SUPPORT_INFO.phone}>{SUPPORT_INFO.phone}</span>
                    <i
                      className={`bi ${copiedField === 'phone' ? 'bi-check2-all copy-icon copy-icon--done' : 'bi-clipboard copy-icon'}`}
                      onClick={() => copyToClipboard(SUPPORT_INFO.phone, 'phone')}
                    />
                  </div>
                </div>
              )}
            </div>

            {showForgotPassword && (
              <Link
                className="dropdown-item"
                onClick={() => {
                  setShowResetPopup(true);
                  setOpenMenu(false);
                  setError("");
                  setOldPassword("");
                  setNewPassword("");
                  setConfirmPassword("");
                }}
              >
                <i className="bi bi-key-fill" />
                <span>Reset password</span>
              </Link>
            )}

            <button className="dropdown-item logout" onClick={() => setShowLogoutPopup(true)}>
              <i className="bi bi-box-arrow-right" />
              <span>Log out</span>
            </button>
          </div>
        )}
      </div>

      {/* ================= UNIFIED RESET PASSWORD POPUP ================= */}
      <PopUp
        isOpen={showResetPopup}
        onClose={() => setShowResetPopup(false)}
        title="Change Your Password"
        subtitle="Enter your current password and choose a new one."
        size="large"
      >
        <div className="reset-password-form-container" style={{ marginTop: "10px" }}>
          {/* CURRENT PASSWORD */}
          <div className="position-relative signup-pass-container" style={{ width: "49%" }}>
            <InputField
              label={formConfig.signin.currentPassword.label}
              value={oldPassword}
              onChange={(e) => setOldPassword(e.target.value)}
              type={showOldPass ? "text" : "password"}
              classN="large"
              max={16}
              onPaste={(e) => e.preventDefault()}
              onCopy={(e) => e.preventDefault()}
            />
            <i className={`bi ${showOldPass ? "bi-eye" : "bi-eye-slash"} eye-icon-signup`}
              onClick={() => setShowOldPass(!showOldPass)}></i>
          </div>

          <div style={{ display: "flex", gap: "10px", marginTop: "10px" }}>
            <div className="position-relative signup-pass-container">
              <PaswordInputBox
                label="New Password"
                value={newPassword}
                onChange={setNewPassword}
                type={showNewPass ? "text" : "password"}
                onValidationChange={setIsPasswordValid}
                validationType="PASSWORD"
                classN="large"
                showCaseInfo={true}
              />
              <i
                className={`bi ${showNewPass ? "bi-eye" : "bi-eye-slash"} eye-icon-signup`}
                onClick={() => setShowNewPass(!showNewPass)}
              />
            </div>

            {/* CONFIRM NEW PASSWORD */}
            <div className="position-relative signup-pass-container">
              <InputField
                label="Confirm New Password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                type={showConfirmPass ? "text" : "password"}
                classN="large"
                max={16}
                onPaste={(e) => e.preventDefault()}
                onCopy={(e) => e.preventDefault()}
              />
              <i className={`bi ${showConfirmPass ? "bi-eye" : "bi-eye-slash"} eye-icon-signup`}
                onClick={() => setShowConfirmPass(!showConfirmPass)}></i>
            </div>
          </div>

          <div className="password-note-section">
            <strong>Note:</strong>
            <ul style={{ marginTop: "5px", paddingLeft: "18px" }}>
              <li>To keep your account safe, change your password every 180 days.</li>
              <li>Once your password is changed, you will be automatically logged out.</li>
            </ul>
          </div>

          <div className="popup-buttons">
            <Button
              text={loading ? "Processing..." : "Update Password"}
              onClick={handleUnifiedReset}
              disabled={loading || !oldPassword || !newPassword || !confirmPassword || !isPasswordValid}
            />
          </div>
        </div>
      </PopUp>

      {/* ── Logout Popup ── */}
      <PopUp
        isOpen={showLogoutPopup}
        onClose={() => setShowLogoutPopup(false)}
        title={formConfig.Logout.logout.label}
        subtitle="Are you sure you want to end your session?"
        icon="bi-box-arrow-right"
        size="small"
      >
        <div className="lo-body">
          <div className="lo-note">
            <i className="bi bi-exclamation-triangle-fill" />
            <p>You will be redirected to the sign-in page and will need to log in again to continue.</p>
          </div>
          <div className="lo-actions">
            <button className="lo-cancel-btn" onClick={() => setShowLogoutPopup(false)}>
              <i className="bi bi-x" /> {formConfig.Logout.cancel.label}
            </button>
            <button className="lo-logout-btn" onClick={handleLogout}>
              <i className="bi bi-box-arrow-right" /> {formConfig.Logout.logout.label}
            </button>
          </div>
        </div>
      </PopUp>
      {/* ── Company Details Popup ── */}
      <PopUp
       isOpen={showCompanyPopup}
       onClose={() => setShowCompanyPopup(false)}
       title="Company Details"
       size="medium"
    >
      <div className="company-details-container">
        <div className="company-details-row">
  
             <InputField
              label={formConfig.vendorprofile.gstno.label}
              name="gstNumber"
              value={companyDetails.gstNumber}
              onChange={handleCompanyChange}
              validationType="GST"
              max={15}
              classN="large"
            />
            <InputField
              label={formConfig.CompanyDetails.companyName.label}
              name="companyName"
              value={companyDetails.companyName}
              onChange={handleCompanyChange}
              required
              max={150}
              classN="large"
            />
        </div>
        <div className="company-details-row">
          
          <div className={`company-details-field floating-textarea ${companyDetails.address ? 'has-value' : ''}`}>
  <textarea
    name={formConfig.CompanyDetails.address.label}
    required
    value={companyDetails.address}
    onChange={handleCompanyChange}
    maxLength={255}
    placeholder=" "  
  />
  <label>
    {formConfig.CompanyDetails.address.label}
    <span className="required">*</span>
  </label>
</div>
        </div>
      </div>
    <Button text="Submit" onClick={handleCompanySubmit} disabled={Object.values(companyDetails).some((value) => !value.trim())} />
    </PopUp>
    </>
  );
};

export default UserProfileMenu;